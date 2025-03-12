package com.guudint.clickargo.clictruck.sage.dto;
// Generated 16 Jun 2023, 11:42:54 am by Hibernate Tools 4.3.6.Final

import java.util.Date;

import com.guudint.clickargo.clictruck.sage.model.TCkCtSageTax;
import com.vcc.camelone.common.dto.AbstractDTO;


public class CkCtSageTax extends AbstractDTO<CkCtSageTax, TCkCtSageTax> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1L;
	public static final String PREFIX_ID = "TXSEQ";

	// Attributes
	/////////////
	private String stId;
	private String stPrefix;
	private long stRangeBegin;
	private long stRangeEnd;
	private long stRangeCurrent;
	private String stRangeFormat;
	private String history;
	private Character stStatus; // E: expired, A: active
	private Date stDtCreate;
	private String stUidCreate;
	private Date stDtLupd;
	private String stUidLupd;

	// Constructors
	///////////////
	public CkCtSageTax() {
	}
	
	public CkCtSageTax(TCkCtSageTax entity) {
		super(entity);
	}

	public CkCtSageTax(String stId, String stPrefix, long stRangeBegin, long stRangeEnd, long stRangeCurrent) {
		this.stId = stId;
		this.stPrefix = stPrefix;
		this.stRangeBegin = stRangeBegin;
		this.stRangeEnd = stRangeEnd;
		this.stRangeCurrent = stRangeCurrent;
	}

	public CkCtSageTax(String stId, String stPrefix, long stRangeBegin, long stRangeEnd, long stRangeCurrent,
			String history, Character stStatus, Date stDtCreate, String stUidCreate, Date stDtLupd, String stUidLupd) {
		this.stId = stId;
		this.stPrefix = stPrefix;
		this.stRangeBegin = stRangeBegin;
		this.stRangeEnd = stRangeEnd;
		this.stRangeCurrent = stRangeCurrent;
		this.history = history;
		this.stStatus = stStatus;
		this.stDtCreate = stDtCreate;
		this.stUidCreate = stUidCreate;
		this.stDtLupd = stDtLupd;
		this.stUidLupd = stUidLupd;
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(CkCtSageTax arg0) {
		return 0;
	}

	@Override
	public void init() {

	}

	// Properties
	/////////////

	public String getStId() {
		return stId;
	}

	public void setStId(String stId) {
		this.stId = stId;
	}

	public String getStPrefix() {
		return stPrefix;
	}

	public void setStPrefix(String stPrefix) {
		this.stPrefix = stPrefix;
	}

	public long getStRangeBegin() {
		return stRangeBegin;
	}

	public void setStRangeBegin(long stRangeBegin) {
		this.stRangeBegin = stRangeBegin;
	}

	public long getStRangeEnd() {
		return stRangeEnd;
	}

	public void setStRangeEnd(long stRangeEnd) {
		this.stRangeEnd = stRangeEnd;
	}

	public long getStRangeCurrent() {
		return stRangeCurrent;
	}

	public void setStRangeCurrent(long stRangeCurrent) {
		this.stRangeCurrent = stRangeCurrent;
	}

	public String getStRangeFormat() {
		return stRangeFormat;
	}

	public void setStRangeFormat(String stRangeFormat) {
		this.stRangeFormat = stRangeFormat;
	}
	

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public Character getStStatus() {
		return stStatus;
	}

	public void setStStatus(Character stStatus) {
		this.stStatus = stStatus;
	}

	public Date getStDtCreate() {
		return stDtCreate;
	}

	public void setStDtCreate(Date stDtCreate) {
		this.stDtCreate = stDtCreate;
	}

	public String getStUidCreate() {
		return stUidCreate;
	}

	public void setStUidCreate(String stUidCreate) {
		this.stUidCreate = stUidCreate;
	}

	public Date getStDtLupd() {
		return stDtLupd;
	}

	public void setStDtLupd(Date stDtLupd) {
		this.stDtLupd = stDtLupd;
	}

	public String getStUidLupd() {
		return stUidLupd;
	}

	public void setStUidLupd(String stUidLupd) {
		this.stUidLupd = stUidLupd;
	}
	
}
