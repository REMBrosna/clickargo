package com.guudint.clickargo.clictruck.admin.account.service.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.guudint.clickargo.clicservice.dao.CkSuspensionLogDao;
import com.guudint.clickargo.clicservice.model.TCkSuspensionLog;
import com.guudint.clickargo.clictruck.admin.account.dto.AccountSuspend;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.service.impl.AccnService;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.master.dto.MstAccnType;

@Service
public class AccountResumptionServiceImpl extends AccnService {
	
	private static Logger LOG = Logger.getLogger(AccountResumptionServiceImpl.class);
	
	@Autowired
	private CkSuspensionLogDao ckSuspensionLogDao;
	
	@Autowired
	protected ICkSession ckSession;
	
	private final String ACC_TYPE_CO = "CARGO OWNER";
	private final String ACC_TYPE_FF = "FREIGHT FORWADER";
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CoreAccn updateStatus(AccountSuspend accountSuspend)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException, Exception {
		LOG.info("updateStatus");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		CoreAccn coreAccn = findById(accountSuspend.getAccnId());
		if (coreAccn == null) {
			throw new EntityNotFoundException("id::" + accountSuspend.getAccnId());
		}
		if(!Character.isAlphabetic(coreAccn.getAccnStatus())) {
			throw new ParameterException("status is null");
		}
		
		TCkSuspensionLog suspLog = new TCkSuspensionLog();
		if (RecordStatus.ACTIVE.getCode() == accountSuspend.getAccnStatus()) {
			coreAccn.setAccnStatus(accountSuspend.getAccnStatus());
			coreAccn.setAccnDtReins(new Date());
			
			suspLog.setSlDetails("Manual Resumption");
			suspLog.setSlId(CkUtil.generateId());
			suspLog.setSlAccnId(accountSuspend.getAccnId());
			suspLog.setSlEvent("ACCN MODIFY");
			suspLog.setSlRemarks(accountSuspend.getRemarks());
			suspLog.setSlDtCreate(new Date());
			suspLog.setSlUidCreate(principal.getUserId());
			
			ckSuspensionLogDao.add(suspLog);
			update(coreAccn, principal);
		}
		
		return coreAccn;
	}
	
	@Override
	protected String getWhereClause(CoreAccn coreAccn, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :";

		if (coreAccn == null) {
			throw new ParameterException("param dto null");
		}

		StringBuffer searchStatement = new StringBuffer(super.getWhereClause(coreAccn, wherePrinted));
		wherePrinted = searchStatement.toString().contains("WHERE");
		
		if (coreAccn.getAccnDtCreate() != null) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.accnDtCreate" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "accnDtCreate");
			wherePrinted = true;
		}

		if (coreAccn.getAccnDtSusp() != null) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.accnDtSusp" + ",'" + DateFormat.MySql.D_M_Y
					+ "')" + EQUAL + "accnDtSusp");
			wherePrinted = true;
		}

		if (coreAccn.getAccnDtReins() != null) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.accnDtReins" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "accnDtReins");
			wherePrinted = true;
		}

		if (null != coreAccn.getTMstAccnType()) {
			if (StringUtils.isBlank(coreAccn.getTMstAccnType().getAtypDescription())) {
				searchStatement.append(getOperator(wherePrinted) + "o.accnStatus" + " IN :validStatus"
						+ " AND o.TMstAccnType.atypDescription" + " IN :atypDescription");
			} else {
				searchStatement.append(getOperator(wherePrinted) + "o.accnStatus" + " IN :validStatus");
			}
		}

		return searchStatement.toString();
	}
	
	@Override
	protected HashMap<String, Object> getParameters(CoreAccn coreAccn) throws ParameterException, ProcessingException {
		if (coreAccn == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = super.getParameters(coreAccn);

		if (null != coreAccn.getTMstAccnType()) {
			if (coreAccn.getTMstAccnType().getAtypDescription() != null) {
				parameters.put("atypDescription", "%" + coreAccn.getTMstAccnType().getAtypDescription() + "%");
			} else {
				parameters.put("atypDescription", Arrays.asList(ACC_TYPE_CO, ACC_TYPE_FF));
			}
		}

		if (coreAccn.getAccnDtCreate() != null) {
			parameters.put("accnDtCreate", sdf.format(coreAccn.getAccnDtCreate()));
		}

		if (coreAccn.getAccnDtSusp() != null) {
			parameters.put("accnDtSusp", sdf.format(coreAccn.getAccnDtSusp()));
		}

		if (coreAccn.getAccnDtReins() != null) {
			parameters.put("accnDtReins", sdf.format(coreAccn.getAccnDtReins()));
		}

		if (Character.isAlphabetic(coreAccn.getAccnStatus())) {
			parameters.put("accnStatus", coreAccn.getAccnStatus());
			parameters.put("validStatus", Arrays.asList(coreAccn.getAccnStatus()));
		} else {
			if (((AccountSuspend) coreAccn).getHistory() != null
					&& ((AccountSuspend) coreAccn).getHistory().equalsIgnoreCase("default")) {
				parameters.put("validStatus", Arrays.asList(RecordStatus.SUSPENDED.getCode()));
			} else if (((AccountSuspend) coreAccn).getHistory() != null
					&& ((AccountSuspend) coreAccn).getHistory().equalsIgnoreCase("history")) {
				parameters.put("validStatus", Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.DEACTIVATE.getCode()));
			}
		}

		return parameters;
	}

	@Override
	protected AccountSuspend whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		try {
			if (filterRequest == null) {
				throw new ParameterException("param filterRequest null");
			}
			SimpleDateFormat sdfDate = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
			AccountSuspend dto = new AccountSuspend();
			
			MstAccnType mstAccnType = new MstAccnType();
			dto.setTMstAccnType(mstAccnType);
			
			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;
				String attribute = "o." + entityWhere.getAttribute();
				if (attribute.equalsIgnoreCase("o.accnId"))
					dto.setAccnId(opValue.get());
				else if (attribute.equalsIgnoreCase("o.accnName"))
					dto.setAccnName(opValue.get());
				else if (attribute.equalsIgnoreCase("o.TMstAccnType.atypDescription"))
					mstAccnType.setAtypDescription(opValue.get());
				else if (attribute.equalsIgnoreCase("o.accnDtCreate"))
					dto.setAccnDtCreate(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.accnDtSusp"))
					dto.setAccnDtSusp(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.accnDtSusp"))
					dto.setAccnDtReins(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.accnStatus"))
					dto.setAccnStatus((opValue.get() == null) ? null : opValue.get().charAt(0));
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
