package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.common.service.AbstractCkListingService;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.dto.CkJobReject;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.job.model.TCkJobReject;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public class CkJobRejectRemarksListService extends AbstractCkListingService<TCkJobReject, String, CkJobReject> {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkJobRejectRemarksListService.class);
	private static String AUDIT_TAG = "CK JOB REJECT";
	private static String TABLE_NAME = "T_CK_JOB_REJECT";

	public CkJobRejectRemarksListService() {
		super("ckJobRejectDao", AUDIT_TAG, TCkJobReject.class.getName(), TABLE_NAME);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkJobReject> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkJobReject dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkJobReject o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkJobReject> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkJobReject> dtos = entities.stream().map(x -> {
				try {
					return dtoFromEntity(x);
				} catch (ParameterException | ProcessingException e) {
					LOG.error("filterBy", e);
				}
				return null;
			}).collect(Collectors.toList());

			return dtos;
		} catch (ParameterException | ProcessingException ex) {
			LOG.error("filterBy", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("filterBy", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkJobReject initEnity(TCkJobReject entity) throws ParameterException, ProcessingException {
		if(entity != null) {
			Hibernate.initialize(entity.getTCkJob());
		}
		
		return entity;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkJobReject dtoFromEntity(TCkJobReject entity) throws ParameterException, ProcessingException {
		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkJobReject dto = new CkJobReject(entity);
			// no deep copy from BeanUtils
			TCkJob ckJobE = entity.getTCkJob();
			if (null != ckJobE) {
				CkJob ckJob = new CkJob(ckJobE);

				dto.setTCkJob(ckJob);
			}

			return dto;
		} catch (ParameterException ex) {
			LOG.error("dtoFromEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("dtoFromEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected String getWhereClause(CkJobReject dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			StringBuffer searchStatement = new StringBuffer();

			Optional<CkJob> opCkJob = Optional.of(dto.getTCkJob());
			if (opCkJob.isPresent()) {
				if (StringUtils.isNotBlank(opCkJob.get().getJobId())) {
					searchStatement.append(getOperator(wherePrinted)).append("o.TCkJob.jobId = :jobId");
					wherePrinted = true;
				}
			}

			return searchStatement.toString();
		} catch (ParameterException ex) {
			LOG.error("getWhereClause", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getWhereClause", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected HashMap<String, Object> getParameters(CkJobReject dto) throws ParameterException, ProcessingException {
		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			Optional<CkJob> opCkJob = Optional.of(dto.getTCkJob());
			if (opCkJob.isPresent()) {
				if (StringUtils.isNotBlank(opCkJob.get().getJobId())) {
					parameters.put("jobId", opCkJob.get().getJobId());
				}
			}

			return parameters;
		} catch (ParameterException ex) {
			LOG.error("getParameters", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getParameters", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkJobReject whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkJobReject dto = new CkJobReject();
			CkJob ckJob = new CkJob();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.jobId"))
					ckJob.setJobId(opValue.get());

			}

			dto.setTCkJob(ckJob);

			return dto;
		} catch (ParameterException ex) {
			LOG.error("whereDto", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("whereDto", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected String formatOrderBy(String attribute) throws Exception {
		String newAttr = attribute;

		if (StringUtils.contains(newAttr, "tckJob"))
			newAttr = newAttr.replace("tckJob", "TCkJob");

		return newAttr;
	}

}
