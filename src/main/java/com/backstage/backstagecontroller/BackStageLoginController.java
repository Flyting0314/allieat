package com.backstage.backstagecontroller;




import com.backstage.backstagedto.AdminDTO;
import com.backstage.backstageservice.BackStageLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/backStage")
public class BackStageLoginController {

    @Autowired
    private BackStageLoginService backStageLoginService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AdminDTO admin) {
    	Map<String, Object> result=backStageLoginService.findByAccount(admin);
    	String check =(String) result.get("loginState");
    	if(check.equals("account does not exist")) {
      		 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);//帳號不存在的回傳
      	}
    	if(check.equals("login failed password is incorrect")) {
    		 return ResponseEntity.ok(result);//密碼錯誤的回傳
    	}
        return ResponseEntity.ok(result);//正確回傳
    }
    
    //錯誤處理
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleLocalException(Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("loginState", "服務異常請重試");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

