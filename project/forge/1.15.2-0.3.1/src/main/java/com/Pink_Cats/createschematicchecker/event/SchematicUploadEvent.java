package com.Pink_Cats.createschematicchecker.event;

import com.simibubi.create.content.schematics.block.SchematicTableTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

public class SchematicUploadEvent extends Event {
    public String SchematicPath;
    public ServerPlayerEntity Player;
    public String SchematicId;

    public World World;
    public SchematicTableTileEntity Table;

    public SchematicUploadEvent(ServerPlayerEntity player, String schematicPath, String schematicId, World world,
                                SchematicTableTileEntity schematicTableBlock) {
        Player = player;
        SchematicId = schematicId;
        SchematicPath = schematicPath;
        World = world;
        Table = schematicTableBlock;
    }

    public SchematicUploadEvent(ServerPlayerEntity player, String schematicPath, String schematicId) {
        Player = player;
        SchematicPath = schematicPath;
        SchematicId = schematicId;
        World = null;
        Table = null;
    }
}
