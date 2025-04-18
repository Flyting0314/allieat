package com.storeOrder.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.storeOrder.dto.StoreOrderDTO;
import com.storeOrder.service.StoreOrderService;

@RestController
@RequestMapping("/registerAndLogin/storeInfo/{storeId}/orders")
public class StoreOrderController {

    @Autowired
    private StoreOrderService storeOrderService ;

    // 取得"今天"該店家的所有訂單
    @GetMapping("/today")
    public ResponseEntity<List<StoreOrderDTO>> getTodayOrders(@PathVariable Integer storeId) {
        List<StoreOrderDTO> result = storeOrderService.getTodayOrders(storeId);
        return ResponseEntity.ok(result);
    }
    
    // 訂單完成
    @PutMapping("/{orderId}/complete")
    public ResponseEntity<String> completeOrder(
            @PathVariable Integer storeId,
            @PathVariable Integer orderId) {
        storeOrderService.completeOrder(orderId);
        return ResponseEntity.ok("訂單完成並已加點數");
    }
    
    // 修改訂單狀態，
    @PutMapping("/{orderId}/{type}")
    public ResponseEntity<String> updateOrderStatus(
        @PathVariable Integer storeId,
        @PathVariable Integer orderId,
        @PathVariable String type) {
        storeOrderService.updateOrderStatus(orderId, type);
        return ResponseEntity.ok("訂單狀態更新成功: " + type);
    }
    
    // 忙碌中切換用
    @PutMapping("/changeAccepting/{type}")
    public ResponseEntity<String> changeAcceptingStatus(
            @PathVariable Integer storeId,
            @PathVariable String type) {
    	String message = storeOrderService.updateOpStat(storeId, type);
        return ResponseEntity.ok(message);
    }
}
