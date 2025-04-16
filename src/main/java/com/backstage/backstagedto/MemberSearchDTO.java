package com.backstage.backstagedto;

 
public class MemberSearchDTO {
    private String name;
    private Integer memberId;
    private Integer organizationId;
    private Integer reviewed;
    private Integer accStat;
    
//    private Integer unclaimedMealCount;
    // 改為String類型，僅用於接收篩選參數
    private String unclaimedMealCount;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getReviewed() {
        return reviewed;
    }

    public void setReviewed(Integer reviewed) {
        this.reviewed = reviewed;
    }

    public Integer getAccStat() {
        return accStat;
    }

    public void setAccStat(Integer accStat) {
        this.accStat = accStat;
    }

//    public Integer getUnclaimedMealCount() {
//        return unclaimedMealCount;
//    }
//
//    public void setUnclaimedMealCount(Integer unclaimedMealCount) {
//        this.unclaimedMealCount = unclaimedMealCount;
//    }

    
    //字串型別的 unclaimedMealCount
    public String getUnclaimedMealCount() {
        return unclaimedMealCount;
    }
    
    public void setUnclaimedMealCount(String unclaimedMealCount) {
        this.unclaimedMealCount = unclaimedMealCount;
    }
    
    
}

