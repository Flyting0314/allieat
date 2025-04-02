package com.backstage.backstagedto;


import org.springframework.stereotype.Component;

import java.sql.Timestamp;
@Component
public class UpdateOrgDTO {

    private Integer organizationId;
    private String name;
    private String type;
    private Timestamp createdTime;
    private Integer status;

    public UpdateOrgDTO(Integer organizationId, String name, String type, Timestamp createdTime, Integer status) {
        this.organizationId = organizationId;
        this.name = name;
        this.type = type;
        this.createdTime = createdTime;
        this.status = status;
    }

    public UpdateOrgDTO() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
