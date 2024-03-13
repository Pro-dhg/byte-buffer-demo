package com.yamu.buffer.demo;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
    public static final int CACHE_CNT = 10 ;
    /**
     * uploadCache大小
     * 单位字节
     * 1024字节=1KB
     */
    public static final int CACHE_SIZE = 3072 ;

    /**
     * 生产者个数
     */
    public static final int PRODUCER_CNT = 5 ;

    /**
     * 消费者个数
     */
    public static final int CONSUMER_CNT = 2 ;

    /**
     * 堆外内存存储集合
     */
    public static final Queue<ByteBuffer> bb = new LinkedList<>();

    public static final String FILE_PATH = "src/main/java/com/yamu/buffer/demo/file";


    public static void main(String[] args) throws IOException {

        System.out.println("初始化》》》》》》》》》》》");
        System.out.println("开始创建uploadCache，共创建"+CACHE_CNT+"个");
        for (int i = 1; i < CACHE_CNT+1; i++) {
            bb.add(ByteBuffer.allocateDirect(CACHE_SIZE));
            System.out.println("第"+i+"个uploadCache已创建成功，buffer的大小为："+CACHE_SIZE+" 字节，并存储到队列中");
        }
        System.out.println("所有uploadCache已初始成功，开始模拟指令日志读取并存放在堆外内存流程");
        System.out.println("---------------------------------------------------------");
        System.out.println("-----------------------堆外内存模拟-------------------------");
        System.out.println("---------------------------------------------------------");
        System.out.println();
        System.out.println();
        System.out.println("开始读取日志信息");
        for (String filePath : findLog()) {
            InputStream in = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader( in, StandardCharsets.UTF_8.name()));
            String line ;
            while ((line = reader.readLine()) != null){
                System.out.println(line);
            }
        }
    }

    public static String[] findLog(){
        List<String> inputFiles = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);
        try {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.log");
            Files.walk(path)
                    .filter(Files::isRegularFile)
                    .filter(path1 -> matcher.matches(path1))
                    .forEach(in->{
                        inputFiles.add(in.toString());
                        System.out.println("扫描到日志文件："+in.getFileName());
                    });
        }catch (IOException e){
            System.err.println("文件路径异常");
        }

        return inputFiles.toArray(new String[0]) ;
    }
}
