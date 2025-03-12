package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtTripDoAttach extends AbstractDTO<CkCtTripDoAttach, TCkCtTripDoAttach> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -2855968671790810880L;
	
	// Attributes
	/////////////
	private String doaId;
	private CkCtTrip TCkCtTrip;
	private String doaName;
	private String doaLoc;
	private String doaSource;
	private Character doaStatus;
	private Date doaDtCreate;
	private String doaUidCreate;
	private Date doaDtLupd;
	private String doaUidLupd;
	
	private byte[] doaData;
	
	private CkCtTripDo ckCtTripDo;
	private boolean isDuplicateDoNo;
	
	// Constructors
	///////////////
	public CkCtTripDoAttach() {
	}

	/**
	 * @param entity
	 */
	public CkCtTripDoAttach(TCkCtTripDoAttach entity) {
		super(entity);
	}

	/**
	 * @param doaId
	 * @param tCkCtTrip
	 * @param doaName
	 * @param doaLoc
	 * @param doaStatus
	 * @param doaDtCreate
	 * @param doaUidCreate
	 * @param doaDtLupd
	 * @param doaUidLupd
	 */
	public CkCtTripDoAttach(String doaId, CkCtTrip tCkCtTrip,
			String doaName, String doaLoc, Character doaStatus, Date doaDtCreate, String doaUidCreate, Date doaDtLupd,
			String doaUidLupd) {
		super();
		this.doaId = doaId;
		TCkCtTrip = tCkCtTrip;
		this.doaName = doaName;
		this.doaLoc = doaLoc;
		this.doaStatus = doaStatus;
		this.doaDtCreate = doaDtCreate;
		this.doaUidCreate = doaUidCreate;
		this.doaDtLupd = doaDtLupd;
		this.doaUidLupd = doaUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the doaId
	 */
	public String getDoaId() {
		return doaId;
	}

	/**
	 * @param doaId the doaId to set
	 */
	public void setDoaId(String doaId) {
		this.doaId = doaId;
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
	 * @return the doaName
	 */
	public String getDoaName() {
		return doaName;
	}

	/**
	 * @param doaName the doaName to set
	 */
	public void setDoaName(String doaName) {
		this.doaName = doaName;
	}

	/**
	 * @return the doaLoc
	 */
	public String getDoaLoc() {
		return doaLoc;
	}

	/**
	 * @param doaLoc the doaLoc to set
	 */
	public void setDoaLoc(String doaLoc) {
		this.doaLoc = doaLoc;
	}
	
	public String getDoaSource() {
		return this.doaSource;
	}

	public void setDoaSource(String doaSource) {
		this.doaSource = doaSource;
	}

	/**
	 * @return the doaStatus
	 */
	public Character getDoaStatus() {
		return doaStatus;
	}

	/**
	 * @param doaStatus the doaStatus to set
	 */
	public void setDoaStatus(Character doaStatus) {
		this.doaStatus = doaStatus;
	}

	/**
	 * @return the doaDtCreate
	 */
	public Date getDoaDtCreate() {
		return doaDtCreate;
	}

	/**
	 * @param doaDtCreate the doaDtCreate to set
	 */
	public void setDoaDtCreate(Date doaDtCreate) {
		this.doaDtCreate = doaDtCreate;
	}

	/**
	 * @return the doaUidCreate
	 */
	public String getDoaUidCreate() {
		return doaUidCreate;
	}

	/**
	 * @param doaUidCreate the doaUidCreate to set
	 */
	public void setDoaUidCreate(String doaUidCreate) {
		this.doaUidCreate = doaUidCreate;
	}

	/**
	 * @return the doaDtLupd
	 */
	public Date getDoaDtLupd() {
		return doaDtLupd;
	}

	/**
	 * @param doaDtLupd the doaDtLupd to set
	 */
	public void setDoaDtLupd(Date doaDtLupd) {
		this.doaDtLupd = doaDtLupd;
	}

	/**
	 * @return the doaUidLupd
	 */
	public String getDoaUidLupd() {
		return doaUidLupd;
	}

	/**
	 * @param doaUidLupd the doaUidLupd to set
	 */
	public void setDoaUidLupd(String doaUidLupd) {
		this.doaUidLupd = doaUidLupd;
	}

	/**
	 * @return the doaData
	 */
	public byte[] getDoaData() {
		return doaData;
	}

	/**
	 * @param doaData the doaData to set
	 */
	public void setDoaData(byte[] doaData) {
		this.doaData = doaData;
	}

	/**
	 * @return the ckCtTripDo
	 */
	public CkCtTripDo getCkCtTripDo() {
		return ckCtTripDo;
	}

	/**
	 * @param ckCtTripDo the ckCtTripDo to set
	 */
	public void setCkCtTripDo(CkCtTripDo ckCtTripDo) {
		this.ckCtTripDo = ckCtTripDo;
	}

	/**
	 * @return the isDuplicateDoNo
	 */
	public boolean isDuplicateDoNo() {
		return isDuplicateDoNo;
	}

	/**
	 * @param isDuplicateDoNo the isDuplicateDoNo to set
	 */
	public void setDuplicateDoNo(boolean isDuplicateDoNo) {
		this.isDuplicateDoNo = isDuplicateDoNo;
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(CkCtTripDoAttach o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}