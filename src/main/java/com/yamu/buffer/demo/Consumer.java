package com.yamu.buffer.demo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
    private Queue<ByteBuffer> bb ;
    // 输出文件路径
    String outputFile = "src/main/java/com/yamu/buffer/demo/file/out.txt";
    FileChannel outputChannel ;
    public Consumer( Queue<ByteBuffer> bb, Queue<ByteBuffer> cc) {
        this.bb = bb ;
        this.cc = cc ;
        // 打开输出文件
        RandomAccessFile outputRandomAccessFile = null;
        try {
            outputRandomAccessFile = new RandomAccessFile(outputFile, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        outputChannel = outputRandomAccessFile.getChannel();

    }

    @Override
    public void run() {
        ByteBuffer buffer = cc.poll();
        while (true){
            if (buffer !=null & buffer.position()>0){
                try {
                    // 切换到读模式
                    buffer.flip();
                    // 将缓冲区内容写入输出文件
                    outputChannel.write(buffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                endOption(buffer);
            }else {
                buffer = getConsumerByteBuffer();
            }
        }
    }

    public synchronized ByteBuffer getConsumerByteBuffer(){
        while (cc.size() == 0) {
            //没有可消费的uploadCache那就等1秒,如果一直没有就一直等
            try {
                System.out.println("暂时无可消费uploadCache，等待1s");
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return cc.poll();
    }

    public synchronized void endOption(ByteBuffer buffer){
        if (!buffer.hasRemaining()){
            System.out.println("uploadCache已读取成功，大小为"+buffer.position()+"字节，读取对象哈希码为："+System.identityHashCode(buffer));
            //清空数据
            buffer.clear();
            bb.add(buffer);
        }
    }
}
