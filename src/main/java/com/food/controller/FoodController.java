package com.food.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.entity.FoodVO;
import com.food.dto.FoodDemo;
import com.food.dto.FoodRequest;
import com.food.service.FoodService;

@Controller
@RequestMapping("registerAndLogin/storeInfo/{storeId}/food")
public class FoodController {

    @Autowired
    private FoodService foodService;
    
    @GetMapping
    public String foodPage(@PathVariable Integer storeId, Model model) {
        model.addAttribute("storeId", storeId);
        return "food/food";
    }

    @PostMapping("/createFood")
    @ResponseBody
    public ResponseEntity<?> createFoodWithImage(
    	@PathVariable Integer storeId,
        @RequestParam("foodName") String foodName,
        @RequestParam("pointCost") Integer pointCost,
        @RequestParam(value = "sideDish", required = false) List<String> sideDish,
        @RequestParam("photo") MultipartFile photoFile
    ) {
        try {
            Path uploadPath = Paths.get("src/main/resources/static/img/upload").toAbsolutePath();
            File uploadDir = uploadPath.toFile();

            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName = photoFile.getOriginalFilename();
            if (fileName == null || fileName.isEmpty()) {
                return ResponseEntity.badRequest().body("請上傳圖片");
            }

            File destination = new File(uploadDir, fileName);
            photoFile.transferTo(destination);

            // 建立 DTO
            FoodRequest foodRequest = new FoodRequest();
            foodRequest.setStoreId(storeId);
            foodRequest.setFoodName(foodName);
            foodRequest.setPointCost(pointCost);
            foodRequest.setPhotoPath("/img/upload/" + fileName);
            foodRequest.setSideDish(sideDish != null ? sideDish : new ArrayList<>());

            // 建立主餐
            Integer foodId = foodService.createFood(foodRequest);

            // 👉 新增副餐資料（attached）
            if (sideDish != null && !sideDish.isEmpty()) {
                foodService.createAttachedList(foodId, sideDish);
            }

            FoodVO foodVO = foodService.getFoodById(foodId);
            return ResponseEntity.status(HttpStatus.CREATED).body(foodVO);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("圖片儲存失敗");
        }
    }
    
    @PutMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteFood(
								    		 @PathVariable Integer storeId,
								    		 @PathVariable Integer id
								    		 ) {
        FoodVO foodVO = foodService.getFoodById(id);
        if (foodVO == null) {
            return ResponseEntity.notFound().build();
        }

        foodVO.setStatus(2); // 設為刪除
        foodService.deleteFood(foodVO); // 你需要在 foodService 中提供 update 方法

        return ResponseEntity.ok("Deleted");
    }
    
    @GetMapping("/findAll")
    @ResponseBody
    public ResponseEntity<List<FoodDemo>> getAllFoods(@PathVariable Integer storeId) {
        List<FoodDemo> foodList = foodService.getSimpleFoodsByStoreId(storeId); // ✅ 傳入 storeId
        return ResponseEntity.ok(foodList);
    }
    
    @PostMapping("/updateFood/{id}")
    @ResponseBody
    public ResponseEntity<?> updateFood(
    	@PathVariable Integer storeId,
        @PathVariable Integer id,
        @RequestParam("foodName") String foodName,
        @RequestParam("pointCost") Integer pointCost,
        @RequestParam("originalPhotoPath") String originalPhotoPath,
        @RequestParam(value = "photo", required = false) MultipartFile newPhoto
    ) {
        FoodVO food = foodService.getFoodById(id);
        if (food == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到該餐點");
        }

        String photoPath = originalPhotoPath;

        if (newPhoto != null && !newPhoto.isEmpty()) {
            try {
                String fileName = newPhoto.getOriginalFilename();
                Path uploadPath = Paths.get("src/main/resources/static/img/upload").toAbsolutePath();
                File dest = new File(uploadPath.toFile(), fileName);
                newPhoto.transferTo(dest);
                photoPath = "/img/upload/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("圖片上傳失敗");
            }
        }

        food.setName(foodName);
        food.setCost(pointCost);
        food.setPhoto(photoPath); // ✅ 更新圖片路徑
        foodService.updateFood(food); // ✅ 存入資料庫

        return ResponseEntity.ok().body(Map.of("success", true));
    }



    
    
}
