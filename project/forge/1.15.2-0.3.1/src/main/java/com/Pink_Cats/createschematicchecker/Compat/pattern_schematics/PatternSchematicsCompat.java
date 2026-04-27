package com.Pink_Cats.createschematicchecker.Compat.pattern_schematics;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * No hard dependency on create_pattern_schematics.
 * Everything is resolved via reflection only if the mod is present at runtime.
 */
public final class PatternSchematicsCompat {

    public static ItemStack getEmptyPatternSchematicStack() {
        ResourceLocation id = new ResourceLocation(
                PatternSchematicsIds.MOD_ID + ":" + PatternSchematicsIds.EMPTY_PATTERN_SCHEMATIC_PATH
        );

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
