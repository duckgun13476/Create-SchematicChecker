package com.Pink_Cats.createschematicchecker.lang;

import java.util.HashMap;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.DefineLanguage;

public class CSCLanguage {

    private static final String DEFAULT_LANGUAGE = "en_us";

    // 定义不同语言的翻译字典
    private static final Map<String, Map<String, String>> languageDictionary = new HashMap<>();

    static {
        // en_us
        Map<String, String> enUs = new HashMap<>();
        enUs.put("console.feedback", "Problem/Feedback: Tencent QQ：1061133894");
        enUs.put("console.LoadingConfig", "CSC is loading...   Success！");
        enUs.put("config.lang", "Choose a language you want to use");
        enUs.put("config.EnableOrNot","Enable CSC (Create:Schematic Checker) or not");
        enUs.put("config.DelayTime","The time if checker is timeout!");
        enUs.put("console.ReloadSuccess", "Reload successful!");
        enUs.put("config.BanTag", "BanTag. If any block has tag in this list will be cleared!");


        enUs.put("core.BanBlock", "The Block or Item you don't want to emerge in Schematic");
        enUs.put("core.BanBlock2", "Any block will clear if remove method failed!");

        languageDictionary.put("en_us", enUs);

        // zh_cn
        Map<String, String> zhCn = new HashMap<>();
        zhCn.put("console.feedback", "问题/反馈: QQ：1061133894");
        zhCn.put("console.LoadingConfig", "CSC 加载配置中。。   加载完毕！");
        zhCn.put("config.lang", "选择默认的文件语言");
        zhCn.put("config.EnableOrNot","是否启用 CSC （机械动力：蓝图检查）");
        zhCn.put("config.DelayTime","蓝图检查的超时时间");
        zhCn.put("console.ReloadSuccess", "配置重载成功！！");
        zhCn.put("config.BanTag", "禁止标签，任何方块如果内部包含此列表的标签，都会被清除！");

        zhCn.put("core.BanBlock", "被禁止的方块或物品，如果被填入，CSC将尝试剔除方块实体内的对应物品。");
        zhCn.put("core.BanBlock2", "如果剔除失败，CSC会将该物品清除实体数据，方块仍然可以打印，但会失去nbt数据。");
        //enUs.put(" ")
        languageDictionary.put("zh_cn", zhCn);


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
