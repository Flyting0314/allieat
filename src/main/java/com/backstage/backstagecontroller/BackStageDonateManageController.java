package com.backstage.backstagecontroller;

import com.backstage.backstagedto.DonateInitResponse;
import com.backstage.backstagedto.DonationDetailsDTO;
import com.backstage.backstageservice.BackStageDonateManageService;
import com.backstage.backstageservice.BackStageDonateManageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/backStage")
public class BackStageDonateManageController {
    @Autowired
    private BackStageDonateManageService backStageDonateManageService;

    @GetMapping("/donaManage")
    public  ResponseEntity<DonateInitResponse> getInitData() {
    	try {
        return ResponseEntity.ok(backStageDonateManageService.getInitData());
    	}catch (Exception e) {
          //配合前端錯誤處理，回傳錯誤訊息。
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(new DonateInitResponse("服務異常請稍後再試"));
      }
    }

    @GetMapping("/donaRecord")
    public  ResponseEntity<DonationDetailsDTO> getDonateRecord(@RequestParam("id") Integer id) {
    	try {
        return ResponseEntity.ok(backStageDonateManageService.getDonateRecord(id));
    	}catch(Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    				.body(new DonationDetailsDTO("查無資料，請重新確認"));
    	}
    }

}
