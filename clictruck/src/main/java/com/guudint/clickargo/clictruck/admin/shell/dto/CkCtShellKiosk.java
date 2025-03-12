package com.guudint.clickargo.clictruck.admin.shell.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellKiosk;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtShellKiosk extends AbstractDTO<CkCtShellKiosk, TCkCtShellKiosk> {

	private static final long serialVersionUID = 7554539689397803888L;
	private String skId;
	private String skName;
	private Character skStatus;
	private Date skDtCreate;
	private String skUidCreate;
	private Date skDtLupd;
	private String skUidLupd;

	public CkCtShellKiosk() {
	}

	public CkCtShellKiosk(TCkCtShellKiosk entity) {
		super(entity);
	}

	public CkCtShellKiosk(String skId, String skName, Character skStatus, Date skDtCreate, String skUidCreate,
			Date skDtLupd, String skUidLupd) {
		this.skId = skId;
		this.skName = skName;
		this.skStatus = skStatus;
		this.skDtCreate = skDtCreate;
		this.skUidCreate = skUidCreate;
		this.skDtLupd = skDtLupd;
		this.skUidLupd = skUidLupd;
	}

	/**
	 * @return the skId
	 */
	public String getSkId() {
		return skId;
	}

	/**
	 * @param skId the skId to set
	 */
	public void setSkId(String skId) {
		this.skId = skId;
	}

	/**
	 * @return the skName
	 */
	public String getSkName() {
		return skName;
	}

	/**
	 * @param skName the skName to set
	 */
	public void setSkName(String skName) {
		this.skName = skName;
	}

	/**
	 * @return the skStatus
	 */
	public Character getSkStatus() {
		return skStatus;
	}

	/**
	 * @param skStatus the skStatus to set
	 */
	public void setSkStatus(Character skStatus) {
		this.skStatus = skStatus;
	}

	/**
	 * @return the skDtCreate
	 */
	public Date getSkDtCreate() {
		return skDtCreate;
	}

	/**
	 * @param skDtCreate the skDtCreate to set
	 */
	public void setSkDtCreate(Date skDtCreate) {
		this.skDtCreate = skDtCreate;
	}

	/**
	 * @return the skUidCreate
	 */
	public String getSkUidCreate() {
		return skUidCreate;
	}

	/**
	 * @param skUidCreate the skUidCreate to set
	 */
	public void setSkUidCreate(String skUidCreate) {
		this.skUidCreate = skUidCreate;
	}

	/**
	 * @return the skDtLupd
	 */
	public Date getSkDtLupd() {
		return skDtLupd;
	}

	/**
	 * @param skDtLupd the skDtLupd to set
	 */
	public void setSkDtLupd(Date skDtLupd) {
		this.skDtLupd = skDtLupd;
	}

	/**
	 * @return the skUidLupd
	 */
	public String getSkUidLupd() {
		return skUidLupd;
	}

	/**
	 * @param skUidLupd the skUidLupd to set
	 */
	public void setSkUidLupd(String skUidLupd) {
		this.skUidLupd = skUidLupd;
	}

	@Override
	public int compareTo(CkCtShellKiosk o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
