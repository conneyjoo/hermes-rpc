package com.xhtech.hermes.core.netty.channel;

import com.xhtech.hermes.core.netty.proto.Proto;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InterlocutionPipeline {

    private static final Logger logger = LoggerFactory.getLogger(InterlocutionPipeline.class);

    private Map<String, ConcurrentLinkedQueue<SerialChannelPromise>> map = new ConcurrentHashMap<>();

    public static final AttributeKey<InterlocutionPipeline> SERIAL_CHANNEL_PIPELINE_KEY = AttributeKey.valueOf("serialChannelPipelineKey");

    public void answers(Proto proto, Channel channel) {
        ConcurrentLinkedQueue<SerialChannelPromise> cfl = get(proto, channel);

        if (cfl != null && !cfl.isEmpty()) {
            cfl.poll().wakeUp(proto);
        }
    }

    private String toKey(Object obj, Channel channel) {
        InetSocketAddress sa = (InetSocketAddress) channel.remoteAddress();
        return String.format("%s:%s:%d", obj.getClass().getSimpleName(), sa.getAddress().getHostAddress(), sa.getPort());
    }

    private ConcurrentLinkedQueue<SerialChannelPromise> get(Proto proto, Channel channel) {
        return map.get(toKey(proto, channel));
    }

    public void ask(Object obj, SerialChannelPromise promise) {
        ConcurrentLinkedQueue<SerialChannelPromise> cfl;
        String key = toKey(obj, promise.channel());

        if ((cfl = map.get(key)) == null) {
            cfl = new ConcurrentLinkedQueue();
            ConcurrentLinkedQueue old = map.putIfAbsent(key, cfl);
            cfl = old != null ? old : cfl;
        }

        cfl.offer(promise);
    }

    public boolean remove(Object obj, SerialChannelPromise promise) {
        ConcurrentLinkedQueue<SerialChannelPromise> cfl = map.get(toKey(obj, promise.channel()));
        return cfl != null ? cfl.remove(promise) : false;
    }
}
