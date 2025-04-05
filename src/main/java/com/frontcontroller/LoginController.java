package com.frontcontroller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.backstage.backstagrepository.PhotoRepository;
import com.entity.MemberOrderRecordDTO;
import com.entity.MemberVO;
import com.entity.PhotoVO;
import com.entity.StoreVO;
import com.frontservice.MemberLoginService;
import com.frontservice.StoreLoginService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/registerAndLogin")
public class LoginController {
	@Autowired
	private PhotoRepository photoRepository;
    @Autowired
    private MemberLoginService memberLoginService;

    @Autowired
    private StoreLoginService storeLoginService;
	@GetMapping("/register")
	public String index() {
		return "registerAndLogin/registerAndLogin";
	}
    @GetMapping("/login")
    public String showLoginPage() {
        return "registerAndLogin/login"; // 對應 login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String userType,
                        @RequestParam String account,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        if ("member".equals(userType)) {
            MemberVO member = memberLoginService.login(account, password);
            if (member == null) {
                model.addAttribute("error", "帳號或密碼錯誤！");
                return "registerAndLogin/login";
            }
            session.setAttribute("loggedInMember", member);
            List<MemberOrderRecordDTO> history = memberLoginService.getOrderRecords(member.getMemberId());
            model.addAttribute("member", member);
            model.addAttribute("history", history);
            return "registerAndLogin/memberInfo"; 

        }
        if ("store".equals(userType)) {
            StoreVO store = storeLoginService.login(account, password); // 用 email 查
            if (store == null) {
                model.addAttribute("error", "帳號或密碼錯誤！");
                return "registerAndLogin/login";
            }
            session.setAttribute("loggedInStore", store);
            model.addAttribute("store", store);
            return "redirect:/registerAndLogin/storeInfo";


        }

        model.addAttribute("error", "請選擇有效的使用者類型");
        return "registerAndLogin/login";
    }
    @PostMapping("/store/update")
    public String updateStoreInfo(@ModelAttribute StoreVO formInput,
                                  HttpSession session,
                                  RedirectAttributes redirectAttrs) {

        StoreVO sessionStore = (StoreVO) session.getAttribute("loggedInStore");
        if (sessionStore == null) {
            redirectAttrs.addFlashAttribute("error", "請先登入後再操作");
            return "redirect:/login";
        }

        boolean success = storeLoginService.updateStoreEditableFields(sessionStore, formInput);

        if (success) {
            redirectAttrs.addFlashAttribute("success", "資料更新成功！");
        } else {
            redirectAttrs.addFlashAttribute("error", "資料更新失敗，請確認欄位是否正確或重新登入");
        }

        return "redirect:/registerAndLogin/storeInfo"; // 頁面跳轉回店家首頁
    }
//    @GetMapping("/storeInfo")
//    public String storeInfoPage(HttpSession session, Model model) {
//        StoreVO store = (StoreVO) session.getAttribute("loggedInStore");
//        if (store == null) 
//        return "redirect:/registerAndLogin/login";
//
//        model.addAttribute("store", store);
//
//        // 店家照片（封面照）
//        List<PhotoVO> photos = photoRepository.findByStoreOrderByUpdateTimeAsc(store);
//        if (!photos.isEmpty()) {
//            String base64Cover = Base64.getEncoder().encodeToString(photos.get(0).getPhotoSrc());
//            model.addAttribute("coverPhoto", base64Cover);
//        }
//
//        return "registerAndLogin/storeInfo";
//    }
    @GetMapping("/storeInfo")
    public String storeInfoPage(HttpSession session, Model model) {
        StoreVO store = (StoreVO) session.getAttribute("loggedInStore");
        if (store == null) {
            System.out.println("使用者未登入，導向登入頁面");
            return "redirect:/registerAndLogin/login";
        }

        model.addAttribute("store", store);

        //  嘗試撈取 photoType = "COVER" 的封面照
        System.out.println("查詢封面照: storeId=" + store.getStoreId());
        Optional<PhotoVO> coverPhotoOpt = photoRepository.findFirstByStoreStoreIdAndPhotoTypeOrderByUpdateTimeDesc(store.getStoreId(), "COVER");

        if (coverPhotoOpt.isPresent()) {
            System.out.println("封面照，photoId=" + coverPhotoOpt.get().getPhotoId());
            String base64Cover = Base64.getEncoder().encodeToString(coverPhotoOpt.get().getPhotoSrc());
            String coverPhotoUrl = "data:image/jpeg;base64," + base64Cover;
            model.addAttribute("coverPhotoUrl", coverPhotoUrl);
            System.out.println("已成功載入");
        } else {
            System.out.println("使用預設圖片");
            model.addAttribute("coverPhotoUrl", "/img/default-cover.png");
        }

        return "registerAndLogin/storeInfo"; //  保留 return，確保視圖正確渲染
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 清除 session 
        System.out.println("登出session 清空");
        return "redirect:/registerAndLogin/login"; //  重導回登入頁面
    }
    
}
