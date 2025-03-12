package com.guudint.clickargo.clictruck.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dto.CkCtAccnInqReq;
import com.guudint.clickargo.clictruck.common.dto.CkCtAccnInqReq.ReqState;
import com.guudint.clickargo.clictruck.common.model.TCkCtAccnInqReq;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.manageaccn.service.CkManageAccnService;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreUsr;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityFilterResponse;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityOrderBy.ORDERED;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.master.controller.PathNotFoundException;
import com.vcc.camelone.master.dto.MstAccnType;
import com.vcc.camelone.util.PrincipalUtilService;

import io.jsonwebtoken.lang.Collections;

@Service
public class CkCtAccnInqReqServiceImpl2 {

	private static Logger log = Logger.getLogger(CkCtAccnInqReqServiceImpl2.class);

	protected static String HISTORY = "history";
	protected static String DEFAULT = "default";

	@Autowired
	private CkManageAccnService ckManageAccnService;

	@Autowired
	@Qualifier("ckCtAccnInqReqDao")
	private GenericDao<TCkCtAccnInqReq, String> ckCtAccnInqReqDao;

	@Autowired
	private PrincipalUtilService principalUtilService;

	public String createAccnInquiryRequest(CkCtAccnInqReq req) throws ParameterException, Exception {
		log.debug("createAccnInquiryRequest");
		try {
			if (req == null)
				throw new ParameterException("param req null");
			if (StringUtils.isBlank(req.getAccnRegTaxNo()))
				throw new ParameterException("param accnRegTaxNo null or empty");

			// Find the account based on the company registration/tax no.
			TCoreAccn accn = ckManageAccnService.getAccountDetailsByRegNo(req.getAccnRegTaxNo());
			if (accn == null) {
				// just return ok, no need to inform requestor that the account exists or not
				return "ok";
			}

			// create the ID
			TCkCtAccnInqReq reqEntity = new TCkCtAccnInqReq();
			req.copyBeanProperties(reqEntity);
			reqEntity.setAirId(CkUtil.generateId("AIR"));
			reqEntity.setTCoreAccn(accn);
			reqEntity.setAirReqState(ReqState.PENDING.name());
			reqEntity.setAirStatus(RecordStatus.ACTIVE.getCode());
			reqEntity.setAirDtCreate(new Date());
			reqEntity.setAirDtLupd(new Date());
			reqEntity.setAirUidCreate("SYS");
			reqEntity.setAirUidLupd("SYS");
			ckCtAccnInqReqDao.add(reqEntity);
			return "ok";

		} catch (Exception ex) {
			log.error("createAccnInquiryRequest", ex);
			throw ex;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtAccnInqReq getAccnInqRequest(String accnReqId)
			throws ParameterException, EntityNotFoundException, Exception {
		log.debug("getAccnInqRequest");
		try {

			if (StringUtils.isBlank(accnReqId))
				throw new ParameterException("param accnReqId null or empty");

			TCkCtAccnInqReq accnInqReqE = ckCtAccnInqReqDao.find(accnReqId);
			if (accnInqReqE == null)
				throw new EntityNotFoundException("id: " + accnReqId + " not found");

			Hibernate.initialize(accnInqReqE.getTCoreAccn());
			Hibernate.initialize(accnInqReqE.getTCoreUsr());

			CkCtAccnInqReq dto = new CkCtAccnInqReq(accnInqReqE);
			CoreAccn accn = new CoreAccn(accnInqReqE.getTCoreAccn());
			CoreUsr usr = new CoreUsr(accnInqReqE.getTCoreUsr());
			dto.setTCoreAccn(accn);
			dto.setTCoreUsr(usr);
			return dto;

		} catch (Exception ex) {
			log.error("getAccnInqRequest", ex);
			throw ex;
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Optional<Object> getEntitiesByProxy(Map<String, String> params)
			throws ParameterException, PathNotFoundException, ProcessingException {
		log.debug("getEntitiesProxy");

		try {

			if (Collections.isEmpty(params))
				throw new ParameterException("param params null or empty");

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null) {
				throw new ProcessingException("principal is null");
			}

			EntityFilterRequest filterRequest = new EntityFilterRequest();
			// start and length parameter extraction
			filterRequest.setDisplayStart(
					params.containsKey("iDisplayStart") ? Integer.valueOf(params.get("iDisplayStart")).intValue() : -1);
			filterRequest.setDisplayLength(
					params.containsKey("iDisplayLength") ? Integer.valueOf(params.get("iDisplayLength")).intValue()
							: -1);
			// where parameters extraction
			ArrayList<EntityWhere> whereList = new ArrayList<>();
			List<String> searches = params.keySet().stream().filter(x -> x.contains("sSearch_"))
					.collect(Collectors.toList());
			for (int nIndex = 1; nIndex <= searches.size(); nIndex++) {
				String searchParam = params.get("sSearch_" + String.valueOf(nIndex));
				String valueParam = params.get("mDataProp_" + String.valueOf(nIndex));
				log.info("searchParam: " + searchParam + " valueParam: " + valueParam);
				whereList.add(new EntityWhere(valueParam, searchParam));
			}

			filterRequest.setWhereList(whereList);
			// order by parameters extraction
			Optional<String> opSortAttribute = Optional.ofNullable(params.get("mDataProp_0"));
			Optional<String> opSortOrder = Optional.ofNullable(params.get("sSortDir_0"));
			if (opSortAttribute.isPresent() && opSortOrder.isPresent()) {
				EntityOrderBy orderBy = new EntityOrderBy();
				orderBy.setAttribute(opSortAttribute.get());
				orderBy.setOrdered(opSortOrder.get().equalsIgnoreCase("desc") ? ORDERED.DESC : ORDERED.ASC);
				filterRequest.setOrderBy(orderBy);
			}

			if (!filterRequest.isValid())
				throw new ProcessingException("Invalid request: " + filterRequest.toJson());

			List<CkCtAccnInqReq> en = filterBy(filterRequest);
			List<Object> entities = List.class.cast(en);
			EntityFilterResponse filterResponse = new EntityFilterResponse();
			filterResponse.setiTotalRecords(entities.size());
			filterResponse.setiTotalDisplayRecords(filterRequest.getTotalRecords());
			filterResponse.setAaData((ArrayList<Object>) entities);

			return Optional.of(filterResponse);
		} catch (ParameterException | PathNotFoundException | ProcessingException ex) {
			log.error("getEntitiesProxy", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("getEntitiesProxy", ex);
			throw new ProcessingException(ex);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtAccnInqReq> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		log.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtAccnInqReq dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(this.countByAnd(dto));

			String selectClause = "from TCkCtAccnInqReq o ";
			String orderByClause = filterRequest.getOrderBy().toString();
			List<TCkCtAccnInqReq> entities = this.findEntitiesByAnd(dto, selectClause, orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtAccnInqReq> dtos = entities.stream().map(x -> {
				try {
					CkCtAccnInqReq accn = dtoFromEntity(x);
					return accn;
				} catch (ParameterException e) {
					log.error("filterBy", e);
				} catch (ProcessingException e) {
					log.error("filterBy", e);
				}
				return null;
			}).collect(Collectors.toList());

			return dtos;
		} catch (ParameterException | ProcessingException ex) {
			log.error("filterBy", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("filterBy", ex);
			throw new ProcessingException(ex);
		}
	}

	protected CkCtAccnInqReq whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		log.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtAccnInqReq dto = new CkCtAccnInqReq();

			CoreAccn accn = new CoreAccn();
			CoreUsr usr = new CoreUsr();

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccn.accnId"))
					accn.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccn.accnName"))
					accn.setAccnName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreUsr.usrUid"))
					usr.setUsrUid(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreUsr.usrName"))
					usr.setUsrName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("airEmailReq"))
					dto.setAirEmailReq(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("airReqState"))
					dto.setAirReqState(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("airStatus"))
					dto.setAirStatus(opValue.get().charAt(0));

				if (entityWhere.getAttribute().equalsIgnoreCase("airDtCreate"))
					dto.setAirDtCreate(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("airDtLupd"))
					dto.setAirDtLupd(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("airUidCreate"))
					dto.setAirUidCreate(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("airUidLupd"))
					dto.setAirUidLupd(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("airDtProcessed"))
					dto.setAirDtProcessed(sdfDate.parse(opValue.get()));

				// history toggle
				if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
					dto.setHistory(opValue.get());
				} else if (entityWhere.getAttribute().equalsIgnoreCase(DEFAULT)) {
					dto.setHistory(opValue.get());
				}

			}

			dto.setTCoreAccn(accn);
			dto.setTCoreUsr(usr);
			return dto;
		} catch (ParameterException ex) {
			log.error("whereDto", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("whereDto", ex);
			throw new ProcessingException(ex);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public int countByAnd(CkCtAccnInqReq dto) throws ParameterException, EntityNotFoundException, ProcessingException {
		log.debug("countByAnd");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			String whereClause = this.getWhereClause(dto, false); // abstract callback
			HashMap<String, Object> parameters = this.getParameters(dto); // abstract callback

			int count = 0;
			if (StringUtils.isNotEmpty(whereClause) && null != parameters && parameters.size() > 0) {
				count = this.ckCtAccnInqReqDao.count("SELECT COUNT(o) FROM TCkCtAccnInqReq o" + whereClause,
						parameters);
			} else {
				count = this.ckCtAccnInqReqDao.count("SELECT COUNT(o) FROM TCkCtAccnInqReq o");
			}
			return count;
		} catch (ParameterException | EntityNotFoundException ex) {
			log.error("countByAnd", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("countByAnd", ex);
			throw new ProcessingException(ex);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected List<TCkCtAccnInqReq> findEntitiesByAnd(CkCtAccnInqReq dto, String selectClause, String orderByClause,
			int limit, int offset) throws ParameterException, ProcessingException {
		log.debug("findEntitesByAnd");
		try {
			if (null == dto)
				throw new ParameterException("param dto null");
			if (StringUtils.isEmpty(selectClause))
				throw new ParameterException("param selectClause null or empty");
			if (StringUtils.isEmpty(orderByClause))
				throw new ParameterException("param orderByClause null or empty");

			String whereClause = this.getWhereClause(dto, false); // abstract callback
			log.debug("whereClause: " + whereClause);
			HashMap<String, Object> parameters = this.getParameters(dto); // abstract callback

			String hqlQuery = StringUtils.isEmpty(whereClause) ? selectClause + orderByClause
					: selectClause + whereClause + orderByClause;

			List<TCkCtAccnInqReq> entities = ckCtAccnInqReqDao.getByQuery(hqlQuery, parameters, limit, offset);
			for (TCkCtAccnInqReq entity : entities)
				this.initEnity(entity); // abstract callback
			return entities;
		} catch (ParameterException | ProcessingException ex) {
			log.error("findEntitiesByAnd", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("findEntitiesByAnd", ex);
			throw new ProcessingException(ex);
		}
	}

	public CkCtAccnInqReq dtoFromEntity(TCkCtAccnInqReq entity) throws ParameterException, ProcessingException {
		log.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtAccnInqReq dto = new CkCtAccnInqReq(entity);

			Optional<TCoreAccn> opCoreAccn = Optional.ofNullable(entity.getTCoreAccn());
			dto.setTCoreAccn(opCoreAccn.isPresent() ? new CoreAccn(opCoreAccn.get()) : null);

			Optional<TCoreUsr> opCoreUsr = Optional.ofNullable(entity.getTCoreUsr());
			dto.setTCoreUsr(opCoreUsr.isPresent() ? new CoreUsr(opCoreUsr.get()) : null);

			return dto;
		} catch (ParameterException ex) {
			log.error("entityFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("dtoFromEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	protected String getWhereClause(CkCtAccnInqReq dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		log.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal null");

			StringBuffer searchStatement = new StringBuffer();

			CoreAccn accn = principal.getCoreAccn();
			Optional<MstAccnType> opAccnType = Optional.ofNullable(accn.getTMstAccnType());
			// only proceed if it's SP
			if (opAccnType.isPresent() && opAccnType.get().getAtypId().equals(AccountTypes.ACC_TYPE_SP.name())) {

				// Exclude Registration Rejected from filter
				searchStatement.append(getOperator(wherePrinted)).append("o.airStatus = :airStatus");
				wherePrinted = true;

				searchStatement.append(getOperator(wherePrinted) + "o.airReqState IN (:airReqState)");
				wherePrinted = true;

				Optional<CoreAccn> opCoreAccn = Optional.ofNullable(dto.getTCoreAccn());
				if (opCoreAccn.isPresent()) {
					if (StringUtils.isNotBlank(opCoreAccn.get().getAccnId())) {
						searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccn.accnId = :accnId");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opCoreAccn.get().getAccnName())) {
						searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccn.accnName LIKE :accnName");
						wherePrinted = true;
					}

				}

				Optional<CoreUsr> opCoreUsr = Optional.ofNullable(dto.getTCoreUsr());
				if (opCoreUsr.isPresent()) {
					if (StringUtils.isNotBlank(opCoreUsr.get().getUsrUid())) {
						searchStatement.append(getOperator(wherePrinted) + "o.TCoreUsr.usrUid = :usrUid");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opCoreUsr.get().getUsrName())) {
						searchStatement.append(getOperator(wherePrinted) + "o.TCoreUsr.usrName LIKE :usrName");
						wherePrinted = true;
					}
				}

				if (StringUtils.isNotBlank(dto.getAirEmailReq())) {
					searchStatement.append(getOperator(wherePrinted) + "o.airEmailReq LIKE :airEmailReq");
					wherePrinted = true;
				}

				if (StringUtils.isNotBlank(dto.getAirRemarks())) {
					searchStatement.append(getOperator(wherePrinted) + "o.airRemarks = :airRemarks");
					wherePrinted = true;
				}

				if (null != dto.getAirDtCreate()) {
					searchStatement
							.append(getOperator(wherePrinted) + "DATE_FORMAT(o.airDtCreate,'%d/%m/%Y') = :airDtCreate");
					wherePrinted = true;
				}

				if (null != dto.getAirDtLupd()) {
					searchStatement
							.append(getOperator(wherePrinted) + "DATE_FORMAT(o.airDtLupd,'%d/%m/%Y') = :airDtLupd");
					wherePrinted = true;
				}

				if (null != dto.getAirDtProcessed()) {
					searchStatement.append(
							getOperator(wherePrinted) + "DATE_FORMAT(o.airDtProcessed,'%d/%m/%Y') = :airDtProcessed");
					wherePrinted = true;
				}

				if (!StringUtils.isEmpty(dto.getAirUidCreate())) {
					searchStatement.append(getOperator(wherePrinted) + "o.airUidCreate LIKE :airUidCreate");
					wherePrinted = true;
				}

				if (!StringUtils.isEmpty(dto.getAirUidLupd())) {
					searchStatement.append(getOperator(wherePrinted) + "o.airUidLupd LIKE :airUidLupd");
					wherePrinted = true;
				}

			}

			return searchStatement.toString();
		} catch (ParameterException ex) {
			log.error("getWhereClause", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("getWhereClause", ex);
			throw new ProcessingException(ex);
		}
	}

	protected HashMap<String, Object> getParameters(CkCtAccnInqReq dto) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		log.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
			HashMap<String, Object> parameters = new HashMap<String, Object>();

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal null");

			CoreAccn accn = principal.getCoreAccn();
			Optional<MstAccnType> opAccnType = Optional.ofNullable(accn.getTMstAccnType());
			// only proceed if it's SP
			if (opAccnType.isPresent() && opAccnType.get().getAtypId().equals(AccountTypes.ACC_TYPE_SP.name())) {

				parameters.put("airStatus", RecordStatus.ACTIVE.getCode());

				if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(DEFAULT)) {
					parameters.put("airReqState", Arrays.asList(ReqState.PENDING.name(), ReqState.INPROGRESS.name()));
				} else {
					parameters.put("airReqState", Arrays.asList(ReqState.COMPLETED.name()));
				}

				Optional<CoreAccn> opCoreAccn = Optional.ofNullable(dto.getTCoreAccn());
				if (opCoreAccn.isPresent()) {
					if (StringUtils.isNotBlank(opCoreAccn.get().getAccnId()))
						parameters.put("accnId", opCoreAccn.get().getAccnId());

					if (StringUtils.isNotBlank(opCoreAccn.get().getAccnName()))
						parameters.put("accnName", "%" + opCoreAccn.get().getAccnName() + "%");

				}

				Optional<CoreUsr> opCoreUsr = Optional.ofNullable(dto.getTCoreUsr());
				if (opCoreUsr.isPresent()) {
					if (StringUtils.isNotBlank(opCoreUsr.get().getUsrUid()))
						parameters.put("usrUid", opCoreUsr.get().getUsrUid());

					if (StringUtils.isNotBlank(opCoreUsr.get().getUsrName())) {
						parameters.put("usrName", "%" + opCoreUsr.get().getUsrName() + "%");
					}
				}

				if (StringUtils.isNotBlank(dto.getAirEmailReq()))
					parameters.put("airEmailReq", "%" + dto.getAirEmailReq() + "%");

				if (StringUtils.isNotBlank(dto.getAirRemarks()))
					parameters.put("airRemarks", "%" + dto.getAirRemarks() + "%");

				if (null != dto.getAirDtCreate())
					parameters.put("airDtCreate", sdfDate.format(dto.getAirDtCreate()));

				if (null != dto.getAirDtLupd())
					parameters.put("airDtLupd", sdfDate.format(dto.getAirDtLupd()));

				if (null != dto.getAirDtProcessed())
					parameters.put("airDtProcessed", sdfDate.format(dto.getAirDtProcessed()));

				if (!StringUtils.isEmpty(dto.getAirUidCreate()))
					parameters.put("airUidCreate", "%" + dto.getAirUidCreate() + "%");

				if (!StringUtils.isEmpty(dto.getAirUidLupd()))
					parameters.put("airUidLupd", "%" + dto.getAirUidLupd() + "%");

			}

			return parameters;
		} catch (ParameterException ex) {
			log.error("getParameters", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("getParameters", ex);
			throw new ProcessingException(ex);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtAccnInqReq initEnity(TCkCtAccnInqReq entity) throws ParameterException, ProcessingException {
		if (null != entity) {
			Hibernate.initialize(entity.getTCoreAccn());
			Hibernate.initialize(entity.getTCoreUsr());
		}
		return entity;
	}

	protected String getOperator(boolean whereprinted) {
		return whereprinted ? " AND " : " WHERE ";
	}
}
