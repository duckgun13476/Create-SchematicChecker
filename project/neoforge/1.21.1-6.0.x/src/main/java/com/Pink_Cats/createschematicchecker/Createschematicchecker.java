package com.Pink_Cats.createschematicchecker;
import com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CKinetics;
import com.simibubi.create.infrastructure.config.CSchematics;
import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.event.CheckBlueprint;
import com.mojang.logging.LogUtils;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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
    public static final String MC_VERSION = "1.21.1";
    public static final String CSC_VERSION = "0.21.18";


    public Createschematicchecker(IEventBus modEventBus, ModContainer modContainer) {

        // 添加监听器
        modEventBus.addListener(this::commonSetup);
        modEventBus.register(new ModEventHandler(this));

        NeoForge.EVENT_BUS.register(new GameEventHandler(this));


        BlueCore shifter = new BlueCore();   //创建一个新的过滤器
        NeoForge.EVENT_BUS.register(new CheckBlueprint(shifter));//注册蓝图上传事件的监听器


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





    private class ModEventHandler {
        private final Createschematicchecker mod;

        public ModEventHandler(Createschematicchecker mod) {
            this.mod = mod;
        }


        @SubscribeEvent
        public void onReload(ModConfigEvent.Reloading event) {
            try {
                CSC_RELOAD();
            } catch (Exception ex) {
                Message.FE(translateDirect("console.ConfigReloadError"));
            }
        }

        public Createschematicchecker getMod() {
            return mod;
        }
    }


    private class GameEventHandler {
        private final Createschematicchecker mod;

        public GameEventHandler(Createschematicchecker mod) {
            this.mod = mod;
        }

        // All normal event
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
        public void onCommandRegister(RegisterCommandsEvent event) {
            RegisterCSCCommand(event);
        }
    }




}
