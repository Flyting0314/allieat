package com.donation.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.frontservice.DonaGoldFlowService;
import com.frontservice.DonaService;

import jakarta.servlet.http.HttpServletRequest;

import com.donation.model.PaymentRequest;
import com.ecpay.payment.integration.AllInOne;
import com.ecpay.payment.integration.domain.AioCheckOutOneTime; // 一次性付款使用正確的類別
import com.ecpay.payment.integration.domain.AioCheckOutPeriod; // 定期定額使用正確的類別
import com.ecpay.payment.integration.domain.InvoiceObj;
import com.entity.DonaVO;

@RestController
@RequestMapping("/donation")
public class DonationController {

	@Autowired
	private DonaGoldFlowService donaGoldFlowService;

	// 建立捐款紀錄並產生綠界付款表單
	@PostMapping("/ecpay-donate")
	public ResponseEntity<String> ecpayDonate(@RequestBody PaymentRequest request) {
		System.out.println("0");
		// 建立捐款資料 → 寫入資料庫
		DonaVO vo = new DonaVO();
		vo.setIdentityData(request.getIdentityData());
		vo.setDonationIncome(request.getAmount());
		vo.setEmail(request.getEmail());
		vo.setPhone(request.getPhone());
		vo.setCounty(request.getCounty());
		vo.setDistrict(request.getDistrict());
		vo.setAddress(request.getAddress());
		vo.setSalutation(request.getSalutation());
		vo.setIdNum(request.getIdNum());
		vo.setGuiNum(request.getGuiNum());
		vo.setDonationType(request.getDonationType());
		vo.setMailMtd(0);
		vo.setPaidStatus(0); // 預設未付款
		System.out.println("1");
		// 寫入資料庫
		DonaVO savedVO = donaGoldFlowService.insertDona(vo);
		System.out.println("2");
		String form = donaGoldFlowService.toEcpay(savedVO);
		System.out.println("3");
		// 產生金流表單 呼叫 SDK 串接金流
		return ResponseEntity.ok(form);
	}

	@PostMapping("ecpayReturn")
	public ResponseEntity<String> ecpayReturn(HttpServletRequest req)throws IOException {
		
		BufferedReader reader = req.getReader();
		StringBuilder reqBody = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			reqBody.append(line);
		}
		reader.close();
		donaGoldFlowService.ecpayReturn(reqBody.toString());
//		ordersService.checkECPayReq(reqBody.toString());

		return ResponseEntity.ok("1|OK");
	}
	// 產生 SDK 表單(單筆或定期定額)
//    public ResponseEntity<String> toEcpay(DonaVO vo, PaymentRequest request) {
//        try {
//            AllInOne allInOneOrder = new AllInOne("");
//            String tradeNo = "DONATE" + vo.getDonationRecordId() + System.currentTimeMillis();
//            String dateStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
//            String form;
//
//            if ("periodic".equalsIgnoreCase(request.getType())) {
//            	// 定期定額
//            	AioCheckOutPeriod aioChenkOutAll = new AioCheckOutPeriod();
//                aioChenkOutAll.setMerchantTradeNo(tradeNo);
//                aioChenkOutAll.setMerchantTradeDate(dateStr);
//                aioChenkOutAll.setTradeDesc(URLEncoder.encode("Allieat 捐款平台", StandardCharsets.UTF_8));
//                aioChenkOutAll.setItemName(URLEncoder.encode(request.getItemDesc(), StandardCharsets.UTF_8));
//                aioChenkOutAll.setPeriodAmount(String.valueOf(request.getAmount()));
//                aioChenkOutAll.setPeriodType("M");  // 月扣
//                aioChenkOutAll.setFrequency("1");  // 每期一次
//                aioChenkOutAll.setExecTimes("99");  // 無限期
//                aioChenkOutAll.setReturnURL("https://8cf9-1-164-225-29.ngrok-free.app/donation/thank");
//                aioChenkOutAll.setPeriodReturnURL("https://8cf9-1-164-225-29.ngrok-free.app/donation/period-notify");
//                aioChenkOutAll.setClientBackURL("http://localhost:8080/dona/donaAddOne");
//                aioChenkOutAll.setNeedExtraPaidInfo("N");
//                
//                form = allInOneOrder.aioCheckOut((Object) aioChenkOutAll, (InvoiceObj) null);
//                
//            } else {
//            	// 單筆
//            	AioCheckOutOneTime aioChenkOutAll = new AioCheckOutOneTime();
//                aioChenkOutAll.setMerchantTradeNo(tradeNo);
//                aioChenkOutAll.setMerchantTradeDate(dateStr);
//                aioChenkOutAll.setTradeDesc(URLEncoder.encode("Allieat 捐款平台", StandardCharsets.UTF_8));
//                aioChenkOutAll.setItemName(URLEncoder.encode(request.getItemDesc(), StandardCharsets.UTF_8));
//                aioChenkOutAll.setTotalAmount(String.valueOf(request.getAmount()));
//                aioChenkOutAll.setReturnURL("https://8cf9-1-164-225-29.ngrok-free.app/donation/ecpay-return");
//                aioChenkOutAll.setClientBackURL("http://localhost:8080/dona/donaAddOne");
//                aioChenkOutAll.setNeedExtraPaidInfo("N");         
//                
//                form = allInOneOrder.aioCheckOut((Object) aioChenkOutAll, (InvoiceObj) null);
//
//            }
//
//            vo.setMerchantTradeNo(tradeNo);
//            donaService.updateDona(vo.getDonationRecordId(), vo);
//            return ResponseEntity.ok(form);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("建立金流表單失敗");
//        }
//    }

	

	// 綠界背景通知（一次性付款）
	@PostMapping("/ecpay-return")
	public ResponseEntity<String> handleEcpayReturn(@RequestParam Map<String, String> data) {
		return donaGoldFlowService.verifyAndUpdatePaymentStatus(data);
	}

	// 綠界背景通知（定期定額）
	@PostMapping("/period-notify")
	public ResponseEntity<String> handleEcpayPeriodNotify(@RequestParam Map<String, String> data) {
		return donaGoldFlowService.verifyAndUpdatePaymentStatus(data);
	}

	// 導回成功頁
	@GetMapping("/thank")
	public String thank() {
		return "thank";
	}
}
