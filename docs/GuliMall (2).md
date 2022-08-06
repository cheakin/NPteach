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
1. /_cat/nodes: 查看所有节点
   GET http://192.168.56.10:9200/_cat/nodes
   返回值中带`*`表示此节点是主节点
2. /_cat/health: 查看ES健康状况
   GET http://192.168.56.10:9200/_cat/health
3. /_cat/master: 查看主节点信息
   GET http://192.168.56.10:9200/_cat/master
4. /_cat/indicies: 查看所有索引
   *等价于 mysql 数据库的 show databases;*
   GET http://192.168.56.10:9200/_cat/indces


#### 新增数据(索引一个文档)
索引一个文档, 即保存一个数据，保存在哪个索引的哪个类型下（哪张数据库哪张表下），保存时用唯一标识指定
**PUT/POST `/customer/external/1`**, 注意设置为json格式
``` json
PUT http://192.168.56.10:9200/customer/external/1
POST http://192.168.56.10:9200/customer/external/1

// 在customer索引下的external类型下保存1号数据为
{
 "name":"John Doe"
}

// 接口返回
{
    "_index": "customer", // 表明该数据在哪个数据库下
    "_type": "external",  // 表明该数据在哪个类型下
    "_id": "1", // 表明被保存数据的id
    "_version": 1,  // 被保存数据的版本
    "result": "created",  // 这里是创建了一条数据，如果重新put一条数据，则该状态会变为updated，并且版本号也会发生变化。
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 0,
    "_primary_term": 1
}
```
PUT和POST都可以
POST新增。如果不指定id，会自动生成id。指定id就会修改这个数据，并新增版本号；
PUT可以新增也可以修改。PUT必须指定id；由于PUT需要指定id，我们一般用来做修改操作，不指定id会报错。
唯一区分是post不指定id时永远为创建

#### 查询数据&乐观锁字段
**查看文档**
GET /customer/external/1
``` json
GET http://192.168.56.10:9200/customer/external/1

// 返回结果
{
    "_index": "customer", 
    "_type": "external",
    "_id": "1",
    "_version": 2,
    "_seq_no": 1, // 并发控制字段，每次更新都会+1，用来做乐观锁
    "_primary_term": 1, // 同上，主分片重新分配，如重启，就会变化
    "found": true,
    "_source": {  // 真正的内容

        "name": "John Doe"
    }
}
```
通过`if_seq_no=1&if_primary_term=1`，当序列号匹配的时候，才进行修改，否则不修改, 当携带数据与实际值不匹配时更新失败

实例：将id=1的数据更新为name=2，起始_seq_no=18(错误值)，_primary_term=1
``` json
// 将name更新为1
PUT http://192.168.56.10:9200/customer/external/1?if_seq_no=2&if_primary_term=1
{
 "name":"2"
}

// 返回结果
状态码为409的错误
```

#### 更新文档
``` json
POST /customer/external/1/_update
{
    "doc":{
        "name":"111"
    }
}
```
或者
``` json
POST /customer/external/1
// 注意请求体内容格式
{
    "name":"222"
}
```

不同：带有update情况下
* POST操作会对比源文档数据，如果相同不会有什么操作，文档version不增加。
* PUT操作总会重新保存并增加version版本
* POST时带_update对比元数据如果一样就不进行任何操作。

看场景：
对于大并发更新，不带update
对于大并发查询偶尔更新，带update；对比更新，重新计算分配规则
POST更新文档，带有_update

#### 删除数据(文档&索引) & bulk批量操作导入样本测试数据
DELETE /customer/external/1
DELETE /customer
> 注：elasticsearch并没有提供删除类型的操作，只提供了删除索引和文档的操作。

**删除数据**
DELETE /customer/external/1
实例：删除id=1的数据，删除后继续查询
``` json 
DELETE http://192.168.56.10:9200/customer/external/1
// 返回结果: 成功
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 14,
    "result": "deleted",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 22,
    "_primary_term": 6
}


// 再次执行删除
DELETE http://192.168.56.10:9200/customer/external/1
// 返回结果: 失败
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 15,
    "result": "not_found",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 23,
    "_primary_term": 6
}

// 再次查询
GET http://192.168.56.10:9200/customer/external/1
// 返回结果: 失败
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "found": false
}
```

**删除索引**
DELTE /customer
实例：删除整个costomer索引数据
``` json
// 删除前，查询所有的索引
GET http://192.168.56.10:9200/_cat/indices
// 返回结果: 所有索引
green  open .kibana_task_manager_1   fpHzUpIiRXuuMseNzGqO9w 1 0 2 0 30.4kb 30.4kb
green  open .apm-agent-configuration mA-5E7Y9SM2tPkxQjFIY2A 1 0 0 0   283b   283b
green  open .kibana_1                aN7CsIGbTzyKGgy_iKvXVg 1 0 8 0 22.8kb 22.8kb
yellow open customer                 aDw6iUCKTLCKWSAkm3x3kg 1 1 1 0  3.5kb  3.5kb

// 删除 customer 索引
DELTE http://192.168.56.10:9200/customer
// 返回结果: 成功
{
    "acknowledged": true
}

// 再次删除 customer 索引
DELTE http://192.168.56.10:9200/customer
// 返回结果: 失败, 找不到索引了
{
    "error": {
        "root_cause": [
            {
                "type": "index_not_found_exception",
                "reason": "no such index [customer]",
                "resource.type": "index_or_alias",
                "resource.id": "customer",
                "index_uuid": "_na_",
                "index": "customer"
            }
        ],
        "type": "index_not_found_exception",
        "reason": "no such index [customer]",
        "resource.type": "index_or_alias",
        "resource.id": "customer",
        "index_uuid": "_na_",
        "index": "customer"
    },
    "status": 404
}


// 删除后，再次查询所有的索引
GET http://192.168.56.10:9200/_cat/indices
// 返回结果: 没有已经被删除的索引了
green open .kibana_task_manager_1   DhtDmKrsRDOUHPJm1EFVqQ 1 0 2 0 31.3kb 31.3kb
green open .apm-agent-configuration vxzRbo9sQ1SvMtGkx6aAHQ 1 0 0 0   283b   283b
green open .kibana_1                rdJ5pejQSKWjKxRtx-EIkQ 1 0 8 3 28.8kb 28.8kb
```

**bulk批量操作导入样本测试数据**
POST /customer/external/_bulk

ES的批量操作——bulk, 匹配导入数据
``` json
POST http://192.168.56.10:9200/customer/external/_bulk
// 两行为一个整体
{"index":{"_id":"1"}}
{"name":"a"}
{"index":{"_id":"2"}}
{"name":"b"}
```
注意格式json和text均不可，要去kibana里的 Dev Tools 操作
语法格式：
{action:{metadata}}\n
{request body  }\n
{action:{metadata}}\n
{request body  }\n

实例1: 执行多条数据
``` json
POST /customer/external/_bulk
{"index":{"_id":"1"}}
{"name":"John Doe"}
{"index":{"_id":"2"}}
{"name":"John Doe"}
// 执行结果
#! Deprecation: [types removal] Specifying types in bulk requests is deprecated.
{
  "took" : 318,  //花费了多少ms
  "errors" : false, //没有发生任何错误
  "items" : [ //每个数据的结果
    {
      "index" : { //保存
        "_index" : "customer", //索引
        "_type" : "external", //类型
        "_id" : "1", //文档
        "_version" : 1, //版本
        "result" : "created", //创建
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 0,
        "_primary_term" : 1,
        "status" : 201 //新建完成
      }
    },
    {
      "index" : { //第二条记录
        "_index" : "customer",
        "_type" : "external",
        "_id" : "2",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 1,
        "_primary_term" : 1,
        "status" : 201
      }
    }
  ]
}
```
这里的批量操作，当发生某一条执行发生失败时，其他的数据仍然能够接着执行，也就是说彼此之间是独立的。

bulk api以此按顺序执行所有的action（动作）。如果一个单个的动作因任何原因失败，它将继续处理它后面剩余的动作。当bulk api返回时，它将提供每个动作的状态（与发送的顺序相同），所以您可以检查是否一个指定的动作是否失败了。

实例2：对于整个索引执行批量操作
``` json
POST /_bulk
{"delete":{"_index":"website","_type":"blog","_id":"123"}}
{"create":{"_index":"website","_type":"blog","_id":"123"}}
{"title":"my first blog post"}
{"index":{"_index":"website","_type":"blog"}}
{"title":"my second blog post"}
{"update":{"_index":"website","_type":"blog","_id":"123"}}
{"doc":{"title":"my updated blog post"}}
// 运行结果：
#! Deprecation: [types removal] Specifying types in bulk requests is deprecated.
{
  "took" : 304,
  "errors" : false,
  "items" : [
    {
      "delete" : { //删除
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 1,
        "result" : "not_found", //没有该记录
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 0,
        "_primary_term" : 1,
        "status" : 404 //没有该记录
      }
    },
    {
      "create" : {  //创建
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 2,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 1,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "index" : {  //保存
        "_index" : "website",
        "_type" : "blog",
        "_id" : "5sKNvncBKdY1wAQmeQNo",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 2,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "update" : { //更新
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 3,
        "result" : "updated",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 3,
        "_primary_term" : 1,
        "status" : 200
      }
    }
  ]
}
```


样本测试数据
准备了一份顾客银行账户信息的虚构的JSON文档样本。每个文档都有下列的schema（模式）。
`https://gitee.com/xlh_blog/common_content/blob/master/es测试数据.json`，导入测试数据
``` json
// 导入数据
POST bank/account/_bulk
'上面的数据'

// 查看所有索引
GET http://192.168.56.10:9200/_cat/indices
// 返回结果: 包含刚才的索引和刚导入了1000条
yellow open bank                     99m64ElxRuiH46wV7RjXZA 1 1 1000 0 427.8kb 427.8kb
```















# 谷粒商城-集群篇(cluster)
包括k8s集群，CI/CD(持续集成)，DevOps等