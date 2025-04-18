package com.frontcontroller;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
    public String registerPage(@ModelAttribute("member") MemberVO member, Model model, HttpSession session,SessionStatus sessionStatus) {
    	 sessionStatus.setComplete();
    	session.removeAttribute("member");
        session.removeAttribute("store");
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

    @PostMapping({"", "/"})
    public String register(@Valid @ModelAttribute("member") MemberVO member,
                           BindingResult result,
                           @RequestParam(value = "kycFile", required = false) MultipartFile kycFile,
                           @RequestParam(value = "agreedToTerms", required = false) String agreedToTerms,
                           HttpSession session,
                           Model model) {
    	System.out.println("✅ POST 會員註冊進來了");

        model.addAttribute("hasSubmitted", true);

        MultipartFile sessionKycFile = (MultipartFile) session.getAttribute("kycFile");

        
        boolean hasFileError = (kycFile == null || kycFile.isEmpty()) && sessionKycFile == null;
        boolean hasTermsError = !"true".equals(agreedToTerms);
        boolean hasOrgError = result.hasFieldErrors("organization.organizationId");
        
        if (member.getPermAddr() == null || member.getPermAddr().contains("請選擇")) {
            result.rejectValue("permAddr", null, "請正確填寫戶籍地址");
        }
        if (member.getAddress() == null || member.getAddress().isBlank() || member.getAddress().contains("請選擇")) {
            result.rejectValue("address", null, "請正確填寫通訊地址");
        }
        System.out.println("是否有錯誤？" + result.hasErrors());
        System.out.println("permAddr 是否為空字串？" + member.getPermAddr().isEmpty());
        if (hasFileError || hasTermsError || result.hasErrors()) {
        	System.out.println("==== 進入驗證錯誤邏輯區塊 ====");
            System.out.println("permAddr: " + member.getPermAddr());
            System.out.println("email: " + member.getEmail());

            for (FieldError fieldError : result.getFieldErrors()) {
                System.out.println("欄位: " + fieldError.getField());
                System.out.println("錯誤訊息: " + fieldError.getDefaultMessage());
            }
            if (member.getPermAddr() != null && member.getPermAddr().length() > 6) {
                String detail = member.getPermAddr().substring(6); // 郵遞區號+縣市+區=約6碼
                model.addAttribute("permDetail", detail);
            }
            if (member.getAddress() != null && member.getAddress().length() > 6) {
                String detail = member.getAddress().substring(6);
                model.addAttribute("commDetail", detail);
            }
            if (hasFileError) {
                model.addAttribute("error", "請上傳身分驗證檔案");
            }

            if (!"true".equals(agreedToTerms)) {
                model.addAttribute("errorMessage", "請詳閱並同意點數與須知相關條款");
            }

            if (member.getOrganization() == null || member.getOrganization().getOrganizationId() == null) {
                result.rejectValue("organization.organizationId", null, "請選擇註冊單位");
            }

            model.addAttribute("organizations", organizationRepository.findByStatus(1));
            return "registerAndLogin/memberRegister";
        }

        try {
            MultipartFile finalFile = (kycFile != null && !kycFile.isEmpty()) ? kycFile : sessionKycFile;
            memberService.prepareMemberForSession(member, finalFile, agreedToTerms);
            session.setAttribute("kycFile", finalFile); // ✅ 保留檔案
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
            return "redirect:/registerAndLogin/register/member";
        }

        try {
            memberService.finalizeRegistration(member);
            status.setComplete(); 
            redirectAttributes.addFlashAttribute("showModal", true);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registerAndLogin/register/member/confirm";
        }
    }
}