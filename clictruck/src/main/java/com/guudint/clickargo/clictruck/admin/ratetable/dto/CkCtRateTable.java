package com.guudint.clickargo.clictruck.admin.ratetable.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.guudint.clickargo.common.enums.JobActions;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.dto.MstCurrency;

public class CkCtRateTable extends AbstractDTO<CkCtRateTable, TCkCtRateTable> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 4761173427300510128L;

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

	private JobActions action;
	private String history;
	// Holder for the rate table state depending on the state of the trip rates
	// associated to it.
	private boolean hasNewTripRate;
	private boolean hasSubmitTripRate;
	private boolean hasVerifyTripRate;

	// Holder for the status to be displayed in SP listing
	// (if has rate table has submitted trip rate, display as NEW
	// if has rate table has verified display as VERIFIED
	private String tempStatus;

	// Holder for remarks
	private String actionRemarks;

	// Constructors
	///////////////
	public CkCtRateTable() {
	}

	/**
	 * @param entity
	 */
	public CkCtRateTable(TCkCtRateTable entity) {
		super(entity);
	}

	/**
	 * @param rtId
	 * @param tCkCtMstVehType
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
	public CkCtRateTable(String rtId, CoreAccn tCoreAccnByRtCompany, CoreAccn tCoreAccnByRtCoFf,
			MstCurrency tMstCurrency, String rtName, String rtDescription, Date rtDtStart, Date rtDtEnd,
			String rtRemarks, Character rtStatus, Date rtDtCreate, String rtUidCreate, Date rtDtLupd,
			String rtUidLupd) {
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
	public int compareTo(CkCtRateTable o) {
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

	public JobActions getAction() {
		return action;
	}

	public void setAction(JobActions action) {
		this.action = action;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public boolean isHasNewTripRate() {
		return hasNewTripRate;
	}

	public void setHasNewTripRate(boolean hasNewTripRate) {
		this.hasNewTripRate = hasNewTripRate;
	}

	public boolean isHasSubmitTripRate() {
		return hasSubmitTripRate;
	}

	public void setHasSubmitTripRate(boolean hasSubmitTripRate) {
		this.hasSubmitTripRate = hasSubmitTripRate;
	}

	public boolean isHasVerifyTripRate() {
		return hasVerifyTripRate;
	}

	public void setHasVerifyTripRate(boolean hasVerifyTripRate) {
		this.hasVerifyTripRate = hasVerifyTripRate;
	}

	/**
	 * @return the tempStatus
	 */
	public String getTempStatus() {
		return tempStatus;
	}

	/**
	 * @param tempStatus the tempStatus to set
	 */
	public void setTempStatus(String tempStatus) {
		this.tempStatus = tempStatus;
	}

	/**
	 * @return the actionRemarks
	 */
	public String getActionRemarks() {
		return actionRemarks;
	}

	/**
	 * @param actionRemarks the actionRemarks to set
	 */
	public void setActionRemarks(String actionRemarks) {
		this.actionRemarks = actionRemarks;
	}

}
