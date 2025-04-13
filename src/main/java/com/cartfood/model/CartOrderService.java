package com.cartfood.model;

import com.entity.FoodVO;
import com.entity.OrderDetailVO;
import com.entity.OrderFoodVO;
import com.cartfood.model.OrderDetailDTO;
import com.backstage.backstagrepository.*;
import com.backstage.backstagrepository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartOrderService {

    @Autowired
    private OrderFoodRepository orderFoodRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public OrderFoodVO saveOrderOnly(OrderFoodVO order) {
        return orderFoodRepository.save(order); // ✅ 真正儲存訂單並回傳帶有 orderId 的物件
    }

    public void saveOrderDetails(List<OrderDetailVO> details) {
        orderDetailRepository.saveAll(details); // ✅ 一次儲存所有訂單明細
    }

    public OrderDetailDTO convertToDTO(OrderDetailVO detail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        FoodVO food = detail.getFood();

        dto.setFoodName(food != null ? food.getName() : "未知主餐");
        dto.setQuantity(detail.getAmount());
        dto.setPointsCost(detail.getPointsCost());
        dto.setCreatedTime(detail.getCreatedTime());
        dto.setNote(detail.getNote() != null ? detail.getNote() : "");
        

        return dto;
    }
}
