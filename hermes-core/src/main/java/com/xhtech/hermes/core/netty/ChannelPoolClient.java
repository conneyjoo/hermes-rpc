package com.xhtech.hermes.core.netty;

import com.xhtech.hermes.core.util.LoopChosser;
import com.xhtech.hermes.core.netty.address.ReadWriteAddress;
import com.xhtech.hermes.core.netty.channel.SerialChannelPromise;
import com.xhtech.hermes.core.netty.proto.Proto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.abs;

public abstract class ChannelPoolClient extends NettyClient {

    private static final Logger logger = LoggerFactory.getLogger(ChannelPoolClient.class);

    protected static final AttributeKey<ReadWriteAddress> KEY_REMOTE_ADDRESS = AttributeKey.newInstance("keyRemoteEndpoint");

    public static final int DEFAULT_CONNECTIONS = 5;

    private int connections = DEFAULT_CONNECTIONS;

    private AbstractChannelPoolMap<ReadWriteAddress, SimpleChannelPool> poolMap;

    private List<RemoteEndpoint> remoteEndpoints = new ArrayList<>();

    private LoopChosser<RemoteEndpoint> loopChosser = new LoopChosser();

    public ChannelPoolClient() {
    }

    public ChannelPoolClient(ApplicationContext springContent) {
        super(springContent);
        createChannelPool();
    }

    public synchronized void createChannelPool() {
        poolMap = new AbstractChannelPoolMap<ReadWriteAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(ReadWriteAddress key) {
                Bootstrap b = new Bootstrap();
                b.group(NettyContext.get().getWorkerEventLoopGroup(ChannelPoolClient.this.getName()));
                b.channel(getNettyContext().getChannelClass());
                b.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true).attr(KEY_REMOTE_ADDRESS, key);
                return new FixedChannelPool(b.remoteAddress(key.getSocketAddress()), ChannelPoolClient.this, ChannelPoolClient.this.getConnections());
            }
        };
    }

    @Override
    public void accept(ChannelHandlerContext ctx) {
        super.accept(ctx);

        ReadWriteAddress remoteAddress = ctx.channel().attr(KEY_REMOTE_ADDRESS).get();
        RemoteEndpoint remoteEndpoint = getRemoteEndpoint(remoteAddress);

        if (remoteAddress.isWrite() && remoteEndpoint != null) {
            connect(remoteEndpoint.readAddress);
            remoteEndpoint.connected = true;
        }

        logger.info("Connection({}) established successfully with the server({})", remoteAddress.getStyleName(), ctx);
    }

    public synchronized boolean isConnected(ReadWriteAddress address) {
        return poolMap.contains(address);
    }

    public synchronized void connect(ReadWriteAddress address) {
        logger.info("Connecting to server --> {}:{}", address.getAddress(), address.getPort());

        doConnect(address);
    }

    public abstract void doConnect(ReadWriteAddress address);

    public synchronized void disconnect(RemoteEndpoint remoteEndpoint) {
        if (poolMap != null && remoteEndpoint != null) {
            poolMap.remove(remoteEndpoint.readAddress);
            poolMap.remove(remoteEndpoint.writeAddress);
        }
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections <= 0 ? DEFAULT_CONNECTIONS : connections;
    }

    public ChannelPoolMap<ReadWriteAddress, SimpleChannelPool> getPoolMap() {
        return poolMap;
    }

    public SimpleChannelPool getPool(ReadWriteAddress address) {
        return poolMap.get(address);
    }

    public Future<Channel> acquire(ReadWriteAddress address) {
        return poolMap.get(address).acquire();
    }

    public void send(final String clientId, final Proto proto) {
        int size = remoteEndpoints().size();

        if (size > 0) {
            RemoteEndpoint remoteEndpoint = remoteEndpoints().get(abs(clientId.hashCode()) % size);
            send(remoteEndpoint.writeAddress, proto);
        } else {
            logger.error("No server to send [clientId = {}, proto = {}]", clientId, proto);
        }
    }

    public void send(final ReadWriteAddress address, final Proto proto) {
        final SimpleChannelPool pool = getPool(address);

        pool.acquire().addListener((FutureListener<Channel>) fl -> {
            if (fl.isSuccess()) {
                Channel ch = null;

                try {
                    ch = fl.getNow();
                    ch.writeAndFlush(proto);
                } finally {
                    if (ch != null) {
                        pool.release(ch);
                    }
                }
            }
        });
    }

    public <T> T sendAndRecv(final T t) {
        if (remoteEndpoints.size() > 0) {
            RemoteEndpoint remoteEndpoint = loopChosser.choose(remoteEndpoints);
            return sendAndRecv(remoteEndpoint.writeAddress, t);
        }

        return null;
    }

    public <T> T sendAndRecv(final ReadWriteAddress address, final T t) {
        return sendAndRecv(address, t, SerialChannelPromise.DEFAULT_RECV_TIMEOUT);
    }

    public <T> T sendAndRecv(final ReadWriteAddress address, final T t, long timeout) {
        SimpleChannelPool pool = getPool(address);

        try {
            Future<Channel> future = pool.acquire().sync();
            if (future.isSuccess()) {
                Channel ch = null;

                try {
                    ch = future.getNow();
                    SerialChannelPromise<T> promise = new SerialChannelPromise(ch, ch.eventLoop());
                    return promise.writeAndFlush(t, timeout);
                } finally {
                    pool.release(ch);
                }
            }

            return null;
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void close(ChannelHandlerContext ctx) {
        super.close(ctx);

        ReadWriteAddress readWriteAddress = ctx.channel().attr(KEY_REMOTE_ADDRESS).get();
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        RemoteEndpoint remoteEndpoint = getRemoteEndpoint(socketAddress.getAddress().getHostAddress(), socketAddress.getPort());

        poolMap.remove(readWriteAddress);

        logger.info("Lost connection({}) to server({}) [remoteEndpoint = {}]", readWriteAddress.getStyleName(), ctx, remoteEndpoint);

        if (remoteEndpoint != null ) {
            remoteEndpoint.connected = false;

            if (readWriteAddress.isWrite()) {
                connect(remoteEndpoint.writeAddress);
            } else {
                doConnect(remoteEndpoint.readAddress);
            }
        }
    }

    public void destroy() {
        poolMap.close();
    }

    public String getName() {
        return null;
    }

    public synchronized RemoteEndpoint addRemoteEndpoint(String host, int port) {
        RemoteEndpoint remoteEndpoint = getRemoteEndpoint(host, port);

        if (remoteEndpoint != null) {
            return remoteEndpoint;
        } else {
            remoteEndpoint = new RemoteEndpoint(host, port);
            remoteEndpoints.add(remoteEndpoint);
            connect(remoteEndpoint.writeAddress);
            return remoteEndpoint;
        }
    }

    public synchronized RemoteEndpoint removeRemoteEndpoint(String host, int port) {
        for (Iterator<RemoteEndpoint> iterator = remoteEndpoints.iterator(); iterator.hasNext(); ) {
            RemoteEndpoint remoteEndpoint = iterator.next();

            if (remoteEndpoint.host.equals(host) && remoteEndpoint.port == port) {
                iterator.remove();
                disconnect(remoteEndpoint);
                return remoteEndpoint;
            }
        }

        return null;
    }

    public RemoteEndpoint getRemoteEndpoint(ReadWriteAddress address) {
        return getRemoteEndpoint(address.getAddress().getHostAddress(), address.getPort());
    }

    public RemoteEndpoint getRemoteEndpoint(String host, int port) {
        for (Iterator<RemoteEndpoint> iterator = remoteEndpoints.iterator(); iterator.hasNext(); ) {
            RemoteEndpoint remoteEndpoint = iterator.next();

            if (remoteEndpoint.host.equals(host) && remoteEndpoint.port == port) {
                return remoteEndpoint;
            }
        }

        return null;
    }

    public List<RemoteEndpoint> remoteEndpoints() {
        return remoteEndpoints;
    }

    public static class RemoteEndpoint {

        String host;

        int port;

        ReadWriteAddress readAddress;

        ReadWriteAddress writeAddress;

        volatile boolean connected = false;

        public RemoteEndpoint(String host, int port) {
            this.host = host;
            this.port = port;
            this.readAddress = new ReadWriteAddress(host, port, ReadWriteAddress.READ_ONLY_STYLE);
            this.writeAddress = new ReadWriteAddress(host, port, ReadWriteAddress.WRITE_ONLY_STYLE);
        }

        public RemoteEndpoint(ReadWriteAddress readAddress) {
            this.host = readAddress.getAddress().getHostAddress();
            this.port = readAddress.getPort();
            this.readAddress = readAddress;
            this.writeAddress = new ReadWriteAddress(host, port, ReadWriteAddress.WRITE_ONLY_STYLE);
        }

        public ReadWriteAddress getReadAddress() {
            return readAddress;
        }

        public ReadWriteAddress getWriteAddress() {
            return writeAddress;
        }

        public boolean isConnected() {
            return connected;
        }

        @Override
        public String toString() {
            return host + ":" + port;
        }
    }
}
