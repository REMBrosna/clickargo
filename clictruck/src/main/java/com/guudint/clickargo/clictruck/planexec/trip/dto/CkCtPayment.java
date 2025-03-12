package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPayment;
import com.guudint.clickargo.payment.dto.CkPaymentTxn;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.dto.MstCurrency;

public class CkCtPayment extends AbstractDTO<CkCtPayment, TCkCtPayment> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1456723889320347715L;
	public static final String PREFIX_FF_CT_PYMNT = "CTPFF";
	public static final String PREFIX_CO_CT_PYMNT = "CTPCO";
	public static final String PREFIX_SP_CT_PYMNT = "CTPSP";

	// Attributes
	/////////////
	private String ctpId;
	private CkPaymentTxn TCkPaymentTxn;
	private MstCurrency TMstCurrency;
	private String ctpJob;
	private String ctpItem;
	private Short ctpQty;
	private BigDecimal ctpAmount;
	private String ctpRef;
	private String ctpAttach;
	private String ctpState;
	private Character ctpStatus;
	private Date ctpDtCreate;
	private String ctpUidCreate;
	private Date ctpDtLupd;
	private String ctpUidLupd;

	// Constructors
	///////////////
	public CkCtPayment() {
	}

	public CkCtPayment(TCkCtPayment entity) {
		super(entity);
	}
	
	/**
	 * @param ctpId
	 * @param tCkPaymentTxn
	 * @param tMstCurrency
	 * @param ctpJob
	 * @param ctpItem
	 * @param ctpQty
	 * @param ctpAmount
	 * @param ctpRef
	 * @param ctpAttach
	 * @param ctpState
	 * @param ctpStatus
	 * @param ctpDtCreate
	 * @param ctpUidCreate
	 * @param ctpDtLupd
	 * @param ctpUidLupd
	 */
	public CkCtPayment(String ctpId, CkPaymentTxn tCkPaymentTxn, MstCurrency tMstCurrency, String ctpJob,
			String ctpItem, Short ctpQty, BigDecimal ctpAmount, String ctpRef, String ctpAttach, String ctpState,
			Character ctpStatus, Date ctpDtCreate, String ctpUidCreate, Date ctpDtLupd, String ctpUidLupd) {
		super();
		this.ctpId = ctpId;
		TCkPaymentTxn = tCkPaymentTxn;
		TMstCurrency = tMstCurrency;
		this.ctpJob = ctpJob;
		this.ctpItem = ctpItem;
		this.ctpQty = ctpQty;
		this.ctpAmount = ctpAmount;
		this.ctpRef = ctpRef;
		this.ctpAttach = ctpAttach;
		this.ctpState = ctpState;
		this.ctpStatus = ctpStatus;
		this.ctpDtCreate = ctpDtCreate;
		this.ctpUidCreate = ctpUidCreate;
		this.ctpDtLupd = ctpDtLupd;
		this.ctpUidLupd = ctpUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the ctpId
	 */
	public String getCtpId() {
		return ctpId;
	}

	/**
	 * @param ctpId the ctpId to set
	 */
	public void setCtpId(String ctpId) {
		this.ctpId = ctpId;
	}

	/**
	 * @return the tCkPaymentTxn
	 */
	public CkPaymentTxn getTCkPaymentTxn() {
		return TCkPaymentTxn;
	}

	/**
	 * @param tCkPaymentTxn the tCkPaymentTxn to set
	 */
	public void setTCkPaymentTxn(CkPaymentTxn tCkPaymentTxn) {
		TCkPaymentTxn = tCkPaymentTxn;
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
	 * @return the ctpJob
	 */
	public String getCtpJob() {
		return ctpJob;
	}

	/**
	 * @param ctpJob the ctpJob to set
	 */
	public void setCtpJob(String ctpJob) {
		this.ctpJob = ctpJob;
	}

	/**
	 * @return the ctpItem
	 */
	public String getCtpItem() {
		return ctpItem;
	}

	/**
	 * @param ctpItem the ctpItem to set
	 */
	public void setCtpItem(String ctpItem) {
		this.ctpItem = ctpItem;
	}

	/**
	 * @return the ctpQty
	 */
	public Short getCtpQty() {
		return ctpQty;
	}

	/**
	 * @param ctpQty the ctpQty to set
	 */
	public void setCtpQty(Short ctpQty) {
		this.ctpQty = ctpQty;
	}

	/**
	 * @return the ctpAmount
	 */
	public BigDecimal getCtpAmount() {
		return ctpAmount;
	}

	/**
	 * @param ctpAmount the ctpAmount to set
	 */
	public void setCtpAmount(BigDecimal ctpAmount) {
		this.ctpAmount = ctpAmount;
	}

	/**
	 * @return the ctpRef
	 */
	public String getCtpRef() {
		return ctpRef;
	}

	/**
	 * @param ctpRef the ctpRef to set
	 */
	public void setCtpRef(String ctpRef) {
		this.ctpRef = ctpRef;
	}

	/**
	 * @return the ctpAttach
	 */
	public String getCtpAttach() {
		return ctpAttach;
	}

	/**
	 * @param ctpAttach the ctpAttach to set
	 */
	public void setCtpAttach(String ctpAttach) {
		this.ctpAttach = ctpAttach;
	}

	/**
	 * @return the ctpState
	 */
	public String getCtpState() {
		return ctpState;
	}

	/**
	 * @param ctpState the ctpState to set
	 */
	public void setCtpState(String ctpState) {
		this.ctpState = ctpState;
	}

	/**
	 * @return the ctpStatus
	 */
	public Character getCtpStatus() {
		return ctpStatus;
	}

	/**
	 * @param ctpStatus the ctpStatus to set
	 */
	public void setCtpStatus(Character ctpStatus) {
		this.ctpStatus = ctpStatus;
	}

	/**
	 * @return the ctpDtCreate
	 */
	public Date getCtpDtCreate() {
		return ctpDtCreate;
	}

	/**
	 * @param ctpDtCreate the ctpDtCreate to set
	 */
	public void setCtpDtCreate(Date ctpDtCreate) {
		this.ctpDtCreate = ctpDtCreate;
	}

	/**
	 * @return the ctpUidCreate
	 */
	public String getCtpUidCreate() {
		return ctpUidCreate;
	}

	/**
	 * @param ctpUidCreate the ctpUidCreate to set
	 */
	public void setCtpUidCreate(String ctpUidCreate) {
		this.ctpUidCreate = ctpUidCreate;
	}

	/**
	 * @return the ctpDtLupd
	 */
	public Date getCtpDtLupd() {
		return ctpDtLupd;
	}

	/**
	 * @param ctpDtLupd the ctpDtLupd to set
	 */
	public void setCtpDtLupd(Date ctpDtLupd) {
		this.ctpDtLupd = ctpDtLupd;
	}

	/**
	 * @return the ctpUidLupd
	 */
	public String getCtpUidLupd() {
		return ctpUidLupd;
	}

	/**
	 * @param ctpUidLupd the ctpUidLupd to set
	 */
	public void setCtpUidLupd(String ctpUidLupd) {
		this.ctpUidLupd = ctpUidLupd;
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(CkCtPayment o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}