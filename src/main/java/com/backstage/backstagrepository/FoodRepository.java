package com.backstage.backstagrepository;

import java.util.List; // ✅ 必須加上這一行
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.FoodVO;

@Repository
public interface FoodRepository extends JpaRepository<FoodVO, Integer> {
    List<FoodVO> findByStore_StoreId(Integer storeId);
}
