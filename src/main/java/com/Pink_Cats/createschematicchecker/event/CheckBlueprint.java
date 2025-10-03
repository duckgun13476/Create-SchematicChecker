package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.core.BlueCore;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.S_bool;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

public class CheckBlueprint {
    BlueCore Checker;

    public CheckBlueprint(BlueCore _filter) {
        Checker = _filter;

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


            if (table.isRemoved()) {
                return;
            }

            List<String> CheatLog = new ArrayList<>();

            long startTime = System.currentTimeMillis();
            Map<String,Object> CheckResult = Checker.SchematicBlueCore(PlayerBlueprintId,CheatLog);
            CheckCount++;
            boolean IsCheatSchematic = S_bool(CheckResult.get("Cheat"));
            boolean IsProblem = S_bool(CheckResult.get("Problem"));
            String player_id = player.getGameProfile().getName();

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


            if (!IsCheatSchematic) {
                if (IsProblem){
                    ProblemCount +=1;
                    Message.FW(translateDirect("console.problemOutput")+player_id+
                            translateDirect("console.problemOutput2")+PlayerBlueprintId);

                }

                table.inventory.setStackInSlot(1, SchematicItem.create(
                        world, PlayerBlueprintId, player_id));

            } else {
                CheatCount +=1;
                Message.FE(translateDirect("console.cheat.find"));
                Message.FE(translateDirect("console.CheatOutput")+player_id+
                        translateDirect("console.CheatOutput2")+PlayerBlueprintId);
                if (DebugCheatFind){
                    broadcast(PlayerBlueprintId,player_id);
                }
                table.inventory.setStackInSlot(0, AllItems.EMPTY_SCHEMATIC.asStack());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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





}
        //player.sendSystemMessage(Component.literal(""));