package com.donation.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class EcpayCheckMacValueGenerator {

    public static String generate(Map<String, String> params, String hashKey, String hashIV) {
        try {
            // Step 1：ASCII 排序（不分大小寫）
            Map<String, String> sortedParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            sortedParams.putAll(params);

            // Step 2：組合原始字串
            String raw = "HashKey=" + hashKey + "&" +
                    sortedParams.entrySet().stream()
                            .map(e -> e.getKey() + "=" + e.getValue())
                            .collect(Collectors.joining("&")) +
                    "&HashIV=" + hashIV;

            // Step 3：URL encode + 小寫 + 特殊字元處理（照綠界 SHA256 規定）
            String urlEncoded = URLEncoder.encode(raw, StandardCharsets.UTF_8.name())
                    .toLowerCase()
                    .replaceAll("%21", "!")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%2a", "*")
                    .replaceAll("%2d", "-")
                    .replaceAll("%2e", ".")
                    .replaceAll("%5f", "_");

            // Step 4：SHA256 加密
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] digest = sha.digest(urlEncoded.getBytes(StandardCharsets.UTF_8));

            // Step 5：轉成大寫的十六進位字串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02X", b));
            }

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("CheckMacValue 產生失敗", e);
        }
    }
}