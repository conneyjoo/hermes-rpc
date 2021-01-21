package com.xhtech.hermes.core.gossip;

import com.xhtech.hermes.core.cluster.NodeBase;
import com.xhtech.hermes.core.gossip.gms.VersionedValue;
import com.xhtech.hermes.core.gossip.locator.EndpointSnitch;
import com.xhtech.hermes.core.gossip.service.GossipService;
import com.xhtech.hermes.core.util.Manifestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Collection;

import static com.xhtech.hermes.core.gossip.gms.ApplicationState.*;
import static com.xhtech.hermes.core.gossip.gms.VersionedValue.VersionedValueFactory.instance;

public abstract class GossipBootstrap implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(GossipBootstrap.class);

    @Autowired
    private ApplicationContext applicationContext;

    private Manifestor manifestor;

    private GossipService gossipService;

    public void start(VersionedValue appType) throws IOException {
        start(1D, 1, true, "", appType);
    }

    public void start(boolean working, VersionedValue appType) throws IOException {
        start(1D, 1, working, "", appType);
    }

    public void start(boolean working, String token, VersionedValue appType) throws IOException {
        start(1D, 1, working, token, appType);
    }

    public void start(double load, int weight, boolean working, String token, VersionedValue appType) throws IOException {
        gossipService = new GossipService();
        manifestor = new Manifestor();

        manifestor.load();

        gossipService.getGossiper().register(() -> {
            gossipService.getGossiper().addLocalApplicationState(LOAD, instance.load(load));
            gossipService.getGossiper().addLocalApplicationState(WEIGHT, instance.weight(weight));
            gossipService.getGossiper().addLocalApplicationState(TYPE, appType);
            gossipService.getGossiper().addLocalApplicationState(VERSION, instance.getVersionedValue(manifestor.getVersion()));
            gossipService.getGossiper().addLocalApplicationState(ID, String.valueOf(NodeBase.nodeID()));
            registerEndpointSnitch(gossipService);
        });

        gossipService.start();
    }

    public void registerEndpointSnitch(GossipService gossipService) {
        Collection<EndpointSnitch> collection = applicationContext.getBeansOfType(EndpointSnitch.class).values();

        if (collection != null && collection.size() > 0) {
            collection.forEach(e -> gossipService.getGossiper().register(e));
        }
    }

    public void stop() {
        if (gossipService != null) {
            try {
                gossipService.stop();
            } catch (Exception ex) {
                logger.warn("stop exception, message:{}, detailL{}", ex.getMessage(), ex);
            }
        }
    }

    public Manifestor getManifestor() {
        return manifestor;
    }
}
