package com.xhtech.hermes.core.netty.handler;

import com.xhtech.hermes.core.netty.listener.HandlerListener;
import com.xhtech.hermes.core.netty.session.ClientSessionManager;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

public interface IHandler<T extends Serializable> {

    void process(ChannelHandlerContext ctx, T t);

    void handler(ChannelHandlerContext ctx, T t);

    void addListener(HandlerListener listener);

    void removeListener(HandlerListener listener);

    void fireBeforeHandlerEvent(ChannelHandlerContext ctx, T t);

    void fireHandlerEvent(ChannelHandlerContext ctx, T t);

    boolean isBlocking();

    void setBlocking(boolean blocking);

    ClientSessionManager getSessionManager();

    void setSessionManager(ClientSessionManager sessionManager);
}
