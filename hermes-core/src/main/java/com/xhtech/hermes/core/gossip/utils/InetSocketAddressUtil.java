package com.xhtech.hermes.core.gossip.utils;

import com.xhtech.hermes.core.gossip.net.ServerAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetSocketAddressUtil {

    public static ServerAddress parseInetSocketAddress(String str) throws UnknownHostException {
        String[] strs = str.split(":");

        if (strs.length == 2) {
            return new ServerAddress(InetAddress.getByName(strs[0]), Integer.parseInt(strs[1]));
        }

        if (strs.length == 3) {
            return new ServerAddress(InetAddress.getByName(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
        } else {
            throw new UnknownHostException(str);
        }
    }
}
