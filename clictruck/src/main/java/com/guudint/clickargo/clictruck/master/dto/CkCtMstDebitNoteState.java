package com.guudint.clickargo.clictruck.master.dto;
// Generated 23 Feb, 2023 4:00:19 PM by Hibernate Tools 5.2.1.Final

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstDebitNoteState;
import com.vcc.camelone.common.dto.AbstractDTO;


public class CkCtMstDebitNoteState extends AbstractDTO<CkCtMstDebitNoteState, TCkCtMstDebitNoteState> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 4596579052392595077L;
	
	// Attributes
	/////////////
	private String dnstId;
	private String dnstName;
	private String dnstDesc;
	private String dnstDescOth;
	private Character dnstStatus;
	private Date dnstDtCreate;
	private String dnstUidCreate;
	private Date dnstDtLupd;
	private String dnstUidLupd;

	// Constructors
	///////////////
	public CkCtMstDebitNoteState() {
	}

	public CkCtMstDebitNoteState(TCkCtMstDebitNoteState entity) {
		super(entity);
	}

	/**
	 * @param dnstId
	 * @param dnstName
	 * @param dnstDesc
	 * @param dnstDescOth
	 * @param dnstStatus
	 * @param dnstDtCreate
	 * @param dnstUidCreate
	 * @param dnstDtLupd
	 * @param dnstUidLupd
	 */
	public CkCtMstDebitNoteState(String dnstId, String dnstName, String dnstDesc, String dnstDescOth,
			Character dnstStatus, Date dnstDtCreate, String dnstUidCreate, Date dnstDtLupd, String dnstUidLupd) {
		super();
		this.dnstId = dnstId;
		this.dnstName = dnstName;
		this.dnstDesc = dnstDesc;
		this.dnstDescOth = dnstDescOth;
		this.dnstStatus = dnstStatus;
		this.dnstDtCreate = dnstDtCreate;
		this.dnstUidCreate = dnstUidCreate;
		this.dnstDtLupd = dnstDtLupd;
		this.dnstUidLupd = dnstUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the dnstId
	 */
	public String getDnstId() {
		return dnstId;
	}

	/**
	 * @param dnstId the dnstId to set
	 */
	public void setDnstId(String dnstId) {
		this.dnstId = dnstId;
	}

	/**
	 * @return the dnstName
	 */
	public String getDnstName() {
		return dnstName;
	}

	/**
	 * @param dnstName the dnstName to set
	 */
	public void setDnstName(String dnstName) {
		this.dnstName = dnstName;
	}

	/**
	 * @return the dnstDesc
	 */
	public String getDnstDesc() {
		return dnstDesc;
	}

	/**
	 * @param dnstDesc the dnstDesc to set
	 */
	public void setDnstDesc(String dnstDesc) {
		this.dnstDesc = dnstDesc;
	}

	/**
	 * @return the dnstDescOth
	 */
	public String getDnstDescOth() {
		return dnstDescOth;
	}

	/**
	 * @param dnstDescOth the dnstDescOth to set
	 */
	public void setDnstDescOth(String dnstDescOth) {
		this.dnstDescOth = dnstDescOth;
	}

	/**
	 * @return the dnstStatus
	 */
	public Character getDnstStatus() {
		return dnstStatus;
	}

	/**
	 * @param dnstStatus the dnstStatus to set
	 */
	public void setDnstStatus(Character dnstStatus) {
		this.dnstStatus = dnstStatus;
	}

	/**
	 * @return the dnstDtCreate
	 */
	public Date getDnstDtCreate() {
		return dnstDtCreate;
	}

	/**
	 * @param dnstDtCreate the dnstDtCreate to set
	 */
	public void setDnstDtCreate(Date dnstDtCreate) {
		this.dnstDtCreate = dnstDtCreate;
	}

	/**
	 * @return the dnstUidCreate
	 */
	public String getDnstUidCreate() {
		return dnstUidCreate;
	}

	/**
	 * @param dnstUidCreate the dnstUidCreate to set
	 */
	public void setDnstUidCreate(String dnstUidCreate) {
		this.dnstUidCreate = dnstUidCreate;
	}

	/**
	 * @return the dnstDtLupd
	 */
	public Date getDnstDtLupd() {
		return dnstDtLupd;
	}

	/**
	 * @param dnstDtLupd the dnstDtLupd to set
	 */
	public void setDnstDtLupd(Date dnstDtLupd) {
		this.dnstDtLupd = dnstDtLupd;
	}

	/**
	 * @return the dnstUidLupd
	 */
	public String getDnstUidLupd() {
		return dnstUidLupd;
	}

	/**
	 * @param dnstUidLupd the dnstUidLupd to set
	 */
	public void setDnstUidLupd(String dnstUidLupd) {
		this.dnstUidLupd = dnstUidLupd;
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
	public int compareTo(CkCtMstDebitNoteState o) {
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
