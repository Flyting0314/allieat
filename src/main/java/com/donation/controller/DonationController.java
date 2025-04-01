package com.donation.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.donation.model.DonationService;
import com.entity.DonationVO;

import jakarta.validation.Valid;



@Controller
@RequestMapping("/donation")
public class DonationController {
	
	@Autowired
	DonationService donationSvc;
	
	@GetMapping("/")
	public String myMethod() {
		return "indexDonation.html";  // src\main\resources\templates\index.html
	}

	
	@GetMapping("addDonation")
	public String addDonation(ModelMap model) {
		DonationVO donationVO = new DonationVO();
		model.addAttribute("donationVO", donationVO);
		return "back-end/donation/addDonation";
	}
	
	
	// 列出所有捐款紀錄
	@GetMapping("/list")
	public String getAllDonation(Model model) {
	    List<DonationVO> donationList = donationSvc.getAllDonation();
	    model.addAttribute("donationList", donationList);  // 變數名稱需與 HTML 一致
	    return "back-end/donation/listAllDonation"; // 確保回傳的名稱與 Thymeleaf 檔案對應
	}
	
	
	
//	@PostMapping("insert")
//	public String insert(@Valid DonationVO donationVO, BindingResult result, ModelMap model,
//			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {
//
//		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
//		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
//		result = removeFieldError(donationVO, result, "upFiles");
//			model.addAttribute("errorMessage", "新增失敗:待修改");
//		
//		if (result.hasErrors() ) {
//			return "back-end/donation/addDonation";
//		}
//		/*************************** 2.開始新增資料 *****************************************/
//		// DonationService donationSvc = new DonationService();
//		donationSvc.addDonation(donationVO);
//		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
//		List<DonationVO> list = donationSvc.getAll();
//		model.addAttribute("donationListData", list);
//		model.addAttribute("success", "- (新增成功)");
//		return "redirect:/donation/listAllDonation"; // 新增成功後重導至IndexController_inSpringBoot.java的第58行@GetMapping("/donation/listAllDonation")
//	}
	

	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("rankId") String rankId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		// DonationService donationSvc = new DonationService();
		DonationVO donationVO = donationSvc.getOneDonation(Integer.valueOf(rankId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("donationVO", donationVO);
		return "back-end/donation/update_donation_input"; // 查詢完成後轉交update_donation_input.html
	}

	@PostMapping("update")
	public String update(@Valid DonationVO donationVO, BindingResult result, ModelMap model) 
			throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		

		if (result.hasErrors()) {
			return "back-end/donation/update_donation_input";
		}
		/*************************** 2.開始修改資料 *****************************************/
		// DonationService donationSvc = new DonationService();
		donationSvc.updateDonation(donationVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		donationVO = donationSvc.getOneDonation(Integer.valueOf(donationVO.getRankId()));
		model.addAttribute("donationVO", donationVO);
		return "back-end/donation/listOneDonation"; // 修改成功後轉交listOneDonation.html
	}
	
	
//	@PostMapping("delete")
//	public String delete(@RequestParam("rankId") String rankId, ModelMap model) {
//		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
//		/*************************** 2.開始刪除資料 *****************************************/
//		// DonationService donationSvc = new DonationService();
//		donationSvc.deleteDonation(Integer.valueOf(rankId));
//		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
//		List<DonationVO> list = donationSvc.getAll();
//		model.addAttribute("donationListData", list);
//		model.addAttribute("success", "- (刪除成功)");
//		return "back-end/donation/listAllDonation"; // 刪除完成後轉交listAllDonation.html
//	}

	/*
	 * 第一種作法 Method used to populate the List Data in view. 如 : 
	 * <form:select path="rankId" id="rankId" items="${donationListData}" itemValue="rankId" itemLabel="dname" />
	 */
	@ModelAttribute("donationListData")
	protected List<DonationVO> referenceListData() {
		// DonationService donationSvc = new DonationService();
		List<DonationVO> list = donationSvc.getAllDonation();
		return list;
	}
	

	/*
	 * 【 第二種作法 】 Method used to populate the Map Data in view. 如 : 
	 * <form:select path="rankId" id="rankId" items="${depMapData}" />
	 */
//	@ModelAttribute("donationMapData") //
//	protected Map<Integer, String> referenceMapData() {
//		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
//		map.put(10, "財務部");
//		map.put(20, "研發部");
//		map.put(30, "業務部");
//		map.put(40, "生管部");
//		return map;
//	}

	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(DonationVO donationVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(donationVO, "donationVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
	/*
	 * This method will be called on select_page.html form submission, handling POST request
	 */
	@PostMapping("listDonations_ByCompositeQuery")
	public String listAllDonation(Model model) {
		List<DonationVO> list = donationSvc.getAllDonation();
		model.addAttribute("donationListData", list); // for listAllDonation.html 第85行用
		return "back-end/donation/listAllDonation";
	}
	
}
