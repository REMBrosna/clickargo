package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.master.dao.CoreAccnConfigDao;
import com.guudint.clickargo.master.dao.CoreAccnDao;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccnConfig;
import com.vcc.camelone.ccm.model.TCoreAccnConfigId;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.master.dto.MstAccnType;

@Service
public class CkTruckMiscMobileService {

	private static Logger LOG = Logger.getLogger(CkTruckMiscMobileService.class);
	private final String acfgKey = "MOBILE_ENABLED";

	@Autowired
	private CoreAccnConfigDao coreAccnConfigDao;

	@Autowired
	private CoreAccnDao coreAccnDao;

	@Autowired
	CkJobTruckService ckJobTruckService;

	@Transactional
	public void checkAndSetMobileEnable(CkJobTruck dto) throws Exception {
		LOG.debug("checkAndSetMobileEnable");

		if (null == dto)
			throw new ParameterException("param dto null");

		Optional<TCoreAccn> opAccnTO = Optional
				.ofNullable(coreAccnDao.find(dto.getTCoreAccnByJobPartyTo().getAccnId()));

		if (!opAccnTO.isPresent())
			throw new ParameterException("param account TO null");

		Hibernate.initialize(opAccnTO.get().getTMstAccnType());

		CoreAccn coreAccn = new CoreAccn(opAccnTO.get());
		coreAccn.setTMstAccnType(new MstAccnType(opAccnTO.get().getTMstAccnType()));

		// Only get mobile enable flag from account config if the job is not created
		// from file upload (excel/xml)
		if (dto.getJobSource() == null
				|| (dto.getJobSource() != null && dto.getJobSource().equalsIgnoreCase("WEB"))) {
			if (null != coreAccn && coreAccn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())) {
				TCoreAccnConfigId tCoreAccnConfigId = new TCoreAccnConfigId();
				tCoreAccnConfigId.setAcfgKey(acfgKey);
				tCoreAccnConfigId.setAcfgAccnid(opAccnTO.get().getAccnId());

				Optional<TCoreAccnConfig> opCoreAccnConfig = Optional
						.ofNullable(coreAccnConfigDao.find(tCoreAccnConfigId));
				if (opCoreAccnConfig.isPresent() && StringUtils.isNotBlank(opCoreAccnConfig.get().getAcfgVal())) {
					dto.setJobMobileEnabled(opCoreAccnConfig.get().getAcfgVal().charAt(0));
				} else {
					dto.setJobMobileEnabled('N');
				}
				
				if(ckJobTruckService.isSingapore()) {
					dto.setJobMobileEnabled('Y');
				}

				// Change in Driver Assignment for mobile
				if (null != dto.getAction() && dto.getAction() == JobActions.ASSIGN) {
					Map<String, Object> drvOth = dto.getJobDrvOth();
					if (null != drvOth && null != drvOth.get("drvId")
							&& drvOth.get("drvId").toString().equalsIgnoreCase("OTHER"))
						dto.setJobMobileEnabled('N');
				}

			}
		}

	}

}
