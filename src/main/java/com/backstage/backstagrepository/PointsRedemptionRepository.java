package com.backstage.backstagrepository;
import com.entity.PointsRedemptionVO;
import com.entity.StoreVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PointsRedemptionRepository extends 
    JpaRepository<PointsRedemptionVO, Integer>, 
    JpaSpecificationExecutor<PointsRedemptionVO> {

    // 根據商店查詢點數兌換記錄
    List<PointsRedemptionVO> findByStore(StoreVO store);

    // 根據兌換月份查詢
    List<PointsRedemptionVO> findByRedemptionMonth(Date redemptionMonth);

    // 根據狀態查詢
    List<PointsRedemptionVO> findByStatus(Integer status);

    // 查詢特定日期範圍內的記錄
    List<PointsRedemptionVO> findByRedemptionMonthBetween(Date startDate, Date endDate);

    // 根據商店和狀態查詢
    List<PointsRedemptionVO> findByStoreAndStatus(StoreVO store, Integer status);

    // 按 redemptionId 降序排序
    List<PointsRedemptionVO> findAllByOrderByRedemptionIdDesc();   
    
    //按月分查詢該月紀錄
    boolean existsByStore_StoreIdAndRedemptionMonth(Integer storeId, Date redemptionMonth);
    //按月分查詢該月紀錄
    PointsRedemptionVO findByStore_StoreIdAndRedemptionMonth(Integer storeId, Date redemptionMonth);
    
    // 根據 storeId 查詢所有點數核銷記錄，不限月份
    List<PointsRedemptionVO> findByStore_StoreId(Integer storeId);

    
    // 根據店家ID、兌換月和狀態查詢
    PointsRedemptionVO findByStore_StoreIdAndRedemptionMonthAndStatus(Integer storeId, Date redemptionMonth, Integer status);

    // 根據兌換月和狀態查詢所有記錄
    List<PointsRedemptionVO> findByRedemptionMonthAndStatus(Date redemptionMonth, Integer status);
    
  

}