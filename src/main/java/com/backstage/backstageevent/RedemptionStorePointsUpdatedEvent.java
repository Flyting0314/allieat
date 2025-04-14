package com.backstage.backstageevent;

<<<<<<< HEAD
 
=======

>>>>>>> b791394 (1. 新增實體 ”店家點數核銷 PointsRedemption” 相關檔案：VO, Repository, Service,)

import org.springframework.context.ApplicationEvent;

public class RedemptionStorePointsUpdatedEvent extends ApplicationEvent {
    private final Integer storeId;
    private final Integer newPoints;

    public RedemptionStorePointsUpdatedEvent(Object source, Integer storeId, Integer newPoints) {
        super(source);
        this.storeId = storeId;
        this.newPoints = newPoints;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public Integer getNewPoints() {
        return newPoints;
    }
}