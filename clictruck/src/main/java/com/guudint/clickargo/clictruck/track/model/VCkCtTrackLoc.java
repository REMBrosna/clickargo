package com.guudint.clickargo.clictruck.track.model;
// Generated 9 Nov 2023, 2:02:16 pm by Hibernate Tools 4.3.6.Final

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.COAbstractEntity;

/**
 * VCkTrackLoc generated by hbm2java
 */
@Entity
@Table(name = "V_CK_CT_TRACK_LOC")
public class VCkCtTrackLoc  extends COAbstractEntity<VCkCtTrackLoc> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1L;

	private String jobPartyTo;
	private String jobPartyCoFf;
	private Date rcdDtBillApproved;
	private String trJob;
	private String address;
	private Date schedulerTime;
	private Date tliDtEnter10;
	private Date tliDtExit10;
	private Date tliDtEnter30;
	private Date tliDtExit30;
	private Date tliDtEnter100;
	private Date tliDtExit100;
	
	private TCoreAccn TCoreAccnCO;
	private TCoreAccn TCoreAccnTO;

	public VCkCtTrackLoc() {
	}


	// Override Methods
	///////////////////
	@Override
	public int compareTo(VCkCtTrackLoc arg0) {
		return 0;
	}

	@Override
	public void init() {
	}
	
	@Id
	@Column(name = "TR_JOB", length = 35)
	public String getTrJob() {
		return this.trJob;
	}

	public void setTrJob(String trJob) {
		this.trJob = trJob;
	}
	
	@Column(name = "JOB_PARTY_TO", length = 35)
	public String getJobPartyTo() {
		return this.jobPartyTo;
	}

	public void setJobPartyTo(String jobPartyTo) {
		this.jobPartyTo = jobPartyTo;
	}

	@Column(name = "JOB_PARTY_CO_FF", length = 35)
	public String getJobPartyCoFf() {
		return this.jobPartyCoFf;
	}

	public void setJobPartyCoFf(String jobPartyCoFf) {
		this.jobPartyCoFf = jobPartyCoFf;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "JOB_PARTY_CO_FF", insertable = false, updatable = false )
	public TCoreAccn getTCoreAccnCO() {
		return TCoreAccnCO;
	}


	public void setTCoreAccnCO(TCoreAccn tCoreAccnCO) {
		TCoreAccnCO = tCoreAccnCO;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "JOB_PARTY_TO", insertable = false, updatable = false )
	public TCoreAccn getTCoreAccnTO() {
		return TCoreAccnTO;
	}


	public void setTCoreAccnTO(TCoreAccn tCoreAccnTO) {
		TCoreAccnTO = tCoreAccnTO;
	}


	@Column(name = "RCD_DT_BILL_APPROVED", length = 19)
	public Date getRcdDtBillApproved() {
		return this.rcdDtBillApproved;
	}

	public void setRcdDtBillApproved(Date rcdDtBillApproved) {
		this.rcdDtBillApproved = rcdDtBillApproved;
	}

	@Column(name = "ADDRESS", length = 16777215)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "SCHEDULER_TIME", length = 19)
	public Date getSchedulerTime() {
		return this.schedulerTime;
	}

	public void setSchedulerTime(Date schedulerTime) {
		this.schedulerTime = schedulerTime;
	}

	@Column(name = "TLI_DT_ENTER_10", length = 19)
	public Date getTliDtEnter10() {
		return this.tliDtEnter10;
	}

	public void setTliDtEnter10(Date tliDtEnter10) {
		this.tliDtEnter10 = tliDtEnter10;
	}

	@Column(name = "TLI_DT_EXIT_10", length = 19)
	public Date getTliDtExit10() {
		return this.tliDtExit10;
	}

	public void setTliDtExit10(Date tliDtExit10) {
		this.tliDtExit10 = tliDtExit10;
	}

	@Column(name = "TLI_DT_ENTER_30", length = 19)
	public Date getTliDtEnter30() {
		return this.tliDtEnter30;
	}

	public void setTliDtEnter30(Date tliDtEnter30) {
		this.tliDtEnter30 = tliDtEnter30;
	}

	@Column(name = "TLI_DT_EXIT_30", length = 19)
	public Date getTliDtExit30() {
		return this.tliDtExit30;
	}

	public void setTliDtExit30(Date tliDtExit30) {
		this.tliDtExit30 = tliDtExit30;
	}

	@Column(name = "TLI_DT_ENTER_100", length = 19)
	public Date getTliDtEnter100() {
		return this.tliDtEnter100;
	}

	public void setTliDtEnter100(Date tliDtEnter100) {
		this.tliDtEnter100 = tliDtEnter100;
	}

	@Column(name = "TLI_DT_EXIT_100", length = 19)
	public Date getTliDtExit100() {
		return this.tliDtExit100;
	}

	public void setTliDtExit100(Date tliDtExit100) {
		this.tliDtExit100 = tliDtExit100;
	}


}
