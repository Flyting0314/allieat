package com.entity;

//pay_record 點數發放表格


//==================新的VO：欄位新增、欄位名稱更正（記得更新點數相關資料庫指令，不然會發生欄位名稱不ㄧ致的狀況）=====================
import java.io.Serializable;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "payrecord")
public class PayRecordVO implements Serializable {

	@Id
	@Column(name = "payoutId", updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer payoutId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payRuleId")
	@JsonIgnore //可能會在前端或 API 回應中找不到 payRuleVO
	private PayRuleVO payRuleVO;

	@Column(name = "payoutPoints")
	private Integer payoutPoints;

	@Column(name = "totalPoints")
	private Integer totalPoints;

	@Column(name = "scheduleTime", updatable = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp scheduleTime;

	@Column(name = "payoutDate", updatable = true) // 系統到了發放時間會回填日期，因此 updatable = true
	private Timestamp payoutDate;

	@Enumerated(EnumType.ORDINAL) // 存成數字 (0,1,2)
	@Column(name = "status", columnDefinition = "TINYINT DEFAULT 0") //預設為0
	private Status status;

	@OneToMany(mappedBy = "payRecordVO", cascade = CascadeType.ALL)
	@OrderBy("payDetailId asc") // 按 payDetailId 排序
	private Set<PayDetailVO> payDetails;

	
	// =======getter/setter=======

	public Integer getPayoutId() {
		return payoutId;
	}

	public void setPayoutId(Integer payoutId) {
		this.payoutId = payoutId;
	}

	public PayRuleVO getPayRuleVO() {
		return payRuleVO;
	}

	public void setPayRuleVO(PayRuleVO payRuleVO) {
		this.payRuleVO = payRuleVO;
	}

	public Integer getPayoutPoints() {
		return payoutPoints;
	}

	public void setPayoutPoints(Integer payoutPoints) {
		this.payoutPoints = payoutPoints;
	}

	public Integer getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
	}

	public Timestamp getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Timestamp scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public Timestamp getPayoutDate() {
		return payoutDate;
	}

	public void setPayoutDate(Timestamp payoutDate) {
		this.payoutDate = payoutDate;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Set<PayDetailVO> getPayDetails() {
		return payDetails;
	}

	public void setPayDetails(Set<PayDetailVO> payDetails) {
		this.payDetails = payDetails;
	}

	@Override
	public String toString() {
		return "PayRecordVO [" + "payoutId=" + payoutId + ", payRuleVO="
				+ (payRuleVO != null ? payRuleVO.hashCode() : "null") 
				+ ", payoutPoints=" + payoutPoints
				+ ", totalPoints=" + totalPoints + ", scheduleTime=" + scheduleTime + ", payoutDate=" + payoutDate
				+ ", status=" + status + ", payDetails="
				+ (payDetails != null? payDetails.stream().map(PayDetailVO::getPayDetailId).collect(Collectors.toList())
						: "null")
				+ ']';
	}
}














//=========================舊的VO==================


//import java.sql.Timestamp;
//import java.sql.Date;
//import java.time.LocalDateTime;
//import java.util.Set;
//
//
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.OrderBy;
//import jakarta.persistence.Table;




//@Entity
//@Table(name = "payRecord")
//public class PayRecordVO implements java.io.Serializable {
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "payoutId ", updatable = false)
//	private Integer payoutId;
//	
//	@OneToMany(mappedBy = "payRecord", cascade = CascadeType.ALL)
//	@OrderBy("payoutId asc")
//	private Set<PayDetailVO> payDetail;
//	
//	private Integer totalPoint;
//	private Timestamp payoutDate; 
//	@Column(name = "status",columnDefinition = "TINYINT(1)")
//	private Integer status;
//	
//	
//	public Integer getPayoutId() {
//		return payoutId;
//	}
//
//	public void setPayoutId(Integer payoutId) {
//		this.payoutId = payoutId;
//	}
//
//	public Set<PayDetailVO> getDetail() {
//		return payDetail;
//	}
//
//	public void setDetail(Set<PayDetailVO> detail) {
//		payDetail = detail;
//	}
//
//	public Integer getTotalPoint() {
//		return totalPoint;
//	}
//	
//	public void setTotalPoint(Integer totalPoint) {
//		this.totalPoint = totalPoint;
//	}
//	
//	public Timestamp getPayoutDate() {
//		return payoutDate;
//	}
//	
//	public void setPayoutDate(Timestamp payoutDate) {
//		this.payoutDate = payoutDate;
//	}
//	
//	public Integer getStatus() {
//		return status;
//	}
//	public void setStatus(Integer status) {
//		this.status = status;
//	}
//	
//	@Override
//	public String toString() {
//		return "payoutId=" + payoutId +"\n" 
//				+ "Detail=" + payDetail.hashCode() +"\n" //避免遞迴使用hashcode代替
//				+ "totalPoint=" + totalPoint+"\n" 
//				+ "payoutDate=" + payoutDate +"\n" 
//				+ "status=" + status;
//	}
//
//}
