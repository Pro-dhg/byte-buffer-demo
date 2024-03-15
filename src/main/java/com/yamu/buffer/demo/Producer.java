package com.yamu.buffer.demo;

import java.io.*;
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

    private final Boolean always ;

    public Producer(File logFile, BlockingQueue<ByteBuffer> bb, BlockingQueue<ByteBuffer> cc, Boolean ALWAYS) {
        this.logFile = logFile;
        this.bb = bb;
        this.cc = cc;
        this.always = ALWAYS ;
    }

    @Override
    public void run() {
        if (always){
            while (true){
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(logFile)) ;
                    ByteBuffer buffer = bb.take();

                    String line ;
                    long start = System.currentTimeMillis();
                    while ((line = reader.readLine()) != null){
                        try {
                            line+="\n";
                            buffer.put(line.getBytes(StandardCharsets.UTF_8));
                        }catch (BufferOverflowException e){
                            // 这个异常是因为满了
                            break;
                        }
                    }
                    long end = System.currentTimeMillis();
                    cc.put(buffer);
                    System.out.println(Thread.currentThread().getId()+" uploadCache写满，大小为"+buffer.position()+"字节，写入对象哈希码为："+System.identityHashCode(buffer)
                            + "  用时："+(end-start)+"ms");
                }catch (IOException e){
                    e.printStackTrace();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }else {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(logFile));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            while (true){
                try {
                    ByteBuffer buffer = bb.take();
                    String line ;
                    long start = System.currentTimeMillis();
                    while ((line = reader.readLine()) != null){
                        try {
                            line+="\n";
                            buffer.put(line.getBytes(StandardCharsets.UTF_8));
                        }catch (BufferOverflowException e){
                            // 这个异常是因为满了
                            break;
                        }
                    }
                    long end = System.currentTimeMillis();
                    if (reader.readLine() == null){
                        cc.put(buffer);
                        System.out.println(Thread.currentThread().getId()+" 最后一个 uploadCache写满，大小为"+buffer.position()+"字节，写入对象哈希码为："+System.identityHashCode(buffer)
                                + "  用时："+(end-start)+"ms");
                        break;
                    }
                    cc.put(buffer);
                    System.out.println(Thread.currentThread().getId()+" uploadCache写满，大小为"+buffer.position()+"字节，写入对象哈希码为："+System.identityHashCode(buffer)
                            + "  用时："+(end-start)+"ms");
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

}
