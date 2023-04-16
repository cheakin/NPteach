# 谷粒商城-集群篇(cluster)
![[assets/谷粒商城-微服务架构图.jpg]]

## k8s
### 简介
Kubernetes 简称 k8s。是用于自动部署，扩展和管理容器化应用程序的开源系统。
中文官网:https://kubernetes.io/zh/
中文社区:https://www.kubernetes.org.cn/
官方文档https://kubernetes.io/zh/docs/home/
社区文档http://docs.kubernetes.org.cn/

略

### 架构原理&核心概念
#### 架构原理
1. 整体主从方式
    ![[Pasted image 20230415224652.png]]
    ![[Pasted image 20230415225115.png]]
2. Master 节点架构
    ![[Pasted image 20230415225249.png]]
    * kube-apiserver
        * 对外暴露 K8S 的 api 接口，是外界进行资源操作的唯一入口
        * 提供认证、授权、访问控制、API 注册和发现等机制
    * etcd
        * etcd 是兼具一致性和高可用性的键值数据库，可以作为保存 Kubernetes 所有集群数据的后台数据库。
        * Kubernetes 集群的 etcd 数据库通常需要有个备份计划
    * kube-scheduler
        * 主节点上的组件，该组件监视那些新创建的未指定运行节点的 Pod，并选择节点让 Pod 在上面运行。
        * 所有对 k8s 的集群操作，都必须经过主节点进行调度
    * Kube-controller-manager
        * 在主节点上运行控制器的组件
        * 这些控制器包括:
            * 节点控制器 (Node Controller) : 负责在节点出现故障时进行通知和响应
            * 副本控制器 (Replication Controller): 负责为系统中的每个副本控制器对象维护正确数量的 Pod。
            * 端点控制器 (Endpoints Controller): 填充端点(Endpoints)对象(即加入 Service与 Pod)
            * 服务帐户和令牌控制器 (Service Account & Token Controllers): 为新的命名空间创建默认帐户和 API 访问令牌
3. Node 节点架构
    ![[Pasted image 20230415225809.png]]
    * kubelet
        * 一个在集群中每个节点上运行的代理。它保证容器都运行在 Pod 中。
        * 负责维护容器的生命周期，同时也负责 Volume (CSI) 和网络 (CNI) 的管理
    * kube-proxy
        * 负责为 Service 提供 cluster 内部的服务发现和负载均衡
    * 容器运行环境(Container Runtime)
    * 容器运行环境是负责运行容器的软件
    * Kubernetes 支持多个容器运行环境: Docker、containerd、cri-o、rktlet 以及任何实现 Kubernetes CRI (容器运行环境接口)。
* fluentd
    * 是一个守护进程，它有助于提供集群层面日志 集群层面的日志

#### 核心概念
![[Pasted image 20230415231830.png]]
* Container: 容器，可以是 docker 启动的一个容器
* Pod:
    * k8s 使用 Pod 来组织一组容器
    * 一个 Pod 中的所有容器共享同一网络。
    * Pod 是 k8s 中的最小部署单元
* Volume
    ![[Pasted image 20230415232643.png]]
    * 声明在 Pod 容器中可访问的文件目录
    * 可以被挂载在 Pod 中一个或多个容器指定路径下
    * 支持多种后端存储抽象(本地存储，分布式存储，云存储
    * Controllers: 更高层次对象，部署和管理 Pod;
    * ReplicaSet: 确保预期的 Pod 副本数量
    * Deplotment: 无状态应用部署
    * StatefulSet: 有状态应用部署
    * DaemonSet: 确保所有 Node 都运行一个指定 Pod
    * Job: 一次性任务
    * Cronjob: 定时任务
* Deployment:
    ![[Pasted image 20230415232745.png]]
    * 定义一组 Pod 的副本数目、版本等
    * 通过控制器 (Controller) 维持 Pod 数目(自动回复失败的 Pod)
    * 通过控制器以指定的策略控制版本(滚动升级，回滚等)
* Service
    ![[Pasted image 20230415232807.png]]
    * 定义一组 Pod 的访问策略
    * Pod 的负载均衡，提供一个或者多个 Pod 的稳定访问地址
    * 支持多种方式 (ClusterlP、NodePort、LoadBalance)
* Label: 标签，用于对象资源的查询，筛选
    ![[Pasted image 20230415233047.png]]
* Namespace: 命名空间，逻辑隔离
    * 一个集群内部的逻辑隔离机制 (鉴权，资源)
    * 每个资源都属于一个 namespace
    * 同一个 namespace 所有资源名不能重复
    * 不同 namespace 可以资源名重复
    ![[Pasted image 20230415233145.png]]
* API:
    我们通过 kubernetes 的 API 来操作整个集群。可以通过 kubectl、ui、curl 最终发送 http+json/yaml 方式的请求给 API Server,然后控制 k8s集群。**k8s 里的所有的资源对象都可以采用 yaml 或JSON 格式的文件定义或描述**
    ![[Pasted image 20230415233417.png]]

### 集群搭建
#### 环境准备
流程叙述
![[Pasted image 20230415233639.png]]
# 谷粒商城-集群篇(cluster)
![[assets/谷粒商城-微服务架构图.jpg]]

## k8s
### 简介
Kubernetes 简称 k8s。是用于自动部署，扩展和管理容器化应用程序的开源系统。
中文官网:https://kubernetes.io/zh/
中文社区:https://www.kubernetes.org.cn/
官方文档https://kubernetes.io/zh/docs/home/
社区文档http://docs.kubernetes.org.cn/

略

### 架构原理&核心概念
#### 架构原理
1. 整体主从方式
    ![[Pasted image 20230415224652.png]]
    ![[Pasted image 20230415225115.png]]
2. Master 节点架构
    ![[Pasted image 20230415225249.png]]
    * kube-apiserver
        * 对外暴露 K8S 的 api 接口，是外界进行资源操作的唯一入口
        * 提供认证、授权、访问控制、API 注册和发现等机制
    * etcd
        * etcd 是兼具一致性和高可用性的键值数据库，可以作为保存 Kubernetes 所有集群数据的后台数据库。
        * Kubernetes 集群的 etcd 数据库通常需要有个备份计划
    * kube-scheduler
        * 主节点上的组件，该组件监视那些新创建的未指定运行节点的 Pod，并选择节点让 Pod 在上面运行。
        * 所有对 k8s 的集群操作，都必须经过主节点进行调度
    * Kube-controller-manager
        * 在主节点上运行控制器的组件
        * 这些控制器包括:
            * 节点控制器 (Node Controller) : 负责在节点出现故障时进行通知和响应
            * 副本控制器 (Replication Controller): 负责为系统中的每个副本控制器对象维护正确数量的 Pod。
            * 端点控制器 (Endpoints Controller): 填充端点(Endpoints)对象(即加入 Service与 Pod)
            * 服务帐户和令牌控制器 (Service Account & Token Controllers): 为新的命名空间创建默认帐户和 API 访问令牌
3. Node 节点架构
    ![[Pasted image 20230415225809.png]]
    * kubelet
        * 一个在集群中每个节点上运行的代理。它保证容器都运行在 Pod 中。
        * 负责维护容器的生命周期，同时也负责 Volume (CSI) 和网络 (CNI) 的管理
    * kube-proxy
        * 负责为 Service 提供 cluster 内部的服务发现和负载均衡
    * 容器运行环境(Container Runtime)
    * 容器运行环境是负责运行容器的软件
    * Kubernetes 支持多个容器运行环境: Docker、containerd、cri-o、rktlet 以及任何实现 Kubernetes CRI (容器运行环境接口)。
* fluentd
    * 是一个守护进程，它有助于提供集群层面日志 集群层面的日志

#### 核心概念
![[Pasted image 20230415231830.png]]
* Container: 容器，可以是 docker 启动的一个容器
* Pod:
    * k8s 使用 Pod 来组织一组容器
    * 一个 Pod 中的所有容器共享同一网络。
    * Pod 是 k8s 中的最小部署单元
* Volume
    ![[Pasted image 20230415232643.png]]
    * 声明在 Pod 容器中可访问的文件目录
    * 可以被挂载在 Pod 中一个或多个容器指定路径下
    * 支持多种后端存储抽象(本地存储，分布式存储，云存储
    * Controllers: 更高层次对象，部署和管理 Pod;
    * ReplicaSet: 确保预期的 Pod 副本数量
    * Deplotment: 无状态应用部署
    * StatefulSet: 有状态应用部署
    * DaemonSet: 确保所有 Node 都运行一个指定 Pod
    * Job: 一次性任务
    * Cronjob: 定时任务
* Deployment:
    ![[Pasted image 20230415232745.png]]
    * 定义一组 Pod 的副本数目、版本等
    * 通过控制器 (Controller) 维持 Pod 数目(自动回复失败的 Pod)
    * 通过控制器以指定的策略控制版本(滚动升级，回滚等)
* Service
    ![[Pasted image 20230415232807.png]]
    * 定义一组 Pod 的访问策略
    * Pod 的负载均衡，提供一个或者多个 Pod 的稳定访问地址
    * 支持多种方式 (ClusterlP、NodePort、LoadBalance)
* Label: 标签，用于对象资源的查询，筛选
    ![[Pasted image 20230415233047.png]]
* Namespace: 命名空间，逻辑隔离
    * 一个集群内部的逻辑隔离机制 (鉴权，资源)
    * 每个资源都属于一个 namespace
    * 同一个 namespace 所有资源名不能重复
    * 不同 namespace 可以资源名重复
    ![[Pasted image 20230415233145.png]]
* API:
    我们通过 kubernetes 的 API 来操作整个集群。可以通过 kubectl、ui、curl 最终发送 http+json/yaml 方式的请求给 API Server,然后控制 k8s集群。**k8s 里的所有的资源对象都可以采用 yaml 或JSON 格式的文件定义或描述**
    ![[Pasted image 20230415233417.png]]

### 集群搭建
#### 环境准备
流程叙述
![[Pasted image 20230415233639.png]]
1. 通过 Kubectl 提交一个创建 RC(Replication Controller) 的请求,该请求通过 APIServer被写入 etcd 中
2. 此时 Controller Manager 通过 API Server 的监听资源变化的接口监听到此 RC事件
3. 分析之后，发现当前集群中还没有它所对应的 Pod 实例，
4. 于是根据 RC 里的 Pod 模板定义生成一个 Pod 对象，通过 APIServer 写入 etcd5、
5. 此事件被 Scheduler 发现，它立即执行一个复杂的调度流程，为这个新 Pod 选定一个落户的 Node，然后通过 API Server 讲这一结果写入到 etcd 中，
6. 目标 Node 上运行的 Kubelet 进程通过 APIServer 监测到这个"新生的"Pod，并按照它的定义，启动该 Pod 并任劳任怨地负责它的下半生，直到 Pod 的生命结束。
7. 随后，我们通过 Kubectl 提交一个新的映射到该 Pod 的 Service 的创建请求
8. ControllerManager 通过 Label 标签查询到关联的 Pod 实例，然后生成 Service 的Endpoints 信息，并通过 APIServer 写入到 etcd 中，
9. 接下来，所有 Node 上运行的 Proxy 进程通过 APIServer 查询并监听 Service 对象与其对应的 Endpoints 信息，建立一个软件方式的负载均衡器来实现 Service 访问到后端Pod 的流量转发功能。
k8s 里的所有的资源对象都可以采用 yaml 或JSON 格式的文件定义或描述

部署步骤
1. 在所有节点上安装 Docker 和 kubeadm
2. 部署 Kubernetes Master
3. 部署容器网络插件
4. 部署 Kubernetes Node，将节点加入 Kubernetes 集群中5.部署 Dashboard Web 页面，可视化查看 Kubernetes 资源
![[Pasted image 20230416205816.png]]

``` bat
vagrant ssh k8s-node1
```

``` sh
su root

vi /etc/ssh/sshd_config

service sshd restart

exit;
exit;
```









