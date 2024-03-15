package com.yamu.buffer.demo;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @ClassName ByteBufferDemo
 * @Author dhg
 * @Version 1.0
 * @Date 2024/3/12 17:54
 * @Description:
 *  1.生产者为n，那么就有n个生产者往uploadCache里写数据
 *  2.每个uploadCache规定好统一大小
 *  3.当uploadCache满了之后，才会发送给消费者
 *  4.当uploadCache未写满且生产者无数据往uploadCache写数据时，也需要发送给消费者
 *
 *  Q：
 *  1）每个uploadCache规定多大，一共多少个，总共多大
 *  2）怎么知道生产者无数据且uploadCache未满时，需要发送数据给消费者
 */
public class ByteBufferDemo {

    /**
     * uploadCache个数
     */
    public static Integer CACHE_CNT = 10 ;
    /**
     * uploadCache大小
     * 单位字节
     * 1024字节=1KB
     */
    public static Integer CACHE_SIZE = 3072 ;

    /**
     * 生产者个数
     */
    public static Integer PRODUCER_CNT = 1 ;

    /**
     * 消费者个数
     */
    public static Integer CONSUMER_CNT = 1 ;

    public static String FILE_PATH = "src/main/java/com/yamu/buffer/demo/file";
    public static String OUTPUT_FILE = "src/main/java/com/yamu/buffer/demo/file/out.log";
    public static Boolean ALWAYS = false ;
    public static Boolean WRITE = false ;

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            CACHE_CNT = Integer.parseInt(args[0]);
            CACHE_SIZE = Integer.parseInt(args[1]);
            PRODUCER_CNT = Integer.parseInt(args[2]);
            CONSUMER_CNT = Integer.parseInt(args[3]);
            FILE_PATH = args[4];
            OUTPUT_FILE = args[5];
            ALWAYS = Objects.equals("false",args[6]) ? ALWAYS : !ALWAYS;
            WRITE = Objects.equals("false",args[7]) ? WRITE : !WRITE;
        } else {
            System.out.println("无参数输入，正在使用默认参数");
        }
        System.out.println("当前使用的参数如下：");
        System.out.println("     uploadCache个数："+CACHE_CNT);
        System.out.println("     uploadCache大小："+CACHE_SIZE);
        System.out.println("     生产者个数："+PRODUCER_CNT);
        System.out.println("     消费者个数："+CONSUMER_CNT);
        System.out.println("     原始文件路径："+FILE_PATH);
        System.out.println("     写出文件路径："+OUTPUT_FILE);
        System.out.println("     是否一直往堆外内存写数据："+ALWAYS);
        System.out.println("     是否输出到文件中："+WRITE);
        System.out.println();


        /**
         * 堆外内存存储集合
         */
        BlockingQueue<ByteBuffer> bb = new LinkedBlockingDeque<>();

        /**
         * 消费者要消费的集合
         */
        BlockingQueue<ByteBuffer> cc = new LinkedBlockingDeque<>();


        System.out.println("开始创建uploadCache，共创建"+CACHE_CNT+"个");
        for (int i = 0; i < CACHE_CNT; i++) {
            bb.add(ByteBuffer.allocateDirect(CACHE_SIZE));
        }
        System.out.println("所有uploadCache已创建成功，可以开始模拟指令日志读取并存放在堆外内存流程");
        System.out.println("开始读取日志信息");

        List<File> logs = findLog();
        System.out.println("日志信息已采集完成，开始创建生产者，共创建"+ (PRODUCER_CNT > logs.size() ? logs.size() : PRODUCER_CNT) +"个");
        for (int i = 0; i < PRODUCER_CNT && i < logs.size(); i++) {
            new Thread(new Producer(logs.get(i),bb,cc,ALWAYS)).start();
        }
        System.out.println("生产者已创建完成，正在往uploadCache写数据");
        System.out.println("开始创建消费者，共创建"+CONSUMER_CNT+"个");
        File outputFIle = new File(OUTPUT_FILE);
        for (int i = 0; i < CONSUMER_CNT; i++) {
            new Thread(new Consumer(bb,cc,outputFIle,WRITE)).start();
        }
        System.out.println("消费者已创建完成，正在处理各个uploadCache");
    }

    public static List<File> findLog(){
        // 获取该路径下所有的.log文件
        List<File> logFiles = new ArrayList<>();
        try {
            Files.walk(Paths.get(FILE_PATH))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".log"))
                    .forEach(path -> {
                        logFiles.add(path.toFile()) ;
                        System.out.println("扫描到日志文件："+path.getFileName());
                    });
        } catch (IOException e) {
            System.err.println("文件路径异常");
            return null;
        }

        if (logFiles.size() <= 0){
            try {
                System.out.println("路径："+FILE_PATH+" 下无用.log结尾的文件，请检查");
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return logFiles;
    }
}
