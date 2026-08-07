package com.Pink_Cats.createschematicchecker.mixin;

import com.simibubi.create.content.logistics.filter.FilterMenu;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** See the Forge 1.20.1 Create 6.0 counterpart for the security rationale. */
@Mixin(value = FilterScreenPacket.class, remap = false)
public abstract class FilterScreenPacketMixin {

    @Shadow @Final private FilterScreenPacket.Option option;
    @Shadow @Final private CompoundTag data;

    @Inject(method = "lambda$handle$0", at = @At("HEAD"), cancellable = true)
    private void csc$validateFilterItem(SimplePacketBase.Context context, CallbackInfo ci) {
        if (option != FilterScreenPacket.Option.UPDATE_FILTER_ITEM) {
            return;
        }

        ServerPlayer player = context.getSender();
        if (player == null || !(player.containerMenu instanceof FilterMenu filterMenu)) {
            return;
        }

        int slot = data.getInt("Slot");
        if (slot < 0 || slot >= filterMenu.ghostInventory.getSlotCount()) {
            ci.cancel();
            return;
        }

        ItemStack requested = ItemStack.of(data.getCompound("Item"));
        if (requested.isEmpty()) {
            return;
        }

        if (requested.getCount() != 1 || !csc$isOwnedBy(player, requested)) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean csc$isOwnedBy(ServerPlayer player, ItemStack requested) {
        for (int index = 0; index < player.getInventory().getContainerSize(); index++) {
            ItemStack owned = player.getInventory().getItem(index);
            if (!owned.isEmpty() && ItemStack.isSameItemSameTags(requested, owned)) {
                return true;
            }
        }
        return false;
    }
}
