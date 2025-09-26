package com.Pink_Cats.createschematicchecker.FancyConfig;

import com.Pink_Cats.createschematicchecker.Message;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static com.Pink_Cats.createschematicchecker.Math.trimALL;

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

                // 读取每一行并跳过注释行
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().startsWith("#")) {
                        content.append(line).append(System.lineSeparator());
                    }
                }
            }

            // 将更新后的内容写回文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }
        } catch (IOException e) {
            Message.FE(e.getMessage());
        }
    }



    // 修改键值
    public Object ConfigValue_IO(String key, Object value) {
        try {
            StringBuilder content = new StringBuilder();

            // 拆分为主表和子表
            String[] keyParts = key.split("\\.");
            String subKey = keyParts.length > 0 ? keyParts[keyParts.length - 1] : null; // 获取最后一个元素或设置为null

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
                            if (!Pre_liner.isEmpty())
                            {
                                NoEndLine = true;
                            }
                            break;
                        }else{
                            Pre_liner = Pre_line.trim();
                            FileLineCount++;
                        }

                    }
                    //Message.FE("LastLine-" + Pre_liner+"-");
                    //Message.FW("FileLineCount:  " + FileLineCount);
                }
                if (FileLineCount == 0 || NoEndLine) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) { // 追加模式
                        writer.write(System.lineSeparator()); // 写入一行空行
                        FileLineCount += 1;
                    } catch (IOException e) {
                        //Message.FE(e.getMessage());
                    }
                }



                String line;
                int i = 0;
                int m = 0;
                boolean Read = true;
                boolean Write = false;



                while ((line = reader.readLine()) != null) {

                    m+=1;
                    if ((line.trim().startsWith("#") || line.isEmpty()) && ( m != FileLineCount ) ) {
                        content.append(line).append(System.lineSeparator());
                        continue;
                    }

                    String kye = GetCurrentLineStringTitle(keyParts,i);
                    String StartSpace = Space4(i);
                    //Message.FW("Finding key kye:  " + kye);
                    //Message.FW("space = -" + StartSpace+"-");
                    int leadingSpaces = 0;
                    while (leadingSpaces < line.length() && line.charAt(leadingSpaces) == ' ') {
                        leadingSpaces++;
                    }
                    //Message.FW("Found space:  " + leadingSpaces);
                   // Message.FW("Checking..."+line+"." );
                    String[] LineKey = line.trim().split("\\.");
                    //Message.FW(m);
                    if ( (LineKey.length < i +1 && !line.contains("=") && !line.contains(",") && !line.contains("]")) || (FileLineCount == m) ) {

                        if (!Write && !HasKey){
                            //Message.FM("end key" + kye);
                            Read = false;
                            for (int j = i; j < keyParts.length; j++) {
                                kye = GetCurrentLineStringTitle(keyParts,j);
                                StartSpace = Space4(j);
                                content = removeTrailingEmptyLines(content);
                                if (j == 0){
                                    content.append(System.lineSeparator());
                                }
                                if (kye.startsWith("["))
                                {
                                    content.append(StartSpace).append(kye).append(System.lineSeparator());
                                }
                                else {
                                    //Message.FE("Insert:::");
                                    if (value instanceof String) {
                                        content.append(StartSpace)
                                                .append(kye)
                                                .append(" = \"")
                                                .append(value)
                                                .append("\"")
                                                .append(System.lineSeparator());
                                    } else if (value instanceof String[]) {
                                        // 处理字符串数组
                                        content.append(StartSpace)
                                                .append(kye)
                                                .append(" = ")
                                                .append(arrayToString((String[]) value,StartSpace)) // 调用方法将数组转换为 TOML 格式
                                                .append(System.lineSeparator());
                                    } else {
                                        content.append(StartSpace)
                                                .append(kye)
                                                .append(" = ")
                                                .append(value) // 直接添加，不加引号
                                                .append(System.lineSeparator());
                                    }
                                    Write = true;
                                }
                            }
                            content.append(System.lineSeparator());
                            content.append(line).append(System.lineSeparator());
                            //Message.FW("end key success !");

                        }

                    }
                   // Message.FE("read = "+ Read);
                    if (Read) {
                        if (line.trim().contains(kye)) {


                            //Message.FP("Found key"+keyParts[i] );
                            if (i < keyParts.length-1) {
                                i  += 1;
                            }
                            if (line.trim().contains("=")){
                                key_line = new StringBuilder(line.trim().split("=", 2)[1].trim().replace("\"", ""));
                                if (line.trim().contains("[")){
                               //     Message.FE(line);
                                    content.append(line).append(System.lineSeparator());
                                    for (int o = 0; o < 100; o++) {
                                        String NextLine = reader.readLine();
                                     //   Message.FE(NextLine);
                                        if (NextLine==null)
                                        {
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

                            //    Message.FM("keyResult: " + key_line);
                                HasKey = true;

                            }
                        }
                        if (!(line.contains("=") && line.contains("["))) {
                       //     Message.FM("addLine: "+line);
                            content.append(line).append(System.lineSeparator());
                        }else {
                            if (!line.trim().contains(kye)){
                                content.append(line).append(System.lineSeparator());
                            }
                        }


                    }

                }

                if (!HasKey) {
                    //Message.FW("No key found");
                }

            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }
            if (HasKey){
                return removeTrailingEmptyLines(key_line.toString());
            }else  {
                return value;
            }

        } catch (IOException e) {
            //Message.FE(e.getMessage());
        }

        return value;
    }



    private String arrayToString(String[] array, String startSpace) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n"); // 开始数组并换行
        for (int i = 0; i < array.length; i++) {
            sb.append(startSpace).append("    \"").append(array[i]).append("\""); // 添加元素并缩进
            if (i < array.length - 1) {
                sb.append(",\n"); // 如果不是最后一个元素，添加逗号和换行
            }
        }
        sb.append(" ]"); // 结束数组并换行
        return sb.toString();
    }





    private String Space4(int count) {
        if (count <= 0) {
            return "";}
        return "    ".repeat(count);
    }

    //toml space remove
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



    private String GetCurrentLineStringTitle(String[] keyParts,int FindKey) {
        String subKey = keyParts.length > 0 ? keyParts[keyParts.length - 1] : null; // 获取最后一个元素或设置为null
        if (Objects.equals(subKey, keyParts[FindKey])) {
            return keyParts[FindKey];
        }
        else  {
            StringBuilder title = new StringBuilder();

            for (int i = 0; i < FindKey+1; i++) {
                title.append(keyParts[i]).append(".");
            }
            if (!title.isEmpty()) {
                title.setLength(title.length() - 1);
            }
            //Message.FW("Title key:  " + title);
            return "["+ title +"]";
        }
    }

    // 插入注释
    public void insertCommentAboveKey(String key, String comment) {
        try {
            StringBuilder content = new StringBuilder();
            boolean keyFound = false;
            boolean commentExists = false; // 用于检查注释是否已存在
            boolean Inlist = false;
            String[] keyParts = key.split("\\.");
            String father ;
            String children;

            if (keyParts.length > 1) {
                father = GetCurrentLineStringTitle(keyParts,keyParts.length-2);
            }
            else {
                Inlist = true;
                father = "";
            }
            children = GetCurrentLineStringTitle(keyParts,keyParts.length-1);

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                List<String> lines = new ArrayList<>();
                String line; // 在这里声明 line 变量

                // 读取所有行到列表中
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                // 遍历所有行以构建新的内容
                for (int i = 0; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                //    Message.FW(currentLine);
                    if (!Inlist && currentLine.trim().startsWith(father)) {
                        Inlist = true;
                        //Message.FW(true +"Inlist");
                    }

                    // 在找到键之前插入注释
                    if (Inlist) {
                       // Message.FP(trimALL(currentLine));
                        if (!keyFound && trimALL(currentLine.trim()).startsWith(children + "=")) {
                            keyFound = true; // 找到键后标记

                            // 检查键上方的行，直到遇到不以 # 开头的行
                            for (int j = i - 1; j >= 0; j--) {
                                String aboveLine = lines.get(j).trim();
                                if (aboveLine.equals("# " + comment)) {
                                    commentExists = true; // 如果注释存在，标记为 true
                                    break; // 找到注释后停止检查
                                }
                                if (!aboveLine.trim().startsWith("#")) {
                                    break; // 如果遇到不以 # 开头的行，停止检查
                                }
                            }

                            // 只有在注释不存在的情况下插入
                            if (!commentExists) {
                                content.append(Space4(keyParts.length-1)).append("# ").append(comment).append(System.lineSeparator());
                            }
                        }


                    }



                    content.append(currentLine).append(System.lineSeparator());
                }
            }

            // 如果键未找到，可以选择是否抛出异常或简单返回
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






}
