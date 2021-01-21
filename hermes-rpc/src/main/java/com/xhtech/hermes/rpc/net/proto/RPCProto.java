package com.xhtech.hermes.rpc.net.proto;

import com.xhtech.hermes.core.netty.proto.AbstractProto;
import io.netty.buffer.ByteBuf;

public abstract class RPCProto extends AbstractProto {

    /** 协议header长度 */
    public static final int HEADER_LENGTH = 6;

    /** header中的cmd位置 */
    public static final int HEADER_CMD_POS = 1;

    /** header中的length位置 */
    public static final int HEADER_LENGTH_POS = 2;

    /** 协议版本 */
    protected byte version = 0x01;

    /** 协议指令 */
    protected byte cmd;

    /** 数据长度 */
    protected int length;

    public RPCProto() {
    }

    public void writeHeader(ByteBuf buf) {
        buf.writeByte(version);
        buf.writeByte(getCmd());
        buf.writeInt(available());
    }

    public void readHeader(ByteBuf buf) {
        version = buf.readByte();
        cmd = buf.readByte();
        length = buf.readInt();
    }

    public int available() {
        return HEADER_LENGTH + bodyLength();
    }
}
