package com.backstage.backstageservice;

import com.backstage.backstagedto.UpdateOrgDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface BackStageOrgManageService {
    ResponseEntity<Map<String, Object>> getInitData();
    ResponseEntity<Map<String, Object>> getUpdateInitData(Integer id);
    ResponseEntity<Map<String, Object>> updateOrgData(UpdateOrgDTO updateData);
    ResponseEntity<Map<String, Object>> newOrgData(UpdateOrgDTO inputData);
}
