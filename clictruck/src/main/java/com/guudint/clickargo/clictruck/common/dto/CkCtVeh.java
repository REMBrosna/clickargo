package com.guudint.clickargo.clictruck.common.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehState;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtVeh extends AbstractDTO<CkCtVeh, TCkCtVeh> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -3900567733427268804L;

	// Attributes
	/////////////
	private String vhId;
	private CkCtMstChassisType TCkCtMstChassisType;
	private CkCtMstVehState TCkCtMstVehState;
	private CkCtMstVehType TCkCtMstVehType;
	// private CkJob TCkJob;
	private CoreAccn TCoreAccn;
	private String vhPlateNo;
	private Byte vhClass;
	private String vhPhotoName;
	private String vhPhotoLoc;
	private Short vhLength;
	private Short vhWidth;
	private Short vhHeight;
	private Integer vhWeight;
	private Integer vhVolume;
	private String vhChassisNo;
	private Character vhIsMaintenance;
	private String vhRemarks;
	private String vhGpsImei;
	private Character vhStatus;
	private Date vhDtCreate;
	private String vhUidCreate;
	private Date vhDtLupd;
	private String vhUidLupd;
	private String base64File;
	private String colorCode;

	private CkCtTrackDevice TCkCtTrackDevice = null;
	private String vhChassisNoOth;

	// place holder for department
	private String department;
	private CkCtDept TCkCtDept;

	// place holder for maintenance for the active record only
	private CkCtVehExt maintenance;

	// place holder for expiry for the active record only
	private CkCtVehExt expiry;

	// Constructors
	///////////////
	public CkCtVeh() {
	}

	public CkCtVeh(String vhId) {
		this.vhId = vhId;
	}

	/**
	 * @param entity
	 */
	public CkCtVeh(TCkCtVeh entity) {
		super(entity);
	}

	/**
	 * @param vhId
	 * @param tCkCtMstChassisType
	 * @param tCkCtMstVehState
	 * @param tCkCtMstVehType
	 * @param tCkJob
	 * @param tCoreAccn
	 * @param vhPlateNo
	 * @param vhClass
	 * @param vhPhotoName
	 * @param vhPhotoLoc
	 * @param vhLength
	 * @param vhWidth
	 * @param vhHeight
	 * @param vhWeight
	 * @param vhVolume
	 * @param vhChassisNo
	 * @param vhIsMaintenance
	 * @param vhRemarks
	 * @param vhGpsImei
	 * @param vhStatus
	 * @param vhDtCreate
	 * @param vhUidCreate
	 * @param vhDtLupd
	 * @param vhUidLupd
	 */
	// public CkCtVeh(String vhId, CkCtMstChassisType tCkCtMstChassisType,
	// CkCtMstVehState tCkCtMstVehState,
	// CkCtMstVehType tCkCtMstVehType, CkJob tCkJob, CoreAccn tCoreAccn, String
	// vhPlateNo, Byte vhClass,
	// String vhPhotoName, String vhPhotoLoc, Short vhLength, Short vhWidth, Short
	// vhHeight, Integer vhWeight,
	// Integer vhVolume, String vhChassisNo, Character vhIsMaintenance, String
	// vhRemarks, String vhGpsImei,
	// Character vhStatus, Date vhDtCreate, String vhUidCreate, Date vhDtLupd,
	// String vhUidLupd) {
	// super();
	// this.vhId = vhId;
	// TCkCtMstChassisType = tCkCtMstChassisType;
	// TCkCtMstVehState = tCkCtMstVehState;
	// TCkCtMstVehType = tCkCtMstVehType;
	// TCkJob = tCkJob;
	// TCoreAccn = tCoreAccn;
	// this.vhPlateNo = vhPlateNo;
	// this.vhClass = vhClass;
	// this.vhPhotoName = vhPhotoName;
	// this.vhPhotoLoc = vhPhotoLoc;
	// this.vhLength = vhLength;
	// this.vhWidth = vhWidth;
	// this.vhHeight = vhHeight;
	// this.vhWeight = vhWeight;
	// this.vhVolume = vhVolume;
	// this.vhChassisNo = vhChassisNo;
	// this.vhIsMaintenance = vhIsMaintenance;
	// this.vhRemarks = vhRemarks;
	// this.vhGpsImei = vhGpsImei;
	// this.vhStatus = vhStatus;
	// this.vhDtCreate = vhDtCreate;
	// this.vhUidCreate = vhUidCreate;
	// this.vhDtLupd = vhDtLupd;
	// this.vhUidLupd = vhUidLupd;
	// }
	public CkCtVeh(String vhId, CkCtMstChassisType tCkCtMstChassisType, CkCtMstVehState tCkCtMstVehState,
			CkCtMstVehType tCkCtMstVehType, CoreAccn tCoreAccn, String vhPlateNo, Byte vhClass, String vhPhotoName,
			String vhPhotoLoc, Short vhLength, Short vhWidth, Short vhHeight, Integer vhWeight, Integer vhVolume,
			String vhChassisNo, Character vhIsMaintenance, String vhRemarks, String vhGpsImei, Character vhStatus,
			Date vhDtCreate, String vhUidCreate, Date vhDtLupd, String vhUidLupd) {
		super();
		this.vhId = vhId;
		TCkCtMstChassisType = tCkCtMstChassisType;
		TCkCtMstVehState = tCkCtMstVehState;
		TCkCtMstVehType = tCkCtMstVehType;
		// TCkJob = tCkJob;
		TCoreAccn = tCoreAccn;
		this.vhPlateNo = vhPlateNo;
		this.vhClass = vhClass;
		this.vhPhotoName = vhPhotoName;
		this.vhPhotoLoc = vhPhotoLoc;
		this.vhLength = vhLength;
		this.vhWidth = vhWidth;
		this.vhHeight = vhHeight;
		this.vhWeight = vhWeight;
		this.vhVolume = vhVolume;
		this.vhChassisNo = vhChassisNo;
		this.vhIsMaintenance = vhIsMaintenance;
		this.vhRemarks = vhRemarks;
		this.vhGpsImei = vhGpsImei;
		this.vhStatus = vhStatus;
		this.vhDtCreate = vhDtCreate;
		this.vhUidCreate = vhUidCreate;
		this.vhDtLupd = vhDtLupd;
		this.vhUidLupd = vhUidLupd;
	}

	public String getBase64File() {
		return base64File;
	}

	public void setBase64File(String base64File) {
		this.base64File = base64File;
	}

	// Properties
	/////////////
	/**
	 * @return the vhId
	 */
	public String getVhId() {
		return vhId;
	}

	/**
	 * @param vhId the vhId to set
	 */
	public void setVhId(String vhId) {
		this.vhId = vhId;
	}

	/**
	 * @return the tCkCtMstChassisType
	 */
	public CkCtMstChassisType getTCkCtMstChassisType() {
		return TCkCtMstChassisType;
	}

	/**
	 * @param tCkCtMstChassisType the tCkCtMstChassisType to set
	 */
	public void setTCkCtMstChassisType(CkCtMstChassisType tCkCtMstChassisType) {
		TCkCtMstChassisType = tCkCtMstChassisType;
	}

	/**
	 * @return the tCkCtMstVehState
	 */
	public CkCtMstVehState getTCkCtMstVehState() {
		return TCkCtMstVehState;
	}

	/**
	 * @param tCkCtMstVehState the tCkCtMstVehState to set
	 */
	public void setTCkCtMstVehState(CkCtMstVehState tCkCtMstVehState) {
		TCkCtMstVehState = tCkCtMstVehState;
	}

	/**
	 * @return the tCkCtMstVehType
	 */
	public CkCtMstVehType getTCkCtMstVehType() {
		return TCkCtMstVehType;
	}

	/**
	 * @param tCkCtMstVehType the tCkCtMstVehType to set
	 */
	public void setTCkCtMstVehType(CkCtMstVehType tCkCtMstVehType) {
		TCkCtMstVehType = tCkCtMstVehType;
	}

	/**
	 * @return the tCkJob
	 */
	// public CkJob getTCkJob() {
	// return TCkJob;
	// }

	// /**
	// * @param tCkJob the tCkJob to set
	// */
	// public void setTCkJob(CkJob tCkJob) {
	// TCkJob = tCkJob;
	// }

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
	 * @return the vhPlateNo
	 */
	public String getVhPlateNo() {
		return vhPlateNo;
	}

	/**
	 * @param vhPlateNo the vhPlateNo to set
	 */
	public void setVhPlateNo(String vhPlateNo) {
		this.vhPlateNo = vhPlateNo;
	}

	/**
	 * @return the vhClass
	 */
	public Byte getVhClass() {
		return vhClass;
	}

	/**
	 * @param vhClass the vhClass to set
	 */
	public void setVhClass(Byte vhClass) {
		this.vhClass = vhClass;
	}

	/**
	 * @return the vhPhotoName
	 */
	public String getVhPhotoName() {
		return vhPhotoName;
	}

	/**
	 * @param vhPhotoName the vhPhotoName to set
	 */
	public void setVhPhotoName(String vhPhotoName) {
		this.vhPhotoName = vhPhotoName;
	}

	/**
	 * @return the vhPhotoLoc
	 */
	public String getVhPhotoLoc() {
		return vhPhotoLoc;
	}

	/**
	 * @param vhPhotoLoc the vhPhotoLoc to set
	 */
	public void setVhPhotoLoc(String vhPhotoLoc) {
		this.vhPhotoLoc = vhPhotoLoc;
	}

	/**
	 * @return the vhLength
	 */
	public Short getVhLength() {
		return vhLength;
	}

	/**
	 * @param vhLength the vhLength to set
	 */
	public void setVhLength(Short vhLength) {
		this.vhLength = vhLength;
	}

	/**
	 * @return the vhWidth
	 */
	public Short getVhWidth() {
		return vhWidth;
	}

	/**
	 * @param vhWidth the vhWidth to set
	 */
	public void setVhWidth(Short vhWidth) {
		this.vhWidth = vhWidth;
	}

	/**
	 * @return the vhHeight
	 */
	public Short getVhHeight() {
		return vhHeight;
	}

	/**
	 * @param vhHeight the vhHeight to set
	 */
	public void setVhHeight(Short vhHeight) {
		this.vhHeight = vhHeight;
	}

	/**
	 * @return the vhWeight
	 */
	public Integer getVhWeight() {
		return vhWeight;
	}

	/**
	 * @param vhWeight the vhWeight to set
	 */
	public void setVhWeight(Integer vhWeight) {
		this.vhWeight = vhWeight;
	}

	/**
	 * @return the vhVolume
	 */
	public Integer getVhVolume() {
		return vhVolume;
	}

	/**
	 * @param vhVolume the vhVolume to set
	 */
	public void setVhVolume(Integer vhVolume) {
		this.vhVolume = vhVolume;
	}

	/**
	 * @return the vhChassisNo
	 */
	public String getVhChassisNo() {
		return vhChassisNo;
	}

	/**
	 * @param vhChassisNo the vhChassisNo to set
	 */
	public void setVhChassisNo(String vhChassisNo) {
		this.vhChassisNo = vhChassisNo;
	}

	/**
	 * @return the vhIsMaintenance
	 */
	public Character getVhIsMaintenance() {
		return vhIsMaintenance;
	}

	/**
	 * @param vhIsMaintenance the vhIsMaintenance to set
	 */
	public void setVhIsMaintenance(Character vhIsMaintenance) {
		this.vhIsMaintenance = vhIsMaintenance;
	}

	/**
	 * @return the vhRemarks
	 */
	public String getVhRemarks() {
		return vhRemarks;
	}

	/**
	 * @param vhRemarks the vhRemarks to set
	 */
	public void setVhRemarks(String vhRemarks) {
		this.vhRemarks = vhRemarks;
	}

	/**
	 * @return the vhGpsImei
	 */
	public String getVhGpsImei() {
		return vhGpsImei;
	}

	/**
	 * @param vhGpsImei the vhGpsImei to set
	 */
	public void setVhGpsImei(String vhGpsImei) {
		this.vhGpsImei = vhGpsImei;
	}

	/**
	 * @return the vhStatus
	 */
	public Character getVhStatus() {
		return vhStatus;
	}

	/**
	 * @param vhStatus the vhStatus to set
	 */
	public void setVhStatus(Character vhStatus) {
		this.vhStatus = vhStatus;
	}

	/**
	 * @return the vhDtCreate
	 */
	public Date getVhDtCreate() {
		return vhDtCreate;
	}

	/**
	 * @param vhDtCreate the vhDtCreate to set
	 */
	public void setVhDtCreate(Date vhDtCreate) {
		this.vhDtCreate = vhDtCreate;
	}

	/**
	 * @return the vhUidCreate
	 */
	public String getVhUidCreate() {
		return vhUidCreate;
	}

	/**
	 * @param vhUidCreate the vhUidCreate to set
	 */
	public void setVhUidCreate(String vhUidCreate) {
		this.vhUidCreate = vhUidCreate;
	}

	/**
	 * @return the vhDtLupd
	 */
	public Date getVhDtLupd() {
		return vhDtLupd;
	}

	/**
	 * @param vhDtLupd the vhDtLupd to set
	 */
	public void setVhDtLupd(Date vhDtLupd) {
		this.vhDtLupd = vhDtLupd;
	}

	/**
	 * @return the vhUidLupd
	 */
	public String getVhUidLupd() {
		return vhUidLupd;
	}

	/**
	 * @param vhUidLupd the vhUidLupd to set
	 */
	public void setVhUidLupd(String vhUidLupd) {
		this.vhUidLupd = vhUidLupd;
	}

	public CkCtTrackDevice getTCkCtTrackDevice() {
		return TCkCtTrackDevice;
	}

	public void setTCkCtTrackDevice(CkCtTrackDevice tCkCtTrackDevice) {
		TCkCtTrackDevice = tCkCtTrackDevice;
	}

	/**
	 * @return the vhChassisNoOth
	 */
	public String getVhChassisNoOth() {
		return vhChassisNoOth;
	}

	/**
	 * @param vhChassisNoOth the vhChassisNoOth to set
	 */
	public void setVhChassisNoOth(String vhChassisNoOth) {
		this.vhChassisNoOth = vhChassisNoOth;
	}

	// Override Methods
	///////////////////
	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *
	 */
	@Override
	public int compareTo(CkCtVeh o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.COAbstractEntity#init()
	 *
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

	public CkCtDept getTCkCtDept() {
		return TCkCtDept;
	}

	public void setTCkCtDept(CkCtDept tCkCtDept) {
		TCkCtDept = tCkCtDept;
	}

	/**
	 * @return the maintenance
	 */
	public CkCtVehExt getMaintenance() {
		return maintenance;
	}

	/**
	 * @param maintenance the maintenance to set
	 */
	public void setMaintenance(CkCtVehExt maintenance) {
		this.maintenance = maintenance;
	}

	/**
	 * @return the expiry
	 */
	public CkCtVehExt getExpiry() {
		return expiry;
	}

	/**
	 * @param expiry the expiry to set
	 */
	public void setExpiry(CkCtVehExt expiry) {
		this.expiry = expiry;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
}
