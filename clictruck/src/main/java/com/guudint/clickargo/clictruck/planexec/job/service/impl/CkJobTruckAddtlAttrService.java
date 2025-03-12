package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtConAddAttrDao;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtAddAttrList;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtConAddAttr;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtConAddAttr;
import com.guudint.clickargo.clictruck.admin.contract.service.CkCtContractService;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckAddAttrDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruckAddAttr;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckAddAttr;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.util.PrincipalUtilService;

@Service
public class CkJobTruckAddtlAttrService {

	@Autowired
	private PrincipalUtilService principalUtilService;

	@Autowired
	private CkCtContractService contractService;

	@Autowired
	private CkCtConAddAttrDao ckCtConAddAttrDao;

	@Autowired
	private CkJobTruckAddAttrDao ckJobTruckAddAttrDao;

	@Autowired
	private CkJobTruckDao ckJobTruckDao;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtConAddAttr> getAdditionalFieldsByContract(String toAccnId, String coAccnId)
			throws ParameterException, EntityNotFoundException, Exception {
		try {

			//Do not  throw exception specially if there's no toAccnId selected yet from frontend
			if (StringUtils.isBlank(toAccnId))
				return null;

			//Do not  throw exception
			if (StringUtils.isBlank(coAccnId))
				return null;

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal null");

			// Get the contracts based on the dto parameter
			CkCtContract contract = contractService.getContractByAccounts(toAccnId, coAccnId);
			if (contract == null)
				return null;

			// If contract is found, retrieve the additional attributes
			List<TCkCtConAddAttr> addtlFieldsList = ckCtConAddAttrDao
					.getAdditionalAttributesByContract(contract.getConId());
			if (addtlFieldsList != null && addtlFieldsList.size() > 0) {
				return addtlFieldsList.stream().map(e -> {
					Hibernate.initialize(e.getTCkCtAddAttrList());
					Hibernate.initialize(e.getTCkCtContract());

					CkCtConAddAttr dto = new CkCtConAddAttr(e);
					dto.setTCkCtAddAttrList(new CkCtAddAttrList(e.getTCkCtAddAttrList()));
					dto.setTCkCtContract(new CkCtContract(e.getTCkCtContract()));
					return dto;
				}).collect(Collectors.toList());
			}

			return null;

		} catch (Exception ex) {
			throw ex;
		}

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkJobTruckAddAttr> getAdditionalFields(CkJobTruck dto)
			throws ParameterException, EntityNotFoundException, Exception {
		if (dto == null)
			throw new ParameterException("param dto null");

		List<CkJobTruckAddAttr> list = new ArrayList<>();
		List<TCkJobTruckAddAttr> jobAddAttrList = ckJobTruckAddAttrDao.getAdditionalAttributes(dto.getJobId());
		if (jobAddAttrList != null && jobAddAttrList.size() > 0) {
			for (TCkJobTruckAddAttr entity : jobAddAttrList) {
				Hibernate.initialize(entity.getTCkCtConAddAttr());
				Hibernate.initialize(entity.getTCkJobTruck());

				CkJobTruckAddAttr addtlAttrDto = new CkJobTruckAddAttr(entity);
				addtlAttrDto.setTCkCtConAddAttr(new CkCtConAddAttr(entity.getTCkCtConAddAttr()));
				addtlAttrDto.setTCkJobTruck(new CkJobTruck(entity.getTCkJobTruck()));
				list.add(addtlAttrDto);

			}
		}

		return list;

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void saveAdditionalFields(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, Exception {
		if (dto == null)
			throw new ParameterException("param dto null");

		if (principal == null)
			throw new ParameterException("principal null");

		// Delete the records first if have
		ckJobTruckAddAttrDao.removeAdditionalAttributes(dto.getJobId());

		Date now = new Date();
		if (dto.getAddtlFields() != null && dto.getAddtlFields().size() > 0) {
			for (CkJobTruckAddAttr addAttr : dto.getAddtlFields()) {
				// find the addAttr first
				Optional<String> opConAddAttrId = Optional.ofNullable(addAttr.getTCkCtConAddAttr().getCaaId());
				if (StringUtils.isBlank(opConAddAttrId.get())) {
					throw new ProcessingException("opConAddAttrId not or empty");
				}

				TCkCtConAddAttr conAddAttr = ckCtConAddAttrDao.find(opConAddAttrId.get());
				if (conAddAttr == null)
					throw new EntityNotFoundException(
							"contract aadditional attributes not found: " + opConAddAttrId.get());

				TCkJobTruckAddAttr jobAddAttr = new TCkJobTruckAddAttr();
//				addAttr.copyBeanProperties(jobAddAttr);
				jobAddAttr.setTCkCtConAddAttr(conAddAttr);
				jobAddAttr.setJaaId(CkUtil.generateId("JAA"));

				TCkJobTruck jobTruck = ckJobTruckDao.find(dto.getJobId());
				jobAddAttr.setTCkJobTruck(jobTruck);

				jobAddAttr.setJaaValue(StringUtils.isBlank(addAttr.getJaaValue()) ? "" : addAttr.getJaaValue());
				jobAddAttr.setJaaDtCreate(now);
				jobAddAttr.setJaaUidCreate(principal.getUserId());
				jobAddAttr.setJaaDtLupd(now);
				jobAddAttr.setJaaUidLupd(principal.getUserId());
				jobAddAttr.setJaaStatus(RecordStatus.ACTIVE.getCode());

				ckJobTruckAddAttrDao.add(jobAddAttr);
			}
		}

	}

}
