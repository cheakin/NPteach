## Redis

### Linux下安装

1. 下载并解压

2. 默认安装在`/usr/local/redis`，以后台方式启动

   1. 修改安装目录下`redis.conf`配置文件

   2. 将次文件中的deamonize设置为yes

      ```daemonize yes```

   3. 在`/usr/local/redis/bin`目录下有启动文件，启动

      ```bash
      cd /usr/local/redis
      ./bin/redis-server ./redis.conf
      ```

   4. 启动redis-cli客户端

      ```bash
      redis-cli -p 6379
      或下面的命令连接redis
      /usr/local/redis/bin/redis-cli 
      ```

   5. `ps -ef|grep redis`查看redis是否启动成功

   6. 关闭redis

      ```b
      exit
      ```

### 基础命令

> Redis支持多线程，但默认是单线程的（多线程不一定比单线程快），redis是将数据放入内存中，多次读写都是在CPU中进行的！

> Redis对大小写不敏感

`select 0`，选择数据库，redis默认16个数据库(从0起)

`dbsize`，当前数据库keys的数量

`keys *`，查看当前数据库所有的key

`flushdb`，清空当前数据库

`flush all`，清空全部数据库

> Redis可以用作数据库、缓存和消息中间件.它支持多种类型的数据结构，如 [字符串（strings）](http://redis.cn/topics/data-types-intro.html#strings)， [散列（hashes）](http://redis.cn/topics/data-types-intro.html#hashes)， [列表（lists）](http://redis.cn/topics/data-types-intro.html#lists)， [集合（sets）](http://redis.cn/topics/data-types-intro.html#sets)， [有序集合（sorted sets）](http://redis.cn/topics/data-types-intro.html#sorted-sets) 与范围查询， [bitmaps](http://redis.cn/topics/data-types-intro.html#bitmaps)， [hyperloglogs](http://redis.cn/topics/data-types-intro.html#hyperloglogs) 和 [地理空间（geospatial）](http://redis.cn/commands/geoadd.html) 索引半径查询。 Redis 内置了 [复制（replication）](http://redis.cn/topics/replication.html)，[LUA脚本（Lua scripting）](http://redis.cn/commands/eval.html)， [LRU驱动事件（LRU eviction）](http://redis.cn/topics/lru-cache.html)，[事务（transactions）](http://redis.cn/topics/transactions.html) 和不同级别的 [磁盘持久化（persistence）](http://redis.cn/topics/persistence.html)， 并通过 [Redis哨兵（Sentinel）](http://redis.cn/topics/sentinel.html)和自动 [分区（Cluster）](http://redis.cn/topics/cluster-tutorial.html)提供高可用性（high availability）。

### Strings字符串

`set k1 v1`，将k1的值存储为v1
`get k1`，获取k1的值

`append k1 hello`，在k1后面拼接hello，如果k1不存在则会创建
`exists k1` ，是否存在k1，get k1的结果是v1hello
`strlength k1 `，获取k1的长度

set view 0
`incr view `，使view自增1；`incrby view 5` 使view自增5
`decr view `，是view自减；`decrby 2` 使view自减2

`getrange k1 0 3`，获取k1的0到3长度内容；`getrange k1 0 -1`,获取k1的所有内容k1
`setrange k1 3 xx`，将k1从3开始替换为xx

`setex(set with expire)`，设置过期时间
`setnx(set if not exist)`，不存在再设值

setex(set with expire)，设置过期时间
`setex k2 30 v2`，为k2设置30秒的存活时间并设值为v2
`ttl k2`，查看k2的剩余时间

setnx(set if not exist)，不存在时再设值；分布式锁常用
`setnx k2 v2`，当k2不存在时设值为v2，成功返回值，失败返回0

`mset k1 v2 k2 v2 k3 v3`，一次性设置多个值
`mget k1 k2 k3`，一次性获取多个值
`msetnx k1 v1 k4 v4`，同时设置多个值，具有原子性，失败则都不设值

`getset k1 vn`，先get再set；先获取k1再将k1设值为v1，如果存在，返回原来的值。*再获取k1时就已经时vn了*

**对象**
`set user:1 {name:zhangsan,age:3}`，设置一个user:1对象，值为json字符来存放
可以转换成多存值方式：`mset user:1:name zhangsan user:1:age 2`；
取值时使用多取值方式：`mget user:1:name user:1:age`

### List列表

> 列表允许有重复值

`lpush l1 one`向列表l1中新增值one；`lpush l1 two three`，向列表l1中新增多个值分别为two,three
`rpush four`向列表l1中右增值four；同理`rpush l1 five six`，向列表l1中右增多个值分别为five ,six

`lrange l1 0 2`获取列表l1的0到3长度内容；`lrange l1 0 -1`，查看列表l1的所有值

`lpop l1`将列表l1左移除一个元素
`rpop l1`将列表l1右移除一个元素

`lindex l1 1`，通过下标向获取列表l1的1处的值(从左，从0开始)。*没有rindex*

`llen l1`，获取列表l1的长度

`lrem l1 2 three`，将列表l1左移除2个值为three的元素(从左，从0开始)，返回移除成功数(有多少移除多少)。*没有rrem*

`ltrim l2 1 2`，通过下标截取列表l2的1到2处的值(从左，从0开始)，返回截取后的列表的长度

`lset l3 1 item`，更新在列表l3的下标1处设值(替换)为item（列表l3 不存在会报错，下标不存在会报错）

`linsert l3 before item2 item1`，向列表l3中值为item1的前面插入值item1
`linsert l3 after item2 item3`，向列表l3中值为item2的前面插入值item3

`rpoplpush l1 l2`，将列表l1的右移除一个元素，并将这个元素左新增到列表l2中

> 列表实际上是一个双向链表，before Node after，left，right都能够插入
>
> 如果list不存在，则创建内容
>
> 如果list存在，新增内容
>
> 如果移除了list或移除了所有的值，则空链表，表示不存在！
>
> 在两边插入或改动值，效率高！中间元素操作，效率小队低~
>
> 可以用作队列、栈！