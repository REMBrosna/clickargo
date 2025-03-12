package com.guudint.clickargo.clictruck.master.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.clictruck.master.dto.PaymentTerms;
import com.guudint.clickargo.clictruck.master.service.MasterService;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.config.model.TCoreSysparam;

public abstract class AbstractPaymentTermsService implements MasterService<PaymentTerms> {

	protected static final String KEY_PAYMENT_TERMS_TO = "CLICTRUCK_TO_PAYMENT_TERMS";
	protected static final String KEY_PAYMENT_TERMS_COFF = "CLICTRUCK_COFF_PAYMENT_TERMS";

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	protected String getSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam == null)
			throw new EntityNotFoundException("sysParam " + key + " not configured");

		return sysParam.getSysVal();

	}

}
