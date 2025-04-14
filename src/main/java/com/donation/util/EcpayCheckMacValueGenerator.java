package com.donation.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class EcpayCheckMacValueGenerator {

    public static String generate(Map<String, String> params, String hashKey, String hashIV) {
        try {
            // 1. 排序參數
            Map<String, String> sortedParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            sortedParams.putAll(params);

            // 2. 組合字串：HashKey=...&key1=value1&...&HashIV=...
            StringBuilder sb = new StringBuilder("HashKey=").append(hashKey);
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
            sb.append("&HashIV=").append(hashIV);

            // 3. URL encode，並轉小寫（綠界規範）
            String encoded = URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8.name())

                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%2A", "*")
                    .replaceAll("\\%20", "+")
                    .replaceAll("\\%5F", "_")
                    .replaceAll("\\%2D", "-")
                    .replaceAll("\\%2E", ".")
                    .replaceAll("\\%7E", "~");

            // 4. SHA256 加密
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(encoded.getBytes(StandardCharsets.UTF_8));
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
