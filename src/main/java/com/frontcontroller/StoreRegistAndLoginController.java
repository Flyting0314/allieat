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
    // **新增這個方法來初始化 photoList**
    @ModelAttribute("photoList")
    public List<Map<String, String>> photoList() {
        return new ArrayList<>();
    }
    @GetMapping("")
    public String registerPage(@ModelAttribute("store") StoreVO store, HttpSession session, Model model) {
        storeService.generatePhotoPreview(store, session, model); // ⬅ 使用新方法
        return "registerAndLogin/storeRegister";
    }
    @PostMapping("")
    public String register(@Valid @ModelAttribute("store") StoreVO store,
                           BindingResult result,
                           @RequestParam("photoFiles") MultipartFile[] photoFiles,
                           @RequestParam(value = "agreedToTerms", required = false) String agreedToTerms,
                           HttpSession session, Model model) {

        model.addAttribute("hasSubmitted", true);

        try {
            store.setStoreToPhoto(storeService.mergePhotos(store, photoFiles));
            storeService.generatePhotoPreview(store, session, model);
        } catch (IOException e) {
        	
            model.addAttribute("photoError", "圖片處理失敗：" + e.getMessage());
        }

        if (result.hasErrors()) {
            model.addAttribute("formErrors", result);
            return "registerAndLogin/storeRegister"; // **不使用 redirect**
        }

        try {
            StoreVO preparedStore = storeService.prepareStoreForSession(store, photoFiles, agreedToTerms);
            model.addAttribute("store", preparedStore);
            storeService.generatePhotoPreview(preparedStore, session, model);
            return "registerAndLogin/storeRegisterConF"; // **直接跳轉，而不影響 session**
        } catch (IllegalArgumentException | IOException e) {
            model.addAttribute("error", e.getMessage());
            return "registerAndLogin/storeRegister"; // **仍然停留在註冊頁**
        }
    }


    @GetMapping("/confirm")
    public String confirmPage(@ModelAttribute("store") StoreVO store, HttpSession session, Model model) {
        if (store == null) {
            return "redirect:/registerAndLogin/register/store/";
        }


        storeService.generatePhotoPreview(store, session, model); 
        return "registerAndLogin/storeRegisterConF";
    }

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
