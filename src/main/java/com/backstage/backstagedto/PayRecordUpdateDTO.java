package com.backstage.backstagedto;


import java.io.Serializable;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.AssertTrue;
//import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;

public class PayRecordUpdateDTO implements Serializable {
	
    @NotNull
    private Integer payoutId;

    @NotNull(message = "發放點數不能為空")
    @Positive(message = "發放點數必須大於 0")
    @Max(value = 100000, message = "點數不能超過100000")
    private Integer payoutPoints;

    // 自定義驗證
    @AssertTrue(message = "點數必須是整數")
    public boolean isValidPayoutPoints() {
        return payoutPoints != null && 
               payoutPoints > 0 && 
               payoutPoints <= 100000 && 
               payoutPoints.doubleValue() == payoutPoints;
    }
    
    private String scheduleTime;

    private Integer status; // 使用 Integer 來接收 status（後端轉為 Status 枚舉）

    // ======= Getter/Setter =======
    public Integer getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(Integer payoutId) {
        this.payoutId = payoutId;
    }

    public Integer getPayoutPoints() {
        return payoutPoints;
    }

    public void setPayoutPoints(Integer payoutPoints) {
        this.payoutPoints = payoutPoints;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PayRecordDTO [payoutId=" + payoutId + ", payoutPoints=" + payoutPoints
                + ", scheduleTime=" + scheduleTime + ", status=" + status + ']';
    }
}
