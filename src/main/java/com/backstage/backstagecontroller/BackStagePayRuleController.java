package com.backstage.backstagecontroller;


import com.backstage.backstagedto.PayRuleDTO;
import com.backstage.backstageservice.BackStagePayRuleService;
import com.entity.PayRuleVO;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("/payrule")
public class BackStagePayRuleController {

    @Autowired
    private BackStagePayRuleService payRuleService;

  
    //=============設定排程規則=================
    @PostMapping("/setup_payrule")
    public ResponseEntity<Map<String, Object>> setupPayRule(
            @Valid @RequestBody PayRuleDTO payRuleDTO,
            BindingResult bindingResult) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 檢查是否有驗證錯誤
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
            
            response.put("status", "error");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // 使用 addOrUpdatePayRule 方法
            PayRuleVO savedPayRule = payRuleService.addOrUpdatePayRule(payRuleDTO.toVO());
            
            // 根據新/更新的規則生成 PayRecord
            payRuleService.generatePayRecordFromRule();
            
            response.put("status", "success");
            response.put("payRule", savedPayRule);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 詳細的錯誤日誌
            System.err.println("設定排程規則時發生錯誤：");
            e.printStackTrace();
            
            response.put("status", "error");
            response.put("message", "設定排程規則時發生錯誤：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    
    
    // ========= 取得目前的 PayRule ===========
    @GetMapping("/current_payrule")
    public ResponseEntity<Map<String, Object>> getCurrentPayRule() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            PayRuleVO currentPayRule = payRuleService.getPayRule();
            
            if (currentPayRule != null) {
                response.put("status", "success");
                response.put("payRule", currentPayRule);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "尚未設定排程規則");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "取得排程規則時發生錯誤：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    
    // ============= 根據當前 PayRule 產生 PayRecord ==============
    @PostMapping("/generate_payrecord")
    public ResponseEntity<Map<String, Object>> generatePayRecord() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            payRuleService.generatePayRecordFromRule();
            
            response.put("status", "success");
            response.put("message", "成功根據排程規則產生發放紀錄");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}