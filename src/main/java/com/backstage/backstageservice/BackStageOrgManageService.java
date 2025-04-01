package com.backstage.backstageservice;

import com.entity.AdminVO;
import com.entity.OrganizationVO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface BackStageOrgManageService {
    ResponseEntity<Map<String, Object>> getInitData();

}
