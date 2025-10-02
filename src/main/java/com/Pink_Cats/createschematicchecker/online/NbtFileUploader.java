package com.Pink_Cats.createschematicchecker.online;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.UUID;
import javax.net.ssl.*;

public class NbtFileUploader {

    /**
     * 初始化信任所有证书的SSL上下文
     */
    private static void initUnsafeSSL() throws NoSuchAlgorithmException, KeyManagementException {
        // 创建信任所有证书的TrustManager
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        // 安装信任所有证书的SSL上下文
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // 设置默认SSLSocketFactory
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        // 设置不验证主机名
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    private static String generateBoundary() {
        return "Boundary-" + UUID.randomUUID().toString().replace("-", "");
    }

    public static String uploadNbtFile(String localFilePath) throws Exception {
        // 初始化不安全的SSL配置
        initUnsafeSSL();

        // 校验本地文件
        File nbtFile = new File(localFilePath);
        if (!nbtFile.exists()) {
            throw new IOException("本地文件不存在：" + localFilePath);
        }
        if (!nbtFile.getName().endsWith(".nbt")) {
            throw new IOException("文件格式错误：仅支持.nbt文件");
        }
        if (!nbtFile.isFile()) {
            throw new IOException("路径不是文件：" + localFilePath);
        }

        String boundary = generateBoundary();
        URL url = new URL(UPLOAD_API_URL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        try {
            // 配置连接
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("User-Agent", "Java-Nbt-Uploader/1.0");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);

            // 构建请求体
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                // 写入文件部分
                String startBoundary = "--" + boundary + "\r\n";
                outputStream.write(startBoundary.getBytes(StandardCharsets.UTF_8));

                String contentDisposition = "Content-Disposition: form-data; name=\"file\"; filename=\"" + nbtFile.getName() + "\"\r\n";
                outputStream.write(contentDisposition.getBytes(StandardCharsets.UTF_8));

                String contentType = "Content-Type: application/octet-stream\r\n\r\n";
                outputStream.write(contentType.getBytes(StandardCharsets.UTF_8));

                // 写入文件内容
                try (FileInputStream fileInputStream = new FileInputStream(nbtFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                // 写入结束边界
                String endBoundary = "\r\n--" + boundary + "--\r\n";
                outputStream.write(endBoundary.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            // 检查响应状态
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                // 读取错误响应
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    throw new IOException(String.format("上传失败，状态码：%d，响应：%s",
                            statusCode, errorResponse.toString()));
                }
            }

            // 读取成功响应
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } finally {
            connection.disconnect();
        }
    }


    private static final String UPLOAD_API_URL = "https://mc.aisaveworld.tech:8144/uploadfile/";
    //private static final String LOCAL_NBT_FILE_PATH = "uploaded/battery.nbt";

    private static final int TIMEOUT_MS = 10000;



    public static void AutoUpdateThread(String filepath){
        try {
            uploadNbtFile(filepath);
        } catch (Exception e) {
        }
    }


    public static void main(String[] args) {
        //AutoUpdateThread(filepath);
    }
}
