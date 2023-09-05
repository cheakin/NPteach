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
## Spring 特点
Spring 特点
    轻量
        从大小与开销两方面而言 Spring 都是轻量的。完整的Spring框架可以在一个大小只有 1M 多的Jar 文件里发布，并且 Spring 所需要的开销也是微不足道的。
        此外，Spring 是非入侵式的、经典的，Spring 应用中的对象不依赖于 Spring 的特定类。
    控制反转
        Spring 通过一种称作控制反转 IOC 的技术促进了低耦合。
        当应用了 IOC ，一个对象依赖的其他对象会通过被动的方式传递进来，而不是这个对象自己创建或者查找依赖对象。
    面向切面
        Spring 支持面向切面的编程，并且把应用业务逻辑和系统服务分开。
    容器
        Spring 包含并管理应用对象的配置和生命周期，在这个意义上它是一种容器，你可以配置你的每个 bean 如何被创建——基于一个可配置原型，你的 bean 可以创建一个单独的实例或者每次需要时都生成一个新的实例——以及他们是如何关联的。
    框架
        Spring可以将简单的组件配置、组合成为复杂的应用。
        在 Spring 中，应用对象被声明式地组合，经典的是在一个 XML 文件里。
        Spring 也提供了很多基础功能（事务管理、持久化框架集成等），将应用逻辑的开发留给开发者。
## Spring 核心组件
![[Pasted image 20230820234236.png]]
## Spring 常用模块
Spring 常用模块
    核心容器
        核心容器提供 Spring 框架的基本功能，核心容器的主要组件时 BeanFactory ，它是工厂模式的实现。BeanFactory 使用控制反转（IOC）模式将应用程序的配置和依赖性规范与实际的应用程序代码分开。
    Spring 上下文
        Spring 上下文是一个配置问价，向 Spring 框架提供上下文信息。Spring 上下文包括企业服务，例如：JNDI、EJB、电子邮件、国际化、校验和调度功能。
    Spring AOP
        通过配置管理特性，Spring AOP 模块直接将面向切面的编程功能集成到了 Spring 框架中，可以将一些通用任务，如安全、事务、日志集中进行管理，提高了复用性和管理的便捷性。
    Spring DAO
        为 JDBC DAO 抽象层提供了有意义的异常层级结构，可用改结构来管理异常处理和不同数据库供应商爆出的错误消息。异常层次结构简化了错误处理，并且极大地降低了需要编写的异常代码数量（例如打开和关闭链接）。Spring DAO 的面向 JDBC 的异常遵从通用的 DAO异常层次结构。
    Spring ORM
        Spring 框架出入了若干个 ORM 框架，从而提供了 ORM 的对象关系工具，其中包括 JDO、Hibernate 和 iBatis SQL Map。所有这些都遵从 Spring 的通用事务和 DAO 异常层次结构。
    Spring Web 模块
        Web 上下文模块建立在应用程序上下文模块之上，为基于 Web 的应用程序提供了上下文。所以，Spring 框架支持与 Jakarta Struts 的集成。Web 模块还简化了处理多部份请求以及将请求参数绑定到域对象的工作。
    Spring MVC 框架
        MVC 框架是一个全功能的构建Web应用程序的 MVC 实现。通过策略接口，MVC 框架编程为高度可配置的，MVC 容纳了大量试图技术，其中包括 JSP、Velocity、Tiles、iText 和 POI 。

## Spring 主要包
略
## Spring 常用注解
bean 注入与装配的的方式有很多种，可以通过 xml，get set 方式，构造函数或者注解等。简单易 用的方式就是使用 Spring 的注解了，Spring 提供了大量的注解方式。

详情，略（已掌握）

## Spring 第三方结合
第三方框架集成
    权限
        shiro
            java的一个安全框架
            认证、授权、加密、会话管理、与Web集成、缓存
    缓存
        Echache
            是一个纯JAVA的进程缓存框架，具有快速、精干等特点，是Hibernate中默认的CacheProvider
        redis
            一个开源的使用ANSI C语言编写、支持网络、可基于内存一个持久化的日志型、Key-Value数据库
    持久层框架
        Hibernate
            一个开放源代码的对象关系映射框架，他对JDBC进行了非常轻量的对象封装，他将POJO数据库表建立映射关系，是一个全自动的ORM框架
        Mybatis
            是支持普通SQL查询，存储过程和高级映射的优秀持久层框架
    定时任务
        quartz
            一个开源的作业调度框架，由java编写，在.NET平台为Quartz.Net，通过Quart可以快速完成任务调度工作
        Spring-Task
            可以将它比作一个轻量级的Quartz，而且使用起来很简单，除spring相关的包外不需要额外的包，而且支持注解和配置文件两种形式
    校验框架
        Hibernate validator
            Hibernate Validator经常用来验证bean的字段，基于注解，方便快捷高效
        Oval
            OVal是一个可扩展的Java对象数据验证框架，验证的规则可以通过配置文件、Annotation、POJO进行设定。可以使用纯Java语言、JavaScript、Groovy、BeanShell等进行规则的编写

## Spring IOC 原理
### 概念
Spring 通过一个配置文件描述 Bean 及 Bean 之间的依赖关系，利用 Java 语言的反射功能实例化 Bean 并建立 Bean 之间的依赖关系。 Spring 的 IOC 容器在完成这些底层工作的基础上，还提供 了 Bean 实例缓存、生命周期管理、 Bean 实例代理、事件发布、资源装载等高级服务。
### Spring 容器高层视图
Spring 启动时读取应用程序提供的 Bean 配置信息，并在 Spring 容器中生成一份相应的 Bean 配 置注册表，然后根据这张注册表实例化 Bean，装配好 Bean 之间的依赖关系，为上层应用提供准 备就绪的运行环境。其中 Bean 缓存池为 HashMap 实现
![[Pasted image 20230823230106.png]]
### IOC 容器实现
#### BeanFactory-框架基础设施
BeanFactory 是 Spring 框架的基础设施，面向 Spring 本身；ApplicationContext 面向使用 Spring 框架的开发者，几乎所有的应用场合我们都直接使用 ApplicationContext 而非底层 的 BeanFactory。
![[Pasted image 20230823230226.png]]

##### BeanDefinitionRegistry 注册表 
1. Spring 配置文件中每一个节点元素在 Spring 容器里都通过一个 BeanDefinition 对象表示，它描述了 Bean 的配置信息。而 BeanDefinitionRegistry 接口提供了向容器手工注册 BeanDefinition 对象的方法。 
##### BeanFactory 顶层接口 
2. 位于类结构树的顶端 ，它最主要的方法就是 getBean(String beanName)，该方法从容器中返回特定名称的 Bean，BeanFactory 的功能通过其他的接口得到不断扩展
##### ListableBeanFactory 
3. 该接口定义了访问容器中 Bean 基本信息的若干方法，如查看 Bean 的个数、获取某一类型 Bean 的配置名、查看容器中是否包括某一 Bean 等方法； 
##### HierarchicalBeanFactory 父子级联 
4. 父子级联 IOC 容器的接口，子容器可以通过接口方法访问父容器； 通过 HierarchicalBeanFactory 接口， **Spring 的 IOC 容器可以建立父子层级关联的容器体系**，子容器可以访问父容器中的 Bean，但父容器不能访问子容器的 Bean。Spring 使用父子容器实现了很多功能，比如**在 Spring MVC 中，展现层 Bean 位于一个子容器中，而业务层和持久 层的 Bean 位于父容器中。这样，展现层 Bean 就可以引用业务层和持久层的 Bean，而业务层和持久层的 Bean 则看不到展现层的 Bean。** 
##### ConfigurableBeanFactory 
5. 是一个重要的接口，增强了 IOC 容器的可定制性，它定义了设置类装载器、属性编辑器、容器初始化后置处理器等方法； 
##### AutowireCapableBeanFactory 自动装配 
6. 定义了将容器中的 Bean 按某种规则（如按名字匹配、按类型匹配等）进行自动装配的方法； 
##### SingletonBeanRegistry 运行期间注册单例 Bean 
7. 定义了允许在运行期间向容器注册单实例 Bean 的方法；对于单实例（ singleton）的 Bean 来说，**BeanFactory 会缓存 Bean 实例，所以第二次使用 getBean() 获取 Bean 时将直接从 IOC 容器的缓存中获取 Bean 实例**。Spring 在 DefaultSingletonBeanRegistry 类中提供了一个用于缓存单实例 Bean 的缓存器，它是一个用 HashMap 实现的缓存器，单实例的 Bean 以 beanName 为键保存在这个 HashMap 中。 
##### 依赖日志框框 
8. **在初始化 BeanFactory 时，必须为其提供一种日志框架**，比如使用 Log4J， 即在类路径下提 供 Log4J 配置文件，这样启动 Spring 容器才不会报错。

#### ApplicationContext 面向开发应用
ApplicationContext 由 BeanFactory 派生而来，提供了更多面向实际应用的功能。 ApplicationContext 继承了 HierarchicalBeanFactory 和 ListableBeanFactory 接口，在此基础 上，还通过多个其他的接口扩展了 BeanFactory 的功能：
![[Pasted image 20230823231113.png]]
1. ClassPathXmlApplicationContext：默认从类路径加载配置文件
2. FileSystemXmlApplicationContext：默认从文件系统中装载配置文件 
3. ApplicationEventPublisher：让容器拥有发布应用上下文事件的功能，包括容器启动事件、关闭事件等。 
4. MessageSource：为应用提供 i18n 国际化消息访问的功能； 
5. ResourcePatternResolver ： 所有 ApplicationContext 实现类都实现了类似于 PathMatchingResourcePatternResolver 的功能，可以通过带前缀的 Ant 风格的资源文 件路径装载 Spring 的配置文件。 
6. LifeCycle：该接口是 Spring 2.0 加入的，该接口提供了 start()和 stop()两个方法，主要用于控制异步处理过程。在具体使用时，该接口同时被 ApplicationContext 实现及具体 Bean 实现， ApplicationContext 会将 start/stop 的信息传递给容器中所有实现了该接口的 Bean，以达到管理和控制 JMX、任务调度等目的。 
7. ConfigurableApplicationContext 扩展于 ApplicationContext，它新增加了两个主要的方法： refresh()和 close()，让 ApplicationContext 具有启动、刷新和关闭应用上下文的能力。在应用上下文关闭的情况下调用 refresh()即可启动应用上下文，在已经启动的状态下，调用 refresh()则清除缓存并重新装载配置信息，而调用 close()则可关闭应用 上下文。
#### WebApplication 体系架构
WebApplicationContext 是专门为 Web 应用准备的，它允许从相对于 Web 根目录的路径中装载配置文件完成初始化工作。从 WebApplicationContext 中可以获得 ServletContext 的引用，**整个 Web 应用上下文对象将作为属性放置到 ServletContext 中**，以便 Web 应用环境可以访问 Spring 应用上下文。
![[Pasted image 20230824215939.png]]

### Spring Bean 作用域
Spring 3 中为 Bean 定义了 5 中作用域，**分别为 singleton（单例）、prototype（原型）、 request、session 和 global session**，5 种作用域说明如下：
#### singleton：单例模式（多线程下不安全）
singleton：单例模式，Spring IOC 容器中只会存在一个共享的 Bean 实例，无论有多少个 Bean 引用它，始终指向同一对象。**该模式在多线程下是不安全的**。Singleton 作用域是 Spring 中的缺省作用域，也可以显示的将 Bean 定义为 singleton 模式，配置为：`<bean id="userDao" class="com.ioc.UserDaoImpl" scope="singleton"/>`
#### prototype:原型模式每次使用时创建
prototype:原型模式，**每次通过 Spring 容器获取 prototype 定义的 bean 时，容器都将创建 一个新的 Bean 实例，每个 Bean 实例都有自己的属性和状态**，而 singleton 全局只有一个对 象。根据经验，**对有状态的bean使用 prototype 作用域，而对无状态的 bean 使用 singleton 作用域**。
#### Request：一次 request 一个实例
request：在**一次 Http 请求中，容器会返回该 Bean 的同一实例**。而对不同的 Http 请求则会 产生新的 Bean，而且该 **bean 仅在当前 Http Request 内有效**，当前 Http 请求结束，该 bean 实例也将会被销毁。
`<bean id="loginAction" class="com.cnblogs.Login" scope="request"/>`
#### session
session：在一次 Http Session 中，容器会返回该 Bean 的同一实例。而对不同的 Session 请求则会创建新的实例，该 bean 实例仅在当前 Session 内有效。同 Http 请求相同，每一次 session 请求创建新的实例，而不同的实例之间不共享属性，且实例仅在自己的 session 请求内有效，请求结束，则实例将被销毁。
`<bean id="userPreference" class="com.ioc.UserPreference" scope="session"/>`
#### global Session
global Session：在一个全局的 Http Session 中，容器会返回该 Bean 的同一个实例，仅在 使用 portlet context 时有效。

### Spring Bean 生命周期
![[Pasted image 20230824221604.png]]
#### 实例化 
1. 实例化一个 Bean，也就是我们常说的 new。 
#### IOC 依赖注入 
2. 按照 Spring 上下文对实例化的 Bean 进行配置，也就是 IOC 注入。 
#### setBeanName 实现 
3. 如果这个 Bean 已经实现了 BeanNameAware 接口，会调用它实现的 setBeanName(String) 方法，此处传递的就是 Spring 配置文件中 Bean 的 id 值 
#### BeanFactoryAware 实现 
4. 如果这个 Bean 已经实现了 BeanFactoryAware 接口，会调用它实现的 setBeanFactory， setBeanFactory(BeanFactory) 传递的是 Spring 工厂自身（可以用这个方式来获取其它 Bean， 只需在 Spring 配置文件中配置一个普通的 Bean 就可以）。 
#### ApplicationContextAware 实现 
5. 如果这个 Bean 已经实现了 ApplicationContextAware 接口，会调用 setApplicationContext(ApplicationContext) 方法，传入 Spring 上下文（同样这个方式也 可以实现步骤 4 的内容，但比 4 更好，因为 ApplicationContext 是 BeanFactory 的子接口，有更多的实现方法） 
#### postProcessBeforeInitialization 接口实现-初始化预处理 
6. 如果这个 Bean 关联了 **BeanPostProcessor 接口，将会调用 postProcessBeforeInitialization(Object obj, String s)方法，BeanPostProcessor 经常被用 作是 Bean 内容的更改，并且由于这个是在 Bean 初始化结束时调用那个的方法，也可以被应 用于内存或缓存技术。** 
#### init-method 
7. 如果 Bean 在 Spring 配置文件中配置了 init-method 属性会自动调用其配置的初始化方法。 
#### postProcessAfterInitialization 
8. 如果这个 Bean 关联了 BeanPostProcessor 接口，将会调用 postProcessAfterInitialization(Object obj, String s) 方法。 注：**以上工作完成以后就可以应用这个 Bean 了**，那这个 Bean 是一个 Singleton 的，所以一 般情况下我们调用同一个 id 的 Bean 会是在内容地址相同的实例，当然在 Spring 配置文件中也可以配置非 Singleton。 
#### Destroy 过期自动清理阶段 
9. 当 Bean 不再需要时，会经过清理阶段，如果 Bean 实现了 DisposableBean 这个接口，会调用那个其实现的 destroy()方法； 
#### destroy-method 自配置清理 
10. 最后，如果这个 Bean 的 Spring 配置中配置了 destroy-method 属性，会自动调用其配置的销毁方法。

11. bean 标签有两个重要的属性**（init-method 和 destroy-method）。用它们你可以自己定制 初始化和注销方法。它们也有相应的注解（@PostConstruct 和@PreDestroy）**。
    `<bean id="" class="" init-method="初始化方法" destroy-method="销毁方法">`

### Spring 依赖注入四种方式
此处略，仅提及一下。
* 构造器注入
* setter 方法注入
* 静态工厂注入
* 实例工厂

## Spring APO 原理
### 概念
"横切"的技术，剖解开封装的对象内部，并将那些影响了多个类的公共行为封装到一个可重用模块， 并将其命名为"Aspect"，即切面。所谓"切面"，简单说就是那些与业务无关，却为业务模块所共同调用的逻辑或责任封装起来，便于减少系统的重复代码，降低模块之间的耦合度，并有利于未 来的可操作性和可维护性。 
使用"横切"技术，AOP 把软件系统分为两个部分：核心关注点和横切关注点。业务处理的主要流程是核心关注点，与之关系不大的部分是横切关注点。横切关注点的一个特点是，他们经常发生 在核心关注点的多处，而各处基本相似，比如权限认证、日志、事物。AOP 的作用在于分离系统中的各种关注点，将核心关注点和横切关注点分离开来。 
AOP 主要应用场景有：
1. Authentication 权限 
2. Caching 缓存 
3. Context passing 内容传递 
4. Error handling 错误处理 
5. Lazy loading 懒加载 
6. Debugging 调试 
7. logging, tracing, profiling and monitoring 记录跟踪 优化 校准 
8. Performance optimization 性能优化 
9. Persistence 持久化 
10. Resource pooling 资源池 
11. Synchronization 同步 
12. Transactions 事务
### AOP 核心概念
1. 切面（aspect）：类是对物体特征的抽象，切面就是对横切关注点的抽象 
2. 横切关注点：对哪些方法进行拦截，拦截后怎么处理，这些关注点称之为横切关注点。 
3. 连接点（joinpoint）：**被拦截到的点**，因为 Spring 只支持方法类型的连接点，所以在 Spring 中连接点指的就是被拦截到的方法，实际上连接点还可以是字段或者构造器。 
4. 切入点（pointcut）：对连接点进行拦截的定义 
5. 通知（advice）：所谓通知指的就是指拦截到连接点之后要执行的代码，**通知分为前置、后置、 异常、最终、环绕通知五类**。 
6. 目标对象：代理的目标对象 
7. 织入（weave）：将切面应用到目标对象并导致代理对象创建的过程
### AOP 两种代理方式
Spring 提供了两种方式来生成代理对象: JDKProxy 和 Cglib，具体使用哪种方式生成由 AopProxyFactory 根据 AdvisedSupport 对象的配置来决定。**默认的策略是如果目标类是接口， 则使用 JDK 动态代理技术，否则使用 Cglib 来生成代理**。
#### JDK 动态接口代理
JDK 动态代理主要涉及到 java.lang.reflect 包中的两个类：**Proxy** 和 **InvocationHandler**。 
**InvocationHandler是一个接口，通过实现该接口定义横切逻辑，并通过反射机制调用目标类的代码，动态将横切逻辑和业务逻辑编制在一起。Proxy 利用 InvocationHandler 动态创建 一个符合某一接口的实例，生成目标类的代理对象。**
#### CGLib 动态代理
CGLib 全称为 Code Generation Library，是一个强大的高性能，**高质量的代码生成类库， 可以在运行期扩展 Java 类与实现 Java 接口**，CGLib 封装了 asm，可以再运行期动态生成新 的 class。和 JDK 动态代理相比较：JDK 创建代理有一个限制，就是只能为接口创建代理实例， 而对于没有通过接口定义业务方法的类，则可以通过 CGLib 创建动态代理。
### 实现原理
略

## Spring MVC 原理
Spring 的模型-视图-控制器（MVC）框架是围绕一个 DispatcherServlet 来设计的，这个 Servlet 会把请求分发给各个处理器，并支持可配置的处理器映射、视图渲染、本地化、时区与主题渲染 等，甚至还能支持文件上传。
### MVC 流程
![[Pasted image 20230825201923.png]]
#### Http 请求到 DispatcherServlet
(1)客户端请求提交到 DispatcherServlet。
#### HandlerMapping 寻找处理器
(2)由 DispatcherServlet 控制器查询一个或多个 HandlerMapping，找到处理请求的 Controller。
#### 调用处理器 Controller
(3)DispatcherServlet 将请求提交到 Controller。
#### Controller 调用业务逻辑处理后，返回 ModelAndView
(4)(5)调用业务处理和返回结果：Controller 调用业务逻辑处理后，返回 ModelAndView。
#### DispatcherServlet 查询 ModelAndView
(6)(7)处理视图映射并返回模型： DispatcherServlet 查询一个或多个 ViewResoler 视图解析器， 找到 ModelAndView 指定的视图。
#### ModelAndView 反馈浏览器 HTTP
(8)Http 响应：视图负责将结果显示到客户端。

### MVC 常用注解
mvc 注解
    视图解析器
        @Controller：略
        @RestController：略
        @Component：略
        @Respository：略
        @Service：略
    参数解析
        @ResponseBody
            异步请求
            该注解用于将 Controller 的方法返回的对象，通过适当的 HttpMessageConverter 转换为指定格式后，写入到 Response 对象的 body 数据区
            返回的数据不是 html 标签的页面，而是其他某种格式的数据是（如json、xml等）使用
        @RequestMapping
            一个用来处理请求地址映射的注解，可用与类或方法上。用于类上，表示类中的所有相应请求的方法都是改地址作为父路径
        @Autowired
            它可以对类成员变量、方法机构造函数进行标注，完成自动装配的工作。通过 @Autowired 的使用来小时 set , get 方法
        @PathVariable
            用于将请求URL中的模板变量映射到功能处理方法的参数上，即取出uri模板中的变量作为参数
        @RequestParam
            主要用于在SpringMVC后台控制层获取参数，类似一种是 request.getParamter("name")
        @ResuestHeader
            可以把Request请求header部分的值绑定到方法的参数上
## Spring Boot 原理
Spring Boot 是由 Pivotal 团队提供的全新框架，其设计目的是用来简化新 Spring 应用的初始搭建以及开发过程。该框架使用了特定的方式来进行配置，从而使开发人员不再需要定义样板化的配置。通过这种方式，Spring Boot 致力于在蓬勃发展的快速应用开发领域(rapid application development)成为领导者。其特点如下：
1. 创建独立的 Spring 应用程序 
2. 嵌入的 Tomcat，无需部署 WAR 文件 
3. 简化 Maven 配置 
4. 自动配置 Spring 
5. 提供生产就绪型功能，如指标，健康检查和外部配置 
6. 绝对没有代码生成和对 XML 没有要求配置
## JPA 原理
### 事务
事务是计算机应用中不可或缺的组件模型，它保证了用户操作的原子性 ( Atomicity )、一致性 ( Consistency )、隔离性 ( Isolation ) 和持久性 ( Durabilily )。
### 本地事务
紧密依赖于底层资源管理器（例如数据库连接 )，**事务处理局限在当前事务资源内**。此种事务处理方式不存在对应用服务器的依赖，因而部署灵活却无法支持多数据源的分布式事务。在数据库连 接中使用本地事务示例如下：
``` java
public void transferAccount() { 
    Connection conn = null; 
    Statement stmt = null; 
    try { 
        conn = getDataSource().getConnection(); 
        // 将自动提交设置为 false，若设置为 true 则数据库将会把每一次数据更新认定为一个事务并自动提交 
        conn.setAutoCommit(false); 
        stmt = conn.createStatement(); 
        // 将 A 账户中的金额减少 500 
        stmt.execute("update t_account set amount = amount - 500 where account_id = 'A'");
        // 将 B 账户中的金额增加 500 
        stmt.execute("update t_account set amount = amount + 500 where account_id = 'B'"); 
        // 提交事务 
        conn.commit(); 
        // 事务提交：转账的两步操作同时成功 
    } catch (SQLException sqle) { 
        // 发生异常，回滚在本事务中的操做 
        conn.rollback(); 
        // 事务回滚：转账的两步操作完全撤销 
        stmt.close(); 
        conn.close(); 
    } 
}
```
### 分布式事务
Java 事务编程接口（JTA：Java Transaction API）和 Java 事务服务 (JTS；Java Transaction Service) 为 J2EE 平台提供了分布式事务服务。分布式事务（Distributed Transaction）包括事务管理器（Transaction Manager）和一个或多个支持 XA 协议的资源管理器 ( Resource Manager )。我们可以将资源管理器看做任意类型的持久化数据存储；事务管理器承担着所有事务参与单元的协调与控制。
### 两阶段提交
两阶段提交主要保证了分布式事务的原子性：即所有结点要么全做要么全不做，所谓的两个阶段是指：**第一阶段：准备阶段；第二阶段：提交阶段。**
#### 准备阶段
事务协调者(事务管理器)给每个参与者(资源管理器)发送 Prepare 消息，每个参与者要么直接返回失败(如权限验证失败)，**要么在本地执行事务，写本地的 redo 和 undo 日志，但不提交**，到达一种"万事俱备，只欠东风”的状态。
#### 提交阶段
如果协调者收到了参与者的失败消息或者超时，直接给每个参与者发送回滚(Rollback)消息；否则，发送提交(Commit)消息；参与者根据协调者的指令执行提交或者回滚操作，释放所有事务处理过程中使用的锁资源。（注意:**必须在最后阶段释放锁资源**）
将提交分成两阶段进行的目的很明确，就是尽可能晚地提交事务，让事务在提交前尽可能地完成所有能完成的工作。
## Mybatis 缓存
Mybatis 中有一级缓存和二级缓存，默认情况下一级缓存是开启的，而且是不能关闭的。一级缓存是指 SqlSession 级别的缓存，当在同一个 SqlSession 中进行相同的 SQL 语句查询时，第二次以后的查询不会从数据库查询，而是直接从缓存中获取，一级缓存最多缓存 1024 条 SQL。二级缓存是指可以跨 SqlSession 的缓存。是 mapper 级别的缓存，对于 mapper 级别的缓存不同的 sqlsession 是可以共享的。
![[Pasted image 20230825205857.png]]
### Mybatis 的一级缓存原理（sqlsession 级别）
第一次发出一个查询 sql，sql 查询结果写入 sqlsession 的一级缓存中，缓存使用的数据结构是一 个 map。 
    **key：MapperID+offset+limit+Sql+所有的入参** 
    value：用户信息 
同一个 sqlsession 再次发出相同的 sql，就从缓存中取出数据。**如果两次中间出现 commit 操作 （修改、添加、删除），本 sqlsession 中的一级缓存区域全部清空，下次再去缓存中查询不到所以要从数据库查询**，从数据库查询到再写入缓存。
### 二级缓存原理（mapper 基本）
二级缓存的范围是 mapper 级别（mapper 同一个命名空间），mapper 以命名空间为单位创建缓存数据结构，结构是 map。mybatis 的二级缓存是通过 CacheExecutor 实现的。CacheExecutor 其实是 Executor 的代理对象。所有的查询操作，在 CacheExecutor 中都会先匹配缓存中是否存 在，不存在则查询数据库。
    key：MapperID+offset+limit+Sql+所有的入参
具体使用需要配置： 
    1. Mybatis 全局配置中启用二级缓存配置 
    2. 在对应的 Mapper.xml 中配置 cache 节点 
    3. 在对应的 select 查询节点中添加 useCache=true
## Tomcat 架构
略

# 微服务
## 服务注册发现
服务注册就是维护一个登记簿，它管理系统内所有的服务地址。当新的服务启动后，它会向登记 簿交待自己的地址信息。服务的依赖方直接向登记簿要 Service Provider 地址就行了。当下用于服 务注册的工具非常多 Nacos，ZooKeeper，Consul，Etcd, 还有 Netflix 家的 eureka 等。服务注册有两种 形式：客户端注册和第三方注册。
## API 网关
API Gateway 是一个服务器，也可以说是进入系统的唯一节点。这跟面向对象设计模式中的 Facade 模式很像。API Gateway 封装内部系统的架构，并且提供 API 给各个客户端。它还可能有 其他功能，如授权、监控、负载均衡、缓存、请求分片和管理、静态响应处理等。下图展示了一个适应当前架构的 API Gateway。
**API Gateway 负责请求转发、合成和协议转换**。所有来自客户端的请求都要先经过 API Gateway， 然后路由这些请求到对应的微服务。API Gateway 将经常通过调用多个微服务来处理一个请求以 及聚合多个服务的结果。它可以在 web 协议与内部使用的非 Web 友好型协议间进行转换，如 HTTP 协议、WebSocket 协议。
### 请求转发
服务转发主要是对客户端的请求安装微服务的负载转发到不同的服务上
### 响应合并
把业务上需要调用多个服务接口才能完成的工作合并成一次调用对外统一提供服务
### 协议转换
重点是支持 SOAP，JMS，Rest 间的协议转换。
### 数据转换
重点是支持 XML 和 Json 之间的报文格式转换能力（可选）
### 安全认证
1. 基于 Token 的客户端访问控制和安全策略 
2. 传输数据和报文加密，到服务端解密，需要在客户端有独立的 SDK 代理包 
3. 基于 Https 的传输加密，客户端和服务端数字证书支持 
4. 基于 OAuth2.0 的服务安全认证(授权码，客户端，密码模式等）
## 配置中心
配置中心一般用作系统的参数配置，它需要满足如下几个要求：高效获取、实时感知、分布式访问。
## 事件调度
消息服务和事件的统一调度，常用用 kafka ，activemq，rabbitmq 等。
### 服务跟踪
随着微服务数量不断增长，需要跟踪一个请求从一个微服务到下一个微服务的传播过程， **Spring Cloud Sleuth 正是解决这个问题，它在日志中引入唯一 ID，以保证微服务调用之间的一致性，这 样你就能跟踪某个请求是如何从一个微服务传递到下一个。**
### 服务熔断
在微服务架构中通常会有多个服务层调用，基础服务的故障可能会导致级联故障，进而造成整个系统不可用的情况，这种现象被称为服务雪崩效应。服务雪崩效应是一种因“服务提供者”的不可用导致“服务消费者”的不可用,并将不可用逐渐放大的过程。 熔断器的原理很简单，如同电力过载保护器。它可以实现快速失败，如果它在一段时间内侦测到 许多类似的错误，会强迫其以后的多个调用快速失败，不再访问远程服务器，从而防止应用程序不断地尝试执行可能会失败的操作，使得应用程序继续执行而不用等待修正错误，或者浪费 CPU 时间去等到长时间的超时产生。熔断器也可以使应用程序能够诊断错误是否已经修正，如果已经修正，应用程序会再次尝试调用操作。
## API 管理
SwaggerAPI 管理工具。
# Netty 与 RPC
## Netty 原理
Netty 是一个高性能、异步事件驱动的 NIO 框架，基于 JAVA NIO 提供的 API 实现。它提供了对 TCP、UDP 和文件传输的支持，作为一个异步 NIO 框架，Netty 的所有 IO 操作都是异步非阻塞 的，**通过 Future-Listener 机制，用户可以方便的主动获取或者通过通知机制获得 IO 操作结果。**
## Netty 高性能
在 IO 编程过程中，当需要同时处理多个客户端接入请求时，可以利用多线程或者 IO 多路复用技术进行处理。IO 多路复用技术通过把多个 IO 的阻塞复用到同一个 select 的阻塞上，从而使得系统在单线程的情况下可以同时处理多个客户端请求。与传统的多线程/多进程模型比，I/O 多路复用的最大优势是系统开销小，系统不需要创建新的额外进程或者线程，也不需要维护这些进程和线程的运行，降低了系统的维护工作量，节省了系统资源。 
与 Socket 类和 ServerSocket 类相对应，NIO 也提供了 SocketChannel 和 ServerSocketChannel 两种不同的套接字通道实现。
### 多路复用通讯方式
Netty 架构按照 Reactor 模式设计和实现，它的服务端通信序列图如下：
![[Pasted image 20230828233701.png]]
客户端通信序列图如下：
![[Pasted image 20230828233809.png]]
Netty 的 IO **线程 NioEventLoop 由于聚合了多路复用器 Selector**，可以同时并发处理成百上千个 客户端 Channel，由于读写操作都是非阻塞的，这就可以充分提升 IO 线程的运行效率，避免由于 频繁 IO 阻塞导致的线程挂起。
### 异步通讯 NIO
由于 Netty 采用了异步通信模式，一个 IO 线程可以并发处理 N 个客户端连接和读写操作，这从根 本上解决了传统同步阻塞 IO 一连接一线程模型，架构的性能、弹性伸缩能力和可靠性都得到了极 大的提升。
### 略

# 网络
## 网络 7 层架构
1. 物理层：主要定义物理设备标准，如网线的接口类型、光纤的接口类型、各种传输介质的传输速率等。它的主要作用是传输比特流（就是由 1、0 转化为电流强弱来进行传输，到达目的地后在转化为 1、0，也就是我们常说的模数转换与数模转换）。这一层的数据叫做比特。 
2. 数据链路层：主要将从物理层接收的数据进行 MAC 地址（网卡的地址）的封装与解封装。常把这 一层的数据叫做帧。在这一层工作的设备是交换机，数据通过交换机来传输。 
3. 网络层：主要将从下层接收到的数据进行 IP 地址（例 192.168.0.1)的封装与解封装。在这一层工作的设备是路由器，常把这一层的数据叫做数据包。 
4. 传输层：定义了一些传输数据的协议和端口号（WWW 端口 80 等），如：TCP（传输控制协议， 传输效率低，可靠性强，用于传输可靠性要求高，数据量大的数据），UDP（用户数据报协议， 与 TCP 特性恰恰相反，用于传输可靠性要求不高，数据量小的数据，如 QQ 聊天数据就是通过这 种方式传输的）。 主要是将从下层接收的数据进行分段进行传输，到达目的地址后在进行重组。 常常把这一层数据叫做段。 
5. 会话层：通过传输层（端口号：传输端口与接收端口）建立数据传输的通路。主要在你的系统之间发起会话或或者接受会话请求（设备之间需要互相认识可以是 IP 也可以是 MAC 或者是主机名） 
6. 表示层：主要是进行对接收的数据进行解释、加密与解密、压缩与解压缩等（也就是把计算机能够识别的东西转换成人能够能识别的东西（如图片、声音等）） 
7. 应用层 主要是一些终端的应用，比如说FTP（各种文件下载），WEB（IE浏览），QQ之类的（你就把它理解成我们在电脑屏幕上可以看到的东西．就是终端应用）。
## TCP/IP 原理
TCP/IP 协议不是 TCP 和 IP 这两个协议的合称，而是指因特网整个 TCP/IP 协议族。从协议分层模型方面来讲，TCP/IP 由四个层次组成：网络接口层、网络层、传输层、应用层。
![[Pasted image 20230829223501.png]]
1. 网络访问层(Network Access Layer)在 TCP/IP 参考模型中并没有详细描述，**只是指出主机必须使用某种协议与网络相连。**
2. 网络层(Internet Layer)是整个体系结构的关键部分，其功能是使主机可以把分组发往任何网 络，并使分组独立地传向目标。这些分组可能经由不同的网络，到达的顺序和发送的顺序也可能不同。高层如果需要顺序收发，那么就必须自行处理对分组的排序。**互联网层使用因特网协议(IP，Internet Protocol)。**
3. 传输层(Tramsport Layer)使源端和目的端机器上的对等实体可以进行会话。**在这一层定义了两个端到端的协议**：传输控制协议(TCP，Transmission Control Protocol)和用户数据报协议(UDP，User Datagram Protocol)。TCP 是面向连接的协议，它提供可靠的报文传输和对上层应用的连接服务。为此，除了基本的数据传输外，它还有可靠性保证、流量控制、多路复用、优先权和安全性控制等功能。UDP 是面向无连接的不可靠传输的协议，主要用于不需要 TCP 的排序和流量控制等功能的应用程序。
4. 应用层(Application Layer)包含所有的高层协议，包括：虚拟终端协议(TELNET，TELecommunications NETwork)、文件传输协议(FTP，File Transfer Protocol)、电子邮件传输协议(SMTP，Simple Mail Transfer Protocol)、域名服务(DNS，Domain Name Service)、网上新闻传输协议(NNTP，Net News Transfer Protocol)和超文本传送协议 (HTTP，HyperText Transfer Protocol)等。
## TCP 三次握手/四次挥手
TCP 在传输之前会进行三次沟通，一般称为“三次握手”，传完数据断开的时候要进行四次沟通，一般称为“四次挥手”。
### 数据包说明
略
### 三次握手
第一次握手：主机 A 发送位码为 syn＝1，随机产生 seq number=1234567 的数据包到服务器，主机 B 由 SYN=1 知道，A 要求建立联机； 
第二次握手：主机 B 收到请求后要确认联机信息，向 A 发送 ack number=( 主 机 A 的 seq+1)，syn=1，ack=1，随机产生 seq=7654321 的包
第三次握手：主机 A 收到后检查 ack number 是否正确，即第一次发送的 seq number+1,以及位码 ack 是否为 1，若正确，主机 A 会再发送 ack number=(主机 B 的 seq+1),ack=1，主机 B 收到后确认 seq 值与 ack=1 则连接建立成功。
![[Pasted image 20230829225218.png]]
### 四次挥手
TCP 建立连接要进行三次握手，而断开连接要进行四次。这是由于 **TCP 的半关闭造成的**。因为 TCP 连 接是全双工的(即数据可在两个方向上同时传递)所以进行关闭时每个方向上都要单独进行关闭。这个单方向的关闭就叫半关闭。当一方完成它的数据发送任务，就发送一个 FIN 来向另一方通告将要终止这个方向的连接。
1. 关闭客户端到服务器的连接：首先客户端 A 发送一个 FIN，用来关闭客户到服务器的数据传送， 然后等待服务器的确认。其中终止标志位 FIN=1，序列号 seq=u 
2. 服务器收到这个 FIN，它发回一个 ACK，确认号 ack 为收到的序号加 1。 
3. 关闭服务器到客户端的连接：也是发送一个 FIN 给客户端。
4. 客户段收到 FIN 后，并发回一个 ACK 报文确认，并将确认序号 seq 设置为收到序号加 1。 首先进行关闭的一方将执行主动关闭，而另一方执行被动关闭。
![[Pasted image 20230829225653.png]]
主机 A 发送 FIN 后，进入终止等待状态， 服务器 B 收到主机 A 连接释放报文段后，就立即给主机 A 发送确认，然后服务器 B 就进入 close-wait 状态，此时 TCP 服务器进程就通知高层应用进程，因而从 A 到 B 的连接就释放了。此时是“半关闭”状态。即 A 不可以发送给 B，但是 B 可以发送给 A。此时，若 B 没有数据报要发送给 A 了，其应用进程就通知 TCP 释放连接，然后发送给 A 连接释放报文段，并等待确认。A 发送确认后，进入 time-wait，注意，此时 TCP 连接还没有释放掉，然后经过时间等待计时器设置的 2MSL 后，A 才进入到 close 状态。
## HTTP 原理
HTTP 是一个无状态的协议。无状态是指客户机（Web 浏览器）和服务器之间不需要建立持久的连接， 这意味着当一个客户端向服务器端发出请求，然后服务器返回响应(response)，连接就被关闭了，在服务器端不保留连接的有关信息.HTTP 遵循请求(Request)/应答(Response)模型。客户机（浏览器）向服务器发送请求，服务器处理请求并返回适当的应答。所有 HTTP 连接都被构造成一套请求和应答。
### 传输流程
此处略，仅提及一下。
1. 地址解析
2. 封装 HTTP 请求数据包
3. 封装成 TCP 包并建立连接
4. 客户机发送请求命、
5. 服务器响应
6. 服务器关闭 TCP 连接
### HTTP 状态
略
### HTTPS
HTTPS（全称：Hypertext Transfer Protocol over Secure Socket Layer），是以安全为目标的 HTTP 通道，简单讲是 HTTP 的安全版。即 HTTP 下加入 SSL 层，HTTPS 的安全基础是 SSL。其所用 的端口号是 443。 过程大致如下：
#### 建立连接获取证书
1. SSL 客户端通过 TCP 和服务器建立连接之后（443 端口），并且在一般的 tcp 连接协商（握手）过程中请求证书。即客户端发出一个消息给服务器，这个消息里面包含了自己可实现的算法列表和其它一些需要的消息，SSL 的服务器端会回应一个数据包，这里面确定了这次通信所需要的算法，然后服务器向客户端返回证书。（证书里面包含了服务器信息：域名。申请证书的公司，公共秘钥）。
#### 证书验证
2. Client 在收到服务器返回的证书后，判断签发这个证书的公共签发机构，并使用这个机构的公共秘钥确认签名是否有效，客户端还会确保证书中列出的域名就是它正在连接的域名。
#### 数据加密和传输
3. 如果确认证书有效，那么生成对称秘钥并使用服务器的公共秘钥进行加密。然后发送给服务器，服务器使用它的私钥对它进行解密，这样两台计算机可以开始进行对称加密进行通信。
![[Pasted image 20230829231507.png]]
## CDN 原理
CND 一般包含分发服务系统、负载均衡系统和管理系统

此处略，仅提及一下。
* 分发服务系统
* 负载均衡系统
* 管理系统

# 日志
## Slf4j
略
## Log4j
略
## LogBack
略
## ELK
ELK 是软件集合 Elasticsearch、Logstash、Kibana 的简称，由这三个软件及其相关的组件可以打 造大规模日志实时处理系统。
* Elasticsearch 是一个基于 Lucene 的、支持全文索引的分布式存储和索引引擎，主要负责将 日志索引并存储起来，方便业务方检索查询。 
* Logstash 是一个日志收集、过滤、转发的中间件，主要负责将各条业务线的各类日志统一收 集、过滤后，转发给 Elasticsearch 进行下一步处理。 
* Kibana 是一个可视化工具，主要负责查询 Elasticsearch 的数据并以可视化的方式展现给业 务方，比如各类饼图、直方图、区域图等。
![[Pasted image 20230830232631.png]]

# Zookeeper
略

# Kafka
## Kafka 概念
Kafka 是一种高吞吐量、分布式、基于发布/订阅的消息系统，最初由 LinkedIn 公司开发，使用 Scala 语言编写，目前是 Apache 的开源项目。
1. broker：Kafka 服务器，负责消息存储和转发 
2. topic：消息类别，Kafka 按照 topic 来分类消息 
3. partition：topic 的分区，一个 topic 可以包含多个 partition，topic 消息保存在各个 partition 上 
4. offset：消息在日志中的位置，可以理解是消息在 partition 上的偏移量，也是代表该消息的唯一序号 
5. Producer：消息生产者 
6. Consumer：消息消费者 
7. Consumer Group：消费者分组，每个 Consumer 必须属于一个 group 
8. Zookeeper：保存着集群 broker、topic、partition 等 meta 数据；另外，还负责 broker 故障发现，partition leader 选举，负载均衡等功能
![[Pasted image 20230831204822.png]]
## Kafka 数据存储设计
此处略，仅提及一下。
* partition 的数据文件（offset，MessageSize，data）
* 数据文件分段 segment（顺序读写、分段命令、二分查找）
* 数据文件分段 segment（顺序读写、分段命令、二分查找）
## 生产者设计
此处略，仅提及一下。
* 负载均衡（partition 会均衡分布到不同 broker 上）
* 批量发送
* 压缩（GZIP 或 Snappy）
![[Pasted image 20230831210124.png]]
## 消费者设计
此处略，仅提及一下
* Consumer Group
![[Pasted image 20230831210057.png]]

# RabbitMQ
## 概念
AMQP ：Advanced Message Queue，高级消息队列协议。它是应用层协议的一个开放标准，为 面向消息的中间件设计，基于此协议的客户端与消息中间件可传递消息，并不受产品、开发语言 等条件的限制。 RabbitMQ 最初起源于金融系统，用于在分布式系统中存储转发消息，在易用性、扩展性、高可 用性等方面表现不俗。具体特点包括：
1. 可靠性（Reliability）：RabbitMQ 使用一些机制来保证可靠性，如持久化、传输确认、发布确认。 
2. 灵活的路由（Flexible Routing）：在消息进入队列之前，通过 Exchange 来路由消息的。对于典型的路由功能，RabbitMQ 已经提供了一些内置的 Exchange 来实现。针对更复杂的路 由功能，可以将多个 Exchange 绑定在一起，也通过插件机制实现自己的 Exchange 。 
3. 消息集群（Clustering）：多个 RabbitMQ 服务器可以组成一个集群，形成一个逻辑 Broker 。 
4. 高可用（Highly Available Queues）：队列可以在集群中的机器上进行镜像，使得在部分节点出问题的情况下队列仍然可用。 
5. 多种协议（Multi-protocol）：RabbitMQ 支持多种消息队列协议，比如 STOMP、MQTT 等等。 
6. 多语言客户端（Many Clients）：RabbitMQ 几乎支持所有常用语言，比如 Java、.NET、 Ruby 等等。 
7. 管理界面（Management UI）:RabbitMQ 提供了一个易用的用户界面，使得用户可以监控和管理消息 Broker 的许多方面。 
8. 跟踪机制（Tracing）:如果消息异常，RabbitMQ 提供了消息跟踪机制，使用者可以找出发生 了什么。 
9. 插件机制（Plugin System）:RabbitMQ 提供了许多插件，来从多方面进行扩展，也可以编写自己的插件。
## RabbitMQ 架构
### Message
消息，消息是不具名的，它由消息头和消息体组成。消息体是不透明的，而消息头则由一系列的可选属性组成，这些属性包括 routing-key（路由键）、priority（相对于其他消息的优先权）、delivery-mode（指出该消息可能需要持久性存储）等。
### Publisher
消息的生产者，也是一个向交换器发布消息的客户端应用程序。
### Exchange（将消息路由给队列 ）
交换器，用来接收生产者发送的消息并将这些消息路由给服务器中的队列。
### Binding（消息队列和交换器之间的关联）
绑定，用于消息队列和交换器之间的关联。一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则，所以可以将交换器理解成一个由绑定构成的路由表。
### Queue
消息队列，用来保存消息直到发送给消费者。它是消息的容器，也是消息的终点。一个消息可投入一个或多个队列。消息一直在队列里面，等待消费者连接到这个队列将其取走。
### Connection
网络连接，比如一个 TCP 连接。
### Channel
信道，多路复用连接中的一条独立的双向数据流通道。信道是建立在真实的 TCP 连接内地虚 拟连接，AMQP 命令都是通过信道发出去的，不管是发布消息、订阅队列还是接收消息，这 些动作都是通过信道完成。因为对于操作系统来说建立和销毁 TCP 都是非常昂贵的开销，所 以引入了信道的概念，以复用一条 TCP 连接。
### Consumer
消息的消费者，表示一个从消息队列中取得消息的客户端应用程序。
### Virtual Host
虚拟主机，表示一批交换器、消息队列和相关对象。虚拟主机是共享相同的身份认证和加密环境的独立服务器域。
### Broker
表示消息队列服务器实体。
## Exchange 类型
Exchange 分发消息时根据类型的不同分发策略有区别，**目前共四种类型：direct、fanout、 topic、headers 。headers 匹配 AMQP 消息的 header 而不是路由键**，此外 headers 交换器和 direct 交换器完全一致，但性能差很多，目前几乎用不到了，所以直接看另外三种类型：
### Direct 键（routing key）分布
Direct：消息中的路由键（routing key）如果和 Binding 中的 binding key 一致， 交换器就将消息发到对应的队列中。它是完全匹配、单播的模式。
![[Pasted image 20230831220408.png]]
### Fanout（广播分发）
Fanout：每个发到 fanout 类型交换器的消息都会分到所有绑定的队列上去。很像子网广播，每台子网内的主机都获得了一份复制的消息。fanout 类型转发消息是最快 的。
![[Pasted image 20230831220454.png]]
### topic 交换器（模式匹配）
topic 交换器：topic 交换器通过模式匹配分配消息的路由键属性，将路由键和某个模 式进行匹配，此时队列需要绑定到一个模式上。它将路由键和绑定键的字符串切分成 单词，这些单词之间用点隔开。它同样也会识别两个通配符：符号“#”和符号 “”。#匹配 0 个或多个单词，匹配不多不少一个单词。
![[Pasted image 20230831220544.png]]
# Hbase
## 概念
Hbase 是分布式、面向列的开源数据库（其实准确的说是面向列族）。HDFS 为 Hbase 提供可靠的底层数据存储服务，MapReduce 为 Hbase 提供高性能的计算能力，Zookeeper 为 Hbase 提供稳定服务和 Failover 机制，因此我们说 Hbase 是一个通过大量廉价的机器解决海量数据的高速存储和读取的分布式数据库解决方案
## 列式存储
列方式所带来的重要好处之一就是，由于查询中的选择规则是通过列来定义的，因此整个数据库是自动索引化的。
![[Pasted image 20230831220827.png]]
这里的列式存储其实说的是列族存储，Hbase 是根据列族来存储数据的。列族下面可以有非常多的列，列族在创建表的时候就必须指定。为了加深对 Hbase 列族的理解，下面是一个简单的关系型数据库的表和 Hbase 数据库的表：
![[Pasted image 20230831221110.png]]
## Hbase 核心概念
此处略，进提及一下。
* Column Family 列族
* Rowkey（Rowkey 查询，Rowkey 范围扫描，全表扫描）
* Region 分区
* TimeStamp 多版本
# MongoDB
## 概念
MongoDB 是由 C++语言编写的，是一个基于分布式文件存储的开源数据库系统。在高负载的情况下，添加更多的节点，可以保证服务器性能。MongoDB 旨在为 WEB 应用提供可扩展的高性能数据存储解决方案。 MongoDB 将数据存储为一个文档，数据结构由键值(key=>value)对组成。MongoDB 文档类似 于 JSON 对象。字段值可以包含其他文档，数组及文档数组。
![[Pasted image 20230831222238.png]]
## 特点
* MongoDB 是一个面向文档存储的数据库，操作起来比较简单和容易。 
* 你可以在 MongoDB 记录中设置任何属性的索引 (如：FirstName="Sameer",Address="8 Ga ndhi Road")来实现更快的排序。 
* 你可以通过本地或者网络创建数据镜像，这使得 MongoDB 有更强的扩展性。 
* 如果负载的增加（需要更多的存储空间和更强的处理能力） ，它可以分布在计算机网络中的其他节点上这就是所谓的分片。 
* Mongo 支持丰富的查询表达式。查询指令使用 JSON 形式的标记，可轻易查询文档中内嵌的对象及数组。 
* MongoDb 使用 update()命令可以实现替换完成的文档（数据）或者一些指定的数据字段 。 
* Mongodb 中的 Map/reduce 主要是用来对数据进行批量处理和聚合操作。 
* Map 和 Reduce。Map 函数调用 emit(key,value)遍历集合中所有的记录，将 key 与 value 传给 Reduce 函数进行处理。 
* Map 函数和 Reduce 函数是使用 Javascript 编写的，并可以通过 db.runCommand 或 mapre duce 命令来执行 MapReduce 操作。 
* GridFS 是 MongoDB 中的一个内置功能，可以用于存放大量小文件。 
* MongoDB 允许在服务端执行脚本，可以用 Javascript 编写某个函数，直接在服务端执行，也可以把函数的定义存储在服务端，下次直接调用即可。
# Cassandra
略
# 设计模式
* 工厂方法模式 
* 抽象工厂模式 
* 单例模式 
* 建造者模式 
* 原型模式 
* 适配器模式 
* 装饰器模式 
* 代理模式 
* 外观模式 
* 桥接模式 
* 组合模式 
* 享元模式 
* 策略模式 
* 模板方法模式 
* 观察者模式 
* 迭代子模式 
* 责任链模式 
* 命令模式 
* 备忘录模式 
* 状态模式 
* 访问者模式 
* 中介者模式 
* 解释器模式
# 负载均衡
负载均衡 建立在现有网络结构之上，它提供了一种廉价有效透明的方法扩展网络设备和服务器的带宽、增加吞吐量、加强网络数据处理能力、提高网络的灵活性和可用性。
## 四层负载均衡 vs 七层负载均衡
![[Pasted image 20230831222928.png]]
### 四层负载均衡（目标地址和端口交换）
主要通过报文中的目标地址和端口，再加上负载均衡设备设置的服务器选择方式，决定最终选择的内部服务器。
以常见的 TCP 为例，**负载均衡设备在接收到第一个来自客户端的 SYN 请求时，即通过上述方式选 择一个最佳的服务器，并对报文中目标 IP 地址进行修改(改为后端服务器 IP），直接转发给该服务器**。TCP 的连接建立，**即三次握手是客户端和服务器直接建立的，负载均衡设备只是起到一个类似路由器的转发动作**。在某些部署情况下，为保证服务器回包可以正确返回给负载均衡设备，在转发报文的同时可能还会对报文原来的源地址进行修改。实现四层负载均衡的软件有：
* F5：硬件负载均衡器，功能很好，但是成本很高。 
* lvs：重量级的四层负载软件。 
* nginx：轻量级的四层负载软件，带缓存功能，正则表达式较灵活。 
* haproxy：模拟四层转发，较灵活。
### 七层负载均衡（内容交换）
所谓七层负载均衡，也称为“内容交换”，也就是主要通过报文中的真正有意义的应用层内容， 再加上负载均衡设备设置的服务器选择方式，决定最终选择的内部服务器。
七层应用负载的好处，是使得整个网络更智能化。例如访问一个网站的用户流量，可以通过七层的方式，将对图片类的请求转发到特定的图片服务器并可以使用缓存技术；将对文字类的请求可以转发到特定的文字服务器并可以使用压缩技术。实现七层负载均衡的软件有： 
* haproxy：天生负载均衡技能，全面支持七层代理，会话保持，标记，路径转移； 
* nginx：只在 http 协议和 mail 协议上功能比较好，性能与 haproxy 差不多； 
* apache：功能较差 
* Mysql proxy：功能尚可
## 负载均衡算法/策略
### 轮循均衡（Round Robin）
每一次来自网络的请求轮流分配给内部中的服务器，从 1 至 N 然后重新开始。此种均衡算法适合 于服务器组中的所有服务器都有相同的软硬件配置并且平均服务请求相对均衡的情况。
### 权重轮循均衡（Weighted Round Robin）
根据服务器的不同处理能力，给每个服务器分配不同的权值，使其能够接受相应权值数的服务请求。例如：服务器 A 的权值被设计成 1，B 的权值是 3，C 的权值是 6，则服务器 A、B、C 将分别接受到 10%、30％、60％的服务请求。此种均衡算法能确保高性能的服务器得到更多的使用率，避免低性能的服务器负载过重。
### 随机均衡（Random）
把来自网络的请求随机分配给内部中的多个服务器。
### 权重随机均衡（Weighted Random）
此种均衡算法类似于权重轮循算法，不过在处理请求分担时是个随机选择的过程
### 响应速度均衡（Response Time 探测时间）
负载均衡设备对内部各服务器发出一个探测请求（例如 Ping），然后根据内部中各服务器对探测请求的最快响应时间来决定哪一台服务器来响应客户端的服务请求。此种均衡算法能较好的反映服务器的当前运行状态，但这最快响应时间仅仅指的是负载均衡设备与服务器间的最快响应时间，而不是客户端与服务器间的最快响应时间。
### 最少连接数均衡（Least Connection）
最少连接数均衡算法对内部中需负载的每一台服务器都有一个数据记录，记录当前该服务器正在处理的连接数量，当有新的服务连接请求时，将把当前请求分配给连接数最少的服务器，使均衡更加符合实际情况，负载更加均衡。此种均衡算法适合长时处理的请求服务，如 FTP。
### 处理能力均衡（CPU、内存）
此种均衡算法将把服务请求分配给内部中处理负荷（根据服务器 CPU 型号、CPU 数量、内存大小及当前连接数等换算而成）最轻的服务器，由于考虑到了内部服务器的处理能力及当前网络运行状况，所以此种均衡算法相对来说更加精确，尤其适合运用到第七层（应用层）负载均衡的情况 下。
### DNS 响应均衡（Flash DNS）
此种均衡算法将把服务请求分配给内部中处理负荷（根据服务器 CPU 型号、CPU 数量、内存大小及当前连接数等换算而成）最轻的服务器，由于考虑到了内部服务器的处理能力及当前网络运行状况，所以此种均衡算法相对来说更加精确，尤其适合运用到第七层（应用层）负载均衡的情况下。
### 哈希算法
一致性哈希一致性 Hash，相同参数的请求总是发到同一提供者。当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动
### IP 地址散列（保证客户端服务器对应关系稳定）
通过管理发送方 IP 和目的地 IP 地址的散列，将来自同一发送方的分组(或发送至同一目的地的分组)统一转发到相同服务器的算法。当客户端有一系列业务需要处理而必须和一个服务器反复通信时，该算法能够以流(会话)为单位，保证来自相同客户端的通信能够一直在同一服务器中进行处 理。
### URL 散列
通过管理客户端请求 URL 信息的散列，将发送至相同 URL 的请求转发至同一服务器的算法。

## LVS
### LVS 原理
LVS 的 IP 负载均衡技术是通过 IPVS 模块来实现的，IPVS 是 LVS 集群系统的核心软件，它的主要作用是：安装在 Director Server 上，同时在 Director Server 上虚拟出一个 IP 地址，用户必须通过这个虚拟的 IP 地址访问服务器。这个虚拟 IP 一般称为 LVS 的 VIP，即 Virtual IP。访问的请求首先经过 VIP 到达负载调度器，然后由负载调度器从 Real Server 列表中选取一个服务节点响应用户的请求。 在用户的请求到达负载调度器后，调度器如何将请求发送到提供服务的 Real Server 节点，而 Real Server 节点如何返回数据给用户，是 IPVS 实现的重点技术。
ipvs ： 工作于内核空间，主要用于使用户定义的策略生效
ipvsadm : 工作于用户空间，主要用于用户定义和管理集群服务的工具
![[Pasted image 20230831225859.png]]
ipvs 工作于内核空间的 INPUT 链上，当收到用户请求某集群服务时，经过 PREROUTING 链，经检查本机路由表，送往 INPUT 链；在进入 netfilter 的 INPUT 链时，ipvs 强行将请求报文通过 ipvsadm 定义的集群服务策略的路径改为 FORWORD 链，将报文转发至后端真实提供服务的主机。
### LVS NAT 模式
![[Pasted image 20230831230112.png]]
1. 客户端将请求发往前端的负载均衡器，请求报文源地址是 CIP(客户端 IP),后面统称为 CIP，目标地址为 VIP(负载均衡器前端地址，后面统称为 VIP)。 
2. 负载均衡器收到报文后，发现请求的是在规则里面存在的地址，那么它将客户端请求报文的目标地址改为了后端服务器的 RIP 地址并将报文根据算法发送出去。 
3. 报文送到 Real Server 后，由于报文的目标地址是自己，所以会响应该请求，并将响应报文返还给 LVS。 
4. 然后 lvs 将此报文的源地址修改为本机并发送给客户端。 
5. 注意：在 NAT 模式中，Real Server 的网关必须指向 LVS，否则报文无法送达客户端

特点：
    1. NAT 技术将请求的报文和响应的报文都需要通过 LB 进行地址改写，因此网站访问量比较大的时候 LB 负载均衡调度器有比较大的瓶颈，一般要求最多之能 10-20 台节点 
    2. 只需要在 LB 上配置一个公网 IP 地址就可以了。 
    3. 每台内部的 realserver 服务器的网关地址必须是调度器 LB 的内网地址。 
    4. NAT 模式支持对 IP 地址和端口进行转换。即用户请求的端口和真实服务器的端口可以不一致。
优点：
    集群中的物理服务器可以使用任何支持 TCP/IP 操作系统，只有负载均衡器需要一个合法的 IP 地址。
缺点：
    扩展性有限。当服务器节点（普通 PC 服务器）增长过多时，负载均衡器将成为整个系统的瓶颈，因为所有的请求包和应答包的流向都经过负载均衡器。当服务器节点过多时，大量的数据包都交汇在负载均衡器那，速度就会变慢！

### LVS DR 模式（局域网改写 mac 地址）
![[Pasted image 20230831231801.png]]
1. 客户端将请求发往前端的负载均衡器，请求报文源地址是 CIP，目标地址为 VIP。 
2. 负载均衡器收到报文后，发现请求的是在规则里面存在的地址，那么它将客户端请求报文的源 MAC 地址改为自己 DIP 的 MAC 地址，目标 MAC 改为了 RIP 的 MAC 地址，并将此包发送给 RS。 
3. RS 发现请求报文中的目的 MAC 是自己，就会将次报文接收下来，处理完请求报文后，将响应 报文通过 lo 接口送给 eth0 网卡直接发送给客户端。 
4. 注意：需要设置 lo 接口的 VIP 不能响应本地网络内的 arp 请求。

总结：
    1. 通过在调度器 LB 上修改数据包的目的 MAC 地址实现转发。注意源地址仍然是 CIP，目的地址仍然是 VIP 地址。 
    2. 请求的报文经过调度器，而 RS 响应处理后的报文无需经过调度器 LB，因此并发访问量大时使用效率很高（和 NAT 模式比） 
    3. 因为 DR 模式是通过 MAC 地址改写机制实现转发，因此所有 RS 节点和调度器 LB 只能在一个局域网里面 
    4. RS 主机需要绑定 VIP 地址在 LO 接口（掩码 32 位）上，并且需要配置 ARP 抑制。 
    5. RS 节点的默认网关不需要配置成 LB，而是直接配置为上级路由的网关，能让 RS 直接出网就可以。
    6. 由于 DR 模式的调度器仅做 MAC 地址的改写，所以调度器 LB 就不能改写目标端口，那么 RS 服务器就得使用和 VIP 相同的端口提供服务。 
    7. 直接对外的业务比如 WEB 等，RS 的 IP 最好是使用公网 IP。对外的服务，比如数据库等最好使用内网 IP
优点： 
    和 TUN（隧道模式）一样，负载均衡器也只是分发请求，应答包通过单独的路由方法返回给客户端。与 VS-TUN 相比，VS-DR 这种实现方式不需要隧道结构，因此可以使用大多数操作系统做为物理服务器。 DR 模式的效率很高，但是配置稍微复杂一点，因此对于访问量不是特别大的公司可以用 haproxy/nginx取代。日1000-2000W PV或者并发请求1万一下都可以考虑用haproxy/nginx。 
缺点： 
    所有 RS 节点和调度器 LB 只能在一个局域网里面
### LVS TUN 模式（IP 封装、跨网段）
![[Pasted image 20230901231758.png]]
1. 客户端将请求发往前端的负载均衡器，请求报文源地址是 CIP，目标地址为 VIP。 
2. 负载均衡器收到报文后，发现请求的是在规则里面存在的地址，那么它将在客户端请求报文的 首部再封装一层 IP 报文,将源地址改为 DIP，目标地址改为 RIP,并将此包发送给 RS。 
3. RS 收到请求报文后，会首先拆开第一层封装，然后发现里面还有一层 IP 首部的目标地址是自己 lo 接口上的 VIP，所以会处理次请求报文，并将响应报文通过 lo 接口送给 eth0 网卡直接发送给客户端。 
4. 注意：需要设置 lo 接口的 VIP 不能在共网上出现

总结： 
    1. TUNNEL 模式必须在所有的 realserver 机器上面绑定 VIP 的 IP 地址 
    2. TUNNEL 模式的 vip ------>realserver 的包通信通过 TUNNEL 模式，不管是内网和外网都能通 信，所以不需要 lvs vip 跟 realserver 在同一个网段内。 
    3. TUNNEL 模式 realserver 会把 packet 直接发给 client 不会给 lvs 了 
    4. TUNNEL 模式走的隧道模式，所以运维起来比较难，所以一般不用。 
优点： 
    负载均衡器只负责将请求包分发给后端节点服务器，而 RS 将应答包直接发给用户。所以，减少了负载均衡器的大量数据流动，负载均衡器不再是系统的瓶颈，就能处理很巨大的请求量，这种方式，一台负载均衡器能够为很多 RS 进行分发。而且跑在公网上就能进行不同地域的分发。 
缺点： 
    隧道模式的 RS 节点需要合法 IP，这种方式需要所有的服务器支持”IP Tunneling” (IP Encapsulation)协议，服务器可能只局限在部分 Linux 系统上。
### LVS FULLNAT 模式
无论是 DR 还是 NAT 模式，不可避免的都有一个问题：LVS 和 RS 必须在同一个 VLAN 下，否则 LVS 无法作为 RS 的网关。这引发的两个问题是： 
1. 同一个 VLAN 的限制导致运维不方便，跨 VLAN 的 RS 无法接入。 
2. LVS 的水平扩展受到制约。当 RS 水平扩容时，总有一天其上的单点 LVS 会成为瓶颈。 
Full-NAT 由此而生，解决的是 LVS 和 RS 跨 VLAN 的问题，而跨 VLAN 问题解决后，LVS 和 RS 不再存在 VLAN 上的从属关系，可以做到多个 LVS 对应多个 RS，解决水平扩容的问题。 Full-NAT 相比 NAT 的主要改进是，在 SNAT/DNAT 的基础上，加上另一种转换，转换过程如下：
![[Pasted image 20230901233330.png]]


1. 在包从 LVS 转到 RS 的过程中，源地址从客户端 IP 被替换成了 LVS 的内网 IP。内网 IP 之间 可以通过多个交换机跨 VLAN 通信。目标地址从 VIP 修改为 RS IP. 
2. 当 RS 处理完接受到的包，处理完成后返回时，将目标地址修改为 LVS ip，原地址修改为 RS IP，最终将这个包返回给 LVS 的内网 IP，这一步也不受限于 VLAN。 
3. LVS 收到包后，在 NAT 模式修改源地址的基础上，再把 RS 发来的包中的目标地址从 LVS 内 网 IP 改为客户端的 IP,并将原地址修改为 VIP。 
Full-NAT 主要的思想是把网关和其下机器的通信，改为了普通的网络通信，从而解决了跨 VLAN 的问题。采用这种方式，LVS 和 RS 的部署在 VLAN 上将不再有任何限制，大大提高了运维部署的便利性

总结 
1. FULL NAT 模式不需要 LBIP 和 realserver ip 在同一个网段； 
2. full nat 因为要更新 sorce ip 所以性能正常比 nat 模式下降 10%
## Keepalive
keepalive 起初是为 LVS 设计的，专门用来**监控 lvs 各个服务节点的状态**，后来加入了 vrrp 的功能，因此除了 lvs，也可以作为其他服务（nginx，haproxy）的高可用软件。VRRP 是 virtual router redundancy protocal（虚拟路由器冗余协议）的缩写。VRRP 的出现就是为了解决静态路由出现的单点故障，它能够保证网络可以不间断的稳定的运行。所以 keepalive 一方面具有 LVS cluster node healthcheck 功能，另一方面也具有 LVS director failover。
## Nginx 反向代理负载均衡
普通的负载均衡软件，如 LVS，**其实现的功能只是对请求数据包的转发、传递**，从负载均衡下的节点服务器来看，接收到的请求还是来自访问负载均衡器的客户端的真实用户；而反向代理就不一样了，**反向代理服务器在接收访问用户请求后，会代理用户重新发起请求代理下的节点服务器**， 最后把数据返回给客户端用户。在节点服务器看来，访问的节点服务器的客户端用户就是反向代理服务器，而非真实的网站访问用户。
### upstream_module 和健康检测
**ngx_http_upstream_module 是负载均衡模块，可以实现网站的负载均衡功能即节点的健康检查**，upstream 模块允许 Nginx 定义一组或多组节点服务器组，使用时可通过 proxy_pass 代理方 式把网站的请求发送到事先定义好的对应 Upstream 组的名字上。

| upstream 模块内参数 | 参数说明 |
| -- | -- |
| weight | 服务器权重 |
| max_fails Nginx | 尝试连接后端主机失败的此时，这是值是配合 proxy_next_upstream、 fastcgi_next_upstream 和 memcached_next_upstream 这三个参数来使用的。当 Nginx 接收后端服务器返回这三个参数定义的状态码时，会将这个请求转发给正常工作的的后端服务器。如 404、503、503，max_files=1 |
| fail_timeout | max_fails 和 fail_timeout 一般会关联使用，如果某台 server 在 fail_timeout 时间内出现了 max_fails 次连接失败，那么 Nginx 会认为其已经挂掉，从而在 fail_timeout 时间内不再去请求它，fail_timeout 默认是 10s，max_fails 默认是 1，即默认情况只要是发生错误就认为服务器挂了，如果将 max_fails 设置为 0，则表示取消这项检查 |
| backup | 表示当前 server 是备用服务器，只有其它非 backup 后端服务器都挂掉了或很忙才会分配请求给它|
| down | 标志服务器永远不可用，可配合 ip_hash 使用 |
``` sh
upstream lvsServer { 
    server 191.168.1.11 weight=5 ; 
    server 191.168.1.22:82; 
    server example.com:8080 
    max_fails=2  fail_timeout=10s backup; 
    #域名的话需要解析的哦，内网记得 hosts 
}
```
### proxy_pass 请求转发
proxy_pass 指令属于 ngx_http_proxy_module 模块，此模块可以将请求转发到另一台服务器， 在实际的反向代理工作中，会通过 location 功能匹配指定的 URI，然后把接收到服务匹配 URI 的请求通过 proyx_pass 抛给定义好的 upstream 节点池。
``` sh
location /download/ { 
    proxy_pass http://download/vedio/; 
} 
#这是前端代理节点的设置
#交给后端 upstream 为 download 的节点
```

| proxy 模块参数 | 说明 |
| -- | -- |
| proxy_next_upstream | 什么情况下将请求传递到下一个 upstream |
| proxy_limite_rate | 限制从后端服务器读取响应的速率 |
| proyx_set_header | 设置 http 请求 header 传给后端服务器节点，如：可实现让代理后端的服务器节点获取访问客户端的这是 ip |
| client_body_buffer_size | 客户端请求主体缓冲区大小 |
| proxy_connect_timeout | 代理与后端节点服务器连接的超时时间 |
| proxy_send_timeout | 后端节点数据回传的超时时间 |
| proxy_read_timeout | 设置 Nginx 从代理的后端服务器获取信息的时间，表示连接成功建立后，Nginx 等待后端服务器的响应时间 |
| proxy_buffer_size | 设置缓冲区大小 |
| proxy_buffers | 设置缓冲区的数量和大小 |
| proyx_busy_buffers_size | 用于设置系统很忙时可以使用的 proxy_buffers 大小，推荐为 proxy_buffers的2倍 |
| proxy_temp_file_write_size | 指定 proxy 缓存临时文件的大小 |
### HAProxy
略
# 数据库
## 存储引擎
### 概念
数据库存储引擎是数据库底层软件组织，数据库管理系统（DBMS）使用数据引擎进行创建、查询、 更新和删除数据。不同的存储引擎提供不同的存储机制、索引技巧、锁定水平等功能，使用不同的存储引擎，还可以获得特定的功能。现在许多不同的数据库管理系统都支持多种不同的数据引擎。存储引擎主要有： 
1. MyIsam , 
2. InnoDB, 
3. Memory, 
4. Archive, 
5. Federated 
### InnoDB（B+树）
InnoDB 底层存储结构为B+树， B树的每个节点对应innodb的一个page，page大小是固定的， 一般设为 16k。其中非叶子节点只有键值，叶子节点包含完成数据。
![[Pasted image 20230904212030.png]]
适用场景： 
1) 经常更新的表，适合处理多重并发的更新请求。 
2) 支持事务。 
3) 可以从灾难中恢复（通过 bin-log 日志等）。 
4) 外键约束。只有他支持外键。 
5) 持自动增加列属性 auto_increment。
### TokuDB（Fractal Tree-节点带数据）
TokuDB 底层存储结构为 Fractal Tree,Fractal Tree 的结构与 B+树有些类似, 在 Fractal Tree 中，**每一个 child 指针除了需要指向一个 child 节点外，还会带有一个 Message Buffer ，这个 Message Buffer 是一个 FIFO 的队列，用来缓存更新操作**。 
例如，一次插入操作只需要落在某节点的 Message Buffer 就可以马上返回了，并不需要搜索到叶子节点。这些缓存的更新会在查询时或后台异步合并应用到对应的节点中。
![[Pasted image 20230904212351.png]]
TokuDB 在线添加索引，不影响读写操作, 非常快的写入性能， Fractal-tree 在事务实现上有优势。 他主要适用于访问频率不高的数据或历史数据归档。
### MyIASM
MyIASM是 MySQL默认的引擎，但是它没有提供对数据库事务的支持，也不支持行级锁和外键， 因此当 INSERT(插入)或 UPDATE(更新)数据时即写操作需要锁定整个表，效率便会低一些。 ISAM 执行**读取操作的速度很快**，而且不占用大量的内存和存储资源。在设计之初就预想数据组织成有固定长度的记录，按顺序存储的。---ISAM 是一种静态索引结构。 
**缺点是它不支持事务处理**。
### Memory
Memory（也叫 HEAP）堆内存：使用存在内存中的内容来创建表。每个 MEMORY 表只实际对应 一个磁盘文件。MEMORY 类型的表访问非常得快，因为它的数据是放在内存中的，并且默认使用 HASH 索引。但是一旦服务关闭，表中的数据就会丢失掉。 Memory **同时支持散列索引和 B 树索 引，B树索引可以使用部分查询和通配查询**，也可以使用<,>和>=等操作符方便数据挖掘，散列索 引相等的比较快但是对于范围的比较慢很多。
## 索引
索引（Index）是帮助 MySQL 高效获取数据的数据结构。常见的查询算法,顺序查找,二分查找,二 叉排序树查找,哈希散列法,分块查找,平衡多路搜索树 B 树（B-tree）
### 常见索引原则有
1. 选择唯一性索引：唯一性索引的值是唯一的，可以更快速的通过该索引来确定某条记录。 
2. 为经常需要排序、分组和联合操作的字段建立索引
3. 为常作为查询条件的字段建立索引。 
4. 限制索引的数目： 越多的索引，会使更新表变得很浪费时间。 尽量使用数据量少的索引 。
5. 如果索引的值很长，那么查询的速度会受到影响。 
6. 尽量使用前缀来索引 
7. 删除不再使用或者很少使用的索引 
8. 最左前缀匹配原则，非常重要的原则。 
9. 尽量选择区分度高的列作为索引：区分度的公式是表示字段不重复的比例 
10. 索引列不能参与计算，保持列“干净”：带函数的查询不参与索引。 
11. 尽量的扩展索引，不要新建索引。
## 数据库三范式
* 第一范式(1st NF －列都是不可再分)
* 第二范式(2nd NF－每个表只描述一件事情)
* 第三范式(3rd NF－ 不存在对非主键列的传递依赖)
## 数据库事务
事务(TRANSACTION)是作为单个逻辑工作单元执行的一系列操作，这些操作作为一个整体一起向 系统提交，要么都执行、要么都不执行 。事务是一个不可分割的工作逻辑单元 
事务必须具备以下四个属性，简称 ACID 属性：
1. 原子性（Atomicity）：事务是一个完整的操作。事务的各步操作是不可分的（原子的）；要么都执行，要么都不执 行。
2. 一致性（Consistency）：一致性（Consistency）
3. 隔离性（Isolation）：对数据进行修改的所有并发事务是彼此隔离的，这表明事务必须是独立的，它不应以任何方 式依赖于或影响其他事务。
4. 永久性（Durability）：事务完成后，它对数据库的修改被永久保持，事务日志能够保持事务的永久性。
## 存储过程(特定功能的 SQL 语句集)
一组为了完成特定功能的 SQL 语句集，存储在数据库中，经过第一次编译后再次调用不需要再次编译，用户通过指定存储过程的名字并给出参数（如果该存储过程带有参数）来执行它。存储过程是数据库中的一个重要对象。
存储过程优化思路：
1. 尽量利用一些 sql 语句来替代一些小循环，例如聚合函数，求平均函数等。 
2. 中间结果存放于临时表，加索引。 
3. 少使用游标。sql 是个集合语言，对于集合运算具有较高性能。而 cursors 是过程运算。比如 对一个 100 万行的数据进行查询。游标需要读表 100 万次，而不使用游标则只需要少量几次读取。 
4. 事务越短越好。sqlserver 支持并发操作。如果事务过多过长，或者隔离级别过高，都会造成 并发操作的阻塞，死锁。导致查询极慢，cpu 占用率极低。 
5. 使用 try-catch 处理错误异常。 
6. 查找语句尽量不要放在循环内。
## 触发器(一段能自动执行的程序)
触发器是一段能自动执行的程序，是一种特殊的存储过程，触发器和普通的存储过程的区别是： 触发器是当**对某一个表进行操作时触发。诸如：update、insert、delete 这些操作的时候，系统会自动调用执行该表上对应的触发器**。SQL Server 2005 中触发器可以分为两类：DML 触发器和 DDL 触发器，其中 DDL 触发器它们会影响多种数据定义语言语句而激发，这些语句有 create、 alter、drop 语句。
## 数据库并发策略
并发控制一般采用三种方法，分别是**乐观锁和悲观锁以及时间戳**。
### 乐观锁
乐观锁认为一个用户读数据的时候，别人不会去写自己所读的数据；
悲观锁就刚好相反，觉得自己读数据库的时候，别人可能刚好在写自己刚读的数据，其实就是持一种比较保守的态度；
时间戳就是不加锁，通过时间戳来控制并发出现的问题。
### 悲观锁
悲观锁就是在读取数据的时候，为了不让别人修改自己读取的数据，就会先对自己读取的数据加锁，只有自己把数据读完了，才允许别人修改那部分数据，或者反过来说，就是**自己修改某条数据的时候，不允许别人读取该数据**，只有等自己的整个事务提交了，才释放自己加上的锁，才允许其他用户访问那部分数据。

以上悲观锁所说的加“锁”，其实分为几种锁，分别是：**排它锁（写锁）和共享锁（读锁）**。
### 时间戳
时间戳就是在数据库表中单独加一列时间戳，比如“TimeStamp”，**每次读出来的时候，把该字段也读出来，当写回去的时候，把该字段加1，提交之前 ，跟数据库的该字段比较一次，如果比数据库的值大的话，就允许保存，否则不允许保存**，这种处理方法虽然不使用数据库系统提供的锁机制，但是这种方法可以大大提高数据库处理的并发量。