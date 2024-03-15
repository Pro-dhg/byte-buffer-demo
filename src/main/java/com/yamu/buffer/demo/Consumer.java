package com.yamu.buffer.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
    private File outputFIle ;
    private Boolean write ;
    public Consumer(BlockingQueue<ByteBuffer> bb, BlockingQueue<ByteBuffer> cc, File outputFIle, Boolean WRITE) {
        this.bb = bb ;
        this.cc = cc ;
        this.outputFIle = outputFIle ;
        this.write = WRITE ;
    }

    @Override
    public void run() {
        if (write){
            while (true) {
                try {
                    ByteBuffer buffer = cc.take();
                    System.out.println(Thread.currentThread().getId() + " uploadCache已读取成功，大小为" + buffer.position() + "字节，读取对象哈希码为：" + System.identityHashCode(buffer));
                    // 读取ByteBuffer中的数据（这里只是模拟消费）
                    while (buffer.hasRemaining()) {
                        buffer.get();
                    }
                    buffer.clear();
                    bb.put(buffer);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }else {
            try {
                FileChannel fileChannel = new FileOutputStream(outputFIle).getChannel();
                while (true) {
                    try {
                        ByteBuffer buffer = cc.take();
                        System.out.println(Thread.currentThread().getId() + " uploadCache已读取成功，大小为" + buffer.position() + "字节，读取对象哈希码为：" + System.identityHashCode(buffer));
                        // 读取ByteBuffer中的数据到文件中
                        while (buffer.hasRemaining()) {
                            buffer.flip();
                            fileChannel.write(buffer);
                        }
                        buffer.clear();
                        bb.put(buffer);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
