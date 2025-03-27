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
@Table(name = "totalAmount")
public class TotalAmountVO implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "totalAmountId", updatable = false)
    private Integer totalAmountId;
    private Integer totalAmount;
    private Timestamp updatedTime;

    public TotalAmountVO() {
    }

    public TotalAmountVO(Integer totalAmountId, Integer totalAmount) {
        this.totalAmountId = totalAmountId;
        this.totalAmount = totalAmount;
    }

    public Integer getTotalAmountId() {
        return totalAmountId;
    }

    public void setTotalAmountId(Integer totalAmountId) {
        this.totalAmountId = totalAmountId;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

	public Timestamp getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Timestamp updatedTime) {
		this.updatedTime = updatedTime;
	}

	@Override
	public String toString() {
		return "totalAmountId=" + totalAmountId+"\n" 
				+ "totalAmount=" + totalAmount +"\n" 
				+ "updatedTime="+ updatedTime ;
	}

}
