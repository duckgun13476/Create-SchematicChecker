package com.Pink_Cats.createschematicchecker.echo;

import com.pinkcats.torque.layer.net.minecraft.commands.CommandNode;
import com.pinkcats.torque.layer.net.minecraft.commands.CommandRegistrar;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;

public class CscCommands {

    public static void register(CommandRegistrar registrar) {
        CommandNode root = registrar.literal("csc")
                .executes(CscCommandActions::main)
                .requiresPermission(4)
                .then(registrar.literal("reload").executes(CscCommandActions::reload))
                .then(registrar.literal("help").executes(CscCommandActions::help))
                .then(registrar.literal("DisableTemp").executes(CscCommandActions::disable))
                .then(registrar.literal("notice")
                        .then(registrar.literal("whitelistid")
                                .then(registrar.literal("on").executes(CscCommandActions::enableWhitelistModeNotice))
                                .then(registrar.literal("off").executes(CscCommandActions::disableWhitelistModeNotice))))
                .then(registrar.literal("mode")
                        .then(registrar.literal("whitelistid")
                                .then(registrar.literal("on").executes(CscCommandActions::enableWhitelistModMode))
                                .then(registrar.literal("off").executes(CscCommandActions::disableWhitelistModMode))))
                .then(registrar.literal("Enable").executes(CscCommandActions::enable))
                .then(registrar.literal("list")
                        .executes(CscCommandActions::list)
                        .then(registrar.literal("IdMatchRuleAll")
                                .executes(sender -> CscCommandActions.outputPara(sender, ID_match_rule)))
                        .then(registrar.literal("OperateMatchRuleAll")
                                .executes(sender -> CscCommandActions.outputPara(sender, Operate_match_rule)))
                        .then(registrar.literal("OperateMatchRule")
                                .executes(sender -> CscCommandActions.mergeRuleOutput(sender, Operate_modify_rule_online, Operate_modify_rule_manual)))
                        .then(registrar.literal("IdMatchRule")
                                .executes(sender -> CscCommandActions.mergeRuleOutput(sender, ID_modify_rule_manual, ID_modify_rule_online))))
                .then(registrar.literal("online")
                        .then(registrar.literal("test")
                                .executes(sender -> CscCommandActions.onlineTest(sender, "all"))
                                .then(registrar.literal("heartbeat")
                                        .executes(sender -> CscCommandActions.onlineTest(sender, "heartbeat")))
                                .then(registrar.literal("version")
                                        .executes(sender -> CscCommandActions.onlineTest(sender, "version")))
                                .then(registrar.literal("rules")
                                        .executes(sender -> CscCommandActions.onlineTest(sender, "rules")))
                                .then(registrar.literal("feedback")
                                        .executes(sender -> CscCommandActions.onlineTest(sender, "feedback")))));

        registrar.register(root);
    }
}
