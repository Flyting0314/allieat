package com.menuEdit.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.backstage.backstagrepository.FoodRepository;
import com.entity.FoodVO;
import com.menuEdit.dto.MenuEditDemo;
import com.menuEdit.service.MenuEditService;

@Component
public class MenuEditServiceImpl implements MenuEditService{
	
	@Autowired
	private FoodRepository foodRepository;

	@Override
	public List<MenuEditDemo> getSimpleFoods() {
		
		List<FoodVO> foodList = foodRepository
				.findAll()
				.stream()
				.filter(f ->f.getStatus() == 1 || f.getStatus() == 2)
				.toList();
		
		List<MenuEditDemo> result = new ArrayList<>();
		
		for (FoodVO foodVO : foodList) {
			result.add(new MenuEditDemo(foodVO.getFoodId(), foodVO.getName(), foodVO.getPhoto(), foodVO.getAmount(), foodVO.getStatus()));
		}
				
		return result;
	}
	
	
	
}
