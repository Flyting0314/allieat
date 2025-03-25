package com.entity;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.*;
@Entity
@Table(name = "store")
public class StoreVO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer storeId;
	private String name;
	private String managerName;
	private String email;
	private String password;
	private String phoneNum;
	private String guiNum;
	private String businessRegNum;
	private Timestamp regTime;
	private Integer points;
	private Boolean accStat;
	private Boolean opStat;
	private Timestamp opTime;
	private Timestamp  pickTime;
	private Timestamp lastOrder;
	private Timestamp closeTime; 
	private String address;
	private String county;
	private String district;
	private Integer postalCode;
	private Integer starNum;
	private Integer visitorsNum;
	private Boolean reviewed; 
	
	public Boolean getReviewed() {
		return reviewed;
	}
	public void setReviewed(Boolean reviewed) {
		this.reviewed = reviewed;
	}
	public Integer getStoreId() {
		return storeId;
	}
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getGuiNum() {
		return guiNum;
	}
	public void setGuiNum(String guiNum) {
		this.guiNum = guiNum;
	}
	public String getBusinessRegNum() {
		return businessRegNum;
	}
	public void setBusinessRegNum(String businessRegNum) {
		this.businessRegNum = businessRegNum;
	}

	public Integer getPoints() {
		return points;
	}
	public Timestamp getRegTime() {
		return regTime;
	}
	public void setRegTime(Timestamp regTime) {
		this.regTime = regTime;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
	public Boolean getAccStat() {
		return accStat;
	}
	public void setAccStat(Boolean accStat) {
		this.accStat = accStat;
	}


	public Boolean getOpStat() {
		return opStat;
	}
	public void setOpStat(Boolean opStat) {
		this.opStat = opStat;
	}
	public Timestamp getOpTime() {
		return opTime;
	}
	public void setOpTime(Timestamp opTime) {
		this.opTime = opTime;
	}
	public Timestamp getPickTime() {
		return pickTime;
	}
	public void setPickTime(Timestamp pickTime) {
		this.pickTime = pickTime;
	}
	public Timestamp getLastOrder() {
		return lastOrder;
	}
	public void setLastOrder(Timestamp lastOrder) {
		this.lastOrder = lastOrder;
	}
	public Timestamp getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(Timestamp closeTime) {
		this.closeTime = closeTime;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public Integer getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(Integer postalCode) {
		this.postalCode = postalCode;
	}
	public Integer getStarNum() {
		return starNum;
	}
	public void setStarNum(Integer starNum) {
		this.starNum = starNum;
	}
	public Integer getVisitorsNum() {
		return visitorsNum;
	}
	public void setVisitorsNum(Integer visitorsNum) {
		this.visitorsNum = visitorsNum;
	}
	
	
	

}
