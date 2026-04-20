package com.Pink_Cats.createschematicchecker;

import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.echo.CscCommands;
import com.Pink_Cats.createschematicchecker.event.CheckBlueprint;
import com.mojang.logging.LogUtils;
import com.pinkcats.torque.layer.forge.command.ForgeCommandEvents;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.lang.reflect.Field;

@Mod(Createschematicchecker.MODID)
public class Createschematicchecker {
    public static final String MODID = "createschematicchecker";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MC_VERSION = "1.18.2";
    public static final String CSC_VERSION = "0.21.18";

    private final CscLifecycle lifecycle = new CscLifecycle(new ForgeCreateConfigSource());

    public Createschematicchecker() {
        ModLoadingContext modLoadingContext = getModLoadingContextViaReflection();
        FMLJavaModLoadingContext modContext = modLoadingContext.extension();
        IEventBus modEventBus = modContext.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CheckBlueprint(new BlueCore()));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        lifecycle.commonSetup();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        lifecycle.serverStarting();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        lifecycle.serverStopping();
    }

    @SubscribeEvent
    public void onReload(ModConfigEvent.Reloading event) {
        lifecycle.configReloading();
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        ForgeCommandEvents.register(event, CscCommands::register);
    }

    @SuppressWarnings("unchecked")
    private static ModLoadingContext getModLoadingContextViaReflection() {
        try {
            Field contextField = ModLoadingContext.class.getDeclaredField("context");
            contextField.setAccessible(true);
            ThreadLocal<ModLoadingContext> contextThreadLocal = (ThreadLocal<ModLoadingContext>) contextField.get(null);
            return contextThreadLocal.get();
        } catch (Exception e) {
            throw new RuntimeException("CreateLazyTick got ERROR in Init:", e);
        }
    }

    private static final class ForgeCreateConfigSource implements CscLifecycle.CreateConfigSource {
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
