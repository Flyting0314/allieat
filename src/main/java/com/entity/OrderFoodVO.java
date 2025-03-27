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
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId",referencedColumnName = "storeId")  // 外鍵
    private StoreVO store;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId",referencedColumnName = "memberId")  // 外鍵
    private MemberVO member;
	@Column(name = "rate", columnDefinition = "TINYINT(1)")
    private Boolean  rate;
	@Column(name = "comment")
    private String comment;
	@Column(name = "serveStat",columnDefinition = "TINYINT(1)")
    private Boolean  serveStat;
	 @Column(name = "pickStat", columnDefinition = "TINYINT(1)")
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



	public StoreVO getStore() {
		return store;
	}


	public void setStore(StoreVO store) {
		this.store = store;
	}


	public MemberVO getMember() {
		return member;
	}


	public void setMember(MemberVO member) {
		this.member = member;
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
				+ ", storeId=" + store.getStoreId() +"\n"
				+ ", memberId=" + member.getMemberId()+"\n"
				+ ", rate=" + rate +"\n"
				+ ", comment=" + comment +"\n"
				+ ", serveStat="+ serveStat +"\n"
				+ ", pickStat=" + pickStat +"\n"
				+ ", pickTime=" + pickTime +"\n"
				+ ", createdTime=" + createdTime;
				
	}


	
	
	

}
