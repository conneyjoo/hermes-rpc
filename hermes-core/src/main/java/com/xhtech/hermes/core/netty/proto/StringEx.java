package com.xhtech.hermes.core.netty.proto;

import java.nio.charset.Charset;

public class StringEx {

    public static final StringEx EMPTY = new StringEx("");

    private String value;

    private byte[] bytes;

    private Charset charset = null;

    public StringEx(byte[] bytes) {
        this.bytes = bytes;
        this.value = new String(bytes);
    }

    public StringEx(String value) {
        this.value = value;
    }

    public StringEx(String value, Charset charset) {
        this.value = value;
        this.charset = charset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public byte[] getBytes() {
        if (bytes == null) {
            bytes = charset != null ? value.getBytes(charset) : value.getBytes();
        }
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int length() {
        return getBytes().length + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StringEx stringEx = (StringEx) o;
        return value.equals(stringEx.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
