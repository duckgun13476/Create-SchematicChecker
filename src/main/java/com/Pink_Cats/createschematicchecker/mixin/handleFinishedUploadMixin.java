package com.Pink_Cats.createschematicchecker.mixin;

import com.Pink_Cats.createschematicchecker.Compat.PendingSchematicStore;
import com.simibubi.create.content.schematics.ServerSchematicLoader;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import com.Pink_Cats.createschematicchecker.event.SchematicUploadEvent;
import com.Pink_Cats.createschematicchecker.lang.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(value = ServerSchematicLoader.class, remap = false)
public abstract class handleFinishedUploadMixin {

    @Unique
    private static final ThreadLocal<Level> TL_WORLD = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<BlockPos> TL_POS = new ThreadLocal<>();

    @Shadow
    private Map<String, ServerSchematicLoader.SchematicUploadEntry> activeUploads;

    @Unique
    private static final ThreadLocal<String> TL_ID = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<ServerPlayer> TL_PLAYER = new ThreadLocal<>();


    @Inject(method = "handleFinishedUpload", at = @At("HEAD"), remap = false)
    private void onHead(ServerPlayer player, String schematic, CallbackInfo ci) {
        TL_PLAYER.set(player);
        TL_ID.set(player.getGameProfile().getName() + "/" + schematic);
        Message.diag("[Diag][UploadMixin][HEAD] player=" + player.getGameProfile().getName()
                + ", schematic=" + schematic
                + ", composedId=" + TL_ID.get());
    }


    @Inject(
            method = "handleFinishedUpload",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/schematics/ServerSchematicLoader;getTable(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lcom/simibubi/create/content/schematics/table/SchematicTableBlockEntity;",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            remap = false
    )
    private void captureWorldPos(ServerPlayer player, String schematic, CallbackInfo ci, String playerSchematicId, ServerSchematicLoader.SchematicUploadEntry removed, Level world, BlockPos pos, BlockState blockState) {
        TL_WORLD.set(world);
        TL_POS.set(pos);
        Message.diag("[Diag][UploadMixin][POS] playerSchematicId=" + playerSchematicId
                + ", world=" + (world == null ? "null" : world.dimension().location())
                + ", pos=" + pos);
    }


    @Redirect(
            method = "handleFinishedUpload",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/schematics/table/SchematicTableBlockEntity$SchematicTableInventory;setStackInSlot(ILnet/minecraft/world/item/ItemStack;)V"
            ),
            remap = true
    )
    private void interceptSlot1(SchematicTableBlockEntity.SchematicTableInventory inventory, int slot, ItemStack stack) {

        if (slot != 1) {
            inventory.setStackInSlot(slot, stack);
            return;
        }

        String id = TL_ID.get();
        Level w = TL_WORLD.get();
        BlockPos p = TL_POS.get();

        if (id != null && stack != null && !stack.isEmpty() && w != null && p != null) {
            PendingSchematicStore.put(id, stack.copy(), w.dimension(), p);
            Message.diag("[Diag][UploadMixin][STORE] id=" + id
                    + ", slot=" + slot
                    + ", stack=" + stack
                    + ", dim=" + w.dimension().location()
                    + ", pos=" + p);
        } else if (id != null && stack != null && !stack.isEmpty()) {
            PendingSchematicStore.put(id, stack.copy(), null, null);
            Message.diag("[Diag][UploadMixin][STORE] id=" + id
                    + ", slot=" + slot
                    + ", stack=" + stack
                    + ", dim=null, pos=null");
        }

        // Pick blueprint
        inventory.setStackInSlot(1, ItemStack.EMPTY);
    }


    @Inject(method = "handleFinishedUpload", at = @At("TAIL"), remap = false)
    private void fireEvent(ServerPlayer player, String schematic, CallbackInfo ci) {
        String id = TL_ID.get();
        if (id == null) id = player.getGameProfile().getName() + "/" + schematic;
        Message.diag("[Diag][UploadMixin][TAIL] player=" + player.getGameProfile().getName()
                + ", schematic=" + schematic
                + ", eventId=" + id);

        SchematicUploadEvent uploadEvent = new SchematicUploadEvent(player, id, schematic);
        NeoForge.EVENT_BUS.post(uploadEvent);

        TL_WORLD.remove();
        TL_POS.remove();
        TL_ID.remove();
        TL_PLAYER.remove();
    }
}
