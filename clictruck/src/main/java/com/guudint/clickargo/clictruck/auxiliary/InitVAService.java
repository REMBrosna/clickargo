package com.guudint.clickargo.clictruck.auxiliary;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.external.clicpay.dto.RequestVA;
import com.guudint.clickargo.external.services.impl.PaymentGatewayService;
import com.guudint.clickargo.master.dao.CoreAccnConfigDao;
import com.guudint.clickargo.master.dao.CoreAccnDao;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Currencies;
import com.guudint.clickargo.payment.enums.PaymentAuditTypes;
import com.guudint.clickargo.payment.model.TCkPaymentAudit;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccnConfig;
import com.vcc.camelone.ccm.model.TCoreAccnConfigId;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceStatus;

@Service
public class InitVAService extends PaymentGatewayService {

	private static Logger log = Logger.getLogger(InitVAService.class);
	public static final String CO2SP_VA_IDR = "CO2SP_VA_IDR";

	@Autowired
	private CoreAccnDao coreAccnDao;

	@Autowired
	private CoreAccnConfigDao coreAccnConfigDao;

	@Transactional
	public void initVA() throws Exception {

		// 1: fetch Account that need to get VA
		List<String> accnTypeList = Arrays.asList(AccountTypes.ACC_TYPE_CO.name(), AccountTypes.ACC_TYPE_FF.name());

		List<TCoreAccn> accnList = coreAccnDao.fetchAccnWithoutInAccnConfig(accnTypeList, CO2SP_VA_IDR);

		log.info("accnList: " + accnList);

		if (accnList == null || accnList.size() == 0) {
			return;
		}

		for (TCoreAccn accn : accnList) {

			log.info("accn.getAccnId(): " + accn.getAccnId());
			if (accn.getAccnContact() != null && !StringUtils.isBlank(accn.getAccnContact().getContactEmail())
					&& !StringUtils.isBlank(accn.getAccnName())) {
				// 2:
				String va = this.getVAFromClicPay(accn);

				log.info("va: " + va);

				if (!StringUtils.isBlank(va)) {
					// 3:
					this.add2AccnConfig(accn, va);
				}
			}
		}
	}

	public String getVAFromClicPay(TCoreAccn accn) throws Exception {

		try {
			Map map = null;

			if (accn == null)
				throw new ParameterException("param accn null");

			// Call login
			if (StringUtils.isBlank(AUTH_TOKEN_HOLDER.get())) {
				login("ADEAND_U001", "CLICTRUCK"); // ????
			}

			Builder builder = initInvocationAuthBuilder(PAYMENT_GW_BASE_URL + "/va", AUTH_TOKEN_HOLDER.get());

			RequestVA reqVA = this.getRequestVA(accn);

			TCkPaymentAudit audit = createPaymentAudit(PaymentAuditTypes.VA_REQUEST, accn.getAccnId(),
					objectMapper.writeValueAsString(reqVA));

			Response apiResponse = builder.post(Entity.entity(reqVA, MediaType.APPLICATION_JSON));

			if (apiResponse == null) {
				audit.setPyaResp("ERROR: no response");
			} else {

				audit.setPyaState(String.valueOf(apiResponse.getStatus()));

				String repsonseBdy = apiResponse.readEntity(String.class);
				ServiceStatus serviceStatus = new ObjectMapper().readValue(repsonseBdy, ServiceStatus.class);
				// String data = (String)serviceStatus.getData();
				// nodeVa = new ObjectMapper().readValue(data, NodeVa.class);
				map = (Map) serviceStatus.getData();

				audit.setPyaResp(repsonseBdy);

				log.info("Response Data: " + repsonseBdy);
			}

			// Update audit
			paymentAuditDao.update(audit);

			return (String) map.get("va");

		} catch (Exception ex) {
			throw ex;
		}
	}

	private void add2AccnConfig(TCoreAccn accn, String va) throws Exception {

		TCoreAccnConfig accnCfg = new TCoreAccnConfig(new TCoreAccnConfigId(accn.getAccnId(), CO2SP_VA_IDR), accn,
				new Date(), new Date(), 1, Constant.ACTIVE_STATUS);

		accnCfg.setAcfgVal(va);
		accnCfg.setAcfgDesc(CO2SP_VA_IDR);

		accnCfg.setAcfgDtCreate(new Date());
		accnCfg.setAcfgUidCreate("sys");

		coreAccnConfigDao.add(accnCfg);
	}

	private RequestVA getRequestVA(TCoreAccn accn) {

		RequestVA reqVA = new RequestVA();

		reqVA.setNode("DNO");
		reqVA.setServiceID("CLICTRUCK");
		reqVA.setRefID(null);
		reqVA.setCallback(null);
		reqVA.setName(accn.getAccnName());
		reqVA.setEmail(accn.getAccnContact()!=null && accn.getAccnContact().getContactEmail()!= null
				? accn.getAccnContact().getContactEmail() : null);
		reqVA.setCcy(Currencies.IDR.name());
		
		return reqVA;
	}

}
