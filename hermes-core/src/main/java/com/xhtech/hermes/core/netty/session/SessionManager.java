package com.xhtech.hermes.core.netty.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private int maxInactiveInterval = 0;

    private Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

    public SessionManager() {
    }

    public Session create(String id) {
        return create(id, maxInactiveInterval);
    }

    public <T> T create(String id, Class<T> cls) {
        return create(id, maxInactiveInterval, cls);
    }

    public Session create(String id, int maxInactiveInterval) {
        Session session = new Session(id, this);
        session.setMaxInactiveInterval(maxInactiveInterval);
        return session;
    }

    public <T> T create(String id, int maxInactiveInterval, Class<T> cls) {
        try {
            Constructor<T> constructor = cls.getConstructor(SessionManager.class);
            T t = constructor.newInstance(this);
            Session session = (Session) t;
            session.setId(id);
            session.setMaxInactiveInterval(maxInactiveInterval);
            return t;
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public Session get(String id) {
        return sessions.get(id);
    }

    public void processExpires() {
        Session session;

        for (Iterator<Session> iterable = getSessions().iterator(); iterable.hasNext(); ) {
            session = iterable.next();
            if (session.invalid()) {
                session.expire();
            }
        }
    }

    public boolean has(String id) {
        return sessions.containsKey(id);
    }

    public void add(Session session) {
        logger.debug("add session, id:{}", session.getId());
        sessions.put(session.getId(), session);
    }

    public void remove(Session session) {
        logger.debug("remove session, id:{}", session.getId());
        remove(session.getId());
    }

    public void remove(String id) {
        logger.debug("remove session by id, id:{}", id);
        sessions.remove(id);
    }

    public Collection<Session> getSessions() {
        return sessions.values();
    }

    public void clean() {
        logger.debug("clear session");
        sessions.clear();
    }

    public int size() {
        return sessions.size();
    }

    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }
}
