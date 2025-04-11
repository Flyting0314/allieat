package com.backstage.scheduler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.backstage.backstageservice.BackStageHomePageServiceImpl;

/*
  *用於排程固定10秒去確認資料
  */
@Component
public class HomePageUpdateScheduler {

    @Autowired
    private BackStageHomePageServiceImpl backStageHomePageServiceImpl; 
    
    @Scheduled(fixedDelay = 10000)
    public void checkDonation() {
    	backStageHomePageServiceImpl.checkForUpdate();
    }
}