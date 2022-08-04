# 谷粒商城
> 参考: [Java项目《谷粒商城》Java架构师 | 微服务 | 大型电商项目](https://www.bilibili.com/video/BV1np4y1C7Yf)
> [从前慢-谷粒商城篇章4](https://blog.csdn.net/unique_perfect/article/details/114685933)
> [guli-mall](https://www.yuque.com/zhangshuaiyin/guli-mall)

# 谷粒商城-高级篇
围绕商城前端的流程系统. 搜索、结算、登录, 以及周边治理、流控、链路追踪等
## ES(ElasticSearch)
官方文档: https://www.elastic.co/guide/index.html
全文搜索属于最常见的需求，开源的 Elasticsearch （以下简称 Elastic）是目前全文搜索引擎的首选。它可以快速地储存、搜索和分析海量数据。
Elastic 的底层是开源库 Lucene。但是，你没法直接用 Lucene，必须自己写代码去调用它的接口。Elastic 是 Lucene 的封装，提供了 REST API 的操作接口，开箱即用。

### 基本概念
**Index(索引)**
动词: 相当于MySQL中的insert
名词：相当于MySQL的Database

**Type类型**
在Index中，可以定义一个或多个类型
类似于MySQL的Table，每一种类型的数据放在一起

**Document文档**
保存在某个索引(Index)下，某种类型(Type)的一个数据文档(Document)，文档是json格式的，
Document就像是MySQL中的某个Table里面的内容。每一行对应的列叫属性

[](./assets/GuliMall.md/GuliMall_high/1659623601145.jpg)


ElasticSearch7-去掉type概念
* 关系型数据库中两个数据表示是独立的，即使他们里面有相同名称的列也不影响使用，但ES中不是这样的。elasticsearch是基于Lucene开发的搜索引擎，而ES中不同type下名称相同的filed最终在Lucene中的处理方式是一样的。
  - 两个不同type下的两个user_name，在ES同一个索引下其实被认为是同一个filed，你必须在两个不同的type中定义相同的filed映射。否则，不同type中的相同字段名称就会在处理中出现冲突的情况，导致Lucene处理效率下降。
  - 去掉type就是为了提高ES处理数据的效率。
* Elasticsearch 7.x
  URL中的type参数为可选。比如，索引一个文档不再要求提供文档类型。
* Elasticsearch8.x
  不再支持URL中的type参数。
* 解决: 将索引从多类型迁移到单类型，每种类型文档一个独立索引

### 安装ElasticSearch, Kibana(Docker)
1. 下载镜像文件(版本间有区别, 注意版本)
    ``` shell
    # 存储和检索数据
    docker pull elasticsearch:7.4.2
    # 可视化检索数据
    docker pull kibana:7.4.2
    ```
    elasticsearch 和 kibana 版本要统一
2. 配置挂载数据文件夹
    ``` shell
    # 创建配置文件目录
    mkdir -p /mydata/elasticsearch/config
    # 创建数据目录
    mkdir -p /mydata/elasticsearch/data
    # 将/mydata/elasticsearch/文件夹中文件都可读可写, 视频后面会讲
    chmod -R 777 /mydata/elasticsearch/
    # 配置任意机器可以访问 elasticsearch
    echo "http.host: 0.0.0.0" >/mydata/elasticsearch/config/elasticsearch.yml
    ```
3. 创建实例(启动Elasticsearch)
    命令后面的 \是换行符，注意前面有空格
    ``` shell
    docker run --name elasticsearch -p 9200:9200 -p 9300:9300 \
      -e  "discovery.type=single-node" \
      -e ES_JAVA_OPTS="-Xms64m -Xmx512m" \
      -v /mydata/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
      -v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
      -v  /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
      -d elasticsearch:7.4.2  
    ```
    `-p 9200:9200 -p 9300:9300`：向外暴露两个端口，9200用于HTTP REST API请求，9300 ES 在分布式集群状态下 ES 之间的通信端口；
    `-e  "discovery.type=single-node"`：es 以单节点运行
    `-e ES_JAVA_OPTS="-Xms64m -Xmx512m"`：**设置启动占用内存，不设置可能会占用当前系统所有内存**
    `-v xxx`：挂载容器中的配置文件、数据文件、插件数据到本机的文件夹；
    `-d elasticsearch:7.4.2 `：指定要启动的镜像

    测试: 
    访问`IP:9200`(没改过配置文件的话就是: 192.168.56.10:9200), 能正常返回json就说明启动成功
4. 设置 Elasticsearch 随Docker启动
    ``` shell
    # 当前 Docker 开机自启，所以 ES 现在也是开机自启
    docker update elasticsearch --restart=always
    ```
5. 启动可视化Kibana
    ``` shell
    # 注意修改主机地址
    docker run --name kibana \
      -e ELASTICSEARCH_HOSTS=http://192.168.56.10:9200 \
      -p 5601:5601 \
      -d kibana:7.4.2
    ```
    测试: 
    访问`IP:5601`(没改过配置文件的话就是: 192.168.56.10:5601), 能正常返回打开就说明启动成功
6. 设置 Kibana 随Docker启动
    ``` shell
    # 当前 Docker 开机自启，所以 kibana 现在也是开机自启
    docker update kibana --restart=always
    ```




### ES-入门
Elasticsearch 是可以通过 `REST API` 接口来操作数据的，那么下面接通过几个接口的请求来演示它的使用。（当前虚拟机IP为192.168.56.10）
#### _cat

# 谷粒商城-集群篇(cluster)
包括k8s集群，CI/CD(持续集成)，DevOps等