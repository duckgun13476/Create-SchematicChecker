package com.Pink_Cats.createschematicchecker.Compat.pattern_schematics;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * No hard dependency on create_pattern_schematics.
 * Everything is resolved via reflection only if the mod is present at runtime.
 */
public final class PatternSchematicsCompat {

    public static ItemStack getEmptyPatternSchematicStack() {
        var id = ResourceLocation.fromNamespaceAndPath(
                PatternSchematicsIds.MOD_ID,
                PatternSchematicsIds.EMPTY_PATTERN_SCHEMATIC_PATH
        );

        var item = BuiltInRegistries.ITEM.get(id);

        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(item);
    }

    public static boolean isPatternSchematicItem(Item item) {
        if (item == null) return false;

        var key = BuiltInRegistries.ITEM.getKey(item);

        return PatternSchematicsIds.isPatternSchematicId(key.getNamespace(), key.getPath());
    }
}
