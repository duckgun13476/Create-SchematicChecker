package com.simibubi.create.content.schematics;

import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class ServerSchematicLoader {
    private final Map<String, SchematicUploadEntry> activeUploads = new HashMap<>();

    public static SchematicTableBlockEntity getTable(Level world, BlockPos pos) {
        return null;
    }

    public void handleFinishedUpload(ServerPlayer player, String schematic) {
    }

    public static class SchematicUploadEntry {
    }
}
