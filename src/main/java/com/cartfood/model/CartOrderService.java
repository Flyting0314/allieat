package com.cartfood.model;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.OrderDetailRepository;
import com.backstage.backstagrepository.OrderFoodRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.OrderDetailVO;
import com.entity.OrderFoodVO;

@Service
public class CartOrderService {

    @Autowired
    private OrderDetailRepository orderDetailRepo;

    @Autowired
    private OrderFoodRepository orderRepo;

    @Autowired
    private StoreRepository storeRepo;

    @Autowired
    private MemberRepository memberRepo;

    // 建立訂單
    public OrderFoodVO createOrder(Integer memberId, Integer storeId, Integer pickStat, Integer serveStat) {
        OrderFoodVO order = new OrderFoodVO();

        order.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        order.setPickStat(pickStat);
        order.setServeStat(serveStat);
        order.setRate(0); // 預設未評價
        order.setComment(null); // 預設無評論

        order.setStore(storeRepo.findById(storeId).orElse(null));
        order.setMember(memberRepo.findById(memberId).orElse(null));

        return orderRepo.save(order);
    }

    // 儲存主訂單與明細
    public void saveOrder(OrderFoodVO order, List<OrderDetailVO> details) {
        OrderFoodVO savedOrder = orderRepo.save(order); // 先存主訂單，拿到 orderId

        for (OrderDetailVO detail : details) {
            detail.setOrder(savedOrder); // 關聯主訂單
        }

        orderDetailRepo.saveAll(details); // 再儲存所有明細
    }

    // 若只想儲存主訂單
    public OrderFoodVO saveOrderOnly(OrderFoodVO order) {
        return orderRepo.save(order);
    }

    // 單獨儲存明細
    public void saveOrderDetails(List<OrderDetailVO> details) {
        orderDetailRepo.saveAll(details);
    }
}
