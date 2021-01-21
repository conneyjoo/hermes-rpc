package com.xhtech.hermes.core.schedule;

import com.xhtech.hermes.commons.util.UUIDUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Task {

    private String id;

    private boolean success = false;

    private AtomicBoolean invalid = new AtomicBoolean(false);

    public abstract void execute();

    public String id() {
        if (id == null) {
            id = UUIDUtil.genId();
        }
        return id;
    }

    public Task expire() {
        return invalid.compareAndSet(false, true) ? this : null;
    }

    public boolean success() {
        return success;
    }

    public Task success(boolean success) {
        this.success = success;
        return this;
    }
}
