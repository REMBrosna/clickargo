package com.guudint.clickargo.clictruck.dashboard.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clicdo.common.DashboardJson;
import com.guudint.clicdo.common.enums.DashboardStatus;
import com.guudint.clickargo.clictruck.dashboard.service.IDashboardStatisticsService;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.payment.enums.PaymentStates;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;

public class JobPaymentsDashboardService implements IDashboardStatisticsService{

	@Autowired
	@Qualifier("ckPaymentTxnDao")
	private GenericDao<TCkPaymentTxn, String> ckPaymentTxnDao;
	
	@SuppressWarnings("rawtypes")
	@Override
	public DashboardJson getStatistics(DashboardJson dbJson, Principal principal) throws Exception {
		StringBuilder hqlStat = new StringBuilder(
				"SELECT SUM(CASE WHEN o.ptxPaymentState in (:oversStates) THEN 1 ELSE 0 END ) AS OVER_STATE, ");
		hqlStat.append(" SUM(CASE WHEN o.ptxPaymentState in (:underStates) THEN 1 ELSE 0 END) as UNDER_STATE ");
		hqlStat.append(" FROM TCkPaymentTxn o ");
		hqlStat.append(" WHERE o.ptxPaymentState in (:includeStates) ")
				.append(" AND o.TCoreAccnByPtxPayer.accnId = :accnId ").append(" AND o.ptxStatus=:status");
		Map<String, Object> params = new HashMap<>();
		params.put("oversStates", Arrays.asList(PaymentStates.PAYING.getCode(), PaymentStates.VER_BILL.getCode(),
				PaymentStates.APP_BILL.getCode(), PaymentStates.NEW.getCode()));
		params.put("underStates", Arrays.asList(PaymentStates.PAID.getCode(), PaymentStates.FAILED.getCode()));
		params.put("includeStates", Arrays.asList(PaymentStates.PAYING.getCode(), PaymentStates.PAID.getCode(),
				PaymentStates.VER_BILL.getCode(), PaymentStates.APP_BILL.getCode(), PaymentStates.NEW.getCode()));
		params.put("accnId", principal.getCoreAccn().getAccnId());
		params.put("status", RecordStatus.ACTIVE.getCode());

		List<TCkPaymentTxn> list = ckPaymentTxnDao.getByQuery(hqlStat.toString(), params);
		if (list != null && list.size() > 0) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Object[] obj = (Object[]) it.next();
				dbJson.getTransStatistic().put(DashboardStatus.ACTIVE.name(),
						obj[0] == null ? 0 : Integer.valueOf(String.valueOf(obj[0])));
				dbJson.getTransStatistic().put(DashboardStatus.PAID.name(),
						obj[1] == null ? 0 : Integer.valueOf(String.valueOf(obj[1])));
			}
		}
		
		return dbJson;
	}

}
