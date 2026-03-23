package com.Pink_Cats.createschematicchecker.online;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.Pink_Cats.createschematicchecker.online.VersionChecker.disableSSLVerification;

public class OnlineInterface {

    private static final String _P_2323_ = "https://mc.aisaveworld.tech:8144/";


    public static String OnlineHandler(String method, String data){

        disableSSLVerification();
        String RequestUrl = _P_2323_ + method;

        if (method.equals("heartbeat")){
            return SimpleHeartbeatPush(RequestUrl,data);
        }


        return "";
    }


    private static String SimpleHeartbeatPush(String RequestUrl,String data){
        try {
            URL url = URI.create(RequestUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // 允许写入请求体（POST 必需）
            conn.setConnectTimeout(3000); // 3秒连接超时
            conn.setReadTimeout(3000);    // 3秒读取超时
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
                os.flush(); // 确保数据全部发送
            }
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                String errorMsg = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                throw new IOException("推送失败！状态码：" + responseCode + "，错误：" + errorMsg);
            }
            String successResp = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            conn.disconnect(); // 关闭连接
            return successResp;

        } catch (IOException ignored) {
            return "";

        }

    }


}
