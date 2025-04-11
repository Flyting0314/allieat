package com.cartfood.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.FoodRepository;

import java.util.List;
import java.util.Optional;
import com.entity.FoodVO;
import com.cartfood.model.*;
import com.entity.AttachedVO;
import com.entity.StoreVO;

@Service
public class CartFoodService {

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
 // 將 FoodVO 轉換成 DTO
    public CarFoodDTO convertToDTO(FoodVO foodVO) {
        if (foodVO == null) return null;

        CarFoodDTO dto = new CarFoodDTO();
        dto.setFoodId(foodVO.getFoodId());
        dto.setName(foodVO.getName());
        dto.setCreatedTime(foodVO.getCreatedTime());
        dto.setStatus(foodVO.getStatus());
        dto.setAmount(foodVO.getAmount());
        dto.setPhoto(foodVO.getPhoto());
        dto.setCost(foodVO.getCost());

        StoreVO store = foodVO.getStore();
        if (store != null) {
            dto.setStoreId(store.getStoreId());
            dto.setStoreName(store.getName()); // 假設 StoreVO 有 getName()
        }

        if (foodVO.getAttached() != null) {
            List<String> attachedNames = foodVO.getAttached()
                .stream()
                .map(AttachedVO::getName) // 假設 AttachedVO 有 getName()
                .toList();
            dto.setAttachedNames(attachedNames);
        }

        return dto;
    }
 

    
    
}
