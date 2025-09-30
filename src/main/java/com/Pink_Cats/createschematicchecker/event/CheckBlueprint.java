package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.Pink_Cats.createschematicchecker.core.NbtInterFace.S_bool;

public class CheckBlueprint {
    BlueCore Checker;

    public CheckBlueprint(BlueCore _filter) {
        Checker = _filter;
        MinecraftForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void OnSchematicUpload(SchematicUploadEvent event) {
        new Thread(() -> handleBlueprintUpload(event)).start();
    }

    private void handleBlueprintUpload(SchematicUploadEvent event) {
        try {
            // simulate delay
            //Thread.sleep(4000);


            SchematicTableBlockEntity table = event.Table;
            Level world = event.World;
            ServerPlayer player = event.Player;
            String PlayerBlueprintId = event.SchematicPath;


            if (table.isRemoved()) {
                return;
            }

            long startTime = System.currentTimeMillis();
            boolean IsCheatSchematic = S_bool(Checker.SchematicBlueCore(PlayerBlueprintId).get("Cheat"));
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            Message.FE(player.getGameProfile().getName());

            Message.FE(PlayerBlueprintId);
            PlayerBlueprintId = removeFirstPathComponent(PlayerBlueprintId);


            Message.FE(PlayerBlueprintId);

            Message.FP("Total use time: " + executionTime + " ms");


            if (!IsCheatSchematic) {

                table.inventory.setStackInSlot(1, SchematicItem.create(
                        world.holderLookup(Registries.BLOCK), PlayerBlueprintId, player.getGameProfile().getName()));

            } else {
                Message.FE("It's cheat!");
                table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String removeFirstPathComponent(String path) {
        int firstSlashIndex = path.indexOf("/");
        if (firstSlashIndex != -1) {
            return path.substring(firstSlashIndex + 1);
        }
        return path;
    }





}
        //player.sendSystemMessage(Component.literal(""));