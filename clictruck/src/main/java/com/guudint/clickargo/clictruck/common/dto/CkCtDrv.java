package com.guudint.clickargo.clictruck.common.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtDrv extends AbstractDTO<CkCtDrv, TCkCtDrv> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 4861621968210618590L;

	// Attributes
	/////////////
	private String drvId;
	private CoreAccn TCoreAccn;
	private String drvName;
	private String drvState;
	private String drvLicenseNo;
	private Date drvLicenseExpiry;
	private String drvLicensePhotoName;
	private String drvLicensePhotoLoc;
	private String drvEmail;
	private String drvPhone;
	private String drvMobileId;
	private String drvMobilePassword;
	private String drvMobileLang;
	private Character drvStatus;
	private Date drvDtCreate;
	private String drvUidCreate;
	private Date drvDtLupd;
	private String drvUidLupd;
	private String base64File;
	
	private boolean drvEditPassword;

	// Constructors
	///////////////
	public CkCtDrv() {
	}

	public CkCtDrv(TCkCtDrv entity) {
		super(entity);
	}

	/**
	 * @param drvId
	 * @param tCkCtVeh
	 * @param tCoreAccn
	 * @param drvName
	 * @param drvLicenseNo
	 * @param drvLicenseExpiry
	 * @param drvLicensePhotoName
	 * @param drvLicensePhotoLoc
	 * @param drvEmail
	 * @param drvPhone
	 * @param drvMobileId
	 * @param drvMobilePassword
	 * @param drvStatus
	 * @param drvDtCreate
	 * @param drvUidCreate
	 * @param drvDtLupd
	 * @param drvUidLupd
	 */
	public CkCtDrv(String drvId, CoreAccn tCoreAccn, String drvName, String drvLicenseNo, Date drvLicenseExpiry,
			String drvLicensePhotoName, String drvLicensePhotoLoc, String drvEmail, String drvPhone, String drvMobileId,
			String drvMobilePassword, Character drvStatus, Date drvDtCreate, String drvUidCreate, Date drvDtLupd,
			String drvUidLupd) {
		super();
		this.drvId = drvId;
		TCoreAccn = tCoreAccn;
		this.drvName = drvName;
		this.drvLicenseNo = drvLicenseNo;
		this.drvLicenseExpiry = drvLicenseExpiry;
		this.drvLicensePhotoName = drvLicensePhotoName;
		this.drvLicensePhotoLoc = drvLicensePhotoLoc;
		this.drvEmail = drvEmail;
		this.drvPhone = drvPhone;
		this.drvMobileId = drvMobileId;
		this.drvMobilePassword = drvMobilePassword;
		this.drvStatus = drvStatus;
		this.drvDtCreate = drvDtCreate;
		this.drvUidCreate = drvUidCreate;
		this.drvDtLupd = drvDtLupd;
		this.drvUidLupd = drvUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the drvId
	 */
	public String getDrvId() {
		return drvId;
	}

	/**
	 * @param drvId the drvId to set
	 */
	public void setDrvId(String drvId) {
		this.drvId = drvId;
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
	 * @return the drvName
	 */
	public String getDrvName() {
		return drvName;
	}

	/**
	 * @param drvName the drvName to set
	 */
	public void setDrvName(String drvName) {
		this.drvName = drvName;
	}

	/**
	 * @return the drvLicenseNo
	 */
	public String getDrvLicenseNo() {
		return drvLicenseNo;
	}

	/**
	 * @param drvLicenseNo the drvLicenseNo to set
	 */
	public void setDrvLicenseNo(String drvLicenseNo) {
		this.drvLicenseNo = drvLicenseNo;
	}

	/**
	 * @return the drvLicenseExpiry
	 */
	public Date getDrvLicenseExpiry() {
		return drvLicenseExpiry;
	}

	/**
	 * @param drvLicenseExpiry the drvLicenseExpiry to set
	 */
	public void setDrvLicenseExpiry(Date drvLicenseExpiry) {
		this.drvLicenseExpiry = drvLicenseExpiry;
	}

	/**
	 * @return the drvLicensePhotoName
	 */
	public String getDrvLicensePhotoName() {
		return drvLicensePhotoName;
	}

	/**
	 * @param drvLicensePhotoName the drvLicensePhotoName to set
	 */
	public void setDrvLicensePhotoName(String drvLicensePhotoName) {
		this.drvLicensePhotoName = drvLicensePhotoName;
	}

	/**
	 * @return the drvLicensePhotoLoc
	 */
	public String getDrvLicensePhotoLoc() {
		return drvLicensePhotoLoc;
	}

	/**
	 * @param drvLicensePhotoLoc the drvLicensePhotoLoc to set
	 */
	public void setDrvLicensePhotoLoc(String drvLicensePhotoLoc) {
		this.drvLicensePhotoLoc = drvLicensePhotoLoc;
	}

	/**
	 * @return the drvEmail
	 */
	public String getDrvEmail() {
		return drvEmail;
	}

	/**
	 * @param drvEmail the drvEmail to set
	 */
	public void setDrvEmail(String drvEmail) {
		this.drvEmail = drvEmail;
	}

	/**
	 * @return the drvPhone
	 */
	public String getDrvPhone() {
		return drvPhone;
	}

	/**
	 * @param drvPhone the drvPhone to set
	 */
	public void setDrvPhone(String drvPhone) {
		this.drvPhone = drvPhone;
	}

	/**
	 * @return the drvMobileId
	 */
	public String getDrvMobileId() {
		return drvMobileId;
	}

	/**
	 * @param drvMobileId the drvMobileId to set
	 */
	public void setDrvMobileId(String drvMobileId) {
		this.drvMobileId = drvMobileId;
	}

	/**
	 * @return the drvMobilePassword
	 */
	public String getDrvMobilePassword() {
		return drvMobilePassword;
	}

	/**
	 * @param drvMobilePassword the drvMobilePassword to set
	 */
	public void setDrvMobilePassword(String drvMobilePassword) {
		this.drvMobilePassword = drvMobilePassword;
	}

	/**
	 * @return the drvStatus
	 */
	public Character getDrvStatus() {
		return drvStatus;
	}

	/**
	 * @param drvStatus the drvStatus to set
	 */
	public void setDrvStatus(Character drvStatus) {
		this.drvStatus = drvStatus;
	}

	/**
	 * @return the drvDtCreate
	 */
	public Date getDrvDtCreate() {
		return drvDtCreate;
	}

	/**
	 * @param drvDtCreate the drvDtCreate to set
	 */
	public void setDrvDtCreate(Date drvDtCreate) {
		this.drvDtCreate = drvDtCreate;
	}

	/**
	 * @return the drvUidCreate
	 */
	public String getDrvUidCreate() {
		return drvUidCreate;
	}

	/**
	 * @param drvUidCreate the drvUidCreate to set
	 */
	public void setDrvUidCreate(String drvUidCreate) {
		this.drvUidCreate = drvUidCreate;
	}

	/**
	 * @return the drvDtLupd
	 */
	public Date getDrvDtLupd() {
		return drvDtLupd;
	}

	/**
	 * @param drvDtLupd the drvDtLupd to set
	 */
	public void setDrvDtLupd(Date drvDtLupd) {
		this.drvDtLupd = drvDtLupd;
	}

	/**
	 * @return the drvUidLupd
	 */
	public String getDrvUidLupd() {
		return drvUidLupd;
	}

	/**
	 * @param drvUidLupd the drvUidLupd to set
	 */
	public void setDrvUidLupd(String drvUidLupd) {
		this.drvUidLupd = drvUidLupd;
	}

	/**
	 * @return the drvState
	 */
	public String getDrvState() {
		return drvState;
	}

	/**
	 * @param drvState the drvState to set
	 */
	public void setDrvState(String drvState) {
		this.drvState = drvState;
	}

	/**
	 * @return the drvMobileLang
	 */
	public String getDrvMobileLang() {
		return drvMobileLang;
	}

	/**
	 * @param drvMobileLang the drvMobileLang to set
	 */
	public void setDrvMobileLang(String drvMobileLang) {
		this.drvMobileLang = drvMobileLang;
	}

	/**
	 * @return the base64File
	 */
	public String getBase64File() {
		return base64File;
	}

	/**
	 * @param base64File the base64File to set
	 */
	public void setBase64File(String base64File) {
		this.base64File = base64File;
	}

	/**
	 * @return the drvEditPassword
	 */
	public boolean isDrvEditPassword() {
		return drvEditPassword;
	}

	/**
	 * @param drvEditPassword the drvEditPassword to set
	 */
	public void setDrvEditPassword(boolean drvEditPassword) {
		this.drvEditPassword = drvEditPassword;
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
	public int compareTo(CkCtDrv o) {
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

}
