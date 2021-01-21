package com.xhtech.hermes.rpc.dto;

import com.xhtech.hermes.core.netty.annotation.ProtoDefine;
import com.xhtech.hermes.core.netty.proto.StringEx;
import com.xhtech.hermes.rpc.net.proto.CMD;
import com.xhtech.hermes.rpc.net.proto.RPCProto;
import io.netty.buffer.ByteBuf;

@ProtoDefine
public class FeedbackMessage extends RPCProto {

    public static final byte FB_MESSAGE_FAILURE_STATE = 0x0;

    public static final byte FB_MESSAGE_SUCCESS_STATE = 0x1;

    private StringEx clientId;

    private long msgId;

    private byte state = FB_MESSAGE_FAILURE_STATE;

    @Override
    public byte getCmd() {
        return CMD.FEEDBACK_MESSAGE_CMD;
    }

    @Override
    public void writeBody(ByteBuf buf) {
        writeString(buf, clientId);
        buf.writeLong(msgId);
        buf.writeByte(state);
    }

    @Override
    public void readBody(ByteBuf buf) {
        clientId = readString(buf);
        msgId = buf.readLong();
        state = buf.readByte();
    }

    @Override
    public int bodyLength() {
        return clientId.length() + 8 + 1;
    }

    public String getClientId() {
        return clientId.getValue();
    }

    public void setClientId(String clientId) {
        this.clientId = new StringEx(clientId);
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }
}
