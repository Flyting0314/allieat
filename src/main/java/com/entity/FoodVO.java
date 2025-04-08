package com.entity;

import java.sql.Timestamp;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "food")  
public class FoodVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "foodId")
    private Integer foodId;
    
    
    @OneToMany(mappedBy = "foodId", cascade = CascadeType.ALL)
	private Set<OrderDetailVO> orderDetail;
    
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
	@OrderBy("foodId asc")
	private Set<AttachedVO> attached;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StoreId")  
	@JsonIgnore// Jackson的忽略標籤，應用於SpringBoot環境。
    private StoreVO store;

    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @CreationTimestamp
    @Column(name = "createdTime")
    private Timestamp createdTime;

    @Column(name = "status", columnDefinition = "TINYINT")
    private Integer status;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "photo", length = 255)
    private String photo;

    @Column(name = "cost")
    private Integer cost;

    
    public FoodVO() {}

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

  
	public Set<OrderDetailVO> getOrderDetailOneToMany() {
		return orderDetail;
	}

	public void setOrderDetailOneToMany(Set<OrderDetailVO> orderDetail) {
		this.orderDetail = orderDetail;
	}



	public Set<AttachedVO> getAttached() {
		return attached;
	}

	public void setAttached(Set<AttachedVO> attached) {
		this.attached = attached;
	}


	public StoreVO getStore() {
		return store;
	}

	public void setStore(StoreVO store) {
		this.store = store;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

	public Set<OrderDetailVO> getOrderDetail() {
		return orderDetail;
	}

	public void setOrderDetail(Set<OrderDetailVO> orderDetail) {
		this.orderDetail = orderDetail;
	}

	@Override
	public String toString() {
		return "foodId=" + foodId +"\n"
				+ ", orderDetail=" + orderDetail  +"\n"
				+ ", attached=" + attached +"\n"
				+ ", store="+ store +"\n"
				+ ", name=" + name +"\n"
				+ ", createdTime=" + createdTime +"\n"
				+ ", status=" + status +"\n"
				+ ", amount="+ amount +"\n"
				+ ", photo=" + photo +"\n"
				+ ", cost=" + cost ;
	}

	
	




	
    
    
}
