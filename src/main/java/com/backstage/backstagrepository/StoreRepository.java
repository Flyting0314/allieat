package com.backstage.backstagrepository;



import com.entity.StoreVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<StoreVO, Integer> {
	
    StoreVO findByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email); // ✅ 根據 email 驗證帳號
}
