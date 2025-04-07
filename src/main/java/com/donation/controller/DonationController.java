package com.donation.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.donation.model.DonationService;
import com.donation.model.PaymentRequest;
import com.entity.DonationVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/donation")
public class DonationController {
	
	@Autowired
	DonationService donationSvc;
	private final String HASH_KEY = "59IVo9slHjyqrXGzKK56Bf2uOOsvdpBa";
    private final String HASH_IV = "CN8TD5OVygqmdzgP";
    private final String MERCHANT_ID = "MS155406184";
    private final String donateUrl = "https://cdonate.newebpay.com/allieat";
	
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
	
	
	/**
	 * 
	 */
    // Step 1: 回傳金流參數給前端
    @PostMapping("/donate-params")
    public ResponseEntity<Map<String, String>> generateParams(@RequestBody PaymentRequest request) {
        long timestamp = System.currentTimeMillis() / 1000;
        String version = "1.1";

        Map<String, String> params = new LinkedHashMap<>();
        params.put("MerchantID", MERCHANT_ID);
        params.put("RespondType", "JSON");
        params.put("TimeStamp", String.valueOf(timestamp));
        params.put("Version", version);
        params.put("MerchantOrderNo", request.getOrderNo());
        params.put("Amt", String.valueOf(request.getAmount()));
        params.put("ItemDesc", request.getItemDesc());
        params.put("CREDIT", "on");
        params.put("NotifyURL", "https://localhost:8081/demo/donation/notify");
        params.put("ReturnURL", "https://localhost:8081/demo/donation/thank");

        params.put("CheckValue", generateCheckValue(params));
        System.out.println(params);
        return ResponseEntity.ok(params);
    }

    // Step 2: 接收金流背景通知
    /*
    @PostMapping("/notify")
    public ResponseEntity<String> notifyPayment(@RequestParam Map<String, String> data) {
        System.out.println("✅ 收到藍新金流背景通知");
        System.out.println(data);
        // 你可以在這裡更新訂單狀態到資料庫
        return ResponseEntity.ok("OK");
    }
    */
    
    @PostMapping("/notify")
    public ResponseEntity<String> receiveNotify(@RequestParam Map<String, String> data) {
        try {
            // Step 1：取得 TradeInfo
            String tradeInfo = data.get("TradeInfo");
            System.out.println(tradeInfo);
            if (tradeInfo == null) return ResponseEntity.badRequest().body("Missing TradeInfo");

            // Step 2：解密 TradeInfo
            String decryptedJson;
            try {
                decryptedJson = decryptAES(tradeInfo); // 嘗試解密（正式）
            } catch (Exception e) {
                // 若解密失敗，就直接 base64 decode（測試用）
                byte[] decoded = Base64.getDecoder().decode(tradeInfo);
                decryptedJson = new String(decoded, StandardCharsets.UTF_8);
            }
            System.out.println("✅ 解密後內容：" + decryptedJson);

            // Step 3：轉成 Map 或物件
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> infoMap = mapper.readValue(decryptedJson, Map.class);

            // Step 4：處理訂單，例如記錄付款狀態
            String orderNo = (String) infoMap.get("MerchantOrderNo");
            String status = (String) infoMap.get("Status");
            System.out.println("✅ 訂單：" + orderNo + " 付款狀態：" + status);

            // TODO：你可以根據付款成功與否更新資料庫

            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }

    // 解密方法（AES/CBC/PKCS5Padding）
    private String decryptAES(String tradeInfo) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(HASH_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec iv = new IvParameterSpec(HASH_IV.getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decoded = Base64.getDecoder().decode(tradeInfo);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
    
    private String generateCheckValue(Map<String, String> params) {
        Map<String, String> checkValueParams = new TreeMap<>();
        checkValueParams.put("Amt", params.get("Amt"));
        checkValueParams.put("MerchantID", params.get("MerchantID"));
        checkValueParams.put("MerchantOrderNo", params.get("MerchantOrderNo"));
        checkValueParams.put("TimeStamp", params.get("TimeStamp"));
        checkValueParams.put("Version", params.get("Version"));

        String raw = checkValueParams.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));

        String toHash = "HashKey=" + HASH_KEY + "&" + raw + "&HashIV=" + HASH_IV;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 error", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    @GetMapping("/thank")
    public String thank() {
    	return "thank";
    }

	
}
