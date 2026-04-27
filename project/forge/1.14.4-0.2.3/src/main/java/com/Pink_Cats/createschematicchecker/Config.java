package com.Pink_Cats.createschematicchecker;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.CSC_RELOAD;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Createschematicchecker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    @SubscribeEvent
    static void onLoad(final ModConfig.ModConfigEvent event) {
        CSC_RELOAD();
    }
    @SubscribeEvent
    static void onReload(final ModConfig.ModConfigEvent event) {
        CSC_RELOAD();
    }



}
