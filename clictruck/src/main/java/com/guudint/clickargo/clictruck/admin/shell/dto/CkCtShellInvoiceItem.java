package com.guudint.clickargo.clictruck.admin.shell.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellInvoiceItem;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtShellInvoiceItem extends AbstractDTO<CkCtShellInvoiceItem, TCkCtShellInvoiceItem> {

	private static final long serialVersionUID = -2384496308836380237L;
	private String itmId;
	private CkCtShellCard TCkCtShellCard;
	private CkCtShellInvoice TCkCtShellInvoice;
	private TCkCtVeh TCkCtVeh;
	private String itmDesc;
	private Date itmDtTxn;
	private BigDecimal itmCost;
	private BigDecimal itmDiscount;
	private BigDecimal itmTax;
	private BigDecimal itmCo2;
	private BigDecimal itmTotal;
	private Character itmStatus;
	private Date itmDtCreate;
	private String itmUidCreate;
	private Date itmDtLupd;
	private String itmUidLupd;

	public CkCtShellInvoiceItem() {
	}

	public CkCtShellInvoiceItem(TCkCtShellInvoiceItem entity) {
		super(entity);
	}

	public CkCtShellInvoiceItem(String itmId, CkCtShellCard TCkCtShellCard, CkCtShellInvoice TCkCtShellInvoice,
			TCkCtVeh TCkCtVeh, String itmDesc, Date itmDtTxn, BigDecimal itmCost, BigDecimal itmDiscount,
			BigDecimal itmTax, BigDecimal itmCo2, BigDecimal itmTotal, Character itmStatus, Date itmDtCreate,
			String itmUidCreate, Date itmDtLupd, String itmUidLupd) {
		this.itmId = itmId;
		this.TCkCtShellCard = TCkCtShellCard;
		this.TCkCtShellInvoice = TCkCtShellInvoice;
		this.TCkCtVeh = TCkCtVeh;
		this.itmDesc = itmDesc;
		this.itmDtTxn = itmDtTxn;
		this.itmCost = itmCost;
		this.itmDiscount = itmDiscount;
		this.itmTax = itmTax;
		this.itmCo2 = itmCo2;
		this.itmTotal = itmTotal;
		this.itmStatus = itmStatus;
		this.itmDtCreate = itmDtCreate;
		this.itmUidCreate = itmUidCreate;
		this.itmDtLupd = itmDtLupd;
		this.itmUidLupd = itmUidLupd;
	}

	/**
	 * @return the itmId
	 */
	public String getItmId() {
		return itmId;
	}

	/**
	 * @param itmId the itmId to set
	 */
	public void setItmId(String itmId) {
		this.itmId = itmId;
	}

	/**
	 * @return the tCkCtShellCard
	 */
	public CkCtShellCard getTCkCtShellCard() {
		return TCkCtShellCard;
	}

	/**
	 * @param tCkCtShellCard the tCkCtShellCard to set
	 */
	public void setTCkCtShellCard(CkCtShellCard tCkCtShellCard) {
		TCkCtShellCard = tCkCtShellCard;
	}

	/**
	 * @return the tCkCtShellInvoice
	 */
	public CkCtShellInvoice getTCkCtShellInvoice() {
		return TCkCtShellInvoice;
	}

	/**
	 * @param tCkCtShellInvoice the tCkCtShellInvoice to set
	 */
	public void setTCkCtShellInvoice(CkCtShellInvoice tCkCtShellInvoice) {
		TCkCtShellInvoice = tCkCtShellInvoice;
	}

	/**
	 * @return the tCkCtVeh
	 */
	public TCkCtVeh getTCkCtVeh() {
		return TCkCtVeh;
	}

	/**
	 * @param tCkCtVeh the tCkCtVeh to set
	 */
	public void setTCkCtVeh(TCkCtVeh tCkCtVeh) {
		TCkCtVeh = tCkCtVeh;
	}

	/**
	 * @return the itmDesc
	 */
	public String getItmDesc() {
		return itmDesc;
	}

	/**
	 * @param itmDesc the itmDesc to set
	 */
	public void setItmDesc(String itmDesc) {
		this.itmDesc = itmDesc;
	}

	/**
	 * @return the itmDtTxn
	 */
	public Date getItmDtTxn() {
		return itmDtTxn;
	}

	/**
	 * @param itmDtTxn the itmDtTxn to set
	 */
	public void setItmDtTxn(Date itmDtTxn) {
		this.itmDtTxn = itmDtTxn;
	}

	/**
	 * @return the itmCost
	 */
	public BigDecimal getItmCost() {
		return itmCost;
	}

	/**
	 * @param itmCost the itmCost to set
	 */
	public void setItmCost(BigDecimal itmCost) {
		this.itmCost = itmCost;
	}

	/**
	 * @return the itmDiscount
	 */
	public BigDecimal getItmDiscount() {
		return itmDiscount;
	}

	/**
	 * @param itmDiscount the itmDiscount to set
	 */
	public void setItmDiscount(BigDecimal itmDiscount) {
		this.itmDiscount = itmDiscount;
	}

	/**
	 * @return the itmTax
	 */
	public BigDecimal getItmTax() {
		return itmTax;
	}

	/**
	 * @param itmTax the itmTax to set
	 */
	public void setItmTax(BigDecimal itmTax) {
		this.itmTax = itmTax;
	}

	/**
	 * @return the itmCo2
	 */
	public BigDecimal getItmCo2() {
		return itmCo2;
	}

	/**
	 * @param itmCo2 the itmCo2 to set
	 */
	public void setItmCo2(BigDecimal itmCo2) {
		this.itmCo2 = itmCo2;
	}

	/**
	 * @return the itmTotal
	 */
	public BigDecimal getItmTotal() {
		return itmTotal;
	}

	/**
	 * @param itmTotal the itmTotal to set
	 */
	public void setItmTotal(BigDecimal itmTotal) {
		this.itmTotal = itmTotal;
	}

	/**
	 * @return the itmStatus
	 */
	public Character getItmStatus() {
		return itmStatus;
	}

	/**
	 * @param itmStatus the itmStatus to set
	 */
	public void setItmStatus(Character itmStatus) {
		this.itmStatus = itmStatus;
	}

	/**
	 * @return the itmDtCreate
	 */
	public Date getItmDtCreate() {
		return itmDtCreate;
	}

	/**
	 * @param itmDtCreate the itmDtCreate to set
	 */
	public void setItmDtCreate(Date itmDtCreate) {
		this.itmDtCreate = itmDtCreate;
	}

	/**
	 * @return the itmUidCreate
	 */
	public String getItmUidCreate() {
		return itmUidCreate;
	}

	/**
	 * @param itmUidCreate the itmUidCreate to set
	 */
	public void setItmUidCreate(String itmUidCreate) {
		this.itmUidCreate = itmUidCreate;
	}

	/**
	 * @return the itmDtLupd
	 */
	public Date getItmDtLupd() {
		return itmDtLupd;
	}

	/**
	 * @param itmDtLupd the itmDtLupd to set
	 */
	public void setItmDtLupd(Date itmDtLupd) {
		this.itmDtLupd = itmDtLupd;
	}

	/**
	 * @return the itmUidLupd
	 */
	public String getItmUidLupd() {
		return itmUidLupd;
	}

	/**
	 * @param itmUidLupd the itmUidLupd to set
	 */
	public void setItmUidLupd(String itmUidLupd) {
		this.itmUidLupd = itmUidLupd;
	}

	@Override
	public int compareTo(CkCtShellInvoiceItem o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
