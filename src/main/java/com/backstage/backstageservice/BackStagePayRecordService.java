package com.backstage.backstageservice;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
//import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
//import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagedto.PayRecordUpdateDTO;
import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.PayDetailRepository;
import com.backstage.backstagrepository.PayRecordRepository;
import com.entity.MemberVO;
import com.entity.PayDetailVO;
import com.entity.PayRecordVO;
import com.entity.Status;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional; //如果專案使用 Spring Boot 3.x




@Service
public class BackStagePayRecordService {

    @Autowired
    private PayRecordRepository payRecordRepository;

    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private MemberRepository memberRepository;

   @Autowired
   	private PayDetailRepository payDetailRepository;

    
    public PayRecordVO addPayRecord(PayRecordVO payRecordVO) {
        return payRecordRepository.save(payRecordVO);
    }


    public void deletePayRecord(Integer payoutId) {
        if (payRecordRepository.existsById(payoutId)) {
        	payRecordRepository.deleteById(payoutId);
        }
    }

    //單筆查詢
    public PayRecordVO getPayRecordById(Integer payoutId) {
        return payRecordRepository.findById(payoutId).orElse(null);
    }
    
    //多筆查詢
    public List<PayRecordVO> getPayRecordsByIds(List<Integer> payoutIds) {
        return payRecordRepository.findAllById(payoutIds);
    }
    
    //查詢全部
    public List<PayRecordVO> getAllPayRecords() {
        return payRecordRepository.findAll();
    }

    // 查詢全部，並按 Id 降冪排序
    public List<PayRecordVO> getAllPayRecordsDesc() {
        return payRecordRepository.findAllByOrderByPayoutIdDesc();
    }
    
    
 // 計算啟用會員數量
    public long getEnabledMemberCount() { //要確定資料庫的表格欄位名稱是不是正確！
        String sql = "SELECT COUNT(*) FROM member WHERE accStat = 1";
        Query query = entityManager.createNativeQuery(sql);
        return ((Number) query.getSingleResult()).longValue();
    }

    

    //===============人工手動對一筆payRecord執行立刻發放功能（無視排程時間）===============
    
    @Transactional
    public boolean executePayRecord(Integer payoutId) {
        // 查詢 PayRecord
        PayRecordVO payRecord = payRecordRepository.findById(payoutId)
            .orElseThrow(() -> new RuntimeException("找不到對應的發放記錄"));

        // 檢查狀態是否可發放
        if (payRecord.getStatus() != Status.PENDING && payRecord.getStatus() != Status.STOPPED) {
            throw new IllegalStateException("當前狀態不允許發放");
        }

        // 查詢啟用的會員
        List<MemberVO> activeMembers = memberRepository.findByAccStat(1);
        

        
        // 計算總發放點數
        int totalPointsDistributed = 0;

        // 為每個啟用的會員建立 PayDetail
        for (MemberVO member : activeMembers) {
            PayDetailVO payDetail = new PayDetailVO();
            payDetail.setMember(member);
            payDetail.setPayRecordVO(payRecord);
            payDetail.setPointsExpensed(payRecord.getPayoutPoints());

            // 更新會員點數
            member.setPointsBalance(member.getPointsBalance() + payRecord.getPayoutPoints());

            // 儲存 PayDetail 和更新會員
            payDetailRepository.save(payDetail);
            memberRepository.save(member);

            totalPointsDistributed += payRecord.getPayoutPoints();
        }

        // 更新 PayRecord
        payRecord.setTotalPoints(totalPointsDistributed);
        payRecord.setPayoutDate(Timestamp.valueOf(LocalDateTime.now()));
        payRecord.setStatus(Status.COMPLETED);

        payRecordRepository.save(payRecord);

        return true;
    }
    
    
    
    
    
    
    // =========== 在 payDetail 頁面人工補發點數完成後，更新 payRecord 發放總點數 totalPoints ========
    @Transactional
    public void updateTotalPoints(Integer payoutId, Integer additionalPoints) {
        PayRecordVO payRecord = payRecordRepository.findById(payoutId)
            .orElseThrow(() -> new RuntimeException("找不到 payRecord"));

        // 將新增的點數加到原有的總點數
        int updatedTotalPoints = payRecord.getTotalPoints() + additionalPoints;

        // 更新 totalPoints
        payRecord.setTotalPoints(updatedTotalPoints);
        payRecordRepository.save(payRecord);

        // 添加日誌紀錄
        System.out.println("PayRecord " + payoutId + " totalPoints 已更新: " + updatedTotalPoints);
    }




    
    //====== 人工手動更新payRecord時的方法（修改發放日期、時間、點數）=========

    @Transactional
    public boolean updatePayRecord(Integer payoutId, PayRecordUpdateDTO request) {
        // 1. 查找要更新的 PayRecord
        PayRecordVO payRecord = payRecordRepository.findById(payoutId)
            .orElseThrow(() -> new IllegalArgumentException("找不到對應的發放記錄"));

        // 2. 檢查是否可以修改
        if (payRecord.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("無法修改已發放的 PayRecord");
        }

        // 3. 時間解析與驗證
        try {
            // 使用 ISO_LOCAL_DATE_TIME 解析前端傳來的時間字串
            // 這個解析器會直接處理 "YYYY-MM-DDTHH:mm:ss" 格式
            LocalDateTime parsedDateTime = LocalDateTime.parse(
                request.getScheduleTime(), 
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
            );

            // 4. 時間範圍驗證
            LocalDateTime now = LocalDateTime.now();
            if (parsedDateTime.isBefore(now)) {
                throw new IllegalArgumentException("排程時間必須是當前或未來時間");
            }

            // 5. 確保在當前月份
            if (parsedDateTime.getYear() != now.getYear() || 
                parsedDateTime.getMonth() != now.getMonth()) {
                throw new IllegalArgumentException("只能修改當前月份的排程");
            }

            // 6. 將 LocalDateTime 轉換為 Timestamp
            // 使用 Timestamp.valueOf() 可以準確保存本地時間
            Timestamp scheduleTime = Timestamp.valueOf(parsedDateTime);
            payRecord.setScheduleTime(scheduleTime);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("時間格式不正確", e);
        }

        // 7. 更新點數（如果有提供）
        if (request.getPayoutPoints() != null) {
            validatePayoutPoints(request.getPayoutPoints());
            payRecord.setPayoutPoints(request.getPayoutPoints());
        }

        // 8. 更新狀態（如果有提供）
        if (request.getStatus() != null) {
            payRecord.setStatus(Status.fromValue(request.getStatus()));
        }

        // 9. 保存更新
        payRecordRepository.save(payRecord);
        return true;
    }

    // 輔助方法：驗證點數
    private void validatePayoutPoints(Integer points) {
        if (points == null || points <= 0) {
            throw new IllegalArgumentException("點數必須大於0");
        }
        if (points > 100000) {
            throw new IllegalArgumentException("點數不能超過100000");
        }
    }
  
    
    
    
    
    
    

    
    // ======= 人工手動更新 payRecord 時，輸入時間參數的驗證方法 =======
    private boolean isValidScheduleTime(Timestamp scheduleTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduleDate = scheduleTime.toLocalDateTime();

        return now.getYear() == scheduleDate.getYear() && now.getMonth() == scheduleDate.getMonth();
    }
    
    
    
    
    //=============動態搜尋功能，包含複合查詢===================
    
    public List<PayRecordVO> findByDynamicCriteria(
    	    Integer payoutId, 
    	    Integer status, 
    	    LocalDate startDate, 
    	    LocalDate endDate
    	) {
    	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	    CriteriaQuery<PayRecordVO> query = cb.createQuery(PayRecordVO.class);
    	    Root<PayRecordVO> root = query.from(PayRecordVO.class);
    	    
    	    List<Predicate> predicates = new ArrayList<>();
    	    
    	    if (payoutId != null) {
    	        predicates.add(cb.equal(root.get("payoutId"), payoutId));
    	    }
    	    
    	    // 修改狀態查詢，使用枚舉
    	    if (status != null) {
    	        predicates.add(cb.equal(root.get("status"), Status.fromValue(status)));
    	    }
    	    
    	    if (startDate != null && endDate != null) {
    	        // 用 payoutDate 進行日期比較
    	        predicates.add(cb.between(
    	            root.get("payoutDate"), 
    	            Timestamp.valueOf(startDate.atStartOfDay()), 
    	            Timestamp.valueOf(endDate.atTime(23, 59, 59))
    	        ));
    	    }
    	    
    	    query.where(predicates.toArray(new Predicate[0]));
    	    query.orderBy(cb.desc(root.get("payoutId")));
    	    
    	    return entityManager.createQuery(query).getResultList();
    	}
    
}
