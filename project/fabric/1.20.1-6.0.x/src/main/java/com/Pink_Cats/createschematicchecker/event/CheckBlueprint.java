package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.Compat.PendingSchematicStore;
import com.Pink_Cats.createschematicchecker.Compat.pattern_schematics.PatternSchematicsCompat;
import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

public class CheckBlueprint {
    private final BlueCore checker;
    private static volatile MinecraftServer currentServer;

    public CheckBlueprint(BlueCore checker) {
        this.checker = checker;
    }

    public static void setCurrentServer(MinecraftServer server) {
        currentServer = server;
    }

    public static void clearCurrentServer() {
        currentServer = null;
    }

    public void onSchematicUpload(SchematicUploadEvent event) {
        ServerPlayer player = event.Player;
        if (player == null || player.getServer() == null) return;

        String playerName = player.getGameProfile().getName();
        BlueprintUploadProcessor processor = new BlueprintUploadProcessor(
                checker,
                (id, pass) -> applyResult(player, id, pass),
                CheckBlueprint::broadcast,
                CheckBlueprint::ExecuteSomeCmd,
                notice -> sendNoticeToOperator(player, notice)
        );
        new Thread(() -> processor.handle(event.SchematicPath, event.SchematicId, playerName), "CSC-SchematicScan").start();
    }

    public void serverTickEvent(MinecraftServer server) {
        currentServer = server;
        ServerTickTasks.tick();
    }

    private void runOnServerMainThread(ServerPlayer player, Runnable task) {
        if (player == null || player.getServer() == null) return;

        if (player.getServer().isSameThread()) {
            task.run();
        } else {
            player.getServer().execute(task);
        }
    }

    private void applyResult(ServerPlayer player, String id, boolean pass) {
        runOnServerMainThread(player, () -> applyResultOnServerThread(player, id, pass));
    }

    private void applyResultOnServerThread(ServerPlayer player, String id, boolean pass) {
        boolean hasPattern = FabricLoader.getInstance().isModLoaded("create_pattern_schematics");
        PendingSchematicStore.PendingMeta meta = PendingSchematicStore.takeMeta(id);
        ItemStack pending = PendingSchematicStore.takeStack(id);
        Message.diag("[Diag][CheckBlueprint][APPLY] id=" + id
                + ", pass=" + pass
                + ", hasMeta=" + (meta != null)
                + ", hasPending=" + (pending != null && !pending.isEmpty())
                + ", hasPattern=" + hasPattern);

        if (meta == null) {
            Message.diag("[Diag][CheckBlueprint][APPLY] meta missing for id=" + id);
            PendingSchematicStore.drop(id);
            return;
        }

        Level world = player.getServer().getLevel(meta.dim);
        if (world == null) {
            Message.diag("[Diag][CheckBlueprint][APPLY] world missing for id=" + id + ", dim=" + meta.dim.location());
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(meta.pos);
        if (!(blockEntity instanceof SchematicTableBlockEntity table)) {
            Message.diag("[Diag][CheckBlueprint][APPLY] table missing for id=" + id + ", pos=" + meta.pos);
            return;
        }
        if (table.isRemoved()) {
            Message.diag("[Diag][CheckBlueprint][APPLY] table removed for id=" + id + ", pos=" + meta.pos);
            return;
        }
        if (pass) {
            if (pending != null && !pending.isEmpty()) {
                Message.diag("[Diag][CheckBlueprint][APPLY] restoring slot1 for id=" + id + ", stack=" + pending);
                table.inventory.setStackInSlot(1, pending);
            }
            return;
        }

        Message.diag("[Diag][CheckBlueprint][APPLY] rejecting upload for id=" + id + ", hasPattern=" + hasPattern);
        if (hasPattern && pending != null && PatternSchematicsCompat.isPatternSchematicItem(pending.getItem())) {
            ItemStack empty = PatternSchematicsCompat.getEmptyPatternSchematicStack();
            table.inventory.setStackInSlot(0, empty.isEmpty() ? AllItems.EMPTY_SCHEMATIC.asStack() : empty);
        } else {
            table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());
        }
    }

    public static List<ServerPlayer> getAllOnlinePlayers() {
        MinecraftServer server = currentServer;
        if (server == null) return new ArrayList<>();
        return new ArrayList<>(server.getPlayerList().getPlayers());
    }

    private void sendNoticeToOperator(ServerPlayer player, String notice) {
        runOnServerMainThread(player, () -> {
            if (!player.hasPermissions(4)) {
                return;
            }
            String[] lines = notice.split("\\r?\\n");
            for (int i = 0; i < lines.length; i++) {
                ChatFormatting color = i == 1 ? ChatFormatting.GREEN : ChatFormatting.GOLD;
                player.sendSystemMessage(Component.literal(lines[i]).setStyle(Style.EMPTY.withColor(color)));
            }
        });
    }

    public static void broadcast(String playerBlueprintId, String playerName) {
        for (final Player player : getAllOnlinePlayers()) {
            player.sendSystemMessage(
                    Component.literal(
                                    translateDirect("console.CheatOutput") + playerName +
                                            translateDirect("console.CheatOutput2") + playerBlueprintId
                            )
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        }
    }

    public static void ExecuteSomeCmd(String playerBlueprintId, String playerName) {
        String command = "";
    }
}
