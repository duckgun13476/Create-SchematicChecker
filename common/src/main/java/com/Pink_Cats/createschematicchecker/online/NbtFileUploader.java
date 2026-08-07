package com.Pink_Cats.createschematicchecker.online;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.report_endpoint;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.report_token;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.user_uuid;

/** Uploads only scanner-classified samples to the authenticated CSC v2 ingress. */
public class NbtFileUploader {

    private static final long MAX_FILE_BYTES = 5L * 1024L * 1024L;
    private static final int TIMEOUT_MS = 10_000;

    private static String generateBoundary() {
        return "Boundary-" + UUID.randomUUID().toString().replace("-", "");
    }

    public static String uploadNbtFile(String localFilePath) throws Exception {
        File nbtFile = validateNbtFile(localFilePath);
        URI endpoint = validateEndpoint();
        String serverId = requireConfigValue("online.UUID", user_uuid);
        String reportToken = requireConfigValue("online.reportToken", report_token);
        String contentHash = sha256(nbtFile);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
        String nonce = UUID.randomUUID().toString();
        String signature = hmacSha256(reportToken, serverId + "\n" + timestamp + "\n" + nonce + "\n" + contentHash);

        URLConnection openedConnection = endpoint.toURL().openConnection();
        if (!(openedConnection instanceof HttpsURLConnection)) {
            throw new IOException("CSC report endpoint must use HTTPS");
        }
        HttpsURLConnection connection = (HttpsURLConnection) openedConnection;
        String boundary = generateBoundary();

        try {
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("User-Agent", "Create-SchematicChecker/2");
            connection.setRequestProperty("X-CSC-Server-Id", serverId);
            connection.setRequestProperty("X-CSC-Timestamp", timestamp);
            connection.setRequestProperty("X-CSC-Nonce", nonce);
            connection.setRequestProperty("X-CSC-Signature", signature);
            // Sent only to allow a fresh installation to register its local credential.
            connection.setRequestProperty("X-CSC-Enrollment-Secret", reportToken);
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setChunkedStreamingMode(4096);

            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                writeMultipartFile(outputStream, boundary, nbtFile);
            }

            int statusCode = connection.getResponseCode();
            if (statusCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("CSC report upload failed, status=" + statusCode + ", response="
                        + readResponse(connection.getErrorStream()));
            }
            return readResponse(connection.getInputStream());
        } finally {
            connection.disconnect();
        }
    }

    private static File validateNbtFile(String localFilePath) throws IOException {
        File nbtFile = new File(localFilePath);
        if (!nbtFile.isFile()) {
            throw new IOException("CSC report file does not exist: " + localFilePath);
        }
        if (!nbtFile.getName().endsWith(".nbt")) {
            throw new IOException("CSC report accepts only .nbt files");
        }
        if (nbtFile.length() > MAX_FILE_BYTES) {
            throw new IOException("CSC report file exceeds " + MAX_FILE_BYTES + " bytes");
        }
        return nbtFile;
    }

    private static URI validateEndpoint() throws IOException {
        String configuredEndpoint = requireConfigValue("online.reportEndpoint", report_endpoint);
        URI endpoint;
        try {
            endpoint = URI.create(configuredEndpoint);
        } catch (IllegalArgumentException error) {
            throw new IOException("Invalid CSC report endpoint", error);
        }
        if (!"https".equalsIgnoreCase(endpoint.getScheme()) || endpoint.getHost() == null) {
            throw new IOException("CSC report endpoint must be an absolute HTTPS URL");
        }
        return endpoint;
    }

    private static String requireConfigValue(String key, String value) throws IOException {
        if (value == null || value.trim().isEmpty()) {
            throw new IOException("Missing required CSC configuration: " + key);
        }
        return value.trim();
    }

    private static String sha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        long size = 0;
        byte[] buffer = new byte[8192];
        try (InputStream input = new FileInputStream(file)) {
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                size += bytesRead;
                if (size > MAX_FILE_BYTES) {
                    throw new IOException("CSC report file exceeds " + MAX_FILE_BYTES + " bytes");
                }
                digest.update(buffer, 0, bytesRead);
            }
        }
        return toHex(digest.digest());
    }

    private static String hmacSha256(String secret, String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return toHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }

    private static String toHex(byte[] bytes) {
        StringBuilder value = new StringBuilder(bytes.length * 2);
        for (byte current : bytes) {
            value.append(Character.forDigit((current >>> 4) & 0xF, 16));
            value.append(Character.forDigit(current & 0xF, 16));
        }
        return value.toString();
    }

    private static void writeMultipartFile(DataOutputStream output, String boundary, File nbtFile) throws IOException {
        output.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        // Never disclose a player-provided blueprint filename to the report service.
        output.write("Content-Disposition: form-data; name=\"file\"; filename=\"sample.nbt\"\r\n"
                .getBytes(StandardCharsets.UTF_8));
        output.write("Content-Type: application/octet-stream\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        try (InputStream input = new FileInputStream(nbtFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        output.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    private static String readResponse(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    public static void AutoUpdateThread(String filepath) {
        try {
            uploadNbtFile(filepath);
        } catch (Exception ignored) {
            // Reporting is best effort and must never affect scan or shutdown progress.
        }
    }
}
