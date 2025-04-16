package com.backstage.backstagrepository;

import com.entity.DonaVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface DonaRepository extends JpaRepository<DonaVO, Integer> {

    // 用訂單編號找紀錄
//    DonaVO findByMerchantTradeNo(String merchantTradeNo);

    // 進階條件查詢：抬頭 + 時間區間 + 身分證 or 統編
    @Query("SELECT d FROM DonaVO d " +
           "WHERE d.salutation = :salutation " +
           "AND d.createdTime BETWEEN :start AND :end " +
           "AND (d.idNum = :idNum OR d.guiNum = :guiNum)")
    List<DonaVO> findByConditions(
            @Param("salutation") String salutation,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end,
            @Param("idNum") String idNum,
            @Param("guiNum") String guiNum
    );
    
    
}
