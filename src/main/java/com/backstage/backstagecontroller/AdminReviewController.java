package com.backstage.backstagecontroller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.backstage.backstagrepository.MemberRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.MemberVO;
import com.entity.StoreVO;
import com.frontservice.EmailService;

@Controller
@RequestMapping("/admin")
public class AdminReviewController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailService emailService;

    // 顯示店家 + 會員審核列表頁
    @GetMapping("/review")
    public String showReviewPage(Model model) {
        // 顯示 reviewed = 3（未審核）和 reviewed = 0（補件）的店家與會員
        List<StoreVO> unreviewedStores = storeRepository.findByReviewedIn(List.of(0, 3));
        List<MemberVO> unreviewedMembers = memberRepository.findByReviewedIn(List.of(0, 3));

        model.addAttribute("stores", unreviewedStores);
        model.addAttribute("members", unreviewedMembers);

        return "registerAndLogin/adminReview";
    }

    // 審核店家
    @PostMapping("/reviewStore")
    public String reviewStore(@RequestParam int storeId,
                              @RequestParam String approved,
                              RedirectAttributes redirectAttributes) {

        Optional<StoreVO> storeOpt = storeRepository.findById(storeId);
        if (storeOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "店家不存在");
            return "redirect:/admin/review";
        }

        StoreVO store = storeOpt.get();
        if ("true".equals(approved)) {
            store.setReviewed(1); // 通過
            emailService.sendVerificationEmail(store);
        } else if ("false".equals(approved)) {
            store.setReviewed(2); // 拒絕
            emailService.sendRejectionEmail(store);
        } else if ("correction".equals(approved)) {
            store.setReviewed(0); // 補件
            emailService.sendCorrectionEmail(store);
        }

        storeRepository.save(store);


        redirectAttributes.addFlashAttribute("message", "店家審核已完成");
        return "redirect:/admin/review";
    }

    // 審核會員
    @PostMapping("/reviewMember")
    public String reviewMember(@RequestParam int memberId,
                               @RequestParam String approved,
                               RedirectAttributes redirectAttributes) {

        Optional<MemberVO> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "會員不存在");
            return "redirect:/admin/review";
        }

        MemberVO member = memberOpt.get();
        if ("true".equals(approved)) {
        	member.setReviewed(1); // 通過
            emailService.sendMemberVerificationEmail(member);
        } else if ("false".equals(approved)) {
        	member.setReviewed(2); // 拒絕
            emailService.sendRejectionEmail(member);
        } else if ("correction".equals(approved)) {
        	member.setReviewed(0); // 補件
            emailService.sendCorrectionEmail(member);
        }
        memberRepository.save(member);

        redirectAttributes.addFlashAttribute("message", "會員審核已完成");
        return "redirect:/admin/review";
    }
}
