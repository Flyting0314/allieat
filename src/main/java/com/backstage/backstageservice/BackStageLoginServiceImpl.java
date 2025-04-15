package com.backstage.backstageservice;

import com.backstage.backstagedto.AdminDTO;
import com.backstage.backstagrepository.BackStageLoginDao;

import com.jwtUtil.JwtUtil;
import com.entity.AdminVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackStageLoginServiceImpl implements BackStageLoginService {
	@Autowired
	private BackStageLoginDao backStageLogin;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private PasswordEncoder encoder;

	@Override
	public Map<String, Object> findByAccount(AdminDTO admin) {
		// 資料Mapping
		AdminVO adminVO = new AdminVO();
		BeanUtils.copyProperties(admin, adminVO);
		List<AdminVO> adminList = backStageLogin.findByAccount(adminVO);
		if (!adminList.isEmpty()) {
			AdminVO foundAdmin = adminList.get(0); // 取出結果
			if (foundAdmin.getPassword() != null && encoder.matches(admin.getPassword(), foundAdmin.getPassword())) {
				// 產生Token
				String token = jwtUtil.generateToken(foundAdmin.getAccount());
				// 回傳值設定
				Map<String, Object> result = createResult("login ok");
				result.put("token", token);
				return result; // 密碼正確
			} else {
				return createResult("login failed password is incorrect"); // 密碼錯誤
			}
		} else {
			return createResult("account does not exist"); 
		}
	}

	// 用於回傳狀態創建
	private Map<String, Object> createResult(String message) {
		Map<String, Object> result = new HashMap<>();
		result.put("loginState", message);
		return result;
	}
}
