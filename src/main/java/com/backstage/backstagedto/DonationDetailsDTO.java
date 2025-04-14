package com.backstage.backstagedto;

import java.sql.Timestamp;

public class DonationDetailsDTO {
    private String donorName;
    private String identityNumber;
    private Boolean identityType;
    private Integer donationAmount;
    private Timestamp donationTime;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String errorMsg;

    public DonationDetailsDTO(String donorName, String identityNumber
            , Boolean identityType, Integer donationAmount
            , Timestamp donationTime, String contactEmail
            , String contactPhone, String address
            , String errorMsg) {
        this.donorName = donorName;
        this.identityNumber = identityNumber;
        this.identityType = identityType;
        this.donationAmount = donationAmount;
        this.donationTime = donationTime;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.address = address;
        this.errorMsg = errorMsg;
    }
    
    public DonationDetailsDTO(String errorMsg) {
    	 this.errorMsg = errorMsg;
    }

    public DonationDetailsDTO() {
    }
    

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public Boolean getIdentityType() {
        return identityType;
    }

    public void setIdentityType(Boolean identityType) {
        this.identityType = identityType;
    }

    public Integer getDonationAmount() {
        return donationAmount;
    }

    public void setDonationAmount(Integer donationAmount) {
        this.donationAmount = donationAmount;
    }

    public Timestamp getDonationTime() {
        return donationTime;
    }

    public void setDonationTime(Timestamp donationTime) {
        this.donationTime = donationTime;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "DonationDetailsDTO{" +
                "donorName='" + donorName + '\'' +
                ", identityNumber='" + identityNumber + '\'' +
                ", identityType=" + identityType +
                ", donationAmount=" + donationAmount +
                ", donationTime=" + donationTime +
                ", contactEmail='" + contactEmail + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", address='" + address + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
