package com.xhtech.hermes.core.netty.handler;


import com.xhtech.hermes.core.netty.listener.HandlerListener;
import com.xhtech.hermes.core.netty.session.ClientSessionManager;
import com.xhtech.hermes.core.schedule.Task;
import com.xhtech.hermes.core.schedule.TaskQueueScheduler;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractHandler<T extends Serializable> implements IHandler<T> {

    private static volatile TaskQueueScheduler taskQueueScheduler;

    private boolean blocking = false;

    private List<HandlerListener<T>> listeners = new LinkedList<>();

    private ClientSessionManager sessionManager;

    @Override
    public void process(ChannelHandlerContext ctx, T t) {
        if (isBlocking()) {
            doHandler(ctx, t);
        } else {
            addTask(new Task() {
                @Override
                public void execute() {
                    doHandler(ctx, t);
                }
            });
        }
    }

    private void doHandler(ChannelHandlerContext ctx, T t) {
        fireBeforeHandlerEvent(ctx, t);
        handler(ctx, t);
        fireHandlerEvent(ctx, t);
    }

    @Override
    public void addListener(HandlerListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HandlerListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void fireBeforeHandlerEvent(ChannelHandlerContext ctx, T t) {
        for (HandlerListener listener : listeners) {
            listener.beforeHandler(ctx, t);
        }
    }

    @Override
    public void fireHandlerEvent(ChannelHandlerContext ctx, T t) {
        for (HandlerListener listener : listeners) {
            listener.handler(ctx, t);
        }
    }

    public void addTask(Task task) {
        if (taskQueueScheduler == null) {
            synchronized (this) {
                if (taskQueueScheduler == null) {
                    taskQueueScheduler = new TaskQueueScheduler(64, getClass().getSimpleName());
                }
            }
        }

        taskQueueScheduler.add(task);
    }

    @Override
    public boolean isBlocking() {
        return blocking;
    }

    @Override
    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    @Override
    public ClientSessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public void setSessionManager(ClientSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public static TaskQueueScheduler getTaskQueueScheduler() {
        return taskQueueScheduler;
    }
}
