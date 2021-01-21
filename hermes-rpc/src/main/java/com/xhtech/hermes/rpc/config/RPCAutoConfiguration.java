package com.xhtech.hermes.rpc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(RPCAutoConfiguration.HERMES_RPC_PACKAGE)
@Configuration
public class RPCAutoConfiguration {

    public static final String HERMES_RPC_PACKAGE = "com.xhtech.hermes.rpc";
}
