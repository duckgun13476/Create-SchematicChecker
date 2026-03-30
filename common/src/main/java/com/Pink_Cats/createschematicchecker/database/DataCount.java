package com.Pink_Cats.createschematicchecker.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.FileIO.createIfNotExists;
import static com.Pink_Cats.createschematicchecker.core.attach.Math.StringToInt;

public class DataCount {
    public static final String filePath = "config/CSC/Data/variables.data";

    public static void writeVariables(String filePath, Map<String, String> variables) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> readVariables(String filePath) throws IOException {
        Map<String, String> variables = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

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

    public static void main(String[] args) throws IOException {
        Map<String, String> CSCVariables = LoadVariables();

        int GuardTime = (StringToInt(CSCVariables.get("GuardTime")));
        GuardTime = GuardTime + 1345;

        CSCVariables.put("GuardTime", String.valueOf(GuardTime));
        WriteVariables(CSCVariables);
    }
}
