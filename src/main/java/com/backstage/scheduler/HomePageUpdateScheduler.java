package com.backstage.scheduler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.backstage.backstageservice.BackStageHomePageService;
import com.backstage.backstageservice.BackStageHomePageServiceImpl;

/*
 *後台HomePage圖卡長輪詢之排程器，固定10秒輪詢資料庫，
 *資料有變化時觸發checkForUpdate。
 */
@Component
public class HomePageUpdateScheduler {

    @Autowired
    private BackStageHomePageService backStageHomePageService; 
    
    @Scheduled(fixedDelay = 10000)
    public void checkDonation() {
    	backStageHomePageService.checkForUpdate();
    }
}