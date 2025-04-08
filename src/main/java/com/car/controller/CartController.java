package com.car.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    // 建立或取得購物車
    private List<Map<String, Object>> getCart(HttpSession session) {
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    // 加入購物車
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody Map<String, Object> newItem, HttpSession session) {
        List<Map<String, Object>> cart = getCart(session);

        Integer newFoodId = (Integer) newItem.get("foodId");
        Integer newAttachedId = (Integer) newItem.get("attachedId");
        Integer newQuantity = (Integer) newItem.get("quantity");

        boolean found = false;

        for (Map<String, Object> item : cart) {
            Integer fId = (Integer) item.get("foodId");
            Integer aId = (Integer) item.get("attachedId");

            if (Objects.equals(fId, newFoodId) && Objects.equals(aId, newAttachedId)) {
                int currentQty = (int) item.get("quantity");
                item.put("quantity", currentQty + newQuantity);
                found = true;
                break;
            }
        }

        if (!found) {
            cart.add(newItem);
        }

        return ResponseEntity.ok("已加入購物車");
    }


    // 查看購物車內容
    @GetMapping
    public List<Map<String, Object>> viewCart(HttpSession session) {
        return getCart(session);
    }

    // 移除購物車指定項目（用 index）
    @DeleteMapping("/remove/{index}")
    public ResponseEntity<String> removeFromCart(@PathVariable int index, HttpSession session) {
        List<Map<String, Object>> cart = getCart(session);
        if (index >= 0 && index < cart.size()) {
            cart.remove(index);
            return ResponseEntity.ok("已移除項目");
        } else {
            return ResponseEntity.badRequest().body("索引無效");
        }
    }

    // 清空購物車
    @PostMapping("/clear")
    public ResponseEntity<String> clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return ResponseEntity.ok("購物車已清空");
    }

    // 計算總點數
    @GetMapping("/total-points")
    public Map<String, Integer> getTotalPoints(HttpSession session) {
        List<Map<String, Object>> cart = getCart(session);
        int total = 0;
        for (Map<String, Object> item : cart) {
            Object costObj = item.get("cost");
            if (costObj instanceof Number) {
                total += ((Number) costObj).intValue();
            } else if (costObj instanceof String) {
                try {
                    total += Integer.parseInt((String) costObj);
                } catch (NumberFormatException ignored) {}
            }
        }
        return Collections.singletonMap("totalPoints", total);
    }
    
    @PostMapping("/update")
    public ResponseEntity<String> updateCartItem(@RequestBody Map<String, Object> payload, HttpSession session) {
        List<Map<String, Object>> cart = getCart(session);

        if (!payload.containsKey("foodId") || !payload.containsKey("attachedId") || !payload.containsKey("quantity")) {
            return ResponseEntity.badRequest().body("缺少必要欄位");
        }

        int foodId = (int) payload.get("foodId");
        int attachedId = (int) payload.get("attachedId");
        int newQty = (int) payload.get("quantity");

        boolean updated = false;

        for (Map<String, Object> item : cart) {
            int fId = (int) item.get("foodId");
            int aId = (int) item.get("attachedId");

            if (fId == foodId && aId == attachedId) {
                item.put("quantity", newQty);
                updated = true;
                break;
            }
        }

        return updated
                ? ResponseEntity.ok("數量已更新")
                : ResponseEntity.badRequest().body("找不到對應項目");
    }
}
