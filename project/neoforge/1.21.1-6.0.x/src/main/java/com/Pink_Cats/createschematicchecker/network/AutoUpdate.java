package com.Pink_Cats.createschematicchecker.network;

import com.Pink_Cats.createschematicchecker.event.ServerTickTasks;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class AutoUpdate {

    @SubscribeEvent
    public static void serverTickEvent(ServerTickEvent.Post evt) {
        ServerTickTasks.tick();
    }
}
