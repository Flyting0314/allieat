package com.storeOrder.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backstage.backstagrepository.OrderFoodRepository;
import com.entity.OrderFoodVO;

@RestController
@RequestMapping("/api/fcm")
public class FcmController {

    @Autowired
    private OrderFoodRepository orderFoodRepository;

    @PostMapping("/register-token")
    public ResponseEntity<String> registerFcmToken(@RequestBody FcmRegisterRequest request) {
        System.out.println("✅ 收到傳送token請求！orderId=" + request.getOrderId() + ", token=" + request.getToken());

        Optional<OrderFoodVO> orderOpt = orderFoodRepository.findById(request.getOrderId());
        if (orderOpt.isPresent()) {
            OrderFoodVO order = orderOpt.get();
            System.out.println("✅ 找到訂單，原本fcm_token=" + order.getFcmToken());
            order.setFcmToken(request.getToken());
            orderFoodRepository.save(order);
            System.out.println("✅ 新fcm_token已更新為：" + order.getFcmToken());
            return ResponseEntity.ok("Token已註冊");
        } else {
            System.out.println("❌ 找不到訂單，orderId=" + request.getOrderId());
            return ResponseEntity.notFound().build();
        }
    }

    // 小小內部用的class，專門收前端傳來的 JSON
    public static class FcmRegisterRequest {
        private Integer orderId;
        private String token;

        // Getter and Setter
        public Integer getOrderId() {
            return orderId;
        }

        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
