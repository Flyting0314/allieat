package com.food.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.backstage.backstagrepository.AttachedRepository;
import com.backstage.backstagrepository.FoodRepository;
import com.entity.AttachedVO;
import com.entity.FoodVO;
import com.entity.StoreVO;
import com.food.dto.FoodDemo;
import com.food.dto.FoodRequest;
import com.food.service.FoodService;

@Component
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodRepository foodRepository;
    
    @Autowired
    private AttachedRepository attachedRepository;

    @Override
    public Integer createFood(FoodRequest foodRequest) {

        // 建立 food 實體
        FoodVO foodVO = new FoodVO();

        // 設定 store（這裡是暫時寫死 storeId=1）
        StoreVO storeVO = new StoreVO();
        storeVO.setStoreId(1);  // TODO: 登入完成後改成從 session 抓
        foodVO.setStore(storeVO);

        // 設定主餐基本欄位
        foodVO.setName(foodRequest.getFoodName());
        foodVO.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        foodVO.setStatus(1); // 預設上架狀態
        foodVO.setAmount(10); // 預設庫存數量
        foodVO.setPhoto(foodRequest.getPhotoPath());
        foodVO.setCost(foodRequest.getPointCost());

        // 儲存主餐，會一併儲存副餐（因為有設定 cascade）
        foodRepository.save(foodVO);

        return foodVO.getFoodId(); // 回傳主鍵
    }

    @Override
    public FoodVO getFoodById(Integer foodId) {
        return foodRepository.findById(foodId).orElse(null);
    }
    
    @Override
    public List<FoodDemo> getSimpleFoods() {
        List<FoodVO> foodList = foodRepository
        		.findAll()
        		.stream()
        		.filter(f -> f.getStatus() == 1)
        		.toList(); // 可加條件 storeId = 1
        List<FoodDemo> result = new ArrayList<>();

        for (FoodVO foodVO : foodList) {
        	result.add(new FoodDemo(foodVO.getFoodId(), foodVO.getName(), foodVO.getPhoto(), foodVO.getCost()));
        }
        

        return result;
    }
    
    @Override
    @Transactional
    public void createAttachedList(Integer foodId, List<String> sideDishList) {
        // 先取得 food 對象
        FoodVO foodVO = foodRepository.findById(foodId).orElse(null);
        if (foodVO == null) {
            throw new RuntimeException("找不到對應的 foodId: " + foodId);
        }

        for (String name : sideDishList) {
            AttachedVO attached = new AttachedVO();
            attached.setFood(foodVO); // ✅ 設定 ManyToOne 的關聯
            attached.setName(name);
            attached.setCreatedTime(new Timestamp(System.currentTimeMillis()));

            attachedRepository.save(attached); // 用 JPA repository 儲存
        }
    }
    
    @Override
    @Transactional
    public void deleteFood(FoodVO foodVO) {
        foodRepository.save(foodVO);
    }

    @Override
    @Transactional
    public void updateFood(FoodVO foodVO) {
        foodRepository.save(foodVO); 
    }
    
    
}
