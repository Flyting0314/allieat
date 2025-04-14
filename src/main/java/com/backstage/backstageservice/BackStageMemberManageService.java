package com.backstage.backstageservice;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.MemberRepository;
import com.entity.MemberVO;
import com.entity.OrganizationVO;
import com.frontservice.OrganizationService;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class BackStageMemberManageService {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private OrganizationService organizationService; 

    // 新增會員
    @Transactional
    public MemberVO addMember(MemberVO memberVO) {
        return memberRepository.save(memberVO);
    }

    // 更新會員
    @Transactional
    public MemberVO updateMember(MemberVO memberVO) {
        // 檢查是否存在該會員
        if (!memberRepository.existsById(memberVO.getMemberId())) {
            throw new RuntimeException("找不到會員");
        }
        return memberRepository.save(memberVO);
    }

    // 刪除會員
    @Transactional
    public void deleteMember(Integer memberId) {
        if (memberRepository.existsById(memberId)) {
            memberRepository.deleteById(memberId);
        } else {
            throw new RuntimeException("找不到會員");
        }
    }

    // 查詢單個會員
    public MemberVO getMemberById(Integer memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    // 查詢多個會員
    public List<MemberVO> getMembersByIds(List<Integer> memberIds) {
        return memberRepository.findAllById(memberIds);
    }

    // ======= 查詢所有會員（方法有調整）==========
    public List<MemberVO> getAllMembers() {
        List<MemberVO> members = memberRepository.findAll();
        
        // 手動載入在冊單位信息
        members.forEach(member -> {
            // 添加詳細的日誌記錄
            System.out.println("Processing member: " + member.getName());
            System.out.println("Current organization: " + member.getOrganization());
            
            // 獲取 organizationId
            Integer orgId = member.getOrganization() != null 
                ? member.getOrganization().getOrganizationId() 
                : null;
            
            System.out.println("OrganizationId: " + orgId);
            
            // 如果 organization 為空且 organizationId 不為空，嘗試查詢
            if (member.getOrganization() == null && orgId != null) {
                try {
                    OrganizationVO org = organizationService.getOneOrganization(orgId);
                    System.out.println("Fetched organization: " + (org != null ? org.getName() : "null"));
                    member.setOrganization(org);
                } catch (Exception e) {
                    System.err.println("Error fetching organization for member " + member.getName() + ": " + e.getMessage());
                }
            }
        });
        
        return members;
    }

    
    
    // 更新會員點數
    @Transactional
    public void updatePointsBalance(Integer memberId, Integer additionalPoints) {
        MemberVO member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("找不到 member"));

        if (member.getPointsBalance() + additionalPoints < 0) {
            throw new RuntimeException("會員的點數不足，無法執行此操作！");
        }

        member.setPointsBalance(member.getPointsBalance() + additionalPoints);
        memberRepository.save(member);
    }
    
   
    
    
}

