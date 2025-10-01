package com.Pink_Cats.createschematicchecker.network;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.enable_auto_config_update;
import static com.Pink_Cats.createschematicchecker.network.VersionChecker.UpdateMainThread;

@Mod.EventBusSubscriber
public class AutoUpdate {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static int tick = 0;
    private static final int update_period = 15;

    @SubscribeEvent
    public static void serverTickEvent(TickEvent.ServerTickEvent evt) {
        if(evt.phase == TickEvent.Phase.END) return;
        // auto save
        tick ++;
        if (tick>20*update_period) {
            tick = 0;
            if(enable_auto_config_update){
                readDataAsync(evt.getServer());
            }


        }
    }


    public static void readDataAsync(MinecraftServer Server) {

        executor.submit(() -> {

            UpdateMainThread();
        });
    }


    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {

    }





}
