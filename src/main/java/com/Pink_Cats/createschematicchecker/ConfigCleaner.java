package com.Pink_Cats.createschematicchecker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigCleaner {

    private static final String CONFIG_FILE_PATH = "config/createschematicchecker-common.toml";
    private static final int MAX_ATTEMPTS = 100;
    private static final long WAIT_TIME_MS = 5000; // 5秒

    public static void cleanConfigFile() {
        Path path = Paths.get(CONFIG_FILE_PATH);
        Path tempFile = Paths.get("config/temp.toml");

        // 检查文件是否存在
        if (!Files.exists(path)) {
            System.err.println("Error: Configuration file does not exist.");
            return;
        }

        // 使用新线程来处理文件清理
        new Thread(() -> {
            int attempts = 0;
            boolean fileAccessible = false;

            while (attempts < MAX_ATTEMPTS && !fileAccessible) {
                try {
                    // 尝试读取文件
                    try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
                         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            // 删除以 '#' 开头的注释行
                            if (!line.trim().startsWith("#")) {
                                writer.write(line);
                                writer.newLine();
                            }
                        }

                        // 替换原文件
                        Files.delete(path);
                        Files.move(tempFile, path);
                        fileAccessible = true; // 文件成功处理

                    } catch (IOException e) {
                        // 文件可能被占用，增加尝试次数
                        attempts++;
                        System.err.println("File is currently in use. Attempt " + attempts + " of " + MAX_ATTEMPTS);
                        Thread.sleep(WAIT_TIME_MS); // 等待5秒后重试
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // 处理线程中断
                }
            }

            if (!fileAccessible) {
                System.err.println("Error: Unable to access the configuration file after " + MAX_ATTEMPTS + " attempts.");
            } else {
                System.out.println("Config file cleaned successfully.");
            }
        }).start(); // 启动新线程
    }

    public static void main(String[] args) {
        cleanConfigFile();
    }
}
