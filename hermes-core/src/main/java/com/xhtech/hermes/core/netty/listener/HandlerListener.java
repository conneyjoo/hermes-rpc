package com.xhtech.hermes.core.netty.listener;

import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

public interface HandlerListener<T extends Serializable> {

    void beforeHandler(ChannelHandlerContext ctx, T t);

    void handler(ChannelHandlerContext ctx, T t);
}
