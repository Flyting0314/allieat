package com.storeOrder.service;

import java.util.List;

import com.storeOrder.dto.StoreOrderDTO;


public interface StoreOrderService {
	
	List<StoreOrderDTO> getTodayOrders(Integer storeId);
	
	void completeOrder(Integer orderId);

	void updateOrderStatus(Integer orderId, String type);
	
}

