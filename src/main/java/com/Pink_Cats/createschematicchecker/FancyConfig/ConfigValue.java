package com.Pink_Cats.createschematicchecker.FancyConfig;

import com.Pink_Cats.createschematicchecker.Message;

import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigHook.readToml;

public class ConfigValue {

    static SimpleTomlEditor tomlEditor = new SimpleTomlEditor("config.toml");


    // 来创建字符串配置项
    public ConfigString define(String key, java.lang.String defaultValue) {

        Object ConfigOperateResult = tomlEditor.ConfigValue_IO(key, defaultValue);
        if (ConfigOperateResult != null) {
            return new ConfigString(key, (String) ConfigOperateResult);
        }
        else {
            return new ConfigString(key, defaultValue);
        }
    }

    public static class ConfigString {
        private final String key;
        private java.lang.String value;

        // 构造方法
        public ConfigString(String key, java.lang.String defaultValue) {
            this.key = key;
            this.value = defaultValue; // 初始化为默认值
        }

        public ConfigString comment(String comment) {
            tomlEditor.insertCommentAboveKey(key, comment);
            return this;
        }

        // 获取默认值
        public java.lang.String getDefaultValue() {
            return value;
        }

        // 设置配置项的值
        public void setValue(java.lang.String value) {
            this.value = value;
        }

        // 获取键
        public String getKey() {
            return key;
        }

        // 静态方法将 Java String 转换为 ConfigString
        public static ConfigString fromJavaString(String key, java.lang.String value) {
            return new ConfigString(key, value);
        }


    }
//---------------------------------------------------------------------------------

    // 来创建布尔配置项
    public ConfigBoolean define(String key, boolean defaultValue) {
        Object configOperateResult = tomlEditor.ConfigValue_IO(key, (Object) defaultValue);
        Message.FW(configOperateResult);

        // 检查 configOperateResult 的类型
        if (configOperateResult instanceof Boolean) {
            return new ConfigBoolean(key, (Boolean) configOperateResult);
        } else if (configOperateResult instanceof String strValue) {
            // 如果是 String 类型，尝试将其转换为布尔值
            boolean booleanValue = Boolean.parseBoolean(strValue);
            return new ConfigBoolean(key, booleanValue);
        } else {
            return new ConfigBoolean(key, defaultValue);
        }
    }

    public static class ConfigBoolean {
        private final String key;
        private boolean value;

        // 构造方法
        public ConfigBoolean(String key, boolean defaultValue) {
            this.key = key;
            this.value = defaultValue; // 初始化为默认值
        }

        public ConfigBoolean comment(String comment) {
            tomlEditor.insertCommentAboveKey(key, comment);
            return this;
        }

        // 获取默认值
        public boolean getDefaultValue() {
            return value;
        }

        // 设置配置项的值
        public void setValue(boolean value) {
            this.value = value;
        }

        // 获取键
        public String getKey() {
            return key;
        }

        // 静态方法将布尔值转换为 ConfigBoolean
        public static ConfigBoolean fromJavaBoolean(String key, boolean value) {
            return new ConfigBoolean(key, value);
        }
    }
//---------------------------------------------------------------------------------

    public ConfigInt define(String key, int defaultValue) {
        Object configOperateResult = tomlEditor.ConfigValue_IO(key, (Object) defaultValue);
        Message.FW(configOperateResult);

        // 检查 configOperateResult 的类型
        if (configOperateResult instanceof Integer) {
            return new ConfigInt(key, (Integer) configOperateResult);
        } else if (configOperateResult instanceof String strValue) {
            // 如果是 String 类型，尝试将其转换为整数
            try {
                int intValue = Integer.parseInt(strValue);
                return new ConfigInt(key, intValue);
            } catch (NumberFormatException e) {
                Message.FW("Invalid integer format for key: " + key);
                return new ConfigInt(key, defaultValue);
            }
        } else {
            return new ConfigInt(key, defaultValue);
        }
    }

    public static class ConfigInt {
        private final String key;
        private int value;

        // 构造方法
        public ConfigInt(String key, int defaultValue) {
            this.key = key;
            this.value = defaultValue; // 初始化为默认值
        }

        public ConfigInt comment(String comment) {
            tomlEditor.insertCommentAboveKey(key, comment);
            return this;
        }

        // 获取默认值
        public int getDefaultValue() {
            return value;
        }

        // 设置配置项的值
        public void setValue(int value) {
            this.value = value;
        }

        // 获取键
        public String getKey() {
            return key;
        }

        // 静态方法将整数值转换为 ConfigInt
        public static ConfigInt fromJavaInt(String key, int value) {
            return new ConfigInt(key, value);
        }
    }

























}
