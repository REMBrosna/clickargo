package com.guudint.clickargo.clictruck.planexec.job.mobile.dto;

import java.io.Serializable;

public class CkMTripCargoDetails implements Serializable {


	private static final long serialVersionUID = -1171499765698075243L;
	// Cargo
	private String cnCgId;
	private String specialInstructions;
	// Container Details
	private String cntType;
	private String cntNo;
	private String cntSealNo;
	private String cntLoad;

	// Goods details
	private String goodsType;
	private String goodsDesc;

	private Character cgPickupStatus;
	private Character cgDropOffStatus;

	/**
	 * @return the specialInstructions
	 */
	public String getSpecialInstructions() {
		return specialInstructions;
	}

	/**
	 * @param specialInstructions the specialInstructions to set
	 */
	public void setSpecialInstructions(String specialInstructions) {
		this.specialInstructions = specialInstructions;
	}

	/**
	 * @return the cntType
	 */
	public String getCntType() {
		return cntType;
	}

	/**
	 * @param cntType the cntType to set
	 */
	public void setCntType(String cntType) {
		this.cntType = cntType;
	}

	/**
	 * @return the cntNo
	 */
	public String getCntNo() {
		return cntNo;
	}

	/**
	 * @param cntNo the cntNo to set
	 */
	public void setCntNo(String cntNo) {
		this.cntNo = cntNo;
	}

	/**
	 * @return the cntSealNo
	 */
	public String getCntSealNo() {
		return cntSealNo;
	}

	/**
	 * @param cntSealNo the cntSealNo to set
	 */
	public void setCntSealNo(String cntSealNo) {
		this.cntSealNo = cntSealNo;
	}

	/**
	 * @return the cntLoad
	 */
	public String getCntLoad() {
		return cntLoad;
	}

	/**
	 * @param cntLoad the cntLoad to set
	 */
	public void setCntLoad(String cntLoad) {
		this.cntLoad = cntLoad;
	}

	/**
	 * @return the goodsType
	 */
	public String getGoodsType() {
		return goodsType;
	}

	/**
	 * @param goodsType the goodsType to set
	 */
	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	/**
	 * @return the goodsDesc
	 */
	public String getGoodsDesc() {
		return goodsDesc;
	}

	/**
	 * @param goodsDesc the goodsDesc to set
	 */
	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public String getCnCgId() {
		return cnCgId;
	}

	public void setCnCgId(String cnCgId) {
		this.cnCgId = cnCgId;
	}

	public Character getCgPickupStatus() {
		return cgPickupStatus;
	}

	public void setCgPickupStatus(Character cgPickupStatus) {
		this.cgPickupStatus = cgPickupStatus;
	}

	public Character getCgDropOffStatus() {
		return cgDropOffStatus;
	}

	public void setCgDropOffStatus(Character cgDropOffStatus) {
		this.cgDropOffStatus = cgDropOffStatus;
	}
}
