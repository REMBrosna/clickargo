package com.guudint.clickargo.clictruck.admin.contract.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractReq;
import com.guudint.clickargo.master.enums.FormActions;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.dto.MstBank;
import com.vcc.camelone.master.dto.MstCurrency;

public class CkCtContractReq extends AbstractDTO<CkCtContractReq, TCkCtContractReq> {

	private static final long serialVersionUID = 8036210985768526431L;
	private String crId;
	private CkCtContractCharge TCkCtContractChargeByCrChargeTo;
	private CkCtContractCharge TCkCtContractChargeByCrChargeCoFf;
	private CkCtContractCharge TCkCtContractChargeByCrChargeOpm;
	private CkCtMstContractReqState TCkCtMstContractReqState;
	private CoreAccn TCoreAccnByCrTo;
	private CoreAccn TCoreAccnByCrCoFf;
	private MstBank TMstBank;
	private MstCurrency TMstCurrency;
	private String crName;
	private String crDescription;
	private Date crDtStart;
	private Date crDtEnd;
	private Integer crPaytermTo;
	private Integer crPaytermCoFf;
	private String crUidRequestor;
	private String crCommentRequestor;
	private Date crDtSubmit;
	private String crUidApprover;
	private String crCommentApprover;
	private Date crDtApproveReject;
	private String crComment;
	private Character crStatus;
	private Date crDtCreate;
	private String crUidCreate;
	private Date crDtLupd;
	private String crUidLupd;

	private FormActions action;
	private String history;
	private boolean isL1;
	private boolean isFinance;

	private boolean additionalTaxTo;
	private boolean witholdTaxTo;
	private boolean additionalTaxCoFf;
	private boolean witholdTaxCoFf;
	private boolean additionalTaxOpm;
	private boolean withholdTaxOpm;
	
	private String crFinanceModel;

	public CkCtContractReq() {
	}

	public CkCtContractReq(TCkCtContractReq entity) {
		super(entity);
	}

	public CkCtContractReq(String crId, CkCtMstContractReqState TCkCtMstContractReqState) {
		this.crId = crId;
		this.TCkCtMstContractReqState = TCkCtMstContractReqState;
	}

	public CkCtContractReq(String crId, CkCtContractCharge TCkCtContractChargeByCrChargeTo,
			CkCtContractCharge TCkCtContractChargeByCrChargeCoFf, CkCtMstContractReqState TCkCtMstContractReqState,
			CoreAccn TCoreAccnByCrTo, CoreAccn TCoreAccnByCrCoFf, MstCurrency TMstCurrency, String crName,
			String crDescription, Date crDtStart, Date crDtEnd, Integer crPaytermTo, Integer crPaytermCoFf,
			String crUidRequestor, String crCommentRequestor, Date crDtSubmit, String crUidApprover,
			String crCommentApprover, Date crDtApproveReject, String crComment, Character crStatus, Date crDtCreate,
			String crUidCreate, Date crDtLupd, String crUidLupd) {
		this.crId = crId;
		this.TCkCtContractChargeByCrChargeTo = TCkCtContractChargeByCrChargeTo;
		this.TCkCtContractChargeByCrChargeCoFf = TCkCtContractChargeByCrChargeCoFf;
		this.TCkCtMstContractReqState = TCkCtMstContractReqState;
		this.TCoreAccnByCrTo = TCoreAccnByCrTo;
		this.TCoreAccnByCrCoFf = TCoreAccnByCrCoFf;
		this.TMstCurrency = TMstCurrency;
		this.crName = crName;
		this.crDescription = crDescription;
		this.crDtStart = crDtStart;
		this.crDtEnd = crDtEnd;
		this.crPaytermTo = crPaytermTo;
		this.crPaytermCoFf = crPaytermCoFf;
		this.crUidRequestor = crUidRequestor;
		this.crCommentRequestor = crCommentRequestor;
		this.crDtSubmit = crDtSubmit;
		this.crUidApprover = crUidApprover;
		this.crCommentApprover = crCommentApprover;
		this.crDtApproveReject = crDtApproveReject;
		this.crComment = crComment;
		this.crStatus = crStatus;
		this.crDtCreate = crDtCreate;
		this.crUidCreate = crUidCreate;
		this.crDtLupd = crDtLupd;
		this.crUidLupd = crUidLupd;
	}

	/**
	 * @return the crId
	 */
	public String getCrId() {
		return crId;
	}

	/**
	 * @param crId the crId to set
	 */
	public void setCrId(String crId) {
		this.crId = crId;
	}

	/**
	 * @return the tCkCtContractChargeByCrChargeTo
	 */
	public CkCtContractCharge getTCkCtContractChargeByCrChargeTo() {
		return TCkCtContractChargeByCrChargeTo;
	}

	/**
	 * @param tCkCtContractChargeByCrChargeTo the tCkCtContractChargeByCrChargeTo to
	 *                                        set
	 */
	public void setTCkCtContractChargeByCrChargeTo(CkCtContractCharge tCkCtContractChargeByCrChargeTo) {
		TCkCtContractChargeByCrChargeTo = tCkCtContractChargeByCrChargeTo;
	}

	/**
	 * @return the tCkCtContractChargeByCrChargeCoFf
	 */
	public CkCtContractCharge getTCkCtContractChargeByCrChargeCoFf() {
		return TCkCtContractChargeByCrChargeCoFf;
	}

	/**
	 * @param tCkCtContractChargeByCrChargeCoFf the
	 *                                          tCkCtContractChargeByCrChargeCoFf to
	 *                                          set
	 */
	public void setTCkCtContractChargeByCrChargeCoFf(CkCtContractCharge tCkCtContractChargeByCrChargeCoFf) {
		TCkCtContractChargeByCrChargeCoFf = tCkCtContractChargeByCrChargeCoFf;
	}

	/**
	 * @return the tCkCtMstContractReqState
	 */
	public CkCtMstContractReqState getTCkCtMstContractReqState() {
		return TCkCtMstContractReqState;
	}

	/**
	 * @param tCkCtMstContractReqState the tCkCtMstContractReqState to set
	 */
	public void setTCkCtMstContractReqState(CkCtMstContractReqState tCkCtMstContractReqState) {
		TCkCtMstContractReqState = tCkCtMstContractReqState;
	}

	/**
	 * @return the tCkCtContractChargeByCrChargeOpm
	 */
	public CkCtContractCharge getTCkCtContractChargeByCrChargeOpm() {
		return TCkCtContractChargeByCrChargeOpm;
	}

	/**
	 * @param tCkCtContractChargeByCrChargeOpm the tCkCtContractChargeByCrChargeOpm
	 *                                         to set
	 */
	public void setTCkCtContractChargeByCrChargeOpm(CkCtContractCharge tCkCtContractChargeByCrChargeOpm) {
		TCkCtContractChargeByCrChargeOpm = tCkCtContractChargeByCrChargeOpm;
	}

	/**
	 * @return the tCoreAccnByCrTo
	 */
	public CoreAccn getTCoreAccnByCrTo() {
		return TCoreAccnByCrTo;
	}

	/**
	 * @param tCoreAccnByCrTo the tCoreAccnByCrTo to set
	 */
	public void setTCoreAccnByCrTo(CoreAccn tCoreAccnByCrTo) {
		TCoreAccnByCrTo = tCoreAccnByCrTo;
	}

	/**
	 * @return the tCoreAccnByCrCoFf
	 */
	public CoreAccn getTCoreAccnByCrCoFf() {
		return TCoreAccnByCrCoFf;
	}

	/**
	 * @param tCoreAccnByCrCoFf the tCoreAccnByCrCoFf to set
	 */
	public void setTCoreAccnByCrCoFf(CoreAccn tCoreAccnByCrCoFf) {
		TCoreAccnByCrCoFf = tCoreAccnByCrCoFf;
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
	 * @return the crName
	 */
	public String getCrName() {
		return crName;
	}

	/**
	 * @param crName the crName to set
	 */
	public void setCrName(String crName) {
		this.crName = crName;
	}

	/**
	 * @return the crDescription
	 */
	public String getCrDescription() {
		return crDescription;
	}

	/**
	 * @param crDescription the crDescription to set
	 */
	public void setCrDescription(String crDescription) {
		this.crDescription = crDescription;
	}

	/**
	 * @return the crDtStart
	 */
	public Date getCrDtStart() {
		return crDtStart;
	}

	/**
	 * @param crDtStart the crDtStart to set
	 */
	public void setCrDtStart(Date crDtStart) {
		this.crDtStart = crDtStart;
	}

	/**
	 * @return the crDtEnd
	 */
	public Date getCrDtEnd() {
		return crDtEnd;
	}

	/**
	 * @param crDtEnd the crDtEnd to set
	 */
	public void setCrDtEnd(Date crDtEnd) {
		this.crDtEnd = crDtEnd;
	}

	/**
	 * @return the crPaytermTo
	 */
	public Integer getCrPaytermTo() {
		return crPaytermTo;
	}

	/**
	 * @param crPaytermTo the crPaytermTo to set
	 */
	public void setCrPaytermTo(Integer crPaytermTo) {
		this.crPaytermTo = crPaytermTo;
	}

	/**
	 * @return the crPaytermCoFf
	 */
	public Integer getCrPaytermCoFf() {
		return crPaytermCoFf;
	}

	/**
	 * @param crPaytermCoFf the crPaytermCoFf to set
	 */
	public void setCrPaytermCoFf(Integer crPaytermCoFf) {
		this.crPaytermCoFf = crPaytermCoFf;
	}

	/**
	 * @return the crUidRequestor
	 */
	public String getCrUidRequestor() {
		return crUidRequestor;
	}

	/**
	 * @param crUidRequestor the crUidRequestor to set
	 */
	public void setCrUidRequestor(String crUidRequestor) {
		this.crUidRequestor = crUidRequestor;
	}

	/**
	 * @return the crCommentRequestor
	 */
	public String getCrCommentRequestor() {
		return crCommentRequestor;
	}

	/**
	 * @param crCommentRequestor the crCommentRequestor to set
	 */
	public void setCrCommentRequestor(String crCommentRequestor) {
		this.crCommentRequestor = crCommentRequestor;
	}

	/**
	 * @return the crDtSubmit
	 */
	public Date getCrDtSubmit() {
		return crDtSubmit;
	}

	/**
	 * @param crDtSubmit the crDtSubmit to set
	 */
	public void setCrDtSubmit(Date crDtSubmit) {
		this.crDtSubmit = crDtSubmit;
	}

	/**
	 * @return the crUidApprover
	 */
	public String getCrUidApprover() {
		return crUidApprover;
	}

	/**
	 * @param crUidApprover the crUidApprover to set
	 */
	public void setCrUidApprover(String crUidApprover) {
		this.crUidApprover = crUidApprover;
	}

	/**
	 * @return the crCommentApprover
	 */
	public String getCrCommentApprover() {
		return crCommentApprover;
	}

	/**
	 * @param crCommentApprover the crCommentApprover to set
	 */
	public void setCrCommentApprover(String crCommentApprover) {
		this.crCommentApprover = crCommentApprover;
	}

	/**
	 * @return the crDtApproveReject
	 */
	public Date getCrDtApproveReject() {
		return crDtApproveReject;
	}

	/**
	 * @param crDtApproveReject the crDtApproveReject to set
	 */
	public void setCrDtApproveReject(Date crDtApproveReject) {
		this.crDtApproveReject = crDtApproveReject;
	}

	/**
	 * @return the crComment
	 */
	public String getCrComment() {
		return crComment;
	}

	/**
	 * @param crComment the crComment to set
	 */
	public void setCrComment(String crComment) {
		this.crComment = crComment;
	}

	/**
	 * @return the crStatus
	 */
	public Character getCrStatus() {
		return crStatus;
	}

	/**
	 * @param crStatus the crStatus to set
	 */
	public void setCrStatus(Character crStatus) {
		this.crStatus = crStatus;
	}

	/**
	 * @return the crDtCreate
	 */
	public Date getCrDtCreate() {
		return crDtCreate;
	}

	/**
	 * @param crDtCreate the crDtCreate to set
	 */
	public void setCrDtCreate(Date crDtCreate) {
		this.crDtCreate = crDtCreate;
	}

	/**
	 * @return the crUidCreate
	 */
	public String getCrUidCreate() {
		return crUidCreate;
	}

	/**
	 * @param crUidCreate the crUidCreate to set
	 */
	public void setCrUidCreate(String crUidCreate) {
		this.crUidCreate = crUidCreate;
	}

	/**
	 * @return the crDtLupd
	 */
	public Date getCrDtLupd() {
		return crDtLupd;
	}

	/**
	 * @param crDtLupd the crDtLupd to set
	 */
	public void setCrDtLupd(Date crDtLupd) {
		this.crDtLupd = crDtLupd;
	}

	/**
	 * @return the crUidLupd
	 */
	public String getCrUidLupd() {
		return crUidLupd;
	}

	/**
	 * @param crUidLupd the crUidLupd to set
	 */
	public void setCrUidLupd(String crUidLupd) {
		this.crUidLupd = crUidLupd;
	}

	@Override
	public int compareTo(CkCtContractReq o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the action
	 */
	public FormActions getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(FormActions action) {
		this.action = action;
	}

	/**
	 * @return the history
	 */
	public String getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(String history) {
		this.history = history;
	}

	/**
	 * @return the isL1
	 */
	public boolean isL1() {
		return isL1;
	}

	/**
	 * @param isL1 the isL1 to set
	 */
	public void setL1(boolean isL1) {
		this.isL1 = isL1;
	}

	/**
	 * @return the isFinance
	 */
	public boolean isFinance() {
		return isFinance;
	}

	/**
	 * @param isFinance the isFinance to set
	 */
	public void setFinance(boolean isFinance) {
		this.isFinance = isFinance;
	}

	/**
	 * @return the additionalTaxTo
	 */
	public boolean isAdditionalTaxTo() {
		return additionalTaxTo;
	}

	/**
	 * @param additionalTaxTo the additionalTaxTo to set
	 */
	public void setAdditionalTaxTo(boolean additionalTaxTo) {
		this.additionalTaxTo = additionalTaxTo;
	}

	/**
	 * @return the witholdTaxTo
	 */
	public boolean isWitholdTaxTo() {
		return witholdTaxTo;
	}

	/**
	 * @param witholdTaxTo the witholdTaxTo to set
	 */
	public void setWitholdTaxTo(boolean witholdTaxTo) {
		this.witholdTaxTo = witholdTaxTo;
	}

	/**
	 * @return the additionalTaxCoFf
	 */
	public boolean isAdditionalTaxCoFf() {
		return additionalTaxCoFf;
	}

	/**
	 * @param additionalTaxCoFf the additionalTaxCoFf to set
	 */
	public void setAdditionalTaxCoFf(boolean additionalTaxCoFf) {
		this.additionalTaxCoFf = additionalTaxCoFf;
	}

	/**
	 * @return the witholdTaxCoFf
	 */
	public boolean isWitholdTaxCoFf() {
		return witholdTaxCoFf;
	}

	/**
	 * @param witholdTaxCoFf the witholdTaxCoFf to set
	 */
	public void setWitholdTaxCoFf(boolean witholdTaxCoFf) {
		this.witholdTaxCoFf = witholdTaxCoFf;
	}

	/**
	 * @return the tMstBank
	 */
	public MstBank getTMstBank() {
		return TMstBank;
	}

	/**
	 * @param tMstBank the tMstBank to set
	 */
	public void setTMstBank(MstBank tMstBank) {
		TMstBank = tMstBank;
	}

	/**
	 * @return the additionalTaxOpm
	 */
	public boolean isAdditionalTaxOpm() {
		return additionalTaxOpm;
	}

	/**
	 * @param additionalTaxOpm the additionalTaxOpm to set
	 */
	public void setAdditionalTaxOpm(boolean additionalTaxOpm) {
		this.additionalTaxOpm = additionalTaxOpm;
	}

	/**
	 * @return the withholdTaxOpm
	 */
	public boolean isWithholdTaxOpm() {
		return withholdTaxOpm;
	}

	/**
	 * @param withholdTaxOpm the withholdTaxOpm to set
	 */
	public void setWithholdTaxOpm(boolean withholdTaxOpm) {
		this.withholdTaxOpm = withholdTaxOpm;
	}

	/**
	 * @return the crFinanceModel
	 */
	public String getCrFinanceModel() {
		return crFinanceModel;
	}

	/**
	 * @param crFinanceModel the crFinanceModel to set
	 */
	public void setCrFinanceModel(String crFinanceModel) {
		this.crFinanceModel = crFinanceModel;
	}

	
	

}
