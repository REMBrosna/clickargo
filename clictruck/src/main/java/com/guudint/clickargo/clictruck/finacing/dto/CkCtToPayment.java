package com.guudint.clickargo.clictruck.finacing.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.finacing.model.TCkCtToPayment;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtToPayment extends AbstractDTO<CkCtToPayment, TCkCtToPayment> {

	private static final long serialVersionUID = -8507319918264403183L;
	public static final String PREFIX_ID = "TOP";

	
	public static enum Status {
		NEW('N',"Only scheduler payment for New status"), 
		Error('E',"Update to Error if fail to verify before payment"), 
		PAYING('P', "Update to paying before call clicPay, make sure never revert to New status"), 
		SUCCESS('S', "Call clicPay and payment successful"), 
		FAILED('F', "Fail to call clicPay or clicPay payment failed"),
		CANCELLED('C', "Cancel");

		private char code;
		private String desc;

		private Status(char code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public char getCode() {
			return this.code;
		}

		public String getDesc() {
			return desc;
		}

	}

	private String topId;
	private CoreAccn TCoreAccn;
	private Date topDtTransfer;
	private BigDecimal topAmt;
	private String topReference;
	private String topJson;
	private String topException;
	private Character topStatus;
	private Date topDtCreate;
	private String topUidCreate;
	private Date topDtLupd;
	private String topUidLupd;

	// Constructors
	///////////////
	public CkCtToPayment() {
	}

	public CkCtToPayment(TCkCtToPayment entity) {
		super(entity);
	}

	public String getTopId() {
		return topId;
	}

	public void setTopId(String topId) {
		this.topId = topId;
	}

	public CoreAccn getTCoreAccn() {
		return TCoreAccn;
	}

	public void setTCoreAccn(CoreAccn tCoreAccn) {
		TCoreAccn = tCoreAccn;
	}

	public Date getTopDtTransfer() {
		return topDtTransfer;
	}

	public void setTopDtTransfer(Date topDtTransfer) {
		this.topDtTransfer = topDtTransfer;
	}

	public BigDecimal getTopAmt() {
		return topAmt;
	}

	public void setTopAmt(BigDecimal topAmt) {
		this.topAmt = topAmt;
	}

	public String getTopReference() {
		return topReference;
	}

	public void setTopReference(String topReference) {
		this.topReference = topReference;
	}

	public String getTopJson() {
		return topJson;
	}

	public void setTopJson(String topJson) {
		this.topJson = topJson;
	}

	public String getTopException() {
		return topException;
	}

	public void setTopException(String topException) {
		this.topException = topException;
	}

	public Character getTopStatus() {
		return topStatus;
	}

	public void setTopStatus(Character topStatus) {
		this.topStatus = topStatus;
	}

	public Date getTopDtCreate() {
		return topDtCreate;
	}

	public void setTopDtCreate(Date topDtCreate) {
		this.topDtCreate = topDtCreate;
	}

	public String getTopUidCreate() {
		return topUidCreate;
	}

	public void setTopUidCreate(String topUidCreate) {
		this.topUidCreate = topUidCreate;
	}

	public Date getTopDtLupd() {
		return topDtLupd;
	}

	public void setTopDtLupd(Date topDtLupd) {
		this.topDtLupd = topDtLupd;
	}

	public String getTopUidLupd() {
		return topUidLupd;
	}

	public void setTopUidLupd(String topUidLupd) {
		this.topUidLupd = topUidLupd;
	}

	@Override
	public int compareTo(CkCtToPayment o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
