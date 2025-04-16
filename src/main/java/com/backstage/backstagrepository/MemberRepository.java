package com.backstage.backstagrepository;



import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
	
	//撈照片，用organizationId找
	@Query(value = "SELECT kycImage FROM member WHERE organizationId = :organizationId AND reviewed = 3", nativeQuery = true)
	String findPendingPicByOrgId(@Param("organizationId") Integer organizationId);
	
	
	 //計算指定審核狀態和帳戶狀態的會員數量
	 //reviewed 審核狀態 (1=已審核) accStat 帳戶狀態 (1=啟用)的會員數量 
	@Query("SELECT COUNT(m) FROM MemberVO m WHERE m.reviewed = :reviewed AND m.accStat = :accStat")
	Integer countByReviewedAndAccStat(@Param("reviewed") Integer reviewed, @Param("accStat") Integer accStat);

}

