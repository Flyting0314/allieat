package com.backstage.backstagrepository;



import com.entity.TotalAmountVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TotalAmountRepository extends JpaRepository<TotalAmountVO, Integer> {
    @Query(value = "SELECT totalAmount FROM totalAmount WHERE updatedTime < NOW() ORDER BY updatedTime DESC LIMIT 1", nativeQuery = true)
    Integer Now();

    @Query(value = "SELECT totalAmount FROM totalAmount WHERE updatedTime >= DATE_SUB(NOW(), INTERVAL 1 MONTH) ORDER BY updatedTime ASC LIMIT 1", nativeQuery = true)
    Integer LastMonth();

    @Query(value = """
    SELECT
        DATE_FORMAT(updatedTime, '%Y-%m') AS month,
        SUM(totalAmount) AS total
    FROM totalamount
    WHERE updatedTime >= DATE_SUB(CURDATE(), INTERVAL 11 MONTH)
    GROUP BY month
    ORDER BY month
    """, nativeQuery = true)
    List<Object[]> findMonthlyTotalAmounts();



}
