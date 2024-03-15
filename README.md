# byte-buffer-demo
java堆外内存demo

## 使用方法
- 1.打包当前代码，生成jar包
- 2.复制start.sh脚本到指定目录
- 3.修改start参数

### 当前截图为任务没有启动的时候系统各参数截图
![启动前](image%2Fone.png)
### 启动脚本后打印信息
![执行页面](image%2Ftwo.png)
### 开始往堆外内存写数据的时候，会看到cpu占用率很高，内存占用不是很高
![执行中](image%2Fthree.png)
### 停止之后，各项指标趋于正常
![结束后](image%2Ffour.png)