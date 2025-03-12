package com.guudint.clickargo.clictruck.common.listener;

import org.springframework.context.ApplicationEvent;

import com.guudint.clickargo.clictruck.common.dto.CkCtAccnInqReq;

public class AccnInquiryEvent extends ApplicationEvent {

	private static final long serialVersionUID = -4620319688917972501L;
	private CkCtAccnInqReq accnInqReq;

	public AccnInquiryEvent(Object source, CkCtAccnInqReq accnInqReq) {
		super(source);
		this.accnInqReq = accnInqReq;
	}

	/**
	 * @return the accnInqReq
	 */
	public CkCtAccnInqReq getAccnInqReq() {
		return accnInqReq;
	}

	/**
	 * @param accnInqReq the accnInqReq to set
	 */
	public void setAccnInqReq(CkCtAccnInqReq accnInqReq) {
		this.accnInqReq = accnInqReq;
	}

}
