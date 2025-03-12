package com.guudint.clickargo.clictruck.finacing.service;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.impl.CkCoreAccnService;
import com.guudint.clickargo.master.dao.CoreAccnDao;
import com.guudint.clickargo.payment.dao.CkPaymentAuditDao;
import com.guudint.clickargo.payment.dao.CkPaymentTxnDao;
import com.guudint.clickargo.payment.dto.PaymentCallbackRequest;
import com.guudint.clickargo.payment.model.TCkPaymentAudit;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.config.model.TCoreSysparam;

public abstract class AbstractPaymentService implements IPaymentService {

	@Autowired
	@Qualifier("ckPaymentAuditDao")
	private CkPaymentAuditDao ckPaymentAuditDao;

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	@Autowired
	protected CkCoreAccnService ckCoreAccnService;

	@Autowired
	protected CoreAccnDao coreAccnDao;

	@Autowired
	@Qualifier("ckPaymentTxnDao")
	protected CkPaymentTxnDao ckPaymentTxnDao;

	protected static ObjectMapper mapper = new ObjectMapper();

	@PostConstruct
	public void init() {

		if (mapper == null) {
			mapper = new ObjectMapper();
		}

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	protected TCkPaymentAudit updatePaymentAudit(String txnId, PaymentCallbackRequest request) throws Exception {
		if (request == null)
			throw new ParameterException("param request null");

		List<TCkPaymentAudit> paymentAuditList = ckPaymentAuditDao.getByReferenceAndStatus(txnId,
				RecordStatus.ACTIVE.getCode());
		if (paymentAuditList.isEmpty())
			throw new ProcessingException("audit not found for " + request.getPayRefNo());

		// Expected only one?
		TCkPaymentAudit payAudit = paymentAuditList.get(0);
		payAudit.setPyaCb(request.toJson());
		payAudit.setPyaDtLupd(new Date());
		payAudit.setPyaUidLupd(Constant.ACCN_CREATE_SYS_USER);
		ckPaymentAuditDao.update(payAudit);

		return payAudit;

	}

	protected String getConfigFromSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam == null)
			throw new EntityNotFoundException("sysparam config " + key + " not found");

		return sysParam.getSysVal();
	}
}
