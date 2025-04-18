package com.frontcontroller;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.registMail.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ResetPasswordController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 第一步：顯示輸入 email 頁面
    @GetMapping("/registerAndLogin/sendCode")
    public String showResetPage() {
        return "registerAndLogin/resetPassword";
    }

    // 第二步：寄送驗證碼
    @PostMapping("/registerAndLogin/sendCode")
    public String sendVerificationCode(@RequestParam("email") String email,
                                       Model model,
                                       HttpSession session) {
        if (email == null || email.isBlank()) {
            model.addAttribute("error", "請輸入 Email");
            return "registerAndLogin/resetPassword";
        }

        boolean exists = storeRepository.findByEmail(email).isPresent()
                || memberRepository.findByEmail(email).isPresent();

        if (!exists) {
            model.addAttribute("error", "查無此 Email，請確認是否正確");
            return "registerAndLogin/resetPassword";
        }

        String code = String.format("%06d", new SecureRandom().nextInt(999999));
        String subject = "【攏呷霸 ALLiEAT】密碼重設驗證碼";
        String body = "您的驗證碼為：" + code + "\n\n請於10分鐘內輸入驗證碼完成驗證。";

        emailService.sendPlainTextEmail(email, subject, body);

        session.setAttribute("resetEmail", email);
        session.setAttribute("resetCode", code);
        session.setAttribute("codeVerified", false);

        return "registerAndLogin/resetPasswordConF";
    }

    // 第三步：驗證碼驗證
    @PostMapping("/registerAndLogin/verifyCode")
    public String verifyCode(@RequestParam("inputCode") String inputCode,
                             HttpSession session,
                             Model model) {
        String sessionCode = (String) session.getAttribute("resetCode");
        String email = (String) session.getAttribute("resetEmail");

        if (sessionCode == null || email == null) {
            model.addAttribute("error", "驗證流程已失效，請重新發送驗證碼。");
            return "registerAndLogin/resetPassword";
        }

        if (sessionCode.equals(inputCode)) {
            session.setAttribute("codeVerified", true);
            return "redirect:/registerAndLogin/newPassword";
        } else {
            model.addAttribute("error", "驗證碼錯誤，請重新輸入。");
            return "registerAndLogin/resetPasswordConF";
        }
    }

    // 第四步：顯示新密碼輸入頁面
    @GetMapping("/registerAndLogin/newPassword")
    public String showNewPasswordPage(HttpSession session) {
        Boolean verified = (Boolean) session.getAttribute("codeVerified");
        
        return (verified != null && verified) ? "registerAndLogin/resetNewPassword" : "redirect:/registerAndLogin/resetPassword";

    }

    // 第五步：儲存新密碼
    @PostMapping("/registerAndLogin/savePassword")
    public String saveNewPassword(@RequestParam("password") String password,
                                  HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");

        if (email == null) {
            return "redirect:/registerAndLogin/resetPassword";
        }

        storeRepository.findByEmail(email).ifPresent(store -> {
            store.setPassword(password);
            storeRepository.save(store);
        });

        memberRepository.findByEmail(email).ifPresent(member -> {
            member.setPassword(password);
            memberRepository.save(member);
        });

        session.invalidate();
        model.addAttribute("success", "密碼重設成功，請重新登入");
        return "registerAndLogin/login";
    }
}
