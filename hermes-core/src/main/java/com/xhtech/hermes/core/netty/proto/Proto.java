package com.xhtech.hermes.core.netty.proto;

import io.netty.buffer.ByteBuf;

import java.io.Serializable;

public interface Proto extends Serializable {

    boolean support(byte cmd);

    byte getCmd();

    ByteBuf encode();

    Proto decode(ByteBuf buf);

    String toSimpleString();
}
