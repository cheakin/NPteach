# JVM
**基本概念**
JVM是可运行Java代码的假想计算机，包括一套指令集、一组寄存器、一个栈、一个垃圾回收、堆、一个存储方法域。JVMu是运行再操作系统只上的，它与硬件没有直接的交互。
JVM
    java代码执行
        代码编译为class，即javac
        装载class，即Classloader
        执行class
            解释执行
            编译执行
                client compiler
                server compiler
    内存管理
        内存空间
            方法区
            堆
            方法栈
            本地方法栈
            pc寄存器
        内存分配
            堆上分配
            TLAB分配
            栈上分配
        内存回收
            算法
                Copy
                Mark-Sweep
                Mark-Compact
            Sun JDK
                分代回收
                    新生代可用的GC
                        串行copying
                        并行回收copying
                        并行copying
                    Minor GC触发机制以及日志格式
                    旧生代可用的GC
                    Full GC 触发机制以及日志格式
                GC参数
                G1
        内存状况分析
            jconsle
            visualvm
            jmap
            MAT
    线程资源同步和交互机制
        线程资源同步
            线程资源执行机制
            线程资源同步机制
                Sunchronized的实现机制
                lock/unlock的实现机制
        线程交互机制
            Object.wait/notify/notifyAll - Double check pattem
            并发包提供的交互机制
                semaphore
                CountdownLatch
        线程状态即分析方法
            jstack
            TDA

**运行过程**
java源文件通过编译器，生产相应的.Class文件，也即是字节码文件，而字节码文件又通过Java虚拟机中的解释器，变异成特定机器上的机器码
    java源文件->编译码->字节码文件
    自建吗文件->JVM->机器码
每一种平台的解释器是不同的，但是现实现的虚拟机时相同的，这也就是Java为什么能够跨平台的原因了，当一个程序从开始运行，这是虚拟机就开始实例化了，多个程序就会存在多个虚拟机实例。程序退出或者关闭，则虚拟机实例消亡，多个虚拟机实例之间数据不能共享。

## 线程
这里所说的线程只程序执行过程中的一个线程实体。JVM允许一个应用并发执行多个线程。
Hotspot JVM中Java线程与原生操作系统线程又直接的映射关系。当线程本地存储、缓冲区分配、同步对象、站、程序计数器等准备好以后，就会创建一个操作系统远程线程。Java线程结束，远程线程随之被回收。操作系统负责调度所有线程，并把他们分配到任何可用的CPU上。当远程线程初始化完毕，就会调用Java线程的run()方法。当线程结束时，会释放远程线程和Java线程的所有资源。

## JVM内存
    线程私有 Thread Local
        程序技术器 PC
            指向虚拟机字节码指令的位置
            唯一一个无 OOM 的区域
        虚拟机栈 VM Stack
            虚拟机栈和线程生命周期相同
            一个线程中，没底啊用一个方法创建一个栈帧(Stack Fram)
            栈帧的结构
                本地变量表 Local Variable
                操作数栈 Operand Stack
                对运行时常量池的引用 Runtime Constant Pool Reference
            异常
                线程请求的栈深度大于JVM所允许的深度 StackOverflowError
                若JVM允许动态扩展，若无法盛情到足够内存 OutOfmemoryError
        本地方法栈 Native Method Stack
            异常
                线程请求的栈深度大于JVM所允许的深度 StackOverflowError
                若JVM允许动态扩展，若无法盛情到足够内存 OutOfmemoryError
    线程共享 Thread Shared
        方法去(永久代) Method Area
            运行时常量池Runtime Constant Pool
        类实例(java堆)Objects
            新生代
                eden
                from servivor
                to survivor
            老年代
            异常
                OutOfMemoryError
    直接内存 Direct Memory
        不受JVM GC管理

## JVM GC垃圾回收与算法
gc要做的三件事
    那些内存需要回收
    什么时候回收
    怎么回收
哪些对象已经“死亡”
    引用计数法Reference Counting——循环引用的问题
    根搜索算法GC Roots Tracing
        通过一系列成为GC Roots的点作为起点，向下搜索。当一个对象到任何GC Roots都没有引用链项链，说明其已经“死亡”
        GC Roots
            VM栈中的引用
            方法去中的静态引用
            JNI中的引用
垃圾收集算法
    标记清除Mark-Sweep
        效率低
        内存碎片多
    复制Coping
        1. eden
        2. survivor
    标记整理Mark-Compat
    分代收集Generational Collecting
**垃圾收集器**
    Serial
    ParNew
    Parallel Scavenge
    Serial Old
    Parallel Old
    CMS - Concurrent Mark Sweep
参数
    Xms
    Xmx
    Xmn
    -XX:+PrintGCDetails
    -XX:SurvivorRatio=8
    -XX:MaxTenuringThreshold
    -XX:-HandlePromotionFailure

## Java四种引用类型
强引用
    把一个对象赋给一个引用变量，这个引用变量就是一个强引 用。当一个对象被强引用变量引用时
软引用
    软引用需要用 SoftReference 类来实现
弱引用
虚引用
    软引用需要用 SoftReference 类来实现

### GC 分代收集算法 VS 分区收集算法
在新生代-复制算法
在老年代-标记整理算

## GC 垃圾收集器
Serial 垃圾收集器（单线程、复制算法）
    Serial（英文连续）是最基本垃圾收集器，使用复制算法
ParNew 垃圾收集器（Serial+多线程）
    是 Serial 收集器的多线程版本。很多 java 虚拟机运行在 Server 模式下新生代的默认垃圾收集器。
Parallel Scavenge 收集器（多线程复制算法、高效）
    它重点关注的是程序达到一个可控制的吞吐量。自适应调节策略也是 ParallelScavenge 收集器与 ParNew 收集器的一个 重要区别。
Serial Old 收集器（单线程标记整理算法 ）
    Serial Old 是 Serial 垃圾收集器年老代版本。运行在 Client 默认的 java 虚拟机默认的年老代垃圾收集器。
Parallel Old 收集器（多线程标记整理算法）
    Parallel Old 正是为了在年老代同样提供吞 吐量优先的垃圾收集器。
CMS 收集器（多线程标记清除算法）
    主要目标是获取最短垃圾 回收停顿时间。它使用多线程的标记-清除算法
G1 收集器
        G1 收集器避免全区域垃圾收集，它把堆内存划分为大小固定的几个独立区域。优先回收垃圾 最多的区域。

## JAVA IO/NIO
### Java IO
阻塞 IO 模型
    最传统的一种 IO 模型，即在读写数据过程中会发生阻塞现象。如果数据没有就 绪，就会一直阻塞在 read 方法
非阻塞 IO 模型
    在 while 循环中需要不断地去询问内核数据是否就 绪，这样会导致 CPU 占用率非常高
多路复用 IO 模型(目前使用得比较多的模型)
    会有一个线程不断去轮询多个 socket 的状态，只有当 socket 真正有读写事件时，才真 正调用实际的 IO 读写操作。
    另外多路复用 IO 为何比非阻塞 IO 模型的效率高是因为在非阻塞 IO 中，不断地询问 socket 状态 时通过用户线程去进行的，而在多路复用 IO 中，轮询每个 socket 状态是内核在进行的，这个效 率要比用户线程要高的多。
    不过要注意的是，多路复用 IO 模型是通过轮询的方式来检测是否有事件到达，并且对到达的事件 逐一进行响应。因此对于多路复用 IO 模型来说，一旦事件响应体很大，那么就会导致后续的事件 迟迟得不到处理，并且会影响新的事件轮询。
信号驱动 IO 模型
异步 IO 模型
    只需要先发起一个请求，当接收内核返回的成功信号时表示 IO 操作已经完成，可以直接 去使用数据了。

* java IO包
    * 字节流
        * InputSteam
            * ByteArrayInputStream
            * FileInputSteam
                * BufferdInputStream
                * DataInputStream
                * LineNumberInputStream
                * PushbackInputStream
            * FilterInputStream
            * ObjectInputStream
            * PipedInputStream
            * SequenceInputStream
            * StringBufferInputStream
        * OutputStream
            * ByteArrayOutputStream
            * FileOutputStream
            * FilterOutputStream
                * BufferedOutputStream
                * DataOutputStream
                * PrintStream
            * ObjectOutputStream
            * PipedOutputStream
    * 字符流
        * Reader
            * BufferedReader
                * LineNumberReader
            * CharArrayReader
            * FilterReader
                * PushbackReader
            * InputStreamReader
                * FileReader
            * PipedReader
            * StringReader
        * Writer
            * BufferedWriter
            * CharArrayWriter
            * FilterWriter
            * OutputStreamWriter
                * FileWriter
            * PipedWriter
            * PrintWriter
            * StringWriter

### Java NIO
NIO 基于 Channel 和 Buffer(缓冲区)进行操作，数据总是从通道读取到缓冲区 中，或者从缓冲区写入到通道中。
NIO 和传统 IO 之间第一个最大的区别是，IO 是面向流的，NIO 是面向缓冲区的。

Channel
    Channel 和 IO 中的 Stream(流)是差不多一个 等级的。
    只不过 Stream 是单向，而 Channel 是双向的。
Buffer
    缓冲区，实际上是一个容器，是一个连续数组。
Selector
    Selector 能够检测多个注册的通道上是否有事件发生，如果有事件发生，便获取事件然后针对每个事件进行相应的响应处理。

## JVM 类加载机制
JVM类加载机制分为五个部分：加载，(验证，准备，解析)，初始化

### 加载阶段
加载：这个阶段会在内存中生成一个代表这个类的 java.lang.Class 对 象，作为方法区这个类的各种数据的入口。*注意这里不一定非得要从一个 Class 文件获取，这里既 可以从 ZIP 包中读取（比如从 jar 包和 war 包中读取），也可以在运行时计算生成（动态代理）， 也可以由其它文件生成（比如将 JSP 文件转换成对应的 Class 类）。*

验证：确保 Class 文件的字节流中包含的信息是否符合当前虚拟机的要求。

准备：在方法区中分配这些变量所使 用的内存空间。

解析：虚拟机将常量池中的符号引用替换为直接引用的过程。

符号引用：引用的目标并不一定要已经加载到内存中。各种虚拟机实现的内存布局可以各不相同。

直接引用：是指向目标的指针，相对偏移量或是一个能间接定位到目标的句柄。引用的目标必定已经在内存中存在。

初始化

类构造器：是执行类构造器方法的过程。如果一个类中没有对静态变量赋值也没有静态语句块，那么编译 器可以不为这个类生成()方法。
### 类加载器
启动类加载器(Bootstrap ClassLoader)：负责加载 JAVA_HOME\lib 目录中的

扩展类加载器(Extension ClassLoader)：负责加载 JAVA_HOME\lib\ext 目录中的

应用程序类加载器(Application ClassLoader)：负责加载用户路径（classpath）上的类库

### 双亲委派
**当一个类收到了类加载请求，他首先不会尝试自己去加载这个类，而是把这个请求委派给父类去完成**，每一个层次类加载器都是如此，因此所有的加载请求都应该传送到启动类加载其中， 只有当**父类加载器反馈自己无法完成这个请求的时候**（在它的加载路径下没有找到所需加载的 Class），子类加载器才会尝试自己去加载。
采用双亲委派的一个好处是比如加载位于 rt.jar 包中的类 java.lang.Object，不管是哪加载器加载这个类，最终都是委托给顶层的启动类加载器进行加载，这样就保证了**使用不同的类加载器最终得到的都是同样一个 Object 对象。**
![[Pasted image 20230811231704.png]]

### OSGI（动态模型系统）
略

# JAVA 集合

## 接口继承关系和实现
集合类存放于 Java.util 包中，主要有 3 种：set(集）、list(列表包含 Queue）和 map(映射)。 
1. Collection：Collection 是集合 List、Set、Queue 的最基本的接口
2. Iterator：迭代器，可以通过迭代器遍历集合中的数据 
3. Map：是映射表的基础接口
![[Pasted image 20230813000849.png]]
集合框架
    Collection
        List
            ArrayList
                排列有序，可重复
                底层使用数组
                速度快，增删快，getter()和setter()方法快
                线程不安全
                当容量不够时，ArraryList时当前容量X1.5+1
            Vector
                排列有序，可重复
                子层使用数组
                速度快，增删慢
                线程安全，效率低
                当容量不够时，Vector默认扩展一倍容量
            LinkedList
                排列有序，可重复
                底层使用双向循环链表数据结构
                查询速度慢，增删快，add()和remove方法快
                线程不安全
        Set
            HashSet
                排列无需，不可重复
                底层使用Hash表实现
                存取速度快
                内部是HashMap
            TreeSet
                排列无需，不可重复
                底层使用二叉树实现
                排序存储
                内部是TreeMap的SortedSet
            LinkedHashSet
                采用hash表存储，并用双向链表记录插入顺序
                内部是LinkedHashMap
        Queue
            在两端出入的List，所以可以用数组或链表来实现
    Map
        HashMap
            键不可重复，值可重复
            底层哈希表
            线程不安全
            允许key值为null，value也可以为null
        HashTable
            键不可重复，值可重复
            底层hash表
            线程安全
            key、value都不允许为空
        TreeMap
            键不可重复，值可重复
            底层二叉树

## List
List 是有序的 Collection。Java List 一共三个实现类： 分别是 ArrayList、Vector 和 LinkedList。
![[Pasted image 20230813181854.png]]

### ArraryList(数组)
当数组大小不满足时需要增加存储能力，就要将已经有数 组的数据复制到新的存储空间中。当从 ArrayList 的中间位置插入或者删除元素时，需要对数组进 行复制、移动、代价比较高。因此，它适合随机查找和遍历，不适合插入和删除。
### Vector(数组实现、线程同步)
是它支持线程的同步，即某一时刻只有一 个线程能够写 Vector。
### LinkList(链表)
LinkedList 是用链表结构存储数据的，很适合数据的动态插入和删除。
## Set
值不能重复。如果想要让两个不同的对象视为相等的，就必须覆盖 Object 的 hashCode 方法和 equals 方 法
![[Pasted image 20230813182240.png]]

### HashSet(Hash表)
HashSet 首先判断两个元素的哈希值，如果哈希值一样，接着会比较 equals 方法 如果 equls 结果为 true ，HashSet 就视为同一个元素。如果 equals 为 false 就不是同一个元素。
一个 hashCode 位置上可以存放多个元素。所以值可以是多样的。

### TreeSet（二叉树）
TreeSet()是使用二叉树的原理对新 add()的对象按照指定的顺序排序（升序、降序），每增 加一个对象都会进行排序，将对象插入的二叉树指定的位置。
Integer 和 String 对象都可以进行默认的 TreeSet 排序，而自定义类的对象是不可以的，自 己定义的类必须实现 Comparable 接口，并且覆写相应的 compareTo() 函数，才可以正常使用。
### LinkedHashSet（HashSet+LinkedHashMap）
略

## Map
![[Pasted image 20230813182931.png]]
### HashMap（数组+链表+红黑树）
HashMap 根据键的 hashCode 值存储数据，大多数情况下可以直接定位到它的值，因而具有很快 的访问速度，但遍历顺序却是不确定的。 HashMap 最多只允许一条记录的键为 null，允许多条记 录的值为 null。HashMap 非线程安全，即任一时刻可以有多个线程同时写 HashMap，可能会导 致数据的不一致。如果需要满足线程安全，可以用 Collections 的 synchronizedMap 方法使 HashMap 具有线程安全的能力，或者使用 ConcurrentHashMap。
#### Java7 实现
大方向上，HashMap 里面是一个数组，然后数组中每个元素是一个单向链表。上图中，每个绿色 的实体是嵌套类 Entry 的实例，Entry 包含四个属性：key, value, hash 值和用于单向链表的 next。 
1. capacity：当前数组容量，始终保持 2^n，可以扩容，扩容后数组大小为当前的 2 倍。 
2. loadFactor：负载因子，默认为 0.75。
3. threshold：扩容的阈值，等于 capacity * loadFactor
![[Pasted image 20230813212422.png]]
#### Java8实现
最大的不同就是利用了红黑树，所以其由 数组+链表+红黑树 组成。
根据 Java7 HashMap 的介绍，我们知道，查找的时候，根据 hash 值我们能够快速定位到数组的 具体下标，但是之后的话，需要顺着链表一个个比较下去才能找到我们需要的，时间复杂度取决 于链表的长度，为 O(n)。为了降低这部分的开销，在 Java8 中，当链表中的元素超过了 8 个以后， 会将链表转换为红黑树，在这些位置进行查找的时候可以降低时间复杂度为 O(logN)。
![[Pasted image 20230813212436.png]]


### ConcurrentHashMap
![[Pasted image 20230813214044.png]]
#### Segment 段
ConcurrentHashMap 和 HashMap 思路是差不多的，但是因为它支持并发操作，所以要复杂一 些。整个 ConcurrentHashMap 由一个个 Segment 组成，Segment 代表”部分“或”一段“的 意思，所以很多地方都会将其描述为分段锁。注意，行文中，我很多地方用了“槽”来代表一个 segment。
#### 线程安全（Segment 继承 ReentrantLock 加锁）
简单理解就是，ConcurrentHashMap 是一个 Segment 数组，Segment 通过继承 ReentrantLock 来进行加锁，所以每次需要加锁的操作锁住的是一个 segment，这样只要保证每 个 Segment 是线程安全的，也就实现了全局的线程安全。

#### 并行度（默认 16）
concurrencyLevel：并行级别、并发数、Segment 数，怎么翻译不重要，理解它。默认是 16， 也就是说 ConcurrentHashMap 有 16 个 Segments，所以理论上，这个时候，最多可以同时支 持 16 个线程并发写，只要它们的操作分别分布在不同的 Segment 上。这个值可以在初始化的时 候设置为其他值，但是一旦初始化以后，它是不可以扩容的。再具体到每个 Segment 内部，其实 每个 Segment 很像之前介绍的 HashMap，不过它要保证线程安全，所以处理起来要麻烦些。
#### Java8 实现 （引入了红黑树）
![[Pasted image 20230813214155.png]]

### HashTable（线程安全）
Hashtable 是遗留类。Hashtable 不建议在新代码中使用，不需要线程安全 的场合可以用 HashMap 替换，需要线程安全的场合可以用 ConcurrentHashMap 替换。
### TreeMap（可排序）
TreeMap 实现 SortedMap 接口，能够把它保存的记录根据键排序，默认是按键值的升序排序， 也可以指定排序的比较器，当用 Iterator 遍历 TreeMap 时，得到的记录是排过序的。 如果使用排序的映射，建议使用 TreeMap。
*在使用 TreeMap 时，key 必须实现 Comparable 接口或者在构造 TreeMap 传入自定义的 Comparator，否则会在运行时抛出 java.lang.ClassCastException 类型的异常。*

### LinkHashMap（记录插入顺序）
*LinkedHashMap 是 HashMap 的一个子类，保存了记录的插入顺序，在用 Iterator 遍历 LinkedHashMap 时，先得到的记录肯定是先插入的，也可以在构造时带参数，按照访问次序排序*

# JAVA 多线程并发
## JAVA 线程实现/创建方式
### 继承 Thread 类
Thread 类本质上是实现了 Runnable 接口的一个实例，代表一个线程的实例。启动线程的唯一方 法就是通过 Thread 类的 start()实例方法。start() 方法是一个 native 方法，它将启动一个新线程，并执行 run() 方法。
``` java
public class MyThread extends Thread { 
    public void run() { 
        System.out.println("MyThread.run()"); 
    }
} 
MyThread myThread1 = new MyThread(); 
myThread1.start();
```
### 实现 Runnable 接口
如果自己的类已经 extends 另一个类，就无法直接 extends Thread，此时，可以实现一个 Runnable 接口。
``` java
public class MyThread extends OtherClass implements Runnable {
    public void run() { 
        System.out.println("MyThread.run()"); 
    } 
}

//启动 MyThread，需要首先实例化一个 Thread，并传入自己的 MyThread 实例： 
MyThread myThread = new MyThread(); 
Thread thread = new Thread(myThread); 
thread.start(); 

//事实上，当传入一个 Runnable target 参数给 Thread 后，Thread 的 run()方法就会调用 
target.run() 
public void run() { 
    if (target != null) { 
        target.run(); 
    } 
}
```

### ExecutorService、Callable、Future 有返回值线程
有返回值的任务必须实现 Callable 接口，类似的，无返回值的任务必须 Runnable 接口。执行 Callable 任务后，可以获取一个 Future 的对象，在该对象上调用 get 就可以获取到 Callable 任务返回的 Object 了，再结合线程池接口 ExecutorService 就可以实现传说中有返回结果的多线程 了。
``` java
//创建一个线程池 
ExecutorService pool = Executors.newFixedThreadPool(taskSize); 
// 创建多个有返回值的任务 
List list = new ArrayList(); 
for (int i = 0; i < taskSize; i++) { 
    Callable c = new MyCallable(i + " "); 
    // 执行任务并获取 Future 对象 
    Future f = pool.submit(c); 
    list.add(f); 
} 
// 关闭线程池 
pool.shutdown(); 
// 获取所有并发任务的运行结果 
for (Future f : list) { 
    // 从 Future 对象上获取任务的返回值，并输出到控制台 
    System.out.println("res：" + f.get().toString()); 
}
```
### 基于线程池的方式
线程和数据库连接这些资源都是非常宝贵的资源。那么每次需要的时候创建，不需要的时候销毁，是非常浪费资源的。那么我们就可以使用缓存的策略，也就是使用线程池。
``` java
// 创建线程池 
ExecutorService threadPool = Executors.newFixedThreadPool(10); 
while(true) { 
    threadPool.execute(new Runnable() { // 提交多个线程任务，并执行 
        @Override public void run() { 
            System.out.println(Thread.currentThread().getName() + " is running .."); 
            try { 
                Thread.sleep(3000); 
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            } 
        } 
    }); 
} 
```

## 4 种线程池
Java 里面线程池的顶级接口是 Executor，但是严格意义上讲 Executor 并不是一个线程池，而 只是一个执行线程的工具。真正的线程池接口是 ExecutorService。
![[Pasted image 20230815230105.png]]
### newCachedThreadPool
创建一个可根据需要创建新线程的线程池，但是在以前构造的线程可用时将重用它们。对于执行很多短期异步任务的程序而言，这些线程池通常可提高程序性能。**调用 execute 将重用以前构造 的线程（如果线程可用）。如果现有线程没有可用的，则创建一个新线程并添加到池中。终止并 从缓存中移除那些已有 60 秒钟未被使用的线程。** 因此，长时间保持空闲的线程池不会使用任何资源。
### newFixedThreadPoo
**创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程。** 在任意点，在大多数 nThreads 线程会处于处理任务的活动状态。如果在所有线程处于活动状态时提交附加任务， 则在有可用线程之前，附加任务将在队列中等待。如果在关闭前的执行期间由于失败而导致任何 线程终止，那么一个新线程将代替它执行后续的任务（如果需要）。在某个线程被显式地关闭之 前，池中的线程将一直存在。
### newScheduledThreadPool
创建一个线程池，它可安排在给定延迟后运行命令或者定期地执行。
``` java
ScheduledExecutorService scheduledThreadPool= Executors.newScheduledThreadPool(3); scheduledThreadPool.schedule(newRunnable(){ 
    @Override 
    public void run() { 
        System.out.println("延迟三秒"); 
    } 
}, 3, TimeUnit.SECONDS); 
scheduledThreadPool.scheduleAtFixedRate(newRunnable(){     
    @Override 
    public void run() {
        System.out.println("延迟 1 秒后每三秒执行一次"); 
    } 
},1,3,TimeUnit.SECONDS);
```
### newSingleThreadExecutor
Executors.newSingleThreadExecutor() 返回一个线程池（这个线程池只有一个线程）,**这个线程池可以在线程死后（或发生异常时）重新启动一个线程来替代原来的线程继续执行下去！** 

## 线程生命周期(状态)
当线程被创建并启动以后，它既不是一启动就进入了执行状态，也不是一直处于执行状态。 在线程的生命周期中，它要经过新建(New)、就绪（Runnable）、运行（Running）、阻塞 (Blocked)和死亡(Dead)5 种状态。尤其是当线程启动以后，它不可能一直"霸占"着 CPU 独自 运行，所以 CPU 需要在多条线程之间切换，于是线程状态也会多次在运行、阻塞之间切换。
![[Pasted image 20230815234514.png]]
* 新建状态（NEW）
    当程序使用 new 关键字创建了一个线程之后，该线程就处于新建状态，此时仅由 JVM 为其分配 内存，并初始化其成员变量的值
* 就绪状态（RUNNABLE）
    当线程对象调用了 start()方法之后，该线程处于就绪状态。Java 虚拟机会为其创建方法调用栈和 程序计数器，等待调度运行。
* 运行状态（RUNNING）![[Pasted image 20230815234449.png]]
    如果处于就绪状态的线程获得了 CPU，开始执行 run()方法的线程执行体，则该线程处于运行状态。
* 阻塞状态（BLOCKED）
    阻塞状态是指线程因为某种原因放弃了 cpu 使用权，也即让出了 cpu timeslice，暂时停止运行。 直到线程进入可运行(runnable)状态，才有机会再次获得 cpu timeslice 转到运行(running)状 态。阻塞的情况分三种：
    * 等待阻塞（o.wait->等待对列）：运行(running)的线程执行 o.wait()方法，JVM 会把该线程放入等待队列(waitting queue) 中。
    * 同步阻塞(lock->锁池) ：运行(running)的线程在获取对象的同步锁时，若该同步锁被别的线程占用，则 JVM 会把该线 程放入锁池(lock pool)中。
    * 其他阻塞(sleep/join) ：运行(running)的线程执行 Thread.sleep(long ms)或 t.join()方法，或者发出了 I/O 请求时， JVM 会把该线程置为阻塞状态。当 sleep()状态超时、join()等待线程终止或者超时、或者 I/O 处理完毕时，线程重新转入可运行(runnable)状态。
* 线程死亡（DEAD）
    线程会以下面三种方式结束，结束后就是死亡状态。
    * 正常结束：run() 或 call() 方法执行完成，线程正常结束。
    * 异常结束：线程抛出一个未捕获的 Exception 或 Error。
    * 调用 stop ：直接调用该线程的 stop() 方法来结束该线程—该方法通常容易导致死锁，不推荐使用。

## 终止线程 4 种方式
* 正常运行结束
    程序运行结束，线程自动结束。
* 使用退出标志退出线程
    一般 run()方法执行完，线程就会正常结束，然而，常常有些线程是伺服线程。**它们需要长时间的运行，只有在外部某些条件满足的情况下，才能关闭这些线程**。使用一个变量来控制循环，例如： 最直接的方法就是设一个 boolean 类型的标志，并通过设置这个标志为 true 或 false 来控制 while 循环是否退出，代码示例：
    ``` java
    public class ThreadSafe extends Thread { 
        public volatile boolean exit = false; public void run() { 
            while (!exit){ 
                //do something 
            } 
        } 
    }
    ```
    定义了一个退出标志 exit，当 exit 为 true 时，while 循环退出，exit 的默认值为 false.在定义 exit 时，**使用了一个 Java 关键字 volatile，这个关键字的目的是使 exit 同步**，也就是说在同一时刻只 能由一个线程来修改 exit 的值。
* Interrupt 方法结束线程
    使用 interrupt()方法来中断线程有两种情况：
    * 线程处于阻塞状态：如使用了 sleep,同步锁的 wait,socket 中的 receiver,accept 等方法时， 会使线程处于阻塞状态。当调用线程的 interrupt()方法时，会抛出 InterruptException 异常。 阻塞中的那个方法抛出这个异常，通过代码捕获该异常，然后 break 跳出循环状态，从而让 我们有机会结束这个线程的执行。**通常很多人认为只要调用 interrupt 方法线程就会结束，实 际上是错的， 一定要先捕获 InterruptedException 异常之后通过 break 来跳出循环，才能正 常结束 run 方法**。
    * 线程未处于阻塞状态：使用 isInterrupted()判断线程的中断标志来退出循环。当使用 interrupt()方法时，中断标志就会置 true，和使用自定义的标志来控制循环是一样的道理。
    ```java
        public class ThreadSafe extends Thread { 
            public void run() { 
                while (!isInterrupted()){ //非阻塞过程中通过判断中断标志来退出 
                    try{ 
                        Thread.sleep(5*1000);//阻塞过程捕获中断异常来退出 
                    }catch(InterruptedException e){ 
                        e.printStackTrace(); 
                        break;//捕获到异常之后，执行 break 跳出循环 
                    } 
                } 
            } 
        }
    ```
* stop 方法终止线程（线程不安全）
    程序中可以直接使用 thread.stop() 来强行终止线程，但是 stop 方法是很危险的，就象突然关 闭计算机电源，而不是按正常程序关机一样，可能会产生不可预料的结果，不安全主要是： thread.stop() 调用之后，创建子线程的线程就会抛出 ThreadDeatherror 的错误，并且会释放子 线程所持有的所有锁。一般任何进行加锁的代码块，都是为了保护数据的一致性，如果在**调用 thread.stop()后导致了该线程所持有的所有锁的突然释放(不可控制)**，那么被保护数据就有可能呈现不一致性，其他线程在使用这些被破坏的数据时，有可能导致一些很奇怪的应用程序错误。因 此，并不推荐使用 stop 方法来终止线程。

## sleep 与 wait 区别
1. 对于 sleep() 方法，我们首先要知道该方法是属于 Thread 类中的。而 wait() 方法，则是属于 Object 类中的。
2. sleep() 方法导致了程序暂停执行指定的时间，让出 cpu 该其他线程，但是他的监控状态依然 保持者，当指定的时间到了又会自动恢复运行状态。
3. 在调用 sleep() 方法的过程中，线程不会释放对象锁。
4. 而当调用 wait() 方法的时候，线程会放弃对象锁，进入等待此对象的等待锁定池，只有针对此对象调用 notify() 方法后本线程才进入对象锁定池准备获取对象锁进入运行状态。

## start 与 run 区别
1. start() 方法来启动线程，真正实现了多线程运行。这时无需等待 run 方法体代码执行完毕， 可以直接继续执行下面的代码。
2. 通过调用 Thread 类的 start()方法来启动一个线程， 这时此线程是处于就绪状态， 并没有运行。
3. 方法 run() 称为线程体，它包含了要执行的这个线程的内容，线程就进入了运行状态，开始运行 run 函数当中的代码。 Run 方法运行结束， 此线程终止。然后 CPU 再调度其它线程。

## JAVA 后台线程
1. 定义：守护线程--也称“服务线程”，他是后台线程，它有一个特性，即为**用户线程提供公共服务**，在没有用户线程可服务时会自动离开。
2. 优先级：守护线程的**优先级比较低**，用于为系统中的其它对象和线程提供服务。
3. 设置：通过 setDaemon(true) 来设置线程为“守护线程”；将一个用户线程设置为守护线程 的方式是在 线程对象创建 之前 用线程对象的 setDaemon 方法。
4. 在 Daemon 线程中产生的新线程也是 Daemon 的。
5. 线程则是 JVM 级别的，以 Tomcat 为例，如果你在 Web 应用中启动一个线程，这个线程的生命周期并不会和 Web 应用程序保持同步。也就是说，即使你停止了 Web 应用，这个线程 依旧是活跃的。
6. 生命周期：守护进程（Daemon）是运行在后台的一种特殊进程。它独立于控制终端并且周 期性地执行某种任务或等待处理某些发生的事件。也就是说守护线程不依赖于终端，但是依 赖于系统，与系统“同生共死”。当 JVM 中所有的线程都是守护线程的时候，JVM 就可以退 出了；如果还有一个或以上的非守护线程则 JVM 不会退出。

## JAVA 锁
### 乐观锁
乐观锁是一种乐观思想，即认为读多写少，遇到并发写的可能性低，每次去拿数据的时候都认为别人不会修改，所以不会上锁，但是**在更新的时候会判断一下在此期间别人有没有去更新这个数据，采取在写时先读出当前版本号，然后加锁操作**（比较跟上一次的版本号，如果一样则更新）， 如果失败则要重复读-比较-写的操作。
java 中的乐观锁基本都是通过 CAS 操作实现的，CAS 是一种更新的原子操作，**比较当前值跟传入值是否一样，一样则更新，否则失败**。
### 悲观锁
悲观锁是就是悲观思想，即认为写多，遇到并发写的可能性高，每次去拿数据的时候都认为别人 会修改，所以每次在读写数据的时候都会上锁，这样别人想读写这个数据就会 block 直到拿到锁。 java中的悲观锁就是**Synchronized**, AQS框架下的锁则是先尝试cas乐观锁去获取锁，获取不到， 才会转换为悲观锁，如 RetreenLock。
### 自旋锁
自旋锁原理非常简单，**如果持有锁的线程能在很短时间内释放锁资源，那么那些等待竞争锁的线程就不需要做内核态和用户态之间的切换进入阻塞挂起状态，它们只需要等一等（自旋）， 等持有锁的线程释放锁后即可立即获取锁，这样就避免用户线程和内核的切换的消耗**。 
线程自旋是需要消耗 cup 的，说白了就是让 cup 在做无用功，如果一直获取不到锁，那线程 也不能一直占用 cup 自旋做无用功，所以需要设定一个自旋等待的最大时间。 如果持有锁的线程执行的时间超过自旋等待的最大时间扔没有释放锁，就会导致其它争用锁的线程在最大等待时间内还是获取不到锁，这时争用线程会停止自旋进入阻塞状态。 
* 自旋锁的优缺点 
    自旋锁尽可能的减少线程的阻塞，这对于锁的竞争不激烈，且占用锁时间非常短的代码块来 说性能能大幅度的提升，因为自旋的消耗会小于线程阻塞挂起再唤醒的操作的消耗，这些操作会 导致线程发生两次上下文切换！ 
    但是如果锁的竞争激烈，或者持有锁的线程需要长时间占用锁执行同步块，这时候就不适合使用自旋锁了，因为自旋锁在获取锁前一直都是占用 cpu 做无用功，占着 XX 不 XX，同时有大量 线程在竞争一个锁，会导致获取锁的时间很长，线程自旋的消耗大于线程阻塞挂起操作的消耗， 其它需要 cup 的线程又不能获取到 cpu，造成 cpu 的浪费。所以这种情况下我们要关闭自旋锁； 
* 自旋锁时间阈值（1.6 引入了适应性自旋锁） 
    自旋锁的目的是为了占着 CPU 的资源不释放，等到获取到锁立即进行处理。但是如何去选择 自旋的执行时间呢？如果自旋执行时间太长，会有大量的线程处于自旋状态占用 CPU 资源，进而 会影响整体系统的性能。因此自旋的周期选的额外重要！
    JVM 对于自旋周期的选择，jdk1.5 这个限度是一定的写死的，在 1.6 引入了适应性自旋锁，适应性自旋锁意味着自旋的时间不在是固定的了，**而是由前一次在同一个锁上的自旋时间以及锁的拥有者的状态来决定**，基本认为一个线程上下文切换的时间是最佳的一个时间，同时 JVM 还针对当 前 CPU 的负荷情况做了较多的优化，如果平均负载小于 CPUs 则一直自旋，如果有超过(CPUs/2) 个线程正在自旋，则后来线程直接阻塞，如果正在自旋的线程发现 Owner 发生了变化则延迟自旋 时间（自旋计数）或进入阻塞，如果 CPU 处于节电模式则停止自旋，自旋时间的最坏情况是 CPU 的存储延迟（CPU A 存储了一个数据，到 CPU B 得知这个数据直接的时间差），自旋时会适当放 弃线程优先级之间的差异。 
* 自旋锁的开启 
    JDK1.6 中-XX:+UseSpinning 开启； 
    -XX:PreBlockSpin=10 为自旋次数； 
    JDK1.7 后，去掉此参数，由 jvm 控制；
### 其他锁
此处略，仅提及一下。
* Synchronized 同步锁
* ReentrantLock
* Semaphore 信号量
* AtomicInteger
* 可重入锁（递归锁）
* 公平锁与非公平锁
* ReadWriteLock 读写锁
* 共享锁和独占锁
* 重量级锁（Mutex Lock）
* 轻量级锁
* 偏向锁
* 分段锁
### 锁优化
* 减少锁持有时间
    只用在有线程安全要求的程序上加锁
* 减小锁粒度
    将大对象（这个对象可能会被很多线程访问），拆成小对象，大大增加并行度，降低锁竞争。 降低了锁的竞争，偏向锁，轻量级锁成功率才会提高。最最典型的减小锁粒度的案例就是 ConcurrentHashMap。
* 锁分离
    最常见的锁分离就是读写锁 ReadWriteLock，根据功能进行分离成读锁和写锁，这样读读不互 斥，读写互斥，写写互斥，即保证了线程安全，又提高了性能，具体也请查看[高并发 Java 五] JDK 并发包 1。读写分离思想可以延伸，只要操作互不影响，锁就可以分离。比如 LinkedBlockingQueue 从头部取出，从尾部放数据
* 锁粗化
    通常情况下，为了保证多线程间的有效并发，会要求每个线程持有锁的时间尽量短，即在使用完 公共资源后，应该立即释放锁。但是，凡事都有一个度，如果对同一个锁不停的进行请求、同步 和释放，其本身也会消耗系统宝贵的资源，反而不利于性能的优化 。
* 锁消除
    锁消除是在编译器级别的事情。在即时编译器时，如果发现不可能被共享的对象，则可以消除这 些对象的锁操作，多数是因为程序员编码不规范引起。

## 线程基本方法
程相关的基本方法有 wait，notify，notifyAll，sleep，join，yield 等。
![[Pasted image 20230816223352.png]]

此处略，仅提及一下。
* 线程等待（wait）
* 线程睡眠（sleep）
* 线程让步（yield）
* 线程中断（interrupt）
* Join 等待其他线程终止
* 为什么要用 join()方法？
* 线程唤醒（notify）
* 其他方法
    * sleep()：强迫一个线程睡眠Ｎ毫秒。 
    * isAlive()： 判断一个线程是否存活。
    * join()： 等待线程终止。 
    * activeCount()： 程序中活跃的线程数。 
    * enumerate()： 枚举程序中的线程。
    * currentThread()： 得到当前线程。
    * isDaemon()： 一个线程是否为守护线程。
    * setDaemon()： 设置一个线程为守护线程。(用户线程和守护线程的区别在于，是否等待主线 程依赖于主线程结束而结束) 
    * setName()： 为线程设置一个名称。
    * wait()： 强迫一个线程等待。 
    * notify()： 通知一个线程继续运行。
    * setPriority()： 设置一个线程的优先级。
    * getPriority():：获得一个线程的优先级。

## 线程上下文切换
巧妙地利用了时间片轮转的方式, CPU 给每个任务都服务一定的时间，然后把当前任务的状态保存 下来，在加载下一任务的状态后，继续服务下一任务，**任务的状态保存及再加载, 这段过程就叫做上下文切换**。时间片轮转的方式使多个任务在同一颗 CPU 上执行变成了可能。
![[Pasted image 20230816223904.png]]

此处略，仅提及一下。
* 进程
* 上下文
* 寄存器
* 程序计数器
* PCB-“切换桢”
* 上下文切换的活动
* 引起线程上下文切换的原因

## 同步锁与死锁
### 同步锁
当多个线程同时访问同一个数据时，很容易出现问题。为了避免这种情况出现，我们要**保证线程同步互斥，就是指并发执行的多个线程**，在同一时间内只允许一个线程访问共享数据。 Java 中可 以使用 synchronized 关键字来取得一个对象的同步锁。
### 死锁
就是多个线程同时被阻塞，它们中的一个或者全部都在等待某个资源被释放。
## 线程池原理
线程池做的工作主要是控制运行的线程的数量，处理过程中将任务放入队列，然后在线程创建后 启动这些任务，如果线程数量超过了最大数量**超出数量的线程排队等候**，等其它线程执行完毕， 再从队列中取出任务来执行。他的主要特点为：**线程复用；控制最大并发数；管理线程**。
### 线程复用
略
### 线程池的组成
略
### 拒绝策略
线程池中的线程已经用完了，无法继续为新任务服务，同时，等待队列也已经排满了，再也 塞不下新任务了。这时候我们就需要拒绝策略机制合理的处理这个问题。 JDK 内置的拒绝策略如下：
1. AbortPolicy ： 直接抛出异常，阻止系统正常运行。 
2. CallerRunsPolicy ： 只要线程池未关闭，该策略直接在调用者线程中，运行当前被丢弃的 任务。显然这样做不会真的丢弃任务，但是，任务提交线程的性能极有可能会急剧下降。 
3. DiscardOldestPolicy ： 丢弃最老的一个请求，也就是即将被执行的一个任务，并尝试再 次提交当前任务。 
4. DiscardPolicy ： 该策略默默地丢弃无法处理的任务，不予任何处理。如果允许任务丢 失，这是最好的一种方案。 
以上内置拒绝策略均实现了 RejectedExecutionHandler 接口，若以上策略仍无法满足实际需要，完全可以自己扩展 RejectedExecutionHandler 接口。
### Java 线程池工作过程
1. 线程池刚创建时，里面没有一个线程。任务队列是作为参数传进来的。不过，就算队列里面有任务，线程池也不会马上执行它们。 
2. 当调用 execute() 方法添加一个任务时，线程池会做如下判断： 
    * 如果正在运行的线程数量小于 corePoolSize，那么马上创建线程运行这个任务；
    * 如果正在运行的线程数量大于或等于 corePoolSize，那么将这个任务放入队列；
    * 如果这时候队列满了，而且正在运行的线程数量小于 maximumPoolSize，那么还是要创建非核心线程立刻运行这个任务；
    * 如果队列满了，而且正在运行的线程数量大于或等于 maximumPoolSize，那么线程池会抛出异常 RejectExecutionException。 
1. 当一个线程完成任务时，它会从队列中取下一个任务来执行。 
2. 当一个线程无事可做，超过一定的时间（keepAliveTime）时，线程池会判断，如果当前运行的线程数大于 corePoolSize，那么这个线程就被停掉。所以线程池的所有任务完成后，它最终会收缩到 corePoolSize 的大小。
![[Pasted image 20230817231321.png]]

## JAVA 阻塞队列原理
阻塞队列，关键字是阻塞，先理解阻塞的含义，在阻塞队列中，线程阻塞有这样的两种情况：
* 当队列中没有数据的情况下，消费者端的所有线程都会被自动阻塞（挂起），直到有数据放入队列。
* 当队列中填满数据的情况下，生产者端的所有线程都会被自动阻塞（挂起），直到队列中有空的位置，线程被自动唤醒。
### 阻塞队列的主要方法
* 抛出异常：抛出一个异常； 
* 特殊值：返回一个特殊值（null 或 false,视情况而定） 
* 阻塞：在成功操作之前，一直阻塞线程 
* 超时：放弃前只在最大的时间内阻塞
### 其他
略
## CyclicBarrier、CountDownLatch、Semaphore 的用法
略
## volatile 关键字的作用（变量可见性、禁止重排序）
Java 语言提供了一种稍弱的同步机制，即 volatile 变量，用来确保将变量的更新操作通知到其他 线程。volatile 变量具备两种特性，volatile 变量不会被缓存在寄存器或者对其他处理器不可见的 地方，因此在读取 volatile 类型的变量时总会返回最新写入的值。
变量可见性
    其一是保证该变量对所有线程可见，这里的可见性指的是当一个线程修改了变量的值，那么新的 值对于其他线程是可以立即获取的。
禁止重排序
    volatile 禁止了指令重排。
比 sychronized 更轻量级的同步
    在访问 volatile 变量时不会执行加锁操作，因此也就不会使执行线程阻塞，因此 volatile 变量是一 种比 sychronized 关键字更轻量级的同步机制。volatile 适合这种场景：一个变量被多个线程共 享，线程直接给这个变量赋值。

### 如何在两个线程之间共享数据
* 将数据抽象成一个类，并将数据的操作作为这个类的方法
* Runnable 对象作为一个类的内部类
### ThreadLocal 作用（线程本地存储）
ThreadLocal，很多地方叫做线程本地变量，也有些地方叫做线程本地存储，ThreadLocal 的作用 是提供线程内的局部变量，**这种变量在线程的生命周期内起作用，减少同一个线程内多个函数**或者组件之间一些**公共变量的传递的复杂度**。

使用场景
    最常见的 ThreadLocal 使用场景为 用来解决 数据库连接、Session 管理等。

## synchronized 和 ReentrantLock 的区别
两者的共同点：
    1. 都是用来协调多线程对共享对象、变量的访问 
    2. 都是可重入锁，同一线程可以多次获得同一个锁 
    3. 都保证了可见性和互斥性
两者的不同点：
    1. ReentrantLock 显示的获得、释放锁，synchronized 隐式获得释放锁 
    2. ReentrantLock 可响应中断、可轮回，synchronized 是不可以响应中断的，为处理锁的不可用性提供了更高的灵活性 
    3. ReentrantLock 是 API 级别的，synchronized 是 JVM 级别的 
    4. ReentrantLock 可以实现公平锁 
    5. ReentrantLock 通过 Condition 可以绑定多个条件 
    6. 底层实现不一样， synchronized 是同步阻塞，使用的是悲观并发策略，lock 是同步非阻 塞，采用的是乐观并发策略 
    7. Lock 是一个接口，而 synchronized 是 Java 中的关键字，synchronized 是内置的语言实现。 
    8. synchronized 在发生异常时，会自动释放线程占有的锁，因此不会导致死锁现象发生； 而 Lock 在发生异常时，如果没有主动通过 unLock()去释放锁，则很可能造成死锁现象， 因此使用 Lock 时需要在 finally 块中释放锁。 
    9. Lock 可以让等待锁的线程响应中断，而 synchronized 却不行，使用 synchronized 时， 等待的线程会一直等待下去，不能够响应中断。 
    10. 通过 Lock 可以知道有没有成功获取锁，而 synchronized 却无法办到。 
    11. Lock 可以提高多个线程进行读操作的效率，既就是实现读写锁等。

## ConcurrentHashMap 并发
### 减小锁粒度
减小锁粒度是指缩小锁定对象的范围，从而减小锁冲突的可能性，从而提高系统的并发能力。减 小锁粒度是一种削弱多线程锁竞争的有效手段，这种技术典型的应用是 ConcurrentHashMap(高 性能的 HashMap)类的实现。对于 HashMap 而言，最重要的两个方法是 get 与 set 方法，如果我 们对整个 HashMap 加锁，可以得到线程安全的对象，但是加锁粒度太大。**Segment 的大小也被 称为 ConcurrentHashMap 的并发度**。
### ConcurrentHashMap 分段锁
ConcurrentHashMap，它内部细分了若干个小的 HashMap，称之为段(Segment)。**默认情况下 一个 ConcurrentHashMap 被进一步细分为 16 个段，既就是锁的并发度**。 
如果需要在 ConcurrentHashMap 中添加一个新的表项，并不是将整个 HashMap 加锁，而是首 先根据 hashcode 得到该表项应该存放在哪个段中，然后对该段加锁，并完成 put 操作。在多线程 环境中，如果多个线程同时进行 put操作，只要被加入的表项不存放在同一个段中，则线程间可以 做到真正的并行。

ConcurrentHashMap 是由 Segment 数组结构和 HashEntry 数组结构组成
    ConcurrentHashMap 是由 Segment 数组结构和 HashEntry 数组结构组成。Segment 是一种可 重入锁 ReentrantLock，在 ConcurrentHashMap 里扮演锁的角色，HashEntry 则用于存储键值 对数据。一个 ConcurrentHashMap 里包含一个 Segment 数组，Segment 的结构和 HashMap 类似，是一种数组和链表结构， 一个 Segment 里包含一个 HashEntry 数组，每个 HashEntry 是 一个链表结构的元素， **每个 Segment 守护一个 HashEntry 数组里的元素,当对 HashEntry 数组的 数据进行修改时，必须首先获得它对应的 Segment 锁**。

## Java 中用到的线程调度
### 抢占式调度：
    抢占式调度指的是每条线程执行的时间、线程的切换都由系统控制，系统控制指的是在系统某种 运行机制下，可能每条线程都分同样的执行时间片，也可能是某些线程执行的时间片较长.
### 协同式调度：
    协同式调度指某一线程执行完后主动通知系统切换到另一线程上执行，这种模式就像接力赛一样
### 线程让出 cpu 的情况：
    1. 当前运行线程主动放弃 CPU，JVM 暂时放弃 CPU 操作（基于时间片轮转调度的 JVM 操作系 统不会让线程永久放弃 CPU，或者说放弃本次时间片的执行权），例如调用 yield()方法。 
    2. 当前运行线程因为某些原因进入阻塞状态，例如阻塞在 I/O 上。 
    3. 当前运行线程结束，即运行完 run()方法里面的任务。

## 进程调度算法
此处略，仅提及一下。
### 优先调度算法
1. 先来先服务调度算法（FCFS）
2. 短作业(进程)优先调度算法
### 高优先权优先调度算法
1. 非抢占式优先权算法
2. 抢占式优先权调度算法
3. 高响应比优先调度算法
### 基于时间片的轮转调度算法
1. 时间片轮转法
2. 多级反馈队列调度算法

## 什么是 CAS（比较并交换-乐观锁机制-锁自旋）
略

## 什么是 AQS（抽象的队列同步器）
略

# JAVA 基础
## JAVA 异常分类及处理
### 异常分类
Throwable
    Error(内存异常)
        AWTEroor
        ThreadDeath
    Exception
        CheckedException(受检异常, 编译器报错)
            SQLException
            IOException
            ClassNotFundException
            ......
        RuntimeExcetion(非受检异常, 运行时报错)
                NullPointerException
                ArithmeticException
                ClassCastException
                ArraryIndexOutOfBundsException
                ......

### 异常的处理方式
* 遇到问题不进行具体处理，而是继续抛给调用者 （throw,throws）
* try catch 捕获异常针对性处理方式

## JAVA 反射
### 动态语言
动态语言，是指程序在运行时可以改变其结构：新的函数可以引进，已有的函数可以被删除等结 构上的变化。比如常见的 JavaScript 就是动态语言，除此之外 Ruby,Python 等也属于动态语言， 而 C、C++则不属于动态语言。从反射角度说 JAVA 属于半动态语言。
### 反射机制概念 （运行状态中知道类所有的属性和方法）
在 Java 中的反射机制是指**在运行状态中，对于任意一个类都能够知道这个类所有的属性和方法； 并且对于任意一个对象，都能够调用它的任意一个方法**；这种动态获取信息以及动态调用对象方 法的功能成为 Java 语言的反射机制。
### 反射的应用场合
* 编译时类型和运行时类型
    ``` java
    Person p=new Student();
    ```
    其中编译时类型为 Person，运行时类型为 Student。
* 编译时类型无法获取具体方法
### Java 反射 API
反射 API 用来生成 JVM 中的类、接口或则对象的信息
    1. Class 类：反射的核心类，可以获取类的属性，方法等信息。 
    2. Field 类：Java.lang.reflec 包中的类，表示类的成员变量，可以用来获取和设置类之中的属性 值。 
    3. Method 类： Java.lang.reflec 包中的类，表示类的方法，它可以用来获取类中的方法信息或 者执行方法。 
    4. Constructor 类： Java.lang.reflec 包中的类，表示类的构造方法。
### 反射使用步骤（获取 Class 对象、调用对象方法）
1. 获取想要操作的类的 Class 对象，他是反射的核心，通过 Class 对象我们可以任意调用类的方 法。 
2. 调用 Class 类中的方法，既就是反射的使用阶段。 
3. 使用反射 API 来操作这些信息。
### 获取 Class 对象的 3 种方法
* 调用某个对象的 getClass()方法 
    ``` java
    Person p=new Person(); 
    Class clazz=p.getClass();
```
* 调用某个类的 class 属性来获取该类对应的 Class 对象
    ``` java
    Class clazz=Person.class; 
```
* 使用 Class 类中的 forName()静态方法(最安全/性能最好) 
    ``` java
    Class clazz=Class.forName("类的全路径"); 
```
当我们获得了想要操作的类的 Class 对象后，可以通过 Class 类中的方法获取并查看该类中的方法 和属性。
``` java
//获取 Person 类的 Class 对象 
Class clazz=Class.forName("reflection.Person"); 

//获取 Person 类的所有方法信息 
Method[] method=clazz.getDeclaredMethods(); 
for(Method m:method){ 
    System.out.println(m.toString()); 
} 

//获取 Person 类的所有成员属性信息 
Field[] field=clazz.getDeclaredFields(); 
for(Field f:field){ 
    System.out.println(f.toString()); 
} 

//获取 Person 类的所有构造方法信息 
Constructor[] constructor=clazz.getDeclaredConstructors(); 
for(Constructor c:constructor){ 
    System.out.println(c.toString()); 
}
```
### 创建对象的两种方法
* Class 对象的 newInstance()
* 调用 Constructor 对象的 newInstance()

## JAVA 注解
略
## JAVA 内部类
此处略，仅提及一下。
* 静态内部类
* 成员内部类
* 局部内部类（定义在方法中的类）
* 匿名内部类（要继承一个父类或者实现一个接口、直接使用 new 来生成一个对象的引用）
## JAVA 泛型
泛型提供了编译时类型安全检测机制，该机制允许程序员在编译时检测到非法的类型。泛型的本 质是参数化类型，也就是说所操作的数据类型被指定为一个参数。比如我们要写一个排序方法， 能够对整型数组、字符串数组甚至其他任何类型的数组进行排序，我们就可以使用 Java 泛型。
### 泛型方法 <\E>
你可以写一个泛型方法，该方法在调用时可以接收不同类型的参数。根据传递给泛型方法的参数 类型，编译器适当地处理每一个方法调用。
``` java
// 泛型方法 printArray 
public static <E> void printArray( E[] inputArray ) { 
    for ( E element : inputArray ){ 
        System.out.printf( "%s ", element ); 
    } 
}
```
1.  <\? extends T>表示该通配符所代表的类型是 T 类型的子类。 
2. <\? super T>表示该通配符所代表的类型是 T 类型的父类。
### 泛型类 <\T>
泛型类的声明和非泛型类的声明类似，除了在类名后面添加了类型参数声明部分。和泛型方法一 样，泛型类的类型参数声明部分也包含一个或多个类型参数，参数间用逗号隔开。一个泛型参数， 也被称为一个类型变量，是用于指定一个泛型类型名称的标识符。因为他们接受一个或多个参数， 这些类被称为参数化的类或参数化的类型。
``` java
public class Box<T> { 
    private T t; 
    public void add(T t) { 
        this.t = t; 
    } 
    public T get() { 
        return t; 
    } 
}
```
### 类型通配符 ?
类型通配符一般是使用 ? 代替具体的类型参数。例如 List 在逻辑上是 List,List 等所有 List<具体类型实参>的父类。
### 类型擦除
**Java 中的泛型基本上都是在编译器这个层次来实现的。在生成的 Java 字节代码中是不包含泛型中的类型信息的**。使用泛型的时候加上的类型参数，会被编译器在编译的时候去掉。这个 过程就称为类型擦除。如在代码中定义的 List和 List等类型，在编译之后 都会变成 List。JVM 看到的只是 List，而由泛型附加的类型信息对 JVM 来说是不可见的。 类型擦除的基本过程也比较简单，首先是找到用来替换类型参数的具体类。这个具体类一般 是 Object。如果指定了类型参数的上界的话，则使用这个上界。把代码中的类型参数都替换 成具体的类。
## JAVA 序列化(创建可复用的 Java 对象)
此处略，仅提及一下。
* 保存(持久化)对象及其状态到内存或者磁盘
* 序列化对象以字节数组保持-静态成员不保存
* 序列化用户远程对象传
* Serializable 实现序列
    * writeObject 和 readObject 自定义序列化策略
    * 序列化 ID
    * 序列化并不保存静态变量
* Transient 关键字阻止该变量被序列化到文件中
## JAVA 复制
将一个对象的引用复制给另外一个对象，一共有三种方式。第一种方式是直接赋值，第二种方式 是浅拷贝，第三种是深拷贝。所以大家知道了哈，这三种概念实际上都是为了拷贝对象。
### 直接赋值复制
### 浅复制（复制引用但不复制引用的对象）
创建一个新对象，然后将当前对象的非静态字段复制到该新对象，**如果字段是值类型的， 那么对该字段执行复制；如果该字段是引用类型的话，则复制引用但不复制引用的对象**。 
因此，原始对象及其副本引用同一个对象。
### 深复制（复制对象和其应用对象）
深拷贝不仅复制对象本身，而且复制对象包含的引用指向的所有对象。
### 序列化（深 clone 一中实现）
在 Java 语言里深复制一个对象，常常可以先使对象实现 Serializable 接口，然后把对 象（实际上只是对象的一个拷贝）写到一个流里，再从流里读出来，便可以重建对象。

# Spring 原理