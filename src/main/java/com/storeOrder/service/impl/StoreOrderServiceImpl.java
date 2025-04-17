package com.storeOrder.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.OrderFoodRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.OrderDetailVO;
import com.entity.OrderFoodVO;
import com.entity.StoreVO;
import com.storeOrder.dto.StoreOrderDTO;
import com.storeOrder.service.FcmService;
import com.storeOrder.service.StoreOrderService;

@Service
public class StoreOrderServiceImpl implements StoreOrderService {

    @Autowired
    private OrderFoodRepository orderFoodRepository;
    
    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private FcmService fcmService;
    
    @Override
    public void completeOrder(Integer orderId) {
        OrderFoodVO order = orderFoodRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new RuntimeException("訂單不存在");
        }

        // 已完成檢查（serveStat = 9 訂單完成，我有更新規格書）
        if (order.getServeStat() != null && order.getServeStat() == 9) {
            throw new RuntimeException("訂單已完成，請勿重複操作");
        }

        // 加總 orderDetails 中的 amount
        List<OrderDetailVO> orderDetails = new ArrayList<>(order.getOrderDetails()); // 這邊怪怪的要小心
        int totalAmount = 0;
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetailVO detail = orderDetails.get(i);
            Integer amount = detail.getPointsCost();
            if (amount != null) {
                totalAmount += amount;
            }
        }

        // 找出店家
        StoreVO store = order.getStore();
        if (store == null) {
            throw new RuntimeException("找不到對應店家");
        }

        // 加點數
        Integer currentPoints = store.getPoints() != null ? store.getPoints() : 0;
        store.setPoints(currentPoints + totalAmount);

        // 訂單設為已完成
        order.setServeStat(9);

        storeRepository.save(store);
        orderFoodRepository.save(order);
    }
    
    @Override
    public void updateOrderStatus(Integer orderId, String type) {
        OrderFoodVO order = orderFoodRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new RuntimeException("訂單不存在");
        }

        switch (type) {
            case "archive":
                order.setServeStat(99);
                break;
            case "accept":
                order.setServeStat(1);
                break;
            case "reject":
                order.setServeStat(2);
                break;
            case "ready":
                order.setPickStat(2);
                orderFoodRepository.save(order); // 先存取餐狀態
                readyForPickup(orderId);          // 再推播通知
                break;
            case "pickedUp":
                order.setPickStat(1);
                break;
            case "notPicked":
                order.setPickStat(3);
                break;
            case "remove":
            	order.setServeStat(99);
            	break;
            default:
                throw new RuntimeException("不支援的動作類型: " + type);
        }

        orderFoodRepository.save(order);
    }

	@Override
    public List<StoreOrderDTO> getTodayOrders(Integer storeId) {
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp startOfTomorrow = Timestamp.valueOf(today.plusDays(1).atStartOfDay());
        
        List<OrderFoodVO> orders = orderFoodRepository.findByStoreStoreIdAndCreatedTimeBetween(storeId, startOfDay, startOfTomorrow);
        List<StoreOrderDTO> result = new ArrayList<>();
        
        for (OrderFoodVO order : orders) {
        	
        	Integer serveStat = order.getServeStat();
            if (serveStat != null && (serveStat == 9 || serveStat == 99)) {
                continue;
            }
            
            StoreOrderDTO dto = new StoreOrderDTO();
            dto.setOrderId(order.getOrderId());
            dto.setCreatedTime(order.getCreatedTime());
            dto.setServeStat(order.getServeStat());
            dto.setPickStat(order.getPickStat());

            if (order.getServeStat() != null && order.getServeStat() == 1) {
                Timestamp deadline = Timestamp.from(order.getCreatedTime().toInstant().plus(1, ChronoUnit.HOURS));
                dto.setPickupDeadline(deadline);
            }

            List<StoreOrderDTO.FoodItem> foodItems = new ArrayList<>();
            for (var detail : order.getOrderDetails()) {
                StoreOrderDTO.FoodItem item = new StoreOrderDTO.FoodItem();
                item.setFoodName(detail.getFood().getName());
                item.setQuantity(detail.getAmount());
                foodItems.add(item);
            }

            dto.setFoodItems(foodItems);
            result.add(dto);
        }

        return result;
    }

	// fcm專用
	@Override
	public void readyForPickup(Integer orderId) {
        OrderFoodVO order = orderFoodRepository.findById(orderId).orElse(null);
        if (order != null) {
            // 更新成可取餐
            order.setPickStat(2); // 你原本的 pickStat=2 就是可取餐
            orderFoodRepository.save(order);

            // 推播通知
            String fcmToken = order.getFcmToken();
            if (fcmToken != null) {
                fcmService.sendPickupNotification(fcmToken, order.getStore().getName(), order.getOrderId());
            }
        }
    }
	
	
}

