package com.Pink_Cats.createschematicchecker;

import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.echo.CscCommands;
import com.Pink_Cats.createschematicchecker.event.CheckBlueprint;
import com.pinkcats.torque.layer.forge.command.ForgeCommandEvents;
import com.simibubi.create.foundation.config.AllConfigs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Createschematicchecker.MODID)
public class Createschematicchecker {
    public static final String MODID = "createschematicchecker";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MC_VERSION = "1.16.5";

    private final CscLifecycle lifecycle = new CscLifecycle(new ForgeCreateConfigSource());

    public Createschematicchecker() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CheckBlueprint(new BlueCore()));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        lifecycle.commonSetup();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        lifecycle.serverStarting();
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        lifecycle.serverStopping();
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        ForgeCommandEvents.register(event, CscCommands::register);
    }

    private static final class ForgeCreateConfigSource implements CscLifecycle.CreateConfigSource {
        @Override
        public String cannonDelay() {
            return AllConfigs.SERVER.schematics.schematicannonDelay.get().toString();
        }

        @Override
        public String maxBeltLength() {
            return AllConfigs.SERVER.kinetics.maxBeltLength.get().toString();
        }

        @Override
        public String maxEjectorDistance() {
            return AllConfigs.SERVER.kinetics.maxEjectorDistance.get().toString();
        }

        @Override
        public String maxChassisRange() {
            return AllConfigs.SERVER.kinetics.maxChassisRange.get().toString();
        }

        @Override
        public boolean hasChainConveyorLength() {
            try {
                var field = AllConfigs.SERVER.kinetics.getClass().getDeclaredField("maxChainConveyorLength");
                field.setAccessible(true);
                return field.get(AllConfigs.SERVER.kinetics) != null;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
