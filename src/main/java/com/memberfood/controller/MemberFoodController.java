package com.memberfood.controller;

import com.entity.FoodVO;
import com.memberfood.model.MemberFoodService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/food")
public class MemberFoodController {

    @Autowired
    private MemberFoodService foodService;

    private static final String FOOD_VIEW = "food/food";
    /**
     * 顯示初始畫面（如果只是 /food）
     */
    @GetMapping
    public String showFoodPage() {
        return FOOD_VIEW;
    }

    /**
     * 根據店家 ID 顯示對應菜單，綁定到 Thymeleaf 的 menuItems 變數
     */
    @GetMapping("/store/{storeId}")
    public String getFoodsByStore(@PathVariable("storeId") Integer storeId, Model model) {
        List<FoodVO> foodList = foodService.getFoodsByStoreId(storeId);

        // Debug 輸出確認資料有無
        System.out.println("取得店家 ID：" + storeId + " 的餐點數量：" + foodList.size());

        model.addAttribute("menuItems", foodList);
        return FOOD_VIEW;
    }

    /**
     * 提供某個餐點詳細資料（通常給 AJAX 使用）
     */
    @GetMapping("/{foodId}")
    @ResponseBody
    public FoodVO getFoodDetail(@PathVariable("foodId") Integer foodId) {
        return foodService.getFoodById(foodId);
    }
}
