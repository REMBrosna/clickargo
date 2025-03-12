package com.guudint.clickargo.clictruck.finacing.constant;

public class FinancingConstants {

	public static enum FinancingTypes {
		OC("Cargo Owner OPM"), OT("Trucking Owner OPM"), BC("Balance Sheet Financing"), NF("Non-Financing");

		private String desc;

		private FinancingTypes(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return this.desc;
		}
	}

}
