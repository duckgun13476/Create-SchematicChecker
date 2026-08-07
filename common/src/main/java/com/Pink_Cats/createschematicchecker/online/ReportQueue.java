package com.Pink_Cats.createschematicchecker.online;

import com.Pink_Cats.createschematicchecker.lang.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Durable outbox for scanner-classified schematic samples.
 *
 * <p>Files stay in the queue until the v2 ingress has acknowledged them. A
 * failed request therefore never changes the scan result and is retried when a
 * later queue flush can reach the ingress.</p>
 */
public final class ReportQueue {

    private static final Path QUEUE_DIRECTORY = Paths.get("config", "CSC", "report-queue");
    private static final int MAX_PENDING_FILES = 64;
    private static final long MAX_PENDING_BYTES = 64L * 1024L * 1024L;
    private static final Object LOCK = new Object();

    private ReportQueue() {
    }

    /**
     * Retains a newly written sample when the local outbox has room for it.
     * The caller owns the file when this method returns {@code false}.
     */
    public static boolean retainNewSample(File sample) {
        synchronized (LOCK) {
            try {
                QueueStats stats = queueStats();
                if (stats.fileCount > MAX_PENDING_FILES
                        || stats.totalBytes > MAX_PENDING_BYTES) {
                    Message.FW("CSC report queue is full; kept scanning but discarded one report sample.");
                    return false;
                }
                return true;
            } catch (IOException exception) {
                Message.FW("CSC report queue is unavailable; kept scanning but discarded one report sample.");
                return false;
            }
        }
    }

    /**
     * Sends queued samples oldest first. The first failure is retained and
     * ends this pass, preventing repeated network attempts during an outage.
     *
     * @return true when every sample seen in this pass was acknowledged
     */
    public static boolean flushPendingReports() {
        synchronized (LOCK) {
            try {
                for (Path pendingFile : pendingFiles()) {
                    try {
                        NbtFileUploader.uploadNbtFile(pendingFile.toString());
                        Files.deleteIfExists(pendingFile);
                    } catch (Exception exception) {
                        Message.FW("CSC report upload deferred; the sample will retry later: "
                                + exception.getMessage());
                        return false;
                    }
                }
                return true;
            } catch (IOException exception) {
                Message.FW("CSC report queue could not be read; it will retry later: " + exception.getMessage());
                return false;
            }
        }
    }

    public static boolean hasPendingReports() {
        synchronized (LOCK) {
            try {
                return !pendingFiles().isEmpty();
            } catch (IOException ignored) {
                return false;
            }
        }
    }

    private static List<Path> pendingFiles() throws IOException {
        if (!Files.isDirectory(QUEUE_DIRECTORY)) {
            return new ArrayList<>();
        }

        List<Path> files = new ArrayList<>();
        try (java.util.stream.Stream<Path> paths = Files.walk(QUEUE_DIRECTORY)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".nbt"))
                    .forEach(files::add);
        }
        files.sort(Comparator.comparing(ReportQueue::lastModifiedSafely));
        return files;
    }

    private static FileTime lastModifiedSafely(Path path) {
        try {
            return Files.getLastModifiedTime(path);
        } catch (IOException ignored) {
            return FileTime.fromMillis(Long.MAX_VALUE);
        }
    }

    private static QueueStats queueStats() throws IOException {
        int fileCount = 0;
        long totalBytes = 0;
        for (Path pendingFile : pendingFiles()) {
            fileCount++;
            totalBytes += Files.size(pendingFile);
        }
        return new QueueStats(fileCount, totalBytes);
    }

    private static final class QueueStats {
        private final int fileCount;
        private final long totalBytes;

        private QueueStats(int fileCount, long totalBytes) {
            this.fileCount = fileCount;
            this.totalBytes = totalBytes;
        }
    }
}
