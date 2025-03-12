package com.guudint.clickargo.clictruck.admin.contract.dto;

public class ContractDto {
	String coAccnId;
	String toAccnId;

	public ContractDto() {
	}

	public ContractDto(String coAccnId, String toAccnId) {
		this.coAccnId = coAccnId;
		this.toAccnId = toAccnId;
	}

	public String getCoAccnId() {
		return coAccnId;
	}

	public void setCoAccnId(String coAccnId) {
		this.coAccnId = coAccnId;
	}

	public String getToAccnId() {
		return toAccnId;
	}

	public void setToAccnId(String toAccnId) {
		this.toAccnId = toAccnId;
	}
}
