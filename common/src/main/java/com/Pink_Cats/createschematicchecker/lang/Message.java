package com.Pink_Cats.createschematicchecker.lang;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.Locale;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.enable_debug;
import static com.Pink_Cats.createschematicchecker.database.SingleLog.CSC_MES;
import static com.Pink_Cats.createschematicchecker.database.SingleLog.CSC_WARN;

public class Message {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String WHITE = "\u001B[37m";
    public static final String BLACK = "\u001B[30m";

    public static final String LOGO = "[CSC]";
    private static final Logger LOGGER = Logger.getLogger("CreateSchematicChecker");
    private static boolean loggingConfigured = false;
    private static final boolean USE_ASCII_FALLBACK =
            !isLikelyUtf8(System.getProperty("file.encoding"))
                    && !isLikelyUtf8(System.getProperty("sun.stdout.encoding"))
                    && !isLikelyUtf8(System.getProperty("sun.stderr.encoding"));

    private static boolean isLikelyUtf8(String encoding) {
        if (encoding == null) return false;
        String lower = encoding.toLowerCase(Locale.ROOT);
        return lower.contains("utf") || lower.contains("unicode");
    }

    private static String normalizeForConsole(Object message) {
        String messageString = String.valueOf(message);
        if (!USE_ASCII_FALLBACK) {
            return messageString;
        }

        StringBuilder builder = new StringBuilder(messageString.length() * 2);
        for (int i = 0; i < messageString.length(); i++) {
            char c = messageString.charAt(i);
            if (c <= 0x7F) {
                builder.append(c);
            } else {
                builder.append(String.format("\\u%04x", (int) c));
            }
        }
        return builder.toString();
    }

    static {
        configureLoggerFormat();
    }

    private static void configureLoggerFormat() {
        if (loggingConfigured) return;
        loggingConfigured = true;

        Formatter formatter = new Formatter() {
            private final SimpleDateFormat dateFormat =
                    new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.US);

            @Override
            public String format(LogRecord record) {
                String message = formatMessage(record);
                String throwable = "";
                if (record.getThrown() != null) {
                    StringWriter writer = new StringWriter();
                    record.getThrown().printStackTrace(new PrintWriter(writer));
                    throwable = System.lineSeparator() + writer;
                }

                return String.format(Locale.US, "%s %s %s: %s%s%n",
                        dateFormat.format(new Date(record.getMillis())),
                        record.getLevel().getName(),
                        record.getLoggerName(),
                        message,
                        throwable);
                }
        };

        try {
            Logger rootLogger = Logger.getLogger("");
            for (Handler handler : rootLogger.getHandlers()) {
                handler.setFormatter(formatter);
                try {
                    handler.setEncoding(StandardCharsets.UTF_8.name());
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void FD(Object message) {
        String messageString = String.valueOf(message);
        LOGGER.info(CYAN + LOGO + BLUE + normalizeForConsole(messageString) + RESET);
        CSC_MES.log("[INFO] " + messageString);
    }

    public static void FW(Object message) {
        String messageString = String.valueOf(message);
        LOGGER.warning(CYAN + LOGO + YELLOW + normalizeForConsole(messageString) + RESET);
        CSC_MES.log("[WARN] " + messageString);
        CSC_WARN.log("[WARN] " + messageString);
    }

    public static void FE(Object message) {
        String messageString = String.valueOf(message);
        LOGGER.severe(CYAN + LOGO + RED + normalizeForConsole(messageString) + RESET);
        CSC_MES.log("[ERROR] " + messageString);
        CSC_WARN.log("[ERROR] " + messageString);
    }

    public static void FM(Object message) {
        String messageString = String.valueOf(message);
        LOGGER.info(CYAN + LOGO + GREEN + normalizeForConsole(messageString) + RESET);
        CSC_MES.log("[INFO] " + messageString);
    }

    public static void FP(Object message) {
        String messageString = String.valueOf(message);
        LOGGER.info(CYAN + LOGO + MAGENTA + normalizeForConsole(messageString) + RESET);
        CSC_MES.log("[INFO] " + messageString);
    }

    public static void debug(Object message) {
        if (enable_debug) {
            String messageString = String.valueOf(message);
            LOGGER.info(CYAN + "[Debug]" + MAGENTA + normalizeForConsole(messageString) + RESET);
            CSC_MES.log("[DevelopDebug] " + messageString);
        }
    }

    public static void diag(Object message) {
        debug(message);
    }
}
