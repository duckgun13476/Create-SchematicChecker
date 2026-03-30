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

    private static final String EMPTY_PATTERN_SCHEMATIC_ID = "create_pattern_schematics:empty_pattern_schematic";
    private static final String PATTERN_SCHEMATIC_NAMESPACE = "create_pattern_schematics";
    private static final String PATTERN_SCHEMATIC_PATH = "pattern_schematic";

    private PatternSchematicsCompat() {
    }

    public static ItemStack getEmptyPatternSchematicStack() {
        ResourceLocation id = ResourceLocation.tryParse(EMPTY_PATTERN_SCHEMATIC_ID);
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

        return PATTERN_SCHEMATIC_NAMESPACE.equals(key.getNamespace())
                && PATTERN_SCHEMATIC_PATH.equals(key.getPath());
    }
}
