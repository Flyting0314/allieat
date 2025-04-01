package com.backstage.backstageservice;

import com.backstage.backstagrepository.DonationRepository;
import com.backstage.backstagrepository.OrganizationRepository;
import com.entity.OrganizationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class BackStageOrgManageServiceImpl implements BackStageOrgManageService {
    @Autowired
    private OrganizationRepository initData;

    @Override
    public ResponseEntity<Map<String, Object>> getInitData() {
        Map<String, Object> result = new HashMap<>();
        try{
            List<OrganizationVO> orgInitDataList = initData.findAll();
            result.put("orgInitDataList", orgInitDataList);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e){
            result.put("error","資料查詢異常");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
