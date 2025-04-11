package com.backstage.backstageservice;


import com.backstage.backstagedto.ChangePasswordDTO;
import com.backstage.backstagedto.NewAdminDTO;
import org.springframework.http.ResponseEntity;

public interface BackStageNewAdminService {
    ResponseEntity<NewAdminDTO> newAdmin(NewAdminDTO newAdminDTO);
    ResponseEntity<ChangePasswordDTO> changePassword(ChangePasswordDTO changePasswordDTO);
}
