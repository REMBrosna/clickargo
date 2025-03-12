package com.guudint.clickargo.clictruck.admin.contract.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ContractReqStateEnum {

	DELETED("DELETED", "DELETED", "DEL"), 
	NEW_REQ("NEW_REQ", "NEW REQUEST DRAFT", "NEW"),
	NEW_SUBMITTED("NEW_SUBMITTED", "NEW REQUEST SUBMITTED", "SUB"),
	NEW_APPROVED("NEW_APPROVED", "NEW REQUEST APPROVED", "APP"),
	NEW_REJECTED("NEW_REJECTED", "NEW REQUEST REJECTED", "REJ"),
	NEW_UPDATE("NEW_UPDATE", "UPDATE REQUEST DRAFT", "NEW"),
	UPDATE_SUBMITTED("UPDATE_SUBMITTED", "UPDATE REQUEST SUBMITTED", "SUB"),
	UPDATE_APPROVED("UPDATE_APPROVED", "UPDATE REQUEST APPROVED", "APP"),
	UPDATE_REJECTED("UPDATE_REJECTED", "UPDATE REQUEST REJECTED", "REJ"), 
	RENEWAL_REQ("RENEWAL_REQ", "NEW RENEWAL REQUEST", "NEW"),
	RENEWAL_SUBMITTED("RENEWAL_SUBMITTED","RENEWAL REQUEST SUBMITTED", "SUB"),
	RENEWAL_APPROVED("RENEWAL_APPROVED", "RENEWAL REQUEST APPROVED", "APP"),
	RENEWAL_REJECTED("RENEWAL_REJECTED", "RENEWAL REQUEST REJECTED", "REJ"),
	EXPIRED("EXPIRED", "REQUEST EXPIRED", "EXP"),
	EXPORTED("EXPORTED", "REQUEST EXPORTED TO CONTRACT", "EX");

	String code;
	String desc;
	String altCode;

	ContractReqStateEnum(String code, String desc, String altCode) {
		this.code = code;
		this.desc = desc;
		this.altCode = altCode;
	}

	public String getCode() {
		return this.code;
	}

	public String getDesc() {
		return this.desc;
	}

	public String getAltCode() {
		return this.altCode;
	}

	public static String getAltCodeByState(String name) {
		for (ContractReqStateEnum e : ContractReqStateEnum.values()) {
			if (e.name().equalsIgnoreCase(name))
				return e.getAltCode();
		}

		return null;
	}

	public static String getStateByAltCode(boolean isNewReq, String altCode) {
		List<ContractReqStateEnum> listNewReq = Arrays.asList(NEW_SUBMITTED, NEW_APPROVED, NEW_REJECTED,
						NEW_REQ, DELETED);
		List<ContractReqStateEnum> listUpdateReq = Arrays.asList(NEW_UPDATE, UPDATE_SUBMITTED,
				UPDATE_APPROVED, UPDATE_REJECTED, DELETED);
		
		if(isNewReq) {
			Optional<ContractReqStateEnum> opNewReqEnum = listNewReq.stream().filter(e-> e.getAltCode().equalsIgnoreCase(altCode)).findAny();
			if(opNewReqEnum.isPresent()) {
				return opNewReqEnum.get().name();
			}
		} else {
			Optional<ContractReqStateEnum> opUpdateReqEnum = listUpdateReq.stream().filter(e-> e.getAltCode().equalsIgnoreCase(altCode)).findAny();
			if(opUpdateReqEnum.isPresent()) {
				return opUpdateReqEnum.get().name();
			}
			
		}
				
		

		return null;
	}
}
