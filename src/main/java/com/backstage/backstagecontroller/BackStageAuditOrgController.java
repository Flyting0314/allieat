package com.backstage.backstagecontroller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backstage.backstageservice.BackStageAuditOrgService;


@RestController
@RequestMapping("/backStage")
public class BackStageAuditOrgController {

    @Autowired
    private BackStageAuditOrgService backStageAuditOrgService;
    
    @GetMapping("/orgAudit/image")
    public ResponseEntity<byte[]> getAuditImg(@RequestParam("orgId") Integer orgId){
    	System.err.println(orgId);
		return backStageAuditOrgService.getImgByOrgId(orgId);
    }
    
}
