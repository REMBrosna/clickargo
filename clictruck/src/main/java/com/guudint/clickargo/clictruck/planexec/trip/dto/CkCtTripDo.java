package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtTripDo extends AbstractDTO<CkCtTripDo, TCkCtTripDo> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -3195300313181292220L;

	// Attributes
	/////////////
	private String doId;
	private CkCtTrip TCkCtTrip;
	private String doNo;
	private String doUnsigned;
	private String doSigned;
	private String doRemarks;
	private Character doStatus;
	private Date doDtCreate;
	private String doUidCreate;
	private Date doDtLupd;
	private String doUidLupd;
	
	private String doUnsignedName;
	private String doSignedName;
	private byte[] doUnsignedData;
	private byte[] doSignedData;
	private boolean isDuplicateDoNo;
	
	// Constructors
	///////////////
	public CkCtTripDo() {
	}

	/**
	 * @param entity
	 */
	public CkCtTripDo(TCkCtTripDo entity) {
		super(entity);
	}

	/**
	 * @param doId
	 * @param tCkCtTrip
	 * @param doNo
	 * @param doUnsigned
	 * @param doSigned
	 * @param doRemarks
	 * @param doStatus
	 * @param doDtCreate
	 * @param doUidCreate
	 * @param doDtLupd
	 * @param doUidLupd
	 */
	public CkCtTripDo(String doId, CkCtTrip tCkCtTrip, String doNo, String doUnsigned, String doSigned,
			String doRemarks, Character doStatus, Date doDtCreate, String doUidCreate, Date doDtLupd,
			String doUidLupd) {
		super();
		this.doId = doId;
		TCkCtTrip = tCkCtTrip;
		this.doNo = doNo;
		this.doUnsigned = doUnsigned;
		this.doSigned = doSigned;
		this.doRemarks = doRemarks;
		this.doStatus = doStatus;
		this.doDtCreate = doDtCreate;
		this.doUidCreate = doUidCreate;
		this.doDtLupd = doDtLupd;
		this.doUidLupd = doUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the doId
	 */
	public String getDoId() {
		return doId;
	}

	/**
	 * @param doId the doId to set
	 */
	public void setDoId(String doId) {
		this.doId = doId;
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
	 * @return the doNo
	 */
	public String getDoNo() {
		return doNo;
	}

	/**
	 * @param doNo the doNo to set
	 */
	public void setDoNo(String doNo) {
		this.doNo = doNo;
	}

	/**
	 * @return the doUnsigned
	 */
	public String getDoUnsigned() {
		return doUnsigned;
	}

	/**
	 * @param doUnsigned the doUnsigned to set
	 */
	public void setDoUnsigned(String doUnsigned) {
		this.doUnsigned = doUnsigned;
	}

	/**
	 * @return the doSigned
	 */
	public String getDoSigned() {
		return doSigned;
	}

	/**
	 * @param doSigned the doSigned to set
	 */
	public void setDoSigned(String doSigned) {
		this.doSigned = doSigned;
	}

	/**
	 * @return the doRemarks
	 */
	public String getDoRemarks() {
		return doRemarks;
	}

	/**
	 * @param doRemarks the doRemarks to set
	 */
	public void setDoRemarks(String doRemarks) {
		this.doRemarks = doRemarks;
	}

	/**
	 * @return the doStatus
	 */
	public Character getDoStatus() {
		return doStatus;
	}

	/**
	 * @param doStatus the doStatus to set
	 */
	public void setDoStatus(Character doStatus) {
		this.doStatus = doStatus;
	}

	/**
	 * @return the doDtCreate
	 */
	public Date getDoDtCreate() {
		return doDtCreate;
	}

	/**
	 * @param doDtCreate the doDtCreate to set
	 */
	public void setDoDtCreate(Date doDtCreate) {
		this.doDtCreate = doDtCreate;
	}

	/**
	 * @return the doUidCreate
	 */
	public String getDoUidCreate() {
		return doUidCreate;
	}

	/**
	 * @param doUidCreate the doUidCreate to set
	 */
	public void setDoUidCreate(String doUidCreate) {
		this.doUidCreate = doUidCreate;
	}

	/**
	 * @return the doDtLupd
	 */
	public Date getDoDtLupd() {
		return doDtLupd;
	}

	/**
	 * @param doDtLupd the doDtLupd to set
	 */
	public void setDoDtLupd(Date doDtLupd) {
		this.doDtLupd = doDtLupd;
	}

	/**
	 * @return the doUidLupd
	 */
	public String getDoUidLupd() {
		return doUidLupd;
	}

	/**
	 * @param doUidLupd the doUidLupd to set
	 */
	public void setDoUidLupd(String doUidLupd) {
		this.doUidLupd = doUidLupd;
	}

	/**
	 * @return the doUnsignedName
	 */
	public String getDoUnsignedName() {
		return doUnsignedName;
	}

	/**
	 * @param doUnsignedName the doUnsignedName to set
	 */
	public void setDoUnsignedName(String doUnsignedName) {
		this.doUnsignedName = doUnsignedName;
	}

	/**
	 * @return the doSignedName
	 */
	public String getDoSignedName() {
		return doSignedName;
	}

	/**
	 * @param doSignedName the doSignedName to set
	 */
	public void setDoSignedName(String doSignedName) {
		this.doSignedName = doSignedName;
	}

	/**
	 * @return the doUnsignedData
	 */
	public byte[] getDoUnsignedData() {
		return doUnsignedData;
	}

	/**
	 * @param doUnsignedData the doUnsignedData to set
	 */
	public void setDoUnsignedData(byte[] doUnsignedData) {
		this.doUnsignedData = doUnsignedData;
	}

	/**
	 * @return the doSignedData
	 */
	public byte[] getDoSignedData() {
		return doSignedData;
	}

	/**
	 * @param doSignedData the doSignedData to set
	 */
	public void setDoSignedData(byte[] doSignedData) {
		this.doSignedData = doSignedData;
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
	public int compareTo(CkCtTripDo o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}