package com.Pink_Cats.createschematicchecker.lang;

import java.util.HashMap;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.DefineLanguage;

public class CSCLanguage {

    private static final String DEFAULT_LANGUAGE = "en_us";
    private static final String mc_version = "1.20.1";
    public static final String csc_version = "0.21";


    // 定义不同语言的翻译字典
    private static final Map<String, Map<String, String>> languageDictionary = new HashMap<>();

    static {
        // en_us
        Map<String, String> enUs = new HashMap<>();
        enUs.put("console.feedback", "Problem/Feedback: Tencent QQ：1061133894");
        enUs.put("console.LoadingConfig", "CSC is loading...   Success！");
        enUs.put("config.lang", "Choose a language you want to use     zh_cn / en_us ");
        enUs.put("config.EnableOrNot","Enable CSC (Create:Schematic Checker) or not");
        enUs.put("config.DelayTime","The time if checker is timeout!");
        enUs.put("console.ReloadSuccess", "[CSC] CSC Reload successful! time consume:");
        enUs.put("config.BanTag", "BanTag. If any block has tag in this list will be cleared!");
        enUs.put("core.BanBlock", "The Block or Item you don't want to emerge in Schematic");
        enUs.put("core.BanBlock2", "Any block will clear if remove method failed!");
        enUs.put("console.McVersion", "Minecraft Version: "+mc_version+" forge");
        enUs.put("console.CscVersion", "[CSC] Version: v"+csc_version);
        enUs.put("console.reload1", " Reloading configuration--");
        enUs.put("console.reload2", " Configuration loaded");
        enUs.put("console.ConfigReloadError"," Reload configuration failure！");
        enUs.put("config.KillEntity", "Whether to remove entities. When set to true, entities inside the schematic will be cleared, except those listed in the whitelist.");
        enUs.put("config.KillEntity2", "Enabling this feature can prevent all potential entity NBT duplication exploits, and also block armor stands from obtaining creative items.");
        enUs.put("config.KillEntity3", "It is recommended to enable this feature!");
        enUs.put("config.BanEntity", "When the KillEntity feature is set to false, this feature will be enabled. Any entity listed here will be removed from the schematic.");
        enUs.put("config.BanEntity2", "However, entities in the whitelist cannot be removed, so do not use the same ID in both lists.");
        enUs.put("config.whitelistEntity", "Entities in the whitelist. They must not conflict with the blacklist. Once added, these entities will be preserved when the KillEntity feature is enabled.");
        enUs.put("config.whitelistEntity2", "Note: Only the entities listed below will be validated by the algorithm. Other entities will retain their NBT data using default matching.");
        enUs.put("config.whitelistEntity3", "Matchable item: Super Glue.");

        enUs.put("config.explain1",        "    Welcome to CSC! CSC (formerly Create:SchematicChecker | Create: Schematic Validator)");
        enUs.put("config.explain2",        "is a blueprint scanning mod specifically designed for Create and all its addons. It helps prevent any known potential or malicious exploits.");
        enUs.put("config.explain3",        "These exploits include, but are not limited to: item duplication, server lag or crashes, unauthorized access to creative items, and even server control takeover.");
        enUs.put("config.explain4",        "Triggering these issues only requires uploading a blueprint with modified parameters, making server disruption extremely easy. With a schematic cannon, anyone can freely");
        enUs.put("config.explain5",        "damage the server at minimal cost and in very little time.");
        enUs.put("config.explain6",        "  This mod was specifically designed to solve these issues and permanently fix blueprint vulnerabilities in Create!");
        enUs.put("config.explain7",        "CSC allows users to customize blacklist tags and blocks, define exclusion rules, and configure detailed validation behavior for schematics.");
        enUs.put("config.explain8",        "Thanks to the universal nature of schematics, CSC can scan and validate content from Create and all its related mods!");
        enUs.put("config.explain9",        "If users opt in, CSC can even connect online to automatically sync rule updates to the local server. Whenever the CSC team discovers a new exploit,");
        enUs.put("config.explain10",       "the configuration file will be updated automatically, allowing potential threats to be removed without restarting the server.");

        enUs.put("config.DebugTotalBlock", "Whether to output the list of detected blueprint items in the chat. This may flood the chat, but it shows block statistics within the blueprint.");
        enUs.put("config.EnableBackup", "Whether to allow CSC to back up blueprints. This feature will back up the blueprint file after each upload, named by timestamp/player.");
        enUs.put("config.EnableBackup2", "This feature is highly recommended! When new exploits are discovered, it allows scanning historical uploads to trace back past activity.");
        enUs.put("config.EnableBackup3", "This feature will consume some storage space, as each uploaded blueprint will be saved.");
        enUs.put("config.checkBelt", "Whether to check the integrity of belts in the blueprint. If incomplete, the blueprint will be considered vulnerable, as belts spanning thousands of blocks can lag the server.");
        enUs.put("config.checkBelt2", "It may also cause chunk data to include belt entities, increasing bandwidth usage. Additionally, incomplete belts can duplicate shafts and belts.");
        enUs.put("config.TryRemoveBeltNotKill", "When incomplete belts are detected, whether to remove them instead of blocking the entire blueprint.");
        enUs.put("config.TryRemoveBeltNotKill2", "This feature is still in Beta. It allows the blueprint to be printed normally, but incomplete belts will not be printed.");

        enUs.put( "console.csc.list",        "IdMatchRuleAll        List all ID exclusion rules");
        enUs.put( "console.csc.list2",       "OperateMatchRuleAll   List all NBT operation rules");
        enUs.put( "console.csc.list3",       "IdMatchRule           List ID exclusion rules from local manual configuration and automatic updates");
        enUs.put( "console.csc.list4",       "OperateMatchRule      List NBT operation rules from local manual configuration and automatic updates");
        enUs.put( "console.csc.list5",       "Local manual configuration is located in config/CSC/user_rule.json ");
        enUs.put( "console.csc.list6",       "Automatic updates are located in config/CSC/online");
        enUs.put( "console.csc.run.command",        "-----[CSC]-----[Command Tips]-----");

        enUs.put( "console.csc.liner",              "----------------------------------");
        enUs.put( "console.csc.welcome",              "Welcome to [CSC] ");
        enUs.put( "console.csc.welcome1",             "[Click to access] Issue    Feedback [GitHub]");
        enUs.put( "console.csc.welcome2",             "[Click to access] Help/Feedback  QQ: 1061133894");
        enUs.put( "console.csc.welcome3",             "/csc reload Reload CSC configurations");
        enUs.put( "console.csc.welcome4",             "/csc list View CSC parameter help");
        enUs.put( "console.csc.welcome5",             "/csc list [Parameter]  View CSC parameters");
        enUs.put( "console.csc.welcome6",             "/csc Enable       Restart CSC");
        enUs.put( "console.csc.welcome7",             "/csc DisableTemp  temporary stop CSC 5min");

        enUs.put( "console.csc.StopServer",             "CSC close completely, thanks for using!");
        enUs.put( "console.paraList1",             "Currently attached ID matching cleanup rules");
        enUs.put( "console.paraList2",             "Currently attached Block NBT matching cleanup rules");
        enUs.put( "console.paraList3",             "All current ID matching cleanup rules, including built-in rules");
        enUs.put( "console.paraList4",             "All current Block NBT matching cleanup rules, including built-in rules");
        enUs.put( "console.error",             "CSC got error：");
        enUs.put("console.manual.config.error",             "[CSC] Configuration file error ->");
        enUs.put("console.config.manual.read.error",       "[CSC] Exception occurred while loading user configuration file! ->");
        enUs.put("console.config.online.read.error",       "[CSC] Exception occurred while loading synchronized configuration file! ->");
        enUs.put("console.makefile.error",                 "[CSC] Failed to create file or write content ->");
        enUs.put("console.miss.operate",                   "[CSC] Missing NBT operation method, must be [limit/clear/replace] ->");
        enUs.put("console.miss.operate2",                  "[CSC] Missing NBT operation prefix, must contain [operate] ->");
        enUs.put("console.miss.id",                        "[CSC] Block/item ID setup error, must be in [create:belt] format ->");
        enUs.put("console.miss.length2",                   "[CSC] Rule length setup error, must be [id 2 item] ->");
        enUs.put("console.miss.length3",                   "[CSC] Rule length setup error, must be [operate 3 item] ->");
        enUs.put("console.miss.format",                    "[CSC] Rule format error ->");
        enUs.put("console.miss.liner",                     " Field format error, missing '[' (line number ->");
        enUs.put("console.miss.json.start",                "[CSC] JSON should start with '{' (line number: 1)");
        enUs.put("console.miss.json.stop",                 "[CSC] JSON should end with '}' (line number ->");
        enUs.put("console.miss.json.empty",                "[CSC] JSON file is empty!");
        enUs.put("console.miss.fail.parse",                "[CSC] JSON file parsing failed, please delete CSC's user_rule.json and restart!");
        enUs.put( "console.newSchematic",             "New Schematic Uploaded: 》");
        enUs.put( "console.thread.new",             "thread run！goal =》");
        enUs.put( "console.total.time",             "bibi~ scan success time consume：");
        enUs.put( "console.cheat.find",             "Find cheat schematic！");

        enUs.put("console.cheat.copycats",             "Copycat item quantity is abnormal or items do not match layers! [Actual Quantity | Expected Quantity] [Included Items | Disguised Layers] =>");
        enUs.put("console.cheat.copycats.count",       "Copycat item quantity does not match!");
        enUs.put("console.cheat.belt.count",           "Number of mismatched conveyor belts exceeds cheat threshold! [Threshold | Actual Quantity]");
        enUs.put("console.cheat.belt.mismatch",        "Conveyor belt quantity does not match!");
        enUs.put("console.cheat.conveyor.angle",       "Chain drive wheel angle has been tampered with, exceeding 45 degrees!");
        enUs.put("console.cheat.conveyor.distance",    "Chain drive wheel length exceeds the upper limit in configuration file [Actual Value | Configured Value]");
        enUs.put("console.cheat.conveyor.limit",       "Number of mismatched chain drive wheels exceeds the cheat threshold in configuration file! [Actual Value | Configured Value]");
        enUs.put("console.cheat.crafter.mismatch",     "Mechanical Crafter entity data does not match!");
        enUs.put("console.cheat.crafter.attach",       "Mechanical Crafters considered the same entity are not connected to each other!");
        enUs.put("console.cheat.FluidTank.mismatch",   "Fluid Tank Controller quantity does not match! [Number of Controllers | Number of Tanks Found]");
        enUs.put("console.CheatOutput",                "Cheat schematic detected! Player:");
        enUs.put("console.CheatOutput2",               "   Schematic:");
        enUs.put("console.debug.itemDetail",           "Meow, output schematic item list!");


        enUs.put("config.maxBeltCheatLimit",             "Maximum allowed number of mismatched conveyor belts. If the number of conveyor belts with mismatched checksums in the schematic exceeds this threshold, the schematic will be deemed a cheat schematic");
        enUs.put("config.maxBeltCheatLimit2",            "If the number of mismatches in the schematic is less than this value, the problematic conveyor belts will be removed");
        enUs.put("config.maxConveyorCheatDistanceLimit", "Maximum allowed length of chain drive wheels (default is 32 in Create). If there are chain drive wheels in the schematic exceeding this distance, the schematic will be deemed a cheat schematic");
        enUs.put("config.maxConveyorCheatDistanceLimit2", "If the length of chain drive wheels is less than this threshold, only the problematic drive wheels will be removed");
        enUs.put("config.maxConveyorCheatLimit",         "Maximum allowed number of mismatched drive wheels. If the number of drive wheels with mismatched checksums in the schematic exceeds this threshold, the schematic will be deemed a cheat schematic");
        enUs.put("config.maxConveyorCheatLimit2",        "If the number of checksum mismatches is less than this value, the schematic will only remove the problematic drive wheels");
        enUs.put("config.online.enableAutoUpdate1",      "Whether to enable cloud-based automatic update synchronization. Note: This feature requires an internet connection!");
        enUs.put("config.online.enableAutoUpdate2",      "After enabling this feature, CSC will automatically synchronize the latest vulnerability exclusion rules from the cloud");
        enUs.put("config.online.enableAutoUpdate3",      "However, if abnormal schematics or unreadable schematics are detected, CSC will upload these schematics to the cloud to enhance cloud rules");
        enUs.put("config.online.enableAutoUpdate4",      "Since cloud checks require schematic samples for continuous updates, the uploaded schematics will only be used for vulnerability detection and no other purposes");
        enUs.put("config.online.enableAutoUpdate5",      "Enabling this feature will collect partial schematic data. Therefore, using this feature means you allow CSC to use your schematics for rule enhancement!");
        enUs.put("config.online.enableManualConfig1",    "Whether to enable user-configured rules. When enabled, the user's additional configuration file will be used as rules to process schematics");
        enUs.put("config.online.enableManualConfig2",    "ID is for the exclusion of entity blocks, used as a supplement when the banblock function fails to completely exclude targets");
        enUs.put("config.online.enableManualConfig3",    "Each object requires two elements: the item's ID and its position. You can query this using the NBTExplorer software");
        enUs.put("config.online.enableManualConfig4",    "By default, paths are separated by dots (.). If encountering a list, you need to add a $ before the list tag to assist in identification");
        enUs.put("config.online.enableManualConfig5",    "Operate is for NBT exclusion, used to manipulate the NBT structure of any block with a high degree of flexibility");
        enUs.put("config.online.enableManualConfig6",    "Limit: When setting Limit, you can specify the upper and lower bounds of a variable. The format required is operate.limit$VariableName$LowerBound$UpperBound");
        enUs.put("config.online.enableManualConfig7",    "Clear: When set to Clear, the corresponding NBT will be removed. The format required is operate.clear$VariableName");
        enUs.put("config.online.enableManualConfig8",    "Replace: When set to Replace, the corresponding NBT tag will be replaced (only strings can be replaced). The format required is operate.replace$VariableName$Result");
        enUs.put( "config.tag.mismatch.output",    "Warning: Forbidden Tag found in the schematic | Block NBT data=>");
        enUs.put( "console.problemOutput",             "Schematic verification mismatch occurred! Player:");
        enUs.put( "console.problemOutput2",             "   Schematic:");
        enUs.put( "console.cheat.createbigcannons",             "The item in the cannon shell is not a cannon fuse!=>");
        enUs.put( "console.cheat.clipboard",             "The clipboard contains tags that shouldn't exist, as this is impossible!=>");
        enUs.put( "console.csc.welcome.help",             "> /csc help for command assistance");
        enUs.put( "console.csc.board.day",             " day(s) ");
        enUs.put( "console.csc.board.hour",             " hour(s) ");
        enUs.put( "console.csc.board.minute",             " minute(s) ");
        enUs.put( "console.csc.board.second",             " second(s) ");
        enUs.put( "console.csc.board.guard",             "> CSC has been protecting your server");
        enUs.put( "console.csc.board.checkCount",             "> Total checked ");
        enUs.put( "console.csc.board.checkCount2",             " schematics");
        enUs.put( "console.csc.board.checkCount3",             "> Found ");
        enUs.put( "console.csc.board.checkCount4",             " problematic schematics");
        enUs.put( "console.csc.board.checkCount5",             "> Blocked ");
        enUs.put( "console.csc.board.checkCount6",             " cheating schematic uploads");
        enUs.put( "check.csc.cheat.filter",             "Too much Filter depth!");
        enUs.put( "check.chain.rule.error",             "A block matching the rule was found, but the variable for processing was not found in the block");
        enUs.put( "check.chain.rule.block",             "Block [");
        enUs.put( "check.chain.rule.rule",             "]  Rule [");
        enUs.put("config.online.UpdateInfo",             "Enable automatic update reminders. When set to true, if CSC has vulnerabilities that require a version update to fix, a reminder will be sent");
        enUs.put("check.Update.Info1",             "Connected to cloud server:");
        enUs.put("check.Update.Info2",             "  Cloud schematic samples:");
        enUs.put("csc.update.start",             "  Detected cloud rule update, preparing to download~");
        enUs.put("csc.update.success",             "  Update completed~");
        enUs.put("tag.no.id",             "Didn't find id ：" );

        enUs.put( "csc.trams.track_problem", "Find track renderer is none, This is unlegal. Set to default") ;
        enUs.put("config.debug.problem", "enable debug mod") ;
        enUs.put( "console.stop.csc.temp.output", "For user config CSC is now disable Please check schematic manly") ;
        enUs.put("csc.off.temporary.restore","CSC restored!");

        enUs.put("console.csc.cannotCheck","CSC can't check this blueprint Please input /csc help to feed back, feedback way is in it!");

        enUs.put("config.WhiteListMod", "Only mods listed here will be allowed to retain NBT. Enter the modid (e.g., for minecraft:dirt, enter minecraft)");
        enUs.put("config.WhiteListModEnable", "Whether to enable whitelist mode. Recommended to enable, as it can directly eliminate blueprint vulnerabilities caused by unfamiliar mods");
        enUs.put("config.DebugCheatFind", "Whether to broadcast cheaters and cheating blueprints when CSC detects a cheating blueprint");
        enUs.put("console.cheat.sign","Sign has command to cheat");

        enUs.put("config.debug.report","Whether to upload uncheck schematics. Default is true, this can help many people and CSC!");

        enUs.put("core.decode.ZipError","Oops！Schematic might broken！");


        // zh_cn
        Map<String, String> zhCn = new HashMap<>();
        zhCn.put("console.feedback", "问题/反馈: QQ：1061133894");
        zhCn.put("console.LoadingConfig", " 加载配置中。。   加载完毕！");
        zhCn.put("config.lang", "选择默认的文件语言 zh_cn / en_us ");
        zhCn.put("config.EnableOrNot","是否启用 CSC （机械动力：蓝图检查）");
        zhCn.put("config.DelayTime","蓝图检查的超时时间");
        zhCn.put("console.ReloadSuccess", "[CSC] 配置重载成功！！ 用时：");
        zhCn.put("config.BanTag", "禁止标签，任何方块如果内部包含此列表的标签，都会被清除！");
        zhCn.put("core.BanBlock", "被禁止的方块或物品，如果被填入，CSC将尝试剔除方块实体内的对应物品。");
        zhCn.put("core.BanBlock2", "如果剔除失败，CSC会将该物品清除实体数据，方块仍然可以打印，但会失去nbt数据。");
        zhCn.put("console.McVersion" ,"Minecraft 版本: "+mc_version+" forge");
        zhCn.put("console.CscVersion","[CSC]   版本: v"+csc_version);
        zhCn.put("console.reload1"," 重载配置中--");
        zhCn.put("console.reload2"," 配置加载完毕");
        zhCn.put("console.ConfigReloadError"," 配置重载发生错误！");
        zhCn.put("config.KillEntity","是否清理实体，当设置为true后，蓝图内的实体会被清除，但不会清除填入白名单的实体。");
        zhCn.put("config.KillEntity2","此功能启用可以阻止所有潜在的实体nbt复制漏洞，也可以阻止盔甲架获取创造物品");
        zhCn.put("config.KillEntity3","此功能建议启用！");
        zhCn.put("config.BanEntity","当KillEntity功能为false时，此功能将会启用，任何填入此列表的实体都会在蓝图中被删除。");
        zhCn.put("config.BanEntity2","但不能删除白名单内的实体，因此不能同时与白名单填入相同的id");
        zhCn.put("config.whitelistEntity","白名单的实体，不能与黑名单冲突，填入后的实体会被保留，在killEntity功能为true时启动");
        zhCn.put("config.whitelistEntity2","注意，此功能只有下面的实体会被算法校验，其他实体都会保留nbt，使用默认匹配");
        zhCn.put("config.whitelistEntity3","可匹配的物品：强力胶。");

        zhCn.put("config.explain1",        "    欢迎您使用CSC，CSC（原名 Create:SchematicChecker | 机械动力：蓝图校验 ）" );
        zhCn.put("config.explain2",        "是专为机械动力与全部机械动力附属而定制的蓝图扫描模组,它可以阻止任何已经记录在案的潜在/恶性漏洞。");
        zhCn.put("config.explain3",        "这些恶性漏洞包括但不限于：复制物品、卡顿服务器甚至崩溃服务器、获取创造物品、获取服务器控制权等。。");
        zhCn.put("config.explain4",        "而触发它们仅需上传固定修改参数的蓝图，这使得破坏服务器变得轻而易举，只要服务器不禁用蓝图炮，那么任何人都可以随意的");
        zhCn.put("config.explain5",        "以极低的成本和极少的时间破坏服务器、崩溃服务器、获取任意物品。");
        zhCn.put("config.explain6",        "  此模组转为此问题而设计，永久解决机械动力蓝图的弊病！");
        zhCn.put("config.explain7",        "CSC 允许用户自定义黑名单标签、方块，允许自定义剔除规则，允许对蓝图的校验结果进行详细的配置和自定义");
        zhCn.put("config.explain8",        "由于蓝图的通用性，这使得CSC可以检测包括机械动力和机械动力的所有附属mod！" );
        zhCn.put("config.explain9",        "如果用户同意，CSC甚至可以选择联网更新自动同步规则到本地服务器，只要CSC团队发现了新的漏洞，");
        zhCn.put("config.explain10",        "那么就会自动更新配置文件，来实现不需要重启服务器即可自动剔除潜在的漏洞。");

        zhCn.put("config.DebugTotalBlock",        "是否在聊天界面输出检测蓝图的物品列表，这可能会刷屏，但是可以显示蓝图内的方块统计");
        zhCn.put("config.EnableBackup",        "是否允许CSC对蓝图进行备份，此功能会在玩家每次上传后备份蓝图文件，以时间/玩家命名");
        zhCn.put("config.EnableBackup2",        "此功能强烈推荐开启，这可以在新的漏洞被发现时，通过扫描历史的上传备份来回溯上传历史！");
        zhCn.put("config.EnableBackup3",        "此功能会占用一些存储空间，因为会对每个上传蓝图进行储存。");
        zhCn.put("config.checkBelt",        "是否检测蓝图内传送带的完整性，如果不完整则会被视为漏洞蓝图，因为上千格的传送带可以卡服");
        zhCn.put("config.checkBelt2",        "同时还会导致区块数据包含传送带实体，增加带宽，同时，不完整的传送带可以复制传动杆和传送带");
        zhCn.put("config.TryRemoveBeltNotKill",        "当传送带被检测出不完整时，是否清除不完整的传送带而不是直接阻止蓝图");
        zhCn.put("config.TryRemoveBeltNotKill2",        "此功能还在Beta版本，这可以在传送带不完整时，正常打印蓝图，只是不完整的传送带不会打印");


        zhCn.put( "console.csc.list",        "IdMatchRuleAll        列出全部的 ID  剔除规则");
        zhCn.put( "console.csc.list2",        "OperateMatchRuleAll   列出全部的 nbt 操作规则");
        zhCn.put( "console.csc.list3",        "IdMatchRule           列出 本地手动配置 和 自动更新 的 ID 剔除规则");
        zhCn.put( "console.csc.list4",        "OperateMatchRule      列出 本地手动配置 和 自动更新 的 nbt 操作规则");
        zhCn.put( "console.csc.list5",        "本地手动配置 位于config/CSC/user_rule.json  ");
        zhCn.put( "console.csc.list6",        "自动更新 位于config/CSC/online");

        zhCn.put( "console.csc.run.command",        "------[CSC]-----[命令提示]-----");
        zhCn.put( "console.csc.liner",              "----------------------------");


        zhCn.put( "console.csc.welcome",              "欢迎使用  [机械动力：蓝图校验] ");
        zhCn.put( "console.csc.welcome1",             "[链接] 问题反馈[GitHub]");
        zhCn.put( "console.csc.welcome2",             "[链接] 帮助/反馈  QQ: 1061133894");
        zhCn.put( "console.csc.welcome3",             "/csc reload      重载CSC配置");
        zhCn.put( "console.csc.welcome4",             "/csc list        查看CSC的参数帮助");
        zhCn.put( "console.csc.welcome5",             "/csc list [参数]  查看CSC的参数");
        zhCn.put( "console.csc.welcome6",             "/csc Enable       重新启动CSC");
        zhCn.put( "console.csc.welcome7",             "/csc DisableTemp  临时关闭CSC 5分钟");



        zhCn.put( "console.csc.StopServer",             "CSC 已经安全关闭，感谢使用喵~");
        zhCn.put( "console.paraList1",             "当前附加的 ID 匹配清理规则");
        zhCn.put( "console.paraList2",             "当前附加的 方块nbt 匹配清理规则");
        zhCn.put( "console.paraList3",             "当前全部的 ID 匹配清理规则，包含内置规则");
        zhCn.put( "console.paraList4",             "当前全部的 方块nbt 匹配清理规则，包含内置规则");
        zhCn.put( "console.error",             "CSC发生了错误-》");


        zhCn.put( "console.manual.config.error",             "[CSC] 配置文件发生了错误-》");
        zhCn.put( "console.config.manual.read.error",             "[CSC] 加载用户配置文件发生了异常！-》");
        zhCn.put( "console.config.online.read.error",             "[CSC] 加载同步配置文件发生了异常！-》");
        zhCn.put( "console.makefile.error",             "[CSC] 创建文件或写入内容失败-》");
        zhCn.put( "console.miss.operate",             "[CSC] 缺少nbt操作方法，必须为 [limit/clear/replace]-》");
        zhCn.put( "console.miss.operate2",             "[CSC] 缺少nbt操作前缀，必须包含 [operate]-》");
        zhCn.put( "console.miss.id",             "[CSC] 方块/物品 id 设置错误，必须为[create:belt]格式-》");
        zhCn.put( "console.miss.length2",             "[CSC] 规则的长度设置错误，必须为[id 2 个变量]-》");
        zhCn.put( "console.miss.length3",             "[CSC] 规则的长度设置错误，必须为[operate 3 个变量]-》");
        zhCn.put( "console.miss.format",             "[CSC] 规则的格式出现错误-》");
        zhCn.put( "console.miss.liner",             " 字段格式错误，缺少'['（行号-》");
        zhCn.put( "console.miss.json.start",             "[CSC] JSON应以'{'开头（行号：1）");
        zhCn.put( "console.miss.json.stop",             "[CSC] JSON应以'}'结尾 行号-》");
        zhCn.put( "console.miss.json.empty",             "[CSC] JSON文件是空的！");
        zhCn.put( "console.miss.fail.parse",             "[CSC] JSON文件解析失败，请删除CSC 的 user_rule.json 并重启！");

        zhCn.put( "console.newSchematic",             "发现新的蓝图上传喵~ =》");
        zhCn.put( "console.thread.new",             "线程启动！目标=》");
        zhCn.put( "console.total.time",             "滴滴~ 扫描完毕 用时：");
        zhCn.put( "console.cheat.find",             "发现作弊蓝图！");




        zhCn.put( "console.cheat.copycats",             "伪装板的物品数量异常或物品与图层不匹配！[实际数量|应该的数量][包含的物品|伪装图层]=》");
        zhCn.put( "console.cheat.copycats.count",             "伪装板的物品数量不匹配！");
        zhCn.put( "console.cheat.belt.count",             "传送带不匹配的传送带超过作弊阈值！[阈值|实际数量]");
        zhCn.put( "console.cheat.belt.mismatch",             "传送带数量不匹配！");
        zhCn.put( "console.cheat.conveyor.angle",             "链式传动轮的角度被篡改，超过了45度！");
        zhCn.put( "console.cheat.conveyor.distance",             "链式传动轮的长度超过了配置文件的上限 [实际值|配置值]");
        zhCn.put( "console.cheat.conveyor.limit",             "链式传动轮不匹配的数量超过了配置文件的作弊阈值！ [实际值|配置值]");
        zhCn.put( "console.cheat.crafter.mismatch",             "动力合成器的实体数据不匹配！");
        zhCn.put( "console.cheat.crafter.attach",             "被视为同一实体的动力合成器没有相互连接！");
        zhCn.put( "console.cheat.FluidTank.mismatch",             "流体储罐控制器数量不匹配！[控制器数量|找到的容器数量]");
        zhCn.put( "console.CheatOutput",             "嘟嘟嘟！侦测到作弊蓝图！玩家：");
        zhCn.put( "console.CheatOutput2",             "   蓝图：");
        zhCn.put( "console.debug.itemDetail",             "呜喵，输出蓝图物品列表！");

        zhCn.put( "config.maxBeltCheatLimit",             "传送带的最大允许不匹配数量，当蓝图内校验和不匹配的传送带数量超过这个阈值后，蓝图会被视为作弊蓝图");
        zhCn.put( "config.maxBeltCheatLimit2",          "当蓝图内的不匹配数量少于这个值时，将会剔除有问题的传送带");
        zhCn.put( "config.maxConveyorCheatDistanceLimit",    "链式传动轮的最大允许长度，机械动力默认为32 当蓝图内有超过这个距离的链式传动轮时，蓝图会被视为作弊蓝图");
        zhCn.put( "config.maxConveyorCheatDistanceLimit2",    "当链式传动轮的长度低于这个阈值时，仅会剔除有问题的传动轮");
        zhCn.put( "config.maxConveyorCheatLimit",    "传动轮的最大允许不匹配数量，当蓝图校验和不匹配的传动轮数量超过这个阈值后，蓝图会被视为作弊蓝图");
        zhCn.put( "config.maxConveyorCheatLimit2",    "如果校验和不匹配数量少于这个值，蓝图仅会剔除有问题的传动轮");
        zhCn.put( "config.online.enableAutoUpdate1",    "是否启用云端自动更新同步功能，转为服务器定制，注意，此功能需要联网！");
        zhCn.put( "config.online.enableAutoUpdate2",    "启用此功能后，CSC将会自动从云端同步最新的漏洞剔除规则");
        zhCn.put( "config.online.enableAutoUpdate3",    "但如果出现了异常蓝图或无法读取的蓝图，CSC会将这些蓝图上传至云端来加强云端规则");
        zhCn.put( "config.online.enableAutoUpdate4",    "因为云端检查需要蓝图样本才能不断更新，上传的蓝图仅会用于漏洞检测，不会用于任何其他用途");
        zhCn.put( "config.online.enableAutoUpdate5",    "启用此功能会收集部分蓝图数据，因此如果您使用此功能就代表允许CSC利用您的蓝图进行规则强化！");
        zhCn.put( "config.online.enableManualConfig1",    "是否启用用户配置规则，当启用后，用户的额外配置文件会作为规则处理蓝图");
        zhCn.put( "config.online.enableManualConfig2",    "id 是针对实体方块的剔除，用于banblock功能剔除不完全时的附加");
        zhCn.put( "config.online.enableManualConfig3",    "每个对象需要两个元素，物品的ID 和位置，可以通过NBTExplorer 软件来查询 ");
        zhCn.put( "config.online.enableManualConfig4",    "默认情况下的路径使用.来分割，如果遇到列表，需要在列表标签前加入$ 来辅助识别");
        zhCn.put( "config.online.enableManualConfig5",    "operate 是针对nbt的剔除功能，用于操作任意方块的nbt结构，具有极高的自由度");
        zhCn.put( "config.online.enableManualConfig6",    "limit 当设置limit时，可以指定变量的上下限，需要使用 operate.limit$变量名$下限$上限  的格式");
        zhCn.put( "config.online.enableManualConfig7",    "clear 当设置为清除时，对应的nbt会被剔除，需要使用 operate.clear$变量名  的格式");
        zhCn.put( "config.online.enableManualConfig8",    "replace 当设置为替换时，对应的nbt标签会被替换 只能替换字符串，需要使用 operate.replace$变量名$结果 的格式");
        zhCn.put( "config.tag.mismatch.output",    "警告：在蓝图内发现被禁止的Tag | 方块nbt数据=》");
        zhCn.put( "console.problemOutput",             "蓝图出现校验不匹配！玩家：");
        zhCn.put( "console.problemOutput2",             "   蓝图：");
        zhCn.put( "console.cheat.createbigcannons",             "火炮炮弹内的物品不是火炮的引信！=》");
        zhCn.put( "console.cheat.clipboard",             "剪贴板包含不应该存在的标签，因为这是不可能的！=》");
        zhCn.put( "console.csc.welcome.help",             "》/csc help 获取相关指令帮助");
        zhCn.put( "console.csc.board.day",             " 天 ");
        zhCn.put( "console.csc.board.hour",             " 小时 ");
        zhCn.put( "console.csc.board.minute",             " 分钟 ");
        zhCn.put( "console.csc.board.second",             " 秒 ");
        zhCn.put( "console.csc.board.guard",             "》CSC已经守护了您的服务器 ");
        zhCn.put( "console.csc.board.checkCount",             "》总共检查了 ");
        zhCn.put( "console.csc.board.checkCount2",             " 个蓝图");
        zhCn.put( "console.csc.board.checkCount3",             "》发现了 ");
        zhCn.put( "console.csc.board.checkCount4",             " 个问题蓝图");
        zhCn.put( "console.csc.board.checkCount5",             "》阻止了 ");
        zhCn.put( "console.csc.board.checkCount6",             " 次作弊蓝图上传");
        zhCn.put( "check.csc.cheat.filter",             "过滤器迭代次数过多！");
        zhCn.put( "check.chain.rule.error",             "发现匹配规则的方块，但是没有在方块中找到处理的变量");
        zhCn.put( "check.chain.rule.block",             "方块[");
        zhCn.put("check.chain.rule.rule",             "]  规则 [");
        zhCn.put("config.online.UpdateInfo",             "启动自动更新提醒，当设置为true，如果CSC有需要更新版本才能修复的漏洞，将会发出提醒");
        zhCn.put("check.Update.Info1",             "已连接到云端的服务器：");
        zhCn.put("check.Update.Info2",             "  云端蓝图样本：");
        zhCn.put("csc.update.start",             "  检测到云端规则更新，准备下载~");
        zhCn.put("csc.update.success",             "  更新完毕~");
        zhCn.put("csc.kill.whitelist.ignore",             "根据配置忽略的物品：");
        zhCn.put("csc.mismatch.output1",             "位于[");
        zhCn.put("csc.mismatch.output2",             "][");
        zhCn.put("csc.mismatch.output3",             "] 不匹配的方块：[");
        zhCn.put("csc.mismatch.output4",             "] 总共应该剔除的数量: ");
        zhCn.put("csc.mismatch.output5",             " 还剩下的数量: " );
        zhCn.put("csc.mismatch.before",             "剔除前: " );
        zhCn.put("csc.mismatch.after",             "剔除后: " );
        zhCn.put("tag.no.id",             "没有在目标nbt内找到 id ：" );
        zhCn.put( "csc.trams.track_problem", "在火车轨道中发现方向渲染为空，这是不合理的，已重置为默认") ;
        zhCn.put("config.debug.problem", "启动调试模式，这会打印更多的报错输出，但会导致控制台变得混乱，绝大多数情况下不需要开启") ;
        zhCn.put( "console.disable.csc", "CSC已临时关闭5分钟，蓝图将不会被检测，请注意检查蓝图！") ;
        zhCn.put( "console.stop.csc.temp.output", "根据用户规则，当前蓝图不会检测，请注意检测蓝图") ;
        zhCn.put("csc.off.temporary.restore","CSC 已恢复启动");
        zhCn.put("console.csc.cannotCheck","CSC 不能检测这个蓝图，为了您和其他所有服务器安全，请输入 /csc help ，提交此蓝图的反馈！");



        zhCn.put("config.WhiteListMod","只有填入此列表的mod才会允许保留nbt，填入modid 比如minecraft:dirt 那就填入minecraft");
        zhCn.put("config.WhiteListModEnable","是否启动白名单模式，推荐开启，可以直接根除不熟悉的mod导致的蓝图漏洞");
        zhCn.put("config.DebugCheatFind","当CSC发现作弊蓝图时，是否广播作弊者和作弊蓝图");

        zhCn.put("console.cheat.sign","包含执行指令的告示牌！");
        zhCn.put("config.debug.report","如果出现了CSC检测不了的蓝图是否自动上传，默认开启，这可以帮助更多的人，也可以让CSC变得更好！");
        zhCn.put("core.decode.ZipError","蓝图发生了解析错误，蓝图可能已经损坏~");


        //enUs.put(" ")
        languageDictionary.put("zh_cn", zhCn);
        languageDictionary.put("en_us", enUs);


    }

    public static String translateDirect(String key) {

        // 获取当前语言的翻译
        String translation = getTranslation(DefineLanguage, key);
        //Message.FW(translation);
       // Message.FW(DefineLanguage);
      //  Message.FW(key);

        // 如果当前语言没有翻译，尝试英文
        if (translation == null) {
            translation = getTranslation(DEFAULT_LANGUAGE, key);
        }

        // 如果英文也没有翻译，返回原字符串
        return translation != null ? translation : key;
    }

    private static String getTranslation(String language, String key) {
        Map<String, String> languageMap = languageDictionary.get(language);
        return languageMap != null ? languageMap.get(key) : null;
    }
}
