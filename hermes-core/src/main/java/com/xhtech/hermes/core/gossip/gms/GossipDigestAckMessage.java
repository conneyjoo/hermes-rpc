/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xhtech.hermes.core.gossip.gms;

import com.xhtech.hermes.core.gossip.io.IVersionedSerializer;
import com.xhtech.hermes.core.gossip.net.ServerAddress;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This mesg gets sent out as a result of the receipt of a GossipDigestSynMessage by an
 * endpoint. This is the 2 stage of the 3 way messaging in the Gossip protocol.
 */

class GossipDigestAckMessage {
    private static IVersionedSerializer<GossipDigestAckMessage> serializer_;

    static {
        serializer_ = new GossipDigestAckMessageSerializer();
    }

    List<GossipDigest> gDigestList_ = new ArrayList<GossipDigest>();
    Map<ServerAddress, EndpointState> epStateMap_ = new HashMap<ServerAddress, EndpointState>();

    static IVersionedSerializer<GossipDigestAckMessage> serializer() {
        return serializer_;
    }

    GossipDigestAckMessage(List<GossipDigest> gDigestList, Map<ServerAddress, EndpointState> epStateMap) {
        gDigestList_ = gDigestList;
        epStateMap_ = epStateMap;
    }

    List<GossipDigest> getGossipDigestList() {
        return gDigestList_;
    }

    Map<ServerAddress, EndpointState> getEndpointStateMap() {
        return epStateMap_;
    }
}

class GossipDigestAckMessageSerializer implements IVersionedSerializer<GossipDigestAckMessage> {
    public void serialize(GossipDigestAckMessage gDigestAckMessage, DataOutput dos) throws IOException {
        GossipDigestSerializationHelper.serialize(gDigestAckMessage.gDigestList_, dos);
        dos.writeBoolean(true); // 0.6 compatibility
        EndpointStatesSerializationHelper.serialize(gDigestAckMessage.epStateMap_, dos);
    }

    public GossipDigestAckMessage deserialize(DataInput dis) throws IOException {
        List<GossipDigest> gDigestList = GossipDigestSerializationHelper.deserialize(dis);
        dis.readBoolean(); // 0.6 compatibility
        Map<ServerAddress, EndpointState> epStateMap = EndpointStatesSerializationHelper.deserialize(dis);
        return new GossipDigestAckMessage(gDigestList, epStateMap);
    }

    public long serializedSize(GossipDigestAckMessage gossipDigestAckMessage) {
        throw new UnsupportedOperationException();
    }
}
