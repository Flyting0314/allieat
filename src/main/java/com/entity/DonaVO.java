
package com.entity;

import java.io.Serializable;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "donationRecord")
public class DonaVO implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer donationRecordId;
	
	@Column(insertable = false, updatable = false)
	private Timestamp createdTime;

	private String identityData;

	private Integer donationIncome;

	private String email;

	private String phone;
	
	private String bthDate;

	private Integer gender;

	private String county;

	private String district;

	private Integer postalCode;

	private String address;

	private Integer mailMtd;

	private String salutation;

	private String idNum;

	private String guiNum;

	private Integer anonymous;
	
	private String merchantTradeNo;

	private Integer donationType;
	
	@NotNull(message = "付款狀態不可為空")
	@Min(value = 0, message = "付款狀態最小為 0（未付款）")
	@Max(value = 1, message = "付款狀態最大為 1（已付款）")
	private Integer paidStatus = 0; // 預設值為 0

	public DonaVO() {
	}

	public Integer getDonationRecordId() {
		return donationRecordId;
	}

	public void setDonationRecordId(Integer donationRecordId) {
		this.donationRecordId = donationRecordId;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public String getIdentityData() {
		return identityData;
	}

	public void setIdentityData(String identityData) {
		this.identityData = identityData;
	}

	public Integer getDonationIncome() {
		return donationIncome;
	}

	public void setDonationIncome(Integer donationIncome) {
		this.donationIncome = donationIncome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBthDate() {
		return bthDate;
	}

	public void setBthDate(String bthDate) {
		this.bthDate = bthDate;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getMailMtd() {
		return mailMtd;
	}

	public void setMailMtd(Integer mailMtd) {
		this.mailMtd = mailMtd;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getIdNum() {
		return idNum;
	}

	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}

	public String getGuiNum() {
		return guiNum;
	}

	public void setGuiNum(String guiNum) {
		this.guiNum = guiNum;
	}

	public Integer getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(Integer anonymous) {
		this.anonymous = anonymous;
	}
	
	public String getMerchantTradeNo() {
	    return merchantTradeNo;
	}
	
	public void setMerchantTradeNo(String merchantTradeNo) {
	    this.merchantTradeNo = merchantTradeNo;
	}

	public Integer getDonationType() {
		return donationType;
	}

	public void setDonationType(Integer donationType) {
		this.donationType = donationType;
	}
	
	public Integer getPaidStatus() {
	    return paidStatus;
	}
	public void setPaidStatus(Integer paidStatus) {
	    this.paidStatus = paidStatus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	

}
