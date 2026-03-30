package com.Pink_Cats.createschematicchecker.network;

import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.GuardTime;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.enable_auto_config_update;
import static com.Pink_Cats.createschematicchecker.online.NbtFileUploader.AutoUpdateThread;
import static com.Pink_Cats.createschematicchecker.online.SimpleHeartbeatPusher.HeartBeatTask;
import static com.Pink_Cats.createschematicchecker.online.VersionChecker.UpdateMainThread;

@EventBusSubscriber
public class AutoUpdate {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static int tick = 0;
    private static int tick_1=0;
    private static final int update_period = 3600;

    @SubscribeEvent
    public static void serverTickEvent(ServerTickEvent.Post evt) {

        // auto save
        tick ++;
        if (tick>20) {
            tick = 0;

            GuardTime ++;


            tick_1 ++;
            if (tick_1>update_period) {
                tick_1 = 0;
                if(enable_auto_config_update){
                    readDataAsync(evt.getServer());
                }
            }



        }
    }


    public static void readDataAsync(MinecraftServer Server) {

        executor.submit(() -> {
            UpdateMainThread();
            HeartBeatTask();
        });
    }

    public static void ReportProblem(String filepath) {
        executor.submit(() -> {

            AutoUpdateThread(filepath);
        });
    }


    public static void PostDataAsync(String filepath) {
        if (enable_auto_config_update)
        {
            executor.submit(() -> {

                AutoUpdateThread(filepath);
            });
        }
    }


    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {

    }





}
