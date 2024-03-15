package com.yamu.buffer.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

/**
 * @ClassName Producer
 * @Author dhg
 * @Version 1.0
 * @Date 2024/3/13 10:05
 * @Description:
 */
public class Producer implements Runnable{
    private final File logFile;
    private final BlockingQueue<ByteBuffer> bb ;
    private final BlockingQueue<ByteBuffer> cc ;

    public Producer(File logFile, BlockingQueue<ByteBuffer> bb, BlockingQueue<ByteBuffer> cc) {
        this.logFile = logFile;
        this.bb = bb;
        this.cc = cc;
    }

    @Override
    public void run() {
        while (true){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(logFile)) ;
                ByteBuffer buffer = bb.take();

                String line ;
                while ((line = reader.readLine()) != null){
                    try {
                        buffer.put(line.getBytes(StandardCharsets.UTF_8));
                    }catch (BufferOverflowException e){
                        // 这个异常是因为满了
                        break;
                    }
                }
                cc.put(buffer);
                System.out.println(Thread.currentThread().getId()+" uploadCache写满，大小为"+buffer.position()+"字节，写入对象哈希码为："+System.identityHashCode(buffer));
            }catch (IOException e){
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

    }

}
