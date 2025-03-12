package com.guudint.clickargo.clictruck.common.dto;

import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.common.model.TCkCtAccnInqReqDocs;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.dto.MstAttType;

public class CkCtAccnInqReqDocs extends AbstractDTO<CkCtAccnInqReqDocs, TCkCtAccnInqReqDocs> {

	private static final long serialVersionUID = 6616191880278344329L;
	private String airdId;
	private CkCtAccnInqReq TCkCtAccnInqReq;
	private MstAttType TMstAttType;
	private String airdFilename;
	private String airdDoc;
	private Character airdStatus;
	private Date airdDtCreate;
	private String airdUidCreate;
	private Date airdDtLupd;
	private String airdUidLupd;

	private byte[] data;
	private List<CkCtAccnInqReqDocs> listDocs;

	public CkCtAccnInqReqDocs() {
	}

	public CkCtAccnInqReqDocs(TCkCtAccnInqReqDocs entity) {
		super(entity);
	}

	public CkCtAccnInqReqDocs(String airdId, CkCtAccnInqReq TCkCtAccnInqReq) {
		this.airdId = airdId;
		this.TCkCtAccnInqReq = TCkCtAccnInqReq;
	}

	public CkCtAccnInqReqDocs(String airdId, CkCtAccnInqReq TCkCtAccnInqReq, MstAttType TMstAttType,
			String airdFilename, String airdDoc, Character airdStatus, Date airdDtCreate, String airdUidCreate,
			Date airdDtLupd, String airdUidLupd) {
		this.airdId = airdId;
		this.TCkCtAccnInqReq = TCkCtAccnInqReq;
		this.TMstAttType = TMstAttType;
		this.airdFilename = airdFilename;
		this.airdDoc = airdDoc;
		this.airdStatus = airdStatus;
		this.airdDtCreate = airdDtCreate;
		this.airdUidCreate = airdUidCreate;
		this.airdDtLupd = airdDtLupd;
		this.airdUidLupd = airdUidLupd;
	}

	/**
	 * @return the airdId
	 */
	public String getAirdId() {
		return airdId;
	}

	/**
	 * @param airdId the airdId to set
	 */
	public void setAirdId(String airdId) {
		this.airdId = airdId;
	}

	/**
	 * @return the tCkCtAccnInqReq
	 */
	public CkCtAccnInqReq getTCkCtAccnInqReq() {
		return TCkCtAccnInqReq;
	}

	/**
	 * @param tCkCtAccnInqReq the tCkCtAccnInqReq to set
	 */
	public void setTCkCtAccnInqReq(CkCtAccnInqReq tCkCtAccnInqReq) {
		TCkCtAccnInqReq = tCkCtAccnInqReq;
	}

	/**
	 * @return the tMstAttType
	 */
	public MstAttType getTMstAttType() {
		return TMstAttType;
	}

	/**
	 * @param tMstAttType the tMstAttType to set
	 */
	public void setTMstAttType(MstAttType tMstAttType) {
		TMstAttType = tMstAttType;
	}

	/**
	 * @return the airdFilename
	 */
	public String getAirdFilename() {
		return airdFilename;
	}

	/**
	 * @param airdFilename the airdFilename to set
	 */
	public void setAirdFilename(String airdFilename) {
		this.airdFilename = airdFilename;
	}

	/**
	 * @return the airdDoc
	 */
	public String getAirdDoc() {
		return airdDoc;
	}

	/**
	 * @param airdDoc the airdDoc to set
	 */
	public void setAirdDoc(String airdDoc) {
		this.airdDoc = airdDoc;
	}

	/**
	 * @return the airdStatus
	 */
	public Character getAirdStatus() {
		return airdStatus;
	}

	/**
	 * @param airdStatus the airdStatus to set
	 */
	public void setAirdStatus(Character airdStatus) {
		this.airdStatus = airdStatus;
	}

	/**
	 * @return the airdDtCreate
	 */
	public Date getAirdDtCreate() {
		return airdDtCreate;
	}

	/**
	 * @param airdDtCreate the airdDtCreate to set
	 */
	public void setAirdDtCreate(Date airdDtCreate) {
		this.airdDtCreate = airdDtCreate;
	}

	/**
	 * @return the airdUidCreate
	 */
	public String getAirdUidCreate() {
		return airdUidCreate;
	}

	/**
	 * @param airdUidCreate the airdUidCreate to set
	 */
	public void setAirdUidCreate(String airdUidCreate) {
		this.airdUidCreate = airdUidCreate;
	}

	/**
	 * @return the airdDtLupd
	 */
	public Date getAirdDtLupd() {
		return airdDtLupd;
	}

	/**
	 * @param airdDtLupd the airdDtLupd to set
	 */
	public void setAirdDtLupd(Date airdDtLupd) {
		this.airdDtLupd = airdDtLupd;
	}

	/**
	 * @return the airdUidLupd
	 */
	public String getAirdUidLupd() {
		return airdUidLupd;
	}

	/**
	 * @param airdUidLupd the airdUidLupd to set
	 */
	public void setAirdUidLupd(String airdUidLupd) {
		this.airdUidLupd = airdUidLupd;
	}

	@Override
	public int compareTo(CkCtAccnInqReqDocs o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * @return the listDocs
	 */
	public List<CkCtAccnInqReqDocs> getListDocs() {
		return listDocs;
	}

	/**
	 * @param listDocs the listDocs to set
	 */
	public void setListDocs(List<CkCtAccnInqReqDocs> listDocs) {
		this.listDocs = listDocs;
	}

}
