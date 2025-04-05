package com.backstage.backstagrepository;




import com.entity.OrderFoodVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderFoodRepository extends JpaRepository<OrderFoodVO, Integer> {
	
	List<OrderFoodVO> findByMember_MemberId(Integer memberId);
	
	
    @Query(value = """
    SELECT DATE_FORMAT(pickTime, '%Y-%m') AS labels, 
               COUNT(*) AS data
    FROM orderList
    WHERE pickStat = 1 
      AND pickTime >= DATE_SUB(CURDATE(), INTERVAL 11 MONTH)
    GROUP BY labels
    ORDER BY labels
    """, nativeQuery = true)
    List<Object[]> findMonthlyPickedOrders();

}
