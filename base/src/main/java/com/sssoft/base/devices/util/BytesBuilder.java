package com.sssoft.base.devices.util;

import java.nio.ByteBuffer;

public class BytesBuilder {
    ByteBuffer buffer;
    byte space;
    public static byte SPACE = 32;

    public String toString(String charset) throws Exception {
        return new String(this.toBytes(), charset);
    }

    public byte[] toBytes() {
        return this.buffer.array();
    }

    public ByteBuffer byteBuffer() {
        return this.buffer;
    }

    public void position(int newPosition) {
        this.buffer.position(newPosition);
    }

    public int position() {
        return this.buffer.position();
    }

    public void setSpace(byte b) {
        this.space = b;
    }

    public void allocateNew(int capacity) {
        byte[] oldbytes = null;
        if (capacity != 0) {
            if (this.buffer == null) {
                oldbytes = new byte[0];
            } else {
                if (this.buffer.remaining() >= capacity) {
                    return;
                }

                oldbytes = this.buffer.array();
            }

            this.buffer = ByteBuffer.allocate(oldbytes.length + capacity);
            this.buffer.put(oldbytes);
        }
    }

    public BytesBuilder() {
        this.space = SPACE;
    }

    public BytesBuilder(int limit) {
        this.space = SPACE;
        this.buffer = ByteBuffer.allocate(limit);
    }

    public BytesBuilder(byte[] buff) {
        this.space = SPACE;
        this.buffer = ByteBuffer.wrap(buff);
    }

    public BytesBuilder(byte[] buff, int off, int len) {
        this.space = SPACE;
        this.buffer = ByteBuffer.wrap(buff, off, len);
    }

    public void append(byte[] b, int off, int len) {
        this.allocateNew(len);
        int limit = b.length - off;
        int slen = 0;
        if (len > limit) {
            len = limit;
            slen = limit - limit;
        }

        this.buffer.put(b, off, len);
        if (slen > 0) {
            this.appendSpace(slen);
        }

    }

    public void append(byte[] b) {
        this.append(b, 0, b.length);
    }

    public void append(byte[] tmpBuff, int len) {
        this.append(tmpBuff, 0, len);
    }

    void appendSpace(int len) {
        this.append(this.space, len);
    }

    public void append(byte b, int len) {
        this.allocateNew(len);

        for(int i = 0; i < len; ++i) {
            this.buffer.put(b);
        }

    }

    public void append(byte b) {
        this.append(b, 1);
    }

    public void appendInt(int i) {
        this.allocateNew(32);
        this.buffer.putInt(i);
    }

    public void appendLong(long l) {
        this.allocateNew(64);
        this.buffer.putLong(l);
    }

    public int getInt() {
        return this.buffer.getInt();
    }

    public Long getLong() {
        return this.buffer.getLong();
    }

    public byte get() {
        return this.buffer.get();
    }

    public byte[] get(int len) {
        byte[] buff = new byte[len];
        this.buffer.get(buff);
        return buff;
    }
}
