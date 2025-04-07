package com.frontservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.OrderDetailRepository;
import com.backstage.backstagrepository.OrderFoodRepository;
import com.backstage.backstagrepository.PayDetailRepository;
import com.entity.MemberOrderRecordDTO;
import com.entity.MemberVO;
import com.entity.OrderDetailVO;
import com.entity.OrderFoodVO;

@Service
public class MemberLoginService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderFoodRepository orderFoodRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private PayDetailRepository payDetailRepository;


    public MemberVO login(String account, String password) {
        return memberRepository.findByAccountAndPassword(account, password);
    }

    public List<MemberOrderRecordDTO> getOrderRecords(Integer memberId) {
        List<OrderFoodVO> orders = orderFoodRepository.findByMember_MemberId(memberId);
        List<MemberOrderRecordDTO> result = new ArrayList<>();

        for (OrderFoodVO order : orders) {
            List<OrderDetailVO> details = orderDetailRepository.findByOrderId(order.getOrderId());
            for (OrderDetailVO detail : details) {
                String storeName = order.getStore().getName();
                String date = detail.getCreatedTime().toLocalDateTime().toLocalDate().toString();
                String point = "-" + detail.getPointsCost() + "點";
                result.add(new MemberOrderRecordDTO(storeName, date, point));
            }
        }

        return result;
    }

    
//    public int calculateDisplayPoints(MemberVO member) {
//        List<OrderDetailVO> orderDetails = orderDetailRepository.findByMember_MemberId(member.getMemberId());
//
//        int usedPoints = orderDetails.stream()
//            .mapToInt(OrderDetailVO::getPointsCost) // 這邊使用 orderDetail 裡的 pointsCost
//            .sum();
//
//        return member.getPointsBalance() + usedPoints; // 加回使用過的點數顯示原本擁有的
//    }

}