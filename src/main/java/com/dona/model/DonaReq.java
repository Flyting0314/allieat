package com.dona.model;

import java.sql.Timestamp;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import com.dona.model.ValidMailInfo;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

@ValidMailInfo
public class DonaReq {
	
	


	
	 // ====== 切換用 ======
	
	
	@NotNull(message = "請勾選個資運用告知聲明", groups = StepTwo.class)
	@AssertTrue(message = "請勾選個資運用告知聲明", groups = StepTwo.class)
    private Boolean agreePrivacy = false;
    
    private Boolean companyDonor = false; // true = 公司捐款, false = 個人捐款

 

    
	
	
	 // ====== Step 1 驗證用 ======
    public interface StepOne {}
    
    @NotNull(message = "捐款金額為必填項" ,groups = StepOne.class)
    @Positive(message = "捐款金額必須為正數",groups = StepOne.class)
    private Integer donationIncome;

    @NotNull(message = "單筆捐贈/定期定額: 請勿空白" ,groups = StepOne.class)
    @Min(value = 0, message = "捐贈選項無效")
    @Max(value = 1, message = "捐贈選項無效")
    private Integer donationType= 1;
    
    
    // ====== Step 2 驗證用 ======
    public interface StepTwo {}   


    
    @NotBlank(message = "姓名: 請勿空白",groups = StepTwo.class)
    @Pattern(regexp = "^[\u4e00-\u9fa5a-zA-Z0-9_]{2,10}$", message = "姓名格式無效，需為 2~10 字的中英文及數字",groups = StepTwo.class)
    private String identityData;
    
    @NotBlank(message = "信箱: 請勿空白",groups = StepTwo.class)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "信箱格式無效",groups = StepTwo.class)
    private String email;

    @NotBlank(message = "電話: 請勿空白",groups = StepTwo.class)
    @Pattern(regexp = "^(09[0-9]{8})$", message = "電話格式無效，須為 09 開頭的 10 位數",groups = StepTwo.class)
    private String phone;

    @NotNull(message = "寄發收據: 請勿空白",groups = StepTwo.class)
    @Min(value = 0, message = "寄發收據選項無效")
    @Max(value = 1, message = "寄發收據選項無效")
    private Integer mailMtd= 0;
    
    @NotNull(message = "徵信匿名: 請勿空白",groups = StepTwo.class)
    @Min(value = 0, message = "徵信匿名選項無效")
    @Max(value = 1, message = "徵信匿名選項無效")
    private Integer anonymous= 0;
    
    
    private Integer donationRecordId;

    private Timestamp createdTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String bthDate;

    @Min(value = 0, message = "性別選項無效")
    @Max(value = 2, message = "性別選項無效")
    private Integer gender = 0;

    private String salutation;
    private String idNum;
    private String guiNum;
//  @NotBlank(message = "縣市: 請勿空白",groups = StepTwo.class)
  private String county;

//  @NotBlank(message = "鄉鎮市區: 請勿空白",groups = StepTwo.class)
  private String district;

//  @NotNull(message = "郵遞區號: 請勿空白",groups = StepTwo.class)
  @Min(value = 100, message = "郵遞區號必須大於或等於 100")
  @Max(value = 99999, message = "郵遞區號必須小於或等於 99999")
  private Integer postalCode;

//  @NotBlank(message = "地址: 請勿空白",groups = StepTwo.class)
  private String address;
  

    
	    
	    // UI 狀態
	    private boolean isRadio = false;  // 默認值為 false
	    private boolean isLightbox = false;  // 默認值為 false
	    
	    
	    public DonaReq() {
	        this.identityData = null;
	        this.donationIncome = null;
	        this.email = null;
	        this.phone = null;
	        this.bthDate = null;
	        this.county = null;
	        this.district = null;
	        this.postalCode = null;
	        this.address = null;
	       
	        this.salutation = null;
	        this.idNum = null;
	        this.guiNum = null;
	        

	    }
	    public Boolean getAgreePrivacy() {
	        return agreePrivacy;
	    }

	    public void setAgreePrivacy(Boolean agreePrivacy) {
	        this.agreePrivacy = agreePrivacy;
	    }

	    public Boolean getCompanyDonor() {
	        return companyDonor;
	    }

	    public void setCompanyDonor(Boolean companyDonor) {
	        this.companyDonor = companyDonor;
	    }
	 public boolean getIsRadio() {
	        return isRadio;
	    }

	    public void setIsRadio(boolean isRadio) {
	        this.isRadio = isRadio;
	    }

	    public boolean getIsLightbox() {
	        return isLightbox;
	    }

	    public void setIsLightbox(boolean isLightbox) {
	        this.isLightbox = isLightbox;
	    }

	public Integer getDonationRecordId() {
		return donationRecordId;
	}

	public void setDonationRecordId(Integer donationRecordId) {
		this.donationRecordId = donationRecordId;
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

	public Integer getDonationType() {
		return donationType;
	}

	public void setDonationType(Integer donationType) {
		this.donationType = donationType;
	}

}
