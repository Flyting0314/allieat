package com.donation.model;

public class PaymentRequest {

    // 金流相關欄位
    private int amount;              // 金額
    private String orderNo;          // 商店訂單編號 (MerchantTradeNo)
    private String itemDesc;         // 商品描述

    // 捐款人資訊欄位
    private String identityData;     // 姓名
    private String email;            // 信箱
    private String phone;            // 電話
    private String county;           // 縣市
    private String district;         // 區域
    private String address;          // 詳細地址
    private String salutation;       // 稱謂（先生／小姐）
    private String idNum;            // 身分證號
    private String guiNum;           // 統一編號
    private int donationType;        // 單筆=0 / 定期定額=1
    private Integer execTimes;          // 定期期數（如有需要）

    // 金流邏輯用：oneTime 或 periodic（由 donationType 決定）
    public String getType() {
        return donationType == 1 ? "periodic" : "oneTime";
    }

    // === Getter & Setter ===
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getIdentityData() {
        return identityData;
    }

    public void setIdentityData(String identityData) {
        this.identityData = identityData;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getDonationType() {
        return donationType;
    }

    public void setDonationType(int donationType) {
        this.donationType = donationType;
    }

    public Integer getExecTimes() {
        return execTimes;
    }

    public void setExecTimes(int execTimes) {
        this.execTimes = execTimes;
    }
}
