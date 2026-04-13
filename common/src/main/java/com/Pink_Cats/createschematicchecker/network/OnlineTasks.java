package com.Pink_Cats.createschematicchecker.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.enable_auto_config_update;
import static com.Pink_Cats.createschematicchecker.online.NbtFileUploader.AutoUpdateThread;
import static com.Pink_Cats.createschematicchecker.online.SimpleHeartbeatPusher.HeartBeatTask;
import static com.Pink_Cats.createschematicchecker.online.VersionChecker.UpdateMainThread;

public class OnlineTasks {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void readDataAsync() {
        executor.submit(() -> {
            UpdateMainThread();
            HeartBeatTask();
        });
    }

    public static void reportProblem(String filepath) {
        executor.submit(() -> AutoUpdateThread(filepath));
    }

    public static void postDataAsync(String filepath) {
        if (enable_auto_config_update) {
            executor.submit(() -> AutoUpdateThread(filepath));
        }
    }
}
