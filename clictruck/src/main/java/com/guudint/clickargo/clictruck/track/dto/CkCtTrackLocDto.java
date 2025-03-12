package com.guudint.clickargo.clictruck.track.dto;
// Generated 9 Nov 2023, 2:02:16 pm by Hibernate Tools 4.3.6.Final

import java.util.Date;

import com.guudint.clickargo.clictruck.track.model.VCkCtTrackLoc;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtTrackLocDto extends AbstractDTO<CkCtTrackLocDto, VCkCtTrackLoc> {

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

	private CoreAccn TCoreAccnCO;
	private CoreAccn TCoreAccnTO;

	private int timeGapMinute; // from T_CORE_SYSPARAM, between schedulerTime and tliDtEnter30

	public CkCtTrackLocDto() {
	}

	public CkCtTrackLocDto(VCkCtTrackLoc entity) {
		super(entity);
	}

	//////////////
	public boolean isInSchedulerTime() {

		if (schedulerTime != null && tliDtEnter30 != null) {
			long interval = Math.abs(schedulerTime.getTime() - tliDtEnter30.getTime());
			return interval < timeGapMinute * 60 * 1000;  // in time gap between scheduler and enter 30 meters 
		}
		return true;
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(CkCtTrackLocDto arg0) {
		return 0;
	}

	@Override
	public void init() {
	}

	public String getTrJob() {
		return this.trJob;
	}

	public void setTrJob(String trJob) {
		this.trJob = trJob;
	}

	public String getJobPartyTo() {
		return this.jobPartyTo;
	}

	public void setJobPartyTo(String jobPartyTo) {
		this.jobPartyTo = jobPartyTo;
	}

	public String getJobPartyCoFf() {
		return this.jobPartyCoFf;
	}

	public void setJobPartyCoFf(String jobPartyCoFf) {
		this.jobPartyCoFf = jobPartyCoFf;
	}

	public CoreAccn getTCoreAccnCO() {
		return TCoreAccnCO;
	}

	public void setTCoreAccnCO(CoreAccn tCoreAccnCO) {
		TCoreAccnCO = tCoreAccnCO;
	}

	public CoreAccn getTCoreAccnTO() {
		return TCoreAccnTO;
	}

	public void setTCoreAccnTO(CoreAccn tCoreAccnTO) {
		TCoreAccnTO = tCoreAccnTO;
	}

	public Date getRcdDtBillApproved() {
		return this.rcdDtBillApproved;
	}

	public void setRcdDtBillApproved(Date rcdDtBillApproved) {
		this.rcdDtBillApproved = rcdDtBillApproved;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getSchedulerTime() {
		return this.schedulerTime;
	}

	public void setSchedulerTime(Date schedulerTime) {
		this.schedulerTime = schedulerTime;
	}

	public Date getTliDtEnter10() {
		return this.tliDtEnter10;
	}

	public void setTliDtEnter10(Date tliDtEnter10) {
		this.tliDtEnter10 = tliDtEnter10;
	}

	public Date getTliDtExit10() {
		return this.tliDtExit10;
	}

	public void setTliDtExit10(Date tliDtExit10) {
		this.tliDtExit10 = tliDtExit10;
	}

	public Date getTliDtEnter30() {
		return this.tliDtEnter30;
	}

	public void setTliDtEnter30(Date tliDtEnter30) {
		this.tliDtEnter30 = tliDtEnter30;
	}

	public Date getTliDtExit30() {
		return this.tliDtExit30;
	}

	public void setTliDtExit30(Date tliDtExit30) {
		this.tliDtExit30 = tliDtExit30;
	}

	public Date getTliDtEnter100() {
		return this.tliDtEnter100;
	}

	public void setTliDtEnter100(Date tliDtEnter100) {
		this.tliDtEnter100 = tliDtEnter100;
	}

	public Date getTliDtExit100() {
		return this.tliDtExit100;
	}

	public void setTliDtExit100(Date tliDtExit100) {
		this.tliDtExit100 = tliDtExit100;
	}

	public int getTimeGapMinute() {
		return timeGapMinute;
	}

	public void setTimeGapMinute(int timeGapMinute) {
		this.timeGapMinute = timeGapMinute;
	}

}
