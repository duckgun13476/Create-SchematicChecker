package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.core.BlueCore;
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
            Thread.sleep(4000);


            SchematicTableBlockEntity table = event.Table;
            Level world = event.World;
            ServerPlayer player = event.Player;
            String PlayerBlueprintId = event.SchematicPath;


            if (table.isRemoved()) {
                return;
            }

            if (S_bool(Checker.SchematicBlueCore(PlayerBlueprintId).get("is_valid"))) {
                table.inventory.setStackInSlot(1, SchematicItem.create(
                        world.holderLookup(Registries.BLOCK), PlayerBlueprintId, player.getGameProfile().getName()));
            } else {
                table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 处理线程中断
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
        //player.sendSystemMessage(Component.literal(""));