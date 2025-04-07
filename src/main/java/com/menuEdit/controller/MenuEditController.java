package com.menuEdit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	@GetMapping("/findAll")
	@ResponseBody
	public ResponseEntity<List<MenuEditDemo>> getAllFoods(){
		List<MenuEditDemo> foodList = menuEditService.getSimpleFoods();
		return ResponseEntity.ok(foodList);
	}
	
	@PostMapping("/update")
	@ResponseBody
	public ResponseEntity<?> updateSingleMenuItem(@RequestBody MenuEditDemo item) {
	    try {
	        menuEditService.updateMenuItem(item); // 單筆更新
	        return ResponseEntity.ok().body("{\"message\": \"更新成功\"}");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("{\"error\": \"更新失敗\"}");
	    }
	}


}
