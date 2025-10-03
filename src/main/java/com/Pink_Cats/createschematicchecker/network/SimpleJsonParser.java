package com.Pink_Cats.createschematicchecker.network;

import com.Pink_Cats.createschematicchecker.lang.Message;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.MaxChassisRange;
import static com.Pink_Cats.createschematicchecker.FancyConfig.FileIO.createIfNotExists;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;
import static com.Pink_Cats.createschematicchecker.online.VersionChecker.UpdateMainThread;

public class SimpleJsonParser {

    /**
     * 解析JSON文件，返回包含id和operate的二维字符串数组
     * @param filePath JSON文件路径
     * @return 长度为2的数组，[0]是id的String[][], [1]是operate的String[][]
     *         若解析失败，返回null
     */
    public static String[][][] parseJsonToArrays(String filePath,List<String> log) {
        try {
            List<String> lines = readAllLines(filePath);
            if (lines == null) {
                return null;
            }

            if (!validateBasicFormat(lines,log)) {
                return null;
            }

            // 关键修改：字段缺失时返回空数组，而非null
            String[][] idArray = parseTwoDimensionalArray(lines, "id",log);
            String[][] operateArray = parseTwoDimensionalArray(lines, "operate",log);
            if (idArray == null) idArray = new String[0][0]; // 缺失id时返回空二维数组
            if (operateArray == null) operateArray = new String[0][0]; // 缺失operate时返回空二维数组

            return new String[][][]{idArray, operateArray};
        } catch (Exception e) {
            log.add("console.miss.fail.parse"+ e.getMessage());
            Message.FE("console.miss.fail.parse"+ e.getMessage());
            return null;
        }
    }

    // 读取文件所有行
    private static List<String> readAllLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim()); // trim()去除首尾空格，便于解析
            }
        } catch (IOException e) {
            //System.err.println("file read fail: " + e.getMessage());
            return null;
        }
        return lines.isEmpty() ? null : lines;
    }

    // 基础格式校验
    private static boolean validateBasicFormat(List<String> lines,List<String> log) {
        if (lines == null || lines.isEmpty()) {
            log.add(translateDirect("console.miss.json.empty"));
            return false;
        }
        if (!lines.get(0).startsWith("{")) {
            log.add(translateDirect("console.miss.json.start"));
            return false;
        }
        if (!lines.get(lines.size() - 1).endsWith("}")) {
            log.add(translateDirect("console.miss.json.stop") + lines.size() );
            return false;
        }
        return true;
    }

    // 解析指定字段的二维数组
    private static String[][] parseTwoDimensionalArray(List<String> lines, String fieldName,List<String> log) {
        // 1. 找到目标字段所在行
        int fieldLineIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("\"" + fieldName + "\":")) {
                fieldLineIndex = i;
                break;
            }
        }

        if (fieldLineIndex == -1) {
            //System.err.println("did not find" + fieldName);
            return null;
        }

        // 2. 提取字段对应的数组内容
        String fieldLine = lines.get(fieldLineIndex);
        int arrayStart = fieldLine.indexOf('[');
        if (arrayStart == -1) {
            log.add(fieldName + translateDirect("console.miss.liner") + (fieldLineIndex + 1) + "）");
            return null;
        }

        // 3. 提取整个二维数组的字符串
        StringBuilder arrayContent = new StringBuilder();
        int bracketLevel = 1;
        arrayContent.append(fieldLine.substring(arrayStart));

        for (int i = fieldLineIndex + 1; i < lines.size(); i++) {
            String line = lines.get(i);
            arrayContent.append(line);

            // 跟踪括号层级，判断数组结束
            for (char c : line.toCharArray()) {
                if (c == '[') bracketLevel++;
                if (c == ']') bracketLevel--;
                if (bracketLevel == 0) {
                    break;
                }
            }
            if (bracketLevel == 0) {
                break;
            }
        }

        // 4. 解析二维数组
        return parseTwoDimensionalArrayString(arrayContent.toString(),log);
    }
    public static String removeLastComma(String input) {
        // 检查字符串是否为空，以及最后一个字符是否为逗号
        if (input != null && !input.isEmpty() && input.charAt(input.length() - 1) == ',') {
            // 如果是逗号，则返回去掉最后一个字符的字符串
            return input.substring(0, input.length() - 1);
        }
        // 如果不是逗号或字符串为空，则返回原字符串
        return input;
    }

    // 解析二维数组字符串为String[][]
    private static String[][] parseTwoDimensionalArrayString(String arrayStr,List<String> log) {
        // 去除最外层的[]
        arrayStr = removeLastComma(arrayStr);

        if (!arrayStr.startsWith("[") || !arrayStr.endsWith("]")) {
            //System.err.println("数组格式错误，缺少首尾 array format wrong[]");
            return null;
        }
        String innerContent = arrayStr.substring(1, arrayStr.length() - 1).trim();
        if (innerContent.isEmpty()) {
            return new String[0][0]; // 空数组
        }

        // 分割出子数组（处理嵌套的[]）
        List<String> subArrayStrs = new ArrayList<>();
        int start = 0;
        int bracketLevel = 0;

        for (int i = 0; i < innerContent.length(); i++) {
            char c = innerContent.charAt(i);
            if (c == '[') bracketLevel++;
            if (c == ']') bracketLevel--;

            // 遇到逗号且括号层级为0时，分割子数组
            if (c == ',' && bracketLevel == 0) {
                subArrayStrs.add(innerContent.substring(start, i).trim());
                start = i + 1;
            }
        }
        // 添加最后一个子数组
        subArrayStrs.add(innerContent.substring(start).trim());

        // 解析每个子数组为String[]
        List<String[]> resultList = new ArrayList<>();
        for (String subArrayStr : subArrayStrs) {
            String[] subArray = parseOneDimensionalArrayString(subArrayStr,log);
            if (subArray == null) {
                return null;
            }
            resultList.add(subArray);
        }

        // 转换为二维数组
        return resultList.toArray(new String[0][]);
    }

    // 解析一维数组字符串为String[]
    private static String[] parseOneDimensionalArrayString(String arrayStr,List<String> log) {
        if (!arrayStr.startsWith("[") || !arrayStr.endsWith("]")) {
            log.add(translateDirect("console.miss.format") + arrayStr);
            return null;
        }

        // 去除首尾[]并trim
        String innerContent = arrayStr.substring(1, arrayStr.length() - 1).trim();
        if (innerContent.isEmpty()) {
            return new String[0]; // 空子数组
        }

        // 分割元素（处理引号和逗号）
        List<String> elements = getStrings(innerContent);

        // 去除元素的引号
        String[] result = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            String elem = elements.get(i);
            // 去除首尾引号
            if (elem.startsWith("\"") && elem.endsWith("\"")) {
                result[i] = elem.substring(1, elem.length() - 1);
            } else {
                result[i] = elem;
            }
        }

        return result;
    }

    private static @NotNull List<String> getStrings(String innerContent) {
        List<String> elements = new ArrayList<>();
        int start = 0;
        boolean inQuotes = false;

        for (int i = 0; i < innerContent.length(); i++) {
            char c = innerContent.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            }
            // 遇到逗号且不在引号内时，分割元素
            else if (c == ',' && !inQuotes) {
                elements.add(innerContent.substring(start, i).trim());
                start = i + 1;
            }
        }
        // 添加最后一个元素
        elements.add(innerContent.substring(start).trim());
        return elements;
    }

    private static boolean ConfigIsValid(String[][][] result,List<String> log) {
        if (result != null) {
            String[][] idArray = result[0];
            String[][] operateArray = result[1];


            for (String[] subArray : idArray) {
                if (subArray.length != 2) {
                    log.add(translateDirect("console.miss.length2") + subArray.length);
                    return false;
                }

                if (!subArray[0].contains(":")) {
                    log.add(translateDirect("console.miss.id") + subArray[0]);
                    return false;
                }

            }
            for (String[] subArray : operateArray) {
                if (subArray.length != 3) {
                    log.add(translateDirect("console.miss.length3") + subArray.length);
                    return false;
                }
                if (!subArray[0].contains(":")) {
                    log.add(translateDirect("console.miss.id") + subArray[0]);
                    return false;
                }
                if (!subArray[2].contains("operate")) {
                    log.add(translateDirect("console.miss.operate2") + subArray[2]);
                    return false;
                }
                if (!subArray[2].contains("limit") && !subArray[2].contains("clear") && !subArray[2].contains("replace")) {
                    log.add(translateDirect("console.miss.operate") + subArray[2]);
                    return false;
                }
            }


        }
        return true;
    }

    public static void ArrayListOutput(String[][] result) {
        if (result != null) {
            for (String[] subArray : result) {
                Message.FM(Arrays.toString(subArray));
            }
        }
    }


    /**
     * 合并两个二维字符串数组
     * @param first 第一个要合并的数组
     * @param second 第二个要合并的数组
     * @return 合并后的新数组，包含两个原数组的所有元素
     */
    public static String[][] merge(String[][] first, String[][] second) {
        // 处理null情况
        if (first == null) {
            return second == null ? new String[0][] : second.clone();
        }
        if (second == null) {
            return first.clone();
        }

        // 创建新数组，长度为两个原数组长度之和
        String[][] result = new String[first.length + second.length][];

        // 复制第一个数组的元素
        System.arraycopy(first, 0, result, 0, first.length);

        // 复制第二个数组的元素
        System.arraycopy(second, 0, result, first.length, second.length);

        return result;
    }

    //json
    private static final String INITIAL_JSON_CONTENT = """
            {
              "id": [
                ["minecraft:item", "path.path.$list"]
              ],
              "operate": [
                ["create:dirt", "path.path.$list,para", "operate.limit$para$0$4"],
                ["create:item", "path.path.para", "operate.clear$para"]
              ]
            }""";

    /**
     * 检查文件是否存在，不存在则创建并写入初始JSON内容
     *
     * @param directoryPath 目录路径
     * @param fileName      文件名
     */
    public static void createUserRuleFile(String directoryPath, String fileName) {
        // 构建完整文件路径
        File file = new File(directoryPath, fileName);

        // 如果文件已存在，直接返回true
        if (file.exists()) {
            return;
        }

        // 文件不存在，创建并写入初始内容
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(INITIAL_JSON_CONTENT);
        } catch (IOException e) {
            Message.FE(translateDirect("console.makefile.error") + e.getMessage());
        }
    }

    public static List<String> UpdateRuleThread(List<String> log){

        String jsonPath = UpdateMainThread();
        String UserRulePath = "config/CSC";
        createIfNotExists(UserRulePath);
        String UserRuleFile = "user_rule.json";
        createUserRuleFile(UserRulePath, UserRuleFile);


        String[][] SurgeryExist = {
                {"create:belt", "nbt.Length", "operate.limit$Length$0$"+MaxBelt},
                {"create:belt", "nbt.Index", "operate.limit$Index$0$"+MaxIndex},
                {"create:weighted_ejector", "nbt.HorizontalDistance", "operate.limit$HorizontalDistance$1$"+MaxEject},
                {"create:deployer", "nbt.Inventory", "operate.clear$Inventory"},

                {"create:andesite_funnel", "nbt.Filter", "operate.clear$Filter"},
                {"create:andesite_funnel", "nbt.FilterAmount", "operate.clear$FilterAmount"},

                {"create:mechanical_arm", "nbt.$InteractionPoints.Pos.X", "operate.limit$X$-5$5"},
                {"create:mechanical_arm", "nbt.$InteractionPoints.Pos.Y", "operate.limit$Y$-5$5"},
                {"create:mechanical_arm", "nbt.$InteractionPoints.Pos.Z", "operate.limit$Z$-5$5"},

                {"create:chassis", "nbt.ScrollValue", "operate.limit$ScrollValue$0$"+MaxChassisRange},

                // Add more entries as needed
                {"createaddition:rolling_mill","nbt.InputInventory", "operate.clear$InputInventory"},
                {"createaddition:rolling_mill","nbt.OutputInventory", "operate.clear$OutputInventory"},

                {"create_enchantment_industry:printer", "nbt.PrintingTemplate", "operate.clear$PrintingTemplate"},

                {"createbigcannons:fuzed_block","nbt.Fuze", "operate.clear$Fuze"},

                //package_frogport
                {"create:package_frogport","nbt.Inventory", "operate.clear$Inventory"},


        };

        //ID InterFace
        String[][] IdLogicArray = {

                //Create 6.0.* neoforge
                {"create:stock_ticker", "nbt.$Categories.id"},
                {"create:redstone_requester", "nbt.EncodedRequest.ordered_stacks.$entries.item_stack.id"},
                {"create:factory_panel", "nbt.bottom_left.Filter.id"},
                {"create:factory_panel", "nbt.top_left.Filter.id"},
                {"create:factory_panel", "nbt.bottom_right.Filter.id"},
                {"create:factory_panel", "nbt.top_right.Filter.id"},


                //Create 6.0.* forge
                {"create:redstone_requester", "nbt.EncodedRequest.OrderedStacks.$Entries.Item.id"},
                {"create:table_cloth", "nbt.$Items.id"},
                {"create:package_frogport", "nbt.Inventory.$Items.id"},
                {"create:package_postbox", "nbt.Inventory.$Items.id"},


                //Create 0.5.1
                {"create:redstone_link", "nbt.FrequencyFirst.id"},
                {"create:redstone_link", "nbt.FrequencyLast.id"},
                {"create:depot", "nbt.HeldItem.Item.id"},
                {"create:weighted_ejector", "nbt.HeldItem.Item.id"},
                {"create:chute", "nbt.Item.id"},
                {"create:smart_chute", "nbt.Item.id"},
                {"create:smart_chute", "nbt.Filter.id"},
                {"create:saw", "nbt.Filter.id"},
                {"create:deployer", "nbt.Filter.id"},
                {"create:deployer", "nbt.$Inventory.id"},
                {"create:funnel", "nbt.Filter.id"},
                {"create:placard", "nbt.Item.id"},
                {"create:content_observer", "nbt.Filter.id"},
                {"create:belt", "nbt.Inventory.$Items.Item.id"},
                {"create:basin", "nbt.Filter.id"},
                {"create:basin", "nbt.InputItems.$Items.id"},
                {"create:basin", "nbt.OutputItems.$Items.id"},
                {"create:smart_fluid_pipe", "nbt.Filter.id"},
                {"create:mechanical_crafter", "nbt.Inventory.$Items.id"},
                {"create:toolbox", "nbt.Inventory.$Compartments.id"},
                {"create:toolbox", "nbt.Inventory.$Items.id"},
                {"create:stockpile_switch", "nbt.Filter.id"},
                {"create:brass_tunnel", "nbt.Filter.id"},
                {"create:brass_tunnel", "nbt.$Filters.Filter.id"},
                {"create:brass_tunnel", "nbt.StackToDistribute.id"},
                {"create:mechanical_roller", "nbt.Filter.id"},

                {"create:creative_crate", "nbt.Filter.id"},

                //millstone can't save nbt so it doesn't need clear
                //{"create:millstone", "nbt.InputInventory.$Items.id"},
                //{"create:millstone", "nbt.OutputInventory.$Items.id"},


        };

        Operate_modify_rule_local = SurgeryExist;
        ID_modify_rule_local = IdLogicArray;


        if (enable_auto_config_update){
            try {
                String[][] idArray = new String[0][];
                String[][] operateArray = new String[0][];
                String[][][] result = parseJsonToArrays(jsonPath,log);
                boolean valid = ConfigIsValid(result,log);
                if (valid) {
                    if (result != null) {
                        idArray = result[0];
                    }
                    if (result != null) {
                        operateArray = result[1];
                    }

                    if (operateArray.length != 0) {
                        Operate_modify_rule_online = operateArray;
                        SurgeryExist = merge(SurgeryExist, operateArray);
                    }
                    if (idArray.length != 0) {
                        ID_modify_rule_online = idArray;
                        IdLogicArray = merge(IdLogicArray, idArray);
                    }

                }
            } catch (Exception e) {
                Message.FE(translateDirect( "console.config.online.read.error")+e.getMessage());
            }
        }


        if (enable_manual_config){
            try {
                String[][] idArray2 = new String[0][];
                String[][] operateArray2 = new String[0][];
                String[][][] result2 = parseJsonToArrays(UserRulePath + "/" + UserRuleFile,log);
                boolean valid2 = ConfigIsValid(result2,log);
                if (valid2) {
                    if (result2 != null) {
                        idArray2 = result2[0];
                    }
                    if (result2 != null) {
                        operateArray2 = result2[1];
                    }
                    if (operateArray2.length != 0) {
                        Operate_modify_rule_manual = operateArray2;
                        SurgeryExist = merge(SurgeryExist, operateArray2);
                    }
                    if (idArray2.length != 0) {
                        ID_modify_rule_manual = idArray2;
                        IdLogicArray = merge(IdLogicArray, idArray2);
                    }

                }
            } catch (Exception e) {
                Message.FE(translateDirect("console.config.manual.read.error")+e.getMessage());
            }
        }

       // Message.FM("Local Result");
       // ArrayListOutput(SurgeryExist);
       // ArrayListOutput(IdLogicArray);

        ID_match_rule = IdLogicArray;
        Operate_match_rule = SurgeryExist;

        return log;
    }


    // 测试方法
    public static void main(String[] args) {
        List<String> log = new ArrayList<>();
        UpdateRuleThread(log);
        Message.FP(Arrays.toString(log.toArray()));

    }
}
