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