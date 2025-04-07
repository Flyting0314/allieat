package com.entity;

public class MemberOrderRecordDTO {
	private String restaurantName;
    private String transactionDate;
    private String pointAmount;

    public MemberOrderRecordDTO(String restaurantName, String transactionDate, String pointAmount) {
        this.restaurantName = restaurantName;
        this.transactionDate = transactionDate;
        this.pointAmount = pointAmount;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getPointAmount() {
        return pointAmount;
    }

    public void setPointAmount(String pointAmount) {
        this.pointAmount = pointAmount;
    }
}