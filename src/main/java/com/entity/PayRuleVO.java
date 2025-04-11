package com.entity;


import java.sql.Time;
import java.sql.Timestamp;
import java.util.Set;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "payrule")
public class PayRuleVO implements java.io.Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payRuleId", updatable = false)
    private Integer payRuleId;
    
    @Column(name = "payoutDay", nullable = false)
    private Integer payoutDay;
    
    @Column(name = "payoutTime", nullable = false)
    private Time payoutTime;
    
    @Column(name = "payoutPoints", nullable = false)
    private Integer payoutPoints;
    
    @Column(name = "updatedAt", nullable = false, updatable = false)
    @UpdateTimestamp //確保每次更新都會自動修改時間
    private Timestamp updatedAt;
    
    @OneToMany(mappedBy = "payRuleVO", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("payoutId asc") 
    private Set<PayRecordVO> payRecords;
    
    
    
    // ======== Getters and Setters =========
    
    public Integer getPayRuleId() {
        return payRuleId;
    }

    public void setPayRuleId(Integer payRuleId) {
        this.payRuleId = payRuleId;
    }

    public Integer getPayoutDay() {
        return payoutDay;
    }

    public void setPayoutDay(Integer payoutDay) {
        this.payoutDay = payoutDay;
    }

    public Time getPayoutTime() {
        return payoutTime;
    }

    public void setPayoutTime(Time payoutTime) {
        this.payoutTime = payoutTime;
    }

    public Integer getPayoutPoints() {
        return payoutPoints;
    }

    public void setPayoutPoints(Integer payoutPoints) {
        this.payoutPoints = payoutPoints;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<PayRecordVO> getPayRecords() {
        return payRecords;
    }

    public void setPayRecords(Set<PayRecordVO> payRecords) {
        this.payRecords = payRecords;
    }
    
    @Override
    public String toString() {
        return "PayRuleVO [" +
                "payRuleId=" + payRuleId +
                ", payoutDay=" + payoutDay +
                ", payoutTime=" + payoutTime +
                ", payoutPoints=" + payoutPoints +
                ", updatedAt=" + updatedAt +
                ", payRecords=" + (payRecords != null ? payRecords.hashCode() : "null") + 
                ']';
    }
}

