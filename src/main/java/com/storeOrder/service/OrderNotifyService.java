package com.storeOrder.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.OrderFoodVO;
import com.storeOrder.handler.OrderWebSocketHandler;

@Service
public class OrderNotifyService {

    @Autowired
    private OrderWebSocketHandler handler;

    public void notifyStoreNewOrder(OrderFoodVO order) {
        try {
            Integer storeId = order.getStore().getStoreId();
            System.out.println("📦 推播開始：storeId = " + storeId);
            String msg = "📦 新訂單通知：#" + String.format("%07d", order.getOrderId());
            handler.sendOrderToStore(storeId, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
