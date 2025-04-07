package com.food.dto;

public class FoodDemo {

    private Integer foodId;
    private String foodName;
    private String photoPath;
	private Integer pointCost;

    public FoodDemo(Integer foodId, String foodName, String photoPath, Integer pointCost) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.photoPath = photoPath;
        this.pointCost = pointCost;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

	public Integer getPointCost() {
		return pointCost;
	}

	public void setPointCost(Integer pointCost) {
		this.pointCost = pointCost;
	}
    
}

