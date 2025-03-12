package com.guudint.clickargo.clictruck.sage.dto;
// Generated 15 Jun 2023, 4:49:30 pm by Hibernate Tools 4.3.6.Final

import java.util.Date;

import com.guudint.clickargo.clictruck.sage.model.TCkCtSage;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtSage extends AbstractDTO<CkCtSage, TCkCtSage> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1L;

	// Attributes
	/////////////

	private String sageId;
	private String sageBatchNo;
	private String sageFileLoc;
	private Date sageDtStart;
	private Date sageDtEnd;
	
	private Character sageStatus; 
	private Date sageDtCreate;
	private String sageUidCreate;
	private Date sageDtLupd;
	private String sageUidLupd;

	// Constructors
	///////////////
	public CkCtSage() {
	}

	/**
	 * @param entity
	 */
	public CkCtSage(TCkCtSage entity) {
		super(entity);
	}

	public CkCtSage(String sageId, String sageBatchNo, String sageFileLoc, Date sageDtStart, Date sageDtEnd) {
		this.sageId = sageId;
		this.sageBatchNo = sageBatchNo;
		this.sageFileLoc = sageFileLoc;
		this.sageDtStart = sageDtStart;
		this.sageDtEnd = sageDtEnd;
	}

	// Override Methods
	///////////////////

	@Override
	public int compareTo(CkCtSage o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	// Properties
	/////////////

	public String getSageId() {
		return sageId;
	}

	public void setSageId(String sageId) {
		this.sageId = sageId;
	}

	public String getSageBatchNo() {
		return sageBatchNo;
	}

	public void setSageBatchNo(String sageBatchNo) {
		this.sageBatchNo = sageBatchNo;
	}

	public String getSageFileLoc() {
		return sageFileLoc;
	}

	public void setSageFileLoc(String sageFileLoc) {
		this.sageFileLoc = sageFileLoc;
	}

	public Date getSageDtStart() {
		return sageDtStart;
	}

	public void setSageDtStart(Date sageDtStart) {
		this.sageDtStart = sageDtStart;
	}

	public Date getSageDtEnd() {
		return sageDtEnd;
	}

	public void setSageDtEnd(Date sageDtEnd) {
		this.sageDtEnd = sageDtEnd;
	}

	
	public Character getSageStatus() {
		return sageStatus;
	}

	public void setSageStatus(Character sageStatus) {
		this.sageStatus = sageStatus;
	}

	public Date getSageDtCreate() {
		return sageDtCreate;
	}

	public void setSageDtCreate(Date sageDtCreate) {
		this.sageDtCreate = sageDtCreate;
	}

	public String getSageUidCreate() {
		return sageUidCreate;
	}

	public void setSageUidCreate(String sageUidCreate) {
		this.sageUidCreate = sageUidCreate;
	}

	public Date getSageDtLupd() {
		return sageDtLupd;
	}

	public void setSageDtLupd(Date sageDtLupd) {
		this.sageDtLupd = sageDtLupd;
	}

	public String getSageUidLupd() {
		return sageUidLupd;
	}

	public void setSageUidLupd(String sageUidLupd) {
		this.sageUidLupd = sageUidLupd;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
