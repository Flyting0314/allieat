package com.menuEdit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.menuEdit.dto.MenuEditDemo;
import com.menuEdit.service.MenuEditService;

@Controller
@RequestMapping("store/1/menuEdit")
public class MenuEditController {
	
	@Autowired
	private MenuEditService menuEditService;
	
	@GetMapping
	public String menuEditPage(Model model) {
		return "menuEdit/menuEdit";
	}
	
	public ResponseEntity<List<MenuEditDemo>> getAllFoods(){
		List<MenuEditDemo> foodList = menuEditService.getSimpleFoods();
		return ResponseEntity.ok(foodList);
	}
}
