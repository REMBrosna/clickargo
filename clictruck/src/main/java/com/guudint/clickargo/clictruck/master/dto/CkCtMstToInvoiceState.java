package com.guudint.clickargo.clictruck.master.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstToInvoiceState;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstToInvoiceState extends AbstractDTO<CkCtMstToInvoiceState, TCkCtMstToInvoiceState> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -6364134725893809115L;
	
	// Attributes
	/////////////
	private String instId;
	private String instName;
	private String instDesc;
	private String instDescOth;
	private Character instStatus;
	private Date instDtCreate;
	private String instUidCreate;
	private Date instDtLupd;
	private String instUidLupd;

	// Constructors
	///////////////
	public CkCtMstToInvoiceState() {
	}

	public CkCtMstToInvoiceState(TCkCtMstToInvoiceState entity) {
		super(entity);
	}

	/**
	 * @param instId
	 * @param instName
	 * @param instDesc
	 * @param instDescOth
	 * @param instStatus
	 * @param instDtCreate
	 * @param instUidCreate
	 * @param instDtLupd
	 * @param instUidLupd
	 */
	public CkCtMstToInvoiceState(String instId, String instName, String instDesc, String instDescOth,
			Character instStatus, Date instDtCreate, String instUidCreate, Date instDtLupd, String instUidLupd) {
		super();
		this.instId = instId;
		this.instName = instName;
		this.instDesc = instDesc;
		this.instDescOth = instDescOth;
		this.instStatus = instStatus;
		this.instDtCreate = instDtCreate;
		this.instUidCreate = instUidCreate;
		this.instDtLupd = instDtLupd;
		this.instUidLupd = instUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the instId
	 */
	public String getInstId() {
		return instId;
	}

	/**
	 * @param instId the instId to set
	 */
	public void setInstId(String instId) {
		this.instId = instId;
	}

	/**
	 * @return the instName
	 */
	public String getInstName() {
		return instName;
	}

	/**
	 * @param instName the instName to set
	 */
	public void setInstName(String instName) {
		this.instName = instName;
	}

	/**
	 * @return the instDesc
	 */
	public String getInstDesc() {
		return instDesc;
	}

	/**
	 * @param instDesc the instDesc to set
	 */
	public void setInstDesc(String instDesc) {
		this.instDesc = instDesc;
	}

	/**
	 * @return the instDescOth
	 */
	public String getInstDescOth() {
		return instDescOth;
	}

	/**
	 * @param instDescOth the instDescOth to set
	 */
	public void setInstDescOth(String instDescOth) {
		this.instDescOth = instDescOth;
	}

	/**
	 * @return the instStatus
	 */
	public Character getInstStatus() {
		return instStatus;
	}

	/**
	 * @param instStatus the instStatus to set
	 */
	public void setInstStatus(Character instStatus) {
		this.instStatus = instStatus;
	}

	/**
	 * @return the instDtCreate
	 */
	public Date getInstDtCreate() {
		return instDtCreate;
	}

	/**
	 * @param instDtCreate the instDtCreate to set
	 */
	public void setInstDtCreate(Date instDtCreate) {
		this.instDtCreate = instDtCreate;
	}

	/**
	 * @return the instUidCreate
	 */
	public String getInstUidCreate() {
		return instUidCreate;
	}

	/**
	 * @param instUidCreate the instUidCreate to set
	 */
	public void setInstUidCreate(String instUidCreate) {
		this.instUidCreate = instUidCreate;
	}

	/**
	 * @return the instDtLupd
	 */
	public Date getInstDtLupd() {
		return instDtLupd;
	}

	/**
	 * @param instDtLupd the instDtLupd to set
	 */
	public void setInstDtLupd(Date instDtLupd) {
		this.instDtLupd = instDtLupd;
	}

	/**
	 * @return the instUidLupd
	 */
	public String getInstUidLupd() {
		return instUidLupd;
	}

	/**
	 * @param instUidLupd the instUidLupd to set
	 */
	public void setInstUidLupd(String instUidLupd) {
		this.instUidLupd = instUidLupd;
	}

	// Override Methods
	///////////////////
	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 */
	@Override
	public int compareTo(CkCtMstToInvoiceState o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.COAbstractEntity#init()
	 * 
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
