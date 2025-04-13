package com.backstage.backstageservice;

import com.backstage.backstagedto.UpdateOrgDTO;
import com.backstage.backstagrepository.DonationRepository;
import com.backstage.backstagrepository.OrganizationRepository;
import com.entity.OrganizationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BackStageOrgManageServiceImpl implements BackStageOrgManageService {
    @Autowired
    private OrganizationRepository data;

    @Override
    public ResponseEntity<Map<String, Object>> getInitData() {
        Map<String, Object> result = new HashMap<>();
        try{
            List<OrganizationVO> orgInitDataList = data.findAll();
            result.put("orgInitDataList", orgInitDataList);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e){
            result.put("error","資料查詢異常");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    public ResponseEntity<Map<String, Object>> getUpdateInitData(Integer id){
        Map<String, Object> result = new HashMap<>();
        if (id == null) {
            result.put("success", false);
            result.put("message", "缺少必要的 OrganizationId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        Optional<OrganizationVO> selectedData = data.findById(id);
        if (selectedData.isEmpty()) {
            result.put("success", false);
            result.put("message", "查無此資料，無法更新");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }else{
            result=convertOrgToMap(selectedData.get());
            return ResponseEntity.ok(result);
        }

    }

    public ResponseEntity<Map<String, Object>> updateOrgData(UpdateOrgDTO updateData) {
        Map<String, Object> result = new HashMap<>();
        Integer id = updateData.getOrganizationId();
       //ID驗證
        if (id == null) {
            result.put("success", false);
            result.put("message", "缺少必要的 OrganizationId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        // 確認資料是否存在
        Optional<OrganizationVO> selectedData = data.findById(id);
        if (selectedData.isEmpty()) {
            result.put("success", false);
            result.put("message", "查無此資料，無法更新");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        //查詢資料
        OrganizationVO savedData = selectedData.get();
        //DTO 資料mapping到VO，但排除id欄位避免變成新增。
        BeanUtils.copyProperties(updateData, savedData, "organizationId","createdTime");
        //儲存資料，需要是被查詢到的那筆作更新。
        data.save(savedData);
        //製作回傳格式
        result=convertOrgToMap(savedData);
        result.put("success", true);

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Map<String, Object>> newOrgData(UpdateOrgDTO inputData) {
        Map<String, Object> result = new HashMap<>();
        OrganizationVO newData = new OrganizationVO();
        //DTO 資料mapping到VO，忽略id因為是自增。
        BeanUtils.copyProperties(inputData, newData, "organizationId","createdTime");
        //重名檢測
        Optional<OrganizationVO> existing = data.findByName(inputData.getName());
        if (existing.isPresent()) {
            result.put("success", false);
            result.put("error", "資料重複請確認");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result); // 更合適的 status
        }
        newData.setCreatedTime(Timestamp.valueOf(LocalDateTime.now()));
        newData = data.save(newData);
        //製作回傳格式
        result=convertOrgToMap(newData);
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> convertOrgToMap(OrganizationVO vo) {
        Map<String, Object> map = new HashMap<>();
        map.put("organizationId", vo.getOrganizationId());
        map.put("name", vo.getName());
        map.put("type", vo.getType());
        map.put("status", vo.getStatus());
        map.put("createdTime", vo.getCreatedTime());
        return map;
    }
    
    
    
    
    
    
    
    
    
}




