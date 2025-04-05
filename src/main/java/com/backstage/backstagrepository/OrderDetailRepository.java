package com.backstage.backstagrepository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.OrderDetailId;
import com.entity.OrderDetailVO;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailVO, OrderDetailId> {
	List<OrderDetailVO> findByOrderId(Integer orderId);
}
