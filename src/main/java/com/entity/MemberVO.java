package com.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import com.dona.model.DonaReq.StepTwo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
@Entity
@Table(name = "member")
public class MemberVO implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "memberId ", updatable = false)
    private Integer memberId;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@OrderBy("memberId asc")
	private Set<PayDetailVO> payDetail;
	
	@ManyToOne
    @JoinColumn(name = "organizationId")  
	@JsonIgnore// Jackson的忽略標籤，應用於SpringBoot環境。
    private OrganizationVO organization;
	@NotBlank(message = "姓名不得為空")
	@Pattern(regexp = "^[\u4e00-\u9fa5a-zA-Z0-9_]{2,10}$", message = "姓名格式無效，需為 2~10 字的中英文及數字")
    private String name;
	@NotBlank(message = "身份證字號不得為空")
    private String idNum;
	@NotBlank(message = "戶籍地址不得為空")
    private String permAddr;
	@NotBlank(message = "通訊地址不得為空")
    private String address;
    private Timestamp regTime;
    private String  kycImage; 
    @Email(message = "請輸入正確的電子郵件格式")
	@NotBlank(message = "信箱不得為空")
    private String email;
    @NotBlank(message = "電話不得為空")
    @Pattern(regexp = "^(09[0-9]{8})$", message = "電話格式無效，須為 09 開頭的 10 位數")
    private String phone;
    @NotBlank(message = "帳號不得為空")
    @Size(min = 6, message = "帳號長度至少需為 6 位")
    private String account;
    @Size(min = 6, message = "密碼長度至少需為 6 位")
    @NotBlank(message = "帳號不得為空")
    private String password;

    private Integer pointsBalance= 0;
    private Integer unclaimedMealCount= 1;
    private Integer accStat= 0;
    private Integer reviewed= 3; 

    private String verificationMail;

 
    public Integer getMemberId() {
        return memberId;
    }
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }



    public OrganizationVO getOrganization() {
		return organization;
	}
	public void setOrganization(OrganizationVO organization) {
		this.organization = organization;
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

    public Integer getReviewed() {
        return reviewed;
    }
    public void setReviewed(Integer reviewed) {
        this.reviewed = reviewed;
    }
	public Set<PayDetailVO> getPayDetail() {
		return payDetail;
	}
	public void setPayDetail(Set<PayDetailVO> payDetail) {
		this.payDetail = payDetail;
	}
	@Override
	public String toString() {
		return "memberId=" + memberId +"\n" 
				+ "payDetail=" + payDetail.hashCode() +"\n" //避免遞迴，使用hashcode替代
				+ "organizationId=" + organization.getOrganizationId()+"\n" 
				+ "name=" + name +"\n" 
				+ "idNum=" + idNum +"\n" 
				+ "permAddr=" + permAddr+"\n" 
				+ "address=" + address+"\n" 
				+ "regTime=" + regTime +"\n" 
				+ "kycImage=" + kycImage +"\n" 
				+ "email=" + email +"\n" 
				+ "phone=" + phone+"\n" 
				+ "account=" + account +"\n" 
				+ "password=" + password +"\n" 
				+ "pointsBalance=" + pointsBalance+"\n" 
				+ "unclaimedMealCount=" + unclaimedMealCount +"\n" 
				+ "accStat=" + accStat +"\n" 
				+ "reviewed=" + reviewed;
	}
	public String getVerificationMail() {
		return verificationMail;
	}
	public void setVerificationMail(String verificationMail) {
		this.verificationMail = verificationMail;
	}

    
}