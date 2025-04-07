package com.entity;


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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.*;
@Entity
@Table(name = "store")
public class StoreVO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "storeId", updatable = false)
	private Integer storeId;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	@OrderBy("store asc")
	private Set<PhotoVO> storeToPhoto;

	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	@OrderBy("store asc")
	private Set<FoodTypeVO> storeToFoodType;

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	@OrderBy("store asc")
	private Set<FoodVO> storeToFood;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	@OrderBy("store asc")
	private Set<FoodVO> storeToOrderFood;
	
	@NotBlank(message = "姓名不得為空")
	@Pattern(regexp = "^[\u4e00-\u9fa5a-zA-Z0-9_]{2,10}$", message = "姓名格式無效，需為 2~10 字的中英文及數字")
	private String name;
	
	@NotBlank(message = "負責人姓名不得為空")
	@Pattern(regexp = "^[\u4e00-\u9fa5a-zA-Z0-9_]{2,10}$", message = "姓名格式無效，需為 2~10 字的中英文及數字")
	private String managerName;
	
	@Email(message = "請輸入正確的電子郵件格式")
	@NotBlank(message = "信箱不得為空")
	private String email;
	
	@Size(min = 6, message = "密碼長度至少需為 6 位")
	private String password;
	
	@NotBlank(message = "電話不得為空")
    @Pattern(regexp = "^(09[0-9]{8})$", message = "電話格式無效，須為 09 開頭的 10 位數")
	private String phoneNum;
	
	@NotBlank(message = "統一編號不得為空")
	private String guiNum;
	
	@NotBlank(message = "店家食品業者登錄字號不得為空")
	private String businessRegNum;
	@Column(name = "regTime")
	private Timestamp regTime;
	
	private Integer points=0;
	
	@Min(value = 0, message = "未啟用")
    @Max(value = 1, message = "啟用")
	@Column(name = "accStat",columnDefinition = "TINYINT(1)")
	private Integer accStat=0;
	
	@Min(value = 0, message = "未")
    @Max(value = 1, message = "有")
	@Column(name = "opStat",columnDefinition = "TINYINT(1)")
	private Integer opStat=0;
	
	private String opTime;
	private String  pickTime;
	private String lastOrder;
	private String closeTime;
	
	@NotBlank(message = "地址: 請勿空白")
	private String address;
	@NotBlank(message = "縣市: 請勿空白")
	private String county;
	@NotBlank(message = "鄉鎮市區: 請勿空白")
	private String district;
	@NotNull(message = "郵遞區號: 請勿空白")
	@Min(value = 100, message = "郵遞區號必須大於或等於 100")
	@Max(value = 99999, message = "郵遞區號必須小於或等於 99999")
	private Integer postalCode;
	private Integer starNum=0;
	private Integer visitorsNum=0;
	@Column(name = "reviewed",columnDefinition = "TINYINT(1)")
	@Min(value = 0, message = "請填審核狀態填寫數字:0=審核中;1=已通過;2=未通過;3=未審核;")
    @Max(value = 3, message = "請填審核狀態填寫數字:0=審核中;1=已通過;2=未通過;3=未審核;")
	private Integer reviewed=3;
	
	private String mapApi;
	
	private String verificationMail;
	
	public String getVerificationMail() {
		return verificationMail;
	}
	public void setVerificationMail(String verificationMail) {
		this.verificationMail = verificationMail;
	}




	public Integer getReviewed() {
		return reviewed;
	}
	public void setReviewed(Integer reviewed) {
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
	public Integer getAccStat() {
		return accStat;
	}
	public void setAccStat(Integer accStat) {
		this.accStat = accStat;
	}


	public Integer getOpStat() {
		return opStat;
	}
	public void setOpStat(Integer opStat) {
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

	public String getMapApi() {
		return mapApi;
	}

	public void setMapApi(String mapApi) {
		this.mapApi = mapApi;
	}
	// 解析 mapApi 成為 latitude
	public Double getLatitude() {
		if (mapApi != null && mapApi.contains(",")) {
			try {
				return Double.parseDouble(mapApi.split(",")[0].trim());
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	// 解析 mapApi 成為 longitude
	public Double getLongitude() {
		if (mapApi != null && mapApi.contains(",")) {
			try {
				return Double.parseDouble(mapApi.split(",")[1].trim());
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
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
				+ "reviewed=" + reviewed+"\n"
				+ "mapApi=" + mapApi ;
	}
	
	public Set<PhotoVO> getStoreToPhoto() {
	    return storeToPhoto;
	}

	public void setStoreToPhoto(Set<PhotoVO> storeToPhoto) {
	    this.storeToPhoto = storeToPhoto;
	}
	

}
