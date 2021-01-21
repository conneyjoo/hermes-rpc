package com.xhtech.hermes.core.gossip.locator;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */


import com.xhtech.hermes.core.gossip.gms.ApplicationState;
import com.xhtech.hermes.core.gossip.gms.EndpointState;
import com.xhtech.hermes.core.gossip.gms.IEndpointStateChangeSubscriber;
import com.xhtech.hermes.core.gossip.gms.VersionedValue;
import com.xhtech.hermes.core.gossip.net.ServerAddress;


/**
 * 1) Snitch will automatically set the public IP by querying the AWS API
 * <p>
 * 2) Snitch will set the private IP as a Gossip application state.
 * <p>
 * 3) Snitch implements IESCS and will reset the connection if it is within the
 * same region to communicate via private IP.
 * <p>
 * Implements Ec2Snitch to inherit its functionality and extend it for
 * Multi-Region.
 * <p>
 * Operational: All the nodes in this cluster needs to be able to (modify the
 * Security group settings in AWS) communicate via Public IP's.
 */
public class EndpointSnitch implements IEndpointStateChangeSubscriber {

    @Override
    public void onAlive(ServerAddress endpoint, EndpointState state) {
    }

    @Override
    public void onChange(ServerAddress endpoint, ApplicationState state, VersionedValue value) {
    }

    @Override
    public void onDead(ServerAddress endpoint, EndpointState state) {
    }

    @Override
    public void onJoin(ServerAddress endpoint, EndpointState epState) {
    }

    @Override
    public void onRemove(ServerAddress endpoint) {
    }

    @Override
    public void onRestart(ServerAddress endpoint, EndpointState state) {
    }
}
