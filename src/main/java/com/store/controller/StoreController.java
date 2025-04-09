package com.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.store.model.StoreService;
import com.entity.StoreVO;
import com.cartfood.model.CartFoodService;
import com.entity.FoodVO;

import java.util.List;

@RestController
@RequestMapping("/api/store")
@CrossOrigin(origins = "*") // 允許跨域
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private CartFoodService foodService;

    // 取得特定 storeId 的餐廳資訊
    @GetMapping("/{storeId}")
    public StoreVO getStoreById(@PathVariable Integer storeId) {
        return storeService.getStoreById(storeId);
    }

    // 取得特定 storeId 的餐點清單
    @GetMapping("/{storeId}/foods")
    public List<FoodVO> getFoodsByStore(@PathVariable Integer storeId) {
        return foodService.getFoodsByStoreId(storeId);
    }
}

