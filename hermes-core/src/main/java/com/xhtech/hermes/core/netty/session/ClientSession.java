package com.xhtech.hermes.core.netty.session;

import com.xhtech.hermes.core.util.LoopChosser;
import com.xhtech.hermes.core.netty.proto.Proto;
import com.xhtech.hermes.core.netty.channel.SerialChannelPromise;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientSession extends Session {

    private static final Logger logger = LoggerFactory.getLogger(ClientSession.class);

    private Set<ChannelHandlerContext> contexts = Collections.synchronizedSet(new HashSet<>());

    private LoopChosser<ChannelHandlerContext> loopChosser = new LoopChosser<ChannelHandlerContext>();

    private volatile boolean closed;

    private AtomicInteger hbTimes = new AtomicInteger(0);

    public ClientSession(ChannelHandlerContext ctx, SessionManager manager) {
        super(manager);
        addContext(ctx);
    }

    @Override
    public void setId(String id) {
        this.id = id;

        if (!this.manager.has(id)) {
            this.manager.add(this);
            tellNew();
        }
    }

    public void close(ChannelHandlerContext ctx) {
        synchronized (this) {
            if (!isClosed()) {
                ctx.close();
                contexts.remove(ctx);
                loopChosser.setArray(contexts.toArray(new ChannelHandlerContext[]{}));

                if (contexts.size() == 0) {
                    closed = true;
                    expire();
                }
            }
        }
    }

    public void close() {
        synchronized (this) {
            if (!isClosed()) {
                ChannelHandlerContext ctx;
                for (Iterator<ChannelHandlerContext> iterator = contexts.iterator(); iterator.hasNext(); ) {
                    ctx = iterator.next();
                    ctx.close();
                }

                contexts.clear();
                loopChosser.setArray(contexts.toArray(new ChannelHandlerContext[]{}));

                if (contexts.size() == 0) {
                    closed = true;
                    expire();
                }
            }
        }
    }

    public void addContext(ChannelHandlerContext ctx) {
        for (ChannelHandlerContext context : contexts) {
            if (context.equals(ctx)) {
                return;
            }
        }

        contexts.add(ctx);
        loopChosser.setArray(contexts.toArray(new ChannelHandlerContext[]{}));
    }

    public <T> T sendAndRecv(T t) {
        return sendAndRecv(t, SerialChannelPromise.DEFAULT_RECV_TIMEOUT);
    }

    public boolean send(Proto proto) {
        ChannelHandlerContext ctx = loopChosser.choose();

        if (ctx != null) {
            ctx.writeAndFlush(proto);
            return true;
        } else {
            return false;
        }
    }

    public <T> T sendAndRecv(T t, long timeout) {
        ChannelHandlerContext context = loopChosser.choose();
        SerialChannelPromise<T> promise = new SerialChannelPromise(context.channel(), context.executor());
        return promise.writeAndFlush(t, timeout);
    }

    public void sendAll(Proto proto) {
        Set<ChannelHandlerContext> contexts = getContexts();

        for (ChannelHandlerContext context : contexts) {
            context.writeAndFlush(proto);
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public String getClientIp() {
        return getRemoteAddress().getAddress().getHostAddress();
    }

    public InetSocketAddress getRemoteAddress() {
        if (contexts.size() == 0) {
            throw new IllegalStateException("Session is already closed.");
        }

        return (InetSocketAddress) contexts.iterator().next().channel().remoteAddress();
    }

    public Set<ChannelHandlerContext> getContexts() {
        return contexts;
    }

    public int heartbeat() {
        return hbTimes.incrementAndGet();
    }

    @Override
    public String toString() {
        return String.format("ClientSession {hbTimes = %d, isClosed = %b, context size = %d}", hbTimes.get(), closed, contexts.size());
    }
}
