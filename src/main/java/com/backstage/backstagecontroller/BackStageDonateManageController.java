package com.backstage.backstagecontroller;

import com.backstage.backstagedto.DonateInitResponse;
import com.backstage.backstagedto.DonationDetailsDTO;
import com.backstage.backstageservice.BackStageDonateManageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/backStage")
public class BackStageDonateManageController {
    @Autowired
    private BackStageDonateManageServiceImpl backStageDonateManageServiceImpl;

    @GetMapping("/donaManage")
    public  ResponseEntity<DonateInitResponse> getInitData() {
        return backStageDonateManageServiceImpl.getInitData();
    }

    @GetMapping("/donaRecord")
    public  ResponseEntity<DonationDetailsDTO> getDonateRecord(@RequestParam("id") Integer id) {
        return backStageDonateManageServiceImpl.getDonateRecord(id);
    }

}
