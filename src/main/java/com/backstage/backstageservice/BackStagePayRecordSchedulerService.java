package com.backstage.backstageservice;


//Spring 相關
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Java 基礎類別
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//自定義倉庫（Repository）
import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.PayDetailRepository;
import com.backstage.backstagrepository.PayRecordRepository;

//自定義 VO（值對象）
import com.entity.MemberVO;
import com.entity.PayDetailVO;
import com.entity.PayRecordVO;


//狀態枚舉
import com.entity.Status;


//排程器錯誤處理
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





//==============發放點數的排程器================
@Service
public class BackStagePayRecordSchedulerService {

 private static final Logger logger = LoggerFactory.getLogger(BackStagePayRecordSchedulerService.class);

 // 批次大小
 private static final int BATCH_SIZE = 20;
 
 
 
 @Autowired
 private PayRecordRepository payRecordRepository;

 @Autowired
 private MemberRepository memberRepository;

 @Autowired
 private PayDetailRepository payDetailRepository;

 @Autowired
 private BackStagePayDetailService payDetailService; // 注入 PayDetailService

 
 
 
 
 //=======================此處為非批次處理交易版本==========================
 
 						//暫時改成100分鐘檢查一次！這邊可自由調整！
// @Scheduled(fixedRate = 6000000) // 60000 毫秒 = 1分鐘 
// @Transactional
// public void checkAndDistributePoints() {
//     try {
//         LocalDateTime now = LocalDateTime.now();
//         List<PayRecordVO> readyToDistributeRecords = payRecordRepository.findByStatusAndScheduleTimeBefore(
//                 Status.PENDING,
//                 Timestamp.valueOf(now)
//         );
//
//         for (PayRecordVO payRecord : readyToDistributeRecords) {
//             distributePointsForPayRecord(payRecord);
//         }
//     } catch (Exception e) {
//         logger.error("點數分配過程中發生錯誤", e);
//         // 可以選擇拋出異常或繼續執行,根據實際需求決定
//         throw e;
//     }
// }
//
// 
// private void distributePointsForPayRecord(PayRecordVO payRecord) {
//     try {
//         List<MemberVO> activeMembers = memberRepository.findByAccStat(1); //  1 代表會員帳號為啟用狀態
//
//         int totalPointsDistributed = 0;
//
//         for (MemberVO member : activeMembers) {
//             PayDetailVO payDetail = new PayDetailVO();
//             payDetail.setMember(member);
//             payDetail.setPayRecordVO(payRecord);
//             payDetail.setPointsExpensed(payRecord.getPayoutPoints());
//
//             // 更新會員點數
//             member.setPointsBalance(member.getPointsBalance() + payRecord.getPayoutPoints());
//
//             // 儲存 PayDetail
//             payDetailRepository.save(payDetail);
//             memberRepository.save(member);
//
//             totalPointsDistributed += payRecord.getPayoutPoints();
//         }
//
//         // 更新 PayRecord 狀態
//         payRecord.setStatus(Status.COMPLETED);
//         payRecord.setTotalPoints(totalPointsDistributed);
//         payRecord.setPayoutDate(Timestamp.valueOf(LocalDateTime.now()));
//
//         payRecordRepository.save(payRecord);
//     } catch (Exception e) {
//         logger.error("處理 PayRecord: {} 時發生錯誤", payRecord.getPayoutId(), e);
//         // 可以選擇拋出異常或繼續執行,根據實際需求決定
//         throw e;
//     }
// }
// 
 
 
 
 
 
 //========================批次處理交易版本=================================
 							
 
 
 //=========掃描是否達到排程時間，並發放點數===========
 
 					//暫時改成100分鐘檢查一次！這邊可自由調整！
 @Scheduled(fixedRate = 6000) // 60000 毫秒 = 1分鐘 
 @Transactional
 public void checkAndDistributePoints() {
     try {
         LocalDateTime now = LocalDateTime.now();
         List<PayRecordVO> readyToDistributeRecords = payRecordRepository.findByStatusAndScheduleTimeBefore(
                 Status.PENDING,
                 Timestamp.valueOf(now)
         );
         
         for (PayRecordVO payRecord : readyToDistributeRecords) {
             distributePointsForPayRecord(payRecord);
         }
     } catch (Exception e) {
         logger.error("點數分配過程中發生錯誤", e);
         throw e;
     }
 }
 
 
 
 //=========批次發放點數的方法：發放後產生paydetail明細紀錄，並更新payrecord資訊==========
 private void distributePointsForPayRecord(PayRecordVO payRecord) {
     try {
         // 取得所有啟用狀態的會員
         List<MemberVO> activeMembers = memberRepository.findByAccStat(1);
         
         int totalPointsDistributed = 0;
         
         // 分批處理會員
         for (int i = 0; i < activeMembers.size(); i += BATCH_SIZE) {
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
         }
         
         // 更新 PayRecord 狀態
         payRecord.setStatus(Status.COMPLETED);
         payRecord.setTotalPoints(totalPointsDistributed);
         payRecord.setPayoutDate(Timestamp.valueOf(LocalDateTime.now()));
         payRecordRepository.save(payRecord);
         
     } catch (Exception e) {
         logger.error("處理 PayRecord: {} 時發生錯誤", payRecord.getPayoutId(), e);
         throw e;
     }
 }
 
 
 
 
 
 
 
 
}