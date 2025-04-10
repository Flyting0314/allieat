package com.backstage.backstagrepository;



import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.entity.MemberVO;
import com.entity.StoreVO;


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

	Optional<MemberVO> findByVerificationMail(String verificationMail);

	Optional<MemberVO> findByAccount(String account);

	List<MemberVO> findByReviewed(int reviewed);

	Optional<MemberVO> findByEmail(String email);

	List<MemberVO> findByReviewedIn(List<Integer> reviewedList);

	MemberVO findByIdNum(String idNum);

	 List<MemberVO> findByAccStat(Integer accStat); //依照會員帳號啟用狀態查詢會員

}

