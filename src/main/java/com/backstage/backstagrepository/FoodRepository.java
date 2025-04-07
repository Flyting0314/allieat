package com.backstage.backstagrepository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.FoodVO;

@Repository
public interface FoodRepository extends JpaRepository<FoodVO, Integer> {
}
