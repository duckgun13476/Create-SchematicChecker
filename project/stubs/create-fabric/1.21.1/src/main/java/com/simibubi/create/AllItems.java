package com.simibubi.create;

import net.minecraft.world.item.ItemStack;

public final class AllItems {
    public static final ItemEntry EMPTY_SCHEMATIC = new ItemEntry();

    private AllItems() {
    }

    public static final class ItemEntry {
        public ItemStack asStack() {
            return ItemStack.EMPTY.copy();
        }
    }
}
