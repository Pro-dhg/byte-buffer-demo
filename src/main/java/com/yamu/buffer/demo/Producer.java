package com.yamu.buffer.demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
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
    private final Lock lock = new ReentrantLock() ;
    int cacheCnt ;
    int cacheSize ;

    public Producer(Queue<GenerateDate> collects, Queue<ByteBuffer> bb, Queue<ByteBuffer> cc, int cacheCnt, int cacheSize) {
        this.collects = collects;
        this.bb = bb;
        this.cc = cc;
        this.cacheCnt = cacheCnt ;
        this.cacheSize = cacheSize ;
    }

    @Override
    public void run() {
        GenerateDate generateDate = getGenerateDate();
        ByteBuffer byteBuffer = getByteBuffer();
        ByteBufferUtil bbu = new ByteBufferUtil(byteBuffer);
        while (true){
            if (byteBuffer!=null){
                byte[] bytes = generateDate.readLine();
                if (bytes==null){
                    generateDate = getGenerateDate();
                }else {
                    boolean b = bbu.put(bytes);
                    if (!b){
                        System.out.println("uploadCache写满，大小为"+bbu.length()+"字节，写入对象哈希码为："+System.identityHashCode(byteBuffer));
                        // 容量不够存放一行数据了，那么让消费者开始消费
                        cc.add(byteBuffer);
                        byteBuffer = null ;
                    }
                }
            }else {
                byteBuffer=getByteBuffer();
                bbu=new ByteBufferUtil(byteBuffer);
            }
        }

    }

    public synchronized ByteBuffer getByteBuffer(){
        while (bb.size() <= 0 && cc.size() <=0) {
            //没有可用的uploadCache那就等1秒,如果一直没有就一直等
            try {
                System.out.println("暂时无可用uploadCache，等待1s");
                Thread.sleep(1000L);
                for (int i = 1; i < cacheCnt+1; i++) {
                    ByteBuffer buffer = ByteBuffer.allocateDirect(cacheSize);
                    bb.add(buffer);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ByteBuffer poll = bb.poll();

        return poll;
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
