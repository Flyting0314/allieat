package com.entity;

import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "orderdetail")
public class OrderDetailVO {

    @EmbeddedId 
    private OrderDetailId id;

    @Column(name = "orderId", insertable = false, updatable = false)
    private Integer orderId;

    @Column(name = "foodId", insertable = false, updatable = false)
    private Integer foodId;

    @Column(name = "createdTime")
    private Timestamp createdTime;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "pointsCost")
    private Integer pointsCost;

    @Column(name = "note")
    private String note;

    // 2025/4/10 新增：建立與 OrderFoodVO 的多對一關聯
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", insertable = false, updatable = false)
    private OrderFoodVO order;

    // 2025/4/10 新增：建立與 FoodVO 的多對一關聯
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foodId", insertable = false, updatable = false)
    private FoodVO food;

    // ===== Getter / Setter =====
    public OrderDetailId getId() {
        return id;
    }

    public void setId(OrderDetailId id) {
        this.id = id;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getPointsCost() {
        return pointsCost;
    }

    public void setPointsCost(Integer pointsCost) {
        this.pointsCost = pointsCost;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public OrderFoodVO getOrder() {
        return order;
    }

    public void setOrder(OrderFoodVO order) {
        this.order = order;
    }

    public FoodVO getFood() {
        return food;
    }

    public void setFood(FoodVO food) {
        this.food = food;
    }

    @Override
    public String toString() {
        return "The result for this search is \n"
                + "id=" + id + "\n"
                + "createdTime=" + createdTime + "\n"
                + "amount=" + amount + "\n"
                + "pointsCost=" + pointsCost + "\n"
                + "note=" + note + "\n";
    }

	public void setAttached(AttachedVO attached) {
		
		
	}
}
