package com.entity;

import java.sql.Timestamp;
import java.util.Arrays;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "photo")
public class PhotoVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photoId")
    private Integer photoId;

    @ManyToOne
    @JoinColumn(name = "storeId")  
	@JsonIgnore// Jackson的忽略標籤，應用於SpringBoot環境。
    private StoreVO store;

    @Lob
    @Column(name = "photoSrc",columnDefinition = "LONGBLOB")
    private byte[] photoSrc;
    
    @CreationTimestamp
    @Column(name = "updateTime")
    private Timestamp updateTime;

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

  

    public StoreVO getStore() {
		return store;
	}

	public void setStore(StoreVO store) {
		this.store = store;
	}

	public byte[] getPhotoSrc() {
        return photoSrc;
    }

    public void setPhotoSrc(byte[] photoSrc) {
        this.photoSrc = photoSrc;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

	@Override
	public String toString() {
		return "photoId=" + photoId +"\n"
				+ "store=" + store.getStoreId()+"\n"
				+ "photoSrc=" + Arrays.toString(photoSrc) +"\n"
				+ "updateTime=" + updateTime ;
	}
    
    
}
