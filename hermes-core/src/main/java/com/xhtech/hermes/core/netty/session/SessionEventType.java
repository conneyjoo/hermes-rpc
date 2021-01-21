package com.xhtech.hermes.core.netty.session;

public enum SessionEventType {

    CREATED(0),
    ACTIVE(1),
    DESTROY(2);


    private int type;

    SessionEventType(int type) {
        this.type = type;
    }

    public boolean isCreated() {
        return this.type == CREATED.type;
    }

    public boolean isDestroy() {
        return this.type == DESTROY.type;
    }

    public boolean isActive() {
        return this.type == ACTIVE.type;
    }

    @Override
    public String toString() {
        return "SessionEventType{" +
                "type=" + type +
                '}';
    }
}
