package com.Pink_Cats.createschematicchecker.FancyConfig;

import com.Pink_Cats.createschematicchecker.lang.CSCLanguage;
import com.Pink_Cats.createschematicchecker.lang.Message;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.ConfigPath;
import static com.Pink_Cats.createschematicchecker.core.attach.Math.trimALL;

public class ConfigValue {

    static SimpleTomlEditor tomlEditor = new SimpleTomlEditor(ConfigPath);

    public ConfigString define(String key, String defaultValue) {
        Object configOperateResult = tomlEditor.ConfigValue_IO(key, defaultValue);
        if (configOperateResult != null) {
            return new ConfigString(key, (String) configOperateResult);
        }
        return new ConfigString(key, defaultValue);
    }

    public static class ConfigString {
        private final String key;
        private String value;

        public ConfigString(String key, String defaultValue) {
            this.key = key;
            this.value = defaultValue;
        }

        public ConfigString comment(String comment) {
            tomlEditor.insertCommentAboveKeyIfMissing(key, CSCLanguage.translateDirect(comment));
            return this;
        }

        public String getDefaultValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public static ConfigString fromJavaString(String key, String value) {
            return new ConfigString(key, value);
        }

        public void reload() {
            Object configOperateResult = tomlEditor.ConfigValue_IO(key, value);
            if (configOperateResult != null) {
                value = (String) configOperateResult;
            }
        }
    }

    public ConfigBoolean define(String key, boolean defaultValue) {
        Object configOperateResult = tomlEditor.ConfigValue_IO(key, defaultValue);
        if (configOperateResult instanceof Boolean) {
            return new ConfigBoolean(key, (Boolean) configOperateResult);
        }
        if (configOperateResult instanceof String) {
            String strValue = trimALL((String) configOperateResult);
            return new ConfigBoolean(key, "true".equals(strValue));
        }
        return new ConfigBoolean(key, defaultValue);
    }

    public static class ConfigBoolean {
        private final String key;
        private boolean value;

        public ConfigBoolean(String key, boolean defaultValue) {
            this.key = key;
            this.value = defaultValue;
        }

        public ConfigBoolean comment(String comment) {
            tomlEditor.insertCommentAboveKeyIfMissing(key, CSCLanguage.translateDirect(comment));
            return this;
        }

        public boolean getDefaultValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void reload() {
            Object configOperateResult = tomlEditor.ConfigValue_IO(key, value);
            if (configOperateResult instanceof Boolean) {
                value = (Boolean) configOperateResult;
            } else if (configOperateResult instanceof String) {
                String strValue = trimALL((String) configOperateResult);
                value = "true".equals(strValue);
            } else {
                value = false;
            }
        }

        public static ConfigBoolean fromJavaBoolean(String key, boolean value) {
            return new ConfigBoolean(key, value);
        }
    }

    public ConfigInt define(String key, int defaultValue) {
        Object configOperateResult = tomlEditor.ConfigValue_IO(key, defaultValue);
        if (configOperateResult instanceof Integer) {
            return new ConfigInt(key, (Integer) configOperateResult);
        }
        if (configOperateResult instanceof String) {
            String strValue = ((String) configOperateResult).trim();
            try {
                return new ConfigInt(key, Integer.parseInt(strValue));
            } catch (NumberFormatException e) {
                Message.FW("Invalid integer format for key: " + key + " value: '" + strValue + "'");
            }
        }
        return new ConfigInt(key, defaultValue);
    }

    public static class ConfigInt {
        private final String key;
        private int value;

        public ConfigInt(String key, int defaultValue) {
            this.key = key;
            this.value = defaultValue;
        }

        public ConfigInt comment(String comment) {
            tomlEditor.insertCommentAboveKeyIfMissing(key, CSCLanguage.translateDirect(comment));
            return this;
        }

        public int getDefaultValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public static ConfigInt fromJavaInt(String key, int value) {
            return new ConfigInt(key, value);
        }

        public void reload() {
            Object configOperateResult = tomlEditor.ConfigValue_IO(key, value);
            if (configOperateResult instanceof Integer) {
                value = (Integer) configOperateResult;
            } else if (configOperateResult instanceof String) {
                String strValue = ((String) configOperateResult).trim();
                try {
                    value = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    Message.FW("Invalid integer format for key: " + key + " value: '" + strValue + "'");
                    value = 0;
                }
            } else {
                value = 0;
            }
        }
    }

    public ConfigStringArray define(String key, String[] defaultValue) {
        Object configOperateResult = tomlEditor.ConfigValue_IO(key, defaultValue);
        if (configOperateResult instanceof String[]) {
            return new ConfigStringArray(key, (String[]) configOperateResult);
        }
        if (configOperateResult instanceof String) {
            String[] strArray = processArrayString((String) configOperateResult);
            return new ConfigStringArray(key, strArray);
        }
        return new ConfigStringArray(key, defaultValue);
    }

    public static class ConfigStringArray {
        private final String key;
        private String[] value;

        public ConfigStringArray(String key, String[] defaultValue) {
            this.key = key;
            this.value = defaultValue;
        }

        public ConfigStringArray comment(String comment) {
            tomlEditor.insertCommentAboveKeyIfMissing(key, CSCLanguage.translateDirect(comment));
            return this;
        }

        public String[] getDefaultValue() {
            return value;
        }

        public void setValue(String[] value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public static ConfigStringArray fromJavaStringArray(String key, String[] value) {
            return new ConfigStringArray(key, value);
        }

        public void reload() {
            Object configOperateResult = tomlEditor.ConfigValue_IO(key, value);
            if (configOperateResult instanceof String[]) {
                value = (String[]) configOperateResult;
            } else if (configOperateResult instanceof String) {
                value = processArrayString((String) configOperateResult);
            } else {
                value = new String[]{};
            }
        }
    }

    public static String[] processArrayString(String strValue) {
        strValue = strValue.replaceAll("[\\[\\]\" {}XYZ]", "");
        strValue = trimALL(strValue);
        return strValue.split(",");
    }

    public static String[] processArrayPos(String strValue) {
        strValue = strValue.replaceAll("[\\[\\]\" {}XYZ:]", "");
        strValue = trimALL(strValue);
        return strValue.split(",");
    }

    public static String[] ChainSplit(String strValue) {
        strValue = strValue.replaceAll("[\\[\\]\" ]", "");
        strValue = trimALL(strValue);
        return strValue.split("\\.");
    }

    public static String[] KnifeSplit(String strValue) {
        strValue = strValue.replaceAll("[\\[\\]\" ]", "");
        strValue = trimALL(strValue);
        return strValue.split("\\$");
    }
}
