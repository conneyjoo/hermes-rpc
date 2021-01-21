/**
 * Created by louis on 2020/10/12.
 */
package com.xhtech.hermes.core.others;

/**
 * com.xhtech.hermes.core.others.IShutdownApplication
 *
 * @author: louis
 * @time: 2020/10/12 1:25 PM
 */
public interface IShutdownApplication {
    /**
     * 关闭webServer
     * @param shutdownAwaitTermination shutdown等待时间， ms
     * @return
     */
    boolean shutdownWebServer(long shutdownAwaitTermination);

    /**
     * shutdown线程池
     * @return
     */
    boolean shutdownThreadPools();

    /**
     * shutdown MQ
     * @return
     */
    boolean shutdownMQ();

    /**
     * shutdown Netty
     * @return
     */
    boolean shutdownNetty();

    /**
     * shutdown gossip
     * @return
     */
    boolean shutdownGossip();
}
