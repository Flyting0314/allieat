package com.backstage.backstageservice;


import com.backstage.backstagedto.ChangePasswordDTO;
import com.backstage.backstagedto.NewAdminDTO;
import org.springframework.http.ResponseEntity;

public interface BackStageAdminService {
    NewAdminDTO newAdmin(NewAdminDTO newAdminDTO);
    ChangePasswordDTO changePassword(ChangePasswordDTO changePasswordDTO);
}
