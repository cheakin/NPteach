# Kubernetes

容器话集群管理系统

## 特性

1. 自动装箱

   基于容器对应运行环境的资源配置要求自动部署应用容器

2. 自我修复(自愈)

   当容器失败时,会对容器进行重启

   当所部署的node节点由问题时，会对容器进行重新部署和重新调度

   当容器为通过监控检查时，会关闭此容器知道容器正常运行，才会对外提供服务

3. 水平扩展(弹性扩展)

   对容器动态的扩容或裁剪

4. 服务发现

   k8s自身就由能里实现服务发现和负载均衡

5. 滚动更新

   可以根据应用变化，对应邀容器运行的应用，进行一次性或批量式更新

6. 版本回退

   可以根据应用部署情况，对应用容器惊喜历史版本即时回退

7. 密钥和配置管理

   在不重启的情况下，更新密钥或配置，类似热部署

8. 存储编排

   自动实现粗出系统挂载及应用，特别对由状态应用实现数据持久化非常重要。粗出系统可以来自于本地、网络、公共云等存储服务

9. 批处理

   提供一次性任务，定时任务



## 集群架构组件

![k8s集群架构组件](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210602180413930.png)

* 控制平面组件（Control Plane）

  * apiserver

    集群统一入口，以restful风格方式，交给ectd存储

  * scheduler

    节点调度，选择节点应用部署

  * cloud-controller-manager

    处理集群中常规后天任务，一个资源对应一个控制器

  * etcd

    存储系统，用户保存集群数据

* Node 组件

  * kubelet

    管理本机的node节点

  * kube-proxy

    提供组件的网络代理



1. pod
   * 最小部署单元
   * 一组容器的集合
   * 共享网络
   * 生命周期时短暂的
2. controller
   * 确保预期的pod副本数量
   * 有状态部署
   * 无状态部署
   * 确保所有的node运行同一个pod
   * 一次性任务或定时任务
3. service
   * 定义一组pod的访问规则



## 搭建k8s集群

### 一、安装多台台虚拟

### 二、初始话环境

1. 关闭防火墙

   `systemctl stop firewalld.service`

   `systemctl disable firewalld.service`

2. 关闭selinux

   `sed -i 's/enforcing/disabled/' /etc/selinux/config`	永久关闭

   `setenforce 0`	临时关闭

3. 关闭swap

   `sed -ri 's/.*swap.*/#&/' /etc/fstab`	永久关闭

   `swapoff -a`	临时关闭

4. 根据规则规划设置主机名

   `hostnamectl set-hostname <hostname>`

   如：`hostnamectl set-hostname k8smaster`

   ​		 `hostnamectl set-hostname k8snode1`

   ​		 `hostnamectl set-hostname k8snode2`

5. 在master添加hosts(只在mastae中添加)

   ```sh
   cat >> /etc/host << EOF
   192.168.42.136 m1
   192.168.42.137 n1
   EOF
   ```

   

6. 将桥接得IPv4流量传递到iptables的链(3台中都执行)

   ```shell
   cat >> /etc/sysctl.d/k8s.conf << EOF
   net.brige/brige-nf-call-ip6tables = 1
   net.brige/brige-nf-call-iptables = 1
   EOF
   ```

   `sysctl --system`	生效

7. 时间同步

   ```shell
   yum install ntpdate -y
   ntpdate time.windows.com
   ```

   

8. 设置yum为阿里云源(可忽略)

   `mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup`	备份

   `wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-8.repo`		下载新的CentOS-Base.repo 到/etc/yum.repos.d/

   `yum makecache`		生成缓存

   

### 三、所有节点安装Docker/kubeadm/bubelet/kubectl

- `kubeadm`：用来初始化集群的指令。
- `kubelet`：在集群中的每个节点上用来启动 Pod 和容器等。
- `kubectl`：用来与集群通信的命令行工具。

#### 1. 安装docker

* 安装

  `yum install -y yum-utils`

  ```bash
  yum-config-manager \
      --add-repo \
      https://download.docker.com/linux/centos/docker-ce.repo
  ```

  `yum install docker-ce docker-ce-cli containerd.io`

* 设置阿里镜像源

  ```sh
  tee /etc/docker/daemon.json <<-'EOF'
  {
    "registry-mirrors": ["https://8td8dhaz.mirror.aliyuncs.com"]
  }
  EOF
  ```

  

* 设置开机启动

  `systemctl enable docker &&  systemctl start docker`

#### 2. 安装kubeadm/bubelet/kubectl

添加Kubernetes的yum源

```sh
cat >> /etc/yum.repos.d/kubernetes.repo <<EOF
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
enabled=1
gpgcheck=0
EOF
```



`yum install -y kubelet kubeadm kubectl`	安装

`systemctl enable kubelet`	开机启动

#### 3. 部署Kubernetes Mastae

在master中执行

```bash
kubeadm init \
--apiserver-advertise-address=192.168.42.133 \	#API 服务器所公布的其正在监听的 IP 地址。如果未设置，则使用默认网络接口。(当前ip)
--image-repository registry.aliyuncs.com/google_containers \	#选择用于拉取控制平面镜像的容器仓库(阿里云)
--service-cidr=10.96..xxx.xxx.xxx/n \	#为服务的虚拟 IP 地址另外指定IP地址段(忽略)
--pod-network-cidr=xxx.xxx.xxx.xxx/16	#指明 pod 网络可以使用的IP地址段(忽略)


kubeadm init \
--apiserver-advertise-address=192.168.42.133 \
--image-repository registry.aliyuncs.com/google_containers \
--service-cidr=10.96.0.0/12 \
--pod-network-cidr=192.168.0.0/16
```

在初始化时，会通过docker去下载一些必要的镜像，遇到某些镜像下不了时可以使用docker下载，然后重新执行初始化命令（参考：https://blog.51cto.com/8999a/2784605）



在成功执行后，会提示接下来的操作

![image-20210603150451862](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210603150451862.png)

* 在master节点执行

  ```bash
  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config
  ```

  

* 在node节点执行

  ```bash
  kubeadm join 192.168.42.133:6443 --token buuhm4.ed8hmcl18qw7zeg6 \
          --discovery-token-ca-cert-hash sha256:056684213b250c74936fbfb1c6457d28d5690b9f08282150a330435a3a41b53f
  ```

  默认token有效期为24小时，当过期之后该token就不可以哦那个了，这是需要重新创建token

  `kubeadm token create --print-join-command`





完成后在master节点中`bukectl get nodes`可以查看各节点信息

![image-20210603151152737](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210603151152737.png)



#### 4. 安装CNI网络插件

1. 此时节还未开始工作，需要安装网络插件

   ```bash
   kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
   ```

   加入报错提示无法连接，是应为被墙了，所以需要修改raw.githubusercontent.com为其真实IP（参考：https://www.cnblogs.com/oscarli/p/12737409.html）

   ```bash
   sudo vim /etc/hosts
   185.199.111.133 raw.githubusercontent.com
   ```

   

   执行完后，通过下面的命令查看kube-flannel是否正常运行

    `kubectl get pods -n kube-system`

   

   此时集群就整正常运行状态了，`kubectl get nodes`

   ![image-20210603154712053](C:\Users\Miittech\AppData\Roaming\Typora\typora-user-images\image-20210603154712053.png)



#### 5. 测试kubernetes集群

在kubernetes集群中船舰一个pod，验证是狗正常运行

```bash
kubectl create deployment nginx --image=nginx	#拉取镜像
kubectl expose deployment nginx --port=80 --type=NodePort	#对外暴露端口
kubectl get pod,svc	#查看暴露的端口
```

访问地址：http:NodeIP:Port



## 二进制方式搭建k8s集群

### 一、安装多台台虚拟

略

### 二、为etcd和apiserver生成自签证书

生成ca-key.pem、ca.pem

以及server-key.pem、server.pem

### 三、部署etcd集群

本质 把etcd服务交给system管理

把生成证书复制过来，启动，设置开机启动

### 四、为apiServer自签证书

与etcd类似

### 五、部署master组件

1. apiServer
2. controller-manager
3. scheduler

交给system管理组件->启动->开机启动

### 六、部署node组件

1. docker
2. kubelet
3. kube-proxy

交给system管理组件->启动->开机启动

4. 批准kubelet证书申请并加入集群

### 七、部署CNI网络插件

略



##  Kubernetes集群管理工具kubectl

kubectl是Kubernetes集群的命令行工具，通过kubectl能够对集群本身进行管理，并能够在集群上进行容器化应用的安装和部署

###  命令格式

```bash
kubectl [command] [type] [name] [flags]
```

参数

- command：指定要对资源执行的操作，例如create、get、describe、delete
- type：指定资源类型，资源类型是大小写敏感的，开发者能够以单数 、复数 和 缩略的形式



通过 help命令，能够获取帮助信息

```bash
# 获取kubectl的命令
kubectl --help

# 获取某个命令的介绍和使用
kubectl get --help
```



###  基础命令

常见的基础命令

| 命令    | 介绍                                           |
| ------- | ---------------------------------------------- |
| create  | 通过文件名或标准输入创建资源                   |
| expose  | 将一个资源公开为一个新的Service                |
| run     | 在集群中运行一个特定的镜像                     |
| set     | 在对象上设置特定的功能                         |
| get     | 显示一个或多个资源                             |
| explain | 文档参考资料                                   |
| edit    | 使用默认的编辑器编辑一个资源                   |
| delete  | 通过文件名，标准输入，资源名称或标签来删除资源 |

### 部署命令

| 命令           | 介绍                                               |
| -------------- | -------------------------------------------------- |
| rollout        | 管理资源的发布                                     |
| rolling-update | 对给定的复制控制器滚动更新                         |
| scale          | 扩容或缩容Pod数量，Deployment、ReplicaSet、RC或Job |
| autoscale      | 创建一个自动选择扩容或缩容并设置Pod数量            |

### 集群管理命令

| 命令         | 介绍                           |
| ------------ | ------------------------------ |
| certificate  | 修改证书资源                   |
| cluster-info | 显示集群信息                   |
| top          | 显示资源(CPU/M)                |
| cordon       | 标记节点不可调度               |
| uncordon     | 标记节点可被调度               |
| drain        | 驱逐节点上的应用，准备下线维护 |
| taint        | 修改节点taint标记              |
|              |                                |

### 故障和调试命令

| 命令         | 介绍                                                         |
| ------------ | ------------------------------------------------------------ |
| describe     | 显示特定资源或资源组的详细信息                               |
| logs         | 在一个Pod中打印一个容器日志，如果Pod只有一个容器，容器名称是可选的 |
| attach       | 附加到一个运行的容器                                         |
| exec         | 执行命令到容器                                               |
| port-forward | 转发一个或多个                                               |
| proxy        | 运行一个proxy到Kubernetes API Server                         |
| cp           | 拷贝文件或目录到容器中                                       |
| auth         | 检查授权                                                     |

### 其它命令

| 命令         | 介绍                                                |
| ------------ | --------------------------------------------------- |
| apply        | 通过文件名或标准输入对资源应用配置                  |
| patch        | 使用补丁修改、更新资源的字段                        |
| replace      | 通过文件名或标准输入替换一个资源                    |
| convert      | 不同的API版本之间转换配置文件                       |
| label        | 更新资源上的标签                                    |
| annotate     | 更新资源上的注释                                    |
| completion   | 用于实现kubectl工具自动补全                         |
| api-versions | 打印受支持的API版本                                 |
| config       | 修改kubeconfig文件（用于访问API，比如配置认证信息） |
| help         | 所有命令帮助                                        |
| plugin       | 运行一个命令行插件                                  |
| version      | 打印客户端和服务版本信息                            |



如：

```bash
# 创建一个nginx镜像
kubectl create deployment nginx --image=nginx

# 对外暴露端口
kubectl expose deployment nginx --port=80 --type=NodePort

# 查看资源
kubectl get pod, svc
```



##  Kubernetes集群YAML文件详解

###  概述

k8s 集群中对资源管理和资源对象编排部署都可以通过声明样式（YAML）文件来解决，也就是可以把需要对资源对象操作编辑到YAML 格式文件中，我们把这种文件叫做资源清单文件，通过kubectl 命令直接使用资源清单文件就可以实现对大量的资源对象进行编排部署了。一般在我们开发的时候，都是通过配置YAML文件来部署集群的。

YAML文件：就是资源清单文件，用于资源编排



###  YAML文件介绍

#### YAML概述

YAML ：仍是一种标记语言。为了强调这种语言以数据做为中心，而不是以标记语言为重点。

YAML 是一个可读性高，用来表达数据序列的格式。

#### YAML 基本语法

- 使用空格做为缩进
- 缩进的空格数目不重要，只要相同层级的元素左侧对齐即可
- 低版本缩进时不允许使用Tab 键，只允许使用空格
- 使用#标识注释，从这个字符一直到行尾，都会被解释器忽略
- 使用 --- 表示新的yaml文件开始

#### YAML 支持的数据结构

##### 对象

键值对的集合，又称为映射(mapping) / 哈希（hashes） / 字典（dictionary）

```
# 对象类型：对象的一组键值对，使用冒号结构表示
name: Tom
age: 18

# yaml 也允许另一种写法，将所有键值对写成一个行内对象
hash: {name: Tom, age: 18}
```

##### 数组

```
# 数组类型：一组连词线开头的行，构成一个数组
People
- Tom
- Jack

# 数组也可以采用行内表示法
People: [Tom, Jack]
```

### YAML文件组成部分

主要分为了两部分，一个是控制器的定义 和 被控制的对象

#### 控制器的定义

![image-20201114110444032](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/7_Kubernetes%E9%9B%86%E7%BE%A4YAML%E6%96%87%E4%BB%B6%E8%AF%A6%E8%A7%A3/images/image-20201114110444032.png)

#### 被控制的对象

包含一些 镜像，版本、端口等

![image-20201114110600165](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/7_Kubernetes%E9%9B%86%E7%BE%A4YAML%E6%96%87%E4%BB%B6%E8%AF%A6%E8%A7%A3/images/image-20201114110600165.png)

#### 属性说明

在一个YAML文件的控制器定义中，有很多属性名称

| 属性名称   | 介绍       |
| :--------- | ---------- |
| apiVersion | API版本    |
| kind       | 资源类型   |
| metadata   | 资源元数据 |
| spec       | 资源规格   |
| replicas   | 副本数量   |
| selector   | 标签选择器 |
| template   | Pod模板    |
| metadata   | Pod元数据  |
| spec       | Pod规格    |
| containers | 容器配置   |





### 如何快速编写YAML文件

一般来说，我们很少自己手写YAML文件，因为这里面涉及到了很多内容，我们一般都会借助工具来创建

#### 一、 使用kubectl create命令

这种方式一般用于资源没有部署的时候，我们可以直接创建一个YAML配置文件

```bash
# 尝试运行,并不会真正的创建镜像
kubectl create deployment web --image=nginx -o yaml --dry-run
```

或者我们可以输出到一个文件中

```bash
kubectl create deployment web --image=nginx -o yaml --dry-run > hello.yaml
```

然后我们就在文件中直接修改即可

#### 二、使用kubectl get命令导出yaml文件

可以首先查看一个目前已经部署的镜像

```bash
kubectl get deploy
```

![image-20201114113115649](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/7_Kubernetes%E9%9B%86%E7%BE%A4YAML%E6%96%87%E4%BB%B6%E8%AF%A6%E8%A7%A3/images/image-20201114113115649.png)

然后我们导出 nginx的配置

```
kubectl get deploy nginx -o=yaml > nginx.yaml
```

然后会生成一个 `nginx.yaml` 的配置文件

![image-20201114184538797](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/7_Kubernetes%E9%9B%86%E7%BE%A4YAML%E6%96%87%E4%BB%B6%E8%AF%A6%E8%A7%A3/images/image-20201114184538797.png)





##  Kubernetes核心技术Pod

###  Pod概述

Pod是K8S系统中可以创建和管理的最小单元，是资源对象模型中由用户创建或部署的最小资源对象模型，也是在K8S上运行容器化应用的资源对象，其它的资源对象都是用来支撑或者扩展Pod对象功能的，比如控制器对象是用来管控Pod对象的，Service或者Ingress资源对象是用来暴露Pod引用对象的，PersistentVolume资源对象是用来为Pod提供存储等等，K8S不会直接处理容器，而是Pod，Pod是由一个或多个container组成。

Pod是Kubernetes的最重要概念，每一个Pod都有一个特殊的被称为 “根容器”的Pause容器。Pause容器对应的镜像属于Kubernetes平台的一部分，除了Pause容器，每个Pod还包含一个或多个紧密相关的用户业务容器。

![image-20201114185528215](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114185528215.png)

### Pod基本概念

- 最小部署的单元
- Pod里面是由一个或多个容器组成【一组容器的集合】
- 一个pod中的容器是共享网络命名空间
- Pod是短暂的
- 每个Pod包含一个或多个紧密相关的用户业务容器

### Pod存在的意义（进程、亲密性调用）

- 创建容器使用docker，一个docker对应一个容器，一个容器运行一个应用进程
- Pod是多进程设计，运用多个应用程序，**也就是一个Pod里面有多个容器，而一个容器里面运行一个应用程序**

![image-20201114190018948](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114190018948.png)

- Pod的存在是为了亲密性应用
  - 两个应用之间进行交互
  - 网络之间的调用【通过127.0.0.1 或 socket】
  - 两个应用之间需要频繁调用



Pod是在K8S集群中运行部署应用或服务的最小单元，它是可以支持多容器的。Pod的设计理念是支持多个容器在一个Pod中共享网络地址和文件系统，可以通过进程间通信和文件共享这种简单高效的方式组合完成服务。同时Pod对多容器的支持是K8S中最基础的设计理念。在生产环境中，通常是由不同的团队各自开发构建自己的容器镜像，在部署的时候组合成一个微服务对外提供服务。

Pod是K8S集群中所有业务类型的基础，可以把Pod看作运行在K8S集群上的小机器人，不同类型的业务就需要不同类型的小机器人去执行。目前K8S的业务主要可以分为以下几种

- 长期伺服型：long-running
- 批处理型：batch
- 节点后台支撑型：node-daemon
- 有状态应用型：stateful application

上述的几种类型，分别对应的小机器人控制器为：Deployment、Job、DaemonSet 和 StatefulSet (后面将介绍控制器)

### Pod实现机制

主要有以下两大机制

- 共享网络
- 共享存储

#### 共享网络

容器本身之间相互隔离的，一般是通过 **namespace** 和 **group** 进行隔离，那么Pod里面的容器如何实现通信？

- 首先需要满足前提条件，也就是容器都在同一个**namespace**之间

关于Pod实现原理，首先会在Pod会创建一个根容器： `pause容器`，然后我们在创建业务容器 【nginx，redis 等】，在我们创建业务容器的时候，会把它添加到 `info容器` 中

而在 `info容器` 中会独立出 ip地址，mac地址，port 等信息，然后实现网络的共享

![image-20201114190913859](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114190913859.png)

完整步骤如下

- 通过 Pause 容器，把其它业务容器加入到Pause容器里，让所有业务容器在同一个名称空间中，可以实现网络共享

#### 共享存储

Pod持久化数据，将数据专门存储到某个地方中

![image-20201114193124160](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114193124160.png)

使用 Volumn数据卷进行共享存储，案例如下所示

![image-20201114193341993](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114193341993.png)



#### Pod镜像拉取策略

我们以具体实例来说，拉取策略就是 `imagePullPolicy`

![image-20201114193605230](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114193605230.png)

拉取策略主要分为了以下几种

- IfNotPresent：默认值，镜像在宿主机上不存在才拉取
- Always：每次创建Pod都会重新拉取一次镜像
- Never：Pod永远不会主动拉取这个镜像



#### Pod资源限制

也就是我们Pod在进行调度的时候，可以对调度的资源进行限制，例如我们限制 Pod调度是使用的资源是 2C4G，那么在调度对应的node节点时，只会占用对应的资源，对于不满足资源的节点，将不会进行调度

![image-20201114194057920](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114194057920.png)

我们在下面的地方进行资源的限制，示例

![image-20201114194245517](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114194245517.png)

这里分了两个部分

- request：表示调度所需的资源
- limits：表示最大所占用的资源



#### Pod重启机制

因为Pod中包含了很多个容器，假设某个容器出现问题了，那么就会触发Pod重启机制

![image-20201114194722125](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114194722125.png)

重启策略主要分为以下三种

- Always：当容器终止退出后，总是重启容器，默认策略 【nginx等，需要不断提供服务】
- OnFailure：当容器异常退出（退出状态码非0）时，才重启容器。
- Never：当容器终止退出，从不重启容器 【批量任务】



#### Pod健康检查

通过容器检查，原来我们使用下面的命令来检查

```
kubectl get pod
```

但是有的时候，程序可能出现了 **Java** 堆内存溢出，程序还在运行，但是不能对外提供服务了，这个时候就不能通过 容器检查来判断服务是否可用了

这个时候就可以使用应用层面的检查

```bash
# 存活检查，如果检查失败，将杀死容器，根据Pod的restartPolicy【重启策略】来操作
livenessProbe

# 就绪检查，如果检查失败，Kubernetes会把Pod从Service endpoints中剔除
readinessProbe
```

![image-20201114195807564](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114195807564.png)

Probe支持以下三种检查方式

- http Get：发送HTTP请求，返回200 - 400 范围状态码为成功
- exec：执行Shell命令返回状态码是0为成功
- tcpSocket：发起TCP Socket建立成功

### Pod调度策略

#### 创建Pod流程

- 首先创建一个pod，然后创建一个API Server 和 Etcd【把创建出来的信息存储在etcd中】
- 然后创建 Scheduler，监控API Server是否有新的Pod，如果有的话，会通过调度算法，把pod调度某个node上
- 在node节点，会通过 `kubelet -- apiserver `读取etcd 拿到分配在当前node节点上的pod，然后通过docker创建容器

![image-20201114201611308](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114201611308.png)



#### 影响Pod调度的属性

Pod资源限制对Pod的调度会有影响

#### 根据request找到足够node节点进行调度

![image-20201114194245517](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114194245517.png)

#### 节点选择器标签影响Pod调度

![image-20201114202456151](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114202456151.png)

关于节点选择器，其实就是有两个环境，然后环境之间所用的资源配置不同进行选择

![image-20201114202643905](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114202643905.png)

我们可以通过以下命令（master中），给我们的节点新增标签，然后节点选择器就会进行调度了

```
kubectl label node node1 env_role=prod
```



#### 节点亲和性

节点亲和性 **nodeAffinity** 和 之前nodeSelector 基本一样的，根据节点上标签约束来决定Pod调度到哪些节点上

- 硬亲和性：约束条件必须满足
- 软亲和性：尝试满足，不保证

![image-20201114203433939](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114203433939.png)

支持常用操作符：in(在...中)、NotIn(不在...中)、Exists(存在)、DoesNotExists(不存在)、Gt(大于)、Lt(小于)、

反亲和性：就是和亲和性刚刚相反，如 NotIn、DoesNotExists等

### 污点和污点容忍

#### 概述

nodeSelector 和 NodeAffinity，都是Prod调度到某些节点上，属于Pod的属性，是在调度的时候实现的。

Taint 污点：节点不做普通分配调度，是节点属性

#### 场景

- 专用节点【限制ip】
- 配置特定硬件的节点【固态硬盘】
- 基于Taint驱逐【在node1不放，在node2放】



查看污点情况

```
kubectl describe node k8smaster | grep Taint
```

![image-20201114204124819](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114204124819.png)

污点值有三个

- NoSchedule：一定不被调度
- PreferNoSchedule：尽量不被调度【也有被调度的几率】
- NoExecute：不会调度，并且还会驱逐Node已有Pod

如master节点一定不会被选中



#### 为节点添加污点

```
kubectl taint node [node] key=value:污点的三个值
```

举例：

```
kubectl taint node k8snode1 env_role=yes:NoSchedule
```

#### 删除污点

```
kubectl taint node k8snode1 env_role:NoSchedule-
```

![image-20201114210022883](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114210022883.png)

#### 演示

我们现在创建多个Pod，查看最后分配到Node上的情况

首先我们创建一个 nginx 的pod

```
kubectl create deployment web --image=nginx
```

然后使用命令查看

```
kubectl get pods -o wide
```

![image-20201114204917548](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114204917548.png)

我们可以非常明显的看到，这个Pod已经被分配到 k8snode1 节点上了

下面我们把pod复制5份，在查看情况pod情况

```
kubectl scale deployment web --replicas=5
```

我们可以发现，因为master节点存在污点的情况，所以节点都被分配到了 node1 和 node2节点上

![image-20201114205135282](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114205135282.png)

我们可以使用下面命令，把刚刚我们创建的pod都删除

```
kubectl delete deployment web
```

现在给了更好的演示污点的用法，我们现在给 node1节点打上污点

```
kubectl taint node k8snode1 env_role=yes:NoSchedule
```

然后我们查看污点是否成功添加

```
kubectl describe node k8snode1 | grep Taint
```

![image-20201114205516154](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114205516154.png)

然后我们在创建一个 pod

```
# 创建nginx pod
kubectl create deployment web --image=nginx
# 复制五次
kubectl scale deployment web --replicas=5
```

然后我们在进行查看

```
kubectl get pods -o wide
```

我们能够看到现在所有的pod都被分配到了 k8snode2上，因为刚刚我们给node1节点设置了污点

![image-20201114205654867](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114205654867.png)

最后我们可以删除刚刚添加的污点

```
kubectl taint node k8snode1 env_role:NoSchedule-
```

#### 污点容忍

污点容忍就是某个节点可能被调度，也可能不被调度

![image-20201114210146123](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/8_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFPod/images/image-20201114210146123.png)







## Kubernetes核心技术-Controller

内容

- 什么是Controller
- Pod和Controller的关系
- Deployment控制器应用场景
- yaml文件字段说明
- Deployment控制器部署应用
- 升级回滚
- 弹性伸缩

### 什么是Controller

Controller是在集群上管理和运行容器的对象，Controller是实际存在的，Pod是虚拟机的

### Pod和Controller的关系

Pod是通过Controller实现应用的运维，比如弹性伸缩，滚动升级等

Pod 和 Controller之间是通过label标签来建立关系，同时Controller又被称为控制器工作负载

![image-20201116092431237](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201116092431237.png)

### Deployment控制器应用

- Deployment控制器可以部署无状态应用
- 管理Pod和ReplicaSet
- 部署，滚动升级等功能
- **应用场景：web服务，微服务**

Deployment表示用户对K8S集群的一次更新操作。Deployment是一个比RS( Replica Set, RS) 应用模型更广的 API 对象，可以是创建一个新的服务，更新一个新的服务，也可以是滚动升级一个服务。滚动升级一个服务，实际是创建一个新的RS，然后逐渐将新 RS 中副本数增加到理想状态，将旧RS中的副本数减少到0的复合操作。

这样一个复合操作用一个RS是不好描述的，所以用一个更通用的Deployment来描述。以K8S的发展方向，未来对所有长期伺服型的业务的管理，都会通过Deployment来管理。

### Deployment部署应用

之前我们也使用Deployment部署过应用，如下代码所示

```
kubectrl create deployment web --image=nginx
```

但是上述代码不是很好的进行复用，因为每次我们都需要重新输入代码，所以我们都是通过YAML进行配置

但是我们可以尝试使用上面的代码创建一个镜像【只是尝试，不会创建】

```
kubectl create deployment web --image=nginx --dry-run -o yaml > nginx.yaml
```

然后输出一个yaml配置文件 `nginx.yml` ，配置文件如下所示

```
apiVersion: apps/v1
kind: Deployment
metadata:
  creationTimestamp: null
  labels:
    app: web
  name: web
spec:
  replicas: 1
  selector:
    matchLabels:
      app: web
  strategy: {}
  template:
    metadata:
      creationTimestamp: null
      labels:
        app: web
    spec:
      containers:
      - image: nginx
        name: nginx
        resources: {}
status: {}
```

我们看到的 selector 和 label 就是我们Pod 和 Controller之间建立关系的桥梁

![image-20201116093638951](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201116093638951.png)

#### 使用YAML创建Pod

通过刚刚的代码，我们已经生成了YAML文件，下面我们就可以使用该配置文件快速创建Pod镜像了

```
kubectl apply -f nginx.yaml
```

![image-20201116094046007](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201116094046007.png)

但是因为这个方式创建的，我们只能在集群内部进行访问，所以我们还需要对外暴露端口

```
kubectl expose deployment web --port=80 --type=NodePort --target-port=80 --name=web1
```

关于上述命令，有几个参数

- --port：就是我们内部的端口号
- --target-port：就是暴露外面访问的端口号
- --name：名称
- --type：类型

同理，我们一样可以导出对应的配置文件

```
kubectl expose deployment web --port=80 --type=NodePort --target-port=80 --name=web1 -o yaml > web1.yaml
```

得到的web1.yaml如下所示

```
apiVersion: v1
kind: Service
metadata:
  creationTimestamp: "2020-11-16T02:26:53Z"
  labels:
    app: web
  managedFields:
  - apiVersion: v1
    fieldsType: FieldsV1
    fieldsV1:
      f:metadata:
        f:labels:
          .: {}
          f:app: {}
      f:spec:
        f:externalTrafficPolicy: {}
        f:ports:
          .: {}
          k:{"port":80,"protocol":"TCP"}:
            .: {}
            f:port: {}
            f:protocol: {}
            f:targetPort: {}
        f:selector:
          .: {}
          f:app: {}
        f:sessionAffinity: {}
        f:type: {}
    manager: kubectl
    operation: Update
    time: "2020-11-16T02:26:53Z"
  name: web2
  namespace: default
  resourceVersion: "113693"
  selfLink: /api/v1/namespaces/default/services/web2
  uid: d570437d-a6b4-4456-8dfb-950f09534516
spec:
  clusterIP: 10.104.174.145
  externalTrafficPolicy: Cluster
  ports:
  - nodePort: 32639
    port: 80
    protocol: TCP
    targetPort: 80
  selector:
    app: web
  sessionAffinity: None
  type: NodePort
status:
  loadBalancer: {}
```

发布

```yaml
kubectl apply -f web1.yml
```

然后我们可以通过下面的命令来查看对外暴露的服务

```
kubectl get pods,svc
```

![image-20201116104021357](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201116104021357.png)

然后我们访问对应的url，即可看到 nginx了 `http://192.168.177.130:32639/`

![image-20201116104131968](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201116104131968.png)



### 升级回滚和弹性伸缩

- 升级： 假设从版本为1.14 升级到 1.15 ，这就叫应用的升级【升级可以保证服务不中断】
- 回滚：从版本1.15 变成 1.14，这就叫应用的回滚
- 弹性伸缩：我们根据不同的业务场景，来改变Pod的数量对外提供服务，这就是弹性伸缩

#### 应用升级和回滚

首先我们先创建一个 1.14版本的Pod

```
apiVersion: apps/v1
kind: Deployment
metadata:
  creationTimestamp: null
  labels:
    app: web
  name: web
spec:
  replicas: 1
  selector:
    matchLabels:
      app: web
  strategy: {}
  template:
    metadata:
      creationTimestamp: null
      labels:
        app: web
    spec:
      containers:
      - image: nginx:1.14
        name: nginx
        resources: {}
status: {}
```

我们先指定版本为1.14，然后开始创建我们的Pod

```
kubectl apply -f nginx.yaml
```

同时，我们使用docker images命令，就能看到我们成功拉取到了一个 1.14版本的镜像

![image-20201116105710966](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201116105710966.png)

##### 升级

我们使用下面的命令，可以将nginx从 1.14 升级到 1.15

```
kubectl set image deployment web nginx=nginx:1.15
```

在我们执行完命令后，能看到升级的过程

![image-20201116105847069](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201116105847069.png)

- 首先是开始的nginx 1.14版本的Pod在运行，然后 1.15版本的在创建
- 然后在1.15版本创建完成后，就会暂停1.14版本
- 最后把1.14版本的Pod移除，完成我们的升级

我们在下载 1.15版本，容器就处于ContainerCreating状态，然后下载完成后，就用 1.15版本去替换1.14版本了，这么做的好处就是：升级可以保证服务不中断

![image-20201116111614085](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201116111614085.png)

我们到我们的node2节点上，查看我们的 docker images;

![image-20201116111315000](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201116111315000.png)

能够看到，我们已经成功拉取到了 1.15版本的nginx了

##### 查看升级状态

下面可以，查看升级状态

```
kubectl rollout status deployment web
```

![image-20201116112139645](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201116112139645.png)

##### 查看历史版本

我们还可以查看历史版本

```
kubectl rollout history deployment web
```



##### 应用回滚

我们可以使用下面命令，完成回滚操作，也就是回滚到上一个版本

```
kubectl rollout undo deployment web
```

然后我们就可以查看状态

```
kubectl rollout status deployment web
```

![image-20201116112524601](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201116112524601.png)

同时我们还可以回滚到指定版本

```
kubectl rollout undo deployment web --to-revision=2
```



##### 弹性伸缩

弹性伸缩，也就是我们通过命令一下创建多个副本

```
kubectl scale deployment web --replicas=10
```

能够清晰看到，我们一下创建了10个副本

![image-20201117092841865](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/9_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFController/images/image-20201117092841865.png)





## Kubernetes核心技术Service

### 前言

前面我们了解到 Deployment 只是保证了支撑服务的微服务Pod的数量，但是没有解决如何访问这些服务的问题。一个Pod只是一个运行服务的实例，随时可能在一个节点上停止，在另一个节点以一个新的IP启动一个新的Pod，因此不能以确定的IP和端口号提供服务。

要稳定地提供服务需要服务发现和负载均衡能力。服务发现完成的工作，是针对客户端访问的服务，找到对应的后端服务实例。在K8S集群中，客户端需要访问的服务就是Service对象。每个Service会对应一个集群内部有效的虚拟IP，集群内部通过虚拟IP访问一个服务。

在K8S集群中，微服务的负载均衡是由kube-proxy实现的。kube-proxy是k8s集群内部的负载均衡器。它是一个分布式代理服务器，在K8S的每个节点上都有一个；这一设计体现了它的伸缩性优势，需要访问服务的节点越多，提供负载均衡能力的kube-proxy就越多，高可用节点也随之增多。与之相比，我们平时在服务器端使用反向代理作负载均衡，还要进一步解决反向代理的高可用问题。

### Service存在的意义

#### 防止Pod失联【服务发现】

因为Pod每次创建都对应一个IP地址，而这个IP地址是短暂的，每次随着Pod的更新都会变化，假设当我们的前端页面有多个Pod时候，同时后端也多个Pod，这个时候，他们之间的相互访问，就需要通过注册中心，拿到Pod的IP地址，然后去访问对应的Pod

![image-20201117093606710](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/10_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFService/images/image-20201117093606710.png)

#### 定义Pod访问策略【负载均衡】

页面前端的Pod访问到后端的Pod，中间会通过Service一层，而Service在这里还能做负载均衡，负载均衡的策略有很多种实现策略，例如：

- 随机
- 轮询
- 响应比

![image-20201117093902459](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/10_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFService/images/image-20201117093902459.png)



### Pod和Service的关系

这里Pod 和 Service 之间还是根据 label 和 selector 建立关联的 【和Controller一样】

![image-20201117094142491](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/10_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFService/images/image-20201117094142491.png)

我们在访问service的时候，其实也是需要有一个ip地址，这个ip肯定不是pod的ip地址，而是 虚拟IP `vip`

### Service常用类型

Service常用类型有三种

- ClusterIp：集群内部访问(默认)
- NodePort：对外访问应用使用
- LoadBalancer：对外访问应用使用，公有云

#### 举例

我们可以导出一个文件 包含service的配置信息

```
kubectl expose deployment web --port=80 --target-port=80 --dry-run -o yaml > service.yaml
```

service.yaml 如下所示

```
apiVersion: v1
kind: Service
metadata:
  creationTimestamp: null
  labels:
    app: web
  name: web
spec:
  ports:
  - port: 80
    protocol: TCP
    targetPort: 80
  selector:
    app: web
status:
  loadBalancer: {}
```

如果我们没有做设置的话，默认使用的是第一种方式 ClusterIp，也就是只能在集群内部使用，我们可以添加一个type字段，用来设置我们的service类型

```
apiVersion: v1
kind: Service
metadata:
  creationTimestamp: null
  labels:
    app: web
  name: web
spec:
  ports:
  - port: 80
    protocol: TCP
    targetPort: 80
  selector:
    app: web
  type: NodePort
status:
  loadBalancer: {}
```

修改完命令后，我们使用创建一个pod

```
kubectl apply -f service.yaml
```

然后能够看到，已经成功修改为 NodePort类型了

最后剩下的一种方式就是LoadBalanced：对外访问应用使用公有云，node一般是在内网进行部署，而外网一般是不能访问到的，那么如何访问的呢？

- 找到一台可以通过外网访问机器，安装nginx，反向代理
- 手动把可以访问的节点添加到nginx中

如果我们使用LoadBalancer，就会有负载均衡的控制器，类似于nginx的功能，就不需要自己添加到nginx上



## Kubernetes控制器Controller详解

### Statefulset

Statefulset主要是用来部署有状态应用

对于StatefulSet中的Pod，每个Pod挂载自己独立的存储，如果一个Pod出现故障，从其他节点启动一个同样名字的Pod，要挂载上原来Pod的存储继续以它的状态提供服务。

#### 无状态应用

我们原来使用 deployment，部署的都是无状态的应用，那什么是无状态应用？

- 认为Pod都是一样的
- 没有顺序要求
- 不考虑应用在哪个node上运行
- 能够进行随意伸缩和扩展

#### 有状态应用

上述的因素都需要考虑到

- 让每个Pod独立的
- 让每个Pod独立的，保持Pod启动顺序和唯一性
- 唯一的网络标识符，持久存储
- 有序，比如mysql中的主从

适合StatefulSet的业务包括数据库服务MySQL 和 PostgreSQL，集群化管理服务Zookeeper、etcd等有状态服务

StatefulSet的另一种典型应用场景是作为一种比普通容器更稳定可靠的模拟虚拟机的机制。传统的虚拟机正是一种有状态的宠物，运维人员需要不断地维护它，容器刚开始流行时，我们用容器来模拟虚拟机使用，所有状态都保存在容器里，而这已被证明是非常不安全、不可靠的。

使用StatefulSet，Pod仍然可以通过漂移到不同节点提供高可用，而存储也可以通过外挂的存储来提供 高可靠性，StatefulSet做的只是将确定的Pod与确定的存储关联起来保证状态的连续性。

#### 部署有状态应用

有状态应用需包含无头service， ClusterIp：none

这里就需要使用 StatefulSet部署有状态应用

![image-20201117202950336](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117202950336.png)

![image-20201117203130867](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117203130867.png)

然后通过查看pod，能否发现每个pod都有唯一的名称

![image-20201117203217016](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117203217016.png)

然后我们在查看service，发现是无头的service

![image-20201117203245641](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117203245641.png)

这里有状态的约定，肯定不是简简单单通过名称来进行约定，而是更加复杂的操作

- deployment：是有身份的，有唯一标识
- statefulset：根据主机名 + 按照一定规则生成域名

每个pod有唯一的主机名，并且有唯一的域名

- 格式：主机名称.service名称.名称空间.svc.cluster.local
- 举例：nginx-statefulset-0.default.svc.cluster.local

### DaemonSet

DaemonSet 即后台支撑型服务，主要是用来部署守护进程

长期伺服型和批处理型的核心在业务应用，可能有些节点运行多个同类业务的Pod，有些节点上又没有这类的Pod运行；而后台支撑型服务的核心关注点在K8S集群中的节点(物理机或虚拟机)，要保证每个节点上都有一个此类Pod运行。节点可能是所有集群节点，也可能是通过 nodeSelector选定的一些特定节点。典型的后台支撑型服务包括：存储、日志和监控等。在每个节点上支撑K8S集群运行的服务。

守护进程在我们每个节点上，运行的是同一个pod，新加入的节点也同样运行在同一个pod里面

- 例子：在每个node节点安装数据采集工具

![image-20201117204430836](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117204430836.png)

这里是不是一个FileBeat镜像，主要是为了做日志采集工作

![image-20201117204810350](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117204810350.png)

进入某个 Pod里面，进入

```
kubectl exec -it ds-test-cbk6v bash
```

通过该命令后，我们就能看到我们内部收集的日志信息了

![image-20201117204912838](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117204912838.png)

### Job和CronJob

一次性任务 和 定时任务

- 一次性任务：一次性执行完就结束
- 定时任务：周期性执行

Job是K8S中用来控制批处理型任务的API对象。批处理业务与长期伺服业务的主要区别就是批处理业务的运行有头有尾，而长期伺服业务在用户不停止的情况下永远运行。Job管理的Pod根据用户的设置把任务成功完成就自动退出了。成功完成的标志根据不同的 spec.completions 策略而不同：单Pod型任务有一个Pod成功就标志完成；定数成功行任务保证有N个任务全部成功；工作队列性任务根据应用确定的全局成功而标志成功。

#### Job

Job也即一次性任务

![image-20201117205635945](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117205635945.png)

使用下面命令，能够看到目前已经存在的Job

```
kubectl get jobs
```

![image-20201117205948374](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117205948374.png)

在计算完成后，通过命令查看，能够发现该任务已经完成

![image-20201117210031725](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117210031725.png)

我们可以通过查看日志，查看到一次性任务的结果

```
kubectl logs pi-qpqff
```

![image-20201117210110343](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117210110343.png)

#### CronJob

定时任务，cronjob.yaml如下所示

![image-20201117210309069](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117210309069.png)

这里面的命令就是每个一段时间，这里是通过 cron 表达式配置的，通过 schedule字段

然后下面命令就是每个一段时间输出

我们首先用上述的配置文件，创建一个定时任务

```
kubectl apply -f cronjob.yaml
```

创建完成后，我们就可以通过下面命令查看定时任务

```
kubectl get cronjobs
```

![image-20201117210611783](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117210611783.png)

我们可以通过日志进行查看

```
kubectl logs hello-1599100140-wkn79
```

![image-20201117210722556](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117210722556.png)

然后每次执行，就会多出一个 pod

![image-20201117210751068](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/11_Kubernetes%E6%8E%A7%E5%88%B6%E5%99%A8Controller%E8%AF%A6%E8%A7%A3/images/image-20201117210751068.png)

### 删除svc 和 statefulset

使用下面命令，可以删除我们添加的svc 和 statefulset

```
kubectl delete svc web

kubectl delete statefulset --all
```

### Replication Controller

Replication Controller 简称 **RC**，是K8S中的复制控制器。RC是K8S集群中最早的保证Pod高可用的API对象。通过监控运行中的Pod来保证集群中运行指定数目的Pod副本。指定的数目可以是多个也可以是1个；少于指定数目，RC就会启动新的Pod副本；多于指定数目，RC就会杀死多余的Pod副本。

即使在指定数目为1的情况下，通过RC运行Pod也比直接运行Pod更明智，因为RC也可以发挥它高可用的能力，保证永远有一个Pod在运行。RC是K8S中较早期的技术概念，只适用于长期伺服型的业务类型，比如控制Pod提供高可用的Web服务。

### Replica Set

Replica Set 检查 RS，也就是副本集。RS是新一代的RC，提供同样高可用能力，区别主要在于RS后来居上，能够支持更多种类的匹配模式。副本集对象一般不单独使用，而是作为Deployment的理想状态参数来使用



## Kubernetes配置管理

### Secret

Secret的主要作用就是加密数据，然后存在etcd里面，让Pod容器以挂载Volume方式进行访问

场景：用户名 和 密码进行加密

一般场景的是对某个字符串进行base64编码 进行加密

```
echo -n 'admin' | base64
```

![image-20201117212037668](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201117212037668.png)

#### 变量形式挂载到Pod

- 创建secret加密数据的yaml文件 secret.yaml

![image-20201117212124476](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201117212124476.png)

然后使用下面命令创建一个pod

```
kubectl create -f secret.yaml
```

通过get命令查看

```
kubectl get secret

kubectl get pods
```

![image-20201118084010980](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118084010980.png)

然后我们通过下面的命令，进入到我们的容器内部

```
kubectl exec -it mypod bash
```

然后我们就可以输出我们的值，这就是以变量的形式挂载到我们的容器中

```
# 输出用户
echo $SECRET_USERNAME
# 输出密码
echo $SECRET_PASSWORD
```

![image-20201118084137942](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118084137942.png)

最后如果我们要删除这个Pod，就可以使用这个命令

```
kubectl delete -f secret-val.yaml
```



#### 数据卷形式挂载

首先我们创建一个 secret-val.yaml 文件(账户和密码依然使用secret.yaml)

![image-20201118084321590](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118084321590.png)

然后创建我们的 Pod

```
# 根据配置创建容器
kubectl apply -f secret-val.yaml
# 进入容器
kubectl exec -it mypod bash
# 查看
ls /etc/foo
```

![image-20201118084707478](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118084707478.png)



### ConfigMap

ConfigMap作用是存储不加密的数据到etcd中，让Pod以变量或数据卷Volume挂载到容器中

应用场景：配置文件

#### 创建配置文件

首先我们需要创建一个配置文件 `redis.properties`

```
redis.port=127.0.0.1
redis.port=6379
redis.password=123456
```

#### 创建ConfigMap

我们使用命令创建configmap

```
kubectl create configmap redis-config --from-file=redis.properties
```

然后查看详细信息

```
kubectl describe cm redis-config
```

![image-20201118085503534](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118085503534.png)

#### Volume数据卷形式挂载

首先我们需要创建一个 `cm.yaml`

![image-20201118085847424](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118085847424.png)

然后使用该yaml创建我们的pod

```
# 创建
kubectl apply -f cm.yaml
# 查看
kubectl get pods
```

![image-20201118090634869](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118090634869.png)

最后我们通过命令就可以查看结果输出了

```
kubectl logs mypod
```

![image-20201118090712780](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118090712780.png)

#### 以变量的形式挂载Pod

首先我们也有一个 myconfig.yaml文件，声明变量信息，然后以configmap创建

![image-20201118090911260](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118090911260.png)

然后我们就可以创建我们的配置文件

```
# 创建pod
kubectl apply -f myconfig.yaml
# 获取
kubectl get cm
```

![image-20201118091042287](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118091042287.png)

然后我们创建完该pod后，我们就需要在创建一个 config-var.yaml 来使用我们的配置信息

![image-20201118091249520](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118091249520.png)

最后我们查看输出

```
kubectl logs mypod
```

![image-20201118091448252](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/12_Kubernetes%E9%85%8D%E7%BD%AE%E7%AE%A1%E7%90%86/images/image-20201118091448252.png)





## Kubernetes集群安全机制

#### 概述

当我们访问K8S集群时，需要经过三个步骤完成具体操作

- 认证
- 鉴权【授权】
- 准入控制

进行访问的时候，都需要经过 apiserver， apiserver做统一协调，比如门卫

- 访问过程中，需要证书、token、或者用户名和密码
- 如果访问pod需要serviceAccount

![image-20201118092356107](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/13_Kubernetes%E9%9B%86%E7%BE%A4%E5%AE%89%E5%85%A8%E6%9C%BA%E5%88%B6/images/image-20201118092356107.png)

#### 认证

对外不暴露8080端口，只能内部访问，对外使用的端口6443

客户端身份认证常用方式

- https证书认证，基于ca证书
- http token认证，通过token来识别用户
- http基本认证，用户名 + 密码认证

#### 鉴权

基于RBAC进行鉴权操作

基于角色访问控制

#### 准入控制

就是准入控制器的列表，如果列表有请求内容就通过，没有的话 就拒绝

### RBAC介绍

基于角色的访问控制，为某个角色设置访问内容，然后用户分配该角色后，就拥有该角色的访问权限

![image-20201118093949893](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/13_Kubernetes%E9%9B%86%E7%BE%A4%E5%AE%89%E5%85%A8%E6%9C%BA%E5%88%B6/images/image-20201118093949893.png)

k8s中有默认的几个角色

- role：特定命名空间访问权限
- ClusterRole：所有命名空间的访问权限

角色绑定

- roleBinding：角色绑定到主体
- ClusterRoleBinding：集群角色绑定到主体

主体

- user：用户
- group：用户组
- serviceAccount：服务账号

### RBAC实现鉴权

- 创建命名空间

#### 创建命名空间

我们可以首先查看已经存在的命名空间

```
kubectl get namespace
```

![image-20201118094516426](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/13_Kubernetes%E9%9B%86%E7%BE%A4%E5%AE%89%E5%85%A8%E6%9C%BA%E5%88%B6/images/image-20201118094516426.png)

然后我们创建一个自己的命名空间 roledemo

```
kubectl create ns roledemo
```

#### 命名空间创建Pod

为什么要创建命名空间？因为如果不创建命名空间的话，默认是在default下

```
kubectl run nginx --image=nginx -n roledemo
```

#### 创建角色

我们通过 rbac-role.yaml进行创建(勘误:namespace的值为roledemo)

![image-20201118094851338](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/13_Kubernetes%E9%9B%86%E7%BE%A4%E5%AE%89%E5%85%A8%E6%9C%BA%E5%88%B6/images/image-20201118094851338.png)

tip：这个角色只对pod 有 get、list权限

然后通过 yaml创建我们的role

```
# 创建
kubectl apply -f rbac-role.yaml
# 查看
kubectl get role -n roledemo
```

![image-20201118095141786](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/13_Kubernetes%E9%9B%86%E7%BE%A4%E5%AE%89%E5%85%A8%E6%9C%BA%E5%88%B6/images/image-20201118095141786.png)

#### 创建角色绑定

我们还是通过 role-rolebinding.yaml 的方式，来创建我们的角色绑定

![image-20201118095248052](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/13_Kubernetes%E9%9B%86%E7%BE%A4%E5%AE%89%E5%85%A8%E6%9C%BA%E5%88%B6/images/image-20201118095248052.png)

然后创建我们的角色绑定

```
# 创建角色绑定
kubectl apply -f rbac-rolebinding.yaml
# 查看角色绑定
kubectl get role, rolebinding -n roledemo
```

![image-20201118095357067](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/13_Kubernetes%E9%9B%86%E7%BE%A4%E5%AE%89%E5%85%A8%E6%9C%BA%E5%88%B6/images/image-20201118095357067.png)

#### 使用证书识别身份

我们首先得有一个 rbac-user.sh 证书脚本

![image-20201118095541427](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/13_Kubernetes%E9%9B%86%E7%BE%A4%E5%AE%89%E5%85%A8%E6%9C%BA%E5%88%B6/images/image-20201118095541427.png)

![image-20201118095627954](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/13_Kubernetes%E9%9B%86%E7%BE%A4%E5%AE%89%E5%85%A8%E6%9C%BA%E5%88%B6/images/image-20201118095627954.png)

这里包含了很多证书文件，在TSL目录下，需要复制过来

通过下面命令执行我们的脚本

```
./rbac-user.sh
```

最后我们进行测试

```
# 用get命令查看 pod 【有权限】
kubectl get pods -n roledemo
# 用get命令查看svc 【没权限】
kubectl get svc -n roledmeo
```

![image-20201118100051043](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/13_Kubernetes%E9%9B%86%E7%BE%A4%E5%AE%89%E5%85%A8%E6%9C%BA%E5%88%B6/images/image-20201118100051043.png)



## Kubernetes核心技术Ingress

### 前言

原来我们需要将端口号对外暴露，通过 ip + 端口号就可以进行访问

原来是使用Service中的NodePort来实现

- 在每个节点上都会启动端口
- 在访问的时候通过任何节点，通过ip + 端口号就能实现访问

但是NodePort还存在一些缺陷

- 因为端口不能重复，所以每个端口只能使用一次，一个端口对应一个应用
- 实际访问中都是用域名，根据不同域名跳转到不同端口服务中

### Ingress和Pod关系

pod 和 ingress 是通过service进行关联的，而ingress作为统一入口，由service关联一组pod中

![image-20201118102637839](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/14_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFIngress/images/image-20201118102637839.png)

- 首先service就是关联我们的pod
- 然后ingress作为入口，首先需要到service，然后发现一组pod
- 发现pod后，就可以做负载均衡等操作

### Ingress工作流程

在实际的访问中，我们都是需要维护很多域名， a.com 和 b.com

然后不同的域名对应的不同的Service，然后service管理不同的pod

![image-20201118102858617](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/14_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFIngress/images/image-20201118102858617.png)

需要注意，ingress不是内置的组件，需要我们单独的安装

### 使用Ingress

步骤如下所示

- 部署ingress Controller【需要下载官方的】
- 创建ingress规则【对哪个Pod、名称空间配置规则】

#### 创建Nginx Pod

创建一个nginx应用，然后对外暴露端口

```
# 创建pod
kubectl create deployment web --image=nginx
# 查看
kubectl get pods
```

对外暴露端口

```
kubectl expose deployment web --port=80 --target-port=80 --type:NodePort
```

#### 部署 ingress controller

下面我们来通过yaml的方式，部署我们的ingress，配置文件如下所示

![image-20201118105427248](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/14_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFIngress/images/image-20201118105427248.png)

这个文件里面，需要注意的是 hostNetwork: true，改成ture是为了让后面访问到

```
kubectl apply -f ingress-con.yaml
```

通过这种方式，其实我们在外面就能访问，这里还需要在外面添加一层

```
kubectl apply -f ingress-con.yaml
```

![image-20201118111256631](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/14_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFIngress/images/image-20201118111256631.png)

最后通过下面命令，查看是否成功部署 ingress

```
kubectl get pods -n ingress-nginx
```

![image-20201118111424735](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/14_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFIngress/images/image-20201118111424735.png)

#### 创建ingress规则文件

创建ingress规则文件，ingress-h.yaml

![image-20201118111700534](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/14_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFIngress/images/image-20201118111700534.png)

#### 添加域名访问规则

在windows 的 hosts文件，添加域名访问规则【因为我们没有域名解析，所以只能这样做】

![image-20201118112029820](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/14_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFIngress/images/image-20201118112029820.png)

最后通过域名就能访问

![image-20201118112212519](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/14_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFIngress/images/image-20201118112212519.png)





## Kubernetes核心技术Helm

Helm就是一个包管理工具【类似于npm】

![img](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/892532-20180224212352306-705544441.png)

### 为什么引入Helm

首先在原来项目中都是基于yaml文件来进行部署发布的，而目前项目大部分微服务化或者模块化，会分成很多个组件来部署，每个组件可能对应一个deployment.yaml,一个service.yaml,一个Ingress.yaml还可能存在各种依赖关系，这样一个项目如果有5个组件，很可能就有15个不同的yaml文件，这些yaml分散存放，如果某天进行项目恢复的话，很难知道部署顺序，依赖关系等，而所有这些包括

- 基于yaml配置的集中存放
- 基于项目的打包
- 组件间的依赖

但是这种方式部署，会有什么问题呢？

- 如果使用之前部署单一应用，少数服务的应用，比较合适
- 但如果部署微服务项目，可能有几十个服务，每个服务都有一套yaml文件，需要维护大量的yaml文件，版本管理特别不方便

Helm的引入，就是为了解决这个问题

- 使用Helm可以把这些YAML文件作为整体管理
- 实现YAML文件高效复用
- 使用helm应用级别的版本管理

### Helm介绍

Helm是一个Kubernetes的包管理工具，就像Linux下的包管理器，如yum/apt等，可以很方便的将之前打包好的yaml文件部署到kubernetes上。

Helm有三个重要概念

- helm：一个命令行客户端工具，主要用于Kubernetes应用chart的创建、打包、发布和管理
- Chart：应用描述，一系列用于描述k8s资源相关文件的集合
- Release：基于Chart的部署实体，一个chart被Helm运行后将会生成对应的release，将在K8S中创建出真实的运行资源对象。也就是应用级别的版本管理
- Repository：用于发布和存储Chart的仓库

### Helm组件及架构

Helm采用客户端/服务端架构，有如下组件组成

- Helm CLI是Helm客户端，可以在本地执行
- Tiller是服务器端组件，在Kubernetes集群上运行，并管理Kubernetes应用程序
- Repository是Chart仓库，Helm客户端通过HTTP协议来访问仓库中Chart索引文件和压缩包

![image-20201119095458328](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201119095458328.png)

### Helm v3变化

2019年11月13日，Helm团队发布了Helm v3的第一个稳定版本

该版本主要变化如下

- 架构变化
  - 最明显的变化是Tiller的删除
  - V3版本删除Tiller
  - relesase可以在不同命名空间重用

V3之前

![image-20201118171523403](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118171523403.png)

V3版本

![image-20201118171956054](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118171956054.png)

### helm配置

首先我们需要去 [官网下载](https://helm.sh/docs/intro/quickstart/)

- 第一步，[下载helm](https://github.com/helm/helm/releases)安装压缩文件，上传到linux系统中(或`wget https://get.helm.sh/helm-v3.1.2-linux-amd64.tar.gz`)
- 第二步，解压helm压缩文件，把解压后的helm目录复制到 usr/bin 目录中
- 使用命令：helm

我们都知道yum需要配置yum源，那么helm就就要配置helm源

### helm仓库

添加仓库

```
helm repo add 仓库名  仓库地址 
```

例如

```
# 配置微软源
helm repo add stable http://mirror.azure.cn/kubernetes/charts
# 配置阿里源
helm repo add aliyun https://kubernetes.oss-cn-hangzhou.aliyuncs.com/charts
# 配置google源
helm repo add google https://kubernetes-charts.storage.googleapis.com/

# 更新
helm repo update
```

然后可以查看我们添加的仓库地址

```
# 查看全部
helm repo list
# 查看某个
helm search repo stable
```

![image-20201118195732281](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118195732281.png)

或者可以删除我们添加的源

```
helm repo remove stable
```

### helm基本命令

- chart install
- chart upgrade
- chart rollback

### 使用helm快速部署应用

#### 使用命令搜索应用

首先我们使用命令，搜索我们需要安装的应用

```
# 搜索 weave仓库
helm search repo weave
```

![image-20201118200603643](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118200603643.png)

#### 根据搜索内容选择安装

搜索完成后，使用命令进行安装

```
helm install ui aliyun/weave-scope
```

可以通过下面命令，来下载yaml文件【如果】

```
kubectl apply -f weave-scope.yaml
```

安装完成后，通过下面命令即可查看

```
helm list
```

![image-20201118203727585](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118203727585.png)

同时可以通过下面命令，查看更新具体的信息

```
helm status ui
```

但是我们通过查看 svc状态，发现没有对象暴露端口

![image-20201118205031343](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118205031343.png)

所以我们需要修改service的yaml文件，添加NodePort

```
kubectl edit svc ui-weave-scope
```

![image-20201118205129431](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118205129431.png)

这样就可以对外暴露端口了

![image-20201118205147631](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118205147631.png)

然后我们通过 ip + 32185 即可访问

#### 如果自己创建Chart

使用命令，自己创建Chart

```
helm create mychart
```

创建完成后，我们就能看到在当前文件夹下，创建了一个 mychart目录

![image-20201118210755621](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118210755621.png)

##### 目录格式

- templates：编写yaml文件存放到这个目录
- values.yaml：存放的是全局的yaml文件
- chart.yaml：当前chart属性配置信息

#### 在templates文件夹创建两个文件

我们创建以下两个

- deployment.yaml
- service.yaml

我们可以通过下面命令创建出yaml文件

```
# 导出deployment.yamlkubectl create deployment web1 --image=nginx --dry-run -o yaml > deployment.yaml# 导出service.yaml 【可能需要创建 deployment，不然会报错】kubectl expose deployment web1 --port=80 --target-port=80 --type=NodePort --dry-run -o yaml > service.yaml
```

#### 安装mychart

执行命令创建

```
helm install web1 mychart
```

![image-20201118213120916](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118213120916.png)

#### 应用升级

当我们修改了mychart中的东西后，就可以进行升级操作

```
helm upgrade web1 mychart
```

### chart模板使用

通过传递参数，动态渲染模板，yaml内容动态从传入参数生成

![image-20201118213630083](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118213630083.png)

刚刚我们创建mychart的时候，看到有values.yaml文件，这个文件就是一些全局的变量，然后在templates中能取到变量的值，下面我们可以利用这个，来完成动态模板

- 在values.yaml定义变量和值
- 具体yaml文件，获取定义变量值
- yaml文件中大题有几个地方不同
  - image
  - tag
  - label
  - port
  - replicas

#### 定义变量和值

在values.yaml定义变量和值

![image-20201118214050899](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118214050899.png)

#### 获取变量和值

我们通过表达式形式 使用全局变量 `{{.Values.变量名称}}`

例如： `{{.Release.Name}}`

![image-20201118214413203](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118214413203.png)

#### 安装应用

在我们修改完上述的信息后，就可以尝试的创建应用了

```
helm install --dry-run web2 mychart
```

![image-20201118214727058](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/15_Kubernetes%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AFHelm/images/image-20201118214727058.png)







## Kubernetes持久化存储

### 前言

之前我们有提到数据卷：`emptydir` ，是本地存储，pod重启，数据就不存在了，需要对数据持久化存储

对于数据持久化存储【pod重启，数据还存在】，有两种方式

- nfs：网络存储【通过一台服务器来存储】

### 步骤

#### 持久化服务器上操作

- 找一台新的服务器nfs服务端，安装nfs
- 设置挂载路径

使用命令安装nfs

```
yum install -y nfs-utils
```

首先创建存放数据的目录

```
mkdir -p /data/nfs
```

设置挂载路径

```bash
# 打开文件
vim /etc/exports
# 添加如下内容
/data/nfs *(rw,no_root_squash)
```

执行完成后，即部署完我们的持久化服务器

#### Node节点上操作

然后需要在k8s集群node节点上安装nfs，这里需要在 node1 和 node2节点上安装

```
yum install -y nfs-utils
```

执行完成后，会自动帮我们挂载上

#### 启动nfs服务端

下面我们回到nfs服务端，启动我们的nfs服务

```
service nfs-server start
```

![image-20201119082047766](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/16_Kubernetes%E6%8C%81%E4%B9%85%E5%8C%96%E5%AD%98%E5%82%A8/images/image-20201119082047766.png)

#### K8s集群部署应用

最后我们在k8s集群上部署应用，使用nfs持久化存储

```
# 创建一个pv文件
mkdir pv
# 进入
cd pv
```

然后创建一个yaml文件 `nfs-nginx.yaml`

![image-20201119082317625](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/16_Kubernetes%E6%8C%81%E4%B9%85%E5%8C%96%E5%AD%98%E5%82%A8/images/image-20201119082317625.png)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
    name: nginx-dep1
spec:
    replicas: 1
    selector: .
        matchLabels:
            app: nginx
    template:
    metadata:
        labels:
            app: nginx
    spec:
        containers:
        - name: nginx
            image: nginx
            volumeMounts: .
            - name: wwwroot
                mountPath: /usr/share/nginx/html
            ports: .
            - cont ainerPort: 80
    volumes:
        - name: wwwroot
            nfs:
                server: 192.168.42.133
                path: /data/nfs
```

通过这个方式，就挂载到了刚刚我们的nfs数据节点下的 /data/nfs 目录

最后就变成了： /usr/share/nginx/html -> 192.168.42.133/data/nfs 内容是对应的

我们通过这个 yaml文件，创建一个pod

```
kubectl apply -f nfs-nginx.yaml
```

创建完成后，我们也可以查看日志

```
kubectl describe pod nginx-dep1
```

![image-20201119083444454](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/16_Kubernetes%E6%8C%81%E4%B9%85%E5%8C%96%E5%AD%98%E5%82%A8/images/image-20201119083444454.png)

可以看到，我们的pod已经成功创建出来了，同时下图也是出于Running状态

![image-20201119083514247](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/16_Kubernetes%E6%8C%81%E4%B9%85%E5%8C%96%E5%AD%98%E5%82%A8/images/image-20201119083514247.png)

下面我们就可以进行测试了，比如现在nfs服务节点上添加数据，然后在看数据是否存在 pod中

```
# 进入pod中查看
kubectl exec -it nginx-dep1 bash
```

![image-20201119095847548](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/16_Kubernetes%E6%8C%81%E4%B9%85%E5%8C%96%E5%AD%98%E5%82%A8/images/image-20201119095847548.png)

### PV和PVC

1

![image-20201119082317625](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/16_Kubernetes%E6%8C%81%E4%B9%85%E5%8C%96%E5%AD%98%E5%82%A8/images/image-20201119082317625.png)

所以这里就需要用到 pv 和 pvc的概念了，方便我们配置和管理我们的 ip 地址等元信息

PV：持久化存储，对存储的资源进行抽象，对外提供可以调用的地方【生产者】

PVC：用于调用，不需要关心内部实现细节【消费者】

PV 和 PVC 使得 K8S 集群具备了存储的逻辑抽象能力。使得在配置Pod的逻辑里可以忽略对实际后台存储 技术的配置，而把这项配置的工作交给PV的配置者，即集群的管理者。存储的PV和PVC的这种关系，跟计算的Node和Pod的关系是非常类似的；PV和Node是资源的提供者，根据集群的基础设施变化而变 化，由K8s集群管理员配置；而PVC和Pod是资源的使用者，根据业务服务的需求变化而变化，由K8s集 群的使用者即服务的管理员来配置。

#### 实现流程

- PVC绑定PV
- 定义PVC
- 定义PV【数据卷定义，指定数据存储服务器的ip、路径、容量和匹配模式】

#### 举例

创建一个 pvc.yaml

![image-20201119101753419](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/16_Kubernetes%E6%8C%81%E4%B9%85%E5%8C%96%E5%AD%98%E5%82%A8/images/image-20201119101753419.png)

第一部分是定义一个 deployment，做一个部署

- 副本数：3
- 挂载路径
- 调用：是通过pvc的模式

然后定义pvc

![image-20201119101843498](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/16_Kubernetes%E6%8C%81%E4%B9%85%E5%8C%96%E5%AD%98%E5%82%A8/images/image-20201119101843498.png)

```
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: my-pvc
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 5Gi
```

然后在创建一个 `pv.yaml`

![image-20201119101957777](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/16_Kubernetes%E6%8C%81%E4%B9%85%E5%8C%96%E5%AD%98%E5%82%A8/images/image-20201119101957777.png)

```bash
apiVersion: v1
kind: PersistentVolume
metadata:
  name: my-pv
spec:
  capacity:
    storage: 5Gi
  accessModes:
    - ReadWriteMany
  nfs:
    path: /data/nfs
    server: 192.168.43.133
```

然后就可以创建pod了

```
kubectl apply -f pv.yaml
```

然后我们就可以通过下面命令，查看我们的 pv 和 pvc之间的绑定关系

```
kubectl get pv, pvc
```

![image-20201119102332786](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/16_Kubernetes%E6%8C%81%E4%B9%85%E5%8C%96%E5%AD%98%E5%82%A8/images/image-20201119102332786.png)

到这里为止，我们就完成了我们 pv 和 pvc的绑定操作，通过之前的方式，进入pod中查看内容

```
kubect exec -it nginx-dep1 bash
```

然后查看 /usr/share/nginx.html

![image-20201119102448226](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/16_Kubernetes%E6%8C%81%E4%B9%85%E5%8C%96%E5%AD%98%E5%82%A8/images/image-20201119102448226.png)

也同样能看到刚刚的内容，其实这种操作和之前我们的nfs是一样的，只是多了一层pvc绑定pv的操作





## Kubernetes集群资源监控

### 概述

#### 监控指标

一个好的系统，主要监控以下内容

- 集群监控
  - 节点资源利用率
  - 节点数
  - 运行Pods
- Pod监控
  - 容器指标
  - 应用程序【程序占用多少CPU、内存】

#### 监控平台

使用普罗米修斯【prometheus】 + Grafana 搭建监控平台

- prometheus【定时搜索被监控服务的状态】
  - 开源的
  - 监控、报警、数据库
  - 以HTTP协议周期性抓取被监控组件状态
  - 不需要复杂的集成过程，使用http接口接入即可
- Grafana
  - 开源的数据分析和可视化工具
  - 支持多种数据源

![image-20201120082257441](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201120082257441.png)

### 部署prometheus

首先需要部署一个守护进程

![image-20201120083606298](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201120083606298.png)

```
---
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: node-exporter
  namespace: kube-system
  labels:
    k8s-app: node-exporter
spec:
  selector:
    matchLabels:
      k8s-app: node-exporter
  template:
    metadata:
      labels:
        k8s-app: node-exporter
    spec:
      containers:
      - image: prom/node-exporter
        name: node-exporter
        ports:
        - containerPort: 9100
          protocol: TCP
          name: http
---
apiVersion: v1
kind: Service
metadata:
  labels:
    k8s-app: node-exporter
  name: node-exporter
  namespace: kube-system
spec:
  ports:
  - name: http
    port: 9100
    nodePort: 31672
    protocol: TCP
  type: NodePort
  selector:
    k8s-app: node-exporter
```

然后执行下面命令

```
kubectl create -f node-exporter.yaml
```

执行完，发现会报错

![image-20201120084034160](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201120084034160.png)

这是因为版本不一致的问题，因为发布的正式版本，而这个属于测试版本

所以我们找到第一行，然后把内容修改为如下所示

```
# 修改前
apiVersion: extensions/v1beta1
# 修改后 【正式版本发布后，测试版本不能使用】
apiVersion: apps/v1
```

创建完成后的效果

![image-20201120085721454](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201120085721454.png)

然后通过yaml的方式部署prometheus

![image-20201120083107594](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201120083107594.png)

- configmap：定义一个configmap：存储一些配置文件【不加密】
- prometheus.deploy.yaml：部署一个deployment【包括端口号，资源限制】
- prometheus.svc.yaml：对外暴露的端口
- rbac-setup.yaml：分配一些角色的权限

下面我们进入目录下，首先部署 rbac-setup.yaml

```
kubectl create -f rbac-setup.yaml
```

![image-20201120090002150](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201120090002150.png)

然后分别部署

```
# 部署configmap
kubectl create -f configmap.yaml
# 部署deployment
kubectl create -f prometheus.deploy.yml
# 部署svc
kubectl create -f prometheus.svc.yml
```

部署完成后，我们使用下面命令查看

```
kubectl get pods -n kube-system
```

![image-20201120093213576](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201120093213576.png)

在我们部署完成后，即可看到 prometheus 的 pod了，然后通过下面命令，能够看到对应的端口

```
kubectl get svc -n kube-system
```

![image-20201121091348752](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201121091348752.png)

通过这个，我们可以看到 `prometheus` 对外暴露的端口为 30003，访问页面即可对应的图形化界面

```
http://192.168.177.130:30003
```

![image-20201121091508851](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201121091508851.png)

在上面我们部署完prometheus后，我们还需要来部署grafana

```
kubectl create -f grafana-deploy.yaml
```

然后执行完后，发现下面的问题

```
error: unable to recognize "grafana-deploy.yaml": no matches for kind "Deployment" in version "extensions/v1beta1"
```

我们需要修改如下内容

```
# 修改
apiVersion: apps/v1

# 添加selector
spec:
  replicas: 1
  selector:
    matchLabels:
      app: grafana
      component: core
```

修改完成后，我们继续执行上述代码

```
# 创建deployment
kubectl create -f grafana-deploy.yaml
# 创建svc
kubectl create -f grafana-svc.yaml
# 创建 ing
kubectl create -f grafana-ing.yaml
```

我们能看到，我们的grafana正在

![image-20201120110426534](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201120110426534.png)

#### 配置数据源

下面我们需要开始打开 Grafana，然后配置数据源，导入数据显示模板

```
kubectl get svc -n kube-system
```

![image-20201120111949197](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201120111949197.png)

我们可以通过 ip + 30431 访问我们的 grafana 图形化页面

![image-20201120112048887](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201120112048887.png)

然后输入账号和密码：admin admin

进入后，我们就需要配置 prometheus 的数据源

![image-20201121092012018](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201121092012018.png)

和 对应的IP【这里IP是我们的ClusterIP】

![image-20201121092053215](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201121092053215.png)

#### 设置显示数据的模板

选择Dashboard，导入我们的模板

![image-20201121092312118](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201121092312118.png)

然后输入 315 号模板

![image-20201121092418180](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201121092418180.png)

然后选择 prometheus数据源 mydb，导入即可

![image-20201121092443266](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201121092443266.png)

导入后的效果如下所示

![image-20201121092610154](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/17_Kubernetes%E9%9B%86%E7%BE%A4%E8%B5%84%E6%BA%90%E7%9B%91%E6%8E%A7/images/image-20201121092610154.png)





## Kubernetes搭建高可用集群

### 前言

之前我们搭建的集群，只有一个master节点，当master节点宕机的时候，通过node将无法继续访问，而master主要是管理作用，所以整个集群将无法提供服务

![image-20201121164522945](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/18_Kubernetes%E6%90%AD%E5%BB%BA%E9%AB%98%E5%8F%AF%E7%94%A8%E9%9B%86%E7%BE%A4/images/image-20201121164522945.png)

### 高可用集群

下面我们就需要搭建一个多master节点的高可用集群，不会存在单点故障问题

但是在node 和 master节点之间，需要存在一个 LoadBalancer组件，作用如下：

- 负载
- 检查master节点的状态

![image-20201121164931760](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/18_Kubernetes%E6%90%AD%E5%BB%BA%E9%AB%98%E5%8F%AF%E7%94%A8%E9%9B%86%E7%BE%A4/images/image-20201121164931760.png)

对外有一个统一的VIP：虚拟ip来对外进行访问

### 高可用集群技术细节

高可用集群技术细节如下所示：

![image-20201121165325194](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/18_Kubernetes%E6%90%AD%E5%BB%BA%E9%AB%98%E5%8F%AF%E7%94%A8%E9%9B%86%E7%BE%A4/images/image-20201121165325194.png)

- keepalived：配置虚拟ip，检查节点的状态
- haproxy：负载均衡服务【类似于nginx】
- apiserver：...
- controller：...
- manager：...
- scheduler：...

### 高可用集群步骤

我们采用2个master节点，一个node节点来搭建高可用集群，下面给出了每个节点需要做的事情

![image-20201121170351461](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/18_Kubernetes%E6%90%AD%E5%BB%BA%E9%AB%98%E5%8F%AF%E7%94%A8%E9%9B%86%E7%BE%A4/images/image-20201121170351461.png)

### 初始化操作

我们需要在这三个节点上进行操作

```
# 关闭防火墙
systemctl stop firewalld
systemctl disable firewalld

# 关闭selinux
# 永久关闭
sed -i 's/enforcing/disabled/' /etc/selinux/config  
# 临时关闭
setenforce 0  

# 关闭swap
# 临时
swapoff -a 
# 永久关闭
sed -ri 's/.*swap.*/#&/' /etc/fstab

# 根据规划设置主机名【master1节点上操作】
hostnamectl set-hostname master1
# 根据规划设置主机名【master2节点上操作】
hostnamectl set-hostname master1
# 根据规划设置主机名【node1节点操作】
hostnamectl set-hostname node1

# 在master添加hosts
cat >> /etc/hosts << EOF
192.168.42.143  k8smaster
192.168.42.140 master01.k8s.io master1
192.168.42.141 master02.k8s.io master2
192.168.42.142 node01.k8s.io node1
EOF


# 将桥接的IPv4流量传递到iptables的链【3个节点上都执行】
cat > /etc/sysctl.d/k8s.conf << EOF
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF

# 生效
sysctl --system  

# 时间同步
yum install ntpdate -y
ntpdate time.windows.com
```

### 部署keepAlived

下面我们需要在所有的master节点【master1和master2】上部署keepAlive

#### 安装相关包

```
# 安装相关工具
yum install -y conntrack-tools libseccomp libtool-ltdl
# 安装keepalived
yum install -y keepalived
```

#### 配置master节点

添加master1的配置

```
cat > /etc/keepalived/keepalived.conf <<EOF 
! Configuration File for keepalived

global_defs {
   router_id k8s
}

vrrp_script check_haproxy {
    script "killall -0 haproxy"
    interval 3
    weight -2
    fall 10
    rise 2
}

vrrp_instance VI_1 {
    state MASTER 
    interface ens33 
    virtual_router_id 51
    priority 250
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass ceb1b3ec013d66163d6ab
    }
    virtual_ipaddress {
        192.168.42.143
    }
    track_script {
        check_haproxy
    }

}
EOF
```

添加master2的配置

```
cat > /etc/keepalived/keepalived.conf <<EOF 
! Configuration File for keepalived

global_defs {
   router_id k8s
}

vrrp_script check_haproxy {
    script "killall -0 haproxy"
    interval 3
    weight -2
    fall 10
    rise 2
}

vrrp_instance VI_1 {
    state BACKUP 
    interface ens33 
    virtual_router_id 51
    priority 200
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass ceb1b3ec013d66163d6ab
    }
    virtual_ipaddress {
        192.168.42.143
    }
    track_script {
        check_haproxy
    }

}
EOF
```

#### 启动和检查

在两台master节点都执行

```
# 启动keepalived
systemctl start keepalived.service
# 设置开机启动
systemctl enable keepalived.service
# 查看启动状态
systemctl status keepalived.service
```

启动后查看master的网卡信息

```
ip a s ens33
```

![image-20201121171619497](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/18_Kubernetes%E6%90%AD%E5%BB%BA%E9%AB%98%E5%8F%AF%E7%94%A8%E9%9B%86%E7%BE%A4/images/image-20201121171619497.png)

### 部署haproxy

haproxy主要做负载的作用，将我们的请求分担到不同的node节点上

#### 安装

在两个master节点安装 haproxy

```
# 安装haproxy
yum install -y haproxy
# 启动 haproxy
systemctl start haproxy
# 开启自启
systemctl enable haproxy
```

启动后，我们查看对应的端口是否包含 16443

```
netstat -tunlp | grep haproxy
```

![image-20201121181803128](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/18_Kubernetes%E6%90%AD%E5%BB%BA%E9%AB%98%E5%8F%AF%E7%94%A8%E9%9B%86%E7%BE%A4/images/image-20201121181803128.png)

#### 配置

两台master节点的配置均相同，配置中声明了后端代理的两个master节点服务器，指定了haproxy运行的端口为16443等，因此16443端口为集群的入口

```
cat > /etc/haproxy/haproxy.cfg << EOF
#---------------------------------------------------------------------
# Global settings
#---------------------------------------------------------------------
global
    # to have these messages end up in /var/log/haproxy.log you will
    # need to:
    # 1) configure syslog to accept network log events.  This is done
    #    by adding the '-r' option to the SYSLOGD_OPTIONS in
    #    /etc/sysconfig/syslog
    # 2) configure local2 events to go to the /var/log/haproxy.log
    #   file. A line like the following can be added to
    #   /etc/sysconfig/syslog
    #
    #    local2.*                       /var/log/haproxy.log
    #
    log         127.0.0.1 local2
    
    chroot      /var/lib/haproxy
    pidfile     /var/run/haproxy.pid
    maxconn     4000
    user        haproxy
    group       haproxy
    daemon 
       
    # turn on stats unix socket
    stats socket /var/lib/haproxy/stats
#---------------------------------------------------------------------
# common defaults that all the 'listen' and 'backend' sections will
# use if not designated in their block
#---------------------------------------------------------------------  
defaults
    mode                    http
    log                     global
    option                  httplog
    option                  dontlognull
    option http-server-close
    option forwardfor       except 127.0.0.0/8
    option                  redispatch
    retries                 3
    timeout http-request    10s
    timeout queue           1m
    timeout connect         10s
    timeout client          1m
    timeout server          1m
    timeout http-keep-alive 10s
    timeout check           10s
    maxconn                 3000
#---------------------------------------------------------------------
# kubernetes apiserver frontend which proxys to the backends
#--------------------------------------------------------------------- 
frontend kubernetes-apiserver
    mode                 tcp
    bind                 *:16443
    option               tcplog
    default_backend      kubernetes-apiserver    
#---------------------------------------------------------------------
# round robin balancing between the various backends
#---------------------------------------------------------------------
backend kubernetes-apiserver
    mode        tcp
    balance     roundrobin
    server      master01.k8s.io   192.168.44.155:6443 check
    server      master02.k8s.io   192.168.44.156:6443 check
#---------------------------------------------------------------------
# collection haproxy statistics message
#---------------------------------------------------------------------
listen stats
    bind                 *:1080
    stats auth           admin:awesomePassword
    stats refresh        5s
    stats realm          HAProxy\ Statistics
    stats uri            /admin?stats
EOF
```

### 安装Docker、Kubeadm、kubectl

所有节点安装Docker/kubeadm/kubelet ，Kubernetes默认CRI（容器运行时）为Docker，因此先安装Docker

#### 安装Docker

首先配置一下Docker的阿里yum源

```
cat >/etc/yum.repos.d/docker.repo<<EOF
[docker-ce-edge]
name=Docker CE Edge - \$basearch
baseurl=https://mirrors.aliyun.com/docker-ce/linux/centos/7/\$basearch/edge
enabled=1
gpgcheck=1
gpgkey=https://mirrors.aliyun.com/docker-ce/linux/centos/gpg
EOF
```

然后yum方式安装docker

```
# yum安装
yum -y install docker-ce

# 查看docker版本
docker --version  

# 启动docker
systemctl enable docker
systemctl start docker
```

配置docker的镜像源

```
cat >> /etc/docker/daemon.json << EOF
{
  "registry-mirrors": ["https://b9pmyelo.mirror.aliyuncs.com"]
}
EOF
```

然后重启docker

```
systemctl restart docker
```

#### 添加kubernetes软件源

然后我们还需要配置一下yum的k8s软件源

```
cat > /etc/yum.repos.d/kubernetes.repo << EOF
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
```

#### 安装kubeadm，kubelet和kubectl

由于版本更新频繁，这里指定版本号部署：

```
# 安装kubelet、kubeadm、kubectl，同时指定版本
yum install -y kubelet-1.18.0 kubeadm-1.18.0 kubectl-1.18.0
# 设置开机启动
systemctl enable kubelet
```

### 部署Kubernetes Master【master节点】

#### 创建kubeadm配置文件

在具有vip的master上进行初始化操作，这里为master1

```
# 创建文件夹
mkdir /usr/local/kubernetes/manifests -p
# 到manifests目录
cd /usr/local/kubernetes/manifests/
# 新建yaml文件
vi kubeadm-config.yaml
```

yaml内容如下所示：

```
apiServer:
  certSANs:
    - master1
    - master2
    - master.k8s.io
    - 192.168.42.143
    - 192.168.42.140
    - 192.168.42.141
    - 127.0.0.1
  extraArgs:
    authorization-mode: Node,RBAC
  timeoutForControlPlane: 4m0s
apiVersion: kubeadm.k8s.io/v1beta2
certificatesDir: /etc/kubernetes/pki
clusterName: kubernetes
controlPlaneEndpoint: "master.k8s.io:16443"
controllerManager: {}
dns: 
  type: CoreDNS
etcd:
  local:    
    dataDir: /var/lib/etcd
imageRepository: registry.aliyuncs.com/google_containers
kind: ClusterConfiguration
kubernetesVersion: v1.16.3
networking: 
  dnsDomain: cluster.local  
  podSubnet: 10.244.0.0/16
  serviceSubnet: 10.1.0.0/16
scheduler: {}
```

然后我们在 master1 节点执行

```
kubeadm init --config kubeadm-config.yaml
```

执行完成后，就会在拉取我们的进行了【需要等待...】

![image-20201121194928988](https://gitee.com/moxi159753/LearningNotes/raw/master/K8S/18_Kubernetes%E6%90%AD%E5%BB%BA%E9%AB%98%E5%8F%AF%E7%94%A8%E9%9B%86%E7%BE%A4/images/image-20201121194928988.png)

按照提示配置环境变量，使用kubectl工具

```
# 执行下方命令
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
# 查看节点
kubectl get nodes
# 查看pod
kubectl get pods -n kube-system
```

**按照提示保存以下内容，一会要使用：**

```
kubeadm join master.k8s.io:16443 --token jv5z7n.3y1zi95p952y9p65 \
    --discovery-token-ca-cert-hash sha256:403bca185c2f3a4791685013499e7ce58f9848e2213e27194b75a2e3293d8812 \
    --control-plane 
```

> --control-plane ： 只有在添加master节点的时候才有

查看集群状态

```
# 查看集群状态
kubectl get cs
# 查看pod
kubectl get pods -n kube-system
```

### 安装集群网络

从官方地址获取到flannel的yaml，在master1上执行

```
# 创建文件夹
mkdir flannel
cd flannel
# 下载yaml文件
wget -c https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
```

安装flannel网络

```
kubectl apply -f kube-flannel.yml 
```

检查

```
kubectl get pods -n kube-system
```

### master2节点加入集群

#### 复制密钥及相关文件

从master1复制密钥及相关文件到master2

```
# ssh root@192.168.44.156 mkdir -p /etc/kubernetes/pki/etcd

# scp /etc/kubernetes/admin.conf root@192.168.44.156:/etc/kubernetes
   
# scp /etc/kubernetes/pki/{ca.*,sa.*,front-proxy-ca.*} root@192.168.44.156:/etc/kubernetes/pki
   
# scp /etc/kubernetes/pki/etcd/ca.* root@192.168.44.156:/etc/kubernetes/pki/etcd
```

#### master2加入集群

执行在master1上init后输出的join命令,需要带上参数`--control-plane`表示把master控制节点加入集群

```
kubeadm join master.k8s.io:16443 --token ckf7bs.30576l0okocepg8b     --discovery-token-ca-cert-hash sha256:19afac8b11182f61073e254fb57b9f19ab4d798b70501036fc69ebef46094aba --control-plane
```

检查状态

```
kubectl get node

kubectl get pods --all-namespaces
```

### 加入Kubernetes Node

在node1上执行

向集群添加新节点，执行在kubeadm init输出的kubeadm join命令：

```
kubeadm join master.k8s.io:16443 --token ckf7bs.30576l0okocepg8b     --discovery-token-ca-cert-hash sha256:19afac8b11182f61073e254fb57b9f19ab4d798b70501036fc69ebef46094aba
```

**集群网络重新安装，因为添加了新的node节点**

检查状态

```
kubectl get node
kubectl get pods --all-namespaces
```

### 测试kubernetes集群

在Kubernetes集群中创建一个pod，验证是否正常运行：

```
# 创建nginx deployment
kubectl create deployment nginx --image=nginx
# 暴露端口
kubectl expose deployment nginx --port=80 --type=NodePort
# 查看状态
kubectl get pod,svc
```

然后我们通过任何一个节点，都能够访问我们的nginx页面







## 卸载

```bash
kubeadm reset	# 卸载服务
```

```bash
rpm -qa|grep kube*|xargs rpm --nodeps -e	# 删除rpm包
```

```bash
docker images -qa|xargs docker rmi -f	# 删除容器及镜像
```







## 其他

`journalctl -u kubelet -n 1000`	#查看节点日志

