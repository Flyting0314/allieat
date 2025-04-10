package com.backstage.backstageservice;

import com.backstage.backstagedto.ChangePasswordDTO;
import com.backstage.backstagedto.NewAdminDTO;
import com.backstage.backstagrepository.AdminRepository;
import com.entity.AdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;


@Service
public class BackStageNewAdminServiceImpl implements BackStageNewAdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder encoder;

    @Override
    public ResponseEntity<NewAdminDTO> newAdmin(NewAdminDTO newAdminDTO) {
        //新增管理員流程
        try {
            Optional<AdminVO> data = adminRepository.findByAccount(newAdminDTO.getAccount());
            NewAdminDTO respDTO = new NewAdminDTO();
            if (!checkAdmin(data)) {
                AdminVO admin = new AdminVO();
                admin.setAccount(newAdminDTO.getAccount());
                admin.setPassword(encoder.encode(newAdminDTO.getPassword()));
                admin.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                adminRepository.save(admin);
                respDTO.setStatus("success");
            } else {
                respDTO.setStatus("帳號已存在");
            }
            return ResponseEntity.ok(respDTO);
        }catch (Exception e){
            NewAdminDTO respDTO = new NewAdminDTO();
            respDTO.setStatus("fail");
            return ResponseEntity.internalServerError().body(respDTO);
        }
    }
    //修改密碼
    @Override
    public  ResponseEntity<ChangePasswordDTO> changePassword(ChangePasswordDTO changePasswordDTO) {
            try {
                Optional<AdminVO> data = adminRepository.findByAccount(changePasswordDTO.getAccount());
                ChangePasswordDTO respDTO = new ChangePasswordDTO();
                boolean passwordCheck = encoder.matches(changePasswordDTO.getOldPassword(), data.get().getPassword());
                if (checkAdmin(data) && passwordCheck) {
                    AdminVO admin = data.get();
                    admin.setPassword(encoder.encode(changePasswordDTO.getNewPassword()));
                    adminRepository.save(admin);
                    respDTO.setStatus("success");
                } else {
                    respDTO.setStatus("帳號不存在");
                }
                return ResponseEntity.ok(respDTO);
            }catch (Exception e){
                ChangePasswordDTO respDTO = new ChangePasswordDTO();
                respDTO.setStatus("fail");
                return ResponseEntity.internalServerError().body(respDTO);
            }
    }



    //確認帳號存在與否?
    boolean checkAdmin(Optional<AdminVO> data) {
        return data.isPresent();
    }
}


