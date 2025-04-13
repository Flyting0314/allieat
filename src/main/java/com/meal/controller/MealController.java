package com.meal.controller;

import com.entity.FoodVO;
import com.store.model.StoreService;
import com.entity.StoreVO;
import com.attached.model.AttachedService;
import com.cartfood.model.CarFoodDTO;
import com.cartfood.model.CartFoodService;
import com.cartfood.model.CartOrderService;
import com.cartfood.model.MemberServiceImpl;
import com.cartfood.model.OrderDetailDTO;
import com.entity.AttachedVO;
import com.entity.OrderDetailId;
import com.entity.OrderDetailVO;
import com.entity.OrderFoodVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpSession;
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
            String note = item.get("note") != null ? (String) item.get("note") : null;

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
            entry.put("note", note);

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
    public Map<String, Object> submitOrder(@RequestBody Map<String, Object> orderData, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();

        try {
            Integer storeId = (Integer) orderData.get("storeId");
            Integer memberId = (Integer) orderData.get("memberId");
            Integer pickStat = (Integer) orderData.getOrDefault("pickStat", 0);
            Integer serveStat = (Integer) orderData.getOrDefault("serveStat", 0);

            OrderFoodVO order = new OrderFoodVO();
            order.setPickStat(pickStat);
            order.setServeStat(serveStat);
            order.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            order.setStore(storeService.getStoreById(storeId));
            order.setMember(memberService.getMemberById(memberId));

            OrderFoodVO savedOrder = orderService.saveOrderOnly(order);
            Integer generatedOrderId = savedOrder.getOrderId();
            System.out.println("✅ 訂單建立成功，ID：" + generatedOrderId);

            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
            List<OrderDetailVO> details = new ArrayList<>();

            for (Map<String, Object> item : items) {
                Integer foodId = item.get("foodId") != null ? (Integer) item.get("foodId") : null;
                Integer quantity = item.get("quantity") != null ? (Integer) item.get("quantity") : 1;
                Integer pointsCost = item.get("pointsCost") != null ? (Integer) item.get("pointsCost") : null;

                if (foodId == null) {
                    errors.add("❌ 缺少 foodId，無法處理某筆訂單明細");
                    continue;
                }

                FoodVO food = foodService.getFoodById(foodId);
                if (food == null) {
                    errors.add("❌ 找不到食物編號：" + foodId);
                    continue;
                }

                OrderDetailVO detail = new OrderDetailVO();
                OrderDetailId detailId = new OrderDetailId();
                detailId.setOrderId(generatedOrderId);
                detailId.setFoodId(foodId);
                detail.setId(detailId);
                detail.setOrderFood(savedOrder);
                detail.setFood(food);
                detail.setAmount(quantity);
                detail.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                detail.setPointsCost(pointsCost != null ? pointsCost : food.getCost());
                detail.setNote(item.get("note") != null ? (String) item.get("note") : null);

                details.add(detail);
            }

            orderService.saveOrderDetails(details);
            System.out.println("✅ 所有有效訂單明細已儲存，共 " + details.size() + " 筆");

            // ✅ 清空 session 購物車
            session.removeAttribute("cart");

            StoreVO store = savedOrder.getStore();
            result.put("storeName", store.getName());
            result.put("address", store.getAddress());
            result.put("pickupCode", "取餐編號-" + savedOrder.getOrderId());
            result.put("pickupTime", null);
            result.put("storeLat", store.getLatitude());
            result.put("storeLng", store.getLongitude());

            List<OrderDetailDTO> detailDTOs = new ArrayList<>();
            for (OrderDetailVO detail : details) {
                detailDTOs.add(orderService.convertToDTO(detail));
            }
            result.put("details", detailDTOs);

            if (!errors.isEmpty()) {
                result.put("warnings", errors);
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", "❌ 建立訂單失敗：" + e.getMessage());
            return result;
        }
    }
}
