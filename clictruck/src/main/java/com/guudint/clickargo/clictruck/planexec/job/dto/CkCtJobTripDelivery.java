package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTripDelivery;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtJobTripDelivery extends AbstractDTO<CkCtJobTripDelivery, TCkCtJobTripDelivery>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2827016322308984262L;
	
	public static final String PREFIX_ID = "CTJTD";
	
	private String jtdId;
	private String jtdJobId;
	private String jtdJobTrip;
	private String jtdImei;
	private Date jtdDtDeliver;
	private Date jtdDtNotifiedDeliver;
	private Date jtdDtLastScan;
	private String jtdOriginLoc;
	private String jtdDestLoc;
	private Integer jtdDistance;
	private Double jtdDuration;
	private Date jtdDtPreNotify;
	private String jtdStatus;
	private Date jtdDtCreate;
	private Date jtdDtLupd;

	public CkCtJobTripDelivery() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CkCtJobTripDelivery(TCkCtJobTripDelivery entity) {
		super(entity);
		// TODO Auto-generated constructor stub
	}

	public CkCtJobTripDelivery(String jtdId, String jtdJobId, String jtdJobTrip, String jtdImei, Date jtdDtDeliver,
			Date jtdDtNotifiedDeliver, Date jtdDtLastScan, String jtdOriginLoc, String jtdDestLoc, Integer jtdDistance,
			Double jtdDuration, Date jtdDtPreNotify, String jtdStatus, Date jtdDtCreate, Date jtdDtLupd) {
		super();
		this.jtdId = jtdId;
		this.jtdJobId = jtdJobId;
		this.jtdJobTrip = jtdJobTrip;
		this.jtdImei = jtdImei;
		this.jtdDtDeliver = jtdDtDeliver;
		this.jtdDtNotifiedDeliver = jtdDtNotifiedDeliver;
		this.jtdDtLastScan = jtdDtLastScan;
		this.jtdOriginLoc = jtdOriginLoc;
		this.jtdDestLoc = jtdDestLoc;
		this.jtdDistance = jtdDistance;
		this.jtdDuration = jtdDuration;
		this.jtdDtPreNotify = jtdDtPreNotify;
		this.jtdStatus = jtdStatus;
		this.jtdDtCreate = jtdDtCreate;
		this.jtdDtLupd = jtdDtLupd;
	}

	public String getJtdId() {
		return jtdId;
	}

	public void setJtdId(String jtdId) {
		this.jtdId = jtdId;
	}

	public String getJtdJobId() {
		return jtdJobId;
	}

	public void setJtdJobId(String jtdJobId) {
		this.jtdJobId = jtdJobId;
	}

	public String getJtdJobTrip() {
		return jtdJobTrip;
	}

	public void setJtdJobTrip(String jtdJobTrip) {
		this.jtdJobTrip = jtdJobTrip;
	}

	public String getJtdImei() {
		return jtdImei;
	}

	public void setJtdImei(String jtdImei) {
		this.jtdImei = jtdImei;
	}

	public Date getJtdDtDeliver() {
		return jtdDtDeliver;
	}

	public void setJtdDtDeliver(Date jtdDtDeliver) {
		this.jtdDtDeliver = jtdDtDeliver;
	}

	public Date getJtdDtNotifiedDeliver() {
		return jtdDtNotifiedDeliver;
	}

	public void setJtdDtNotifiedDeliver(Date jtdDtNotifiedDeliver) {
		this.jtdDtNotifiedDeliver = jtdDtNotifiedDeliver;
	}

	public Date getJtdDtLastScan() {
		return jtdDtLastScan;
	}

	public void setJtdDtLastScan(Date jtdDtLastScan) {
		this.jtdDtLastScan = jtdDtLastScan;
	}

	public String getJtdOriginLoc() {
		return jtdOriginLoc;
	}

	public void setJtdOriginLoc(String jtdOriginLoc) {
		this.jtdOriginLoc = jtdOriginLoc;
	}

	public String getJtdDestLoc() {
		return jtdDestLoc;
	}

	public void setJtdDestLoc(String jtdDestLoc) {
		this.jtdDestLoc = jtdDestLoc;
	}

	public Integer getJtdDistance() {
		return jtdDistance;
	}

	public void setJtdDistance(Integer jtdDistance) {
		this.jtdDistance = jtdDistance;
	}

	public Double getJtdDuration() {
		return jtdDuration;
	}

	public void setJtdDuration(Double jtdDuration) {
		this.jtdDuration = jtdDuration;
	}

	public Date getJtdDtPreNotify() {
		return jtdDtPreNotify;
	}

	public void setJtdDtPreNotify(Date jtdDtPreNotify) {
		this.jtdDtPreNotify = jtdDtPreNotify;
	}

	public String getJtdStatus() {
		return jtdStatus;
	}

	public void setJtdStatus(String jtdStatus) {
		this.jtdStatus = jtdStatus;
	}

	public Date getJtdDtCreate() {
		return jtdDtCreate;
	}

	public void setJtdDtCreate(Date jtdDtCreate) {
		this.jtdDtCreate = jtdDtCreate;
	}

	public Date getJtdDtLupd() {
		return jtdDtLupd;
	}

	public void setJtdDtLupd(Date jtdDtLupd) {
		this.jtdDtLupd = jtdDtLupd;
	}

	@Override
	public int compareTo(CkCtJobTripDelivery o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
