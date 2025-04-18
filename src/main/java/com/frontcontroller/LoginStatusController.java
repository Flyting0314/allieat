package com.frontcontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
public class LoginStatusController {

    @GetMapping("/registerAndLogin/api/login-status")
    public Map<String, Boolean> getLoginStatus(HttpSession session) {
        Map<String, Boolean> status = new HashMap<>();
        status.put("isMember", session.getAttribute("member") != null);
        status.put("isStore", session.getAttribute("store") != null);
        return status;
    }
}
