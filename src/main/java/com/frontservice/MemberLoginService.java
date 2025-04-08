package com.frontservice;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    private MemberRegistAndLoginService memberService;

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
    public String handleReupload(Integer memberId, MultipartFile file) {
        try {
            Optional<MemberVO> optionalMember = memberRepository.findById(memberId);
            if (optionalMember.isEmpty()) {
                return "找不到對應的會員資料";
            }

            MemberVO member = optionalMember.get();
            if (member.getReviewed() != 2) {
                return "帳號狀態錯誤或無需補件";
            }

            String savedFile = memberService.saveKycFile(file); // 重用原有邏輯
            member.setKycImage(savedFile);
            member.setReviewed(3); // 補件後重新進入審核
            member.setRegTime(new Timestamp(System.currentTimeMillis()));

            memberService.updateMember(member);
            return "success";
        } catch (Exception e) {
            return "補件失敗：" + e.getMessage();
        }
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