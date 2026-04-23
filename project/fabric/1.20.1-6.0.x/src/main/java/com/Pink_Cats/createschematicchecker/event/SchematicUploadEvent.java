package com.Pink_Cats.createschematicchecker.event;

import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class SchematicUploadEvent {
    public String SchematicPath;
    public ServerPlayer Player;
    public String SchematicId;

    public Level World;
    public SchematicTableBlockEntity Table;

    public SchematicUploadEvent(ServerPlayer player, String schematicPath, String schematicId, Level world, SchematicTableBlockEntity schematicTableBlock) {
        Player = player;
        SchematicId = schematicId;
        SchematicPath = schematicPath;
        World = world;
        Table = schematicTableBlock;
    }

    public SchematicUploadEvent(ServerPlayer player, String schematicPath, String schematicId) {
        Player = player;
        SchematicPath = schematicPath;
        SchematicId = schematicId;
        World = null;
        Table = null;
    }
}
