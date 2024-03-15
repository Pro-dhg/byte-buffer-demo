package com.yamu.buffer.demo;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * @ClassName Consumer
 * @Author dhg
 * @Version 1.0
 * @Date 2024/3/13 10:05
 * @Description:
 */
public class Consumer implements Runnable{

    private BlockingQueue<ByteBuffer> bb ;
    private BlockingQueue<ByteBuffer> cc ;
    public Consumer(BlockingQueue<ByteBuffer> bb, BlockingQueue<ByteBuffer> cc) {
        this.bb = bb ;
        this.cc = cc ;
    }

    @Override
    public void run() {
        while (true){
            try {
                ByteBuffer buffer = cc.take();
                // 读取ByteBuffer中的数据（这里只是模拟消费）
                while (buffer.hasRemaining()) {
                    buffer.get();
                }
                System.out.println(Thread.currentThread().getId()+" uploadCache已读取成功，大小为"+buffer.position()+"字节，读取对象哈希码为："+System.identityHashCode(buffer));
                buffer.clear();
                bb.put(buffer);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

}
