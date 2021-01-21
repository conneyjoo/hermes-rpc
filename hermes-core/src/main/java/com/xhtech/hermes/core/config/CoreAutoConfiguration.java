package com.xhtech.hermes.core.config;

import com.xhtech.hermes.core.cluster.NodeEndpointSnitch;
import com.xhtech.hermes.core.cluster.NodeProperties;
import com.xhtech.hermes.core.gossip.config.GossipConfig;
import com.xhtech.hermes.core.gossip.config.GossiperDescriptor;
import com.xhtech.hermes.core.monitor.Dashboard;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreAutoConfiguration {

    public static final String GOSSIP_CONFIG_BEAN_NAME = "gossipConfig";

    @Bean(GOSSIP_CONFIG_BEAN_NAME)
    @ConditionalOnBean(NodeProperties.class)
    @ConditionalOnProperty(prefix = "node", name="host")
    public GossipConfig gossipConfig(NodeProperties nodeProperties) {
        GossipConfig gossipConfig = new GossipConfig(nodeProperties.identity(), nodeProperties.getSeeds());
        GossiperDescriptor.init(gossipConfig);
        return gossipConfig;
    }

    @Bean
    public Dashboard dashboard() {
        return new Dashboard();
    }

    @Configuration
    @EnableConfigurationProperties({NodeProperties.class})
    public static class NodeAutoConfiguration {

        public static final String NODE_ENDPOINT_SNITCH_BEAN_NAME = "nodeEndpointSnitch";

        @Bean
        @ConditionalOnMissingBean(name = NODE_ENDPOINT_SNITCH_BEAN_NAME)
        public NodeEndpointSnitch nodeEndpointSnitch(ApplicationContext context) {
            NodeEndpointSnitch endpointSnitch = new NodeEndpointSnitch(context);
            return endpointSnitch;
        }
    }
}
