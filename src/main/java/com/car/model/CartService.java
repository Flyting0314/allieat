package com.car.model;

import com.entity.AttachedVO;
import com.entity.FoodVO;

import java.util.List;
import java.util.Optional;

public interface CartService {
    List<FoodVO> getMainDishes();
    List<AttachedVO> getAttachedItems();

    Optional<FoodVO> getMainDishById(Integer foodId);
    Optional<AttachedVO> getAttachedItemById(Integer attachedId);
}
