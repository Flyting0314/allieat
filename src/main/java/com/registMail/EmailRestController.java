package com.registMail;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.MemberVO;
import com.entity.StoreVO;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;

@RestController
@RequestMapping("/registerAndLogin")
public class EmailRestController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 通用驗證連結
//    @GetMapping("/verify")
//    public Map<String, String> verify(@RequestParam("token") String token) {
//        Map<String, String> response = new HashMap<>();
//
//        boolean storeActivated = emailService.activateStoreByToken(token);
//        boolean memberActivated = emailService.activateMemberByToken(token);
//
//        if (storeActivated) {
//            response.put("message", "店家帳號啟用成功，請登入設定營業資訊！");
//        } else if (memberActivated) {
//            response.put("message", "會員帳號啟用成功，請登入使用服務！");
//        } else {
//            response.put("error", "啟用連結無效或已過期！");
//        }
//
//        return response;
//    }

    // 重新寄送驗證信（店家 / 會員合併）
//    @PostMapping("/resend")
//    public Map<String, String> resendVerification(@RequestParam("email") String email) {
//        Map<String, String> response = new HashMap<>();
//
//        if (email == null || email.trim().isEmpty()) {
//            response.put("error", "請輸入 Email");
//            return response;
//        }
//
//        boolean found = false;
//
//        Optional<StoreVO> storeOpt = storeRepository.findByEmail(email);
//        if (storeOpt.isPresent()) {
//            found = true;
//            StoreVO store = storeOpt.get();
//            switch (store.getReviewed()) {
//                case 3 -> response.put("error", "帳號審核中，通過後會核發信件");
//                case 2 -> {
//                    emailService.sendRejectionEmail(store);
//                    response.put("error", "帳號未通過審核");
//                }
//                case 0 -> {
//                    emailService.sendCorrectionEmail(store);
//                    response.put("error", "補件通知已寄送，請補齊資料");
//                }
//                case 1 -> {
//                    if (store.getAccStat() == 1) {
//                        response.put("message", "帳號已啟用，請直接登入！");
//                    } else {
//                        emailService.sendVerificationEmail(store);
//                        response.put("message", "驗證信已重新寄送，請至信箱點擊啟用！");
//                    }
//                }
//            }
//        }
//
//        Optional<MemberVO> memberOpt = memberRepository.findByEmail(email);
//        if (memberOpt.isPresent()) {
//            found = true;
//            MemberVO member = memberOpt.get();
//            switch (member.getReviewed()) {
//                case 3 -> response.put("error", "帳號審核中，通過後會核發信件");
//                case 2 -> {
//                    emailService.sendRejectionEmail(member);
//                    response.put("error", "帳號未通過審核");
//                }
//                case 0 -> {
//                    emailService.sendCorrectionEmail(member);
//                    response.put("error", "補件通知已寄送，請補齊文件");
//                }
//                case 1 -> {
//                    if (member.getAccStat() == 1) {
//                        response.put("message", "帳號已啟用，請直接登入！");
//                    } else {
//                        emailService.sendMemberVerificationEmail(member);
//                        response.put("message", "驗證信已重新寄送，請至信箱點擊啟用！");
//                    }
//                }
//            }
//        }
//
//        if (!found) {
//            response.put("error", "查無此 Email，請確認輸入正確");
//        }
//
//        return response;
//    }
//}
    
    
    
    
    //=============新verify======================

 // 通用驗證連結
    @GetMapping("/verify")
    public Object verify(@RequestParam("token") String token) {
        boolean storeActivated = emailService.activateStoreByToken(token);
        boolean memberActivated = emailService.activateMemberByToken(token);
        
        if (storeActivated) {
            // 店家啟用成功也返回HTML頁面
            return createHtmlResponse("店家帳號啟用成功，請登入設定營業資訊！", true, null);
        } else if (memberActivated) {
            // 會員啟用成功返回HTML頁面
            return createHtmlResponse("會員帳號啟用成功，請登入使用服務！", true, null);
        } else {
            // 連結無效或過期的情況返回HTML頁面
            return createHtmlResponse("啟用連結無效或已過期！", false, null);
        }
    }
    
    
    
    
  //==============新resend====================

    // 重新寄送驗證信（店家 / 會員合併）
    @PostMapping("/resend")
    public Object resendVerification(@RequestParam("email") String email) {
        Map<String, String> response = new HashMap<>();
        
        if (email == null || email.trim().isEmpty()) {
            response.put("error", "請輸入 Email");
            return response;
        }
        
        boolean found = false;
        boolean isVerificationSent = false; // 標記是否寄送了驗證信
        String resultMessage = ""; // 用於保存最終消息
        
        // 處理店家部分 - 現在也使用HTML響應
        Optional<StoreVO> storeOpt = storeRepository.findByEmail(email);
        if (storeOpt.isPresent()) {
            found = true;
            StoreVO store = storeOpt.get();
            switch (store.getReviewed()) {
                case 3 -> {
                    resultMessage = "帳號審核中，通過後會核發信件";
                    response.put("error", resultMessage);
                }
                case 2 -> {
                    emailService.sendRejectionEmail(store);
                    resultMessage = "帳號未通過審核";
                    response.put("error", resultMessage);
                }
                case 0 -> {
                    emailService.sendCorrectionEmail(store);
                    resultMessage = "補件通知已寄送，請補齊資料";
                    response.put("error", resultMessage);
                }
                case 1 -> {
                    if (store.getAccStat() == 1) {
                        // 店家帳號已啟用的情況也返回HTML
                        return createHtmlResponse("帳號已啟用，請直接登入！", true, null);
                    } else {
                        emailService.sendVerificationEmail(store);
                        isVerificationSent = true;
                        return createHtmlResponse("驗證信已重新寄送，請至信箱點擊啟用！", true, email);
                    }
                }
            }
        }
        
        // 處理會員部分 - 使用HTML響應
        Optional<MemberVO> memberOpt = memberRepository.findByEmail(email);
        if (memberOpt.isPresent()) {
            found = true;
            MemberVO member = memberOpt.get();
            switch (member.getReviewed()) {
                case 3 -> {
                    resultMessage = "帳號審核中，通過後會核發信件";
                    response.put("error", resultMessage);
                }
                case 2 -> {
                    emailService.sendRejectionEmail(member);
                    resultMessage = "帳號未通過審核";
                    response.put("error", resultMessage);
                }
                case 0 -> {
                    emailService.sendCorrectionEmail(member);
                    resultMessage = "補件通知已寄送，請補齊文件";
                    response.put("error", resultMessage);
                }
                case 1 -> {
                    if (member.getAccStat() == 1) {
                        // 會員帳號已啟用的情況也返回HTML
                        return createHtmlResponse("帳號已啟用，請直接登入！", true, null);
                    } else {
                        emailService.sendMemberVerificationEmail(member);
                        isVerificationSent = true;
                        return createHtmlResponse("驗證信已重新寄送，請至信箱點擊啟用！", true, email);
                    }
                }
            }
        }
        
        if (!found) {
            resultMessage = "查無此 Email，請確認輸入正確";
            response.put("error", resultMessage);
        }
        
        // 其他錯誤情況返回原有的JSON
        return response;
    }

    // HTML回應生成方法
    private ResponseEntity<?> createHtmlResponse(String message, boolean isSuccess, String email) {
        String cssStyle = "body { font-family: Arial, sans-serif; text-align: center; padding-top: 50px; background-color: #f7f7f7; }"
                        + "div.container { max-width: 500px; margin: 0 auto; padding: 20px; background-color: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }"
                        + "h2 { color: #333; }"
                        + "p { color: #666; margin: 20px 0; }"
                        + "p.success { color: #4CAF50; font-weight: bold; }"
                        + "p.error { color: #F44336; font-weight: bold; }"
                        + "p.email { font-weight: bold; color: #333; }"
                        + "a.button { display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; margin-top: 15px; }"
                        + "p.note { font-size: 0.9em; color: #999; margin-top: 20px; }";
        
        String title;
        String messageClass;
        
        if (isSuccess) {
            if (message.contains("驗證信已重新寄送")) {
                title = "驗證信已寄送";
            } else if (message.contains("帳號已啟用")) {
                title = "帳號已啟用";
            } else if (message.contains("店家帳號啟用成功")) {
                title = "店家帳號啟用成功";
            } else {
                title = "帳號啟用成功";
            }
            messageClass = "success";
        } else {
            title = "連結已失效";
            messageClass = "error";
        }
        
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>")
                  .append("<html>")
                  .append("<head>")
                  .append("<meta charset='UTF-8'>")
                  .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                  .append("<title>").append(title).append("</title>")
                  .append("<style>").append(cssStyle).append("</style>")
                  .append("</head>")
                  .append("<body>")
                  .append("<div class='container'>")
                  .append("<h2>").append(title).append("</h2>")
                  .append("<p class='").append(messageClass).append("'>").append(message).append("</p>");
        
        // 如果是重新發送驗證信的情況，顯示電子郵件地址
        if (email != null && !email.isEmpty()) {
            htmlBuilder.append("<p>驗證信已發送至：</p>")
                      .append("<p class='email'>").append(email).append("</p>")
                      .append("<p class='note'>如果您未收到信件，請檢查垃圾郵件夾或等待幾分鐘後再試。</p>");
        }
        
        htmlBuilder.append("<a href='/registerAndLogin/login' class='button'>前往登入頁面</a>")
                  .append("</div>")
                  .append("</body>")
                  .append("</html>");

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlBuilder.toString());
    }
}
