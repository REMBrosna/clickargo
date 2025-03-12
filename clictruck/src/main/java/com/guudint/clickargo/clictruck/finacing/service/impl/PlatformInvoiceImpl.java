package com.guudint.clickargo.clictruck.finacing.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractCharge;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractCharge.ContractChargeTypes;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.dto.InvoiceFields;
import com.guudint.clickargo.clictruck.dto.InvoiceSubLine;
import com.guudint.clickargo.clictruck.finacing.dto.ToInvoiceStates;
import com.guudint.clickargo.clictruck.finacing.service.AbstractPaymentUtil;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService.InvoiceTypes;
import com.guudint.clickargo.clictruck.finacing.service.IPlatformInvoiceService;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstToInvoiceState;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstToInvoiceState;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceItemDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice.PlatformInvoiceStates;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoiceItem;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoiceItem;
import com.guudint.clickargo.clictruck.sage.service.SageTaxService;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.ICkSeqNoService;
import com.guudint.clickargo.common.service.impl.CkSeqNoServiceImpl;
import com.guudint.clickargo.docs.util.JasperDocsUtil;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.payment.enums.PaymentStates;
import com.guudint.clickargo.tax.dao.CkTaxInvoiceDao;
import com.guudint.clickargo.tax.dto.CkTaxInvoice;
import com.guudint.clickargo.tax.model.TCkTaxInvoice;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreAddress;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.master.dto.MstCurrency;
import com.vcc.camelone.master.model.TMstCurrency;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Platform Invoice service implementation.
 */
@Service
public class PlatformInvoiceImpl extends AbstractPaymentUtil implements IPlatformInvoiceService {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(PlatformInvoiceImpl.class);

	// @Autowired
	// private GenericDao<TCkCtPlatformInvoice, String> ckCtPlatformInvDao;

	@Autowired
	private CkCtPlatformInvoiceDao ckCtPlatformInvDao;

	@Autowired
	private CkCtPlatformInvoiceItemDao ckCtPlatformInvItemDao;

	@Autowired
	private ICkSeqNoService seqnoService;

	@Autowired
	private SageTaxService sageTaxService;

	@Autowired
	private CkTaxInvoiceDao ckTaxInvoiceDao;

	@Override
	public CkCtPlatformInvoice createPlatFormInvoice(CkJobTruck jobTruck, Integer addDate,
			CkCtContractCharge contractCharge, CoreAccn pfInvFrom, CoreAccn pfInvTo) throws Exception {

		if (jobTruck == null)
			throw new ParameterException("param jobTruck null");
		if (pfInvFrom == null)
			throw new ParameterException("param pfInvFrom null");
		if (pfInvTo == null)
			throw new ParameterException("param pfInvTo null");
		if (contractCharge == null)
			throw new ParameterException("param pfInvTo null");

		Calendar calendar = Calendar.getInstance();
		TCkCtPlatformInvoice pfInvE = new TCkCtPlatformInvoice();
		pfInvE.setInvId(CkUtil.generateId(CkCtPlatformInvoice.PREFIX_ID));

		// 20230904 Due to the changes in workflow in billing approval, the date is
		// captured from when the billing is approved (acknowledged) by CO
		// Calendar coApprovedCal = Calendar.getInstance();
		Calendar coApprovedCal = Calendar.getInstance();
		// if there is bill acknowledged date, otherwise it will be set to current date
		if (jobTruck.getTCkJob().getTCkRecordDate().getRcdDtBillAcknowledged() != null) {
			coApprovedCal.setTime(jobTruck.getTCkJob().getTCkRecordDate().getRcdDtBillAcknowledged());
		}

		// set the date for invoice no, before it is calculated with payment terms/7
		// days payment
//		Date coAppDate = coApprovedCal.getTime();

		// initial invoice date
		Calendar issueDateCal = Calendar.getInstance();
		issueDateCal.setTime(new Date());
		pfInvE.setInvDtIssue(issueDateCal.getTime());

		issueDateCal.setTime(super.getInitIssueDate());

		TCkCtMstToInvoiceState invState = new TCkCtMstToInvoiceState(ToInvoiceStates.NEW.name(),
				ToInvoiceStates.NEW.name());
		pfInvE.setTCkCtMstToInvoiceState(invState);
		pfInvE.setTCoreAccnByInvTo(pfInvTo.toEntity(new TCoreAccn()));
		pfInvE.setTCoreAccnByInvFrom(pfInvFrom.toEntity(new TCoreAccn()));
		pfInvE.setInvJobId(jobTruck.getJobId());

		pfInvE.setInvUidCreate("SYS");
		pfInvE.setInvUidLupd("SYS");
		pfInvE.setInvDtCreate(calendar.getTime());
		pfInvE.setInvStatus(RecordStatus.ACTIVE.getCode());

		// set 30 days due date
		if (addDate != null) {
			coApprovedCal.add(Calendar.DAY_OF_YEAR, addDate);
			pfInvE.setInvDtDue(coApprovedCal.getTime());
		} else {
			int dueDate = Integer.valueOf(getSysParam(IPaymentService.KEY_CLICTRUCK_DEFAULT_PAYTERMS_TO));
			coApprovedCal.add(Calendar.DAY_OF_YEAR, dueDate);
			pfInvE.setInvDtDue(coApprovedCal.getTime());
		}

//		String invNoSeq = seqnoService.getNextSequence(CkSeqNoServiceImpl.SeqNoCode.CT_PF_INV_NO.name());
//
		String invNo = null;
//		if (StringUtils.isBlank(invNoSeq)) {
		// if no configuration found just set to systimemillis
		invNo = CkUtil.generateId("PFINV");
//		} else {
//			// set the invoice no, based on the co approval date, not the current date gli
//			// approved
//			invNo = invNoSeq.replaceAll("YYYYMMDD", sdf.format(coAppDate));
//		}
		pfInvE.setInvNo(invNo + NO_POSTFIX);

		this.compuateInvoiceAmount(jobTruck, pfInvE, contractCharge);

		// set Sage Tax NO
//		pfInvE.setInvSageTaxNo(sageTaxService.getNextSageTaxSequence());

		ckCtPlatformInvDao.add(pfInvE);

		// For platform Invoice it's always one?
		Date now = new Date();
		TCkCtPlatformInvoiceItem pfInvItem = new TCkCtPlatformInvoiceItem();
		pfInvItem.setItmId(CkUtil.generateId(CkCtPlatformInvoice.PREFIX_ID));
		pfInvItem.setTCkCtPlatformInvoice(pfInvE);
		TMstCurrency curr = new TMstCurrency();
		curr.setCcyCode(defCurrency);
		pfInvItem.setTMstCurrency(curr);
		pfInvItem.setItmSno((short) 1);
		pfInvItem.setItmQty((short) 1);
		pfInvItem.setItmItem(String.format(IPlatformInvoiceService.PF_ITEM_DESC, jobTruck.getJobId()));
		pfInvItem.setItmUnitPrice(pfInvE.getInvAmt());
		pfInvItem.setItmAmount(pfInvE.getInvAmt());
		pfInvItem.setItmRef(jobTruck.getJobId());
		pfInvItem.setItmStatus(RecordStatus.ACTIVE.getCode());
		pfInvItem.setItmDtCreate(now);
		pfInvItem.setItmUidCreate("SYS");
		pfInvItem.setItmDtLupd(now);
		pfInvItem.setItmUidLupd("SYS");
		ckCtPlatformInvItemDao.add(pfInvItem);

		CkCtPlatformInvoice pfInvDto = new CkCtPlatformInvoice(pfInvE);
		pfInvDto.setTCoreAccnByInvFrom(pfInvFrom);
		pfInvDto.setTCoreAccnByInvTo(pfInvTo);

		List<CkCtPlatformInvoiceItem> itemsList = new ArrayList<>();
		CkCtPlatformInvoiceItem pfInvItemDto = new CkCtPlatformInvoiceItem(pfInvItem);
		pfInvItemDto.setTMstCurrency(new MstCurrency(pfInvItem.getTMstCurrency()));
		pfInvItemDto.setTCkCtPlatformInvoice(pfInvDto);
		itemsList.add(pfInvItemDto);

		pfInvE.setInvLoc(generatePlatformInvoicePdf(pfInvDto, itemsList, true));
		pfInvE.setInvName(pfInvE.getInvId());

		ckCtPlatformInvDao.update(pfInvE);

		// Moved to afterPaid2To
//		TCkTaxInvoice tCkTaxInvoice = new TCkTaxInvoice();
//		tCkTaxInvoice.setTiId(CkUtil.generateId(CkTaxInvoice.PREFIX_ID));
//		tCkTaxInvoice.setTCoreAccn(pfInvE.getTCoreAccnByInvTo());
//		tCkTaxInvoice.setTiAmt(pfInvE.getInvAmt());
//		tCkTaxInvoice.setTiDtCreate(now);
//		tCkTaxInvoice.setTiDtLupd(now);
//		tCkTaxInvoice.setTiInvDtIssue(pfInvE.getInvDtIssue());
//		tCkTaxInvoice.setTiInvNo(pfInvE.getInvNo());
//		tCkTaxInvoice.setTiJobNo(pfInvE.getInvJobId());
//		tCkTaxInvoice.setTiNo(pfInvE.getInvSageTaxNo());
//		tCkTaxInvoice.setTiService(ServiceTypes.CLICTRUCK.getId());
//		tCkTaxInvoice.setTiStatus('N');
//		tCkTaxInvoice.setTiDoc("");
//		tCkTaxInvoice.setTiUidCreate("SYS");
//		tCkTaxInvoice.setTiUidLupd("SYS");
//		ckTaxInvoiceDao.add(tCkTaxInvoice);
		return pfInvDto;
	}

	@Override
	public TCkCtPlatformInvoice computePlatformFee4COff(CkJobTruck jobTruck) throws Exception {

		CoreAccn toAccn = jobTruck.getTCoreAccnByJobPartyTo();
		CoreAccn coFfAccn = jobTruck.getTCoreAccnByJobPartyCoFf();

		// Retrieve the contract for platform fee charge of the corresponding accounts
		CkCtContract coffToContract = contractService.getContractByAccounts(toAccn.getAccnId(), coFfAccn.getAccnId());
		if (coffToContract == null)
			throw new ProcessingException(
					"no contract found for " + toAccn.getAccnId() + " and " + coFfAccn.getAccnId());

		// create the platform invoice per trip, create for TO and CO/FF

		TCkCtPlatformInvoice coInv = new TCkCtPlatformInvoice();
		this.compuateInvoiceAmount(jobTruck, coInv, coffToContract.getTCkCtContractChargeByConChargeCoFf());

		return coInv;
	}

	@Override
	public TCkCtPlatformInvoice computePlatformFee4TO(CkJobTruck jobTruck) throws Exception {

		CoreAccn toAccn = jobTruck.getTCoreAccnByJobPartyTo();
		CoreAccn coFfAccn = jobTruck.getTCoreAccnByJobPartyCoFf();

		// Retrieve the contract for platform fee charge of the corresponding accounts
		CkCtContract coffToContract = contractService.getContractByAccounts(toAccn.getAccnId(), coFfAccn.getAccnId());
		if (coffToContract == null)
			throw new ProcessingException(
					"no contract found for " + toAccn.getAccnId() + " and " + coFfAccn.getAccnId());

		TCkCtPlatformInvoice toInv = new TCkCtPlatformInvoice();
		this.compuateInvoiceAmount(jobTruck, toInv, coffToContract.getTCkCtContractChargeByConChargeTo());

		return toInv;
	}

	@Override
	public void afterPaid2TO(CkJobTruck jobTruck, Calendar paidDate) throws Exception {

		// paid today default;
		if (null == paidDate) {
			paidDate = Calendar.getInstance();
		}

		Calendar dtIssued = Calendar.getInstance();
		try {
			List<TCkCtPlatformInvoice> invList = ckCtPlatformInvDao.findByJobId(jobTruck.getJobId());

			// retrieve jobtruck to update due dates
			TCkJobTruck jobTruckE = ckJobTruckDao.find(jobTruck.getJobId());
			boolean isOut = false;
			if (null != invList && invList.size() > 0) {
				for (TCkCtPlatformInvoice inv : invList) {

					//
					Date dueDate = null;
					if (AccountTypes.ACC_TYPE_TO.name()
							.equalsIgnoreCase(inv.getTCoreAccnByInvTo().getTMstAccnType().getAtypId())) {
						// TO
						dueDate = this.getToDueDate(paidDate);
						isOut = true;

					} else {
						dueDate = this.getCoDueDate(paidDate, jobTruck.getTCoreAccnByJobPartyTo().getAccnId(),
								jobTruck.getTCoreAccnByJobPartyCoFf().getAccnId());
						isOut = false;

					}

					inv.setInvDtIssue(dtIssued.getTime());

					// update invoice NO
					if (inv.getInvNo().endsWith(NO_POSTFIX)) {
						// Get the invoice no. first before changing to update the T_CK_CT_PAYMENT
						// records
						String invNoSeq = seqnoService
								.getNextSequence(CkSeqNoServiceImpl.SeqNoCode.CT_PF_INV_NO.name());

						inv.setInvNo(invNoSeq.replaceAll("YYYYMMDD", sdf.format(dtIssued.getTime())));
						inv.setInvSageTaxNo(sageTaxService.getNextSageTaxSequence());

						// Moved the insertion of tax invoice here:
						TCkTaxInvoice tCkTaxInvoice = new TCkTaxInvoice();
						tCkTaxInvoice.setTiId(CkUtil.generateId(CkTaxInvoice.PREFIX_ID));
						tCkTaxInvoice.setTCoreAccn(inv.getTCoreAccnByInvTo());
						tCkTaxInvoice.setTiAmt(inv.getInvAmt());
						tCkTaxInvoice.setTiDtCreate(dtIssued.getTime());
						tCkTaxInvoice.setTiDtLupd(dtIssued.getTime());
						tCkTaxInvoice.setTiInvDtIssue(inv.getInvDtIssue());
						tCkTaxInvoice.setTiInvNo(inv.getInvNo());
						tCkTaxInvoice.setTiJobNo(inv.getInvJobId());
						tCkTaxInvoice.setTiNo(inv.getInvSageTaxNo());
						tCkTaxInvoice.setTiService(ServiceTypes.CLICTRUCK.getId());
						tCkTaxInvoice.setTiStatus('N');
						tCkTaxInvoice.setTiDoc("");
						tCkTaxInvoice.setTiUidCreate("SYS");
						tCkTaxInvoice.setTiUidLupd("SYS");
						ckTaxInvoiceDao.add(tCkTaxInvoice);

					}

					// generate invoice;
					List<CkCtPlatformInvoiceItem> itmList = ckCtPlatformInvItemDao.getPlatformFeeItems(inv.getInvId());
					String location = this.generatePlatformInvoicePdf(this.entityToDto(inv), itmList, false);

					inv.setInvDtDue(dueDate);
					inv.setInvLoc(location);

					// Update job payment due dates (in/outbound)
					if (isOut) {
						jobTruckE.setJobOutPaymentDtDue(dueDate);
					} else {
						jobTruckE.setJobInPaymentDtDue(dueDate);
					}

					ckJobTruckDao.update(jobTruckE);

					if (StringUtils.isNotBlank(location)) {
						File file = new File(location);
						inv.setInvName(file.getName());
					}

					ckCtPlatformInvDao.update(inv);

					updateCtPaymentByRef(inv.getInvId(), inv.getInvNo(), location);

					// Update to send to eMaterai for eStamping
					// Nina: if pfInvE.stampDuty is greater than 0 that means there's stamp duty
					// value process the eMeterai.
					if (inv.getInvStampDuty().compareTo(BigDecimal.ZERO) == 1) {
						eStampDocument(inv.getInvId(), inv.getInvName(), inv.getInvLoc(), InvoiceTypes.PLATFORM_FEE);
					}

				}
			}
		} catch (Exception e) {
			Log.error("", e);
			throw e;
		}
	}

	@Override
	public String generatePlatformInvoicePdf(CkCtPlatformInvoice pfInv, List<CkCtPlatformInvoiceItem> pfInvItems,
			boolean isDraft) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("inv_no", pfInv.getInvNo());
		DateUtil dateUtil = new DateUtil(pfInv.getInvDtIssue());
		param.put("inv_date", dateUtil.toStringFormat(DateFormat.Java.DD_MMMM_YYYY));
		param.put("inv_ref", pfInv.getInvJobId());
		param.put("inv_va_transid", "");
		param.put("inv_remarks", Optional.ofNullable(pfInv.getInvInvocieeRemarks()).orElse(""));
		param.put("recipient_name", pfInv.getTCoreAccnByInvTo().getAccnName());
		param.put("product", "CLICTRUCK");
		String address = "";
		CoreAddress coreAddress = pfInv.getTCoreAccnByInvTo().getAccnAddr();
		if (coreAddress != null) {
			address += Optional.ofNullable(coreAddress.getAddrLn1()).orElse("");
			address += Optional.ofNullable(coreAddress.getAddrLn2()).orElse("");
			address += Optional.ofNullable(coreAddress.getAddrLn3()).orElse("");
		}
		param.put("address", address);
		InvoiceFields invoiceFields = new InvoiceFields();
		BigDecimal subTotalValue = BigDecimal.ZERO;
		for (CkCtPlatformInvoiceItem platformInvoiceItem : pfInvItems) {
			InvoiceSubLine invoiceItem = new InvoiceSubLine();
			invoiceItem.setSeqNo(platformInvoiceItem.getItmSno().toString());
			invoiceItem.setInvNo(pfInv.getInvNo());
			invoiceItem.setCurrency("");
			invoiceItem.setItemDesc(platformInvoiceItem.getItmItem());
			invoiceItem.setAmount(NumberUtil.currencyFormat("Rp.", platformInvoiceItem.getItmAmount()));
			subTotalValue = subTotalValue.add(platformInvoiceItem.getItmAmount());
			invoiceFields.getInvoiceLines().add(invoiceItem);
		}
		if (NumberUtil.toDouble(pfInv.getInvStampDuty()) > 0.0) {
			InvoiceSubLine subTotal = new InvoiceSubLine();
			subTotal.setSeqNo("");
			subTotal.setInvNo(pfInv.getInvNo());
			subTotal.setCurrency("");
			subTotal.setItemDesc("Sub Total");
			subTotal.setAmount(NumberUtil.currencyFormat("Rp.", subTotalValue));
			invoiceFields.getInvoiceLines().add(subTotal);
		}
		InvoiceSubLine vat = new InvoiceSubLine();
		vat.setSeqNo("");
		vat.setInvNo(pfInv.getInvNo());
		vat.setCurrency("");
		vat.setItemDesc("VAT");
		vat.setAmount(NumberUtil.currencyFormat("Rp.", pfInv.getInvVat()));
		invoiceFields.getInvoiceLines().add(vat);
		if (NumberUtil.toDouble(pfInv.getInvStampDuty()) > 0.0) {
			InvoiceSubLine stampDuty = new InvoiceSubLine();
			stampDuty.setSeqNo("");
			stampDuty.setInvNo(pfInv.getInvNo());
			stampDuty.setCurrency("");
			stampDuty.setItemDesc("Stamp Duty");
			stampDuty.setAmount(NumberUtil.currencyFormat("Rp.", pfInv.getInvStampDuty()));
			invoiceFields.getInvoiceLines().add(stampDuty);
		}
		InvoiceSubLine total = new InvoiceSubLine();
		total.setSeqNo("");
		total.setInvNo(pfInv.getInvNo());
		total.setCurrency("");
		total.setItemDesc("Total");
		total.setAmount(NumberUtil.currencyFormat("Rp.", pfInv.getInvTotal()));
		invoiceFields.getInvoiceLines().add(total);
		List<InvoiceFields> invoiceLines = Arrays.asList(invoiceFields);
		JRBeanCollectionDataSource collectionDataSource = new JRBeanCollectionDataSource(invoiceLines);
		try {
			String jrxmlBasePath = coreSysparamDao.find(CtConstant.KEY_JRXML_BASE_PATH).getSysVal();
			String jrxmlInvoicePath;
			if (isDraft) {
				jrxmlInvoicePath = coreSysparamDao.find(CtConstant.KEY_JRXML_DRAFT_INVOICE_PATH).getSysVal();
			} else {
				jrxmlInvoicePath = coreSysparamDao.find(CtConstant.KEY_JRXML_INVOICE_PATH).getSysVal();
			}
			String basePath = coreSysparamDao.find(CtConstant.KEY_ATTCH_BASE_LOCATION).getSysVal();
			File subReportFile = ResourceUtils
					.getFile(jrxmlBasePath.concat("docs/").concat("InvoiceClictruckSub.jasper"));
			param.put("invoiceSub", subReportFile);
			param.put("gli_logo", jrxmlBasePath + "docs/ClicLogo.png");
			Map<String, Object> mapParam = new HashMap<>();
			mapParam.put("PARAM_INFO", param);
			String filename = pfInv.getInvNo() + ".pdf";
			byte[] data = JasperDocsUtil.createPDF(jrxmlBasePath, jrxmlInvoicePath, mapParam, collectionDataSource);
			return FileUtil.saveAttachment(pfInv.getInvJobId(), basePath, filename, data);

		} catch (Exception e) {
			throw new ProcessingException(e);
		}
	}

	private CkCtPlatformInvoice entityToDto(TCkCtPlatformInvoice pfInvE) {

		CkCtPlatformInvoice dto = new CkCtPlatformInvoice(pfInvE);
		dto.setTCoreAccnByInvFrom(new CoreAccn(pfInvE.getTCoreAccnByInvFrom()));
		dto.setTCoreAccnByInvTo(new CoreAccn(pfInvE.getTCoreAccnByInvTo()));

		return dto;
	}

	private void compuateInvoiceAmount(CkJobTruck jobTruck, TCkCtPlatformInvoice pfInvE,
			CkCtContractCharge contractCharge) {
		BigDecimal grandTotalInvAmt = BigDecimal.ZERO;
		BigDecimal totalPfInvAmnt = BigDecimal.ZERO;
		BigDecimal totalAmount = ((jobTruck.getJobTotalCharge() == null ? BigDecimal.ZERO
				: jobTruck.getJobTotalCharge())
				.add(jobTruck.getJobTotalReimbursements() == null ? BigDecimal.ZERO
						: jobTruck.getJobTotalReimbursements()))
				.setScale(0, RoundingMode.HALF_UP);

		// set the amount of platform fee based on the contract charge
		if (contractCharge.getConcPltfeeType() != null
				&& contractCharge.getConcPltfeeType() == ContractChargeTypes.PERCENTAGE.getCode()) {
			// for percentage, the platform fee is % of the total job charge + job
			// reimbursements
			totalPfInvAmnt = totalAmount.multiply(contractCharge.getConcPltfeeAmt().divide(BigDecimal.valueOf(100)));
		} else {
			totalPfInvAmnt = contractCharge.getConcPltfeeAmt() == null ? BigDecimal.ZERO
					: contractCharge.getConcPltfeeAmt();
		}

		totalPfInvAmnt = totalPfInvAmnt.setScale(0, RoundingMode.HALF_UP);
		pfInvE.setInvAmt(totalPfInvAmnt);

		// compute the vat % of the invoice amount
		BigDecimal vatCharge = pfInvE.getInvAmt().multiply(BigDecimal.valueOf(vatPercent)).setScale(0,
				RoundingMode.DOWN);
		pfInvE.setInvVat(vatCharge);
		grandTotalInvAmt = grandTotalInvAmt.add(totalPfInvAmnt.add(vatCharge));

		pfInvE.setInvStampDuty(BigDecimal.ZERO);
		if (isStampDutyOn) {
			if (BigDecimal.valueOf(amtLimitForStampDuty).compareTo(totalPfInvAmnt) == -1) {
				pfInvE.setInvStampDuty(BigDecimal.valueOf(stampDutyValue));
				grandTotalInvAmt = grandTotalInvAmt.add(BigDecimal.valueOf(stampDutyValue));
			}
		}

		pfInvE.setInvTotal(grandTotalInvAmt.setScale(0, RoundingMode.HALF_UP));
	}

	//// Move below function to Dao class?
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtPlatformInvoice getPlatformInvoice(CkJobTruck jobTruck, CoreAccn pfInvTo,
			PlatformInvoiceStates pfInvState) throws Exception {
		if (jobTruck == null)
			throw new ParameterException("param jobTruck null");
		if (pfInvTo == null)
			throw new ParameterException("param pfInvTo null");
		if (pfInvState == null)
			throw new ParameterException("param pfInvState null");

		String hql = "from TCkCtPlatformInvoice o where o.TCoreAccnByInvTo.accnId=:toAccnId"
				+ " and o.TCkCtMstToInvoiceState.instId=:state and o.invStatus=:status" + " and o.invJobId=:jobId";
		Map<String, Object> params = new HashMap<>();
		params.put("toAccnId", pfInvTo.getAccnId());
		params.put("state", pfInvState.name());
		params.put("jobId", jobTruck.getJobId());
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCkCtPlatformInvoice> pfInvList = ckCtPlatformInvDao.getByQuery(hql, params);
		if (pfInvList != null && pfInvList.size() > 0) {
			// expecting only one new platform invoice
			TCkCtPlatformInvoice pfInvE = pfInvList.get(0);
			Hibernate.initialize(pfInvE.getTCkCtMstToInvoiceState());
			Hibernate.initialize(pfInvE.getTCoreAccnByInvFrom());
			Hibernate.initialize(pfInvE.getTCoreAccnByInvTo());

			CkCtPlatformInvoice dto = new CkCtPlatformInvoice(pfInvE);
			dto.setTCkCtMstToInvoiceState(new CkCtMstToInvoiceState(pfInvE.getTCkCtMstToInvoiceState()));
			dto.setTCoreAccnByInvFrom(new CoreAccn(pfInvE.getTCoreAccnByInvFrom()));
			dto.setTCoreAccnByInvTo(new CoreAccn(pfInvE.getTCoreAccnByInvTo()));
			return dto;

		}

		return null;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.clictruck.finacing.service.IPlatformInvoiceService#getDueInvoicesToDate(java.util.Date,
	 *      java.lang.String[])
	 *
	 */
	public List<CkCtPlatformInvoice> getDueInvoicesToDate(Date dueDate, String... accnTypes) {
		String sql = "FROM TCkCtPlatformInvoice o WHERE o.TCoreAccnByInvTo.accnStatus = :accnStatus "
				+ " AND o.TCkCtMstToInvoiceState.instId = :state AND o.invDtDue <= :dueDate AND o.TCoreAccnByInvTo.TMstAccnType.atypId IN :accnTypes ";

		Map<String, Object> param = new HashMap<>();
		param.put("dueDate", dueDate);
		param.put("accnTypes", Arrays.asList(accnTypes));
		param.put("accnStatus", RecordStatus.ACTIVE.getCode());
		param.put("state", PaymentStates.NEW.getCode());

		try {
			List<CkCtPlatformInvoice> overDuePfInvsList = new ArrayList<CkCtPlatformInvoice>();
			List<TCkCtPlatformInvoice> tckCtPlatFormInvoices = ckCtPlatformInvDao.getByQuery(sql, param);
			for (TCkCtPlatformInvoice tckCtPlatFormInvoice : tckCtPlatFormInvoices) {
				CkCtPlatformInvoice ckCtPlatformInvoice = new CkCtPlatformInvoice(tckCtPlatFormInvoice);
				ckCtPlatformInvoice.setTCoreAccnByInvTo(new CoreAccn(tckCtPlatFormInvoice.getTCoreAccnByInvTo()));
				overDuePfInvsList.add(ckCtPlatformInvoice);
			}
			return overDuePfInvsList;
		} catch (Exception e) {
			LOG.error("getAccountSuspendedToday", e);
		}
		return null;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.clictruck.finacing.service.IPlatformInvoiceService#getExpiredInvByAccn(java.util.Date,
	 *      java.lang.String)
	 *
	 */
	public List<CkCtPlatformInvoice> getExpiredInvByAccn(Date suspendDate, String accnId) {
		String sql = "FROM TCkCtPlatformInvoice o WHERE o.invStatus = :invStatus "
				+ " AND o.TCkCtMstToInvoiceState.instId=:state AND o.invDtDue <= :suspendDate AND o.TCoreAccnByInvTo.accnId IN :accnId ";

		Map<String, Object> param = new HashMap<>();
		param.put("suspendDate", suspendDate);
		// invStatus remains unchanged so search for active is ok
		param.put("invStatus", RecordStatus.ACTIVE.getCode());
		param.put("state", PaymentStates.NEW.getCode());
		param.put("accnId", accnId);

		try {
			List<CkCtPlatformInvoice> ckCtPlatFormInvoices = new ArrayList<CkCtPlatformInvoice>();
			List<TCkCtPlatformInvoice> tckCtPlatFormInvoices = ckCtPlatformInvDao.getByQuery(sql, param);
			for (TCkCtPlatformInvoice tckCtPlatFormInvoice : tckCtPlatFormInvoices) {
				CkCtPlatformInvoice ckCtPlatformInvoice = new CkCtPlatformInvoice(tckCtPlatFormInvoice);
				ckCtPlatformInvoice.setTCoreAccnByInvTo(new CoreAccn(tckCtPlatFormInvoice.getTCoreAccnByInvTo()));
				ckCtPlatFormInvoices.add(ckCtPlatformInvoice);
			}
			return ckCtPlatFormInvoices;
		} catch (Exception e) {
			LOG.error("getExpiredInvByAccn", e);
		}
		return null;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.clictruck.finacing.service.IPlatformInvoiceService#getByTruckJobIdAndAccn(java.lang.String,
	 *      java.lang.String)
	 *
	 */
	public TCkCtPlatformInvoice getByTruckJobIdAndAccn(String truckJobId, String accnId) {
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtPlatformInvoice.class);
			criteria.add(Restrictions.eq("invJobId", truckJobId));
			criteria.add(Restrictions.eq("TCoreAccnByInvTo.accnId", accnId));
			return ckCtPlatformInvDao.getOne(criteria);
		} catch (Exception e) {
			return null;
		}
	}
}
