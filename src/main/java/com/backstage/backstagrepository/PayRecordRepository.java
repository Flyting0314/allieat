package com.backstage.backstagrepository;




import com.entity.PayRecordVO;
import com.entity.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.sql.Timestamp;

@Repository
public interface PayRecordRepository extends JpaRepository<PayRecordVO, Integer> {
	
	  // 查詢所有 PayRecord 並按 payoutId 降冪排序
    List<PayRecordVO> findAllByOrderByPayoutIdDesc();
    
    //排程使用，查找是否有符合時間和狀態的payrecord需要被發放點數
    List<PayRecordVO> findByStatusAndScheduleTimeBefore(Status status, Timestamp currentTime);
}
