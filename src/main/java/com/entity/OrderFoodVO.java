package com.entity;

import java.sql.Timestamp;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "orderlist")
public class OrderFoodVO {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId", updatable = false)
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", referencedColumnName = "storeId", nullable = false)
    private StoreVO store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", referencedColumnName = "memberId", nullable = false)
    private MemberVO member;

    @Column(name = "rate", columnDefinition = "TINYINT(1)")
    private Integer rate;

    @Column(name = "comment", length = 100)
    private String comment;

    @Column(name = "serveStat", columnDefinition = "TINYINT(1)", nullable = false)
    private Integer serveStat;

    @Column(name = "pickStat", columnDefinition = "TINYINT(1)", nullable = false)
    private Integer pickStat;

    @Column(name = "pickTime")
    private Timestamp pickTime;

    @Column(name = "createdTime", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdTime;

    // 2025/04/13 新增：與訂單明細(OrderDetailVO)建立一對多關聯
    @OneToMany(mappedBy = "orderFood", cascade = CascadeType.ALL)
    private Set<OrderDetailVO> orderDetails;

    // Getter & Setter
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public StoreVO getStore() {
        return store;
    }

    public void setStore(StoreVO store) {
        this.store = store;
    }

    public MemberVO getMember() {
        return member;
    }

    public void setMember(MemberVO member) {
        this.member = member;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getServeStat() {
        return serveStat;
    }

    public void setServeStat(Integer serveStat) {
        this.serveStat = serveStat;
    }

    public Integer getPickStat() {
        return pickStat;
    }

    public void setPickStat(Integer pickStat) {
        this.pickStat = pickStat;
    }

    public Timestamp getPickTime() {
        return pickTime;
    }

    public void setPickTime(Timestamp pickTime) {
        this.pickTime = pickTime;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Set<OrderDetailVO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(Set<OrderDetailVO> orderDetails) {
        this.orderDetails = orderDetails;
    }
} 
