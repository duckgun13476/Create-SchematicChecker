package com.Pink_Cats.createschematicchecker.network;

import com.Pink_Cats.createschematicchecker.event.ServerTickTasks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AutoUpdate {

    @SubscribeEvent
    public static void serverTickEvent(TickEvent.ServerTickEvent evt) {
        if(evt.phase == TickEvent.Phase.END) return;
        ServerTickTasks.tick();
    }
}
