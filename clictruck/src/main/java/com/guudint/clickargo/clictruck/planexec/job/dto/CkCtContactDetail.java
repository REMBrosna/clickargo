package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtContactDetail;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtContactDetail  extends AbstractDTO<CkCtContactDetail, TCkCtContactDetail> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 4330291175586455014L;
	
	// Attributes
	/////////////
	private String cdId;
	private String cdName;
	private String cdPhone;
	private String cdEmail;
	private Character cdStatus;
	private Date cdDtCreate;
	private String cdUidCreate;
	private Date cdDtLupd;
	private String rrUidLupd;

	// Constructors
	///////////////
	public CkCtContactDetail() {
	}

	public CkCtContactDetail(TCkCtContactDetail entity) {
		super(entity);
	}

	/**
	 * @param cdId
	 * @param cdName
	 * @param cdPhone
	 * @param cdEmail
	 * @param cdStatus
	 * @param cdDtCreate
	 * @param cdUidCreate
	 * @param cdDtLupd
	 * @param rrUidLupd
	 */
	public CkCtContactDetail(String cdId, String cdName, String cdPhone, String cdEmail, Character cdStatus,
			Date cdDtCreate, String cdUidCreate, Date cdDtLupd, String rrUidLupd) {
		super();
		this.cdId = cdId;
		this.cdName = cdName;
		this.cdPhone = cdPhone;
		this.cdEmail = cdEmail;
		this.cdStatus = cdStatus;
		this.cdDtCreate = cdDtCreate;
		this.cdUidCreate = cdUidCreate;
		this.cdDtLupd = cdDtLupd;
		this.rrUidLupd = rrUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the cdId
	 */
	public String getCdId() {
		return cdId;
	}

	/**
	 * @param cdId the cdId to set
	 */
	public void setCdId(String cdId) {
		this.cdId = cdId;
	}

	/**
	 * @return the cdName
	 */
	public String getCdName() {
		return cdName;
	}

	/**
	 * @param cdName the cdName to set
	 */
	public void setCdName(String cdName) {
		this.cdName = cdName;
	}

	/**
	 * @return the cdPhone
	 */
	public String getCdPhone() {
		return cdPhone;
	}

	/**
	 * @param cdPhone the cdPhone to set
	 */
	public void setCdPhone(String cdPhone) {
		this.cdPhone = cdPhone;
	}

	/**
	 * @return the cdEmail
	 */
	public String getCdEmail() {
		return cdEmail;
	}

	/**
	 * @param cdEmail the cdEmail to set
	 */
	public void setCdEmail(String cdEmail) {
		this.cdEmail = cdEmail;
	}

	/**
	 * @return the cdStatus
	 */
	public Character getCdStatus() {
		return cdStatus;
	}

	/**
	 * @param cdStatus the cdStatus to set
	 */
	public void setCdStatus(Character cdStatus) {
		this.cdStatus = cdStatus;
	}

	/**
	 * @return the cdDtCreate
	 */
	public Date getCdDtCreate() {
		return cdDtCreate;
	}

	/**
	 * @param cdDtCreate the cdDtCreate to set
	 */
	public void setCdDtCreate(Date cdDtCreate) {
		this.cdDtCreate = cdDtCreate;
	}

	/**
	 * @return the cdUidCreate
	 */
	public String getCdUidCreate() {
		return cdUidCreate;
	}

	/**
	 * @param cdUidCreate the cdUidCreate to set
	 */
	public void setCdUidCreate(String cdUidCreate) {
		this.cdUidCreate = cdUidCreate;
	}

	/**
	 * @return the cdDtLupd
	 */
	public Date getCdDtLupd() {
		return cdDtLupd;
	}

	/**
	 * @param cdDtLupd the cdDtLupd to set
	 */
	public void setCdDtLupd(Date cdDtLupd) {
		this.cdDtLupd = cdDtLupd;
	}

	/**
	 * @return the rrUidLupd
	 */
	public String getRrUidLupd() {
		return rrUidLupd;
	}

	/**
	 * @param rrUidLupd the rrUidLupd to set
	 */
	public void setRrUidLupd(String rrUidLupd) {
		this.rrUidLupd = rrUidLupd;
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
	public int compareTo(CkCtContactDetail o) {
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
