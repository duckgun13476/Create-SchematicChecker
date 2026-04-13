package com.Pink_Cats.createschematicchecker.network;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.GuardTime;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.enable_auto_config_update;

@Mod.EventBusSubscriber
public class AutoUpdate {

    private static int tick = 0;
    private static int tick_1=0;
    private static final int update_period = 3600;

    @SubscribeEvent
    public static void serverTickEvent(TickEvent.ServerTickEvent evt) {
        if(evt.phase == TickEvent.Phase.END) return;
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
        OnlineTasks.readDataAsync();
    }

    public static void ReportProblem(String filepath) {
        OnlineTasks.reportProblem(filepath);
    }


    public static void PostDataAsync(String filepath) {
        OnlineTasks.postDataAsync(filepath);
    }


    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {

    }





}
