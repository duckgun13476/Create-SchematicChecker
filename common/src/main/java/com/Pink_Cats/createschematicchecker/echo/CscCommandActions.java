package com.Pink_Cats.createschematicchecker.echo;

import com.Pink_Cats.createschematicchecker.event.TempOffTicker;
import com.Pink_Cats.createschematicchecker.FancyConfig.WhitelistModeNotice;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.Pink_Cats.createschematicchecker.online.SimpleHeartbeatPusher;
import com.Pink_Cats.createschematicchecker.online.VersionChecker;
import com.pinkcats.torque.layer.net.minecraft.commands.CommandColor;
import com.pinkcats.torque.layer.net.minecraft.commands.CommandSender;

import java.util.Arrays;
import java.util.Locale;
import java.util.List;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;
import static com.Pink_Cats.createschematicchecker.network.SimpleJsonParser.merge;

public class CscCommandActions {

    public static long executionTime;
    public static final int SINGLE_SUCCESS = 1;

    public static int reload(CommandSender sender) {
        try {
            long startTime = System.currentTimeMillis();
            List<String> reloadResult = CSC_RELOAD();
            long endTime = System.currentTimeMillis();
            executionTime = endTime - startTime;

            if (!reloadResult.isEmpty()) {
                Message.FE(translateDirect("console.manual.config.error"));
                send(sender, translateDirect("console.manual.config.error"), CommandColor.RED);
                for (String s : reloadResult) {
                    Message.FE("| " + s);
                    send(sender, "| " + s, CommandColor.RED);
                }
            } else {
                String message = translateDirect("console.ReloadSuccess") + executionTime + "ms";
                Message.FM(message);
                send(sender, message, CommandColor.GREEN);
            }
        } catch (Exception ex) {
            Message.FE(translateDirect("console.error") + ex.getMessage());
        }
        return SINGLE_SUCCESS;
    }

    public static int list(CommandSender sender) {
        send(sender, translateDirect("console.csc.run.command"), CommandColor.GOLD);
        send(sender, translateDirect("console.csc.list"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.list2"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.list3"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.list4"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.list5"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.list6"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.liner"), CommandColor.GOLD);
        return SINGLE_SUCCESS;
    }

    public static int mergeRuleOutput(CommandSender sender, String[][] idModifyRuleManual, String[][] idModifyRuleOnline) {
        try {
            for (String[] idRule : merge(idModifyRuleManual, idModifyRuleOnline)) {
                send(sender, Arrays.toString(idRule), CommandColor.GREEN);
            }
        } catch (Exception ex) {
            Message.FE(translateDirect("console.error") + ex.getMessage());
        }
        return SINGLE_SUCCESS;
    }

    public static int outputPara(CommandSender sender, String[][] idMatchRule) {
        try {
            for (String[] idRule : idMatchRule) {
                send(sender, Arrays.toString(idRule), CommandColor.GREEN);
            }
        } catch (Exception ex) {
            Message.FE(translateDirect("console.error") + ex.getMessage());
        }
        return SINGLE_SUCCESS;
    }

    public static int disable(CommandSender sender) {
        send(sender, translateDirect("console.disable.csc"), CommandColor.GOLD);
        TempOffTicker.Temporary_stop = true;
        return SINGLE_SUCCESS;
    }

    public static int enableWhitelistModeNotice(CommandSender sender) {
        setWhiteListModeNoticeCommand(sender, true);
        return SINGLE_SUCCESS;
    }

    public static int disableWhitelistModeNotice(CommandSender sender) {
        setWhiteListModeNoticeCommand(sender, false);
        return SINGLE_SUCCESS;
    }

    public static int enableWhitelistModMode(CommandSender sender) {
        setWhiteListModModeCommand(sender, true);
        return SINGLE_SUCCESS;
    }

    public static int disableWhitelistModMode(CommandSender sender) {
        setWhiteListModModeCommand(sender, false);
        return SINGLE_SUCCESS;
    }

    private static void setWhiteListModeNoticeCommand(CommandSender sender, boolean enabled) {
        setWhiteListModeNotice(enabled);
        CSC_RELOAD();
        WhitelistModeNotice.updateForCurrentStartup(white_list_mod_notice, white_list_mod_enable);
        send(sender, translateDirect(enabled ? "console.whitelistid.notice.on" : "console.whitelistid.notice.off"), CommandColor.GOLD);
    }

    private static void setWhiteListModModeCommand(CommandSender sender, boolean enabled) {
        setWhiteListModEnable(enabled);
        CSC_RELOAD();
        WhitelistModeNotice.updateForCurrentStartup(white_list_mod_notice, white_list_mod_enable);
        send(sender, translateDirect(enabled ? "console.whitelistid.mode.on" : "console.whitelistid.mode.off"), CommandColor.GOLD);
    }

    public static int enable(CommandSender sender) {
        send(sender, translateDirect("csc.off.temporary.restore"), CommandColor.GOLD);
        TempOffTicker.StopTick = 5;
        return SINGLE_SUCCESS;
    }

    public static int help(CommandSender sender) {
        send(sender, translateDirect("console.csc.run.command"), CommandColor.GOLD);
        sendClickable(sender, translateDirect("console.csc.welcome1"), CommandColor.LIGHT_PURPLE,
                "https://github.com/duckgun13476/Create-SchematicChecker/issues");
        if (isChineseLanguage()) {
            sendClickable(sender, translateDirect("console.csc.welcome2"), CommandColor.LIGHT_PURPLE,
                    "https://qm.qq.com/q/9N0m84sQfY");
        }
        sendClickable(sender, translateDirect("console.csc.welcome.discord"), CommandColor.LIGHT_PURPLE,
                "https://discord.gg/rQV5JPauY7");
        send(sender, translateDirect("console.csc.welcome3"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.welcome4"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.welcome5"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.welcome6"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.welcome7"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.whitelistid.help"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.whitelistid.mode.help"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.liner"), CommandColor.GOLD);
        return SINGLE_SUCCESS;
    }

    private static boolean isChineseLanguage() {
        String normalizedLanguage = DefineLanguage == null
                ? ""
                : DefineLanguage.trim().toLowerCase(Locale.ROOT);
        return normalizedLanguage.startsWith("zh_");
    }

    public static int main(CommandSender sender) {
        send(sender, translateDirect("console.csc.run.command"), CommandColor.GOLD);
        send(sender, translateDirect("console.csc.welcome"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.welcome.help"), CommandColor.GREEN);

        int seconds = Math.toIntExact(GuardTime % 60);
        int totalMinutes = Math.toIntExact(GuardTime / 60);
        int minutes = totalMinutes % 60;
        int totalHours = totalMinutes / 60;
        int hours = totalHours % 24;
        int days = totalHours / 24;

        send(sender, translateDirect("console.csc.board.guard") + days
                + translateDirect("console.csc.board.day") + hours
                + translateDirect("console.csc.board.hour") + minutes
                + translateDirect("console.csc.board.minute") + seconds
                + translateDirect("console.csc.board.second"), CommandColor.GREEN);

        send(sender, translateDirect("console.csc.board.checkCount") + CheckCount
                + translateDirect("console.csc.board.checkCount2"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.board.checkCount3") + ProblemCount
                + translateDirect("console.csc.board.checkCount4"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.board.checkCount5") + CheatCount
                + translateDirect("console.csc.board.checkCount6"), CommandColor.GREEN);
        send(sender, translateDirect("console.csc.liner"), CommandColor.GOLD);
        return SINGLE_SUCCESS;
    }

    public static int onlineTest(CommandSender sender, String mode) {
        try {
            sendTestMessage(sender, CommandColor.GOLD, "[CSC] Online test start: " + mode);

            if (mode.equals("heartbeat") || mode.equals("all")) {
                String payload = SimpleHeartbeatPusher.buildHeartbeatPayload();
                String response = SimpleHeartbeatPusher.debugHeartbeat();
                sendTestMessage(sender, CommandColor.AQUA, "[heartbeat] payload=" + payload);
                sendTestMessage(sender, response == null || response.isEmpty() ? CommandColor.RED : CommandColor.GREEN,
                        "[heartbeat] response=" + CommandText.shorten(response));
            }

            if (mode.equals("version") || mode.equals("all")) {
                String version = VersionChecker.debugFetchServerVersion();
                sendTestMessage(sender, CommandColor.GREEN, "[version] serverVersion=" + version);
            }

            if (mode.equals("rules") || mode.equals("all")) {
                String rulePath = VersionChecker.debugSyncRules();
                sendTestMessage(sender, rulePath == null ? CommandColor.RED : CommandColor.GREEN,
                        "[rules] localPath=" + rulePath);
            }

            if (mode.equals("feedback") || mode.equals("all")) {
                String feedback = VersionChecker.debugFetchServerFeedback();
                sendTestMessage(sender, feedback == null || feedback.isEmpty() ? CommandColor.RED : CommandColor.GREEN,
                        "[feedback] response=" + CommandText.shorten(feedback));
            }

            sendTestMessage(sender, CommandColor.GOLD, "[CSC] Online test end: " + mode);
        } catch (Exception ex) {
            if (enable_debug) {
                Message.FE("[CSC] Online test failed: " + ex.getMessage());
                sendTestMessage(sender, CommandColor.RED, "[CSC] Online test failed: " + ex.getMessage());
            }
        }
        return SINGLE_SUCCESS;
    }

    private static void sendTestMessage(CommandSender sender, CommandColor color, String text) {
        if (!enable_debug) {
            return;
        }
        Message.FM(text);
        send(sender, text, color);
    }

    private static void send(CommandSender sender, String text, CommandColor color) {
        if (sender != null && sender.hasPlayer()) {
            sender.send(text, color);
        }
    }

    private static void sendClickable(CommandSender sender, String text, CommandColor color, String url) {
        if (sender != null && sender.hasPlayer()) {
            sender.sendClickable(text, color, url);
        }
    }
}
