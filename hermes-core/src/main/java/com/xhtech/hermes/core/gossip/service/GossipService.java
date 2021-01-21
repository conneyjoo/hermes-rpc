package com.xhtech.hermes.core.gossip.service;

import com.xhtech.hermes.core.gossip.gms.ApplicationState;
import com.xhtech.hermes.core.gossip.gms.Gossiper;
import com.xhtech.hermes.core.gossip.gms.VersionedValue;
import com.xhtech.hermes.core.gossip.net.MessagingService;
import com.xhtech.hermes.core.gossip.net.NetSnitch;
import com.xhtech.hermes.core.gossip.utils.FBUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GossipService {

    private static Logger logger = LoggerFactory.getLogger(GossipService.class);

    static {
        Gossiper.instance.register(new NetSnitch());
    }

    public Gossiper getGossiper() {
        return Gossiper.instance;
    }

    public void start() throws IOException {
        start((int) (System.currentTimeMillis() / 1000));
    }

    public void start(int generationNbr) throws IOException {
        logger.info("Gossip service({}) has been started...", FBUtilities.getLocalAddress());
        MessagingService.instance().listen(FBUtilities.getLocalAddress());
        logger.info("Gossip starting up...");
        Gossiper.instance.start(generationNbr);
        logger.info("Gossip has been started...");
    }


    public void stop() throws IOException, InterruptedException {
        logger.info("stop gossip");
        Gossiper.instance.stop();
        logger.info("Gossip has been stoped...");

        MessagingService.instance().shutdownAllConnections();
        logger.info("All Gossiper connection has been closed...");
    }

    public static VersionedValue getLocalState(ApplicationState state) {
        return Gossiper.instance.getLocalEndpointState().getApplicationState(state);
    }

    public static void setLocalState(ApplicationState state, VersionedValue value) {
        Gossiper.instance.addLocalApplicationState(state, value);
    }

    public static void setLocalState(ApplicationState state, String value) {
        Gossiper.instance.addLocalApplicationState(state, value);
    }
}
