/**
 * Created by louis on 2020/9/15.
 */
package com.xhtech.hermes.rpc.dto;

import com.xhtech.hermes.core.netty.annotation.ProtoDefine;
import com.xhtech.hermes.core.netty.proto.StringEx;
import com.xhtech.hermes.rpc.net.proto.CMD;
import com.xhtech.hermes.rpc.net.proto.RPCProto;
import io.netty.buffer.ByteBuf;

/**
 * com.xhtech.hermes.rpc.dto.DeviceHeartbeatForward
 * <p>
 * 将App发送到通讯服务的心跳转发到消息服务
 *
 * @author: louis
 * @time: 2020/9/15 5:58 PM
 */
@ProtoDefine
public class DeviceHeartbeatForward extends RPCProto {

    private StringEx cid;
    private StringEx clientId;
    /**
     * 该连接最初建立的时间
     */
    private long connectedTime;


    public DeviceHeartbeatForward() {
    }

    public DeviceHeartbeatForward(String clientId, String cid, long connectedTime) {
        this.cid = new StringEx(cid);
        this.clientId = new StringEx(clientId);
        this.connectedTime = connectedTime;
    }

    @Override
    public byte getCmd() {
        return CMD.DEVICE_HEARTBEAT_FORWARD_CMD;
    }

    @Override
    public void writeBody(ByteBuf buf) {
        writeString(buf, cid);
        writeString(buf, clientId);
        buf.writeLong(connectedTime);
    }

    @Override
    public void readBody(ByteBuf buf) {
        cid = readString(buf);
        clientId = readString(buf);
        connectedTime = buf.readLong();
    }

    @Override
    public int bodyLength() {
        return cid.length() + clientId.length() + Long.BYTES;
    }


    public String getCid() {
        return cid.getValue();
    }

    public void setCid(String cid) {
        this.cid = new StringEx(cid);
    }

    public String getClientId() {
        return clientId.getValue();
    }

    public void setClientId(String clientId) {
        this.clientId = new StringEx(clientId);
    }

    public long getConnectedTime() {
        return connectedTime;
    }

    public void setConnectedTime(long connectedTime) {
        this.connectedTime = connectedTime;
    }

    @Override
    public String toString() {
        return "DeviceHeartbeatForward{" +
                "cid=" + cid.getValue() +
                ", clientId=" + clientId.getValue() +
                ", connectedTime=" + connectedTime+
                '}';
    }
}
