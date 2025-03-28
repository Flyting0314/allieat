package com.backstage.backstagrepository;




import com.entity.PayDetailVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayDetailRepository extends JpaRepository<PayDetailVO, Integer> {
}
