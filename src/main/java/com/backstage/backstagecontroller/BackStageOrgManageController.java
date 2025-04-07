package com.backstage.backstagecontroller;

import com.backstage.backstagedto.UpdateOrgDTO;
import com.backstage.backstageservice.BackStageOrgManageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/backStage")
public class BackStageOrgManageController {
    @Autowired
    private BackStageOrgManageService backStageOrgManageService;
    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping("/orgManage")
    public ResponseEntity<Map<String, Object>> getInitData() {
        return  backStageOrgManageService.getInitData();
    }
    @GetMapping("/orgManage/updateInit")
    public ResponseEntity<Map<String, Object>>updateInit(@RequestParam("id") Integer id) {
        return backStageOrgManageService.getUpdateInitData(id);
    }

    @PutMapping("/orgManage/updateData")
    public ResponseEntity<Map<String, Object>> updateOrgData(@RequestBody Map<String, Object> data) {
        return backStageOrgManageService.updateOrgData(objectMapper.convertValue(data, UpdateOrgDTO.class));
    }
    @PutMapping("/orgManage/newOrgData")
    public ResponseEntity<Map<String, Object>> newOrgData(@RequestBody Map<String, Object> data) {
        return backStageOrgManageService.newOrgData(objectMapper.convertValue(data, UpdateOrgDTO.class));
    }

}
