package com.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Entity
@Table(name = "pointsRedemption")
public class PointsRedemptionVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "redemptionId", updatable = false)
    private Integer redemptionId;

    @ManyToOne
    @JoinColumn(name = "storeId")
    @JsonIgnore
    private StoreVO store;
    


    @Column(name = "redemptionMonth", nullable = false)
    private java.sql.Date redemptionMonth;

    @Column(name = "pointsAmount", nullable = false)
    private Integer pointsAmount;

    @Column(name = "cashAmount")
    private Integer cashAmount;

    @Min(value = 0)
    @Max(value = 2)  // 修改為 2，以允許異常狀態
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT(1)")
    private Integer status;

    @Column(name = "paymentTime")
    private Timestamp paymentTime;

    @Column(name = "createdTime", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdTime;

    // Getters and Setters
    public Integer getRedemptionId() {
        return redemptionId;
    }

    public void setRedemptionId(Integer redemptionId) {
        this.redemptionId = redemptionId;
    }

    public StoreVO getStore() {
        return store;
    }

    public void setStore(StoreVO store) {
        this.store = store;
    }

    public Integer getStoreId() {
        if (store != null) {
            return store.getStoreId();  // 假設 StoreVO 類別中有 getStoreId() 方法
        }
        return null;
    }

    // 同樣需要為 setStoreId() 提供一個 setter 方法來支持資料綁定
    public void setStoreId(Integer storeId) {
        if (this.store == null) {
            this.store = new StoreVO();
        }
        this.store.setStoreId(storeId);
    }
    

    
    
    public java.sql.Date getRedemptionMonth() {
        return redemptionMonth;
    }

    public void setRedemptionMonth(java.sql.Date redemptionMonth) {
        this.redemptionMonth = redemptionMonth;
    }

    public Integer getPointsAmount() {
        return pointsAmount;
    }

    public void setPointsAmount(Integer pointsAmount) {
        this.pointsAmount = pointsAmount;
    }

    public Integer getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Integer cashAmount) {
        this.cashAmount = cashAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Timestamp paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    // toString method
    @Override
    public String toString() {
        return "PointsRedemptionVO{" +
                "redemptionId=" + redemptionId +
                ", store=" + (store != null ? store.hashCode() : null) +
                ", redemptionMonth=" + redemptionMonth +
                ", pointsAmount=" + pointsAmount +
                ", cashAmount=" + cashAmount +
                ", status=" + status +
                ", paymentTime=" + paymentTime +
                ", createdTime=" + createdTime +
                '}';
    }
}