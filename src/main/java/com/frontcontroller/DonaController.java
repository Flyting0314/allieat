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
	
		
	@GetMapping("/")
	public String index() {
		return "index";
	}
	@GetMapping("")
	public String redirectToIndex() {
	    return "redirect:/allieatnew/"; 
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
        return "dona/donaAddA"; 
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
            return "dona/donaAddA"; 
        }
        return "redirect:/dona/donaAddB"; 
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
            @ModelAttribute("donaReq") DonaReq donaReq,
            BindingResult result,
            Model model) {

        validator.validate(donaReq, result);
        validator.validate(donaReq, result, StepTwo.class);

        boolean isCompany = Boolean.TRUE.equals(donaReq.getCompanyDonor());

        if (!Boolean.TRUE.equals(donaReq.getAgreePrivacy())) {
            result.rejectValue("agreePrivacy", "error.agreePrivacy", "請詳閱並勾選同意個資運用聲明");
        }

        if (result.hasErrors()) {
            model.addAttribute("showErrors", true);
            model.addAttribute("activeTab", isCompany ? "company" : "personal");
            return "dona/donaAddB";
        }

        return "redirect:/dona/donaAddC";
    }
    // 第三階段：顯示第三階段頁面
    @GetMapping("/donaAddC")
    public String showAddFinalForm(@ModelAttribute("donaReq") DonaReq donaReq, Model model) {
    	System.out.println("donationType = " + donaReq.getDonationType());
//        model.addAttribute("donaReq", donaReq); // 帶入前兩階段暫存資料..@SessionAttributes不需要
        return "dona/donaAddC"; 
    }

    @PostMapping("/donaAddC")
    public String insertDona(
            @RequestParam String cardNumber, 
            @RequestParam String cardExpiry, 
            @RequestParam String cardCvv,
            @ModelAttribute("donaReq") DonaReq donaReq, 
            BindingResult result, RedirectAttributes redirectAttributes) {
        

    	// 驗證信用卡資料
        if (!donaService.isValidCard(cardNumber, cardExpiry, cardCvv)) {
        	result.reject("creditCard.invalid", "信用卡資訊不正確！");
            return "dona/donaAddC"; 
        }
        DonaVO savedDona = donaService.insertDona(donaReq);
        System.out.println(" 前往成功頁面 dona = " + savedDona);
       
        redirectAttributes.addFlashAttribute("dona", savedDona);
        
        return "redirect:/dona/donaAddOne";
    }   
  //最後捐完顯示
  	@GetMapping("/donaAddOne")
  	public String showDonaSuccessPage(@ModelAttribute("dona") DonaVO dona, Model model, SessionStatus status) { 		 
  		System.out.println("======跳轉到 donaAddOneF.html 準備中...======");  		
  		 model.addAttribute("dona", dona);
  	    // 清除 Session
  	    status.setComplete();
  	    return "dona/donaAddOneF";
  	}
	// ========= 捐款三步驟結束 =========
    
    

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
			return "dona/update_dona_input"; 
		}
		donaService.updateDona(donaReq.getDonationRecordId(), donaReq);
		return "redirect:/dona/list"; 
	}
	
	
	// 顯示捐款查詢頁面
    @GetMapping("/donaAddD")
    public String showSelectPage(Model model) {
        model.addAttribute("donaReq", new DonaReq()); 
        return "dona/donaAddD"; 
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
        
        Timestamp startTime = donaService.createTimestamp(startYear, startMonth, 1);  // 開始
        Timestamp endTime = donaService.createTimestamp(endYear, endMonth, 31);      // 結束
   
        boolean hasSalutation = donaReq.getSalutation() != null && !donaReq.getSalutation().isBlank();
        boolean hasId = donaReq.getIdNum() != null && !donaReq.getIdNum().isBlank();
        boolean hasGui = donaReq.getGuiNum() != null && !donaReq.getGuiNum().isBlank();
    
        if (!hasSalutation || (!hasId && !hasGui)) {
            model.addAttribute("errorMessage", "請輸入捐款抬頭，並輸入身分證或統一編號其中一項");
            model.addAttribute("donaList", List.of()); 
            return "dona/donaAddD"; 
        }
       
        List<DonaVO> filteredDonaList = donaService.searchDonas(donaReq, startTime, endTime);
        model.addAttribute("donaList", filteredDonaList); 
        return "dona/donaAddD"; 
    }


    
    @PostMapping("/getOne")
    public String getOneDona(@RequestParam Integer donationRecordId, Model model) {
    	DonaVO dona = donaService.findById(donationRecordId);
        if (dona != null) {
            model.addAttribute("dona", dona); 
        } else {
            model.addAttribute("errorMessage", "找不到對應的捐款紀錄"); 
        }
        model.addAttribute("donaList", donaService.getAllDonas()); 
        return "dona/select_page"; 
    }

    
	@PostMapping("/delete/{donationRecordId}")
	public String deleteDona(@PathVariable Integer donationRecordId) {
		donaService.deleteDona(donationRecordId);
		return "redirect:/dona/list";
	}
	
	// === 清空 session ===
	@ModelAttribute("donaReq")
	public DonaReq initializeDonaReq() {
	    DonaReq donaReq = new DonaReq();
	    donaReq.setIsRadio(false);
	    donaReq.setIsLightbox(false);

        donaReq.setAgreePrivacy(false);
        donaReq.setCompanyDonor(false); // 預設個人捐款
	    return donaReq;
	}
	
}