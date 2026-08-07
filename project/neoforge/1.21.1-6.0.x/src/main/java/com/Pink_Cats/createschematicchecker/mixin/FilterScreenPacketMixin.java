package com.Pink_Cats.createschematicchecker.mixin;

import com.simibubi.create.content.logistics.filter.FilterMenu;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket;
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

/**
 * Server-side authority for Create 6 filter ghost slots.  Do not allow a client to
 * persist a filter entry whose item components were never present in its inventory.
 */
@Mixin(value = FilterScreenPacket.class, remap = false)
public abstract class FilterScreenPacketMixin {

    @Shadow @Final private FilterScreenPacket.Option option;
    @Shadow @Final private CompoundTag data;

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void csc$validateFilterItem(ServerPlayer player, CallbackInfo ci) {
        if (option != FilterScreenPacket.Option.UPDATE_FILTER_ITEM) {
            return;
        }

        if (data == null || !(player.containerMenu instanceof FilterMenu filterMenu)) {
            ci.cancel();
            return;
        }

        int slot = data.getInt("Slot");
        if (slot < 0 || slot >= filterMenu.ghostInventory.getSlots()) {
            ci.cancel();
            return;
        }

        ItemStack requested = ItemStack.parseOptional(player.registryAccess(), data.getCompound("Item"));
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
            if (!owned.isEmpty() && ItemStack.isSameItemSameComponents(requested, owned)) {
                return true;
            }
        }
        return false;
    }
}
