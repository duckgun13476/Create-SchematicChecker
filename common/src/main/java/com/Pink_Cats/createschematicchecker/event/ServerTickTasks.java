package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.network.OnlineTasks;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.GuardTime;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.enable_auto_config_update;

public class ServerTickTasks {

    private static int tick = 0;
    private static int updateTick = 0;
    private static final int updatePeriod = 3600;

    public static void tick() {
        TempOffTicker.tick();

        tick++;
        if (tick > 20) {
            tick = 0;

            GuardTime++;

            updateTick++;
            if (updateTick > updatePeriod) {
                updateTick = 0;
                if (enable_auto_config_update) {
                    OnlineTasks.readDataAsync();
                }
            }
        }
    }
}
