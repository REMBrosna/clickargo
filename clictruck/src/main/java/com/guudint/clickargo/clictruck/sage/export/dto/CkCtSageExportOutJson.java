package com.guudint.clickargo.clictruck.sage.export.dto;

import java.util.List;

public class CkCtSageExportOutJson extends CkCtSageExportJson {

	public long total;
	public Provider provider;

	// Constructor
	public CkCtSageExportOutJson() {
		super();
	}

	public CkCtSageExportOutJson(String service, String type, String reference, String dateTime, long total,
			Provider provider) {
		super(service, type, reference, dateTime);
		this.total = total;
		this.provider = provider;
	}

	public static class Provider {
		public String id;
		public List<Item> items;
		
		public Provider() {
			super();
		}

		public Provider(String id, List<Item> items) {
			super();
			this.id = id;
			this.items = items;
		}
		
	}

}
