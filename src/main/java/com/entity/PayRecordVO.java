package com.entity;

import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Set;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

//pay_record 點數發放表格
@Entity
@Table(name = "payRecord")
public class PayRecordVO implements java.io.Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payoutId ", updatable = false)
	private Integer payoutId;
	
	@OneToMany(mappedBy = "payRecord", cascade = CascadeType.ALL)
	@OrderBy("payoutId asc")
	private Set<PayDetailVO> payDetail;
	
	private Integer totalPoint;
	private Timestamp payoutDate; 
	@Column(name = "status",columnDefinition = "TINYINT(1)")
	private Integer status;
	
	
	public Integer getPayoutId() {
		return payoutId;
	}

	public void setPayoutId(Integer payoutId) {
		this.payoutId = payoutId;
	}

	public Set<PayDetailVO> getDetail() {
		return payDetail;
	}

	public void setDetail(Set<PayDetailVO> detail) {
		payDetail = detail;
	}

	public Integer getTotalPoint() {
		return totalPoint;
	}
	
	public void setTotalPoint(Integer totalPoint) {
		this.totalPoint = totalPoint;
	}
	
	public Timestamp getPayoutDate() {
		return payoutDate;
	}
	
	public void setPayoutDate(Timestamp payoutDate) {
		this.payoutDate = payoutDate;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "payoutId=" + payoutId +"\n" 
				+ "Detail=" + payDetail.hashCode() +"\n" //避免遞迴使用hashcode代替
				+ "totalPoint=" + totalPoint+"\n" 
				+ "payoutDate=" + payoutDate +"\n" 
				+ "status=" + status;
	}

}
