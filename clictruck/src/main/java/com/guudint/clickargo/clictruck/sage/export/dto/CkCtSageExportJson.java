package com.guudint.clickargo.clictruck.sage.export.dto;

public class CkCtSageExportJson {

	public String service;
	public String type;
	public String reference;
	public String dateTime;


	// Constructor
	public CkCtSageExportJson() {
		super();
	}

	public CkCtSageExportJson(String service, String type, String reference, String dateTime) {
		super();

		this.service = service;
		this.type = type;
		this.reference = reference;
		this.dateTime = dateTime;
	}

	// static inner nested class

	public static class ConsumerProviderBooking {

		public String id;
		public Invoice invoice;
		public DebitNote debitNote;

		public ConsumerProviderBooking() {
			super();
		}

		public ConsumerProviderBooking(String id, Invoice invoice, DebitNote debitNote) {
			super();
			this.id = id;
			this.invoice = invoice;
			this.debitNote = debitNote;
		}
	}

	public static class DebitNote {
		public String no;
		public String issuedDate;
		public String ccy;
		public long amount;
		public long duty;
		public String terms;
		public long total;

		public DebitNote() {
			super();
		}

		public DebitNote(String no, String issuedDate, String ccy, long amount, long duty, String terms, long total) {
			super();
			this.no = no;
			this.issuedDate = issuedDate;
			this.ccy = ccy;
			this.amount = amount;
			this.duty = duty;
			this.terms = terms;
			this.total = total;
		}

	}

	public static class Invoice {
		public String no;
		public String issuedDate;
		public String ccy;
		public long amount;
		public long vat;
		public long duty;
		public String terms;
		public String taxNo;
		public long total;

		public Invoice() {
			super();
		}

		public Invoice(String no, String issuedDate, String ccy, long amount, long vat, long duty, String terms,
				String taxNo, long total) {
			super();
			this.no = no;
			this.issuedDate = issuedDate;
			this.ccy = ccy;
			this.amount = amount;
			this.vat = vat;
			this.duty = duty;
			this.terms = terms;
			this.taxNo = taxNo;
			this.total = total;
		}

	}

	public static class Item{
	    
		public DebitNote debitNote;
	    public Invoice invoice;
	    
		public Item() {
			super();
		}
		
		public Item(DebitNote debitNote, Invoice invoice) {
			super();
			this.debitNote = debitNote;
			this.invoice = invoice;
		}
	    
	}
	
	@Override
	public String toString() {
		return "CkCtSageExportBookingJson [service=" + service + ", type=" + type + ", reference=" + reference
				+ ", dateTime=" + dateTime + "]";
	}

}
