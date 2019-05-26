package com.inuker.bluetooth.concurrency;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ScheduleThreadPoolManager {

    private final String TAG = ScheduleThreadPoolManager.class.getSimpleName();
    private static ScheduleThreadPoolManager threadPoolManager;
    private ScheduledExecutorService executorService;

    private ScheduleThreadPoolManager() {
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
        executorService = Executors.newScheduledThreadPool(6, threadFactory);
    }

    public static ScheduleThreadPoolManager getInstance() {
        if (threadPoolManager == null) {
            synchronized (ScheduleThreadPoolManager.class) {
                if (threadPoolManager == null) {
                    threadPoolManager = new ScheduleThreadPoolManager();
                }
            }
        }
        return threadPoolManager;
    }

    public void shutdown() {
        if (!executorService.isTerminated()) {
            executorService.shutdownNow();
        }
    }

    public void cancelTask(ScheduledFuture scheduledFuture) {
        if(scheduledFuture == null) {
            return;
        }
        scheduledFuture.cancel(true);
    }

    public ScheduledFuture schedule(@NonNull Runnable command, long initDelay, TimeUnit timeUnit) {
        return executorService.schedule(command, initDelay, timeUnit);
    }

    public ScheduledFuture schedule(@NonNull Callable function, long initDelay, TimeUnit timeUnit) {
        return executorService.schedule(function, initDelay, timeUnit);
    }

    public ScheduledFuture scheduleWithFixedDelay(@NonNull Runnable command, long initDelay, long delay, TimeUnit timeUnit) {
        return executorService.scheduleWithFixedDelay(command, initDelay, delay, timeUnit);
    }

    public ScheduledFuture scheduleAtFixedRate(@NonNull Runnable command, long initDelay, long period, TimeUnit timeUnit) {
        return executorService.scheduleAtFixedRate(command, initDelay, period, timeUnit);
    }

}
