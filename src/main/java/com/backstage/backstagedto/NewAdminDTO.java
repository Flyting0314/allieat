package com.backstage.backstagedto;

import jakarta.validation.constraints.NotBlank;


import java.sql.Timestamp;

public class NewAdminDTO {
    @NotBlank
    private String account;
    @NotBlank
    private String password;
    private String status;
    private Timestamp createdTime;

    public NewAdminDTO(String account, String password, String verifyPassword) {
        this.account = account;
        this.password = password;
    }

    public NewAdminDTO() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }
}
