package com.Pink_Cats.createschematicchecker.FancyConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigHook {

    public static String validateToml(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            boolean readingArray = false;
            String arrayKey = null;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();

                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                if (readingArray) {
                    String cleanLine = trimmed.split("#", 2)[0].trim();
                    if (cleanLine.endsWith("]")) {
                        readingArray = false;
                        arrayKey = null;
                    }
                    continue;
                }

                if (trimmed.startsWith("[")) {
                    if (!trimmed.endsWith("]")) {
                        return "Invalid table header at line " + lineNumber + ": " + trimmed;
                    }
                    if (trimmed.length() <= 2) {
                        return "Empty table header at line " + lineNumber;
                    }
                    continue;
                }

                if (!trimmed.contains("=")) {
                    return "Invalid key-value pair at line " + lineNumber + ": " + trimmed;
                }

                String[] parts = trimmed.split("=", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();
                if (key.isEmpty()) {
                    return "Empty key at line " + lineNumber;
                }
                if (value.isEmpty()) {
                    return "Empty value for key '" + key + "' at line " + lineNumber;
                }

                if (value.startsWith("[") && !value.endsWith("]")) {
                    readingArray = true;
                    arrayKey = key;
                }
            }

            if (readingArray) {
                return "Unclosed array for key '" + arrayKey + "'";
            }
        } catch (IOException e) {
            return "Unable to read config: " + e.getMessage();
        }

        return null;
    }

    public static void writeToml(String filePath, Map<String, String> tomlData) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, String> entry : tomlData.entrySet()) {
                writer.write(entry.getKey() + " = \"" + entry.getValue() + "\"");
                writer.newLine();
            }
        }
    }

    public static Map<String, Object> readToml(String filePath) {
        Map<String, Object> tomlData = new HashMap<>();
        String currentTable = null;

        try {
            // 检查文件是否存在，如果不存在则创建一个新的空文件
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile(); // 创建新文件
                // 可选择在此写入一些默认配置
                // try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                //     writer.write("# Default configuration\n");
                // }
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                StringBuilder arrayValueBuilder = new StringBuilder();
                String key = null;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    // 忽略空行
                    if (line.isEmpty()) {
                        continue;
                    }
                    // 处理表格
                    if (line.startsWith("[") && line.endsWith("]")) {
                        currentTable = line.substring(1, line.length() - 1).trim();
                        continue;
                    }
                    // 处理键值对
                    if (line.contains("=")) {
                        // 检查是否为数组
                        String[] parts = line.split("=", 2);
                        key = parts[0].trim();
                        String value = parts[1].trim();

                        // 如果值是数组（以 [ 开头）
                        if (value.startsWith("[")) {
                            arrayValueBuilder.append(value); // 添加当前行的值
                            // 继续读取后续行，直到找到对应的 ]
                            while (!((line = reader.readLine()) == null)) {
                                line = line.trim();
                                if (line.isEmpty()) {
                                    continue; // 忽略空行
                                }
                                // 去掉注释部分
                                String[] splitLine = line.split("#", 2);
                                String cleanLine = splitLine[0].trim(); // 只保留 # 前面的部分
                                arrayValueBuilder.append(" ").append(cleanLine); // 添加后续的行
                                if (cleanLine.endsWith("]")) {
                                    break; // 找到数组结束
                                }
                            }
                            // 处理完整的数组内容
                            Object arrayValue = parseArray(arrayValueBuilder.toString());
                            // 如果有当前表格，则将键合并
                            if (currentTable != null) {
                                key = currentTable + "." + key;
                            }
                            tomlData.put(key, arrayValue);
                            arrayValueBuilder.setLength(0); // 重置 StringBuilder
                        } else {
                            // 处理普通键值对
                            Object valueObj = parseValue(value);
                            // 如果有当前表格，则将键合并
                            if (currentTable != null) {
                                key = currentTable + "." + key;
                            }
                            tomlData.put(key, valueObj);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tomlData;
    }




    private static Object parseValue(String value) {
        value = value.trim();

        // 处理数组
        if (value.startsWith("[") && value.endsWith("]")) {
            return parseArray(value);
        }

        // 去掉引号
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }

        // 检测数据类型
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        } else if (value.matches("\\d+")) {
            return Integer.parseInt(value);
        } else if (value.matches("\\d+\\.\\d+")) {
            return Double.parseDouble(value);
        } else {
            return value; // 默认作为字符串处理
        }
    }

    private static List<String> parseArray(String value) {
        List<String> arrayValues = new ArrayList<>();
        // 去掉方括号并将内容中的换行符替换为空格
        value = value.substring(1, value.length() - 1).replace("\n", " ").trim();

        // 使用正则表达式分割数组元素，考虑到可能的引号
        String[] items = value.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String item : items) {
            item = item.trim();
            // 去掉引号
            if (item.startsWith("\"") && item.endsWith("\"")) {
                item = item.substring(1, item.length() - 1);
            }
            arrayValues.add(item);
        }
        return arrayValues;
    }
}
