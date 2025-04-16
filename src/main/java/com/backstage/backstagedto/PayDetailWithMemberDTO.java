package com.backstage.backstagedto;



import java.sql.Timestamp;

import com.entity.PayDetailVO;

//DTO（資料傳輸物件）的屬性名決定了 JSON 的字段名
//前端 fetch 發送的 JSON 物件中，字段名必須與後端的 DTO 屬性名完全匹配

public class PayDetailWithMemberDTO {
    private Integer payDetailId;        // 點數發放明細ID
    private Timestamp payDate;          // 點數發放時間
    private Integer memberId;           // 受助者ID
    private String name;                // 姓名
    private String organizationName;    // 在冊單位
    private String idNum;               // 身分證字號
    private String phone;               // 行動電話
    private Integer pointsExpensed;     // 本月取得點數
    private Integer payoutId;           // 點數發放ID，顯示在頁面上
    private Integer pointsBalance;   // 對應 MemberVO的點數，因為paydetail允許人工補發點數，並且會回填member的總累積點數
    private Integer payoutPoints;      // 應發放點數，用於前端判斷是否允許補發
    
    public PayDetailWithMemberDTO() {
    }

    
    public PayDetailWithMemberDTO(PayDetailVO payDetail) {
        this.payDetailId = payDetail.getPayDetailId();
        this.pointsExpensed = payDetail.getPointsExpensed();
        this.memberId = payDetail.getMember().getMemberId();
        this.pointsBalance = payDetail.getMember().getPointsBalance();
        if(payDetail.getPayRecordVO() != null) {
            this.payoutPoints = payDetail.getPayRecordVO().getPayoutPoints();
        }
    }
    
    
    
    // getters and setters
    public Integer getPayDetailId() {
        return payDetailId;
    }

    public void setPayDetailId(Integer payDetailId) {
        this.payDetailId = payDetailId;
    }

    public Timestamp getPayDate() {
        return payDate;
    }

    public void setPayDate(Timestamp payDate) {
        this.payDate = payDate;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getIdNum() {
        return idNum;
    }
 
    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPointsExpensed() {
        return pointsExpensed;
    }

    public void setPointsExpensed(Integer pointsExpensed) {
        this.pointsExpensed = pointsExpensed;
    }

    public Integer getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(Integer payoutId) {
        this.payoutId = payoutId;
    }
    
    public Integer getPointsBalance() {
        return pointsBalance;
    }

    public void setPointsBalance(Integer pointsBalance) {
        this.pointsBalance = pointsBalance;
    }
    
    
    public Integer getPayoutPoints() {
        return payoutPoints;
    }
    
    public void setPayoutPoints(Integer payoutPoints) {
        this.payoutPoints = payoutPoints;
    }
}

