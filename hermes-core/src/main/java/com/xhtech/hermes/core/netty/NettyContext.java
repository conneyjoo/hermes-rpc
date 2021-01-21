package com.xhtech.hermes.core.netty;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import static com.xhtech.hermes.core.schedule.TaskQueueScheduler.DEFAULT_EVENT_LOOP_THREADS;

/**
 * netty启动配置的上下文
 * 根据操作系统的网络模型支持选择socket文件事件
 * linux2.6以上使用epoll
 * 其他操作系统使用selector
 */
public class NettyContext {

    private static final NettyContext INSTANCE = new NettyContext();

    private final Class<? extends ServerChannel> serverChannelClass;
    private final Class<? extends Channel> channelClass;
    private final Class<? extends DatagramChannel> datagramChannelClass;


    private NettyContext() {
        super();

        if (Epoll.isAvailable()) {
            serverChannelClass = EpollServerSocketChannel.class;
            channelClass = EpollSocketChannel.class;
            datagramChannelClass = EpollDatagramChannel.class;
        } else {
            serverChannelClass = NioServerSocketChannel.class;
            channelClass = NioSocketChannel.class;
            datagramChannelClass = NioDatagramChannel.class;
        }
    }

    public Class<? extends ServerChannel> getServerChannelClass() {
        return serverChannelClass;
    }

    public Class<? extends Channel> getChannelClass() {
        return channelClass;
    }

    public EventLoopGroup getBossEventLoopGroup() {
        return getBossEventLoopGroup(null);
    }

    public EventLoopGroup getBossEventLoopGroup(String groupName) {
        if (groupName == null) {
            return Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        } else {
            return Epoll.isAvailable() ? new EpollEventLoopGroup(DEFAULT_EVENT_LOOP_THREADS, new DefaultThreadFactory(groupName)) : new NioEventLoopGroup(DEFAULT_EVENT_LOOP_THREADS, new DefaultThreadFactory(groupName));
        }
    }

    public EventLoopGroup getWorkerEventLoopGroup() {
        return getWorkerEventLoopGroup(null);
    }

    public EventLoopGroup getWorkerEventLoopGroup(String groupName) {
        if (groupName == null) {
            return Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        } else {
            return Epoll.isAvailable() ? new EpollEventLoopGroup(DEFAULT_EVENT_LOOP_THREADS, new DefaultThreadFactory(groupName)) : new NioEventLoopGroup(DEFAULT_EVENT_LOOP_THREADS, new DefaultThreadFactory(groupName));
        }
    }

    public Class<? extends DatagramChannel> getDatagramChannelClass() {
        return datagramChannelClass;
    }

    public static final NettyContext get() {
        return INSTANCE;
    }
}
