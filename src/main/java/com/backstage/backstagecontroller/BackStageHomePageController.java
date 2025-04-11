package com.backstage.backstagecontroller;

import com.backstage.backstageservice.BackStageHomePageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

@RestController
@RequestMapping("/backStage")

public class BackStageHomePageController {
    @Autowired
    private BackStageHomePageServiceImpl backStageHomePageServiceImpl;

    @GetMapping("/homePage/totalDonations")
    public ResponseEntity<Map<String, Object>> totalDonations() {
        return backStageHomePageServiceImpl.getTotalDonations();
    }
    @GetMapping("/homePage/totalDonors")
    public ResponseEntity<Map<String, Object>> totalDonors() {
        return backStageHomePageServiceImpl.getTotalDonors();
    }

    @GetMapping("/homePage/monthlyDonations")
    public ResponseEntity<Map<String, Object>> monthlyDonations() {
        return backStageHomePageServiceImpl.getMonthlyDonations();
    }

    @GetMapping("/homePage/newDonors")
    public ResponseEntity<Map<String, Object>> newDonors() {
        return backStageHomePageServiceImpl.getNewDonors();
    }

    @GetMapping("/homePage/donationChart")
    public ResponseEntity<Map<String, Object>> donationChart() {
        return backStageHomePageServiceImpl.getDonationChart();
    }

    @GetMapping("/homePage/usageChart")
    public ResponseEntity<Map<String, Object>> usageChart() {
        return backStageHomePageServiceImpl.getUsageChart();
    }
    
    //長輪詢控制器
    @GetMapping("/homePage/watch")
    public DeferredResult<String> watchDonation() {
        DeferredResult<String> reply = new DeferredResult<>(30_000L, "timeout");
        backStageHomePageServiceImpl.registerListener(reply);
        return reply;
    }
    



}
