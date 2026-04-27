package com.Pink_Cats.createschematicchecker.mixin;

import com.Pink_Cats.createschematicchecker.Compat.PendingSchematicStore;
import com.Pink_Cats.createschematicchecker.event.SchematicUploadEvent;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.simibubi.create.content.schematics.ServerSchematicLoader;
import com.simibubi.create.content.schematics.block.SchematicTableTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ServerSchematicLoader.class, remap = false)
public abstract class handleFinishedUploadMixin {

    @Unique
    private static final ThreadLocal<World> TL_WORLD = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<BlockPos> TL_POS = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<String> TL_ID = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<ServerPlayerEntity> TL_PLAYER = new ThreadLocal<>();

    @Inject(method = "handleFinishedUpload", at = @At("HEAD"), remap = false)
    private void onHead(ServerPlayerEntity player, String schematic, CallbackInfo ci) {
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
                    target = "Lcom/simibubi/create/content/schematics/ServerSchematicLoader;getTable(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lcom/simibubi/create/content/schematics/block/SchematicTableTileEntity;",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            remap = false
    )
    private void captureWorldPos(ServerPlayerEntity player, String schematic, CallbackInfo ci, String playerSchematicId,
                                 ServerSchematicLoader.SchematicUploadEntry removed, World world, BlockPos pos,
                                 BlockState blockState) {
        TL_WORLD.set(world);
        TL_POS.set(pos);
        Message.diag("[Diag][UploadMixin][POS] playerSchematicId=" + playerSchematicId
                + ", world=" + world
                + ", pos=" + pos);
    }

    @Redirect(
            method = "handleFinishedUpload",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/schematics/block/SchematicTableTileEntity$SchematicTableInventory;setStackInSlot(ILnet/minecraft/item/ItemStack;)V"
            ),
            remap = false
    )
    private void interceptSlot1(SchematicTableTileEntity.SchematicTableInventory inventory, int slot, ItemStack stack) {
        if (slot != 1) {
            inventory.setStackInSlot(slot, stack);
            return;
        }

        String id = TL_ID.get();
        World world = TL_WORLD.get();
        BlockPos pos = TL_POS.get();

        if (id != null && stack != null && !stack.isEmpty() && world != null && pos != null) {
            PendingSchematicStore.put(id, stack.copy(), world.getDimensionKey(), pos);
            Message.diag("[Diag][UploadMixin][STORE] id=" + id
                    + ", slot=" + slot
                    + ", stack=" + stack
                    + ", dim=" + world.getDimensionKey()
                    + ", pos=" + pos);
        } else if (id != null && stack != null && !stack.isEmpty()) {
            PendingSchematicStore.put(id, stack.copy(), null, null);
            Message.diag("[Diag][UploadMixin][STORE] id=" + id
                    + ", slot=" + slot
                    + ", stack=" + stack
                    + ", dim=null, pos=null");
        }

        inventory.setStackInSlot(1, ItemStack.EMPTY);
    }

    @Inject(method = "handleFinishedUpload", at = @At("TAIL"), remap = false)
    private void fireEvent(ServerPlayerEntity player, String schematic, CallbackInfo ci) {
        String id = TL_ID.get();
        if (id == null) id = player.getGameProfile().getName() + "/" + schematic;
        Message.diag("[Diag][UploadMixin][TAIL] player=" + player.getGameProfile().getName()
                + ", schematic=" + schematic
                + ", eventId=" + id);

        MinecraftForge.EVENT_BUS.post(new SchematicUploadEvent(player, id, schematic));

        TL_WORLD.remove();
        TL_POS.remove();
        TL_ID.remove();
        TL_PLAYER.remove();
    }
}
