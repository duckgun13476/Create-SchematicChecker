package com.Pink_Cats.createschematicchecker.FancyConfig;


import com.Pink_Cats.createschematicchecker.lang.Message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.FileIO.createIfNotExists;
import static com.Pink_Cats.createschematicchecker.database.DataCount.*;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.csc_version;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;
import static com.Pink_Cats.createschematicchecker.network.SimpleJsonParser.UpdateRuleThread;

public class ConfigRegister {

    // 定义 LANGUAGE 为 ConfigValue.String 类型
    static {
        createIfNotExists("config/CSC");
    }
    public static final String CommitBreak = "#----------------------------------------------------------------------";
    static final String ConfigPath = "config/CSC/config.toml";
    private static final String ConfigVersionKey = "ConfigVersion";
    private static final List<String> MIGRATABLE_CONFIG_KEYS = Arrays.asList(
            "Language",
            "UUID",
            "core.Enable",
            "core.DelayTime",
            "core.WhiteListModEnable",
            "core.ShowWhitelistModeNotice",
            "core.KillEntity",
            "debug.DebugTotalBlock",
            "debug.DebugCheatFind",
            "debug.EnableBackup",
            "debug.problem",
            "function.checkBelt",
            "function.TryRemoveBeltNotKill",
            "function.TryRemoveFluidTankNotKill",
            "function.maxBeltCheatLimit",
            "function.maxConveyorCheatDistanceLimit",
            "function.maxConveyorCheatLimit",
            "function.maxConveyorAllowDegree",
            "online.enableAutoUpdate",
            "online.enableManualConfig",
            "online.UpdateInfo",
            "online.report"
    );
    static {
        ensureValidConfigToml();
        archiveOutdatedConfigToml();
    }
    private static final ConfigValue ConfigBuild = new ConfigValue();
    private static final SimpleTomlEditor tomlEditor = new SimpleTomlEditor(ConfigPath);
    public static String DefineLanguage;
    static {
        Object languageValue = ConfigHook.readToml(ConfigPath).get("Language");
        DefineLanguage = (languageValue != null) ? languageValue.toString() : "en_us";
    }


    public static ConfigValue.ConfigString CONFIG_VERSION = ConfigBuild
            .define(ConfigVersionKey, csc_version)
            .comment(CommitBreak)
            .comment("config.ConfigVersion");


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



    public static ConfigValue.ConfigBoolean WHITE_LIST_MOD_ENABLE = ConfigBuild
            .define("core.WhiteListModEnable", false)
            .comment(CommitBreak)
            .comment("config.WhiteListModEnable");

    public static ConfigValue.ConfigBoolean WHITE_LIST_MOD_NOTICE = ConfigBuild
            .define("core.ShowWhitelistModeNotice", true)
            .comment(CommitBreak)
            .comment("config.ShowWhitelistModeNotice");


    public static ConfigValue.ConfigStringArray WHITE_LIST_MOD = ConfigBuild
            .define("core.WhiteListModList", new String[]{
                    "create","minecraft",
                    "createdieselgenerators",
                    "createrailwaysnavigator",
                    "createaddition",
                    "railways",
                    "copycats",
                    "createsifter",
                    "computercraft",
                    "mekanism",
                    "simulated",
                    "aeronautics",
                    "offroad",
                    "yuushya"})
            .comment(CommitBreak)
            .comment("config.WhiteListMod");



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
                    "AttributeModifiers", "run_command",
                    "using_converts_to","bundle_contents",
                    "minecraft:container","minecraft:attribute_modifiers"})
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
                    "minecraft:armor_stand",
                    "create:crafting_blueprint"})
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

    public static ConfigValue.ConfigBoolean TRY_REMOVE_PROBLEM_FLUID_TANK_NOT_KILL = ConfigBuild
            .define("function.TryRemoveFluidTankNotKill", true)
            .comment(CommitBreak)
            .comment("config.TryRemoveFluidTankNotKill")
            .comment("config.TryRemoveFluidTankNotKill2");

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

    public static ConfigValue.ConfigInt MAX_CONVEYOR_DEGREE = ConfigBuild
            .define("function.maxConveyorAllowDegree",45)
            .comment(CommitBreak)
            .comment("config.maxConveyorAllowDegree");


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

    public static ConfigValue.ConfigBoolean REPORT_SCHEMATIC= ConfigBuild
            .define("online.report", true)
            .comment(CommitBreak)
            .comment("config.debug.report");

    public static ConfigValue.ConfigBoolean ENABLE_DEBUG= ConfigBuild
            .define("debug.problem", false)
            .comment(CommitBreak)
            .comment("config.debug.problem");

    static {
        syncConfigComments();
    }


    public static String language = LANGUAGE.getDefaultValue();
    public static String user_uuid = UUID.getDefaultValue();
    public static boolean enable_csc = ENABLE.getDefaultValue();
    public static String[] ban_block = BAN_BLOCK.getDefaultValue();
    public static String[] ban_tag = BAN_TAG.getDefaultValue();
    public static boolean kill_entity = KILL_ENTITY.getDefaultValue();
    public static boolean debug_total_block = DEBUG_TOTAL_BLOCK.getDefaultValue();
    public static boolean check_belt = CHECK_BELT_MISMATCH.getDefaultValue();
    public static boolean remove_belt_instead_kill = TRY_REMOVE_PROBLEM_BELT_NOT_KILL.getDefaultValue();
    public static boolean remove_fluid_tank_instead_kill = TRY_REMOVE_PROBLEM_FLUID_TANK_NOT_KILL.getDefaultValue();
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
    public static boolean enable_debug = ENABLE_DEBUG.getDefaultValue();
    public static boolean report_schematic = REPORT_SCHEMATIC.getDefaultValue();
    public static int max_conveyor_degree = MAX_CONVEYOR_DEGREE.getDefaultValue();

    public static boolean CheckRunCommand = false;

    public static String[] white_list_mod = WHITE_LIST_MOD.getDefaultValue();
    public static boolean white_list_mod_enable = WHITE_LIST_MOD_ENABLE.getDefaultValue();
    public static boolean white_list_mod_notice = WHITE_LIST_MOD_NOTICE.getDefaultValue();


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
        white_list_mod = WHITE_LIST_MOD.getDefaultValue();
        white_list_mod_enable = WHITE_LIST_MOD_ENABLE.getDefaultValue();
        white_list_mod_notice = WHITE_LIST_MOD_NOTICE.getDefaultValue();
        enable_debug = ENABLE_DEBUG.getDefaultValue();
        update_info = UPDATE_INFO.getDefaultValue();
        DebugCheatFind = DEBUG_CHEAT_FIND.getDefaultValue();
        maxConveyorCheatDistanceLimit = MAX_CONVEYOR_CHEAT_DISTANCE_LIMIT.getDefaultValue();
        maxConveyorCheatLimit = MAX_CONVEYOR_CHEAT_LIMIT.getDefaultValue();
        maxBeltCheatLimit = MAX_BELT_CHEAT_LIMIT.getDefaultValue();
        language = LANGUAGE.getDefaultValue();
        DefineLanguage = language;
        user_uuid = UUID.getDefaultValue();
        enable_csc = ENABLE.getDefaultValue();
        ban_block = BAN_BLOCK.getDefaultValue();
        ban_tag = BAN_TAG.getDefaultValue();
        kill_entity = KILL_ENTITY.getDefaultValue();
        debug_total_block = DEBUG_TOTAL_BLOCK.getDefaultValue();
        check_belt = CHECK_BELT_MISMATCH.getDefaultValue();
        remove_belt_instead_kill = TRY_REMOVE_PROBLEM_BELT_NOT_KILL.getDefaultValue();
        remove_fluid_tank_instead_kill = TRY_REMOVE_PROBLEM_FLUID_TANK_NOT_KILL.getDefaultValue();
        whitelist_entity = WHITELIST_ENTITY.getDefaultValue();
        ban_entity = BAN_ENTITY.getDefaultValue();
        enable_backup = ENABLE_SCHEMATIC_BACKUP.getDefaultValue();
        enable_auto_config_update = ENABLE_AUTO_UPDATE.getDefaultValue();
        enable_manual_config = ENABLE_MANUAL_CONFIG.getDefaultValue();
        report_schematic =  REPORT_SCHEMATIC.getDefaultValue();
        max_conveyor_degree = MAX_CONVEYOR_DEGREE.getDefaultValue();
        syncConfigComments();
        UpdateRuleThread(log);
        return log;
    }


    public static List<String> CSC_RELOAD() {
        ensureValidConfigToml();
        Message.FM(translateDirect("console.reload1"));
        List<String> list = new ArrayList<String>();
        CONFIG_VERSION.reload();
        WHITE_LIST_MOD.reload();
        WHITE_LIST_MOD_ENABLE.reload();
        WHITE_LIST_MOD_NOTICE.reload();
        ENABLE_DEBUG.reload();
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
        TRY_REMOVE_PROBLEM_FLUID_TANK_NOT_KILL.reload();
        WHITELIST_ENTITY.reload();
        BAN_ENTITY.reload();
        ENABLE_SCHEMATIC_BACKUP.reload();
        ENABLE_AUTO_UPDATE.reload();
        ENABLE_MANUAL_CONFIG.reload();
        REPORT_SCHEMATIC.reload();
        MAX_CONVEYOR_DEGREE.reload();
        CSC_INIT(list);
        Message.FM(translateDirect("console.reload2"));
        return list;
    }

    public static void setWhiteListModeNotice(boolean enabled) {
        white_list_mod_notice = enabled;
        WHITE_LIST_MOD_NOTICE.setValue(enabled);
        tomlEditor.setScalarValue(WHITE_LIST_MOD_NOTICE.getKey(), enabled);
    }

    public static void setWhiteListModEnable(boolean enabled) {
        white_list_mod_enable = enabled;
        WHITE_LIST_MOD_ENABLE.setValue(enabled);
        tomlEditor.setScalarValue(WHITE_LIST_MOD_ENABLE.getKey(), enabled);
    }

    private static void ensureValidConfigToml() {
        String validateResult = ConfigHook.validateToml(ConfigPath);
        if (validateResult == null) {
            return;
        }

        try {
            Path configFile = Path.of(ConfigPath);
            if (Files.exists(configFile)) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
                String originalFileName = configFile.getFileName().toString();
                Path backupFile = configFile.resolveSibling(originalFileName + ".problem_" + timestamp+".toml");
                Files.move(configFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
                Message.FE("[CSC] Invalid config.toml detected, moved to " + backupFile.toAbsolutePath() + ". Reason: " + validateResult);
            }
            Files.deleteIfExists(Path.of(ConfigPath));
            Files.createFile(Path.of(ConfigPath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to recover invalid config.toml: " + e.getMessage(), e);
        }
    }

    private static void archiveOutdatedConfigToml() {
        try {
            Path configFile = Path.of(ConfigPath);
            if (!Files.exists(configFile) || Files.size(configFile) == 0) {
                return;
            }

            new SimpleTomlEditor(ConfigPath).relocateTopLevelKeyIfNeeded(ConfigVersionKey);
            Map<String, Object> oldConfig = ConfigHook.readToml(ConfigPath);
            Object versionValue = oldConfig.get(ConfigVersionKey);
            String configVersion = versionValue == null ? null : versionValue.toString();
            if (csc_version.equals(configVersion)) {
                return;
            }
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
            Path archiveFile = configFile.resolveSibling("config_old_" + timestamp + ".toml");
            Files.move(configFile, archiveFile, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(configFile);
            Files.createFile(configFile);
            writeMigratedConfigValues(configFile, oldConfig);
            ConfigArchiveNotice.markArchived(configVersion, csc_version, archiveFile.toAbsolutePath());
            Message.FW("[CSC] Old config.toml archived to " + archiveFile.toAbsolutePath()
                    + " because ConfigVersion changed from "
                    + (configVersion == null ? "missing" : configVersion)
                    + " to "
                    + csc_version);
        } catch (IOException e) {
            throw new RuntimeException("Failed to archive outdated config.toml: " + e.getMessage(), e);
        }
    }

    private static String languageFromOldConfig(Object languageValue) {
        if (languageValue == null) {
            return "en_us";
        }
        String oldLanguage = languageValue.toString().trim();
        return oldLanguage.isEmpty() ? "en_us" : oldLanguage;
    }

    private static void writeMigratedConfigValues(Path configFile, Map<String, Object> oldConfig) throws IOException {
        SimpleTomlEditor newConfigEditor = new SimpleTomlEditor(configFile.toString());
        newConfigEditor.ConfigValue_IO(ConfigVersionKey, csc_version);
        newConfigEditor.ConfigValue_IO("Language", languageFromOldConfig(oldConfig.get("Language")));
        for (String key : MIGRATABLE_CONFIG_KEYS) {
            if ("Language".equals(key)) {
                continue;
            }
            Object oldValue = oldConfig.get(key);
            if (isMigratableScalar(oldValue)) {
                newConfigEditor.ConfigValue_IO(key, oldValue);
            }
        }
    }

    private static boolean isMigratableScalar(Object value) {
        return value instanceof String
                || value instanceof Boolean
                || value instanceof Integer;
    }

    private static void syncConfigComments() {
        syncComments(ConfigVersionKey, CommitBreak, "config.ConfigVersion");
        syncComments("Language",
                CommitBreak,
                "config.explain1",
                "config.explain2",
                "config.explain3",
                "config.explain4",
                "config.explain5",
                "config.explain6",
                "config.explain7",
                "config.explain8",
                "config.explain9",
                "config.explain10",
                "config.lang");
        syncComments("core.Enable", CommitBreak, "config.EnableOrNot");
        syncComments("core.DelayTime", CommitBreak, "config.DelayTime");
        syncComments("core.WhiteListModEnable", CommitBreak, "config.WhiteListModEnable");
        syncComments("core.ShowWhitelistModeNotice", CommitBreak, "config.ShowWhitelistModeNotice");
        syncComments("core.WhiteListModList", CommitBreak, "config.WhiteListMod");
        syncComments("core.BanBlock", CommitBreak, "core.BanBlock", "core.BanBlock2");
        syncComments("core.BanTag", CommitBreak, "config.BanTag");
        syncComments("core.KillEntity", CommitBreak, "config.KillEntity", "config.KillEntity2", "config.KillEntity3");
        syncComments("core.BanEntity", CommitBreak, "config.BanEntity", "config.BanEntity2");
        syncComments("core.whitelistEntity", CommitBreak, "config.whitelistEntity", "config.whitelistEntity2", "config.whitelistEntity3");
        syncComments("debug.DebugTotalBlock", CommitBreak, "config.DebugTotalBlock");
        syncComments("debug.DebugCheatFind", CommitBreak, "config.DebugCheatFind");
        syncComments("debug.EnableBackup", CommitBreak, "config.EnableBackup", "config.EnableBackup2", "config.EnableBackup3");
        syncComments("function.checkBelt", CommitBreak, "config.checkBelt", "config.checkBelt2");
        syncComments("function.TryRemoveBeltNotKill", CommitBreak, "config.TryRemoveBeltNotKill", "config.TryRemoveBeltNotKill2");
        syncComments("function.TryRemoveFluidTankNotKill", CommitBreak, "config.TryRemoveFluidTankNotKill", "config.TryRemoveFluidTankNotKill2");
        syncComments("function.maxBeltCheatLimit", CommitBreak, "config.maxBeltCheatLimit", "config.maxBeltCheatLimit2");
        syncComments("function.maxConveyorCheatDistanceLimit", CommitBreak, "config.maxConveyorCheatDistanceLimit", "config.maxConveyorCheatDistanceLimit2");
        syncComments("function.maxConveyorCheatLimit", CommitBreak, "config.maxConveyorCheatLimit", "config.maxConveyorCheatLimit2");
        syncComments("function.maxConveyorAllowDegree", CommitBreak, "config.maxConveyorAllowDegree");
        syncComments("online.enableAutoUpdate", CommitBreak,
                "config.online.enableAutoUpdate1",
                "config.online.enableAutoUpdate2",
                "config.online.enableAutoUpdate3",
                "config.online.enableAutoUpdate4",
                "config.online.enableAutoUpdate5");
        syncComments("online.enableManualConfig", CommitBreak,
                "config.online.enableManualConfig1",
                "config.online.enableManualConfig2",
                "config.online.enableManualConfig3",
                "config.online.enableManualConfig4",
                "config.online.enableManualConfig5",
                "config.online.enableManualConfig6",
                "config.online.enableManualConfig7",
                "config.online.enableManualConfig8");
        syncComments("online.UpdateInfo", CommitBreak, "config.online.UpdateInfo");
        syncComments("online.report", CommitBreak, "config.debug.report");
        syncComments("debug.problem", CommitBreak, "config.debug.problem");
        tomlEditor.compactBlankLinesInSections();
    }

    private static void syncComments(String key, String... commentKeys) {
        tomlEditor.relocateKeyToParentSectionIfNeeded(key);
        List<String> comments = Arrays.stream(commentKeys)
                .map(translateKey -> CommitBreak.equals(translateKey) ? CommitBreak : translateDirect(translateKey))
                .collect(java.util.stream.Collectors.toList());
        tomlEditor.syncCommentsAboveKey(key, comments);
    }





}
