# Redis

> Redis可以用作数据库、缓存和消息中间件.它支持多种类型的数据结构，如 [字符串（strings）](http://redis.cn/topics/data-types-intro.html#strings)， [散列（hashes）](http://redis.cn/topics/data-types-intro.html#hashes)， [列表（lists）](http://redis.cn/topics/data-types-intro.html#lists)， [集合（sets）](http://redis.cn/topics/data-types-intro.html#sets)， [有序集合（sorted sets）](http://redis.cn/topics/data-types-intro.html#sorted-sets) 与范围查询， [bitmaps](http://redis.cn/topics/data-types-intro.html#bitmaps)， [hyperloglogs](http://redis.cn/topics/data-types-intro.html#hyperloglogs) 和 [地理空间（geospatial）](http://redis.cn/commands/geoadd.html) 索引半径查询。 Redis 内置了 [复制（replication）](http://redis.cn/topics/replication.html)，[LUA脚本（Lua scripting）](http://redis.cn/commands/eval.html)， [LRU驱动事件（LRU eviction）](http://redis.cn/topics/lru-cache.html)，[事务（transactions）](http://redis.cn/topics/transactions.html) 和不同级别的 [磁盘持久化（persistence）](http://redis.cn/topics/persistence.html)， 并通过 [Redis哨兵（Sentinel）](http://redis.cn/topics/sentinel.html)和自动 [分区（Cluster）](http://redis.cn/topics/cluster-tutorial.html)提供高可用性（high availability）。

## 安装

### **Linux下安装**

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

## Redis基础

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



### 事务

> 事务时一组命令的集合一个事务中的所有命令都会被序列化。在事务执行过程中，会按照顺序执行！
> 一次性，顺序性，排他性；执行一系列的命令！
> * Redis事务没有隔离级别的概念！
> * Redis单条命令具有原子性，但事务不保证原子性；

1. 正常执行事务；`multi`、`...`、`exec`
   **开启事务(multi)**：表示开启事务
   **命令入队(...)**：将命令入队，此时命令并没有执行
   **执行事务(exec)**：依次按顺序执行命令
   ```bash
   127.0.0.1:6379> multi
   OK
   127.0.0.1:6379> set k1 v1
   QUEUED
   127.0.0.1:6379> set k2 v2
   QUEUED
   127.0.0.1:6379> get k2
   QUEUED
   127.0.0.1:6379> set k3 v3
   QUEUED
   127.0.0.1:6379> exec
   1) OK
   2) OK
   3) "v2"
   4) OK
   ```

2. 放弃事务；`multi`、`...`、`discard`
   ```ba
   127.0.0.1:6379> multi
   OK
   127.0.0.1:6379> set k1 v11
   QUEUED
   127.0.0.1:6379> set k2 v22
   QUEUED
   127.0.0.1:6379> discard
   OK
   127.0.0.1:6379> keys *
   1) "k2"
   2) "k1"
   3) "k3"
   
   ```

3. 编译异常；所有命令都不会执行
   ```ba
   127.0.0.1:6379> multi
   OK
   127.0.0.1:6379> set k1 v11
   QUEUED
   127.0.0.1:6379> set k1 v22
   QUEUED
   127.0.0.1:6379> getset k3	#错误的命令
   (error) ERR wrong number of arguments for 'getset' command
   127.0.0.1:6379> set k4 v4
   QUEUED
   127.0.0.1:6379> exec	#所有的命令都不会执行
   (error) EXECABORT Transaction discarded because of previous errors.
   127.0.0.1:6379> keys *
   1) "k2"
   2) "k1"
   3) "k3"
   ```

4. 运行时异常；错误命令报异常，其他命令会正常运行
   ```ba
   127.0.0.1:6379> multi
   OK
   127.0.0.1:6379> incr k1
   QUEUED
   127.0.0.1:6379> set n1 1
   QUEUED
   127.0.0.1:6379> incr n1
   QUEUED
   127.0.0.1:6379> exec
   1) (error) ERR value is not an integer or out of range
   2) OK
   3) (integer) 2
   127.0.0.1:6379> keys *
   1) "k2"
   2) "k1"
   3) "k3"
   4) "n1"
   127.0.0.1:6379> get n1
   "2"
   ```

### 监控(锁)
* 悲观锁：认为什么售后都会出问题，无论做什么都会加锁
* 乐观锁：认为什么时候都不会出问题，所以不会上锁，更新数据的时候去判断一下，在此期间此数据是否被修改。执行前获取version，执行后比较 version

**监测**
1. 加锁；`watch`
```bash
127.0.0.1:6379> set money 100
OK
127.0.0.1:6379> set cost 0
OK
127.0.0.1:6379> watch money		#加锁，监视money
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> decreby money 10
(error) ERR unknown command `decreby`, with args beginning with: `money`, `10`, 
127.0.0.1:6379> decrby money 10
QUEUED
127.0.0.1:6379> incrby cost 10
QUEUED
127.0.0.1:6379> exec	#执行前倘若监视的值被修改了，则事务执行失败
(nil)
127.0.0.1:6379> mget money cost
1) "100"
2) "0"
```

2. 解锁；`unwatch`
```ba
127.0.0.1:6379> unwatch		#如果发现事务执行失败，就先解锁
OK
127.0.0.1:6379> watch money		#解锁后获取新的值，再次加锁
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> decrby money 10
QUEUED
127.0.0.1:6379> incrby cost 10
QUEUED
127.0.0.1:6379> exec	#比较监视的值是否被修改，没有被修改则执行成功
1) (integer) 90
2) (integer) 10
```

> *`exec`和`discard`都会自动解锁*


## Redis进阶

### Jedis
>Redis官方推荐的java连接工具

1. 导入依赖
   ```xml
   <dependencies>
       <!--jedis-->
       <dependency>
           <groupId>redis.clients</groupId>
           <artifactId>jedis</artifactId>
           <version>3.3.0</version>
       </dependency>
       <!--fastjson-->
       <dependency>
           <groupId>com.alibaba</groupId>
           <artifactId>fastjson</artifactId>
           <version>1.2.75</version>
       </dependency>
   </dependencies>
   ```

2. 编码测试
   ```java
   public class TestPing {
       public static void main(String[] args) {
           // 1.new Jedis对象即可
           Jedis jedis = new Jedis("127.0.0.1", 6379);
           
           //redis所有的指令就是jedis对象的方法
           System.out.println(jedis.ping());   //输出PONG
   
   
           JSONObject jsonObject = new JSONObject();
           jsonObject.put("hello", "world");
           jsonObject.put("name", "zhangsan");
           //事务
           Transaction multi = jedis.multi();
           String result = jsonObject.toJSONString();
           try {
               multi.set("user1",  result);
               multi.set("user2", result);
   
               multi.exec();   //执行事务
           } catch (Exception e) {
               multi.discard();    //放弃事务
               e.printStackTrace();
           } finally {
               System.out.println(jedis.get("user1"));
               System.out.println(jedis.get("user2"));
               jedis.close();
           }
       }
   }
   ```


### SpringBoot集成Redis
>说明：SpingBoot2.X之后，Jedis替换为lettuce了
>
>- ​	jedis采用直连方式，多线程操作不安全；要避免这个情况就需要使用jedis pool连接池
>
>- ​	lettuce采用netty，实例可以在多个线程中共享，就不存在线程不安全的情况，可以减少线程数量

源码分析`RedisAutoConfiguration`
```java
@Bean
@ConditionalOnMissingBean(	//当Bean不存在时，此类生效
    name = {"redisTemplate"}
)
@ConditionalOnSingleCandidate(RedisConnectionFactory.class)
public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    //默认的RedisTemplate没有过多的设值，redis对象都是需要序列化！
    //	此处以两个Object类型接收，使用时需强制转换
    RedisTemplate<Object, Object> template = new RedisTemplate();
    template.setConnectionFactory(redisConnectionFactory);
    return template;
}

@Bean
@ConditionalOnMissingBean
@ConditionalOnSingleCandidate(RedisConnectionFactory.class)
//由于String类型较为常用，所有提出一个StringRedisTemplate
public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    StringRedisTemplate template = new StringRedisTemplate();
    template.setConnectionFactory(redisConnectionFactory);
    return template;
}
```

1. 引入依赖，或在创建项目时勾选NoSQL中的Redis
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
   ```

2. 编写配置文件，如主机地址、端口、密码等（默认使用lettuce，所以默认应配置lettuce的连接池）

3. 测试。自动注入`redistemplate`，然后使用`opsForxxx()`方法设置操作的数据类型之后就可以进行增删改查了。

   ```java
   @Autowired
   RedisTemplate redisTemplate;
   
   @Test
   void contextLoads() {
       //RedisTemplate
       //可以获取redis连接对象
       RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
       connection.flushDb();       //清空当前数据库
       connection.flushAll();      //清空所有数据库
   
   
   
       //redisTemplate.opsForValue()   操作字符串
       //redisTemplate.opsForList()    操作List
       //redisTemplate.opsForSet()     操作Set
       //.....     再链式编程继续写就可以了
       redisTemplate.opsForValue().set("k1","v1");
       System.out.println(redisTemplate.opsForValue().get("k1"));
   }
   ```

4. 可以编写配置类来修改template的一些配置
   > 首先准备一个实体类并实现序列化接口`implements Serializable`(若没有实例化，在redis时会报未序列化的错)，此处以User{name:xxx,age:xx}实体对象为例

   **修改redis的序列化方式**，*查看redisTemplte源码可以知道template默认以jdk的方式序列化*。

   当我们在用默认序列化方式时，我们在存入redis前和在从redis取出后打印，都能够正常的展示处对象，但我们在redis中查看子对象的值却时乱码的情况。

   ```java
   @Configuration
   public class RedisConfig {
       //此处可以参照RedisAutoConfiguration类
       @Bean
       public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
           RedisTemplate<String, Object> template = new RedisTemplate();
   
           //配置具体的序列化方式
           Jackson2JsonRedisSerializer<Object> objectJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
           template.setKeySerializer(objectJackson2JsonRedisSerializer);
   
           StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
           template.setKeySerializer(stringRedisSerializer);       //设置字符串key的序列化方式
           template.setHashKeySerializer(stringRedisSerializer);    //设置Hash key的序列化方式
           //......
   
           template.setValueSerializer(objectJackson2JsonRedisSerializer);     //设置字符串value的序列化方式为jackson方式
           template.setValueSerializer(objectJackson2JsonRedisSerializer);      //设置Hash value的序列化方式为jackson方式
           //......
   
           return template;
       }
   }
   
   ```

   *在自动注入式可能会有RedisTemplate歧义的情况出现，使用`@Qualifier("redisTemplate")`注解解决*

   此时查看redis中数据的值就不会式乱码了

   > 在真实的开发中，使用`redisTemplate`方式操作数据库还是很慢，所以一般公司都有自己分装的`RedisUtil`工具类。



### Redis配置(Redis.config)

#### 1. unit对大小写不敏感
```bash
# Note on units: when memory size is needed, it is possible to specify
# it in the usual form of 1k 5GB 4M and so forth:
#
# 1k => 1000 bytes
# 1kb => 1024 bytes
# 1m => 1000000 bytes
# 1mb => 1024*1024 bytes
# 1g => 1000000000 bytes
# 1gb => 1024*1024*1024 bytes
#
# units are case insensitive so 1GB 1Gb 1gB are all the same.
```

#### 2. INCLUDES，其他配置文件
```bash
# Include one or more other config files here.  This is useful if you
# have a standard template that goes to all Redis servers but also need
# to customize a few per-server settings.  Include files can include
# other files, so use this wisely.
#
# Notice option "include" won't be rewritten by command "CONFIG REWRITE"
# from admin or Redis Sentinel. Since Redis always uses the last processed
# line as value of a configuration directive, you'd better put includes
# at the beginning of this file to avoid overwriting config change at runtime.
#
# If instead you are interested in using includes to override configuration
# options, it is better to use include as the last line.
#
# include /path/to/local.conf
# include /path/to/other.conf
# include /path/to/local.conf
# include /path/to/other.conf
```

#### 3. 网络
```bash
bind 127.0.0.1		#绑定一个或多个ip
protected-mode yes	#保护模式
port 6379		#端口设置

```

#### 4. GENERAL，通用配置
```bash
daemonize yes	#以守护方式(后台)运行,默认式no
supervised no	#开机自启动
pidfile /var/run/redis_6379.pid	#当以后台方式运行，则需要指定pid文件！

# Specify the server verbosity level.
# This can be one of:
# debug (a lot of information, useful for development/testing)
# verbose (many rarely useful info, but not a mess like the debug level)
# notice (moderately verbose, what you want in production probably)
# warning (only very important / critical messages are logged)
loglevel notice		#日志级别，
logfile ""		#日志的文件文件名，为空则以标准输出

databases 16	#数据库数量，默认16个，使用时从0开始

always-show-logo yes	#运行redis时是否展示logo，默认开启
```

#### 5. SNAPSHOTTING，快照；
在规定时间内，执行了多少次操作，则会持久化到文件中，`.rdb`和`.aof`文件

*redis是内存数据库，没有持久化的话，断电即失*

```bash
save 900 1		#如果900s内，如果至少有1个key进行了操作，就进行持久化操作
save 300 10		#如果300s内，如果至少有10个key进行了操作，就进行持久化操作
save 60 10000	#如果60s内，如果至少有10000个key进行了操作，就进行持久化操作(并发大)

stop-writes-on-bgsave-error yes		#持久化如果出错了，时候还需要继续工作，默认yes

rdbcompression yes		#是否压缩.rdb文件，会消耗cpu资源
rdbchecksum yes			#保存.rdb文件时，是否错误校验
dbfilename dump.rdb		#.rdb文件名
dir ./					#.rdb文件保存目录，默认当前文件
```

#### 6. REPLICATION，主从复制。

见下面详解

#### 7. SECURITY，安全

也可以使用命令，通过`config set requirepass "123456"`给redis数据库设置密码
*连接数据库时使用`auth 123456`来验证*

```bash
requirepass 123456		#设置数据库密码，默认为空
```

#### **8. CLIENTS，限制(*)**

```bash
maxclients 10000	#设置能连接redis数据的最大客户端数，默认不设置（不限制）
maxmemory <bytes>	#redis配置最大的内存容量

maxmemory-policy noeviction		#内存达到上限时的触发策略,默认报错
	# volatile-lru -> 指对设置了过期时间的key进行lru操作
	# allkeys-lru -> 删除lru算法的key
	# volatile-random -> 随机删除一些即将过期的key
	# allkeys-random -> 随机删除
	# volatile-ttl -> 移除一些过期的key	
	# noeviction -> 不过期，报错
```

#### 9. APPEND ONLY MODE，aof模式配置

参见下面详解
```bash
appendonly no	#默认不开启aof模式，默认使用rdb方式持久化
appendfilename "appendonly.aof"	#aof持久化时文件名
no-appendfsync-on-rewrite no	#是否以重写文件的方式记录

appendfsync everysec	#aof方式同步模式
	# always -> 每次修改都会同步，消耗性能
	# everysec -> 每秒同步依次，可能会丢失宕机该秒的数据
	# no -> 不同步
```



### Redis持久化

>redis是内存数据库，没有持久化的话，断电即失。两种持久化方式不同，可同时开启

#### RDB(Redis Database)方式

> RDB持久化是在指定的时间间隔内将内从中的数据集快照写入磁盘，在恢复时将快照文件读取到内存中。默认以RDB方式保存，默认rdb文件是`dump.rdb`。在主从复制中，rdb用于备用
>
> 优点
>
> * 适合大规模的数据恢复！dump.rdb
>
> 缺点
>
> * 对数据的完整性不高，需要一定时间的间隔操作，保存不了宕机时未持久化的数据
>
> * fork进程时会占用一定的内存空间

Redis会单独创建(fork)一个子进程来进行持久化，会先将数据写入到一个临时文件中，待持久化过程都结束了，再用这个临时文件替换上次持久化好的文件，整个过程中，主进程不进行任何IO操作。这就确保了极高的性能。如果需要进行大规模数据的恢复，且对于数据恢复的完整性不是非常敏感，那RDB方式比AOF方式更高效，RDB方式的缺点是在进行最后依次持久化时的数据可能会丢失。

##### 1. 持久化触发机制

* 配置文件中的save规则满足的情况下，会自动触发rdb
* 执行`fluashall`命令也会触发rdb规则
* redis关机时也会产生rdb文件

##### **2. 恢复rdb文件**

将rdb文件放到redis启动目录下即可，redis启动时会自动加载文件中的数据。

可以通过`config get dir`获取当前数据库的路径



#### AOF(Append Only File)方式

> AOF是将所有执行的写命令记录下来，history，恢复时将这个文件全不指令在执行一遍。AOF并不是默认的持久化方式，所以要使用需要在配置中打开，默认的aof文件是`appendonly.aof`。
>
> 优点
>
> * 每一次修改都会同步，文件的完整性好（always）
> * 每秒同步一次，，可能会丢失1秒的数据（everysec）
> * 从不同步，效率最高（no）
>
> 缺点
>
> * 相对于数据文件来说，aof远远大于rdb，修复的速度也比rdb慢
> * 读写操作多，所以aof运行效率也比rdb慢

以日志的形式记录每个写操作，将Redis执行过的所有指令记录下来（读操作不记录），只允许追加文件但不可以改写文件，redis启动时会读取该文件重修构建数据。换言之，redis重启的话就根据日志文件的内容将指令从前到后以完成数据的恢复工作。

##### 开启aof方式持久化

在配置文件中将`appendonly no`改为`appendonly yes`，**重启redis后生效**

*当aof文件有错误(被篡改)，这时redis是启动不起来的，我们需要修复此文件*

```bash
appendonly no	#默认不开启aof模式，默认使用rdb方式持久化
appendfilename "appendonly.aof"	#aof持久化时文件名
no-appendfsync-on-rewrite no	#是否以重写文件的方式记录
auto-aof-rewrite-percentage 100	#aof文件重写规则，超过阈值(百分比)的限制重写
auto-aof-rewrite-min-size 64mb	#aof文件重写规则，aof文件大于64mb重新fork一个进程写入新的文件

appendfsync everysec	#aof方式同步模式
	# always -> 每次修改都会同步，消耗性能
	# everysec -> 每秒同步依次，可能会丢失宕机该秒的数据
	# no -> 不同步
```

##### aof文件修复

redis提供了aof文件修复工具`redis-check-aof --fix`(默认在redsi的根目录下)。

当aof文件损坏时，以此工具修复aof文件即可，它会将aof文件中有错误的命令删除，相应的会丢失该条数据

```bash
redis-check-aof --fix appendonly.aof
```



##### 两种方式对比

>1、RDB持久化方式能够在指定的时间间隔内对你的数据进行快照存储！
>
>2、AOF持久化方式记录每次对服务器写的操作,当服务器重启的时候会重新执行这些命令来恢复原始的数据, AOF命令以Redis协议追加保存每次写的操作到文件未尾. Redis还能对AOF文件进行后台重写.使得AOF文件的体积不至于过大。
>3、只做缓存,如果你只希望你的数据在服务器运行的时候存在,你也可以不使用任何持久化
>4、同时开启两种持久化方式
>
>* 在这种情况下,当redis重启的时候会优先载入AOF文件来恢复原始的数据,因为在通常情况下AOF文件保存的数据集要比RDB文件保存的数据集要完整。
>  务
>* RDB 的数据不实时,同时使用两者时服务器重启也只会找AOF文件 ,那要不要 只使用AOF呢?作者建议不要 ,因为RDB更适合用于备份数据库( AOF在不断变化不好备份) , 快速重启,而且不会有AOF可能潜在的Bug ,留着作为一个万一-的手段。
>
>5.性能建议
>
>* 因为RDB文件只用作后备用途,建议只在Slave上持久化RDB文件,而且只要15分钟备份一次就够了,只保留save 900 1这条规则。
>* 如果Enable AOF ,好处是在最恶劣情况下也只会失不超过两秒数据,启动脚本较简单只load自己的AOF文件就可以了,代价一是带来了持续的I0 ,二是AOF rewrite的最后将rewrite过程中产生的新数据写到新文件造成的阻塞几乎是不可避免的。只要硬盘许可,应该尽量减少AOF rewrite的频率, AOF重写的基础大小默认值64M太小了,可以设到5G以上,默认超过原大小100%大小重写可以改到适当的数值。
>* 如果不Enable AOF , 仅靠Master-Slave Repllcation实现高可用性也可以,能省掉一大笔I0 ,也减少了rewrite时带来的系统波动。代价是如果Master/Slave同时倒掉,会丢失十几分钟的数据,启动脚本也要比较两个Master/Slave中的RDB文件,载入较新的那个,微博就是这种架构。



### Redis发布订阅

> Redis发布订阅(pub/sub)是一种消息通信模式：发送者(pub)、订阅者(sub)接收消息。Redis客户端可以订阅人任意数量的频道。
>
> 消息发布者、频道（队列）、消息订阅者；eg:公众号发布文章----->微信----->用户接收订阅的消息

##### **命令**

`PSUBSCRIBE [patternpattern ...]`订阅一个或多个符合给定模式的频道。

`PUBSUB subcommand [argument [argument ...]]`查看订阅与发布系统状态。

`PUNSUBSCRIBE [pattern [pattern ...]]`将信息发送到指定的频道。

```bash
 PUBLISH bilibili "hello"	#向频道bilibili发布消息“hello",订阅此频道的订阅者会收到此消息
```

`PUNSUBSCRIBE [pattern [pattern ...]]`退订所有给定模式的频道。

`SUBSCRIBE channel [channel ...]`订阅给定的一个或多个频道的信息。

```bash
subscribe bilibili	#订阅频道
```

 `UNSUBSCRIBE [channel [channel ...]]`指退订给定的频道。

##### 应用场景

* 实时消息系统（注册即订阅系统消息）
* 实时聊天室（频道当作聊天室，将消息回显给所有人）

*复杂的场景我们会使用消息中间件(MQ)实现*



### Redis主从复制

主从复制,是指将一-台Redis服务器的数据,复制到其他的Redis服务器。前者称为主节点(master/leader),后者称为从节点(slavefollower) ;数据的复制是单向的,只能由主节点到从节点。Master以写为主, Slave 以读为主。
默认情况下,每台Redis服务器都是主节点;且-一个主节点可以有多个从节点(或没有从节点) ,但-个从节点只能有一-个主节点。

主从复制的作用主要包括:

1. 数据冗余：主从复制实现了数据的热备份,是持久化之外的- -种数据冗余方式。
2. 故障恢复：当主节点出现问题时,可以由从节点提供服务,实现快速的故障恢复;实际上是-种服务的冗余。
3. 负载均衡：在主从复制的基础上,配合读写分离,可以由主节点提供写服务,由从节点提供读服务(即写Redis数据时应用连接主节点,读Redis数据时应用连接从节点) , 分担服务器负载;尤其是在写少读多的场景下,通过多个从节点分担读负载,可以大大提高Redis服务器的并发量。
4. 高可用(集群)基石：除了上述作用以外，主从复制还是哨兵和集群能够实施的基础,因此说主从复制是Redis高可用的基础。

* 一般来说,要将Redis运用于工程项目中,只使用-台Redis是万万不能的,原因如下:
  1.从结构上,单个Redis服务器会发生单点故障,并且一台服务器需要处理所有的请求负载,压力较大;
  2.从容量上,单个Redis服务器内存容量有限,就算一台Redis服务器内存容量为256G ,也不能将所有内存用作Redis存储内存，
* 一般来说 ,单台Redis最大使用内存不应该超过20G。
  电商网站上的商品, -般都是一次上传,无数次浏览的,说专业点也就是“多读少写"。
  对于这种场景,我们可以使如下这种架构:

> 主从复制，读写分离！80%的情况下都是在进行都操作！可以有效减缓服务器的压力！架构中经常使用！
>
> 实际生产中普遍都是集群模式，单机很容易达到瓶颈

#### 搭建集群(配置)环境

只配置从库，不配置主库(默认启动就是主库)

`info replication`，查看当前库的信息

```bash
127.0.0.1:6379> info replication	#查看当前库的信息
# Replication
role:master		#角色：master
connected_slaves:0	#从机数：0
master_repl_offset:0
repl_backlog_active:0
repl_backlog_size:1048576
repl_backlog_first_byte_offset:0
repl_backlog_histlen:0	
```

这里我们在同一台机器上开1个主机和2个从机演示。**以不同的配置文件启动多个redis（修改端口、启动pid、logfile文件名、.rdf文件名）**

#### 主从配置

在默认方式下启动，每个redis都是主节点。从机------->主机<-------从机，此时就需要配置从机的所属主机

* 命令方式配置
  `slaveof 127.0.0.1 6379`配置次从机的所属主机
* 配置文件配置
  主机只能写，从机只能读。主机的所有信息和数据都会被从机自动保存（从机写会报错）
  当前集群，当主机宕机了，从机并不会变成主机，主机重新上线后继续同步数据；当从机宕机（若是以命令方式配置的主机，重启会变成主机，配置为从机后同步**全量复制**数据，之后的数据后会继续使用**增量复制**）

**宕机后手动配置主机**
从机也能当做主机（此时任然不能写）主机<------从机<-------从机。 若第一台主机宕机了，可以将次机变成主机slaveof no one（需手动命令配置）。若第一台主机恢复，第二台主机也需要手动变为从机。

### 哨兵模式

概述
主从切换技术的方法是：当主服务器宕机后，需要手动把-台从服务器切换为主服务器,这就需要人工干预,费事费力,还会造成一段时间内服务不可用。这不是一种推荐的方式,更多时候,我们优先考虑哨兵模式。Redis从2.8开始正式提供了Sentinel (哨兵)架构来解决这个问题。
谋朝篡位的自动版,能够后台监控主机是否故障，如果故障了根据投票数**自动将从库转换为主库**。

哨兵模式是一种特殊的模式，首先Redis提供了哨兵的命令，哨兵是-一个独立的进程，作为进程,它会独立运行。其原理是哨兵通过发送心跳，等待Redis服务器响应。从而监控运行的多个Redis实例。

这里的哨兵有两个作用

* 通过发送命令,让Redis服务器返回监控其运行状态,包括主服务器和从服务器。
* 当哨兵监测到master宕机,会自动将slave切换成master ,然后通过发布订阅模式通知其他的从服务器修改配置文件 ,让它们切换主机。

然而一个哨兵进程对Redis服务器进行监控,可能会出现问题,为此,我们可以使用多个哨兵进行监控。各个哨兵之间还会进行监控,这样就形成了多哨兵模式。

假设主服务器宕机,哨兵1先检测到这个结果,系统并不会马上进行failover(选举)过程,仅仅是哨兵1主观的认为主服务器不可用,这个现象成为**主观下线**。当后面的哨兵也检测到主服务器不可用,并且数量达到一定值时,那么哨兵之间就会进行一次投票,投票的结果由一个哨兵发起,进行failover[故障转移]操作。切换成功后,就会通过发布订阅模式,让各个哨兵把自己监控的从服务器实现切换主机，这个过程称为**客观下线**。

#### 哨兵配置(sentinel.conf)

`sentinel monitor myredis 127.0.0.1 6379 1`->`sentinel monitor 被监控名称 host port 选举模式`，后面的1表示主句挂了，slave将投票选举主机

#### 启动哨兵

`redis-sentinel /usr/local/redis/sentinel.conf`，以配置启动哨兵

启用哨兵模式后，若主机宕机，则会自动心跳、投票，会从从机中选举出一个新的主机。此时宕机了的主机重新上线，会归并到新的主机下变为从机。

> 优点
>
> * 哨兵集群，基于主从复制模式，继承所有主从配置的优点
> * 主从切换，故障发生后可转移，系统可用性更好
> * 哨兵模式就是主从模式的自动化
>
> 缺点
>
> * Redis不方便扩容，集群容量一旦达到上限，在线扩容十分麻烦
> * 实现哨兵模式的配置其实很麻烦

#### 哨兵模式的全部配置

```bash
#哨兵sentinel实例运行的端口，默认26379。多哨兵需要多配置文件（多端口等）
prot 26379

#哨兵sentine1的工作目录
dir /tmp

#哨兵sentine1监控的redis主节点的ip port
# master-name 可以自己命名的主节点名字只能由字时A-z.数字0-9、这三个字符”.-。“组成。
# quorum 配置多少个sentine1哨兵统- -认为master主节点失联那么这时客观上认为主节点失联了
# sentine1 monitor cmaster-name> <ip> <redis-port> cquorum>
sentine1 monitor mymaster 127.0.0.1 6379 2

#当在Redis实例中开启了requirepass foobared 授权密码这样所有连接Redis实例的客户端都要提供密码
#设置哨兵sentine1 连接主从的密码注意必须为主从设置一样的验证密码
# sentinel auth-pass cmaster-name> <password>
sentine1 auth-pass mymaster MySUPER--secret-0123passwOrd

#指定多少毫秒之后主节点没有应答哨兵sentine1 此时哨兵主观上认为主节点下线默认30秒
# sentinel down-after-milliseconds cmaster-name> <mi 1liseconds>
sentinel down-after-mi 11iseconds mymaster 30000

#这个配置项指定了在发生failover主备切换时最多可以有多少个slave间时对新的master进行同步，
#这个数字越小，完成failover所需的时间就越长，
#但是如果这个数字越大，就意味着越多的slave因为replication而不可用。
#可以通过将这个值设为1来保证每次只有一个slave处于不能处理命令请求的状态。
# sentine1 paralle1-syncs cmaster-name> cnumslaves>
sentine1 paralle1-syncs mymaster 1
#故障转移的超时时间failover-timeout 可以用在以下这些方面:
#1.网一个sentine1对同一个master两次fai lover之间的问隔时间。
#2.当一个slave从 一个错误的master那里同步数据开始计算时间。直到slave被纠正 为向正确的master那里同步数据时。
#3.当想要取消一个正在进行的failover所需要的时间。
#4.当进行failover时，配置所有slaves指向新的master所需的最大时间。不过，即使过了这个超时，slaves依然 会被正确配置为指
master.但是就不按paralle1-syncs所配置的规则来 了
#默认三分钟
# sentine1 failover-timeout cmaster-name> cmi11iseconds>
sentine1 failover-timeout mymaster 180000
I
# SCRIPTS EXECUTION
#配置当某- - 事件发生时所需要执行的脚本，可以通过脚本来通知管理员，例如当系统运行不正常时发邮件通知相关人员。
#对于脚本的运行结果有以下规则:
#若脚本执行后返回1，那么该脚本稍后将会被再次执行，重复次数目前默认为10
#若脚本执行后返回2，或者比2更高的一个返回值。脚本将不会重复执行。
#如果脚本在执行过程中由于收到系统中断信号被终止了，则问返回值为时的行为相间。
#一个脚本的最大执行时间为:60s.如果超过这个时间，脚本将会被- -个SIGKILL信号终止，之后重新执行。

#通知型脚本:当sentine1有任何警告级别的事件发生时(比如说redis实例的主观失效和客观失效等等)，将会去调用这个脚本，这时这个脚本应该通过邮件，SMS等方式去通知系统管理员关于系统不正常运行的信息。调用该脚木时，将传给脚本两个参数，一个是事件的类型，一个是事件的描述。如果sentinel.conf配置文件中配置了这个脚木路径，那么必须保证这个脚本存在于这个路径，井且是可执行的，否则sentine1无法正常启动成功。
#通知脚本(shell编程)
# sentinel notification-script cmaster-name> <scriptfpath>
sentine1 notification-script mymaster /var/redis/notify.sh

#客户端重新配置主节点参数脚本
#当一个master由于failover而发生改变时，这个脚本将会被调用，通如相关的客户端关于master地址已经发生改变的信息。
#以下参数将会在调用脚本时传给脚本:
# <master-name> <role> <state> <from-ip> <from-port> <to-ip> <to-port>
#目前<state> 总是“failover"，
# <role> 是"1eader"或者“observer"中的一个。
#参数from-ip, from-port, to-ip, to-port是用来和旧的master和新的master(即旧的slave)通信的
#这个脚本应该是通用的，能被多次调用，不是针对性的。
# sentinel client-reconfig-script cmaster-name> <script-path>
#这个脚本应该是通用的，能被多次调用，不是针对性的。
# sentinel client-reconfig-script <master-name> <script-path>
sentine1 client-reconfig-script mymaster /var/redis/reconfig.sh		#一般是由运维来配置
```



### **Redis缓存穿透和雪崩*****

#### 缓存穿透

> 概念(查不到导致)

缓存穿透的概念很简单。用户想要查询一个数据时，发现redis内存数据库没有（缓存没有命中），于是想持久层数据库查询。发现也没有，于是本次查询失败。当用户很多并且都没有命中的时候，于是都去请求了持久层数据库。着会给持久层数据库造成很大的压力了，这种情况就被描述为缓存穿透。

> 解决方案

* 布隆过滤器(BloomFilter)

  布隆过滤器是一种数据结构，对所有可能的查询参数以hash形式存储，在控制层先进行校验，不符合则丢弃，从而大大减小了对底层存储系统的查询压力。

* 缓存空对象

  当存储层不命中后，即使返回的是空对象仍然将其缓存起来，同时设置一个过期时间，之后再访问这个数据是将会从缓存总获取，保存了后端数据源。但这种方法存在两个问题

  * 如果空值能被缓存起来，着就以为着缓存需要更多的坤见存储键值，因为其中可能会有很多空值的键；
  * 即使对空值设置了过期时间，还是会存在缓存层和存储层的数据会有一段时间窗口不一致，和对于需要高一致性的业务会有影响。



#### 缓存击穿

> 概述(量太大和缓存过期导致)

这里需要注意缓存击穿和缓存穿透的区别。缓存击穿，是指一个key非常热点，在不停是扛着高并发，大并发集中对这一个点进行访问，当这个key在失效的瞬间，持续的大并发就会穿破缓存，直接请求持久层数据库，就像屏障被击穿了。

当某个key在过期的瞬间，有大量的并发访问，这类数据一般是热点数据，由于缓存过期，会同时访问数据库来查询最新的数据，并回写到缓存，会导致数据库压瞬间压力过大。

> 解决方案

* 设置热点数据永不过期

  从缓存层面来看，没有设置过期时间，所以不会出现热点key过期后产生的问题。并不能完全避免，缓存数据过多时也换删除部分缓存

* 加互斥锁

  * 分布式锁：使用分布式锁，保证对于每个key同时只有一个线程去查询后端服务，其他线程没有获得分布式锁的权限，因此只需要等待即可。这种方式将高并发的压力转移到了分布式锁，因此对分布式锁的压力考验很大



#### 缓存雪崩

> 概念

缓存雪崩，是指在某一个时间段，缓存集中过期失效。Redis宕机！缓存击穿是点，缓存雪崩是面

产生雪崩的原因之一 ,比如在写本文的时候,马上就要到双十二零点,很快就会迎来-波抢购,这波商品时间比较集中的放入了缓存,假设缓存一个小时。那么到了凌晨- -点钟的时候,这批商品的缓存就都过期了。而对这批商品的访问查询,都落到了数据库上,对于数据库而言,就会产生周期性的压力波峰。于是所有的请求都会达到存储层,存储层的调用量会暴增,造成存储层也会挂掉的情况。

> 解决方案

* redis高可用

  这个思想的含义是，de 既然redis有可能宕机，那就多增设几台redis，这样有一台宕机后其他还可以继续工作，即集群（入异地多活）

* 限流降级

  这个解决方案的思想是，在缓存失效后，通过加锁或者队列来控制读数据库和写缓存的线程数量(甚至停用部分服务)。比如对某个key只允许一个线程查询数据和写缓存，其他线程需等待。

* 数据预热

  数据预热的含义就是在正式部署前，先把可能的数据预先访问存到缓存种，这样部分可能大访问的数据就会加载到缓存中。即在将发生大访问前手动触发加载缓存不同的key，设置不同的过期时间，让缓存失效的时间尽量均匀一些。





>此笔记来由观看[Redis最新超详细版教程通俗易懂](https://www.bilibili.com/video/BV1S54y1R7SB)所记录。---2020/12/07

