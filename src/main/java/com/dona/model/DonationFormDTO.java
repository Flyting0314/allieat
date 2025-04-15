package com.dona.model;

public class DonationFormDTO {
    private Integer amount;
    private String email;
    private String phone;
    private String salutation;
    private String idNum;
    private String guiNum;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
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
}
