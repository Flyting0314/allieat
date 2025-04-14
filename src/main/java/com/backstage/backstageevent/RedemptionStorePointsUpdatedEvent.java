package com.backstage.backstageevent;



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