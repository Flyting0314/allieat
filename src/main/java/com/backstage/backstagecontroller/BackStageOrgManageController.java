package com.backstage.backstagecontroller;

import com.backstage.backstageservice.BackStageOrgManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/backStage")
public class BackStageOrgManageController {
    @Autowired
    private BackStageOrgManageService backStageOrgManageService;
    @GetMapping("/orgManage")
    public ResponseEntity<Map<String, Object>> getInitData() {
        return  backStageOrgManageService.getInitData();
    }


}
