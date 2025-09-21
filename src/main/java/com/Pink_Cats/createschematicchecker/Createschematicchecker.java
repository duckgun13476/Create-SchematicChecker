package com.Pink_Cats.createschematicchecker;
import net.minecraft.client.Minecraft;
import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.event.CheckBlueprint;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import static com.Pink_Cats.createschematicchecker.CSCLanguage.translateDirect;
import static com.Pink_Cats.createschematicchecker.ConfigCleaner.cleanConfigFile;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Createschematicchecker.MODID)
public class Createschematicchecker {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "createschematicchecker";
    public static final Logger LOGGER = LogUtils.getLogger();


    public Createschematicchecker() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // 添加监听器
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        BlueCore shifter = new BlueCore();   //创建一个新的过滤器

        MinecraftForge.EVENT_BUS.register(new CheckBlueprint(shifter));//注册蓝图上传事件的监听器




    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        Message.FM(translateDirect("console.LoadingConfig"));
        cleanConfigFile();  //replace lang

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        //CscConfigIO();
        Message.FM("   _____  _____  _____ ");
        Message.FM("  / ____|/ ____|/ ____|");
        Message.FM(" | |    | (___ | |     "+"   "+ "CSC version: 0.21");
        Message.FM(" | |     \\___ \\| |     "+"   "+ "Minecraft version: 1.20.1");
        Message.FM(" | |____ ____) | |____ "+"   "+ translateDirect( "console.feedback"));
        Message.FM("  \\_____|_____/ \\_____|");
        Message.FM("                       ");

    }

}
