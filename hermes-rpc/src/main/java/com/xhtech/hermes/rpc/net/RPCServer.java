package com.xhtech.hermes.rpc.net;

import com.xhtech.hermes.core.netty.NettyServer;
import com.xhtech.hermes.core.netty.codec.ProtoDecoder;
import com.xhtech.hermes.core.netty.codec.ProtoEncoder;
import com.xhtech.hermes.core.netty.proto.Proto;
import com.xhtech.hermes.rpc.net.codec.RPCDecode;
import com.xhtech.hermes.rpc.net.codec.RPCEncode;
import com.xhtech.hermes.rpc.net.proto.RPCProto;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

public class RPCServer extends NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);

    public static final int READER_IDLE_TIME = RPCClient.DEFAULT_WRITER_IDLE_TIME * 6;

    public RPCServer(ApplicationContext springContent) {
        super(springContent);
    }

    @Override
    public boolean isProtoType(Proto proto) {
        return proto instanceof RPCProto;
    }

    @Override
    public IdleStateHandler getIdleStateHandler() {
        return new IdleStateHandler(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
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