package com.guudint.clickargo.clictruck.opm;

import org.apache.commons.lang3.StringUtils;

import com.guudint.clickargo.clictruck.opm.OpmConstants.Opm_Validation;
import com.vcc.camelone.common.exception.ValidationException;

public class OpmException extends ValidationException {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 2903305130525224929L;

	private String rowKey = null; // jobId or taxNO.

	// Constructor
	//////////////
	public OpmException() {

	}

	@Override
	public String toString() {
		String errMsg = "errorCode=" + getCode() + " " + ",jobId(taxNo)=" + getRowKey() + " msg: " + this.getMessage();
		return (errMsg.length() > 200) ? errMsg.substring(0, 200) : errMsg;
	}

	public OpmException(Opm_Validation opmValidation) {
		super(opmValidation.code, opmValidation.msg);
	}

	public OpmException(Opm_Validation opmValidation, String errorFieldName) {
		super(opmValidation.code, opmValidation.msg.replaceAll("\\{0\\}", errorFieldName));
	}

	public OpmException(String msg) {
		super(msg);
	}

	public OpmException(String code, String msg) {
		super(code, msg);
	}

	public OpmException(Exception ex) {
		super(Opm_Validation.UNKNOW.code, ex.getMessage());
	}

	public OpmException(Exception ex, String id) {

		super(Opm_Validation.UNKNOW.code, StringUtils.isNoneBlank(ex.getMessage()) ? (ex.getMessage() + ":" + id) : id);

	}

	public String getRowKey() {
		return rowKey;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

}
