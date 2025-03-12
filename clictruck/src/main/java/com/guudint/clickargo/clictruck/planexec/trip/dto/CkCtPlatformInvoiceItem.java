package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoiceItem;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.dto.MstCurrency;

public class CkCtPlatformInvoiceItem extends AbstractDTO<CkCtPlatformInvoiceItem, TCkCtPlatformInvoiceItem> {

	private static final long serialVersionUID = 3047348464061502153L;
	private String itmId;
	private CkCtPlatformInvoice TCkCtPlatformInvoice;
	private Short itmSno;
	private String itmItem;
	private Short itmQty;
	private MstCurrency TMstCurrency;
	private BigDecimal itmUnitPrice;
	private BigDecimal itmAmount;
	private String itmRef;
	private Character itmStatus;
	private Date itmDtCreate;
	private String itmUidCreate;
	private Date itmDtLupd;
	private String itmUidLupd;

	public CkCtPlatformInvoiceItem() {
	}

	public CkCtPlatformInvoiceItem(TCkCtPlatformInvoiceItem entity) {
		super(entity);
	}

	public String getItmId() {
		return itmId;
	}

	public void setItmId(String itmId) {
		this.itmId = itmId;
	}

	public CkCtPlatformInvoice getTCkCtPlatformInvoice() {
		return TCkCtPlatformInvoice;
	}

	public void setTCkCtPlatformInvoice(CkCtPlatformInvoice tCkCtPlatformInvoice) {
		TCkCtPlatformInvoice = tCkCtPlatformInvoice;
	}

	public Short getItmSno() {
		return itmSno;
	}

	public void setItmSno(Short itmSno) {
		this.itmSno = itmSno;
	}

	public String getItmItem() {
		return itmItem;
	}

	public void setItmItem(String itmItem) {
		this.itmItem = itmItem;
	}

	public Short getItmQty() {
		return itmQty;
	}

	public void setItmQty(Short itmQty) {
		this.itmQty = itmQty;
	}

	public MstCurrency getTMstCurrency() {
		return TMstCurrency;
	}

	public void setTMstCurrency(MstCurrency tMstCurrency) {
		TMstCurrency = tMstCurrency;
	}

	public BigDecimal getItmUnitPrice() {
		return itmUnitPrice;
	}

	public void setItmUnitPrice(BigDecimal itmUnitPrice) {
		this.itmUnitPrice = itmUnitPrice;
	}

	public BigDecimal getItmAmount() {
		return itmAmount;
	}

	public void setItmAmount(BigDecimal itmAmount) {
		this.itmAmount = itmAmount;
	}

	public String getItmRef() {
		return itmRef;
	}

	public void setItmRef(String itmRef) {
		this.itmRef = itmRef;
	}

	public Character getItmStatus() {
		return itmStatus;
	}

	public void setItmStatus(Character itmStatus) {
		this.itmStatus = itmStatus;
	}

	public Date getItmDtCreate() {
		return itmDtCreate;
	}

	public void setItmDtCreate(Date itmDtCreate) {
		this.itmDtCreate = itmDtCreate;
	}

	public String getItmUidCreate() {
		return itmUidCreate;
	}

	public void setItmUidCreate(String itmUidCreate) {
		this.itmUidCreate = itmUidCreate;
	}

	public Date getItmDtLupd() {
		return itmDtLupd;
	}

	public void setItmDtLupd(Date itmDtLupd) {
		this.itmDtLupd = itmDtLupd;
	}

	public String getItmUidLupd() {
		return itmUidLupd;
	}

	public void setItmUidLupd(String itmUidLupd) {
		this.itmUidLupd = itmUidLupd;
	}

	@Override
	public int compareTo(CkCtPlatformInvoiceItem o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
