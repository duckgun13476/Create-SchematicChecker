package com.Pink_Cats.createschematicchecker.lang;

import com.Pink_Cats.createschematicchecker.Createschematicchecker;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.enable_debug;
import static com.Pink_Cats.createschematicchecker.database.SingleLog.CSC_MES;
import static com.Pink_Cats.createschematicchecker.database.SingleLog.CSC_WARN;

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

    public static final String LOGO = "[CSC]";

    public static void FD(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.info(CYAN+LOGO+BLUE + "{}" + RESET, messageString);
        CSC_MES.log("[INFO] "+messageString);
    }
    public static void FW(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.warn(CYAN+LOGO+YELLOW + "{}" + RESET, messageString);
        CSC_MES.log("[WARN] "+messageString);
        CSC_WARN.log("[WARN] "+messageString);
    }
    public static void FE(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.error(CYAN+LOGO+RED + "{}" + RESET, messageString);
        CSC_MES.log("[ERROR] "+messageString);
        CSC_WARN.log("[ERROR] "+messageString);
    }
    public static void FM(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.info(CYAN+LOGO+GREEN + "{}" + RESET, messageString);
        CSC_MES.log("[INFO] "+messageString);
    }
    public static void FP(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.info(CYAN+LOGO+MAGENTA + "{}" + RESET, messageString);
        CSC_MES.log("[INFO] "+messageString);
    }

    public static void debug(Object message) {
        if (enable_debug) {
            String messageString = String.valueOf(message);
            Createschematicchecker.LOGGER.info(CYAN + "[Debug]" + MAGENTA + "{}" + RESET, messageString);
            CSC_MES.log("[DevelopDebug] " + messageString);
        }
    }


}
