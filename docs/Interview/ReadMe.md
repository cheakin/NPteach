# JVM
**基本概念**
JVM是可运行Java代码的假想计算机，包括一套指令集、一组寄存器、一个栈、一个垃圾回收、堆、一个存储方法域。JVMu是运行再操作系统只上的，它与硬件没有直接的交互。
JVM
    * java代码执行
        * 代码编译为class，即javac
        * 装载class，即Classloader
        * 执行class
            * 解释执行
            * 编译执行
                * client compiler
                * server compiler
    * 内存管理
        * 内存空间
            * 方法区
            * 堆
            * 方法栈
            * 本地方法栈
            * pc寄存器
        * 内存分配
            * 堆上分配
            * TLAB分配
            * 栈上分配
        * 内存回收
            * 算法
                * Copy
                * Mark-Sweep
                * Mark-Compact
            * Sun JDK
                * 分代回收
                    * 新生代可用的GC
                        * 串行copying
                        * 并行回收copying
                        * 并行copying
                    * Minor GC触发机制以及日志格式
                    * 旧生代可用的GC
                    * Full GC 触发机制以及日志格式
                * GC参数
                * G1
        * 内存状况分析
            * jconsle
            * visualvm
            * jmap
            * MAT
    * 线程资源同步和交互机制
        * 线程资源同步
            * 线程资源执行机制
            * 线程资源同步机制
                * Sunchronized的实现机制
                * lock/unlock的实现机制
        * 线程交互机制
            * Object.wait/notify/notifyAll - Double check pattem
            * 并发包提供的交互机制
                * semaphore
                * CountdownLatch
        * 线程状态即分析方法
            * jstack
            * TDA

**运行过程**
java源文件通过编译器，生产相应的.Class文件，也即是字节码文件，而字节码文件又通过Java虚拟机中的解释器，变异成特定机器上的机器码
    java源文件->编译码->字节码文件
    自建吗文件->JVM->机器码
每一种平台的解释器是不同的，但是现实现的虚拟机时相同的，这也就是Java为什么能够跨平台的原因了，当一个程序从开始运行，这是虚拟机就开始实例化了，多个程序就会存在多个虚拟机实例。程序退出或者关闭，则虚拟机实例消亡，多个虚拟机实例之间数据不能共享。

## 线程
这里所说的线程只程序执行过程中的一个线程实体。JVM允许一个应用并发执行多个线程。
Hotspot JVM中Java线程与原生操作系统线程又直接的映射关系。当线程本地存储、缓冲区分配、同步对象、站、程序计数器等准备好以后，就会创建一个操作系统远程线程。Java线程结束，远程线程随之被回收。操作系统负责调度所有线程，并把他们分配到任何可用的CPU上。当远程线程初始化完毕，就会调用Java线程的run()方法。当线程结束时，会释放远程线程和Java线程的所有资源。

## JVM内存
    * 线程私有 Thread Local
        * 程序技术器 PC
            * 指向虚拟机字节码指令的位置
            * 唯一一个无 OOM 的区域
        * 虚拟机栈 VM Stack
            * 虚拟机栈和线程生命周期相同
            * 一个线程中，没底啊用一个方法创建一个栈帧(Stack Fram)
            * 栈帧的结构
                * 本地变量表 Local Variable
                * 操作数栈 Operand Stack
                * 对运行时常量池的引用 Runtime Constant Pool Reference
            * 异常
                * 线程请求的栈深度大于JVM所允许的深度 StackOverflowError
                * 若JVM允许动态扩展，若无法盛情到足够内存 OutOfmemoryError
        * 本地方法栈 Native Method Stack
            * 异常
                * 线程请求的栈深度大于JVM所允许的深度 StackOverflowError
                * 若JVM允许动态扩展，若无法盛情到足够内存 OutOfmemoryError
    * 线程共享 Thread Shared
        * 方法去(永久代) Method Area
            * 运行时常量池Runtime Constant Pool
        * 类实例(java堆)Objects
            * 新生代
                * eden
                * from servivor
                * to survivor
            * 老年代
            * 异常
                * OutOfMemoryError
    * 直接内存 Direct Memory
        * 不受JVM GC管理

## JVM GC垃圾回收与算法
* gc要做的三件事
    * 那些内存需要回收
    * 什么时候回收
    * 怎么回收
* 哪些对象已经“死亡”
    * 引用计数法Reference Counting——循环引用的问题
    * 根搜索算法GC Roots Tracing
        * 通过一系列成为GC Roots的点作为起点，向下搜索。当一个对象到任何GC Roots都没有引用链项链，说明其已经“死亡”
        * GC Roots
            * VM栈中的引用
            * 方法去中的静态引用
            * JNI中的引用
* 垃圾收集算法
    * 标记清除Mark-Sweep
        * 效率低
        * 内存碎片多
    * 复制Coping
        1. eden
        2. survivor
    * 标记整理Mark-Compat
    * 分代收集Generational Collecting
* **垃圾收集器**
    * Serial
    * ParNew
    * Parallel Scavenge
    * Serial Old
    * Parallel Old
    * CMS - Concurrent Mark Sweep
* 参数
    * Xms
    * Xmx
    * Xmn
    * -XX:+PrintGCDetails
    * -XX:SurvivorRatio=8
    * -XX:MaxTenuringThreshold
    * -XX:-HandlePromotionFailure

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

Java NIO


