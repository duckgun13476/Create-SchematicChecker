package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.Compat.PendingSchematicStore;
import com.Pink_Cats.createschematicchecker.Compat.pattern_schematics.PatternSchematicsCompat;
import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.simibubi.create.AllItems;
import com.simibubi.create.modules.schematics.block.SchematicTableTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
        ServerPlayerEntity player = event.Player;
        if (player == null || player.getServer() == null) return;

        String playerName = player.getGameProfile().getName();
        BlueprintUploadProcessor processor = new BlueprintUploadProcessor(
                checker,
                (id, pass) -> applyResult(player, id, pass),
                CheckBlueprint::broadcast,
                CheckBlueprint::ExecuteSomeCmd
        );
        SchematicScanTasks.submit(
                () -> processor.handle(event.SchematicPath, event.SchematicId, playerName),
                () -> applyResult(player, event.SchematicId, false)
        );
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        ServerTickTasks.tick();
    }

    private void runOnServerMainThread(ServerPlayerEntity player, Runnable task) {
        if (player == null || player.getServer() == null) return;

        if (player.getServer().isOnExecutionThread()) {
            task.run();
        } else {
            player.getServer().execute(task);
        }
    }

    private void applyResult(ServerPlayerEntity player, String id, boolean pass) {
        runOnServerMainThread(player, () -> applyResultOnServerThread(player, id, pass));
    }

    private void applyResultOnServerThread(ServerPlayerEntity player, String id, boolean pass) {
        ModList list = ModList.get();
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

        DimensionType dimension = meta.dim;
        ServerWorld world = dimension == null ? null : player.getServer().getWorld(dimension);
        if (world == null) {
            Message.diag("[Diag][CheckBlueprint][APPLY] world missing for id=" + id + ", dim=" + dimension);
            return;
        }

        TileEntity blockEntity = world.getTileEntity(meta.pos);
        if (!(blockEntity instanceof SchematicTableTileEntity)) {
            Message.diag("[Diag][CheckBlueprint][APPLY] table missing for id=" + id + ", pos=" + meta.pos);
            return;
        }
        SchematicTableTileEntity table = (SchematicTableTileEntity) blockEntity;
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
            table.inventory.setStackInSlot(0, empty.isEmpty() ? new ItemStack(AllItems.EMPTY_BLUEPRINT.get()) : empty);
        } else {
            table.inventory.setStackInSlot(0, new ItemStack(AllItems.EMPTY_BLUEPRINT.get()));
        }
    }

    public static List<ServerPlayerEntity> getAllOnlinePlayers() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return new ArrayList<>();
        return new ArrayList<>(server.getPlayerList().getPlayers());
    }

    public static void broadcast(String playerBlueprintId, String playerName) {
        for (final ServerPlayerEntity player : getAllOnlinePlayers()) {
            player.sendMessage(new StringTextComponent(
                    translateDirect("console.CheatOutput") + playerName +
                            translateDirect("console.CheatOutput2") + playerBlueprintId
            ).setStyle(new Style().setColor(TextFormatting.GOLD)));
        }
    }

    public static void ExecuteSomeCmd(String playerBlueprintId, String playerName) {
        String command = "";
    }
}
