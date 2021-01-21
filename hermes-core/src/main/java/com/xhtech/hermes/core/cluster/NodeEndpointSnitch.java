package com.xhtech.hermes.core.cluster;

import com.xhtech.hermes.core.cluster.replication.ReplicationHandler;
import com.xhtech.hermes.core.gossip.gms.ApplicationState;
import com.xhtech.hermes.core.gossip.gms.VersionedValue;
import com.xhtech.hermes.core.gossip.locator.EndpointSnitch;
import com.xhtech.hermes.core.gossip.net.ServerAddress;
import org.springframework.context.ApplicationContext;

public class NodeEndpointSnitch extends EndpointSnitch {

    private ReplicationHandler changeHandler;

    public NodeEndpointSnitch(ApplicationContext context) {
        this.changeHandler = new ReplicationHandler(context);
    }

    @Override
    public void onChange(ServerAddress endpoint, ApplicationState state, VersionedValue value) {
        super.onChange(endpoint, state, value);

        if (state == ApplicationState.CHANGE) {
            changeHandler.handle(value);
        }
    }
}