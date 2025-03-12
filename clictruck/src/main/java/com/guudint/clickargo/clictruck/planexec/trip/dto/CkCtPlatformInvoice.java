package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstToInvoiceState;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtPlatformInvoice extends AbstractDTO<CkCtPlatformInvoice, TCkCtPlatformInvoice> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 2134305902981083238L;

	public static final String PREFIX_ID = "PFINV";
	public static final String DEFAULT_CURRENCY = "IDR";
	public static final String DEFAULT_DESC = "Platform Fee for Job %s";

	public static enum PlatformInvoiceStates {

		NEW, PAID
	}

	// Attributes
	/////////////
	private String invId;
	private CkCtMstToInvoiceState TCkCtMstToInvoiceState;
	private CkCtTrip TCkCtTrip;
	private CoreAccn TCoreAccnByInvTo;
	private CoreAccn TCoreAccnByInvFrom;
	private String invJobId;
	private String invNo;
	private String invPaymentTxnRef;
	private BigDecimal invAmt;
	private BigDecimal invVat;
	private BigDecimal invStampDuty;
	private BigDecimal invTotal;
	private Date invDtIssue;
	private Date invDtDue;
	private Date invDtPaid;
	private String invName;
	private String invLoc;
	private String invInvocierComment;
	private String invInvocieeRemarks;
	private Character invStatus;
	private Date invDtCreate;
	private String invUidCreate;
	private Date invDtLupd;
	private String invUidLupd;

	public CkCtPlatformInvoice() {
	}

	public CkCtPlatformInvoice(TCkCtPlatformInvoice entity) {
		super(entity);
	}

	/**
	 * 
	 * @param invId
	 * @param tCkCtMstToInvoiceState
	 * @param tCkCtTrip
	 * @param tCoreAccnByInvTo
	 * @param tCoreAccnByInvFrom
	 * @param invJobId
	 * @param invNo
	 * @param invPaymentTxnRef
	 * @param invDtIssue
	 * @param invDtDue
	 * @param invDtPaid
	 * @param invName
	 * @param invLoc
	 * @param invInvocierComment
	 * @param invInvocieeRemarks
	 * @param invStatus
	 * @param invDtCreate
	 * @param invUidCreate
	 * @param invDtLupd
	 * @param invUidLupd
	 */
	public CkCtPlatformInvoice(String invId, CkCtMstToInvoiceState tCkCtMstToInvoiceState, CkCtTrip tCkCtTrip,
			CoreAccn tCoreAccnByInvTo, CoreAccn tCoreAccnByInvFrom, String invJobId, String invNo,
			String invPaymentTxnRef, Date invDtIssue, Date invDtDue, Date invDtPaid, String invName, String invLoc,
			String invInvocierComment, String invInvocieeRemarks, Character invStatus, Date invDtCreate,
			String invUidCreate, Date invDtLupd, String invUidLupd) {
		super();
		this.invId = invId;
		TCkCtMstToInvoiceState = tCkCtMstToInvoiceState;
		TCkCtTrip = tCkCtTrip;
		TCoreAccnByInvTo = tCoreAccnByInvTo;
		TCoreAccnByInvFrom = tCoreAccnByInvFrom;
		this.invJobId = invJobId;
		this.invNo = invNo;
		this.invPaymentTxnRef = invPaymentTxnRef;
		this.invDtIssue = invDtIssue;
		this.invDtDue = invDtDue;
		this.invDtPaid = invDtPaid;
		this.invName = invName;
		this.invLoc = invLoc;
		this.invInvocierComment = invInvocierComment;
		this.invInvocieeRemarks = invInvocieeRemarks;
		this.invStatus = invStatus;
		this.invDtCreate = invDtCreate;
		this.invUidCreate = invUidCreate;
		this.invDtLupd = invDtLupd;
		this.invUidLupd = invUidLupd;
	}

	// Properties
	/////////////
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
	 * @return the tCkCtMstToInvoiceState
	 */
	public CkCtMstToInvoiceState getTCkCtMstToInvoiceState() {
		return TCkCtMstToInvoiceState;
	}

	/**
	 * @param tCkCtMstToInvoiceState the tCkCtMstToInvoiceState to set
	 */
	public void setTCkCtMstToInvoiceState(CkCtMstToInvoiceState tCkCtMstToInvoiceState) {
		TCkCtMstToInvoiceState = tCkCtMstToInvoiceState;
	}

	/**
	 * @return the tCkCtTrip
	 */
	public CkCtTrip getTCkCtTrip() {
		return TCkCtTrip;
	}

	/**
	 * @param tCkCtTrip the tCkCtTrip to set
	 */
	public void setTCkCtTrip(CkCtTrip tCkCtTrip) {
		TCkCtTrip = tCkCtTrip;
	}

	/**
	 * @return the tCoreAccnByInvTo
	 */
	public CoreAccn getTCoreAccnByInvTo() {
		return TCoreAccnByInvTo;
	}

	/**
	 * @param tCoreAccnByInvTo the tCoreAccnByInvTo to set
	 */
	public void setTCoreAccnByInvTo(CoreAccn tCoreAccnByInvTo) {
		TCoreAccnByInvTo = tCoreAccnByInvTo;
	}

	/**
	 * @return the tCoreAccnByInvFrom
	 */
	public CoreAccn getTCoreAccnByInvFrom() {
		return TCoreAccnByInvFrom;
	}

	/**
	 * @param tCoreAccnByInvFrom the tCoreAccnByInvFrom to set
	 */
	public void setTCoreAccnByInvFrom(CoreAccn tCoreAccnByInvFrom) {
		TCoreAccnByInvFrom = tCoreAccnByInvFrom;
	}

	/**
	 * @return the invJobId
	 */
	public String getInvJobId() {
		return invJobId;
	}

	/**
	 * @param invJobId the invJobId to set
	 */
	public void setInvJobId(String invJobId) {
		this.invJobId = invJobId;
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
	 * @return the invPaymentTxnRef
	 */
	public String getInvPaymentTxnRef() {
		return invPaymentTxnRef;
	}

	/**
	 * @param invPaymentTxnRef the invPaymentTxnRef to set
	 */
	public void setInvPaymentTxnRef(String invPaymentTxnRef) {
		this.invPaymentTxnRef = invPaymentTxnRef;
	}

	/**
	 * @return the invDtIssue
	 */
	public Date getInvDtIssue() {
		return invDtIssue;
	}

	/**
	 * @param invDtIssue the invDtIssue to set
	 */
	public void setInvDtIssue(Date invDtIssue) {
		this.invDtIssue = invDtIssue;
	}

	/**
	 * @return the invDtDue
	 */
	public Date getInvDtDue() {
		return invDtDue;
	}

	/**
	 * @param invDtDue the invDtDue to set
	 */
	public void setInvDtDue(Date invDtDue) {
		this.invDtDue = invDtDue;
	}

	/**
	 * @return the invDtPaid
	 */
	public Date getInvDtPaid() {
		return invDtPaid;
	}

	/**
	 * @param invDtPaid the invDtPaid to set
	 */
	public void setInvDtPaid(Date invDtPaid) {
		this.invDtPaid = invDtPaid;
	}

	/**
	 * @return the invName
	 */
	public String getInvName() {
		return invName;
	}

	/**
	 * @param invName the invName to set
	 */
	public void setInvName(String invName) {
		this.invName = invName;
	}

	/**
	 * @return the invLoc
	 */
	public String getInvLoc() {
		return invLoc;
	}

	/**
	 * @param invLoc the invLoc to set
	 */
	public void setInvLoc(String invLoc) {
		this.invLoc = invLoc;
	}

	/**
	 * @return the invInvocierComment
	 */
	public String getInvInvocierComment() {
		return invInvocierComment;
	}

	/**
	 * @param invInvocierComment the invInvocierComment to set
	 */
	public void setInvInvocierComment(String invInvocierComment) {
		this.invInvocierComment = invInvocierComment;
	}

	/**
	 * @return the invInvocieeRemarks
	 */
	public String getInvInvocieeRemarks() {
		return invInvocieeRemarks;
	}

	/**
	 * @param invInvocieeRemarks the invInvocieeRemarks to set
	 */
	public void setInvInvocieeRemarks(String invInvocieeRemarks) {
		this.invInvocieeRemarks = invInvocieeRemarks;
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

	public BigDecimal getInvAmt() {
		return invAmt;
	}

	public void setInvAmt(BigDecimal invAmt) {
		this.invAmt = invAmt;
	}

	public BigDecimal getInvVat() {
		return invVat;
	}

	public void setInvVat(BigDecimal invVat) {
		this.invVat = invVat;
	}

	public BigDecimal getInvStampDuty() {
		return invStampDuty;
	}

	public void setInvStampDuty(BigDecimal invStampDuty) {
		this.invStampDuty = invStampDuty;
	}

	public BigDecimal getInvTotal() {
		return invTotal;
	}

	public void setInvTotal(BigDecimal invTotal) {
		this.invTotal = invTotal;
	}

	// Override Methods
	///////////////////
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public int compareTo(CkCtPlatformInvoice o) {
		// TODO Auto-generated method stub
		return 0;
	}

}