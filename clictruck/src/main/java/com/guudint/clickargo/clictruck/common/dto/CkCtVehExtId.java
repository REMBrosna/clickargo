package com.guudint.clickargo.clictruck.common.dto;

import com.guudint.clickargo.clictruck.common.model.TCkCtVehExtId;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtVehExtId extends AbstractDTO<CkCtVehExtId, TCkCtVehExtId> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 7970685936432401269L;
	
	// Attributes
	/////////////
	private String vextId;
	private String vextParam;

	// Constructors
	///////////////
	public CkCtVehExtId() {
	}

	public CkCtVehExtId(String vextId, String vextParam) {
		super();
		this.vextId = vextId;
		this.vextParam = vextParam;
	}

	public CkCtVehExtId(TCkCtVehExtId id) {
		super(id);
	}

	public String getVextId() {
		return vextId;
	}

	public void setVextId(String vextId) {
		this.vextId = vextId;
	}

	public String getVextParam() {
		return vextParam;
	}

	public void setVextParam(String vextParam) {
		this.vextParam = vextParam;
	}

	@Override
	public int compareTo(CkCtVehExtId o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
