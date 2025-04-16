package com.storeOrder.controller;

import com.backstage.backstagrepository.OrderFoodRepository;
import com.entity.OrderFoodVO;
import com.storeOrder.dto.StoreOrderDTO;
import com.storeOrder.dto.StoreOrderDTO.FoodItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/registerAndLogin/storeInfo/{storeId}/orders")
public class StoreOrderController {

    @Autowired
    private OrderFoodRepository orderFoodRepository;

    // 取得今天該店家的所有訂單
    @GetMapping("/today")
    public ResponseEntity<List<StoreOrderDTO>> getTodayOrders(@PathVariable Integer storeId) {
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp startOfTomorrow = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        List<OrderFoodVO> orders = orderFoodRepository.findByStoreStoreIdAndCreatedTimeBetween(storeId, startOfDay, startOfTomorrow);

        List<StoreOrderDTO> result = orders.stream().map(order -> {
            StoreOrderDTO dto = new StoreOrderDTO();
            dto.setOrderId(order.getOrderId());
            dto.setCreatedTime(order.getCreatedTime());
            dto.setServeStat(order.getServeStat());
            dto.setPickStat(order.getPickStat());

            if (order.getServeStat() != null && order.getServeStat() == 1) {
                dto.setPickupDeadline(Timestamp.from(order.getCreatedTime().toInstant().plus(1, ChronoUnit.HOURS)));
            }

            List<FoodItem> foodItems = order.getOrderDetails().stream().map(detail -> {
                FoodItem item = new FoodItem();
                item.setFoodName(detail.getFood().getName());
                item.setQuantity(detail.getAmount());
                return item;
            }).collect(Collectors.toList());

            dto.setFoodItems(foodItems);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
