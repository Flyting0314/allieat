package com.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class OrderDetailId implements Serializable {

    private Integer orderId;
    private Integer foodId;

    // 無參數構造器
    public OrderDetailId() {}

    // 帶參構造器
    public OrderDetailId(Integer orderId, Integer foodId) {
        this.orderId = orderId;
        this.foodId = foodId;
    }

    // getter 和 setter
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    // 覆寫 equals 和 hashCode 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailId that = (OrderDetailId) o;
        return orderId.equals(that.orderId) && foodId.equals(that.foodId);
    }

    @Override
    public int hashCode() {
        return 31 * orderId.hashCode() + foodId.hashCode();
    }

	@Override
	public String toString() {
		return "orderId=" + orderId +"\n"
				+ "foodId=" + foodId ;
	}
    
}
