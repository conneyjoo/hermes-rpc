package com.xhtech.hermes.core.gossip.gms;

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
public enum ApplicationState {

    /* 节点状态 */
    STATUS,

    /* 节点负载 */
    LOAD,

    /* 节点权重 */
    WEIGHT,

    /* 程序版本 */
    VERSION,

    /* 节点ID */
    ID,

    /* 节点类型 */
    TYPE,

    /* 节点业务数据改变(比如: 通过该state将本地缓存改动的内容传递给其他节点) */
    CHANGE,

    /* 本节点和对端的连接网络(比如: message -> conmunication, conmunication -> message的连接信息) */
    NETWORK
}
