package com.cartfood.model;

import com.entity.FoodVO;
import com.entity.OrderDetailVO;
import com.entity.OrderFoodVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.cartfood.model.OrderDetailDTO;
import com.backstage.backstagrepository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartOrderService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OrderFoodRepository orderFoodRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public OrderFoodVO saveOrderOnly(OrderFoodVO order) {
        return orderFoodRepository.save(order); // ✅ 儲存訂單主檔
    }

    public void saveOrderDetails(List<OrderDetailVO> details) {
        orderDetailRepository.saveAll(details); // ✅ 儲存訂單明細
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

    public Optional<OrderFoodVO> findOrderById(Integer orderId) {
        return orderFoodRepository.findById(orderId); // ✅ 查詢訂單
    }

    public boolean updatePickStat(Integer orderId, Integer newStatus) {
        Optional<OrderFoodVO> optionalOrder = orderFoodRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            OrderFoodVO order = optionalOrder.get();
            order.setPickStat(newStatus);
            orderFoodRepository.save(order); // ✅ 更新狀態
            return true;
        }
        return false;
    }

    public void saveEvaluation(Integer orderId, Integer rating, String comment) {
        Optional<OrderFoodVO> optionalOrder = orderFoodRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            OrderFoodVO order = optionalOrder.get();
            order.setRate(rating);
            order.setComment(comment);
            orderFoodRepository.save(order); // ✅ 儲存評價
        } else {
            throw new RuntimeException("找不到訂單 ID：" + orderId);
        }
    }

    // ✅ 原始全撈方法（仍保留）
    public List<OrderFoodVO> getAllOrders() {
        return entityManager.createQuery("SELECT o FROM OrderFoodVO o ORDER BY o.createdTime DESC", OrderFoodVO.class)
                             .getResultList();
    }

    // ✅ 新增：分頁查詢訂單
    public List<OrderFoodVO> getOrdersPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));
        return orderFoodRepository.findAll(pageable).getContent();
    }

    // ✅ 新增：查詢訂單總數
    public long getTotalOrderCount() {
        return orderFoodRepository.count();
    }
}
