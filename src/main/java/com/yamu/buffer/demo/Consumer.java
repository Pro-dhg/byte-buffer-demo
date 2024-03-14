package com.yamu.buffer.demo;

import java.nio.ByteBuffer;
import java.util.Queue;

/**
 * @ClassName Consumer
 * @Author dhg
 * @Version 1.0
 * @Date 2024/3/13 10:05
 * @Description:
 */
public class Consumer implements Runnable{

    private Queue<ByteBuffer> cc ;
    public Consumer(Queue<ByteBuffer> cc) {
        this.cc = cc ;

    }

    @Override
    public void run() {
        while (true){
            ByteBuffer buffer = getConsumerByteBuffer();
            if (buffer !=null && buffer.position()>0){
                endOption(buffer);
            }
        }
    }

    public synchronized ByteBuffer getConsumerByteBuffer(){
        while (cc.size() <= 0) {
            //没有可消费的uploadCache那就等1秒,如果一直没有就一直等
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        return cc.poll();
    }

    public synchronized void endOption(ByteBuffer buffer){
        System.out.println("uploadCache已读取成功，大小为"+buffer.position()+"字节，读取对象哈希码为："+System.identityHashCode(buffer));
//        //清空数据,置为null，垃圾收集器会在合适时间回收
//        buffer.clear() ;
    }
}
