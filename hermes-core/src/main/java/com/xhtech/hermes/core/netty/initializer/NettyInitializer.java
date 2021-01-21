package com.xhtech.hermes.core.netty.initializer;

import com.xhtech.hermes.core.netty.NettyContext;
import com.xhtech.hermes.core.netty.codec.ProtoDecoder;
import com.xhtech.hermes.core.netty.codec.ProtoEncoder;
import com.xhtech.hermes.core.netty.handler.Dispatcher;
import com.xhtech.hermes.core.netty.handler.NettyHandler;
import com.xhtech.hermes.core.netty.handler.SocketEventHandler;
import com.xhtech.hermes.core.netty.proto.Proto;
import com.xhtech.hermes.core.netty.session.ClientSessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class NettyInitializer extends ChannelInitializer<Channel> implements SocketEventHandler {

    protected ApplicationContext springContent;

    protected ClientSessionManager sessionManager;

    protected NettyContext nettyContext;

    protected Dispatcher dispatcher;

    protected ThreadLocal<ProtoDecoder> decoders = ThreadLocal.withInitial(() -> createProtoDecode());

    protected ProtoEncoder encoder;

    protected NettyHandler handler;

    private AtomicBoolean afterPropertiesSetted = new AtomicBoolean(false);

    public NettyInitializer() {
    }

    public NettyInitializer(ApplicationContext springContent) {
        this.springContent = springContent;
    }

    public void afterPropertiesSet() {
        if (springContent == null) {
            throw new IllegalArgumentException("Spring content not be null");
        }

        this.sessionManager = createSessionManager();
        this.dispatcher = new Dispatcher(this);
        this.encoder = getEncoder();
        this.handler = getNettyHandler();
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        if (afterPropertiesSetted.compareAndSet(false, true)) {
            afterPropertiesSet();
        }

        initPipeline(ch);
    }

    public void initPipeline(Channel ch) {
        addPipeline(ch, getIdleStateHandler());
        addPipeline(ch, getLengthFieldBasedFrameDecoder());
        addPipeline(ch, decoders.get());

        addPipeline(ch, encoder);
        addPipeline(ch, handler);
    }

    public void addPipeline(Channel ch, ChannelHandler handler) {
        if (handler != null) {
            ch.pipeline().addLast(handler);
        }
    }

    public ProtoDecoder createProtoDecode() {
        ProtoDecoder decoder = getDecoder();
        List<Proto> protocols = new ArrayList<>();

        dispatcher.getProtocols().stream().forEach((e) -> {
            if (isProtoType(e.getValue())) {
                protocols.add(e.getValue());
            }
        });

        decoder.setProtocols(protocols);
        return decoder;
    }

    public synchronized ClientSessionManager createSessionManager() {
        return sessionManager == null ? new ClientSessionManager() : sessionManager;
    }

    public boolean isProtoType(Proto proto) {
        return true;
    }

    public abstract boolean isClient();

    public abstract IdleStateHandler getIdleStateHandler();

    public abstract ByteToMessageDecoder getLengthFieldBasedFrameDecoder();

    public abstract ProtoEncoder getEncoder();

    public abstract ProtoDecoder getDecoder();

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void setSpringContent(ApplicationContext springContent) {
        this.springContent = springContent;
    }

    public ApplicationContext getSpringContent() {
        return springContent;
    }

    public NettyHandler getNettyHandler() {
        return new NettyHandler(dispatcher, this);
    }

    public synchronized NettyContext getNettyContext() {
        if (nettyContext == null) {
            nettyContext = NettyContext.get();
        }
        return nettyContext;
    }

    public ClientSessionManager getSessionManager() {
        return sessionManager;
    }

    public synchronized void setSessionManager(ClientSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}