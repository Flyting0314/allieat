package com.backstage.backstagrepository;


import com.entity.DonationVO;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DonationRepository extends JpaRepository<DonationVO, Integer> {
    DonationVO findTopByOrderByRankIdDesc();
 // ✔ 原本的刪除方法（使用 native SQL）
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM donation WHERE rankId = ?1", nativeQuery = true)
    void deleteByRankId(int rankId);

    // ✔ 修正後的自訂查詢方法（使用 JPQL，不再用錯誤的 LIKE）
    @Query("FROM DonationVO d WHERE d.rankId = ?1 AND d.amount = ?2 AND d.updatedTime = ?3 ORDER BY d.rankId")
    List<DonationVO> findByOthers(int rankId, int amount, Timestamp updatedTime);

    // ✔ 或者可以用 Spring Data JPA 的命名查詢方式（不需要 @Query）
    // List<DonationVO> findByRankIdAndAmountAndUpdatedTimeOrderByRankId(int rankId, int amount, Timestamp updatedTime);
}