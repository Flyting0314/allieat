package com.backstage.backstageservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; //用於多個線程（同時執行的程式）同時存取

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.PayDetailRepository;
import com.backstage.backstagrepository.PayRecordRepository;
import com.entity.MemberVO;
import com.entity.PayDetailVO;
import com.entity.PayRecordVO;
import com.entity.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BackStagePayRecordSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(BackStagePayRecordSchedulerService.class);

    // 批次大小
    private static final int BATCH_SIZE = 20;
    
    // 最大重試次數
    private static final int MAX_RETRY_COUNT = 3;
    
    // 使用記憶體快取來記錄重試資訊，不需修改資料庫結構
    // key: PayRecord ID, value: [重試次數, 上次重試時間的時間戳]
    private final Map<Integer, Object[]> retryCache = new ConcurrentHashMap<>();

    @Autowired
    private PayRecordRepository payRecordRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PayDetailRepository payDetailRepository;

    @Autowired
    private BackStagePayDetailService payDetailService;

    //=======================非批次處理交易版本==========================
    
//    @Scheduled(fixedRate = 6000000) // 60000 毫秒 = 1分鐘，設定較長時間避免重疊執行 
//    @Transactional
//    public void checkAndDistributePointsNonBatch() {
//        Instant startTime = Instant.now();
//        logger.info("開始執行非批次處理發放點數...");
//        
//        try {
//            LocalDateTime now = LocalDateTime.now();
//            List<PayRecordVO> readyToDistributeRecords = payRecordRepository.findByStatusAndScheduleTimeBefore(
//                    Status.PENDING,
//                    Timestamp.valueOf(now)
//            );
//
//            logger.info("找到 {} 筆待處理的點數發放記錄", readyToDistributeRecords.size());
//            
//            for (PayRecordVO payRecord : readyToDistributeRecords) {
//                try {
//                    distributePointsForPayRecordNonBatch(payRecord);
//                } catch (Exception e) {
//                    // 處理異常，記錄到快取中
//                    handleRetryLogic(payRecord.getPayoutId(), e);
//                    logger.error("非批次處理 PayRecord: {} 時發生錯誤", payRecord.getPayoutId(), e);
//                }
//            }
//            
//            Instant endTime = Instant.now();
//            long executionTime = Duration.between(startTime, endTime).toMillis();
//            logger.info("非批次處理發放點數完成，總執行時間: {} 毫秒", executionTime);
//            
//        } catch (Exception e) {
//            Instant endTime = Instant.now();
//            long executionTime = Duration.between(startTime, endTime).toMillis();
//            logger.error("非批次點數分配過程中發生錯誤，執行時間: {} 毫秒", executionTime, e);
//            throw e;
//        }
//    }
//
//    private void distributePointsForPayRecordNonBatch(PayRecordVO payRecord) {
//        Instant startTime = Instant.now();
//        logger.info("開始為 PayRecord ID: {} 執行非批次處理發放點數...", payRecord.getPayoutId());
//        
//        // 檢查是否超過最大重試次數
//        Integer payoutId = payRecord.getPayoutId();
//        if (retryCache.containsKey(payoutId)) {
//            Object[] retryInfo = retryCache.get(payoutId);
//            int retryCount = (Integer) retryInfo[0];
//            
//            if (retryCount >= MAX_RETRY_COUNT) {
//                logger.warn("PayRecord ID: {} 已達最大重試次數 {}, 將不再處理", payoutId, MAX_RETRY_COUNT);
//                return; // 不再處理此記錄
//            }
//        }
//        
//        try {
//            List<MemberVO> activeMembers = memberRepository.findByAccStat(1); // 1 代表會員帳號為啟用狀態
//            logger.info("找到 {} 位啟用狀態的會員", activeMembers.size());
//
//            int totalPointsDistributed = 0;
//
//            for (MemberVO member : activeMembers) {
//                PayDetailVO payDetail = new PayDetailVO();
//                payDetail.setMember(member);
//                payDetail.setPayRecordVO(payRecord);
//                payDetail.setPointsExpensed(payRecord.getPayoutPoints());
//
//                // 更新會員點數
//                member.setPointsBalance(member.getPointsBalance() + payRecord.getPayoutPoints());
//
//                // 儲存 PayDetail
//                payDetailRepository.save(payDetail);
//                memberRepository.save(member);
//
//                totalPointsDistributed += payRecord.getPayoutPoints();
//            }
//
//            // 更新 PayRecord 狀態
//            payRecord.setStatus(Status.COMPLETED);
//            payRecord.setTotalPoints(totalPointsDistributed);
//            payRecord.setPayoutDate(Timestamp.valueOf(LocalDateTime.now()));
//            payRecordRepository.save(payRecord);
//            
//            // 成功完成，從重試快取中移除
//            retryCache.remove(payoutId);
//            
//            Instant endTime = Instant.now();
//            long executionTime = Duration.between(startTime, endTime).toMillis();
//            logger.info("PayRecord ID: {} 非批次處理完成，處理 {} 位會員，總執行時間: {} 毫秒", 
//                    payRecord.getPayoutId(), activeMembers.size(), executionTime);
//            
//        } catch (Exception e) {
//            Instant endTime = Instant.now();
//            long executionTime = Duration.between(startTime, endTime).toMillis();
//            logger.error("處理 PayRecord: {} 非批次處理時發生錯誤，執行時間: {} 毫秒", 
//                    payRecord.getPayoutId(), executionTime, e);
//            throw e; // 讓外層捕捉並處理
//        }
//    }

    //========================批次處理交易版本=================================
    
    //=========掃描是否達到排程時間，並發放點數===========
    @Scheduled(fixedRate = 6000000) // 60000 毫秒 = 1分鐘，測試用設定較短 
    @Transactional
    public void checkAndDistributePointsBatch() {
        Instant startTime = Instant.now();
        logger.info("開始執行批次處理發放點數...");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 查找待處理的記錄
            List<PayRecordVO> readyToDistributeRecords = payRecordRepository.findByStatusAndScheduleTimeBefore(
                    Status.PENDING,
                    Timestamp.valueOf(now)
            );
            
            logger.info("找到 {} 筆待處理的點數發放記錄", readyToDistributeRecords.size());
            
            for (PayRecordVO payRecord : readyToDistributeRecords) {
                try {
                    distributePointsForPayRecordBatch(payRecord);
                } catch (Exception e) {
                    // 處理異常，記錄到快取中
                    handleRetryLogic(payRecord.getPayoutId(), e);
                    logger.error("批次處理 PayRecord: {} 時發生錯誤", payRecord.getPayoutId(), e);
                }
            }
            
            Instant endTime = Instant.now();
            long executionTime = Duration.between(startTime, endTime).toMillis();
            logger.info("批次處理發放點數完成，總執行時間: {} 毫秒", executionTime);
            
        } catch (Exception e) {
            Instant endTime = Instant.now();
            long executionTime = Duration.between(startTime, endTime).toMillis();
            logger.error("批次點數分配過程中發生錯誤，執行時間: {} 毫秒", executionTime, e);
            throw e; //由呼叫 distributePointsForPayRecordBatch 的外層方法來捕捉並處理。
        }
    }

    
    
    //=========批次發放點數的方法==========
    private void distributePointsForPayRecordBatch(PayRecordVO payRecord) {
        Instant startTime = Instant.now();
        logger.info("開始為 PayRecord ID: {} 執行批次處理發放點數...", payRecord.getPayoutId());
        
        Integer payoutId = payRecord.getPayoutId();
        
        // 檢查是否超過最大重試次數
        if (retryCache.containsKey(payoutId)) {
            Object[] retryInfo = retryCache.get(payoutId);
            int retryCount = (Integer) retryInfo[0];
            
            if (retryCount >= MAX_RETRY_COUNT) {
                logger.warn("PayRecord ID: {} 已達最大重試次數 {}, 將不再處理", payoutId, MAX_RETRY_COUNT);
                return; // 不再處理此記錄
            }
        }
        
        try {
            // 取得所有啟用狀態的會員
            List<MemberVO> activeMembers = memberRepository.findByAccStat(1);
            logger.info("找到 {} 位啟用狀態的會員", activeMembers.size());
            
            int totalPointsDistributed = 0;
            int batchCount = 0;
            
            // 分批處理會員
            for (int i = 0; i < activeMembers.size(); i += BATCH_SIZE) {
                Instant batchStartTime = Instant.now();
                batchCount++;
                
                // 計算當前批次的結束索引
                int end = Math.min(i + BATCH_SIZE, activeMembers.size());
                
                // 取得當前批次的會員
                List<MemberVO> currentBatch = activeMembers.subList(i, end);
                
                // 處理當前批次
                List<PayDetailVO> batchPayDetails = new ArrayList<>();
                List<MemberVO> updatedMembers = new ArrayList<>();
                
                for (MemberVO member : currentBatch) {
                    PayDetailVO payDetail = new PayDetailVO();
                    payDetail.setMember(member);
                    payDetail.setPayRecordVO(payRecord);
                    payDetail.setPointsExpensed(payRecord.getPayoutPoints());
                    
                    // 更新會員點數
                    member.setPointsBalance(member.getPointsBalance() + payRecord.getPayoutPoints());
                    
                    batchPayDetails.add(payDetail);
                    updatedMembers.add(member);
                    
                    totalPointsDistributed += payRecord.getPayoutPoints();
                }
                
                // 批次儲存 PayDetail 和 Member
                payDetailRepository.saveAll(batchPayDetails);
                memberRepository.saveAll(updatedMembers);
                
                Instant batchEndTime = Instant.now();
                long batchExecutionTime = Duration.between(batchStartTime, batchEndTime).toMillis();
                logger.info("批次 {} 處理完成，處理 {} 位會員，執行時間: {} 毫秒", 
                        batchCount, currentBatch.size(), batchExecutionTime);
            }
            
            // 更新 PayRecord 狀態
            payRecord.setStatus(Status.COMPLETED);
            payRecord.setTotalPoints(totalPointsDistributed);
            payRecord.setPayoutDate(Timestamp.valueOf(LocalDateTime.now()));
            payRecordRepository.save(payRecord);
            
            // 成功完成，從重試快取中移除
            retryCache.remove(payoutId);
            
            Instant endTime = Instant.now();
            long executionTime = Duration.between(startTime, endTime).toMillis();
            logger.info("PayRecord ID: {} 批次處理完成，共 {} 批次，總執行時間: {} 毫秒", 
                    payRecord.getPayoutId(), batchCount, executionTime);
            
        } catch (Exception e) {
            Instant endTime = Instant.now();
            long executionTime = Duration.between(startTime, endTime).toMillis();
            logger.error("處理 PayRecord: {} 批次處理時發生錯誤，執行時間: {} 毫秒", 
                    payRecord.getPayoutId(), executionTime, e);
            throw e; // 讓外層捕捉並處理
        }
    }
    
    
    
    // 處理重試邏輯，將資訊儲存在記憶體中
    private void handleRetryLogic(Integer payoutId, Exception e) {
    	// 從快取中獲取這個任務的重試信息，如果沒有則創建新的
    	//嘗試從 retryCache 中查找ID為payoutId的任務的重試信息：如果沒找到(第一次失敗)，就創建新的信息：[0, 當前時間]，0表示「還沒重試過」
        Object[] retryInfo = retryCache.getOrDefault(payoutId, new Object[]{0, System.currentTimeMillis()});
        int currentRetryCount = (Integer) retryInfo[0];
        
        // 增加重試計數：重試次數+1
        retryInfo[0] = currentRetryCount + 1;
        // 更新上次重試時間
        retryInfo[1] = System.currentTimeMillis();
        
        // 記錄到快取
        retryCache.put(payoutId, retryInfo);
        
        logger.info("PayRecord ID: {} 將重試，當前重試次數: {}", payoutId, retryInfo[0]);
    }
    
    
    
    
    
    // 專門用於檢查重試的排程
    @Scheduled(fixedRate = 6000000) // 60000=1分鐘。
    @Transactional
    public void checkAndRetryFailedRecords() {
        try {
            LocalDateTime now = LocalDateTime.now();
            long currentTime = System.currentTimeMillis();
            
            // 掃描所有待處理記錄
            List<PayRecordVO> allRecords = payRecordRepository.findAll();
            List<PayRecordVO> pendingRecords = new ArrayList<>();
            for (PayRecordVO record : allRecords) {
                if (record.getStatus() == Status.PENDING) {
                    pendingRecords.add(record);
                }
            }
            
            for (PayRecordVO record : pendingRecords) {
                Integer payoutId = record.getPayoutId();
                
                // 檢查是否在重試快取中
                if (retryCache.containsKey(payoutId)) {
                    Object[] retryInfo = retryCache.get(payoutId);
                    int retryCount = (Integer) retryInfo[0];
                    long lastRetryTime = (Long) retryInfo[1];
                    
                    // 檢查是否超過最大重試次數
                    if (retryCount >= MAX_RETRY_COUNT) {
                        logger.warn("PayRecord ID: {} 已達最大重試次數: {}, 將狀態改為STOPPED並從快取中移除", 
                                payoutId, MAX_RETRY_COUNT);
                        
                        // 將狀態改為STOPPED
                        record.setStatus(Status.STOPPED);
                        payRecordRepository.save(record);
                        
                        // 從快取中移除
                        retryCache.remove(payoutId);
                        continue;
                    }
                    
                    // 確保與上次重試間隔至少5分鐘
                    long fiveMinutesInMillis = 5 * 60 * 1000;
                    if (currentTime - lastRetryTime < fiveMinutesInMillis) {
                        logger.info("PayRecord ID: {} 距離上次重試不足5分鐘，將稍後重試", payoutId);
                        continue;
                    }
                    
                    // 嘗試重新處理
                    logger.info("開始重試 PayRecord ID: {}, 重試次數: {}", payoutId, retryCount);
                    try {
                        distributePointsForPayRecordBatch(record);
                    } catch (Exception e) {
                        // 更新重試資訊
                        handleRetryLogic(payoutId, e);
                        logger.error("重試 PayRecord: {} 時發生錯誤", payoutId, e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("檢查重試記錄過程中發生錯誤", e);
        }
    }
}