package com.guudint.clickargo.clictruck.sage.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.sage.dao.CkCtSageTaxDao;
import com.guudint.clickargo.clictruck.sage.dto.CkCtSageTax;
import com.guudint.clickargo.clictruck.sage.model.TCkCtSageTax;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.event.AbstractJobEvent;
import com.guudint.clickargo.job.service.AbstractJobService;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;

@Service
public class SageTaxService extends AbstractJobService<CkCtSageTax, TCkCtSageTax, String> implements ICkConstant {
	
	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(SageTaxService.class);
	private static String AUDIT_TAG = "CK CT SAGE TAX";
	private static String TABLE_NAME = "T_CK_CT_SAGE_TAX";


	public SageTaxService() {
		super("ckCtSageTaxDaoImpl", AUDIT_TAG, TCkCtSageTax.class.getName(), TABLE_NAME);
	}

	@Autowired
	private CkCtSageTaxDao ckSageTaxDao;
	
	public static char TAX_SEQ_STATUS_EXPIRE = 'E';
	
	public String getNextSageTaxSequence() throws Exception {
		
		try {

			List<TCkCtSageTax> ctList  = ckSageTaxDao.findActiveSageTax();
			if( ctList.isEmpty()) {
				throw new Exception("No active Sage Tax data.");
			}
			
			TCkCtSageTax tCkCtSageTaxEx = null;
			String resultStr = "";

			for (TCkCtSageTax tCkCtSageTax : ctList) {
				if(RecordStatus.ACTIVE.getCode() == tCkCtSageTax.getStStatus()) {
					if(tCkCtSageTax.getStRangeCurrent() == tCkCtSageTax.getStRangeEnd()) {
						tCkCtSageTax.setStStatus(TAX_SEQ_STATUS_EXPIRE);
						tCkCtSageTaxEx = tCkCtSageTax;
					} else {
						tCkCtSageTax.setStRangeCurrent(tCkCtSageTax.getStRangeCurrent()+1);
						resultStr = tCkCtSageTax.getStPrefix() + String.format(tCkCtSageTax.getStRangeFormat(), tCkCtSageTax.getStRangeCurrent());
					}
					ckSageTaxDao.update(tCkCtSageTax);
				}
				
			}
			
			if(tCkCtSageTaxEx != null) {
				for (TCkCtSageTax tCkCtSageTax : ctList) {
					if(RecordStatus.INACTIVE.getCode() == tCkCtSageTax.getStStatus()) {
						tCkCtSageTax.setStStatus(RecordStatus.ACTIVE.getCode());
						ckSageTaxDao.update(tCkCtSageTax);
						resultStr = tCkCtSageTax.getStPrefix() + String.format(tCkCtSageTax.getStRangeFormat(), tCkCtSageTax.getStRangeCurrent());
						break;
					}
				}
			}
			
			return resultStr;

		} catch (Exception ex) {
			throw ex;
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtSageTax deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.debug("deleteById");

		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		return updateStatus(id, "delete");
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtSageTax updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("updateStatus");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		CkCtSageTax ckCtSageTax = findById(id);
		if (ckCtSageTax == null) {
			throw new EntityNotFoundException("id::" + id);
		}
		if ("active".equals(status)) {
			ckCtSageTax.setStStatus(RecordStatus.ACTIVE.getCode());
		} else if ("inactive".equals(status)) {
			ckCtSageTax.setStStatus(RecordStatus.INACTIVE.getCode());
		}else if("delete".equals(status)){
			if(ckCtSageTax.getStStatus().equals(RecordStatus.INACTIVE.getCode())) {
				ckCtSageTax.setStStatus(RecordStatus.DEACTIVATE.getCode());
			}
			else if (ckCtSageTax.getStStatus().equals(RecordStatus.ACTIVE.getCode())) {
				throw new ParameterException("Sage Tax id : " + id + " is ACTIVE");
			}
		}
		update(ckCtSageTax, principal);
		return ckCtSageTax;
	}

	@Override
	public List<CkCtSageTax> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtSageTax ckCtSageTax = whereDto(filterRequest);
		if (ckCtSageTax == null) {
			throw new ProcessingException("whereDto null");
		}
		filterRequest.setTotalRecords(countByAnd(ckCtSageTax));
		List<CkCtSageTax> ckCtSageTaxs = new ArrayList<>();
		try {
			String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtSageTax> tCkCtSageTaxs = findEntitiesByAnd(ckCtSageTax, "from TCkCtSageTax o ", orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtSageTax tCkCtSageTax : tCkCtSageTaxs) {
				CkCtSageTax dto = dtoFromEntity(tCkCtSageTax);
				if (dto != null) {
					ckCtSageTaxs.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return ckCtSageTaxs;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtSageTax findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtSageTax entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);
			this.initEnity(entity);

			return this.dtoFromEntity(entity);

		} catch (ParameterException | EntityNotFoundException ex) {
			LOG.error("findById", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("findById", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected Class<?>[] _validateGroupClass(JobEvent jobEvent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void _auditEvent(JobEvent jobEvent, CkCtSageTax dto, Principal principal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void _auditError(JobEvent jobEvent, CkCtSageTax dto, Exception ex, Principal principal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected AbstractJobEvent<CkCtSageTax> _getJobEvent(JobEvent jobEvent, CkCtSageTax dto, Principal principal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtSageTax _newJob(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtSageTax ckCtSageTax = new CkCtSageTax();
		DateUtil dateUtil = new DateUtil(new Date());
		ckCtSageTax.setStDtCreate(dateUtil.toDate(dateUtil.getDateOnly()));
		return ckCtSageTax;
	}

	@Override
	protected CkCtSageTax _createJob(CkCtSageTax ckSageTax, CkJob parentJob, Principal principal)
			throws ParameterException, ValidationException, ProcessingException, Exception {
		if (ckSageTax == null) {
			throw new ParameterException("param dto null");
		}
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		
		List<TCkCtSageTax> ctList  = ckSageTaxDao.getAll();
		ckSageTax.setStId(CkUtil.generateId(ckSageTax.PREFIX_ID));
		if(ctList.isEmpty()) {
			ckSageTax.setStStatus(RecordStatus.ACTIVE.getCode());
		} else {
			ckSageTax.setStStatus(RecordStatus.INACTIVE.getCode());
		}
		
		ckSageTax.setStRangeCurrent(ckSageTax.getStRangeBegin());
		return add(ckSageTax, principal);
	}

	@Override
	protected CkCtSageTax _submitJob(CkCtSageTax ckSageTax, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtSageTax _rejectJob(CkCtSageTax ckSageTax, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtSageTax _cancelJob(CkCtSageTax ckSageTax, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtSageTax _confirmJob(CkCtSageTax ckSageTax, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtSageTax _payJob(CkCtSageTax ckSageTax, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtSageTax _paidJob(CkCtSageTax ckSageTax, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtSageTax _completeJob(CkCtSageTax ckSageTax, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String formatOrderBy(String attribute) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initBusinessValidator() {
		// TODO Auto-generated method stub
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return LOG;
	}

	@Override
	protected CkCtSageTax dtoFromEntity(TCkCtSageTax tCkCtSageTax) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtSageTax == null) {
			throw new ParameterException("param entity null");
		}
		CkCtSageTax ckCtSageTax = new CkCtSageTax(tCkCtSageTax);
		return ckCtSageTax;
	}

	@Override
	protected TCkCtSageTax entityFromDTO(CkCtSageTax ckCtSageTax) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (ckCtSageTax == null) {
			throw new ParameterException("param dto null");
		}
		TCkCtSageTax tCkCtSageTax = ckCtSageTax.toEntity(new TCkCtSageTax());
		return tCkCtSageTax;
	}

	@Override
	protected String entityKeyFromDTO(CkCtSageTax ckCtSageTax) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtSageTax == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtSageTax.getStId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtSageTax arg0)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtSageTax ckCtSageTax) throws ParameterException, ProcessingException {
		if (ckCtSageTax == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();
		if (StringUtils.isNotBlank(ckCtSageTax.getStId())) {
			parameters.put("stId", "%" + ckCtSageTax.getStId() + "%");
		}
		
		if (StringUtils.isNotBlank(ckCtSageTax.getStPrefix())) {
			parameters.put("stPrefix", "%" + ckCtSageTax.getStPrefix() + "%");
		}
		
		if (ckCtSageTax.getStRangeBegin()!=0) {
		    parameters.put("stRangeBegin", ckCtSageTax.getStRangeBegin());
		}
		
		if (ckCtSageTax.getStRangeEnd()!=0) {
		    parameters.put("stRangeEnd", ckCtSageTax.getStRangeEnd());
		}
		
		if (ckCtSageTax.getStRangeCurrent()!=0) {
		    parameters.put("stRangeCurrent", ckCtSageTax.getStRangeCurrent());
		}
		
		if (ckCtSageTax.getStDtCreate() != null) {
			parameters.put("stDtCreate", sdf.format(ckCtSageTax.getStDtCreate()));
		}
		if (ckCtSageTax.getStDtLupd() != null) {
			parameters.put("stDtLupd", sdf.format(ckCtSageTax.getStDtLupd()));
		}
		
		if (ckCtSageTax.getStStatus() != null) {
			parameters.put("stStatus", ckCtSageTax.getStStatus());
			parameters.put("validStatus", ckCtSageTax.getStStatus());
		} else {
			if (ckCtSageTax.getHistory() != null && ckCtSageTax.getHistory().equalsIgnoreCase("default")) {
				parameters.put("validStatus", Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.INACTIVE.getCode()));
			} 
			else if (ckCtSageTax.getHistory() != null && ckCtSageTax.getHistory().equalsIgnoreCase("history")) {
				parameters.put("validStatus", Arrays.asList(RecordStatus.DEACTIVATE.getCode(), TAX_SEQ_STATUS_EXPIRE));
			}
		}

		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtSageTax ckCtSageTax, boolean wherePrinted) throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (ckCtSageTax == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();
		if (StringUtils.isNotBlank(ckCtSageTax.getStId())) {
			condition.append(getOperator(wherePrinted) + "o.stId" + CONTAIN
					+ "stId");
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtSageTax.getStPrefix())) {
			condition.append(getOperator(wherePrinted) + "o.stPrefix" + CONTAIN
					+ "stPrefix");
			wherePrinted = true;
		}
		if (ckCtSageTax.getStRangeBegin()!=0) {
			condition.append(getOperator(wherePrinted) + "o.stRangeBegin" + EQUAL
					+ "stRangeBegin");
			wherePrinted = true;
		}
		if (ckCtSageTax.getStRangeEnd()!=0) {
			condition.append(getOperator(wherePrinted) + "o.stRangeEnd" + EQUAL
					+ "stRangeEnd");
			wherePrinted = true;
		}
		if (ckCtSageTax.getStRangeCurrent()!=0) {
			condition.append(getOperator(wherePrinted) + "o.stRangeCurrent" + EQUAL
					+ "stRangeCurrent");
			wherePrinted = true;
		}
		if (ckCtSageTax.getStDtCreate() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.stDtCreate" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "stDtCreate");
			wherePrinted = true;
		}
		if (ckCtSageTax.getStDtLupd() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.stDtLupd" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "stDtLupd");
			wherePrinted = true;
		}
		
		if (ckCtSageTax.getStStatus() != null) {
			condition.append(getOperator(wherePrinted) + "o.stStatus" + CONTAIN
					+ "stStatus");
			wherePrinted = true;
		}
		condition.append(getOperator(wherePrinted) + "o.stStatus" + " IN :validStatus");
	
		return condition.toString();
	}

	@Override
	protected TCkCtSageTax initEnity(TCkCtSageTax arg0) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtSageTax preSaveUpdateDTO(TCkCtSageTax arg0, CkCtSageTax arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void preSaveValidation(CkCtSageTax arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtSageTax arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtSageTax setCoreMstLocale(CoreMstLocale arg0, CkCtSageTax arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCkCtSageTax updateEntity(ACTION action, TCkCtSageTax tCkCtSageTax, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (tCkCtSageTax == null)
			throw new ParameterException("param entity null");
		if (principal == null)
			throw new ParameterException("param principal null");
		if (date == null)
			throw new ParameterException("param date null");

		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (action) {
		case CREATE:
			tCkCtSageTax.setStUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			tCkCtSageTax.setStDtCreate(date);
			tCkCtSageTax.setStDtLupd(date);
			tCkCtSageTax.setStUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		case MODIFY:
			try {
				tCkCtSageTax.setStDtLupd(date);
				tCkCtSageTax.setStUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
				if(!tCkCtSageTax.getStStatus().equals(RecordStatus.DEACTIVATE.getCode())) {
					tCkCtSageTax.setStStatus(ckSageTaxDao.find(tCkCtSageTax.getStId()).getStStatus()); // last existing status (cannot be change)
				}
				break;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		default:
			break;
		}
		return tCkCtSageTax;
	}

	@Override
	protected TCkCtSageTax updateEntityStatus(TCkCtSageTax tCkCtSageTax, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkCtSageTax == null) {
			throw new ParameterException("entity param null");
		}
		tCkCtSageTax.setStStatus(status);
		return tCkCtSageTax;
	}

	@Override
	protected CkCtSageTax whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		
		try {
			if (filterRequest == null) {
				throw new ParameterException("param filterRequest null");
			}
			SimpleDateFormat sdfDate = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
			CkCtSageTax dto = new CkCtSageTax();
			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;
				String attribute = "o." + entityWhere.getAttribute();
				if (attribute.equalsIgnoreCase("o.stId"))
					dto.setStId(opValue.get());
				else if (attribute.equalsIgnoreCase("o.stPrefix"))
					dto.setStPrefix(opValue.get());
				else if (attribute.equalsIgnoreCase("o.stRangeBegin"))
					dto.setStRangeBegin(Long.parseLong(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.stRangeEnd"))
					dto.setStRangeEnd(Long.parseLong(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.stRangeCurrent"))
					dto.setStRangeCurrent(Long.parseLong(opValue.get()));	
				else if (attribute.equalsIgnoreCase("o.stDtCreate"))
					dto.setStDtCreate(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.stDtLupd"))
					dto.setStDtLupd(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.stStatus"))
					dto.setStStatus((opValue.get() == null) ? null : opValue.get().charAt(0));
				else if (attribute.equalsIgnoreCase("o.history"))
					dto.setHistory(opValue.get());
			}

			return dto;
		} catch (ParameterException ex) {
			LOG.error("whereDto", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("whereDto", ex);
			throw new ProcessingException(ex);
		}
	}

}
