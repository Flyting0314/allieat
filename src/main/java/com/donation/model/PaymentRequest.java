package com.donation.model;

public class PaymentRequest {
	
	private int amount;
    private String orderNo;
    private String itemDesc;
    
    public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getItemDesc() {
		return itemDesc;
	}
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}
	public Object getExecTimes() {
		// TODO Auto-generated method stub
		return null;
	}
}
