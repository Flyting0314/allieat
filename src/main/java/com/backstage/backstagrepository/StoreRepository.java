package com.backstage.backstagrepository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.StoreVO;

@Repository
public interface StoreRepository extends JpaRepository<StoreVO, Integer> {
	
    StoreVO findByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email); // ✅ 根據 email 驗證帳號
    Optional<StoreVO> findByEmail(String email);

    Optional<StoreVO> findByVerificationMail(String verificationMail);

    List<StoreVO> findByReviewed(Integer reviewed);

    List<StoreVO> findByReviewedIn(List<Integer> reviewedList);


	
}
