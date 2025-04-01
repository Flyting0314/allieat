package com.frontcontroller;

import java.io.IOException;

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

import com.backstage.backstagrepository.StoreRepository;
import com.entity.StoreVO;
import com.frontservice.StoreRegistAndLoginService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/store")
public class StoreRegistAndLoginController {

    @Autowired
    private StoreRegistAndLoginService storeService;

    @GetMapping("/register")
    public String showRegisterPage(Model model, HttpSession session) {
        StoreVO store = (StoreVO) session.getAttribute("storeForm");
        if (store == null) {
            store = new StoreVO();
        }
        model.addAttribute("store", store);
        return "store/storeRegister";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("store") StoreVO store,
                                 BindingResult result,
                                 @RequestParam("photoFiles") MultipartFile[] photoFiles,
                                 @RequestParam(value = "agreedToTerms", required = false) String agreedToTerms,
                                 Model model,
                                 HttpSession session) {
        if (result.hasErrors()) {
            return "store/storeRegister";
        }

        try {
            StoreVO preparedStore = storeService.prepareStoreForSession(store, photoFiles, agreedToTerms);
            session.setAttribute("storeForm", preparedStore);
            return "redirect:/store/register/confirm";
        } catch (IllegalArgumentException | IOException e) {
            model.addAttribute("error", e.getMessage());
            return "store/storeRegister";
        }
    }

    @GetMapping("/register/confirm")
    public String showConfirmPage(HttpSession session, Model model) {
        StoreVO store = (StoreVO) session.getAttribute("storeForm");
        if (store == null) {
            return "redirect:/store/register";
        }
        model.addAttribute("store", store);
        return "store/storeRegisterConF";
    }

    @PostMapping("/register/submit")
    public String finalizeRegistration(HttpSession session, RedirectAttributes redirectAttributes) {
        StoreVO store = (StoreVO) session.getAttribute("storeForm");
        if (store == null) {
            return "redirect:/store/register";
        }

        storeService.finalizeRegistration(store);
        session.removeAttribute("storeForm");
        redirectAttributes.addFlashAttribute("success", "註冊成功，請等待審核");
        return "redirect:/store/registerAndLogin";
    }

    @GetMapping("/registerAndLogin")
    public String registerAndLoginPage() {
        return "store/registerAndLogin";
    }
}

