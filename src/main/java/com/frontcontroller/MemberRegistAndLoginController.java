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
@RequestMapping("/member")
public class MemberRegistAndLoginController {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberRegistAndLoginService memberService;
    @Autowired
    private OrganizationRepository organizationRepository;

    
	@GetMapping("/registerAndLogin")
	public String index() {
		return "member/registerAndLogin";
	}
    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("memberForm");
        if (member == null) {
            member = new MemberVO();
            member.setOrganization(new OrganizationVO());
        }
        List<OrganizationVO> organizations = organizationRepository.findAll();
        model.addAttribute("member", member);
        model.addAttribute("organizations", organizations);
        return "member/registerMember";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("member") MemberVO member,
                           BindingResult result,
                           @RequestParam("kycFile") MultipartFile kycFile,@RequestParam(value = "agreedToTerms", required = false) String agreedToTerms,
                           Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("organizations", organizationRepository.findAll());
            return "member/registerMember";
        }

        try {
            // ✅ 整合帳號驗證、單位邏輯與檔案儲存
            MemberVO preparedMember = memberService.prepareMemberForSession(member, kycFile,agreedToTerms);
            session.setAttribute("memberForm", preparedMember);
            return "redirect:/member/register/confirm";

        } catch (IllegalArgumentException | IOException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("organizations", organizationRepository.findAll());
            return "member/registerMember";
        }
    }
    @PostMapping("/register/submit")
    public String submitFinalRegister(HttpSession session, RedirectAttributes redirectAttributes) {
        MemberVO member = (MemberVO) session.getAttribute("memberForm");
        if (member == null || member.getKycImage() == null) {
            return "redirect:/member/register";
        }

        try {
            memberService.finalizeRegistration(member);
            session.removeAttribute("memberForm");

            // ✅ 傳遞 FlashAttribute 給 /register/confirm
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/member/register/confirm";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/member/register/confirm";
        }
    }

    @GetMapping("/register/confirm")
    public String confirmPage(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("memberForm");
        if (member == null) {
            return "redirect:/member/register";
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

        return "member/registerMemberConF";
    }



    
    
//    @GetMapping("/register/confirm")
//    public String confirmPage(HttpSession session, Model model) {
//        MemberVO member = (MemberVO) session.getAttribute("memberForm");
//        if (member == null) {
//            return "redirect:/member/register";
//        }
//        model.addAttribute("member", member);
//        return "member/registerMemberConF";
//    }

//    @PostMapping("/register/submit")
//    public String submitFinalRegister(HttpSession session, Model model) {
//        MemberVO member = (MemberVO) session.getAttribute("memberForm");
//        if (member == null || member.getKycImage() == null) {
//            return "redirect:/member/register";
//        }
//
//        try {
//            memberService.finalizeRegistration(member);
//            session.removeAttribute("memberForm");
//            return "redirect:/member/register/confirm?submitted=true";
//
//
//        } catch (IllegalArgumentException e) {
//            model.addAttribute("error", e.getMessage());
//            model.addAttribute("member", member);
//            return "member/registerMemberConF";
//        }
//    }


    @GetMapping("/login")
    public String loginPage() {
        return "member/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String account,
                        @RequestParam String password,
                        Model model) {
        MemberVO member = memberRepository.findByAccountAndPassword(account, password);
        if (member == null) {
            model.addAttribute("error", "帳號或密碼錯誤！");
            return "member/login";
        }
        return "redirect:/dashboard";
    }
}