package com.Pink_Cats.createschematicchecker.lang;

import java.util.HashMap;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.DefineLanguage;

public class CSCLanguage {

    private static final String DEFAULT_LANGUAGE = "en_us";
    private static final String mc_version = "1.20.1";
    private static final String csc_version = "0.21";


    // 定义不同语言的翻译字典
    private static final Map<String, Map<String, String>> languageDictionary = new HashMap<>();

    static {
        // en_us
        Map<String, String> enUs = new HashMap<>();
        enUs.put("console.feedback", "Problem/Feedback: Tencent QQ：1061133894");
        enUs.put("console.LoadingConfig", "CSC is loading...   Success！");
        enUs.put("config.lang", "Choose a language you want to use     zh_ch / en_us ");
        enUs.put("config.EnableOrNot","Enable CSC (Create:Schematic Checker) or not");
        enUs.put("config.DelayTime","The time if checker is timeout!");
        enUs.put("console.ReloadSuccess", "CSC Reload successful!");
        enUs.put("config.BanTag", "BanTag. If any block has tag in this list will be cleared!");
        enUs.put("core.BanBlock", "The Block or Item you don't want to emerge in Schematic");
        enUs.put("core.BanBlock2", "Any block will clear if remove method failed!");
        enUs.put("console.McVersion", "Minecraft Version: "+mc_version+" forge");
        enUs.put("console.CscVersion", "[CSC] Version: v"+csc_version);
        enUs.put("console.reload1", "[CSC]   Reloading configuration--");
        enUs.put("console.reload2", "[CSC]   Configuration loaded");
        enUs.put("console.ConfigReloadError","[CSC]   Reload configuration failure！");
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


        // zh_cn
        Map<String, String> zhCn = new HashMap<>();
        zhCn.put("console.feedback", "问题/反馈: QQ：1061133894");
        zhCn.put("console.LoadingConfig", "[CSC] 加载配置中。。   加载完毕！");
        zhCn.put("config.lang", "选择默认的文件语言 zh_ch / en_us ");
        zhCn.put("config.EnableOrNot","是否启用 CSC （机械动力：蓝图检查）");
        zhCn.put("config.DelayTime","蓝图检查的超时时间");
        zhCn.put("console.ReloadSuccess", "[CSC] 配置重载成功！！");
        zhCn.put("config.BanTag", "禁止标签，任何方块如果内部包含此列表的标签，都会被清除！");
        zhCn.put("core.BanBlock", "被禁止的方块或物品，如果被填入，CSC将尝试剔除方块实体内的对应物品。");
        zhCn.put("core.BanBlock2", "如果剔除失败，CSC会将该物品清除实体数据，方块仍然可以打印，但会失去nbt数据。");
        zhCn.put("console.McVersion" ,"Minecraft 版本: "+mc_version+" forge");
        zhCn.put("console.CscVersion","[CSC]   版本: v"+csc_version);
        zhCn.put("console.reload1","[CSC]   重载配置中--");
        zhCn.put("console.reload2","[CSC]   配置加载完毕");
        zhCn.put("console.ConfigReloadError","[CSC]   配置重载发生错误！");
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
        zhCn.put("config.explain4",        "触发它们仅需上传固定修改参数的蓝图，这使得破坏服务器变得轻而易举，只要有蓝图炮，那么任何人都可以随意的");
        zhCn.put("config.explain5",        "以极低的成本和极少的时间破坏服务器。");
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
