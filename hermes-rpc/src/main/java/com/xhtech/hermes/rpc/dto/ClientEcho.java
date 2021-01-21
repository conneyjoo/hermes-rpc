package com.xhtech.hermes.rpc.dto;

import com.xhtech.hermes.core.netty.annotation.ProtoDefine;
import com.xhtech.hermes.core.netty.proto.StringEx;
import com.xhtech.hermes.rpc.net.proto.CMD;
import com.xhtech.hermes.rpc.net.proto.RPCProto;
import io.netty.buffer.ByteBuf;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

@ProtoDefine
public class ClientEcho extends RPCProto {

    private StringEx clientId;

    private StringEx message;

    @Override
    public byte getCmd() {
        return CMD.CLIENT_ECHO_CMD;
    }

    public ClientEcho() {
    }

    public ClientEcho(String clientId, String message) {
        this.clientId = new StringEx(clientId);
        this.message = new StringEx(message);
    }

    @Override
    public void writeBody(ByteBuf buf) {
        writeString(buf, clientId);
        writeString(buf, message);
    }

    @Override
    public void readBody(ByteBuf buf) {
        clientId = readString(buf);
        message = readString(buf);
    }

    @Override
    public int bodyLength() {
        return clientId.length() + message.length();
    }

    public String format() {
        return String.format("%s#%s#", getClientId(), getMessage());
    }

    public ClientEcho parse(String text) {
        String[] split;

        if (isNotEmpty(text) && (split = text.split("#")).length > 1) {
            setClientId(split[0]);
            setMessage(split[1]);
        }

        return this;
    }

    public String getClientId() {
        return clientId.getValue();
    }

    public void setClientId(String clientId) {
        this.clientId = new StringEx(clientId);
    }

    public String getMessage() {
        return message.getValue();
    }

    public void setMessage(String message) {
        this.message = new StringEx(message);
    }
}
