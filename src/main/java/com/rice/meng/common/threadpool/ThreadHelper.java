package com.rice.meng.common.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

import static com.rice.meng.common.threadpool.ThreadComponent.createThreadFactory;

@Slf4j
public class ThreadHelper {

    /**
     * 默认拒绝策略
     */
    private static final RejectedExecutionHandler defaultHandler = new ThreadComponent.DefaultRejected();

    /**
     * 创建固定数量线程的线程池
     *
     * @param nThreads  固定线程数
     * @param queueSize 任务队列大小, 默认使用 LinkedBlockingQueue 类型
     * @param factoryName 线程工厂名称
     * @return 使用默认拒绝策略创建的线程池实例
     */
    public static ExecutorService createFixedThreadPool(int nThreads, int queueSize, String factoryName) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueSize),
                createThreadFactory(factoryName), defaultHandler);
    }

    /**
     * 创建周期性任务执行的线程池
     *
     * @param corePoolSize 核心线程数
     * @param factoryName 线程工厂名称
     * @return 使用默认拒绝策略创建的线程池实例
     */
    public static ScheduledExecutorService createScheduledExecutor(int corePoolSize, String factoryName) {
        return new ScheduledThreadPoolExecutor(corePoolSize, createThreadFactory(factoryName), defaultHandler);
    }

    /**
     * 创建周期性任务执行的线程池
     *
     * @param corePoolSize 核心线程数
     * @param factoryName 线程工厂名称
     * @param handler 自定义拒绝策略
     * @return 使用自定义拒绝策略创建的线程池实例
     */
    public static ScheduledExecutorService createScheduledExecutor(int corePoolSize, String factoryName, RejectedExecutionHandler handler) {
        return new ScheduledThreadPoolExecutor(corePoolSize, createThreadFactory(factoryName), handler);
    }

}
