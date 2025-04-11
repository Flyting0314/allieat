package com.meal.controller;

import com.entity.FoodVO;
import com.store.model.StoreService;
import com.entity.StoreVO;
import com.attached.model.AttachedService;
import com.cartfood.model.CarFoodDTO;
import com.cartfood.model.CartFoodService;
import com.cartfood.model.CartOrderService;
import com.cartfood.model.MemberServiceImpl;
import com.entity.AttachedVO;
import com.entity.OrderDetailId;
import com.entity.OrderDetailVO;
import com.entity.OrderFoodVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/meal")
public class MealController {

    @Autowired
    private CartFoodService foodService;

    @Autowired
    private AttachedService attachedService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private MemberServiceImpl memberService;

    @Autowired
    private CartOrderService orderService;

    @GetMapping("/restaurant/{id}")
    public StoreVO getRestaurantInfo(@PathVariable Integer id) {
        return storeService.getStoreById(id);
    }

    @GetMapping("/food")
    public List<FoodVO> getFoodList() {
        return foodService.getAllFoods();
    }

    @GetMapping("/food/store/{storeId}")
    public List<FoodVO> getFoodListByStoreId(@PathVariable Integer storeId) {
        return foodService.getFoodsByStoreId(storeId);
    }

    @GetMapping("/attached/food/{foodId}")
    public List<AttachedVO> getAttachedListByFoodId(@PathVariable Integer foodId) {
        return attachedService.getAttachedByFoodId(foodId);
    }

    @GetMapping("/attached")
    public List<AttachedVO> getAttachedList() {
        return attachedService.getAllAttached();
    }

    @GetMapping("/food/{foodId}")
    public FoodVO getFoodById(@PathVariable Integer foodId) {
        return foodService.getFoodById(foodId);
    }

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

    @PostMapping("/cart/dto")
    public List<CarFoodDTO> getCartDTOs(@RequestBody List<Integer> foodIds) {
        List<CarFoodDTO> dtos = new ArrayList<>();
        for (Integer foodId : foodIds) {
            FoodVO food = foodService.getFoodById(foodId);
            if (food != null) {
                CarFoodDTO dto = foodService.convertToDTO(food);
                dtos.add(dto);
            }
        }
        return dtos;
    }

    @GetMapping("/food/store/dto")
    public List<CarFoodDTO> getFoodDTOsByStoreId(@RequestParam Integer storeId) {
        List<FoodVO> foodList = foodService.getFoodsByStoreId(storeId);
        List<CarFoodDTO> dtoList = new ArrayList<>();
        for (FoodVO food : foodList) {
            CarFoodDTO dto = foodService.convertToDTO(food);
            dto.setFoodImage(food.getPhoto());
            dtoList.add(dto);
        }
        return dtoList;
    }

    @PostMapping("/order/submit")
    public Map<String, Object> submitOrder(@RequestBody Map<String, Object> orderData) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 取得基本資料
            Integer storeId = (Integer) orderData.get("storeId");
            Integer memberId = (Integer) orderData.get("memberId");
            Boolean pickStat = (Boolean) orderData.getOrDefault("pickStat", false);
            Boolean serveStat = (Boolean) orderData.getOrDefault("serveStat", false);

            // 2. 建立主訂單
            OrderFoodVO order = new OrderFoodVO();
            order.setPickStat(pickStat);
            order.setServeStat(serveStat);
            order.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            order.setStore(storeService.getStoreById(storeId));
            order.setMember(memberService.getMemberById(memberId));

            OrderFoodVO savedOrder = orderService.saveOrderOnly(order);
            Integer generatedOrderId = savedOrder.getOrderId();

            // 3. 處理訂單明細
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
            List<OrderDetailVO> details = new ArrayList<>();

            for (Map<String, Object> item : items) {
                Integer foodId = (Integer) item.get("foodId");
                Integer quantity = (Integer) item.getOrDefault("quantity", 1);

                OrderDetailVO detail = new OrderDetailVO();
                OrderDetailId detailId = new OrderDetailId();
                detailId.setOrderId(generatedOrderId);
                detailId.setFoodId(foodId);
                detail.setId(detailId);

                FoodVO food = foodService.getFoodById(foodId); // ⚠ 為了多次使用這裡提早取出
                detail.setOrder(savedOrder);
                detail.setFood(food);
                detail.setAmount(quantity);
                detail.setCreatedTime(new Timestamp(System.currentTimeMillis()));

                // ✅ 設定 pointsCost，避免資料庫報錯
                detail.setPointsCost(food.getCost());

                details.add(detail);
            }

            

            // 4. 儲存明細
            orderService.saveOrderDetails(details);

            // 5. 回傳給 success.html
            result.put("storeName", savedOrder.getStore().getName());
            result.put("address", savedOrder.getStore().getAddress());
            result.put("pickupCode", "取餐編號-" + savedOrder.getOrderId());
            result.put("pickupTime", savedOrder.getCreatedTime().toLocalDateTime().plusMinutes(30).toString());

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", "❌ 建立訂單失敗：" + e.getMessage());
            return result;
        }
    }
}