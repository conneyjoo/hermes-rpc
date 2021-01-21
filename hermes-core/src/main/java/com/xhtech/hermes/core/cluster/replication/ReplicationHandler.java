package com.xhtech.hermes.core.cluster.replication;

import com.xhtech.hermes.core.gossip.gms.VersionedValue;
import com.xhtech.hermes.commons.util.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.xhtech.hermes.core.cluster.replication.Replicate.DELETE_FLAG;
import static com.xhtech.hermes.core.cluster.replication.Replicate.UPDATE_FLAG;

public class ReplicationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReplicationHandler.class);

    private Map<String, Entry> changeables = new HashMap<>();

    public ReplicationHandler(ApplicationContext context) {
        Collection<Changeable> values = context.getBeansOfType(Changeable.class).values();

        for (Changeable changeable : values) {
            changeable.afterPropertiesSet();
        }

        for (Changeable changeable : values) {
            Class<?> type = getType(changeable);
            changeables.put(type.getName(), new Entry(type, changeable));
        }
    }

    public Class<?> getType(Object obj) {
        return ReflectUtils.getGenericParameterType(obj.getClass());
    }

    public void handle(VersionedValue versionedValue) {
        try {
            Replicate replicate = new Replicate(versionedValue);
            Entry entry = changeables.get(replicate.getName());

            if (entry != null) {
                switch (replicate.getFlag()) {
                    case UPDATE_FLAG:
                        entry.update(replicate);
                        break;
                    case DELETE_FLAG:
                        entry.delete(replicate);
                        break;
                    default:
                        break;
                }
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    class Entry {

        Class<?> cls;

        Changeable changeable;

        Entry(Class<?> cls, Changeable changeable) {
            this.cls = cls;
            this.changeable = changeable;
        }

        void update(Replicate replicate) {
            changeable.update(replicate.object(cls));
        }

        void delete(Replicate replicate) {
            changeable.delete(replicate.object(cls));
        }
    }
}
