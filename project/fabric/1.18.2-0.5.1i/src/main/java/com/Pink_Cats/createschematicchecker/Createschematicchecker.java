package com.Pink_Cats.createschematicchecker;

import com.Pink_Cats.createschematicchecker.FancyConfig.ConfigArchiveNotice;
import com.Pink_Cats.createschematicchecker.FancyConfig.WhitelistModeNotice;
import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.echo.CscCommands;
import com.Pink_Cats.createschematicchecker.event.CheckBlueprint;
import com.Pink_Cats.createschematicchecker.event.SchematicUploadEvent;
import com.mojang.logging.LogUtils;
import com.pinkcats.torque.layer.fabric.command.FabricCommandEvents;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;

import java.lang.reflect.Field;

public class Createschematicchecker implements ModInitializer {
    public static final String MODID = "createschematicchecker";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MC_VERSION = "1.18.2";
    public static final String CSC_VERSION = "0.21.18";

    private static final CheckBlueprint CHECK_BLUEPRINT = new CheckBlueprint(new BlueCore());

    private final CscLifecycle lifecycle = new CscLifecycle(new FabricCreateConfigSource());

    @Override
    public void onInitialize() {
        lifecycle.commonSetup();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            CheckBlueprint.setCurrentServer(server);
            lifecycle.serverStarting();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> lifecycle.serverStopping());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> CheckBlueprint.clearCurrentServer());
        ServerTickEvents.END_SERVER_TICK.register(CHECK_BLUEPRINT::serverTickEvent);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ConfigArchiveNotice.tryNotifyPlayer(handler.getPlayer());
            WhitelistModeNotice.tryNotifyPlayer(handler.getPlayer());
        });

        FabricCommandEvents.register(CscCommands::register);
    }

    public static void dispatchSchematicUpload(SchematicUploadEvent event) {
        CHECK_BLUEPRINT.onSchematicUpload(event);
    }

    private static final class FabricCreateConfigSource implements CscLifecycle.CreateConfigSource {
        @Override
        public String cannonDelay() {
            return AllConfigs.server().schematics.schematicannonDelay.get().toString();
        }

        @Override
        public String maxBeltLength() {
            return AllConfigs.server().kinetics.maxBeltLength.get().toString();
        }

        @Override
        public String maxEjectorDistance() {
            return AllConfigs.server().kinetics.maxEjectorDistance.get().toString();
        }

        @Override
        public String maxChassisRange() {
            return AllConfigs.server().kinetics.maxChassisRange.get().toString();
        }

        @Override
        public boolean hasChainConveyorLength() {
            try {
                Field field = AllConfigs.server().kinetics.getClass().getDeclaredField("maxChainConveyorLength");
                field.setAccessible(true);
                return field.get(AllConfigs.server().kinetics) != null;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
