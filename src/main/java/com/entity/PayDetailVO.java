package com.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "PayDetail")
public class PayDetailVO implements java.io.Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payDetailId ", updatable = false)
    private Integer payDetailId;
	
	@ManyToOne
    @JoinColumn(name = "payoutId")  
	@JsonIgnore// Jackson的忽略標籤，應用於SpringBoot環境。
    private PayRecordVO payRecord;
	
	@ManyToOne
    @JoinColumn(name = "memberId")  
	@JsonIgnore// Jackson的忽略標籤，應用於SpringBoot環境。
    private MemberVO member;
    
    private Integer pointsExpensed;
    private Timestamp createdTime;


    public Integer getPayDetailId() {
        return payDetailId;
    }

    public void setPayDetailId(Integer payDetailId) {
        this.payDetailId = payDetailId;
    }

	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
	}


    public Integer getPointsExpensed() {
        return pointsExpensed;
    }

    public void setPointsExpensed(Integer pointsExpensed) {
        this.pointsExpensed = pointsExpensed;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

	public PayRecordVO getPayRecord() {
		return payRecord;
	}

	public void setPayRecord(PayRecordVO payRecord) {
		this.payRecord = payRecord;
	}

	@Override
	public String toString() {
		return "payDetailId=" + payDetailId+"\n" 
				+ "payRecord=" + payRecord.getPayoutId() +"\n" 
				+ "memberId=" + member.getMemberId()+"\n" 
				+ "pointsExpensed=" + pointsExpensed +"\n"  
				+ "createdTime=" + createdTime+"\n";
	}
    
    
}
