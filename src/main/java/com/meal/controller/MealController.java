package com.meal.controller;

import com.food.model.FoodService;
import com.entity.FoodVO;
import com.store.model.StoreService;
import com.entity.StoreVO;
import com.attached.model.AttachedService;
import com.entity.AttachedVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController  // 使用 RestController，回傳 JSON
@RequestMapping("/meal")  // REST API 的路徑
public class MealController {

    @Autowired
    private FoodService foodService;

    @Autowired
    private AttachedService attachedService;

    @Autowired
    private StoreService storeService;  // 假設這是餐廳相關的服務

    // 取得餐廳詳細資訊
    @GetMapping("/restaurant/{id}")
    public StoreVO getRestaurantInfo(@PathVariable Integer id) {
        return storeService.getStoreById(id);
    }

    // 取得所有主餐
    @GetMapping("/food")
    public List<FoodVO> getFoodList() {
        return foodService.getAllFoods();  // 回傳 JSON 格式的資料
    }

    @GetMapping("/food/store/{storeId}")
    public List<FoodVO> getFoodListByStoreId(@PathVariable Integer storeId) {
        return foodService.getFoodsByStoreId(storeId);
    }

    @GetMapping("/attached/food/{foodId}")
    public List<AttachedVO> getAttachedListByFoodId(@PathVariable Integer foodId) {
        return attachedService.getAttachedByFoodId(foodId);
    }

    // 取得所有附餐
    @GetMapping("/attached")
    public List<AttachedVO> getAttachedList() {
        return attachedService.getAllAttached();
    }

    // 附餐頁顯示主餐資訊
    @GetMapping("/food/{foodId}")
    public FoodVO getFoodById(@PathVariable Integer foodId) {
        return foodService.getFoodById(foodId);
    }

    // 根據購物車資訊回傳詳細名稱與價格
    @PostMapping("/cart/details")
    public List<Map<String, Object>> getCartDetails(@RequestBody List<Map<String, Object>> cartItems) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> item : cartItems) {
            Integer foodId = (Integer) item.get("foodId");
            Integer attachedId = item.get("attachedId") != null ? (Integer) item.get("attachedId") : null;
            Integer quantity = item.get("quantity") != null ? (Integer) item.get("quantity") : 1;

            FoodVO food = foodService.getFoodById(foodId);
            AttachedVO attached = null;
            if (attachedId != null) {
                attached = attachedService.getAttachedById(attachedId).orElse(null);
            }

            Map<String, Object> entry = new HashMap<>();
            entry.put("foodId", foodId);
            entry.put("attachedId", attachedId);
            entry.put("foodName", food.getName());
            entry.put("attachedName", attached != null ? attached.getName() : "無");
            entry.put("cost", food.getCost());
            entry.put("quantity", quantity);

            // ✨加上店家資訊，讓 cart.html 可以 "繼續點餐"
            if (food.getStore() != null) {
                entry.put("storeId", food.getStore().getStoreId());
                entry.put("storeName", food.getStore().getName());
            } else {
                entry.put("storeId", null);
                entry.put("storeName", "未知餐廳");
            }

            result.add(entry);
        }

        return result;
    }


    }

