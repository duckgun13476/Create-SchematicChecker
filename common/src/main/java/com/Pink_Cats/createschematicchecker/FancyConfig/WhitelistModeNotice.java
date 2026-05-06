package com.Pink_Cats.createschematicchecker.FancyConfig;

import com.Pink_Cats.createschematicchecker.lang.Message;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

public final class WhitelistModeNotice {
    private static final Set<String> notifiedOperators = new HashSet<>();
    private static boolean pending;
    private static boolean whitelistModeEnabled;

    private WhitelistModeNotice() {
    }

    public static synchronized void markForCurrentStartup(boolean noticeEnabled, boolean modeEnabled) {
        updateForCurrentStartup(noticeEnabled, modeEnabled);
        if (pending) {
            Message.FM(noticeText());
        }
    }

    public static synchronized void updateForCurrentStartup(boolean noticeEnabled, boolean modeEnabled) {
        notifiedOperators.clear();
        whitelistModeEnabled = modeEnabled;
        pending = noticeEnabled;
    }

    public static void tryNotifyPlayer(Object player) {
        if (player == null || !hasOperatorPermission(player)) {
            return;
        }

        String operatorId = playerId(player);
        if (!shouldNotify(operatorId)) {
            return;
        }

        sendMessage(player, messageLines());
    }

    public static synchronized void clear() {
        pending = false;
        notifiedOperators.clear();
        whitelistModeEnabled = false;
    }

    private static synchronized boolean shouldNotify(String operatorId) {
        if (!pending || operatorId == null || operatorId.trim().isEmpty()) {
            return false;
        }
        return notifiedOperators.add(operatorId);
    }

    private static synchronized String noticeText() {
        return translateDirect(whitelistModeEnabled
                ? "console.whitelistid.notice.scan.on"
                : "console.whitelistid.notice.scan.off");
    }

    private static List<NoticeLine> messageLines() {
        String[] lines = noticeText().split("\\r?\\n");
        List<NoticeLine> noticeLines = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            noticeLines.add(new NoticeLine(lines[i], i == 1 ? "GREEN" : "GOLD"));
        }
        return noticeLines;
    }

    private static boolean hasOperatorPermission(Object player) {
        try {
            Method method = player.getClass().getMethod("hasPermissions", int.class);
            return (Boolean) method.invoke(player, 4);
        } catch (Exception ignored) {
            try {
                Method method = player.getClass().getMethod("canUseCommand", int.class, String.class);
                return (Boolean) method.invoke(player, 4, "csc");
            } catch (Exception ignoredAgain) {
                return false;
            }
        }
    }

    private static String playerId(Object player) {
        try {
            Object profile = player.getClass().getMethod("getGameProfile").invoke(player);
            Object id = profile.getClass().getMethod("getId").invoke(profile);
            if (id != null) {
                return id.toString();
            }
            Object name = profile.getClass().getMethod("getName").invoke(profile);
            return String.valueOf(name);
        } catch (Exception ignored) {
            return String.valueOf(System.identityHashCode(player));
        }
    }

    private static void sendMessage(Object player, List<NoticeLine> lines) {
        for (NoticeLine line : lines) {
            if (trySendModernComponent(player, line)) {
                continue;
            }
            if (trySendTextComponent(player, line,
                    "net.minecraft.network.chat.TextComponent",
                    "net.minecraft.network.chat.Component")) {
                continue;
            }
            trySendTextComponent(player, line,
                    "net.minecraft.util.text.StringTextComponent",
                    "net.minecraft.util.text.ITextComponent");
        }
    }

    private static boolean trySendModernComponent(Object player, NoticeLine line) {
        try {
            Class<?> componentClass = Class.forName("net.minecraft.network.chat.Component");
            Method literal = componentClass.getMethod("literal", String.class);
            Object component = literal.invoke(null, line.text);
            component = withModernColor(component, line.colorName);
            Method sendSystemMessage = player.getClass().getMethod("sendSystemMessage", componentClass);
            sendSystemMessage.invoke(player, component);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static boolean trySendTextComponent(Object player, NoticeLine line, String componentClassName, String messageParameterClassName) {
        try {
            Class<?> componentClass = Class.forName(componentClassName);
            Class<?> messageParameterClass = Class.forName(messageParameterClassName);
            Constructor<?> constructor = componentClass.getConstructor(String.class);
            Object component = constructor.newInstance(line.text);
            component = withLegacyColor(component, line.colorName);

            try {
                Method sendMessage = player.getClass().getMethod("sendMessage", messageParameterClass);
                sendMessage.invoke(player, component);
                return true;
            } catch (NoSuchMethodException ignored) {
                Method sendMessage = player.getClass().getMethod("sendMessage", messageParameterClass, UUID.class);
                sendMessage.invoke(player, component, new UUID(0L, 0L));
                return true;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    private static Object withModernColor(Object component, String colorName) {
        try {
            Class<?> chatFormattingClass = Class.forName("net.minecraft.ChatFormatting");
            Object color = enumConstant(chatFormattingClass, colorName);
            Class<?> styleClass = Class.forName("net.minecraft.network.chat.Style");
            Object emptyStyle = styleClass.getField("EMPTY").get(null);
            Object coloredStyle = styleClass.getMethod("withColor", chatFormattingClass).invoke(emptyStyle, color);
            return component.getClass().getMethod("setStyle", styleClass).invoke(component, coloredStyle);
        } catch (Exception ignored) {
            return component;
        }
    }

    private static Object withLegacyColor(Object component, String colorName) {
        try {
            Class<?> textFormattingClass = Class.forName("net.minecraft.util.text.TextFormatting");
            Object color = enumConstant(textFormattingClass, colorName);
            Class<?> styleClass = Class.forName("net.minecraft.util.text.Style");
            Object emptyStyle = styleClass.getField("EMPTY").get(null);
            Object coloredStyle = styleClass.getMethod("applyFormatting", textFormattingClass).invoke(emptyStyle, color);
            return component.getClass().getMethod("setStyle", styleClass).invoke(component, coloredStyle);
        } catch (Exception ignored) {
            return component;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object enumConstant(Class<?> enumClass, String constantName) {
        return Enum.valueOf((Class<? extends Enum>) enumClass.asSubclass(Enum.class), constantName);
    }

    private static final class NoticeLine {
        private final String text;
        private final String colorName;

        private NoticeLine(String text, String colorName) {
            this.text = text;
            this.colorName = colorName;
        }
    }
}
