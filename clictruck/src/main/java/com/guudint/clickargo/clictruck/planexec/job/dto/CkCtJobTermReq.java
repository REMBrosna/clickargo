package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTermReq;
import com.guudint.clickargo.common.enums.JobActions;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtJobTermReq extends AbstractDTO<CkCtJobTermReq, TCkCtJobTermReq> {

	private static final long serialVersionUID = 5335667404762767501L;
	private String jtrId;
	private String jtrState;
	
	private CoreAccn TCoreAccn;
	private Double jtrCrLimit;
	private Double jtrCrBal;
	private Integer jtrInvOpen;
	private Double jtrAmtOpen;
	private Date jtrDtDueInv;
	private Short jtrNoJobs;
	private BigDecimal jtrJobsPltfeeAmt;
	private BigDecimal jtrJobsDnAmt;
	private BigDecimal jtrJobsDefault;
	private String jtrUidRequestor;
	private String jtrCommentRequestor;
	private Date jtrDtSubmit;
	private String jtrUidApprover;
	private String jtrCommentApprover;
	private Date jtrDtApproveReject;
	private String jtrComment;
	private Character jtrStatus;
	private Date jtrDtCreate;
	private String jtrUidCreate;
	private Date jtrDtLupd;
	private String jtrUidLupd;


	private JobActions action;
	private String history;

	public CkCtJobTermReq() {
	}

	public CkCtJobTermReq(TCkCtJobTermReq entity) {
		super(entity);
	}

	public CkCtJobTermReq(String jtrId, CoreAccn TCoreAccn) {
		this.jtrId = jtrId;
		this.TCoreAccn = TCoreAccn;
	}

	public CkCtJobTermReq(String jtrId, CoreAccn TCoreAccn, Double jtrCrLimit, Double jtrCrBal, Integer jtrInvOpen,
			Double jtrAmtOpen, Date jtrDtDueInv, Short jtrNoJobs, BigDecimal jtrJobsPltfeeAmt, BigDecimal jtrJobsDnAmt,
			BigDecimal jtrJobsDefault, String jtrUidRequestor, String jtrCommentRequestor, Date jtrDtSubmit,
			String jtrUidApprover, String jtrCommentApprover, Date jtrDtApproveReject, String jtrComment,
			Character jtrStatus, Date jtrDtCreate, String jtrUidCreate, Date jtrDtLupd, String jtrUidLupd) {
		this.jtrId = jtrId;
		this.TCoreAccn = TCoreAccn;
		this.jtrCrLimit = jtrCrLimit;
		this.jtrCrBal = jtrCrBal;
		this.jtrInvOpen = jtrInvOpen;
		this.jtrAmtOpen = jtrAmtOpen;
		this.jtrDtDueInv = jtrDtDueInv;
		this.jtrNoJobs = jtrNoJobs;
		this.jtrJobsPltfeeAmt = jtrJobsPltfeeAmt;
		this.jtrJobsDnAmt = jtrJobsDnAmt;
		this.jtrJobsDefault = jtrJobsDefault;
		this.jtrUidRequestor = jtrUidRequestor;
		this.jtrCommentRequestor = jtrCommentRequestor;
		this.jtrDtSubmit = jtrDtSubmit;
		this.jtrUidApprover = jtrUidApprover;
		this.jtrCommentApprover = jtrCommentApprover;
		this.jtrDtApproveReject = jtrDtApproveReject;
		this.jtrComment = jtrComment;
		this.jtrStatus = jtrStatus;
		this.jtrDtCreate = jtrDtCreate;
		this.jtrUidCreate = jtrUidCreate;
		this.jtrDtLupd = jtrDtLupd;
		this.jtrUidLupd = jtrUidLupd;
	}

	/**
	 * @return the jtrId
	 */
	public String getJtrId() {
		return jtrId;
	}

	/**
	 * @param jtrId the jtrId to set
	 */
	public void setJtrId(String jtrId) {
		this.jtrId = jtrId;
	}

	public String getJtrState() {
		return jtrState;
	}

	public void setJtrState(String jtrState) {
		this.jtrState = jtrState;
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
	 * @return the jtrCrLimit
	 */
	public Double getJtrCrLimit() {
		return jtrCrLimit;
	}

	/**
	 * @param jtrCrLimit the jtrCrLimit to set
	 */
	public void setJtrCrLimit(Double jtrCrLimit) {
		this.jtrCrLimit = jtrCrLimit;
	}

	/**
	 * @return the jtrCrBal
	 */
	public Double getJtrCrBal() {
		return jtrCrBal;
	}

	/**
	 * @param jtrCrBal the jtrCrBal to set
	 */
	public void setJtrCrBal(Double jtrCrBal) {
		this.jtrCrBal = jtrCrBal;
	}

	/**
	 * @return the jtrInvOpen
	 */
	public Integer getJtrInvOpen() {
		return jtrInvOpen;
	}

	/**
	 * @param jtrInvOpen the jtrInvOpen to set
	 */
	public void setJtrInvOpen(Integer jtrInvOpen) {
		this.jtrInvOpen = jtrInvOpen;
	}

	/**
	 * @return the jtrAmtOpen
	 */
	public Double getJtrAmtOpen() {
		return jtrAmtOpen;
	}

	/**
	 * @param jtrAmtOpen the jtrAmtOpen to set
	 */
	public void setJtrAmtOpen(Double jtrAmtOpen) {
		this.jtrAmtOpen = jtrAmtOpen;
	}

	/**
	 * @return the jtrDtDueInv
	 */
	public Date getJtrDtDueInv() {
		return jtrDtDueInv;
	}

	/**
	 * @param jtrDtDueInv the jtrDtDueInv to set
	 */
	public void setJtrDtDueInv(Date jtrDtDueInv) {
		this.jtrDtDueInv = jtrDtDueInv;
	}

	/**
	 * @return the jtrNoJobs
	 */
	public Short getJtrNoJobs() {
		return jtrNoJobs;
	}

	/**
	 * @param jtrNoJobs the jtrNoJobs to set
	 */
	public void setJtrNoJobs(Short jtrNoJobs) {
		this.jtrNoJobs = jtrNoJobs;
	}

	/**
	 * @return the jtrJobsPltfeeAmt
	 */
	public BigDecimal getJtrJobsPltfeeAmt() {
		return jtrJobsPltfeeAmt;
	}

	/**
	 * @param jtrJobsPltfeeAmt the jtrJobsPltfeeAmt to set
	 */
	public void setJtrJobsPltfeeAmt(BigDecimal jtrJobsPltfeeAmt) {
		this.jtrJobsPltfeeAmt = jtrJobsPltfeeAmt;
	}

	/**
	 * @return the jtrJobsDnAmt
	 */
	public BigDecimal getJtrJobsDnAmt() {
		return jtrJobsDnAmt;
	}

	/**
	 * @param jtrJobsDnAmt the jtrJobsDnAmt to set
	 */
	public void setJtrJobsDnAmt(BigDecimal jtrJobsDnAmt) {
		this.jtrJobsDnAmt = jtrJobsDnAmt;
	}

	/**
	 * @return the jtrJobsDefault
	 */
	public BigDecimal getJtrJobsDefault() {
		return jtrJobsDefault;
	}

	/**
	 * @param jtrJobsDefault the jtrJobsDefault to set
	 */
	public void setJtrJobsDefault(BigDecimal jtrJobsDefault) {
		this.jtrJobsDefault = jtrJobsDefault;
	}

	/**
	 * @return the jtrUidRequestor
	 */
	public String getJtrUidRequestor() {
		return jtrUidRequestor;
	}

	/**
	 * @param jtrUidRequestor the jtrUidRequestor to set
	 */
	public void setJtrUidRequestor(String jtrUidRequestor) {
		this.jtrUidRequestor = jtrUidRequestor;
	}

	/**
	 * @return the jtrCommentRequestor
	 */
	public String getJtrCommentRequestor() {
		return jtrCommentRequestor;
	}

	/**
	 * @param jtrCommentRequestor the jtrCommentRequestor to set
	 */
	public void setJtrCommentRequestor(String jtrCommentRequestor) {
		this.jtrCommentRequestor = jtrCommentRequestor;
	}

	/**
	 * @return the jtrDtSubmit
	 */
	public Date getJtrDtSubmit() {
		return jtrDtSubmit;
	}

	/**
	 * @param jtrDtSubmit the jtrDtSubmit to set
	 */
	public void setJtrDtSubmit(Date jtrDtSubmit) {
		this.jtrDtSubmit = jtrDtSubmit;
	}

	/**
	 * @return the jtrUidApprover
	 */
	public String getJtrUidApprover() {
		return jtrUidApprover;
	}

	/**
	 * @param jtrUidApprover the jtrUidApprover to set
	 */
	public void setJtrUidApprover(String jtrUidApprover) {
		this.jtrUidApprover = jtrUidApprover;
	}

	/**
	 * @return the jtrCommentApprover
	 */
	public String getJtrCommentApprover() {
		return jtrCommentApprover;
	}

	/**
	 * @param jtrCommentApprover the jtrCommentApprover to set
	 */
	public void setJtrCommentApprover(String jtrCommentApprover) {
		this.jtrCommentApprover = jtrCommentApprover;
	}

	/**
	 * @return the jtrDtApproveReject
	 */
	public Date getJtrDtApproveReject() {
		return jtrDtApproveReject;
	}

	/**
	 * @param jtrDtApproveReject the jtrDtApproveReject to set
	 */
	public void setJtrDtApproveReject(Date jtrDtApproveReject) {
		this.jtrDtApproveReject = jtrDtApproveReject;
	}

	/**
	 * @return the jtrComment
	 */
	public String getJtrComment() {
		return jtrComment;
	}

	/**
	 * @param jtrComment the jtrComment to set
	 */
	public void setJtrComment(String jtrComment) {
		this.jtrComment = jtrComment;
	}

	/**
	 * @return the jtrStatus
	 */
	public Character getJtrStatus() {
		return jtrStatus;
	}

	/**
	 * @param jtrStatus the jtrStatus to set
	 */
	public void setJtrStatus(Character jtrStatus) {
		this.jtrStatus = jtrStatus;
	}

	/**
	 * @return the jtrDtCreate
	 */
	public Date getJtrDtCreate() {
		return jtrDtCreate;
	}

	/**
	 * @param jtrDtCreate the jtrDtCreate to set
	 */
	public void setJtrDtCreate(Date jtrDtCreate) {
		this.jtrDtCreate = jtrDtCreate;
	}

	/**
	 * @return the jtrUidCreate
	 */
	public String getJtrUidCreate() {
		return jtrUidCreate;
	}

	/**
	 * @param jtrUidCreate the jtrUidCreate to set
	 */
	public void setJtrUidCreate(String jtrUidCreate) {
		this.jtrUidCreate = jtrUidCreate;
	}

	/**
	 * @return the jtrDtLupd
	 */
	public Date getJtrDtLupd() {
		return jtrDtLupd;
	}

	/**
	 * @param jtrDtLupd the jtrDtLupd to set
	 */
	public void setJtrDtLupd(Date jtrDtLupd) {
		this.jtrDtLupd = jtrDtLupd;
	}

	/**
	 * @return the jtrUidLupd
	 */
	public String getJtrUidLupd() {
		return jtrUidLupd;
	}

	/**
	 * @param jtrUidLupd the jtrUidLupd to set
	 */
	public void setJtrUidLupd(String jtrUidLupd) {
		this.jtrUidLupd = jtrUidLupd;
	}

	public JobActions getAction() {
		return action;
	}

	public void setAction(JobActions action) {
		this.action = action;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	@Override
	public int compareTo(CkCtJobTermReq o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
