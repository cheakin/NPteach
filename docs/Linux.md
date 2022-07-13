# Linux



## 脚本命令
### source
```bash
source /etc/profile
```
执行指定文件中的命令

### dirname $0
````sh
cd `dirname $0`
````
切换到所执行脚本文件所在目录

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

### 压缩文件
> 参考: [Linux环境下，文件的压缩/解压](https://blog.csdn.net/qq_41038824/article/details/96880371)

1. zip格式
   windows平台用的最多的压缩格式之一
   * 优点: 可以在不同的操作系统平台上使用
   * 缺点: 支持的压缩率不是很高。而tar.gz和tar.bz2在压缩率方面做得非常好。

  **压缩一个zip文件**
  ``` shell
  zip -r newfilename.zip filename
  # -r是压缩文件, newfilename.zip是指压缩之后的文件名称，filename是指要压缩的文件名称
  ```

  **解压一个zip文件**, （路径：解压文件在当前文件下）
  ``` shell
  unzip filename.zip
  # 默认解压文件在当前文件下

  unzip filename.zip -d newdir
  # 使用-d参数,解压文件可以将文件解压缩至指定的目录
  # filename.zip压缩文件名称，newdir压缩路径
  ```

2. tar/tar.gz格式
  tar是在Linux中使用得非常广泛的文档打包格式。它的好处就是它只消耗非常少的CPU以及时间去打包文件，它仅仅只是一个打包工具，并不负责压缩。
  **打包**目录
  ``` shell
  tar -cvf newfilename.tar filename
  # -c参数是建立新的存档，
  # -v参数详细显示处理的文件， 
  # -f参数指定存档或设备,
  # newfilename.tar是指压缩之后的文件名称，filename是指要压缩的文件名称
  ```

  将打包文件**解包**
  ``` shell
  tar -xvf filename.tar
  # 解包为多文件，filename是指要解包的文件名称

  tar -xvf filename.tar -C newdir
  # 解包到指定的路径, 
  # filename是指要解包的文件名称，newdir为指定路径，注意此处解包的参数是大写C，不是小写c
  ```

  **压缩**打包文件
  ``` shell
  tar -zcvf newfilename.tar filename.tar.gz
  # newfilename.tar要压缩的文件名称, filename.tar.gz压缩文件名称
  ```

  **解压**
  ``` shell
  tar -zxvf filename.tar.gz
  # 解压缩文件，filename.tar.gz是指要解压的文件名称

  tar -zxvf filename.tar.gz -C newdir
  # 指定解包的路径, filename.tar.gz是指要解压的文件名称, newdir为指定路径
  ```

  参数的意义:
  ``` json
  -c:参数是建立新的存档
  -f:参数指定存档或设备
  -x:释放文件内存
  -t:仅仅查看包中内容，而不释放
  -v:参数详细显示处理的文件
  ```

3. tar.bz2格式  
  这种压缩格式是三种方式中压缩率最好的。这也就意味着，它比前面的方式要**占用更多的CPU与时间** 。
  **压缩**
  ``` shell
  tar -jcvf newfilename.tar.bz2 filename
  ```

  **解压**
  ``` shell
  tar -jxvf filename.tar.bz2

  # tar -jxvf filename.tar.bz2 -C newdir
  tar -jxvf filename.tar.bz2 -C newdir
  ```



## 脚本
### 安装JDK脚本(wget)
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

### 安装MySQL(wget, yum)
```SHELL
#!/bin/bash
#日志路径 /var/bin/mysqld.log

#安装wget
#yum install wget -y

#下载mysql rpm
wget http://repo.mysql.com/mysql57-community-release-el7.rpm

#安装mysql rpm
rpm -ivh mysql57-community-release-el7.rpm

#安装服务
yum install -y mysql-server 
 
#设置权限
chown mysql:mysql -R /var/lib/mysql

#初始化mysql(生成随机的初始密码),也可以使用 mysqld --initialize-insecure 将初始密码设置为空
#再次初始化前需要删除/var/lib/mysql文件夹下的内容rm -rf /var/lib/mysql
mysqld --initialize

#启动 MySQL
systemctl start mysqld

#完成mysql 安装 查看 MySQL 运行状态
systemctl status mysqld

#查看安装的mysql密码
grep 'temporary password' /var/log/mysqld.log

#添加mysql端口3306和Tomcat端口8080
firewall-cmd --zone=public --add-port=3306/tcp --permanent
#firewall-cmd --zone=public --add-port=8080/tcp --permanent
firewall-cmd --reload


#登录mysql
mysql 

#修改密码策略
set global validate_password_policy=0;
set global validate_password_length=4;
#修改密码 为123456
SET PASSWORD = PASSWORD('123456');

#设置远程登录 登录密码123456
grant all privileges on *.* to 'root'@'%' identified by '123456' with grant option;
flush privileges; 

#退出mysql登录
exit;
```

>  参考:
>
>  1. [CentOS 7 MySQL5.7 全自动l脚本 mysql安装只需要1分钟 - ypeuee - 博客园 (cnblogs.com)](https://www.cnblogs.com/ypeuee/p/13235307.html)
>  2. [CentOS 安装Mysql的脚本 - 掘金 (juejin.cn)](https://juejin.cn/post/6976957164619317255)



### 启动java应用
```sh
#!/bin/bash
#这里可替换为你自己的执行程序，其他代码无需更改
APP_NAME=mq_monitor-0.0.1-SNAPSHOT.jar
cd `dirname $0`
#使用说明，用来提示输入参数
tips() {
    echo "Usage: sh 执行脚本.sh [start|stop|restart|status]"
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
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is already running. pid=${pid} ."
  else
    nohup java -jar $APP_NAME > /dev/null 2>&1 &
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
    start
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
    tips
    ;;
esac
```

> 参考:
> 1. [Linux脚本启动jar包_pocher的博客-CSDN博客_linux启动jar包脚本](https://blog.csdn.net/bingxuesiyang/article/details/88531613)
> 2. [linux脚本执行jar包运行 - 贾小仙 - 博客园 (cnblogs.com)](https://www.cnblogs.com/hackerxian/p/13722821.html)

### 拷贝远程Linux文件(拷贝并允许jar包)
> 参考: 
> 1. [shell中 expect 命令](https://www.cnblogs.com/itwangqiang/articles/14207133.html?ivk_sa=1024320u)
> 2. [xsync同步脚本的使用](https://blog.csdn.net/nalw2012/article/details/98322637)

1. 开始考虑远程执行本地命令的方式(不推荐)
   * 安装expect
    此方式需要`expect`, (如果有apt, 就不需要再使用weget下包来操作了)
    ``` shell
    apt install expect
    ```
    安装完成后便可以使用`expect`命令
   * 编写脚本
    ``` shell
    #!/bin/bash
    ip='host'
    port=22
    user=root
    pwd="xxxx"
    serverdir="/meal_health/java_project/mealhealth-boot-admin-1.0.0.jar"
    clienterdir="/"
    project_root="/meal_health/java_project"
    expect -c "
    set timeout 3600
    spawn scp $user@$ip:$serverdir $clienterdir
    expect \"password:\"
    send \"$pwd\r\"
    expect eof
    "
    ```
    执行脚本会出现一个问题, 在使用占位符(如$user, $serverdir)的expect命令中, 会莫名其妙的多了一个空格导致命令执行失败.
    暂时无解, 放弃

2. 使用SSH密钥
   * 生成密钥
    直接运行
      ``` shell
      ssh-keygen
      ```
      使用默认位置(`/用户/.ssh/`)下会生成两个文件:
      * `id_rsa`: 私钥
      * `id_rsa.pub`: 公钥

      创建完成后, 可以手动SFTP的方式拷贝到远程服务器上的`~/.ssh/`目录下, 并将文件重名命名`authorized_keys`
      也可以使用`ssh`命令连接远程服务器.
      ``` shell
      sftp root@host << EOF
      put ~/.ssh/id_rsa.pub /root/.ssh/authorized_keys
      EOF
      ```
      > `authorized_keys` 文件中存的是能远程的服务器公钥, 可以用来远程登录远程服务器.多个公钥追加添加即可

      至此就能测试能不能免密登录远程服务器了
      ``` shell
      ssh root@host
      ```
   * 远程获取文件脚本
      ``` Shell
      PROJECT_ROOT="/java_project"
      JAR_NAME="jar-1.0.0"
      ENV="test"

      # get jar from 
      scp root@host:$PROJECT_ROOT/$JAR_NAME.jar $PROJECT_ROOT/$JAR_NAME.jar
      ```
   * 远程获取jar包并运行
      ``` shell
      PROJECT_ROOT="/java_project"
      JAR_NAME="jar-1.0.0"
      ENV="test"

      # get jar from 27
      scp root@host:$PROJECT_ROOT/$JAR_NAME.jar $PROJECT_ROOT/$JAR_NAME.jar

      # start
      pid=`ps aux | grep $JAR_NAME | grep -v grep | awk '{print $2}'`
      echo $pid
      kill -9 $pid
      nohup java -jar -Duser.timezone=GMT+08 $PROJECT_ROOT/$JAR_NAME.jar --spring.profiles.active=$ENV > $PROJECT_ROOT/$JAR_NAME.log &
      ```

### 远程执行命令
> 参考: [Linux远程执行脚本](ssh root@192.168.1.1 "cd /opt/sys; /opt/misroservice/gzcx/travel-api/docker_run.sh")
``` ssh
ssh root@192.168.1.1 "cd /opt/sys; /opt/misroservice/gzcx/travel-api/docker_run.sh"
```
其中一定注意  引号内是两条命令, 已分号分隔



## 报错
### $'\r':command not found
存现这种错误是因为 编写的 shell脚本是在win下编写的，每行结尾是\r\n 的Unix 结果行是\n 

所以在[Linux](https://so.csdn.net/so/search?from=pc_blog_highlight&q=Linux)下运行脚本 会任务\r 是一个字符，所以运行错误，需要把文件转换下
运行脚本 
```bash
dos2unix  脚本名
```
> 参考: [shell脚本执行错误 $'\r':command not found_liuxiangke0210的专栏-CSDN博客](https://blog.csdn.net/liuxiangke0210/article/details/80395707)

### 远程运行脚本(运行jar包)时, 提示无法找到java命令
是由与在远程执行脚本时, 无法加载环境变量, 所以就无法执行java命令了.
解决: 在脚本最前面加上
```shell
source /etc/profile
```
这样脚本就会先去加载`/etc/profile`的内容了, 这个文件是环境变量的配置
当然也可以用到什么环境加什么环境变量

### No usable temporary directory found in ['/tmp', '/var/tmp', '/usr/tmp', '/home/vagrant']
在运行一些命令时,找不到这些文件夹, 一般是由于系统没有足够的空间了, 所以无法创建这些文件

解决: 删除占用过大的文件(可能时缓存, 可能时日志)
``` shell
# 进入根目录
cd /

# 分析文件大小
du -sh * 

# 删除较大的文件
sudo rm -rf 文件(夹)名
```

### vagrant可能会遇到磁盘爆满的情况
> 参考: [Vagrant 磁盘爆满](https://blog.csdn.net/weixin_45518808/article/details/122429563)
使用Vagrant安装虚拟机可能会遇到vagrant磁盘爆满的情况
根目录下用`du -sh *`分析后会发现`/vagrant`占用超级大. 原因是vagrant同步了`c/user/用户名`下的文件
解决: 
1. 创建`VagrantSyncFolder`文件夹;
2. 在`Vagrantfile`加上`config.vm.synced_folder "./VagrantSyncFolder", "/vagrant"`(指定映射文件夹)
3. 加载配置并重启`vagrant reload`



