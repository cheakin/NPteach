## 数仓概念
### 数据仓库简介
数据仓库(Data Warehouse),是为企业制定决策，提供数据支持的。可以帮助企业，改进业务流程、提高产品质量等。

### 数据分类
数据仓库的输入数据通常包括：业务数据、用户行为数据和爬虫数据等
![[Pasted image 20230713160752.png]]

### 总体介绍
![[Pasted image 20230713161102.png]]

### 项目需求分析
1. 用户行为数据采集
	1. 平台搭建
	2. 业务数据采集平台搭建
2. 离线需求：
	![[Pasted image 20230713161835.png]]
	![[Pasted image 20230713161715.png ]]
3. 实时数仓
	![[Pasted image 20230713162227.png]]
	![[Pasted image 20230713162350.png]]

### 技术选型
![[Pasted image 20230713164153.png]]

### 系统数据流程
![[Pasted image 20230713164312.png]]
### 框架版本选型
![[Pasted image 20230713164815.png]]

### 具体版本号选择
#### Apache框架版本
![[Pasted image 20230713164724.png]]
注意事项: 框架选型尽量不要选择最新的框架，选择最新框架半年前左右的稳定版。

### 服务器选型
![[Pasted image 20230713165451.png]]
### 集群规模
![[Pasted image 20230713170519.png]]
### 集群资源规划
![[Pasted image 20230713171011.png]]
![[Pasted image 20230713171511.png]]

## 同步行为数据模拟
### 埋点简介
![[Pasted image 20230713171811.png]]
### 用户行为日志内容
![[Pasted image 20230713172009.png]]
页面浏览记录
![[Pasted image 20230713172112.png]]
动作记录
![[Pasted image 20230713172206.png]]
曝光
![[Pasted image 20230713172301.png]]
启动
![[Pasted image 20230713173416.png]]
错误
![[Pasted image 20230714000523.png]]
### 埋点日志格式
![[Pasted image 20230714000725.png]]
#### 页面日志
![[Pasted image 20230714000753.png]]
#### 启动日志
![[Pasted image 20230714001320.png]]

### 服务准备和JDK准备
1. 需要准备4G+50G的服务器3台
    1. 保证可以连接外网
    2. 如果是使用最小化安装所安装的系统，需要额外安装`epel-release`
    3. 安装`net-tools`和`vim`
2. 关闭防火墙
    ``` sh
    systemctl stop firewalld
    systemctl disable firewalld.service
```
3. 创建用户，并修改密码（略）
4. 为创建的用户分配root权限
5. 在`/opt`目录下创建文件家，并修改所属主和所属组
    1. 在`/opt`目录下创建`module`、`software`文件夹
        ``` sh
        mkdir /opt/module
        mkdir /opt/software
```
    2. 修改所属主和所属组（略）
    3. 查看文件夹所有者和所属组
        ``` sh
        cd /opt/
        ll
```
6. 卸载自带的JDK（最小化安装略）
    ``` sh
    rpm -qa | grep -i java | xargs -n1 rpm -e --nodeps
    
    # rpm -qa：查询所安装的所有rpm软件包
    # grep -i：忽略大小写
    # xargs-nl：表示每次只传递一个参数
```

### 阿里云准备（备选）
略

### 集群同步脚本
``` sh
#!/bin/bash
#1. 判断参数个数
if [ $# -lt 1 ]
then
	echo Not Enough Arguement!
	exit;
fi
#2. 遍历集群所有机器
for host in pri-dev-bigdata-1 pri-dev-bigdata-2 pri-dev-bigdata-3
do
	echo ================= $host =================
	#3. 遍历所有目录，挨个发送
	for file in $@
	do
		#4. 判断文件是否存在
		if [ -e $file ]
		then
			#5. 获取父目录
			pdir=$(cd -P $(dirname $file); pwd)
			#6. 获取当前文件的名称
			fname=$(basename $file)
			ssh $host "mkdir -p $pdir"
			rsync -av $pdir/$fname $host:$pdir
		else
			echo $file does not exists!
		fi
	done
done
```

### 免密登录配置
``` sh
# 使用root用户登录
cd ~/.ssh/
ssh-keygen -t rsa

ssh-copy-id hadoop102
ssh-copy-id hadoop103
ssh-copy-id hadoop104
```

### 安装JDK
``` sh
# 卸载
rpm -qa | grep -i java | xargs -n1 rpm -e --nodeps

# 安装
yum install java-1.8.0-openjdk-devel

# 验证
java -version
```

### Linux环境变量说明
登录式shell和非登录式shell
![[Pasted image 20230716120146.png]]

### 模拟数据
![[gmall_mock.zip]]

同步模拟数据的脚本
``` sh 
#!/bin/bash
for i in hadop102 hadop103 hadop104
do
	echo "-------------------- $i --------------------"
	ssh $i "cd /opt/module/applog;java -jar gmall2020-mock-log-2021-10-10.jar >/dev/null 2>&1 &" 
done


cd
cd bin/
vim lg.sh

chmod 777 lg.sh
lg.sh
```

### haddop安装
收集打印脚本`xcall`
``` sh
#!/bin/bash
for i in hadop102 hadop103 hadop104
do
    echo "-------------------- $i --------------------"
	ssh $i "$**" 
done

chmod 777 xcall
```
`core.xml`配置
``` xml
<configuration>
	<!-- 指定NameNode地址 -->
	<property>
		<name>fs.defaultFS</name>
		<value>hdfs://hadoop103:8020</value>
	</property>
	
	<!-- 指定 hadoop数据的存储目录 -->
	<property>
		<name>hadoop.tmp.dir</name>
		<value>/opt/module/hadoop/data</value>
	</property>
	
	<!-- 配置HDES 网页登录使用的静态用户为miit -->
	<property>
		<name>hadoop.http.staticuser.user</name>
		<value>miit</value>
	</property>
	<!-- 配置该miit(superUser)允许通过代理访问的主机节点 -->
	<property>
		<name>hadoop.proxyuseiqu.hosts</name>
		<value>*</value>
	</property>
	<!-- 配置该atquigu(superUser)允许通过代理用户所属组 -->
	<property>
		<name>hadoop.proxyuser.miit.groups</name>
		<value>*</value>
	</property>
	<!-- 配置该 miit(superUser)允许通过代理的用户-->
	<property>
		<name>hadoop .proxyyuser.miit.users</name>
		<value>*</value>
	</property>
</configuration>
```
`hdfs.xml`配置
``` xml
<configuration>
	<!-- nn web 端访问地址 -->
	<property>
		<name>dfs.namenode.http-address</name>
		<value>hadoop102:9870</value>
	</property>
	<!-- 2nn web 端访问地址-->
	<property>
		<name>dfsnamenode.secondary.http-address</name>
		<value>hadoop104:9868</value>
	</property>
	<!-- 测试环境指定HDES 副本的数量1-->
	<property>
		<name>dfs.replication</name>
		<value>3</value>
	</property>
</configuration>
```
xml
``` xml
<configuration>
	<!-- 指定MR走shuffle -->
	<property>
		<name>yarn.nodemanager.aux-services</name>
		<value>mapreduce shuffle</value>
	</property>
	<!-- 指定 ResourceManager的地址-->
	<property>
		<name>yarn.resourcemanager.hostname</name>
		<value>10.10.10.35</value>
	</property>
	<!-- 环境变量的继承-->
	<property>
		<name>yarn.nodemanager.env-whitelist</name>
		<value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRED_HOME</value>
	</property>
	<!--yarn 单个容器允许分配的最大最小内存 -->
	<property>
		<name>yarn.scheduler.minimum-allocation-mb</name>
		<value>512</value>
	</property>
	<property>
		<name>yarn.scheduler.maximum-allocation-mb</name>
		<value>4096</value>
	</property>
	<!-- yarn 容器允许管理的物理内存大小-->
	<property>
		<name>yarn.nodemanagerresource.memory-mb</name>
		<value>4096</value>
	</property>
	<!-- 关闭 yarn 对物理内存和虚拟内存的限制检查 -->
	<property>
		<name>yarn.nodemanager.pmem-check-enabled</name>
		<value>true</value>
		</property>
	<property>
		<name>yarn.nodemanager.vmem-check-enabled</name>
		<value>false</value>
	</property>
</configuration>
```

xml
``` xml
```