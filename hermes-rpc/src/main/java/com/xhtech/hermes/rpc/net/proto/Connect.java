package com.xhtech.hermes.rpc.net.proto;

import com.xhtech.hermes.core.netty.annotation.ProtoDefine;
import io.netty.buffer.ByteBuf;

@ProtoDefine
public class Connect extends RPCProto {

    private long id;

    private byte ack;

    @Override
    public byte getCmd() {
        return CMD.CONNECT_CMD;
    }

    @Override
    public void writeBody(ByteBuf buf) {
        buf.writeLong(id);
        buf.writeByte(++ack);
    }

    @Override
    public void readBody(ByteBuf buf) {
        id = buf.readLong();
        ack = buf.readByte();
    }

    public byte ack() {
        return ack;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int bodyLength() {
        return 8 + 1;
    }
}
