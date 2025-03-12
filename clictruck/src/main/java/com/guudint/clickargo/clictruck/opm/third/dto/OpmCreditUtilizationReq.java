package com.guudint.clickargo.clictruck.opm.third.dto;

import java.util.Date;
import java.util.List;

public class OpmCreditUtilizationReq extends OpmCreditReq {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;

	// Attributes
	/////////////
    private long total;
    private long tenor;
    private long platform_fee;
    private String cargo_owner;
    private String remark;
    private String action;
    private OpmDebitNote debitNote;
    private OpmInvoice invoice;
    private OpmPod pod;
    private List<OpmGp> gps;

	// Constructors
	///////////////

	// Properties
	/////////////

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getTenor() {
		return tenor;
	}

	public void setTenor(long tenor) {
		this.tenor = tenor;
	}

	public long getPlatform_fee() {
		return platform_fee;
	}

	public void setPlatform_fee(int platform_fee) {
		this.platform_fee = platform_fee;
	}

	public String getCargo_owner() {
		return cargo_owner;
	}

	public void setCargo_owner(String cargo_owner) {
		this.cargo_owner = cargo_owner;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public OpmDebitNote getDebitNote() {
		return debitNote;
	}

	public void setDebitNote(OpmDebitNote debitNote) {
		this.debitNote = debitNote;
	}

	public OpmInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(OpmInvoice invoice) {
		this.invoice = invoice;
	}

	public OpmPod getPod() {
		return pod;
	}

	public void setPod(OpmPod pod) {
		this.pod = pod;
	}

	public List<OpmGp> getGps() {
		return gps;
	}

	public void setGps(List<OpmGp> gps) {
		this.gps = gps;
	}

	public class OpmDebitNote{
    	private String no;
    	private Date issue_date;
    	private int amt;
    	private String document;
		public String getNo() {
			return no;
		}
		public void setNo(String no) {
			this.no = no;
		}
		public Date getIssue_date() {
			return issue_date;
		}
		public void setIssue_date(Date issue_date) {
			this.issue_date = issue_date;
		}
		public int getAmt() {
			return amt;
		}
		public void setAmt(int amt) {
			this.amt = amt;
		}
		public String getDocument() {
			return document;
		}
		public void setDocument(String document) {
			this.document = document;
		}
    	
    	
    }

    public class OpmGp{
    	
    	private String resolution;
        private String coordinates;
        private String timestamp;
        
		public String getResolution() {
			return resolution;
		}
		public void setResolution(String resolution) {
			this.resolution = resolution;
		}
		public String getCoordinates() {
			return coordinates;
		}
		public void setCoordinates(String coordinates) {
			this.coordinates = coordinates;
		}
		public String getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}
        
    }

    public class OpmInvoice{
    	private String no;
    	private Date issue_date;
    	private int amt;
    	private String document;
    	
		public String getNo() {
			return no;
		}
		public void setNo(String no) {
			this.no = no;
		}
		public Date getIssue_date() {
			return issue_date;
		}
		public void setIssue_date(Date issue_date) {
			this.issue_date = issue_date;
		}
		public int getAmt() {
			return amt;
		}
		public void setAmt(int amt) {
			this.amt = amt;
		}
		public String getDocument() {
			return document;
		}
		public void setDocument(String document) {
			this.document = document;
		}

    }

    public class OpmPod{
    	
    	private String no;
    	private String document;
    	
		public String getNo() {
			return no;
		}
		public void setNo(String no) {
			this.no = no;
		}
		public String getDocument() {
			return document;
		}
		public void setDocument(String document) {
			this.document = document;
		}

    	
    }
    
}
