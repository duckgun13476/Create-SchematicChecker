package com.Pink_Cats.createschematicchecker.network;

import com.Pink_Cats.createschematicchecker.lang.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.enable_auto_config_update;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.report_schematic;
import static com.Pink_Cats.createschematicchecker.online.NbtFileUploader.AutoUpdateThread;
import static com.Pink_Cats.createschematicchecker.online.ReportQueue.flushPendingReports;
import static com.Pink_Cats.createschematicchecker.online.ReportQueue.hasPendingReports;
import static com.Pink_Cats.createschematicchecker.online.SimpleHeartbeatPusher.HeartBeatTask;
import static com.Pink_Cats.createschematicchecker.online.VersionChecker.UpdateMainThread;

public class OnlineTasks {

    private static final Object LOCK = new Object();
    private static final int MAX_QUEUE_SIZE = 16;
    private static volatile boolean acceptingTasks;
    private static volatile ThreadPoolExecutor executor;
    private static final AtomicBoolean reportFlushScheduled = new AtomicBoolean(false);

    public static void startServer() {
        synchronized (LOCK) {
            acceptingTasks = true;
            if (executor == null || executor.isShutdown() || executor.isTerminated()) {
                executor = newExecutor();
                // No scan thread may be responsible for first creating this worker.
                executor.prestartCoreThread();
            }
        }
        requestPendingReportFlush();
    }

    public static void stopServer() {
        acceptingTasks = false;
        synchronized (LOCK) {
            if (executor != null) {
                executor.shutdownNow();
                executor = null;
            }
        }
    }

    public static void readDataAsync() {
        submit(() -> {
            UpdateMainThread();
            HeartBeatTask();
        });
    }

    public static void reportProblem(String filepath) {
        requestPendingReportFlush();
    }

    public static void postDataAsync(String filepath) {
        if (enable_auto_config_update) {
            submit(() -> AutoUpdateThread(filepath));
        }
    }

    private static void requestPendingReportFlush() {
        if (!report_schematic || !reportFlushScheduled.compareAndSet(false, true)) {
            return;
        }
        if (!submit(() -> {
            try {
                boolean completed = flushPendingReports();
                if (completed && hasPendingReports()) {
                    reportFlushScheduled.set(false);
                    requestPendingReportFlush();
                }
            } finally {
                reportFlushScheduled.set(false);
            }
        })) {
            reportFlushScheduled.set(false);
        }
    }

    private static boolean submit(Runnable task) {
        if (!acceptingTasks) {
            return false;
        }

        ThreadPoolExecutor current = executor;
        if (current == null) {
            return false;
        }

        try {
            current.execute(() -> {
                if (!acceptingTasks) {
                    return;
                }
                try {
                    task.run();
                } catch (Throwable throwable) {
                    if (throwable instanceof ThreadDeath) {
                        throw (ThreadDeath) throwable;
                    }
                    Message.FW("Online task failed: " + throwable.getMessage());
                }
            });
            return true;
        } catch (java.util.concurrent.RejectedExecutionException ignored) {
            if (acceptingTasks) {
                Message.FW("Online task queue is full; skipped a non-critical task.");
            }
            return false;
        }
    }

    private static ThreadPoolExecutor newExecutor() {
        return new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(MAX_QUEUE_SIZE),
                daemonFactory("CSC-Online"),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    private static ThreadFactory daemonFactory(String prefix) {
        AtomicInteger nextId = new AtomicInteger(1);
        return runnable -> {
            Thread thread = new Thread(runnable, prefix + "-" + nextId.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        };
    }
}
