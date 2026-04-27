package com.Pink_Cats.createschematicchecker.online;

import com.Pink_Cats.createschematicchecker.lang.Message;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.Pink_Cats.createschematicchecker.FancyConfig.FileIO.createIfNotExists;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;
import static com.Pink_Cats.createschematicchecker.network.rule_io.RuleFixer;

public class VersionChecker {

    public static void disableSSLVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception ex) {
        }
    }

    private static String fetchDataByGet(String targetUrl) throws Exception {
        disableSSLVerification();
        HttpsURLConnection conn = getHttpsURLConnection(targetUrl);

        StringBuilder responseContent = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        )) {
            String line;
            while ((line = in.readLine()) != null) {
                responseContent.append(line);
            }
        } finally {
            conn.disconnect();
        }

        return responseContent.toString();
    }

    private static @NotNull HttpsURLConnection getHttpsURLConnection(String targetUrl) throws IOException {
        HttpsURLConnection conn = getUrlConnection(targetUrl);

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpsURLConnection.HTTP_OK) {
            throw new IOException(
                    String.format("GET 璇锋眰澶辫触锛佸湴鍧€锛?s锛岀姸鎬佺爜锛?d锛岀姸鎬佹弿杩帮細%s",
                            targetUrl, responseCode, conn.getResponseMessage())
            );
        }
        return conn;
    }

    private static @NotNull HttpsURLConnection getUrlConnection(String targetUrl) throws IOException {
        URL url = URI.create(targetUrl).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
        conn.setRequestProperty("User-Agent", "Java-HTTPS-GET-Client/1.0");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);
        return conn;
    }

    private static String fetchServerVersion(String versionUrl) throws IOException {
        URL url = URI.create(versionUrl).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write("{}".getBytes());
            os.flush();
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            String json = response.toString();
            int start = json.indexOf(":\"") + 2;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) {
        }
        return String.valueOf(new Date());
    }

    private static String fetchServerVersionByGet(String versionUrl) throws IOException {
        URL url = URI.create(versionUrl).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            String json = response.toString();
            int start = json.indexOf(":\"") + 2;
            int end = json.indexOf("\"", start);
            if (start == 1 || end == -1) {
                throw new IOException("Failed to parse version from server response: " + json);
            }
            return json.substring(start, end);
        }
    }

    private static void downloadFile(String baseUrl, String version, String savePath) throws IOException {
        String fileUrl = baseUrl + "?version=" + URLEncoder.encode(version, "UTF-8");
        URL url = URI.create(fileUrl).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream();
             FileOutputStream out = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (Exception ignored) {
        }
    }

    private static Date parseVersionDate(String version) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            return sdf.parse(version);
        } catch (ParseException e) {
            Message.FE("Date Parse error: " + e.getMessage());
            return new Date();
        }
    }

    public static Date getLocalVersion(String path) {
        File directory = new File(path);
        List<File> jsonFiles = new ArrayList<>();

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        jsonFiles.add(file);
                    }
                }
            }
        } else {
            Message.FE("鎸囧畾璺緞涓嶅瓨鍦ㄦ垨涓嶆槸鐩綍: " + path);
        }

        if (jsonFiles.size() > 2) {
            jsonFiles.sort((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

            for (int i = 2; i < jsonFiles.size(); i++) {
                File fileToDelete = jsonFiles.get(i);
                if (fileToDelete.delete()) {
                }
            }

            jsonFiles = jsonFiles.subList(0, 2);
        }

        if (jsonFiles.isEmpty()) {
            return parseVersionDate("2010_10_10_10_10_10");
        } else {
            Date versionDate = null;
            for (File jsonFile : jsonFiles) {
                Date itemDate = parseVersionDate(
                        jsonFile.getName().replaceAll("rule_(.*)\\.json", "$1")
                );
                if (versionDate == null || itemDate.after(versionDate)) {
                    versionDate = itemDate;
                }
            }
            return versionDate;
        }
    }

    public static String UpdateMainThread() {
        String BaseOnlinePath = "config/CSC/online";
        createIfNotExists(BaseOnlinePath);

        String versionUrl = "https://mc.aisaveworld.tech:8144/Version";
        String fileBaseUrl = "https://mc.aisaveworld.tech:8144/GetLatestRule";

        try {
            disableSSLVerification();
            String serverVersion = fetchServerVersion(versionUrl);
            Date serverDate = parseVersionDate(serverVersion);
            Date localDate = getLocalVersion(BaseOnlinePath);
            String path = BaseOnlinePath + "/rule_" + serverVersion + ".json";
            if (localDate.before(serverDate)) {
                Message.FM(translateDirect("csc.update.start"));
                downloadFile(fileBaseUrl, serverVersion, path);
                Message.FM(translateDirect("csc.update.success"));
            }
            RuleFixer(path);

            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String debugFetchServerVersion() throws IOException {
        disableSSLVerification();
        return fetchServerVersion("https://mc.aisaveworld.tech:8144/Version");
    }

    public static String debugFetchServerFeedback() throws Exception {
        return fetchDataByGet("https://mc.aisaveworld.tech:8144/server_feedback");
    }

    public static String debugSyncRules() {
        return UpdateMainThread();
    }

    public static void main(String[] args) throws Exception {
        String Result = fetchDataByGet("https://mc.aisaveworld.tech:8144/server_feedback");
    }
}
