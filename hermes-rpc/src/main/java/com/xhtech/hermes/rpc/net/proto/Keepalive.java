package com.xhtech.hermes.rpc.net.proto;

import com.xhtech.hermes.core.netty.annotation.ProtoDefine;
import io.netty.buffer.ByteBuf;

@ProtoDefine
public class Keepalive extends RPCProto {

    @Override
    public byte getCmd() {
        return CMD.KEEPALIVE_CMD;
    }

    @Override
    public void writeBody(ByteBuf buf) {
    }

    @Override
    public void readBody(ByteBuf buf) {
    }

    @Override
    public int bodyLength() {
        return 0;
    }
}
