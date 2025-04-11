package com.entity;


//==================新 VO ======================

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "paydetail")
public class PayDetailVO implements Serializable {

	@Id
	@Column(name = "payDetailId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer payDetailId;

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "memberId", nullable = false)
	@JsonIgnore
	private MemberVO member;

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payoutId", nullable = false)
	@JsonIgnore
	private PayRecordVO payRecordVO;
	
	@Column(name = "pointsExpensed", nullable = false)
	private Integer pointsExpensed  = 0;  // 預設值為 0。即使系統出錯沒發到點數，pointsExpensed 也不會是 NULL，而是 0，確保數據正確運行
	
	@Column(name = "createdTime",nullable = false, updatable = false, insertable = false)
	@CreationTimestamp
	private Timestamp createdTime;

	
	//=======getter/setter=======
	
	 // payDetailId getter 和 setter
    public Integer getPayDetailId() {
        return payDetailId;
    }

    public void setPayDetailId(Integer payDetailId) {
        this.payDetailId = payDetailId;
    }

    // memberVO getter 和 setter
	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
	}

    // PayRecordVO 的 getter 和 setter
    public PayRecordVO getPayRecordVO() {
    	return payRecordVO;
    }
    
    public void setPayRecordVO(PayRecordVO payRecordVO) {
    	this.payRecordVO = payRecordVO;
    }

    // pointsExpensed getter 和 setter
    public Integer getPointsExpensed() {
        return pointsExpensed;
    }

    public void setPointsExpensed(Integer pointsExpensed) {
        this.pointsExpensed = pointsExpensed;
    }

    // createdTime getter 和 setter
    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }
    
    public String toString() {
    	return "PayDetail [payDetailId="+ payDetailId + "payRecordId=" + payRecordVO.getPayoutId()+",memberId="+member.getMemberId()+",pointsExpensed="+pointsExpensed+",createdTime="+createdTime+"]";
    }
}





//==================舊 VO ======================
//
//import java.sql.Timestamp;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//@Entity
//@Table(name = "PayDetail")
//public class PayDetailVO implements java.io.Serializable {
//	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "payDetailId ", updatable = false)
//    private Integer payDetailId;
//	
//	@ManyToOne
//    @JoinColumn(name = "payoutId")  
//	@JsonIgnore// Jackson的忽略標籤，應用於SpringBoot環境。
//    private PayRecordVO payRecord;
//	
//	@ManyToOne
//    @JoinColumn(name = "memberId")  
//	@JsonIgnore// Jackson的忽略標籤，應用於SpringBoot環境。
//    private MemberVO member;
//    
//    private Integer pointsExpensed;
//    private Timestamp createdTime;
//
//
//    public Integer getPayDetailId() {
//        return payDetailId;
//    }
//
//    public void setPayDetailId(Integer payDetailId) {
//        this.payDetailId = payDetailId;
//    }
//
//	public MemberVO getMember() {
//		return member;
//	}
//
//	public void setMember(MemberVO member) {
//		this.member = member;
//	}
//
//
//    public Integer getPointsExpensed() {
//        return pointsExpensed;
//    }
//
//    public void setPointsExpensed(Integer pointsExpensed) {
//        this.pointsExpensed = pointsExpensed;
//    }
//
//    public Timestamp getCreatedTime() {
//        return createdTime;
//    }
//
//    public void setCreatedTime(Timestamp createdTime) {
//        this.createdTime = createdTime;
//    }
//
//	public PayRecordVO getPayRecord() {
//		return payRecord;
//	}
//
//	public void setPayRecord(PayRecordVO payRecord) {
//		this.payRecord = payRecord;
//	}
//
//	@Override
//	public String toString() {
//		return "payDetailId=" + payDetailId+"\n" 
//				+ "payRecord=" + payRecord.getPayoutId() +"\n" 
//				+ "memberId=" + member.getMemberId()+"\n" 
//				+ "pointsExpensed=" + pointsExpensed +"\n"  
//				+ "createdTime=" + createdTime+"\n";
//	}
//    
//    
//}
