# ElasticSearch
> 参考:  
> 1. [【尚硅谷】ElasticSearch教程入门到精通（基于ELK技术栈elasticsearch 7.8.x版本）](https://www.bilibili.com/video/BV1hh411D7sb)
> 2. [Elasticsearch学习笔记](https://blog.csdn.net/u011863024/article/details/115721328)


## 概述
**Elasticsearch 是什么**  
The Elastic Stack, 包括 Elasticsearch、 Kibana、 Beats 和 Logstash（也称为 ELK Stack）。能够安全可靠地获取任何来源、任何格式的数据，然后实时地对数据进行搜索、分析和可视化。
Elaticsearch，简称为 ES， ES 是一个开源的高扩展的分布式全文搜索引擎， 是整个 ElasticStack 技术栈的核心。
它可以近乎实时的存储、检索数据；本身扩展性很好，可以扩展到上百台服务器，处理 PB 级别的数据。

**全文搜索引擎**  
Google，百度类的网站搜索，它们都是根据网页中的关键字生成索引，我们在搜索的时候输入关键字，它们会将该关键字即索引匹配到的所有网页返回；还有常见的项目中应用日志的搜索等等。对于这些非结构化的数据文本，关系型数据库搜索不是能很好的支持。

一般传统数据库，全文检索都实现的很鸡肋，因为一般也没人用数据库存文本字段。进行全文检索需要扫描整个表，如果数据量大的话即使对 SQL 的语法优化，也收效甚微。建立了索引，但是维护起来也很麻烦，对于 insert 和 update 操作都会重新构建索引。 

基于以上原因可以分析得出，在一些生产环境中，使用常规的搜索方式，性能是非常差的：

* 搜索的数据对象是大量的非结构化的文本数据。
* 文件记录量达到数十万或数百万个甚至更多。
* 支持大量基于交互式文本的查询。
* 需求非常灵活的全文搜索查询。
* 对高度相关的搜索结果的有特殊需求，但是没有可用的关系数据库可以满足。
* 对不同记录类型、非文本数据操作或安全事务处理的需求相对较少的情况。为了解决结构化数据搜索和非结构化数据搜索性能问题，我们就需要专业，健壮，强大的全文搜索引擎 。

这里说到的全文搜索引擎指的是目前广泛应用的主流搜索引擎。它的工作原理是计算机索引程序通过扫描文章中的每一个词，对每一个词建立一个索引，指明该词在文章中出现的次数和位置，当用户查询时，检索程序就根据事先建立的索引进行查找，并将查找的结果反馈给用户的检索方式。这个过程类似于通过字典中的检索字表查字的过程。


## Elasticsearch入门
> 官方网址: https://www.elastic.co/cn/
> 官方文档: https://www.elastic.co/guide/index.html

### 准备
#### 下载并启动Elasticsearch
    Windows 版的 Elasticsearch 压缩包，解压即安装完毕，解压后的 Elasticsearch 的目录结构如下 ：
    | 目录 | 含义 |
    | -- | -- |
    | bin | 可执行脚本目录 |
    | config | 配置目录|
    | jdk | 内置 JDK 目录|
    | lib | 类库|
    | modules | 模块目录|
    | plugins | 插件目录|
    解压后，进入 bin 文件目录，点击 elasticsearch.bat 文件启动 ES 服务 。

    注意: 启动后会显示默认账号Elasticsearch的密码
    增加用户密码和SSL对于初学来说，徒增不必要的麻烦,这里直接干掉:
    config/elasticsearch.yml
    ```yaml
    xpack.security.enabled: false
    以及
    xpack.security.http.ssl:
        enabled: false
    ```

    注意： 9300 端口为 Elasticsearch 集群间组件的通信端口， 9200 端口为浏览器访问的 http协议`RESTful`端口。

    打开浏览器，输入地址： `http://localhost:9200`，测试返回结果, 正常访问即启动成功.

    * RESTful 测试工具
        这里推荐使用`Apifox`, 官网: https://www.apifox.cn/

#### 数据格式
    Elasticsearch 是面向文档型数据库，一条数据在这里就是一个文档。 为了方便大家理解，我们将 Elasticsearch 里存储文档数据和关系型数据库 MySQL 存储数据的概念进行一个类比
    ![Img](./FILES/ElasticSearch.md/img-20220630103238.png)
    ES 里的 Index 可以看做一个库，而 Types 相当于表， Documents 则相当于表的行。这里 Types 的概念已经被逐渐弱化， Elasticsearch 6.X 中，一个 index 下已经只能包含一个type， Elasticsearch 7.X 中, Type 的概念已经被删除了。

    **倒排索引**  
    传统的正排索引
    | id | content |
    | -- | -- |
    | 1001 | my name is zhangsan |
    | 1002 | my name is lisi |

    ES的倒排索引
    |keyword|id|
    |--|--|
    |name|1001, 1002|
    |zhang|1001|

#### HTTP-操作
1. 索引-创建
    对比关系型数据库，创建索引就等同于创建数据库。

    向 ES 服务器发`PUT`请求 ： `http://127.0.0.1:9200/shopping`
    请求后，服务器返回响应：
    ``` json
    {
        "acknowledged": true,
        "shards_acknowledged": true,
        "index": "shopping"
    }
    ```
    这样表示成功, 如果再次请求会提示`已存在`

    查看后台有` [shopping] creating index, cause [api], templates [], shards [1]/[1]`

2. 索引-查询&删除  
    * 查询单个索引
    向 ES 服务器发`GET`请求 ： `http://127.0.0.1:9200/shopping`, 返回结果: 
    ``` json
    {
        "shopping": {   // 索引名
            "aliases": {},  // 别名
            "mappings": {}, // 映射
            "settings": {   // 设置
                "index": {  // 设置-索引
                    "routing": {        // 设置-索引路由
                        "allocation": {
                            "include": {
                                "_tier_preference": "data_content"
                            }
                        }
                    },
                    "number_of_shards": "1",
                    "provided_name": "shopping",
                    "creation_date": "1656561245711",   // 设置-索引-创建时间
                    "number_of_replicas": "1",
                    "uuid": "N5a6epUoSVi3qis7wQFo3w",
                    "version": {
                        "created": "8030099"
                    }
                }
            }
        }
    }
    ```

    * 查询所有索引
        向 ES 服务器发`GET`请求 ： `http://127.0.0.1:9200/_cat/indices?v`
        这里请求路径中的_cat 表示查看的意思， indices 表示索引，所以整体含义就是查看当前 ES服务器中的所有索引，就好像 MySQL 中的 show tables 的感觉，服务器响应结果如下 :
        ```
        health status index    uuid                   pri rep docs.count docs.deleted store.size pri.store.size
        yellow open   shopping N5a6epUoSVi3qis7wQFo3w   1   1          0            0       225b           225b
        ```  
        |表头|含义|
        |--|--|
        | health	| 当前服务器健康状态： green(集群完整) yellow(单点正常、集群不完整) red(单点不正常)|
        |status	| 索引打开、关闭状态|
        |index	| 索引名|
        ||uuid	| 索引统一编号|
        |pri	| 主分片数量|
        |rep	| 副本数量|
        |docs.count	| 可用文档数量|
        |docs.deleted	| 文档删除状态（逻辑删除）|
        |store.size	| 主分片和副分片整体占空间大小|
        |pri.store.size	| 主分片占空间大小|  

    * 删除索引
    向 ES 服务器发`DELETE`请求 ： `http://127.0.0.1:9200/shopping`, 返回结果: 
    ``` json
    {
        "acknowledged": true
    }
    ```
    再次查看索引, 已经没有 shopping 了


假设索引已经创建好了，接下来我们来创建文档等操作。
这里的文档可以类比为关系型数据库中的表数据，添加的数据格式为 JSON 格式  

4. 文档-创建
    向 ES 服务器发 `POST` 请求 ： `http://127.0.0.1:9200/shopping/_doc`，请求体JSON内容为：
    ``` json
    {
        "title":"小米手机",
        "category":"小米",
        "images":"http://www.gulixueyuan.com/xm.jpg",
        "price":3999.00
    }
    ```
    注意: 指定请求体的格式为JSON(application/json)
    返回结果:
    ``` json
    {
        "_index": "shopping",   // 索引
        "_type": "_doc",    // 类型-文档
        "_id": "ANQqsHgBaKNfVnMbhZYU",  // 唯一标识，可以类比为 MySQL 中的主键，随机生成
        "_version": 1,  // 版本
        "result": "created",    // 结果，这里的 create 表示创建成功
        "_shards": {
            "total": 2,
            "successful": 1,
            "failed": 0
        },
        "_seq_no": 0,
        "_primary_term": 1
    }
    ```
    上面的数据创建后，由于没有指定数据唯一性标识（ID），默认情况下， ES 服务器会随机生成一个。

    如果想要自定义唯一性标识，需要在创建时指定, 向 ES 服务器发`PUT`或`POST`请求： `http://127.0.0.1:9200/shopping/_doc/1`, 如果增加数据时明确数据主键，那么请求方式也可以为`PUT`。请求结果为:
    ``` json
    {
        "_index": "shopping",
        "_id": "1", //<-------唯一键
        "_version": 1,
        "result": "created",
        "_shards": {
            "total": 2,
            "successful": 1,
            "failed": 0
        },
        "_seq_no": 2,
        "_primary_term": 1
    }
    ```

5. 主键查询 & 全查询  
    * 查询指定数据  
        向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_doc/1`, 请求结果如下:
        ``` json
        {
            "_index": "shopping",   // 索引
            "_id": "1", // 唯一键
            "_version": 1,
            "_seq_no": 2, 
            "_primary_term": 1,
            "found": true,  // 存在, 不存在则为false
            "_source": {    // 查询结果
                "title": "小米手机",
                "category": "小米",
                "images": "http://www.gulixueyuan.com/xm.jpg",
                "price": 3999
            }
        }
        ```
    * 查询所有数据  
        向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`, 请求结果为:
        ``` json
        {
            "took": 181,    // 耗时
            "timed_out": false,
            "_shards": {
                "total": 1,
                "successful": 1,
                "skipped": 0,
                "failed": 0
            },
            "hits": {   // 命中结果
                "total": {
                    "value": 3,
                    "relation": "eq"
                },
                "max_score": 1,
                "hits": [
                    {
                        "_index": "shopping",
                        "_id": "6IUzs4EB5TX8OJxkDCeX",
                        "_score": 1,
                        "_source": {
                            "title": "小米手机",
                            "category": "小米",
                            "images": "http://www.gulixueyuan.com/xm.jpg",
                            "price": 3999
                        }
                    },
                    {
                        "_index": "shopping",
                        "_id": "6YU0s4EB5TX8OJxkkici",
                        "_score": 1,
                        "_source": {
                            "title": "小米手机",
                            "category": "小米",
                            "images": "http://www.gulixueyuan.com/xm.jpg",
                            "price": 3999
                        }
                    },
                    {
                        "_index": "shopping",
                        "_id": "1",
                        "_score": 1,
                        "_source": {
                            "title": "小米手机",
                            "category": "小米",
                            "images": "http://www.gulixueyuan.com/xm.jpg",
                            "price": 3999
                        }
                    }
                ]
            }
        }
        ```

6. 全量修改 & 局部修改 & 删除
    * 全量修改
        和新增文档一样，输入相同的 URL 地址请求，如果请求体变化，会将原有的数据内容覆盖
        向 ES 服务器发 `PUT` 或 `POST` (都需要明确id) 请求 ： `http://127.0.0.1:9200/shopping/_doc/1`, 请求的json为
        ``` json
        {
            "title":"华为手机",
            "category":"华为",
            "images":"http://www.gulixueyuan.com/hw.jpg",
            "price":1999.00
        }
        ```
        请求的响应
        ``` json
        {
            "_index": "shopping",
            "_type": "_doc",
            "_id": "1",
            "_version": 2,
            "result": "updated",//<-----------updated 表示数据被更新
            "_shards": {
                "total": 2,
                "successful": 1,
                "failed": 0
            },
            "_seq_no": 2,
            "_primary_term": 1
        }
        ```
    * 局部修改
        修改数据时，也可以只修改某一给条数据的局部信息
        向 ES 服务器发 `POST` 请求 ： `http://127.0.0.1:9200/shopping/_update/1`, 请求的json为
        ``` json
        {
            "doc": {
                "title":"小米手机",
                "category":"小米"
            }
        }
        ```
        返回结果如下：
        ``` json
        {
            "_index": "shopping",
            "_type": "_doc",
            "_id": "1",
            "_version": 3,
            "result": "updated",//<-----------updated 表示数据被更新
            "_shards": {
                "total": 2,
                "successful": 1,
                "failed": 0
            },
            "_seq_no": 3,
            "_primary_term": 1
        }
        ```
        向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_doc/1`，查看修改内容, 返回结果为
        ``` json
        {
            "_index": "shopping",
            "_id": "1",
            "_version": 3,
            "_seq_no": 4,
            "_primary_term": 1,
            "found": true,
            "_source": {
                "title": "小米手机",
                "category": "小米",
                "images": "http://www.gulixueyuan.com/hw.jpg",
                "price": 1999
            }
        }
        ```
    * 删除
        删除一个文档不会立即从磁盘上移除，它只是被标记成已删除（逻辑删除）。
        向 ES 服务器发 `DELETE` 请求 ： `http://127.0.0.1:9200/shopping/_doc/1`, 返回结果为:
        ``` json
        {
            "_index": "shopping",
            "_id": "1",
            "_version": 4,
            "result": "deleted",    // 已删除
            "_shards": {
                "total": 2,
                "successful": 1,    // 成功
                "failed": 0
            },
            "_seq_no": 5,
            "_primary_term": 1
        }
        ```
        向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_doc/1`，查看是否删除成功：
        ``` json
        {
            "_index": "shopping",
            "_id": "1",
            "found": false
        }
        ```


7. 条件查询 & 分页查询 & 查询排序
    * 条件查询
        * URL带参查询
            查找category为小米的文档, 向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search?q=category:小米`，返回结果如下：
            ``` json
            {
                "took": 654,
                "timed_out": false,
                "_shards": {
                    "total": 1,
                    "successful": 1,
                    "skipped": 0,
                    "failed": 0
                },
                "hits": {
                    "total": {
                        "value": 2,
                        "relation": "eq"
                    },
                    "max_score": 0.7133499,
                    "hits": [
                        {
                            "_index": "shopping",
                            "_id": "6IUzs4EB5TX8OJxkDCeX",
                            "_score": 0.7133499,
                            "_source": {
                                "title": "小米手机",
                                "category": "小米",
                                "images": "http://www.gulixueyuan.com/xm.jpg",
                                "price": 3999
                            }
                        },
                        {
                            "_index": "shopping",
                            "_id": "6YU0s4EB5TX8OJxkkici",
                            "_score": 0.7133499,
                            "_source": {
                                "title": "小米手机",
                                "category": "小米",
                                "images": "http://www.gulixueyuan.com/xm.jpg",
                                "price": 3999
                            }
                        }
                    ]
                }
            }
            ```
            上述为URL带参数形式查询，这很容易让不善者心怀恶意，或者参数值出现中文会出现乱码情况。为了避免这些情况，我们可用使用带JSON请求体请求进行查询。
        * 请求体带参查询
            仍然查找category为小米的文档, 向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
            ``` json
            {
                "query":{
                    "match":{
                        "category":"小米"
                    }
                }
            }
            ```
            返回结果如下：
            ``` json
            {
                "took": 3,
                "timed_out": false,
                "_shards": {
                    "total": 1,
                    "successful": 1,
                    "skipped": 0,
                    "failed": 0
                },
                "hits": {
                    "total": {
                        "value": 3,
                        "relation": "eq"
                    },
                    "max_score": 1.3862942,
                    "hits": [
                        {
                            "_index": "shopping",
                            "_type": "_doc",
                            "_id": "ANQqsHgBaKNfVnMbhZYU",
                            "_score": 1.3862942,
                            "_source": {
                                "title": "小米手机",
                                "category": "小米",
                                "images": "http://www.gulixueyuan.com/xm.jpg",
                                "price": 3999
                            }
                        },
                        {
                            "_index": "shopping",
                            "_type": "_doc",
                            "_id": "A9R5sHgBaKNfVnMb25Ya",
                            "_score": 1.3862942,
                            "_source": {
                                "title": "小米手机",
                                "category": "小米",
                                "images": "http://www.gulixueyuan.com/xm.jpg",
                                "price": 1999
                            }
                        },
                        {
                            "_index": "shopping",
                            "_type": "_doc",
                            "_id": "BNR5sHgBaKNfVnMb7pal",
                            "_score": 1.3862942,
                            "_source": {
                                "title": "小米手机",
                                "category": "小米",
                                "images": "http://www.gulixueyuan.com/xm.jpg",
                                "price": 1999
                            }
                        }
                    ]
                }
            }
            ```
        * 带请求体方式的查找所有内容
            查找所有文档内容, 向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
            ``` json
            {
                "query":{
                    "match_all":{}
                }
            }
            ```
            返回所有文档结果：
            ``` json
            {
                "took": 3,
                "timed_out": false,
                "_shards": {
                    "total": 1,
                    "successful": 1,
                    "skipped": 0,
                    "failed": 0
                },
                "hits": {
                    "total": {
                        "value": 2,
                        "relation": "eq"
                    },
                    "max_score": 1,
                    "hits": [
                        {
                            "_index": "shopping",
                            "_id": "6IUzs4EB5TX8OJxkDCeX",
                            "_score": 1,
                            "_source": {
                                "title": "小米手机",
                                "category": "小米",
                                "images": "http://www.gulixueyuan.com/xm.jpg",
                                "price": 3999
                            }
                        },
                        {
                            "_index": "shopping",
                            "_id": "6YU0s4EB5TX8OJxkkici",
                            "_score": 1,
                            "_source": {
                                "title": "小米手机",
                                "category": "小米",
                                "images": "http://www.gulixueyuan.com/xm.jpg",
                                "price": 3999
                            }
                        }
                    ]
                }
            }
            ```
        * 查询指定字段(不指定的字段不查)
            查询指定字段，向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
            ``` json
            {
                "query":{
                    "match_all":{}
                },
                "_source":["title"]
            }
            ```
            返回结果如下：
            ``` json
            {
                "took": 47,
                "timed_out": false,
                "_shards": {
                    "total": 1,
                    "successful": 1,
                    "skipped": 0,
                    "failed": 0
                },
                "hits": {
                    "total": {
                        "value": 2,
                        "relation": "eq"
                    },
                    "max_score": 1,
                    "hits": [
                        {
                            "_index": "shopping",
                            "_id": "6IUzs4EB5TX8OJxkDCeX",
                            "_score": 1,
                            "_source": {
                                "title": "小米手机"
                            }
                        },
                        {
                            "_index": "shopping",
                            "_id": "6YU0s4EB5TX8OJxkkici",
                            "_score": 1,
                            "_source": {
                                "title": "小米手机"
                            }
                        }
                    ]
                }
            }
            ```
    * 分页查询
        向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
        ``` json
        {
            "query":{
                "match_all":{}
            },
            "from":0,
            "size":2
        }
        ```
        返回结果:
        ``` json
        {
            "took": 1,
            "timed_out": false,
            "_shards": {
                "total": 1,
                "successful": 1,
                "skipped": 0,
                "failed": 0
            },
            "hits": {
                "total": {
                    "value": 2,
                    "relation": "eq"
                },
                "max_score": 1,
                "hits": [
                    {
                        "_index": "shopping",
                        "_id": "6IUzs4EB5TX8OJxkDCeX",
                        "_score": 1,
                        "_source": {
                            "title": "小米手机",
                            "category": "小米",
                            "images": "http://www.gulixueyuan.com/xm.jpg",
                            "price": 3999
                        }
                    },
                    {
                        "_index": "shopping",
                        "_id": "6YU0s4EB5TX8OJxkkici",
                        "_score": 1,
                        "_source": {
                            "title": "小米手机",
                            "category": "小米",
                            "images": "http://www.gulixueyuan.com/xm.jpg",
                            "price": 3999
                        }
                    }
                ]
            }
        }
        ```
    * 查询排序
            如果你想通过排序查出价格最高的手机,向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
            ``` json
            {
                "query":{
                    "match_all":{}
                },
                "sort":{
                    "price":{
                        "order":"desc"
                    }
                }
            }
            ```
            返回结果:
            ``` json
            {
                "took": 1,
                "timed_out": false,
                "_shards": {
                    "total": 1,
                    "successful": 1,
                    "skipped": 0,
                    "failed": 0
                },
                "hits": {
                    "total": {
                        "value": 2,
                        "relation": "eq"
                    },
                    "max_score": null,
                    "hits": [
                        {
                            "_index": "shopping",
                            "_id": "6IUzs4EB5TX8OJxkDCeX",
                            "_score": null,
                            "_source": {
                                "title": "小米手机",
                                "category": "小米",
                                "images": "http://www.gulixueyuan.com/xm.jpg",
                                "price": 3999
                            },
                            "sort": [
                                3999
                            ]
                        },
                        {
                            "_index": "shopping",
                            "_id": "6YU0s4EB5TX8OJxkkici",
                            "_score": null,
                            "_source": {
                                "title": "小米手机",
                                "category": "小米",
                                "images": "http://www.gulixueyuan.com/xm.jpg",
                                "price": 3999
                            },
                            "sort": [
                                3999
                            ]
                        }
                    ]
                }
            }
            ```
            

8. 多条件查询 & 范围查询
    * 多条件查询
        **假设想找出小米牌子，价格为3999元的。（must相当于数据库的&&）**
        向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
        ``` json
        {
            "query":{   // 查询
                "bool":{    // 条件
                    "must":[{   // &&
                        "match":{
                            "category":"小米"
                        }
                    },{
                        "match":{
                            "price":3999.00
                        }
                    }]
                }
            }
        }
        ```
        返回结果:
        ``` json
        {
            "took": 77,
            "timed_out": false,
            "_shards": {
                "total": 1,
                "successful": 1,
                "skipped": 0,
                "failed": 0
            },
            "hits": {
                "total": {
                    "value": 2,
                    "relation": "eq"
                },
                "max_score": 1.7133498,
                "hits": [
                    {
                        "_index": "shopping",
                        "_id": "6IUzs4EB5TX8OJxkDCeX",
                        "_score": 1.7133498,
                        "_source": {
                            "title": "小米手机",
                            "category": "小米",
                            "images": "http://www.gulixueyuan.com/xm.jpg",
                            "price": 3999
                        }
                    },
                    {
                        "_index": "shopping",
                        "_id": "6YU0s4EB5TX8OJxkkici",
                        "_score": 1.7133498,
                        "_source": {
                            "title": "小米手机",
                            "category": "小米",
                            "images": "http://www.gulixueyuan.com/xm.jpg",
                            "price": 3999
                        }
                    }
                ]
            }
        }
        ```
        **假设想找出小米和华为的牌子。（should相当于数据库的||）**
        ``` json
        {
            "query":{
                "bool":{
                    "should":[{
                        "match":{
                            "category":"小米"
                        }
                    },{
                        "match":{
                            "category":"华为"
                        }
                    }]
                },
                "filter":{
                    "range":{
                        "price":{
                            "gt":2000
                        }
                    }
                }
            }
        }
        ```
        返回结果:
        ``` json
        {
            "error": {
                "root_cause": [
                    {
                        "type": "parsing_exception",
                        "reason": "[bool] malformed query, expected [END_OBJECT] but found [FIELD_NAME]",
                        "line": 14,
                        "col": 9
                    }
                ],
                "type": "parsing_exception",
                "reason": "[bool] malformed query, expected [END_OBJECT] but found [FIELD_NAME]",
                "line": 14,
                "col": 9
            },
            "status": 400
        }
        ```
    * 范围查询
        **假设想找出小米和华为的牌子，价格大于2000元的手机。**
        向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
        ``` json
        {
            "query":{
                "bool":{
                    "should":[{
                        "match":{
                            "category":"小米"
                        }
                    },{
                        "match":{
                            "category":"华为"
                        }
                    }],
                    "filter":{  // 过滤
                        "range":{   // 范围
                            "price":{
                                "gt":2000   // >2000
                            }
                        }
                    }
                }
            }
        }
        ```
        返回结果:
        ``` json
        {
            "took": 65,
            "timed_out": false,
            "_shards": {
                "total": 1,
                "successful": 1,
                "skipped": 0,
                "failed": 0
            },
            "hits": {
                "total": {
                    "value": 2,
                    "relation": "eq"
                },
                "max_score": 0.7133499,
                "hits": [
                    {
                        "_index": "shopping",
                        "_id": "6IUzs4EB5TX8OJxkDCeX",
                        "_score": 0.7133499,
                        "_source": {
                            "title": "小米手机",
                            "category": "小米",
                            "images": "http://www.gulixueyuan.com/xm.jpg",
                            "price": 3999
                        }
                    },
                    {
                        "_index": "shopping",
                        "_id": "6YU0s4EB5TX8OJxkkici",
                        "_score": 0.7133499,
                        "_source": {
                            "title": "小米手机",
                            "category": "小米",
                            "images": "http://www.gulixueyuan.com/xm.jpg",
                            "price": 3999
                        }
                    }
                ]
            }
        }
        ```

9. 全文检索 & 完全匹配 & 高亮查询
    * 全文检索
        **这功能像搜索引擎那样，如品牌输入“小华”，返回结果带回品牌有“小米”和“华为”的。**
        向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
        ``` json
        {
            "query":{
                "match":{
                    "category" : "小华"
                }
            }
        }
        返回结果如下：
        ``` json
        {
            "took": 2,
            "timed_out": false,
            "_shards": {
                "total": 1,
                "successful": 1,
                "skipped": 0,
                "failed": 0
            },
            "hits": {
                "total": {
                    "value": 2,
                    "relation": "eq"
                },
                "max_score": 0.35667494,
                "hits": [
                    {
                        "_index": "shopping",
                        "_id": "6IUzs4EB5TX8OJxkDCeX",
                        "_score": 0.35667494,
                        "_source": {
                            "title": "小米手机",
                            "category": "小米",
                            "images": "http://www.gulixueyuan.com/xm.jpg",
                            "price": 3999
                        }
                    },
                    {
                        "_index": "shopping",
                        "_id": "6YU0s4EB5TX8OJxkkici",
                        "_score": 0.35667494,
                        "_source": {
                            "title": "小米手机",
                            "category": "小米",
                            "images": "http://www.gulixueyuan.com/xm.jpg",
                            "price": 3999
                        }
                    }
                ]
            }
        }
        ```
    * 完全匹配
        只查询出完全匹配的参数
        向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
        ``` json
        {
            "query":{
                "match_phrase":{
                    "category" : "为"
                }
            }
        }
        返回结果如下：
        ``` json
        {
            "took": 1,
            "timed_out": false,
            "_shards": {
                "total": 1,
                "successful": 1,
                "skipped": 0,
                "failed": 0
            },
            "hits": {
                "total": {
                    "value": 0,
                    "relation": "eq"
                },
                "max_score": null,
                "hits": []
            }
        }
    * 高亮查询
        会将返回结果当中的指定字段高亮显示
        向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
        ``` json
        {
            "query":{
                "match_phrase":{
                    "category" : "为"
                }
            },
            "highlight":{
                "fields":{
                    "category":{}//<----高亮这字段
                }
            }
        }
        ```
        返回结果如下：
        ``` json
        {
            "took": 2,
            "timed_out": false,
            "_shards": {
                "total": 1,
                "successful": 1,
                "skipped": 0,
                "failed": 0
            },
            "hits": {
                "total": {
                    "value": 0,
                    "relation": "eq"
                },
                "max_score": null,
                "hits": []
            }
        }
        ```
10. 聚合查询
    聚合允许使用者对 es 文档进行统计分析，类似与关系型数据库中的 group by，当然还有很多其他的聚合，例如取最大值max、平均值avg等等。
    接下来按price字段进行分组：
    向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
    ``` json
    {
        "aggs":{    // 聚合操作
            "price_group":{ // 分组名称，随意起名
                "terms":{   // 分组
                    "field":"price" // 分组的字段
                }
            }
        }
    }
    ```
    返回结果如下：
    ``` json
    {
        "took": 120,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 2,
                "relation": "eq"
            },
            "max_score": 1,
            "hits": [
                {
                    "_index": "shopping",
                    "_id": "6IUzs4EB5TX8OJxkDCeX",
                    "_score": 1,
                    "_source": {
                        "title": "小米手机",
                        "category": "小米",
                        "images": "http://www.gulixueyuan.com/xm.jpg",
                        "price": 3999
                    }
                },
                {
                    "_index": "shopping",
                    "_id": "6YU0s4EB5TX8OJxkkici",
                    "_score": 1,
                    "_source": {
                        "title": "小米手机",
                        "category": "小米",
                        "images": "http://www.gulixueyuan.com/xm.jpg",
                        "price": 3999
                    }
                }
            ]
        },
        "aggregations": {
            "price_group": {
                "doc_count_error_upper_bound": 0,
                "sum_other_doc_count": 0,
                "buckets": [
                    {
                        "key": 3999,
                        "doc_count": 2
                    }
                ]
            }
        }
    }
    ```
    上面返回结果会附带原始数据的。若不想要不附带原始数据的结果，向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
    ``` json
    {
        "aggs":{
            "price_group":{
                "terms":{
                    "field":"price"
                }
            }
        },
        "size":0    // 不需要原始数据
    }
    ```
    返回结果如下：
    ``` json
    {
        "took": 45,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 2,
                "relation": "eq"
            },
            "max_score": null,
            "hits": []
        },
        "aggregations": {
            "price_group": {
                "doc_count_error_upper_bound": 0,
                "sum_other_doc_count": 0,
                "buckets": [
                    {
                        "key": 3999,
                        "doc_count": 2
                    }
                ]
            }
        }
    }
    ```
    若想对所有手机价格求平均值。向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:9200/shopping/_search`，附带JSON体如下：
    ``` json
    {
        "aggs":{
            "price_avg":{//名称，随意起名
                "avg":{//求平均
                    "field":"price"
                }
            }
        },
        "size":0
    }
    ```
    返回结果如下：
    ``` json
    {
        "took": 4,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 2,
                "relation": "eq"
            },
            "max_score": null,
            "hits": []
        },
        "aggregations": {
            "price_avg": {
                "value": 3999
            }
        }
    }
    ```
11. 映射关系
    有了索引库，等于有了数据库中的 database。
    接下来就需要建索引库(index)中的映射了，类似于数据库(database)中的表结构(table)。
    创建数据库表需要设置字段名称，类型，长度，约束等；索引库也一样，需要知道这个类型下有哪些字段，每个字段有哪些约束信息，这就叫做映射(mapping)。

    先创建一个索引：
    ``` json
    PUT http://127.0.0.1:9200/user
    
    // 返回结果
    {
        "acknowledged": true,
        "shards_acknowledged": true,
        "index": "user"
    }
    ```

    创建映射
    ``` json
    PUT http://127.0.0.1:9200/user/_mapping
    {
        "properties": {
            "name":{
                "type": "text", // 可分词
                "index": true   // 可索引查询
            },
            "sex":{
                "type": "keyword",  // 不能分词, 需完整匹配
                "index": true   // 可索引查询
            },
            "tel":{
                "type": "keyword",  // 不能分词, 需完整匹配
                "index": false  // 不可索引查询
            }
        }
    }
    
    // 返回结果
    {
        "acknowledged": true
    }
    ```

    查询映射
    ``` json
    GET http://127.0.0.1:9200/user/_mapping
    
    // 返回结果
    {
        "user": {
            "mappings": {
                "properties": {
                    "name": {
                        "type": "text"
                    },
                    "sex": {
                        "type": "keyword"
                    },
                    "tel": {
                        "type": "keyword",
                        "index": false
                    }
                }
            }
        }
    }
    ```

    增加数据
    ``` json
    PUT http://127.0.0.1:9200/user/_create/1001
    {
        "name":"小米",
        "sex":"男的",
        "tel":"1111"
    }
    
    // 返回结果
    {
        "_index": "user",
        "_id": "1001",
        "_version": 1,
        "result": "created",
        "_shards": {
            "total": 2,
            "successful": 1,
            "failed": 0
        },
        "_seq_no": 0,
        "_primary_term": 1
    }
    ```

    查找name含有"小"数据：
    ``` json
        GET http://127.0.0.1:9200/user/_search
    {
        "query":{
            "match":{
                "name":"小"
            }
        }
    }

    //返回结果如下：
    {
        "took": 1128,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 1,
                "relation": "eq"
            },
            "max_score": 0.2876821,
            "hits": [
                {
                    "_index": "user",
                    "_id": "1001",
                    "_score": 0.2876821,
                    "_source": {
                        "name": "小米",
                        "sex": "男的",
                        "tel": "1111"
                    }
                }
            ]
        }
    }
    ```

    查找sex含有"男"数据
    ``` json
    GET http://127.0.0.1:9200/user/_search
    {
        "query":{
            "match":{
                "sex":"男"
            }
        }
    }

    // 返回结果
    {
        "took": 2,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 0,
                "relation": "eq"
            },
            "max_score": null,
            "hits": []
        }
    }
    ```
    找不想要的结果，只因创建映射时"sex"的类型为"keyword"。
    "sex"只能完全为"男"的，才能得出原数据。
    ``` json
    GET http://127.0.0.1:9200/user/_search
    {
        "query":{
            "match":{
                "sex":"男的"
            }
        }
    }

    // 返回结果如下：
    {
        "took": 1,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 1,
                "relation": "eq"
            },
            "max_score": 0.2876821,
            "hits": [
                {
                    "_index": "user",
                    "_id": "1001",
                    "_score": 0.2876821,
                    "_source": {
                        "name": "小米",
                        "sex": "男的",
                        "tel": "1111"
                    }
                }
            ]
        }
    }
    ```

    查询电话
    ``` json
    GET http://127.0.0.1:9200/user/_search
    {
        "query":{
            "match":{
                "tel":"11"
            }
        }
    }

    // 返回结果如下：
    {
        "took": 6,
        "timed_out": false,
        "_shards": {
            "total": 1,
            "successful": 1,
            "skipped": 0,
            "failed": 0
        },
        "hits": {
            "total": {
                "value": 0,
                "relation": "eq"
            },
            "max_score": null,
            "hits": []
        }
    }
    ```
    查不到只因创建映射时"tel"的"index"为false。

### Java API
1. 环境准备
    新建Maven工程, 添加依赖：
    ``` maven
        <dependencies>
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
            <version>7.8.0</version>
        </dependency>
        <!-- elasticsearch 的客户端 -->
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-high-level-client</artifactId>
            <version>7.8.0</version>
        </dependency>
        <!-- elasticsearch 依赖 2.x 的 log4j -->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-api</artifactId>
            <version>2.8.2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>2.8.2</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.9</version>
        </dependency>
        <!-- junit 单元测试 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
    </dependencies>
    ```

    测试连接
    ``` java
    public class Client {

        public static void main(String[] args) throws IOException {
    //        new TransportClient(); // 已经不推荐使用

            // 创建ES客户端
            RestHighLevelClient esClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("localhost", 9200, "http"))
            );

            // 关闭客户端
            esClient.close();

        }
    }
    ```

2. 索引 - 创建
    ``` java
        public class IndexCreate {

        public static void main(String[] args) throws IOException {
            // 创建ES客户端
            RestHighLevelClient esClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("localhost", 9200, "http"))
            );

            // 创建索引
            CreateIndexRequest request = new CreateIndexRequest("user");
            CreateIndexResponse response = esClient.indices().create(request, RequestOptions.DEFAULT);

            // 响应状态
            boolean result = response.isAcknowledged();
            System.out.println("索引操作： " + result);

            // 关闭客户端
            esClient.close();

        }

    }

    // 后台打印
    索引操作： true
    ```

3. 索引 - 查询 & 删除
    查询
    ``` java
    public class IndexSearch {
        public static void main(String[] args) throws IOException {
            // 创建ES客户端
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("localhost", 9200, "http"))
            );

            // 查询索引 - 请求对象
            GetIndexRequest request = new GetIndexRequest("user");
            // 发送请求，获取响应
            GetIndexResponse response = client.indices().get(request,
                    RequestOptions.DEFAULT);

            System.out.println("aliases:"+response.getAliases());
            System.out.println("mappings:"+response.getMappings());
            System.out.println("settings:"+response.getSettings());


            // 关闭客户端
            client.close();
        }

    }


    // 控制台打印
    aliases:{user=[]}
    mappings:{user=org.elasticsearch.cluster.metadata.MappingMetadata@89e179ff}
    settings:{user={"index.creation_date":"1656741519990","index.number_of_replicas":"1","index.number_of_shards":"1","index.provided_name":"user","index.routing.allocation.include._tier_preference":"data_content","index.uuid":"G_Ps7dPkTVWNvowjR1sQ5g","index.version.created":"8030099"}}
    ```

    删除
    ``` java
    public class IndexDelete {
        public static void main(String[] args) throws IOException {
            // 创建ES客户端
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("localhost", 9200, "http"))
            );

            // 删除索引 - 请求对象
            DeleteIndexRequest request = new DeleteIndexRequest("user2");
            // 发送请求，获取响应
            AcknowledgedResponse response = client.indices().delete(request,RequestOptions.DEFAULT);
            // 操作结果
            System.out.println("操作结果 ： " + response.isAcknowledged());

            // 关闭客户端
            client.close();
        }
    }

    // 打印结果
    操作结果 ： true
    ```

接下来的文档操作会用到实体类, 这里先创建一下
``` java
@Data
public class User {
    // 姓名
    private String name;

    // 年龄
    private Integer age;

    // 性别
    private String sex;

}
```

4. 文档 - 新增 & 修改
    ``` java
    public class DocInsert {
        public static void main(String[] args) throws IOException {
            // 创建ES客户端
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("localhost", 9200, "http"))
            );

            // 新增文档 - 请求对象
            IndexRequest request = new IndexRequest();
            // 设置索引及唯一性标识
            request.index("user").id("1001");

            // 创建数据对象
            User user = new User();
            user.setName("zhangsan");
            user.setAge(30);
            user.setSex("男");

            ObjectMapper objectMapper = new ObjectMapper();
            String productJson = objectMapper.writeValueAsString(user);
            // 添加文档数据，数据格式为 JSON 格式
            request.source(productJson, XContentType.JSON);
            // 客户端发送请求，获取响应对象
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            // 3.打印结果信息
            System.out.println("response.getResult() = " + response.getResult());

            // 关闭客户端
            client.close();
        }
    }

    // 控制台打印
    ```




    
5. 文档 - 查询 & 删除
    * 查询
        ``` java
            public static void main(String[] args) throws IOException {
                // 创建ES客户端
                RestHighLevelClient client = new RestHighLevelClient(
                        RestClient.builder(new HttpHost("localhost", 9200, "http"))
                );

                // 新增文档 - 请求对象
                IndexRequest request = new IndexRequest();
                // 设置索引及唯一性标识
                request.index("user").id("1001");

                // 创建数据对象
                User user = new User();
                user.setName("zhangsan");
                user.setAge(30);
                user.setSex("男");

                // 添加文档数据, es8.0以后可以直接传对象 <= 报错, 但是操作成功
                request.source(user, XContentType.JSON);
                // 客户端发送请求，获取响应对象
                IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                // 3.打印结果信息
                System.out.println("response.getResult() = " + response.getResult());

                // 关闭客户端
                client.close();
            }
        }
        ```
    * 删除
        ``` java
        public class DocDelete {
        public static void main(String[] args) throws IOException {
            // 创建ES客户端
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("localhost", 9200, "http"))
            );

            DeleteRequest request = new DeleteRequest();
            request.index("user").id("1001");
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);

            System.out.println(response.toString());

            // 关闭客户端
            client.close();
        }
    }
        ```

6. 文档 - 高级查询 - 全量查询
    ``` java
    public class DocSearch {
        public static void main(String[] args) throws IOException {
            // 创建ES客户端
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("localhost", 9200, "http"))
            );

            // 新增文档 - 请求对象
            IndexRequest request = new IndexRequest();
            // 设置索引及唯一性标识
            request.index("user").id("1001");

            // 创建数据对象
            User user = new User();
            user.setName("zhangsan");
            user.setAge(30);
            user.setSex("男");

            // 添加文档数据, es8.0以后可以直接传对象 <= 报错, 但是操作成功
            request.source(user, XContentType.JSON);
            // 客户端发送请求，获取响应对象
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            // 3.打印结果信息
            System.out.println("response.getResult() = " + response.getResult());

            // 关闭客户端
            client.close();
        }
    }
    ```

7. 文档-高级查询 - 分页查询 & 条件查询 & 查询排序 & 组合查询 & 范围查询 & 模糊查询 & 高亮查询 & 最大值查询 & 分组查询
    ``` java
    public class DocQuery {
        public static void main(String[] args) throws IOException {
            // 创建ES客户端
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("localhost", 9200, "http"))
            );

            // 创建搜索请求对象
            SearchRequest request = new SearchRequest();
            request.indices("user");

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 1.条件查询
            /*sourceBuilder.query(QueryBuilders.termQuery("age", "30"));*/

            // 2.分页查询
            /*sourceBuilder.query(QueryBuilders.matchAllQuery());
            // 分页查询
            // 当前页其实索引(第一条数据的顺序号)， from
            sourceBuilder.from(0);
            // 每页显示多少条 size
            sourceBuilder.size(2);*/

            // 3.排序查询
            /*sourceBuilder.query(QueryBuilders.matchAllQuery());
            // 排序
            sourceBuilder.sort("age", SortOrder.ASC);
            // 需要的话可以加 排除
            String[] include = {"name"};
            String[] excludes = {};
            sourceBuilder.fetchSource(include, excludes);*/

            // 4.组合查询
            /*BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            // 必须包含
            boolQueryBuilder.must(QueryBuilders.matchQuery("age", "30"));
            // 一定不含
            boolQueryBuilder.mustNot(QueryBuilders.matchQuery("name", "zhangsan"));
            // 可能包含
            boolQueryBuilder.should(QueryBuilders.matchQuery("sex", "男"));
            sourceBuilder.query(boolQueryBuilder);*/

            // 5.范围查询
            /*RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("age");
            // 大于等于
            //rangeQuery.gte("30");
            // 小于等于
            rangeQuery.lte("40");
            sourceBuilder.query(rangeQuery);*/

            // 6.模糊查询
            /*sourceBuilder.query(QueryBuilders.fuzzyQuery("name","wangwu")
                    .fuzziness(Fuzziness.ONE)); // 允许偏差值*/

            // 7.高亮查询
            /*TermsQueryBuilder termsQueryBuilder =
                    QueryBuilders.termsQuery("name","lisi");
            sourceBuilder.query(termsQueryBuilder);
            // 构建高亮字段
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<font color='red'>"); // 设置标签前缀
            highlightBuilder.postTags("</font>");   // 设置标签后缀
            highlightBuilder.field("name"); // 设置高亮字段
            // 设置高亮构建对象
            sourceBuilder.highlighter(highlightBuilder);*/

            // 8.最大值查询
            /*sourceBuilder.aggregation(AggregationBuilders.max("maxAge").field("age"));*/

            // 9.分组查询
            sourceBuilder.aggregation(AggregationBuilders.terms("age_groupby").field("age"));

            request.source(sourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            System.out.println("response = " + response);

            // 关闭客户端
            client.close();
        }
    }
    ```

## Elasticsearch环境
### 简介
* 单机 & 集群
    单台 Elasticsearch 服务器提供服务，往往都有最大的负载能力，超过这个阈值，服务器
    性能就会大大降低甚至不可用，所以生产环境中，一般都是运行在指定服务器集群中。
    除了负载能力，单点服务器也存在其他问题：
    * 单台机器存储容量有限
    * 单服务器容易出现单点故障，无法实现高可用
    * 单服务的并发处理能力有限
    配置服务器集群时，集群中节点数量没有限制，大于等于 2 个节点就可以看做是集群了。一
般出于高性能及高可用方面来考虑集群中节点数量都是 3 个以上

总之，集群能提高性能，增加容错。

* 集群 Cluster
**一个集群就是由一个或多个服务器节点组织在一起，共同持有整个的数据，并一起提供索引和搜索功能。**一个 Elasticsearch 集群有一个唯一的名字标识，这个名字默认就是”elasticsearch”。这个名字是重要的，因为一个节点只能通过指定某个集群的名字，来加入这个集群。

* 节点 Node
集群中包含很多服务器， 一个节点就是其中的一个服务器。 作为集群的一部分，它存储数据，参与集群的索引和搜索功能。

一个节点也是由一个名字来标识的，默认情况下，这个名字是一个随机的漫威漫画角色的名字，这个名字会在启动的时候赋予节点。这个名字对于管理工作来说挺重要的，因为在这个管理过程中，你会去确定网络中的哪些服务器对应于 Elasticsearch 集群中的哪些节点。

一个节点可以通过配置集群名称的方式来加入一个指定的集群。默认情况下，每个节点都会被安排加入到一个叫做“elasticsearch”的集群中，这意味着，如果你在你的网络中启动了若干个节点，并假定它们能够相互发现彼此，它们将会自动地形成并加入到一个叫做“elasticsearch”的集群中。

在一个集群里，只要你想，可以拥有任意多个节点。而且，如果当前你的网络中没有运
行任何 Elasticsearch 节点，这时启动一个节点，会默认创建并加入一个叫做“elasticsearch”的
集群。

### Windows集群部署
* 配置
    使用新环境, 复制出三个服务, 分别为node1001, node1002, node1003
    [](./assets/ElasticSearch.md/1656948629345.jpg)

    若内存不够, 可再 config/jvm.options 中做出如下修改(默认是1g)
    ``` yml
    -Xms4g -> 修改为 -Xms256m
    -Xmx4g -> 修改为 -Xmx256m
    ```
    * node-1001 节点
        ``` yml
        #集群名称，节点之间要保持一致
        cluster.name: "my-application"

        # 节点名称
        node.name: node-1001
        # 节点角色, ES8.0以后使用角色的方式赋值
        node.roles: [ master, data ]

        # 通信地址
        network.host: localhost
        # 通信端口
        http.port: 1001
        # 通信监听端口, 8.0不再是`transport.tcp.port`
        transport.port: 9301

        # 跨域配置
        http.cors.enabled: true
        http.cors.allow-origin: "*"

        # 关闭认证模式
        xpack.security.enabled: false
        ```

    * node-1002 节点
        ``` yml
        #集群名称，节点之间要保持一致
        cluster.name: "my-application"

        # 节点名称
        node.name: node-1002
        # 节点角色, ES8.0以后使用角色的方式赋值
        node.roles: [ master, data ]

        # 通信地址
        network.host: localhost
        # 通信端口
        http.port: 1002
        # 通信监听端口, 8.0不再是`transport.tcp.port`
        transport.port: 9302

        discovery.seed_hosts: ["localhost:9301"]

        # 跨域配置
        http.cors.enabled: true
        http.cors.allow-origin: "*"

        # 关闭认证模式
        xpack.security.enabled: false
        ```

    * node-1003节点
        ``` yml
        #集群名称，节点之间要保持一致
        cluster.name: "my-application"

        # 节点名称
        node.name: node-1002
        # 节点角色, ES8.0以后使用角色的方式赋值
        node.roles: [ master, data ]

        # 通信地址
        network.host: localhost
        # 通信端口
        http.port: 1003
        # 通信监听端口, 8.0不再是`transport.tcp.port`
        transport.port: 9303

        discovery.seed_hosts: ["localhost:9301", "localhost:9302"]

        # 跨域配置
        http.cors.enabled: true
        http.cors.allow-origin: "*"

        # 关闭认证模式
        xpack.security.enabled: false
        ```

* 启动集群
    分别依次双击执行节点的bin/elasticsearch.bat, 启动节点服务器，启动后，会自动加入指定名称的集群。
    
* 测试集群
    * 状态
        向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:1001/_cluster/health`, 返回结果为: 
        ``` json
        {
            "cluster_name": "my-application",
            "status": "green",
            "timed_out": false,
            "number_of_nodes": 2,
            "number_of_data_nodes": 2,
            "active_primary_shards": 1,
            "active_shards": 2,
            "relocating_shards": 0,
            "initializing_shards": 0,
            "unassigned_shards": 0,
            "delayed_unassigned_shards": 0,
            "number_of_pending_tasks": 0,
            "number_of_in_flight_fetch": 0,
            "task_max_waiting_in_queue_millis": 0,
            "active_shards_percent_as_number": 100
        }
        ```
        **status字段**指示着当前集群在总体上是否工作正常。它的三种颜色含义如下：
        green：所有的主分片和副本分片都正常运行。
        yellow：所有的主分片都正常运行，但不是所有的副本分片都正常运行。
        red：有主分片没能正常运行。
    
    * 索引
        向集群中的node-1001节点增加索引：向 ES 服务器发 `PUT` 请求 ： `http://127.0.0.1:9200/shopping/_search`, 返回结果为:
        ``` json
        {
            "acknowledged": true,
            "shards_acknowledged": true,
            "index": "user"
        }
        ```
        此时向集群中的node-1003节点获取索引：向 ES 服务器发 `GET` 请求 ： `http://127.0.0.1:1003/user`, 返回结果为:
        ``` json
        {
            "user": {
                "aliases": {},
                "mappings": {},
                "settings": {
                    "index": {
                        "creation_date": "1617993035885",
                        "number_of_shards": "1",
                        "number_of_replicas": "1",
                        "uuid": "XJKERwQlSJ6aUxZEN2EV0w",
                        "version": {
                            "created": "7080099"
                        },
                        "provided_name": "user"
                    }
                }
            }
        }
        ```
        即:在node1001节点创建的索引会自动同步到node1003
### Linux单节点部署
略

## Elasticsearch进阶
### 核心概念
#### 索引Index
一个索引就是一个拥有几分相似特征的文档的集合。比如说，你可以有一个客户数据的索引，另一个产品目录的索引，还有一个订单数据的索引。一个索引由一个名字来标识（必须全部是小写字母），并且当我们要对这个索引中的文档进行索引、搜索、更新和删除（CRUD）的时候，都要使用到这个名字。在一个集群中，可以定义任意多的索引。

能搜索的数据必须索引，这样的好处是可以提高查询速度，比如：新华字典前面的目录就是索引的意思，目录可以提高查询速度。

**能搜索的数据必须索引，这样的好处是可以提高查询速度，比如：新华字典前面的目录就是索引的意思，目录可以提高查询速度。**

#### 类型Type
在一个索引中，你可以定义一种或多种类型。
一个类型是你的索引的一个逻辑上的分类/分区，其语义完全由你来定。通常，会为具有一组共同字段的文档定义一个类型。不同的版本，类型发生了不同的变化。
| 版本	| Type |
| -- | -- |
| 5.x |	支持多种 type |
|6.x |	只能有一种 type |
|7.x |	默认不再支持自定义索引类型（默认类型为： _doc） |

#### 文档Document、
一个文档是一个可被索引的基础信息单元，也就是一条数据。

比如：你可以拥有某一个客户的文档，某一个产品的一个文档，当然，也可以拥有某个订单的一个文档。文档以 JSON（Javascript Object Notation）格式来表示，而 JSON 是一个到处存在的互联网数据交互格式。

在一个 index/type 里面，你可以存储任意多的文档。

#### 字段Field
相当于是数据表的字段，对文档数据根据不同属性进行的分类标识。

#### 映射Mapping
mapping 是处理数据的方式和规则方面做一些限制，如：某个字段的数据类型、默认值、分析器、是否被索引等等。这些都是映射里面可以设置的，其它就是处理 ES 里面数据的一些使用规则设置也叫做映射，按着最优规则处理数据对性能提高很大，因此才需要建立映射，并且需要思考如何建立映射才能对性能更好。

#### 分片Shards
一个索引可以存储超出单个节点硬件限制的大量数据。比如，一个具有 10 亿文档数据
的索引占据 1TB 的磁盘空间，而任一节点都可能没有这样大的磁盘空间。 或者单个节点处理搜索请求，响应太慢。为了解决这个问题，**Elasticsearch 提供了将索引划分成多份的能力，每一份就称之为分片。**当你创建一个索引的时候，你可以指定你想要的分片的数量。**每个分片本身也是一个功能完善并且独立的“索引”**，这个“索引”可以被放置到集群中的任何节点上。

分片很重要，主要有两方面的原因：
1. 允许你水平分割 / 扩展你的内容容量。
2. 允许你在分片之上进行分布式的、并行的操作，进而提高性能/吞吐量。
至于一个分片怎样分布，它的文档怎样聚合和搜索请求，是完全由 Elasticsearch 管理的，对于作为用户的你来说，这些都是透明的，无需过分关心。

被混淆的概念是，一个 Lucene 索引 我们在 Elasticsearch 称作 分片 。 一个Elasticsearch 索引 是分片的集合。 当 Elasticsearch 在索引中搜索的时候， 他发送查询到每一个属于索引的分片（Lucene 索引），然后合并每个分片的结果到一个全局的结果集。

Lucene 是 Apache 软件基金会 Jakarta 项目组的一个子项目，提供了一个简单却强大的应用程式接口，能够做全文索引和搜寻。在 Java 开发环境里 Lucene 是一个成熟的免费开源工具。就其本身而言， Lucene 是当前以及最近几年最受欢迎的免费 Java 信息检索程序库。但 Lucene 只是一个提供全文搜索功能类库的核心工具包，而真正使用它还需要一个完善的服务框架搭建起来进行应用。

目前市面上流行的搜索引擎软件，主流的就两款： Elasticsearch 和 Solr,这两款都是基于 Lucene 搭建的，可以独立部署启动的搜索引擎服务软件。由于内核相同，所以两者除了服务器安装、部署、管理、集群以外，对于数据的操作 修改、添加、保存、查询等等都十分类似。

#### 副本Replicas
在一个网络 / 云的环境里，失败随时都可能发生，在某个分片/节点不知怎么的就处于
离线状态，或者由于任何原因消失了，这种情况下，有一个故障转移机制是非常有用并且是强烈推荐的。为此目的， Elasticsearch 允许你创建分片的一份或多份拷贝，这些拷贝叫做复制分片(副本)。

复制分片之所以重要，有两个主要原因：
* 在分片/节点失败的情况下，**提供了高可用性**。因为这个原因，注意到复制分片从不与原/主要（original/primary）分片置于同一节点上是非常重要的。
* 扩展你的搜索量/吞吐量，因为搜索可以在所有的副本上并行运行。

总之，每个索引可以被分成多个分片。一个索引也可以被复制 0 次（意思是没有复制）或多次。一旦复制了，每个索引就有了主分片（作为复制源的原来的分片）和复制分片（主分片的拷贝）之别。

分片和复制的数量可以在索引创建的时候指定。在索引创建之后，你可以在任何时候动态地改变复制的数量，但是你事后不能改变分片的数量。

默认情况下，Elasticsearch 中的每个索引被分片 1 个主分片和 1 个复制，这意味着，如果你的集群中至少有两个节点，你的索引将会有 1 个主分片和另外 1 个复制分片（1 个完全拷贝），这样的话每个索引总共就有 2 个分片， 我们需要根据索引需要确定分片个数。

#### 分配Allocation
将分片分配给某个节点的过程，包括分配主分片或者副本。如果是副本，还包含从主分片复制数据的过程。这个过程是由 master 节点完成的。

### 系统架构
[](./assets/ElasticSearch.md/1657037866096.jpg)
一个运行中的 Elasticsearch 实例称为一个节点，而集群是由一个或者多个拥有相同
cluster.name 配置的节点组成， 它们共同承担数据和负载的压力。当有节点加入集群中或者从集群中移除节点时，集群将会重新平均分布所有的数据。

当一个节点被选举成为主节点时， 它将负责管理集群范围内的所有变更，例如增加、
删除索引，或者增加、删除节点等。 而主节点并不需要涉及到文档级别的变更和搜索等操作，所以当集群只拥有一个主节点的情况下，即使流量的增加它也不会成为瓶颈。 任何节点都可以成为主节点。我们的示例集群就只有一个节点，所以它同时也成为了主节点。

作为用户，我们可以将请求发送到集群中的任何节点 ，包括主节点。 每个节点都知道
任意文档所处的位置，并且能够将我们的请求直接转发到存储我们所需文档的节点。 无论我们将请求发送到哪个节点，它都能负责从各个包含我们所需文档的节点收集回数据，并将最终结果返回給客户端。 Elasticsearch 对这一切的管理都是透明的。

### 单节点集群
我们在包含一个空节点的集群内创建名为 users 的索引，为了演示目的，我们将分配 3个主分片和一份副本（每个主分片拥有一个副本分片）。
``` json
#PUT http://127.0.0.1:1001/users
{
    "settings" : {
        "number_of_shards" : 3,
        "number_of_replicas" : 1
    }
}

//返回结果
{
    "acknowledged": true,
    "shards_acknowledged": true,
    "index": "users"
}
```
集群现在是拥有一个索引的单节点集群。所有 3 个主分片都被分配在 node-1 。
[](./assets/ElasticSearch.md/1657037978706.jpg)
通过 elasticsearch-head 插件（一个Chrome插件）查看集群情况 。
* 集群健康值:yellow( 3 of 6 )：表示当前集群的全部主分片都正常运行，但是副本分片没有全部处在正常状态。
    [](./assets/ElasticSearch.md/1657039351820.jpg)
* 3 个主分片正常。
  [](./assets/ElasticSearch.md/1657107387986.jpg)
* 3 个副本分片都是 Unassigned，它们都没有被分配到任何节点。 在同 一个节点上既保存原始数据又保存副本是没有意义的，因为一旦失去了那个节点，我们也将丢失该节点 上的所有副本数据。
[](./assets/ElasticSearch.md/1657039527897.jpg)

当前集群是正常运行的，但存在丢失数据的风险。

### 故障转移
当集群中只有一个节点在运行时，意味着会有一个单点故障问题——没有冗余。 幸运的是，我们只需再启动一个节点即可防止数据丢失。当你在同一台机器上启动了第二个节点时，只要它和第一个节点有同样的 cluster.name 配置，它就会自动发现集群并加入到其中。但是在不同机器上启动节点的时候，为了加入到同一集群，你需要配置一个可连接到的单播主机列表。之所以配置为使用单播发现，以防止节点无意中加入集群。只有在同一台机器上
运行的节点才会自动组成集群。
如果启动了第二个节点，集群将会拥有两个节点 : 所有主分片和副本分片都已被分配。

通过 elasticsearch-head 插件查看集群情况:
* 集群健康值:green( 3 of 6 )：表示所有 6 个分片（包括 3 个主分片和 3 个副本分片）都在正常运行。
* 3 个主分片正常。
  [](./assets/ElasticSearch.md/1657108187627.jpg)
* 第二个节点加入到集群后， 3 个副本分片将会分配到这个节点上——每 个主分片对应一个副本分片。这意味着当集群内任何一个节点出现问题时，我们的数据都完好无损。所 有新近被索引的文档都将会保存在主分片上，然后被并行的复制到对应的副本分片上。这就保证了我们 既可以从主分片又可以从副本分片上获得文档。
  [](./assets/ElasticSearch.md/1657108237082.jpg)

### 水平扩容
怎样为我们的正在增长中的应用程序按需扩容呢？当启动了第三个节点，我们的集群将会拥有三个节点的集群 : 为了分散负载而对分片进行重新分配 。

通过 elasticsearch-head 插件查看集群情况。
* 集群健康值:green( 3 of 6 )：表示所有 6 个分片（包括 3 个主分片和 3 个副本分片）都在正常运行。
* Node 1 和 Node 2 上各有一个分片被迁移到了新的 Node 3 节点，现在每个节点上都拥有 2 个分片， 而不是之前的 3 个。 这表示每个节点的硬件资源（CPU, RAM, I/O）将被更少的分片所共享，每个分片 的性能将会得到提升。
  [](./assets/ElasticSearch.md/1657108640314.jpg)
分片是一个功能完整的搜索引擎，它拥有使用一个节点上的所有资源的能力。 我们这个拥有 6 个分 片（3 个主分片和 3 个副本分片）的索引可以最大扩容到 6 个节点，每个节点上存在一个分片，并且每个 分片拥有所在节点的全部资源。

**但是如果我们想要扩容超过 6 个节点怎么办呢？**
主分片的数目在索引创建时就已经确定了下来。实际上，这个数目定义了这个索引能够
存储 的最大数据量。（实际大小取决于你的数据、硬件和使用场景。） 但是，读操作——
搜索和返回数据——可以同时被主分片 或 副本分片所处理，所以当你拥有越多的副本分片
时，也将拥有越高的吞吐量。
在运行中的集群上是可以动态调整副本分片数目的，我们可以按需伸缩集群。让我们把
副本数从默认的 1 增加到 2。
``` json
#PUT http://127.0.0.1:1001/users/_settings
{
    "number_of_replicas" : 2
}
```
users 索引现在拥有 9 个分片： 3 个主分片和 6 个副本分片。 这意味着我们可以将集群
扩容到 9 个节点，每个节点上一个分片。相比原来 3 个节点时，集群搜索性能可以提升 3 倍。

当然，如果只是在相同节点数目的集群上增加更多的副本分片并不能提高性能，因为每
个分片从节点上获得的资源会变少。 你需要增加更多的硬件资源来提升吞吐量。

但是更多的副本分片数提高了数据冗余量：按照上面的节点配置，我们可以在失去 2 个节点
的情况下不丢失任何数据。

### 应对故障
我们关闭第一个节点，这时集群的状态为:关闭了一个节点后的集群。

我们关闭的节点是一个主节点。而集群必须拥有一个主节点来保证正常工作，所以发生
的第一件事情就是选举一个新的主节点： Node 2 。在我们关闭 Node 1 的同时也失去了主
分片 1 和 2 ，并且在缺失主分片的时候索引也不能正常工作。 如果此时来检查集群的状况，我们看到的状态将会为 red ：不是所有主分片都在正常工作。

幸运的是，在其它节点上存在着这两个主分片的完整副本， 所以新的主节点立即将这些分片在 Node 2 和 Node 3 上对应的副本分片提升为主分片， 此时集群的状态将会为yellow。这个提升主分片的过程是瞬间发生的，如同按下一个开关一般。

**为什么我们集群状态是 yellow 而不是 green 呢？**
虽然我们拥有所有的三个主分片，但是同时设置了每个主分片需要对应 2 份副本分片，而此
时只存在一份副本分片。 所以集群不能为 green 的状态，不过我们不必过于担心：如果我
们同样关闭了 Node 2 ，我们的程序 依然 可以保持在不丢任何数据的情况下运行，因为
Node 3 为每一个分片都保留着一份副本。

如果想回复原来的样子，要确保Node-1的配置文件有如下配置：
``` yml
discovery.seed_hosts: ["localhost:9302", "localhost:9303"]
```
集群可以将缺失的副本分片再次进行分配，那么集群的状态也将恢复成之前的状态。 如果 Node 1 依然拥有着之前的分片，它将尝试去重用它们，同时仅从主分片复制发生了修改的数据文件。和之前的集群相比，只是 Master 节点切换了。

### 路由计算 & 分片控制
* 路由计算
    当索引一个文档的时候，文档会被存储到一个主分片中。 Elasticsearch 如何知道一个文档应该存放到哪个分片中呢？当我们创建文档时，它如何决定这个文档应当被存储在分片 1 还是分片 2 中呢？首先这肯定不会是随机的，否则将来要获取文档的时候我们就不知道从何处寻找了。实际上，这个过程是根据下面这个公式决定的：
     ``` json
    shard = hash(routing) % number_of_primary_shards
    ```
    routing 是一个可变值，默认是文档的 _id ，也可以设置成一个自定义的值。 routing 通过hash 函数生成一个数字，然后这个数字再除以 number_of_primary_shards （主分片的数量）后得到余数 。这个分布在 0 到 number_of_primary_shards-1 之间的余数，就是我们所寻求的文档所在分片的位置。

    这就解释了为什么我们要在创建索引的时候就确定好主分片的数量并且永远不会改变这个数量:因为如果数量变化了，那么所有之前路由的值都会无效，文档也再也找不到了。

    所有的文档API ( get . index . delete 、 bulk , update以及 mget ）都接受一个叫做routing 的路由参数，通过这个参数我们可以自定义文档到分片的映射。一个自定义的路由参数可以用来确保所有相关的文档—一例如所有属于同一个用户的文档——都被存储到同一个分片中。

* 分片控制
  我们可以发送请求到集群中的任一节点。每个节点都有能力处理任意请求。每个节点都知道集群中任一文档位置，所以可以直接将请求转发到需要的节点上。在下面的例子中，如果将所有的请求发送到Node 1001，我们将其称为协调节点coordinating node。

  当发送请求的时候， 为了扩展负载，更好的做法是轮询集群中所有的节点。

### 数据写流程
新建、索引和删除请求都是写操作， 必须在主分片上面完成之后才能被复制到相关的副本分片。
[](./assets/ElasticSearch.md/1657039527897.jpg)

在客户端收到成功响应时，文档变更已经在主分片和所有副本分片执行完成，变更是安全的。有一些可选的请求参数允许您影响这个过程，可能以数据安全为代价提升性能。这些选项很少使用，因为 Elasticsearch 已经很快，但是为了完整起见， 请参考下文：
   1. consistency
    * 即一致性。在默认设置下，即使仅仅是在试图执行一个写操作之前，主分片都会要求必须要有规定数量quorum（或者换种说法，也即必须要有大多数）的分片副本处于活跃可用状态，才会去执行写操作（其中分片副本 可以是主分片或者副本分片）。这是为了避免在发生网络分区故障（network partition）的时候进行写操作，进而导致数据不一致。 规定数量即： int((primary + number_of_replicas) / 2 ) + 1
    * consistency 参数的值可以设为：
      * one ：只要主分片状态 ok 就允许执行写操作。
      * all：必须要主分片和所有副本分片的状态没问题才允许执行写操作。
      * quorum：默认值为quorum , 即大多数的分片副本状态没问题就允许执行写操作。
    * 注意，规定数量的计算公式中number_of_replicas指的是在索引设置中的设定副本分片数，而不是指当前处理活动状态的副本分片数。如果你的索引设置中指定了当前索引拥有3个副本分片，那规定数量的计算结果即：int((1 primary + 3 replicas) / 2) + 1 = 3，如果此时你只启动两个节点，那么处于活跃状态的分片副本数量就达不到规定数量，也因此您将无法索引和删除任何文档。
   2. timeout
    如果没有足够的副本分片会发生什么？Elasticsearch 会等待，希望更多的分片出现。默认情况下，它最多等待 1 分钟。 如果你需要，你可以使用timeout参数使它更早终止：100是100 毫秒，30s是30秒。
新索引默认有1个副本分片，这意味着为满足规定数量应该需要两个活动的分片副本。 但是，这些默认的设置会阻止我们在单一节点上做任何事情。为了避免这个问题，要求只有当number_of_replicas 大于1的时候，规定数量才会执行。

### 数据读流程
[](assets/ElasticSearch.md/1657116259349.jpg)
在处理读取请求时，协调结点在每次请求的时候都会通过轮询所有的副本分片来达到负载均衡。在文档被检索时，已经被索引的文档可能已经存在于主分片上但是还没有复制到副本分片。 在这种情况下，副本分片可能会报告文档不存在，但是主分片可能成功返回文档。 一旦索引请求成功返回给用户，文档在主分片和副本分片都是可用的。

### 更新流程 & 批量操作流程
* 更新流程
    部分更新一个文档结合了先前说明的读取和写入流程：
    [](./assets/ElasticSearch.md/1657119286989.jpg)
    部分更新一个文档的步骤如下：
      1. 客户端向Node 1发送更新请求。
      2. 它将请求转发到主分片所在的Node 3 。
      3. Node 3从主分片检索文档，修改_source字段中的JSON，并且尝试重新索引主分片的文档。如果文档已经被另一个进程修改,它会重试步骤3 ,超过retry_on_conflict次后放弃。
      4. 如果 Node 3成功地更新文档，它将新版本的文档并行转发到Node 1和 Node 2上的副本分片，重新建立索引。一旦所有副本分片都返回成功，Node 3向协调节点也返回成功，协调节点向客户端返回成功。
    当主分片把更改转发到副本分片时， 它不会转发更新请求。 相反，它转发完整文档的新版本。请记住，这些更改将会异步转发到副本分片，并且不能保证它们以发送它们相同的顺序到达。 如果 Elasticsearch 仅转发更改请求，则可能以错误的顺序应用更改，导致得到损坏的文档。
* 批量操作流程
    **mget和 bulk API的模式类似于单文档模式。**区别在于协调节点知道每个文档存在于哪个分片中。它将整个多文档请求分解成每个分片的多文档请求，并且将这些请求并行转发到每个参与节点。

    协调节点一旦收到来自每个节点的应答，就将每个节点的响应收集整理成单个响应，返回给客户端。
    [](./assets/ElasticSearch.md/1657119435446.jpg)

    **用单个 mget 请求取回多个文档所需的步骤顺序:**
      1. 客户端向 Node 1 发送 mget 请求。
      2. Node 1为每个分片构建多文档获取请求，然后并行转发这些请求到托管在每个所需的主分片或者副本分片的节点上。一旦收到所有答复，Node 1 构建响应并将其返回给客户端。
    可以对docs数组中每个文档设置routing参数。

    bulk API， 允许在单个批量请求中执行多个创建、索引、删除和更新请求。
    [](./assets/ElasticSearch.md/1657119567175.jpg)
    bulk API 按如下步骤顺序执行：
      1. 客户端向Node 1 发送 bulk请求。
      2. Node 1为每个节点创建一个批量请求，并将这些请求并行转发到每个包含主分片的节点主机。
      3. 主分片一个接一个按顺序执行每个操作。当每个操作成功时,主分片并行转发新文档（或删除）到副本分片，然后执行下一个操作。一旦所有的副本分片报告所有操作成功，该节点将向协调节点报告成功，协调节点将这些响应收集整理并返回给客户端。

### 倒排索引
分片是Elasticsearch最小的工作单元。但是究竟什么是一个分片，它是如何工作的？

传统的数据库每个字段存储单个值，但这对全文检索并不够。文本字段中的每个单词需要被搜索，对数据库意味着需要单个字段有索引多值的能力。最好的支持是一个字段多个值需求的数据结构是倒排索引。 

#### 倒排索引原理
Elasticsearch使用一种称为倒排索引的结构，它适用于快速的全文搜索。

见其名，知其意，有倒排索引，肯定会对应有正向索引。正向索引（forward index），反向索引（inverted index）更熟悉的名字是**倒排索引**。

所谓的**正向索引**，就是搜索引擎会将待搜索的文件都对应一个文件ID，搜索时将这个ID和搜索关键字进行对应，形成K-V对，然后对关键字进行统计计数。（统计？？下文有解释）
[](./assets/ElasticSearch.md/1657119981407.jpg)
但是互联网上收录在搜索引擎中的文档的数目是个天文数字，这样的索引结构根本无法满足实时返回排名结果的要求。所以，搜索引擎会将正向索引重新构建为倒排索引，即把文件ID对应到关键词的映射转换为关键词到文件ID的映射，每个关键词都对应着一系列的文件，这些文件中都出现这个关键词。
[](./assets/ElasticSearch.md/1657120032909.jpg))

#### 倒排索引的例子
一个倒排索引由文档中所有不重复词的列表构成，对于其中每个词，有一个包含它的文档列表。例如，假设我们有两个文档，每个文档的content域包含如下内容：
  * The quick brown fox jumped over the lazy dog
  * Quick brown foxes leap over lazy dogs in summer

为了创建倒排索引，我们首先将每个文档的content域拆分成单独的词（我们称它为词条或tokens )，创建一个包含所有不重复词条的排序列表，然后列出每个词条出现在哪个文档。结果如下所示：
[](assets/ElasticSearch.md/1657120146999.jpg)
现在，如果我们想搜索 `quick brown` ，我们只需要查找包含每个词条的文档：
[](./assets/ElasticSearch.md/1657120185675.jpg)
两个文档都匹配，但是第一个文档比第二个匹配度更高。如果我们使用仅计算匹配词条数量的简单相似性算法，那么我们可以说，对于我们查询的相关性来讲，第一个文档比第二个文档更佳。

但是，我们目前的倒排索引有一些问题：
  * Quick和quick以独立的词条出现，然而用户可能认为它们是相同的词。
  * fox和foxes非常相似，就像dog和dogs；他们有相同的词根。
  * jumped和leap，尽管没有相同的词根，但他们的意思很相近。他们是同义词。 

使用前面的索引搜索`+Quick`, `+fox`不会得到任何匹配文档。(记住，＋前缀表明这个词必须存在）。

只有同时出现Quick和fox 的文档才满足这个查询条件，但是第一个文档包含quick fox ，第二个文档包含Quick foxes 。

我们的用户可以合理的期望两个文档与查询匹配。我们可以做的更好。
如果我们将词条规范为标准模式，那么我们可以找到与用户搜索的词条不完全一致，但具有足够相关性的文档。例如：
  * Quick可以小写化为quick。
  * foxes可以词干提取变为词根的格式为fox。类似的，dogs可以为提取为dog。
  * jumped和leap是同义词，可以索引为相同的单词jump 。
现在索引看上去像这样：
[](./assets/ElasticSearch.md/1657120920487.jpg)
这还远远不够。我们搜索`+Quick` `+fox` 仍然会失败，因为在我们的索引中，已经没有`Quick`了。但是，如果我们对搜索的字符串使用与content域相同的标准化规则，会变成查询`+quick` `+fox`，这样两个文档都会匹配！分词和标准化的过程称为**分析**，这非常重要。你只能搜索在索引中出现的词条，所以索引文本和查询字符串必须标准化为相同的格式。

### 文档搜索
* 不可改变的倒排索引
    早期的全文检索会为整个文档集合建立一个很大的倒排索引并将其写入到磁盘。 一旦新的索引就绪，旧的就会被其替换，这样最近的变化便可以被检索到。

    倒排索引被写入磁盘后是不可改变的：它永远不会修改。
      * 不需要锁。如果你从来不更新索引，你就不需要担心多进程同时修改数据的问题。
      * 一旦索引被读入内核的文件系统缓存，便会留在哪里，由于其不变性。只要文件系统缓存中还有足够的空间，那么大部分读请求会直接请求内存，而不会命中磁盘。这提供了很大的性能提升。
      * 其它缓存(像filter缓存)，在索引的生命周期内始终有效。它们不需要在每次数据改变时被重建，因为数据不会变化。
      * 写入单个大的倒排索引允许数据被压缩，减少磁盘IO和需要被缓存到内存的索引的使用量。

    当然，一个不变的索引也有不好的地方。主要事实是它是不可变的! 你不能修改它。如果你需要让一个新的文档可被搜索，你需要重建整个索引。这要么对一个索引所能包含的数据量造成了很大的限制，要么对索引可被更新的频率造成了很大的限制。

* 动态更新索引
    如何在保留不变性的前提下实现倒排索引的更新？
    答案是：用更多的索引。通过增加新的补充索引来反映新近的修改，而不是直接重写整个倒排索引。每一个倒排索引都会被轮流查询到,从最早的开始查询完后再对结果进行合并。

    Elasticsearch基于Lucene，这个java库引入了**按段搜索**的概念。每一段本身都是一个倒排索引，但索引在 Lucene 中除表示所有段的集合外，还增加了提交点的概念—一个列出了所有已知段的文件。
    [](./assets/ElasticSearch.md/1657121243009.jpg)

    按段搜索会以如下流程执行：
    一. 新文档被收集到内存索引缓存。


