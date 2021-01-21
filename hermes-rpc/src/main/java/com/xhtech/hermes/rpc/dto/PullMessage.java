package com.xhtech.hermes.rpc.dto;

import com.xhtech.hermes.core.netty.annotation.ProtoDefine;
import com.xhtech.hermes.core.netty.proto.StringEx;
import com.xhtech.hermes.rpc.net.proto.CMD;
import com.xhtech.hermes.rpc.net.proto.RPCProto;
import io.netty.buffer.ByteBuf;

@ProtoDefine
public class PullMessage extends RPCProto {

    private StringEx clientId;

    private StringEx deviceId;

    private int start;

    private int end;

    public PullMessage() {
    }

    public PullMessage(String clientId, String deviceId, int start, int end) {
        this.clientId = new StringEx(clientId);
        this.deviceId = new StringEx(deviceId);
        this.start = start;
        this.end = end;
    }

    @Override
    public byte getCmd() {
        return CMD.PULL_MESSAGE_CMD;
    }

    @Override
    public void writeBody(ByteBuf buf) {
        writeString(buf, clientId);
        writeString(buf, deviceId);
        buf.writeInt(start);
        buf.writeInt(end);
    }

    @Override
    public void readBody(ByteBuf buf) {
        clientId = readString(buf);
        deviceId = readString(buf);
        start = buf.readInt();
        end = buf.readInt();
    }

    @Override
    public int bodyLength() {
        return clientId.length() + deviceId.length() + 8;
    }

    public String getClientId() {
        return clientId.getValue();
    }

    public void setClientId(String clientId) {
        this.clientId = new StringEx(clientId);
    }

    public String getDeviceId() {
        return deviceId.getValue();
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = new StringEx(deviceId);
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
