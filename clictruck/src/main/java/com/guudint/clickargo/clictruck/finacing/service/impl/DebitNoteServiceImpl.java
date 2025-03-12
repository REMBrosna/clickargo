package com.guudint.clickargo.clictruck.finacing.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.dto.InvoiceFields;
import com.guudint.clickargo.clictruck.dto.InvoiceSubLine;
import com.guudint.clickargo.clictruck.finacing.constant.FinancingConstants.FinancingTypes;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtDebitNoteDao;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtDebitNoteItemDao;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote.DebitNoteStates;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNoteItem;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNoteItem;
import com.guudint.clickargo.clictruck.finacing.service.AbstractPaymentUtil;
import com.guudint.clickargo.clictruck.finacing.service.IDebitNoteService;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService.InvoiceTypes;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstDebitNoteState;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstDebitNoteState;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.ICkSeqNoService;
import com.guudint.clickargo.common.service.impl.CkSeqNoServiceImpl;
import com.guudint.clickargo.docs.util.JasperDocsUtil;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreAddress;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.master.dto.MstCurrency;
import com.vcc.camelone.master.model.TMstCurrency;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Debit note service implementation.
 */
@Service
public class DebitNoteServiceImpl extends AbstractPaymentUtil implements IDebitNoteService {

	private static final Logger LOG = Logger.getLogger(DebitNoteServiceImpl.class);

	@Autowired
	private CkCtDebitNoteDao ckCtDebitNoteDao;

	@Autowired
	private CkCtDebitNoteItemDao ckCtDebitNoteItemDao;

	@Autowired
	private ICkSeqNoService seqnoService;

	private SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");

	@Override
	public CkCtDebitNote createDebitNote(CkJobTruck jobTruck, CkCtContract contract, CoreAccn from, CoreAccn to,
			boolean isApplyStampDuty) throws Exception {

		if (jobTruck == null)
			throw new ParameterException("param jobTruck null");
		if (from == null)
			throw new ParameterException("param from null");
		if (to == null)
			throw new ParameterException("param to null");

		Calendar calendar = Calendar.getInstance();
		TCkCtDebitNote dnEntity = new TCkCtDebitNote();
		TCkCtMstDebitNoteState dnState = new TCkCtMstDebitNoteState(DebitNoteStates.NEW.name(),
				DebitNoteStates.NEW.name());
		dnEntity.setTCkCtMstDebitNoteState(dnState);

		this.computeDebitNoteAmount(dnEntity, jobTruck, isApplyStampDuty);

		dnEntity.setTCoreAccnByDnFrom(from.toEntity(new TCoreAccn()));
		dnEntity.setTCoreAccnByDnTo(to.toEntity(new TCoreAccn()));
		dnEntity.setDnJobId(jobTruck.getJobId());
		// TODO to change dnNo mentioned provided by TO
		String dnId = CkUtil.generateId(CkCtDebitNote.PREFIX_ID);
		dnEntity.setDnId(dnId);
		// TODO where to get the ageBand? and the payment?
		dnEntity.setDnAgeBand((short) 1);

		Date now = calendar.getTime();

		// 20230904 Due to the changes in workflow in billing approval, the date is
		// captured from when the billing is approved (acknowledged) by CO
		Calendar coApprovedCal = Calendar.getInstance();

		// if there is bill acknowledged date, otherwise it will be set to current date
		if (jobTruck.getTCkJob().getTCkRecordDate().getRcdDtBillAcknowledged() != null) {
			coApprovedCal.setTime(jobTruck.getTCkJob().getTCkRecordDate().getRcdDtBillAcknowledged());
		}

		// set the date for invoice no, before it is calculated with payment terms/7
		// days payment
//		Date coAppDate = coApprovedCal.getTime();

		Calendar issueDateCal = Calendar.getInstance();
		issueDateCal.setTime(new Date());
		dnEntity.setDnDtIssue(issueDateCal.getTime());

		issueDateCal.setTime(super.getInitIssueDate());

		if (contract == null) {
			// get from sysparam that means this is TO to SP
			int dueDate = Integer.valueOf(getSysParam(IPaymentService.KEY_CLICTRUCK_DEFAULT_PAYTERMS_TO));
			coApprovedCal.add(Calendar.DAY_OF_YEAR, dueDate);
			dnEntity.setDnDtDue(coApprovedCal.getTime());
		} else {

			if (contract.getConPaytermCoFf() != null) {
				coApprovedCal.add(Calendar.DAY_OF_YEAR, contract.getConPaytermCoFf());
				dnEntity.setDnDtDue(coApprovedCal.getTime());

			} else {
				dnEntity.setDnDtDue(coApprovedCal.getTime());
			}

		}
		dnEntity.setDnPaymentRef(jobTruck.getJobShipmentRef());
		dnEntity.setDnDtCreate(now);
		dnEntity.setDnUidCreate("SYS");
		dnEntity.setDnDtLupd(now);
		dnEntity.setDnUidLupd("SYS");
		dnEntity.setDnStatus(RecordStatus.ACTIVE.getCode());

//		String dnNoSeq = seqnoService.getNextSequence(CkSeqNoServiceImpl.SeqNoCode.CT_DN_NO.name());
		String dnNo = null;
//		if (StringUtils.isBlank(dnNoSeq)) {
		// if no configuration found just set to systimemillis
		dnNo = CkUtil.generateId("DN");
//		} else {
//			// set the invoice no, based on the co approval date, not the current date gli
//			// approved
//			dnNo = dnNoSeq.replaceAll("YYYYMMDD", sdf.format(coAppDate));
//		}
		dnEntity.setDnNo(dnNo + NO_POSTFIX);

		// 20250322 OPM - if opm = oc/ot set the dn amount to 0
		boolean isOpmFinancing = false;
		// temporary holder to set after saving
		if (Arrays.asList(FinancingTypes.OC.name(), FinancingTypes.OT.name()).contains(jobTruck.getJobFinanceOpt())) {
			isOpmFinancing = true;
		}

		if (isOpmFinancing)
			dnEntity.setDnTotal(new BigDecimal(0));

		ckCtDebitNoteDao.add(dnEntity);

		CkCtDebitNote dnDto = new CkCtDebitNote(dnEntity);

		dnDto.setTCoreAccnByDnFrom(from);
		dnDto.setTCoreAccnByDnTo(to);
		dnDto.setTCkCtMstDebitNoteState(new CkCtMstDebitNoteState(dnState));

		List<CkCtDebitNoteItem> debiteNoteItems = new ArrayList<>();
		TMstCurrency curr = new TMstCurrency();
		curr.setCcyCode(defCurrency);

		// Add debitnote item for total trip charge
		TCkCtDebitNoteItem dnItemTripCharge = new TCkCtDebitNoteItem();
		dnItemTripCharge.setItmId(CkUtil.generateId(CkCtDebitNote.PREFIX_ID));
		dnItemTripCharge.setTCkCtDebitNote(dnEntity);
		dnItemTripCharge.setTMstCurrency(curr);
		dnItemTripCharge.setItmItem(String.format(IDebitNoteService.DN_ITEM_DESC_TRIP_CHARGES, jobTruck.getJobId()));
		dnItemTripCharge.setItmSno((short) 1);
		dnItemTripCharge.setItmQty((short) 1);
		dnItemTripCharge.setItmAmount(jobTruck.getJobTotalCharge().setScale(0, RoundingMode.HALF_UP));
		dnItemTripCharge.setItmUnitPrice(jobTruck.getJobTotalCharge().setScale(0, RoundingMode.HALF_UP));
		dnItemTripCharge.setItmRef(jobTruck.getJobId());
		dnItemTripCharge.setItmStatus(RecordStatus.ACTIVE.getCode());
		dnItemTripCharge.setItmDtCreate(now);
		dnItemTripCharge.setItmUidCreate("SYS");
		dnItemTripCharge.setItmDtLupd(now);
		dnItemTripCharge.setItmUidLupd("SYS");
		ckCtDebitNoteItemDao.add(dnItemTripCharge);
		CkCtDebitNoteItem itm1 = new CkCtDebitNoteItem(dnItemTripCharge);
		itm1.setTCkCtDebitNote(dnDto);
		itm1.setTMstCurrency(new MstCurrency(dnItemTripCharge.getTMstCurrency()));
		debiteNoteItems.add(itm1);

		// Add debite note item for total reimbursement
		TCkCtDebitNoteItem dnItemReimCharge = new TCkCtDebitNoteItem();
		dnItemReimCharge.setItmId(CkUtil.generateId(CkCtDebitNote.PREFIX_ID));
		dnItemReimCharge.setTCkCtDebitNote(dnEntity);
		dnItemReimCharge.setTMstCurrency(curr);
		dnItemReimCharge.setItmItem(String.format(IDebitNoteService.DN_ITEM_DESC_REIM_CHRAGES, jobTruck.getJobId()));
		dnItemReimCharge.setItmSno((short) 2);
		dnItemReimCharge.setItmQty((short) 1);
		dnItemReimCharge.setItmAmount(jobTruck.getJobTotalReimbursements().setScale(0, RoundingMode.HALF_UP));
		dnItemReimCharge.setItmUnitPrice(jobTruck.getJobTotalReimbursements().setScale(0, RoundingMode.HALF_UP));
		dnItemReimCharge.setItmRef(jobTruck.getJobId());
		dnItemReimCharge.setItmStatus(RecordStatus.ACTIVE.getCode());
		dnItemReimCharge.setItmDtCreate(now);
		dnItemReimCharge.setItmUidCreate("SYS");
		dnItemReimCharge.setItmDtLupd(now);
		dnItemReimCharge.setItmUidLupd("SYS");
		ckCtDebitNoteItemDao.add(dnItemReimCharge);
		CkCtDebitNoteItem itm2 = new CkCtDebitNoteItem(dnItemReimCharge);
		itm2.setTCkCtDebitNote(dnDto);
		itm2.setTMstCurrency(new MstCurrency(dnItemReimCharge.getTMstCurrency()));
		debiteNoteItems.add(itm2);

		// 20250322 OPM - only generate pdf if it's non-opm
		if (!isOpmFinancing)
			dnEntity.setDnLoc(generateDebitNotePdf(dnDto, debiteNoteItems, true));

		dnEntity.setDnName(dnEntity.getDnId());
		ckCtDebitNoteDao.update(dnEntity);

		return dnDto;

	}

	@Override
	public void computeDebitNoteAmount(TCkCtDebitNote dnEntity, CkJobTruck jobTruck, boolean isApplyStampDuty) {

		// calculate the total charge + reimbursement for debit note amount
		BigDecimal totalAmount = ((jobTruck.getJobTotalCharge() == null ? BigDecimal.ZERO
				: jobTruck.getJobTotalCharge())
				.add((jobTruck.getJobTotalReimbursements() == null ? BigDecimal.ZERO
						: jobTruck.getJobTotalReimbursements())))
				.setScale(0, RoundingMode.HALF_UP);
		BigDecimal totalDn = totalAmount;

		dnEntity.setDnStampDuty(BigDecimal.ZERO);
		if (isStampDutyOn) {
			// No vat for trucking related services
			// check if totalAmount is > than the configured limit
			if (isApplyStampDuty) {
				if (BigDecimal.valueOf(amtLimitForStampDuty).compareTo(totalAmount) == -1) {
					dnEntity.setDnStampDuty(BigDecimal.valueOf(stampDutyValue));
					totalAmount = totalAmount.add(BigDecimal.valueOf(stampDutyValue));
				}
			}

		}

		dnEntity.setDnAmt(totalDn.setScale(0, RoundingMode.HALF_UP));
		dnEntity.setDnTotal(totalAmount.setScale(0, RoundingMode.HALF_UP));
	}

	@Override
	public void afterPaid2TO(CkJobTruck jobTruck, Calendar paidDate) throws Exception {

		// paid today default;

		if (null == paidDate) {
			paidDate = Calendar.getInstance();
		}

		Calendar dtIssued = Calendar.getInstance();
		try {
			List<TCkCtDebitNote> dnList = ckCtDebitNoteDao.findByJobId(jobTruck.getJobId());

			// retrieve jobtruck to update due dates
			TCkJobTruck jobTruckE = ckJobTruckDao.find(jobTruck.getJobId());
			boolean isOut = false;
			if (null != dnList && dnList.size() > 0) {
				for (TCkCtDebitNote dn : dnList) {

					//
					Date dueDate = null;
					if (AccountTypes.ACC_TYPE_TO.name()
							.equalsIgnoreCase(dn.getTCoreAccnByDnFrom().getTMstAccnType().getAtypId())) {
						// DN_FROM is TO, DN_TO is GLI
						dueDate = super.getToDueDate(paidDate);
						isOut = true;

					} else {
						// DN_FROM is GLI, DN_TO is CO
						dueDate = this.getCoDueDate(paidDate, jobTruck.getTCoreAccnByJobPartyTo().getAccnId(),
								jobTruck.getTCoreAccnByJobPartyCoFf().getAccnId());
						isOut = false;

					}

					dn.setDnDtIssue(dtIssued.getTime());

					// update invoice NO, if no -D that means no need to regenerate
					if (dn.getDnNo().endsWith(NO_POSTFIX)) {
						String dnNoSeq = seqnoService.getNextSequence(CkSeqNoServiceImpl.SeqNoCode.CT_DN_NO.name());
						dn.setDnNo(dnNoSeq.replaceAll("YYYYMMDD", sdf.format(dtIssued.getTime())));
					}

					// generate invoice;
					List<CkCtDebitNoteItem> itmList = ckCtDebitNoteItemDao.getDebitNoteitems(dn.getDnId());
					String location = this.generateDebitNotePdf(this.entityToDto(dn), itmList, false);

					dn.setDnDtDue(dueDate);
					dn.setDnLoc(location);

					// Update job payment due dates (in/outbound)
					if (isOut) {
						jobTruckE.setJobOutPaymentDtDue(dueDate);
					} else {
						jobTruckE.setJobInPaymentDtDue(dueDate);
					}

					ckJobTruckDao.update(jobTruckE);

					if (StringUtils.isNotBlank(location)) {
						File file = new File(location);
						dn.setDnName(file.getName());
					}

					ckCtDebitNoteDao.update(dn);

					// update tckctpayment refno, attachment
					// Double check if drftInvNo ends with -D
					updateCtPaymentByRef(dn.getDnId(), dn.getDnNo(), location);

					// Update to send to eMaterai for eStamping
					// Nina: if dnEntity.stampDuty is greater than 0 that means there's stamp duty
					// value process the eMeterai.
					if (dn.getDnStampDuty().compareTo(BigDecimal.ZERO) == 1) {
						LOG.info("eStamping " + dn.getDnJobId());
						eStampDocument(dn.getDnId(), dn.getDnName(), dn.getDnLoc(), InvoiceTypes.DEBIT_NOTE);
					}
				}
			}
		} catch (Exception e) {
			Log.error("", e);
			throw e;
		}
	}

	// Move below codes to DAO class?

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtDebitNote getDebitNote(CkJobTruck jobTruck, CoreAccn toAccn, DebitNoteStates state) throws Exception {
		if (jobTruck == null)
			throw new ParameterException("param jobTruck null");
		if (toAccn == null)
			throw new ParameterException("param toAccn null");
		if (state == null)
			throw new ParameterException("param state null");

		String hql = "from TCkCtDebitNote o where o.TCkCtMstDebitNoteState.dnstId=:dnState"
				+ " and o.TCoreAccnByDnTo.accnId=:toAccnId" + " and o.dnStatus=:status" + " and o.dnJobId=:jobId";
		Map<String, Object> params = new HashMap<>();
		params.put("dnState", state.name());
		// recipient of the DN to charge
		params.put("toAccnId", toAccn.getAccnId());
		params.put("jobId", jobTruck.getJobId());
		params.put("status", RecordStatus.ACTIVE.getCode());

		List<TCkCtDebitNote> listDn = ckCtDebitNoteDao.getByQuery(hql, params);
		if (listDn != null && listDn.size() > 0) {
			// expecting only one per from/to accn and state
			TCkCtDebitNote dnE = listDn.get(0);
			Hibernate.initialize(dnE.getTCkCtMstDebitNoteState());
			Hibernate.initialize(dnE.getTCoreAccnByDnFrom());
			Hibernate.initialize(dnE.getTCoreAccnByDnTo());
			CkCtDebitNote dto = new CkCtDebitNote(dnE);
			dto.setTCkCtMstDebitNoteState(new CkCtMstDebitNoteState(dnE.getTCkCtMstDebitNoteState()));
			dto.setTCoreAccnByDnFrom(new CoreAccn(dnE.getTCoreAccnByDnFrom()));
			dto.setTCoreAccnByDnTo(new CoreAccn(dnE.getTCoreAccnByDnTo()));
			return dto;

		}
		return null;
	}

	@Override
	public String generateDebitNotePdf(CkCtDebitNote debitNote, List<CkCtDebitNoteItem> debitNoteItems, boolean isDraft)
			throws Exception {
		if (debitNote == null) {
			throw new ParameterException("param debit note null");
		}
		Map<String, Object> param = new HashMap<>();
		param.put("dn_no", debitNote.getDnNo());
		DateUtil dateUtil = new DateUtil(debitNote.getDnDtIssue());
		param.put("dn_date", dateUtil.toStringFormat(DateFormat.Java.DD_MMMM_YYYY));
		param.put("dn_ref", debitNote.getDnPaymentRef());
		param.put("dn_va_transid", "");
		param.put("dn_remarks", "");
		param.put("recipient_name", debitNote.getTCoreAccnByDnTo().getAccnName());
		param.put("product", "CLICTRUCK");
		String address = "";
		CoreAddress coreAddress = debitNote.getTCoreAccnByDnTo().getAccnAddr();
		if (coreAddress != null) {
			address += Optional.ofNullable(coreAddress.getAddrLn1()).orElse("");
			address += Optional.ofNullable(coreAddress.getAddrLn2()).orElse("");
			address += Optional.ofNullable(coreAddress.getAddrLn3()).orElse("");
		}
		param.put("address", address);
		Map<String, Object> paramInfo = new HashMap<>();
		paramInfo.put("PARAM_INFO", param);
		InvoiceFields invoiceFields = new InvoiceFields();
		BigDecimal subTotalValue = BigDecimal.ZERO;
		for (CkCtDebitNoteItem debitNoteItem : debitNoteItems) {
			InvoiceSubLine invoiceItem = new InvoiceSubLine();
			invoiceItem.setSeqNo(debitNoteItem.getItmSno().toString());
			invoiceItem.setInvNo(debitNote.getDnNo());
			invoiceItem.setCurrency("");
			invoiceItem.setItemDesc(debitNoteItem.getItmItem());
			invoiceItem.setAmount(NumberUtil.currencyFormat("Rp.", debitNoteItem.getItmAmount()));
			subTotalValue = subTotalValue.add(debitNoteItem.getItmAmount());
			invoiceFields.getInvoiceLines().add(invoiceItem);
		}
		if (NumberUtil.toDouble(debitNote.getDnStampDuty()) > 0.0) {
			InvoiceSubLine subTotal = new InvoiceSubLine();
			subTotal.setSeqNo("");
			subTotal.setInvNo(debitNote.getDnNo());
			subTotal.setCurrency("");
			subTotal.setItemDesc("Sub Total");
			subTotal.setAmount(NumberUtil.currencyFormat("Rp.", subTotalValue));
			invoiceFields.getInvoiceLines().add(subTotal);
			InvoiceSubLine stampDuty = new InvoiceSubLine();
			stampDuty.setSeqNo("");
			stampDuty.setInvNo(debitNote.getDnNo());
			stampDuty.setCurrency("");
			stampDuty.setItemDesc("Stamp Duty");
			stampDuty.setAmount(NumberUtil.currencyFormat("Rp.", debitNote.getDnStampDuty()));
			invoiceFields.getInvoiceLines().add(stampDuty);
		}
		InvoiceSubLine total = new InvoiceSubLine();
		total.setSeqNo("");
		total.setInvNo(debitNote.getDnNo());
		total.setCurrency("");
		total.setItemDesc("Total");
		total.setAmount(NumberUtil.currencyFormat("Rp.", debitNote.getDnTotal()));
		invoiceFields.getInvoiceLines().add(total);
		List<InvoiceFields> invoiceLines = Arrays.asList(invoiceFields);
		JRBeanCollectionDataSource collectionDataSource = new JRBeanCollectionDataSource(invoiceLines);
		try {
			String jrxmlBasePath = getSysParam(CtConstant.KEY_JRXML_BASE_PATH);
			String jrxmlDebitNotePath;
			if (isDraft) {
				jrxmlDebitNotePath = getSysParam(CtConstant.KEY_JRXML_DN_PATH);
			} else {
				jrxmlDebitNotePath = getSysParam(CtConstant.KEY_JRXML_DEBIT_NOTE_PATH);
			}
			String basePath = getSysParam(CtConstant.KEY_ATTCH_BASE_LOCATION);
			File subReportFile = ResourceUtils
					.getFile(jrxmlBasePath.concat("docs/").concat("InvoiceClictruckSub.jasper"));
			param.put("invoiceSub", subReportFile);
			param.put("gli_logo", jrxmlBasePath + "docs/ClicLogo.png");
			Map<String, Object> mapParam = new HashMap<>();
			mapParam.put("PARAM_INFO", param);
			String filename = debitNote.getDnNo() + ".pdf";
			byte[] data = JasperDocsUtil.createPDF(jrxmlBasePath, jrxmlDebitNotePath, mapParam, collectionDataSource);
			return FileUtil.saveAttachment(debitNote.getDnJobId(), basePath, filename, data);

		} catch (Exception e) {
			throw new ProcessingException(e);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.clictruck.finacing.service.IDebitNoteService#getByTruckJobIdAndAccn(java.lang.String,
	 *      java.lang.String)
	 *
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public TCkCtDebitNote getByTruckJobIdAndAccn(String truckJobId, String accnId) {
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtDebitNote.class);
			criteria.add(Restrictions.eq("dnJobId", truckJobId));
			criteria.add(Restrictions.eq("TCoreAccnByDnTo.accnId", accnId));
			return ckCtDebitNoteDao.getOne(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	private CkCtDebitNote entityToDto(TCkCtDebitNote tdn) {

		CkCtDebitNote dto = new CkCtDebitNote(tdn);
		dto.setTCoreAccnByDnFrom(new CoreAccn(tdn.getTCoreAccnByDnFrom()));
		dto.setTCoreAccnByDnTo(new CoreAccn(tdn.getTCoreAccnByDnTo()));

		return dto;
	}
}