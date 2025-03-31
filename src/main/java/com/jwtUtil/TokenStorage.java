package com.jwtUtil;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStorage {
    //共用Map，需要多執行續安全。
    private final Map<String, String> userTokenMap = new ConcurrentHashMap<>();
    //儲存發出去的Token
    public void saveToken(String username, String token) {
        userTokenMap.put(username, token);
    }
    //比對Token是否是發出去的那一個
    public boolean isTokenValid(String username, String token) {
        return token.equals(userTokenMap.get(username));
    }
}
