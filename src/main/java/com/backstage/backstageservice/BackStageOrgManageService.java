package com.backstage.backstageservice;

import com.backstage.backstagedto.UpdateOrgDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface BackStageOrgManageService {
    Map<String, Object> getInitData();
    Map<String, Object> getUpdateInitData(Integer id);
    Map<String, Object> updateOrgData(UpdateOrgDTO updateData);
    Map<String, Object> newOrgData(UpdateOrgDTO inputData);
}
