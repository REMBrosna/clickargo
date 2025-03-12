package com.guudint.clickargo.clictruck.admin.shell.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCardTruck;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtShellCardTruck extends AbstractDTO<CkCtShellCardTruck, TCkCtShellCardTruck> {

	private static final long serialVersionUID = -7949148570070890482L;
	private String ctId;
	private CkCtShellCard TCkCtShellCard;
	private TCkCtVeh TCkCtVeh;
	private TCoreAccn TCoreAccn;
	private Character ctStatus;
	private Date ctDtCreate;
	private String ctUidCreate;
	private Date ctDtLupd;
	private String ctUidLupd;

	private String history;

	public CkCtShellCardTruck() {
	}

	public CkCtShellCardTruck(TCkCtShellCardTruck entity) {
		super(entity);
	}

	public CkCtShellCardTruck(String ctId, CkCtShellCard TCkCtShellCard, com.guudint.clickargo.clictruck.common.model.TCkCtVeh TCkCtVeh, com.vcc.camelone.ccm.model.TCoreAccn TCoreAccn, Character ctStatus, Date ctDtCreate, String ctUidCreate, Date ctDtLupd, String ctUidLupd, String history) {
		this.ctId = ctId;
		this.TCkCtShellCard = TCkCtShellCard;
		this.TCkCtVeh = TCkCtVeh;
		this.TCoreAccn = TCoreAccn;
		this.ctStatus = ctStatus;
		this.ctDtCreate = ctDtCreate;
		this.ctUidCreate = ctUidCreate;
		this.ctDtLupd = ctDtLupd;
		this.ctUidLupd = ctUidLupd;
		this.history = history;
	}

	/**
	 * @return the ctId
	 */
	public String getCtId() {
		return ctId;
	}

	/**
	 * @param ctId the ctId to set
	 */
	public void setCtId(String ctId) {
		this.ctId = ctId;
	}

	/**
	 * @return the tCkCtShellCard
	 */
	public CkCtShellCard getTCkCtShellCard() {
		return TCkCtShellCard;
	}

	/**
	 * @param tCkCtShellCard the tCkCtShellCard to set
	 */
	public void setTCkCtShellCard(CkCtShellCard tCkCtShellCard) {
		TCkCtShellCard = tCkCtShellCard;
	}

	/**
	 * @return the tCkCtVeh
	 */
	public TCkCtVeh getTCkCtVeh() {
		return TCkCtVeh;
	}

	/**
	 * @param tCkCtVeh the tCkCtVeh to set
	 */
	public void setTCkCtVeh(TCkCtVeh tCkCtVeh) {
		TCkCtVeh = tCkCtVeh;
	}

	/**
	 * @return the tCoreAccn
	 */
	public TCoreAccn getTCoreAccn() {
		return TCoreAccn;
	}

	/**
	 * @param tCoreAccn the tCoreAccn to set
	 */
	public void setTCoreAccn(TCoreAccn tCoreAccn) {
		TCoreAccn = tCoreAccn;
	}

	/**
	 * @return the ctStatus
	 */
	public Character getCtStatus() {
		return ctStatus;
	}

	/**
	 * @param ctStatus the ctStatus to set
	 */
	public void setCtStatus(Character ctStatus) {
		this.ctStatus = ctStatus;
	}

	/**
	 * @return the ctDtCreate
	 */
	public Date getCtDtCreate() {
		return ctDtCreate;
	}

	/**
	 * @param ctDtCreate the ctDtCreate to set
	 */
	public void setCtDtCreate(Date ctDtCreate) {
		this.ctDtCreate = ctDtCreate;
	}

	/**
	 * @return the ctUidCreate
	 */
	public String getCtUidCreate() {
		return ctUidCreate;
	}

	/**
	 * @param ctUidCreate the ctUidCreate to set
	 */
	public void setCtUidCreate(String ctUidCreate) {
		this.ctUidCreate = ctUidCreate;
	}

	/**
	 * @return the ctDtLupd
	 */
	public Date getCtDtLupd() {
		return ctDtLupd;
	}

	/**
	 * @param ctDtLupd the ctDtLupd to set
	 */
	public void setCtDtLupd(Date ctDtLupd) {
		this.ctDtLupd = ctDtLupd;
	}

	/**
	 * @return the ctUidLupd
	 */
	public String getCtUidLupd() {
		return ctUidLupd;
	}

	/**
	 * @param ctUidLupd the ctUidLupd to set
	 */
	public void setCtUidLupd(String ctUidLupd) {
		this.ctUidLupd = ctUidLupd;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	@Override
	public int compareTo(CkCtShellCardTruck o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
