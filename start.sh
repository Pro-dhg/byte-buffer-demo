#!/bin/bash

#uploadCache个数
CACHE_CNT=3
#uploadCache大小 单位kb
CACHE_SIZE=204800
#生产者个数
PRODUCER_CNT=300
#消费者个数
CONSUMER_CNT=10
#原始文件路径
FILE_PATH=/root/demo/file
#输出文件路径
OUTPUT_FILE=/root/demo/file/out.log
#是否一直往堆外内存写数据
ALWAYS=true
#是否输出到文件中 true:不写  false:写
WRITE=true

/openjdk-11/bin/java -jar /root/demo/byte-buffer-demo.jar $CACHE_CNT $CACHE_SIZE $PRODUCER_CNT $CONSUMER_CNT $FILE_PATH $OUTPUT_FILE $ALWAYS $WRITE