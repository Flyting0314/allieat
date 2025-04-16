package com.frontcontroller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.backstage.backstagrepository.PhotoRepository;
import com.entity.MemberOrderRecordDTO;
import com.entity.MemberVO;
import com.entity.PhotoVO;
import com.entity.StoreVO;
import com.frontservice.MemberLoginService;
import com.frontservice.StoreLoginService;
import com.frontservice.StoreRegistAndLoginService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/registerAndLogin")
public class LoginController {
	@Autowired
	private PhotoRepository photoRepository;
    @Autowired
    private MemberLoginService memberLoginService;
    @Autowired
    private StoreRegistAndLoginService storeRegistAndLoginService;
    @Autowired
    private StoreLoginService storeLoginService;
    
	@GetMapping("/register")
	public String index(HttpSession session, Model model) {
		 if (session.getAttribute("member") != null || session.getAttribute("store") != null) {
		        return "redirect:/registerAndLogin/logout?redirectTo=register";
		    }
		return "registerAndLogin/registerAndLogin";
	}
	
    @GetMapping("/login")
    public String showLoginPage(Model model) {
    	model.addAttribute("isLoggedIn", true);
        return "registerAndLogin/login"; 
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

            if (member.getReviewed() == 3) {
                model.addAttribute("error", "帳號審核中，請耐心等候審核通知信件！");
                return "registerAndLogin/login";
            } else if (member.getReviewed() == 2) {
            	session.setAttribute("reuploadMember", member); 
                return "redirect:/registerAndLogin/reupload";  
            } else if (member.getReviewed() == 1 && member.getAccStat() == 0) {
                model.addAttribute("error", "帳號已審核通過，請至信箱點擊啟用連結後再登入！");
                return "registerAndLogin/login";
            } else if (member.getAccStat() == 2) {
                model.addAttribute("error", "帳號異常,請聯繫官方");
                return "registerAndLogin/login";
            }
            
            session.setAttribute("member", member);
            List<MemberOrderRecordDTO> history = memberLoginService.getOrderRecords(member.getMemberId());
            model.addAttribute("member", member);
            model.addAttribute("payDetail", history); 
            model.addAttribute("userType", userType);
            return "redirect:/registerAndLogin/memberInfo";  

        }
        if ("store".equals(userType)) {
            StoreVO store = storeLoginService.login(account, password); // 用 email 查
            if (store == null) {
                model.addAttribute("error", "帳號或密碼錯誤！");
                model.addAttribute("userType", userType);
                return "registerAndLogin/login";
            }

            if (store.getReviewed() == 3) {
                model.addAttribute("error", "帳號審核中，請耐心等候審核通知信件！");
                return "registerAndLogin/login";
            } else if (store.getReviewed() == 2) {
                session.setAttribute("reuploadStore", store); // 補件 session
                return "redirect:/registerAndLogin/reuploadStore"; // 導向補件頁
            } else if (store.getReviewed() == 1 && store.getAccStat() == 0) {
                model.addAttribute("error", "帳號已審核通過，請至信箱點擊啟用連結後再登入！");
                return "registerAndLogin/login";
            } else if (store.getAccStat() == 2) {
                model.addAttribute("error", "帳號異常,請聯繫官方");
                return "registerAndLogin/login";
            }
            
            session.setAttribute("store", store);
            model.addAttribute("userType", userType);
            model.addAttribute("store", store);
            

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
    @GetMapping("/memberInfo")
    public String showMemberInfo(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("member"); 

        if (member == null) {
            return "redirect:/registerAndLogin/login";
        }

        model.addAttribute("member", member);



        return "registerAndLogin/memberInfo"; 
    }
    
    @PostMapping("/store/update")
    public String updateStoreInfo(@ModelAttribute StoreVO formInput,
                                  HttpSession session,
                                  RedirectAttributes redirectAttrs) {

        StoreVO sessionStore = (StoreVO) session.getAttribute("store");
        if (sessionStore == null) {
            redirectAttrs.addFlashAttribute("error", "請先登入後再操作");
            return "redirect:/registerAndLogin/login";
        }

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
        StoreVO store = (StoreVO) session.getAttribute("store");
        if (store == null) {
            System.out.println("使用者未登入，導向登入頁面");
            return "redirect:/registerAndLogin/login";
        }
     
        if (store.getAccStat() == 1 &&
        	    storeLoginService.isStoreMissingOpeningInfo(store) &&
        	    !Boolean.TRUE.equals(forceEdit)) {
            redirectAttrs.addFlashAttribute("warning", "首次登入請設定營業時間");
            return "redirect:/registerAndLogin/storeInfo?forceEdit=true";
        }
        model.addAttribute("forceEdit", forceEdit != null && forceEdit);

        model.addAttribute("store", store);

        
        System.out.println("查詢封面照: storeId=" + store.getStoreId());
        Optional<PhotoVO> coverPhotoOpt = photoRepository.findFirstByStoreStoreIdAndPhotoTypeOrderByUpdateTimeDesc(store.getStoreId(), "COVER");


        List<PhotoVO> photos = photoRepository.findByStoreStoreId(store.getStoreId());
        List<Map<String, String>> photoList = new ArrayList<>();

        for (PhotoVO photo : photos) {
            Map<String, String> map = new HashMap<>();
            map.put("photoType", photo.getPhotoType());
            map.put("src", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(photo.getPhotoSrc()));
            photoList.add(map);
        }

        model.addAttribute("photoList", photoList);

        return "registerAndLogin/storeInfo"; 
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/registerAndLogin/login"; 
    }
    

    
    @GetMapping("/resendMail")
    public String showResendPage() {
        return "registerAndLogin/resendMail";
    }
 
    @GetMapping("/redirectAfterLogin")
    public String redirectAfterLogin(HttpSession session) {
        if (session.getAttribute("member") != null) {
            return "redirect:/registerAndLogin/memberInfo";
        } else if (session.getAttribute("store") != null) {
            return "redirect:/registerAndLogin/storeInfo";
        } else {
            return "redirect:/registerAndLogin/login";
        }
    }
    @GetMapping("/reupload")
    public String showReuploadPage(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("reuploadMember");
        if (member == null) {
            return "redirect:/registerAndLogin/login";
        }
        model.addAttribute("member", member);
        return "registerAndLogin/registerReupload"; 
    }

    @PostMapping("/reupload")
    public String handleReupload(@RequestParam("memberId") Integer memberId,
                                 @RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        String result = memberLoginService.handleReupload(memberId, file);
        if ("success".equals(result)) {
            redirectAttributes.addFlashAttribute("success", "補件成功，請等待審核！");
            return "redirect:/registerAndLogin/login";
        } else {
            redirectAttributes.addFlashAttribute("error", result);
        }
        return "redirect:/registerAndLogin/reupload";
    }
    
    @GetMapping("/reuploadStore")
    public String showStoreReuploadPage(HttpSession session, Model model) {
        StoreVO store = (StoreVO) session.getAttribute("reuploadStore");
        if (store == null) return "redirect:/registerAndLogin/login";
        
       
        List<PhotoVO> photos = photoRepository.findByStoreStoreId(store.getStoreId());
        List<Map<String, String>> photoList = new ArrayList<>();
        for (PhotoVO photo : photos) {
            Map<String, String> map = new HashMap<>();
            map.put("photoType", photo.getPhotoType());
            map.put("src", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(photo.getPhotoSrc()));
            photoList.add(map);
        }
        model.addAttribute("photoList", photoList);

        model.addAttribute("store", store);
        return "registerAndLogin/registerReupload"; // 共用畫面
    }

    @PostMapping("/reuploadStore")
    public String handleStoreReupload(@RequestParam("storeId") Integer storeId,
                                      @RequestParam("photoFiles") MultipartFile[] photoFiles,
                                      RedirectAttributes redirectAttributes) {
        try {
            StoreVO store = storeRegistAndLoginService.getOneStore(storeId);
            if (store == null || store.getReviewed() != 2) {
                redirectAttributes.addFlashAttribute("error", "店家狀態錯誤或無需補件");
                return "redirect:/registerAndLogin/reuploadStore";
            }

            Set<PhotoVO> updatedPhotos = storeRegistAndLoginService.mergePhotos(store, photoFiles);
            store.setStoreToPhoto(updatedPhotos);
            store.setReviewed(3); // 補件後重新審核
            storeRegistAndLoginService.finalizeRegistration(store);
            redirectAttributes.addFlashAttribute("success", "補件成功，請等待審核！");
            return "redirect:/registerAndLogin/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "補件失敗：" + e.getMessage());
            return "redirect:/registerAndLogin/reuploadStore";
        }
    }
}

