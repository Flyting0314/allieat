package com.frontservice;

import com.entity.DonaVO;

import jakarta.transaction.Transactional;

import com.backstage.backstagrepository.DonorRepository;
import com.ecpay.payment.integration.AllInOne;
import com.ecpay.payment.integration.domain.AioCheckOutOneTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


@Service("donaGoldFlowService")
public class DonaGoldFlowService {

    @Autowired
    private DonorRepository donorRepository;

    // 建立捐款紀錄
    public DonaVO insertDona(DonaVO vo) {
    	vo.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        return donorRepository.save(vo);
    }

    // 更新捐款紀錄
    public DonaVO updateDona(Integer donationRecordId, DonaVO vo) {
        return donorRepository.findById(donationRecordId).map(existing -> {
            vo.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            return donorRepository.save(vo);
        }).orElse(null);
    }
    
    //  驗證金流背景通知並更新付款狀態（一次性與定期定額共用）
    public ResponseEntity<String> verifyAndUpdatePaymentStatus(Map<String, String> data) {
        AllInOne all = new AllInOne(""); // 讀取 SDK 金鑰設定
        Hashtable<String, String> ecpayParams = new Hashtable<>();
        ecpayParams.putAll(data);
    	
     // 1. 驗證 CheckMacValue
        boolean isValid = all.compareCheckMacValue(ecpayParams);

        if (!isValid) {
            return ResponseEntity.badRequest().body("CheckMacValue 錯誤");
        }
        
     // 2. 判斷付款成功則更新資料庫
        String orderNo = data.get("MerchantTradeNo"); // 訂單編號
        String rtnCode = data.get("RtnCode"); // 綠界付款狀態

        if ("1".equals(rtnCode)) {
            updatePaidStatusByMerchantTradeNo(orderNo); // 用 merchantTradeNo更新付款狀態
            return ResponseEntity.ok("1|OK");
        }

        return ResponseEntity.ok("0|FAIL");
    }

    @Transactional
    private void updatePaidStatusByMerchantTradeNo(String orderNo) {
        donorRepository.updatePaidStatusByMerchantTradeNo(orderNo);	
	}

	//  查詢所有捐款紀錄
    public List<DonaVO> getAllDonas() {
        return donorRepository.findAll();
    }

    //  查詢單筆捐款紀錄
    public DonaVO findById(Integer donationRecordId) {
        return donorRepository.findById(donationRecordId).orElse(null);
    }
    
//	@Transactional
//	public void updatePaidStatusByMerchantTradeNo(String merchantTradeNo) {
//	    DonaVO dona = donorRepository.findByMerchantTradeNo(merchantTradeNo);
//	    if (dona != null) {
//	    	donorRepository.updatePaidStatusByMerchantTradeNo(merchantTradeNo);
//	        dona.setPaidStatus(1); // 設為已付款
//	        dona.setUpdatedTime(new Timestamp(System.currentTimeMillis())); // 若有付款時間欄位
//	        donorRepository.save(dona);
//	    }
//	}
    
    
    
    //將存入資料庫的物件拿出來用於建立ECPay訂單 回傳付款頁面
    public String toEcpay(DonaVO vo) {

		AllInOne allInOneOrder = new AllInOne("");
		
		String dateStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		String tradeNo = "D" + vo.getDonationRecordId()+"T"+ System.currentTimeMillis();
		System.out.println(tradeNo);
		String form;
		// 單筆
		AioCheckOutOneTime aioChenkOutAll = new AioCheckOutOneTime();
		aioChenkOutAll.setMerchantID("3002607");
		aioChenkOutAll.setMerchantTradeNo(tradeNo);
		aioChenkOutAll.setMerchantTradeDate(dateStr);
		aioChenkOutAll.setTradeDesc("Allieat 捐款平台");
		aioChenkOutAll.setItemName("待用餐愛心捐款");
		aioChenkOutAll.setTotalAmount(String.valueOf(vo.getDonationIncome()));
		aioChenkOutAll.setReturnURL("https://8cf9-1-164-225-29.ngrok-free.app/donation/ecpayReturn");
		aioChenkOutAll.setClientBackURL("http://localhost:8080/dona/donaAddOne");
		aioChenkOutAll.setNeedExtraPaidInfo("N");

		form = allInOneOrder.aioCheckOut(aioChenkOutAll, null);
//		vo.setMerchantTradeNo(tradeNo);
//		donaService.updateDona(vo.getDonationRecordId(), vo);
		System.out.println(form);
		return form;
	}
//    [0]  age=1  [1]name=
//    [0] age  [1] 1
//    [0] name 
    public void ecpayReturn(String ecpayReq) {
    	String [] strarry = ecpayReq.split("&");
    	Hashtable<String, String> ECPayReqTable = new Hashtable<>();
    	for(String a : strarry) {
    		String [] y = a.split("=");
    		String key = y[0];
    		String val = y.length == 1 ? "" : y[1];
    		ECPayReqTable.put(key, val);
    	}
    	String DonationRecordId = ECPayReqTable.get("MerchantTradeNo").replaceAll(".*D(\\d+)T.*", "$1");
    	
    	System.out.println(DonationRecordId);
    	AllInOne aio = new AllInOne("");
    	if (aio.compareCheckMacValue(ECPayReqTable)) {
    		System.out.println("比對通過");
    		if ("1".equals(ECPayReqTable.get("RtnCode"))) {
    			donorRepository.updatePaidStatusByMerchantTradeNo(DonationRecordId);
    			System.out.println("已付款");
    			
    		}else{
    			
    			System.out.println("付款失敗");
    		}
    	}else {
    		System.out.println("比對失敗");
    	}
    }
} 
