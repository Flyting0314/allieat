package com.registMail.dto;

import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class StoreListDTO {

	private Integer storeId;
	private String name;
	private String email;
	private String regTime;
	private Integer reviewed;
	private Integer accStat;

	// 建構器 (從 StoreVO轉過來用)
	public StoreListDTO(Integer storeId, String name, String email, Timestamp regTime, Integer reviewed,
			Integer accStat) {
		this.storeId = storeId;
		this.name = name;
		this.email = email;
		this.reviewed = reviewed;
		this.accStat = accStat;
		this.regTime = (regTime != null)
				? regTime.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
				: null;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRegTime() {
		return regTime;
	}

	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}

	public Integer getReviewed() {
		return reviewed;
	}

	public void setReviewed(Integer reviewed) {
		this.reviewed = reviewed;
	}

	public Integer getAccStat() {
		return accStat;
	}

	public void setAccStat(Integer accStat) {
		this.accStat = accStat;
	}
}
