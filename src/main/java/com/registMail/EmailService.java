package com.registMail;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.MemberVO;
import com.entity.StoreVO;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StoreRepository storeRepository;
    @Autowired  
    private MemberRepository memberRepository;
    
    
//店家通過=1
    public void sendVerificationEmail(StoreVO store) {
        String token = UUID.randomUUID().toString();
        store.setVerificationMail(token);
        storeRepository.save(store);

        String verifyUrl = "http://localhost:8080/verify.html?token=" + token;


        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(store.getEmail());
        message.setSubject("【攏呷霸 ALLiEAT】店家帳號驗證信");
        message.setText("親愛的 " + store.getName() + " 店家您好：\n\n"
                + "請點擊以下連結完成帳號啟用：\n"
                + verifyUrl + "\n\n-- 攏呷霸 ALLiEAT 團隊");

        mailSender.send(message);
    }
    
  //店家未通過=2
    public void sendRejectionEmail(StoreVO store) {
        SimpleMailMessage message = new SimpleMailMessage();
        String verifyUrl = "http://localhost:8080/registerAndLogin/login";
        message.setTo(store.getEmail());
        message.setSubject("【攏呷霸 ALLiEAT】店家帳號審核未通過通知");
        message.setText("親愛的 " + store.getName() + " 店家您好：\n\n"
        		 + "很遺憾通知您，您的帳號審核未通過，若需補件請點擊以下連結，輸入註冊時的帳號及密碼進入重新提交頁面。\n\n"
                 + verifyUrl + "\n\n-- 攏呷霸 ALLiEAT 團隊");
        mailSender.send(message);
    }
    
  //店家補件=0
    public void sendCorrectionEmail(StoreVO store) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(store.getEmail());
        message.setSubject("【攏呷霸 ALLiEAT】店家補件通知");
        message.setText("親愛的 " + store.getName() + " 店家您好：\n\n"
                + "您的店家登記資料有缺漏（如：店面照片毀損或登記字號不符等），請補件後重新提交。\n"
                + "如有疑問請聯絡客服。\n\n-- 攏呷霸 ALLiEAT 團隊");
        mailSender.send(message);
    }
    public boolean activateStoreByToken(String token) {
        Optional<StoreVO> optionalStore = storeRepository.findByVerificationMail(token);
        if (optionalStore.isPresent()) {
            StoreVO store = optionalStore.get();
            System.out.println("驗證成功，storeId=" + store.getStoreId());
            store.setAccStat(1);
            store.setVerificationMail(null);
            storeRepository.save(store);
            return true;
        }
        System.out.println("驗證失敗，token 不存在：" + token);
        return false;
    }
 // 寄送會員驗證信
    
  //受助者通過=1
    public void sendMemberVerificationEmail(MemberVO member) {
        String token = UUID.randomUUID().toString();
        member.setVerificationMail(token); // 確保 MemberVO 也有這個欄位
        member.setAccStat(0); // 預設為未啟用
        memberRepository.save(member); // ⚠ 確保注入了 memberRepository

        String verifyUrl = "http://localhost:8080/registerAndLogin/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(member.getEmail());
        message.setSubject("【攏呷霸 ALLiEAT】會員帳號驗證信");
        message.setText("親愛的 " + member.getName() + " 您好：\n\n"
                + "請點擊以下連結完成帳號啟用：\n"
                + verifyUrl + "\n\n-- 攏呷霸 ALLiEAT 團隊");

        mailSender.send(message);
    }
    //受助者未通過=2
    public void sendRejectionEmail(MemberVO member) {
        SimpleMailMessage message = new SimpleMailMessage();
        String verifyUrl = "http://localhost:8080/registerAndLogin/login";
        message.setTo(member.getEmail());
        message.setSubject("【攏呷霸 ALLiEAT】會員帳號審核未通過通知");
        message.setText("親愛的 " + member.getName() + " 您好：\n\n"
                + "很遺憾通知您，您的帳號審核未通過，若需補件請點擊以下連結，輸入註冊時的帳號及密碼進入重新提交頁面。\n\n"
                + verifyUrl + "\n\n-- 攏呷霸 ALLiEAT 團隊");
        mailSender.send(message);
    }
    
    //受助者補件=0
    public void sendCorrectionEmail(MemberVO member) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(member.getEmail());
        message.setSubject("【攏呷霸 ALLiEAT】會員補件通知");
        message.setText("親愛的 " + member.getName() + "，您好：\n\n"
                + "您提交的證明文件可能因缺漏、模糊等問題無法審核，請補齊後重新上傳。\n"
                + "如有疑問請聯絡客服。\n\n-- 攏呷霸 ALLiEAT 團隊");
        mailSender.send(message);
    }
    // 驗證會員啟用
    public boolean activateMemberByToken(String token) {
        Optional<MemberVO> optionalMember = memberRepository.findByVerificationMail(token);
        if (optionalMember.isPresent()) {
            MemberVO member = optionalMember.get();
            member.setAccStat(1);
            member.setVerificationMail(null);
            memberRepository.save(member);
            System.out.println("[DEBUG] 收到驗證 token: " + token);
            System.out.println("[DEBUG] 找到會員帳號: " + member.getAccount());
            System.out.println("[DEBUG] accStat 設為: " + member.getAccStat());
            return true;
        }
        return false;
    }

    public void sendPlainTextEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }


}