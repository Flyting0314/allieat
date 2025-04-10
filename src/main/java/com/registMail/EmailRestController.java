package com.registMail;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.MemberVO;
import com.entity.StoreVO;

@RestController
@RequestMapping("/registerAndLogin")
public class EmailRestController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MemberRepository memberRepository;

    // ✅ 通用驗證連結
    @GetMapping("/verify")
    public Map<String, String> verify(@RequestParam("token") String token) {
        Map<String, String> response = new HashMap<>();

        boolean storeActivated = emailService.activateStoreByToken(token);
        boolean memberActivated = emailService.activateMemberByToken(token);

        if (storeActivated) {
            response.put("message", "店家帳號啟用成功，請登入設定營業資訊！");
        } else if (memberActivated) {
            response.put("message", "會員帳號啟用成功，請登入使用服務！");
        } else {
            response.put("error", "啟用連結無效或已過期！");
        }

        return response;
    }

    // ✅ 重新寄送驗證信（店家 / 會員合併）
    @PostMapping("/resend")
    public Map<String, String> resendVerification(@RequestParam("email") String email) {
        Map<String, String> response = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            response.put("error", "請輸入 Email");
            return response;
        }

        boolean found = false;

        Optional<StoreVO> storeOpt = storeRepository.findByEmail(email);
        if (storeOpt.isPresent()) {
            found = true;
            StoreVO store = storeOpt.get();
            switch (store.getReviewed()) {
                case 3 -> response.put("error", "帳號審核中，通過後會核發信件");
                case 2 -> {
                    emailService.sendRejectionEmail(store);
                    response.put("error", "帳號未通過審核");
                }
                case 0 -> {
                    emailService.sendCorrectionEmail(store);
                    response.put("error", "補件通知已寄送，請補齊資料");
                }
                case 1 -> {
                    if (store.getAccStat() == 1) {
                        response.put("message", "帳號已啟用，請直接登入！");
                    } else {
                        emailService.sendVerificationEmail(store);
                        response.put("message", "驗證信已重新寄送，請至信箱點擊啟用！");
                    }
                }
            }
        }

        Optional<MemberVO> memberOpt = memberRepository.findByEmail(email);
        if (memberOpt.isPresent()) {
            found = true;
            MemberVO member = memberOpt.get();
            switch (member.getReviewed()) {
                case 3 -> response.put("error", "帳號審核中，通過後會核發信件");
                case 2 -> {
                    emailService.sendRejectionEmail(member);
                    response.put("error", "帳號未通過審核");
                }
                case 0 -> {
                    emailService.sendCorrectionEmail(member);
                    response.put("error", "補件通知已寄送，請補齊文件");
                }
                case 1 -> {
                    if (member.getAccStat() == 1) {
                        response.put("message", "帳號已啟用，請直接登入！");
                    } else {
                        emailService.sendMemberVerificationEmail(member);
                        response.put("message", "驗證信已重新寄送，請至信箱點擊啟用！");
                    }
                }
            }
        }

        if (!found) {
            response.put("error", "查無此 Email，請確認輸入正確");
        }

        return response;
    }
}
