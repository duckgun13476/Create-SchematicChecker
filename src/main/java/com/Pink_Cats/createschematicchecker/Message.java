package com.Pink_Cats.createschematicchecker;

public class Message {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m"; // 黄色
    public static final String CYAN = "\u001B[36m";   // 青色
    public static final String MAGENTA = "\u001B[35m"; // 品红
    public static final String WHITE = "\u001B[37m";   // 白色
    public static final String BLACK = "\u001B[30m";   // 黑色



    public static void FD(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.info(BLUE + "{}" + RESET, messageString);
    }
    public static void FW(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.warn(YELLOW + "{}" + RESET, messageString);
    }
    public static void FE(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.error(RED + "{}" + RESET, messageString);
    }
    public static void FM(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.info(GREEN + "{}" + RESET, messageString);
    }
    public static void FP(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.info(MAGENTA + "{}" + RESET, messageString);
    }

}
