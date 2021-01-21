package com.xhtech.hermes.core.gossip.config;

import com.xhtech.hermes.core.gossip.locator.SeedProvider;
import com.xhtech.hermes.core.gossip.locator.SimpleSeedProvider;
import com.xhtech.hermes.core.gossip.net.ServerAddress;
import com.xhtech.hermes.core.gossip.utils.InetSocketAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GossiperDescriptor {

    private static Logger logger = LoggerFactory.getLogger(GossiperDescriptor.class);

    private static GossipConfig conf;
    private static SeedProvider seedProvider;
    private static ServerAddress listenAddress;   // leave null so we can fall through to getLocalHost

    private static boolean hasInited = false;

    /***
     * 务必保证GossiperDescriptor在所有方法被调用之前，init方法已经被调用。
     *
     * 本应放到static块中，自己new一个GossipConfig出来。调用更底层的配置读取类，new出GossipConfig。
     * @param config
     */
    public static void init(GossipConfig config) {
        conf = config;
        seedProvider = new SimpleSeedProvider(conf.getSeeds());
        parseConfigInfo();
        hasInited = true;
    }

    public static void checkHasInited() {
        if (!hasInited) {
            logger.error("启动逻辑错误！GossiperDescriptor的init方法尚未调用！");
            System.err.println("启动逻辑错误！GossiperDescriptor的init方法尚未调用！");
            System.exit(1);
        }
    }


    /**
     * 判断gossip是否初始化了。
     * @return
     */
    public static boolean hasInited(){
        return hasInited;
    }

    public static String getClusterName() {
        checkHasInited();
        return conf.getCluster_name();
    }

    public static int getPhiConvictThreshold() {
        checkHasInited();
        return conf.getPhi_convict_threshold();
    }

    public static long getRpcTimeout() {
        checkHasInited();
        return conf.getRpc_timeout_in_ms();
    }

    public static Integer getRing_delay() {
        checkHasInited();
        return conf.getRing_delay_in_ms();
    }

    public static Set<ServerAddress> getSeeds() {
        checkHasInited();
        return Collections.unmodifiableSet(new HashSet<ServerAddress>(seedProvider.getSeeds()));
    }

    public static ServerAddress getListenAddress() {
        checkHasInited();
        return listenAddress;
    }

    public static ServerAddress getBroadcastAddress() {
        checkHasInited();
        return listenAddress;
    }

    private static void parseConfigInfo() {
        try {
            if (conf.getListen_address() != null) {
                if (conf.getListen_address().equals("0.0.0.0")) {
                    throw new Exception("listen_address cannot be 0.0.0.0!");
                }

                try {
                    listenAddress = InetSocketAddressUtil.parseInetSocketAddress(conf.getListen_address());
                } catch (UnknownHostException e) {
                    throw new Exception("Unknown listen_address '" + conf.getListen_address() + "'");
                }
            } else {
                throw new Exception("listen_address cannot be null!");
            }

            if (seedProvider.getSeeds().size() == 0) {
                throw new Exception("The seed provider lists no seeds.");
            }
        } catch (Exception e) {
            logger.error("Fatal configuration error", e);
            System.err.println(e.getMessage() + "\nFatal configuration error; unable to start server.  See log for stacktrace.");
            System.exit(1);
        }
    }
}
