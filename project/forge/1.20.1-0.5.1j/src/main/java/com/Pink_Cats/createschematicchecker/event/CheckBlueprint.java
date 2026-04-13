package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.Compat.PendingSchematicStore;
import com.Pink_Cats.createschematicchecker.Compat.pattern_schematics.PatternSchematicsCompat;
import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import com.pinkcats.torque.layer.net.minecraft.nbt.CompoundTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.Nbt;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.S_bool;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.getCurrentDateTime;
import static com.Pink_Cats.createschematicchecker.event.TempOffEvent.CheckSchematic;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;


public class CheckBlueprint {

    private final BlueCore Checker;

    public CheckBlueprint(BlueCore _filter) {
        Checker = _filter;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void OnSchematicUpload(SchematicUploadEvent event) {
        new Thread(() -> handleBlueprintUpload(event), "CSC-SchematicScan").start();
    }

    // run on main server thread
    private void runOnServerMainThread(ServerPlayer player, Runnable task) {
        if (player == null) return;
        if (player.getServer() == null) return;

        if (player.getServer().isSameThread()) {
            task.run();
        } else {
            player.getServer().execute(task);
        }
    }


    private void applyResult(ServerPlayer player, String id, boolean pass) {
        runOnServerMainThread(player, () -> {
            var list = ModList.get();
            boolean HAS_PATTERN = list != null && list.isLoaded("create_pattern_schematics");
            PendingSchematicStore.PendingMeta meta = PendingSchematicStore.takeMeta(id);
            ItemStack pending = PendingSchematicStore.takeStack(id);
            Message.diag("[Diag][CheckBlueprint][APPLY] id=" + id
                    + ", pass=" + pass
                    + ", hasMeta=" + (meta != null)
                    + ", hasPending=" + (pending != null && !pending.isEmpty())
                    + ", hasPattern=" + HAS_PATTERN);

            if (meta == null) {
                Message.diag("[Diag][CheckBlueprint][APPLY] meta missing for id=" + id);
                PendingSchematicStore.drop(id);
                return;
            }

            Level w = player.getServer().getLevel(meta.dim);
            if (w == null) {
                Message.diag("[Diag][CheckBlueprint][APPLY] world missing for id=" + id + ", dim=" + meta.dim.location());
                return;
            }

            BlockEntity be = w.getBlockEntity(meta.pos);
            if (!(be instanceof SchematicTableBlockEntity table)) {
                Message.diag("[Diag][CheckBlueprint][APPLY] table missing for id=" + id + ", pos=" + meta.pos);
                return;
            }
            if (table.isRemoved()) {
                Message.diag("[Diag][CheckBlueprint][APPLY] table removed for id=" + id + ", pos=" + meta.pos);
                return;
            }
            if (pass) {
                // restore to slot1 (keeps the other mod's item type)
                if (pending != null && !pending.isEmpty()) {
                    Message.diag("[Diag][CheckBlueprint][APPLY] restoring slot1 for id=" + id + ", stack=" + pending);
                    table.inventory.setStackInSlot(1, pending);
                }
            } else {
                Message.diag("[Diag][CheckBlueprint][APPLY] rejecting upload for id=" + id + ", hasPattern=" + HAS_PATTERN);

                // reject: slot0 empty schematic, slot1 stays empty
                if (HAS_PATTERN) {
                    if (pending != null && PatternSchematicsCompat.isPatternSchematicItem(pending.getItem())) {
                        ItemStack empty = PatternSchematicsCompat.getEmptyPatternSchematicStack();
                        if (!empty.isEmpty()) {
                            table.inventory.setStackInSlot(0, empty);
                        } else {
                            table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());
                        }
                    } else {
                        table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());
                    }
                } else {
                    table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());
                }

            }
        });
    }

    private void handleBlueprintUpload(SchematicUploadEvent event) {
        try {
            ServerPlayer player = event.Player;
            if (player == null || player.getServer() == null) return;


            String id = event.SchematicPath;
            String schematicName = event.SchematicId;
            String player_id = player.getGameProfile().getName();
            Message.diag("[Diag][CheckBlueprint][START] player=" + player_id
                    + ", eventPath=" + id
                    + ", schematicName=" + schematicName);

            if (!CheckSchematic) {
                Message.FE(translateDirect("console.stop.csc.temp.output"));

                String DefaultPath = "./schematics/uploaded/";
                String path = DefaultPath + id;

                CompoundTag nbt_data = Path_to_CompoundTag(path);
                String User = id.split("/")[0];
                String Blueprint = id.split("/")[1];
                Message.diag("[Diag][CheckBlueprint][BYPASS] fullPath=" + path
                        + ", user=" + User
                        + ", blueprintSegment=" + Blueprint
                        + ", slashCount=" + id.chars().filter(ch -> ch == '/').count());

                if (enable_backup) {
                    Checker.CompoundTag_to_Path(nbt_data, "config/CSC/backup/" + User + "/", getCurrentDateTime() + Blueprint);
                }
                Checker.CompoundTag_to_Path(nbt_data, DefaultPath + User + "/", Blueprint);

                applyResult(player, id, true); // pass: restore slot1
                return;
            }

            List<String> CheatLog = new ArrayList<>();
            long startTime = System.currentTimeMillis();

            Map<String, Object> CheckResult = Checker.SchematicBlueCore(id, CheatLog);
            Message.diag("[Diag][CheckBlueprint][RESULT] id=" + id
                    + ", resultIsNull=" + (CheckResult == null)
                    + ", resultKeys=" + (CheckResult == null ? "null" : CheckResult.keySet()));

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            String displayName = removeFirstPathComponent(id);

            if (!CheatLog.isEmpty()) {
                for (String s : CheatLog) {
                    Message.FE(s);
                }
            }
            Message.FP(translateDirect("console.total.time") + executionTime + " ms");

            if (CheckResult == null) {

                // treat as cannotCheck => reject
                applyResult(player, id, false);
                return;
            }

            CheckCount++;

            boolean IsCheatSchematic = S_bool(CheckResult.get("Cheat"));
            boolean IsProblem = S_bool(CheckResult.get("Problem"));
            boolean CannotCheck = S_bool(CheckResult.get("CannotCheck"));

            if (CannotCheck) {
                Message.FE(translateDirect("console.csc.cannotCheck"));
                applyResult(player, id, false);
                return;
            }

            if (!IsCheatSchematic) {
                if (IsProblem) {
                    ProblemCount += 1;
                    Message.FW(translateDirect("console.problemOutput") + player_id
                            + translateDirect("console.problemOutput2") + displayName);
                }

                // pass => restore pending stack
                applyResult(player, id, true);

            } else {
                CheatCount += 1;
                Message.FE(translateDirect("console.cheat.find"));
                Message.FE(translateDirect("console.CheatOutput") + player_id
                        + translateDirect("console.CheatOutput2") + displayName);

                if (DebugCheatFind) {
                    broadcast(displayName, player_id);
                }
                if (CheckRunCommand) {
                    ExecuteSomeCmd(displayName, player_id);
                }

                // reject => slot0 empty
                applyResult(player, id, false);
            }

        } catch (Exception e) {
            Message.diag("[Diag][CheckBlueprint][EXCEPTION] " + e);
            if (enable_debug) e.printStackTrace();
        }
    }

    public CompoundTag Path_to_CompoundTag(String SchematicPath) throws IOException {
        File BluePrint = new File(SchematicPath);
        try (FileInputStream file_stream = new FileInputStream(BluePrint)) {
            return Nbt.readCompressed(file_stream);
        }
    }

    public String removeFirstPathComponent(String path) {
        int firstSlashIndex = path.indexOf("/");
        if (firstSlashIndex != -1) {
            return path.substring(firstSlashIndex + 1);
        }
        return path;
    }

    public static List<ServerPlayer> getAllOnlinePlayers() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return new ArrayList<>();
        return new ArrayList<>(server.getPlayerList().getPlayers());
    }

    public static void broadcast(String PlayerBlueprintId, String PlayerName) {
        for (final Player player : getAllOnlinePlayers()) {
            player.sendSystemMessage(
                    Component.literal(
                                    translateDirect("console.CheatOutput") + PlayerName +
                                            translateDirect("console.CheatOutput2") + PlayerBlueprintId
                            )
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        }
    }

    public static void ExecuteSomeCmd(String PlayerBlueprintId, String PlayerName) {
        String command = "";
    }
}
