package com.backstage.backstagedto;

import jakarta.validation.constraints.NotBlank;

import java.sql.Timestamp;

public class ChangePasswordDTO {
    @NotBlank
    private String account;
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
    private String status;

    public ChangePasswordDTO(String account, String oldPassword, String newPassword) {
        this.account = account;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public ChangePasswordDTO() {
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
