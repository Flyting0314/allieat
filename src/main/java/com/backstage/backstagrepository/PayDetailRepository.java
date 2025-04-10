package com.backstage.backstagrepository;



import java.util.List;
import com.entity.PayDetailVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayDetailRepository extends JpaRepository<PayDetailVO, Integer> {
	  // 根據 PayRecord 的 payoutId 查詢所有對應的 PayDetail
    List<PayDetailVO> findByPayRecordVO_PayoutId(Integer payoutId);
	
}
