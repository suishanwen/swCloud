package com.sw.vote.util;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author sw
 */
public class ScheduledExecutorUtil {
    private volatile static ScheduledExecutorService pool;
    private static final List<Future> futures = Lists.newArrayList();

    private static ScheduledExecutorService getPool() {
        if (pool == null || pool.isShutdown()) {
            synchronized (ScheduledExecutorUtil.class) {
                if (pool == null || pool.isShutdown()) {
                    // 创建线程池
                    pool = Executors.newScheduledThreadPool(6);
                }
            }
        }
        return pool;
    }

    private static synchronized void addFuture(Future future) {
        futures.add(future);
    }

    @SuppressWarnings("unused")
    public static synchronized void cancel(Future future) {
        future.cancel(true);
        futures.remove(future);
    }

    @SuppressWarnings("unused")
    public static synchronized void cancelAll() {
        futures.forEach(future ->
                future.cancel(true)
        );
        futures.clear();
    }

    public static <T extends Runnable> void scheduleAtFixedRate(T task, long initialDelay, long period) {
        Future future = getPool().scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
        ScheduledExecutorUtil.addFuture(future);
    }

    @SuppressWarnings("unused")
    public static <V, T extends Callable<V>> Future<V> schedule(T task, long period) {
        Future<V> future = getPool().schedule(task, period, TimeUnit.SECONDS);
        ScheduledExecutorUtil.addFuture(future);
        return future;
    }
}
