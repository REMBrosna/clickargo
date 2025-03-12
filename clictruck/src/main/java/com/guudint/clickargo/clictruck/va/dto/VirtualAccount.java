package com.guudint.clickargo.clictruck.va.dto;

import com.vcc.camelone.ccm.dto.CoreAccnConfig;
import com.vcc.camelone.ccm.model.TCoreAccnConfig;

public class VirtualAccount extends CoreAccnConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1574636586863834449L;
	
	private String history;

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public VirtualAccount() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VirtualAccount(TCoreAccnConfig entity) {
		super(entity);
		// TODO Auto-generated constructor stub
	}

}
