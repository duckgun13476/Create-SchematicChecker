package com.Pink_Cats.createschematicchecker.core.attach;

public class Math {

    public static String trimALL(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", "");
    }

    public static int StringToInt(String str) {
        return Integer.parseInt(str);
    }

    public static float StringToFloat(String str) {
        return Float.parseFloat(str);
    }

}
