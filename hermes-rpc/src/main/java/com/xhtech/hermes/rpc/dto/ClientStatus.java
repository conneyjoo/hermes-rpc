package com.xhtech.hermes.rpc.dto;

import com.xhtech.hermes.core.cluster.NodeBase;
import com.xhtech.hermes.core.localevent.DomainEvent;
import com.xhtech.hermes.core.netty.annotation.ProtoDefine;
import com.xhtech.hermes.core.netty.proto.StringEx;
import com.xhtech.hermes.rpc.net.proto.CMD;
import com.xhtech.hermes.rpc.net.proto.RPCProto;
import io.netty.buffer.ByteBuf;

@ProtoDefine
public class ClientStatus extends RPCProto implements DomainEvent {

    /* 客户端离线状态 */
    public static final byte CLIENT_OFFLINE_STATUS = 0x0;

    /* 客户端在线状态 */
    public static final byte CLIENT_ONLINE_STATUS = 0x1;

    /* 客户端活跃状态 */
    public static final byte CLIENT_ACTIVE_STATUS = 0x2;

    /* 找不到客户端状态 */
    public static final byte CLIENT_NONE_STATUS = 0x3;

    private long cid;

    private StringEx clientId;

    private StringEx deviceId = StringEx.EMPTY;

    private byte status = CLIENT_OFFLINE_STATUS;

    /* 发生时间 */
    private long occurredTime;

    @Override
    public byte getCmd() {
        return CMD.CLIENT_STATUS_CMD;
    }

    @Override
    public void writeBody(ByteBuf buf) {
        buf.writeLong(cid);
        writeString(buf, clientId);
        writeString(buf, deviceId);
        buf.writeByte(status);
        buf.writeLong(occurredTime);
    }

    @Override
    public void readBody(ByteBuf buf) {
        cid = buf.readLong();
        clientId = readString(buf);
        deviceId = readString(buf);
        status = buf.readByte();
        occurredTime = buf.readLong();
    }

    @Override
    public int bodyLength() {
        return 8 + clientId.length() + deviceId.length() + 1 + 8;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
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

    public long getOccurredTime() {
        return occurredTime;
    }

    public void setOccurredTime(long occurredTime) {
        this.occurredTime = occurredTime;
    }

    public static ClientStatus online() {
        ClientStatus clientStatus = new ClientStatus();
        clientStatus.status = CLIENT_ONLINE_STATUS;
        return clientStatus;
    }

    public static ClientStatus online(String clientId, String devideId, long occurredTime) {
        ClientStatus clientStatus = online();
        clientStatus.setCid(NodeBase.nodeID());
        clientStatus.setClientId(clientId);
        clientStatus.setDeviceId(devideId);
        clientStatus.setOccurredTime(occurredTime);
        return clientStatus;
    }

    public static ClientStatus offline() {
        ClientStatus clientStatus = new ClientStatus();
        clientStatus.status = CLIENT_OFFLINE_STATUS;
        return clientStatus;
    }

    public static ClientStatus offline(String clientId) {
        ClientStatus clientStatus = offline();
        clientStatus.setCid(NodeBase.nodeID());
        clientStatus.setClientId(clientId);
        clientStatus.setOccurredTime(System.currentTimeMillis());
        return clientStatus;
    }

    public static ClientStatus active() {
        ClientStatus clientStatus = new ClientStatus();
        clientStatus.status = CLIENT_ACTIVE_STATUS;
        return clientStatus;
    }

    public static ClientStatus active(long cid, String clientId, String devideId) {
        ClientStatus activeStatus = active();
        activeStatus.setCid(cid);
        activeStatus.setClientId(clientId);
        activeStatus.setDeviceId(devideId);
        return activeStatus;
    }

    public static ClientStatus none() {
        ClientStatus clientStatus = new ClientStatus();
        clientStatus.status = CLIENT_NONE_STATUS;
        return clientStatus;
    }

    public static ClientStatus none(String clientId, long occurredTime) {
        ClientStatus clientStatus = none();
        clientStatus.status = CLIENT_NONE_STATUS;
        clientStatus.setCid(NodeBase.nodeID());
        clientStatus.setClientId(clientId);
        clientStatus.setOccurredTime(occurredTime);
        return clientStatus;
    }

    public boolean isOnline() {
        return status == CLIENT_ONLINE_STATUS;
    }

    public boolean isOffline() {
        return status == CLIENT_OFFLINE_STATUS;
    }

    public boolean isActive() {
        return status == CLIENT_ACTIVE_STATUS;
    }

    public boolean isNone() {
        return status == CLIENT_NONE_STATUS;
    }

    @Override
    public String toString() {
        String statusDescription = "online";
        if (isOffline()) {
            statusDescription = "offline";
        } else if (isActive()) {
            statusDescription = "active";
        }
        return "ClientStatus{" +
                "cid=" + getCid() +
                ", clientId=" + getClientId() +
                ", status=" + statusDescription +
                ", occurredTime=" + occurredTime +
                '}';
    }
}
