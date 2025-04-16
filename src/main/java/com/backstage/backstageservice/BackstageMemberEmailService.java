package com.backstage.backstageservice;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.MemberRepository;
import com.entity.MemberVO;
 
@Service
public class BackstageMemberEmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MemberRepository memberRepository;
    
    // 受助者通過=1
    public void sendMemberVerificationEmail(MemberVO member) {
        String token = UUID.randomUUID().toString();
        member.setVerificationMail(token);
        member.setAccStat(0); // 預設為未啟用
        memberRepository.save(member);

        // 注意這裡的URL路徑需要與您的controller endpoint一致
        String verifyUrl = "http://localhost:8080/memberManagement/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(member.getEmail());
        message.setSubject("【攏呷霸 ALLiEAT】會員帳號驗證信");
        message.setText("親愛的 " + member.getName() + " 您好：\n\n"
                + "請點擊以下連結完成帳號啟用：\n"
                + verifyUrl + "\n\n-- 攏呷霸 ALLiEAT 團隊");

        mailSender.send(message);
    }
    
    // 受助者未通過=2
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
    
    // 受助者補件=0 (雖然現在審核已合併，但保留此方法以備後續需要)
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
            member.setAccStat(1); // 設置為啟用
            member.setVerificationMail(null); // 清除驗證token
            memberRepository.save(member);
            System.out.println("[DEBUG] 收到驗證 token: " + token);
            System.out.println("[DEBUG] 找到會員帳號: " + member.getAccount());
            System.out.println("[DEBUG] accStat 設為: " + member.getAccStat());
            return true;
        }
        return false;
    }
}