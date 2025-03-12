package com.guudint.clickargo.clictruck.admin.contract.event;

import org.springframework.context.ApplicationEvent;

import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;

public class NotifyExpireEvent extends ApplicationEvent {

	// Static attributes
	private static final long serialVersionUID = -8893724944708281172L;
	
    // Attributes
	private CkCtContract contract;
    private String expiredDate;
    
    // Constructor
	public NotifyExpireEvent(Object source, CkCtContract contract, String expiredDate) {
		super(source);
		this.contract = contract;
		this.expiredDate = expiredDate;
	}

	// Properties
	/////////////
	public CkCtContract getContract() {
		return contract;
	}

	public void setContract(CkCtContract contract) {
		this.contract = contract;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}

}
