package com.guudint.clickargo.clictruck.master.dto;
// Generated 14 Mar 2024, 6:38:26 pm by Hibernate Tools 4.3.6.Final

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstOpmRate;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.model.TMstBank;
import com.vcc.camelone.master.model.TMstCurrency;

@Deprecated
public class CkCtMstOpmRate extends AbstractDTO<CkCtMstOpmRate, TCkCtMstOpmRate> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;
	
	// Attributes
	/////////////
	private String opmrId;
	private TMstBank TMstBank;
	private TMstCurrency TMstCurrency;
	private Date opmrDtStart;
	private Date opmrDtEnd;
	private Double opmrPercentBank;
	private Double opmrPercentGli;
	private Integer opmrPaytermCo;
	private Integer opmrPaytermTo;
	private char opmrStatus;
	private Date opmrDtCreate;
	private String opmrUidCreate;
	private Date opmrDtLupd;
	private String opmrUidLupd;

	public CkCtMstOpmRate() {
	}

	public CkCtMstOpmRate(String opmrId, char opmrStatus) {
		this.opmrId = opmrId;
		this.opmrStatus = opmrStatus;
	}

	public CkCtMstOpmRate(String opmrId, TMstBank TMstBank, TMstCurrency TMstCurrency, Date opmrDtStart,
			Date opmrDtEnd, Double opmrPercentBank, Double opmrPercentGli, Integer opmrPaytermCo, Integer opmrPaytermTo,
			char opmrStatus, Date opmrDtCreate, String opmrUidCreate, Date opmrDtLupd, String opmrUidLupd) {
		this.opmrId = opmrId;
		this.TMstBank = TMstBank;
		this.TMstCurrency = TMstCurrency;
		this.opmrDtStart = opmrDtStart;
		this.opmrDtEnd = opmrDtEnd;
		this.opmrPercentBank = opmrPercentBank;
		this.opmrPercentGli = opmrPercentGli;
		this.opmrPaytermCo = opmrPaytermCo;
		this.opmrPaytermTo = opmrPaytermTo;
		this.opmrStatus = opmrStatus;
		this.opmrDtCreate = opmrDtCreate;
		this.opmrUidCreate = opmrUidCreate;
		this.opmrDtLupd = opmrDtLupd;
		this.opmrUidLupd = opmrUidLupd;
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(CkCtMstOpmRate o) {
		return 0;
	}

	@Override
	public void init() {		
	}

	public String getOpmrId() {
		return this.opmrId;
	}

	public void setOpmrId(String opmrId) {
		this.opmrId = opmrId;
	}

	public TMstBank getTMstBank() {
		return this.TMstBank;
	}

	public void setTMstBank(TMstBank TMstBank) {
		this.TMstBank = TMstBank;
	}

	public TMstCurrency getTMstCurrency() {
		return this.TMstCurrency;
	}

	public void setTMstCurrency(TMstCurrency TMstCurrency) {
		this.TMstCurrency = TMstCurrency;
	}

	public Date getOpmrDtStart() {
		return this.opmrDtStart;
	}

	public void setOpmrDtStart(Date opmrDtStart) {
		this.opmrDtStart = opmrDtStart;
	}

	public Date getOpmrDtEnd() {
		return this.opmrDtEnd;
	}

	public void setOpmrDtEnd(Date opmrDtEnd) {
		this.opmrDtEnd = opmrDtEnd;
	}

	public Double getOpmrPercentBank() {
		return this.opmrPercentBank;
	}

	public void setOpmrPercentBank(Double opmrPercentBank) {
		this.opmrPercentBank = opmrPercentBank;
	}

	public Double getOpmrPercentGli() {
		return this.opmrPercentGli;
	}

	public void setOpmrPercentGli(Double opmrPercentGli) {
		this.opmrPercentGli = opmrPercentGli;
	}

	public Integer getOpmrPaytermCo() {
		return this.opmrPaytermCo;
	}

	public void setOpmrPaytermCo(Integer opmrPaytermCo) {
		this.opmrPaytermCo = opmrPaytermCo;
	}

	public Integer getOpmrPaytermTo() {
		return this.opmrPaytermTo;
	}

	public void setOpmrPaytermTo(Integer opmrPaytermTo) {
		this.opmrPaytermTo = opmrPaytermTo;
	}

	public char getOpmrStatus() {
		return this.opmrStatus;
	}

	public void setOpmrStatus(char opmrStatus) {
		this.opmrStatus = opmrStatus;
	}

	public Date getOpmrDtCreate() {
		return this.opmrDtCreate;
	}

	public void setOpmrDtCreate(Date opmrDtCreate) {
		this.opmrDtCreate = opmrDtCreate;
	}

	public String getOpmrUidCreate() {
		return this.opmrUidCreate;
	}

	public void setOpmrUidCreate(String opmrUidCreate) {
		this.opmrUidCreate = opmrUidCreate;
	}

	public Date getOpmrDtLupd() {
		return this.opmrDtLupd;
	}

	public void setOpmrDtLupd(Date opmrDtLupd) {
		this.opmrDtLupd = opmrDtLupd;
	}

	public String getOpmrUidLupd() {
		return this.opmrUidLupd;
	}

	public void setOpmrUidLupd(String opmrUidLupd) {
		this.opmrUidLupd = opmrUidLupd;
	}

}
