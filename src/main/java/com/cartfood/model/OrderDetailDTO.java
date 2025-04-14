package com.cartfood.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class OrderDetailDTO {

    @JsonProperty("foodName")
    private String foodName;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("pointsCost")
    private Integer pointsCost;

    @JsonProperty("createdTime")
    private Timestamp createdTime;

    @JsonProperty("note")
    private String note;

    public OrderDetailDTO() {
    }

    // Getter & Setter
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPointsCost() {
        return pointsCost;
    }

    public void setPointsCost(Integer pointsCost) {
        this.pointsCost = pointsCost;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
