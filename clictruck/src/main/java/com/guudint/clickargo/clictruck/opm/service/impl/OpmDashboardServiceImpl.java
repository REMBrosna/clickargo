package com.guudint.clickargo.clictruck.opm.service.impl;

import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.accnconfigex.service.ClictruckAccnConfigExService;
import com.guudint.clickargo.clictruck.opm.dao.CkOpmDao;
import com.guudint.clickargo.clictruck.opm.dao.CkOpmSummaryDao;
import com.guudint.clickargo.clictruck.opm.dto.CkOpm;
import com.guudint.clickargo.clictruck.opm.dto.CkOpmSummary;
import com.guudint.clickargo.clictruck.opm.model.TCkOpm;
import com.guudint.clickargo.clictruck.opm.model.TCkOpmSummary;
import com.guudint.clickargo.clictruck.opm.service.IOpmDashboardService;
import com.guudint.clickargo.common.CKCountryConfig;
import com.guudint.clickargo.master.dto.CkMstCreditState;
import com.guudint.clickargo.master.dto.CkMstServiceType;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.master.dto.MstCurrency;

@Service
public class OpmDashboardServiceImpl implements IOpmDashboardService {

	@Autowired
	private CkOpmDao opmDao;

	@Autowired
	private CkOpmSummaryDao opmSummaryDao;

	@Autowired
	@Qualifier("clictruckAccnConfigExService")
	private ClictruckAccnConfigExService clictruckAccnConfigExtService;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	@Override
	public CkOpm find(CkOpm dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		try {

			if (dto == null)
				throw new ParameterException("param dto null");
			if (principal == null)
				throw new ParameterException("param principal null");

			if (null == dto.getTCkMstServiceType())
				throw new ParameterException("param tckMstServiceType null");

			//Get the currency from sysparam config where the application is deployed
			CKCountryConfig ctryConfig = clictruckAccnConfigExtService.getCtryEnv();
			MstCurrency currency = new MstCurrency();
			currency.setCcyCode(ctryConfig.getCurrency());
			dto.setTMstCurrency(currency);
			
			TCkOpm entity = opmDao.getByServiceTypeAndAccnAndCcy(dto.getTCkMstServiceType(), principal.getCoreAccn(),
					dto.getTMstCurrency());
			if (null == entity)
				throw new EntityNotFoundException("No OPM setup for account: " + principal.getCoreAccn().getAccnId()
						+ " for service " + dto.getTCkMstServiceType().getSvctId());

			initEntity(entity);

			dto = dtoFromEntity(entity);

			TCkOpmSummary opmSummary = opmSummaryDao.getByServiceTypeAndAccnAndCcy(dto.getTCkMstServiceType(),
					principal.getCoreAccn(), dto.getTMstCurrency());
			if (opmSummary != null) {
				CkOpmSummary opmSummaryDto = new CkOpmSummary(opmSummary);
				dto.setOpmSummary(opmSummaryDto);
			}

			return dto;

		} catch (Exception ex) {
			throw ex;
		}
	}
	
	private void initEntity(TCkOpm entity) {
		Optional.ofNullable(entity.getTCkMstServiceType()).ifPresent(x -> Hibernate.initialize(x));
		Optional.ofNullable(entity.getTCkMstCreditState()).ifPresent(x -> Hibernate.initialize(x));
		Optional.ofNullable(entity.getTCoreAccn()).ifPresent(x -> Hibernate.initialize(x));
		Optional.ofNullable(entity.getTMstCurrency()).ifPresent(x -> Hibernate.initialize(x));
	}
	
	private CkOpm dtoFromEntity(TCkOpm entity) {
		CkOpm dto = new CkOpm(entity);
		dto.setTCkMstCreditState(new CkMstCreditState(entity.getTCkMstCreditState()));
		dto.setTCkMstServiceType(new CkMstServiceType(entity.getTCkMstServiceType()));
		dto.setTCoreAccn(new CoreAccn(entity.getTCoreAccn()));
		dto.setTMstCurrency(new MstCurrency(entity.getTMstCurrency()));
		return dto;
	}

}
