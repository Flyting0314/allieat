package com.cartfood.model;

import java.sql.Timestamp;
import java.util.List;

public class CarFoodDTO {

    private Integer foodId;
    private String name;
    private Timestamp createdTime;
    private Integer status;
    private Integer amount;
    private String photo;
    private Integer cost;
    private Integer storeId;
    private String storeName;
    private String foodImage;

    private List<String> attachedNames; // 假設附餐名稱就好，避免傳整個對象

    public CarFoodDTO() {}

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer string) {
        this.foodId = string;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<String> getAttachedNames() {
        return attachedNames;
    }

    public void setAttachedNames(List<String> attachedNames) {
        this.attachedNames = attachedNames;
    }
    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }
}
