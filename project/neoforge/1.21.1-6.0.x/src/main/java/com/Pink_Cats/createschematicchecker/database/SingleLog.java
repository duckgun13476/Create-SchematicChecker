package com.Pink_Cats.createschematicchecker.database;

import com.Pink_Cats.createschematicchecker.lang.Message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SingleLog {
    // 1. 定义默认日志路径（不指定路径时使用）
    private static final String DEFAULT_LOG_PATH = "config/Log/CSC_Record.log";
    // 日志文件最大容量（4MB）
    private static final long MAX_FILE_SIZE = 4194304L;

    // 成员变量
    private File logFile;
    private BufferedWriter writer;
    private String lastLogMessage = "";
    // 单例缓存：key=日志路径，value=对应路径的SingleLog实例（支持多路径同时使用）
    private static final java.util.Map<String, SingleLog> INSTANCE_CACHE = new java.util.HashMap<>();


    // 2. 私有构造方法（禁止外部直接创建，通过工厂方法控制）
    private SingleLog(String filePath) {
        // 初始化日志文件对象
        this.logFile = new File(filePath);
        // 确保父目录存在（不存在则创建）
        File parentDir = this.logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean dirCreated = parentDir.mkdirs();
            if (dirCreated) {
                System.out.println("Log parent directory created: " + parentDir.getAbsolutePath());
            }
        }
        // 初始化输出流（追加模式）
        initWriter();
        // 注册JVM关闭钩子：程序退出时自动关闭流（避免资源泄漏）
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }


    // 3. 初始化BufferedWriter（抽取为独立方法，避免代码重复）
    private void initWriter() {
        try {
            // 追加模式（true）：不覆盖原有日志，在末尾追加
            this.writer = new BufferedWriter(new FileWriter(this.logFile, true));
        } catch (IOException e) {
            System.err.println("Failed to initialize log writer for path: " + this.logFile.getAbsolutePath());
            e.printStackTrace();
        }
    }


    // 4. 静态工厂方法（核心：支持“指定路径”和“默认路径”，且保证单例）
    /**
     * 获取SingleLog实例
     * @param customPath 自定义日志路径（可为null，null时使用默认路径）
     * @return 对应路径的SingleLog单例
     */
    public static SingleLog getInstance(String... customPath) {
        // 确定最终使用的路径：有自定义路径则用自定义，无则用默认
        String targetPath = (customPath != null && customPath.length > 0 && customPath[0] != null)
                ? customPath[0]
                : DEFAULT_LOG_PATH;

        // 从缓存获取实例：不存在则创建并缓存（保证单例，避免重复打开流）
        return INSTANCE_CACHE.computeIfAbsent(targetPath, SingleLog::new);
    }


    // 5. 获取当前时间戳（格式：yyyy-MM-dd HH:mm:ss）
    private String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }


    // 6. 日志写入方法（核心逻辑不变，优化异常提示）
    public void log(String message) {
        // 避免重复写入相同日志
        if (message.equals(this.lastLogMessage)) {
            return;
        }
        // 流未初始化则跳过（避免空指针）
        if (this.writer == null) {
            System.err.println("Log writer not initialized, skip logging: " + message);
            return;
        }

        try {
            // 日志文件超过最大容量时归档
            if (this.logFile.length() >= MAX_FILE_SIZE) {
                archiveLogFile();
            }

            // 拼接日志内容（时间戳 + 业务消息）
            String logContent = getCurrentTimestamp() + " " + message;
            this.writer.write(logContent);
            this.writer.newLine(); // 换行，保证每条日志独立一行
            this.writer.flush(); // 强制刷盘，避免日志滞留缓冲区
            this.lastLogMessage = message; // 更新最后一条日志，用于去重

        } catch (IOException e) {
            System.err.println("Failed to write log message: " + message);
            e.printStackTrace();
        }
    }


    // 7. 日志归档方法（优化异常处理和路径合法性）
    private void archiveLogFile() {
        // 生成归档文件名（格式：原文件名_时间戳.log，避免重复）
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String originalName = this.logFile.getName();
        String archivedName = originalName.replace(".log", "_" + timestamp + ".log");
        // 归档文件路径 = 原文件父目录 + 归档文件名（避免跨目录问题）
        File archivedFile = new File(this.logFile.getParent(), archivedName);

        try {
            // 先关闭原流（避免文件被占用，导致重命名失败）
            this.writer.close();

            // 执行归档（重命名原文件为归档文件）
            boolean archived = this.logFile.renameTo(archivedFile);
            if (archived) {
                Message.FM("Log file archived successfully: " + archivedFile.getAbsolutePath());
            } else {
                System.err.println("Failed to archive log file: " + this.logFile.getAbsolutePath()
                        + " → " + archivedFile.getAbsolutePath());
            }

            // 重新初始化流（继续写入新日志）
            initWriter();

        } catch (IOException e) {
            System.err.println("Error during log archive: " + e.getMessage());
            e.printStackTrace();
            // 归档失败时重新初始化流，避免后续日志无法写入
            initWriter();
        }
    }


    // 8. 关闭流资源（程序退出时自动调用，也支持手动调用）
    public void close() {
        if (this.writer != null) {
            try {
                this.writer.flush(); // 关闭前最后一次刷盘，确保缓冲区无残留
                this.writer.close();
                System.out.println("Log writer closed: " + this.logFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to close log writer: " + this.logFile.getAbsolutePath());
                e.printStackTrace();
            } finally {
                this.writer = null; // 置空，避免重复关闭
            }
        }
    }

    public void reopen() {
        // 如果流已经关闭（为null），则重新初始化
        if (this.writer == null) {
            initWriter();
        }
    }

    public static SingleLog CSC_MES = SingleLog.getInstance("config/CSC/Log/CSC_Record.log");
    public static SingleLog CSC_WARN = SingleLog.getInstance("config/CSC/CSC_Warn.log");

    // 9. 测试方法（验证“默认路径”和“指定路径”两种场景）
    public static void main(String[] args) {
        // 场景1：不指定路径 → 使用默认路径（config/Log/CSC_Record.log）

        CSC_MES.log("Default path log: This is a test message.");
        CSC_WARN.log("Default path log: Reuse instance, no new writer created.");

        // 手动关闭（实际项目中可依赖JVM关闭钩子自动关闭）
        CSC_MES.close();
        CSC_WARN.close();
    }
}