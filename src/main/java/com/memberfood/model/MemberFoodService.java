package com.memberfood.model;

import com.entity.FoodVO;
import com.memberfood.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MemberFoodService {

    @Autowired
    private MemberFoodRepository foodRepository;

    public List<FoodVO> getFoodsByStoreId(Integer storeId) {
        return foodRepository.findByStoreStoreId(storeId);
    }

    public FoodVO getFoodById(Integer foodId) {
        return foodRepository.findById(foodId).orElse(null);
    }
}