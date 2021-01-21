package com.xhtech.hermes.core.cluster;

import com.xhtech.hermes.core.gossip.utils.FBUtilities;

import java.net.InetSocketAddress;

public abstract class NodeBase {

    private static long nodeId;

    public static long nodeID() {
        if (nodeId == 0) {
            nodeId = convertNodeID(FBUtilities.getLocalAddress());
        }
        return nodeId;
    }

    public static String convertAddress(long nodeId) {
        StringBuilder address = new StringBuilder();
        address.append((nodeId >> 24) & 0xFFL).append(".");
        address.append((nodeId >> 16) & 0xFFL).append(".");
        address.append((nodeId >> 8) & 0xFFL).append(".");
        address.append(nodeId & 0xFFL).append(":");
        address.append((nodeId >> 32) & 0xFFFFL);
        return address.toString();
    }

    public static long convertNodeID(InetSocketAddress address) {
        byte[] bytes = address.getAddress().getAddress();
        int port = address.getPort();
        return (port & 0xFFFFL) << 32 | (bytes[0] & 0xFFL) << 24 | (bytes[1] & 0xFFL) << 16 | (bytes[2] & 0xFFL) << 8 | bytes[3] & 0xFFL;
    }
}
