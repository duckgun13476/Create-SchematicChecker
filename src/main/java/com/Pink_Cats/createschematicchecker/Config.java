package com.Pink_Cats.createschematicchecker;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.CSC_RELOAD;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@EventBusSubscriber(modid = Createschematicchecker.MODID)
public class Config {

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        CSC_RELOAD();
    }
    @SubscribeEvent
    static void onReload(final ModConfigEvent event) {
        CSC_RELOAD();
    }



}
