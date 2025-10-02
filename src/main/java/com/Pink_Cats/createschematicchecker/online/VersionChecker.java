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
        // 1. 初始化 HTTPS 连接
        HttpsURLConnection conn = getHttpsURLConnection(targetUrl);

        // 4. 读取服务器响应内容（用 BufferedReader 高效读取文本流，自动处理字符编码）
        StringBuilder responseContent = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8) // 显式指定 UTF-8 编码，避免乱码
        )) {
            String line;
            while ((line = in.readLine()) != null) {
                responseContent.append(line); // 逐行读取，拼接完整响应（若需保留换行符，可加 responseContent.append("\n")）
            }
        } finally {
            conn.disconnect(); // 无论成功失败，都关闭连接释放资源
        }

        // 5. 返回完整响应字符串（若接口返回 JSON/XML/纯文本，均直接返回原始内容）
        return responseContent.toString();
    }



    private static @NotNull HttpsURLConnection getHttpsURLConnection(String targetUrl) throws IOException {
        HttpsURLConnection conn = getUrlConnection(targetUrl);

        // 3. 检查 HTTP 响应状态码（仅 200 OK 视为请求成功，其他状态码抛异常）
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpsURLConnection.HTTP_OK) {
            throw new IOException(
                    String.format("GET 请求失败！地址：%s，状态码：%d，状态描述：%s",
                            targetUrl, responseCode, conn.getResponseMessage())
            );
        }
        return conn;
    }

    private static @NotNull HttpsURLConnection getUrlConnection(String targetUrl) throws IOException {
        URL url = new URL(targetUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        // 2. 配置 GET 请求参数（GET 请求无请求体，核心是设置请求方法和必要头信息）
        conn.setRequestMethod("GET"); // 强制指定为 GET 方法
        conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8"); // 适配文本类响应（可根据接口调整）
        conn.setRequestProperty("User-Agent", "Java-HTTPS-GET-Client/1.0"); // 模拟客户端标识，避免部分服务器拦截
        conn.setConnectTimeout(5000); // 连接超时（5秒，可根据需求调整）
        conn.setReadTimeout(10000); // 读取响应超时（10秒，可根据需求调整）
        return conn;
    }

    // 请求服务器版本（POST）
    private static String fetchServerVersion(String versionUrl) throws IOException {
        URL url = new URL(versionUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        // 可选：发送空 JSON 请求体
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

            // 简单解析 {"version":"2025_10_01_07_43_12"}
            String json = response.toString();
            int start = json.indexOf(":\"") + 2;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        }
    }


    //get
    private static String fetchServerVersionByGet(String versionUrl) throws IOException {
        // 1. 初始化 HTTPS 连接（与原函数一致，仅请求方法改为 GET）
        URL url = new URL(versionUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET"); // 核心修改：将 POST 改为 GET
        // 保留原函数的 Content-Type（即使 GET 无请求体，保持头信息与原函数一致，避免接口兼容性问题）
        conn.setRequestProperty("Content-Type", "application/json");
        // 移除 POST 特有的 setDoOutput(true) 和请求体写入（GET 不需要输出流）

        // 2. 读取服务器响应（与原函数完全一致的流处理逻辑）
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            // 3. 解析 JSON 响应（与原函数完全一致的解析逻辑：提取 "version" 字段值）
            String json = response.toString();
            int start = json.indexOf(":\"") + 2;
            int end = json.indexOf("\"", start);
            // 若未找到 "version" 字段（JSON 格式错误），抛出明确异常
            if (start == 1 || end == -1) {
                throw new IOException("服务器响应格式错误，无法解析版本：" + json);
            }
            return json.substring(start, end);
        }
    }


    // 下载 JSON 文件
    private static void downloadFile(String baseUrl, String version, String savePath) throws IOException {
        String fileUrl = baseUrl + "?version=" + URLEncoder.encode(version, StandardCharsets.UTF_8);
        URL url = new URL(fileUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream();
             FileOutputStream out = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    // 将版本字符串转换为 Date
    private static Date parseVersionDate(String version) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            return sdf.parse(version);

        } catch (ParseException e) {
            Message.FE("Date Parse error: " + e.getMessage());
            return new  Date();
        }
    }


    private static boolean checkServerVersion(String version, String serverVersion)  {
        System.out.println(serverVersion);
        System.out.println(version);
        return false;
    }

    public static Date getLocalVersion(String path) {
        File directory = new File(path);
        List<File> jsonFiles = new ArrayList<>();

        // 收集所有JSON文件
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
            Message.FE("指定路径不存在或不是目录: " + path);
        }

        // 如果有超过2个文件，只保留最新的2个，删除其余
        if (jsonFiles.size() > 2) {
            // 按最后修改时间排序（最新的在前）
            jsonFiles.sort((f1, f2) -> {
                // 注意：这里用f2.compareTo(f1)实现降序排序
                return Long.compare(f2.lastModified(), f1.lastModified());
            });

            // 删除从第4个开始的所有文件
            for (int i = 2; i < jsonFiles.size(); i++) {
                File fileToDelete = jsonFiles.get(i);
                if (fileToDelete.delete()) {
                    // 可以添加日志：文件已删除
                }
            }

            // 只保留前3个最新的文件
            jsonFiles = jsonFiles.subList(0, 2);
        }

        // 确定最新版本日期
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

    public static String UpdateMainThread(){
        String BaseOnlinePath = "config/CSC/online";
        createIfNotExists(BaseOnlinePath);

        String versionUrl = "https://mc.aisaveworld.tech:8144/Version"; // Flask 的 POST 接口
        String fileBaseUrl = "https://mc.aisaveworld.tech:8144/GetLatestRule"; // Flask 的 GET 接口

        try {
            disableSSLVerification();
            String serverVersion = fetchServerVersion(versionUrl);
            Date serverDate = parseVersionDate(serverVersion);
            Date localDate = getLocalVersion(BaseOnlinePath);
            String path = BaseOnlinePath+"/rule_"+serverVersion+".json";
            if (localDate.before(serverDate)) {
                System.out.println("old local download...");

                downloadFile(fileBaseUrl, serverVersion, path);
                System.out.println("download success");
            }
            RuleFixer(path);

            return path;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void main(String[] args) throws Exception {
        //String LOCAL_NBT_FILE_PATH = "uploaded/battery.nbt";
        //PostDataAsync(LOCAL_NBT_FILE_PATH);
        String Result = fetchDataByGet("https://mc.aisaveworld.tech:8144/server_feedback");


    }
}
