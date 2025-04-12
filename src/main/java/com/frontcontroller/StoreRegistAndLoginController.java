package com.frontcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.entity.StoreVO;
import com.frontservice.StoreRegistAndLoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/registerAndLogin/register/store")
@SessionAttributes({"store", "photoError", "agreeWarning", "formErrors", "hasSubmitted"})

public class StoreRegistAndLoginController {

    @Autowired
    private StoreRegistAndLoginService storeService;

    @ModelAttribute("store")
    public StoreVO storeForm() {
        return new StoreVO();
    }

    @ModelAttribute("photoList")
    public List<Map<String, String>> photoList() {
        return new ArrayList<>();
    }
    
    //註冊顯示頁
    @GetMapping("")
    public String registerPage(@ModelAttribute("store") StoreVO store, HttpSession session, Model model) {
        storeService.generatePhotoPreview(store, session, model); 
        model.addAttribute("hasSubmitted", false);
        return "registerAndLogin/storeRegister";
    }
    
    //註冊
    @PostMapping("")
    public String register(@Valid @ModelAttribute("store") StoreVO store,
                           BindingResult result,                           
                           @RequestParam("photoFiles") MultipartFile[] photoFiles,
                           @RequestParam(value = "agreedToTerms", required = false) String agreedToTerms,
                           HttpSession session, Model model, HttpServletRequest req) {

        model.addAttribute("hasSubmitted", true);

     // 處理圖片
        if (photoFiles.length > 0 && !photoFiles[0].isEmpty()) {
            try {
                store.setStoreToPhoto(storeService.mergePhotos(store, photoFiles));
                storeService.generatePhotoPreview(store, session, model);
            } catch (IOException e) {
                model.addAttribute("photoError", "圖片處理失敗：" + e.getMessage());
                return "registerAndLogin/storeRegister";
            }
        } else {
            storeService.generatePhotoPreview(store, session, model);
        }
        // 檢查
        if (result.hasErrors() || !"true".equals(agreedToTerms) || store.getStoreToPhoto() == null || store.getStoreToPhoto().size() < 3) {
            if (store.getStoreToPhoto() == null || store.getStoreToPhoto().size() < 3) {
                model.addAttribute("photoError", "請上傳三張照片");
            }
            if (!"true".equals(agreedToTerms)) {
                model.addAttribute("errorMessage", "請詳閱並同意使用須知");
            }
            return "registerAndLogin/storeRegister";
        }


        try {
            StoreVO preparedStore = storeService.prepareStoreForSession(store, photoFiles, agreedToTerms);
            model.addAttribute("store", preparedStore);
            storeService.generatePhotoPreview(preparedStore, session, model);
            return "registerAndLogin/storeRegisterConF";
        } catch (IllegalArgumentException | IOException e) {
        	model.addAttribute("photoError", e.getMessage());
            result.reject("error.general", e.getMessage());
            return "registerAndLogin/storeRegister";
        }
    }

    //確認
    @GetMapping("/confirm")
    public String confirmPage(@ModelAttribute("store") StoreVO store, HttpSession session, Model model) {
        if (store == null) {
            return "redirect:/registerAndLogin/register/store/";
        }
        storeService.generatePhotoPreview(store, session, model); 
        return "registerAndLogin/storeRegisterConF";
    }

    //送出
    @PostMapping("/submit")
    public String submitFinalRegister(SessionStatus status,
                                      @ModelAttribute("store") StoreVO store,
                                      RedirectAttributes redirectAttributes) {
        if (store == null || store.getStoreToPhoto().isEmpty()) {
            return "redirect:/registerAndLogin/register/store/";
        }

        try {
            storeService.finalizeRegistration(store);
            status.setComplete(); // 清除 SessionAttributes
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/map";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registerAndLogin/register/store/confirm";
        }
    }



    @GetMapping("/activate")
    public String activateAccount(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        boolean activated = storeService.activateAccountByToken(token);
        if (activated) {
            redirectAttributes.addFlashAttribute("success", "帳號啟用成功，請登入設定營業資訊！");
        } else {
            redirectAttributes.addFlashAttribute("error", "啟用連結無效或已過期！");
        }
        return "redirect:/registerAndLogin/login";
    }

    //審核驗證
    @PostMapping("/admin/review")
    public String reviewStore(@RequestParam("storeId") Integer storeId,
                              @RequestParam("approved") boolean approved,
                              RedirectAttributes redirectAttributes) {
        if (approved) {
            storeService.approveStoreAndSendEmail(storeId);
            redirectAttributes.addFlashAttribute("success", "已審核通過並寄送啟用信");
        } else {
            StoreVO store = storeService.getOneStore(storeId);
            if (store != null) {
                store.setReviewed(2); // 審核未通過
                storeService.finalizeRegistration(store);
            }
            redirectAttributes.addFlashAttribute("error", "審核未通過");
        }
        return "redirect:/admin/review";
    }
}
