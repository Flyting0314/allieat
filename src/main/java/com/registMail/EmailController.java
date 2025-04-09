package com.registMail;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.MemberVO;
import com.entity.StoreVO;

@Controller
@RequestMapping("/registerAndLogin")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MemberRepository memberRepository;

    // ✅ 通用驗證連結（判斷是店家或會員）
    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        boolean storeActivated = emailService.activateStoreByToken(token);
        boolean memberActivated = emailService.activateMemberByToken(token);

        if (storeActivated) {
            redirectAttributes.addFlashAttribute("success", "店家帳號啟用成功，請登入設定營業資訊！");
        } else if (memberActivated) {
            redirectAttributes.addFlashAttribute("success", "會員帳號啟用成功，請登入使用服務！");
        } else {
            redirectAttributes.addFlashAttribute("error", "啟用連結無效或已過期！");
        }

        return "redirect:/registerAndLogin/login";
    }
 // ✅ 重新寄送驗證信（合併會員與店家）
    @PostMapping("/resend")
    public String resendVerification(@RequestParam("email") String email,
                                     RedirectAttributes redirectAttributes) {
        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "請輸入 Email");
            return "redirect:/registerAndLogin/login";
        }

        boolean found = false;

        Optional<StoreVO> storeOpt = storeRepository.findByEmail(email);
        if (storeOpt.isPresent()) {
            found = true;
            StoreVO store = storeOpt.get();
            if (store.getReviewed() == 3) {
                redirectAttributes.addFlashAttribute("error", "帳號審核中，通過後會核發信件");
            } else if (store.getReviewed() == 2) {
            	emailService.sendRejectionEmail(store);
                redirectAttributes.addFlashAttribute("error", "帳號未通過審核");
            } else if (store.getReviewed() == 0) {
                emailService.sendCorrectionEmail(store);
                redirectAttributes.addFlashAttribute("error", "店家資料補件通知已寄送，請依指示補齊相關資料");
            } else if (store.getReviewed() == 1 && store.getAccStat() == 1) {
                redirectAttributes.addFlashAttribute("success", "帳號已啟用，請直接登入！");
            } else if (store.getReviewed() == 1 && store.getAccStat() == 0) {
                emailService.sendVerificationEmail(store);
                redirectAttributes.addFlashAttribute("success", "驗證信已重新寄送，請至信箱點擊啟用！");
            }
        }

        Optional<MemberVO> memberOpt = memberRepository.findByEmail(email);
        if (memberOpt.isPresent()) {
            found = true;
            MemberVO member = memberOpt.get();
            if (member.getReviewed() == 3) {
                redirectAttributes.addFlashAttribute("error", "帳號審核中，通過後會核發信件");
            } else if (member.getReviewed() == 2) {
            	emailService.sendRejectionEmail(member);
                redirectAttributes.addFlashAttribute("error", "帳號未通過審核");
            } else if (member.getReviewed() == 0) {
                emailService.sendCorrectionEmail(member);
                redirectAttributes.addFlashAttribute("error", "會員補件通知已寄送，請依信件補齊文件後再等候審核");   
            } else if (member.getReviewed() == 1 && member.getAccStat() == 1) {
                redirectAttributes.addFlashAttribute("success", "帳號已啟用，請直接登入！");
            } else if (member.getReviewed() == 1 && member.getAccStat() == 0) {
                emailService.sendMemberVerificationEmail(member);
                redirectAttributes.addFlashAttribute("success", "驗證信已重新寄送，請至信箱點擊啟用！");
            }
        }
        
        if (!found) {
            redirectAttributes.addFlashAttribute("error", "查無此 Email，請確認輸入正確");
        }

        return "redirect:/registerAndLogin/login";
    }
}

//    // ✅ 重新寄送驗證信（店家）
//    @PostMapping("/resend")
//    public String resendStoreVerification(@RequestParam("email") String email,
//                                     RedirectAttributes redirectAttributes) {
//        Optional<StoreVO> storeOpt = storeRepository.findByEmail(email);
//
//        if (storeOpt.isPresent()) {
//            StoreVO store = storeOpt.get();
//
//            if (store.getAccStat() != null && store.getAccStat() == 1) {
//                redirectAttributes.addFlashAttribute("success", "帳號已啟用，請直接登入！");
//            } else {
//                emailService.sendVerificationEmail(store);
//                redirectAttributes.addFlashAttribute("success", "驗證信已重新寄送，請至信箱點擊啟用！");
//            }
//
//        } else {
//            redirectAttributes.addFlashAttribute("error", "查無此店家信箱，請確認輸入正確");
//        }
//
//        return "redirect:/registerAndLogin/login";
//    }
//
//    // ✅ 重新寄送驗證信（會員）
//    @PostMapping("/resendMember")
//    public String resendMemberVerification(@RequestParam("account") String account,
//                                           RedirectAttributes redirectAttributes) {
//        Optional<MemberVO> memberOpt = memberRepository.findByAccount(account);
//
//        if (memberOpt.isPresent()) {
//            MemberVO member = memberOpt.get();
//
//            if (member.getAccStat() != null && member.getAccStat() == 1) {
//                redirectAttributes.addFlashAttribute("success", "帳號已啟用，請直接登入！");
//            } else {
//                emailService.sendMemberVerificationEmail(member);
//                redirectAttributes.addFlashAttribute("success", "驗證信已重新寄送，請至信箱點擊啟用！");
//            }
//
//        } else {
//            redirectAttributes.addFlashAttribute("error", "查無此會員帳號，請確認輸入正確");
//        }
//
//        return "redirect:/registerAndLogin/login";
//    }
//}
