package com.guudint.clickargo.clictruck.admin.shell.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCard;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtShellCard extends AbstractDTO<CkCtShellCard, TCkCtShellCard> {

	private static final long serialVersionUID = -1260988404015343326L;
	private String scId;
	private Date scDtExpiry;
	private Character scStatus;
	private Date scDtCreate;
	private String scUidCreate;
	private Date scDtLupd;
	private String scUidLupd;

	public CkCtShellCard() {
	}

	public CkCtShellCard(TCkCtShellCard entity) {
		super(entity);
	}

	public CkCtShellCard(String scId, Date scDtExpiry, Character scStatus, Date scDtCreate, String scUidCreate,
			Date scDtLupd, String scUidLupd) {
		this.scId = scId;
		this.scDtExpiry = scDtExpiry;
		this.scStatus = scStatus;
		this.scDtCreate = scDtCreate;
		this.scUidCreate = scUidCreate;
		this.scDtLupd = scDtLupd;
		this.scUidLupd = scUidLupd;
	}

	/**
	 * @return the scId
	 */
	public String getScId() {
		return scId;
	}

	/**
	 * @param scId the scId to set
	 */
	public void setScId(String scId) {
		this.scId = scId;
	}

	/**
	 * @return the scDtExpiry
	 */
	public Date getScDtExpiry() {
		return scDtExpiry;
	}

	/**
	 * @param scDtExpiry the scDtExpiry to set
	 */
	public void setScDtExpiry(Date scDtExpiry) {
		this.scDtExpiry = scDtExpiry;
	}

	/**
	 * @return the scStatus
	 */
	public Character getScStatus() {
		return scStatus;
	}

	/**
	 * @param scStatus the scStatus to set
	 */
	public void setScStatus(Character scStatus) {
		this.scStatus = scStatus;
	}

	/**
	 * @return the scDtCreate
	 */
	public Date getScDtCreate() {
		return scDtCreate;
	}

	/**
	 * @param scDtCreate the scDtCreate to set
	 */
	public void setScDtCreate(Date scDtCreate) {
		this.scDtCreate = scDtCreate;
	}

	/**
	 * @return the scUidCreate
	 */
	public String getScUidCreate() {
		return scUidCreate;
	}

	/**
	 * @param scUidCreate the scUidCreate to set
	 */
	public void setScUidCreate(String scUidCreate) {
		this.scUidCreate = scUidCreate;
	}

	/**
	 * @return the scDtLupd
	 */
	public Date getScDtLupd() {
		return scDtLupd;
	}

	/**
	 * @param scDtLupd the scDtLupd to set
	 */
	public void setScDtLupd(Date scDtLupd) {
		this.scDtLupd = scDtLupd;
	}

	/**
	 * @return the scUidLupd
	 */
	public String getScUidLupd() {
		return scUidLupd;
	}

	/**
	 * @param scUidLupd the scUidLupd to set
	 */
	public void setScUidLupd(String scUidLupd) {
		this.scUidLupd = scUidLupd;
	}

	@Override
	public int compareTo(CkCtShellCard o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
