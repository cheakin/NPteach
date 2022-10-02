package cn.cheakin.gulimall.search.thread;

import java.util.concurrent.*;

/**
 * Create by botboy on 2022/09/26.
 **/
public class ThreadTest {

    // 系统中池应该只有一二三个，每个一部任务，提交给线程池让他自己去执行就行
    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main......start.....");


        /*CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }, executor);*/

        // 方法完成后处理1
        /*CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }, executor);*/
        // 方法完成后处理2
        /*CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 0;
            System.out.println("运行结果：" + i);
            return i;
        }, executor).whenComplete((res,exception) -> {
            //虽然能得到异常信息，但是没法修改返回数据
            System.out.println("异步任务成功完成了...结果是：" + res + "异常是：" + exception);
        }).exceptionally(throwable -> {
            //可以感知异常，同时返回默认值
            return 10;
        });*/

        // 方法执行完后端处理
        /*CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }, executor).handle((result, thr) -> {
            if (result != null) {
                return result * 2;
            }
            if (thr != null) {
                System.out.println("异步任务成功完成了...结果是：" + result + "异常是：" + thr);
                return 0;
            }
            return 0;
        });*/

        /*Integer integer = future.get();
        System.out.println("main......end....." + integer);*/


        /**
         * 线程串行化
         * 1、thenRunL：不能获取上一步的执行结果
         * 2、thenAcceptAsync：能接受上一步结果，但是无返回值
         * 3、thenApplyAsync：能接受上一步结果，有返回值
         */
        // 1、thenRunL：不能获取上一步的执行结果
        /*CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }, executor).thenRunAsync(() -> {
            System.out.println("任务2启动了...");
        }, executor);*/
        // 2、thenAcceptAsync：能接受上一步结果，但是无返回值
        /*CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }, executor).thenAcceptAsync(res -> {
            System.out.println("任务2启动了..." + res);
        }, executor);*/
        // 3、thenApplyAsync：能接受上一步结果，有返回值
        /*CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }, executor).thenApplyAsync(res -> {
            System.out.println("任务2启动了..." + res);
            return "Hello" + res;
        }, executor);
        System.out.println("main......end....." + future.get());*/


        /**
         * 两个任务组合
         */
        /*CompletableFuture<Object> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("任务1运行结果：" + i);
            return i;
        });
        CompletableFuture<Object> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程：" + Thread.currentThread().getId());
            try {
                Thread.sleep(3000);
                System.out.println("任务2运行结束");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello";
        }, executor);*/

        /**
         * 两任务组合-都要完成
         * runAfterBothAsync:   不需要任务1任务2的返回
         * thenAcceptBothAsync: 需要任务1任务2的返回
         * thenCombineAsync:    需要组合任务的返回
         */
        // runAfterBothAsync: 不需要任务1任务2的返回
        /*future01.runAfterBothAsync(future02, () -> {
            System.out.println("任务3开始");
        }, executor);*/
        // thenAcceptBothAsync: 需要任务1任务2的返回
        /*future01.thenAcceptBothAsync(future02, (f1, f2) -> {
            System.out.println("任务3开始...之前的结果" + f1 + "--->" + f2);
        }, executor);*/
        // thenCombineAsync: 需要组合任务的返回
        /*CompletableFuture<String> future = future01.thenCombineAsync(future02, (f1, f2) -> {
            return f1 + "：" + f2 + " -> hehe";
        }, executor);*/

        /**
         * 两任务组合-只要有一个完成，我们就执行任务3
         * runAfterEitherAsync: 不感知结果，自己业务返回值
         * acceptEitherAsync:   感知结果，自己没有返回值
         * applyToEitherAsync:  感知结果，自己有返回值
         */
        // runAfterEitherAsync: 不感知结果，自己业务返回值
        /*future01.runAfterEitherAsync(future02, () -> {
            System.out.println("任务3开始。。。之前的结果");
        }, executor);*/
        // acceptEitherAsync:   感知结果，自己没有返回值
        /*future01.acceptEitherAsync(future02, res -> {
            System.out.println("任务3开始。。。之前的结果" + res);
        }, executor);*/
        // applyToEitherAsync:  感知结果，自己有返回值
        /*CompletableFuture<String> future = future01.applyToEitherAsync(future02, res -> {
            System.out.println("任务3开始。。。之前的结果" + res);
            return res.toString() + "->哈哈";
        }, executor);*/

        /**
         * 多任务组合
         * allOf: 等待所有结果完成
         * anyOf: 任意一个完成
         */
        /*CompletableFuture<String> futureImag = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片信息");
            return "hello.jpg";
        });
        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片属性");
            return "黑色+256G";
        });
        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("查询商品介绍");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "华为";
        });*/
        // allOf: 等待所有结果完成
        /*CompletableFuture<Void> allOf = CompletableFuture.allOf(futureImag, futureAttr, futureDesc);
        allOf.get();*/
        // anyOf: 任意一个完成
        /*CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImag, futureAttr, futureDesc);
        anyOf.get();*/

        System.out.println("main......end.....");
    }


    public void thread(String[] args) throws ExecutionException, InterruptedException {
        // 1.继承Thread
        /*System.out.println("main......start.....");
        Thread thread = new Thread01();
        thread.start();
        System.out.println("main......end.....");*/

        // 2.实现Runable接口
        /*Runable01 runable01 = new Runable01();
        new Thread(runable01).start();*/

        // 3.实现FutureTask + FutureTask (可以i拿到返回结果, 可以处理异常)
        /*FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
        new Thread(futureTask).start();
        // 阻塞等待整个线程执行完成, 获取返回结果
        System.out.println(futureTask.get());*/

        // 我们以后在业务代码里面, 以上三种启动线程的方式都不用【将所有的线程异步任务都交给线程池执行】

        // 4.使用线程池[ExecutorService]
        executor.execute(new Runable01());

        /**
         * 区别：
         * 1,2都不能得到返回值，3可以获取返回值
         * 1，2，3都不能控制资源
         * 4可以控制资源，性能稳定。
         */


        // 系统中池应该只有一二三个，每个一部任务，提交给线程池让他自己去执行就行
        /**
         * 七大参数
         * corePoolSize:[5]核心线程书[一直存在除非(allowCoreThreadTimeOut];线程池，创建好以后就准备就绪的的线程数量，就等待来接受任务
         *      5个  Thread thred  = new Thred();    thread.start();
         * maximumPoolSize:[200] 最大线程数量；控制资源
         * keepAliveTime:存活时间， 如果当前线程数量大于核心数量
         *      释放空闲的线程(maximumPoolSize-corePoolSize)，只要线程空闲大于指定的keepAliveTime；
         * unit:时间单位
         * BlockingQueue<Runnable>：阻塞队列。如果任务有很多，就会将目前多的任务放在队列里面。
         *      只要有线程空闲，就会去队列里面取出新的任务执行
         * threadFactory：线程的创建工厂。
         * RejectedExecutionHandler handler：如果队列满了，按照我们指定的拒绝策略拒绝执行任务
         *
         * 工作顺序
         * 1、线程池创建，准备好 core 数量的核心线程，准备接受任务
         *  (1) 、core 满了，就将再进来的任务放入阻塞队列中。空闲的 core 就会自己去阻塞队列获取任务执行
         *  (2) 、阻塞队列满了，就直接开新线程执行，最大只能开到 max 指定的数量
         *  (3) 、max满了就用RejectedExecutionHandler拒绝任务
         *  (4) 、max都执行完成，有很多空闲，在指定的时间 keepAliveTime 以后，释放 max-core这些线程
         * 3、所有的线程创建都是由指定的 factory 创建的。
         *
         *      new LinkedBlockingDeque<>(), 默认是Integer的最大值。内存会不够
         *
         *
         *
         * 面试：
         * Q: 一个线程池 core 7； ； max 20  ，queue ：50 ，100 并发进来怎么分配的；
         * A: 先有 7 个能直接得到执行，接下来 50 个进入队列排队，在多开 13 个继续执行。现在 70 个被安排上了。剩下 30 个使用拒绝策略。
         */
        // 线程池方式一
        /*ThreadPoolExecutor executor = new ThreadPoolExecutor(5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());*/
        // 线程池方式二：
        /*Executors.newCachedThreadPool();    // core是0，所有都可回收。创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
        Executors.newFixedThreadPool(5);     // 固定大小,core=max:都不可回收。 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
        Executors.newScheduledThreadPool();     // 定时任务的线程池。 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
        Executors.newSingleThreadExecutor();    // 单线程的线程池, 后台从队列中获取任务, 依次执行。 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。*/


        // Future: 可以获取到异步结果

    }

    private static void threadPool() {

        ExecutorService threadPool = new ThreadPoolExecutor(
                200,
                10,
                10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        //定时任务的线程池
        ExecutorService service = Executors.newScheduledThreadPool(2);
    }


    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }


    public static class Runable01 implements Runnable {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }


    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }

}
