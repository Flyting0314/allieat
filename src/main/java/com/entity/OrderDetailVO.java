package com.entity;


import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "orderdetail")
public class OrderDetailVO {

	@EmbeddedId 
	private OrderDetailId id;

	@Column(name = "createdTime")
	private Timestamp createdTime ;
	@Column(name = "amount")
	private Integer amount ;
	@Column(name = "pointsCost")
	private Integer pointsCost ;
	@Column(name = "note")
	private String note ;
	public OrderDetailId getId() {
		return id;
	}
	public void setId(OrderDetailId id) {
		this.id = id;
	}
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Integer getPointsCost() {
		return pointsCost;
	}
	public void setPointsCost(Integer pointsCost) {
		this.pointsCost = pointsCost;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	@Override
	public String toString() {
		return "The result for this search is \n"
				+"id=" + id +"\n"
				+ "createdTime=" + createdTime +"\n"
				+ "amount=" + amount +"\n"
				+ "pointsCost="+ pointsCost +"\n"
				+ "note=" + note +"\n"
				;
	}
	

}
