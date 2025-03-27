package com.entity;

import java.io.Serializable;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "donationRecord")
public class DonaVO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "donationRecordId", updatable = false)
	private Integer donationRecordId;
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
	@Column(name = "mailMtd",columnDefinition = "TINYINT(1)")
	private Boolean mailMtd;
	private String salutation;
	private String idNum;
	private String guiNum;
	@Column(name = "anonymous",columnDefinition = "TINYINT(1)")
	private Boolean anonymous;
	@Column(name = "donationType",columnDefinition = "TINYINT(1)")
	private Boolean donationType;
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
	
	public Boolean getMailMtd() {
		return mailMtd;
	}
	public void setMailMtd(Boolean mailMtd) {
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
	public Boolean getAnonymous() {
		return anonymous;
	}
	public void setAnonymous(Boolean anonymous) {
		this.anonymous = anonymous;
	}
	public Boolean getDonationType() {
		return donationType;
	}
	public void setDonationType(Boolean donationType) {
		this.donationType = donationType;
	}
	@Override
	public String toString() {
		return "donationRecordId=" + donationRecordId +"\n"
				+ ", createdTime=" + createdTime +"\n"
				+ ", identityData="+ identityData +"\n"
				+ ", donationIncome=" + donationIncome +"\n"
				+ ", email=" + email +"\n"
				+ ", phone=" + phone+"\n"
				+ ", bthDate=" + bthDate +"\n"
				+ ", gender=" + gender +"\n"
				+ ", county=" + county +"\n"
				+ ", district=" + district+"\n"
				+ ", postalCode=" + postalCode+"\n"
				+ ", address=" + address +"\n"
				+ ", mailMtd=" + mailMtd +"\n"
				+ ", salutation="+ salutation +"\n"
				+ ", idNum=" + idNum +"\n"
				+ ", guiNum=" + guiNum +"\n"
				+ ", anonymous=" + anonymous+"\n"
				+ ", donationType=" + donationType;
	}
	
	
	
	
}
