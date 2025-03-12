package com.guudint.clickargo.clictruck.admin.contract.dto;

import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.dto.MstBank;
import com.vcc.camelone.master.dto.MstCurrency;

public class CkCtContract extends AbstractDTO<CkCtContract, TCkCtContract> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -810869705016771861L;
	public static final char STATUS_EXPIRED = 'E';

	// Attributes
	/////////////
	private String conId;
	private CkCtContractCharge TCkCtContractChargeByConChargeTo;
	private Integer conPaytermTo;
	private CkCtContractCharge TCkCtContractChargeByConChargeCoFf;
	private Integer conPaytermCoFf;
	private CoreAccn TCoreAccnByConTo;
	private CoreAccn TCoreAccnByConCoFf;
	private MstCurrency TMstCurrency;
	private String conName;
	private String conDescription;
	private Date conDtStart;
	private Date conDtEnd;
	private Character conStatus;
	private Date conDtCreate;
	private String conUidCreate;
	private Date conDtLupd;
	private String conUidLupd;
	private boolean additionalTaxTo;
	private boolean witholdTaxTo;
	private boolean additionalTaxCoFf;
	private boolean witholdTaxCoFf;

	// holder for cs filter
	private boolean isForCsView;
	private boolean isEditable;
	private List<CoreAccn> accnForCsList;

	// OPM
	private String conFinanceModel;
	private CkCtContractCharge TCkCtContractChargeByConOpm;
	private MstBank TMstBank;

	// Constructors
	///////////////
	public CkCtContract() {
	}

	/**
	 * @param entity
	 */
	public CkCtContract(TCkCtContract entity) {
		super(entity);
	}

	/**
	 * @param conId
	 * @param tCkCtContractChargeByConChargeTo
	 * @param tCkCtContractChargeByConChargeCoFf
	 * @param tCoreAccnByConTo
	 * @param tCoreAccnByConCoFf
	 * @param tMstCurrency
	 * @param conName
	 * @param conDescription
	 * @param conDtStart
	 * @param conDtEnd
	 * @param conStatus
	 * @param conDtCreate
	 * @param conUidCreate
	 * @param conDtLupd
	 * @param conUidLupd
	 */
	public CkCtContract(String conId, CkCtContractCharge tCkCtContractChargeByConChargeTo, Integer conPaytermTo,
			CkCtContractCharge tCkCtContractChargeByConChargeCoFf, Integer conPaytermCoFf, CoreAccn tCoreAccnByConTo,
			CoreAccn tCoreAccnByConCoFf, MstCurrency tMstCurrency, String conName, String conDescription,
			Date conDtStart, Date conDtEnd, Character conStatus, Date conDtCreate, String conUidCreate, Date conDtLupd,
			String conUidLupd) {
		super();
		this.conId = conId;
		TCkCtContractChargeByConChargeTo = tCkCtContractChargeByConChargeTo;
		this.conPaytermTo = conPaytermTo;
		this.conPaytermCoFf = conPaytermCoFf;
		TCkCtContractChargeByConChargeCoFf = tCkCtContractChargeByConChargeCoFf;
		TCoreAccnByConTo = tCoreAccnByConTo;
		TCoreAccnByConCoFf = tCoreAccnByConCoFf;
		TMstCurrency = tMstCurrency;
		this.conName = conName;
		this.conDescription = conDescription;
		this.conDtStart = conDtStart;
		this.conDtEnd = conDtEnd;
		this.conStatus = conStatus;
		this.conDtCreate = conDtCreate;
		this.conUidCreate = conUidCreate;
		this.conDtLupd = conDtLupd;
		this.conUidLupd = conUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the conId
	 */
	public String getConId() {
		return conId;
	}

	/**
	 * @param conId the conId to set
	 */
	public void setConId(String conId) {
		this.conId = conId;
	}

	/**
	 * @return the tCkCtContractChargeByConChargeTo
	 */
	public CkCtContractCharge getTCkCtContractChargeByConChargeTo() {
		return TCkCtContractChargeByConChargeTo;
	}

	/**
	 * @param tCkCtContractChargeByConChargeTo the tCkCtContractChargeByConChargeTo
	 *                                         to set
	 */
	public void setTCkCtContractChargeByConChargeTo(CkCtContractCharge tCkCtContractChargeByConChargeTo) {
		TCkCtContractChargeByConChargeTo = tCkCtContractChargeByConChargeTo;
	}

	/**
	 * @return the tCkCtContractChargeByConChargeCoFf
	 */
	public CkCtContractCharge getTCkCtContractChargeByConChargeCoFf() {
		return TCkCtContractChargeByConChargeCoFf;
	}

	public Integer getConPaytermTo() {
		return this.conPaytermTo;
	}

	public void setConPaytermTo(int conPaytermTo) {
		this.conPaytermTo = conPaytermTo;
	}

	public Integer getConPaytermCoFf() {
		return this.conPaytermCoFf;
	}

	public void setConPaytermCoFf(int conPaytermCoFf) {
		this.conPaytermCoFf = conPaytermCoFf;
	}

	/**
	 * @param tCkCtContractChargeByConChargeCoFf the
	 *                                           tCkCtContractChargeByConChargeCoFf
	 *                                           to set
	 */
	public void setTCkCtContractChargeByConChargeCoFf(CkCtContractCharge tCkCtContractChargeByConChargeCoFf) {
		TCkCtContractChargeByConChargeCoFf = tCkCtContractChargeByConChargeCoFf;
	}

	/**
	 * @return the tCoreAccnByConTo
	 */
	public CoreAccn getTCoreAccnByConTo() {
		return TCoreAccnByConTo;
	}

	/**
	 * @param tCoreAccnByConTo the tCoreAccnByConTo to set
	 */
	public void setTCoreAccnByConTo(CoreAccn tCoreAccnByConTo) {
		TCoreAccnByConTo = tCoreAccnByConTo;
	}

	/**
	 * @return the tCoreAccnByConCoFf
	 */
	public CoreAccn getTCoreAccnByConCoFf() {
		return TCoreAccnByConCoFf;
	}

	/**
	 * @param tCoreAccnByConCoFf the tCoreAccnByConCoFf to set
	 */
	public void setTCoreAccnByConCoFf(CoreAccn tCoreAccnByConCoFf) {
		TCoreAccnByConCoFf = tCoreAccnByConCoFf;
	}

	/**
	 * @return the tMstCurrency
	 */
	public MstCurrency getTMstCurrency() {
		return TMstCurrency;
	}

	/**
	 * @param tMstCurrency the tMstCurrency to set
	 */
	public void setTMstCurrency(MstCurrency tMstCurrency) {
		TMstCurrency = tMstCurrency;
	}

	/**
	 * @return the conName
	 */
	public String getConName() {
		return conName;
	}

	/**
	 * @param conName the conName to set
	 */
	public void setConName(String conName) {
		this.conName = conName;
	}

	/**
	 * @return the conDescription
	 */
	public String getConDescription() {
		return conDescription;
	}

	/**
	 * @param conDescription the conDescription to set
	 */
	public void setConDescription(String conDescription) {
		this.conDescription = conDescription;
	}

	/**
	 * @return the conDtStart
	 */
	public Date getConDtStart() {
		return conDtStart;
	}

	/**
	 * @param conDtStart the conDtStart to set
	 */
	public void setConDtStart(Date conDtStart) {
		this.conDtStart = conDtStart;
	}

	/**
	 * @return the conDtEnd
	 */
	public Date getConDtEnd() {
		return conDtEnd;
	}

	/**
	 * @param conDtEnd the conDtEnd to set
	 */
	public void setConDtEnd(Date conDtEnd) {
		this.conDtEnd = conDtEnd;
	}

	public String getConFinanceModel() {
		return this.conFinanceModel;
	}

	public void setConFinanceModel(String conFinanceModel) {
		this.conFinanceModel = conFinanceModel;
	}

	public CkCtContractCharge getTCkCtContractChargeByConOpm() {
		return TCkCtContractChargeByConOpm;
	}

	public void setTCkCtContractChargeByConOpm(CkCtContractCharge tCkCtContractChargeByConOpm) {
		TCkCtContractChargeByConOpm = tCkCtContractChargeByConOpm;
	}

	public MstBank getTMstBank() {
		return TMstBank;
	}

	public void setTMstBank(MstBank tMstBank) {
		TMstBank = tMstBank;
	}

	/**
	 * @return the conStatus
	 */
	public Character getConStatus() {
		return conStatus;
	}

	/**
	 * @param conStatus the conStatus to set
	 */
	public void setConStatus(Character conStatus) {
		this.conStatus = conStatus;
	}

	/**
	 * @return the conDtCreate
	 */
	public Date getConDtCreate() {
		return conDtCreate;
	}

	/**
	 * @param conDtCreate the conDtCreate to set
	 */
	public void setConDtCreate(Date conDtCreate) {
		this.conDtCreate = conDtCreate;
	}

	/**
	 * @return the conUidCreate
	 */
	public String getConUidCreate() {
		return conUidCreate;
	}

	/**
	 * @param conUidCreate the conUidCreate to set
	 */
	public void setConUidCreate(String conUidCreate) {
		this.conUidCreate = conUidCreate;
	}

	/**
	 * @return the conDtLupd
	 */
	public Date getConDtLupd() {
		return conDtLupd;
	}

	/**
	 * @param conDtLupd the conDtLupd to set
	 */
	public void setConDtLupd(Date conDtLupd) {
		this.conDtLupd = conDtLupd;
	}

	/**
	 * @return the conUidLupd
	 */
	public String getConUidLupd() {
		return conUidLupd;
	}

	/**
	 * @param conUidLupd the conUidLupd to set
	 */
	public void setConUidLupd(String conUidLupd) {
		this.conUidLupd = conUidLupd;
	}

	public boolean isAdditionalTaxTo() {
		return this.additionalTaxTo;
	}

	public void setAdditionalTaxTo(boolean additionalTaxTo) {
		this.additionalTaxTo = additionalTaxTo;
	}

	public boolean isWitholdTaxTo() {
		return this.witholdTaxTo;
	}

	public void setWitholdTaxTo(boolean witholdTaxTo) {
		this.witholdTaxTo = witholdTaxTo;
	}

	public boolean isAdditionalTaxCoFf() {
		return this.additionalTaxCoFf;
	}

	public void setIsAdditionalTaxCoFf(boolean additionalTaxCoFf) {
		this.additionalTaxCoFf = additionalTaxCoFf;
	}

	public boolean isWitholdTaxCoFf() {
		return this.witholdTaxCoFf;
	}

	public void setWitholdTaxCoFf(boolean witholdTaxCoFf) {
		this.witholdTaxCoFf = witholdTaxCoFf;
	}

	public void setConPaytermTo(Integer conPaytermTo) {
		this.conPaytermTo = conPaytermTo;
	}

	public void setConPaytermCoFf(Integer conPaytermCoFf) {
		this.conPaytermCoFf = conPaytermCoFf;
	}

	public void setAdditionalTaxCoFf(boolean additionalTaxCoFf) {
		this.additionalTaxCoFf = additionalTaxCoFf;
	}

	public List<CoreAccn> getAccnForCsList() {
		return accnForCsList;
	}

	public void setAccnForCsList(List<CoreAccn> accnForCsList) {
		this.accnForCsList = accnForCsList;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public boolean isForCsView() {
		return isForCsView;
	}

	public void setForCsView(boolean isForCsView) {
		this.isForCsView = isForCsView;
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
	public int compareTo(CkCtContract o) {
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
