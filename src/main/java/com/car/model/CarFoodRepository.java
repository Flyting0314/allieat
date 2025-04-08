package com.car.model;

import com.entity.FoodVO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarFoodRepository extends JpaRepository<FoodVO, Integer> {
    // 你也可以寫 findByStoreId(...) 等自訂方法
}
