package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.lang.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Owns schematic scans for the lifetime of a server.  In particular, scans must
 * not outlive the server which accepted the upload.
 */
public final class SchematicScanTasks {
    private static final Object LOCK = new Object();
    private static final int MAX_QUEUE_SIZE = 8;
    private static volatile boolean acceptingTasks;
    private static volatile ThreadPoolExecutor executor;

    private SchematicScanTasks() {
    }

    public static void startServer() {
        synchronized (LOCK) {
            acceptingTasks = true;
            if (executor == null || executor.isShutdown() || executor.isTerminated()) {
                executor = newExecutor();
                // Creating this daemon during startup prevents an upload scan from
                // becoming the thread that has to create a worker later.
                executor.prestartCoreThread();
            }
        }
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

    public static boolean submit(Runnable scan, Runnable rejectedWhileRunning) {
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
                    scan.run();
                } catch (Throwable throwable) {
                    if (throwable instanceof ThreadDeath) {
                        throw (ThreadDeath) throwable;
                    }
                    Message.FE("Schematic scan task failed: " + throwable.getMessage());
                }
            });
            return true;
        } catch (java.util.concurrent.RejectedExecutionException ignored) {
            if (acceptingTasks) {
                rejectedWhileRunning.run();
                Message.FW("Schematic scan queue is full; rejected the upload safely.");
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
                daemonFactory("CSC-SchematicScan"),
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
