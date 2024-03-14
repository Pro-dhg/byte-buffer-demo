package com.yamu.buffer.demo;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 * @ClassName ByteBufferUtil
 * @Author dhg
 * @Version 1.0
 * @Date 2024/3/13 16:22
 * @Description:
 */
public class ByteBufferUtil {
    private final ByteBuffer buffer;

    public ByteBufferUtil(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public synchronized boolean put(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return true;
        }
        boolean b = bytes.length <= available();
        if (b) {
            try {
                this.buffer.put(bytes);
            }catch (BufferOverflowException e){
                // 这个异常是因为满了
                return false ;
            }/*catch (IllegalArgumentException e){
                // 这个异常是因为NIO缓冲区的位置大于限制的值
                return false ;
            }*/
        }
        return b;
    }

    public int available() {
        return buffer.capacity() - buffer.position();
    }

    public int length() {
        return buffer.position();
    }
}
