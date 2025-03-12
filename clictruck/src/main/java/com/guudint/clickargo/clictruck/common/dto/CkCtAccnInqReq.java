package com.guudint.clickargo.clictruck.common.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.common.model.TCkCtAccnInqReq;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreUsr;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtAccnInqReq extends AbstractDTO<CkCtAccnInqReq, TCkCtAccnInqReq> {

	public enum ReqState {
		PENDING, INPROGRESS, COMPLETED
	}

	public enum ReqAction {
		SAVE, SEND
	}

	private static final long serialVersionUID = 2316425964232123382L;
	private String airId;
	private CoreAccn TCoreAccn;
	private CoreUsr TCoreUsr;
	private String airEmailReq;
	private String airReqState;
	private String airRemarks;
	private Date airDtProcessed;
	private Character airStatus;
	private Date airDtCreate;
	private String airUidCreate;
	private Date airDtLupd;
	private String airUidLupd;

	private String accnRegTaxNo;
	private String history;
	private ReqAction action;

	public CkCtAccnInqReq() {
	}

	public CkCtAccnInqReq(TCkCtAccnInqReq entity) {
		super(entity);
	}

	@Override
	public int compareTo(CkCtAccnInqReq o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the airId
	 */
	public String getAirId() {
		return airId;
	}

	/**
	 * @param airId the airId to set
	 */
	public void setAirId(String airId) {
		this.airId = airId;
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
	 * @return the tCoreUsr
	 */
	public CoreUsr getTCoreUsr() {
		return TCoreUsr;
	}

	/**
	 * @param tCoreUsr the tCoreUsr to set
	 */
	public void setTCoreUsr(CoreUsr tCoreUsr) {
		TCoreUsr = tCoreUsr;
	}

	/**
	 * @return the airEmailReq
	 */
	public String getAirEmailReq() {
		return airEmailReq;
	}

	/**
	 * @param airEmailReq the airEmailReq to set
	 */
	public void setAirEmailReq(String airEmailReq) {
		this.airEmailReq = airEmailReq;
	}

	/**
	 * @return the airReqState
	 */
	public String getAirReqState() {
		return airReqState;
	}

	/**
	 * @param airReqState the airReqState to set
	 */
	public void setAirReqState(String airReqState) {
		this.airReqState = airReqState;
	}

	/**
	 * @return the airStatus
	 */
	public Character getAirStatus() {
		return airStatus;
	}

	/**
	 * @param airStatus the airStatus to set
	 */
	public void setAirStatus(Character airStatus) {
		this.airStatus = airStatus;
	}

	/**
	 * @return the airDtCreate
	 */
	public Date getAirDtCreate() {
		return airDtCreate;
	}

	/**
	 * @param airDtCreate the airDtCreate to set
	 */
	public void setAirDtCreate(Date airDtCreate) {
		this.airDtCreate = airDtCreate;
	}

	/**
	 * @return the airUidCreate
	 */
	public String getAirUidCreate() {
		return airUidCreate;
	}

	/**
	 * @param airUidCreate the airUidCreate to set
	 */
	public void setAirUidCreate(String airUidCreate) {
		this.airUidCreate = airUidCreate;
	}

	/**
	 * @return the airDtLupd
	 */
	public Date getAirDtLupd() {
		return airDtLupd;
	}

	/**
	 * @param airDtLupd the airDtLupd to set
	 */
	public void setAirDtLupd(Date airDtLupd) {
		this.airDtLupd = airDtLupd;
	}

	/**
	 * @return the airUidLupd
	 */
	public String getAirUidLupd() {
		return airUidLupd;
	}

	/**
	 * @param airUidLupd the airUidLupd to set
	 */
	public void setAirUidLupd(String airUidLupd) {
		this.airUidLupd = airUidLupd;
	}

	/**
	 * @return the airRemarks
	 */
	public String getAirRemarks() {
		return airRemarks;
	}

	/**
	 * @param airRemarks the airRemarks to set
	 */
	public void setAirRemarks(String airRemarks) {
		this.airRemarks = airRemarks;
	}

	/**
	 * @return the accnRegTaxNo
	 */
	public String getAccnRegTaxNo() {
		return accnRegTaxNo;
	}

	/**
	 * @param accnRegTaxNo the accnRegTaxNo to set
	 */
	public void setAccnRegTaxNo(String accnRegTaxNo) {
		this.accnRegTaxNo = accnRegTaxNo;
	}

	/**
	 * @return the history
	 */
	public String getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(String history) {
		this.history = history;
	}

	/**
	 * @return the airDtProcessed
	 */
	public Date getAirDtProcessed() {
		return airDtProcessed;
	}

	/**
	 * @param airDtProcessed the airDtProcessed to set
	 */
	public void setAirDtProcessed(Date airDtProcessed) {
		this.airDtProcessed = airDtProcessed;
	}

	/**
	 * @return the action
	 */
	public ReqAction getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(ReqAction action) {
		this.action = action;
	}

}
