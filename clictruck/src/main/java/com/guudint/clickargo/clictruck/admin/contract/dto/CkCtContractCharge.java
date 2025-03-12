package com.guudint.clickargo.clictruck.admin.contract.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractCharge;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtContractCharge extends AbstractDTO<CkCtContractCharge, TCkCtContractCharge> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 5420006493106674466L;

	public static enum ContractChargeTypes {
		FIXED('F'), PERCENTAGE('P');

		private char code;

		ContractChargeTypes(char code) {
			this.code = code;
		}

		public char getCode() {
			return code;
		}
	}

	// Attributes
	/////////////
	private String concId;
	private BigDecimal concPltfeeAmt;
	private Character concPltfeeType;
	private BigDecimal concAddtaxAmt;
	private Character concAddtaxType;
	private BigDecimal concWhtaxAmt;
	private Character concWhtaxType;
	private Character concStatus;
	private Date concDtCreate;
	private String concUidCreate;
	private Date concDtLupd;
	private String concUidLupd;

	// Constructors
	///////////////
	public CkCtContractCharge() {
	}

	/**
	 * @param entity
	 */
	public CkCtContractCharge(TCkCtContractCharge entity) {
		super(entity);
	}

	/**
	 * @param concId
	 * @param concPltfeeAmt
	 * @param concPltfeeType
	 * @param concAddtaxAmt
	 * @param concAddtaxType
	 * @param concWhtaxAmt
	 * @param concWhtaxType
	 * @param concStatus
	 * @param concDtCreate
	 * @param concUidCreate
	 * @param concDtLupd
	 * @param concUidLupd
	 * @param tCkCtContractsForConChargeTo
	 * @param tCkCtContractsForConChargeCoFf
	 */
	public CkCtContractCharge(String concId, BigDecimal concPltfeeAmt, Character concPltfeeType,
			BigDecimal concAddtaxAmt, Character concAddtaxType, BigDecimal concWhtaxAmt, Character concWhtaxType,
			Character concStatus, Date concDtCreate, String concUidCreate, Date concDtLupd, String concUidLupd) {
		super();
		this.concId = concId;
		this.concPltfeeAmt = concPltfeeAmt;
		this.concPltfeeType = concPltfeeType;
		this.concAddtaxAmt = concAddtaxAmt;
		this.concAddtaxType = concAddtaxType;
		this.concWhtaxAmt = concWhtaxAmt;
		this.concWhtaxType = concWhtaxType;
		this.concStatus = concStatus;
		this.concDtCreate = concDtCreate;
		this.concUidCreate = concUidCreate;
		this.concDtLupd = concDtLupd;
		this.concUidLupd = concUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the concId
	 */
	public String getConcId() {
		return concId;
	}

	/**
	 * @param concId the concId to set
	 */
	public void setConcId(String concId) {
		this.concId = concId;
	}

	/**
	 * @return the concPltfeeAmt
	 */
	public BigDecimal getConcPltfeeAmt() {
		return concPltfeeAmt;
	}

	/**
	 * @param concPltfeeAmt the concPltfeeAmt to set
	 */
	public void setConcPltfeeAmt(BigDecimal concPltfeeAmt) {
		this.concPltfeeAmt = concPltfeeAmt;
	}

	/**
	 * @return the concPltfeeType
	 */
	public Character getConcPltfeeType() {
		return concPltfeeType;
	}

	/**
	 * @param concPltfeeType the concPltfeeType to set
	 */
	public void setConcPltfeeType(Character concPltfeeType) {
		this.concPltfeeType = concPltfeeType;
	}

	/**
	 * @return the concAddtaxAmt
	 */
	public BigDecimal getConcAddtaxAmt() {
		return concAddtaxAmt;
	}

	/**
	 * @param concAddtaxAmt the concAddtaxAmt to set
	 */
	public void setConcAddtaxAmt(BigDecimal concAddtaxAmt) {
		this.concAddtaxAmt = concAddtaxAmt;
	}

	/**
	 * @return the concAddtaxType
	 */
	public Character getConcAddtaxType() {
		return concAddtaxType;
	}

	/**
	 * @param concAddtaxType the concAddtaxType to set
	 */
	public void setConcAddtaxType(Character concAddtaxType) {
		this.concAddtaxType = concAddtaxType;
	}

	/**
	 * @return the concWhtaxAmt
	 */
	public BigDecimal getConcWhtaxAmt() {
		return concWhtaxAmt;
	}

	/**
	 * @param concWhtaxAmt the concWhtaxAmt to set
	 */
	public void setConcWhtaxAmt(BigDecimal concWhtaxAmt) {
		this.concWhtaxAmt = concWhtaxAmt;
	}

	/**
	 * @return the concWhtaxType
	 */
	public Character getConcWhtaxType() {
		return concWhtaxType;
	}

	/**
	 * @param concWhtaxType the concWhtaxType to set
	 */
	public void setConcWhtaxType(Character concWhtaxType) {
		this.concWhtaxType = concWhtaxType;
	}

	/**
	 * @return the concStatus
	 */
	public Character getConcStatus() {
		return concStatus;
	}

	/**
	 * @param concStatus the concStatus to set
	 */
	public void setConcStatus(Character concStatus) {
		this.concStatus = concStatus;
	}

	/**
	 * @return the concDtCreate
	 */
	public Date getConcDtCreate() {
		return concDtCreate;
	}

	/**
	 * @param concDtCreate the concDtCreate to set
	 */
	public void setConcDtCreate(Date concDtCreate) {
		this.concDtCreate = concDtCreate;
	}

	/**
	 * @return the concUidCreate
	 */
	public String getConcUidCreate() {
		return concUidCreate;
	}

	/**
	 * @param concUidCreate the concUidCreate to set
	 */
	public void setConcUidCreate(String concUidCreate) {
		this.concUidCreate = concUidCreate;
	}

	/**
	 * @return the concDtLupd
	 */
	public Date getConcDtLupd() {
		return concDtLupd;
	}

	/**
	 * @param concDtLupd the concDtLupd to set
	 */
	public void setConcDtLupd(Date concDtLupd) {
		this.concDtLupd = concDtLupd;
	}

	/**
	 * @return the concUidLupd
	 */
	public String getConcUidLupd() {
		return concUidLupd;
	}

	/**
	 * @param concUidLupd the concUidLupd to set
	 */
	public void setConcUidLupd(String concUidLupd) {
		this.concUidLupd = concUidLupd;
	}

	// Override Methods
	///////////////////
	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *
	 */
	@Override
	public int compareTo(CkCtContractCharge o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.COAbstractEntity#init()
	 *
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
