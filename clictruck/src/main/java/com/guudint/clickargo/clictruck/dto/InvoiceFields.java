package com.guudint.clickargo.clictruck.dto;

import java.util.ArrayList;
import java.util.List;

public class InvoiceFields {

	List<InvoiceSubLine> invoiceLines;

	public List<InvoiceSubLine> getInvoiceLines() {
		if (invoiceLines == null) {
			invoiceLines = new ArrayList<>();
		}
		return invoiceLines;
	}

	public void setInvoiceLines(List<InvoiceSubLine> invoiceLines) {
		this.invoiceLines = invoiceLines;
	}

}
