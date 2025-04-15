package com.backstage.backstagecontroller;

import com.backstage.backstagedto.UpdateOrgDTO;
import com.backstage.backstageservice.BackStageOrgManageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import static com.backstage.constant.OrgManageConstant.*;

@RestController
@RequestMapping("/backStage")
public class BackStageOrgManageController {
    @Autowired
    private BackStageOrgManageService backStageOrgManageService;
    @Autowired
    private ObjectMapper objectMapper;
    
    @GetMapping("/orgManage")
    public ResponseEntity<Map<String, Object>> getInitData() {
    	Map<String, Object> result = new HashMap<>();
    	try{
    		result = backStageOrgManageService.getInitData();
    		return ResponseEntity.ok(result);
    	}catch(Exception e) {
    		result.put("error", ORG_ERROR);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    	}
    }
    
    @GetMapping("/orgManage/updateInit")
    public ResponseEntity<Map<String, Object>>updateInit(@RequestParam("id") Integer id) {
    	Map<String, Object> result = backStageOrgManageService.getUpdateInitData(id);
    	if(ORG_ID_MISSING.equals(result.get("message"))) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    	}
        
    	if(ORG_NOT_FOUND .equals(result.get("message"))) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    	}
    	return ResponseEntity.ok(result);
    }

    @PutMapping("/orgManage/updateData")
    public ResponseEntity<Map<String, Object>> updateOrgData(@RequestBody Map<String, Object> data) {
    	Map<String, Object> result = backStageOrgManageService
    			.updateOrgData(objectMapper.convertValue(data, UpdateOrgDTO.class));
    	
    	if(ORG_ID_MISSING.equals(result.get("message"))) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    	}
    	if(ORG_NOT_FOUND .equals(result.get("message"))) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    	}
    	return ResponseEntity.ok(result);
    }
    
    @PutMapping("/orgManage/newOrgData")
    public ResponseEntity<Map<String, Object>> newOrgData(@RequestBody Map<String, Object> data) {
    	Map<String, Object> result = backStageOrgManageService
    			.newOrgData(objectMapper.convertValue(data, UpdateOrgDTO.class));
    	if(ORG_DUPLICATE.equals(result.get("error"))) {
    		return ResponseEntity.status(HttpStatus.CONFLICT).body(result); 
    	}
      return ResponseEntity.ok(result);
    }

}
