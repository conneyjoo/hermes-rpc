package com.xhtech.hermes.core.netty.address;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ReadWriteAddress implements java.io.Serializable {

    private static final long serialVersionUID = 4215720748342549866L;

    public static final int READ_ONLY_STYLE = 1;

    public static final int WRITE_ONLY_STYLE = 1 << 1;

    /**
     * FIXED(hhy/zjy): 已在唯一的构造函数中初始化，此处可删除 [构造方法的style可能是READ_ONLY_STYLE或者WRITE_ONLY_STYLE, 为了让style范围不超过READ_ONLY_STYLE和WRITE_ONLY_STYLE, 默认为读\写都都支持]
     */
    private int style = READ_ONLY_STYLE | WRITE_ONLY_STYLE;

    private InetSocketAddress socketAddress;

    public ReadWriteAddress(String hostname, int port, int style) {
        socketAddress = new InetSocketAddress(hostname, port);
        this.style &= style;
    }

    public final int getPort() {
        return socketAddress.getPort();
    }

    public final InetAddress getAddress() {
        return socketAddress.getAddress();
    }

    public final String getHostName() {
        return socketAddress.getHostName();
    }

    public final String getHostString() {
        return socketAddress.getHostString();
    }

    public final boolean isUnresolved() {
        return socketAddress.isUnresolved();
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public int getStyle() {
        return style;
    }

    public boolean isRead() {
        return (style & READ_ONLY_STYLE) == READ_ONLY_STYLE;
    }

    public boolean isWrite() {
        return (style & WRITE_ONLY_STYLE) == WRITE_ONLY_STYLE;
    }

    public String getStyleName() {
        return isWrite() ? "write" : "read";
    }

    @Override
    public boolean equals(Object o) {
        return socketAddress.equals(o) && style == ((ReadWriteAddress) o).style;
    }

    @Override
    public int hashCode() {
        int result = socketAddress.hashCode();
        result = 31 * result + style;
        return result;
    }
}
