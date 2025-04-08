package com.food.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.FoodRepository;

import java.util.List;
import java.util.Optional;
import com.entity.FoodVO;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    // 新增 Food
    public FoodVO addFood(FoodVO foodVO) {
        return foodRepository.save(foodVO);
    }

    // 更新 Food
    public FoodVO updateFood(FoodVO foodVO) {
        return foodRepository.save(foodVO);
    }

    // 刪除 Food
    public void deleteFood(Integer foodId) {
        if (foodRepository.existsById(foodId)) {
            foodRepository.deleteById(foodId);
        }
    }

    // 單筆查詢 Food
    public FoodVO getFoodById(Integer foodId) {
        return foodRepository.findById(foodId).orElse(null);
    }

    // 查詢多筆 Food
    public List<FoodVO> getFoodsByIds(List<Integer> foodIds) {
        return foodRepository.findAllById(foodIds);
    }

    // 查詢所有 Food
    public List<FoodVO> getAllFoods() {
        return foodRepository.findAll();
    }

    // 查詢特定店家的 Food
    public List<FoodVO> getFoodsByStoreId(Integer storeId) {
        return foodRepository.findByStore_StoreId(storeId);
    }
    
    
    
}
