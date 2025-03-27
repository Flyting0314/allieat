package com.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "foodType")
public class FoodTypeVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foodTypeId")
    private Integer foodTypeId;

    @ManyToOne
    @JoinColumn(name = "storeId")  
	@JsonIgnore// Jackson的忽略標籤，應用於SpringBoot環境。
    private StoreVO store;

    @Column(name = "type")
    private String type;

    public Integer getFoodTypeId() {
        return foodTypeId;
    }

    public void setFoodTypeId(Integer foodTypeId) {
        this.foodTypeId = foodTypeId;
    }

    

    public StoreVO getStore() {
		return store;
	}

	public void setStore(StoreVO store) {
		this.store = store;
	}

	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	@Override
	public String toString() {
		return "foodTypeId=" + foodTypeId +"\n" 
				+ "store=" + store.getStoreId() +"\n" 
				+ "type=" + type;
	}
    
    
    
}
