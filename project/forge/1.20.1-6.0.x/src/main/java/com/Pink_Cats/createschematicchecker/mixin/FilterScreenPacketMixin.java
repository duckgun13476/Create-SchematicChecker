package com.Pink_Cats.createschematicchecker.mixin;

import com.simibubi.create.content.logistics.filter.FilterMenu;
import com.simibubi.create.content.logistics.filter.FilterScreenPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.validate_filter_screen_packet;

/**
 * Create's filter packet previously accepted a complete ItemStack NBT payload from
 * the client.  A filter is only a ghost slot, but its persisted NBT can later be
 * returned or copied by other Create interactions.  Require a real matching stack
 * in the sender's inventory before accepting the ghost entry.
 */
@Mixin(value = FilterScreenPacket.class, remap = false)
public abstract class FilterScreenPacketMixin {

    @Shadow @Final private FilterScreenPacket.Option option;
    @Shadow @Final private CompoundTag data;

    @Inject(method = "lambda$handle$0", at = @At("HEAD"), cancellable = true)
    private void csc$validateFilterItem(NetworkEvent.Context context, CallbackInfo ci) {
        if (!validate_filter_screen_packet) {
            return;
        }
        if (option != FilterScreenPacket.Option.UPDATE_FILTER_ITEM) {
            return;
        }

        ServerPlayer player = context.getSender();
        if (player == null || !(player.containerMenu instanceof FilterMenu filterMenu)) {
            return;
        }

        int slot = data.getInt("Slot");
        if (slot < 0 || slot >= filterMenu.ghostInventory.getSlots()) {
            ci.cancel();
            return;
        }

        ItemStack requested = ItemStack.of(data.getCompound("Item"));
        if (requested.isEmpty()) {
            return;
        }

        // Normal ghost-slot updates use a single representative item.  The item
        // itself must already exist in the sender's inventory with identical NBT.
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
