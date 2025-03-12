package com.guudint.clickargo.clictruck.admin.ratetable.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.guudint.clickargo.clictruck.admin.ratetable.constant.CkCtRateConstant.TripType;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtTripRate;
import com.guudint.clickargo.clictruck.common.dto.CkCtLocation;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.common.enums.JobActions;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtTripRate extends AbstractDTO<CkCtTripRate, TCkCtTripRate> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -5013677351472946164L;

	// Attributes
	/////////////
	private String trId;
	private CkCtLocation TCkCtLocationByTrLocTo;
	private CkCtLocation TCkCtLocationByTrLocFrom;
	private CkCtRateTable TCkCtRateTable;
	private CkCtMstVehType TCkCtMstVehType;
	private BigDecimal trCharge;
	private Character trStatus;
	private Date trDtCreate;
	private String trUidCreate;
	private Date trDtLupd;
	private String trUidLupd;

	private CkCtTripRate TCkCtTripRate;
	private String trType;
	private Integer trSeq;
	private List<CkCtTripRate> TCkCtTripRates = new ArrayList<CkCtTripRate>(0);

	private String trTypeFilter;
	private JobActions action;
	private String history;

	// Constructors
	///////////////
	public CkCtTripRate() {
	}

	/**
	 * @param entity
	 */
	public CkCtTripRate(TCkCtTripRate entity) {
		super(entity);
	}

	/**
	 * @param trId
	 * @param tCkCtLocationByTrLocTo
	 * @param tCkCtLocationByTrLocFrom
	 * @param tCkCtRateTable
	 * @param trCharge
	 * @param trStatus
	 * @param trDtCreate
	 * @param trUidCreate
	 * @param trDtLupd
	 * @param trUidLupd
	 */
	public CkCtTripRate(String trId, CkCtLocation tCkCtLocationByTrLocTo, CkCtLocation tCkCtLocationByTrLocFrom,
			CkCtRateTable tCkCtRateTable, CkCtMstVehType tCkCtMstVehType, BigDecimal trCharge, Character trStatus,
			Date trDtCreate, String trUidCreate, Date trDtLupd, String trUidLupd) {
		super();
		this.trId = trId;
		TCkCtLocationByTrLocTo = tCkCtLocationByTrLocTo;
		TCkCtLocationByTrLocFrom = tCkCtLocationByTrLocFrom;
		TCkCtRateTable = tCkCtRateTable;
		TCkCtMstVehType = tCkCtMstVehType;
		this.trCharge = trCharge;
		this.trStatus = trStatus;
		this.trDtCreate = trDtCreate;
		this.trUidCreate = trUidCreate;
		this.trDtLupd = trDtLupd;
		this.trUidLupd = trUidLupd;
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
	public int compareTo(CkCtTripRate o) {
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
	}
	////////////////

	public String getTrTypeDesc() {
		try {
			return TripType.valueOf(trType).getDesc();
		} catch (Exception e) {

		}
		return null;
	}

	public boolean equalsLocAndSeq(TCkCtTripRate entity) {

		if (entity == null || entity.getTCkCtLocationByTrLocFrom() == null
				|| entity.getTCkCtLocationByTrLocTo() == null)
			return false;

		return Objects.equals(TCkCtLocationByTrLocFrom.getLocId(), entity.getTCkCtLocationByTrLocFrom().getLocId())
				&& Objects.equals(TCkCtLocationByTrLocTo.getLocId(), entity.getTCkCtLocationByTrLocTo().getLocId())
				&& Objects.equals(trSeq, entity.getTrSeq());
	}

	// Properties
	/////////////
	/**
	 * @return the trId
	 */
	public String getTrId() {
		return trId;
	}

	/**
	 * @param trId the trId to set
	 */
	public void setTrId(String trId) {
		this.trId = trId;
	}

	/**
	 * @return the tCkCtLocationByTrLocTo
	 */
	public CkCtLocation getTCkCtLocationByTrLocTo() {
		return TCkCtLocationByTrLocTo;
	}

	/**
	 * @param tCkCtLocationByTrLocTo the tCkCtLocationByTrLocTo to set
	 */
	public void setTCkCtLocationByTrLocTo(CkCtLocation tCkCtLocationByTrLocTo) {
		TCkCtLocationByTrLocTo = tCkCtLocationByTrLocTo;
	}

	/**
	 * @return the tCkCtLocationByTrLocFrom
	 */
	public CkCtLocation getTCkCtLocationByTrLocFrom() {
		return TCkCtLocationByTrLocFrom;
	}

	/**
	 * @param tCkCtLocationByTrLocFrom the tCkCtLocationByTrLocFrom to set
	 */
	public void setTCkCtLocationByTrLocFrom(CkCtLocation tCkCtLocationByTrLocFrom) {
		TCkCtLocationByTrLocFrom = tCkCtLocationByTrLocFrom;
	}

	/**
	 * @return the tCkCtRateTable
	 */
	public CkCtRateTable getTCkCtRateTable() {
		return TCkCtRateTable;
	}

	/**
	 * @param tCkCtRateTable the tCkCtRateTable to set
	 */
	public void setTCkCtRateTable(CkCtRateTable tCkCtRateTable) {
		TCkCtRateTable = tCkCtRateTable;
	}

	/**
	 * @return the trCharge
	 */
	public BigDecimal getTrCharge() {
		return trCharge;
	}

	/**
	 * @param trCharge the trCharge to set
	 */
	public void setTrCharge(BigDecimal trCharge) {
		this.trCharge = trCharge;
	}

	/**
	 * @return the trStatus
	 */
	public Character getTrStatus() {
		return trStatus;
	}

	/**
	 * @param trStatus the trStatus to set
	 */
	public void setTrStatus(Character trStatus) {
		this.trStatus = trStatus;
	}

	/**
	 * @return the trDtCreate
	 */
	public Date getTrDtCreate() {
		return trDtCreate;
	}

	/**
	 * @param trDtCreate the trDtCreate to set
	 */
	public void setTrDtCreate(Date trDtCreate) {
		this.trDtCreate = trDtCreate;
	}

	/**
	 * @return the trUidCreate
	 */
	public String getTrUidCreate() {
		return trUidCreate;
	}

	/**
	 * @param trUidCreate the trUidCreate to set
	 */
	public void setTrUidCreate(String trUidCreate) {
		this.trUidCreate = trUidCreate;
	}

	/**
	 * @return the trDtLupd
	 */
	public Date getTrDtLupd() {
		return trDtLupd;
	}

	/**
	 * @param trDtLupd the trDtLupd to set
	 */
	public void setTrDtLupd(Date trDtLupd) {
		this.trDtLupd = trDtLupd;
	}

	/**
	 * @return the trUidLupd
	 */
	public String getTrUidLupd() {
		return trUidLupd;
	}

	/**
	 * @param trUidLupd the trUidLupd to set
	 */
	public void setTrUidLupd(String trUidLupd) {
		this.trUidLupd = trUidLupd;
	}

	public CkCtMstVehType getTCkCtMstVehType() {
		return TCkCtMstVehType;
	}

	/**
	 * @param tCkCtMstVehType the tCkCtMstVehType to set
	 */
	public void setTCkCtMstVehType(CkCtMstVehType tCkCtMstVehType) {
		TCkCtMstVehType = tCkCtMstVehType;
	}

	public CkCtTripRate getTCkCtTripRate() {
		return TCkCtTripRate;
	}

	public void setTCkCtTripRate(CkCtTripRate tCkCtTripRate) {
		TCkCtTripRate = tCkCtTripRate;
	}

	public String getTrType() {
		return trType;
	}

	public void setTrType(String trType) {
		this.trType = trType;
	}

	public Integer getTrSeq() {
		return trSeq;
	}

	public void setTrSeq(Integer trSeq) {
		this.trSeq = trSeq;
	}

	public List<CkCtTripRate> getTCkCtTripRates() {
		return TCkCtTripRates;
	}

	public void setTCkCtTripRates(List<CkCtTripRate> tCkCtTripRates) {
		TCkCtTripRates = tCkCtTripRates;
	}

	public String getTrTypeFilter() {
		return trTypeFilter;
	}

	public void setTrTypeFilter(String trTypeFilter) {
		this.trTypeFilter = trTypeFilter;
	}

	public JobActions getAction() {
		return action;
	}

	public void setAction(JobActions action) {
		this.action = action;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

}
