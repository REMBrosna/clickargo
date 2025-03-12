package com.guudint.clickargo.clictruck.sage.export.dto;

import java.util.ArrayList;

public class CkCtSageExportInJson extends CkCtSageExportJson {

	public String total;
	public Consumer consumer;

	// Constructor
	public CkCtSageExportInJson() {
		super();
	}

	public static class Consumer {
		public String id;
		public ArrayList<Item> items;
	}

}
