package com.xhtech.hermes.core.netty.proto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public abstract class AbstractProto implements Proto {

    public boolean support(byte cmd) {
        return cmd == getCmd();
    }

    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer(available());
        writeHeader(buf);
        writeBody(buf);
        return buf;
    }

    public Proto decode(ByteBuf buf) {
        readHeader(buf);
        readBody(buf);
        return this;
    }

    public abstract void writeHeader(ByteBuf buf);

    public abstract void writeBody(ByteBuf buf);

    public abstract void readHeader(ByteBuf buf);

    public abstract void readBody(ByteBuf buf);

    public abstract int bodyLength();

    public abstract int available();

    public void writeBytes(ByteBuf buf, byte[] bytes) {
        writeBytes(buf, bytes, bytes.length);
    }

    public void writeBytes(ByteBuf buf, byte[] bytes, int length) {
        buf.writeBytes(bytes, 0, length);
    }

    public void writeString(ByteBuf buf, StringEx s) {
        buf.writeByte(s.getBytes().length & 0xFF);
        buf.writeBytes(s.getBytes());
    }

    public void writeInts(ByteBuf buf, Integer[] array) {
        writeInts(buf, array, array.length);
    }

    public void writeInts(ByteBuf buf, Integer[] array, int length) {
        buf.writeInt(length);
        for (int i = 0; i < length; i++) {
            buf.writeInt(array[i]);
        }
    }

    public byte[] readBytes(ByteBuf buf, int length) {
        byte[] array = new byte[length];
        buf.readBytes(array);
        return array;
    }

    public StringEx readString(ByteBuf buf) {
        int length = buf.readByte() & 0xFF;

        if (length == 0) {
            return StringEx.EMPTY;
        }

        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new StringEx(bytes);
    }

    public Integer[] readInts(ByteBuf buf) {
        int length = buf.readInt();
        return readInts(buf, length);
    }

    public Integer[] readInts(ByteBuf buf, int length) {
        Integer[] array = new Integer[length];
        for (int i = 0; i < length; i++) {
            array[i] = buf.readInt();
        }
        return array;
    }

    public static double intToDouble(int i) {
        return ((i >> 8) + ((i & 0xFF)) / 100D);
    }

    public static int doubleToInt(double d) {
        double decimal = d - (int) d;
        int prefix = (int) d;
        int suffix = (int) (decimal * 100);
        return ((prefix & 0xFFFFFF) << 8) | suffix & 0xFF;
    }

    @Override
    public String toSimpleString() {
        return toString();
    }

    public String toHexString() {
        return toHexString(encode(), this.getClass());
    }

    public static String toHexString(ByteBuf buf, Class<?> cls) {
        int length = buf.readableBytes();
        StringBuffer buffer = new StringBuffer();

        buffer.append(String.format("\n%s(%d)\n", cls.getSimpleName(), length));

        for (int i = 0; i < length; i++) {
            int b = buf.getByte(i) & 0xFF;
            buffer.append(String.format(b > 0xF ? "%s " : "0%s ", Integer.toHexString(b)));
        }

        return buffer.toString();
    }
}
