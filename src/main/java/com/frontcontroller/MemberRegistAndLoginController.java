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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.OrganizationRepository;
import com.entity.MemberVO;
import com.entity.OrganizationVO;
import com.frontservice.MemberRegistAndLoginService;

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
        model.addAttribute("organizations", organizationRepository.findAll());
        return "registerAndLogin/memberRegister";
    }


    @PostMapping({"", "/"})
    public String register(@Valid @ModelAttribute("member") MemberVO member,
                           BindingResult result,
                           @RequestParam("kycFile") MultipartFile kycFile,
                           @RequestParam(value = "agreedToTerms", required = false) String agreedToTerms,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("organizations", organizationRepository.findAll());
            return "registerAndLogin/memberRegister";
        }

        try {
            memberService.prepareMemberForSession(member, kycFile, agreedToTerms);
            return "redirect:/registerAndLogin/register/member/confirm";
        } catch (IllegalArgumentException | IOException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("organizations", organizationRepository.findAll());
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
            status.setComplete(); // ✅ 清除 session
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/map";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registerAndLogin/register/member/confirm";
        }
    }
}