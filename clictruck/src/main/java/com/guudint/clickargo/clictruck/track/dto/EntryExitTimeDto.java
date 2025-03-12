package com.guudint.clickargo.clictruck.track.dto;

import java.io.Serializable;

public class EntryExitTimeDto implements Serializable{

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1L;

	// Attributes
	/////////////
	String enterFmt;
	long enter;
	String exitFmt;
	long exit;

	// Constructor
	public EntryExitTimeDto() {
		super();
	}

	@Override
	public String toString() {
		return "EntryExitTimeDto [enterFmt=" + enterFmt + ", enter=" + enter + ", exitFmt=" + exitFmt + ", exit=" + exit
				+ "]";
	}

	// Properties
	/////////////
	public String getEnterFmt() {
		return enterFmt;
	}

	public void setEnterFmt(String enterFmt) {
		this.enterFmt = enterFmt;
	}

	public long getEnter() {
		return enter;
	}

	public void setEnter(long enter) {
		this.enter = enter;
	}

	public String getExitFmt() {
		return exitFmt;
	}

	public void setExitFmt(String exitFmt) {
		this.exitFmt = exitFmt;
	}

	public long getExit() {
		return exit;
	}

	public void setExit(long exit) {
		this.exit = exit;
	}
	
	
	
}
