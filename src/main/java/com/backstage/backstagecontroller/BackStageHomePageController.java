package com.backstage.backstagecontroller;


import com.backstage.backstageservice.BackStageHomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/backStage")

public class BackStageHomePageController {
    @Autowired
    private BackStageHomePageService backStageHomePageService;

    @GetMapping("/homePage/totalDonations")
    public ResponseEntity<Map<String, Object>> totalDonations() {
        return  ResponseEntity.ok(backStageHomePageService.getTotalDonations());
    }
    @GetMapping("/homePage/totalDonors")
    public ResponseEntity<Map<String, Object>> totalDonors() {
        return ResponseEntity.ok(backStageHomePageService.getTotalDonors());
    }

    @GetMapping("/homePage/monthlyDonations")
    public ResponseEntity<Map<String, Object>> monthlyDonations() {
        return ResponseEntity.ok(backStageHomePageService.getMonthlyDonations());
    }

    @GetMapping("/homePage/newDonors")
    public ResponseEntity<Map<String, Object>> newDonors() {
        return ResponseEntity.ok(backStageHomePageService.getNewDonors());
    }

    @GetMapping("/homePage/donationChart")
    public ResponseEntity<Map<String, Object>> donationChart() {
        return ResponseEntity.ok(backStageHomePageService.getDonationChart());
    }

    @GetMapping("/homePage/usageChart")
    public ResponseEntity<Map<String, Object>> usageChart() {
        return ResponseEntity.ok(backStageHomePageService.getUsageChart());
    }
    
    //長輪詢控制器
    @GetMapping("/homePage/watch")
    public DeferredResult<String> watchDonation() {
        DeferredResult<String> reply = new DeferredResult<>(30_000L, "timeout");
        backStageHomePageService.registerListener(reply);
        return reply;
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleLocalException(Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "伺服器內部錯誤");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }



}
