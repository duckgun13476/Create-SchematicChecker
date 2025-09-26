package com.Pink_Cats.createschematicchecker.event;

import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

public class SchematicUploadEvent extends Event {//蓝图上传事件
    public String SchematicPath;
    public ServerPlayer Player;
    public String SchematicId;

    public Level World;
    public SchematicTableBlockEntity Table;


    public SchematicUploadEvent(ServerPlayer Player_P, String SchematicPath_P, String SchematicId_P, Level World_P, SchematicTableBlockEntity SchematicTableBlock) {//构造函数
        Player = Player_P;
        SchematicId = SchematicId_P;
        SchematicPath = SchematicPath_P;

        World = World_P;
        Table = SchematicTableBlock;

    }
}



