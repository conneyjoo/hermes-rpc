package com.xhtech.hermes.core.localevent;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * EventDispatcher, 已经使用单例， 不要注入bean, 这样可以在其他不是spring bean的对象中使用
 */
@Slf4j
public class EventDispatcher {

    /**
     * eventBus
     */
    private EventBus eventBus;


    /**
     * 单例获取EventDispatcher
     * @return
     */
    public static EventDispatcher instance() {
        return EventDispatcher.EventDispatcherHolder.instance;
    }

    /**
     * 注册事件
     * @param object
     */
    public void register(Object object) {
        eventBus.register(object);
    }

    /**
     * 注销事件
     * @param object
     */
    public void unregister(Object object) {
        eventBus.unregister(object);
    }

    /**
     * 发送事件
     * @param event
     */
    public void post(Object event) {
        eventBus.post(event);
    }


    private static class EventDispatcherHolder {
        public static final EventDispatcher instance = new EventDispatcher();
    }

    private EventDispatcher() {
        initEventBus();
    }

    private void initEventBus() {
        log.debug("initEventBus");
        eventBus = new AsyncEventBus(new ThreadPoolExecutor(2, 20,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "XH_EventPool_" + r.hashCode());
            }
        }, new ThreadPoolExecutor.DiscardOldestPolicy()));
    }

}
