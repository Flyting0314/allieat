package com.backstage.backstageservice;


import com.backstage.backstagrepository.BackStageLoginDao;

import com.jwtUtil.JwtUtil;
import com.jwtUtil.TokenStorage;
import com.entity.AdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackStageLoginServiceImpl implements BackStageLoginService {
    @Autowired
    private BackStageLoginDao backStageLogin;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private TokenStorage tokenStorage;


    @Override
    public ResponseEntity<Map<String, Object>> findByAccount(AdminVO admin) {
        try {
            List<AdminVO> adminList = backStageLogin.findByAccount(admin);
            if (!adminList.isEmpty()) {
                AdminVO foundAdmin = adminList.get(0);  // 取出結果

                if (foundAdmin.getPassword() != null && foundAdmin.getPassword().equals(admin.getPassword())) {
                    //產生Token
                    String token = jwtUtil.generateToken(foundAdmin.getAccount());
                    //存Token
                    tokenStorage.saveToken(foundAdmin.getAccount(), token);
                    //回傳值設定
                    Map<String,Object> result = createResult("login ok","/backstage_homepage.html");
                    result.put("token", token);
                    return ResponseEntity.status(HttpStatus.OK).body(result);  // 密碼正確
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResult("login failed password is incorrect",null));  // 密碼錯誤
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResult("account does not exist",null));  // 帳號不存在
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResult("服務異常請重試",null));
        }
    }

    //用於回傳狀態創建
    private Map<String, Object> createResult(String message,String uri) {
    	Map<String, Object> result = new HashMap<>();
        result.put("loginState", message);
        result.put("redirectUrl", uri);
        return result;
    }
}
