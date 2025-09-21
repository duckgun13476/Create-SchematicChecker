package com.Pink_Cats.createschematicchecker;

import java.util.HashMap;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.TryRun.DefineLanguage;

public class CSCLanguage {

    private static final String DEFAULT_LANGUAGE = "en_us";

    // 定义不同语言的翻译字典
    private static final Map<String, Map<String, String>> languageDictionary = new HashMap<>();

    static {
        // en_us
        Map<String, String> enUs = new HashMap<>();
        enUs.put("console.feedback", "Problem/Feedback: Tencent QQ：1061133894");
        enUs.put("console.LoadingConfig", "CSC is loading...");

        enUs.put("config.lang", "Choose a language you want to use");

        languageDictionary.put("en_us", enUs);

        // zh_cn
        Map<String, String> zhCn = new HashMap<>();
        zhCn.put("console.feedback", "问题/反馈: QQ：1061133894");
        enUs.put("console.LoadingConfig", "CSC 加载配置中。。");

        enUs.put("config.lang", "选择默认的文件语言");
        languageDictionary.put("zh_cn", zhCn);


    }

    public static String translateDirect(String key) {

        // 获取当前语言的翻译
        String translation = getTranslation(DefineLanguage, key);

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
