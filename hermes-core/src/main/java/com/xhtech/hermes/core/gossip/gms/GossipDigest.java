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
import com.xhtech.hermes.core.gossip.net.CompactEndpointSerializationHelper;
import com.xhtech.hermes.core.gossip.net.ServerAddress;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Contains information about a specified list of Endpoints and the largest version
 * of the state they have generated as known by the local endpoint.
 */

public class GossipDigest implements Comparable<GossipDigest> {
    private static IVersionedSerializer<GossipDigest> serializer;

    static {
        serializer = new GossipDigestSerializer();
    }

    ServerAddress endpoint;
    int generation;
    int maxVersion;

    public static IVersionedSerializer<GossipDigest> serializer() {
        return serializer;
    }

    GossipDigest(ServerAddress ep, int gen, int version) {
        endpoint = ep;
        generation = gen;
        maxVersion = version;
    }

    ServerAddress getEndpoint() {
        return endpoint;
    }

    int getGeneration() {
        return generation;
    }

    int getMaxVersion() {
        return maxVersion;
    }

    public int compareTo(GossipDigest gDigest) {
        if (generation != gDigest.generation)
            return (generation - gDigest.generation);
        return (maxVersion - gDigest.maxVersion);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(endpoint);
        sb.append(":");
        sb.append(generation);
        sb.append(":");
        sb.append(maxVersion);
        return sb.toString();
    }
}

class GossipDigestSerializer implements IVersionedSerializer<GossipDigest> {
    public void serialize(GossipDigest gDigest, DataOutput dos) throws IOException {
        CompactEndpointSerializationHelper.serialize(gDigest.endpoint, dos);
        dos.writeInt(gDigest.generation);
        dos.writeInt(gDigest.maxVersion);
    }

    public GossipDigest deserialize(DataInput dis) throws IOException {
        ServerAddress endpoint = CompactEndpointSerializationHelper.deserialize(dis);
        int generation = dis.readInt();
        int maxVersion = dis.readInt();
        return new GossipDigest(endpoint, generation, maxVersion);
    }

    public long serializedSize(GossipDigest gossipDigest) {
        throw new UnsupportedOperationException();
    }
}
