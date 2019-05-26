package com.inuker.bluetooth.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class ThreadPoolManager {
    private final String TAG = ThreadPoolManager.class.getSimpleName();
    private static ThreadPoolManager threadPoolManager;
    private ExecutorService executorService;

    private ThreadPoolManager() {
        ThreadFactoryBuilder builder = new ThreadFactoryBuilder()
                .setNamePrefix("HH_Thread")
                .setDaemon(false)
                .setPriority(Thread.MAX_PRIORITY)
                .setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        Log.e(TAG, t.getName() + " error:" + e.toString());
                    }
                });
        ThreadFactory threadFactory = builder.build();
        executorService = Executors.newCachedThreadPool(threadFactory);
    }

    public static ThreadPoolManager getInstance() {
        if (threadPoolManager == null) {
            synchronized (ThreadPoolManager.class) {
                if (threadPoolManager == null) {
                    threadPoolManager = new ThreadPoolManager();
                }
            }
        }
        return threadPoolManager;
    }

    public Future<?> submit(@NonNull Runnable task) {
        return executorService.submit(task);
    }

    public Future<?> submit(@NonNull Callable task) {
        return executorService.submit(task);
    }

    public void cancel(Future<?> future) {
        if (future == null) {
            return;
        }
        future.cancel(false);
    }

    public void shutdown() {
        if (!executorService.isTerminated()) {
            executorService.shutdownNow();
        }
    }
}
