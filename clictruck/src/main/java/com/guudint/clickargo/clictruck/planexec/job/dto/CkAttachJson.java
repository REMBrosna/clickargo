package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vcc.camelone.common.COAbstractEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CkAttachJson extends COAbstractEntity<CkAttachJson> {

	private static final long serialVersionUID = 3711404481340259122L;

	private AttachJson row;
	private List<AttachJson> subRow;

	/**
	 * @return the row
	 */
	public AttachJson getRow() {
		return row;
	}

	/**
	 * @param row the row to set
	 */
	public void setRow(AttachJson row) {
		this.row = row;
	}

	/**
	 * @return the subRow
	 */
	public List<AttachJson> getSubRow() {
		return subRow;
	}

	/**
	 * @param subRow the subRow to set
	 */
	public void setSubRow(List<AttachJson> subRow) {
		this.subRow = subRow;
	}

	@Override
	public int compareTo(CkAttachJson o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
