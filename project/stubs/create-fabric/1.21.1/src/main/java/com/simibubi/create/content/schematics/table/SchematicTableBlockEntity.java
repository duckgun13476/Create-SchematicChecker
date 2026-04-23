package com.simibubi.create.content.schematics.table;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SchematicTableBlockEntity extends BlockEntity {
    public final SchematicTableInventory inventory = new SchematicTableInventory();

    public SchematicTableBlockEntity() {
        super(null, BlockPos.ZERO, Blocks.AIR.defaultBlockState());
    }

    public static class SchematicTableInventory {
        private final ItemStack[] stacks = {ItemStack.EMPTY, ItemStack.EMPTY};

        public void setStackInSlot(int slot, ItemStack stack) {
            if (slot < 0 || slot >= stacks.length) {
                return;
            }
            stacks[slot] = stack;
        }
    }
}
