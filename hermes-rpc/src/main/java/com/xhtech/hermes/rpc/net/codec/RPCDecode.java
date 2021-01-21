package com.xhtech.hermes.rpc.net.codec;

import com.xhtech.hermes.core.netty.codec.ProtoDecoder;
import com.xhtech.hermes.core.netty.exception.ProtoDecodeException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import static com.xhtech.hermes.rpc.net.proto.RPCProto.HEADER_CMD_POS;
import static com.xhtech.hermes.rpc.net.proto.RPCProto.HEADER_LENGTH;

@ChannelHandler.Sharable
public class RPCDecode extends ProtoDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = decode(ctx, in);

        if (decoded != null) {
            out.add(decoded);
        }
    }

    @Override
    public boolean check(ByteBuf in) throws ProtoDecodeException {
        if (in.readableBytes() < HEADER_LENGTH) {
            throw new ProtoDecodeException("Readable bytes cannot be below minimum(" + HEADER_LENGTH + ")");
        }

        return true;
    }

    @Override
    public byte getCmd(ByteBuf in) {
        return in.getByte(HEADER_CMD_POS);
    }
}