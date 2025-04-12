package com.frontcontroller;
import java.io.IOException;
import java.util.List;

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

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.OrganizationRepository;
import com.entity.MemberVO;
import com.entity.OrganizationVO;
import com.frontservice.MemberRegistAndLoginService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/registerAndLogin/register/member")
@SessionAttributes("member") 
public class MemberRegistAndLoginController {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberRegistAndLoginService memberService;
    @Autowired
    private OrganizationRepository organizationRepository;

    @ModelAttribute("member")
    public MemberVO memberForm() {
        MemberVO member = new MemberVO();
        member.setOrganization(new OrganizationVO());
        return member;
    }


    @GetMapping({"", "/"})
    public String registerPage(@ModelAttribute("member") MemberVO member, Model model) {
        List<OrganizationVO> orgList = organizationRepository.findByStatus(1); // 只撈啟用的單位

        OrganizationVO selectedOrg = member.getOrganization();

        if (selectedOrg != null && selectedOrg.getStatus() != null) {
            if (selectedOrg.getStatus() == 2) {
                // 使用者之前選的是「其他」，所以保留選項為 -1
                selectedOrg.setOrganizationId(-1);
            }
        }

        model.addAttribute("organizations", orgList);
        memberService.generateKycPreview(member, model);
        return "registerAndLogin/memberRegister";
    }

  //註冊
    @PostMapping({"", "/"})
    public String register(@Valid @ModelAttribute("member") MemberVO member,
                           BindingResult result,
                           @RequestParam(value = "kycFile", required = false) MultipartFile kycFile, // ✅ 改為非必填
                           @RequestParam(value = "agreedToTerms", required = false) String agreedToTerms,
                           HttpSession session, Model model) {

        model.addAttribute("hasSubmitted", true);

        if (!"true".equals(agreedToTerms)) {
            model.addAttribute("errorMessage", "請詳閱並同意使用須知");
        }

        
        MultipartFile sessionKycFile = (MultipartFile) session.getAttribute("kycFile");
        if ((kycFile == null || kycFile.isEmpty()) && sessionKycFile == null) {
            model.addAttribute("error", "請上傳身分驗證檔案");
            model.addAttribute("organizations", organizationRepository.findByStatus(1));
            return "registerAndLogin/memberRegister";
        }

        try {
            if (kycFile != null && !kycFile.isEmpty()) {
                session.setAttribute("kycFile", kycFile); 
            }

            memberService.prepareMemberForSession(member, kycFile != null ? kycFile : sessionKycFile, agreedToTerms);
            return "redirect:/registerAndLogin/register/member/confirm";
        } catch (IllegalArgumentException | IOException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("organizations", organizationRepository.findByStatus(1));
            return "registerAndLogin/memberRegister";
        }
    }
    @GetMapping("/confirm")
    public String confirmPage(@ModelAttribute("member") MemberVO member, Model model) {
        model.addAttribute("member", member);
        return "registerAndLogin/memberRegisterConF";
    }

    @PostMapping("/submit")
    public String submitFinalRegister(@ModelAttribute("member") MemberVO member,
                                      SessionStatus status,
                                      RedirectAttributes redirectAttributes) {
        if (member == null || member.getKycImage() == null) {
            return "redirect:/registerAndLogin/register/member/";
        }

        try {
            memberService.finalizeRegistration(member);
            status.setComplete(); 
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/map";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registerAndLogin/register/member/confirm";
        }
    }
}