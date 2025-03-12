package com.guudint.clickargo.clictruck.common.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.common.model.TCkCtCo2x;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtCo2x extends AbstractDTO<CkCtCo2x, TCkCtCo2x> {

	private static final long serialVersionUID = 7424293006688508920L;
	private String co2xId;
	private CoreAccn TCoreAccn;
	private String co2xAccnName;
	private String co2xCoyId;
	private Date co2xDtExpiry;
	private String co2xUid;
	private String co2xPwd;
	private Character co2xStatus;
	private Date co2xDtCreate;
	private String co2xUidCreate;
	private Date co2xDtLupd;
	private String co2xUidLupd;

	private String history;

	// place holder for the ascent url for co2x sso
	private String ssoUrl;

	public CkCtCo2x() {
	}

	public CkCtCo2x(TCkCtCo2x entity) {
		super(entity);
	}

	/**
	 * @return the co2xId
	 */
	public String getCo2xId() {
		return co2xId;
	}

	/**
	 * @param co2xId the co2xId to set
	 */
	public void setCo2xId(String co2xId) {
		this.co2xId = co2xId;
	}

	/**
	 * @return the tCoreAccn
	 */
	public CoreAccn getTCoreAccn() {
		return TCoreAccn;
	}

	/**
	 * @param tCoreAccn the tCoreAccn to set
	 */
	public void setTCoreAccn(CoreAccn tCoreAccn) {
		TCoreAccn = tCoreAccn;
	}

	/**
	 * @return the co2xAccnName
	 */
	public String getCo2xAccnName() {
		return co2xAccnName;
	}

	/**
	 * @param co2xAccnName the co2xAccnName to set
	 */
	public void setCo2xAccnName(String co2xAccnName) {
		this.co2xAccnName = co2xAccnName;
	}

	/**
	 * @return the co2xCoyId
	 */
	public String getCo2xCoyId() {
		return co2xCoyId;
	}

	/**
	 * @param co2xCoyId the co2xCoyId to set
	 */
	public void setCo2xCoyId(String co2xCoyId) {
		this.co2xCoyId = co2xCoyId;
	}

	/**
	 * @return the co2xDtExpiry
	 */
	public Date getCo2xDtExpiry() {
		return co2xDtExpiry;
	}

	/**
	 * @param co2xDtExpiry the co2xDtExpiry to set
	 */
	public void setCo2xDtExpiry(Date co2xDtExpiry) {
		this.co2xDtExpiry = co2xDtExpiry;
	}

	/**
	 * @return the co2xUid
	 */
	public String getCo2xUid() {
		return co2xUid;
	}

	/**
	 * @param co2xUid the co2xUid to set
	 */
	public void setCo2xUid(String co2xUid) {
		this.co2xUid = co2xUid;
	}

	/**
	 * @return the co2xPwd
	 */
	public String getCo2xPwd() {
		return co2xPwd;
	}

	/**
	 * @param co2xPwd the co2xPwd to set
	 */
	public void setCo2xPwd(String co2xPwd) {
		this.co2xPwd = co2xPwd;
	}

	/**
	 * @return the co2xStatus
	 */
	public Character getCo2xStatus() {
		return co2xStatus;
	}

	/**
	 * @param co2xStatus the co2xStatus to set
	 */
	public void setCo2xStatus(Character co2xStatus) {
		this.co2xStatus = co2xStatus;
	}

	/**
	 * @return the co2xDtCreate
	 */
	public Date getCo2xDtCreate() {
		return co2xDtCreate;
	}

	/**
	 * @param co2xDtCreate the co2xDtCreate to set
	 */
	public void setCo2xDtCreate(Date co2xDtCreate) {
		this.co2xDtCreate = co2xDtCreate;
	}

	/**
	 * @return the co2xUidCreate
	 */
	public String getCo2xUidCreate() {
		return co2xUidCreate;
	}

	/**
	 * @param co2xUidCreate the co2xUidCreate to set
	 */
	public void setCo2xUidCreate(String co2xUidCreate) {
		this.co2xUidCreate = co2xUidCreate;
	}

	/**
	 * @return the co2xDtLupd
	 */
	public Date getCo2xDtLupd() {
		return co2xDtLupd;
	}

	/**
	 * @param co2xDtLupd the co2xDtLupd to set
	 */
	public void setCo2xDtLupd(Date co2xDtLupd) {
		this.co2xDtLupd = co2xDtLupd;
	}

	/**
	 * @return the co2xUidLupd
	 */
	public String getCo2xUidLupd() {
		return co2xUidLupd;
	}

	/**
	 * @param co2xUidLupd the co2xUidLupd to set
	 */
	public void setCo2xUidLupd(String co2xUidLupd) {
		this.co2xUidLupd = co2xUidLupd;
	}

	@Override
	public int compareTo(CkCtCo2x o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the history
	 */
	public String getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(String history) {
		this.history = history;
	}

	/**
	 * @return the ssoUrl
	 */
	public String getSsoUrl() {
		return ssoUrl;
	}

	/**
	 * @param ssoUrl the ssoUrl to set
	 */
	public void setSsoUrl(String ssoUrl) {
		this.ssoUrl = ssoUrl;
	}

}
