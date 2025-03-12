package com.guudint.clickargo.clictruck.common.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.common.model.TCkCtVehMlog;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtVehMlog extends AbstractDTO<CkCtVehMlog, TCkCtVehMlog> {
	
	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -146599239824139721L;
	
	// Attributes
	/////////////
	private String vmlId;
	private CkCtVeh TCkCtVeh;
	private Date vmlDtStart;
	private Date vmlDtEnd;
	private BigDecimal vmlCost;
	private String vmlRemarks;
	private Character vmlStatus;
	private Date vmlDtCreate;
	private String vmlUidCreate;
	private Date vmlDtLupd;
	private String vmlUidLupd;

	// Constructors
	///////////////
	public CkCtVehMlog() {
	}
	
	public CkCtVehMlog(TCkCtVehMlog entity) {
		super(entity);
	}
	
	public CkCtVehMlog(String vmlId, CkCtVeh tCkCtVeh, Date vmlDtStart,
			Date vmlDtEnd, BigDecimal vmlCost, String vmlRemarks, Character vmlStatus, Date vmlDtCreate,
			String vmlUidCreate, Date vmlDtLupd, String vmlUidLupd) {
		super();
		this.vmlId = vmlId;
		TCkCtVeh = tCkCtVeh;
		this.vmlDtStart = vmlDtStart;
		this.vmlDtEnd = vmlDtEnd;
		this.vmlCost = vmlCost;
		this.vmlRemarks = vmlRemarks;
		this.vmlStatus = vmlStatus;
		this.vmlDtCreate = vmlDtCreate;
		this.vmlUidCreate = vmlUidCreate;
		this.vmlDtLupd = vmlDtLupd;
		this.vmlUidLupd = vmlUidLupd;
	}

	public String getVmlId() {
		return vmlId;
	}

	public void setVmlId(String vmlId) {
		this.vmlId = vmlId;
	}

	public CkCtVeh getTCkCtVeh() {
		return TCkCtVeh;
	}

	public void setTCkCtVeh(CkCtVeh tCkCtVeh) {
		TCkCtVeh = tCkCtVeh;
	}

	public Date getVmlDtStart() {
		return vmlDtStart;
	}

	public void setVmlDtStart(Date vmlDtStart) {
		this.vmlDtStart = vmlDtStart;
	}

	public Date getVmlDtEnd() {
		return vmlDtEnd;
	}

	public void setVmlDtEnd(Date vmlDtEnd) {
		this.vmlDtEnd = vmlDtEnd;
	}

	public BigDecimal getVmlCost() {
		return vmlCost;
	}

	public void setVmlCost(BigDecimal vmlCost) {
		this.vmlCost = vmlCost;
	}

	public String getVmlRemarks() {
		return vmlRemarks;
	}

	public void setVmlRemarks(String vmlRemarks) {
		this.vmlRemarks = vmlRemarks;
	}

	public Character getVmlStatus() {
		return vmlStatus;
	}

	public void setVmlStatus(Character vmlStatus) {
		this.vmlStatus = vmlStatus;
	}

	public Date getVmlDtCreate() {
		return vmlDtCreate;
	}

	public void setVmlDtCreate(Date vmlDtCreate) {
		this.vmlDtCreate = vmlDtCreate;
	}

	public String getVmlUidCreate() {
		return vmlUidCreate;
	}

	public void setVmlUidCreate(String vmlUidCreate) {
		this.vmlUidCreate = vmlUidCreate;
	}

	public Date getVmlDtLupd() {
		return vmlDtLupd;
	}

	public void setVmlDtLupd(Date vmlDtLupd) {
		this.vmlDtLupd = vmlDtLupd;
	}

	public String getVmlUidLupd() {
		return vmlUidLupd;
	}

	public void setVmlUidLupd(String vmlUidLupd) {
		this.vmlUidLupd = vmlUidLupd;
	}

	@Override
	public int compareTo(CkCtVehMlog o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
