package com.Pink_Cats.createschematicchecker.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger("CreateSchematicChecker");

    public static void blue(Object message) {
        String messageString = String.valueOf(message);
        LOGGER.info(CYAN + LOGO + BLUE + "{}" + RESET, messageString);
    }

    public static void warn(Object message) {
        String messageString = String.valueOf(message);
        LOGGER.warn(CYAN + LOGO + YELLOW + "{}" + RESET, messageString);
    }

    public static void error(Object message) {
        String messageString = String.valueOf(message);
        LOGGER.error(CYAN + LOGO + RED + "{}" + RESET, messageString);
    }

    public static void mess(Object message) {
        String messageString = String.valueOf(message);
        LOGGER.info(CYAN + LOGO + GREEN + "{}" + RESET, messageString);
    }

    public static void mega(Object message) {
        String messageString = String.valueOf(message);
        LOGGER.info(CYAN + LOGO + MAGENTA + "{}" + RESET, messageString);
    }
}
