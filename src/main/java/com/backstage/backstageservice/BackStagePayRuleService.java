package com.backstage.backstageservice;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import org.springframework.transaction.annotation.Transactional;

import com.backstage.backstagrepository.PayRecordRepository;
import com.backstage.backstagrepository.PayRuleRepository;
import com.entity.PayRecordVO;
import com.entity.PayRuleVO;
import com.entity.Status;



@Service
public class BackStagePayRuleService {

    @Autowired
    private PayRuleRepository payRuleRepository;
    
    @Autowired
    private PayRecordRepository payRecordRepository;



    public void deletePayRule(Integer payRuleId) {
        if (payRuleRepository.existsById(payRuleId)) {
        	payRuleRepository.deleteById(payRuleId);
        }
    }
    
    //取得唯一一筆 PayRule.
    public PayRuleVO getPayRule() {
        return payRuleRepository.findAll().stream().findFirst().orElse(null);
    }

    
    
    
    //===========更新排程發放規則===============
    
    @Transactional
    public PayRuleVO addOrUpdatePayRule(PayRuleVO payRuleVO) {
        Optional<PayRuleVO> existingRule = payRuleRepository.findAll().stream().findFirst();
        
        if (existingRule.isPresent()) {
            // 更新現有規則
            payRuleVO.setPayRuleId(existingRule.get().getPayRuleId());
        }
        
        return payRuleRepository.save(payRuleVO);
    }
    
    
    
    
   //========== 根據 payRule 產生一筆 payRecord ==========
     
    @Transactional
    public void generatePayRecordFromRule() {
        System.out.println("====== generatePayRecordFromRule 方法開始執行 ======");

        PayRuleVO payRule = getPayRule();
        if (payRule == null) {
            throw new RuntimeException("尚未制定排程規則");
        }

        // 將排程的日和時間轉換成排程時間，方便 payRecord 設定 scheduleTime
     // 將 Time 物件轉換為 String，並解析成 LocalTime
        String payoutTimeString = payRule.getPayoutTime().toString(); // 假設 Time 已經是格式 '10:00:00'
        LocalTime payoutTime = LocalTime.parse(payoutTimeString);

        // 設定日期，組合成 LocalDateTime
        LocalDate payoutDate = LocalDate.now().withDayOfMonth(payRule.getPayoutDay());
        LocalDateTime scheduleDateTime = LocalDateTime.of(payoutDate, payoutTime);

        // 儲存時間為 Timestamp
        Timestamp timestamp = Timestamp.valueOf(scheduleDateTime);


        // 設定 PayRecord 資料
        PayRecordVO payRecord = new PayRecordVO();
        payRecord.setPayRuleVO(payRule); // 連結 PayRule
        payRecord.setPayoutPoints(payRule.getPayoutPoints()); // 每位會員的發放點數
        payRecord.setTotalPoints(0); // 初始為 0，後續會依據實際發放情況填寫
        payRecord.setScheduleTime(timestamp);
        payRecord.setPayoutDate(null); // 明確設置為 null
        payRecord.setStatus(Status.PENDING); // 初始狀態：未發放
        
        // 增加日誌輸出驗證狀態
        System.out.println("Debug - PayRecord 創建前:");
        System.out.println("PayRuleId: " + payRule.getPayRuleId());
        System.out.println("Payout Points: " + payRecord.getPayoutPoints());
        System.out.println("Schedule Time: " + payRecord.getScheduleTime());
        System.out.println("Status: " + payRecord.getStatus());
        System.out.println("Status Value: " + payRecord.getStatus().getValue());
        

        payRecordRepository.save(payRecord);
        
        // 保存後再次驗證
        System.out.println("\nDebug - PayRecord 保存後:");
        System.out.println("Saved PayoutId: " + payRecord.getPayoutId());
        System.out.println("Saved Status: " + payRecord.getStatus());
        System.out.println("Saved Status Value: " + payRecord.getStatus().getValue());
    }
}
