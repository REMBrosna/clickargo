package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.IJobTruckStateService;
import com.guudint.clickargo.common.service.impl.CKEncryptionUtil;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.entity.IEntityService;

@Service
public class TruckJobNoSessionService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(TruckJobNoSessionService.class);

	@Autowired
	private CkJobTruckService jobTruckService;

	@Autowired
	private IJobTruckStateService<CkJobTruck> jobStateService;

	@Autowired
	@Qualifier("ccmAccnService")
	private IEntityService<TCoreAccn, String, CoreAccn> accnService;

	public CkJobTruck findJobTruck(String id)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		try {
			String jobId = CKEncryptionUtil.decrypt(id, "000");

			CkJobTruck jobTruck = jobTruckService.findById(jobId);
			return jobTruck;
		} catch (Exception ex) {
			throw ex;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CkJobTruck update(CkJobTruck dto, String encAccnId, String encRoles)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		try {

			if (dto == null)
				throw new ParameterException("param dto null");

			if (StringUtils.isBlank(encAccnId))
				throw new ParameterException("param encAccnId null or empty");

			if (StringUtils.isBlank(encRoles))
				throw new ParameterException("param encRoles null or empty");

			String accnIdStr = CKEncryptionUtil.decrypt(encAccnId, dto.getJobId());
			String rolesStr = CKEncryptionUtil.decrypt(encRoles, dto.getJobId());

			log.info(String.format("jobId: %s, accnId: %s, action: %s", dto.getJobId(), accnIdStr, dto.getAction()));

			Principal principal = createFakePrincipal(accnIdStr, rolesStr);
			if (null != dto.getAction()) {
				switch (dto.getAction()) {

				case VERIFY_BILL:
					return jobStateService.verifyJobPayment(dto, principal);
				case ACKNOWLEDGE_BILL:
					return jobStateService.acknowledgeJobPayment(dto, principal);
				case REJECT_BILL:
					return jobStateService.rejectJobPayment(dto, principal);
				default:
					break;

				}
			}

			return dto;

		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException ex) {
			throw ex;

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

	}

	private Principal createFakePrincipal(String accnId, String roles) throws Exception {

		Principal principal = new Principal();
		principal.setUserId("SYS_EMAIL");
		principal.setUserName("SYS_EMAIL");
		principal.setUserAccnId(accnId);
		principal.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		CoreAccn accn = accnService.findById(accnId);
		principal.setCoreAccn(accn);
		principal.setRoleList(Arrays.asList(roles.split(":")));
		return principal;
	}
}
