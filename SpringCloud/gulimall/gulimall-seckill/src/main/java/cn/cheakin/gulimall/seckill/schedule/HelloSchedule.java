package cn.cheakin.gulimall.seckill.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *  定时任务
 *      1. @EnableScheduling 开启定时任务
 *      2. @Scheduled    开启一个定时任务
 *      3. 自动配置 TaskSchedulingAutoConfiguration
 *
 *  异步任务
 *      1. @EnableAsync 开启异步任务功能
 *      2. @Async 给希望异步执行的方法上标注
 *      3. 自动配置类 TaskExecutionAutoConfiguration 属性绑定在TaskExecutionProperties
 *
 */

//@EnableAsync
//@EnableScheduling
@Component
@Slf4j
public class HelloSchedule {

    /**
     * 1. Spring中由6位组成，不允许第7位的年
     * 2. 在周几的位置，1-7代表周一到周日，MON-SUN也可以
     * 3. 定时任务不应该阻塞。默认是阻塞的
     *      1）可以让业务运行以异步的方式，自己提交到线程池
     *          CompletableFuture.runAsync(() -> {
     *             xxxService.hello();
     *         }, executor);
     *      2) 支持定时任务线程池:设置 TaskSchedulingProperties
     *          spring.task.scheduling.pool.size=5
     *      3) 让定时任务异步执行
     *
     *      解决：使用异步+定时任务来完成定时任务不阻塞的功能
     *
     *
     * @throws InterruptedException
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Async
    public void hello() throws InterruptedException {
        Thread.sleep(3000);
        log.info("hello...");

        /*CompletableFuture.runAsync(() -> {
            xxxService.hello();
        }, executor);*/
    }
}