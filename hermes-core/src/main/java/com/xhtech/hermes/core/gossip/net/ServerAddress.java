package com.xhtech.hermes.core.gossip.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerAddress extends InetSocketAddress {

    private int serverPort;

    public ServerAddress(String hostname, int port) {
        super(hostname, port);
    }

    public ServerAddress(String hostname, int port, int serverPort) {
        super(hostname, port);
        this.serverPort = serverPort;
    }

    public ServerAddress(InetAddress addr, int port) {
        super(addr, port);
    }

    public ServerAddress(InetAddress addr, int port, int serverPort) {
        super(addr, port);
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public String toString() {
        if (isUnresolved()) {
            return getHostName() + ":" + getPort() + (serverPort != 0 ? ":" + serverPort : "");
        } else {
            return getAddress().getHostAddress() + ":" + getPort() + (serverPort != 0 ? ":" + serverPort : "");
        }
    }
}
