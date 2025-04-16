package com.frontcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.entity.StoreVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/registerAndLogin/storeInfo/{storeId}")
public class StorePointsRedemptionController {
    
    @GetMapping("/pointsRedemption")
    public String viewPointsRedemption(@PathVariable Integer storeId, Model model, HttpSession session) {
        // 檢查登入狀態
        StoreVO store = (StoreVO) session.getAttribute("store");
        if (store == null || !store.getStoreId().equals(storeId)) {
            return "redirect:/registerAndLogin/login";
        }
        
        // 將店家資訊添加到模型中
        model.addAttribute("store", store);
        
        // 返回模板路徑 - 修改為符合您的文件路徑
        return "storePointsRedemption/StorePointsRedemption";
    }
}