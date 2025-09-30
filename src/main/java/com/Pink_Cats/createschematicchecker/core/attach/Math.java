package com.Pink_Cats.createschematicchecker.core.attach;

public class Math {

    public static String trimALL(String input) {
        if (input == null) {
            return null; // 如果输入为 null，返回 null
        }
        return input.replaceAll("\\s+", ""); // 使用正则表达式去掉所有空格
    }

}
