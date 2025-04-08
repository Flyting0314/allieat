package com.memberfood.model;

import com.entity.FoodVO;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemberFoodRepository extends JpaRepository<FoodVO, Integer> {
    List<FoodVO> findByStoreStoreId(Integer storeId);
}
