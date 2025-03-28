package com.backstage.backstageservice;

import com.backstage.backstagedto.ChartDTO;
import com.backstage.backstagrepository.DonorRepository;
import com.backstage.backstagrepository.DonationRepository;
import com.backstage.backstagrepository.OrderFoodRepository;
import com.backstage.backstagrepository.TotalAmountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackStageHomePageServiceImpl implements BackStageHomePageService {

    @Autowired
    private DonationRepository accDona;
    @Autowired
    private DonorRepository donor;
    @Autowired
    private TotalAmountRepository totalAmount;
    @Autowired
    private OrderFoodRepository monthlyRecipients;

    @Override
    public ResponseEntity<Map<String, Object>> getTotalDonations() {
        Map<String, Object> result = new HashMap<>();
        try {
            //查詢最後一筆捐款總額
            result.put("totalDonations", accDona.findTopByOrderByRankIdDesc().getAmount());
        } catch (Exception e) {
            result.put("error", "查詢總捐款金額失敗");
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getTotalDonors() {
        Map<String, Object> result = new HashMap<>();
        try {
            //查詢總捐款人有多少
            result.put("totalDonors", donor.count());
        } catch (Exception e) {
            result.put("error", "查詢總捐款人數失敗");
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getMonthlyDonations() {
        Map<String, Object> result = new HashMap<>();
        try {
            //最新的累計值
            result.put("monthlyDonations", totalAmount.Now()-totalAmount.LastMonth());
        } catch (Exception e) {
            result.put("error", "查詢當月捐款金額失敗");
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getNewDonors() {
        Map<String, Object> result = new HashMap<>();
        try {
            //最近新增的捐款人
            result.put("newDonors", donor.countDonorLastMonth());
        } catch (Exception e) {
            result.put("error", "查詢新增捐款人失敗");
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDonationChart() {
        Map<String, Object> result = new HashMap<>();
        try {
            //最近12個月累積捐款圖表
            List<Object[]> test = totalAmount.findMonthlyTotalAmounts();
            List<String> labels = new ArrayList<>();
            List<BigDecimal> data = new ArrayList<>();

            for (Object[] it : test) {
                labels.add((String) it[0]);
                data.add((BigDecimal) it[1]);
            }
            //資料封裝
            ChartDTO chartDTO = new ChartDTO(labels, data);
            result.put("donationChart", chartDTO);
        } catch (Exception e) {
            result.put("error", "查詢捐款圖表資料失敗");
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getUsageChart() {
        Map<String, Object> result = new HashMap<>();
        try {
            //最近12個月的領用情況
            List<Object[]> test = monthlyRecipients.findMonthlyPickedOrders();
            List<String> labels = new ArrayList<>();
            List<Long> data = new ArrayList<>();
            for (Object[] it : test) {
                labels.add((String) it[0]);
                data.add((Long) it[1]);
            }
            //資料封裝
            ChartDTO chartDTO1 = new ChartDTO(labels, data);
            result.put("usageChart", chartDTO1);
        } catch (Exception e) {
            result.put("error", "查詢領用圖表資料失敗");
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
