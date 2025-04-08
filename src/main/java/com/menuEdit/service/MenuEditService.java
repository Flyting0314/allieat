package com.menuEdit.service;

import java.util.List;

import com.menuEdit.dto.MenuEditDemo;

public interface MenuEditService {

	List<MenuEditDemo> getSimpleFoodsByStoreId(Integer storeId);
//	List<MenuEditDemo> getSimpleFoods();

	void updateMenuItem(MenuEditDemo item);


	
}
