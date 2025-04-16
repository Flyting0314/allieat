package com.backstage.backstageservice;
import com.entity.PointsRedemptionVO;
import com.entity.StoreVO;
import com.store.model.StoreService;
import com.backstage.backstageevent.RedemptionStorePointsUpdatedEvent;
import com.backstage.backstagrepository.PointsRedemptionRepository;
import com.backstage.backstagrepository.StoreRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class BackStagePointsRedemptionService {

    @Autowired
    private PointsRedemptionRepository pointsRedemptionRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private StoreService storeService;

    // 新增點數兌換記錄
    @Transactional
    public PointsRedemptionVO addPointsRedemption(PointsRedemptionVO pointsRedemptionVO) {
        return pointsRedemptionRepository.save(pointsRedemptionVO);
    }

    // 單筆查詢
    public PointsRedemptionVO getPointsRedemptionById(Integer redemptionId) {
        return pointsRedemptionRepository.findById(redemptionId).orElse(null);
    }

    // 查詢全部
    public List<PointsRedemptionVO> getAllPointsRedemptions() {
        return pointsRedemptionRepository.findAll();
    }

    // 根據狀態查詢點數兌換記錄
    public List<PointsRedemptionVO> getPointsRedemptionsByStatus(Integer status) {
        return pointsRedemptionRepository.findByStatus(status);
    }

    // 根據商店查詢點數兌換記錄
    public List<PointsRedemptionVO> getPointsRedemptionsByStore(StoreVO store) {
        return pointsRedemptionRepository.findByStore(store);
    }
    
    
 // 1. 根據月份查詢所有的 Redemption 記錄

 // 修改 getRedemptionsByMonth 方法，確保返回所有記錄
 public List<PointsRedemptionVO> getRedemptionsByMonth(Date month) {
     // 獲取目標月份的第一天
     Calendar cal = Calendar.getInstance();
     cal.setTime(month);
     cal.set(Calendar.DAY_OF_MONTH, 1);
     Date firstDay = new Date(cal.getTimeInMillis());
     
     // 獲取目標月份的最後一天
     cal.add(Calendar.MONTH, 1);
     cal.add(Calendar.DAY_OF_MONTH, -1);
     Date lastDay = new Date(cal.getTimeInMillis());
     
     // 查詢這個月份範圍內的所有記錄，不分狀態
     List<PointsRedemptionVO> records = pointsRedemptionRepository.findByRedemptionMonthBetween(firstDay, lastDay);
     System.out.println("找到的記錄數量：" + records.size());
     
     return records;
 }

    // 2. 根據 ID 查詢 Redemption
    public PointsRedemptionVO getRedemptionById(Integer id) {
        return pointsRedemptionRepository.findById(id).orElse(null);
    }

    // 3. 檢查某餐廳是否已經在指定的月份有 Redemption 記錄
    public boolean hasRedemptionThisMonth(Integer storeId, Date month) {
        return pointsRedemptionRepository.existsByStore_StoreIdAndRedemptionMonth(storeId, month);
    }

    // 4. 新增 Redemption 記錄
    public void addRedemption(PointsRedemptionVO redemption) {
    	pointsRedemptionRepository.save(redemption);
    }

    // 5. 更新 Redemption 記錄
    // 更新記錄時避免重置點數
    @Transactional
    public void updateRedemption(PointsRedemptionVO redemption) {
        try {
            // 使用 merge 方法而非 save，可能有助於減少驗證錯誤
            PointsRedemptionVO existing = pointsRedemptionRepository.findById(redemption.getRedemptionId()).orElse(null);
            if (existing == null) {
                throw new RuntimeException("找不到要更新的記錄");
            }
            
            // 只更新核銷相關欄位
            existing.setCashAmount(redemption.getCashAmount());
            existing.setPointsAmount(redemption.getPointsAmount());
            existing.setStatus(redemption.getStatus());
            
            if (redemption.getPaymentTime() != null) {
                existing.setPaymentTime(redemption.getPaymentTime());
            }
            
            pointsRedemptionRepository.save(existing);
        } catch (Exception e) {
            throw new RuntimeException("更新記錄失敗: " + e.getMessage(), e);
        }
    }

    // 6. 根據餐廳和月份查詢 Redemption 記錄
    public PointsRedemptionVO getRedemptionByStoreAndMonth(Integer storeId, Date month) {
        return pointsRedemptionRepository.findByStore_StoreIdAndRedemptionMonth(storeId, month);
    }
    
    

    // 複雜查詢：使用 Criteria API
    public List<PointsRedemptionVO> searchPointsRedemptions(StoreVO store, Date startDate, Date endDate, Integer status, Integer minPointsAmount, Integer maxPointsAmount) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PointsRedemptionVO> query = cb.createQuery(PointsRedemptionVO.class);
        Root<PointsRedemptionVO> root = query.from(PointsRedemptionVO.class);

        List<Predicate> predicates = new ArrayList<>();

        if (store != null) {
            predicates.add(cb.equal(root.get("store"), store));
        }

        if (startDate != null && endDate != null) {
            predicates.add(cb.between(root.get("redemptionMonth"), startDate, endDate));
        }

        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        if (minPointsAmount != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("pointsAmount"), minPointsAmount));
        }

        if (maxPointsAmount != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("pointsAmount"), maxPointsAmount));
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

    // 自動建立每月的點數兌換記錄
    @Transactional
    public List<PointsRedemptionVO> generateMonthlyRedemptions() {
        List<StoreVO> storesWithPoints = storeRepository.findByPointsGreaterThan(0);
        List<PointsRedemptionVO> newRedemptions = new ArrayList<>();

        java.sql.Date thisMonth = java.sql.Date.valueOf(LocalDate.now().withDayOfMonth(25));

        for (StoreVO store : storesWithPoints) {
            PointsRedemptionVO redemption = new PointsRedemptionVO();
            redemption.setStore(store);
            redemption.setPointsAmount(store.getPoints());
            redemption.setStatus(0); // 未發放
            redemption.setRedemptionMonth(thisMonth);
            redemption.setCreatedTime(new Timestamp(System.currentTimeMillis()));

            newRedemptions.add(pointsRedemptionRepository.save(redemption));
        }

        return newRedemptions;
    }

    // 人工核銷發放金錢
    @Transactional
    public PointsRedemptionVO manuallyPayout(Integer redemptionId) {
        Optional<PointsRedemptionVO> optional = pointsRedemptionRepository.findById(redemptionId);
        if (optional.isPresent()) {
            PointsRedemptionVO redemption = optional.get();
            if (redemption.getStatus() == 0) {
                redemption.setStatus(1);
                redemption.setPaymentTime(new Timestamp(System.currentTimeMillis()));
                redemption.setCashAmount(redemption.getPointsAmount());
                return pointsRedemptionRepository.save(redemption);
            }
        }
        return null;
    }
    
    
    

 // 複合搜尋功能
 // 支援核銷異常狀態
 // 支持模糊查詢店家名稱的搜尋方法
 // 直接使用資料庫中的狀態值
    
    public List<PointsRedemptionVO> searchRedemptions(
        Integer storeId, String storeName, Integer status, 
        Date startDate, Date endDate) {
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PointsRedemptionVO> query = cb.createQuery(PointsRedemptionVO.class);
        Root<PointsRedemptionVO> root = query.from(PointsRedemptionVO.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // 根據店家ID篩選
        if (storeId != null) {
            // 不使用 root.get("storeId")，而是使用關聯的 store 實體
            predicates.add(cb.equal(root.get("store").get("storeId"), storeId));
        }
        
        // 根據店家名稱進行模糊查詢
        if (storeName != null && !storeName.trim().isEmpty()) {
            // 使用關聯的 store 實體的 name 屬性
            predicates.add(cb.like(root.get("store").get("name"), "%" + storeName.trim() + "%"));
        }
        
        // 根據狀態篩選 - 直接使用資料庫中的狀態值
        if (status != null) {
            // 直接使用狀態值
            predicates.add(cb.equal(root.get("status"), status));
        }
        
        // 根據年月選擇器篩選：使用redemptionMonth欄位進行日期範圍過濾
        if (startDate != null && endDate != null) {
            // 直接使用redemptionMonth進行範圍查詢，不轉換為Timestamp
            predicates.add(cb.between(root.get("redemptionMonth"), startDate, endDate));
        }
        
        // 設定查詢條件
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        
        // 默認按照 redemptionId 降序排序
        query.orderBy(cb.desc(root.get("redemptionId")));
        
        try {
            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            // 如果查詢失敗，返回空列表
            return new ArrayList<>();
        }
    }
    
    
    
    //========= 使用一個前台和後台都能共同使用的初始化方法，讓載入和創建的資料不會重複生成 ==========
    
    public List<PointsRedemptionVO> initializeCurrentMonthRedemptions() {
        LocalDate now = LocalDate.now();
        Date thisMonth = Date.valueOf(now.withDayOfMonth(1));

        // 找出本月所有已存在的 pointsRedemption 記錄
        List<PointsRedemptionVO> existingRecords = getRedemptionsByMonth(thisMonth);
        Set<Integer> existingStoreIds = existingRecords.stream()
            .map(PointsRedemptionVO::getStoreId)
            .collect(Collectors.toSet());

        // 所有商家資料
        List<StoreVO> stores = storeService.getAllStores();
        List<PointsRedemptionVO> createdRecords = new ArrayList<>();

        for (StoreVO store : stores) {
            if (store.getPoints() > 0 && store.getReviewed() == 1 && store.getAccStat() == 1 && !existingStoreIds.contains(store.getStoreId())) {
                PointsRedemptionVO r = new PointsRedemptionVO();
                r.setStoreId(store.getStoreId());
                r.setPointsAmount(store.getPoints());
                r.setCashAmount(0);
                r.setRedemptionMonth(thisMonth);
                r.setStatus(0); // 未核銷
                r.setCreatedTime(new Timestamp(System.currentTimeMillis()));

                pointsRedemptionRepository.save(r);
                createdRecords.add(r);
            }
        }

        // 合併原本有的 + 新增的
        List<PointsRedemptionVO> result = new ArrayList<>(existingRecords);
        result.addAll(createdRecords);
        return result;
    }

    
    
    

    @Transactional
    public void updatePointsRedemptionForStore(Integer storeId, Integer newPoints) {
        LocalDate now = LocalDate.now();
        Date thisMonth = Date.valueOf(now.withDayOfMonth(1));
        
        PointsRedemptionVO record = pointsRedemptionRepository.findByStore_StoreIdAndRedemptionMonthAndStatus(
            storeId, thisMonth, 0); // 狀態0表示未核銷
        
        if (record != null) {
            record.setPointsAmount(newPoints);
            pointsRedemptionRepository.save(record);
        }
    }
    
    
//  ========= 更新餐廳當月未核銷記錄的點數金額，確保每次載入頁面時拿到的都是餐廳點數最新的資訊 =========
    @Transactional
    public void updateCurrentMonthPointsAmount() {
        LocalDate now = LocalDate.now();
        Date thisMonth = Date.valueOf(now.withDayOfMonth(1));
        
        // 獲取當月所有未核銷的記錄
        List<PointsRedemptionVO> currentRecords = pointsRedemptionRepository.findByRedemptionMonthAndStatus(
            thisMonth, 0); // 狀態0表示未核銷
        
        int updatedCount = 0;
        
        for (PointsRedemptionVO record : currentRecords) {
            // 獲取最新的店家點數
            StoreVO store = storeService.getStoreById(record.getStoreId());
            
            if (store != null && store.getPoints() > 0 && !store.getPoints().equals(record.getPointsAmount())) {
                // 只有當點數不同時才更新
                record.setPointsAmount(store.getPoints());
                pointsRedemptionRepository.save(record);
                updatedCount++;
            }
        }
        
        System.out.println("成功更新 " + updatedCount + " 筆未核銷記錄的點數");
    }
    
    
 // 添加事件監聽器方法
    @EventListener
    @Transactional
    public void handleStorePointsUpdated(RedemptionStorePointsUpdatedEvent event) {
        Integer storeId = event.getStoreId();
        Integer newPoints = event.getNewPoints();
        
        // 更新當月未核銷的點數核銷記錄
        LocalDate now = LocalDate.now();
        Date thisMonth = Date.valueOf(now.withDayOfMonth(1));
        
        PointsRedemptionVO record = pointsRedemptionRepository.findByStore_StoreIdAndRedemptionMonthAndStatus(
            storeId, thisMonth, 0); // 狀態0表示未核銷
        
        if (record != null) {
            record.setPointsAmount(newPoints);
            pointsRedemptionRepository.save(record);
            System.out.println("更新店家 " + storeId + " 的點數核銷記錄為 " + newPoints + " 點");
        }
    }
    
    
    
    
    
    
}
