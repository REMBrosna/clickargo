package com.guudint.clickargo.clictruck.admin.shell.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellInvoice;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtShellInvoice extends AbstractDTO<CkCtShellInvoice, TCkCtShellInvoice> {

	private static final long serialVersionUID = 2161171149442572801L;
	private String invId;
	private TCoreAccn TCoreAccn;
	private String invNo;
	private Date invDt;
	private BigDecimal invAmt;
	private Date invPaymentDt;
	private BigDecimal invPaymentAmt;
	private BigDecimal invBalanceAmt;
	private Character invStatus;
	private Date invDtCreate;
	private String invUidCreate;
	private Date invDtLupd;
	private String invUidLupd;

	public CkCtShellInvoice() {
	}

	public CkCtShellInvoice(TCkCtShellInvoice entity) {
		super(entity);
	}

	public CkCtShellInvoice(String invId, TCoreAccn TCoreAccn, String invNo, Date invDt, BigDecimal invAmt,
			Date invPaymentDt, BigDecimal invPaymentAmt, BigDecimal invBalanceAmt, Character invStatus,
			Date invDtCreate, String invUidCreate, Date invDtLupd, String invUidLupd) {
		this.invId = invId;
		this.TCoreAccn = TCoreAccn;
		this.invNo = invNo;
		this.invDt = invDt;
		this.invAmt = invAmt;
		this.invPaymentDt = invPaymentDt;
		this.invPaymentAmt = invPaymentAmt;
		this.invBalanceAmt = invBalanceAmt;
		this.invStatus = invStatus;
		this.invDtCreate = invDtCreate;
		this.invUidCreate = invUidCreate;
		this.invDtLupd = invDtLupd;
		this.invUidLupd = invUidLupd;
	}

	/**
	 * @return the invId
	 */
	public String getInvId() {
		return invId;
	}

	/**
	 * @param invId the invId to set
	 */
	public void setInvId(String invId) {
		this.invId = invId;
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
	 * @return the invNo
	 */
	public String getInvNo() {
		return invNo;
	}

	/**
	 * @param invNo the invNo to set
	 */
	public void setInvNo(String invNo) {
		this.invNo = invNo;
	}

	/**
	 * @return the invDt
	 */
	public Date getInvDt() {
		return invDt;
	}

	/**
	 * @param invDt the invDt to set
	 */
	public void setInvDt(Date invDt) {
		this.invDt = invDt;
	}

	/**
	 * @return the invAmt
	 */
	public BigDecimal getInvAmt() {
		return invAmt;
	}

	/**
	 * @param invAmt the invAmt to set
	 */
	public void setInvAmt(BigDecimal invAmt) {
		this.invAmt = invAmt;
	}

	/**
	 * @return the invPaymentDt
	 */
	public Date getInvPaymentDt() {
		return invPaymentDt;
	}

	/**
	 * @param invPaymentDt the invPaymentDt to set
	 */
	public void setInvPaymentDt(Date invPaymentDt) {
		this.invPaymentDt = invPaymentDt;
	}

	/**
	 * @return the invPaymentAmt
	 */
	public BigDecimal getInvPaymentAmt() {
		return invPaymentAmt;
	}

	/**
	 * @param invPaymentAmt the invPaymentAmt to set
	 */
	public void setInvPaymentAmt(BigDecimal invPaymentAmt) {
		this.invPaymentAmt = invPaymentAmt;
	}

	/**
	 * @return the invBalanceAmt
	 */
	public BigDecimal getInvBalanceAmt() {
		return invBalanceAmt;
	}

	/**
	 * @param invBalanceAmt the invBalanceAmt to set
	 */
	public void setInvBalanceAmt(BigDecimal invBalanceAmt) {
		this.invBalanceAmt = invBalanceAmt;
	}

	/**
	 * @return the invStatus
	 */
	public Character getInvStatus() {
		return invStatus;
	}

	/**
	 * @param invStatus the invStatus to set
	 */
	public void setInvStatus(Character invStatus) {
		this.invStatus = invStatus;
	}

	/**
	 * @return the invDtCreate
	 */
	public Date getInvDtCreate() {
		return invDtCreate;
	}

	/**
	 * @param invDtCreate the invDtCreate to set
	 */
	public void setInvDtCreate(Date invDtCreate) {
		this.invDtCreate = invDtCreate;
	}

	/**
	 * @return the invUidCreate
	 */
	public String getInvUidCreate() {
		return invUidCreate;
	}

	/**
	 * @param invUidCreate the invUidCreate to set
	 */
	public void setInvUidCreate(String invUidCreate) {
		this.invUidCreate = invUidCreate;
	}

	/**
	 * @return the invDtLupd
	 */
	public Date getInvDtLupd() {
		return invDtLupd;
	}

	/**
	 * @param invDtLupd the invDtLupd to set
	 */
	public void setInvDtLupd(Date invDtLupd) {
		this.invDtLupd = invDtLupd;
	}

	/**
	 * @return the invUidLupd
	 */
	public String getInvUidLupd() {
		return invUidLupd;
	}

	/**
	 * @param invUidLupd the invUidLupd to set
	 */
	public void setInvUidLupd(String invUidLupd) {
		this.invUidLupd = invUidLupd;
	}

	@Override
	public int compareTo(CkCtShellInvoice o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
