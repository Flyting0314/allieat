package com.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.entity.FoodVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "attached")
public class AttachedVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "attachedId", updatable = false)
    private Integer attachedId;
    @ManyToOne
    @JoinColumn(name = "foodId")  
	@JsonIgnore// Jackson的忽略標籤，應用於SpringBoot環境。
    private FoodVO food;
    
    private String name;
    private Timestamp createdTime;

    public AttachedVO() {}

    public Integer getAttachedId() {
        return attachedId;
    }

    public void setAttachedId(Integer attachedId) {
        this.attachedId = attachedId;
    }

    public FoodVO getFood() {
		return food;
	}

	public void setFood(FoodVO food) {
		this.food = food;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "attachedId=" + attachedId +"\n"
                +"foodId=" + food.getFoodId()   +"\n"
                +"name=" + name  +"\n"
                +"createdTime=" + createdTime;
    }
}
