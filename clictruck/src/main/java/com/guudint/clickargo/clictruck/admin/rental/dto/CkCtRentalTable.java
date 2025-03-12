package com.guudint.clickargo.clictruck.admin.rental.dto;

import java.util.Date;
import java.util.Set;

import com.guudint.clickargo.clictruck.admin.rental.model.TCkCtRentalTable;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.dto.MstCurrency;

public class CkCtRentalTable extends AbstractDTO<CkCtRentalTable, TCkCtRentalTable> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 7999033814586401779L;

	// Attributes
	/////////////
	private String rtId;
	private CoreAccn TCoreAccnByRtCompany;
	private CoreAccn TCoreAccnByRtCoFf;
	private MstCurrency TMstCurrency;
	private String rtName;
	private String rtDescription;
	private Date rtDtStart;
	private Date rtDtEnd;
	private String rtRemarks;
	private Character rtStatus;
	private Date rtDtCreate;
	private String rtUidCreate;
	private Date rtDtLupd;
	private String rtUidLupd;
	
	// Constructors
	///////////////
	public CkCtRentalTable() {
	}

	/**
	 * @param entity
	 */
	public CkCtRentalTable(TCkCtRentalTable entity) {
		super(entity);
	}

	/**
	 * @param rtId
	 * @param tCoreAccnByRtCompany
	 * @param tCoreAccnByRtCoFf
	 * @param tMstCurrency
	 * @param rtName
	 * @param rtDescription
	 * @param rtDtStart
	 * @param rtDtEnd
	 * @param rtRemarks
	 * @param rtStatus
	 * @param rtDtCreate
	 * @param rtUidCreate
	 * @param rtDtLupd
	 * @param rtUidLupd
	 */
	public CkCtRentalTable(String rtId, CoreAccn tCoreAccnByRtCompany, CoreAccn tCoreAccnByRtCoFf,
			MstCurrency tMstCurrency, String rtName, String rtDescription, Date rtDtStart, Date rtDtEnd,
			String rtRemarks, Character rtStatus, Date rtDtCreate, String rtUidCreate, Date rtDtLupd, String rtUidLupd,
			Set<CkCtRentalVeh> tCkCtRentalVehs) {
		super();
		this.rtId = rtId;
		TCoreAccnByRtCompany = tCoreAccnByRtCompany;
		TCoreAccnByRtCoFf = tCoreAccnByRtCoFf;
		TMstCurrency = tMstCurrency;
		this.rtName = rtName;
		this.rtDescription = rtDescription;
		this.rtDtStart = rtDtStart;
		this.rtDtEnd = rtDtEnd;
		this.rtRemarks = rtRemarks;
		this.rtStatus = rtStatus;
		this.rtDtCreate = rtDtCreate;
		this.rtUidCreate = rtUidCreate;
		this.rtDtLupd = rtDtLupd;
		this.rtUidLupd = rtUidLupd;
	}

	// Properties
	/////////////	
	/**
	 * @return the rtId
	 */
	public String getRtId() {
		return rtId;
	}

	/**
	 * @param rtId the rtId to set
	 */
	public void setRtId(String rtId) {
		this.rtId = rtId;
	}

	/**
	 * @return the tCoreAccnByRtCompany
	 */
	public CoreAccn getTCoreAccnByRtCompany() {
		return TCoreAccnByRtCompany;
	}

	/**
	 * @param tCoreAccnByRtCompany the tCoreAccnByRtCompany to set
	 */
	public void setTCoreAccnByRtCompany(CoreAccn tCoreAccnByRtCompany) {
		TCoreAccnByRtCompany = tCoreAccnByRtCompany;
	}

	/**
	 * @return the tCoreAccnByRtCoFf
	 */
	public CoreAccn getTCoreAccnByRtCoFf() {
		return TCoreAccnByRtCoFf;
	}

	/**
	 * @param tCoreAccnByRtCoFf the tCoreAccnByRtCoFf to set
	 */
	public void setTCoreAccnByRtCoFf(CoreAccn tCoreAccnByRtCoFf) {
		TCoreAccnByRtCoFf = tCoreAccnByRtCoFf;
	}

	/**
	 * @return the tMstCurrency
	 */
	public MstCurrency getTMstCurrency() {
		return TMstCurrency;
	}

	/**
	 * @param tMstCurrency the tMstCurrency to set
	 */
	public void setTMstCurrency(MstCurrency tMstCurrency) {
		TMstCurrency = tMstCurrency;
	}

	/**
	 * @return the rtName
	 */
	public String getRtName() {
		return rtName;
	}

	/**
	 * @param rtName the rtName to set
	 */
	public void setRtName(String rtName) {
		this.rtName = rtName;
	}

	/**
	 * @return the rtDescription
	 */
	public String getRtDescription() {
		return rtDescription;
	}

	/**
	 * @param rtDescription the rtDescription to set
	 */
	public void setRtDescription(String rtDescription) {
		this.rtDescription = rtDescription;
	}

	/**
	 * @return the rtDtStart
	 */
	public Date getRtDtStart() {
		return rtDtStart;
	}

	/**
	 * @param rtDtStart the rtDtStart to set
	 */
	public void setRtDtStart(Date rtDtStart) {
		this.rtDtStart = rtDtStart;
	}

	/**
	 * @return the rtDtEnd
	 */
	public Date getRtDtEnd() {
		return rtDtEnd;
	}

	/**
	 * @param rtDtEnd the rtDtEnd to set
	 */
	public void setRtDtEnd(Date rtDtEnd) {
		this.rtDtEnd = rtDtEnd;
	}

	/**
	 * @return the rtRemarks
	 */
	public String getRtRemarks() {
		return rtRemarks;
	}

	/**
	 * @param rtRemarks the rtRemarks to set
	 */
	public void setRtRemarks(String rtRemarks) {
		this.rtRemarks = rtRemarks;
	}

	/**
	 * @return the rtStatus
	 */
	public Character getRtStatus() {
		return rtStatus;
	}

	/**
	 * @param rtStatus the rtStatus to set
	 */
	public void setRtStatus(Character rtStatus) {
		this.rtStatus = rtStatus;
	}

	/**
	 * @return the rtDtCreate
	 */
	public Date getRtDtCreate() {
		return rtDtCreate;
	}

	/**
	 * @param rtDtCreate the rtDtCreate to set
	 */
	public void setRtDtCreate(Date rtDtCreate) {
		this.rtDtCreate = rtDtCreate;
	}

	/**
	 * @return the rtUidCreate
	 */
	public String getRtUidCreate() {
		return rtUidCreate;
	}

	/**
	 * @param rtUidCreate the rtUidCreate to set
	 */
	public void setRtUidCreate(String rtUidCreate) {
		this.rtUidCreate = rtUidCreate;
	}

	/**
	 * @return the rtDtLupd
	 */
	public Date getRtDtLupd() {
		return rtDtLupd;
	}

	/**
	 * @param rtDtLupd the rtDtLupd to set
	 */
	public void setRtDtLupd(Date rtDtLupd) {
		this.rtDtLupd = rtDtLupd;
	}

	/**
	 * @return the rtUidLupd
	 */
	public String getRtUidLupd() {
		return rtUidLupd;
	}

	/**
	 * @param rtUidLupd the rtUidLupd to set
	 */
	public void setRtUidLupd(String rtUidLupd) {
		this.rtUidLupd = rtUidLupd;
	}

	// Override Methods
	///////////////////
	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *
	 */
	@Override
	public int compareTo(CkCtRentalTable o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * 
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
