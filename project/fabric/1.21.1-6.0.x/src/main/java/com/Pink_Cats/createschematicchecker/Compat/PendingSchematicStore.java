package com.Pink_Cats.createschematicchecker.Compat;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.concurrent.ConcurrentHashMap;

public class PendingSchematicStore {

    public static class PendingMeta {
        public final ResourceKey<Level> dim;
        public final BlockPos pos;

        public PendingMeta(ResourceKey<Level> dim, BlockPos pos) {
            this.dim = dim;
            this.pos = pos;
        }
    }

    private static final ConcurrentHashMap<String, ItemStack> PENDING_STACK = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, PendingMeta> PENDING_META = new ConcurrentHashMap<>();

    public static void put(String id, ItemStack stack, ResourceKey<Level> dim, BlockPos pos) {
        if (stack != null) PENDING_STACK.put(id, stack);
        if (dim != null && pos != null) PENDING_META.put(id, new PendingMeta(dim, pos));
    }

    public static ItemStack takeStack(String id) {
        return PENDING_STACK.remove(id);
    }

    public static PendingMeta takeMeta(String id) {
        return PENDING_META.remove(id);
    }

    public static void drop(String id) {
        PENDING_STACK.remove(id);
        PENDING_META.remove(id);
    }
}
