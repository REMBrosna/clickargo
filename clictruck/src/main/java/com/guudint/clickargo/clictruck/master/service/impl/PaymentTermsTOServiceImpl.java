package com.guudint.clickargo.clictruck.master.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.guudint.clickargo.clictruck.master.dto.PaymentTerms;

public class PaymentTermsTOServiceImpl extends AbstractPaymentTermsService {

	private static final Logger LOG = Logger.getLogger(PaymentTermsTOServiceImpl.class.getName());

	@Override
	public List<PaymentTerms> listAll() {
		List<PaymentTerms> payTerms = new ArrayList<>();
		try {

			String strList = getSysParam(KEY_PAYMENT_TERMS_TO);
			if (StringUtils.isNotBlank(strList)) {
				List<String> arr = Arrays.asList(strList.split(","));
				arr.stream().forEach(e -> {
					PaymentTerms terms = new PaymentTerms(e, e);
					payTerms.add(terms);
				});
			}

		} catch (Exception e) {
			LOG.error("listAll", e);
		}

		return payTerms;

	}

	@Override
	public List<PaymentTerms> listByStatus(Character status) {
		// TODO Auto-generated method stub
		return null;
	}
}
