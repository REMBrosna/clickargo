package com.guudint.clickargo.clictruck.finacing.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstDebitNoteState;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtDebitNote extends AbstractDTO<CkCtDebitNote, TCkCtDebitNote> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 7584261726568710011L;

	public static final String PREFIX_ID = "DN";
	public static final String DEFAULT_CURRENCY = "IDR";
	public static final String DEFAULT_DESC = "Debit Note for Job %s";

	public static enum DebitNoteStates {

		NEW, PAID, TERMINATED
	}

	// Attributes
	/////////////
	private String dnId;
	private CkCtMstDebitNoteState TCkCtMstDebitNoteState;
	private CoreAccn TCoreAccnByDnTo;
	private CoreAccn TCoreAccnByDnFrom;
	private String dnJobId;
	private String dnNo;
	private Date dnDtIssue;
	private Date dnDtDue;
	private Date dnDtPaid;
	private String dnName;
	private String dnLoc;
	private String dnPaymentRef;
	private String dnPaymentTxnRef;
	private Date dnDtPayment;
	private Short dnAgeBand;
	private BigDecimal dnAmt;
	private BigDecimal dnVat;
	private BigDecimal dnStampDuty;
	private BigDecimal dnTotal;
	private Character dnStatus;
	private Date dnDtCreate;
	private String dnUidCreate;
	private Date dnDtLupd;
	private String dnUidLupd;

	// Constructors
	///////////////
	public CkCtDebitNote() {
	}

	public CkCtDebitNote(TCkCtDebitNote entity) {
		super(entity);
	}

	/**
	 * 
	 * @param dnId
	 * @param tCkCtMstDebitNoteState
	 * @param tCoreAccnByDnTo
	 * @param tCoreAccnByDnFrom
	 * @param dnJobId
	 * @param dnNo
	 * @param dnDtIssue
	 * @param dnDtDue
	 * @param dnDtPaid
	 * @param dnPaymentRef
	 * @param dnPaymentTxnRef
	 * @param dnDtPayment
	 * @param dnAgeBand
	 * @param dnStatus
	 * @param dnDtCreate
	 * @param dnUidCreate
	 * @param dnDtLupd
	 * @param dnUidLupd
	 */
	public CkCtDebitNote(String dnId, CkCtMstDebitNoteState tCkCtMstDebitNoteState, CoreAccn tCoreAccnByDnTo,
			CoreAccn tCoreAccnByDnFrom, String dnJobId, String dnNo, Date dnDtIssue, Date dnDtDue, Date dnDtPaid,
			String dnPaymentRef, String dnPaymentTxnRef, Date dnDtPayment, Short dnAgeBand, Character dnStatus,
			Date dnDtCreate, String dnUidCreate, Date dnDtLupd, String dnUidLupd) {
		super();
		this.dnId = dnId;
		TCkCtMstDebitNoteState = tCkCtMstDebitNoteState;
		TCoreAccnByDnTo = tCoreAccnByDnTo;
		TCoreAccnByDnFrom = tCoreAccnByDnFrom;
		this.dnJobId = dnJobId;
		this.dnNo = dnNo;
		this.dnDtIssue = dnDtIssue;
		this.dnDtDue = dnDtDue;
		this.dnDtPaid = dnDtPaid;
		this.dnPaymentRef = dnPaymentRef;
		this.dnPaymentTxnRef = dnPaymentTxnRef;
		this.dnDtPayment = dnDtPayment;
		this.dnAgeBand = dnAgeBand;
		this.dnStatus = dnStatus;
		this.dnDtCreate = dnDtCreate;
		this.dnUidCreate = dnUidCreate;
		this.dnDtLupd = dnDtLupd;
		this.dnUidLupd = dnUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the dnId
	 */
	public String getDnId() {
		return dnId;
	}

	/**
	 * @param dnId the dnId to set
	 */
	public void setDnId(String dnId) {
		this.dnId = dnId;
	}

	/**
	 * @return the tCkCtMstDebitNoteState
	 */
	public CkCtMstDebitNoteState getTCkCtMstDebitNoteState() {
		return TCkCtMstDebitNoteState;
	}

	/**
	 * @param tCkCtMstDebitNoteState the tCkCtMstDebitNoteState to set
	 */
	public void setTCkCtMstDebitNoteState(CkCtMstDebitNoteState tCkCtMstDebitNoteState) {
		TCkCtMstDebitNoteState = tCkCtMstDebitNoteState;
	}

	/**
	 * @return the tCoreAccnByDnTo
	 */
	public CoreAccn getTCoreAccnByDnTo() {
		return TCoreAccnByDnTo;
	}

	/**
	 * @param tCoreAccnByDnTo the tCoreAccnByDnTo to set
	 */
	public void setTCoreAccnByDnTo(CoreAccn tCoreAccnByDnTo) {
		TCoreAccnByDnTo = tCoreAccnByDnTo;
	}

	/**
	 * @return the tCoreAccnByDnFrom
	 */
	public CoreAccn getTCoreAccnByDnFrom() {
		return TCoreAccnByDnFrom;
	}

	/**
	 * @param tCoreAccnByDnFrom the tCoreAccnByDnFrom to set
	 */
	public void setTCoreAccnByDnFrom(CoreAccn tCoreAccnByDnFrom) {
		TCoreAccnByDnFrom = tCoreAccnByDnFrom;
	}

	/**
	 * @return the dnJobId
	 */
	public String getDnJobId() {
		return dnJobId;
	}

	/**
	 * @param dnJobId the dnJobId to set
	 */
	public void setDnJobId(String dnJobId) {
		this.dnJobId = dnJobId;
	}

	/**
	 * @return the dnNo
	 */
	public String getDnNo() {
		return dnNo;
	}

	/**
	 * @param dnNo the dnNo to set
	 */
	public void setDnNo(String dnNo) {
		this.dnNo = dnNo;
	}

	/**
	 * @return the dnDtIssue
	 */
	public Date getDnDtIssue() {
		return dnDtIssue;
	}

	/**
	 * @param dnDtIssue the dnDtIssue to set
	 */
	public void setDnDtIssue(Date dnDtIssue) {
		this.dnDtIssue = dnDtIssue;
	}

	/**
	 * @return the dnPaymentRef
	 */
	public String getDnPaymentRef() {
		return dnPaymentRef;
	}

	/**
	 * @param dnPaymentRef the dnPaymentRef to set
	 */
	public void setDnPaymentRef(String dnPaymentRef) {
		this.dnPaymentRef = dnPaymentRef;
	}

	/**
	 * @return the dnPaymentTxnRef
	 */
	public String getDnPaymentTxnRef() {
		return dnPaymentTxnRef;
	}

	/**
	 * @param dnPaymentTxnRef the dnPaymentTxnRef to set
	 */
	public void setDnPaymentTxnRef(String dnPaymentTxnRef) {
		this.dnPaymentTxnRef = dnPaymentTxnRef;
	}

	/**
	 * @return the dnDtPayment
	 */
	public Date getDnDtPayment() {
		return dnDtPayment;
	}

	/**
	 * @param dnDtPayment the dnDtPayment to set
	 */
	public void setDnDtPayment(Date dnDtPayment) {
		this.dnDtPayment = dnDtPayment;
	}

	/**
	 * @return the dnAgeBand
	 */
	public Short getDnAgeBand() {
		return dnAgeBand;
	}

	/**
	 * @param dnAgeBand the dnAgeBand to set
	 */
	public void setDnAgeBand(Short dnAgeBand) {
		this.dnAgeBand = dnAgeBand;
	}

	/**
	 * @return the dnStatus
	 */
	public Character getDnStatus() {
		return dnStatus;
	}

	/**
	 * @param dnStatus the dnStatus to set
	 */
	public void setDnStatus(Character dnStatus) {
		this.dnStatus = dnStatus;
	}

	/**
	 * @return the dnDtCreate
	 */
	public Date getDnDtCreate() {
		return dnDtCreate;
	}

	/**
	 * @param dnDtCreate the dnDtCreate to set
	 */
	public void setDnDtCreate(Date dnDtCreate) {
		this.dnDtCreate = dnDtCreate;
	}

	/**
	 * @return the dnUidCreate
	 */
	public String getDnUidCreate() {
		return dnUidCreate;
	}

	/**
	 * @param dnUidCreate the dnUidCreate to set
	 */
	public void setDnUidCreate(String dnUidCreate) {
		this.dnUidCreate = dnUidCreate;
	}

	/**
	 * @return the dnDtLupd
	 */
	public Date getDnDtLupd() {
		return dnDtLupd;
	}

	/**
	 * @param dnDtLupd the dnDtLupd to set
	 */
	public void setDnDtLupd(Date dnDtLupd) {
		this.dnDtLupd = dnDtLupd;
	}

	/**
	 * @return the dnUidLupd
	 */
	public String getDnUidLupd() {
		return dnUidLupd;
	}

	/**
	 * @param dnUidLupd the dnUidLupd to set
	 */
	public void setDnUidLupd(String dnUidLupd) {
		this.dnUidLupd = dnUidLupd;
	}

	/**
	 * @return the dnDtDue
	 */
	public Date getDnDtDue() {
		return dnDtDue;
	}

	/**
	 * @param dnDtDue the dnDtDue to set
	 */
	public void setDnDtDue(Date dnDtDue) {
		this.dnDtDue = dnDtDue;
	}

	/**
	 * @return the dnDtPaid
	 */
	public Date getDnDtPaid() {
		return dnDtPaid;
	}

	/**
	 * @param dnDtPaid the dnDtPaid to set
	 */
	public void setDnDtPaid(Date dnDtPaid) {
		this.dnDtPaid = dnDtPaid;
	}
	
	public String getDnName() {
		return dnName;
	}

	public void setDnName(String dnName) {
		this.dnName = dnName;
	}

	public String getDnLoc() {
		return dnLoc;
	}

	public void setDnLoc(String dnLoc) {
		this.dnLoc = dnLoc;
	}

	public BigDecimal getDnAmt() {
		return dnAmt;
	}

	public void setDnAmt(BigDecimal dnAmt) {
		this.dnAmt = dnAmt;
	}

	public BigDecimal getDnVat() {
		return dnVat;
	}

	public void setDnVat(BigDecimal dnVat) {
		this.dnVat = dnVat;
	}

	public BigDecimal getDnStampDuty() {
		return dnStampDuty;
	}

	public void setDnStampDuty(BigDecimal dnStampDuty) {
		this.dnStampDuty = dnStampDuty;
	}

	public BigDecimal getDnTotal() {
		return dnTotal;
	}

	public void setDnTotal(BigDecimal dnTotal) {
		this.dnTotal = dnTotal;
	}

	// Override Methods
	///////////////////
	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 */
	@Override
	public int compareTo(CkCtDebitNote o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.COAbstractEntity#init()
	 * 
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
