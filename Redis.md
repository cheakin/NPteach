## Redis

> Redis可以用作数据库、缓存和消息中间件.它支持多种类型的数据结构，如 [字符串（strings）](http://redis.cn/topics/data-types-intro.html#strings)， [散列（hashes）](http://redis.cn/topics/data-types-intro.html#hashes)， [列表（lists）](http://redis.cn/topics/data-types-intro.html#lists)， [集合（sets）](http://redis.cn/topics/data-types-intro.html#sets)， [有序集合（sorted sets）](http://redis.cn/topics/data-types-intro.html#sorted-sets) 与范围查询， [bitmaps](http://redis.cn/topics/data-types-intro.html#bitmaps)， [hyperloglogs](http://redis.cn/topics/data-types-intro.html#hyperloglogs) 和 [地理空间（geospatial）](http://redis.cn/commands/geoadd.html) 索引半径查询。 Redis 内置了 [复制（replication）](http://redis.cn/topics/replication.html)，[LUA脚本（Lua scripting）](http://redis.cn/commands/eval.html)， [LRU驱动事件（LRU eviction）](http://redis.cn/topics/lru-cache.html)，[事务（transactions）](http://redis.cn/topics/transactions.html) 和不同级别的 [磁盘持久化（persistence）](http://redis.cn/topics/persistence.html)， 并通过 [Redis哨兵（Sentinel）](http://redis.cn/topics/sentinel.html)和自动 [分区（Cluster）](http://redis.cn/topics/cluster-tutorial.html)提供高可用性（high availability）。

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

> Redis对大小写不敏感;
>
> 使用tab键可以将命令自动补全

`select 0`，选择数据库，redis默认16个数据库(从0起)

`dbsize`，当前数据库keys的数量

`keys *`，查看当前数据库所有的key

`flushdb`，清空当前数据库

`flush all`，清空全部数据库



### String字符串

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

> llist是有序可重复集合

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



###  Set集合

>set是无序不重复集合

`sadd s1 hello1`，向集合s1中存入值hello1

`smemers s1` ，查看集合s1中的所有元素

`sismember s1 hello`，查看集合s1中是否存在元素hello；存在返回1，不存在返回0

`scard s1`，查看集合s1中存在多少的元素，返回元素数

`srem s1 hellon`，移除集合s1中值为hellon的元素

`srandmember s1 2`，获取集合s1中的2个随机值，个数的缺省值是1

`spop s1`，随机移除集合s1中的一个元素

`smove s1 s2 "hello2"`，将s1中值为hello2的元素移动到s2中，s2不存在则会新建

`sdiff s1 s2`，查看集合s1与集合s2的差集
`sinter s1 s2`，查看集合s1与集合s2的交集（共同好友）
`sunion s1 s2`，查看集合s1与集合s2的并集



### Hash散列表(Map)

> 可以看作是Java中的Map集合，key-Map！(map)

`hset h1 f1 v1`，向散列表h1中存入值为<f1,v1>的键值对
`hmset h1 f2 v2 f3 v3`，向散列表h1中存入多个值，分别为<f1,v1>，<f1,v1>的键值对

`hget h1 f1`，获取散列表h1指定键f1的值
`hgetall h1`，获取散列表h1中的所有键值对，按顺序一次返回键(key)和值(value)

`hdel h1 f1`，删除散列表h1指定键f1，相应的值也就没有了

`hlen h1`，查看散列表h1的长度

`hexist h1 f1`，查看散列表h1指定键f1是否存在；存在返回1，不存在返回0

`hkeys h1`，查看散列表h1所有的键
`hvals h1`，查看散列表h1所有的值（不包含键）

`hincreby h1 f1 1`，将散列表h1指定键f1的值自增1
`decreby h1 f1 1`，将散列表h1指定键f1的值自减1，也可以转化为`hincreby h1 f1 -1`

`hsetnx h1 f2 hello`，查看散列表h1指定键f2是否存在，不存在则设值为hello，存在则不操作

*没有hsetex命令*



### Zset(有序集合)

在set的基础上，增加了一个排序的值`set s1 v1`>>>>>`zset s1 score1 v1`

`zadd z1 1 one`，向集合z1中新增序号为1的值one
`zadd z1 1 one 2 two 3 three`， 向集合z1中新增多个值

`zrange z1 0 -1`，查询集合z1中的所有值

`zrangebyscore z1 -inf +inf`，将集合z1在负无穷到正无穷的范围(默认闭区间，可以用`(`、`)`标识为闭区间)内按**顺序**排序输出**值**
`zrevrangebyscore z1 +inf -inf`，将集合z1在负无穷到正无穷的范围(默认闭区间，可以用`(`、`)`标识为闭区间)内按**倒序**排序输出**值**
`zrangebyscore z1 -inf +inf withscore`将集合z1在负无穷到正无穷的范围内按顺序排序输出**值和顺序**

`zrem z1 one`，移除有序集合中的指定的值为one的元素

`zcard z1`，获取集合z1中的个数

`zcount z1 count 1 3`，获取集合z1中顺序在1到3之间的值(闭区间)



### Geospatial地理空间(Geo)

> 规则：两极无法直接添加，一般会下载城市数据，直接通过java程序一次性导入
>
> * 有效的经度从-180°到180°，有效的纬度从-85.051112878°到85.051112878°

命令 key 经度 纬度 名称

`geoadd g:city 121.48 31.22 v1`，给位置g:city添加一个坐标(121.48 31.22)并设值为v1
`geoadd g:city 102.72 25.05 v2`

`geopops g:city v1` ，获取指定位置的经纬度
`geopops g:city v1 v2`，获取几个指定位置的经纬度

`geodist g:city v1 v2 m`，计算两个位置间的距离（单位的缺省值是米(m)，可以是千米(km)、英里(mi)、英尺(ft)）

`georadius g:city 110 30 2000 km`，在位置集合g:city找出以(110 30)为中心半径1000km的坐标
`georadius g:city 110 30 2000 km withdist`，在位置集合g:city找出以(110 30)为中心半径1000km的坐标和距离
`georadius g:city 110 30 2000 km withcoord`，在位置集合g:city找出以(110 30)为中心半径1000km的坐标和经纬度
`georadius g:city 110 30 2000 km withdist withcoord count 1 `在位置集合g:city找出以(110 30)为中心半径1000km的坐标、距离、经纬度且只输出1个

`georadiusbymember g:city v2 3000 km`，在位置集合g:city找出以v2(已存在点)为中心半径3000km的坐标

`geohash g:city v1`，将位置集合g:city中位置v1的两位经度坐标哈希为一位的字符串
`geohash g:city v1 `v2，将位置集合中多个位置的经度坐标哈希为字符串

geo 的底层是使用zset，所以
可以使用`range g:city 0 -1`查看位置集合g:city的所有坐标
可以使用`zrem g:city v1` 移除位置集合 g:city的v1坐标



### Hyperloglogs

> 基数：一个集合中不重复元素的个数；
>
> ---->UV(unique visitor)，网站的独立访客
>
> * hyperloglogs存在%0.81的误差

UV虽然可以使用set(不重复集合)存id实现，但没必要(目的是计数而不是统计)；

`pfadd p1 a b c c d b c`新建一组元素p1值为a b c c d b c

`pfcount p1` 计算集合p1的基数

pfadd p2 d e d f
`pfmerge p3 p1 p2`，新建p1与p2的并集p3



### Bitmaps

>位图，位存储；操作而精致位来记录，只有两个状态
>
>eg：活跃和不活跃，登录和未登录，打卡和未打卡

使用bitmap来记录一周的打卡情况：周一1，周二1，周三0，周四0，周五1

```setbit sign 1 1
setbit sign 1 1
setbit sign 2 1
setbit sign 3 0
setbit sign 4 0
setbit sign 5 1
```

查看某一天是否打卡

```bash
 getbit sign 4
```

统计操作，统计打卡的天数

```bash
bitcount sign # 返回3
```



