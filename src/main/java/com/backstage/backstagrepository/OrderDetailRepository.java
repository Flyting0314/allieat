package com.backstage.backstagrepository;



import com.entity.OrderDetailId;
import com.entity.OrderDetailVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailVO, OrderDetailId> {
}
