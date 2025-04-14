package com.backstage.backstageservice;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

public interface BackStageHomePageService {

    Map<String, Object> getTotalDonations();
    Map<String, Object>getTotalDonors();
    Map<String, Object> getMonthlyDonations();
    Map<String, Object>getNewDonors();
    Map<String, Object> getDonationChart();
    Map<String, Object> getUsageChart();
    void registerListener(DeferredResult<String> dr);
    void checkForUpdate();

}
