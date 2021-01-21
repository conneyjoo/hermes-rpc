/**
 * Created by louis on 2020/10/12.
 */
package com.xhtech.hermes.core.others;

import com.xhtech.hermes.commons.util.ThreadPoolUtils;
import com.xhtech.hermes.core.gossip.GossipBootstrap;
import com.xhtech.hermes.core.gossip.gms.Gossiper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;

/**
 * com.xhtech.hermes.core.others.ShutdownApplicationAdapter
 * 对IShutdownApplication做了默认的实现
 *
 * @author: louis
 * @time: 2020/10/12 1:51 PM
 */
public class ShutdownApplicationAdapter implements IShutdownApplication, ApplicationContextAware {
    private static final Logger LOG = LoggerFactory.getLogger(ShutdownApplicationAdapter.class);
    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean shutdownWebServer(long shutdownAwaitTermination) {
        LOG.info("start shutdownWebServer, waitTime:{}...", shutdownAwaitTermination);
        return false;
    }

    @Override
    public boolean shutdownThreadPools() {
        LOG.info("start shutdownThreadPools...");
        return false;
    }

    @Override
    public boolean shutdownMQ() {
        LOG.info("start shutdownMQ...");
        return false;
    }

    @Override
    public boolean shutdownNetty() {
        LOG.info("start shutdownNetty...");
        return false;
    }

    @Override
    public boolean shutdownGossip() {
        LOG.info("start shutdownGossip...");
        ThreadPoolUtils.shutdownGraceful(Gossiper.instance.getExecutor(), 60 * 1000);

        Map<String, GossipBootstrap> gossipBootstrapMap = applicationContext.getBeansOfType(GossipBootstrap.class);
        if (gossipBootstrapMap.isEmpty()) {
            LOG.warn("gossip is not running");
            return true;
        }

        Collection<GossipBootstrap> bootstraps = gossipBootstrapMap.values();
        GossipBootstrap bootstrap = bootstraps.iterator().next();
        if (bootstrap != null) {
            bootstrap.stop();
        }

        return true;
    }
}
