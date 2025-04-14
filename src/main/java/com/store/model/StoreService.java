package com.store.model;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backstage.backstageevent.RedemptionStorePointsUpdatedEvent;
import com.backstage.backstageservice.BackStagePointsRedemptionService;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.StoreVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;


import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    


    // 取得指定 ID 的餐廳
    public StoreVO getStoreById(Integer storeId) {
        return storeRepository.findById(storeId).orElse(null);
    }

    // 取得所有餐廳
    public List<StoreVO> getAllStores() {
        return storeRepository.findAll();
    }
    
    // 取得點數大於 0 的餐廳
    public List<StoreVO> getStoresWithPointsGreaterThanZero() {
        return storeRepository.findByPointsGreaterThan(0);
    }
    
    //更新store
    public void updateStore(StoreVO store) {
        storeRepository.save(store);
    }

    
    //新增方法：根據店家名稱搜尋店家
    public List<StoreVO> searchStoresByName(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StoreVO> query = cb.createQuery(StoreVO.class);
        Root<StoreVO> root = query.from(StoreVO.class);
        
        // 使用 like 查詢匹配店家名稱
        Predicate namePredicate = cb.like(
            cb.lower(root.get("name")), 
            "%" + name.toLowerCase() + "%"
        );
        
        query.where(namePredicate);
        return entityManager.createQuery(query).getResultList();
    }
    
    
    
    
    //=====後台餐廳點數核銷用：開發測試用按鈕，重置當月發放紀錄與餐廳點數=========
    @Transactional
    public void restoreStorePoints(Integer storeId, Integer pointsToRestore) {
        StoreVO store = getStoreById(storeId);
        if (store != null) {
            // 將點數加回去
            store.setPoints(store.getPoints() + pointsToRestore);
            updateStore(store);
        }
    }
    
    
    //=====後台餐廳點數核銷用：開發測試用按鈕，重置當月發放紀錄與餐廳點數=========
    @Transactional
    public void forceRestoreStorePoints(Integer storeId, Integer pointsToRestore) {
        try {
            // 直接使用原生 SQL 或 JPQL 更新點數
            entityManager.createQuery(
                "UPDATE StoreVO s SET s.points = s.points + :points WHERE s.storeId = :storeId"
            )
            .setParameter("points", pointsToRestore)
            .setParameter("storeId", storeId)
            .executeUpdate();
        } catch (Exception e) {
            // 記錄錯誤，但不拋出異常
            System.err.println("恢復店家點數時出錯: " + e.getMessage());
        }
    }
    
    
    
 // =========後台餐廳點數核銷用：每次刷新頁面都取得餐廳點數的最新資料============
    //防止循環依賴，調用事件監聽器 RedemptionStorePointsUpdatedEvent
    @Transactional
    public void updateStorePoints(Integer storeId, Integer newPoints) {
        StoreVO store = getStoreById(storeId);
        if (store != null) {
            store.setPoints(newPoints);
            updateStore(store);
            
            // 發布事件而不是直接調用另一個服務
            eventPublisher.publishEvent(new RedemptionStorePointsUpdatedEvent(this, storeId, newPoints));
        }
    }
    
}
