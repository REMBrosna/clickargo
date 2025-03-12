package com.guudint.clickargo.clictruck.dto;

import java.util.ArrayList;
import java.util.List;

public class DsvFields {

	List<DsvSubLine> dsvLines;

	public List<DsvSubLine> getDsvLines() {
		if (dsvLines == null) {
			dsvLines = new ArrayList<>();
		}
		return dsvLines;
	}

	public void setDsvLines(List<DsvSubLine> dsvLines) {
		this.dsvLines = dsvLines;
	}
	
	
}
