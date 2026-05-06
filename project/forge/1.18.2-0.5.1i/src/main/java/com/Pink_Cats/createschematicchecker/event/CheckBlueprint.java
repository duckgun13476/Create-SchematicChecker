package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.Compat.PendingSchematicStore;
import com.Pink_Cats.createschematicchecker.Compat.pattern_schematics.PatternSchematicsCompat;
import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

public class CheckBlueprint {
    private final BlueCore checker;

    public CheckBlueprint(BlueCore checker) {
        this.checker = checker;
    }

    @SubscribeEvent
    public void OnSchematicUpload(SchematicUploadEvent event) {
        ServerPlayer player = event.Player;
        if (player == null || player.getServer() == null) return;

        String playerName = player.getGameProfile().getName();
        BlueprintUploadProcessor processor = new BlueprintUploadProcessor(
                checker,
                (id, pass) -> applyResult(player, id, pass),
                CheckBlueprint::broadcast,
                CheckBlueprint::ExecuteSomeCmd
        );
        new Thread(() -> processor.handle(event.SchematicPath, event.SchematicId, playerName), "CSC-SchematicScan").start();
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
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
        var list = ModList.get();
        boolean hasPattern = list != null && list.isLoaded("create_pattern_schematics");
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
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return new ArrayList<>();
        return new ArrayList<>(server.getPlayerList().getPlayers());
    }

    public static void broadcast(String playerBlueprintId, String playerName) {
        for (final ServerPlayer player : getAllOnlinePlayers()) {
            player.sendMessage(
                    new TextComponent(
                            translateDirect("console.CheatOutput") + playerName +
                                    translateDirect("console.CheatOutput2") + playerBlueprintId
                    ).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                    Util.NIL_UUID
            );
        }
    }

    public static void ExecuteSomeCmd(String playerBlueprintId, String playerName) {
        String command = "";
    }
}
