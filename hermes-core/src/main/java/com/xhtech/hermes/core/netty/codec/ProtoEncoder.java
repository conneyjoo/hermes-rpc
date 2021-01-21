package com.xhtech.hermes.core.netty.codec;

import com.xhtech.hermes.core.netty.proto.Proto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public abstract class ProtoEncoder extends MessageToByteEncoder<Proto> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Proto proto, ByteBuf out) throws Exception {
        ByteBuf buf = proto.encode();
        out.writeBytes(buf);
        buf.clear();
        buf.release();
    }
}
