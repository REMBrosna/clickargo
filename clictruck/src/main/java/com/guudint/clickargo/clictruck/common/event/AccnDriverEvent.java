package com.guudint.clickargo.clictruck.common.event;

import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import org.springframework.context.ApplicationEvent;

public class AccnDriverEvent extends ApplicationEvent  {

	private static final long serialVersionUID = 7788222901510444021L;

	public enum AccnDrvEventAction {
		NEW_DRIVER, RESET_PASSWORD,FORGOT_PASSWORD
	}

	private TCkCtDrv drv;
	private AccnDrvEventAction accnDrvEventAction;

	private String generatePwd;

	public AccnDriverEvent(Object source) {
		super(source);
	}

	public AccnDriverEvent(Object source, TCkCtDrv drv, AccnDrvEventAction accnDrvEventAction, String generatePwd) {
		super(source);
		this.drv = drv;
		this.accnDrvEventAction = accnDrvEventAction;
		this.generatePwd = generatePwd;
	}

	public TCkCtDrv getDrv() {
		return drv;
	}

	public void setDrv(TCkCtDrv drv) {
		this.drv = drv;
	}

	public AccnDrvEventAction getAccnDrvEventAction() {
		return accnDrvEventAction;
	}

	public void setAccnDrvEventAction(AccnDrvEventAction accnDrvEventAction) {
		this.accnDrvEventAction = accnDrvEventAction;
	}

	public String getGeneratePwd() {
		return generatePwd;
	}

	public void setGeneratePwd(String generatePwd) {
		this.generatePwd = generatePwd;
	}
}
