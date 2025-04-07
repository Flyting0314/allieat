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
public class MemberRegistAndLoginController {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberRegistAndLoginService memberService;
    @Autowired
    private OrganizationRepository organizationRepository;

    

    @GetMapping("/")
    public String registerPage(Model model, HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("memberForm");
        if (member == null) {
            member = new MemberVO();
            member.setOrganization(new OrganizationVO());
        }
        List<OrganizationVO> organizations = organizationRepository.findAll();
        model.addAttribute("member", member);
        model.addAttribute("organizations", organizations);
        return "registerAndLogin/memberRegister";
    }

    @PostMapping("/")
    public String register(@Valid @ModelAttribute("member") MemberVO member,
                           BindingResult result,
                           @RequestParam("kycFile") MultipartFile kycFile,@RequestParam(value = "agreedToTerms", required = false) String agreedToTerms,
                           Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("organizations", organizationRepository.findAll());
            return "registerAndLogin/memberRegister";
        }

        try {
            // ✅ 整合帳號驗證、單位邏輯與檔案儲存
            MemberVO preparedMember = memberService.prepareMemberForSession(member, kycFile,agreedToTerms);
            session.setAttribute("memberForm", preparedMember);
            return "redirect:/registerAndLogin/register/member/confirm";

        } catch (IllegalArgumentException | IOException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("organizations", organizationRepository.findAll());
            return "registerAndLogin/memberRegister";
        }
    }
    @PostMapping("/submit")
    public String submitFinalRegister(HttpSession session, RedirectAttributes redirectAttributes) {
        MemberVO member = (MemberVO) session.getAttribute("memberForm");
        if (member == null || member.getKycImage() == null) {
            return "redirect:/registerAndLogin/register/member/";
        }

        try {
            memberService.finalizeRegistration(member);
            session.removeAttribute("memberForm");

            // ✅ 傳遞 FlashAttribute 給 /register/confirm
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/map";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registerAndLogin/register/member/confirm";
        }
    }

    @GetMapping("/confirm")
    public String confirmPage(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("memberForm");
        if (member == null) {
            return "redirect:/registerAndLogin/register/member/";
        }

        model.addAttribute("member", member);

        // ✅ 從 FlashAttribute 裡讀 showModal
        if (model.containsAttribute("showModal")) {
            model.addAttribute("showModal", true);
        }

        // ✅ 錯誤訊息也是用 FlashAttribute 檢查
        if (model.containsAttribute("error")) {
            // 已自動加入，不需再次處理，Thymeleaf 可直接用 ${error}
        }

        return "registerAndLogin/memberRegisterConF";
    }
}