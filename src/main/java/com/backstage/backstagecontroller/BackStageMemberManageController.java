package com.backstage.backstagecontroller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backstage.backstagedto.MemberSearchDTO;
import com.backstage.backstageservice.BackStageMemberManageService;
import com.backstage.backstageservice.BackstageMemberEmailService;
import com.backstage.backstagrepository.MemberRepository;
import com.entity.MemberVO;
import com.entity.OrganizationVO;
import com.frontservice.OrganizationService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;



@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/memberManagement")
public class BackStageMemberManageController {
    @Autowired
    private BackStageMemberManageService memberManageService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private BackstageMemberEmailService memberEmailService;
    
    @Autowired
    private MemberRepository memberRepository;
    
    
    // ============ 獲取在冊單位列表 ===============
    @GetMapping("/organization")
    public ResponseEntity<List<UnitDTO>> getAllOrganizations() {
        List<OrganizationVO> organizations = organizationService.getAll();
        
        List<UnitDTO> unitDTOs = organizations.stream()
            .map(org -> new UnitDTO(org.getOrganizationId(), org.getName()))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(unitDTOs);
    }
    
    // =========== 會員列表獲取與動態過濾搜尋功能 ===============
    @PostMapping("/listAll")
//    public ResponseEntity<List<MemberVO>> getAllMembers(@RequestBody(required = false) MemberSearchDTO searchDTO) {
    public ResponseEntity<?> getAllMembers(@RequestBody(required = false) MemberSearchDTO searchDTO) {
    
    	
    	// 獲取所有會員
        List<MemberVO> allMembers = memberManageService.getAllMembers();

        // 動態過濾
        List<MemberVO> filteredMembers = allMembers.stream()
            .filter(member -> {
                // 加入診斷日誌
                System.out.println("Filtering member: " + member.getName());
                System.out.println("Organization: " + 
                    (member.getOrganization() != null 
                        ? "ID=" + member.getOrganization().getOrganizationId() + ", Name=" + member.getOrganization().getName() 
                        : "null")
                );
                
                // 增加未取餐次數範圍篩選邏輯
                boolean mealsCondition = true; // 預設為true，表示不過濾
                if (searchDTO != null && searchDTO.getUnclaimedMealCount() != null) {
                    String mealFilter = searchDTO.getUnclaimedMealCount();
                    Integer memberMeals = member.getUnclaimedMealCount();
                    
                    if ("0".equals(mealFilter)) {
                        mealsCondition = memberMeals == 0;
                    } else if ("1-5".equals(mealFilter)) {
                        mealsCondition = memberMeals >= 1 && memberMeals <= 5;
                    } else if ("6-10".equals(mealFilter)) {
                        mealsCondition = memberMeals >= 6 && memberMeals <= 10;
                    } else if ("11-15".equals(mealFilter)) {
                        mealsCondition = memberMeals >= 11 && memberMeals <= 15;
                    } else if ("15+".equals(mealFilter)) {
                        mealsCondition = memberMeals > 15;
                    }
                }
        
                return (searchDTO == null ||
                       (searchDTO.getName() == null || member.getName().contains(searchDTO.getName())) &&
                       (searchDTO.getMemberId() == null || member.getMemberId().equals(searchDTO.getMemberId())) &&
                       (searchDTO.getOrganizationId() == null ||
                        (member.getOrganization() != null &&
                         member.getOrganization().getOrganizationId().equals(searchDTO.getOrganizationId()))) &&
                       (searchDTO.getReviewed() == null || member.getReviewed().equals(searchDTO.getReviewed())) &&
                       (searchDTO.getAccStat() == null || member.getAccStat().equals(searchDTO.getAccStat())) &&
                       mealsCondition);
            })
            .collect(Collectors.toList());

//        return ResponseEntity.ok(filteredMembers);
        return ResponseEntity.ok().body(filteredMembers);
    }
    
    
    
    // ============== 更新會員審核狀態 =============== 
    @PutMapping("/updateReviewStatus/{memberId}")
//    public ResponseEntity<Map<String, Object>> updateReviewStatus(
      public ResponseEntity<?> updateReviewStatus(	
    		  
            @PathVariable Integer memberId,
            @RequestBody Map<String, String> request
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            String status = request.get("status");
            Integer reviewedValue = convertAuditStatusToValue(status);
            
            MemberVO member = memberManageService.getMemberById(memberId);
            if (member == null) {
                response.put("success", false);
                response.put("message", "找不到會員");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 修改邏輯：允許已通過(1)和未通過(2)之間的轉換
            // 但已審核的會員不能改為待審核(3)狀態
            if ((member.getReviewed() == 1 || member.getReviewed() == 2) && reviewedValue == 3) {
                response.put("success", false);
                response.put("message", "已審核的會員不能改為待審核狀態");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 根據審核結果設置狀態並發送對應郵件
            if (reviewedValue == 1) { // 已通過
                member.setReviewed(1);
                // 設置為未啟用，等待用戶點擊郵件中的連結啟用
                member.setAccStat(0);
                memberEmailService.sendMemberVerificationEmail(member);
                response.put("message", "審核通過，已發送啟用郵件");
            } else if (reviewedValue == 2) { // 未通過/補件
                member.setReviewed(2);
                memberEmailService.sendRejectionEmail(member);
                response.put("message", "審核未通過，已發送通知郵件");
            } else if (reviewedValue == 3) { // 待審核
                member.setReviewed(3);
                response.put("message", "已將狀態設為待審核");
            }
            
            memberManageService.updateMember(member);
            
            response.put("success", true);
            return ResponseEntity.ok().body(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    
    // ==============驗證郵件=================
    // 此端點需要與EmailService中生成的URL匹配
    @GetMapping("/verify")
    public ResponseEntity<?> verifyMember(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();
        
        boolean activated = memberEmailService.activateMemberByToken(token);
        
        if (activated) {
            // 使用 ResponseEntity 返回重定向
            return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/registerAndLogin/login")
                .build();
        } else {
            response.put("success", false);
            response.put("message", "驗證連結無效或已過期");
        }
        
        return ResponseEntity.ok().body(response);
    }
    
    
    
    
 
    
    
    // ============ 更新帳號啟用狀態 ==============
    @PutMapping("/updateAccountStatus/{memberId}")
    public ResponseEntity<?> updateAccountStatus(
            @PathVariable Integer memberId,
            @RequestBody Map<String, String> request
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            String status = request.get("status");
            Integer accStatValue = convertAccountStatusToValue(status);
            
            MemberVO member = memberManageService.getMemberById(memberId);
            if (member == null) {
                response.put("success", false);
                response.put("message", "找不到會員");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 驗證業務邏輯
            if (member.getReviewed() != 1) {
                response.put("success", false);
                response.put("message", "只有審核通過的會員才能變更帳號狀態");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (member.getAccStat() == 0) {
                response.put("success", false);
                response.put("message", "未啟用的帳號需由會員自行啟用");
                return ResponseEntity.badRequest().body(response);
            }
            
            if ((member.getAccStat() == 1 && accStatValue != 2) || 
                (member.getAccStat() == 2 && accStatValue != 1)) {
                response.put("success", false);
                response.put("message", "帳號狀態變更不符合業務規則");
                return ResponseEntity.badRequest().body(response);
            }
            
            member.setAccStat(accStatValue);
            memberManageService.updateMember(member);
            
            response.put("success", true);
            response.put("message", "帳號狀態更新成功");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 審核狀態轉換（前端 -> 後端）
    private Integer convertAuditStatusToValue(String status) {
        switch (status) {
            // 保留pending(0)的轉換，但實際不使用
            case "pending": return 0; // 審核中
            case "approved": return 1; // 已通過
            case "rejected": return 2; // 未通過/補件
            case "waiting": return 3; // 待審核
            default: throw new IllegalArgumentException("無效的審核狀態");
        }
    }
    
    // 帳號狀態轉換（前端 -> 後端）
    private Integer convertAccountStatusToValue(String status) {
        switch (status) {
            case "not-activated": return 0; // 未啟用
            case "active": return 1; // 啟用
            case "inactive": return 2; // 停用
            default: throw new IllegalArgumentException("無效的帳號狀態");
        }
    }

    // DTO 用於返回單位信息
    public static class UnitDTO {
        private Integer id;
        private String name;

        public UnitDTO(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}