package com.Pink_Cats.createschematicchecker.Compat.pattern_schematics;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * No hard dependency on create_pattern_schematics.
 * Everything is resolved via registry lookup only if the mod item exists at runtime.
 */
public final class PatternSchematicsCompat {

    private PatternSchematicsCompat() {
    }

    public static ItemStack getEmptyPatternSchematicStack() {
        ResourceLocation id = ResourceLocation.tryParse(PatternSchematicsIds.EMPTY_PATTERN_SCHEMATIC_ID);
        if (id == null) {
            return ItemStack.EMPTY;
        }

        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item == null || item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(item);
    }

    public static boolean isPatternSchematicItem(Item item) {
        if (item == null) return false;

        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) return false;

        return PatternSchematicsIds.isPatternSchematicId(key.getNamespace(), key.getPath());
    }
}
