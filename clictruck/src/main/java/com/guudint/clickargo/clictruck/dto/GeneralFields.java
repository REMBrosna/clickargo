package com.guudint.clickargo.clictruck.dto;

import java.math.BigDecimal;

public class GeneralFields {
	
	private String username;
	private String jobNo;
	private String cusRefNo;
	private String shipmentType;
	private String planDate;
	private String bookDate;
	private String deliveryDate;
	private String loading;
	private BigDecimal estimatedPricing;
	private String emailNotif;
	private String goodsInfo;
	
	public GeneralFields() {

	}

	public GeneralFields(String username, String jobNo, String cusRefNo, String shipmentType,
			String planDate, String bookDate, String deliveryDate, String loading, BigDecimal estimatedPricing,
			String emailNotif, String goodsInfo) {
		super();
		this.username = username;
		this.jobNo = jobNo;
		this.cusRefNo = cusRefNo;
		this.shipmentType = shipmentType;
		this.planDate = planDate;
		this.bookDate = bookDate;
		this.deliveryDate = deliveryDate;
		this.loading = loading;
		this.estimatedPricing = estimatedPricing;
		this.emailNotif = emailNotif;
		this.goodsInfo = goodsInfo;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getJobNo() {
		return jobNo;
	}
	
	public void setJobNo(String jobNo) {
		this.jobNo = jobNo;
	}
	
	public String getCusRefNo() {
		return cusRefNo;
	}
	
	public void setCusRefNo(String cusRefNo) {
		this.cusRefNo = cusRefNo;
	}
	
	public String getShipmentType() {
		return shipmentType;
	}
	
	public void setShipmentType(String shipmentType) {
		this.shipmentType = shipmentType;
	}
	
	public String getPlanDate() {
		return planDate;
	}
	
	public void setPlanDate(String planDate) {
		this.planDate = planDate;
	}
	
	public String getBookDate() {
		return bookDate;
	}
	
	public void setBookDate(String bookDate) {
		this.bookDate = bookDate;
	}
	
	public String getDeliveryDate() {
		return deliveryDate;
	}
	
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	
	public String getLoading() {
		return loading;
	}
	
	public void setLoading(String loading) {
		this.loading = loading;
	}
	
	public BigDecimal getEstimatedPricing() {
		return estimatedPricing;
	}
	
	public void setEstimatedPricing(BigDecimal estimatedPricing) {
		this.estimatedPricing = estimatedPricing;
	}
	
	public String getEmailNotif() {
		return emailNotif;
	}
	
	public void setEmailNotif(String emailNotif) {
		this.emailNotif = emailNotif;
	}
	
	public String getGoodsInfo() {
		return goodsInfo;
	}
	
	public void setGoodsInfo(String goodsInfo) {
		this.goodsInfo = goodsInfo;
	}

}
