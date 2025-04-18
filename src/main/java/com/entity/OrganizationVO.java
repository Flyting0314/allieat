package com.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "organization")
public class OrganizationVO implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "organizationId", updatable = false)
//	@NotNull(message = "請選擇註冊單位")
    private Integer organizationId;
	
	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	@OrderBy("organizationId asc")
	private Set<MemberVO> Member;
    private String name;            
    private String type;            
    private Timestamp createdTime;  
    @Column(name = "status",columnDefinition = "TINYINT(1)")
    private Integer status;         

    
    public OrganizationVO() {}

   
    public OrganizationVO(Integer organizationId, String name, String type, Timestamp createdTime, Integer status) {
        this.organizationId = organizationId;
        this.name = name;
        this.type = type;
        this.createdTime = createdTime;
        this.status = status;
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

    @Override
    public String toString() {
        return  "organizationId=" + organizationId +"\n" 
        		+"name=" + name +"\n" 
        		+"type=" + type  +"\n" 
        		+"createdTime=" + createdTime +"\n" 
                +"status=" + status;
    }
}
