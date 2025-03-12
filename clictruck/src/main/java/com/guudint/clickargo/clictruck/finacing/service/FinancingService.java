package com.guudint.clickargo.clictruck.finacing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstOpmRate;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.master.dto.MstBank;
import com.vcc.camelone.master.model.TMstBank;

@Service
public class FinancingService {

	@Autowired
	@Qualifier("ckCtMstOpmRateDao")
	private GenericDao<TCkCtMstOpmRate, String> ckCtMstOpmRateDao;

	@Autowired
	@Qualifier("mstBankDao")
	private GenericDao<TMstBank, String> mstBankDao;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<FinancerOptions> getFinancers() throws Exception {

		List<FinancerOptions> options = new ArrayList<>();

		try {

			List<String> tempFinancerFilterList = new ArrayList<>();

			String hql = "from TCkCtMstOpmRate o where o.opmrStatus=:status";
			Map<String, Object> params = new HashMap<>();
			params.put("status", RecordStatus.ACTIVE.getCode());
			List<TCkCtMstOpmRate> opmRateList = ckCtMstOpmRateDao.getByQuery(hql, params);
			if (opmRateList != null && opmRateList.size() > 0) {
				for (TCkCtMstOpmRate or : opmRateList) {
					Hibernate.initialize(or.getTMstBank());
					Hibernate.initialize(or.getTMstCurrency());
					if (!tempFinancerFilterList.contains(or.getTMstBank().getBankId())) {
						tempFinancerFilterList.add(or.getTMstBank().getBankId());
						options.add(new FinancerOptions(or.getTMstBank().getBankId(),
								or.getTMstBank().getBankDescription()));

					}
				}
			}

		} catch (Exception ex) {
			throw ex;
		}

		return options;
	}

	public MstBank getBankDetails(String bankId) throws Exception {
		if (StringUtils.isBlank(bankId))
			throw new ParameterException("param bank ID empty or null");

		String hql = "from TMstBank o where o.bankStatus = :status and o.bankId = :bankId";
		Map<String, Object> params = new HashMap<>();
		params.put("status", RecordStatus.ACTIVE.getCode());
		params.put("bankId", bankId);

		List<TMstBank> bankList = mstBankDao.getByQuery(hql, params);
		if (bankList != null && bankList.size() > 0) {
			// only expect one
			TMstBank entity = bankList.get(0);
			return new MstBank(entity);
		}

		return null;
	}

	class FinancerOptions {
		String value;
		String desc;

		public FinancerOptions(String value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * @return the desc
		 */
		public String getDesc() {
			return desc;
		}

		/**
		 * @param desc the desc to set
		 */
		public void setDesc(String desc) {
			this.desc = desc;
		}

	}
}
