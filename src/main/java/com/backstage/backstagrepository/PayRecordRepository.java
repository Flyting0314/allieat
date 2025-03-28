package com.backstage.backstagrepository;




import com.entity.PayRecordVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayRecordRepository extends JpaRepository<PayRecordVO, Integer> {
}
