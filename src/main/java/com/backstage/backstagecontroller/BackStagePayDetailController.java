package com.backstage.backstagecontroller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.backstage.backstagedto.PayDetailWithMemberDTO;
import com.backstage.backstageservice.BackStagePayDetailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("/paydetails")
public class BackStagePayDetailController {

    @Autowired
    private BackStagePayDetailService payDetailService;

    
    
    // ========= 根據 payoutId 查詢相應的 PayDetail 列表，同時支援動態查詢（複合查詢）功能 ======== 
    @GetMapping("/getByPayRecord/{payoutId}")
    public ResponseEntity<List<PayDetailWithMemberDTO>> getPayDetailsByPayRecord(
        @PathVariable("payoutId") Integer payoutId,
        @RequestParam(required = false) Integer memberId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String pointsStatus
    ) {
        List<PayDetailWithMemberDTO> details;
        
        // 當沒有任何篩選參數時，返回指定 payoutId 的所有資料
        if (memberId == null && (name == null || name.trim().isEmpty()) && (pointsStatus == null || pointsStatus.trim().isEmpty())) {
            details = payDetailService.getPayDetailsByPayRecord(payoutId);
        } else {
            // 有篩選參數時，執行動態查詢
            details = payDetailService.searchPayDetailsDirectDTO(memberId, name, pointsStatus, payoutId);
        }
        
        return ResponseEntity.ok(details);
    }

    

    
    // =======人工補發點數時，更新 payDetail.pointsExpensed, 更新 payRecord.totalPoints, 更新 member.pointsBalance ======   
    @PostMapping("/update-points")
    @ResponseBody
    public Map<String, Object> updatePoints(@RequestBody PayDetailWithMemberDTO request) {
        try {
            // 觀察取得的明細id和發放點數
            System.out.println("Received payDetailId: " + request.getPayDetailId());
            System.out.println("Received pointsExpensed: " + request.getPointsExpensed());

            //呼叫 payDetailService 的人工發放點數方法
            PayDetailWithMemberDTO updatedDetail = payDetailService.manuallyUpdatePoints(
                request.getPayDetailId(), request.getPointsExpensed());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "點數更新成功！");
            response.put("detail", updatedDetail);
            return response;

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "發生錯誤：" + e.getMessage());
            return response;
        }
    }

}

