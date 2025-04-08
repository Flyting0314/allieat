package com.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient; // ← 要加這個 import

@Entity
@Table(name = "administrator")
public class AdminVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adminId", updatable = false)
	private Integer adminId;
	@Column(name = "account")
	private String account;
	@Column(name = "password")
	private String password;
	@Column(name = "createdTime")
	private Timestamp createdTime;
	
	public int getAdminId() {
		return adminId;
	}
	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Timestamp getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
	@Override
	public String toString() {
		return "adminId=" + adminId +"\n" 
				+ "account=" + account +"\n"
				+ "password=" + password +"\n"
				+ "createdTime="+ createdTime;
	}
	
	@Transient
	private Integer point = 0;

	@Transient
	private Integer quantity = 1;

	@Transient
	private Integer subtotal;

	public Integer getPoint() {
	    return point;
	}

	public void setPoint(Integer point) {
	    this.point = point;
	}

	public Integer getQuantity() {
	    return quantity;
	}

	public void setQuantity(Integer quantity) {
	    this.quantity = quantity;
	}

	public Integer getSubtotal() {
	    return subtotal != null ? subtotal : (point != null && quantity != null ? point * quantity : 0);
	}

	public void setSubtotal(Integer subtotal) {
	    this.subtotal = subtotal;
	}
	
}