package com.xhtech.hermes.core.cluster.replication;

public interface Changeable<T> {

    void afterPropertiesSet();

    void update(T t);

    void delete(T t);
}
