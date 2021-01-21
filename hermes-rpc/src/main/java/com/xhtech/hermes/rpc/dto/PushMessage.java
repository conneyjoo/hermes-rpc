package com.xhtech.hermes.rpc.dto;

import com.xhtech.hermes.core.netty.annotation.ProtoDefine;
import com.xhtech.hermes.core.netty.proto.StringEx;
import com.xhtech.hermes.rpc.net.proto.CMD;
import com.xhtech.hermes.rpc.net.proto.RPCProto;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ProtoDefine
public class PushMessage extends RPCProto {

    private StringEx clientId;

    private long connectionVersion;

    private List<Entry> entries = new ArrayList<>();

    public PushMessage() {
    }

    public PushMessage(String clientId) {
        this.clientId = new StringEx(clientId);
    }

    public PushMessage(String clientId, long msgId, byte[] content, int sendCount) {
        this(clientId);
        add(msgId, content, (byte) sendCount);
    }

    @Override
    public byte getCmd() {
        return CMD.PUSH_MESSAGE_CMD;
    }

    @Override
    public void writeBody(ByteBuf buf) {
        writeString(buf, clientId);
        buf.writeLong(connectionVersion);
        buf.writeShort(entries.size());
        entries.stream().forEach((e) -> e.write(buf));
    }

    @Override
    public void readBody(ByteBuf buf) {
        clientId = readString(buf);
        connectionVersion = buf.readLong();
        for (int i = 0, len = buf.readUnsignedShort(); i < len; i++) {
            entries.add(new Entry(buf));
        }
    }

    @Override
    public int bodyLength() {
        return clientId.length() + 10 + entries.stream().mapToInt(Entry::length).sum();
    }

    @Override
    public String toSimpleString(){
        if (CollectionUtils.isEmpty(entries)){
            return "[]";
        }

        List<String> stringList = entries.stream().map(entry -> entry.toSimpleString()).collect(Collectors.toList());
        return  StringUtils.join(stringList, ",");
    }

    public void add(long msgId, byte[] content, byte sendCount) {
        entries.add(new Entry(msgId, content, sendCount));
    }

    public String getClientId() {
        return clientId.getValue();
    }

    public void setClientId(String clientId) {
        this.clientId = new StringEx(clientId);
    }

    public long getConnectionVersion() {
        return connectionVersion;
    }

    public void setConnectionVersion(long connectionVersion) {
        this.connectionVersion = connectionVersion;
    }

    public List<Entry> entries() {
        return entries;
    }

    public boolean isEmptyEntry(){
        return CollectionUtils.isEmpty(entries);
    }

    public class Entry {

        long msgId;

        byte[] content;

        byte sendCount = 0;

        Entry(long msgId, byte[] content, byte sendCount) {
            this.msgId = msgId;
            this.content = content;
            this.sendCount = sendCount;
        }

        Entry(ByteBuf buf) {
            read(buf);
        }

        void write(ByteBuf buf) {
            buf.writeLong(msgId);
            buf.writeInt(content.length);
            buf.writeBytes(content);
            buf.writeByte(sendCount);
        }

        void read(ByteBuf buf) {
            msgId = buf.readLong();
            content = readBytes(buf, buf.readInt());
            sendCount = buf.readByte();
        }

        int length() {
            return 8 + 4 + content.length + 1;
        }

        public long getMsgId() {
            return msgId;
        }

        public void setMsgId(long msgId){
            this.msgId = msgId;
        }

        public byte[] getContent() {
            return content;
        }

        public byte getSendCount() {
            return sendCount;
        }

        public void setSendCount(byte sendCount) {
            this.sendCount = sendCount;
        }


        public String toSimpleString() {
            return "msgId=" + msgId;
        }
    }
}
