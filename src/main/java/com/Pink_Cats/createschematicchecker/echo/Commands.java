package com.Pink_Cats.createschematicchecker.echo;

import com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.List;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.ID_modify_rule_manual;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.ID_modify_rule_online;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;
import static com.Pink_Cats.createschematicchecker.network.SimpleJsonParser.merge;

public class Commands {

    public static long executionTime;
    public static void RegisterCSCCommand(net.neoforged.neoforge.event.RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                net.minecraft.commands.Commands.literal("csc")
                        .executes(context ->  {
                                    Player player = context.getSource().getPlayer();
                                    if (player != null) {
                                        CSC_MAIN(player);

                                    }
                                    return Command.SINGLE_SUCCESS;
                                }
                        )
                        .requires(source ->source.hasPermission(4))
                        .then(net.minecraft.commands.Commands.literal("reload")
                                .executes(context -> {
                                            Player player = context.getSource().getPlayer();
                                            try {
                                                long startTime = System.currentTimeMillis();
                                                List<String> ReloadResult = CSC_RELOAD();
                                                long endTime = System.currentTimeMillis();
                                                executionTime = endTime - startTime;

                                                if (!ReloadResult.isEmpty()) {
                                                    if (player != null) {
                                                        player.sendSystemMessage(Component.literal(translateDirect("console.manual.config.error"))
                                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                                                    }
                                                    for (String s : ReloadResult) {
                                                        if (player != null) {
                                                            Message.FE("| " + s);
                                                            player.sendSystemMessage(Component.literal("| " + s)
                                                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                                                        }
                                                    }


                                                }else {
                                                    if (player != null) {
                                                        player.sendSystemMessage(Component.literal(translateDirect("console.ReloadSuccess")+executionTime + "ms")
                                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                                                    }
                                                }



                                            }catch (Exception ex){
                                                Message.FE(translateDirect("console.error")+ex.getMessage());
                                            }

                                    return Command.SINGLE_SUCCESS;
                                        }
                                ))

                        .then(net.minecraft.commands.Commands.literal("help")
                                .executes(context ->  {
                                            Player player = context.getSource().getPlayer();
                                            if (player != null) {
                                                CSC_HELP(player);
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        }
                                ))

                        .then(net.minecraft.commands.Commands.literal("list")
                                .executes(context -> {
                                    Player player = context.getSource().getPlayer();
                                    if (player != null) {
                                        player.sendSystemMessage(Component.literal(translateDirect("console.csc.run.command"))
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
                                        player.sendSystemMessage(Component.literal(translateDirect("console.csc.list"))
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                                        player.sendSystemMessage(Component.literal(translateDirect("console.csc.list2"))
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                                        player.sendSystemMessage(Component.literal(translateDirect("console.csc.list3"))
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                                        player.sendSystemMessage(Component.literal(translateDirect("console.csc.list4"))
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                                        player.sendSystemMessage(Component.literal(translateDirect("console.csc.list5"))
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                                        player.sendSystemMessage(Component.literal(translateDirect("console.csc.list6"))
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                                        player.sendSystemMessage(Component.literal(translateDirect("console.csc.liner"))
                                                .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })

                                .then(net.minecraft.commands.Commands.literal("IdMatchRuleAll")
                                        .executes(context -> {
                                                    Player player = context.getSource().getPlayer();
                                            if (player != null) {
                                                player.sendSystemMessage(Component.literal(translateDirect("console.paraList3"))
                                                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                                            }
                                                    return OutPutPara(player, ID_match_rule);
                                                }
                                        )
                                )
                                .then(net.minecraft.commands.Commands.literal("OperateMatchRuleAll")
                                        .executes(context -> {
                                                    Player player = context.getSource().getPlayer();
                                            if (player != null) {
                                                player.sendSystemMessage(Component.literal(translateDirect("console.paraList4"))
                                                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                                            }

                                                    return OutPutPara(player, ConfigRegister.Operate_match_rule);
                                                }
                                        )
                                )
                                .then(net.minecraft.commands.Commands.literal("OperateMatchRule")
                                        .executes(context -> {
                                                    Player player = context.getSource().getPlayer();
                                            if (player != null) {
                                                player.sendSystemMessage(Component.literal(translateDirect("console.paraList1"))
                                                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                                            }
                                                    return MergeRuleOutput(player, ConfigRegister.Operate_modify_rule_online, ConfigRegister.Operate_modify_rule_manual);
                                                }
                                        )
                                )
                                .then(net.minecraft.commands.Commands.literal("IdMatchRule")
                                        .executes(context -> {
                                                    Player player = context.getSource().getPlayer();
                                            if (player != null) {
                                                player.sendSystemMessage(Component.literal(translateDirect("console.paraList2"))
                                                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                                            }
                                                    return MergeRuleOutput(player, ID_modify_rule_manual, ID_modify_rule_online);
                                                }
                                        )
                                )




                        )





        );



    }

    private static int MergeRuleOutput(Player player, String[][] idModifyRuleManual, String[][] idModifyRuleOnline) {
        try {
            for (String[] id_rule : merge(idModifyRuleManual, idModifyRuleOnline)) {
                Component message = Component.literal(Arrays.toString(id_rule))
                        .setStyle(Style.EMPTY
                                .withColor(ChatFormatting.GREEN)
                        );
                if (player != null) {
                    player.sendSystemMessage(message);
                }

            }
        }catch (Exception ex){
            Message.FE(translateDirect("console.error")+ex.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int OutPutPara(Player player, String[][] idMatchRule) {


        try {
            for (String[] id_rule : idMatchRule) {
                Component message = Component.literal(Arrays.toString(id_rule))
                        .setStyle(Style.EMPTY
                                .withColor(ChatFormatting.GREEN)
                        );
                if (player != null) {
                    player.sendSystemMessage(message);
                }

            }
        }catch (Exception ex){
            Message.FE(translateDirect("console.error")+ex.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }


    public static void CSC_HELP(Player player){
        player.sendSystemMessage(Component.literal(translateDirect("console.csc.run.command"))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        player.sendSystemMessage(Component.literal(translateDirect("console.csc.welcome1"))
                .setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.OPEN_URL,
                                "https://github.com/duckgun13476/Create-SchematicChecker/issues"
                        ))
                        .withColor(ChatFormatting.LIGHT_PURPLE)));
        player.sendSystemMessage(Component.literal(translateDirect("console.csc.welcome2"))
                .setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.OPEN_URL,
                                "https://qm.qq.com/q/9N0m84sQfY"
                        ))
                        .withColor(ChatFormatting.LIGHT_PURPLE)));
        player.sendSystemMessage(Component.literal(translateDirect("console.csc.welcome3"))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
        player.sendSystemMessage(Component.literal(translateDirect("console.csc.welcome4"))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
        player.sendSystemMessage(Component.literal(translateDirect("console.csc.welcome5"))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
        player.sendSystemMessage(Component.literal(translateDirect("console.csc.liner"))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
    }


    public static void CSC_MAIN(Player player){

        player.sendSystemMessage(Component.literal(translateDirect("console.csc.run.command"))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));

        player.sendSystemMessage(Component.literal(translateDirect("console.csc.welcome"))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));

        player.sendSystemMessage(Component.literal(translateDirect("console.csc.welcome.help"))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));




        int seconds = Math.toIntExact(GuardTime % 60);         // 剩余秒数
        int totalMinutes = Math.toIntExact(GuardTime / 60);    // 总分钟数
        int minutes = totalMinutes % 60;         // 剩余分钟数
        int totalHours = totalMinutes / 60;      // 总小时数
        int hours = totalHours % 24;             // 剩余小时数
        int days = totalHours / 24;              // 天数

        player.sendSystemMessage(Component.literal(
                translateDirect("console.csc.board.guard")+days+
                        translateDirect( "console.csc.board.day")+hours+
                        translateDirect( "console.csc.board.hour")+minutes+
                        translateDirect("console.csc.board.minute")+seconds+
                        translateDirect("console.csc.board.second")
                )
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));



        player.sendSystemMessage(Component.literal(
                translateDirect("console.csc.board.checkCount")+CheckCount+
                        translateDirect( "console.csc.board.checkCount2")
                )
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));

        player.sendSystemMessage(Component.literal(
                translateDirect("console.csc.board.checkCount3")+ProblemCount+
                        translateDirect( "console.csc.board.checkCount4")
                )
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
        player.sendSystemMessage(Component.literal(
                translateDirect("console.csc.board.checkCount5")+CheatCount+
                translateDirect("console.csc.board.checkCount6")
                )
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));



        player.sendSystemMessage(Component.literal(translateDirect("console.csc.liner"))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
    }


}
