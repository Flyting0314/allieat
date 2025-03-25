package com.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "member")
public class MemberVO implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberId;
    private Integer organizationId;
    private String name;
    private String idNum;
    private String permAddr;
    private String address;
    private Timestamp regTime;
    private String  kycImage; 
    private String email;
    private String phone;
    private String account;
    private String password;
    private Integer pointsBalance;
    private Integer unclaimedMealCount;
    private Integer accStat;
    private Boolean reviewed; 

 
    public Integer getMemberId() {
        return memberId;
    }
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }
    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getIdNum() {
        return idNum;
    }
    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getPermAddr() {
        return permAddr;
    }
    public void setPermAddr(String permAddr) {
        this.permAddr = permAddr;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getRegTime() {
        return regTime;
    }
    public void setRegTime(Timestamp regTime) {
        this.regTime = regTime;
    }

    public String  getKycImage() {
        return kycImage;
    }
    public void setKycImage(String  kycImage) {
        this.kycImage = kycImage;
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

    public Integer getPointsBalance() {
        return pointsBalance;
    }
    public void setPointsBalance(Integer pointsBalance) {
        this.pointsBalance = pointsBalance;
    }

    public Integer getUnclaimedMealCount() {
        return unclaimedMealCount;
    }
    public void setUnclaimedMealCount(Integer unclaimedMealCount) {
        this.unclaimedMealCount = unclaimedMealCount;
    }

    public Integer getAccStat() {
        return accStat;
    }
    public void setAccStat(Integer accStat) {
        this.accStat = accStat;
    }

    public Boolean getReviewed() {
        return reviewed;
    }
    public void setReviewed(Boolean reviewed) {
        this.reviewed = reviewed;
    }
}