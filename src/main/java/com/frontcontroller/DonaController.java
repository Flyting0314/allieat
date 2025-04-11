package com.frontcontroller;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.entity.DonaVO;
import com.dona.model.DonaReq;
import com.dona.model.DonaReq.StepOne;
import com.dona.model.DonaReq.StepTwo;
import com.frontservice.DonaService;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("donaReq")
@RequestMapping("/dona")
public class DonaController {

	@Autowired
	private DonaService donaService;
	@Autowired
	private SmartValidator validator;
	
//	@ModelAttribute("donaReq")
//    public DonaReq donaReq() {
//        return new DonaReq(); // 預設表單物件
//    }
	
	

	// === 清空 session ===
		@ModelAttribute("donaReq")
		public DonaReq initializeDonaReq() {
		    DonaReq donaReq = new DonaReq();
		    donaReq.setIsRadio(false);
		    donaReq.setIsLightbox(false);

	        donaReq.setAgreePrivacy(false);
	        donaReq.setCompanyDonor(false); // 預設為個人捐款
		    return donaReq;
		}
		
	@GetMapping("/")
	public String index() {
		return "index";
	}
	@GetMapping("")
	public String redirectToIndex() {
	    return "redirect:/allieatnew/"; // 重導向至正確路徑
	}

	// 列出所有捐款紀錄
	@GetMapping("/list")
	public String getAllDonas(Model model) {
		List<DonaVO> donaList = donaService.getAllDonas();
		model.addAttribute("donaList", donaList);
		return "dona/listAllDona";
	}

//錯誤頁面
	@GetMapping("/exception")
	public String showException() {
		throw new RuntimeException();
	}
	// ========= 捐款三步驟開始 =========
	
	// 第一階段：顯示第一階段頁面	
    @GetMapping("donaAddA")
    public String showAddTypeForm(Model model) {
        model.addAttribute("donaReq", new DonaReq());
        return "dona/donaAddA"; // 第一階段頁面
    }

    // 第一階段：處理第一階段表單
    @PostMapping("donaAddA")
    public String handleAddType( @ModelAttribute("donaReq") DonaReq donaReq, BindingResult result) {
    	 
    	if (donaReq.getDonationType() == null) {
             result.rejectValue("donationType", "error.donationType", "請選擇捐款類型");
         }
         if (donaReq.getDonationIncome() == null || donaReq.getDonationIncome() <= 0) {
             result.rejectValue("donationIncome", "error.donationIncome", "請填寫有效的金額");
         }

    	
    	System.out.println("Received donationType: " + donaReq.getDonationType());
        System.out.println("Received donationIncome: " + donaReq.getDonationIncome());
        validator.validate(donaReq, result, StepOne.class);
    	if (result.hasErrors()) {
    		result.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
        	System.out.println("失敗");
            return "dona/donaAddA"; // 若驗證失敗，返回第一階段頁面
        }
    

        // 確保捐款類型已填寫
//        if (donaReq.getDonationIncome() == null || donaReq.getDonationIncome() <= 0) {
//            result.rejectValue("donationIncome", "error.donationIncome", "請填寫有效的金額");
//            return "dona/donaAddA";
//        }
//        model.addAttribute("donaReq", donaReq); // 暫存資料..@SessionAttributes不需要
        return "redirect:/dona/donaAddB"; // 跳轉至第二階段
    }

    // 第二階段：顯示第二階段頁面
    @GetMapping("donaAddB")
    public String showAddInfoForm(@ModelAttribute("donaReq") DonaReq donaReq, Model model) {
        model.addAttribute("showErrors", false);
        model.addAttribute("activeTab", donaReq.getCompanyDonor() ? "company" : "personal");
        return "dona/donaAddB";
    }

    @PostMapping("donaAddB")
    public String handleAddInfo(
            @ModelAttribute("donaReq") DonaReq donaReq, BindingResult result, Model model) {

        validator.validate(donaReq, result); // class-level 驗證器
        validator.validate(donaReq, result, StepTwo.class); // field 驗證器

        boolean isCompany = Boolean.TRUE.equals(donaReq.getCompanyDonor());

        if (result.hasErrors()) {
            model.addAttribute("showErrors", true); // <== 關鍵：送出才顯示錯誤
            model.addAttribute("activeTab", isCompany ? "company" : "personal");
            return "dona/donaAddB";
        }

        return "redirect:/dona/donaAddC";
    }
    
    // 第三階段：顯示第三階段頁面
    @GetMapping("/donaAddC")
    public String showAddFinalForm(@ModelAttribute("donaReq") DonaReq donaReq, Model model) {
//        model.addAttribute("donaReq", donaReq); // 帶入前兩階段暫存資料..@SessionAttributes不需要
        return "dona/donaAddC"; // 第三階段頁面
    }

    @PostMapping("/donaAddC")
    public String insertDona(
            @RequestParam String cardNumber, 
            @RequestParam String cardExpiry, 
            @RequestParam String cardCvv,
            @ModelAttribute("donaReq") DonaReq donaReq, 
            BindingResult result, RedirectAttributes redirectAttributes) {
        
    	System.out.println("收到信用卡資訊: " + cardNumber + ", " + cardExpiry + ", " + cardCvv);
    	// 驗證信用卡資料
        if (!donaService.isValidCard(cardNumber, cardExpiry, cardCvv)) {
        	
        	System.out.println("❌ 信用卡驗證失敗");
        	result.reject("creditCard.invalid", "信用卡資訊不正確！");
            return "dona/donaAddC"; // 返回第三階段頁面
        }
        System.out.println("✅ 信用卡驗證成功，準備儲存捐款");
        // 儲存捐款資料（不儲存信用卡信息）
     // 儲存並回傳真正的 DonaVO 實體
        DonaVO savedDona = donaService.insertDona(donaReq);
        System.out.println("🚀 前往成功頁面 dona = " + savedDona);
       
        redirectAttributes.addFlashAttribute("dona", savedDona);
     // 印出儲存的捐款資訊（幫助你確認有沒有正確放進 model）
        

        // 重導向至完成頁面
        return "redirect:/dona/donaAddOne";
      
    }   
  //最後捐完顯示
  	@GetMapping("/donaAddOne")
  	public String showDonaSuccessPage(@ModelAttribute("dona") DonaVO dona, Model model, SessionStatus status) {
  		 
  		System.out.println("======跳轉到 donaAddOneF.html 準備中...======");
  		
  		// 顯示資料
//  	    model.addAttribute("dona", donaReq);
  		 model.addAttribute("dona", dona);
  	    // ✅ 清除 Session 避免 F5 重送
  	    status.setComplete();

  	    return "dona/donaAddOneF";
  	}
	// ========= 捐款三步驟結束 =========
    
    
//    // 最終頁面：顯示完成資訊
//    @GetMapping("/addShow")
//    public String showCompletedPage(@ModelAttribute("donaReq") DonaReq donaReq, Model model) {
//        model.addAttribute("donaReq", donaReq); // 顯示保存成功的資料
//        return "dona/donaAddShow"; // 最終頁面
//    }
 // 清理會話數據因為@SessionAttributes的緣故
    @PostMapping("/complete")
    public String completeForm(SessionStatus status) {
    	status.setComplete(); // 清除會話中的 donaReq
        return "redirect:/dona/donaAddD";
    }

	// 顯示修改表單
	@GetMapping("/update/{donationRecordId}")
	public String showUpdateForm(@PathVariable Integer donationRecordId, Model model) {
		DonaVO dona = donaService.findById(donationRecordId);
		DonaReq donaReq = new DonaReq();
		if (dona != null) {
			donaReq.setDonationRecordId(dona.getDonationRecordId());
			donaReq.setIdentityData(dona.getIdentityData());
			donaReq.setDonationIncome(dona.getDonationIncome());
			donaReq.setEmail(dona.getEmail());
			donaReq.setPhone(dona.getPhone());
			donaReq.setBthDate(dona.getBthDate());
			donaReq.setGender(dona.getGender());
			donaReq.setCounty(dona.getCounty());
			donaReq.setDistrict(dona.getDistrict());
			donaReq.setPostalCode(dona.getPostalCode());
			donaReq.setAddress(dona.getAddress());
			donaReq.setMailMtd(dona.getMailMtd());
			donaReq.setSalutation(dona.getSalutation());
			donaReq.setIdNum(dona.getIdNum());
			donaReq.setGuiNum(dona.getGuiNum());
			donaReq.setAnonymous(dona.getAnonymous());
			donaReq.setDonationType(dona.getDonationType());
		}
		model.addAttribute("donaReq", donaReq);

		if (dona != null) {
		    System.out.println("Birthday from Dona: " + dona.getBthDate());
		    System.out.println("Gender from Dona: " + dona.getGender());
		}
		return "dona/update_dona_input";
	}

	// 處理修改紀錄
	@PostMapping("/update")
	public String updateDona(@Valid @ModelAttribute DonaReq donaReq, BindingResult result) {
		if (result.hasErrors()) {
			return "dona/update_dona_input"; // 若驗證失敗，返回修改表單
		}
		donaService.updateDona(donaReq.getDonationRecordId(), donaReq);
		return "redirect:/dona/list"; // 成功後重導向列表
	}
	
	
	// 顯示捐款查詢頁面
    @GetMapping("/donaAddD")
    public String showSelectPage(Model model) {
        model.addAttribute("donaReq", new DonaReq()); // 初始化查詢條件物件
        return "dona/donaAddD"; // 返回查詢頁面
    }

 // 處理查詢表單並返回結果
    @PostMapping("/donaAddD")
    public String handleSelect(
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer startMonth,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) Integer endMonth,
            @ModelAttribute DonaReq donaReq,
            Model model) {
        // 利用服務層方法構造日期範圍
        Timestamp startTime = donaService.createTimestamp(startYear, startMonth, 1);  // 開始日期
        Timestamp endTime = donaService.createTimestamp(endYear, endMonth, 31);      // 結束日期（假設月份最大為 31 天）
     // 驗證輸入條件（捐款抬頭 + 身分證 或 統編）
        boolean hasSalutation = donaReq.getSalutation() != null && !donaReq.getSalutation().isBlank();
        boolean hasId = donaReq.getIdNum() != null && !donaReq.getIdNum().isBlank();
        boolean hasGui = donaReq.getGuiNum() != null && !donaReq.getGuiNum().isBlank();
     //  若驗證失敗，顯示錯誤並不查詢
        if (!hasSalutation || (!hasId && !hasGui)) {
            model.addAttribute("errorMessage", "請輸入捐款抬頭，並輸入身分證或統一編號其中一項");
            model.addAttribute("donaList", List.of()); // 清空表格
            return "dona/donaAddD"; // 提前 return
        }
        // 根據條件進行查詢
        List<DonaVO> filteredDonaList = donaService.searchDonas(donaReq, startTime, endTime);
        model.addAttribute("donaList", filteredDonaList); // 添加篩選結果到 Model
        return "dona/donaAddD"; // 返回查詢結果頁面
    }
 // 處理查詢表單並返回結果2版本  
//    @GetMapping("/donaAddD")
//    public String showSelectPage(Model model) {
//        model.addAttribute("donaReq", new DonaReq());
//        return "dona/donaAddD";
//    }
//
//    @PostMapping("/donaAddD")
//    public String handleSelect(@RequestParam(required = false) Integer startYear, @RequestParam(required = false) Integer startMonth, @RequestParam(required = false) Integer endYear, @RequestParam(required = false) Integer endMonth, @ModelAttribute DonaReq donaReq, Model model) {
//        Timestamp startTime = donaService.createTimestamp(startYear, startMonth, 1);
//        Timestamp endTime = donaService.createTimestamp(endYear, endMonth, 31);
//        List<Dona> filteredDonaList = donaService.searchDonas(donaReq, startTime, endTime);
//        model.addAttribute("donaList", filteredDonaList);
//        return "dona/donaAddD";
//    }

//	// 顯示捐款查詢頁面(原本的)
//    @GetMapping("/select")
//    public String showSelectPage(Model model) {
//        model.addAttribute("donaList", donaService.getAllDonas()); // 獲取所有資料作為下拉選單
//        return "dona/select_page";
//    }

		
//	@PostMapping("getOne")
//	public String getOneDona(@RequestParam Integer donationRecordId, Model model) {
//	    // 透過 donationRecordId 查詢資料
//	    Dona dona = donaService.findById(donationRecordId);
//	    if (dona != null) {
//	        model.addAttribute("dona", dona); // 將結果傳遞給前端
//	        return "dona/listOneDona";       // 返回對應的 Thymeleaf 頁面
//	    } else {
//	        model.addAttribute("errorMessage", "找不到對應的捐款紀錄");
//	        return "dona/errorPage";         // 返回錯誤頁面
//	    }
//	}
    
    @PostMapping("/getOne")
    public String getOneDona(@RequestParam Integer donationRecordId, Model model) {
    	DonaVO dona = donaService.findById(donationRecordId);
        if (dona != null) {
            model.addAttribute("dona", dona); // 如果找到資料，將資料添加到 Model
        } else {
            model.addAttribute("errorMessage", "找不到對應的捐款紀錄"); // 找不到時添加錯誤訊息
        }
        model.addAttribute("donaList", donaService.getAllDonas()); // 用於下拉選單
        return "dona/select_page"; // 返回查詢頁面本身
    }

    
	@PostMapping("/delete/{donationRecordId}")
	public String deleteDona(@PathVariable Integer donationRecordId) {
		donaService.deleteDona(donationRecordId);
		return "redirect:/dona/list";
	}
	
	
	 

	
	
	
//	// 顯示新增表單(原測版)
//	@GetMapping("/add")
//	public String showAddForm(Model model) {
//		model.addAttribute("donaReq", new DonaReq());
//		return "dona/addDona";
//	}
//
//	// 處理新增捐款紀錄(原測版)
//	@PostMapping("/add")
//	public String insertDona(@Valid @ModelAttribute DonaReq donaReq, BindingResult result) {
//		if (result.hasErrors()) {
//			return "dona/addDona"; // 若驗證失敗，返回新增表單
//		}
//		donaService.insertDona(donaReq);
//		return "redirect:/dona/list"; // 成功後重導向列表
//	}
}