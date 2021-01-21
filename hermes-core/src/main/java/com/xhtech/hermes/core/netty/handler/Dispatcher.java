package com.xhtech.hermes.core.netty.handler;

import com.xhtech.hermes.core.netty.annotation.NonBlocking;
import com.xhtech.hermes.core.netty.initializer.NettyInitializer;
import com.xhtech.hermes.core.netty.listener.HandlerListener;
import com.xhtech.hermes.core.netty.proto.Proto;
import com.xhtech.hermes.commons.util.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Dispatcher {

    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    private Map<Byte, IHandler> handlers = new ConcurrentHashMap<>();

    private Collection<HandlerListener> listeners;

    private NettyInitializer nettyInitializer;

    public Dispatcher(NettyInitializer nettyInitializer) {
        this.nettyInitializer = nettyInitializer;
        afterPropertiesSet();
    }

    public void afterPropertiesSet() {
        listeners = nettyInitializer.getSpringContent().getBeansOfType(HandlerListener.class).values();
        listeners.stream().filter((e) -> nettyInitializer.isProtoType(getProto(e))).collect(Collectors.toList());

        Map<String, IHandler> beans = nettyInitializer.getSpringContent().getBeansOfType(IHandler.class);
        Set<Map.Entry<String, IHandler>> entries = beans.entrySet();

        for (Map.Entry<String, IHandler> entry : entries) {
            register(entry.getValue());
        }
    }

    public void register(IHandler handler) {
        Proto proto = getProto(handler);

        if (nettyInitializer.isProtoType(proto)) {
            handlers.put(proto.getCmd(), handler);
            bindListener(handler, proto.getCmd());
            handler.setBlocking(!handler.getClass().isAnnotationPresent(NonBlocking.class));
            handler.setSessionManager(nettyInitializer.getSessionManager());
        }
    }

    public IHandler dispatch(Byte cmd) {
        return handlers.get(cmd);
    }

    public void bindListener(IHandler handler, Byte cmd) {
        if (listeners != null) {
            for (HandlerListener listener : listeners) {
                if (cmd.equals(getCmd(listener))) {
                    handler.addListener(listener);
                }
            }
        }
    }

    public Byte getCmd(Object obj) {
        Proto proto = getProto(obj);
        return proto != null ? proto.getCmd() : null;
    }

    public Proto getProto(Object obj) {
        try {
            return (Proto) ReflectUtils.getGenericParameterType(obj.getClass()).newInstance();
        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public Set<Map.Entry<String, Proto>> getProtocols() {
        return nettyInitializer.getSpringContent().getBeansOfType(Proto.class).entrySet();
    }
}