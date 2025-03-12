package com.guudint.clickargo.clictruck.opm.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.opm.model.TCkOpm;
import com.guudint.clickargo.master.dto.CkMstCreditState;
import com.guudint.clickargo.master.dto.CkMstServiceType;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.dto.MstCurrency;

public class CkOpm extends AbstractDTO<CkOpm, TCkOpm> {

	// Static Attributes
	////////////////////

	private static final long serialVersionUID = -448042674366800940L;
	// Attributes
	/////////////
	private String opmId;
	private CkMstCreditState TCkMstCreditState;
	private CkMstServiceType TCkMstServiceType;
	private CoreAccn TCoreAccn;
	private MstCurrency TMstCurrency;
	private String opmFinancer;
	private BigDecimal opmAmt;

	private Date opmDtStart;
	private Date opmDtEnd;
	private String opmUsrVerify;
	private Date opmDtVerify;
	private String opmUsrApprove;
	private Date opmDtApprove;
	private String opmRemarks;

	private Character opmStatus;
	private Date opmDtCreate;
	private String opmUidCreate;
	private Date opmDtLupd;
	private String opmUidLupd;

	private CkOpmSummary opmSummary;

	public CkOpm() {
	}


	public CkOpm(TCkOpm entity) {
		super(entity);
	}

	public CkOpm(String opmId, CkMstCreditState TCkMstCreditState, CkMstServiceType TCkMstServiceType,
			CoreAccn TCoreAccn, MstCurrency TMstCurrency, String opmFinancer, BigDecimal opmAmt, Date opmDtStart,
			Date opmDtEnd, String opmUsrVerify, Date opmDtVerify, String opmUsrApprove, Date opmDtApprove,
			String opmRemarks, Character opmStatus, Date opmDtCreate, String opmUidCreate, Date opmDtLupd,
			String opmUidLupd) {
		this.opmId = opmId;
		this.TCkMstCreditState = TCkMstCreditState;
		this.TCkMstServiceType = TCkMstServiceType;
		this.TCoreAccn = TCoreAccn;
		this.TMstCurrency = TMstCurrency;
		this.opmFinancer = opmFinancer;
		this.opmAmt = opmAmt;
		this.opmDtStart = opmDtStart;
		this.opmDtEnd = opmDtEnd;
		this.opmUsrVerify = opmUsrVerify;
		this.opmDtVerify = opmDtVerify;
		this.opmUsrApprove = opmUsrApprove;
		this.opmDtApprove = opmDtApprove;
		this.opmRemarks = opmRemarks;
		this.opmStatus = opmStatus;
		this.opmDtCreate = opmDtCreate;
		this.opmUidCreate = opmUidCreate;
		this.opmDtLupd = opmDtLupd;
		this.opmUidLupd = opmUidLupd;
	}

	/**
	 * @return the opmId
	 */
	public String getOpmId() {
		return opmId;
	}

	/**
	 * @param opmId the opmId to set
	 */
	public void setOpmId(String opmId) {
		this.opmId = opmId;
	}

	/**
	 * @return the tCkMstCreditState
	 */
	public CkMstCreditState getTCkMstCreditState() {
		return TCkMstCreditState;
	}

	/**
	 * @param tCkMstCreditState the tCkMstCreditState to set
	 */
	public void setTCkMstCreditState(CkMstCreditState tCkMstCreditState) {
		TCkMstCreditState = tCkMstCreditState;
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
	 * @return the opmFinancer
	 */
	public String getOpmFinancer() {
		return opmFinancer;
	}

	/**
	 * @param opmFinancer the opmFinancer to set
	 */
	public void setOpmFinancer(String opmFinancer) {
		this.opmFinancer = opmFinancer;
	}

	/**
	 * @return the opmAmt
	 */
	public BigDecimal getOpmAmt() {
		return opmAmt;
	}

	/**
	 * @param opmAmt the opmAmt to set
	 */
	public void setOpmAmt(BigDecimal opmAmt) {
		this.opmAmt = opmAmt;
	}

	/**
	 * @return the opmDtStart
	 */
	public Date getOpmDtStart() {
		return opmDtStart;
	}

	/**
	 * @param opmDtStart the opmDtStart to set
	 */
	public void setOpmDtStart(Date opmDtStart) {
		this.opmDtStart = opmDtStart;
	}

	/**
	 * @return the opmDtEnd
	 */
	public Date getOpmDtEnd() {
		return opmDtEnd;
	}

	/**
	 * @param opmDtEnd the opmDtEnd to set
	 */
	public void setOpmDtEnd(Date opmDtEnd) {
		this.opmDtEnd = opmDtEnd;
	}

	/**
	 * @return the opmUsrVerify
	 */
	public String getOpmUsrVerify() {
		return opmUsrVerify;
	}

	/**
	 * @param opmUsrVerify the opmUsrVerify to set
	 */
	public void setOpmUsrVerify(String opmUsrVerify) {
		this.opmUsrVerify = opmUsrVerify;
	}

	/**
	 * @return the opmDtVerify
	 */
	public Date getOpmDtVerify() {
		return opmDtVerify;
	}

	/**
	 * @param opmDtVerify the opmDtVerify to set
	 */
	public void setOpmDtVerify(Date opmDtVerify) {
		this.opmDtVerify = opmDtVerify;
	}

	/**
	 * @return the opmUsrApprove
	 */
	public String getOpmUsrApprove() {
		return opmUsrApprove;
	}

	/**
	 * @param opmUsrApprove the opmUsrApprove to set
	 */
	public void setOpmUsrApprove(String opmUsrApprove) {
		this.opmUsrApprove = opmUsrApprove;
	}

	/**
	 * @return the opmDtApprove
	 */
	public Date getOpmDtApprove() {
		return opmDtApprove;
	}

	/**
	 * @param opmDtApprove the opmDtApprove to set
	 */
	public void setOpmDtApprove(Date opmDtApprove) {
		this.opmDtApprove = opmDtApprove;
	}

	/**
	 * @return the opmRemarks
	 */
	public String getOpmRemarks() {
		return opmRemarks;
	}

	/**
	 * @param opmRemarks the opmRemarks to set
	 */
	public void setOpmRemarks(String opmRemarks) {
		this.opmRemarks = opmRemarks;
	}

	/**
	 * @return the opmStatus
	 */
	public Character getOpmStatus() {
		return opmStatus;
	}

	/**
	 * @param opmStatus the opmStatus to set
	 */
	public void setOpmStatus(Character opmStatus) {
		this.opmStatus = opmStatus;
	}

	/**
	 * @return the opmDtCreate
	 */
	public Date getOpmDtCreate() {
		return opmDtCreate;
	}

	/**
	 * @param opmDtCreate the opmDtCreate to set
	 */
	public void setOpmDtCreate(Date opmDtCreate) {
		this.opmDtCreate = opmDtCreate;
	}

	/**
	 * @return the opmUidCreate
	 */
	public String getOpmUidCreate() {
		return opmUidCreate;
	}

	/**
	 * @param opmUidCreate the opmUidCreate to set
	 */
	public void setOpmUidCreate(String opmUidCreate) {
		this.opmUidCreate = opmUidCreate;
	}

	/**
	 * @return the opmDtLupd
	 */
	public Date getOpmDtLupd() {
		return opmDtLupd;
	}

	/**
	 * @param opmDtLupd the opmDtLupd to set
	 */
	public void setOpmDtLupd(Date opmDtLupd) {
		this.opmDtLupd = opmDtLupd;
	}

	/**
	 * @return the opmUidLupd
	 */
	public String getOpmUidLupd() {
		return opmUidLupd;
	}

	/**
	 * @param opmUidLupd the opmUidLupd to set
	 */
	public void setOpmUidLupd(String opmUidLupd) {
		this.opmUidLupd = opmUidLupd;
	}

	@Override
	public int compareTo(CkOpm o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the opmSummary
	 */
	public CkOpmSummary getOpmSummary() {
		return opmSummary;
	}

	/**
	 * @param opmSummary the opmSummary to set
	 */
	public void setOpmSummary(CkOpmSummary opmSummary) {
		this.opmSummary = opmSummary;
	}

}
