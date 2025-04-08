package com.backstage.backstageservice;

import com.backstage.backstagedto.DonateInitResponse;
import org.springframework.http.ResponseEntity;

public interface BackStageDonateManageService {
    ResponseEntity<DonateInitResponse> getInitData();
}
