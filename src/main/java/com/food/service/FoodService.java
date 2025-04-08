package com.food.service;

import java.util.List;

import com.entity.FoodVO;
import com.food.dto.FoodDemo;
import com.food.dto.FoodRequest;

import jakarta.validation.Valid;

public interface FoodService {

	Integer createFood(@Valid FoodRequest foodRequest);

	FoodVO getFoodById(Integer foodId);
	
	List<FoodDemo> getSimpleFoodsByStoreId(Integer storeId);
//	List<FoodDemo> getSimpleFoods();

	void createAttachedList(Integer foodId, List<String> sideDishList);
	
	void deleteFood(FoodVO foodVO);

	void updateFood(FoodVO foodVO);

}
