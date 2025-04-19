package com.backstage.backstagecontroller;
 
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.backstage.backstagedto.PayRecordUpdateDTO;
import com.backstage.backstageservice.BackStagePayDetailService;
import com.backstage.backstageservice.BackStagePayRecordService;
import com.entity.PayRecordVO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@CrossOrigin(origins = "*") 
@RestController  
@RequestMapping("/payrecord")
public class BackStagePayRecordController {

    @Autowired
    private BackStagePayRecordService payRecordService;

    @Autowired
    private BackStagePayDetailService payDetailService;
 
    @GetMapping
    public String payRecord(Model model) {
    	
    	return("payrecordmaking");
    }
   
  //============ list all ====================
    @GetMapping("/listAll")
    public ResponseEntity<List<PayRecordVO>> getAllPayRecords(
        @RequestParam(required = false) Integer payoutId,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<PayRecordVO> records;
        
        // 當沒有任何篩選參數時，返回所有資料並預設排序
        if (payoutId == null && status == null && startDate == null && endDate == null) {
            records = payRecordService.getAllPayRecordsDesc();
        } else {
            // 有篩選參數時，執行動態查詢
            records = payRecordService.findByDynamicCriteria(payoutId, status, startDate, endDate);
        }
        
        return ResponseEntity.ok(records);
    }
      
    
    
    //========= 人工更新payrecord：修改發放時間、點數等 ==========
    @PutMapping("/update/{payoutId}")
    public ResponseEntity<Map<String, String>> updatePayRecord(
            @PathVariable String payoutId,
            @Valid @RequestBody PayRecordUpdateDTO request,
            BindingResult bindingResult) {
        
        // 創建響應映射，用於存儲操作結果
        Map<String, String> response = new HashMap<>();
        
        // 檢查是否有 Bean Validation 驗證錯誤
        // 這些錯誤來自 DTO 上的驗證註解，如 @NotNull, @Min 等
        if (bindingResult.hasErrors()) {
            // 收集所有驗證錯誤訊息
            List<String> errors = bindingResult.getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
            
            // 設置錯誤狀態和錯誤訊息
            response.put("status", "error");
            response.put("message", String.join(", ", errors));
            
            // 返回 400 壞請求響應
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 將路徑變數 payoutId 轉換為整數
            Integer payoutIdInt = Integer.parseInt(payoutId);

            // 調用服務層方法更新記錄
            boolean updated = payRecordService.updatePayRecord(payoutIdInt, request);
            
            if (updated) {
                // 更新成功
                response.put("status", "success");
                return ResponseEntity.ok(response);
            } else {
                // 更新失敗（可能是找不到記錄）
                response.put("status", "error");
                response.put("message", "更新失敗");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (IllegalArgumentException e) {
            // 捕捉來自 Service 層的特定錯誤
            // 這可能包括時間驗證、點數驗證等業務邏輯錯誤
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    
    
   //=============== 人工手動對單一筆 payRecord 執行發放（無視排程時間）===============
    
    @PostMapping("/execute/{payoutId}")
    public ResponseEntity<Map<String, String>> executePayRecord(@PathVariable Integer payoutId) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean executed = payRecordService.executePayRecord(payoutId);
            if (executed) {
                response.put("status", "success");
                response.put("message", "點數已成功發放");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "發放失敗");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    
    
    
  //=============== 獲取平台資金狀態 ===============
    @GetMapping("/platform-funds")
    public ResponseEntity<Map<String, Object>> getPlatformFunds() {
        // 獲取平台資金信息
        Integer availableFunds = payRecordService.calculateAvailableFunds();
        
        Map<String, Object> response = new HashMap<>();
        response.put("availableFunds", availableFunds);
        
        return ResponseEntity.ok(response);
    }
    
    
    
    //=============== 獲取啟用狀態會員數量 ===============
    @GetMapping("/active-member-count")
    public ResponseEntity<Map<String, Integer>> getActiveMemberCount() {
        // 獲取啟用狀態的會員數量
        Integer count = payRecordService.countActiveMember();
        
        Map<String, Integer> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }
    
}
