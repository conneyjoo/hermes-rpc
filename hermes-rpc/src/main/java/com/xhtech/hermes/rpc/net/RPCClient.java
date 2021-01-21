package com.xhtech.hermes.rpc.net;

import com.xhtech.hermes.core.netty.ChannelPoolClient;
import com.xhtech.hermes.core.netty.address.ReadWriteAddress;
import com.xhtech.hermes.core.netty.codec.ProtoDecoder;
import com.xhtech.hermes.core.netty.codec.ProtoEncoder;
import com.xhtech.hermes.core.netty.proto.Proto;
import com.xhtech.hermes.rpc.net.codec.RPCDecode;
import com.xhtech.hermes.rpc.net.codec.RPCEncode;
import com.xhtech.hermes.rpc.net.proto.Connect;
import com.xhtech.hermes.rpc.net.proto.Keepalive;
import com.xhtech.hermes.rpc.net.proto.RPCProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class RPCClient extends ChannelPoolClient {

    private static final Logger logger = LoggerFactory.getLogger(RPCClient.class);

    public static final Keepalive KEEPALIVE = new Keepalive();

    public RPCClient(ApplicationContext springContent) {
        super(springContent);
    }

    @Override
    public void doConnect(ReadWriteAddress address) {
        if (address.isRead()) {
            send(address, new Connect());
        } else {
            send(address, KEEPALIVE);
        }
    }

    @Override
    public void accept(ChannelHandlerContext ctx) {
        super.accept(ctx);
    }

    @Override
    public void heartbeat(ChannelHandlerContext ctx) {
        if (logger.isTraceEnabled()) {
            logger.trace("Send heartbeat to server({})", ctx);
        }

        ctx.writeAndFlush(KEEPALIVE);
    }

    @Override
    public boolean isProtoType(Proto proto) {
        return proto instanceof RPCProto;
    }

    @Override
    public LengthFieldBasedFrameDecoder getLengthFieldBasedFrameDecoder() {
        return new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, RPCProto.HEADER_LENGTH_POS, 4, -6, 0, true);
    }

    @Override
    public ProtoEncoder getEncoder() {
        return new RPCEncode();
    }

    @Override
    public ProtoDecoder getDecoder() {
        return new RPCDecode();
    }
}
