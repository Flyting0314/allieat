package com.storeOrder.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        
        String fcmToken = order.getFcmToken(); // 取fcmToken
        String storeName = order.getStore().getName(); // 店名
        
        switch (type) {
            case "archive":
                order.setServeStat(99);
                break;
            case "accept":
                order.setServeStat(1);
                order.setPickStat(0);
                break;
            case "reject":
                order.setServeStat(2);
                if (fcmToken != null) {
                    fcmService.sendNotification(fcmToken, "訂單被拒絕", "很抱歉，您的訂單被「" + storeName + "」拒絕了，期待下次再為您服務！");
                }
                break;
            case "ready":
                order.setPickStat(2);
                if (fcmToken != null) {
                    fcmService.sendNotification(fcmToken, "餐點可取餐囉！", storeName + "準備好了！取餐編號：" + order.getOrderId());
                }
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

            if (order.getPickStat() == 2 && order.getServeStat() == 1) {
                Timestamp deadline = Timestamp.from(java.time.Instant.now().plus(1, ChronoUnit.HOURS));
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

	@Override
	public String updateOpStat(Integer storeId, String type) {
		
	    Optional<StoreVO> optionalStore = storeRepository.findById(storeId);

	    StoreVO store = optionalStore.get();

	    switch (type) {
	        case "cancel":
	            store.setOpStat(2); // 忙碌中
	            storeRepository.save(store);
	            return "已切換為忙碌中，暫停接單";
	        case "resume":
	            store.setOpStat(0); // 營業中
	            storeRepository.save(store);
	            return "已恢復正常接單";
	        default:
	            throw new IllegalArgumentException("無效的操作類型: " + type);
	    }
	}
	
}

