package com.Pink_Cats.createschematicchecker.database;

import com.Pink_Cats.createschematicchecker.lang.Message;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.FileIO.createIfNotExists;
import static com.Pink_Cats.createschematicchecker.core.attach.Math.StringToInt;

public class DataCount {
    public static final String filePath = "config/CSC/Data/variables.data";
    /**
     * 将变量写入文件
     * @param filePath 文件路径
     * @param variables 要写入的变量键值对
     * @throws IOException 当IO操作失败时抛出
     */
    public static void writeVariables(String filePath, Map<String, String> variables) {
        // 使用try-with-resources确保资源自动关闭
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                // 写入格式: 变量名=变量值
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件读取变量
     * @param filePath 文件路径
     * @return 包含所有变量的键值对Map
     * @throws IOException 当IO操作失败时抛出
     */
    public static Map<String, String> readVariables(String filePath) throws IOException {
        Map<String, String> variables = new HashMap<>();

        // 使用try-with-resources确保资源自动关闭
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过空行和注释行
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                // 分割键值对
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    variables.put(parts[0].trim(), parts[1].trim());
                }
            }
        }

        return variables;
    }

    public static Map<String, String> LoadVariables() {

        createIfNotExists("config/CSC/Data");
        try {
            return readVariables(filePath);


        } catch (IOException e) {
            Map<String, String> variables = new HashMap<>();
            variables.put("GuardTime", "0");
            variables.put("CheckCount", "0");
            variables.put("ProblemCount", "0");
            variables.put("CheatCount", "0");
            writeVariables(filePath, variables);
            return variables;
        }
    }

    public static void WriteVariables(Map<String, String> variables) {
        writeVariables(filePath, variables);
    }



    // 示例用法
    public static void main(String[] args) throws IOException {
        Map<String, String> CSCVariables = LoadVariables();

        int GuardTime = (StringToInt(CSCVariables.get("GuardTime")));
        GuardTime = GuardTime + 1345;

        CSCVariables.put("GuardTime", String.valueOf(GuardTime));


        WriteVariables(CSCVariables);

    }
}
