package com.Pink_Cats.createschematicchecker.FancyConfig;

import com.Pink_Cats.createschematicchecker.lang.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.Pink_Cats.createschematicchecker.core.attach.Math.trimALL;

public class SimpleTomlEditor {

    private final String filePath;

    public SimpleTomlEditor(String filePath) {
        this.filePath = filePath;
    }

    public void removeAllComments() {
        try {
            StringBuilder content = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (!line.trim().startsWith("#")) {
                        content.append(line).append(System.lineSeparator());
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            Message.FE(e.getMessage());
        }
    }

    public Object ConfigValue_IO(String key, Object value) {
        try {
            if (!key.contains(".")) {
                relocateTopLevelKeyIfNeeded(key);
            }

            StringBuilder content = new StringBuilder();

            String[] keyParts = key.split("\\.");
            String subKey = keyParts.length > 0 ? keyParts[keyParts.length - 1] : null;

            boolean HasKey = false;
            StringBuilder key_line = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                int FileLineCount = 0;
                boolean NoEndLine = false;
                try (BufferedReader read = new BufferedReader(new FileReader(filePath))) {
                    String Pre_liner = "";
                    String Pre_line;
                    for (int i_1 = 0; i_1 < 500; i_1++) {
                        Pre_line = read.readLine();
                        if (Pre_line == null) {
                            if (!Pre_liner.isEmpty()) {
                                NoEndLine = true;
                            }
                            break;
                        } else {
                            Pre_liner = Pre_line.trim();
                            FileLineCount++;
                        }
                    }
                }
                if (FileLineCount == 0 || NoEndLine) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                        writer.write(System.lineSeparator());
                        FileLineCount += 1;
                    } catch (IOException e) {
                    }
                }

                String line;
                int i = 0;
                int m = 0;
                boolean Read = true;
                boolean Write = false;

                while ((line = reader.readLine()) != null) {
                    m += 1;
                    if ((line.trim().startsWith("#") || line.isEmpty()) && (m != FileLineCount)) {
                        content.append(line).append(System.lineSeparator());
                        continue;
                    }

                    String kye = GetCurrentLineStringTitle(keyParts, i);
                    String StartSpace = Space4(i);
                    int leadingSpaces = 0;
                    while (leadingSpaces < line.length() && line.charAt(leadingSpaces) == ' ') {
                        leadingSpaces++;
                    }
                    String[] LineKey = line.trim().split("\\.");
                    if ((LineKey.length < i + 1 && !line.contains("=") && !line.contains(",") && !line.contains("]")) || (FileLineCount == m)) {
                        if (!Write && !HasKey) {
                            Read = false;
                            for (int j = i; j < keyParts.length; j++) {
                                kye = GetCurrentLineStringTitle(keyParts, j);
                                StartSpace = Space4(j);
                                content = removeTrailingEmptyLines(content);
                                if (j == 0) {
                                    content.append(System.lineSeparator());
                                }
                                if (kye.startsWith("[")) {
                                    content.append(StartSpace).append(kye).append(System.lineSeparator());
                                } else {
                                    if (value instanceof String) {
                                        content.append(StartSpace)
                                                .append(kye)
                                                .append(" = \"")
                                                .append(value)
                                                .append("\"")
                                                .append(System.lineSeparator());
                                    } else if (value instanceof String[]) {
                                        content.append(StartSpace)
                                                .append(kye)
                                                .append(" = ")
                                                .append(arrayToString((String[]) value, StartSpace))
                                                .append(System.lineSeparator());
                                    } else {
                                        content.append(StartSpace)
                                                .append(kye)
                                                .append(" = ")
                                                .append(value)
                                                .append(System.lineSeparator());
                                    }
                                    Write = true;
                                }
                            }
                            content.append(System.lineSeparator());
                            content.append(line).append(System.lineSeparator());
                        }
                    }
                    if (Read) {
                        if (line.trim().contains(kye)) {
                            if (i < keyParts.length - 1) {
                                i += 1;
                            }
                            if (line.trim().contains("=")) {
                                key_line = new StringBuilder(line.trim().split("=", 2)[1].trim().replace("\"", ""));
                                if (line.trim().contains("[")) {
                                    content.append(line).append(System.lineSeparator());
                                    for (int o = 0; o < 100; o++) {
                                        String NextLine = reader.readLine();
                                        if (NextLine == null) {
                                            break;
                                        }
                                        content.append(NextLine).append(System.lineSeparator());

                                        if (NextLine.trim().startsWith("#") || NextLine.isEmpty()) {
                                            continue;
                                        }

                                        key_line.append(NextLine);
                                        if (NextLine.contains("]")) {
                                            break;
                                        }
                                    }
                                }
                                HasKey = true;
                            }
                        }
                        if (!(line.contains("=") && line.contains("["))) {
                            content.append(line).append(System.lineSeparator());
                        } else {
                            if (!line.trim().contains(kye)) {
                                content.append(line).append(System.lineSeparator());
                            }
                        }
                    }
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }
            if (HasKey) {
                return removeTrailingEmptyLines(key_line.toString()).trim();
            } else {
                return value;
            }
        } catch (IOException e) {
        }

        return value;
    }

    private String arrayToString(String[] array, String startSpace) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < array.length; i++) {
            sb.append(startSpace).append("    \"").append(array[i]).append("\"");
            if (i < array.length - 1) {
                sb.append(",\n");
            }
        }
        sb.append(" ]");
        return sb.toString();
    }

    private String Space4(int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < count; i++) {
            spaces.append("    ");
        }
        return spaces.toString();
    }

    public static StringBuilder removeTrailingEmptyLines(StringBuilder content) {
        String[] lines = content.toString().split(System.lineSeparator());
        return StringBuilderCoreSpace(lines);
    }

    public static String removeTrailingEmptyLines(String content) {
        String[] lines = content.split(System.lineSeparator());
        return StringBuilderCoreSpace(lines).toString();
    }

    private static StringBuilder StringBuilderCoreSpace(String[] lines) {
        StringBuilder newContent = new StringBuilder();
        int i = lines.length - 1;
        while (i >= 0 && lines[i].trim().isEmpty()) {
            i--;
        }
        for (int j = 0; j <= i; j++) {
            newContent.append(lines[j]).append(System.lineSeparator());
        }
        return newContent;
    }

    private String GetCurrentLineStringTitle(String[] keyParts, int FindKey) {
        String subKey = keyParts.length > 0 ? keyParts[keyParts.length - 1] : null;
        if (Objects.equals(subKey, keyParts[FindKey])) {
            return keyParts[FindKey];
        } else {
            StringBuilder title = new StringBuilder();

            for (int i = 0; i < FindKey + 1; i++) {
                title.append(keyParts[i]).append(".");
            }
            if (title.length() > 0) {
                title.setLength(title.length() - 1);
            }
            return "[" + title + "]";
        }
    }

    public void insertCommentAboveKey(String key, String comment) {
        try {
            StringBuilder content = new StringBuilder();
            boolean keyFound = false;
            boolean commentExists = false;
            boolean Inlist = false;
            String[] keyParts = key.split("\\.");
            String father;
            String children;

            if (keyParts.length > 1) {
                father = GetCurrentLineStringTitle(keyParts, keyParts.length - 2);
            } else {
                Inlist = true;
                father = "";
            }
            children = GetCurrentLineStringTitle(keyParts, keyParts.length - 1);

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                List<String> lines = new ArrayList<>();
                String line;

                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                for (int i = 0; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    if (!Inlist && currentLine.trim().startsWith(father)) {
                        Inlist = true;
                    }

                    if (Inlist) {
                        if (!keyFound && trimALL(currentLine.trim()).startsWith(children + "=")) {
                            keyFound = true;

                            for (int j = i - 1; j >= 0; j--) {
                                String aboveLine = lines.get(j).trim();
                                if (aboveLine.equals("# " + comment)) {
                                    commentExists = true;
                                    break;
                                }
                                if (!aboveLine.trim().startsWith("#")) {
                                    break;
                                }
                            }

                            if (!commentExists) {
                                content.append(Space4(keyParts.length - 1)).append("# ").append(comment).append(System.lineSeparator());
                            }
                        }
                    }

                    content.append(currentLine).append(System.lineSeparator());
                }
            }

            if (!keyFound) {
                Message.FE("Key not found: " + children);
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            Message.FE(e.getMessage());
        }
    }

    public void insertCommentAboveKeyIfMissing(String key, String comment) {
        try {
            String[] keyParts = key.split("\\.");
            boolean inList = keyParts.length <= 1;
            String father = inList ? "" : GetCurrentLineStringTitle(keyParts, keyParts.length - 2);
            String children = GetCurrentLineStringTitle(keyParts, keyParts.length - 1);

            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            int keyLineIndex = -1;
            for (int i = 0; i < lines.size(); i++) {
                String currentLine = lines.get(i);
                if (!inList && currentLine.trim().startsWith(father)) {
                    inList = true;
                }
                if (inList && trimALL(currentLine.trim()).startsWith(children + "=")) {
                    keyLineIndex = i;
                    break;
                }
            }

            if (keyLineIndex < 0) {
                Message.FE("Key not found: " + children);
                return;
            }

            int sameCommentCount = 0;
            int commentBlockStart = keyLineIndex;
            for (int i = keyLineIndex - 1; i >= 0; i--) {
                String aboveLine = lines.get(i).trim();
                if (aboveLine.equals("# " + comment)) {
                    sameCommentCount++;
                }
                if (!aboveLine.startsWith("#")) {
                    break;
                }
                commentBlockStart = i;
            }

            if (sameCommentCount == 1) {
                return;
            }

            StringBuilder content = new StringBuilder();
            boolean keptExpectedComment = false;
            for (int i = 0; i < lines.size(); i++) {
                if (i >= commentBlockStart && i < keyLineIndex && lines.get(i).trim().equals("# " + comment)) {
                    if (keptExpectedComment) {
                        continue;
                    }
                    keptExpectedComment = true;
                }
                if (i == keyLineIndex) {
                    if (!keptExpectedComment) {
                        content.append(Space4(keyParts.length - 1))
                                .append("# ")
                                .append(comment)
                                .append(System.lineSeparator());
                    }
                }
                content.append(lines.get(i)).append(System.lineSeparator());
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            Message.FE(e.getMessage());
        }
    }

    public void syncCommentsAboveKey(String key, List<String> comments) {
        try {
            String[] keyParts = key.split("\\.");
            boolean inList = keyParts.length <= 1;
            String father = inList ? "" : GetCurrentLineStringTitle(keyParts, keyParts.length - 2);
            String children = GetCurrentLineStringTitle(keyParts, keyParts.length - 1);

            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            int keyLineIndex = -1;
            for (int i = 0; i < lines.size(); i++) {
                String currentLine = lines.get(i);
                if (!inList && currentLine.trim().startsWith(father)) {
                    inList = true;
                }
                if (inList && trimALL(currentLine.trim()).startsWith(children + "=")) {
                    keyLineIndex = i;
                    break;
                }
            }

            if (keyLineIndex < 0) {
                Message.FE("Key not found: " + children);
                return;
            }

            int commentBlockStart = keyLineIndex;
            for (int i = keyLineIndex - 1; i >= 0; i--) {
                String aboveLine = lines.get(i).trim();
                if (aboveLine.startsWith("#")) {
                    commentBlockStart = i;
                    continue;
                }
                break;
            }

            List<String> expectedBlock = new ArrayList<>();
            String indent = Space4(keyParts.length - 1);
            for (String comment : comments) {
                expectedBlock.add(indent + "# " + comment);
            }

            List<String> existingBlock = commentBlockStart < keyLineIndex
                    ? new ArrayList<>(lines.subList(commentBlockStart, keyLineIndex))
                    : Collections.emptyList();

            if (existingBlock.equals(expectedBlock)) {
                return;
            }

            List<String> rebuiltLines = new ArrayList<>();
            rebuiltLines.addAll(lines.subList(0, commentBlockStart));
            rebuiltLines.addAll(expectedBlock);
            rebuiltLines.addAll(lines.subList(keyLineIndex, lines.size()));

            StringBuilder content = new StringBuilder();
            for (String line : rebuiltLines) {
                content.append(line).append(System.lineSeparator());
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            Message.FE(e.getMessage());
        }
    }

    public void relocateKeyToParentSectionIfNeeded(String key) {
        String[] keyParts = key.split("\\.");
        if (keyParts.length <= 1) {
            return;
        }

        String parentSection = GetCurrentLineStringTitle(keyParts, keyParts.length - 2);
        String childKey = GetCurrentLineStringTitle(keyParts, keyParts.length - 1);

        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            int parentStart = -1;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).trim().equals(parentSection)) {
                    parentStart = i;
                    break;
                }
            }
            if (parentStart < 0) {
                return;
            }

            int parentEnd = lines.size();
            for (int i = parentStart + 1; i < lines.size(); i++) {
                String t = lines.get(i).trim();
                if (t.startsWith("[") && t.endsWith("]")) {
                    parentEnd = i;
                    break;
                }
            }

            int keyLineIndex = -1;
            for (int i = 0; i < lines.size(); i++) {
                if (trimALL(lines.get(i).trim()).startsWith(childKey + "=")) {
                    keyLineIndex = i;
                    break;
                }
            }
            if (keyLineIndex < 0) {
                return;
            }

            if (keyLineIndex > parentStart && keyLineIndex < parentEnd) {
                return;
            }

            int blockStart = keyLineIndex;
            for (int i = keyLineIndex - 1; i >= 0; i--) {
                String t = lines.get(i).trim();
                if (t.startsWith("#")) {
                    blockStart = i;
                    continue;
                }
                break;
            }

            int blockEnd = keyLineIndex;
            String keyLine = lines.get(keyLineIndex);
            int eqIndex = keyLine.indexOf('=');
            if (eqIndex >= 0) {
                String valuePart = keyLine.substring(eqIndex + 1).trim();
                if (valuePart.startsWith("[")) {
                    int bracketDepth = countChar(valuePart, '[') - countChar(valuePart, ']');
                    while (bracketDepth > 0 && blockEnd + 1 < lines.size()) {
                        blockEnd++;
                        String nextLine = lines.get(blockEnd);
                        bracketDepth += countChar(nextLine, '[') - countChar(nextLine, ']');
                    }
                }
            }

            List<String> block = new ArrayList<>(lines.subList(blockStart, blockEnd + 1));
            lines.subList(blockStart, blockEnd + 1).clear();

            if (blockStart < parentStart) {
                parentStart -= block.size();
            }

            parentEnd = lines.size();
            for (int i = parentStart + 1; i < lines.size(); i++) {
                String t = lines.get(i).trim();
                if (t.startsWith("[") && t.endsWith("]")) {
                    parentEnd = i;
                    break;
                }
            }

            int insertIndex = parentEnd;
            if (insertIndex > 0 && !lines.get(insertIndex - 1).trim().isEmpty()) {
                lines.add(insertIndex, "");
                insertIndex++;
            }
            lines.addAll(insertIndex, block);

            StringBuilder content = new StringBuilder();
            for (String line : lines) {
                content.append(line).append(System.lineSeparator());
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            Message.FE(e.getMessage());
        }
    }

    /**
     * Ensure a root key is not accidentally kept inside the last TOML table.
     */
    public void relocateTopLevelKeyIfNeeded(String key) {
        if (key == null || key.trim().isEmpty() || key.contains(".")) {
            return;
        }

        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            int misplacedKeyIndex = -1;
            boolean inTable = false;
            for (int i = 0; i < lines.size(); i++) {
                String trimmed = lines.get(i).trim();
                if (isTableHeader(trimmed)) {
                    inTable = true;
                    continue;
                }
                if (isScalarKeyLine(trimmed, key)) {
                    if (!inTable) {
                        return;
                    }
                    misplacedKeyIndex = i;
                    break;
                }
            }

            if (misplacedKeyIndex < 0) {
                return;
            }

            int blockStart = misplacedKeyIndex;
            for (int i = misplacedKeyIndex - 1; i >= 0; i--) {
                String trimmed = lines.get(i).trim();
                if (trimmed.startsWith("#")) {
                    blockStart = i;
                    continue;
                }
                break;
            }

            List<String> block = new ArrayList<>(lines.subList(blockStart, misplacedKeyIndex + 1));
            lines.subList(blockStart, misplacedKeyIndex + 1).clear();

            int insertIndex = 0;
            while (insertIndex < lines.size() && !isTableHeader(lines.get(insertIndex).trim())) {
                insertIndex++;
            }

            if (insertIndex > 0 && !lines.get(insertIndex - 1).trim().isEmpty()) {
                lines.add(insertIndex, "");
                insertIndex++;
            }
            lines.addAll(insertIndex, block);
            insertIndex += block.size();
            if (insertIndex < lines.size() && !lines.get(insertIndex).trim().isEmpty()) {
                lines.add(insertIndex, "");
            }

            StringBuilder content = new StringBuilder();
            for (String line : lines) {
                content.append(line).append(System.lineSeparator());
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            Message.FE(e.getMessage());
        }
    }

    private static boolean isTableHeader(String trimmed) {
        return trimmed.startsWith("[") && trimmed.endsWith("]");
    }

    private static boolean isScalarKeyLine(String trimmed, String key) {
        return trimALL(trimmed).startsWith(key + "=");
    }

    private static int countChar(String input, char ch) {
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }

    public void compactBlankLinesInSections() {
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            StringBuilder content = new StringBuilder();
            boolean inSection = false;
            boolean pendingBlankOutsideSection = false;

            for (String line : lines) {
                String trimmed = line.trim();
                boolean isSectionHeader = trimmed.startsWith("[") && trimmed.endsWith("]");
                boolean isBlank = trimmed.isEmpty();

                if (isSectionHeader) {
                    if (content.length() > 0 && !content.toString().endsWith(System.lineSeparator() + System.lineSeparator())) {
                        content.append(System.lineSeparator());
                    }
                    content.append(line).append(System.lineSeparator());
                    inSection = true;
                    pendingBlankOutsideSection = false;
                    continue;
                }

                if (isBlank) {
                    if (inSection) {
                        continue;
                    }
                    if (!pendingBlankOutsideSection) {
                        content.append(System.lineSeparator());
                        pendingBlankOutsideSection = true;
                    }
                    continue;
                }

                content.append(line).append(System.lineSeparator());
                pendingBlankOutsideSection = false;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            Message.FE(e.getMessage());
        }
    }
}
