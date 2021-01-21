package com.xhtech.hermes.core.netty;

import com.xhtech.hermes.core.netty.initializer.NettyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class NettyServer extends NettyInitializer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private AtomicBoolean running = new AtomicBoolean(false);

    private ServerBootstrap serverBootstrap;

    public NettyServer() {
    }

    public NettyServer(ApplicationContext springContent) {
        super(springContent);
    }

    public void start(int port) {
        if (running.compareAndSet(false, true)) {
            new Thread(() -> {
                String name = getName();
                EventLoopGroup bossGroup = getNettyContext().getBossEventLoopGroup(name);
                EventLoopGroup workerGroup = getNettyContext().getWorkerEventLoopGroup(name);

                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup);
                    b.channel(getNettyContext().getServerChannelClass());
                    b.childHandler(this);
                    serverBootstrap = b;

                    logger.info("Server listen port: {}", port);

                    ChannelFuture f = b.bind(port).sync();
                    f.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    logger.error("Server start error", e);
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                    running.set(false);
                }
            }).start();
        } else {
            logger.info("Server already started");
        }
    }

    public void stop(){
        logger.info("stop netty server");
        if (!isRunning() || serverBootstrap == null) {
            logger.warn("Netty server has not been started");
            return;
        }

        EventLoopGroup bossGroup = serverBootstrap.config().group();
        EventLoopGroup workerGroup = serverBootstrap.config().childGroup();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void accept(ChannelHandlerContext ctx) {
    }

    @Override
    public void heartbeat(ChannelHandlerContext ctx) {
    }

    @Override
    public void close(ChannelHandlerContext ctx) {
    }

    @Override
    public boolean isClient() {
        return false;
    }

    public String getName() {
        return null;
    }
}