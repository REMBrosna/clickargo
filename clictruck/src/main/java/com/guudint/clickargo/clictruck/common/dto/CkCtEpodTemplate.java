package com.guudint.clickargo.clictruck.common.dto;
// Generated 7 Oct 2024, 5:36:51 pm by Hibernate Tools 4.3.6.Final

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.guudint.clickargo.clictruck.common.model.TCkCtEpodTemplate;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtEpodTemplate extends AbstractDTO<CkCtEpodTemplate, TCkCtEpodTemplate> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;

	private String epodId;
	private String epodAccnFf;
	private String epodAccnTo;
	private String epodAccnCo;
	private String epodEpodJrxml;
	private String epodEpodLogo;
	private String epodEpodSubReport;
	private String epodEpodService;
	private char epodStatus;
	private Date epodDtCreate;
	private String epodUidCreate;
	private Date epodDtLupd;
	private String epodUidLupd;

	// Constructors
	///////////////
	public CkCtEpodTemplate() {
	}

	public CkCtEpodTemplate(String epodId) {
		this.epodId = epodId;
	}

	public CkCtEpodTemplate(TCkCtEpodTemplate entity) {
		super(entity);
	}

	// Override Methods
	///////////////////
	@Override
	public void init() {
	}
	
	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *
	 */
	@Override
	public int compareTo(CkCtEpodTemplate o) {
		return 0;
	}

	// Properties
	/////////////
	@Id

	@Column(name = "EPOD_ID", unique = true, nullable = false, length = 70)
	public String getEpodId() {
		return this.epodId;
	}

	public void setEpodId(String epodId) {
		this.epodId = epodId;
	}

	@Column(name = "EPOD_ACCN_FF", length = 70)
	public String getEpodAccnFf() {
		return this.epodAccnFf;
	}

	public void setEpodAccnFf(String epodAccnFf) {
		this.epodAccnFf = epodAccnFf;
	}

	@Column(name = "EPOD_ACCN_TO", length = 70)
	public String getEpodAccnTo() {
		return this.epodAccnTo;
	}

	public void setEpodAccnTo(String epodAccnTo) {
		this.epodAccnTo = epodAccnTo;
	}

	@Column(name = "EPOD_ACCN_CO", length = 70)
	public String getEpodAccnCo() {
		return this.epodAccnCo;
	}

	public void setEpodAccnCo(String epodAccnCo) {
		this.epodAccnCo = epodAccnCo;
	}

	@Column(name = "EPOD_EPOD_JRXML", length = 256)
	public String getEpodEpodJrxml() {
		return this.epodEpodJrxml;
	}

	public void setEpodEpodJrxml(String epodEpodJrxml) {
		this.epodEpodJrxml = epodEpodJrxml;
	}

	@Column(name = "EPOD_EPOD_LOGO", length = 256)
	public String getEpodEpodLogo() {
		return epodEpodLogo;
	}

	public void setEpodEpodLogo(String epodEpodLogo) {
		this.epodEpodLogo = epodEpodLogo;
	}

	@Column(name = "EPOD_EPOD_SUB_REPORT", length = 256)
	public String getEpodEpodSubReport() {
		return epodEpodSubReport;
	}

	public void setEpodEpodSubReport(String epodEpodSubReport) {
		this.epodEpodSubReport = epodEpodSubReport;
	}

	@Column(name = "EPOD_EPOD_SERVICE", length = 256)
	public String getEpodEpodService() {
		return this.epodEpodService;
	}

	public void setEpodEpodService(String epodEpodService) {
		this.epodEpodService = epodEpodService;
	}

	@Column(name = "EPOD_STATUS", nullable = false, length = 1)
	public char getEpodStatus() {
		return this.epodStatus;
	}

	public void setEpodStatus(char epodStatus) {
		this.epodStatus = epodStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EPOD_DT_CREATE", nullable = false, length = 19)
	public Date getEpodDtCreate() {
		return this.epodDtCreate;
	}

	public void setEpodDtCreate(Date epodDtCreate) {
		this.epodDtCreate = epodDtCreate;
	}

	@Column(name = "EPOD_UID_CREATE", nullable = false, length = 35)
	public String getEpodUidCreate() {
		return this.epodUidCreate;
	}

	public void setEpodUidCreate(String epodUidCreate) {
		this.epodUidCreate = epodUidCreate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EPOD_DT_LUPD", length = 19)
	public Date getEpodDtLupd() {
		return this.epodDtLupd;
	}

	public void setEpodDtLupd(Date epodDtLupd) {
		this.epodDtLupd = epodDtLupd;
	}

	@Column(name = "EPOD_UID_LUPD", length = 35)
	public String getEpodUidLupd() {
		return this.epodUidLupd;
	}

	public void setEpodUidLupd(String epodUidLupd) {
		this.epodUidLupd = epodUidLupd;
	}

}
