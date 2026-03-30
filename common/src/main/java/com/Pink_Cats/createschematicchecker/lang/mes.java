package com.Pink_Cats.createschematicchecker.lang;

import com.Pink_Cats.createschematicchecker.Createschematicchecker;

public class mes {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String WHITE = "\u001B[37m";
    public static final String BLACK = "\u001B[30m";

    public static final String LOGO = "[CL]";

    public static void blue(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.info(CYAN + LOGO + BLUE + "{}" + RESET, messageString);
    }

    public static void warn(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.warn(CYAN + LOGO + YELLOW + "{}" + RESET, messageString);
    }

    public static void error(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.error(CYAN + LOGO + RED + "{}" + RESET, messageString);
    }

    public static void mess(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.info(CYAN + LOGO + GREEN + "{}" + RESET, messageString);
    }

    public static void mega(Object message) {
        String messageString = String.valueOf(message);
        Createschematicchecker.LOGGER.info(CYAN + LOGO + MAGENTA + "{}" + RESET, messageString);
    }
}
