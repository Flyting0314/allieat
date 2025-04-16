package com.storeOrder.dto;

import java.sql.Timestamp;
import java.util.List;

public class StoreOrderDTO {
    private Integer orderId;
    private Timestamp createdTime;
    private List<FoodItem> foodItems;
    private Integer serveStat;
    private Integer pickStat;
    private Timestamp pickupDeadline;

    // ===== 餐點項目內部類別 =====
    public static class FoodItem {
        private String foodName;
        private Integer quantity;

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
    }

    // ===== Getter / Setter =====
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    public Integer getServeStat() {
        return serveStat;
    }

    public void setServeStat(Integer serveStat) {
        this.serveStat = serveStat;
    }

    public Integer getPickStat() {
        return pickStat;
    }

    public void setPickStat(Integer pickStat) {
        this.pickStat = pickStat;
    }

    public Timestamp getPickupDeadline() {
        return pickupDeadline;
    }

    public void setPickupDeadline(Timestamp pickupDeadline) {
        this.pickupDeadline = pickupDeadline;
    }
}
