package com.backstage.backstagedto;


import java.sql.Time;

import com.entity.PayRuleVO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class PayRuleDTO implements Serializable {
    @NotNull(message = "發放日期不能為空")
    @Min(value = 1, message = "發放日期必須在1到28之間")
    @Max(value = 28, message = "發放日期必須在1到28之間")
    private Integer payoutDay;

    @NotNull(message = "發放時間不能為空")
    private String payoutTime; // 使用字串接收，方便前端傳遞

    @NotNull(message = "發放點數不能為空")
    @Min(value = 1, message = "發放點數必須大於0")
    @Max(value = 100000, message = "發放點數不能超過100000")
    private Integer payoutPoints;

    // 預設建構子
    public PayRuleDTO() {}

    // Getters and Setters
    public Integer getPayoutDay() {
        return payoutDay;
    }

    public void setPayoutDay(Integer payoutDay) {
        this.payoutDay = payoutDay;
    }

    public String getPayoutTime() {
        return payoutTime;
    }

    public void setPayoutTime(String payoutTime) {
        this.payoutTime = payoutTime;
    }

    public Integer getPayoutPoints() {
        return payoutPoints;
    }

    public void setPayoutPoints(Integer payoutPoints) {
        this.payoutPoints = payoutPoints;
    }

    // 轉換方法：將 DTO 轉換為 VO
    public PayRuleVO toVO() {
        PayRuleVO vo = new PayRuleVO();
        vo.setPayoutDay(this.payoutDay);
        // 將字串時間轉換為 Time 物件
        vo.setPayoutTime(Time.valueOf(this.payoutTime + ":00")); // 預設加入秒
        vo.setPayoutPoints(this.payoutPoints);
        return vo;
    }
}