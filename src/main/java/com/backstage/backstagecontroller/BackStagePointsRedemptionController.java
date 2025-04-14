package com.backstage.backstagecontroller;

import com.store.model.StoreService;
import com.backstage.backstageservice.BackStagePointsRedemptionService;
import com.backstage.backstagrepository.PointsRedemptionRepository;
import com.entity.PointsRedemptionVO;
import com.entity.StoreVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
 
=======

>>>>>>> b791394 (1. 新增實體 ”店家點數核銷 PointsRedemption” 相關檔案：VO, Repository, Service,)

@RestController
@RequestMapping("/pointsRedemption")
public class BackStagePointsRedemptionController {
	
    @Autowired
    private BackStagePointsRedemptionService pointsRedemptionService;
    
    @Autowired
    private PointsRedemptionRepository pointsRedemptionRepository;

    @Autowired
    private StoreService storeService;
    
    
    
    //===========點數核銷後台頁面載入資料：使用service中的初始化功能===============
 
 // 1(A). 初始化 + 回傳當月紀錄（有點數 > 0 的餐廳，如果尚未建立就新增）
    @GetMapping("/current")
    public List<Map<String, Object>> getCurrentMonthRedemptions() {
        // 1. 更新當月未核銷記錄的點數 (確保點數是最新的)
        pointsRedemptionService.updateCurrentMonthPointsAmount();
        
        // 2. 獲取初始化的當月記錄 (這會確保所有應該有記錄的店家都有記錄)
        List<PointsRedemptionVO> redemptions = pointsRedemptionService.initializeCurrentMonthRedemptions();
        
        System.out.println("當月記錄總數: " + redemptions.size());
        return redemptions.stream().map(this::formatRedemption).collect(Collectors.toList());
    }

    
    
  //============== 前台餐廳方看到的點數核銷頁面載入資料：使用service中的初始化功能 ===============
    // 1(B). 初始化 + 回傳當月紀錄
    @GetMapping("/frontend/current/{storeId}")
    public List<Map<String, Object>> getRedemptionsForStore(@PathVariable Integer storeId) {
        // 1. 先初始化當月記錄（確保當月記錄存在）
        pointsRedemptionService.initializeCurrentMonthRedemptions();
        
        // 2. 獲取該店家的所有點數核銷記錄（所有月份）
        List<PointsRedemptionVO> allRecords = pointsRedemptionRepository.findByStore_StoreId(storeId);
        
        // 3. 格式化並按月份降序排序
        return allRecords.stream()
            .map(this::formatRedemption)
            .sorted((a, b) -> {
                // 按照 redemptionMonth 降序排序（最新的月份在前）
                Date dateA = (Date) a.get("redemptionMonth");
                Date dateB = (Date) b.get("redemptionMonth");
                return dateB.compareTo(dateA);
            })
            .collect(Collectors.toList());
    }

        
    
    
    
    // 2. 切換月份查詢紀錄（例如使用者點選 2025-03）
    //確保返回所有記錄，包括已核銷的
    @GetMapping("/byMonth")
    public List<Map<String, Object>> getByMonth(@RequestParam String month) {
        Date targetMonth = Date.valueOf(month + "-01");
        List<PointsRedemptionVO> allRecords = pointsRedemptionService.getRedemptionsByMonth(targetMonth);
        
        System.out.println("月份：" + month + " 找到記錄數：" + allRecords.size());
        
        return allRecords.stream()
                .map(this::formatRedemption)
                .collect(Collectors.toList());
    }

    
    
    
    // 3. 更新某筆紀錄的核銷金額（點數），同時設定 status = 1（表示已核銷）
 // 修改 updateRedemption 方法，將核銷金額小於應核銷點數的情況標記為狀態2（核銷異常）
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateRedemption(
            @PathVariable Integer id,
            @RequestBody PointsRedemptionVO updated) {

        Map<String, Object> response = new HashMap<>();

        try {
            PointsRedemptionVO existing = pointsRedemptionService.getRedemptionById(id);
            if (existing == null) {
                response.put("message", "更新失敗：找不到該筆核銷記錄");
                return ResponseEntity.badRequest().body(response);
            }

            int inputCashAmount = updated.getCashAmount();
            int currentPointsAmount = existing.getPointsAmount() != null ? existing.getPointsAmount() : 0;
            int previousCashAmount = existing.getCashAmount() != null ? existing.getCashAmount() : 0;
            
            if (inputCashAmount <= 0) {
                response.put("message", "撥款金額必須大於 0");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 確保核銷金額不超過剩餘點數
            if (inputCashAmount > currentPointsAmount) {
                response.put("message", "核銷金額不能超過剩餘點數");
                return ResponseEntity.badRequest().body(response);
            }

            // 計算本次實際新增的核銷金額
            int additionalCashAmount = inputCashAmount;
            
            // 將店家點數更新操作分離到獨立事務
            try {
                if (additionalCashAmount > 0) {
                    updateStorePoints(existing.getStoreId(), additionalCashAmount);
                }
            } catch (Exception e) {
                System.err.println("更新店家點數時發生錯誤: " + e.getMessage());
                response.put("warning", "核銷成功但無法更新店家點數餘額: " + e.getMessage());
            }

            // 更新已撥款金額 - 累加到之前核銷金額上
            existing.setCashAmount(previousCashAmount + inputCashAmount);
            
            // 更新點數餘額 - 減去本次核銷金額
            existing.setPointsAmount(currentPointsAmount - inputCashAmount);

            // 設定狀態 - 根據是否為部分核銷決定狀態
            // 如果核銷後剩餘點數 > 0，標記為狀態2（核銷異常）
            if (currentPointsAmount - inputCashAmount > 0) {
                existing.setStatus(2); // 核銷異常
            } else {
                existing.setStatus(1); // 正常核銷（完全核銷）
            }
            
            // 首次核銷時設定核銷時間
            if (existing.getPaymentTime() == null) {
                existing.setPaymentTime(new Timestamp(System.currentTimeMillis()));
            }

            // 更新資料庫
            pointsRedemptionService.updateRedemption(existing);

            response.put("message", "核銷更新成功");
            response.put("data", formatRedemption(existing));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "更新失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    
 // 單獨處理店家點數更新，使用新的事務
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void updateStorePoints(Integer storeId, int pointsToDeduct) {
        try {
            StoreVO store = storeService.getStoreById(storeId);
            if (store != null) {
                int storePoints = store.getPoints();
                store.setPoints(Math.max(storePoints - pointsToDeduct, 0));
                storeService.updateStore(store);
            }
        } catch (Exception e) {
            throw e; // 繼續拋出異常以便調用方處理
        }
    }


    
    //=========== 批次/ 多筆核銷功能 ==============
    @PostMapping("/batchRedemption")
    @Transactional
    public ResponseEntity<Map<String, Object>> batchRedemption(@RequestBody List<Integer> redemptionIds) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> processedRecords = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (Integer redemptionId : redemptionIds) {
            try {
                PointsRedemptionVO existing = pointsRedemptionService.getRedemptionById(redemptionId);
                
                if (existing == null || existing.getStatus() != 0) {
                    // 跳過已核銷或不存在的記錄
                    failCount++;
                    continue;
                }

                // 使用現有的單筆核銷邏輯
                int pointsAmount = existing.getPointsAmount();
                
                // 更新店家點數
                updateStorePoints(existing.getStoreId(), pointsAmount);

                existing.setCashAmount(pointsAmount);
                existing.setPointsAmount(0);
                existing.setStatus(1);
                existing.setPaymentTime(new Timestamp(System.currentTimeMillis()));

                pointsRedemptionService.updateRedemption(existing);
                
                processedRecords.add(formatRedemption(existing));
                successCount++;

            } catch (Exception e) {
                failCount++;
                // 可以記錄錯誤日誌
                System.err.println("批次核銷 ID " + redemptionId + " 失敗：" + e.getMessage());
            }
        }

        response.put("successCount", successCount);
        response.put("failCount", failCount);
        response.put("processedRecords", processedRecords);
        response.put("message", "批次核銷完成");

        return ResponseEntity.ok(response);
    }
    
    
    
    
    
    
    
 // 根據ID獲取單筆核銷記錄
    @GetMapping("/getById/{id}")
    public ResponseEntity<Map<String, Object>> getRedemptionById(@PathVariable Integer id) {
        try {
            PointsRedemptionVO redemption = pointsRedemptionService.getRedemptionById(id);
            if (redemption == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> formattedData = formatRedemption(redemption);
            
            // 確保返回的是有效的日期格式 - 用毫秒時間戳
            if (redemption.getPaymentTime() != null) {
                formattedData.put("paymentTime", redemption.getPaymentTime().getTime()); 
            }
            
            return ResponseEntity.ok(formattedData);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "獲取核銷記錄失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    
    
    
 // 搜尋 API，支援複合查詢
 // 添加輸入驗證，將錯誤訊息返回前端
    
    @GetMapping("/search")
    public ResponseEntity<?> searchPointsRedemptions(
        @RequestParam(required = false) String storeId,
        @RequestParam(required = false) String storeName,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String month  // 年月參數
    ) {
        Map<String, String> errors = new HashMap<>();
        
        // 驗證店家ID
        Integer storeIdInt = null;
        if (storeId != null && !storeId.trim().isEmpty()) {
            try {
                storeIdInt = Integer.parseInt(storeId.trim());
                if (storeIdInt <= 0) {
                    errors.put("storeId", "店家ID必須大於0");
                }
            } catch (NumberFormatException e) {
                errors.put("storeId", "店家ID必須為整數");
            }
        }
        
        // 驗證店家名稱
        String storeNameTrim = null;
        if (storeName != null && !storeName.trim().isEmpty()) {
            storeNameTrim = storeName.trim();
            if (!storeNameTrim.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s]+$")) {
                errors.put("storeName", "店家名稱只能包含中文、英文字母、數字和空格");
            }
        }
        
        // 驗證狀態
        Integer statusInt = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusInt = Integer.parseInt(status.trim());
                if (statusInt < 0 || statusInt > 2) {
                    errors.put("status", "狀態值無效，必須是0、1或2");
                }
            } catch (NumberFormatException e) {
                errors.put("status", "狀態必須為整數");
            }
        }
        
        // 驗證年月格式
        Date startDateObj = null;
        Date endDateObj = null;
        if (month != null && !month.trim().isEmpty()) {
            try {
                // 驗證年月格式 (YYYY-MM)
                if (!month.trim().matches("^\\d{4}-\\d{2}$")) {
                    errors.put("month", "年月格式無效，應為YYYY-MM");
                } else {
                    // 將月份轉換為該月第一天
                    startDateObj = Date.valueOf(month.trim() + "-01");
                    
                    // 計算該月最後一天
                    LocalDate localDate = startDateObj.toLocalDate();
                    LocalDate lastDayOfMonth = localDate.withDayOfMonth(localDate.lengthOfMonth());
                    endDateObj = Date.valueOf(lastDayOfMonth);
                }
            } catch (IllegalArgumentException e) {
                errors.put("month", "無效的年月格式");
            }
        }
        
        // 如果有驗證錯誤，立即返回
        if (!errors.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // 使用轉換後的日期範圍進行搜尋
            List<PointsRedemptionVO> results = pointsRedemptionService.searchRedemptions(
                storeIdInt, storeNameTrim, statusInt, startDateObj, endDateObj);
            
            // 轉換結果為前端格式
            List<Map<String, Object>> formattedResults = results.stream()
                .map(this::formatRedemption)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", formattedResults);
            response.put("count", formattedResults.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "搜尋過程中發生錯誤: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
     
    
    
    //============= 修復顯示異常的歷史紀錄(開發測試用) =================
    //在瀏覽器直接訪問即可調用，或者在頁面加入一個測試按鈕
    
 // 修改 fixHistoricalRecords 方法，將部分核銷的記錄標記為狀態2
    @GetMapping("/fixHistoricalRecords")
    @Transactional
    public ResponseEntity<Map<String, Object>> fixHistoricalRecords() {
        Map<String, Object> response = new HashMap<>();
        int updatedCount = 0;
        
        try {
            List<PointsRedemptionVO> allRecords = pointsRedemptionService.getAllPointsRedemptions();
            
            for (PointsRedemptionVO record : allRecords) {
                boolean isUpdated = false;
                
                // 已核銷但還有剩餘點數的情況 - 標記為異常核銷(狀態2)
                if (record.getStatus() == 1 && record.getPointsAmount() != null && record.getPointsAmount() > 0 
                    && record.getCashAmount() != null && record.getCashAmount() > 0) {
                    record.setStatus(2); // 設為核銷異常
                    isUpdated = true;
                }
                
                // 確保核銷時間存在
                if ((record.getStatus() == 1 || record.getStatus() == 2) && record.getPaymentTime() == null) {
                    record.setPaymentTime(new Timestamp(System.currentTimeMillis()));
                    isUpdated = true;
                }
                
                // 未核銷但有核銷金額的異常情況
                if (record.getStatus() == 0 && record.getCashAmount() != null && record.getCashAmount() > 0) {
                    // 判斷是完全核銷還是部分核銷
                    if (record.getPointsAmount() > 0) {
                        record.setStatus(2); // 部分核銷，標記為異常
                    } else {
                        record.setStatus(1); // 完全核銷
                    }
                    record.setPaymentTime(new Timestamp(System.currentTimeMillis()));
                    isUpdated = true;
                }
                
                // 已核銷但無核銷金額的異常情況
                if ((record.getStatus() == 1 || record.getStatus() == 2) && 
                    (record.getCashAmount() == null || record.getCashAmount() == 0)) {
                    // 如果有原始點數記錄，設置核銷金額等於原始點數
                    int originalPoints = record.getPointsAmount() != null ? record.getPointsAmount() : 0;
                    record.setCashAmount(originalPoints);
                    record.setPointsAmount(0);
                    record.setStatus(1); // 設為正常核銷
                    isUpdated = true;
                }
                
                if (isUpdated) {
                    pointsRedemptionService.updateRedemption(record);
                    updatedCount++;
                }
            }
            
            response.put("message", "成功修復 " + updatedCount + " 筆歷史資料");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "修復失敗：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    
    
    
    
    
    //============測試開發用按鈕：重置當月所有紀錄之餐廳點數與核銷狀態=================
    
    @GetMapping("/resetCurrentMonthRecords")
    @Transactional
    public ResponseEntity<Map<String, Object>> resetCurrentMonthRecords() {
        Map<String, Object> response = new HashMap<>();
        int resetCount = 0;

        try {
            // 獲取當前月份
            LocalDate now = LocalDate.now();
            Date currentMonth = Date.valueOf(now.withDayOfMonth(1));

            // 查找當前月份的所有點數核銷記錄
            List<PointsRedemptionVO> currentMonthRecords = 
                pointsRedemptionRepository.findByRedemptionMonthBetween(
                    currentMonth, 
                    Date.valueOf(now.withDayOfMonth(now.lengthOfMonth()))
                );

            for (PointsRedemptionVO record : currentMonthRecords) {
                // 只處理已核銷的記錄
                if (record.getStatus() > 0 && record.getCashAmount() != null) {
                    // 保存已核銷金額，用於恢復點數
                    int cashAmountToRestore = record.getCashAmount();
                    
                    // 重置記錄狀態
                    record.setStatus(0);  // 未核銷
                    record.setPointsAmount(record.getPointsAmount() + cashAmountToRestore); // 恢復原始點數
                    record.setCashAmount(null);  // 清除已核銷金額
                    record.setPaymentTime(null);  // 清除核銷時間

                    // 嘗試恢復店家點數
                    StoreVO store = record.getStore();
                    if (store != null && cashAmountToRestore > 0) {
                        // 使用已核銷的金額來恢復點數
                        storeService.forceRestoreStorePoints(store.getStoreId(), cashAmountToRestore);
                    }

                    pointsRedemptionRepository.save(record);
                    resetCount++;
                }
            }

            response.put("message", "成功重置 " + resetCount + " 筆當月記錄");
            response.put("resetCount", resetCount);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "重置失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    
    
    
    
    
    
    
    // ======== 這個方法的目的是把 PointsRedemptionVO 轉成前端要用的格式（通常是 Map） ===========
    private Map<String, Object> formatRedemption(PointsRedemptionVO vo) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", vo.getRedemptionId());
        map.put("storeId", vo.getStoreId());
        map.put("pointsAmount", vo.getPointsAmount());
        map.put("cashAmount", vo.getCashAmount());
        map.put("redemptionMonth", vo.getRedemptionMonth());
        map.put("status", vo.getStatus());
        
        // 添加支付時間，轉換為時間戳
        if (vo.getPaymentTime() != null) {
            map.put("paymentTime", vo.getPaymentTime().getTime());
        }
        
        // 添加創建時間
        if (vo.getCreatedTime() != null) {
            map.put("createdTime", vo.getCreatedTime().getTime());
        }

        StoreVO store = storeService.getStoreById(vo.getStoreId());
        if (store != null) {
            map.put("storeName", store.getName());
        }

        return map;
    }
}

