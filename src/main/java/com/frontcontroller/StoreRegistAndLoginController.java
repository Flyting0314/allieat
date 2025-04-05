package com.frontcontroller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.entity.StoreVO;
import com.frontservice.StoreRegistAndLoginService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/registerAndLogin/register/store")
public class StoreRegistAndLoginController {

    @Autowired
    private StoreRegistAndLoginService storeService;

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        StoreVO store = (StoreVO) session.getAttribute("storeForm");

        if (store == null) {
            store = new StoreVO();
        }
        model.addAttribute("store", store);

        // ✅ 確保表單驗證的錯誤訊息載入
        BindingResult result = (BindingResult) session.getAttribute("formErrors");
        if (result != null) {
            model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "store", result);
            session.removeAttribute("formErrors"); // 清除 session 以避免錯誤殘留
        }

        // ✅ 確保所有錯誤訊息載入
        model.addAttribute("photoError", session.getAttribute("photoError"));
        model.addAttribute("agreeWarning", session.getAttribute("agreeWarning"));
        model.addAttribute("error", session.getAttribute("error"));
        session.removeAttribute("agreeWarning"); // ✅ 避免錯誤殘留
        session.removeAttribute("photoError"); 
        return "registerAndLogin/storeRegister";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("store") StoreVO store,
                           BindingResult result,
                           @RequestParam("photoFiles") MultipartFile[] photoFiles,
                           @RequestParam(value = "agreedToTerms", required = false) String agreedToTerms,
                           Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            session.setAttribute("formErrors", result); // ✅ 保留錯誤訊息
            session.setAttribute("storeForm", store);   // ✅ 保留填寫的資料
            return "redirect:/registerAndLogin/register/store/register";
        }

        long validPhotoCount = Arrays.stream(photoFiles)
                                     .filter(f -> f != null && !f.isEmpty())
                                     .count();

        if (!"true".equals(agreedToTerms)) {
            session.setAttribute("agreeWarning", "請勾選同意使用須知"); // ✅ 存入 session
            session.setAttribute("storeForm", store);  // ✅ 保留填寫的資料
            return "redirect:/registerAndLogin/register/store/register";
        }

        

        if (validPhotoCount < 3) {
            session.setAttribute("photoError", "請上傳至少三張照片"); // ✅ 存入 session
            session.setAttribute("storeForm", store);  // ✅ 保留填寫的資料
            return "redirect:/registerAndLogin/register/store/register";
        }

        try {
            StoreVO preparedStore = storeService.prepareStoreForSession(store, photoFiles, agreedToTerms);
            session.setAttribute("storeForm", preparedStore);
            return "redirect:/registerAndLogin/register/store/register/confirm";
        } catch (IllegalArgumentException | IOException e) {
            session.setAttribute("storeForm", store);  // ✅ 保留填寫的資料
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registerAndLogin/register/store/register";
        }
    }
    @PostMapping("/register/submit")
    public String submitFinalRegister(HttpSession session, RedirectAttributes redirectAttributes) {
        StoreVO store = (StoreVO) session.getAttribute("storeForm");
        if (store == null || store.getStoreToPhoto().isEmpty()) {
            return "redirect:/registerAndLogin/register/store/register";
        }

        try {
            storeService.finalizeRegistration(store);
            session.removeAttribute("storeForm");

            // ✅ 傳遞 FlashAttribute 給 /register/confirm
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/registerAndLogin/register/store/register/confirm";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registerAndLogin/register/store/register/confirm";
        }
    }
    
    @GetMapping("/register/confirm")
    public String confirmPage(HttpSession session, Model model) {
        StoreVO store = (StoreVO) session.getAttribute("storeForm");
        if (store == null) {
            return "redirect:/registerAndLogin/register/store/register";
        }

        // 改成 List<Map<String, String>> 結合 base64 + photoType
        List<Map<String, String>> base64Photos = store.getStoreToPhoto().stream().map(photo -> {
            Map<String, String> map = new HashMap<>();
            byte[] bytes = photo.getPhotoSrc();
            String base64 = (bytes != null && bytes.length > 0)
                    ? "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes)
                    : "";
            map.put("photoType", photo.getPhotoType()); // COVER / KITCHEN / STORE
            map.put("src", base64);
            return map;
        }).toList();

        model.addAttribute("store", store);
        model.addAttribute("photoList", base64Photos);

        // ✅ 彈窗提示用
        if (model.containsAttribute("showModal")) {
            model.addAttribute("showModal", true);
        }

        return "registerAndLogin/storeRegisterConF";
    }


//    @GetMapping("/register/confirm")
//    public String confirmPage(HttpSession session, Model model) {
//        StoreVO store = (StoreVO) session.getAttribute("storeForm");
//        if (store == null) {
//            return "redirect:/store/register";
//        }
//
//        List<String> base64List = store.getStoreToPhoto().stream()
//            .map(photo -> {
//                byte[] bytes = photo.getPhotoSrc();
//                return (bytes != null && bytes.length > 0) ?
//                        "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes) : "";
//            })
//            .toList();
//
//        model.addAttribute("store", store);
//        model.addAttribute("photoBase64List", base64List);
//
//        // ✅ 從 FlashAttribute 讀取 showModal
//        if (model.containsAttribute("showModal")) {
//            model.addAttribute("showModal", true);
//        }

       
//        return "store/storeRegisterConF";
//    }
}
