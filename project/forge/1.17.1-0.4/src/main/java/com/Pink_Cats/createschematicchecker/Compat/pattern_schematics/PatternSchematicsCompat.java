package com.Pink_Cats.createschematicchecker.Compat.pattern_schematics;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * No hard dependency on create_pattern_schematics.
 * Everything is resolved via reflection only if the mod is present at runtime.
 */
public final class PatternSchematicsCompat {

    public static ItemStack getEmptyPatternSchematicStack() {
        var id = new ResourceLocation(
                PatternSchematicsIds.MOD_ID + ":" + PatternSchematicsIds.EMPTY_PATTERN_SCHEMATIC_PATH
        );

        var item = ForgeRegistries.ITEMS.getValue(id);

        if (item == null || item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(item);
    }

    public static boolean isPatternSchematicItem(Item item) {
        if (item == null) return false;

        var key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) return false;

        return PatternSchematicsIds.isPatternSchematicId(key.getNamespace(), key.getPath());
    }
}
