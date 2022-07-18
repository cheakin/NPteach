# 谷粒商城
> 参考: [Java项目《谷粒商城》Java架构师 | 微服务 | 大型电商项目](https://www.bilibili.com/video/BV1np4y1C7Yf)
> [从前慢-谷粒商城篇章1](https://blog.csdn.net/unique_perfect/article/details/111392634)
> [从前慢-谷粒商城篇章2](https://blog.csdn.net/unique_perfect/article/details/113824202)
> [从前慢-谷粒商城篇章3](https://blog.csdn.net/unique_perfect/article/details/114035775)
> [guli-mall](https://www.yuque.com/zhangshuaiyin/guli-mall)

## 项目简介
*  电商模式
  市面上有5种常见的电商模式 B2B、B2C、C2B、C2C、O2O
  - B2B 模式
    B2B(Business to Business)，是指商家和商家建立的商业关系，如阿里巴巴
  - B2C 模式
    B2C(Business to Consumer) 就是我们经常看到的供应商直接把商品卖给用户，即“商对客”模式，也就是我们呢说的商业零售，直接面向消费销售产品和服务，如苏宁易购，京东，天猫，小米商城
  - C2B 模式
    C2B (Customer to Business),即消费者对企业，先有消费者需求产生而后有企业生产，即先有消费者提出需求，后又生产企业按需求组织生产
  - C2C 模式
    C2C (Customer to Consumer) 客户之间把自己的东西放到网上去卖 。如淘宝、咸鱼
  - O2O 模式
    O2O 即 Online To Offline，也即将线下商务的机会与互联网结合在一起，让互联网成为线上交易前台，线上快速支付，线上优质服务，如：饿了么，美团，淘票票，京东到家

### 谷粒商城
谷粒商城是一个B2C模式的电商平台，销售自营商品给客户

* **项目架构图**
  [项目微服务架构图](./assets/谷粒商城-微服务架构图.jpg)

  前后分离开发，分为内网部署和外网部署，外网是面向公众访问的。访问前端项目，可以有手机APP，电脑网页；内网部署的是后端集群，前端在页面上操作发送请求到后端，在这途中会经过Nginx集群，Nginx把请求转交给API网关(springcloud gateway)（网关可以根据当前请求动态地路由到指定的服务，看当前请求是想调用商品服务还是购物车服务还是检索服务），从路由过来如果请求很多，可以负载均衡地调用商品服务器中一台（商品服务复制了多份），当商品服务器出现问题也可以在网关层面对服务进行熔断或降级（使用阿里的sentinel组件），网关还有其他的功能如认证授权、限流（只放行部分到服务器）等。

  到达服务器后进行处理（springboot为微服务），服务与服务可能会相互调用（使用feign组件），有些请求可能经过登录才能进行（基于OAuth2.0的认证中心。安全和权限使用springSecurity控制）

  服务可能保存了一些数据或者需要使用缓存，我们使用redis集群（分片+哨兵集群）。持久化使用mysql，读写分离和分库分表。

  服务和服务之间会使用消息队列（RabbitMQ），来完成异步解耦，分布式事务的一致性。有些服务可能需要全文检索，检索商品信息，使用ElaticSearch。

  服务可能需要存取数据，使用阿里云的对象存储服务OSS。

  项目上线后为了快速定位问题，使用ELK对日志进行处理，使用LogStash收集业务里的各种日志，把日志存储到ES中，用Kibana可视化页面从ES中检索出相关信息，帮助我们快速定位问题所在。

  在分布式系统中，由于我们每个服务都可能部署在很多台机器，服务和服务可能相互调用，就得知道彼此都在哪里，所以需要将所有服务都注册到注册中心。服务从注册中心发现其他服务所在位置（使用阿里Nacos作为注册中心）。

  每个服务的配置众多，为了实现改一处配置相同配置就同步更改，就需要配置中心，也使用阿里的Nacos，服务从配置中心中动态取配置。

  服务追踪，追踪服务调用链哪里出现问题，使用springcloud提供的Sleuth、Zipkin、Metrics，把每个服务的信息交给开源的Prometheus进行聚合分析，再由Grafana进行可视化展示，提供Prometheus提供的AlterManager实时得到服务的警告信息，以短信/邮件的方式告知服务开发人员。

  还提供了持续集成和持续部署。项目发布起来后，因为微服务众多，每一个都打包部署到服务器太麻烦，有了持续集成后开发人员可以将修改后的代码提交到github，运维人员可以通过自动化工具Jenkins Pipeline将github中获取的代码打包成docker镜像，最终是由k8s集成docker服务，将服务以docker容器的方式运行。


* **微服务划分图**
  [微服务划分图](./assets/谷粒商城-微服务划分图.jpg)

  反映了需要创建的微服务以及相关技术。

  前后分离开发。前端项目分为admin-vue（工作人员使用的后台管理系统）、shop-vue（面向公众访问的web网站）、app（公众）、小程序（公众）

  商品服务：商品的增删改查、商品的上下架、商品详情
  支付服务
  优惠服务
  用户服务：用户的个人中心、收货地址
  仓储服务：商品的库存
  秒杀服务
  订单服务：订单增删改查
  检索服务：商品的检索ES
  中央认证服务：登录、注册、单点登录、社交登录
  购物车服务
  后台管理系统：添加优惠信息等

* 项目技术&特色
  - 前后分离开发，并开发基于vue的后台管理系统
  - SpringCloud全新的解决方案
  - 应用监控、限流、网关、熔断降级等分布式方案，全方位涉及
  - 透彻讲解分布式事务，分布式锁等分布式系统的难点
  - 压力测试与性能优化
  - 各种集群技术的区别以及使用
  - CI/CD 使用

* 项目前置要求
  - 熟悉SpringBoot以及常见整合方案
  - 了解SpringCloud
  - 熟悉 git  maven
  - 熟悉 linux redis docker 基本操作
  - 了解 html，css，js，vue
  - 熟练使用idea开发项目

# 谷粒商城-基础篇
围绕电商后台管理系统. 使用前后端分离, 前端使用Vue.

## 分布式基础概念
### 微服务
微服务架构风格，就像是把一个单独的应用程序开发成一套小服务，每个小服务运行在自己的进程中，并使用轻量级机制通信，通常是 HTTP API 这些服务围绕业务能力来构建，	并通过完全自动化部署机制来独立部署，这些服务使用不同的编程语言书写，以及不同数据存储技术，并保持最低限度的集中式管理  
**简而言之，拒绝大型单体应用，基于业务边界进行服务微化拆分，每个服务独立部署运行。**

### 集群&分布式&节点
集群是个物理状态，分布式是个工作方式  
只要是一堆机器，也可以叫做集群，他们是不是一起协作干活，这谁也不知道。

《分布式系统原理与范型》定义：
* 分布式系统是若干独立计算机的集合，这些计算机对于用户来说像单个系统
* 分布式系统 (distributed system) 是建立网络之上的软件系统

分布式是指根据不同的业务分布在不同的地方，集群指的是将几台服务器集中在一起，实现同一业务  
例如：**京东是一个分布式系统，众多业务运行在不同的机器上**，所有业务构成一个大型的分布式**业务集群**，每一个小的业务，比如用户系统，访问压力大的时候一台服务器是不够的，我们就应该将用户系统部署到多个服务器，也就是每一个业务系统也可以做集群化

**分布式中的每一个节点，都可以做集群，而集群并不一定就是分布式的**  
*节点：集群中的一个服务器*

### 远程调用
在分布式系统中，各个服务可能处于不同主机，但是服务之间不可避免的需要互相调用，我们称之为远程调用

SpringCloud中使用HTTP+JSON的方式来完成远程调用
[](./assets/GuliMall.md/GuliMall_base/1657552169506.jpg)

### 负载均衡
[](./assets/GuliMall.md/GuliMall_base/20201218205600292.png)
分布式系统中，A 服务需要调用B服务，B服务在多台机器中都存在， A调用任意一个服务器均可完成功能
为了使每一个服务器都不要太或者太闲，我们可以负载均衡调用每一个服务器，提升网站的健壮性

**常见的负载均衡算法：**
* **轮询**：为第一个请求选择健康池中的每一个后端服务器，然后按顺序往后依次选择，直到最后一个，然后循环
* **最小连接**：优先选择链接数最少，也就是压力最小的后端服务器，在会话较长的情况下可以考虑采取这种方式

### 服务注册/发现&注册中心
A服务调用B服务，A服务不知道B服务当前在哪几台服务器上有，哪些正常的，哪些服务已经下线，解决这个问题可以引入注册中心
[](assets/GuliMall.md/GuliMall_base/20201218205736266.png)
如果某些服务下线，我们其他人可以实时的感知到其他服务的状态，从而避免调用不可用的服务

### 配置中心
[](./assets/GuliMall.md/GuliMall_base/20201218205841204.png)
每一个服务最终都有大量配置，并且每个服务都可能部署在多个服务器上，我们经常需要变更配置，我们可以让每个服务在配置中心获取自己的配置。

**配置中心用来集中管理微服务的配置信息**

### 服务熔断&服务降级
在微服务架构中，微服务之间通过网络来进行通信，存在相互依赖，当其中一个服务不可用时，有可能会造成雪崩效应，要防止这种情况，必须要有容错机制来保护服务
[](./assets/GuliMall.md/GuliMall_base/20201218211017200.png)

情景(rpc)：
订单服务 --> 商品服务 --> 库存服务
库存服务出现故障导致响应慢，导致商品服务需要等待，可能等到10s后库存服务才能响应。库存服务的不可用导致商品服务阻塞，商品服务等的期间，订单服务也处于阻塞。一个服务不可用导致整个服务链都阻塞。如果是高并发，第一个请求调用后阻塞10s得不到结果，第二个请求直接阻塞10s。更多的请求进来导致请求积压，全部阻塞，最终服务器的资源耗尽。导致雪崩

1、服务熔断
设置服务的超时，当被调用的服务经常失败到达某个阈值，我们可以开启断路保护机制，后来的请求不再去调用这个服务，本地直接返回默认的数据

2、服务降级
在运维期间，当系统处于高峰期，系统资源紧张，我们可以让非核心业务降级运行，降级：某些服务不处理，或者简单处理【抛异常，返回NULL，调用 Mock数据，调用 FallBack 处理逻辑】

### API 网关
在微服务架构中，API Gateway 作为整体架构的重要组件，它*抽象服务中需要的公共功能*，同时它提供了客户端**负载均衡，服务自动熔断，灰度发布，统一认证，限流监控，日志统计**等丰富功能，帮助我们解决很多API管理的难题
[](./assets/GuliMall.md/GuliMall_base/20201218211725909.png)

### 微服务架构图


## 环境搭建
### 安装虚拟机(CentOS)
Oracle VM VirtualBox下载地址: https://download.virtualbox.org/virtualbox/6.1.34/VirtualBox-6.1.34a-150636-Win.exe
> 安装完成后可以在'全局设定'->'常规'里修改虚拟机的安装位置

Vagrant下载地址: https://releases.hashicorp.com/vagrant/2.2.19/vagrant_2.2.19_x86_64.msi
> 如果vagrant下载速度很慢, 可以将地址复制到迅雷中, 使用迅雷下载  
安装完后再`cmd`命令窗口内运行`vagrant`查看安装结果

* 初始化并启动虚拟机
  ``` shell
  # 初始化虚拟机, 运行后可以在对应目录下看到`Vagrantfile`文件
  vagrant init centos/7 # 初始化虚拟机, 会自动下载镜像, 若下载慢可以更换镜像源

  # 启动虚拟机
  vagrant up

  # 进入虚拟机
  vagrant ssh
  ```
  > VirtualBox可能与其他软件冲突, 安装时注意避免
  > vagrant可能会遇到磁盘爆满的情况, 根目录下用`du -sh *`分析后会发现`/vagrant`占用超级大. 原因是vagrant同步了`c/user/用户名`下的文件,解决: 1.创建`VagrantSyncFolder`文件夹; 2.在`Vagrantfile`加上`config.vm.synced_folder "./VagrantSyncFolder", "/vagrant"`(指定映射文件夹); 3.加载配置并重启`vagrant reload`

* 网络设置
  如果使用端口转发的方式, 每安装一个软件就需要设置一次, 比较麻烦. 所以使用设置网络的方式, 可以设置一次, 后续不用再设置了

  默认虚拟机的ip地址是不固定的,开发不方便
  修改`Vagrantfile`文件, 在`Vagrantfile`文件中添加如下内容
  ``` shell
  config.vm.network "private_network", ip: "192.168.56.10"
  ```
  设置完后在cmd执行`vagrant reload`重启并重新加载即可

  检查网络配置
  ``` shell
  # 进入虚拟机
  vagrant ssh
  
  # 查看ip地址
  ip addr
  ```
  也可以宿主机与虚拟机互ping


### 安装docker（CentOS）
Docker, 虚拟化容器技术。Docker基于镜像，可以秒级启动各种容器。每一种容器都是一个完整的运环境，容器之间互相隔离。
[](./assets/GuliMall.md/GuliMall_base/1657605006009.jpg)
docker官方文档: https://docs.docker.com/get-started/overview/

``` shell
# 1.卸载系统之前的docker(没有可以省略)
sudo yum remove docker \
  docker-client \
  docker-client-latest \
  docker-common \
  docker-latest \
  docker-latest-logrotate \
  docker-logrotate \
  docker-engine

# 2.安装依赖并
sudo yum install -y yum-utils \
  device-mapper-persistent-data \
  lvm2

# 3. 设置存储库(阿里云)
sudo yum-config-manager \
  --add-repo \
  http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

# 4.安装DOCKER引擎
sudo yum install docker-ce docker-ce-cli containerd.io

# 4.启动Docker.
sudo systemctl start docker
# 4.1 设置开机自启
sudo systemctl enable docker

# 5.配置镜像加速
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://chqac97z.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker

# 检查docker状态
systemctl status docker
```

### 安装mysql(docker)
1. 安装mysql
    ``` shell
    # 1.拉取mysql镜像
    sudo docker pull mysql:5.7
    # 1.1.检查镜像
    sudo docker images

    # 2.启动mysql容器
    # --name指定容器名字 -p指定端口映射[容器端口:宿主机端口] -v目录挂载[宿主机路径:容器内路径] -e设置mysql启动参数 -d后台运行 镜像名
    sudo docker run --name mysql\
      -p 3306:3306\
      -v /mydata/mysql/log:/var/log/mysql\
      -v /mydata/mysql/data:/var/lib/mysql\
      -v /mydata/mysql/conf:/etc/mysql\
      -e MYSQL_ROOT_PASSWORD=root\
      -d mysql:5.7
    # 2.1.查看容器
    sudo docker ps

    # 0.切换为root，这样就不用每次都sudo来赐予了. 因为时使用vagrant创建的, 默认密码为`vagrant`
    su root

    # 3.进入mysql容器
    docker exec -it 容器名称|容器id bin/bash

    # 9.推出mysql容器
    exit
    ```
    [](./assets/GuliMall.md/GuliMall_base/1657690334567.jpg)

2. 配置mysql 
    进入配置文件(参考docker映射的目录)
    ``` shell
    vi /mydata/mysql/conf/my.cnf
    ```
    插入以下内容
    ``` shell
    [client]
    default-character-set=utf8
    [mysql]
    default-character-set=utf8
    [mysqld]
    init_connect='SET collation_connection = utf8_unicode_ci'
    init_connect='SET NAMES utf8'
    character-set-server=utf8
    collation-server=utf8_unicode_ci
    skip-character-set-client-handshake
    skip-name-resolve

    # Esc
    # :wq
    ```

3. 重启容器使配置生效
    ``` shell
    docker restart mysql
    ```

### 安装redis(docker)
Redis中文文档: [](http://www.redis.cn/)
1. 拉取redis镜像到本地
  ``` shell
  docker pull redis
  ```

2. 修改需要自定义的配置(docker-redis默认没有配置文件，自己在宿主机建立后挂载映射)
  ``` shell
  # 创建并编辑一下文件
  mkdir -p /mydata/redis/conf
  touch /mydata/redis/conf/redis.conf
  ```

3. 启动redis服务运行容器
  ``` shell
  docker run --name redis -p 6379:6379\
    -v /usr/local/redis/data:/data\
    -v /usr/local/redis/redis.conf:/usr/local/etc/redis/redis.conf\
    -d redis:6.0.10  redis-server /usr/local/etc/redis/redis.conf 
  ```

  查看容器
  ``` shell
  docker ps
  ```

  进入容器内的redis, 并测试
  ``` shell
  # 进入redis
  docker exec -it redis redis-cli
  
  # 存
  set a b

  # 取
  get a

  # 退出
  exit
  ```

4. 配置redis
  ``` shell
  # 开启远程权限
  echo "bind 0.0.0.0"  >> /mydata/redis/conf/redis.conf
  
  # 开启aof持久化
  echo "appendonly yes"  >> /mydata/redis/conf/redis.conf

  # 重启redis容器, 重启完后redis数据便可以持久化了
  docker restart redis
  ```

### 安装开发工具及插件(可选-方便开发)
#### Maven
这里在平时开发的时候应该已经配置过了,具体步骤就略过了
``` yaml
# 在maven配置文件配置配置阿里云镜像
<mirrors>
	<mirror>
		<id>nexus-aliyun</id>
		<mirrorOf>central</mirrorOf>
		<name>Nexus aliyun</name>
		<url>http://maven.aliyun.com/nexus/content/groups/public</url>
	</mirror>
</mirrors>

# 配置 jdk 1.8 编译项目
<profiles>
	<profile>
		<id>jdk-1.8</id>
		<activation>
			<activeByDefault>true</activeByDefault>
			<jdk>1.8</jdk>
		</activation>
		<properties>
			<maven.compiler.source>1.8</maven.compiler.source>
			<maven.compiler.target>1.8</maven.compiler.target>
			<maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
		</properties>
	</profile>
</profiles>
```

#### IDEA设置及插件
后端使用 IDEA 开发
* 设置Manven构建工具
  在 File -> Settings -> Build,Execution,Deployment -> Build Tools -> Maven 中
  将 User settings file 配置为指定的安装路径

* 插件
  - Lombok
  - MyBatisX

#### VS Code及插件
前端使用 VS Code 开发

* 插件
  - Auto Close Tag
  - Auto Rename Tag
  - Chinese
  - ESlint
  - HTML CSS Support
  - HTML Snippets
  - JavaScript (ES6) code snippets
  - Live Server
  - open in brower
  - Vetur

### 安装git
安装完git后, 打开git bash

* 配置账户
  ``` shell
  # 配置用户名
  git config --global user.name "username"  //(用户名)

  # 配置邮箱
  git config --global user.email "xxxx@qq.com" // 注册账号时使用的邮箱
  ```

* 配置ssh免密登录
  ``` shell
  # 生成ssh密钥
  ssh-keygen -t rsa -C "xxx@qq.com"

  # 三次回车后生成了密钥，也可以查看密钥
  cat ~/.ssh/id_rsa.pub
  ```
  浏览器登录码云后，个人头像上点设置、然后点ssh公钥、设置标题后，然后赋值刚才打印的密钥

* 测试
  ``` shell
  ssh -T git@gitee.com

    # 测试成功
    Hi unique_perfect! You've successfully a
```

### 创建仓库
``` shell
在码云新建仓库，仓库名gulimall，选择语言java，在.gitignore选中maven，
许可证选Apache-2.0，开发模型选生产/开发模型，开发时在dev分支，
发布时在master分支，创建如图所示
```

### 项目结构创建
> 这里我创建工程和依赖管理的方式和视频有出入，请酌情参考!!!
> 在码云创建项目和拉取这里就省略了

**创建模块**
创建以下根模块(gulimall)下创建一下微服务
商品服务product
存储服务ware
订单服务order
优惠券服务coupon
用户服务member

每个服务都有一下共同点:
1. 每个模块都导入基础依赖web和openFeign
2. 每个服务包名都统一一下, 以`com.atguigu.gulimall.xxx(product/order/ware/coupon/member)`
3. 模块名均应符合`gulimall-xxx`格式

**目录结构**
[](./assets/GuliMall.md/GuliMall_base/1657725334297.jpg)
多模块开发
多模块开发中，使用父模块对子模块的管理非常方便。
1. 父模块pom中的<properties>属性会被子模块继承
2. 父模块pom中，在<dependencyManagement>中可以进行子模块依赖的版本管理，子模块继承父模块之后，提供作用：锁定版本 + 子模块不用再写 version。
3. 此外，父模块中可以添加依赖作为全局依赖，子模块自动继承。<dependencyManagement>外的<dependencies>中定义全局依赖。

**修改父模块pom**
创建父模块：在gulimall中创建并修改pom.xml(以后还会继续添加), 并引入公共依赖
> 注意版本关系, 可参考官网
> Spring Cloud Alibaba: https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E
> Spring Cloud: https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.cheakin</groupId>
    <artifactId>gulimall</artifactId>
    <packaging>pom</packaging>
    <version>0.0.1-SNAPSHOT</version>
    <name>gulimall</name>
    <description>聚合服务</description>

    <modules>
        <module>gulimall-coupon</module>
        <module>gulimall-member</module>
        <module>gulimall-order</module>
        <module>gulimall-product</module>
        <module>gulimall-ware</module>
    </modules>

    <!--  这里的属性会被子模块继承  -->
    <properties>
        <java.version>1.8</java.version>
        <spring.boot.version>2.6.3</spring.boot.version>
        <spring-cloud.version>2021.0.1</spring-cloud.version>
    </properties>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring.boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <!--  这里的依赖会被子模块继承  -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```
子模块pom示例
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.zsy</groupId>
        <artifactId>guli-mall</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <groupId>com.zsy</groupId>
    <artifactId>mall-product</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>mall-product</name>
    <description>商品服务</description>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

**修改gitignore**
修改总项目的.gitignore，把小项目里的垃圾文件在提交的时候忽略掉
``` shell
 HELP.md
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

### VS Code ###
.vscode/

### other ###
**/mvnw
**/mvnw.cmd
**/.mvn
**/target
```
删除各模块下的`mvnw`, `mvnw.cmd`, `.mvn`, 这几个文件是声明maven版本的, 我们已经在设置中指定和修改过镜像源了, 不在需要这几个文件了; `HELP.md`也可以删除, 要的话留根目录下的就可以了

### 创建数据库
创建数据库之前需要启动docker服务
``` shell
sudo docker ps
sudo docker ps -a
# 这两个命令的差别就是后者会显示  【已创建但没有启动的容器】

# 我们接下来设置我们要用的容器每次都是自动启动
sudo docker update redis --restart=always
sudo docker update mysql --restart=always
# 如果不配置上面的内容的话，我们也可以选择手动启动
sudo docker start mysql
sudo docker start redis
# 如果要进入已启动的容器
sudo docker exec -it mysql /bin/bash
```

> 所有的数据库数据再复杂也不建立外键，因为在电商系统里，数据量大，做外键关联很耗性能。

**创建数据库**
数据库工具中开始操作,
建立数据库,字符集选utf8mb4，他能兼容utf8且能解决一些乱码的问题。
分别建立了下面数据库:
- gulimall_oms
- gulimall_pms
- gulimall_sms
- gulimall_ums
- gulimall_wms

<details>
  <summary>gulimall-oms.sql</summary>

  ``` sql
  drop table if exists oms_order;

  drop table if exists oms_order_item;

  drop table if exists oms_order_operate_history;

  drop table if exists oms_order_return_apply;

  drop table if exists oms_order_return_reason;

  drop table if exists oms_order_setting;

  drop table if exists oms_payment_info;

  drop table if exists oms_refund_info;

  /*==============================================================*/
  /* Table: oms_order                                             */
  /*==============================================================*/
  create table oms_order
  (
    id                   bigint not null auto_increment comment 'id',
    member_id            bigint comment 'member_id',
    order_sn             char(32) comment '订单号',
    coupon_id            bigint comment '使用的优惠券',
    create_time          datetime comment 'create_time',
    member_username      varchar(200) comment '用户名',
    total_amount         decimal(18,4) comment '订单总额',
    pay_amount           decimal(18,4) comment '应付总额',
    freight_amount       decimal(18,4) comment '运费金额',
    promotion_amount     decimal(18,4) comment '促销优化金额（促销价、满减、阶梯价）',
    integration_amount   decimal(18,4) comment '积分抵扣金额',
    coupon_amount        decimal(18,4) comment '优惠券抵扣金额',
    discount_amount      decimal(18,4) comment '后台调整订单使用的折扣金额',
    pay_type             tinyint comment '支付方式【1->支付宝；2->微信；3->银联； 4->货到付款；】',
    source_type          tinyint comment '订单来源[0->PC订单；1->app订单]',
    status               tinyint comment '订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】',
    delivery_company     varchar(64) comment '物流公司(配送方式)',
    delivery_sn          varchar(64) comment '物流单号',
    auto_confirm_day     int comment '自动确认时间（天）',
    integration          int comment '可以获得的积分',
    growth               int comment '可以获得的成长值',
    bill_type            tinyint comment '发票类型[0->不开发票；1->电子发票；2->纸质发票]',
    bill_header          varchar(255) comment '发票抬头',
    bill_content         varchar(255) comment '发票内容',
    bill_receiver_phone  varchar(32) comment '收票人电话',
    bill_receiver_email  varchar(64) comment '收票人邮箱',
    receiver_name        varchar(100) comment '收货人姓名',
    receiver_phone       varchar(32) comment '收货人电话',
    receiver_post_code   varchar(32) comment '收货人邮编',
    receiver_province    varchar(32) comment '省份/直辖市',
    receiver_city        varchar(32) comment '城市',
    receiver_region      varchar(32) comment '区',
    receiver_detail_address varchar(200) comment '详细地址',
    note                 varchar(500) comment '订单备注',
    confirm_status       tinyint comment '确认收货状态[0->未确认；1->已确认]',
    delete_status        tinyint comment '删除状态【0->未删除；1->已删除】',
    use_integration      int comment '下单时使用的积分',
    payment_time         datetime comment '支付时间',
    delivery_time        datetime comment '发货时间',
    receive_time         datetime comment '确认收货时间',
    comment_time         datetime comment '评价时间',
    modify_time          datetime comment '修改时间',
    primary key (id)
  );

  alter table oms_order comment '订单';

  /*==============================================================*/
  /* Table: oms_order_item                                        */
  /*==============================================================*/
  create table oms_order_item
  (
    id                   bigint not null auto_increment comment 'id',
    order_id             bigint comment 'order_id',
    order_sn             char(32) comment 'order_sn',
    spu_id               bigint comment 'spu_id',
    spu_name             varchar(255) comment 'spu_name',
    spu_pic              varchar(500) comment 'spu_pic',
    spu_brand            varchar(200) comment '品牌',
    category_id          bigint comment '商品分类id',
    sku_id               bigint comment '商品sku编号',
    sku_name             varchar(255) comment '商品sku名字',
    sku_pic              varchar(500) comment '商品sku图片',
    sku_price            decimal(18,4) comment '商品sku价格',
    sku_quantity         int comment '商品购买的数量',
    sku_attrs_vals       varchar(500) comment '商品销售属性组合（JSON）',
    promotion_amount     decimal(18,4) comment '商品促销分解金额',
    coupon_amount        decimal(18,4) comment '优惠券优惠分解金额',
    integration_amount   decimal(18,4) comment '积分优惠分解金额',
    real_amount          decimal(18,4) comment '该商品经过优惠后的分解金额',
    gift_integration     int comment '赠送积分',
    gift_growth          int comment '赠送成长值',
    primary key (id)
  );

  alter table oms_order_item comment '订单项信息';

  /*==============================================================*/
  /* Table: oms_order_operate_history                             */
  /*==============================================================*/
  create table oms_order_operate_history
  (
    id                   bigint not null auto_increment comment 'id',
    order_id             bigint comment '订单id',
    operate_man          varchar(100) comment '操作人[用户；系统；后台管理员]',
    create_time          datetime comment '操作时间',
    order_status         tinyint comment '订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】',
    note                 varchar(500) comment '备注',
    primary key (id)
  );

  alter table oms_order_operate_history comment '订单操作历史记录';

  /*==============================================================*/
  /* Table: oms_order_return_apply                                */
  /*==============================================================*/
  create table oms_order_return_apply
  (
    id                   bigint not null auto_increment comment 'id',
    order_id             bigint comment 'order_id',
    sku_id               bigint comment '退货商品id',
    order_sn             char(32) comment '订单编号',
    create_time          datetime comment '申请时间',
    member_username      varchar(64) comment '会员用户名',
    return_amount        decimal(18,4) comment '退款金额',
    return_name          varchar(100) comment '退货人姓名',
    return_phone         varchar(20) comment '退货人电话',
    status               tinyint(1) comment '申请状态[0->待处理；1->退货中；2->已完成；3->已拒绝]',
    handle_time          datetime comment '处理时间',
    sku_img              varchar(500) comment '商品图片',
    sku_name             varchar(200) comment '商品名称',
    sku_brand            varchar(200) comment '商品品牌',
    sku_attrs_vals       varchar(500) comment '商品销售属性(JSON)',
    sku_count            int comment '退货数量',
    sku_price            decimal(18,4) comment '商品单价',
    sku_real_price       decimal(18,4) comment '商品实际支付单价',
    reason               varchar(200) comment '原因',
    description述         varchar(500) comment '描述',
    desc_pics            varchar(2000) comment '凭证图片，以逗号隔开',
    handle_note          varchar(500) comment '处理备注',
    handle_man           varchar(200) comment '处理人员',
    receive_man          varchar(100) comment '收货人',
    receive_time         datetime comment '收货时间',
    receive_note         varchar(500) comment '收货备注',
    receive_phone        varchar(20) comment '收货电话',
    company_address      varchar(500) comment '公司收货地址',
    primary key (id)
  );

  alter table oms_order_return_apply comment '订单退货申请';

  /*==============================================================*/
  /* Table: oms_order_return_reason                               */
  /*==============================================================*/
  create table oms_order_return_reason
  (
    id                   bigint not null auto_increment comment 'id',
    name                 varchar(200) comment '退货原因名',
    sort                 int comment '排序',
    status               tinyint(1) comment '启用状态',
    create_time          datetime comment 'create_time',
    primary key (id)
  );

  alter table oms_order_return_reason comment '退货原因';

  /*==============================================================*/
  /* Table: oms_order_setting                                     */
  /*==============================================================*/
  create table oms_order_setting
  (
    id                   bigint not null auto_increment comment 'id',
    flash_order_overtime int comment '秒杀订单超时关闭时间(分)',
    normal_order_overtime int comment '正常订单超时时间(分)',
    confirm_overtime     int comment '发货后自动确认收货时间（天）',
    finish_overtime      int comment '自动完成交易时间，不能申请退货（天）',
    comment_overtime     int comment '订单完成后自动好评时间（天）',
    member_level         tinyint(2) comment '会员等级【0-不限会员等级，全部通用；其他-对应的其他会员等级】',
    primary key (id)
  );

  alter table oms_order_setting comment '订单配置信息';

  /*==============================================================*/
  /* Table: oms_payment_info                                      */
  /*==============================================================*/
  create table oms_payment_info
  (
    id                   bigint not null auto_increment comment 'id',
    order_sn             char(32) comment '订单号（对外业务号）',
    order_id             bigint comment '订单id',
    alipay_trade_no      varchar(50) comment '支付宝交易流水号',
    total_amount         decimal(18,4) comment '支付总金额',
    subject              varchar(200) comment '交易内容',
    payment_status       varchar(20) comment '支付状态',
    create_time          datetime comment '创建时间',
    confirm_time         datetime comment '确认时间',
    callback_content     varchar(4000) comment '回调内容',
    callback_time        datetime comment '回调时间',
    primary key (id)
  );

  alter table oms_payment_info comment '支付信息表';

  /*==============================================================*/
  /* Table: oms_refund_info                                       */
  /*==============================================================*/
  create table oms_refund_info
  (
    id                   bigint not null auto_increment comment 'id',
    order_return_id      bigint comment '退款的订单',
    refund               decimal(18,4) comment '退款金额',
    refund_sn            varchar(64) comment '退款交易流水号',
    refund_status        tinyint(1) comment '退款状态',
    refund_channel       tinyint comment '退款渠道[1-支付宝，2-微信，3-银联，4-汇款]',
    refund_content       varchar(5000),
    primary key (id)
  );

  alter table oms_refund_info comment '退款信息';
  ```
</details>

<details>
  <summary>gulimall-pms.sql</summary>

  ``` sql
  drop table if exists pms_attr;

  drop table if exists pms_attr_attrgroup_relation;

  drop table if exists pms_attr_group;

  drop table if exists pms_brand;

  drop table if exists pms_category;

  drop table if exists pms_category_brand_relation;

  drop table if exists pms_comment_replay;

  drop table if exists pms_product_attr_value;

  drop table if exists pms_sku_images;

  drop table if exists pms_sku_info;

  drop table if exists pms_sku_sale_attr_value;

  drop table if exists pms_spu_comment;

  drop table if exists pms_spu_images;

  drop table if exists pms_spu_info;

  drop table if exists pms_spu_info_desc;

  /*==============================================================*/
  /* Table: pms_attr                                              */
  /*==============================================================*/
  create table pms_attr
  (
    attr_id              bigint not null auto_increment comment '属性id',
    attr_name            char(30) comment '属性名',
    search_type          tinyint comment '是否需要检索[0-不需要，1-需要]',
    icon                 varchar(255) comment '属性图标',
    value_select         char(255) comment '可选值列表[用逗号分隔]',
    attr_type            tinyint comment '属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]',
    enable               bigint comment '启用状态[0 - 禁用，1 - 启用]',
    catelog_id           bigint comment '所属分类',
    show_desc            tinyint comment '快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整',
    primary key (attr_id)
  );

  alter table pms_attr comment '商品属性';

  /*==============================================================*/
  /* Table: pms_attr_attrgroup_relation                           */
  /*==============================================================*/
  create table pms_attr_attrgroup_relation
  (
    id                   bigint not null auto_increment comment 'id',
    attr_id              bigint comment '属性id',
    attr_group_id        bigint comment '属性分组id',
    attr_sort            int comment '属性组内排序',
    primary key (id)
  );

  alter table pms_attr_attrgroup_relation comment '属性&属性分组关联';

  /*==============================================================*/
  /* Table: pms_attr_group                                        */
  /*==============================================================*/
  create table pms_attr_group
  (
    attr_group_id        bigint not null auto_increment comment '分组id',
    attr_group_name      char(20) comment '组名',
    sort                 int comment '排序',
    descript             varchar(255) comment '描述',
    icon                 varchar(255) comment '组图标',
    catelog_id           bigint comment '所属分类id',
    primary key (attr_group_id)
  );

  alter table pms_attr_group comment '属性分组';

  /*==============================================================*/
  /* Table: pms_brand                                             */
  /*==============================================================*/
  create table pms_brand
  (
    brand_id             bigint not null auto_increment comment '品牌id',
    name                 char(50) comment '品牌名',
    logo                 varchar(2000) comment '品牌logo地址',
    descript             longtext comment '介绍',
    show_status          tinyint comment '显示状态[0-不显示；1-显示]',
    first_letter         char(1) comment '检索首字母',
    sort                 int comment '排序',
    primary key (brand_id)
  );

  alter table pms_brand comment '品牌';

  /*==============================================================*/
  /* Table: pms_category                                          */
  /*==============================================================*/
  create table pms_category
  (
    cat_id               bigint not null auto_increment comment '分类id',
    name                 char(50) comment '分类名称',
    parent_cid           bigint comment '父分类id',
    cat_level            int comment '层级',
    show_status          tinyint comment '是否显示[0-不显示，1显示]',
    sort                 int comment '排序',
    icon                 char(255) comment '图标地址',
    product_unit         char(50) comment '计量单位',
    product_count        int comment '商品数量',
    primary key (cat_id)
  );

  alter table pms_category comment '商品三级分类';

  /*==============================================================*/
  /* Table: pms_category_brand_relation                           */
  /*==============================================================*/
  create table pms_category_brand_relation
  (
    id                   bigint not null auto_increment,
    brand_id             bigint comment '品牌id',
    catelog_id           bigint comment '分类id',
    brand_name           varchar(255),
    catelog_name         varchar(255),
    primary key (id)
  );

  alter table pms_category_brand_relation comment '品牌分类关联';

  /*==============================================================*/
  /* Table: pms_comment_replay                                    */
  /*==============================================================*/
  create table pms_comment_replay
  (
    id                   bigint not null auto_increment comment 'id',
    comment_id           bigint comment '评论id',
    reply_id             bigint comment '回复id',
    primary key (id)
  );

  alter table pms_comment_replay comment '商品评价回复关系';

  /*==============================================================*/
  /* Table: pms_product_attr_value                                */
  /*==============================================================*/
  create table pms_product_attr_value
  (
    id                   bigint not null auto_increment comment 'id',
    spu_id               bigint comment '商品id',
    attr_id              bigint comment '属性id',
    attr_name            varchar(200) comment '属性名',
    attr_value           varchar(200) comment '属性值',
    attr_sort            int comment '顺序',
    quick_show           tinyint comment '快速展示【是否展示在介绍上；0-否 1-是】',
    primary key (id)
  );

  alter table pms_product_attr_value comment 'spu属性值';

  /*==============================================================*/
  /* Table: pms_sku_images                                        */
  /*==============================================================*/
  create table pms_sku_images
  (
    id                   bigint not null auto_increment comment 'id',
    sku_id               bigint comment 'sku_id',
    img_url              varchar(255) comment '图片地址',
    img_sort             int comment '排序',
    default_img          int comment '默认图[0 - 不是默认图，1 - 是默认图]',
    primary key (id)
  );

  alter table pms_sku_images comment 'sku图片';

  /*==============================================================*/
  /* Table: pms_sku_info                                          */
  /*==============================================================*/
  create table pms_sku_info
  (
    sku_id               bigint not null auto_increment comment 'skuId',
    spu_id               bigint comment 'spuId',
    sku_name             varchar(255) comment 'sku名称',
    sku_desc             varchar(2000) comment 'sku介绍描述',
    catalog_id           bigint comment '所属分类id',
    brand_id             bigint comment '品牌id',
    sku_default_img      varchar(255) comment '默认图片',
    sku_title            varchar(255) comment '标题',
    sku_subtitle         varchar(2000) comment '副标题',
    price                decimal(18,4) comment '价格',
    sale_count           bigint comment '销量',
    primary key (sku_id)
  );

  alter table pms_sku_info comment 'sku信息';

  /*==============================================================*/
  /* Table: pms_sku_sale_attr_value                               */
  /*==============================================================*/
  create table pms_sku_sale_attr_value
  (
    id                   bigint not null auto_increment comment 'id',
    sku_id               bigint comment 'sku_id',
    attr_id              bigint comment 'attr_id',
    attr_name            varchar(200) comment '销售属性名',
    attr_value           varchar(200) comment '销售属性值',
    attr_sort            int comment '顺序',
    primary key (id)
  );

  alter table pms_sku_sale_attr_value comment 'sku销售属性&值';

  /*==============================================================*/
  /* Table: pms_spu_comment                                       */
  /*==============================================================*/
  create table pms_spu_comment
  (
    id                   bigint not null auto_increment comment 'id',
    sku_id               bigint comment 'sku_id',
    spu_id               bigint comment 'spu_id',
    spu_name             varchar(255) comment '商品名字',
    member_nick_name     varchar(255) comment '会员昵称',
    star                 tinyint(1) comment '星级',
    member_ip            varchar(64) comment '会员ip',
    create_time          datetime comment '创建时间',
    show_status          tinyint(1) comment '显示状态[0-不显示，1-显示]',
    spu_attributes       varchar(255) comment '购买时属性组合',
    likes_count          int comment '点赞数',
    reply_count          int comment '回复数',
    resources            varchar(1000) comment '评论图片/视频[json数据；[{type:文件类型,url:资源路径}]]',
    content              text comment '内容',
    member_icon          varchar(255) comment '用户头像',
    comment_type         tinyint comment '评论类型[0 - 对商品的直接评论，1 - 对评论的回复]',
    primary key (id)
  );

  alter table pms_spu_comment comment '商品评价';

  /*==============================================================*/
  /* Table: pms_spu_images                                        */
  /*==============================================================*/
  create table pms_spu_images
  (
    id                   bigint not null auto_increment comment 'id',
    spu_id               bigint comment 'spu_id',
    img_name             varchar(200) comment '图片名',
    img_url              varchar(255) comment '图片地址',
    img_sort             int comment '顺序',
    default_img          tinyint comment '是否默认图',
    primary key (id)
  );

  alter table pms_spu_images comment 'spu图片';

  /*==============================================================*/
  /* Table: pms_spu_info                                          */
  /*==============================================================*/
  create table pms_spu_info
  (
    id                   bigint not null auto_increment comment '商品id',
    spu_name             varchar(200) comment '商品名称',
    spu_description      varchar(1000) comment '商品描述',
    catalog_id           bigint comment '所属分类id',
    brand_id             bigint comment '品牌id',
    weight               decimal(18,4),
    publish_status       tinyint comment '上架状态[0 - 下架，1 - 上架]',
    create_time          datetime,
    update_time          datetime,
    primary key (id)
  );

  alter table pms_spu_info comment 'spu信息';

  /*==============================================================*/
  /* Table: pms_spu_info_desc                                     */
  /*==============================================================*/
  create table pms_spu_info_desc
  (
    spu_id               bigint not null comment '商品id',
    decript              longtext comment '商品介绍',
    primary key (spu_id)
  );

  alter table pms_spu_info_desc comment 'spu信息介绍';
  ```
</details>

<details>
  <summary>gulimall_sms.sql</summary>
  
  ``` sql
  drop table if exists sms_coupon;

  drop table if exists sms_coupon_history;

  drop table if exists sms_coupon_spu_category_relation;

  drop table if exists sms_coupon_spu_relation;

  drop table if exists sms_home_adv;

  drop table if exists sms_home_subject;

  drop table if exists sms_home_subject_spu;

  drop table if exists sms_member_price;

  drop table if exists sms_seckill_promotion;

  drop table if exists sms_seckill_session;

  drop table if exists sms_seckill_sku_notice;

  drop table if exists sms_seckill_sku_relation;

  drop table if exists sms_sku_full_reduction;

  drop table if exists sms_sku_ladder;

  drop table if exists sms_spu_bounds;

  /*==============================================================*/
  /* Table: sms_coupon                                            */
  /*==============================================================*/
  create table sms_coupon
  (
    id                   bigint not null auto_increment comment 'id',
    coupon_type          tinyint(1) comment '优惠卷类型[0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券]',
    coupon_img           varchar(2000) comment '优惠券图片',
    coupon_name          varchar(100) comment '优惠卷名字',
    num                  int comment '数量',
    amount               decimal(18,4) comment '金额',
    per_limit            int comment '每人限领张数',
    min_point            decimal(18,4) comment '使用门槛',
    start_time           datetime comment '开始时间',
    end_time             datetime comment '结束时间',
    use_type             tinyint(1) comment '使用类型[0->全场通用；1->指定分类；2->指定商品]',
    note                 varchar(200) comment '备注',
    publish_count        int(11) comment '发行数量',
    use_count            int(11) comment '已使用数量',
    receive_count        int(11) comment '领取数量',
    enable_start_time    datetime comment '可以领取的开始日期',
    enable_end_time      datetime comment '可以领取的结束日期',
    code                 varchar(64) comment '优惠码',
    member_level         tinyint(1) comment '可以领取的会员等级[0->不限等级，其他-对应等级]',
    publish              tinyint(1) comment '发布状态[0-未发布，1-已发布]',
    primary key (id)
  );

  alter table sms_coupon comment '优惠券信息';

  /*==============================================================*/
  /* Table: sms_coupon_history                                    */
  /*==============================================================*/
  create table sms_coupon_history
  (
    id                   bigint not null auto_increment comment 'id',
    coupon_id            bigint comment '优惠券id',
    member_id            bigint comment '会员id',
    member_nick_name     varchar(64) comment '会员名字',
    get_type             tinyint(1) comment '获取方式[0->后台赠送；1->主动领取]',
    create_time          datetime comment '创建时间',
    use_type             tinyint(1) comment '使用状态[0->未使用；1->已使用；2->已过期]',
    use_time             datetime comment '使用时间',
    order_id             bigint comment '订单id',
    order_sn             bigint comment '订单号',
    primary key (id)
  );

  alter table sms_coupon_history comment '优惠券领取历史记录';

  /*==============================================================*/
  /* Table: sms_coupon_spu_category_relation                      */
  /*==============================================================*/
  create table sms_coupon_spu_category_relation
  (
    id                   bigint not null auto_increment comment 'id',
    coupon_id            bigint comment '优惠券id',
    category_id          bigint comment '产品分类id',
    category_name        varchar(64) comment '产品分类名称',
    primary key (id)
  );

  alter table sms_coupon_spu_category_relation comment '优惠券分类关联';

  /*==============================================================*/
  /* Table: sms_coupon_spu_relation                               */
  /*==============================================================*/
  create table sms_coupon_spu_relation
  (
    id                   bigint not null auto_increment comment 'id',
    coupon_id            bigint comment '优惠券id',
    spu_id               bigint comment 'spu_id',
    spu_name             varchar(255) comment 'spu_name',
    primary key (id)
  );

  alter table sms_coupon_spu_relation comment '优惠券与产品关联';

  /*==============================================================*/
  /* Table: sms_home_adv                                          */
  /*==============================================================*/
  create table sms_home_adv
  (
    id                   bigint not null auto_increment comment 'id',
    name                 varchar(100) comment '名字',
    pic                  varchar(500) comment '图片地址',
    start_time           datetime comment '开始时间',
    end_time             datetime comment '结束时间',
    status               tinyint(1) comment '状态',
    click_count          int comment '点击数',
    url                  varchar(500) comment '广告详情连接地址',
    note                 varchar(500) comment '备注',
    sort                 int comment '排序',
    publisher_id         bigint comment '发布者',
    auth_id              bigint comment '审核者',
    primary key (id)
  );

  alter table sms_home_adv comment '首页轮播广告';

  /*==============================================================*/
  /* Table: sms_home_subject                                      */
  /*==============================================================*/
  create table sms_home_subject
  (
    id                   bigint not null auto_increment comment 'id',
    name                 varchar(200) comment '专题名字',
    title                varchar(255) comment '专题标题',
    sub_title            varchar(255) comment '专题副标题',
    status               tinyint(1) comment '显示状态',
    url                  varchar(500) comment '详情连接',
    sort                 int comment '排序',
    img                  varchar(500) comment '专题图片地址',
    primary key (id)
  );

  alter table sms_home_subject comment '首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】';

  /*==============================================================*/
  /* Table: sms_home_subject_spu                                  */
  /*==============================================================*/
  create table sms_home_subject_spu
  (
    id                   bigint not null auto_increment comment 'id',
    name                 varchar(200) comment '专题名字',
    subject_id           bigint comment '专题id',
    spu_id               bigint comment 'spu_id',
    sort                 int comment '排序',
    primary key (id)
  );

  alter table sms_home_subject_spu comment '专题商品';

  /*==============================================================*/
  /* Table: sms_member_price                                      */
  /*==============================================================*/
  create table sms_member_price
  (
    id                   bigint not null auto_increment comment 'id',
    sku_id               bigint comment 'sku_id',
    member_level_id      bigint comment '会员等级id',
    member_level_name    varchar(100) comment '会员等级名',
    member_price         decimal(18,4) comment '会员对应价格',
    add_other            tinyint(1) comment '可否叠加其他优惠[0-不可叠加优惠，1-可叠加]',
    primary key (id)
  );

  alter table sms_member_price comment '商品会员价格';

  /*==============================================================*/
  /* Table: sms_seckill_promotion                                 */
  /*==============================================================*/
  create table sms_seckill_promotion
  (
    id                   bigint not null auto_increment comment 'id',
    title                varchar(255) comment '活动标题',
    start_time           datetime comment '开始日期',
    end_time             datetime comment '结束日期',
    status               tinyint comment '上下线状态',
    create_time          datetime comment '创建时间',
    user_id              bigint comment '创建人',
    primary key (id)
  );

  alter table sms_seckill_promotion comment '秒杀活动';

  /*==============================================================*/
  /* Table: sms_seckill_session                                   */
  /*==============================================================*/
  create table sms_seckill_session
  (
    id                   bigint not null auto_increment comment 'id',
    name                 varchar(200) comment '场次名称',
    start_time           datetime comment '每日开始时间',
    end_time             datetime comment '每日结束时间',
    status               tinyint(1) comment '启用状态',
    create_time          datetime comment '创建时间',
    primary key (id)
  );

  alter table sms_seckill_session comment '秒杀活动场次';

  /*==============================================================*/
  /* Table: sms_seckill_sku_notice                                */
  /*==============================================================*/
  create table sms_seckill_sku_notice
  (
    id                   bigint not null auto_increment comment 'id',
    member_id            bigint comment 'member_id',
    sku_id               bigint comment 'sku_id',
    session_id           bigint comment '活动场次id',
    subcribe_time        datetime comment '订阅时间',
    send_time            datetime comment '发送时间',
    notice_type          tinyint(1) comment '通知方式[0-短信，1-邮件]',
    primary key (id)
  );

  alter table sms_seckill_sku_notice comment '秒杀商品通知订阅';

  /*==============================================================*/
  /* Table: sms_seckill_sku_relation                              */
  /*==============================================================*/
  create table sms_seckill_sku_relation
  (
    id                   bigint not null auto_increment comment 'id',
    promotion_id         bigint comment '活动id',
    promotion_session_id bigint comment '活动场次id',
    sku_id               bigint comment '商品id',
    seckill_price        decimal comment '秒杀价格',
    seckill_count        decimal comment '秒杀总量',
    seckill_limit        decimal comment '每人限购数量',
    seckill_sort         int comment '排序',
    primary key (id)
  );

  alter table sms_seckill_sku_relation comment '秒杀活动商品关联';

  /*==============================================================*/
  /* Table: sms_sku_full_reduction                                */
  /*==============================================================*/
  create table sms_sku_full_reduction
  (
    id                   bigint not null auto_increment comment 'id',
    sku_id               bigint comment 'spu_id',
    full_price           decimal(18,4) comment '满多少',
    reduce_price         decimal(18,4) comment '减多少',
    add_other            tinyint(1) comment '是否参与其他优惠',
    primary key (id)
  );

  alter table sms_sku_full_reduction comment '商品满减信息';

  /*==============================================================*/
  /* Table: sms_sku_ladder                                        */
  /*==============================================================*/
  create table sms_sku_ladder
  (
    id                   bigint not null auto_increment comment 'id',
    sku_id               bigint comment 'spu_id',
    full_count           int comment '满几件',
    discount             decimal(4,2) comment '打几折',
    price                decimal(18,4) comment '折后价',
    add_other            tinyint(1) comment '是否叠加其他优惠[0-不可叠加，1-可叠加]',
    primary key (id)
  );

  alter table sms_sku_ladder comment '商品阶梯价格';

  /*==============================================================*/
  /* Table: sms_spu_bounds                                        */
  /*==============================================================*/
  create table sms_spu_bounds
  (
    id                   bigint not null auto_increment comment 'id',
    spu_id               bigint,
    grow_bounds          decimal(18,4) comment '成长积分',
    buy_bounds           decimal(18,4) comment '购物积分',
    work                 tinyint(1) comment '优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]',
    primary key (id)
  );

  alter table sms_spu_bounds comment '商品spu积分设置';
  ```
</details>

<details>
  <summary>gulimall_ums.sql</summary>

  ``` sql
  drop table if exists ums_growth_change_history;

  drop table if exists ums_integration_change_history;

  drop table if exists ums_member;

  drop table if exists ums_member_collect_spu;

  drop table if exists ums_member_collect_subject;

  drop table if exists ums_member_level;

  drop table if exists ums_member_login_log;

  drop table if exists ums_member_receive_address;

  drop table if exists ums_member_statistics_info;

  /*==============================================================*/
  /* Table: ums_growth_change_history                             */
  /*==============================================================*/
  create table ums_growth_change_history
  (
    id                   bigint not null auto_increment comment 'id',
    member_id            bigint comment 'member_id',
    create_time          datetime comment 'create_time',
    change_count         int comment '改变的值（正负计数）',
    note                 varchar(0) comment '备注',
    source_type          tinyint comment '积分来源[0-购物，1-管理员修改]',
    primary key (id)
  );

  alter table ums_growth_change_history comment '成长值变化历史记录';

  /*==============================================================*/
  /* Table: ums_integration_change_history                        */
  /*==============================================================*/
  create table ums_integration_change_history
  (
    id                   bigint not null auto_increment comment 'id',
    member_id            bigint comment 'member_id',
    create_time          datetime comment 'create_time',
    change_count         int comment '变化的值',
    note                 varchar(255) comment '备注',
    source_tyoe          tinyint comment '来源[0->购物；1->管理员修改;2->活动]',
    primary key (id)
  );

  alter table ums_integration_change_history comment '积分变化历史记录';

  /*==============================================================*/
  /* Table: ums_member                                            */
  /*==============================================================*/
  create table ums_member
  (
    id                   bigint not null auto_increment comment 'id',
    level_id             bigint comment '会员等级id',
    username             char(64) comment '用户名',
    password             varchar(64) comment '密码',
    nickname             varchar(64) comment '昵称',
    mobile               varchar(20) comment '手机号码',
    email                varchar(64) comment '邮箱',
    header               varchar(500) comment '头像',
    gender               tinyint comment '性别',
    birth                date comment '生日',
    city                 varchar(500) comment '所在城市',
    job                  varchar(255) comment '职业',
    sign                 varchar(255) comment '个性签名',
    source_type          tinyint comment '用户来源',
    integration          int comment '积分',
    growth               int comment '成长值',
    status               tinyint comment '启用状态',
    create_time          datetime comment '注册时间',
    primary key (id)
  );

  alter table ums_member comment '会员';

  /*==============================================================*/
  /* Table: ums_member_collect_spu                                */
  /*==============================================================*/
  create table ums_member_collect_spu
  (
    id                   bigint not null comment 'id',
    member_id            bigint comment '会员id',
    spu_id               bigint comment 'spu_id',
    spu_name             varchar(500) comment 'spu_name',
    spu_img              varchar(500) comment 'spu_img',
    create_time          datetime comment 'create_time',
    primary key (id)
  );

  alter table ums_member_collect_spu comment '会员收藏的商品';

  /*==============================================================*/
  /* Table: ums_member_collect_subject                            */
  /*==============================================================*/
  create table ums_member_collect_subject
  (
    id                   bigint not null auto_increment comment 'id',
    subject_id           bigint comment 'subject_id',
    subject_name         varchar(255) comment 'subject_name',
    subject_img          varchar(500) comment 'subject_img',
    subject_urll         varchar(500) comment '活动url',
    primary key (id)
  );

  alter table ums_member_collect_subject comment '会员收藏的专题活动';

  /*==============================================================*/
  /* Table: ums_member_level                                      */
  /*==============================================================*/
  create table ums_member_level
  (
    id                   bigint not null auto_increment comment 'id',
    name                 varchar(100) comment '等级名称',
    growth_point         int comment '等级需要的成长值',
    default_status       tinyint comment '是否为默认等级[0->不是；1->是]',
    free_freight_point   decimal(18,4) comment '免运费标准',
    comment_growth_point int comment '每次评价获取的成长值',
    priviledge_free_freight tinyint comment '是否有免邮特权',
    priviledge_member_price tinyint comment '是否有会员价格特权',
    priviledge_birthday  tinyint comment '是否有生日特权',
    note                 varchar(255) comment '备注',
    primary key (id)
  );

  alter table ums_member_level comment '会员等级';

  /*==============================================================*/
  /* Table: ums_member_login_log                                  */
  /*==============================================================*/
  create table ums_member_login_log
  (
    id                   bigint not null auto_increment comment 'id',
    member_id            bigint comment 'member_id',
    create_time          datetime comment '创建时间',
    ip                   varchar(64) comment 'ip',
    city                 varchar(64) comment 'city',
    login_type           tinyint(1) comment '登录类型[1-web，2-app]',
    primary key (id)
  );

  alter table ums_member_login_log comment '会员登录记录';

  /*==============================================================*/
  /* Table: ums_member_receive_address                            */
  /*==============================================================*/
  create table ums_member_receive_address
  (
    id                   bigint not null auto_increment comment 'id',
    member_id            bigint comment 'member_id',
    name                 varchar(255) comment '收货人姓名',
    phone                varchar(64) comment '电话',
    post_code            varchar(64) comment '邮政编码',
    province             varchar(100) comment '省份/直辖市',
    city                 varchar(100) comment '城市',
    region               varchar(100) comment '区',
    detail_address       varchar(255) comment '详细地址(街道)',
    areacode             varchar(15) comment '省市区代码',
    default_status       tinyint(1) comment '是否默认',
    primary key (id)
  );

  alter table ums_member_receive_address comment '会员收货地址';

  /*==============================================================*/
  /* Table: ums_member_statistics_info                            */
  /*==============================================================*/
  create table ums_member_statistics_info
  (
    id                   bigint not null auto_increment comment 'id',
    member_id            bigint comment '会员id',
    consume_amount       decimal(18,4) comment '累计消费金额',
    coupon_amount        decimal(18,4) comment '累计优惠金额',
    order_count          int comment '订单数量',
    coupon_count         int comment '优惠券数量',
    comment_count        int comment '评价数',
    return_order_count   int comment '退货数量',
    login_count          int comment '登录次数',
    attend_count         int comment '关注数量',
    fans_count           int comment '粉丝数量',
    collect_product_count int comment '收藏的商品数量',
    collect_subject_count int comment '收藏的专题活动数量',
    collect_comment_count int comment '收藏的评论数量',
    invite_friend_count  int comment '邀请的朋友数量',
    primary key (id)
  );

  alter table ums_member_statistics_info comment '会员统计信息';

  ```
</details>

<details>
  <summary>gulimall_wms.sql</summary>

  ``` sql
  /*
  SQLyog Ultimate v11.25 (64 bit)
  MySQL - 5.7.27 : Database - gulimall_wms
  *********************************************************************
  */


  /*!40101 SET NAMES utf8 */;

  /*!40101 SET SQL_MODE=''*/;

  /*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
  /*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
  /*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
  /*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
  CREATE DATABASE /*!32312 IF NOT EXISTS*/`gulimall_wms` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

  USE `gulimall_wms`;

  /*Table structure for table `undo_log` */

  DROP TABLE IF EXISTS `undo_log`;

  CREATE TABLE `undo_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `branch_id` bigint(20) NOT NULL,
    `xid` varchar(100) NOT NULL,
    `context` varchar(128) NOT NULL,
    `rollback_info` longblob NOT NULL,
    `log_status` int(11) NOT NULL,
    `log_created` datetime NOT NULL,
    `log_modified` datetime NOT NULL,
    `ext` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

  /*Data for the table `undo_log` */

  /*Table structure for table `wms_purchase` */

  DROP TABLE IF EXISTS `wms_purchase`;

  CREATE TABLE `wms_purchase` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `assignee_id` bigint(20) DEFAULT NULL,
    `assignee_name` varchar(255) DEFAULT NULL,
    `phone` char(13) DEFAULT NULL,
    `priority` int(4) DEFAULT NULL,
    `status` int(4) DEFAULT NULL,
    `ware_id` bigint(20) DEFAULT NULL,
    `amount` decimal(18,4) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购信息';

  /*Data for the table `wms_purchase` */

  /*Table structure for table `wms_purchase_detail` */

  DROP TABLE IF EXISTS `wms_purchase_detail`;

  CREATE TABLE `wms_purchase_detail` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `purchase_id` bigint(20) DEFAULT NULL COMMENT '采购单id',
    `sku_id` bigint(20) DEFAULT NULL COMMENT '采购商品id',
    `sku_num` int(11) DEFAULT NULL COMMENT '采购数量',
    `sku_price` decimal(18,4) DEFAULT NULL COMMENT '采购金额',
    `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
    `status` int(11) DEFAULT NULL COMMENT '状态[0新建，1已分配，2正在采购，3已完成，4采购失败]',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

  /*Data for the table `wms_purchase_detail` */

  /*Table structure for table `wms_ware_info` */

  DROP TABLE IF EXISTS `wms_ware_info`;

  CREATE TABLE `wms_ware_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name` varchar(255) DEFAULT NULL COMMENT '仓库名',
    `address` varchar(255) DEFAULT NULL COMMENT '仓库地址',
    `areacode` varchar(20) DEFAULT NULL COMMENT '区域编码',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库信息';

  /*Data for the table `wms_ware_info` */

  /*Table structure for table `wms_ware_order_task` */

  DROP TABLE IF EXISTS `wms_ware_order_task`;

  CREATE TABLE `wms_ware_order_task` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `order_id` bigint(20) DEFAULT NULL COMMENT 'order_id',
    `order_sn` varchar(255) DEFAULT NULL COMMENT 'order_sn',
    `consignee` varchar(100) DEFAULT NULL COMMENT '收货人',
    `consignee_tel` char(15) DEFAULT NULL COMMENT '收货人电话',
    `delivery_address` varchar(500) DEFAULT NULL COMMENT '配送地址',
    `order_comment` varchar(200) DEFAULT NULL COMMENT '订单备注',
    `payment_way` tinyint(1) DEFAULT NULL COMMENT '付款方式【 1:在线付款 2:货到付款】',
    `task_status` tinyint(2) DEFAULT NULL COMMENT '任务状态',
    `order_body` varchar(255) DEFAULT NULL COMMENT '订单描述',
    `tracking_no` char(30) DEFAULT NULL COMMENT '物流单号',
    `create_time` datetime DEFAULT NULL COMMENT 'create_time',
    `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
    `task_comment` varchar(500) DEFAULT NULL COMMENT '工作单备注',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存工作单';

  /*Data for the table `wms_ware_order_task` */

  /*Table structure for table `wms_ware_order_task_detail` */

  DROP TABLE IF EXISTS `wms_ware_order_task_detail`;

  CREATE TABLE `wms_ware_order_task_detail` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
    `sku_name` varchar(255) DEFAULT NULL COMMENT 'sku_name',
    `sku_num` int(11) DEFAULT NULL COMMENT '购买个数',
    `task_id` bigint(20) DEFAULT NULL COMMENT '工作单id',
    `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
    `lock_status` int(1) DEFAULT NULL COMMENT '1-已锁定  2-已解锁  3-扣减',
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存工作单';

  /*Data for the table `wms_ware_order_task_detail` */

  /*Table structure for table `wms_ware_sku` */

  DROP TABLE IF EXISTS `wms_ware_sku`;

  CREATE TABLE `wms_ware_sku` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `sku_id` bigint(20) DEFAULT NULL COMMENT 'sku_id',
    `ware_id` bigint(20) DEFAULT NULL COMMENT '仓库id',
    `stock` int(11) DEFAULT NULL COMMENT '库存数',
    `sku_name` varchar(200) DEFAULT NULL COMMENT 'sku_name',
    `stock_locked` int(11) DEFAULT '0' COMMENT '锁定库存',
    PRIMARY KEY (`id`),
    KEY `sku_id` (`sku_id`) USING BTREE,
    KEY `ware_id` (`ware_id`) USING BTREE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品库存';

  /*Data for the table `wms_ware_sku` */

  /*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
  /*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
  /*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
  /*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
  ```
</details>

## 快速开发
通过人人开源生成代码

在码云上搜索人人开源，我们使用renren-fast，renren-fast-vue项目。
``` shell
git clone https://gitee.com/renrenio/renren-fast.git

git clone https://gitee.com/renrenio/renren-fast-vue.git
```
下载到了桌面，我们把renren-fast移动到我们的项目(gulimall)文件夹（删掉.git文件），
而renren-fast-vue是用VSCode打开的（后面再弄）

### renren-fast
在idea(root)项目里的pom.xml添加一个
``` xml
<modules>
    <module>gulimall-coupon</module>
    <module>gulimall-member</module>
    <module>gulimall-order</module>
    <module>gulimall-product</module>
    <module>gulimall-ware</module>

    <module>renren-fast</module>
</modules>
```
数据库工具中创建`gulimall_admin`数据库, 
在`renren-fast/db/mysql.sql`目录下执行sql, 创建系统基础数据表
<details>
  <summary>gulimall_admin</summary>

  ``` sql
  -- 菜单
  CREATE TABLE `sys_menu` (
    `menu_id` bigint NOT NULL AUTO_INCREMENT,
    `parent_id` bigint COMMENT '父菜单ID，一级菜单为0',
    `name` varchar(50) COMMENT '菜单名称',
    `url` varchar(200) COMMENT '菜单URL',
    `perms` varchar(500) COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
    `type` int COMMENT '类型   0：目录   1：菜单   2：按钮',
    `icon` varchar(50) COMMENT '菜单图标',
    `order_num` int COMMENT '排序',
    PRIMARY KEY (`menu_id`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='菜单管理';

  -- 系统用户
  CREATE TABLE `sys_user` (
    `user_id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(100) COMMENT '密码',
    `salt` varchar(20) COMMENT '盐',
    `email` varchar(100) COMMENT '邮箱',
    `mobile` varchar(100) COMMENT '手机号',
    `status` tinyint COMMENT '状态  0：禁用   1：正常',
    `create_user_id` bigint(20) COMMENT '创建者ID',
    `create_time` datetime COMMENT '创建时间',
    PRIMARY KEY (`user_id`),
    UNIQUE INDEX (`username`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='系统用户';

  -- 系统用户Token
  CREATE TABLE `sys_user_token` (
    `user_id` bigint(20) NOT NULL,
    `token` varchar(100) NOT NULL COMMENT 'token',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `token` (`token`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='系统用户Token';

  -- 系统验证码
  CREATE TABLE `sys_captcha` (
    `uuid` char(36) NOT NULL COMMENT 'uuid',
    `code` varchar(6) NOT NULL COMMENT '验证码',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    PRIMARY KEY (`uuid`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='系统验证码';

  -- 角色
  CREATE TABLE `sys_role` (
    `role_id` bigint NOT NULL AUTO_INCREMENT,
    `role_name` varchar(100) COMMENT '角色名称',
    `remark` varchar(100) COMMENT '备注',
    `create_user_id` bigint(20) COMMENT '创建者ID',
    `create_time` datetime COMMENT '创建时间',
    PRIMARY KEY (`role_id`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='角色';

  -- 用户与角色对应关系
  CREATE TABLE `sys_user_role` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint COMMENT '用户ID',
    `role_id` bigint COMMENT '角色ID',
    PRIMARY KEY (`id`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='用户与角色对应关系';

  -- 角色与菜单对应关系
  CREATE TABLE `sys_role_menu` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `role_id` bigint COMMENT '角色ID',
    `menu_id` bigint COMMENT '菜单ID',
    PRIMARY KEY (`id`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='角色与菜单对应关系';

  -- 系统配置信息
  CREATE TABLE `sys_config` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `param_key` varchar(50) COMMENT 'key',
    `param_value` varchar(2000) COMMENT 'value',
    `status` tinyint DEFAULT 1 COMMENT '状态   0：隐藏   1：显示',
    `remark` varchar(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE INDEX (`param_key`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='系统配置信息表';


  -- 系统日志
  CREATE TABLE `sys_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `username` varchar(50) COMMENT '用户名',
    `operation` varchar(50) COMMENT '用户操作',
    `method` varchar(200) COMMENT '请求方法',
    `params` varchar(5000) COMMENT '请求参数',
    `time` bigint NOT NULL COMMENT '执行时长(毫秒)',
    `ip` varchar(64) COMMENT 'IP地址',
    `create_date` datetime COMMENT '创建时间',
    PRIMARY KEY (`id`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='系统日志';


  -- 文件上传
  CREATE TABLE `sys_oss` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `url` varchar(200) COMMENT 'URL地址',
    `create_date` datetime COMMENT '创建时间',
    PRIMARY KEY (`id`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='文件上传';


  -- 定时任务
  CREATE TABLE `schedule_job` (
    `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务id',
    `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
    `params` varchar(2000) DEFAULT NULL COMMENT '参数',
    `cron_expression` varchar(100) DEFAULT NULL COMMENT 'cron表达式',
    `status` tinyint(4) DEFAULT NULL COMMENT '任务状态  0：正常  1：暂停',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`job_id`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='定时任务';

  -- 定时任务日志
  CREATE TABLE `schedule_job_log` (
    `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志id',
    `job_id` bigint(20) NOT NULL COMMENT '任务id',
    `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
    `params` varchar(2000) DEFAULT NULL COMMENT '参数',
    `status` tinyint(4) NOT NULL COMMENT '任务状态    0：成功    1：失败',
    `error` varchar(2000) DEFAULT NULL COMMENT '失败信息',
    `times` int(11) NOT NULL COMMENT '耗时(单位：毫秒)',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`log_id`),
    KEY `job_id` (`job_id`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='定时任务日志';



  -- 用户表
  CREATE TABLE `tb_user` (
    `user_id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `mobile` varchar(20) NOT NULL COMMENT '手机号',
    `password` varchar(64) COMMENT '密码',
    `create_time` datetime COMMENT '创建时间',
    PRIMARY KEY (`user_id`),
    UNIQUE INDEX (`username`)
  ) ENGINE=`InnoDB` DEFAULT CHARACTER SET utf8mb4 COMMENT='用户';






  -- 初始数据 
  INSERT INTO `sys_user` (`user_id`, `username`, `password`, `salt`, `email`, `mobile`, `status`, `create_user_id`, `create_time`) VALUES ('1', 'admin', '9ec9750e709431dad22365cabc5c625482e574c74adaebba7dd02f1129e4ce1d', 'YzcmCZNvbXocrsz9dm8e', 'root@renren.io', '13612345678', '1', '1', '2016-11-11 11:11:11');

  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (1, 0, '系统管理', NULL, NULL, 0, 'system', 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (2, 1, '管理员列表', 'sys/user', NULL, 1, 'admin', 1);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (3, 1, '角色管理', 'sys/role', NULL, 1, 'role', 2);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (4, 1, '菜单管理', 'sys/menu', NULL, 1, 'menu', 3);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (5, 1, 'SQL监控', 'http://localhost:8080/renren-fast/druid/sql.html', NULL, 1, 'sql', 4);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (6, 1, '定时任务', 'job/schedule', NULL, 1, 'job', 5);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (7, 6, '查看', NULL, 'sys:schedule:list,sys:schedule:info', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (8, 6, '新增', NULL, 'sys:schedule:save', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (9, 6, '修改', NULL, 'sys:schedule:update', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (10, 6, '删除', NULL, 'sys:schedule:delete', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (11, 6, '暂停', NULL, 'sys:schedule:pause', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (12, 6, '恢复', NULL, 'sys:schedule:resume', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (13, 6, '立即执行', NULL, 'sys:schedule:run', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (14, 6, '日志列表', NULL, 'sys:schedule:log', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (15, 2, '查看', NULL, 'sys:user:list,sys:user:info', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (16, 2, '新增', NULL, 'sys:user:save,sys:role:select', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (17, 2, '修改', NULL, 'sys:user:update,sys:role:select', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (18, 2, '删除', NULL, 'sys:user:delete', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (19, 3, '查看', NULL, 'sys:role:list,sys:role:info', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (20, 3, '新增', NULL, 'sys:role:save,sys:menu:list', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (21, 3, '修改', NULL, 'sys:role:update,sys:menu:list', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (22, 3, '删除', NULL, 'sys:role:delete', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (23, 4, '查看', NULL, 'sys:menu:list,sys:menu:info', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (24, 4, '新增', NULL, 'sys:menu:save,sys:menu:select', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (25, 4, '修改', NULL, 'sys:menu:update,sys:menu:select', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (26, 4, '删除', NULL, 'sys:menu:delete', 2, NULL, 0);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (27, 1, '参数管理', 'sys/config', 'sys:config:list,sys:config:info,sys:config:save,sys:config:update,sys:config:delete', 1, 'config', 6);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (29, 1, '系统日志', 'sys/log', 'sys:log:list', 1, 'log', 7);
  INSERT INTO `sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES (30, 1, '文件上传', 'oss/oss', 'sys:oss:all', 1, 'oss', 6);

  INSERT INTO `sys_config` (`param_key`, `param_value`, `status`, `remark`) VALUES ('CLOUD_STORAGE_CONFIG_KEY', '{\"aliyunAccessKeyId\":\"\",\"aliyunAccessKeySecret\":\"\",\"aliyunBucketName\":\"\",\"aliyunDomain\":\"\",\"aliyunEndPoint\":\"\",\"aliyunPrefix\":\"\",\"qcloudBucketName\":\"\",\"qcloudDomain\":\"\",\"qcloudPrefix\":\"\",\"qcloudSecretId\":\"\",\"qcloudSecretKey\":\"\",\"qiniuAccessKey\":\"NrgMfABZxWLo5B-YYSjoE8-AZ1EISdi1Z3ubLOeZ\",\"qiniuBucketName\":\"ios-app\",\"qiniuDomain\":\"http://7xqbwh.dl1.z0.glb.clouddn.com\",\"qiniuPrefix\":\"upload\",\"qiniuSecretKey\":\"uIwJHevMRWU0VLxFvgy0tAcOdGqasdtVlJkdy6vV\",\"type\":1}', '0', '云存储配置信息');
  INSERT INTO `schedule_job` (`bean_name`, `params`, `cron_expression`, `status`, `remark`, `create_time`) VALUES ('testTask', 'renren', '0 0/30 * * * ?', '0', '参数测试', now());


  -- 账号：13612345678  密码：admin
  INSERT INTO `tb_user` (`username`, `mobile`, `password`, `create_time`) VALUES ('mark', '13612345678', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', '2017-03-23 22:37:41');



  --  quartz自带表结构
  CREATE TABLE QRTZ_JOB_DETAILS(
  SCHED_NAME VARCHAR(120) NOT NULL,
  JOB_NAME VARCHAR(200) NOT NULL,
  JOB_GROUP VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(250) NULL,
  JOB_CLASS_NAME VARCHAR(250) NOT NULL,
  IS_DURABLE VARCHAR(1) NOT NULL,
  IS_NONCONCURRENT VARCHAR(1) NOT NULL,
  IS_UPDATE_DATA VARCHAR(1) NOT NULL,
  REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
  JOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE TABLE QRTZ_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  JOB_NAME VARCHAR(200) NOT NULL,
  JOB_GROUP VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(250) NULL,
  NEXT_FIRE_TIME BIGINT(13) NULL,
  PREV_FIRE_TIME BIGINT(13) NULL,
  PRIORITY INTEGER NULL,
  TRIGGER_STATE VARCHAR(16) NOT NULL,
  TRIGGER_TYPE VARCHAR(8) NOT NULL,
  START_TIME BIGINT(13) NOT NULL,
  END_TIME BIGINT(13) NULL,
  CALENDAR_NAME VARCHAR(200) NULL,
  MISFIRE_INSTR SMALLINT(2) NULL,
  JOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
  REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  REPEAT_COUNT BIGINT(7) NOT NULL,
  REPEAT_INTERVAL BIGINT(12) NOT NULL,
  TIMES_TRIGGERED BIGINT(10) NOT NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE TABLE QRTZ_CRON_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  CRON_EXPRESSION VARCHAR(120) NOT NULL,
  TIME_ZONE_ID VARCHAR(80),
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE TABLE QRTZ_SIMPROP_TRIGGERS
    (          
      SCHED_NAME VARCHAR(120) NOT NULL,
      TRIGGER_NAME VARCHAR(200) NOT NULL,
      TRIGGER_GROUP VARCHAR(200) NOT NULL,
      STR_PROP_1 VARCHAR(512) NULL,
      STR_PROP_2 VARCHAR(512) NULL,
      STR_PROP_3 VARCHAR(512) NULL,
      INT_PROP_1 INT NULL,
      INT_PROP_2 INT NULL,
      LONG_PROP_1 BIGINT NULL,
      LONG_PROP_2 BIGINT NULL,
      DEC_PROP_1 NUMERIC(13,4) NULL,
      DEC_PROP_2 NUMERIC(13,4) NULL,
      BOOL_PROP_1 VARCHAR(1) NULL,
      BOOL_PROP_2 VARCHAR(1) NULL,
      PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
      FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
      REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE TABLE QRTZ_BLOB_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  BLOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE TABLE QRTZ_CALENDARS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  CALENDAR_NAME VARCHAR(200) NOT NULL,
  CALENDAR BLOB NOT NULL,
  PRIMARY KEY (SCHED_NAME,CALENDAR_NAME))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE TABLE QRTZ_FIRED_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  ENTRY_ID VARCHAR(95) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  INSTANCE_NAME VARCHAR(200) NOT NULL,
  FIRED_TIME BIGINT(13) NOT NULL,
  SCHED_TIME BIGINT(13) NOT NULL,
  PRIORITY INTEGER NOT NULL,
  STATE VARCHAR(16) NOT NULL,
  JOB_NAME VARCHAR(200) NULL,
  JOB_GROUP VARCHAR(200) NULL,
  IS_NONCONCURRENT VARCHAR(1) NULL,
  REQUESTS_RECOVERY VARCHAR(1) NULL,
  PRIMARY KEY (SCHED_NAME,ENTRY_ID))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE TABLE QRTZ_SCHEDULER_STATE (
  SCHED_NAME VARCHAR(120) NOT NULL,
  INSTANCE_NAME VARCHAR(200) NOT NULL,
  LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
  CHECKIN_INTERVAL BIGINT(13) NOT NULL,
  PRIMARY KEY (SCHED_NAME,INSTANCE_NAME))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE TABLE QRTZ_LOCKS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  LOCK_NAME VARCHAR(40) NOT NULL,
  PRIMARY KEY (SCHED_NAME,LOCK_NAME))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
  CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);

  CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
  CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
  CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
  CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
  CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
  CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
  CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
  CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
  CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
  CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
  CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
  CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

  CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
  CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
  CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
  CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
  CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
  CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
  ```
</details>

`renren-fast`项目中的`application.yml`默认使用`dev`配置文件，
修改`application-dev.yml`中数据库连接配置: 
``` yml
url: jdbc:mysql://192.168.56.10:3306/gulimall_admin?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
username: root
password: root
```

然后执行java下的RenrenApplication, *此时`renren-fast`的`pom.xml`可能会爆红,可以先不用管,仍然能启动起来*
浏览器输入http://localhost:8080/renren-fast/ 得到{“msg”:“invalid token”,“code”:401}就代表无误

### renren-fast-vue
用VSCode打开renren-fast-vue

* 安装node：
  官网: https://nodejs.org/en/, NPM是随同NodeJS一起安装的包管理工具.
  下载后缀为`LTS`(稳定版)的, 然后直接安装...
  安装完后可以在cmd命令窗口使用`node -v`验证
  
  接下来配置npm使用淘宝镜像, cmd在控制台运行
  ``` shell
  npm config set registry http://registry.npm.taobao.org/
  ```

  > 注意：版本为v10.16.3，python版本为3（因为不同版本等下下面遇到的问题可能不一样）
  
* 启动项目
  然后在VScode的终端
  手下先下载和安装项目所需的依赖, 进入项目中输入 
  ``` shell
  npm install
  ```
  安装的依赖都会存到node_modules里
  
  启动项目
  ``` shell
  npm run dev
  ```

  浏览器输入启动后的地址, 如: `http://localhost:8001`, (后端需要启动)
  就可以看到内容了，登录账号admin 密码admin

### 逆向工程搭建
git clone https://gitee.com/renrenio/renren-generator.git
下载到桌面后，同样把里面的.git文件删除，然后移动到我们IDEA项目目录中，

* 生成代码
  同样配置好pom.xml(root)
  ``` xml
  <modules>
      <module>gulimall-coupon</module>
      <module>gulimall-member</module>
      <module>gulimall-order</module>
      <module>gulimall-product</module>
      <module>gulimall-ware</module>
      <module>renren-fast</module>
      <module>renren-generator</module>
  </modules>
  ```

  修改`renren-generator`的`application.yml`
  ``` yml
  url: jdbc:mysql://192.168.1.103:3306/要生成的对应的数据库?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
  username: root
  password: root
  ```
  修改`renren-generator`的`generator.properties`
  ``` properties
  mainPath=cn.cheakin # 主目录
  package=cn.cheakin.gulimall # 包名
  moduleName=对应的模块名   # 模块名
  author=botboy  # 作者
  email=cheakin@foxmail.com  # email
  tablePrefix=对应数据库的表前缀   # 我们的对应数据库中的表的都用相同前缀，如果写了表前缀，每一张表对于的javaBean就不会添加前缀了
  ```
  修改`renren-generator`中`resource/templates/Controller.java.vm`(这是生成Controller的模板). 
  将`@RequiresPermissions("xxx")`以java注释的方式注释掉, 并将`import org.apache.shiro.authz.annotation.RequiresPermissions;`包引入删除掉


  运行`RenrenApplication`。如果启动不成功，修改`application.yml`中的`port`为`其他端口`。访问`http://localhost:80`
  1. 然后选择对应模块,选择全部(注意分页)，点击生成代码。下载压缩包
  2. 解压压缩包，把`main`放到`对应模块(guilmall-product, gulimall-order...)`的同级目录下; `main/resources/view/`文件夹用不到,可以删除
  依次创建以下代码
  * coupon(sms)(7000端口)
  * member(ums)(8000端口)
  * order(oms)(9000端口)
  * product(pms)(100000端口)
  * ware(wms)(11000端口)

* 创建公共模块`gulimall-common`
  创建后会发现有很多报红, 是因为缺少对应的包, 在`renren-fast`中都包含了这些缺失的包.
  现在我们将这个公共的整合到一个公共模块`gulimall-common`中
  1. 新建`gulimall-common`模块
  2. 将每个guli-*模块的`pom.xml`中引入`gulimall-common`
  3. 查看报红的地方, 依次将代码从`renren-fast`中移动到`gulimall-common`中, 其中`pom.xml`也需要同样拷贝过来
    `gulimall-common`模块的`pom.xml`, *xxs攻击后面处理*
    [](./assets/GuliMall.md/GuliMall_base/1657871814306.jpg)
    ``` xml
    <?xml version="1.0" encoding="UTF-8"?>
      <project xmlns="http://maven.apache.org/POM/4.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
          <modelVersion>4.0.0</modelVersion>
          <parent>
              <artifactId>gulimall</artifactId>
              <groupId>cn.cheakin</groupId>
              <version>0.0.1-SNAPSHOT</version>
          </parent>
          <groupId>cn.cheakin</groupId>
          <artifactId>gulimall-common</artifactId>
          <version>0.0.1-SNAPSHOT</version>
          <description>公共模块</description>
          <build>
              <plugins>
                  <plugin>
                      <groupId>org.apache.maven.plugins</groupId>
                      <artifactId>maven-compiler-plugin</artifactId>
                      <configuration>
                          <source>8</source>
                          <target>8</target>
                      </configuration>
                  </plugin>
              </plugins>
          </build>

          <dependencies>
              <!--mybatis-plus-->
              <dependency>
                  <groupId>com.baomidou</groupId>
                  <artifactId>mybatis-plus-boot-starter</artifactId>
                  <version>3.5.1 </version>
              </dependency>

              <!--lombok-->
              <dependency>
                  <groupId>org.projectlombok</groupId>
                  <artifactId>lombok</artifactId>
                  <version>1.18.22</version>
              </dependency>

              <!--renrenfast其他依赖-->
              <dependency>
                  <groupId>org.apache.httpcomponents</groupId>
                  <artifactId>httpcore</artifactId>
                  <version>4.4.15</version>
              </dependency>
              <dependency>
                  <groupId>commons-lang</groupId>
                  <artifactId>commons-lang</artifactId>
                  <version>2.6</version>
              </dependency>
          </dependencies>

      </project>
    ```
  4. 配置(整合)`mybatis-plus`
    1. 配置数据源:
         1. 导入`mybatis-plus-spring-boot-starter`依赖
           ``` xml
           <!--mybatis-plus-->
           <dependency>
               <groupId>com.baomidou</groupId>
               <artifactId>mybatis-plus-boot-starter</artifactId>
               <version>3.5.1 </version>
           </dependency>
           ```
         2. 导入MySQL数据库的驱动, 官方提示: 5.1或8.0(推荐)的依赖是兼容5.7的
           ```xml
           <!--mysql驱动-->
           <dependency>
               <groupId>mysql</groupId>
               <artifactId>mysql-connector-java</artifactId>
               <version>8.0.27</version>
           </dependency>
           ```
         3. 配置数据源
           为每个模块都配置对应的数据源, 如`gulimall-product`的`application.yml`:
           ``` yml
           spring:
             datasource: 
               username: root
               password: root
               url: jdbc:mysql://192.168.56.10:3306/gulimall_pms
               driver-class-name: com.mysql.cj.jdbc.Driver # 8.o用com.mysql.cj.jdbc.Driver， 8.0一下可以用 com.mysql.jdbc.Driver
           ```
    2. 配置MyBatis-Plus
      1. 使用`@MapperScan`
        在启动类上添加`@MapperScan`注解. **注意：不要重复注入，可能会出现bean重复**
      2. 指定MyBtis-Plus的映射文件位置
        同样在每个模块配置的配置文件`application.yml`中添加
        ``` yml 
        mybatis-plus:
          # 指定mapper文件位置
          mapper-locations: classpath*:/mapper/**/*.xml # classpath指当前项目的classpath, classpath*则不指定当前项目
          # 指定主键自增
          global-config:
            db-config:
              id-type: auto
        ```
    3. application.yml
      applicatin.yml的完整配置
      ``` YML
      spring:
        datasource:
          username: root
          password: root
          url: jdbc:mysql://192.168.56.10:3306/gulimall_pms
          driverClassName: com.mysql.cj.jdbc.Driver
      mybatis-plus:
        # 指定mapper文件位置
        mapper-locations: classpath*:/mapper/**/*.xml # classpath指当前项目的classpath, classpath*则不指定当前项目
        # 指定主键自增
        global-config:
          db-config:
            id-type: auto
      server:
        port: 10000
      ```
      注意区别不通模块的数据库和端口
  5. 测试
    在`gulimall-product`模块的单元测试中测试
    ``` java
    @SpringBootTest
    class GulimallProductApplicationTests {

        @Autowired
        BrandService brandService;

        @Test
        void add() {
            BrandEntity brandEntity = new BrandEntity();
            brandEntity.setName("华为");

            brandService.save(brandEntity);
            System.out.println("保存完成");
        }

        @Test
        void update() {
            BrandEntity brandEntity = new BrandEntity();

            brandEntity.setBrandId(1L);
            brandEntity.setDescript("华为手机");

            brandService.updateById(brandEntity);
        }

        @Test
        void quey() {
            List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
            list.forEach(item -> {
                System.out.println("item = " + item);
            });
        }

    }
    ```
    



## Spring Cloud Alibab
[](./assets/GuliMall.md/GuliMall_base/1657939731397.jpg)

### 简介
Spring Cloud Alibaba 致力于提供微服务开发的一站式解决方案。此项目包含开发分布式应用微服务的必需组件，方便开发者通过 Spring Cloud 编程模型轻松使用这些组件来开发分布
式应用服务。
依托 Spring Cloud Alibaba，您只需要添加一些注解和少量配置，就可以将 Spring Cloud应用接入阿里微服务解决方案，通过阿里中间件来迅速搭建分布式应用系统。
官方仓库: https://github.com/alibaba/spring-cloud-alibaba
Spring Cloud Alibaba 版本说明:https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E 

SpringCloud的几大痛点:
* SpringCloud部分组件停止维护和更新，给开发带来不便；
* SpringCloud部分环境搭建复杂，没有完善的可视化界面，我们需要大量的二次开发和定制
* SpringCloud配置复杂，难以上手，部分配置差别难以区分和合理应用

SpringCloud Alibaba 的优势：
阿里使用过的组件经历了考验，性能强悍，设计合理，现在开源出来大家用成套的产品搭配完善的可视化界面给开发运维带来极大的便利. 搭建简单，学习曲线低。

结合 SpringCloud Alibaba 我们最终的技术搭配方案。
**SpringCloud Alibaba - Nacos：注册中心（服务发现/注册）**
**SpringCloud Allbaba - Nacos " 配置中心（动态配直管理）**
**SpringCloud - Ribbon：负载均衡**
**SpringCloud - Feign：卢明式HTTP客户请（调用远程服务）**
**Springdloud Allbaba - Sentinel：服务容错（限流、降级、熔断）**
**SpringCloud - Gateway : API 网关（webflux 编程模式)**
**SpringCloud - Sleuth：调用链监控**
**SpringCloud Alibaba - Seata . 原 Fescar，即分布式事务解决方案**

* 引入依赖
在`gulimall-common`中的`pom.xml`中添加(参考:[Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba/blob/2021.x/README-zh.md))
``` xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2021.0.1.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Nacos(注册中心)
官方文档: https://nacos.io/zh-cn/docs/what-is-nacos.html
Spring Cloud Ablibab - Nacos Discovery (注册中心): https://github.com/alibaba/spring-cloud-alibaba/blob/2.2.x/spring-cloud-alibaba-examples/nacos-example/nacos-discovery-example/readme-zh.md

1. 下载并启动
  下载地址： https://github.com/alibaba/nacos/releases
  下载对应的版本即可，默认端口是`8848`, 启动后访问`http://127.0.0.1:8848/nacos`, 默认账号密码都是`nacos`
  > 注意: 目前我们是非集群模式, windows下启动需要将`/bin/startup.sh`启动脚本中的`set MODE="cluster"`修改为`set MODE="standalone"`; linux下参照文档使用`sh startup.sh -m standalone`启动即可
2. 引入依赖
  在`gulimall-common`中的`pom.xml`中添加
  ``` xml
  <dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
      <exclusions>
          <exclusion>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-netflix-ribbon</artifactId>
          </exclusion>
      </exclusions>
  </dependency>
  ```
3. 配置注册地址
  在需要的模块(即除gulimall-common外)的`application.yml`中增加
  ``` yml
  spring:  
    cloud:
      nacos:
        discovery:
          server-addr: 127.0.0.1:8848
    application:
      name: gulimall-coupon # 必须指定服务名称, 否则无法注册成功
  ```
4. 使用`@EnableDiscoveryClient`注解开启服务注册与发现功能
   启动服务后在注册中心就可以看到服务了
   [](./assets/GuliMall.md/GuliMall_base/1657974842626.jpg)
5. 依上, 为其他服务配置注册中心

若远程服务掉线, 那么nacos也会对应更新状态
[](./assets/GuliMall.md/GuliMall_base/1657993655727.jpg)


### Feign远程调用
#### 简介
Feign是一个声明式的HTTP客户端.它的目的就是让远程调用更加简单。Feign 提供了 HTTP请求的横板，**通过编写简单的接口和插入注解**，就可以定义好HTTP请求的参数、格式、地址等信息。
Feign 整合了**Ribbon（负载均衡）**和**Hystrix(服务熔断)**，可以让我们不再需要显式地使用这两个组件。
SpringCloudFeign 在 NetflixFelgn的基础上扩展了 对SpringMVC注解的支持，在其实现下，我们只需创建一个接口并用注解的方式来配置它，即可完成对服务提供方的接口绑定。简化了SpringCloudRibbon自行封装服务调用客户端的开发量。

#### 使用
* 引入依赖
  创建项目时引入过的就可以忽略了
  ``` xml
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-openfeign</artifactId>
  </dependency>
  ```
* 开启Feign功能(如前端通过**接口调用gulimall-member**, **gulimall-member远程调用**远程的**gulimall-coupon的接口和服务**)
  首先gulimall-coupon需要有相应的接口和服务, 如在`gulimall-coupon`的`CouponController`新建接口和服务(为方便,这里简单写一个controller层的方法)
  ``` java
  @RequestMapping("/member/list")
  public R membercouponList(){
      CouponEntity entity = new CouponEntity();
      entity.setCouponName("满100-10");
      return R.ok().put("coupons", Arrays.asList(entity));
  }
  ```
  1. 引入open-feign
    启动类使用`@EnableDiscoveryClient`注解(**gulimall-member的启动类**)
  2. 编写接口, 告诉SpringCloud这个接口这个接口需要远程服务(**gulimall-member中**)
    - 声明远程服务调用哪一个方法
      方便管理, 把统一服务的远程调用写到同一个类中
      ``` java
      @FeignClient("gulimall-coupon")
      public interface CouponFeignService {

          @RequestMapping("/coupon/coupon/member/list") // 需要完整的路径
          public R membercouponList();

      }
      ```
    - 声明接口(**gulimall-member中发起远程调用的接口**)
      如在`gulimall-member`的`MemberController`中增加用户调用接口
      ``` java
      @RequestMapping("/coupons")
      public R test(){
          MemberEntity memberEntity = new MemberEntity();
          memberEntity.setNickname("张三");

          R membercoupons = couponFeignService.membercouponList();

          return R.ok().put("member", memberEntity)
                  .put("coupons", membercoupons.get("coupons"));
      }
      ```
  3. 开启远程调用功能
    使用`@EnableFeignClients`注解(**在gulimall-member的启动类上加`@EnableFeignClients(basePackages = "cn.cheakin.gulimall.member.feign")`注解**)
  4. 排错
    OpenFeign报错
    ``` java
    No Feign Client for loadBalancing defined. Didyou forget to include spring-cloud-starter-loadbalance
    ```
    Spring Cloud版本 2020.0.3, Spring Boot版本 2.4.6
    原因是因为SpringCloud Feign在Hoxton.M2 RELEASED版本之后抛弃了Ribbon，使用了spring-cloud-loadbalancer，所以我们这里还需要引入spring-cloud-loadbalancer的依赖，否则就会报错
    ``` xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-loadbalancer</artifactId>
    </dependency>
    ```
    同时nacos也要排除掉ribbo
    ``` xml
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-netflix-ribbon</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    ```
    > 参考: https://blog.csdn.net/weixin_44524763/article/details/121730595
  5. 测试
    访问`http://localhost:8000/member/member/coupons`, 返回
    ``` json
    {"msg":"success","code":0,"coupons":[{"id":null,"couponType":null,"couponImg":null,"couponName":"满100-10","num":null,"amount":null,"perLimit":null,"minPoint":null,"startTime":null,"endTime":null,"useType":null,"note":null,"publishCount":null,"useCount":null,"receiveCount":null,"enableStartTime":null,"enableEndTime":null,"code":null,"memberLevel":null,"publish":null}],"member":{"id":null,"levelId":null,"username":null,"password":null,"nickname":"张三","mobile":null,"email":null,"header":null,"gender":null,"birth":null,"city":null,"job":null,"sign":null,"sourceType":null,"integration":null,"growth":null,"status":null,"createTime":null}}
    ```

### 配置中心
官方文档: https://nacos.io/zh-cn/docs/what-is-nacos.html
Spring Cloud Ablibab - Nacos Discovery (注册中心): https://github.com/alibaba/spring-cloud-alibaba/blob/2.2.x/spring-cloud-alibaba-examples/nacos-example/nacos-config-example/readme-zh.md

1. 下载并启动
    略
2. 引入依赖
    在`gulimall-common`中的`pom.xml`中添加
    ``` xml
    <!--Nacos注册中心-->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
    <!--新版spring-cloud弃用bootstrap.properties导致的传统配置方式不生效-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-bootstrap</artifactId>
    </dependency>
    ```
3. 配置配置中心地址(如`gulimall-coupon`模块)
    在应用的 `/src/main/resources/bootstrap.properties`(bootstrap.properties的启动优先级是大于aplication.yml的) 配置文件中配置 Nacos Config 元数据
    ``` YML
    spring.application.name=gulimall-coupon # 一般用服务名
    spring.cloud.nacos.config.server-addr=127.0.0.1:8848
    ```
4. 编写配置(如`gulimall-coupon`模块)
    在`gulimall-coupon`新建`/src/main/resources/application.properties`, 充当默认配置
    ``` yml
    coupon.user.name=zhangsan
    coupon.user.age=18
    ```

    **在配置中心配置**
    [](./assets/GuliMall.md/GuliMall_base/1658061630460.jpg)
    [](./assets/GuliMall.md/GuliMall_base/1658061780954.jpg)
    Data ID 默认为 应用名.properties, 如gulimall-coupon.properties
5. 测试(如`gulimall-coupon`模块)
    编写测试接口, 在`CouponController`类中, **类上加`@RefreshScope`注解表示启用启动刷新**
    ``` java
    @Value("${coupon.user.name}")
    private String name;
    @Value("${coupon.user.age}")
    private Integer age;

    /**
     * 测试配置中心
     */
    @RequestMapping("/test")
    public R test() {
        return R.ok().put("name", name).put("age", age);
    }
    ```
    访问`http://localhost:7000/coupon/coupon/test`, 返回结果:
    ``` json
    {"msg":"success","code":0,"name":"zhangsan","age":18}
    ```

    **若修改配置**
    [](./assets/GuliMall.md/GuliMall_base/1658063170131.jpg)
    访问`http://localhost:7000/coupon/coupon/test`, 返回结果:
    ``` json
    {"msg":"success","code":0,"name":"zhangsan","age":22}
    ```

    > 如果配置中心和当前应用都配置了相同的项, 会优先使用配置中心的

### 配置中心进阶
1. 命名空间
    用于进行租户粒度的配置隔离。不同的命名空间下，可以存在相同的 `Group` 或 `Data ID` 的配置。`Namespace` 的常用场景之一是不同环境的配置的区分隔离，例如开发测试环境和生产环境的资源（如配置、服务）隔离等。
    默认：public（保留空间）：默认新增的所有配百都在pubLic空间。Nacos2.1是可以自定义id的
    - 方式一
      如开发（dev)、测试(test)、生产(prod)，切换不同的命名空间, 可以利用命名空间做环境隔离
      只需要在`bootstrap.properties`中配置
      ``` yml
      spring.cloud.nacos.config.namespace=e62d9969-bade-4ab7-a028-e6201d362a23  # 命名空间id
      ```
      [](./assets/GuliMall.md/GuliMall_base/1658064849230.jpg)
    - 方式二
      每一个微服务之间项目隔离配置, 每个微服务都创建自己的命名空间, 只加载自己命名空间下的所有配置
      原理都是一样的, 只是分类的方式不同而已

2. 配置集
  所有配置的集合

3. 配置集ID
  即Data ID, 类似文件名

4. 配置分组
  默认所有的配置集都属于: DEFAULT_GROUP
  可以用与区分环境配置

`gulimall`中我们将采用: 每个微服务船舰自己命名空间, 使用配置分组区分环境(dev/test/prod)
[](./assets/GuliMall.md/GuliMall_base/1658067186247.jpg)

**加载多配置集**
我们将`application.yml`中不同类型的配置独立开来
* 数据源相关配置`datasource.yml`
  [](./assets/GuliMall.md/GuliMall_base/1658067448324.jpg)
  ``` yml
  spring:
    datasource:
      username: root
      password: root
      url: jdbc:mysql://192.168.56.10:3306/gulimall_sms
      driverClassName: com.mysql.cj.jdbc.Driver
  ```
* MyBatis相关配置`mybatis.yml`
  [](./assets/GuliMall.md/GuliMall_base/1658067562785.jpg)
  ``` yml
  mybatis-plus:
  # 指定mapper文件位置
  mapper-locations: classpath*:/mapper/**/*.xml # classpath指当前项目的classpath, classpath*则不指定当前项目
  # 指定主键自增
  global-config:
    db-config:
      id-type: auto
  ```
* 其他相关配置`other.yml`
  [](./assets/GuliMall.md/GuliMall_base/1658067755354.jpg)
  ``` yml
  spring:
    cloud:
      nacos:
        discovery:
          server-addr: 127.0.0.1:8848
    application:
      name: gulimall-coupon

  server:
    port: 7000
  ```
在`bootstrap.properties`中新增配置
``` yml
spring.cloud.nacos.config.extension-configs[0].data-id=datasource.yml
spring.cloud.nacos.config.extension-configs[0].group=dev
spring.cloud.nacos.config.extension-configs[0].refresh=true

spring.cloud.nacos.config.extension-configs[1].data-id=mybatis.yml
spring.cloud.nacos.config.extension-configs[1].group=dev
spring.cloud.nacos.config.extension-configs[1].refresh=true

spring.cloud.nacos.config.extension-configs[2].data-id=other.yml
spring.cloud.nacos.config.extension-configs[2].group=dev
spring.cloud.nacos.config.extension-configs[2].refresh=true
```
能正常启动并能正常访问`http://localhost:7000/coupon/coupon/test`即可
- 微服务任何配置信息, 任何配置文件都可以放在配置中心中
- 只需要在`bootstrap.properties`说明加载配置重谢的哪些配置文件即可
- @Value, @ConfigrationPoroperties同样可以使用(原来怎么用现在还是怎么用)
  


### 网关
[](./assets/GuliMall.md/GuliMall_base/1658136252118.jpg)
#### 简介
网关作为流量的入口，常用功能包括路由转发、权限校验、限流控制等。而springcloud gateway作为SpringCloud官方推出的第二代网关框架，取代了Zuul网关。

* 创建模块gulimall-gateway

# 谷粒商城-高级篇
围绕商城前端的流程系统. 搜索、结算、登录, 以及周边治理、流控、链路追踪等


# 谷粒商城-集群篇
包括k8s集群，CI/CD(持续集成)，DevOps等


# 其他
## 项目架构
### 架构图
[](./assets/GuliMall.md/谷粒商城-微服务架构图.jpg)
### 微服务划分图
[](./assets/GuliMall.md/谷粒商城-微服务划分图.jpg)

## [基础篇](./GuliMall_base.md)
[基础篇](./GuliMall_base), 

## [高级篇](./GuliMall_senior.md)
[高级篇](./GuliMall_senior.md), 

## [集群篇](./GuliMall_cluster.md)
[集群篇](./GuliMall_cluster.md), 包括k8s集群，CI/CD(持续集成)，DevOps等

## 环境
### 安装jdk1.8
``` shell
#1.下载并解压jdk1.8
tar  -zxvf  jdk-8u281-linux-x64.tar.gz(检查本机有没有jdk有的话卸载掉。安装上自己的jdk)

# 2.配上环境变量
vim /etc/profile
# 添加一下两行代码
export JAVA_HOME=jdk的位置
export PATH=$PATH:$JAVA_HOME/bin

# 3.使配置生效
source  /etc/profile
```