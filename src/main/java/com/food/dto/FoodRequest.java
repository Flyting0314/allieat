package com.food.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;


public class FoodRequest {
	
	@NotNull
	private Integer storeId;
	
	@NotNull
	private String foodName;

	@NotNull
	private Integer pointCost;
	
	@NotNull
	private String photoPath;
	
	private List<String> sideDish;
	
	
	
	public Integer getStoreId() {
		return storeId;
	}


	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}


	public String getPhotoPath() {
		return photoPath;
	}


	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}


	public String getFoodName() {
		return foodName;
	}


	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}


	public Integer getPointCost() {
		return pointCost;
	}


	public void setPointCost(Integer pointCost) {
		this.pointCost = pointCost;
	}


	public List<String> getSideDish() {
		return sideDish;
	}


	public void setSideDish(List<String> sideDish) {
		this.sideDish = sideDish;
	}


	
}
