package com.xhtech.hermes.core.cluster.replication;

import com.alibaba.fastjson.JSON;
import com.xhtech.hermes.core.gossip.gms.ApplicationState;
import com.xhtech.hermes.core.gossip.gms.Gossiper;
import com.xhtech.hermes.core.gossip.gms.VersionedValue;

public class Replicate {

    public static final String VALUE_SPLIT = "#";

    public static final String UPDATE_FLAG = "U";

    public static final String DELETE_FLAG = "D";

    private String flag;

    private String name;

    private String value;

    protected Replicate(VersionedValue versionedValue) {
        String[] split = versionedValue.value.split(VALUE_SPLIT);

        if (split.length < 2) {
            throw new IllegalArgumentException(String.format("Illegal of value, format must is '%s' catenate a string(Operator%sClass%sObjectJson)", VALUE_SPLIT, VALUE_SPLIT, VALUE_SPLIT));
        }

        this.flag = split[0];
        this.name = split[1];
        this.value = split[2];
    }

    protected Replicate(String flag, Object object) {
        this.flag = flag;
        this.name = object.getClass().getName();
        this.value = Serialization.serialize(object);
    }

    protected VersionedValue versionedValue() {
        StringBuilder builder = new StringBuilder(name.length() + value.length() + 3);
        builder.append(flag).append(VALUE_SPLIT);
        builder.append(name).append(VALUE_SPLIT);
        builder.append(value);
        return VersionedValue.VersionedValueFactory.instance.getVersionedValue(builder.toString());
    }

    public <T> T object(Class<T> cls) {
        return Serialization.deserialize(value, cls);
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class Builder {

        private String flag;

        private Object object;

        public Replicate.Builder flag(String flag) {
            this.flag = flag;
            return this;
        }

        public Replicate.Builder object(Object object) {
            this.object = object;
            return this;
        }

        public Replicate build() {
            return new Replicate(flag, object);
        }

        public Replicate update(Object object) {
            return new Replicate(UPDATE_FLAG, object);
        }

        public Replicate delete(Object object) {
            return new Replicate(DELETE_FLAG, object);
        }
    }

    public static class Serialization {

        public static String serialize(Object obj) {
            if (obj instanceof String) {
                return (String) obj;
            } else {
                return JSON.toJSONString(obj);
            }
        }

        public static <T> T deserialize(String value, Class<T> cls) {
            return JSON.parseObject(value, cls);
        }
    }

    public static class Trigger {

        public static void update(Object object) {
            Replicate replicate = new Replicate.Builder().update(object);
            Gossiper.instance.addLocalApplicationState(ApplicationState.CHANGE, replicate.versionedValue());
        }

        public static void delete(Object object) {
            Replicate replicate = new Replicate.Builder().delete(object);
            Gossiper.instance.addLocalApplicationState(ApplicationState.CHANGE, replicate.versionedValue());
        }

        /**
         * 通过gossip同步更新操作，不会通知本地节点
         * @param object
         */
        public static void updateWithoutLocalNotify(Object object) {
            Replicate replicate = new Replicate.Builder().update(object);
            Gossiper.instance.addLocalApplicationStateWithoutLocalNotify(ApplicationState.CHANGE, replicate.versionedValue());
        }

        /**
         * 通过gossip同步删除操作，不会通知本地节点
         * @param object
         */
        public static void deleteWithoutLocalNotify(Object object) {
            Replicate replicate = new Replicate.Builder().delete(object);
            Gossiper.instance.addLocalApplicationStateWithoutLocalNotify(ApplicationState.CHANGE, replicate.versionedValue());
        }
    }
}
