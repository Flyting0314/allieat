package com.entity;

import java.sql.Timestamp;
import jakarta.persistence.*;

@Entity
@Table(name = "orderlist")
public class OrderFoodVO {
	


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orderId", updatable = false)
    private Integer orderId;
//	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId",referencedColumnName = "storeId")  // 外鍵
    private StoreVO storeVO;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId",referencedColumnName = "memberId")  // 外鍵
    private MemberVO memberVO;
	@Column(name = "rate")
    private Boolean  rate;
	@Column(name = "comment")
    private String comment;
	@Column(name = "serveStat")
    private Boolean  serveStat;
	@Column(name = "pickStat")
    private Boolean pickStat;
	@Column(name = "pickTime")
    private Timestamp pickTime;
	@Column(name = "createdTime",updatable = false)
    private Timestamp createdTime;


	// Constructor
    public OrderFoodVO() {
    }

     
    // Getter & Setter 方法
	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public StoreVO getStoreVO() {
		return storeVO;
	}

	public void setStoreVO(StoreVO storeVO) {
		this.storeVO = storeVO;
	}

	public MemberVO getMemberVO() {
		return memberVO;
	}

	public void setMemberVO(MemberVO memberVO) {
		this.memberVO = memberVO;
	}

	public Boolean getRate() {
		return rate;
	}

	public void setRate(Boolean rate) {
		this.rate = rate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean  getServeStat() {
		return serveStat;
	}

	public void setServeStat(Boolean  serveStat) {
		this.serveStat = serveStat;
	}

	public Boolean getPickStat() {
		return pickStat;
	}

	public void setPickStat(Boolean pickStat) {
		this.pickStat = pickStat; 	
	}

	public Timestamp getPickTime() {
		return pickTime;
	}

	public void setPickTime(Timestamp pickTime) {
		this.pickTime = pickTime;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}


	@Override
	public String toString() {
		return "orderId=" + orderId +"\n"
				+ ", storeId=" + storeVO +"\n"
				+ ", memberId=" + memberVO+"\n"
				+ ", rate=" + rate +"\n"
				+ ", comment=" + comment +"\n"
				+ ", serveStat="+ serveStat +"\n"
				+ ", pickStat=" + pickStat +"\n"
				+ ", pickTime=" + pickTime +"\n"
				+ ", createdTime=" + createdTime;
				
	}


	
	
	

}
