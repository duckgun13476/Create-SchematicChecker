package com.Pink_Cats.createschematicchecker;
import com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister;
import com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CKinetics;
import com.simibubi.create.infrastructure.config.CSchematics;
import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.event.CheckBlueprint;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

import static com.Pink_Cats.createschematicchecker.core.attach.Math.StringToInt;
import static com.Pink_Cats.createschematicchecker.database.SingleLog.CSC_MES;
import static com.Pink_Cats.createschematicchecker.database.SingleLog.CSC_WARN;
import static com.Pink_Cats.createschematicchecker.echo.Commands.RegisterCSCCommand;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;

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
        List<String> log = List.of();
        CSC_Variables_Load();
        CSC_INIT(log);
    }

    public CSchematics CreateSchematicConfig() {
        return AllConfigs.server().schematics;
    }

    public CKinetics CreateCKineticsConfig() {
        return AllConfigs.server().kinetics;
    }


    public void CreateConfigInject(){
        try{
            CannonDelay = CreateSchematicConfig().schematicannonDelay.get().toString();
            MaxBelt = CreateCKineticsConfig().maxBeltLength.get().toString();
            MaxIndex = String.valueOf((StringToInt(ConfigRegister.MaxBelt)-1));
            MaxEject = CreateCKineticsConfig().maxEjectorDistance.get().toString();
            MaxChassisRange = CreateCKineticsConfig().maxChassisRange.get().toString();

            try {
                // 获取配置对象
                Object config = CreateCKineticsConfig();
                Class<?> ckClass = config.getClass();
                Field field = ckClass.getDeclaredField("maxChainConveyorLength");
                field.setAccessible(true);
                Object fieldValue = field.get(config);
                CreateVersion = "6.0";
            } catch (Exception e) {
                CreateVersion = "0.5";
            }



        } catch(Exception ex){
            Message.FE(translateDirect("console.ConfigInjectCreateError"));
        }

    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        //CscConfigIO();
        CreateConfigInject();
        CSC_MES.reopen();
        CSC_WARN.reopen();
        Message.FM("   _____  _____  _____ ");
        Message.FM("  / ____|/ ____|/ ____|");
        Message.FM(" | |    | (___ | |     "+"   "+ translateDirect("console.CscVersion"));
        Message.FM(" | |     \\___ \\| |     "+"   "+ translateDirect("console.McVersion"));
        Message.FM(" | |____ ____) | |____ "+"   "+ translateDirect( "console.feedback"));
        Message.FM("  \\_____|_____/ \\_____|");
        Message.FM("                       ");

    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {

        CSC_Variables_Save();
        Message.FM(translateDirect("console.csc.StopServer"));
        CSC_MES.close();
        CSC_WARN.close();

    }





    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        try {
            CSC_RELOAD();
        }catch (Exception ex){
            Message.FE(translateDirect("console.ConfigReloadError"));
        }
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        RegisterCSCCommand(event);

    }



}
