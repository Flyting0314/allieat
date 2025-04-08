package com.backstage.backstagedto;



public class DonateDTO {
    private Integer donationRecordId;
    private String identityData;
    private String idNum;
    private Integer donationIncome;


    public DonateDTO(Integer donationRecordId, String identityData, String idNum, Integer donationIncome) {
        this.donationRecordId = donationRecordId;
        this.identityData = identityData;
        this.idNum = idNum;
        this.donationIncome = donationIncome;
    }

    public DonateDTO() {
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

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public Integer getDonationIncome() {
        return donationIncome;
    }

    public void setDonationIncome(Integer donationIncome) {
        this.donationIncome = donationIncome;
    }
}
