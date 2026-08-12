package com.Pink_Cats.createschematicchecker.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.FileIO.createIfNotExists;
import static com.Pink_Cats.createschematicchecker.core.attach.Math.StringToInt;

public class DataCount {
    public static final String filePath = "config/CSC/Data/variables.data";
    private static final String GUARD_TIME = "GuardTime";
    private static final String CHECK_COUNT = "CheckCount";
    private static final String PROBLEM_COUNT = "ProblemCount";
    private static final String CHEAT_COUNT = "CheatCount";

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
        Map<String, String> loaded;
        try {
            loaded = readVariables(filePath);
        } catch (IOException e) {
            loaded = new HashMap<>();
        }

        Map<String, String> normalized = normalizeVariables(loaded);
        if (!normalized.equals(loaded)) {
            writeVariables(filePath, normalized);
        }
        return normalized;
    }

    /**
     * Statistics must never prevent CSC from starting. Existing but incomplete
     * files do not throw while being read, so validate every required counter
     * before ConfigRegister parses it during common setup.
     */
    static Map<String, String> normalizeVariables(Map<String, String> variables) {
        Map<String, String> normalized = new LinkedHashMap<>(variables);
        normalized.put(GUARD_TIME, normalizeNonNegativeLong(variables.get(GUARD_TIME)));
        normalized.put(CHECK_COUNT, normalizeNonNegativeInt(variables.get(CHECK_COUNT)));
        normalized.put(PROBLEM_COUNT, normalizeNonNegativeInt(variables.get(PROBLEM_COUNT)));
        normalized.put(CHEAT_COUNT, normalizeNonNegativeInt(variables.get(CHEAT_COUNT)));
        return normalized;
    }

    private static String normalizeNonNegativeLong(String value) {
        if (value == null) {
            return "0";
        }
        try {
            long parsed = Long.parseLong(value.trim());
            return parsed < 0 ? "0" : String.valueOf(parsed);
        } catch (NumberFormatException ignored) {
            return "0";
        }
    }

    private static String normalizeNonNegativeInt(String value) {
        if (value == null) {
            return "0";
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed < 0 ? "0" : String.valueOf(parsed);
        } catch (NumberFormatException ignored) {
            return "0";
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
