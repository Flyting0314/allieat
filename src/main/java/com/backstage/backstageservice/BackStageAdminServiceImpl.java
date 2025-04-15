package com.backstage.backstageservice;

import com.backstage.backstagedto.ChangePasswordDTO;
import com.backstage.backstagedto.NewAdminDTO;
import com.backstage.backstagrepository.AdminRepository;
import com.entity.AdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class BackStageAdminServiceImpl implements BackStageAdminService {

	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private PasswordEncoder encoder;

	@Override
	public NewAdminDTO newAdmin(NewAdminDTO newAdminDTO) {
		// 新增管理員流程
		Optional<AdminVO> data = adminRepository.findByAccount(newAdminDTO.getAccount());
		NewAdminDTO respDTO = new NewAdminDTO();
		if (data.isPresent()) {
			respDTO.setStatus("帳號已存在");
			return respDTO;
		}

		AdminVO admin = new AdminVO();
		admin.setAccount(newAdminDTO.getAccount());
		admin.setPassword(encoder.encode(newAdminDTO.getPassword()));
		admin.setCreatedTime(new Timestamp(System.currentTimeMillis()));
		adminRepository.save(admin);
		respDTO.setStatus("success");
		return respDTO;
	}

	// 修改密碼
	@Override
	public ChangePasswordDTO changePassword(ChangePasswordDTO changePasswordDTO) {
		Optional<AdminVO> data = adminRepository.findByAccount(changePasswordDTO.getAccount());
		//避免沒有這個帳號
		if (!data.isPresent()) {
			ChangePasswordDTO respDTO = new ChangePasswordDTO();
			respDTO.setStatus("帳號不存在");
			return respDTO;
		}
		
		boolean passwordCheck = encoder.matches(changePasswordDTO.getOldPassword(), data.get().getPassword());

		if (!passwordCheck) {
			ChangePasswordDTO respDTO = new ChangePasswordDTO();
			respDTO.setStatus("舊密碼錯誤");
			return respDTO;
		}
		AdminVO admin = data.get();
		admin.setPassword(encoder.encode(changePasswordDTO.getNewPassword()));
		adminRepository.save(admin);
		ChangePasswordDTO respDTO = new ChangePasswordDTO();
		respDTO.setStatus("success");
		return respDTO;
	}

}
