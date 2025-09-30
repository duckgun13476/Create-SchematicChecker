package com.Pink_Cats.createschematicchecker.FancyConfig;


import com.Pink_Cats.createschematicchecker.lang.Message;

import static com.Pink_Cats.createschematicchecker.FancyConfig.FileIO.createIfNotExists;

public class ConfigRegister {

    // 定义 LANGUAGE 为 ConfigValue.String 类型
    static {
        createIfNotExists("config/CSC");
    }
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
            .define( "Language", "zh_cn")
            .comment("config.lang");


    public static ConfigValue.ConfigString UUID = ConfigBuild
            .define( "UUID", "wd2d-ddd2-2dav");

    public static ConfigValue.ConfigBoolean ENABLE = ConfigBuild
            .define("core.Enable", true)
            .comment("config.EnableOrNot");

    public static ConfigValue.ConfigInt RANDOM = ConfigBuild
            .define("core.DelayTime", 52525)
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
                    "minecraft:command_block",
                    "minecraft:kelp" })

            .comment("core.BanBlock")
            .comment("core.BanBlock2");

    public static ConfigValue.ConfigStringArray BAN_TAG = ConfigBuild
            .define("core.BanTag", new String[]{
                    "AttributeModifiers", "run_command","using_converts_to","bundle_contents","minecraft:container"})
            .comment("config.BanTag");


    public static ConfigValue.ConfigBoolean KILL_ENTITY = ConfigBuild
            .define("core.KillEntity", true)
            .comment("config.KillEntity");


    public static ConfigValue.ConfigStringArray BAN_ENTITY = ConfigBuild
            .define("core.BanEntity", new String[]{
                    "minecraft:armor_stand"})
            .comment("config.BanEntity");

    public static ConfigValue.ConfigStringArray WHITELIST_ENTITY = ConfigBuild
            .define("core.whitelistEntity", new String[]{
                    "create:super_glue"})
            .comment("config.whitelistEntity");


    public static ConfigValue.ConfigBoolean DEBUG_TOTAL_BLOCK = ConfigBuild
            .define("debug.DebugTotalBlock", true)
            .comment("config.DebugTotalBlock");

    public static ConfigValue.ConfigBoolean ENABLE_SCHEMATIC_BACKUP = ConfigBuild
            .define("debug.EnableBackup", true)
            .comment("config.EnableBackup");


    public static ConfigValue.ConfigBoolean CHECK_BELT_MISMATCH = ConfigBuild
            .define("function.checkBelt", true)
            .comment("config.checkBelt");

    public static ConfigValue.ConfigBoolean TRY_REMOVE_PROBLEM_BELT_NOT_KILL = ConfigBuild
            .define("function.TryRemoveBeltNotKill", true)
            .comment("config.TryRemoveBeltNotKill");

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


    public static String CannonDelay = "10";
    public static String MaxBelt = "20";
    public static String MaxIndex = "19";
    public static String MaxEject = "32";
    public static String MaxChassisRange = "16";


    public static void CSC_INIT() {
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
        Message.FM("Ban Block: " );
        for (String s : ban_block) {
            Message.FM(s);
        }
        Message.FM(debug_total_block);
        Message.FM(kill_entity);
        Message.FM("enable backup"+ enable_backup);
    }


    public static  void CSC_RELOAD() {
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
        CSC_INIT();


        Message.FM("Ban Block: " );
        for (String s : ban_block) {
            Message.FM(s);
        }
        Message.FM(debug_total_block);
        Message.FM(kill_entity);
        Message.FM("enable backup"+ enable_backup);
    }





}