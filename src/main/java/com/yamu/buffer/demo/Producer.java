package com.yamu.buffer.demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import static com.yamu.buffer.demo.ByteBufferDemo.generateDateCollects;

/**
 * @ClassName Producer
 * @Author dhg
 * @Version 1.0
 * @Date 2024/3/13 10:05
 * @Description:
 */
public class Producer implements Runnable{
    private Queue<GenerateDate> collects ;
    private final Queue<ByteBuffer> bb ;
    private final Queue<ByteBuffer> cc ;

    public Producer(Queue<GenerateDate> collects, Queue<ByteBuffer> bb, Queue<ByteBuffer> cc) {
        this.collects = collects;
        this.bb = bb;
        this.cc = cc;
    }

    @Override
    public void run() {
        GenerateDate generateDate = getGenerateDate();
        ByteBuffer byteBuffer = getByteBuffer();
        while (true){
            ByteBufferUtil bbu = new ByteBufferUtil(byteBuffer);
            byte[] bytes = generateDate.readLine();
            if (bytes==null){
                generateDate = getGenerateDate();
            }else {
                boolean b = bbu.put(bytes);
                if (!b){
                    System.out.println("uploadCache已写满，大小为"+bbu.length()+"字节，写入对象哈希码为："+System.identityHashCode(byteBuffer));
                    // 容量不够存放一行数据了，那么让消费者开始消费
                    cc.add(byteBuffer);
                    byteBuffer = getByteBuffer();
                }
            }
        }

    }

    public synchronized ByteBuffer getByteBuffer(){
        if (bb.size() == 0) {
            //没有可用的uploadCache那就等1秒,如果一直没有就一直等
            try {
                System.out.println("暂时无可用uploadCache，等待1s");
                Thread.sleep(1000L);
                getByteBuffer();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else {
            return bb.poll();
        }
        return getByteBuffer();
    }

    public synchronized GenerateDate getGenerateDate() {
        if (collects.size() == 0) {
            collects = new LinkedList<>();
            //重新获取数据
            try {
                generateDateCollects(collects);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return collects.poll();
    }
}
