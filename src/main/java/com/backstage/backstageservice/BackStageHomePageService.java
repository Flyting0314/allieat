package com.backstage.backstageservice;

import com.backstage.backstagedto.ChartDTO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

public interface BackStageHomePageService {

    ResponseEntity<Map<String, Object>> getTotalDonations();
    ResponseEntity<Map<String, Object>>getTotalDonors();
    ResponseEntity<Map<String, Object>> getMonthlyDonations();
    ResponseEntity<Map<String, Object>>getNewDonors();
    ResponseEntity<Map<String, Object>> getDonationChart();
    ResponseEntity<Map<String, Object>> getUsageChart();
}
