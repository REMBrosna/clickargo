package com.guudint.clickargo.clictruck.va.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.auxiliary.InitVAService;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.va.dto.VirtualAccount;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.master.dao.CoreAccnDao;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.payment.dao.CkPaymentTxnDao;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreAccnConfig;
import com.vcc.camelone.ccm.dto.CoreAccnConfigId;
import com.vcc.camelone.ccm.dto.CoreAddress;
import com.vcc.camelone.ccm.dto.CoreContact;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccnConfig;
import com.vcc.camelone.ccm.model.TCoreAccnConfigId;
import com.vcc.camelone.ccm.service.impl.AccnConfigService;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.master.dto.MstAccnType;

@Service
public class VirtualAccountServiceImpl extends AccnConfigService {

	private static Logger LOG = Logger.getLogger(VirtualAccountServiceImpl.class);

	private static final String CO2SP_VA_IDR = "CO2SP_VA_IDR";
	
	private static final String descriptive = "SP Payee Account (IDR)";
	
	@Autowired
	protected ICkSession ckSession;
	
	@Autowired
	private CkPaymentTxnDao ckPaymentTxnDao;
	
	@Autowired
	private CoreAccnDao coreAccnDao;
	
	@Autowired
	private InitVAService initVAService;
	
	@Override
	protected TCoreAccnConfig updateEntity(ACTION attribute, TCoreAccnConfig entity, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");
			if (null == principal)
				throw new ParameterException("param principal null");
			if (null == date)
				throw new ParameterException("param date null");
			if (!principal.getRoleList().contains(Roles.SP_OP_ADMIN.toString()))
				throw new ProcessingException("access denied");
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.YEAR, 2);
			Date newDate = calendar.getTime();
			
			TCoreAccnConfigId tCoreAccnConfigId = new TCoreAccnConfigId();

			Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
			switch (attribute) {
			case CREATE:
				tCoreAccnConfigId.setAcfgAccnid(entity.getId().getAcfgAccnid());
				tCoreAccnConfigId.setAcfgKey(CO2SP_VA_IDR);
				entity.setId(tCoreAccnConfigId);
				entity.setAcfgDesc(descriptive);
				entity.setAcfgValidFromDt(date);	
				entity.setAcfgValidToDt(newDate);
				entity.setAcfgSeq(1);
				entity.setAcfgStatus(RecordStatus.ACTIVE.getCode());
				entity.setAcfgUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAcfgDtCreate(date);
				entity.setAcfgUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAcfgDtLupd(date);
				break;

			case MODIFY:
				entity.setAcfgUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAcfgDtLupd(date);
				break;

			default:
				break;
			}

			return entity;
		} catch (ParameterException e) {
			LOG.error("updateEntity", e);
			throw e;
		} catch (Exception e) {
			LOG.error("updateEntity", e);
			throw new ProcessingException(e);
		}
	}

	@Override
	protected String getWhereClause(CoreAccnConfig coreAccnConfig, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";

		if (coreAccnConfig == null) {
			throw new ParameterException("param dto null");
		}
		
		StringBuffer condition = new StringBuffer(super.getWhereClause(coreAccnConfig, wherePrinted));
		wherePrinted = condition.toString().contains("WHERE");
		
		 if (null != coreAccnConfig.getId()) {
	            if (StringUtils.isNotBlank(coreAccnConfig.getId().getAcfgKey())) {
	                condition.append(getOperator(wherePrinted) + "o.id.acfgKey" + EQUAL + "acfgKey");
	                wherePrinted = true;
	            }
	            if (StringUtils.isNotBlank(coreAccnConfig.getId().getAcfgAccnid())) {
	                condition.append(getOperator(wherePrinted) + "o.id.acfgAccnid" + CONTAIN + "acfgAccnid");
	                wherePrinted = true;
	            }
	        }
		
        if (null != coreAccnConfig.getTCoreAccn().getTMstAccnType()) {
            if (StringUtils.isNotBlank(coreAccnConfig.getTCoreAccn().getTMstAccnType().getAtypDescription())) {
                condition.append(getOperator(wherePrinted) + "o.TCoreAccn.TMstAccnType.atypDescription" + CONTAIN
                        + "atypDescription");
                wherePrinted = true;
            }
        }
     	
		if (StringUtils.isNotBlank(coreAccnConfig.getTCoreAccn().getAccnName())) {
			condition.append(getOperator(wherePrinted) + "o.TCoreAccn.accnName" + CONTAIN + "accnName");
			wherePrinted = true;
		}

		if (null != coreAccnConfig.getAcfgDtCreate()) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.acfgDtCreate" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "acfgDtCreate");
			wherePrinted = true;
		}

		if (null != coreAccnConfig.getAcfgDtLupd()) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.acfgDtLupd" + ",'" + DateFormat.MySql.D_M_Y
					+ "')" + EQUAL + "acfgDtLupd");
			wherePrinted = true;
		}
		
		if (null != ((VirtualAccount) coreAccnConfig).getHistory()) {
			condition.append(getOperator(wherePrinted) + "o.acfgStatus = :acfgStatus");
			wherePrinted = true;
		}
		
		return condition.toString();
	}

	@Override
	protected HashMap<String, Object> getParameters(CoreAccnConfig coreAccnConfig)
			throws ParameterException, ProcessingException {
		if (coreAccnConfig == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = super.getParameters(coreAccnConfig);
		
		if (null != coreAccnConfig.getId()) {
			if (coreAccnConfig.getId().getAcfgKey() != null) {
				parameters.put("acfgKey", coreAccnConfig.getId().getAcfgKey());
			}
			if (coreAccnConfig.getId().getAcfgAccnid() != null) {
				parameters.put("acfgAccnid", "%" + coreAccnConfig.getId().getAcfgAccnid() + "%");
			}
		}
		
        if (null != coreAccnConfig.getTCoreAccn().getTMstAccnType()) {
            if (coreAccnConfig.getTCoreAccn().getTMstAccnType().getAtypDescription() != null) {
                parameters.put("atypDescription", "%" + coreAccnConfig.getTCoreAccn().getTMstAccnType().getAtypDescription() + "%");
            }
        }

		if (StringUtils.isNotBlank(coreAccnConfig.getTCoreAccn().getAccnName())) {
			parameters.put("accnName", "%" + coreAccnConfig.getTCoreAccn().getAccnName() + "%");
		}

		if (null != coreAccnConfig.getAcfgDtCreate()) {
			parameters.put("acfgDtCreate", sdf.format(coreAccnConfig.getAcfgDtCreate()));
		}

		if (null != coreAccnConfig.getAcfgDtLupd()) {
			parameters.put("acfgDtLupd", sdf.format(coreAccnConfig.getAcfgDtLupd()));
		}
		
		if(null != ((VirtualAccount)coreAccnConfig).getHistory()) {
			if(((VirtualAccount)coreAccnConfig).getHistory().equalsIgnoreCase("default")) {
				parameters.put("acfgStatus", RecordStatus.ACTIVE.getCode());
			} else if(((VirtualAccount)coreAccnConfig).getHistory().equalsIgnoreCase("history")) {
				parameters.put("acfgStatus", RecordStatus.INACTIVE.getCode());
			}
		}
		
		return parameters;
	}

	@Override
	protected CoreAccnConfig whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		try {
			if (filterRequest == null) {
				throw new ParameterException("param filterRequest null");
			}
			SimpleDateFormat sdfDate = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
			VirtualAccount dto = new VirtualAccount();

			CoreAccn coreAccn = new CoreAccn();
			dto.setTCoreAccn(coreAccn);

			MstAccnType mstAccnType = new MstAccnType();
			
			TCoreAccnConfigId tCoreAccnConfigId = new TCoreAccnConfigId();
			dto.setId(new CoreAccnConfigId(tCoreAccnConfigId));
			dto.getId().setAcfgKey(CO2SP_VA_IDR);
	
			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;
				String attribute = "o." + entityWhere.getAttribute();
				
				if (attribute.equalsIgnoreCase("o.TCoreAccn.accnId"))
					coreAccn.setAccnId(opValue.get());
				else if (attribute.equalsIgnoreCase("o.TCoreAccn.accnName"))
					coreAccn.setAccnName(opValue.get());
				else if (attribute.equalsIgnoreCase("o.TCoreAccn.TMstAccnType.atypDescription")) {
					if(null!=opValue.get()) {
						mstAccnType.setAtypDescription(opValue.get());
					}else {
						mstAccnType.setAtypId(AccountTypes.ACC_TYPE_CO.getDesc());
					}
					coreAccn.setTMstAccnType(mstAccnType);
				}
				else if (attribute.equalsIgnoreCase("o.id.acfgAccnid"))
					dto.getId().setAcfgAccnid(opValue.get());
				else if (attribute.equalsIgnoreCase("o.acfgVal"))
					dto.setAcfgVal(opValue.get());
				else if (attribute.equalsIgnoreCase("o.acfgDtCreate"))
					dto.setAcfgDtCreate(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.acfgDtLupd"))
					dto.setAcfgDtLupd(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.acfgStatus"))
					dto.setAcfgStatus((opValue.get() == null) ? null : opValue.get().charAt(0));
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
	
	@Transactional(rollbackFor = {Exception.class})
	public List<CoreAccn> accnVAEnable() throws Exception {
		Principal principal = ckSession.getPrincipal();
		if (principal == null)
			throw new ParameterException("principal is null");

		if (!principal.getRoleList().contains(Roles.SP_OP_ADMIN.toString()))
			throw new ProcessingException("access denied");
		
		List<String> accnTypeList = Arrays.asList(AccountTypes.ACC_TYPE_CO.name());

		List<TCoreAccn> accnList = coreAccnDao.fetchAccnWithoutInAccnConfig(accnTypeList, CO2SP_VA_IDR);
		
		List<CoreAccn> list = new ArrayList<>();
		for (TCoreAccn tCoreAccn : accnList) {
			CoreAccn coreAccn = new CoreAccn(tCoreAccn);
			coreAccn.setTMstAccnType(new MstAccnType(tCoreAccn.getTMstAccnType()));
			coreAccn.setAccnAddr(new CoreAddress(tCoreAccn.getAccnAddr()));
			coreAccn.setAccnContact(new CoreContact(tCoreAccn.getAccnContact()));
			list.add(coreAccn);
		}
		return list;
	}
	
	@Transactional(rollbackFor = {Exception.class})
	public String generateVANumber(String id) throws Exception {
		Principal principal = ckSession.getPrincipal();
		if (principal == null)
			throw new ParameterException("principal is null");

		if (!principal.getRoleList().contains(Roles.SP_OP_ADMIN.toString()))
			throw new ProcessingException("access denied");
		
		TCoreAccn tCoreAccn = coreAccnDao.find(id);
		
		String va = initVAService.getVAFromClicPay(tCoreAccn);
		return va;
	}
	
	protected List<String> checkVaPayment(String va) throws Exception{
		List<TCkPaymentTxn>listPaymentTxnByVA = ckPaymentTxnDao.findByVa(va);
		List<String> payment = new ArrayList<>();
		
		if(!listPaymentTxnByVA.isEmpty()) {
			for(TCkPaymentTxn tCkPaymentTxn : listPaymentTxnByVA) {
				payment.add(tCkPaymentTxn.getPtxId());
			}
		}
		return payment;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CoreAccnConfig deleteObj(Object obj, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		try {
			if (null == principal)
				throw new ParameterException("param principal null");

			if (!principal.getRoleList().contains(Roles.SP_OP_ADMIN.toString()))
				throw new ProcessingException("access denied");

			CoreAccnConfig coreAccnConfig = (CoreAccnConfig) obj;
			TCoreAccnConfigId entityId = new TCoreAccnConfigId();

			TCoreAccnConfig entity = (TCoreAccnConfig) this.dao.find(coreAccnConfig.getId().toEntity(entityId));

			List<String> payment = checkVaPayment(entity.getAcfgVal());

			if (payment.isEmpty()) {
				return super.delete(coreAccnConfig, principal);
			} else {
				throw new ProcessingException("VA Number : " + entity.getAcfgVal() + " is active for Payment Txn : " + String.join(",", payment));
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CoreAccnConfig> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CoreAccnConfig dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(this.countByAnd(dto));

			String selectClause = "from TCoreAccnConfig o ";
			String orderByClause = this.formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCoreAccnConfig> entities = this.findEntitiesByAnd(dto, selectClause, orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CoreAccnConfig> dtos = entities.stream().map(x -> {
				try {
					CoreAccnConfig config = dtoFromEntity(x);
					return config;
				} catch (ParameterException e) {
					LOG.error("filterBy", e);
				} catch (ProcessingException e) {
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
	
	/**
	 * 
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	protected EntityOrderBy formatOrderByObj(EntityOrderBy orderBy) throws Exception {

		if (orderBy == null)
			return null;

		if (StringUtils.isEmpty(orderBy.getAttribute()))
			return null;

		String newAttr = formatOrderBy(orderBy.getAttribute());
		if (StringUtils.isEmpty(newAttr))
			return orderBy;

		orderBy.setAttribute(newAttr);
		return orderBy;

	}
	
	/**
	 * 
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	protected String formatOrderBy(String attribute) throws Exception {

		String newAttr = attribute;
		if (StringUtils.contains(newAttr, "tcoreAccn"))
			newAttr = newAttr.replace("tcoreAccn", "TCoreAccn");

		return newAttr;
	}
}
