package com.Pink_Cats.createschematicchecker.FancyConfig;


import com.Pink_Cats.createschematicchecker.lang.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.FileIO.createIfNotExists;
import static com.Pink_Cats.createschematicchecker.database.DataCount.*;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;
import static com.Pink_Cats.createschematicchecker.network.SimpleJsonParser.UpdateRuleThread;

public class ConfigRegister {

    // 定义 LANGUAGE 为 ConfigValue.String 类型
    static {
        createIfNotExists("config/CSC");
    }
    public static final String CommitBreak = "#----------------------------------------------------------------------";
    static final String ConfigPath = "config/CSC/config.toml";
    private static final ConfigValue ConfigBuild = new ConfigValue();
    private static final SimpleTomlEditor tomlEditor = new SimpleTomlEditor(ConfigPath);
    public static String DefineLanguage;
    static {
        tomlEditor.removeAllComments();
        Object languageValue = ConfigHook.readToml(ConfigPath).get("Language");
        DefineLanguage = (languageValue != null) ? languageValue.toString() : "en_us";
    }
    public static ConfigValue.ConfigString LANGUAGE = ConfigBuild
            .define( "Language", "en_us")
            .comment(CommitBreak)
            .comment("config.explain1")
            .comment("config.explain2")
            .comment("config.explain3")
            .comment("config.explain4")
            .comment("config.explain5")
            .comment("config.explain6")
            .comment("config.explain7")
            .comment("config.explain8")
            .comment("config.explain9")
            .comment("config.explain10")
            .comment("config.lang");


    public static ConfigValue.ConfigString UUID = ConfigBuild
            .define( "UUID", java.util.UUID.randomUUID().toString());

    public static ConfigValue.ConfigBoolean ENABLE = ConfigBuild
            .define("core.Enable", true)
            .comment(CommitBreak)
            .comment("config.EnableOrNot");

    public static ConfigValue.ConfigInt RANDOM = ConfigBuild
            .define("core.DelayTime", 10)
            .comment(CommitBreak)
            .comment("config.DelayTime");

    public static ConfigValue.ConfigStringArray BAN_BLOCK = ConfigBuild
            .define("core.BanBlock", new String[]{
                    "create:creative_crate",
                    "create:creative_fluid_tank",
                    "create_integrated_farming:chicken_roost",
                    "create:creative_motor",
                    "create:creative_blaze_cake",
                    "create:handheld_worldshaper",
                    "minecraft:spawner",
                    "twilightforest:giant_obsidian",
                    "twilightforest:giant_leaves",
                    "twilightforest:giant_log",
                    "twilightforest:giant_cobblestone",
                    "twilightforest:huge_lily_pad",
                    "ae2:creative_item_cell",
                    "ae2:creative_fluid_cell",
                    "ae2:creative_energy_cell",
                    "appmek:creative_chemical_cell",
                    "vs_clockwork:creative_gravitron",
                    "createbigcannons:creative_autocannon_ammo_container",
                    "createaddition:creative_energy",
                    "create_connected:creative_fluid_vessel",
                    "destroy:creative_pump",
                    "immersiveengineering:capacitor_creative",
                    "mekanism:creative_bin",
                    "mekanism:creative_fluid_tank",
                    "mekanism:creative_energy_cube",
                    "mekanism:creative_chemical_tank",
                    "numismatics:creative_vendor",
                    "mekanism_extras:upgrade_creative",
                    "the_vmod:physgun",
                    "the_vmod:toolgun",
                    "Item.of('tacz:ammo_box', '{Creative:1b}')",
                    "Item.of('tacz:ammo_box', '{AllTypeCreative:1b}')",
                    "minecraft:command_block",
                    "quark:grate",
                    "minecraft:kelp" })

            .comment(CommitBreak)
            .comment("core.BanBlock")
            .comment("core.BanBlock2");

    public static ConfigValue.ConfigStringArray BAN_TAG = ConfigBuild
            .define("core.BanTag", new String[]{
                    "AttributeModifiers", "run_command","using_converts_to","bundle_contents","minecraft:container"})
            .comment(CommitBreak)
            .comment("config.BanTag");


    public static ConfigValue.ConfigBoolean KILL_ENTITY = ConfigBuild
            .define("core.KillEntity", true)
            .comment(CommitBreak)
            .comment("config.KillEntity")
            .comment("config.KillEntity2")
            .comment("config.KillEntity3");


    public static ConfigValue.ConfigStringArray BAN_ENTITY = ConfigBuild
            .define("core.BanEntity", new String[]{
                    "minecraft:armor_stand"})
            .comment(CommitBreak)
            .comment("config.BanEntity")
            .comment("config.BanEntity2");

    public static ConfigValue.ConfigStringArray WHITELIST_ENTITY = ConfigBuild
            .define("core.whitelistEntity", new String[]{
                    "create:super_glue"})
            .comment(CommitBreak)
            .comment("config.whitelistEntity")
            .comment("config.whitelistEntity2")
            .comment("config.whitelistEntity3");


    public static ConfigValue.ConfigBoolean DEBUG_TOTAL_BLOCK = ConfigBuild
            .define("debug.DebugTotalBlock", true)
            .comment(CommitBreak)
            .comment("config.DebugTotalBlock");

    public static ConfigValue.ConfigBoolean DEBUG_CHEAT_FIND = ConfigBuild
            .define("debug.DebugCheatFind", true)
            .comment(CommitBreak)
            .comment("config.DebugCheatFind");

    public static ConfigValue.ConfigBoolean ENABLE_SCHEMATIC_BACKUP = ConfigBuild
            .define("debug.EnableBackup", true)
            .comment(CommitBreak)
            .comment("config.EnableBackup")
            .comment("config.EnableBackup2")
            .comment("config.EnableBackup3");


    public static ConfigValue.ConfigBoolean CHECK_BELT_MISMATCH = ConfigBuild
            .define("function.checkBelt", true)
            .comment(CommitBreak)
            .comment("config.checkBelt")
            .comment("config.checkBelt2");

    public static ConfigValue.ConfigBoolean TRY_REMOVE_PROBLEM_BELT_NOT_KILL = ConfigBuild
            .define("function.TryRemoveBeltNotKill", true)
            .comment(CommitBreak)
            .comment("config.TryRemoveBeltNotKill")
            .comment("config.TryRemoveBeltNotKill2");

    public static ConfigValue.ConfigInt MAX_BELT_CHEAT_LIMIT = ConfigBuild
            .define("function.maxBeltCheatLimit", 10)
            .comment(CommitBreak)
            .comment("config.maxBeltCheatLimit")
            .comment("config.maxBeltCheatLimit2");


    public static ConfigValue.ConfigInt MAX_CONVEYOR_CHEAT_DISTANCE_LIMIT = ConfigBuild
            .define("function.maxConveyorCheatDistanceLimit", 50)
            .comment(CommitBreak)
            .comment("config.maxConveyorCheatDistanceLimit")
            .comment("config.maxConveyorCheatDistanceLimit2");


    public static ConfigValue.ConfigInt MAX_CONVEYOR_CHEAT_LIMIT = ConfigBuild
            .define("function.maxConveyorCheatLimit", 10)
            .comment(CommitBreak)
            .comment("config.maxConveyorCheatLimit")
            .comment("config.maxConveyorCheatLimit2");




    public static ConfigValue.ConfigBoolean ENABLE_AUTO_UPDATE = ConfigBuild
            .define("online.enableAutoUpdate", false)
            .comment(CommitBreak)
            .comment("config.online.enableAutoUpdate1")
            .comment("config.online.enableAutoUpdate2")
            .comment("config.online.enableAutoUpdate3")
            .comment("config.online.enableAutoUpdate4")
            .comment("config.online.enableAutoUpdate5");

    public static ConfigValue.ConfigBoolean ENABLE_MANUAL_CONFIG= ConfigBuild
            .define("online.enableManualConfig", false)
            .comment(CommitBreak)
            .comment("config.online.enableManualConfig1")
            .comment("config.online.enableManualConfig2")
            .comment("config.online.enableManualConfig3")
            .comment("config.online.enableManualConfig4")
            .comment("config.online.enableManualConfig5")
            .comment("config.online.enableManualConfig6")
            .comment("config.online.enableManualConfig7")
            .comment("config.online.enableManualConfig8");

    public static ConfigValue.ConfigBoolean UPDATE_INFO= ConfigBuild
            .define("online.UpdateInfo", true)
            .comment(CommitBreak)
            .comment("config.online.UpdateInfo");


    public static String language = LANGUAGE.getDefaultValue();
    public static String user_uuid = UUID.getDefaultValue();
    public static boolean enable_csc = ENABLE.getDefaultValue();
    public static String[] ban_block = BAN_BLOCK.getDefaultValue();
    public static String[] ban_tag = BAN_TAG.getDefaultValue();
    public static boolean kill_entity = KILL_ENTITY.getDefaultValue();
    public static boolean debug_total_block = DEBUG_TOTAL_BLOCK.getDefaultValue();
    public static boolean check_belt = CHECK_BELT_MISMATCH.getDefaultValue();
    public static boolean remove_belt_instead_kill = TRY_REMOVE_PROBLEM_BELT_NOT_KILL.getDefaultValue();
    public static String[] ban_entity = BAN_ENTITY.getDefaultValue();
    public static String[] whitelist_entity = WHITELIST_ENTITY.getDefaultValue();
    public static boolean enable_backup = ENABLE_SCHEMATIC_BACKUP.getDefaultValue();
    public static boolean enable_auto_config_update = ENABLE_AUTO_UPDATE.getDefaultValue();
    public static boolean enable_manual_config = ENABLE_MANUAL_CONFIG.getDefaultValue();
    public static int maxBeltCheatLimit =  MAX_BELT_CHEAT_LIMIT.getDefaultValue();
    public static int maxConveyorCheatLimit =   MAX_CONVEYOR_CHEAT_LIMIT.getDefaultValue();
    public static int maxConveyorCheatDistanceLimit = MAX_CONVEYOR_CHEAT_DISTANCE_LIMIT.getDefaultValue();
    public static boolean DebugCheatFind = DEBUG_CHEAT_FIND.getDefaultValue();
    public static boolean update_info = UPDATE_INFO.getDefaultValue();


    public static String[][] ID_match_rule;
    public static String[][] Operate_match_rule;

    public static String[][] Operate_modify_rule_local;
    public static String[][] ID_modify_rule_local;
    public static String[][] Operate_modify_rule_online;
    public static String[][] ID_modify_rule_online;
    public static String[][] Operate_modify_rule_manual;
    public static String[][] ID_modify_rule_manual;


    public static String CannonDelay = "10";
    public static String MaxBelt = "20";
    public static String MaxIndex = "19";
    public static String MaxEject = "32";
    public static String MaxChassisRange = "16";

    public static long GuardTime;
    public static int  CheckCount;
    public static int  ProblemCount;
    public static int CheatCount;
    public static String CreateVersion = "0.5";



    public static void CSC_Variables_Load(){
        Map<String, String> CSCVariables= LoadVariables();
        GuardTime = Long.parseLong(CSCVariables.get("GuardTime"));
        CheckCount = Integer.parseInt(CSCVariables.get("CheckCount"));
        ProblemCount = Integer.parseInt(CSCVariables.get("ProblemCount"));
        CheatCount = Integer.parseInt(CSCVariables.get("CheatCount"));

    }


    public static void  CSC_Variables_Save(){
        Map<String, String> variables = new HashMap<>();
        variables.put("GuardTime", String.valueOf(GuardTime));
        variables.put("CheckCount", String.valueOf(CheckCount));
        variables.put("ProblemCount", String.valueOf(ProblemCount));
        variables.put("CheatCount", String.valueOf(CheatCount));
        WriteVariables(variables);
    }



    public static List<String> CSC_INIT(List<String> log) {
        update_info = UPDATE_INFO.getDefaultValue();
        DebugCheatFind = DEBUG_CHEAT_FIND.getDefaultValue();
        maxConveyorCheatDistanceLimit = MAX_CONVEYOR_CHEAT_DISTANCE_LIMIT.getDefaultValue();
        maxConveyorCheatLimit = MAX_CONVEYOR_CHEAT_LIMIT.getDefaultValue();
        maxBeltCheatLimit = MAX_BELT_CHEAT_LIMIT.getDefaultValue();
        language = LANGUAGE.getDefaultValue();
        user_uuid = UUID.getDefaultValue();
        enable_csc = ENABLE.getDefaultValue();
        ban_block = BAN_BLOCK.getDefaultValue();
        ban_tag = BAN_TAG.getDefaultValue();
        kill_entity = KILL_ENTITY.getDefaultValue();
        debug_total_block = DEBUG_TOTAL_BLOCK.getDefaultValue();
        check_belt = CHECK_BELT_MISMATCH.getDefaultValue();
        remove_belt_instead_kill = TRY_REMOVE_PROBLEM_BELT_NOT_KILL.getDefaultValue();
        whitelist_entity = WHITELIST_ENTITY.getDefaultValue();
        ban_entity = BAN_ENTITY.getDefaultValue();
        enable_backup = ENABLE_SCHEMATIC_BACKUP.getDefaultValue();
        enable_auto_config_update = ENABLE_AUTO_UPDATE.getDefaultValue();
        enable_manual_config = ENABLE_MANUAL_CONFIG.getDefaultValue();
        UpdateRuleThread(log);
        return log;
    }


    public static List<String> CSC_RELOAD() {
        Message.FM(translateDirect("console.reload1"));
        List<String> list = new ArrayList<String>();
        UPDATE_INFO.reload();
        DEBUG_CHEAT_FIND.reload();
        MAX_CONVEYOR_CHEAT_DISTANCE_LIMIT.reload();
        MAX_CONVEYOR_CHEAT_LIMIT.reload();
        MAX_BELT_CHEAT_LIMIT.reload();
        LANGUAGE.reload();
        UUID.reload();
        ENABLE.reload();
        BAN_BLOCK.reload();
        BAN_TAG.reload();
        KILL_ENTITY.reload();
        DEBUG_TOTAL_BLOCK.reload();
        CHECK_BELT_MISMATCH.reload();
        TRY_REMOVE_PROBLEM_BELT_NOT_KILL.reload();
        WHITELIST_ENTITY.reload();
        BAN_ENTITY.reload();
        ENABLE_SCHEMATIC_BACKUP.reload();
        ENABLE_AUTO_UPDATE.reload();
        ENABLE_MANUAL_CONFIG.reload();
        CSC_INIT(list);
        Message.FM(translateDirect("console.reload2"));
        return list;
    }





}