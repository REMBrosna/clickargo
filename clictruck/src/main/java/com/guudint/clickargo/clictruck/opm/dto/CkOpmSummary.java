package com.guudint.clickargo.clictruck.opm.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.opm.model.TCkOpmSummary;
import com.guudint.clickargo.master.dto.CkMstServiceType;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.dto.MstCurrency;

public class CkOpmSummary extends AbstractDTO<CkOpmSummary, TCkOpmSummary> {

	// Static Attributes
	////////////////////

	private static final long serialVersionUID = -6992288173881355801L;
	// Attributes
	/////////////
	private String opmsId;
	private CkMstServiceType TCkMstServiceType;
	private CoreAccn TCoreAccn;
	private MstCurrency TMstCurrency;
	private BigDecimal opmsAmt;
	private BigDecimal opmsReserve;
	private BigDecimal opmsUtilized;
	private BigDecimal opmsBalance;
	private Character opmsStatus;
	private Date opmsDtCreate;
	private String opmsUidCreate;
	private Date opmsDtLupd;
	private String opmsUidLupd;
	private String opmsCredit;

	public CkOpmSummary() {
	}

	public CkOpmSummary(String opmsId) {
		this.opmsId = opmsId;
	}

	public CkOpmSummary(TCkOpmSummary entity) {
		super(entity);
	}

	@Override
	public int compareTo(CkOpmSummary o) {
		return 0;
	}

	@Override
	public void init() {
	}

	/**
	 * @return the opmsId
	 */
	public String getOpmsId() {
		return opmsId;
	}

	/**
	 * @param opmsId the opmsId to set
	 */
	public void setOpmsId(String opmsId) {
		this.opmsId = opmsId;
	}

	/**
	 * @return the tCkMstServiceType
	 */
	public CkMstServiceType getTCkMstServiceType() {
		return TCkMstServiceType;
	}

	/**
	 * @param tCkMstServiceType the tCkMstServiceType to set
	 */
	public void setTCkMstServiceType(CkMstServiceType tCkMstServiceType) {
		TCkMstServiceType = tCkMstServiceType;
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
	 * @return the opmsAmt
	 */
	public BigDecimal getOpmsAmt() {
		return opmsAmt;
	}

	/**
	 * @param opmsAmt the opmsAmt to set
	 */
	public void setOpmsAmt(BigDecimal opmsAmt) {
		this.opmsAmt = opmsAmt;
	}

	/**
	 * @return the opmsReserve
	 */
	public BigDecimal getOpmsReserve() {
		return opmsReserve;
	}

	/**
	 * @param opmsReserve the opmsReserve to set
	 */
	public void setOpmsReserve(BigDecimal opmsReserve) {
		this.opmsReserve = opmsReserve;
	}

	/**
	 * @return the opmsUtilized
	 */
	public BigDecimal getOpmsUtilized() {
		return opmsUtilized;
	}

	/**
	 * @param opmsUtilized the opmsUtilized to set
	 */
	public void setOpmsUtilized(BigDecimal opmsUtilized) {
		this.opmsUtilized = opmsUtilized;
	}

	/**
	 * @return the opmsBalance
	 */
	public BigDecimal getOpmsBalance() {
		return opmsBalance;
	}

	/**
	 * @param opmsBalance the opmsBalance to set
	 */
	public void setOpmsBalance(BigDecimal opmsBalance) {
		this.opmsBalance = opmsBalance;
	}

	/**
	 * @return the opmsStatus
	 */
	public Character getOpmsStatus() {
		return opmsStatus;
	}

	/**
	 * @param opmsStatus the opmsStatus to set
	 */
	public void setOpmsStatus(Character opmsStatus) {
		this.opmsStatus = opmsStatus;
	}

	/**
	 * @return the opmsDtCreate
	 */
	public Date getOpmsDtCreate() {
		return opmsDtCreate;
	}

	/**
	 * @param opmsDtCreate the opmsDtCreate to set
	 */
	public void setOpmsDtCreate(Date opmsDtCreate) {
		this.opmsDtCreate = opmsDtCreate;
	}

	/**
	 * @return the opmsUidCreate
	 */
	public String getOpmsUidCreate() {
		return opmsUidCreate;
	}

	/**
	 * @param opmsUidCreate the opmsUidCreate to set
	 */
	public void setOpmsUidCreate(String opmsUidCreate) {
		this.opmsUidCreate = opmsUidCreate;
	}

	/**
	 * @return the opmsDtLupd
	 */
	public Date getOpmsDtLupd() {
		return opmsDtLupd;
	}

	/**
	 * @param opmsDtLupd the opmsDtLupd to set
	 */
	public void setOpmsDtLupd(Date opmsDtLupd) {
		this.opmsDtLupd = opmsDtLupd;
	}

	/**
	 * @return the opmsUidLupd
	 */
	public String getOpmsUidLupd() {
		return opmsUidLupd;
	}

	/**
	 * @param opmsUidLupd the opmsUidLupd to set
	 */
	public void setOpmsUidLupd(String opmsUidLupd) {
		this.opmsUidLupd = opmsUidLupd;
	}

	/**
	 * @return the opmsCredit
	 */
	public String getOpmsCredit() {
		return opmsCredit;
	}

	/**
	 * @param opmsCredit the opmsCredit to set
	 */
	public void setOpmsCredit(String opmsCredit) {
		this.opmsCredit = opmsCredit;
	}

}
