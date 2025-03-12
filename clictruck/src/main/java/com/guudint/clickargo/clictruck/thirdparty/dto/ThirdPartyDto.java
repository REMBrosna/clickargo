package com.guudint.clickargo.clictruck.thirdparty.dto;

import java.io.Serializable;
import java.util.Map;

public class ThirdPartyDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	ThirdPartyEnum thirdParty;
	String url;
	Map<String, String> parameters;

	// Constructor
	public ThirdPartyDto() {
		super();
	}
	public ThirdPartyDto(ThirdPartyEnum thirdParty) {
		super();
		this.thirdParty = thirdParty;
	}

	// Properties

	public ThirdPartyEnum getThirdParty() {
		return thirdParty;
	}
	public void setThirdParty(ThirdPartyEnum thirdParty) {
		this.thirdParty = thirdParty;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
}

/*-
[
	{
		thirdParty: "co2",
		parameters: [
			{ name: "email", value: "abc@gmailc.com"},
			{ name: "password", value: "password1234"}
			] 
	},
	{
		thirdParty: "ispot",
		parameters: [
			{ name: "token", value: "AscentTokenString"},
			{ name: "sact", value: "registInAscentSubAccount"}
			] 
	}
]

*/