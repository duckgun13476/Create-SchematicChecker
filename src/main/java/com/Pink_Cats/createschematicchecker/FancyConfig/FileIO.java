package com.Pink_Cats.createschematicchecker.FancyConfig;

import java.io.File;

public class FileIO {

    public static void createIfNotExists(String path) {
        File file = new File(path);

        if (!file.exists()) {
            boolean created = file.mkdirs();

            if (created) {
                System.out.println("Path created successfully: " + path);
            } else {
                System.out.println("Failed to create path: " + path);
            }
        } else {
            System.out.println("Path already exists: " + path);
        }
    }


    public static void listFilesInDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        // 获取目录中的所有文件和文件夹
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("Directory: " + file.getName());
                } else {
                    System.out.println("File: " + file.getName());
                }
            }
        } else {
            System.out.println("No files found in the directory.");
        }
    }
}
