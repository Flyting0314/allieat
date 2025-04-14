package com.backstage.backstageservice;



import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.PayDetailRepository;
import com.backstage.backstagedto.PayDetailWithMemberDTO;
import com.entity.MemberVO;
import com.entity.OrganizationVO;
import com.entity.PayDetailVO;
import com.entity.PayRecordVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import java.util.stream.Collectors;
import java.sql.Timestamp;



import jakarta.transaction.Transactional; //如果專案使用 Spring Boot 3.x

@Service
public class BackStagePayDetailService {

	@Autowired
	private PayDetailRepository payDetailRepository;

	@Autowired
	private BackStagePayRecordService payRecordService;

	@Autowired
	private BackStageMemberManageService memberService;

	@Autowired
	private JdbcTemplate jdbcTemplate; // 使用 JdbcTemplate 來執行原生 SQL 查詢

	@Autowired
	private EntityManager entityManager;
	
	
	
	public PayDetailVO addPayDetail(PayDetailVO payDetailVO) {
		return payDetailRepository.save(payDetailVO);
	}

	public PayDetailVO updatePayDetail(PayDetailVO payDetailVO) {
		return payDetailRepository.save(payDetailVO);
	}

	public void deletePayDetail(Integer payDetailId) {
		if (payDetailRepository.existsById(payDetailId)) {
			payDetailRepository.deleteById(payDetailId);
		}
	}

	// 單筆查詢
	public PayDetailVO getPayDetailById(Integer PayDetailId) {
		return payDetailRepository.findById(PayDetailId).orElse(null);
	}

	// 多筆查詢 - 根據多個 PayDetailId 查詢
	public List<PayDetailVO> getPayDetailsByIds(List<Integer> payDetailIds) {
		return payDetailRepository.findAllById(payDetailIds);
	}

	// 查詢全部 - 取得所有 PayDetailVO
	public List<PayDetailVO> getAllPayDetails() {
		return payDetailRepository.findAll();
	}

	
	// ======== 根據 PayRecord ID 查詢所有對應的 PayDetail 並轉換為 DTO ========
	public List<PayDetailWithMemberDTO> getPayDetailsByPayRecord(Integer payoutId) {
		// 查詢對應的 PayDetail
		List<PayDetailVO> payDetails = payDetailRepository.findByPayRecordVO_PayoutId(payoutId);

		// 將 PayDetailVO 轉換為 PayDetailWithMemberDTO
		List<PayDetailWithMemberDTO> payDetailDTOs = new ArrayList<>();
		for (PayDetailVO payDetail : payDetails) {
			PayDetailWithMemberDTO dto = convertToDTO(payDetail);
			payDetailDTOs.add(dto);
		}
		return payDetailDTOs;
	}

	
	// ======== 將 PayDetailVO 轉換為 PayDetailWithMemberDTO ========
	private PayDetailWithMemberDTO convertToDTO(PayDetailVO payDetail) {
	    PayDetailWithMemberDTO dto = new PayDetailWithMemberDTO();

	    // 設定 PayDetail 的欄位
	    dto.setPayDetailId(payDetail.getPayDetailId());
	    dto.setPayoutId(payDetail.getPayRecordVO().getPayoutId()); // PayoutId 來自 PayRecordVO
	    dto.setPointsExpensed(payDetail.getPointsExpensed());
	    dto.setPayDate(payDetail.getCreatedTime());
	    
	    // 設定應發放點數
	    dto.setPayoutPoints(payDetail.getPayRecordVO().getPayoutPoints());

	    // 設定會員相關資料
	    MemberVO member = payDetail.getMember();
	    if (member != null) {
	        dto.setMemberId(member.getMemberId());
	        dto.setName(member.getName());
	        dto.setOrganizationName(member.getOrganization().getName());// 讓頁面上出現在冊單位
	        dto.setIdNum(member.getIdNum());
	        dto.setPhone(member.getPhone());
	    }

	    return dto;
	}

	
	
	// ======== 查詢啟用的會員數量（剔除未啟用會員） --> 可在 member service 調整 ========
	public long getEnabledMemberCount() {
		String sql = "SELECT COUNT(*) FROM member WHERE accStat = 1";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}
	
	

	// ========== 人工補發放點數（針對單一會員）==========
	@Transactional
	public PayDetailWithMemberDTO manuallyUpdatePoints(Integer payDetailId, Integer points) {
		// 檢查點數是否為空或無效
		if (points == null || points <= 0) {
			throw new IllegalArgumentException("點數不得為空值，且必須大於零");
		}

		// 取得 PayDetail，如果找不到則拋出異常
		PayDetailVO payDetail = payDetailRepository.findById(payDetailId)
				.orElseThrow(() -> new RuntimeException("找不到對應的點數發放明細"));

		// 取得關聯的 PayRecord
		PayRecordVO payRecord = payDetail.getPayRecordVO();

		// 計算更新後的已發放點數
		int updatedPointsExpensed = payDetail.getPointsExpensed() + points;

		// 檢查是否超過該 PayRecord 的最大可發放點數
		if (updatedPointsExpensed > payRecord.getPayoutPoints()) {
			throw new RuntimeException("人工補發的點數不能超過設定的發放點數上限");
		}

		// 更新 PayDetail 的已發放點數
		payDetail.setPointsExpensed(updatedPointsExpensed);

		// 取得會員並更新會員點數餘額
		MemberVO member = payDetail.getMember();
		int updatedPointsBalance = member.getPointsBalance() + points;
		member.setPointsBalance(updatedPointsBalance);

		// 儲存更新
		payDetailRepository.save(payDetail);
		memberService.updateMember(member);

		// 呼叫 PayRecordService 的 updateTotalPoints 方法
		payRecordService.updateTotalPoints(payRecord.getPayoutId(), points);

		// 將更新後的 PayDetail 轉換為 DTO 並返回
		return new PayDetailWithMemberDTO(payDetail);
	}

	
	
	
	//===============動態查詢（支持複合查詢）=======================
	
	// 使用 CriteriaBuilder 直接查詢並生成 DTO
	// 避免二次轉換的開銷
	
		public List<PayDetailWithMemberDTO> searchPayDetailsDirectDTO(
				Integer memberId,
				String name,
				String pointsStatus,
				Integer payoutId) {
			
			// 獲取當前 EntityManager 的 CriteriaBuilder 實例
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			
			// 創建一個 Tuple 類型的 CriteriaQuery
			CriteriaQuery<Tuple> query = cb.createTupleQuery();
			
			// 從 PayDetailVO 實體開始
			Root<PayDetailVO> root = query.from(PayDetailVO.class);
			
			// 關聯 PayRecordVO 和 MemberVO
			Join<PayDetailVO, PayRecordVO> payRecordJoin = root.join("payRecordVO", JoinType.INNER);
			Join<PayDetailVO, MemberVO> memberJoin = root.join("member", JoinType.INNER);
			Join<MemberVO, OrganizationVO> orgJoin = memberJoin.join("organization", JoinType.INNER);
			
			// 選擇需要的所有欄位
		    query.multiselect(
		            root.get("payDetailId").alias("payDetailId"),
		            root.get("createdTime").alias("payDate"),
		            root.get("pointsExpensed").alias("pointsExpensed"),
		            memberJoin.get("memberId").alias("memberId"),
		            memberJoin.get("name").alias("name"),
		            memberJoin.get("idNum").alias("idNum"),
		            memberJoin.get("phone").alias("phone"),
		            memberJoin.get("pointsBalance").alias("pointsBalance"),
		            orgJoin.get("name").alias("organizationName"),
		            payRecordJoin.get("payoutId").alias("payoutId"),
		            payRecordJoin.get("payoutPoints").alias("payoutPoints") // 添加應發放點數
		        );
			
			// 準備條件列表
			List<Predicate> predicates = new ArrayList<>();
			
			// 如果指定了 payoutId
			if (payoutId != null) {
				predicates.add(cb.equal(payRecordJoin.get("payoutId"), payoutId));
			}
			
			// 如果指定了 memberId
			if (memberId != null) {
				predicates.add(cb.equal(memberJoin.get("memberId"), memberId));
			}
			
			// 如果指定了會員姓名
			if (name != null && !name.trim().isEmpty()) {
				//cb.like 方法： JPA CriteriaBuilder 的方法，用於創建 SQL 中的 LIKE 操作
				predicates.add(cb.like(memberJoin.get("name"), "%" + name.trim() + "%"));
			}
			
			// 如果指定了點數狀態（成功或異常）
			if (pointsStatus != null && !pointsStatus.trim().isEmpty()) {
				if ("1".equals(pointsStatus)) { // 已取得點數
					predicates.add(cb.greaterThan(root.get("pointsExpensed"), 0));
				} else if ("0".equals(pointsStatus)) { // 未取得點數
					predicates.add(cb.equal(root.get("pointsExpensed"), 0));
				}
			}
			
			// 添加所有條件到 query
			query.where(predicates.toArray(new Predicate[0]));
			
			// 排序 - 按 payDetailId 降序排列
			query.orderBy(cb.desc(root.get("payDetailId")));
			
			// 執行查詢
			List<Tuple> results = entityManager.createQuery(query).getResultList();
			
			// 將結果轉換為 DTO 對象列表
			   List<PayDetailWithMemberDTO> dtoList = results.stream().map(tuple -> {
			        PayDetailWithMemberDTO dto = new PayDetailWithMemberDTO();
			        dto.setPayDetailId(tuple.get("payDetailId", Integer.class));
			        dto.setPayDate(tuple.get("payDate", Timestamp.class));
			        dto.setPointsExpensed(tuple.get("pointsExpensed", Integer.class));
			        dto.setMemberId(tuple.get("memberId", Integer.class));
			        dto.setName(tuple.get("name", String.class));
			        dto.setIdNum(tuple.get("idNum", String.class));
			        dto.setPhone(tuple.get("phone", String.class));
			        dto.setPointsBalance(tuple.get("pointsBalance", Integer.class));
			        dto.setOrganizationName(tuple.get("organizationName", String.class));
			        dto.setPayoutId(tuple.get("payoutId", Integer.class));
			        dto.setPayoutPoints(tuple.get("payoutPoints", Integer.class)); // 設置應發放點數
			        return dto;
			    }).collect(Collectors.toList());
			
			return dtoList;
		}
}
