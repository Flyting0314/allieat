package com.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "store")
public class StoreVO implements Serializable {

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

	private String name;
	private String managerName;
	private String email;
	private String password;
	private String phoneNum;
	private String guiNum;
	private String businessRegNum;
	private Timestamp regTime;
	private Integer points;

	@Column(name = "accStat", columnDefinition = "TINYINT(1)")
	private Boolean accStat;

	@Column(name = "opStat", columnDefinition = "TINYINT(1)")
	private Boolean opStat;

	private String opTime;
	private String pickTime;
	private String lastOrder;
	private String closeTime;
	private String address;
	private String county;
	private String district;
	private Integer postalCode;
	private Integer starNum;
	private Integer visitorsNum;

	@Column(name = "reviewed", columnDefinition = "TINYINT(1)")
	private Boolean reviewed;

	private String mapApi;

	// ---------- Getter / Setter ----------
	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public Set<PhotoVO> getStoreToPhoto() {
		return storeToPhoto;
	}

	public void setStoreToPhoto(Set<PhotoVO> storeToPhoto) {
		this.storeToPhoto = storeToPhoto;
	}

	public Set<FoodTypeVO> getStoreToFoodType() {
		return storeToFoodType;
	}

	public void setStoreToFoodType(Set<FoodTypeVO> storeToFoodType) {
		this.storeToFoodType = storeToFoodType;
	}

	public Set<FoodVO> getStoreToFood() {
		return storeToFood;
	}

	public void setStoreToFood(Set<FoodVO> storeToFood) {
		this.storeToFood = storeToFood;
	}

	public Set<FoodVO> getStoreToOrderFood() {
		return storeToOrderFood;
	}

	public void setStoreToOrderFood(Set<FoodVO> storeToOrderFood) {
		this.storeToOrderFood = storeToOrderFood;
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

	public Timestamp getRegTime() {
		return regTime;
	}

	public void setRegTime(Timestamp regTime) {
		this.regTime = regTime;
	}

	public Integer getPoints() {
		return points;
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

	public Boolean getReviewed() {
		return reviewed;
	}

	public void setReviewed(Boolean reviewed) {
		this.reviewed = reviewed;
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
		return "storeId=" + storeId + "\n"
			 + "name=" + name + "\n"
			 + "managerName=" + managerName + "\n"
			 + "email=" + email + "\n"
			 + "password=" + password + "\n"
			 + "phoneNum=" + phoneNum + "\n"
			 + "guiNum=" + guiNum + "\n"
			 + "businessRegNum=" + businessRegNum + "\n"
			 + "regTime=" + regTime + "\n"
			 + "points=" + points + "\n"
			 + "accStat=" + accStat + "\n"
			 + "opStat=" + opStat + "\n"
			 + "opTime=" + opTime + "\n"
			 + "pickTime=" + pickTime + "\n"
			 + "lastOrder=" + lastOrder + "\n"
			 + "closeTime=" + closeTime + "\n"
			 + "address=" + address + "\n"
			 + "county=" + county + "\n"
			 + "district=" + district + "\n"
			 + "postalCode=" + postalCode + "\n"
			 + "starNum=" + starNum + "\n"
			 + "visitorsNum=" + visitorsNum + "\n"
			 + "reviewed=" + reviewed + "\n"
			 + "mapApi=" + mapApi;
	}
}
