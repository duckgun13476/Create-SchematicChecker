package com.Pink_Cats.createschematicchecker.online;

import com.Pink_Cats.createschematicchecker.lang.Message;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.csc_version;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;
import static com.Pink_Cats.createschematicchecker.online.OnlineInterface.OnlineHandler;


public class SimpleHeartbeatPusher {

    public static Map<String, String> parseJsonToMap(String json) {
        Map<String, String> resultMap = new HashMap<>();
        if (json == null || json.trim().isEmpty()) {
            return resultMap;}
        String content = json.trim();
        if (content.startsWith("{")) {
            content = content.substring(1);}
        if (content.endsWith("}")) {
            content = content.substring(0, content.length() - 1);}
        String[] keyValuePairs = content.split(",");
        for (String pair : keyValuePairs) {
            pair = pair.trim();
            int colonIndex = pair.indexOf(":");
            if (colonIndex == -1) {
                continue;}
            String key = pair.substring(0, colonIndex).trim()
                    .replace("\"", ""); // 移除引号
            String value = pair.substring(colonIndex + 1).trim()
                    .replace("\"", ""); // 移除引号
            resultMap.put(key, value);
        }
        return resultMap;
    }


    public static void HeartBeatTask(){
        try {
            String jsonData = getString();
            String Return = OnlineHandler("heartbeat", jsonData);
            Map<String,String> Data = parseJsonToMap(Return);
            if (!Objects.equals(Data.get("version"), csc_version)){

                if (update_info){
                    Message.FM(translateDirect("config.online.UpdateInfo"));

                }

            }
            if (update_info){
                Message.FM(
                        translateDirect("check.Update.Info1")+Data.get("all_server")+
                                translateDirect("check.Update.Info2") +Data.get("all_schematic"));
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull String getString() {
        String cleanUuid = user_uuid.replaceAll("[\\r\\n]", "");
        String jsonData = String.format("{" +
                "\"uuid\":\"%s\"," +
                "\"count\":1," +
                "\"type\":\"java\"," +
                "\"GuardTime\":%d," +
                "\"CheatCount\":%d," +
                "\"CheckCount\":%d," +
                "\"ProblemCount\":%d" +
                "}", cleanUuid, GuardTime, CheatCount, CheckCount, ProblemCount);
        return jsonData;
    }


    // 测试入口：直接运行即可推送一条心跳数据
    public static void main(String[] args) {
        HeartBeatTask();

    }

}