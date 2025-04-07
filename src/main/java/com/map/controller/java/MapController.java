package com.map.controller.java;

import com.entity.StoreVO;
import com.map.model.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/map")
public class MapController {

    @Autowired
    private MapService mapService;

    @GetMapping
    public String mapPage(Model model) {
        // 從 Service 取得所有餐廳資料
        List<StoreVO> storeList = mapService.findAll();

        // 加入到 Thymeleaf 的 model 裡
        model.addAttribute("storeList", storeList);

        // 回傳 Thymeleaf 的模板名稱（例如 /templates/map/map.html）
        return "map/map";
    }
}
