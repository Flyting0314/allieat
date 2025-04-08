package com.checkout.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.*;

import com.food.model.FoodService;
import com.entity.FoodVO;
import com.store.model.StoreService;

@RestController
@RequestMapping("/meal")
public class CheckoutController {

    @Autowired
    private FoodService foodService;

    @Autowired
    private StoreService storeService;

    // ✅ 檢查購物車每個商品是否還有庫存
    @PostMapping("/cart/stock-status")
    public List<Map<String, Object>> checkStock(@RequestBody List<Map<String, Object>> cartItems) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> item : cartItems) {
            Integer foodId = (Integer) item.get("foodId");
            Integer quantity = (Integer) item.get("quantity");

            FoodVO food = foodService.getFoodById(foodId);
            boolean inStock = food != null && food.getAmount() >= quantity;

            Map<String, Object> check = new HashMap<>();
            check.put("foodId", foodId);
            check.put("foodName", food.getName());
            check.put("available", inStock);
            result.add(check);
        }

        return result;
    }

//    // ✅ 檢查餐廳是否可接單
//    @GetMapping("/store/status/{storeId}")
//    public Map<String, Object> checkStoreStatus(@PathVariable Integer storeId) {
//        Map<String, Object> result = new HashMap<>();
//        var store = storeService.getStoreById(storeId);
//
//        if (store == null) {
//            result.put("status", "not_found");
//            result.put("message", "找不到店家");
//        } else if (store.getCanOrder() != null && !store.getCanOrder()) {
//            result.put("status", "unavailable");
//            result.put("message", "店家目前無法接單");
//        } else {
//            result.put("status", "available");
//            result.put("message", "店家可接單");
//        }
//
//        return result;
//    }
}
