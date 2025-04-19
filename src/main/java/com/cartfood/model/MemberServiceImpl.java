package com.cartfood.model;

import com.backstage.backstagrepository.MemberRepository;
import com.entity.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl {

    @Autowired
    private MemberRepository memberRepo;

    /**
     * 根據 memberId 取得會員資料
     * @param memberId 會員編號
     * @return MemberVO 物件，如果找不到則回傳 null
     */
    public MemberVO getMemberById(Integer memberId) {
        Optional<MemberVO> memberOpt = memberRepo.findById(memberId);
        return memberOpt.orElse(null);
    }

    /**
     * 根據帳號和密碼進行登入（可選）
     */
    public MemberVO login(String account, String password) {
        return memberRepo.findByAccountAndPassword(account, password);
    }

	public void updateMember(MemberVO member) {
		// TODO Auto-generated method stub
		
	}

    // 其他會員相關功能可於此擴充
}
