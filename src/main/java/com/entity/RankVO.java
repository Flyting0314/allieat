package com.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "rankrank")
public class RankVO implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rankId", updatable = false)
    private Integer rankId;         
    private String identityData;    
    private Integer totalDonation;  
    private Timestamp updatedTime;  


    public RankVO() {}


    public RankVO(Integer rankId, String identityData, Integer totalDonation, Timestamp updatedTime) {
        this.rankId = rankId;
        this.identityData = identityData;
        this.totalDonation = totalDonation;
        this.updatedTime = updatedTime;
    }


    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
    }

    public String getIdentityData() {
        return identityData;
    }

    public void setIdentityData(String identityData) {
        this.identityData = identityData;
    }

    public Integer getTotalDonation() {
        return totalDonation;
    }

    public void setTotalDonation(Integer totalDonation) {
        this.totalDonation = totalDonation;
    }

    public Timestamp getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "rankId=" + rankId +"\n"
                +"identityData='" + identityData +"\n" 
                + "totalDonation=" + totalDonation +"\n" 
                +"updatedTime=" + updatedTime ;
    }
}
