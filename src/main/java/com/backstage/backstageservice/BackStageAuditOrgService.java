package com.backstage.backstageservice;

import org.springframework.http.ResponseEntity;

public interface BackStageAuditOrgService {
	ResponseEntity<byte[]> getImgByOrgId(Integer orgId);
}
