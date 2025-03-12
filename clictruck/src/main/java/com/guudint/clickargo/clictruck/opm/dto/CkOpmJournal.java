package com.guudint.clickargo.clictruck.opm.dto;
// Generated 14 Mar 2024, 9:10:12 am by Hibernate Tools 4.3.6.Final

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.opm.model.TCkOpmJournal;
import com.guudint.clickargo.master.dto.CkMstJournalTxnType;
import com.guudint.clickargo.master.dto.CkMstServiceType;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.dto.MstCurrency;

public class CkOpmJournal extends AbstractDTO<CkOpmJournal, TCkOpmJournal> {

	// Static Attributes
	////////////////////

	private static final long serialVersionUID = 8841125983854122344L;
	// Attributes
	/////////////
	private String opmjId;
	private CkMstJournalTxnType TCkMstJournalTxnType;
	private CkMstServiceType TCkMstServiceType;
	private CoreAccn TCoreAccn;
	private MstCurrency TMstCurrency;
	private String opmjTxnRef;
	private BigDecimal opmjReserve;
	private BigDecimal opmjUtilized;
	private Character opmjStatus;
	private Date opmjDtCreate;
	private String opmjUidCreate;
	private Date opmjDtLupd;
	private String opmjUidLupd;

	public CkOpmJournal() {
	}

	public CkOpmJournal(String opmjId) {
		this.opmjId = opmjId;
	}

	public CkOpmJournal(TCkOpmJournal entity) {
		super(entity);
	}

	@Override
	public int compareTo(CkOpmJournal o) {
		return 0;
	}

	@Override
	public void init() {
	}

	/**
	 * @return the opmjId
	 */
	public String getOpmjId() {
		return opmjId;
	}

	/**
	 * @param opmjId the opmjId to set
	 */
	public void setOpmjId(String opmjId) {
		this.opmjId = opmjId;
	}

	/**
	 * @return the tCkMstJournalTxnType
	 */
	public CkMstJournalTxnType getTCkMstJournalTxnType() {
		return TCkMstJournalTxnType;
	}

	/**
	 * @param tCkMstJournalTxnType the tCkMstJournalTxnType to set
	 */
	public void setTCkMstJournalTxnType(CkMstJournalTxnType tCkMstJournalTxnType) {
		TCkMstJournalTxnType = tCkMstJournalTxnType;
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
	 * @return the opmjTxnRef
	 */
	public String getOpmjTxnRef() {
		return opmjTxnRef;
	}

	/**
	 * @param opmjTxnRef the opmjTxnRef to set
	 */
	public void setOpmjTxnRef(String opmjTxnRef) {
		this.opmjTxnRef = opmjTxnRef;
	}

	/**
	 * @return the opmjReserve
	 */
	public BigDecimal getOpmjReserve() {
		return opmjReserve;
	}

	/**
	 * @param opmjReserve the opmjReserve to set
	 */
	public void setOpmjReserve(BigDecimal opmjReserve) {
		this.opmjReserve = opmjReserve;
	}

	/**
	 * @return the opmjUtilized
	 */
	public BigDecimal getOpmjUtilized() {
		return opmjUtilized;
	}

	/**
	 * @param opmjUtilized the opmjUtilized to set
	 */
	public void setOpmjUtilized(BigDecimal opmjUtilized) {
		this.opmjUtilized = opmjUtilized;
	}

	/**
	 * @return the opmjStatus
	 */
	public Character getOpmjStatus() {
		return opmjStatus;
	}

	/**
	 * @param opmjStatus the opmjStatus to set
	 */
	public void setOpmjStatus(Character opmjStatus) {
		this.opmjStatus = opmjStatus;
	}

	/**
	 * @return the opmjDtCreate
	 */
	public Date getOpmjDtCreate() {
		return opmjDtCreate;
	}

	/**
	 * @param opmjDtCreate the opmjDtCreate to set
	 */
	public void setOpmjDtCreate(Date opmjDtCreate) {
		this.opmjDtCreate = opmjDtCreate;
	}

	/**
	 * @return the opmjUidCreate
	 */
	public String getOpmjUidCreate() {
		return opmjUidCreate;
	}

	/**
	 * @param opmjUidCreate the opmjUidCreate to set
	 */
	public void setOpmjUidCreate(String opmjUidCreate) {
		this.opmjUidCreate = opmjUidCreate;
	}

	/**
	 * @return the opmjDtLupd
	 */
	public Date getOpmjDtLupd() {
		return opmjDtLupd;
	}

	/**
	 * @param opmjDtLupd the opmjDtLupd to set
	 */
	public void setOpmjDtLupd(Date opmjDtLupd) {
		this.opmjDtLupd = opmjDtLupd;
	}

	/**
	 * @return the opmjUidLupd
	 */
	public String getOpmjUidLupd() {
		return opmjUidLupd;
	}

	/**
	 * @param opmjUidLupd the opmjUidLupd to set
	 */
	public void setOpmjUidLupd(String opmjUidLupd) {
		this.opmjUidLupd = opmjUidLupd;
	}

}
