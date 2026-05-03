package com.Pink_Cats.createschematicchecker;

import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.FancyConfig.ConfigArchiveNotice;
import com.Pink_Cats.createschematicchecker.echo.CscCommands;
import com.Pink_Cats.createschematicchecker.event.CheckBlueprint;
import com.mojang.logging.LogUtils;
import com.pinkcats.torque.layer.neoforge.command.NeoForgeCommandEvents;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

import java.lang.reflect.Field;

@Mod(Createschematicchecker.MODID)
public class Createschematicchecker {
    public static final String MODID = "createschematicchecker";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MC_VERSION = "1.21.1";
    public static final String CSC_VERSION = "0.21.18";

    private final CscLifecycle lifecycle = new CscLifecycle(new NeoForgeCreateConfigSource());

    public Createschematicchecker(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.register(new ModEventHandler());

        NeoForge.EVENT_BUS.register(new GameEventHandler());
        NeoForge.EVENT_BUS.register(new CheckBlueprint(new BlueCore()));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        lifecycle.commonSetup();
    }

    private class ModEventHandler {
        @SubscribeEvent
        public void onReload(ModConfigEvent.Reloading event) {
            lifecycle.configReloading();
        }
    }

    private class GameEventHandler {
        @SubscribeEvent
        public void onServerStarting(ServerStartingEvent event) {
            lifecycle.serverStarting();
        }

        @SubscribeEvent
        public void onServerStopping(ServerStoppingEvent event) {
            lifecycle.serverStopping();
        }

        @SubscribeEvent
        public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            ConfigArchiveNotice.tryNotifyPlayer(eventPlayer(event));
        }

        @SubscribeEvent
        public void onCommandRegister(RegisterCommandsEvent event) {
            NeoForgeCommandEvents.register(event, CscCommands::register);
        }
    }

    private static Object eventPlayer(Object event) {
        try {
            return event.getClass().getMethod("getEntity").invoke(event);
        } catch (Exception ignored) {
            try {
                return event.getClass().getMethod("getPlayer").invoke(event);
            } catch (Exception ignoredAgain) {
                return null;
            }
        }
    }

    private static final class NeoForgeCreateConfigSource implements CscLifecycle.CreateConfigSource {
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
