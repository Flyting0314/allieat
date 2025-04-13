package com.backstage.backstagrepository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.RankVO;

@Repository
public interface RankRepository extends JpaRepository<RankVO, Integer> {
	 List<RankVO> findAllByOrderByTotalDonationDesc();
	 
}
