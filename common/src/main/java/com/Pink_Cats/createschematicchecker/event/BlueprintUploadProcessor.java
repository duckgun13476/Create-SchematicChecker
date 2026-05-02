package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.pinkcats.torque.layer.net.minecraft.nbt.CompoundTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.Nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.S_bool;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.getCurrentDateTime;
import static com.Pink_Cats.createschematicchecker.event.BlueprintPaths.removeFirstPathComponent;
import static com.Pink_Cats.createschematicchecker.event.TempOffTicker.CheckSchematic;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

public class BlueprintUploadProcessor {
    public interface ResultApplier {
        void apply(String id, boolean pass);
    }

    public interface CheatBroadcaster {
        void broadcast(String blueprintId, String playerName);
    }

    public interface CommandRunner {
        void run(String blueprintId, String playerName);
    }

    public interface NoticeSender {
        void send(String notice);
    }

    private final BlueCore checker;
    private final ResultApplier resultApplier;
    private final CheatBroadcaster cheatBroadcaster;
    private final CommandRunner commandRunner;
    private final NoticeSender noticeSender;

    public BlueprintUploadProcessor(
            BlueCore checker,
            ResultApplier resultApplier,
            CheatBroadcaster cheatBroadcaster,
            CommandRunner commandRunner
    ) {
        this(checker, resultApplier, cheatBroadcaster, commandRunner, notice -> {
        });
    }

    public BlueprintUploadProcessor(
            BlueCore checker,
            ResultApplier resultApplier,
            CheatBroadcaster cheatBroadcaster,
            CommandRunner commandRunner,
            NoticeSender noticeSender
    ) {
        this.checker = checker;
        this.resultApplier = resultApplier;
        this.cheatBroadcaster = cheatBroadcaster;
        this.commandRunner = commandRunner;
        this.noticeSender = noticeSender;
    }

    public void handle(String id, String schematicName, String playerName) {
        try {
            Message.diag("[Diag][CheckBlueprint][START] player=" + playerName
                    + ", eventPath=" + id
                    + ", schematicName=" + schematicName);

            if (!CheckSchematic) {
                passWithoutChecking(id);
                return;
            }

            List<String> cheatLog = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            Map<String, Object> checkResult = checker.SchematicBlueCore(id, cheatLog);
            Message.diag("[Diag][CheckBlueprint][RESULT] id=" + id
                    + ", resultIsNull=" + (checkResult == null)
                    + ", resultKeys=" + (checkResult == null ? "null" : checkResult.keySet()));

            long executionTime = System.currentTimeMillis() - startTime;
            String displayName = removeFirstPathComponent(id);

            for (String line : cheatLog) {
                Message.FE(line);
            }
            Message.FP(translateDirect("console.total.time") + executionTime + " ms");
            logWhitelistModeNotice();

            if (checkResult == null) {
                resultApplier.apply(id, false);
                return;
            }

            CheckCount++;

            boolean isCheatSchematic = S_bool(checkResult.get("Cheat"));
            boolean isProblem = S_bool(checkResult.get("Problem"));
            boolean cannotCheck = S_bool(checkResult.get("CannotCheck"));

            if (cannotCheck) {
                Message.FE(translateDirect("console.csc.cannotCheck"));
                resultApplier.apply(id, false);
                return;
            }

            if (!isCheatSchematic) {
                handlePass(id, displayName, playerName, isProblem);
                return;
            }

            handleCheat(id, displayName, playerName);
        } catch (Exception e) {
            Message.diag("[Diag][CheckBlueprint][EXCEPTION] " + e);
            if (enable_debug) e.printStackTrace();
        }
    }

    private void passWithoutChecking(String id) throws IOException {
        Message.FE(translateDirect("console.stop.csc.temp.output"));

        String defaultPath = "./schematics/uploaded/";
        String path = defaultPath + id;

        CompoundTag nbtData = pathToCompoundTag(path);
        String user = id.split("/")[0];
        String blueprint = id.split("/")[1];
        Message.diag("[Diag][CheckBlueprint][BYPASS] fullPath=" + path
                + ", user=" + user
                + ", blueprintSegment=" + blueprint
                + ", slashCount=" + id.chars().filter(ch -> ch == '/').count());

        if (enable_backup) {
            checker.CompoundTag_to_Path(nbtData, "config/CSC/backup/" + user + "/", getCurrentDateTime() + blueprint);
        }
        checker.CompoundTag_to_Path(nbtData, defaultPath + user + "/", blueprint);

        resultApplier.apply(id, true);
    }

    private void handlePass(String id, String displayName, String playerName, boolean isProblem) {
        if (isProblem) {
            ProblemCount += 1;
            Message.FW(translateDirect("console.problemOutput") + playerName
                    + translateDirect("console.problemOutput2") + displayName);
        }

        resultApplier.apply(id, true);
    }

    private void handleCheat(String id, String displayName, String playerName) {
        CheatCount += 1;
        Message.FE(translateDirect("console.cheat.find"));
        Message.FE(translateDirect("console.CheatOutput") + playerName
                + translateDirect("console.CheatOutput2") + displayName);

        if (DebugCheatFind) {
            cheatBroadcaster.broadcast(displayName, playerName);
        }
        if (CheckRunCommand) {
            commandRunner.run(displayName, playerName);
        }

        resultApplier.apply(id, false);
    }

    private void logWhitelistModeNotice() {
        if (!white_list_mod_notice) {
            return;
        }

        String notice = translateDirect(white_list_mod_enable
                ? "console.whitelistid.notice.scan.on"
                : "console.whitelistid.notice.scan.off");
        Message.FM(notice);
        noticeSender.send(notice);
    }

    public static CompoundTag pathToCompoundTag(String schematicPath) throws IOException {
        File blueprint = new File(schematicPath);
        try (FileInputStream fileStream = new FileInputStream(blueprint)) {
            return Nbt.readCompressed(fileStream);
        }
    }
}
