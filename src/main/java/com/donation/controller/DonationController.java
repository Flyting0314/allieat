package com.donation.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.donation.model.DonationService;
import com.donation.model.PaymentRequest;
import com.donation.util.EcpayCheckMacValueGenerator;
import com.entity.DonationVO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/donation")
public class DonationController {

    @Autowired
    DonationService donationSvc;

    private final String MERCHANT_ID = "2000132"; // 綠界測試帳號
    private final String HASH_KEY = "5294y06JbISpM5x9";
    private final String HASH_IV = "v77hoKGq4kWxNNIS";

    // 綠界金流付款 API
    @PostMapping("/ecpay-donate")
	public ResponseEntity<String> ecpayDonate(@RequestBody PaymentRequest request) {
		// 自動產生不重複訂單編號
    	String orderNo = "ORD" + System.currentTimeMillis(); // 最多 20 字
    	if (orderNo.length() > 20) {
    	    orderNo = orderNo.substring(0, 20);  // 強制限制長度
    	}

		Map<String, String> params = new LinkedHashMap<>();
		params.put("MerchantID", MERCHANT_ID);
		params.put("MerchantTradeNo", orderNo); // 用後端產生的
		params.put("MerchantTradeDate", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		params.put("PaymentType", "aio");
		params.put("TotalAmount", String.valueOf(request.getAmount()));
		params.put("TradeDesc", "Allieat 捐款平台");
		params.put("ItemName", request.getItemDesc());
		params.put("ReturnURL", "http://localhost:8080/donation/ecpay-return");
		params.put("ChoosePayment", "Credit");
		params.put("EncryptType", "1");

		String checkMacValue = EcpayCheckMacValueGenerator.generate(params, HASH_KEY, HASH_IV);
		params.put("CheckMacValue", checkMacValue);

		// 可選：儲存訂單到資料庫
		// DonationVO donationVO = new DonationVO();
		// donationVO.setOrderNo(orderNo);
		// donationVO.setAmount(request.getAmount());
		// donationVO.setDesc(request.getItemDesc());
		// donationSvc.addDonation(donationVO);

		// 自動產生表單
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body onload='document.forms[0].submit()'>");
		sb.append("<form method='post' action='https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5'>");
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append("<input type='hidden' name='" + entry.getKey() + "' value='" + entry.getValue() + "'/>");
		}
		sb.append("</form></body></html>");

		return ResponseEntity.ok(sb.toString());
	}

    @GetMapping("addDonation")
    public String addDonation(ModelMap model) {
        DonationVO donationVO = new DonationVO();
        model.addAttribute("donationVO", donationVO);
        return "back-end/donation/addDonation";
    }

    @GetMapping("/list")
    public String getAllDonation(Model model) {
        List<DonationVO> donationList = donationSvc.getAllDonation();
        model.addAttribute("donationList", donationList);
        return "back-end/donation/listAllDonation";
    }

    @PostMapping("getOne_For_Update")
    public String getOne_For_Update(@RequestParam("rankId") String rankId, ModelMap model) {
        DonationVO donationVO = donationSvc.getOneDonation(Integer.valueOf(rankId));
        model.addAttribute("donationVO", donationVO);
        return "back-end/donation/update_donation_input";
    }

    @PostMapping("update")
    public String update(@Valid DonationVO donationVO, BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            return "back-end/donation/update_donation_input";
        }
        donationSvc.updateDonation(donationVO);
        model.addAttribute("success", "- (修改成功)");
        donationVO = donationSvc.getOneDonation(Integer.valueOf(donationVO.getRankId()));
        model.addAttribute("donationVO", donationVO);
        return "back-end/donation/listOneDonation";
    }

    @ModelAttribute("donationListData")
    protected List<DonationVO> referenceListData() {
        return donationSvc.getAllDonation();
    }

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

    @PostMapping("/ecpay-return")
public ResponseEntity<String> handleEcpayReturn(@RequestParam Map<String, String> data) {
    System.out.println("綠界背景通知：" + data);

    String receivedMac = data.get("CheckMacValue");

    // 驗證 CheckMacValue 是否正確（移除原本的 CheckMacValue）
    Map<String, String> dataWithoutMac = new LinkedHashMap<>(data);
    dataWithoutMac.remove("CheckMacValue");

    String generatedMac = EcpayCheckMacValueGenerator.generate(dataWithoutMac, HASH_KEY, HASH_IV);

    if (!receivedMac.equals(generatedMac)) {
        System.out.println("CheckMacValue 錯誤");
        return ResponseEntity.badRequest().body("CheckMacValue 錯誤");
    }

    // 驗證通過，更新訂單狀態
    String orderNo = data.get("MerchantTradeNo");
    String rtnCode = data.get("RtnCode"); // 1 = 成功

    if ("1".equals(rtnCode)) {
        System.out.println("訂單：" + orderNo + " 付款成功");
        // TODO: 根據 orderNo 更新付款狀態到資料庫
    }

    return ResponseEntity.ok("1|OK"); // 綠界固定回傳格式
}

    @GetMapping("/thank")
    public String thank() {
        return "thank";
    }
}
