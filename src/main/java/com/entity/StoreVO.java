package com.entity;

import java.sql.Date;
import java.sql.Timestamp;
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

import java.io.*;
@Entity
@Table(name = "store")
public class StoreVO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "storeId", updatable = false)
	private Integer storeId;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	@OrderBy("storeId asc")
	private Set<PhotoVO> storeToPhoto;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	@OrderBy("storeId asc")
	private Set<FoodTypeVO> storeToFoodType;

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	@OrderBy("storeId asc")
	private Set<FoodVO> storeToFood;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	@OrderBy("storeId asc")
	private Set<FoodVO> storeToOrderFood;
	
	private String name;
	private String managerName;
	private String email;
	private String password;
	private String phoneNum;
	private String guiNum;
	private String businessRegNum;
	private Timestamp regTime;
	private Integer points;
	@Column(name = "accStat",columnDefinition = "TINYINT(1)")
	private Boolean accStat;
	@Column(name = "opStat",columnDefinition = "TINYINT(1)")
	private Boolean opStat;
	private String opTime;
	private String  pickTime;
	private String lastOrder;
	private String closeTime; 
	private String address;
	private String county;
	private String district;
	private Integer postalCode;
	private Integer starNum;
	private Integer visitorsNum;
	@Column(name = "reviewed",columnDefinition = "TINYINT(1)")
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

	public String getOpTime() {
		return opTime;
	}
	public void setOpTime(String opTime) {
		this.opTime = opTime;
	}
	
	public String getPickTime() {
		return pickTime;
	}
	public void setPickTime(String pickTime) {
		this.pickTime = pickTime;
	}
	public String getLastOrder() {
		return lastOrder;
	}
	public void setLastOrder(String lastOrder) {
		this.lastOrder = lastOrder;
	}
	public String getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(String closeTime) {
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
	@Override
	public String toString() {
		return "storeId=" + storeId +"\n"
				+ "name=" + name  +"\n"
				+ "managerName=" + managerName  +"\n"
				+ "email=" + email +"\n"
				+ "password=" + password  +"\n"
				+ "phoneNum=" + phoneNum  +"\n"
				+ "guiNum=" + guiNum  +"\n"
				+ "businessRegNum="+ businessRegNum  +"\n"
				+ "regTime=" + regTime  +"\n"
				+ "points=" + points  +"\n"
				+ "accStat=" + accStat  +"\n"
				+ "opStat="+ opStat  +"\n"
				+ "opTime=" + opTime  +"\n"
				+ "pickTime=" + pickTime  +"\n"
				+ "lastOrder=" + lastOrder  +"\n"
				+ "closeTime="+ closeTime  +"\n"
				+ "address=" + address  +"\n"
				+ "county=" + county  +"\n"
				+ "district=" + district  +"\n"
				+ "postalCode="+ postalCode  +"\n"
				+ "starNum=" + starNum  +"\n"
				+ "visitorsNum=" + visitorsNum +"\n"
				+ "reviewed=" + reviewed;
	}
	
	
	

}
