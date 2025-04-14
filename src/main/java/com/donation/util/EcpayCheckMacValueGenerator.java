package com.donation.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class EcpayCheckMacValueGenerator {

    public static String generate(Map<String, String> params, String hashKey, String hashIV) {
        try {
            // 1. 將參數按字母排序（不分大小寫）
            Map<String, String> sortedParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            sortedParams.putAll(params);

            // 2. 組成格式：HashKey=...&key1=value1&...&HashIV=...
            StringBuilder sb = new StringBuilder();
            sb.append("HashKey=").append(hashKey);
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
            sb.append("&HashIV=").append(hashIV);

            // 3. URL encode（符合綠界規範）
            String urlEncoded = URLEncoder.encode(sb.toString(), "UTF-8")
                .toLowerCase()
                .replace("+", "%20")
                .replace("%21", "!")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%2a", "*")
                .replace("%2d", "-")
                .replace("%2e", ".")
                .replace("%5f", "_")
                .replace("%7e", "~");

            // 4. SHA256 加密
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(urlEncoded.getBytes(StandardCharsets.UTF_8));

            // 5. 轉換為十六進位大寫
            StringBuilder hash = new StringBuilder();
            for (byte b : digest) {
                hash.append(String.format("%02X", b));
            }

            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("CheckMacValue 產生失敗", e);
        }
    }
}