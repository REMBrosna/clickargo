package com.guudint.clickargo.clictruck.common.dto;

import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.common.model.TCkCtDept;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreUsr;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtDept extends AbstractDTO<CkCtDept, TCkCtDept> {

	private static final long serialVersionUID = 7854152766012316998L;
	private String deptId;
	private CoreAccn TCoreAccn;
	private String deptName;
	private String deptDesc;
	private Character deptColor;
	private Character deptStatus;
	private Date deptDtCreate;
	private String deptUidCreate;
	private Date deptDtLupd;
	private String deptUidLupd;

	private String history;

	// place holder for the users that belongs to this department
	private List<CoreUsr> deptUsers;
	// place holder for the users that belongs to this account
	private List<CoreUsr> accnUsers;

	// place holder for the trucks that belongs to this department
	private List<CkCtVeh> deptVehs;
	// place holder for the vehs that belongs to this account
	private List<CkCtVeh> accnVehs;

	public CkCtDept() {
	}

	public CkCtDept(TCkCtDept entity) {
		super(entity);
	}

	public CkCtDept(String deptId, CoreAccn TCoreAccn, String deptName, String deptDesc, Character deptColor,
			Character deptStatus, Date deptDtCreate, String deptUidCreate, Date deptDtLupd, String deptUidLupd) {
		this.deptId = deptId;
		this.TCoreAccn = TCoreAccn;
		this.deptName = deptName;
		this.deptDesc = deptDesc;
		this.deptColor = deptColor;
		this.deptStatus = deptStatus;
		this.deptDtCreate = deptDtCreate;
		this.deptUidCreate = deptUidCreate;
		this.deptDtLupd = deptDtLupd;
		this.deptUidLupd = deptUidLupd;
	}

	/**
	 * @return the deptId
	 */
	public String getDeptId() {
		return deptId;
	}

	/**
	 * @param deptId the deptId to set
	 */
	public void setDeptId(String deptId) {
		this.deptId = deptId;
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
	 * @return the deptName
	 */
	public String getDeptName() {
		return deptName;
	}

	/**
	 * @param deptName the deptName to set
	 */
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	/**
	 * @return the deptDesc
	 */
	public String getDeptDesc() {
		return deptDesc;
	}

	/**
	 * @param deptDesc the deptDesc to set
	 */
	public void setDeptDesc(String deptDesc) {
		this.deptDesc = deptDesc;
	}

	/**
	 * @return the deptColor
	 */
	public Character getDeptColor() {
		return deptColor;
	}

	/**
	 * @param deptColor the deptColor to set
	 */
	public void setDeptColor(Character deptColor) {
		this.deptColor = deptColor;
	}

	/**
	 * @return the deptStatus
	 */
	public Character getDeptStatus() {
		return deptStatus;
	}

	/**
	 * @param deptStatus the deptStatus to set
	 */
	public void setDeptStatus(Character deptStatus) {
		this.deptStatus = deptStatus;
	}

	/**
	 * @return the deptDtCreate
	 */
	public Date getDeptDtCreate() {
		return deptDtCreate;
	}

	/**
	 * @param deptDtCreate the deptDtCreate to set
	 */
	public void setDeptDtCreate(Date deptDtCreate) {
		this.deptDtCreate = deptDtCreate;
	}

	/**
	 * @return the deptUidCreate
	 */
	public String getDeptUidCreate() {
		return deptUidCreate;
	}

	/**
	 * @param deptUidCreate the deptUidCreate to set
	 */
	public void setDeptUidCreate(String deptUidCreate) {
		this.deptUidCreate = deptUidCreate;
	}

	/**
	 * @return the deptDtLupd
	 */
	public Date getDeptDtLupd() {
		return deptDtLupd;
	}

	/**
	 * @param deptDtLupd the deptDtLupd to set
	 */
	public void setDeptDtLupd(Date deptDtLupd) {
		this.deptDtLupd = deptDtLupd;
	}

	/**
	 * @return the deptUidLupd
	 */
	public String getDeptUidLupd() {
		return deptUidLupd;
	}

	/**
	 * @param deptUidLupd the deptUidLupd to set
	 */
	public void setDeptUidLupd(String deptUidLupd) {
		this.deptUidLupd = deptUidLupd;
	}

	@Override
	public int compareTo(CkCtDept o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

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
	 * @return the deptUsers
	 */
	public List<CoreUsr> getDeptUsers() {
		return deptUsers;
	}

	/**
	 * @param deptUsers the deptUsers to set
	 */
	public void setDeptUsers(List<CoreUsr> deptUsers) {
		this.deptUsers = deptUsers;
	}

	/**
	 * @return the accnUsers
	 */
	public List<CoreUsr> getAccnUsers() {
		return accnUsers;
	}

	/**
	 * @param accnUsers the accnUsers to set
	 */
	public void setAccnUsers(List<CoreUsr> accnUsers) {
		this.accnUsers = accnUsers;
	}

	/**
	 * @return the deptVehs
	 */
	public List<CkCtVeh> getDeptVehs() {
		return deptVehs;
	}

	/**
	 * @param deptVehs the deptVehs to set
	 */
	public void setDeptVehs(List<CkCtVeh> deptVehs) {
		this.deptVehs = deptVehs;
	}

	/**
	 * @return the accnVehs
	 */
	public List<CkCtVeh> getAccnVehs() {
		return accnVehs;
	}

	/**
	 * @param accnVehs the accnVehs to set
	 */
	public void setAccnVehs(List<CkCtVeh> accnVehs) {
		this.accnVehs = accnVehs;
	}

}
