package com.guudint.clickargo.clictruck.finacing.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.service.CkCtContractService;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService.InvoiceTypes;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPayment;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtPaymentService;
import com.guudint.clickargo.estamp.dto.StampRequest;
import com.guudint.clickargo.external.services.IStampGateway;
import com.guudint.clickargo.master.enums.Currencies;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.config.model.TCoreSysparam;

public abstract class AbstractPaymentUtil {

	// Static Attributes
	///////////////////
	private static Logger log = Logger.getLogger(AbstractPaymentUtil.class);

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	@Autowired
	private CkCtPaymentService ckctPaymentService;

	@Autowired
	@Qualifier("ckCtPaymentDao")
	private GenericDao<TCkCtPayment, String> ckCtPaymentDao;

	protected double vatPercent = 0.0;
	protected double amtLimitForStampDuty = 0.0;
	protected double stampDutyValue = 0.0;
	protected String defCurrency = Currencies.IDR.getCode();
	protected int toPaymentTerm = 5;
	protected boolean isStampDutyOn = false;

	private static Date initIssueDate = null;
	protected static String NO_POSTFIX = "-D";

	protected SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");

	@Autowired
	protected IStampGateway eStampGateway;

	@Autowired
	protected CkCtContractService contractService;
	
	 @Autowired
	 protected GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@PostConstruct
	public void initSysParam() throws NumberFormatException, Exception {
		vatPercent = Double.valueOf(getSysParam(IPaymentService.KEY_CLICTRUCK_VAT_PERCENTAGE));
		amtLimitForStampDuty = Double.valueOf(getSysParam(IPaymentService.KEY_CLICTRUCK_STAMP_DUTY_LIMIT));
		stampDutyValue = Double.valueOf(getSysParam(IPaymentService.KEY_CLICTRUCK_STAMP_DUTY_VALUE));
		defCurrency = getSysParam(IPaymentService.KEY_CLICTRUCK_DEFAULT_CURRENCY);
		isStampDutyOn = Boolean.valueOf(getSysParam(IPaymentService.KEY_CLICTRUCK_STAMP_DUTY_ON));
	}

	protected String getSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam != null) {
			return sysParam.getSysVal();
		}

		throw new EntityNotFoundException("sys param config " + key + " not set");
	}

	protected Date getInitIssueDate() throws ParseException {

		if (null == initIssueDate) {
			initIssueDate = sdf.parse("21000101"); // first date of 2100
		}

		return initIssueDate;
	}

	protected Date getCoDueDate(Calendar coApprovedCal, String toAccnId, String coAccnId)
			throws NumberFormatException, Exception {

		Calendar dueDateCal = Calendar.getInstance();
		dueDateCal.setTime(coApprovedCal.getTime());
		CkCtContract coffToContract = contractService.getContractByAccounts(toAccnId, coAccnId);
		dueDateCal.add(Calendar.DAY_OF_YEAR, coffToContract.getConPaytermCoFf());

		return dueDateCal.getTime();
	}

	protected Date getToDueDate(Calendar coApprovedCal) throws NumberFormatException, Exception {

		Calendar dueDateCal = Calendar.getInstance();
		dueDateCal.setTime(coApprovedCal.getTime());
		int dueDate = Integer.valueOf(getSysParam(IPaymentService.KEY_CLICTRUCK_DEFAULT_PAYTERMS_TO));
		dueDateCal.add(Calendar.DAY_OF_YEAR, dueDate);

		return dueDateCal.getTime();
	}

	protected void eStampDocument(String invId, String filename, String fileLoc, InvoiceTypes invType) {
		log.info("Stamping document: " + filename);
		try {

			StampRequest stampRequest = new StampRequest();
			stampRequest.setFilename(filename);
			stampRequest.setInvId(invId);
			stampRequest.setInvType(invType.name());
			stampRequest.setPathFile(fileLoc);
			stampRequest.setDoc(IOUtils.toByteArray(Files.newInputStream(Paths.get(fileLoc))));
			stampRequest.setServiceId(ServiceTypes.CLICTRUCK.name());
			eStampGateway.stampDocument(stampRequest);

		} catch (Exception ex) {
			log.error("eStampDocument", ex);
		}

	}

	protected void updateCtPaymentByRef(String invId, String newInvNo, String attachLocation) throws Exception {
		if (StringUtils.isBlank(invId))
			throw new ParameterException("param invId is null or empty");

		TCkCtPayment ctPayment = ckctPaymentService.getByRef(invId);
		if (ctPayment != null) {
			ctPayment.setCtpRef(newInvNo);
			ctPayment.setCtpAttach(attachLocation);
			ctPayment.setCtpDtLupd(new Date());
			ckCtPaymentDao.update(ctPayment);
		}
	}
}