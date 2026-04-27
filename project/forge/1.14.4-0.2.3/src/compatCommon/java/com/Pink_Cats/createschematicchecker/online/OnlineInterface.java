package com.Pink_Cats.createschematicchecker.online;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.Pink_Cats.createschematicchecker.online.VersionChecker.disableSSLVerification;

public class OnlineInterface {

    private static final String _P_2323_ = "https://mc.aisaveworld.tech:8144/";

    public static String OnlineHandler(String method, String data) {
        disableSSLVerification();
        String requestUrl = _P_2323_ + method;

        if (method.equals("heartbeat")) {
            return SimpleHeartbeatPush(requestUrl, data);
        }

        return "";
    }

    private static String SimpleHeartbeatPush(String requestUrl, String data) {
        try {
            URL url = URI.create(requestUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                String errorMsg = readStream(conn.getErrorStream());
                throw new IOException("Heartbeat push failed: " + responseCode + " " + errorMsg);
            }

            String successResp = readStream(conn.getInputStream());
            conn.disconnect();
            return successResp;
        } catch (IOException ignored) {
            return "";
        }
    }

    private static String readStream(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }

        try (InputStream input = stream; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return output.toString(StandardCharsets.UTF_8.name());
        }
    }
}
