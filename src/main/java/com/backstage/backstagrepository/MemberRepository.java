package com.backstage.backstagrepository;



import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.entity.MemberVO;


@Repository
public interface MemberRepository extends JpaRepository<MemberVO, Integer> {
	@Transactional
	@Modifying
	@Query(value = "delete from member where memberId =?1", nativeQuery = true)
	void deleteByMemberId(int memberId);

	//● (自訂)條件查詢
	@Query(value = "from MemberVO where memberId=?1 and name like?2 and regTime=?3 order by memberId")
	List<MemberVO> findByOthers(int memberId , String name , Timestamp regTime);

	MemberVO findByAccountAndPassword(String account, String password);
	
	boolean existsByAccount(String account); //  檢查帳號是否已存在
}
