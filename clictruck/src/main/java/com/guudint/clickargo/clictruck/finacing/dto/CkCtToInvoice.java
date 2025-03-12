package com.guudint.clickargo.clictruck.finacing.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstToInvoiceState;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtToInvoice extends AbstractDTO<CkCtToInvoice, TCkCtToInvoice> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 672949698656479393L;
	public static final String PREFIX_ID = "CTINV";

	// Attributes
	/////////////
	private String invId;
	private CkCtMstToInvoiceState TCkCtMstToInvoiceState;
	private CoreAccn TCoreAccnByInvTo;
	private CoreAccn TCoreAccnByInvFrom;
	private CkCtTrip TCkCtTrip;
	private String invJobId;
	private String invNo;
	private Date invDtIssue;
	private String invName;
	private String invLoc;
	private String invInvocierComment;
	private String invInvocieeRemarks;
	private Character invStatus;
	private Date invDtCreate;
	private String invUidCreate;
	private Date invDtLupd;
	private String invUidLupd;
	private String base64File;
	private TripCharges tripCharges;
	private TripDoDetail tripDoDetail;

	// Constructors
	///////////////
	public CkCtToInvoice() {
	}

	public CkCtToInvoice(TCkCtToInvoice entity) {
		super(entity);
	}

	/**
	 * @param invId
	 * @param tCkCtMstToInvoiceState
	 * @param tCoreAccnByInvTo
	 * @param tCoreAccnByInvFrom
	 * @param invJobId
	 * @param invNo
	 * @param invDtIssue
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
	public CkCtToInvoice(String invId, CkCtMstToInvoiceState tCkCtMstToInvoiceState, CoreAccn tCoreAccnByInvTo,
			CoreAccn tCoreAccnByInvFrom, String invJobId, String invNo, Date invDtIssue, String invName, String invLoc,
			String invInvocierComment, String invInvocieeRemarks, Character invStatus, Date invDtCreate,
			String invUidCreate, Date invDtLupd, String invUidLupd) {
		super();
		this.invId = invId;
		TCkCtMstToInvoiceState = tCkCtMstToInvoiceState;
		TCoreAccnByInvTo = tCoreAccnByInvTo;
		TCoreAccnByInvFrom = tCoreAccnByInvFrom;
		this.invJobId = invJobId;
		this.invNo = invNo;
		this.invDtIssue = invDtIssue;
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

	public CkCtTrip getTCkCtTrip() {
		return this.TCkCtTrip;
	}

	public void setTCkCtTrip(CkCtTrip TCkCtTrip) {
		this.TCkCtTrip = TCkCtTrip;
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

	public String getBase64File() {
		return this.base64File;
	}

	public void setBase64File(String base64File) {
		this.base64File = base64File;
	}

	public TripCharges getTripCharges() {
		return this.tripCharges;
	}

	public void setTripCharges(TripCharges tripCharges) {
		this.tripCharges = tripCharges;
	}

	public TripDoDetail getTripDoDetail() {
		return this.tripDoDetail;
	}

	public void setTripDoDetail(TripDoDetail tripDoDetail) {
		this.tripDoDetail = tripDoDetail;
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
	public int compareTo(CkCtToInvoice o) {
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
