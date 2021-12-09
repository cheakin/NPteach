# Linux



## 命令

### source

```bash
source /etc/profile
```

执行指定文件中的命令



### yum相关命令

1. 使用YUM查找软件包
   命令：`yum search`
2. 列出所有可安装的软件包
   命令：`yum list`
3. 列出所有可更新的软件包
   命令：`yum list updates`
4. 列出所有已安装的软件包
   命令：`yum list installed`
5. 列出所有已安装但不在 Yum Repository 内的软件包
   命令：`yum list extras`
6. 列出所指定的软件包
   命令：`yum list`
7. 使用YUM获取软件包信息
   命令：`yum info`
8. 列出所有软件包的信息
   命令：`yum info`
9. 列出所有可更新的软件包信息
   命令：`yum info updates`
10. 列出所有已安装的软件包信息
    命令：`yum info installed`
11. 列出所有已安装但不在 Yum Repository 内的软件包信息
    命令：`yum info extras`
12. 列出软件包提供哪些文件
    命令：`yum provides`



### 运行jar包

1. 这是最基本的jar包执行方式，但是当我们用ctrl+c中断或者关闭窗口时，程序也会中断执行。

   ```sh
   java -jar XXX.jar
   ```

2. &代表在后台运行，使用ctrl+c不会中断程序的运行，但是关闭窗口会中断程序的运行。

   ```sh
   java -jar XXX.jar &`
   ```

3. 使用这种方式运行的程序日志会输出到当前目录下的nohup.out文件，使用ctrl+c中断或者关闭窗口都不会中断程序的执行。

   ```sh
   nohup java -jar XXX.jar &
   ```

4. temp.out的意思是将日志输出重定向到temp.out文件，使用ctrl+c中断或者关闭窗口都不会中断程序的执行。

   ```sh
   nohup java -jar XXX.jar >temp.out &
   ```

> 参考: [linux运行jar包_Java_Mike的博客-CSDN博客_linux 启动jar](https://blog.csdn.net/Java_Mike/article/details/80383126)





## 脚本

### 安装Java脚本

```bash
echo "start install jdk"

mkdir /usr/local/java
cd /usr/local/java
echo "mkdir success"
wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u141-b15/336fa29ff2bb4ef291e347e091f7f4a7/jdk-8u141-linux-x64.tar.gz"
#wget http://192.168.12.47/upload/jdk-9.0.1_linux-x64_bin.tar.gz
echo "wget success"

#tar -zxvf /usr/local/java/jdk-9.0.1_linux-x64_bin.tar.gz
tar -zxvf /usr/local/java/jdk-8u141-linux-x64.tar.gz
echo "tar success"

cat >> /etc/java_profile << EOF
JAVA_HOME=/usr/local/java/jdk-8u141
JRE_HOME=/usr/local/java/jdk-8u141/jre
PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin
CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib
export JAVA_HOME JRE_HOME PATH CLASSPATH
EOF
echo "cat success"
source /etc/java_profile
echo "JDK is installed"
```

> 参考:[CentOS一键安装JDK脚本_Firoly的博客-CSDN博客](https://blog.csdn.net/Firoly/article/details/79378891)



### 安装JDK

```SHELL
#!/bin/bash
#这里可替换为你自己的执行程序，其他代码无需更改
APP_NAME=wopihost-0.0.1-SNAPSHOT.jar
cd `dirname $0`
#使用说明，用来提示输入参数
usage() {
    echo "Usage: sh 脚本名.sh [start|stop|restart|status] [environment(default=prod)]"
    exit 1
}
#检查程序是否在运行
is_exist(){
  pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}' `
  #如果不存在返回1，存在返回0
  if [ -z "${pid}" ]; then
   return 1
  else
    return 0
  fi
}

#启动方法
start(){
  is_exist
  #判断环境
  env=$1
  if [ ! $env ] ;then
    env="prod"
  fi
  # 创建log文件夹
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is already running. pid=${pid} ."
  else
    if [ ! -d log ];then
      mkdir log
    fi
    nohup java -jar $APP_NAME --spring.profiles.active=$env > log/$(date +%Y%m%d%H%M%S).log 2>&1 &
    echo "${APP_NAME} is start success"
    #tail -f fileserver-web.out
  fi
}

#停止方法
stop(){
  is_exist
  if [ $? -eq "0" ]; then
    kill -9 $pid
    echo "${APP_NAME} is  stoped"
  else
    echo "${APP_NAME} is not running"
  fi
}

#输出运行状态
status(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is running. Pid is ${pid}"
  else
    echo "${APP_NAME} is NOT running."
  fi
}

#重启
restart(){
  stop
  start
}

#根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
  "start")
    start $2
    ;;
  "stop")
    stop
    ;;
  "status")
    status
    ;;
  "restart")
    restart
    ;;
  *)
    usage
    ;;
esac
```

>  参考:[安装Java脚本](#安装Java脚本)







##  其他报错

### $'\r':command not found

存现这种错误是因为 编写的 shell脚本是在win下编写的，每行结尾是\r\n 的Unix 结果行是\n 

所以在[Linux](https://so.csdn.net/so/search?from=pc_blog_highlight&q=Linux)下运行脚本 会任务\r 是一个字符，所以运行错误，需要把文件转换下，

运行脚本 

```bash
dos2unix  脚本名
```

OK

> 参考: [shell脚本执行错误 $'\r':command not found_liuxiangke0210的专栏-CSDN博客](https://blog.csdn.net/liuxiangke0210/article/details/80395707)



