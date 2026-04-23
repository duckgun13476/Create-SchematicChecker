package com.Pink_Cats.createschematicchecker.lang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.DefineLanguage;

public class CSCLanguage {

    private static final String DEFAULT_LANGUAGE = "en_us";
    private static final String RESOURCE_ROOT = "assets/createschematicchecker/lang/";
    private static final String BUILD_INFO_RESOURCE = "createschematicchecker-build.properties";
    private static final Map<String, Map<String, String>> jsonLanguageDictionary = new HashMap<>();
    private static final Properties buildInfo = loadBuildInfo();
    public static final String mc_version = buildInfo.getProperty("minecraft_version", "unknown");
    public static final String csc_version = buildInfo.getProperty("csc_version", "unknown");

    public static String translateDirect(String key) {
        String translation = getTranslation(normalizeLanguage(DefineLanguage), key);
        if (translation == null) {
            translation = getTranslation(DEFAULT_LANGUAGE, key);
        }
        return translation != null ? translation : key;
    }

    private static String getTranslation(String language, String key) {
        Map<String, String> languageMap = jsonLanguageDictionary.computeIfAbsent(language, CSCLanguage::loadJsonLanguage);
        return languageMap.get(key);
    }

    private static Map<String, String> loadJsonLanguage(String language) {
        Map<String, String> result = new HashMap<>();
        String resourcePath = RESOURCE_ROOT + language + ".json";

        try (InputStream inputStream = CSCLanguage.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return result;
            }

            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    result.put(entry.getKey(), resolveTemplate(entry.getValue().getAsString()));
                }
            }
        } catch (Exception ignored) {
            return new HashMap<>();
        }

        return result;
    }

    private static Properties loadBuildInfo() {
        Properties properties = new Properties();

        try (InputStream inputStream = CSCLanguage.class.getClassLoader().getResourceAsStream(BUILD_INFO_RESOURCE)) {
            if (inputStream == null) {
                return properties;
            }

            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
        } catch (Exception ignored) {
            return new Properties();
        }

        return properties;
    }

    private static String resolveTemplate(String value) {
        return value
                .replace("${mc_version}", mc_version)
                .replace("${csc_version}", csc_version);
    }

    private static String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            return DEFAULT_LANGUAGE;
        }
        return language.trim().toLowerCase(Locale.ROOT);
    }
}
