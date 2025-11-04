package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.S_bool;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.getCurrentDateTime;
import static com.Pink_Cats.createschematicchecker.event.TempOffEvent.CheckSchematic;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

public class CheckBlueprint {
    BlueCore Checker;

    public CheckBlueprint(BlueCore _filter) {
        Checker = _filter;
        MinecraftForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void OnSchematicUpload(SchematicUploadEvent event) {
        new Thread(() -> handleBlueprintUpload(event)).start();
    }

    private void handleBlueprintUpload(SchematicUploadEvent event) {
        try {
            // simulate delay
            //Thread.sleep(4000);


            SchematicTableBlockEntity table = event.Table;
            Level world = event.World;
            ServerPlayer player = event.Player;
            String PlayerBlueprintId = event.SchematicPath;
            String player_id = player.getGameProfile().getName();


            if (table.isRemoved()) {
                return;
            }


            if (!CheckSchematic)
            {
                Message.FE(translateDirect("console.stop.csc.temp.output"));
                String DefaultPath = "./schematics/uploaded/";
                //String DefaultPath = System.getProperty("user.dir");
                String path = DefaultPath + PlayerBlueprintId;
                CompoundTag nbt_data = Path_to_CompoundTag(path);
                String User = PlayerBlueprintId.split("/")[0];
                String Blueprint = PlayerBlueprintId.split("/")[1];
                if (enable_backup)
                {
                    Checker.CompoundTag_to_Path(nbt_data, "config/CSC/backup/"+User+"/",getCurrentDateTime()+Blueprint);
                }
                Checker.CompoundTag_to_Path(nbt_data, DefaultPath+User+"/",Blueprint);

                table.inventory.setStackInSlot(1, SchematicItem.create(
                        PlayerBlueprintId, player.getGameProfile()
                        .getName()));
                return;
            }



            List<String> CheatLog = new ArrayList<>();

            long startTime = System.currentTimeMillis();



            Map<String,Object> CheckResult = Checker.SchematicBlueCore(PlayerBlueprintId,CheatLog);
            if (CheckResult == null) {
                table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());
                return;
            }




            CheckCount++;
            boolean IsCheatSchematic = S_bool(CheckResult.get("Cheat"));
            boolean IsProblem = S_bool(CheckResult.get("Problem"));
            boolean CannotCheck = S_bool(CheckResult.get("CannotCheck"));

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            //Message.FE(player.getGameProfile().getName());

            //Message.FE(PlayerBlueprintId);
            PlayerBlueprintId = removeFirstPathComponent(PlayerBlueprintId);
            if (!CheatLog.isEmpty()){
                for (String s : CheatLog) {
                    Message.FE(s);
                }
            }
            //Message.FE(PlayerBlueprintId);
            Message.FP(translateDirect("console.total.time") + executionTime + " ms");

            if (CannotCheck) {
                Message.FE(translateDirect("console.csc.cannotCheck"));
                table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());
                return;
            }



            if (!IsCheatSchematic) {
                if (IsProblem){
                    ProblemCount +=1;
                    Message.FW(translateDirect("console.problemOutput")+player_id+
                            translateDirect("console.problemOutput2")+PlayerBlueprintId);

                }

                table.inventory.setStackInSlot(1, SchematicItem.create(
                        PlayerBlueprintId, player.getGameProfile()
                                .getName()));
            } else {
                CheatCount +=1;
                Message.FE(translateDirect("console.cheat.find"));
                Message.FE(translateDirect("console.CheatOutput")+player_id+
                        translateDirect("console.CheatOutput2")+PlayerBlueprintId);
                if (DebugCheatFind){
                    broadcast(PlayerBlueprintId,player_id);
                }
                if (CheckRunCommand){
                    ExecuteSomeCmd(PlayerBlueprintId,player_id);
                }

                table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());
            }

        } catch (Exception e) {
            if (enable_debug)
                e.printStackTrace();
        }
    }

    public CompoundTag Path_to_CompoundTag(String SchematicPath) throws IOException {
        File BluePrint = new File(SchematicPath);
        FileInputStream file_stream = new FileInputStream(BluePrint);
        return NbtIo.readCompressed(file_stream);
    }

    public String removeFirstPathComponent(String path) {
        int firstSlashIndex = path.indexOf("/");
        if (firstSlashIndex != -1) {
            return path.substring(firstSlashIndex + 1);
        }
        return path;
    }


    public static List<ServerPlayer> getAllOnlinePlayers() {
        // 1. 获取服务器实例（ Forge 提供的工具类）
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            // 服务器未启动时返回空列表
            return new ArrayList<>();
        }

        // 2. 获取所有在线玩家（返回的是不可修改列表，建议转为新列表）
        return new ArrayList<>(server.getPlayerList().getPlayers());
    }

    public static void broadcast(String PlayerBlueprintId, String PlayerName) {

        for (final Player player : getAllOnlinePlayers())
        {
            player.sendSystemMessage(
                    Component.literal(
                            translateDirect("console.CheatOutput")+PlayerName+
                            translateDirect("console.CheatOutput2")+PlayerBlueprintId

                    )
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        }

    }
    public static void ExecuteSomeCmd(String PlayerBlueprintId, String PlayerName) {
        String command = "";
    }




}
        //player.sendSystemMessage(Component.literal(""));