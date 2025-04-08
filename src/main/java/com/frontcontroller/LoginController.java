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
                        Model model,RedirectAttributes redirectAttrs,@RequestParam(value = "forceEdit", required = false) Boolean forceEdit) {

        if ("member".equals(userType)) {
            MemberVO member = memberLoginService.login(account, password);
            if (member == null) {
                model.addAttribute("error", "帳號或密碼錯誤！");
                return "registerAndLogin/login";
            }
            // ✅ 加入審核與啟用狀態檢查
            if (member.getReviewed() == 3) {
                model.addAttribute("error", "帳號審核中，請耐心等候審核通知信件！");
                return "registerAndLogin/login";
            } else if (member.getReviewed() == 2) {
                model.addAttribute("error", "未通過審核，已將結果寄送至您的信箱");
                return "registerAndLogin/login";
            } else if (member.getReviewed() == 1 && member.getAccStat() == 0) {
                model.addAttribute("error", "帳號已審核通過，請至信箱點擊啟用連結後再登入！");
                return "registerAndLogin/login";
            } else if (member.getAccStat() == 0) {
                model.addAttribute("error", "店家資料補件通知已寄送，請依指示補齊相關資料");
                return "registerAndLogin/login";
            }
            
            session.setAttribute("loggedInMember", member);
            List<MemberOrderRecordDTO> history = memberLoginService.getOrderRecords(member.getMemberId());
            model.addAttribute("member", member);
            model.addAttribute("payDetail", history); 
            model.addAttribute("userType", userType);
            return "registerAndLogin/memberInfo"; 

        }
        if ("store".equals(userType)) {
            StoreVO store = storeLoginService.login(account, password); // 用 email 查
            if (store == null) {
                model.addAttribute("error", "帳號或密碼錯誤！");
                model.addAttribute("userType", userType);
                return "registerAndLogin/login";
            }
            // ✅ 加入審核與啟用狀態檢查
            if (store.getReviewed() == 3) {
                model.addAttribute("error", "帳號審核中，請耐心等候審核通知信件！");
                return "registerAndLogin/login";
            } else if (store.getReviewed() == 2) {
                model.addAttribute("error", "未通過審核，已將結果寄送至您的信箱");
                return "registerAndLogin/login";
            } else if (store.getReviewed() == 1 && store.getAccStat() == 0) {
                model.addAttribute("error", "帳號已審核通過，請至信箱點擊啟用連結後再登入！");
                return "registerAndLogin/login";
            } else if (store.getAccStat() == 0) {
                model.addAttribute("error", "店家資料補件通知已寄送，請依指示補齊相關資料");
                return "registerAndLogin/login";
            }
            
            session.setAttribute("loggedInStore", store);
            model.addAttribute("userType", userType);
            model.addAttribute("store", store);
            
            // ✅ 檢查是否尚未設定營業資訊
//            if (store.getOpTime() == null || store.getCloseTime() == null ||
//                store.getLastOrder() == null || store.getPickTime() == null) {
//                // ✅ 轉導到強制編輯頁面（或用參數控制進入編輯模式）
//                return "redirect:/registerAndLogin/storeInfo?forceEdit=true";
//            }
            if (store.getAccStat() == 1 &&
            	    storeLoginService.isStoreMissingOpeningInfo(store) &&
            	    !Boolean.TRUE.equals(forceEdit)) {
                redirectAttrs.addFlashAttribute("warning", "首次登入請設定營業時間！");
                return "redirect:/registerAndLogin/storeInfo?forceEdit=true";
            }
            return "redirect:/registerAndLogin/storeInfo";
        }
        

        model.addAttribute("error", "請選擇有效的使用者類型");
        model.addAttribute("userType", userType);
        return "registerAndLogin/login";
    }
    @PostMapping("/store/update")
    public String updateStoreInfo(@ModelAttribute StoreVO formInput,
                                  HttpSession session,
                                  RedirectAttributes redirectAttrs) {

        StoreVO sessionStore = (StoreVO) session.getAttribute("loggedInStore");
        if (sessionStore == null) {
            redirectAttrs.addFlashAttribute("error", "請先登入後再操作");
            return "redirect:/registerAndLogin/login";
        }
//     // ✅ 使用 service 方法來檢查營業時間是否缺漏
//        if (storeLoginService.isStoreMissingOpeningInfo(formInput)) {
//            redirectAttrs.addFlashAttribute("error", "所有營業時間欄位皆為必填，請完整填寫後再儲存！");
//            return "redirect:/registerAndLogin/storeInfo?forceEdit=true";
//        }
        if (storeLoginService.isInvalidTimeOrder(formInput)) {
            redirectAttrs.addFlashAttribute("error", "營業時間順序有誤，請確認後再儲存！");
            return "redirect:/registerAndLogin/storeInfo?forceEdit=true";
        }
        try {
            storeLoginService.updateStoreEditableFields(sessionStore, formInput);
            System.out.println("收到更新請求：" + formInput.getOpTime() + ", " + formInput.getCloseTime());
            redirectAttrs.addFlashAttribute("success", "資料更新成功！");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/registerAndLogin/storeInfo";
        }

        return "redirect:/registerAndLogin/storeInfo";
    }


    @GetMapping("/storeInfo")
    public String storeInfoPage(@RequestParam(value = "forceEdit", required = false) Boolean forceEdit,HttpSession session, Model model,RedirectAttributes redirectAttrs) {
        StoreVO store = (StoreVO) session.getAttribute("loggedInStore");
        if (store == null) {
            System.out.println("使用者未登入，導向登入頁面");
            return "redirect:/registerAndLogin/login";
        }
     // ✅ 檢查是否尚未填營業資訊，導向強制編輯模式
        if (store.getAccStat() == 1 &&
        	    storeLoginService.isStoreMissingOpeningInfo(store) &&
        	    !Boolean.TRUE.equals(forceEdit)) {
            redirectAttrs.addFlashAttribute("warning", "首次登入請設定營業時間");
            return "redirect:/registerAndLogin/storeInfo?forceEdit=true";
        }
        model.addAttribute("forceEdit", forceEdit != null && forceEdit);

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
    
    @GetMapping("/resendMail")
    public String showResendPage() {
        return "registerAndLogin/resendMail"; // 對應上面那個 HTML 頁面
    }
//  @GetMapping("/storeInfo")
//  public String storeInfoPage(HttpSession session, Model model) {
//      StoreVO store = (StoreVO) session.getAttribute("loggedInStore");
//      if (store == null) 
//      return "redirect:/registerAndLogin/login";
//
//      model.addAttribute("store", store);
//
//      // 店家照片（封面照）
//      List<PhotoVO> photos = photoRepository.findByStoreOrderByUpdateTimeAsc(store);
//      if (!photos.isEmpty()) {
//          String base64Cover = Base64.getEncoder().encodeToString(photos.get(0).getPhotoSrc());
//          model.addAttribute("coverPhoto", base64Cover);
//      }
//
//      return "registerAndLogin/storeInfo";
//  }
}
