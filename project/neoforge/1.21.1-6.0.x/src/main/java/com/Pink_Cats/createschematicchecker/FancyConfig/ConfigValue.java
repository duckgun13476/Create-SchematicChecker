package com.Pink_Cats.createschematicchecker.FancyConfig;

import com.Pink_Cats.createschematicchecker.lang.CSCLanguage;
import com.Pink_Cats.createschematicchecker.lang.Message;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.ConfigPath;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.NoAir;

public class ConfigValue {

    static SimpleTomlEditor tomlEditor = new SimpleTomlEditor(ConfigPath);


//---------------------------------------------------------------------------------
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
            tomlEditor.insertCommentAboveKeyIfMissing(key, CSCLanguage.translateDirect(comment));
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

        public void reload() {
            Object configOperateResult = tomlEditor.ConfigValue_IO(key, value);
            if (configOperateResult != null) {
                value = (String) configOperateResult; // 更新当前值
            }
        }


    }


//---------------------------------------------------------------------------------
    // 来创建布尔配置项
    public ConfigBoolean define(String key, boolean defaultValue) {
        Object configOperateResult = tomlEditor.ConfigValue_IO(key, (Object) defaultValue);


        // 检查 configOperateResult 的类型
        if (configOperateResult instanceof Boolean) {
            return new ConfigBoolean(key, (Boolean) configOperateResult);
        } else if (configOperateResult instanceof String strValue) {
            strValue = NoAir(strValue);
            // 如果是 String 类型，尝试将其转换为布尔值
            if ("true".equals(strValue)){
                return new ConfigBoolean(key, true);
            }else{
                return new ConfigBoolean(key, false);
            }
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
            tomlEditor.insertCommentAboveKeyIfMissing(key, CSCLanguage.translateDirect(comment));
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

        public void reload() {
            Object configOperateResult = tomlEditor.ConfigValue_IO(key, value);
            if (configOperateResult instanceof Boolean) {
                value = (boolean) configOperateResult; // 更新当前值
            } else if (configOperateResult instanceof String strValue) {
            strValue = NoAir(strValue);
            // 如果是 String 类型，尝试将其转换为布尔值
                value = "true".equals(strValue);
            } else {
                value = false;
            }


        }

        // 静态方法将布尔值转换为 ConfigBoolean
        public static ConfigBoolean fromJavaBoolean(String key, boolean value) {
            return new ConfigBoolean(key, value);
        }
    }


//---------------------------------------------------------------------------------

    public ConfigInt define(String key, int defaultValue) {
        Object configOperateResult = tomlEditor.ConfigValue_IO(key, (Object) defaultValue);

        // 检查 configOperateResult 的类型
        if (configOperateResult instanceof Integer) {
            return new ConfigInt(key, (Integer) configOperateResult);
        } else if (configOperateResult instanceof String strValue) {
            strValue = strValue.trim();
            try {
                int intValue = Integer.parseInt(strValue);
                return new ConfigInt(key, intValue);
            } catch (NumberFormatException e) {
                Message.FW("Invalid integer format for key: " + key + " value: '" + strValue + "'");
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
            tomlEditor.insertCommentAboveKeyIfMissing(key, CSCLanguage.translateDirect(comment));
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

        public void reload() {
            Object configOperateResult = tomlEditor.ConfigValue_IO(key, value);
            if (configOperateResult instanceof Integer) {
                value = (int) configOperateResult; // 更新当前值

            } else if (configOperateResult instanceof String strValue) {
                strValue = strValue.trim();
                try {
                    int intValue = Integer.parseInt(strValue);
                    value = new ConfigInt(key, intValue).getDefaultValue();
                } catch (NumberFormatException e) {
                    Message.FW("Invalid integer format for key: " + key + " value: '" + strValue + "'");
                    value = 0;
                }
            } else {
                value = 0;
            }
        }
    }


//---------------------------------------------------------------------------------

    public ConfigStringArray define(String key, String[] defaultValue) {
        Object configOperateResult = tomlEditor.ConfigValue_IO(key, (Object) defaultValue);

        // 检查 configOperateResult 的类型
        if (configOperateResult instanceof String[]) {
            return new ConfigStringArray(key, (String[]) configOperateResult);
        } else if (configOperateResult instanceof String strValue) {

            String[] strArray = processArrayString(strValue); // 假设使用逗号分隔

            return new ConfigStringArray(key, strArray);
        } else {
            return new ConfigStringArray(key, defaultValue);
        }
    }

    public static class ConfigStringArray {
        private final String key;
        private String[] value;

        // 构造方法
        public ConfigStringArray(String key, String[] defaultValue) {
            this.key = key;
            this.value = defaultValue; // 初始化为默认值
        }

        public ConfigStringArray comment(String comment) {
            tomlEditor.insertCommentAboveKeyIfMissing(key, CSCLanguage.translateDirect(comment));
            return this;
        }

        // 获取默认值
        public String[] getDefaultValue() {
            return value;
        }

        // 设置配置项的值
        public void setValue(String[] value) {
            this.value = value;
        }

        // 获取键
        public String getKey() {
            return key;
        }

        // 静态方法将字符串数组转换为 ConfigStringArray
        public static ConfigStringArray fromJavaStringArray(String key, String[] value) {
            return new ConfigStringArray(key, value);
        }

        public void reload() {
            Object configOperateResult = tomlEditor.ConfigValue_IO(key, value);
            if (configOperateResult instanceof String[]) {
                value = (String[]) configOperateResult; // 更新当前值

            } else if (configOperateResult instanceof String strValue) {

                // 假设使用逗号分隔

                value = processArrayString(strValue);
            } else {
                value = new String[]{};
            }




        }
    }











    /**
     * Remove [ ] and " split.
     * @param strValue String
     * @return result
     */
    public static String[] processArrayString(String strValue) {
        // 去掉方括号和空格
        strValue = strValue.replaceAll("[\\[\\]\" {}XYZ]", "");
        strValue = NoAir(strValue);
        // 使用逗号分割字符串
        return strValue.split(",");
    }

    public static String[] processArrayPos(String strValue) {
        // 去掉方括号和空格
        strValue = strValue.replaceAll("[\\[\\]\" {}XYZ:]", "");
        strValue = NoAir(strValue);
        // 使用逗号分割字符串
        return strValue.split(",");
    }

    public static String[] ChainSplit(String strValue) {
        // 去掉方括号和空格
        strValue = strValue.replaceAll("[\\[\\]\" ]", "");
        strValue = NoAir(strValue);
        // 使用.分割字符串
        return strValue.split("\\.");
    }



    public static String[] KnifeSplit(String strValue) {
        // 去掉方括号和空格
        strValue = strValue.replaceAll("[\\[\\]\" ]", "");
        strValue = NoAir(strValue);
        // 使用.分割字符串
        return strValue.split("\\$");
    }


}
