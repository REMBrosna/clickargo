package com.guudint.clickargo.clictruck.dto;

import java.util.ArrayList;
import java.util.List;

public class DsvFieldSea {
	
	List<DsvSublineSea> dsvSeaLines;

	public List<DsvSublineSea> getDsvSeaLines() {
		if (dsvSeaLines == null) {
			dsvSeaLines = new ArrayList<>();
		}
		return dsvSeaLines;
	}

	public void setDsvLines(List<DsvSublineSea> dsvSeaLines) {
		this.dsvSeaLines = dsvSeaLines;
	}

}
