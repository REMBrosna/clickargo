package com.guudint.clickargo.clictruck.jobupload.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JobRecordTempate implements Serializable {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;

	List<JobRecordTempateItem> itemList = new ArrayList<>();

	public List<JobRecordTempateItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<JobRecordTempateItem> itemList) {
		this.itemList = itemList;
	}

	///////////////////////////////////
	public static class JobRecordTempateItem implements Serializable {

		// Static Attributes
		////////////////////
		private static final long serialVersionUID = 1L;

		String field;
		String label;
		String defaultVal;
		String hidden;
		String type;
		
		@Override
		public String toString() {
			return "JobRecordTempateItem [field=" + field + ", label=" + label + ", defaultVal=" + defaultVal + ", hidden=" + hidden + "]";
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getDefault() {
			return defaultVal;
		}

		public void setDefault(String defaultVal) {
			this.defaultVal = defaultVal;
		}

		public String getHidden() {
			return hidden;
		}

		public void setHidden(String hidden) {
			this.hidden = hidden;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}


	}
}
