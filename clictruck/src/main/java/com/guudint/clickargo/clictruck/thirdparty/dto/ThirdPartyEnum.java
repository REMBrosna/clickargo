package com.guudint.clickargo.clictruck.thirdparty.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ThirdPartyEnum {
	
	CO2X("co2x","CO2X Monitoring","POST", "CO2X Monitoring is an advanced environmental management platform designed to track and analyze carbon dioxide (CO2) emissions in real-time. Utilizing cutting-edge sensor technology and data analytics, CO2X Monitoring provides businesses, industries, and environmental agencies with precise and actionable insights into their carbon footprint.\n"
			+ "\n"
			+ "The platform offers customizable dashboards, alert systems, and detailed reporting features, enabling users to monitor CO2 levels continuously and make informed decisions to reduce emissions. CO2X Monitoring is essential for organizations committed to sustainability, compliance with environmental regulations, and achieving carbon neutrality goals."),

	ISPOT("ispot","ISPOT","GET", "ISPOT is a sophisticated tracking and analytics tool designed to enhance the performance and visibility of internet service providers (ISPs). By providing real-time data on network usage, latency, bandwidth, and customer behavior, Ascent ISPOT empowers ISPs to optimize their services and ensure a seamless user experience.\n"
			+ "\n"
			+ "The platform offers detailed performance metrics, customizable alerts, and advanced reporting capabilities, enabling ISPs to proactively address network issues, improve service quality, and make data-driven decisions. Ascent ISPOT is an essential solution for ISPs seeking to maintain competitive edge, enhance customer satisfaction, and maximize operational efficiency.");
	
	private String name;
	private String label;
	private String method;
	private String desc;
	
	// Construct
	private ThirdPartyEnum() {
	}

	private ThirdPartyEnum(String name, String label, String method, String desc) {
		this.name = name;
		this.label = label;
		this.method = method;
		this.desc = desc;
	}

	// Properties
	public String getName() {
		return name;
	}
	public String getLabel() {
		return label;
	}
	public String getDesc() {
		return desc;
	}
	public String getMethod() {
		return method;
	}	
}
