package com.Pink_Cats.createschematicchecker;
import com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister;
import com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CKinetics;
import com.simibubi.create.infrastructure.config.CSchematics;
import net.minecraft.ChatFormatting;
import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.event.CheckBlueprint;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

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
            MaxIndex = String.valueOf((NbtInterFace.StringToInt(ConfigRegister.MaxBelt)-1));
            MaxEject = CreateCKineticsConfig().maxEjectorDistance.get().toString();
            MaxChassisRange = CreateCKineticsConfig().maxChassisRange.get().toString();
        } catch(Exception ex){
            Message.FE(translateDirect("console.ConfigInjectCreateError"));
        }

    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        //CscConfigIO();
        CreateConfigInject();

        Message.FM("   _____  _____  _____ ");
        Message.FM("  / ____|/ ____|/ ____|");
        Message.FM(" | |    | (___ | |     "+"   "+ translateDirect("console.CscVersion"));
        Message.FM(" | |     \\___ \\| |     "+"   "+ translateDirect("console.McVersion"));
        Message.FM(" | |____ ____) | |____ "+"   "+ translateDirect( "console.feedback"));
        Message.FM("  \\_____|_____/ \\_____|");
        Message.FM("                       ");

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
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("csc")
                        .executes(context ->  {
                                    Player player = context.getSource().getPlayer();
                                    if (player != null) {
                                        Component message = Component.literal("Hello, welcome to use CSC for Create!")
                                                .setStyle(Style.EMPTY
                                                        .withColor(ChatFormatting.GREEN)
                                                        .withClickEvent(new ClickEvent(
                                                                ClickEvent.Action.OPEN_URL,
                                                                "https://mcg.tuanzi.ink/"
                                                        )));
                                        player.sendSystemMessage(message);
                                        Component url = Component.literal("Hello, welcome to use CSC for Create! ")
                                                .setStyle(Style.EMPTY
                                                        .withColor(ChatFormatting.GREEN)
                                                        .withClickEvent(new ClickEvent(
                                                                ClickEvent.Action.OPEN_URL,
                                                                "https://mcg.tuanzi.ink/"
                                                        )));
                                        player.sendSystemMessage(url);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                }
                        )

                        .then(Commands.literal("reload")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayer();
                                    try {
                                        CSC_RELOAD();
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }


                                    Component message = Component.literal(translateDirect("console.ReloadSuccess"))
                                            .setStyle(Style.EMPTY
                                                    .withColor(ChatFormatting.GOLD)
                                                    );
                                    player.sendSystemMessage(message);
                                    return Command.SINGLE_SUCCESS;
                                }
                        ))

        );

    }






}
