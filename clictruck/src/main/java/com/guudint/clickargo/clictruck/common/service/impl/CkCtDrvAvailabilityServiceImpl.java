package com.guudint.clickargo.clictruck.common.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractDao;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.common.constant.CkCtDrvConstant;
import com.guudint.clickargo.clictruck.common.dto.CkCtDrvAvail;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;

public class CkCtDrvAvailabilityServiceImpl extends AbstractClickCargoEntityService<TCkCtDrv, String, CkCtDrvAvail>
		implements ICkConstant {

	private static Logger LOG = Logger.getLogger(CkCtDrvServiceImpl.class);

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	@Autowired
	@Qualifier("ckJobTruckDao")
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@Autowired
	private CkCtContractDao ckCtContractDao;

	public CkCtDrvAvailabilityServiceImpl() {
		super(CkCtDrvConstant.Table.NAME_DAO, CkCtDrvConstant.Prefix.AUDIT_TAG, CkCtDrvConstant.Table.NAME_ENTITY,
				CkCtDrvConstant.Table.NAME);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtDrvAvail> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtDrvAvail ckCtDrv = whereDto(filterRequest);
		if (ckCtDrv == null) {
			throw new ProcessingException("whereDto null result");
		}
		filterRequest.setTotalRecords(countByAnd(ckCtDrv));
		List<CkCtDrvAvail> ckCtDrvs = new ArrayList<>();
		try {
			String orderClause = formatOrderBy(filterRequest.getOrderBy().toString());
			List<TCkCtDrv> tCkCtDrvs = findEntitiesByAnd(ckCtDrv, "from TCkCtDrv o ", orderClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtDrv tCkCtDrv : tCkCtDrvs) {
				CkCtDrvAvail dto = dtoFromEntity(tCkCtDrv, false);
				if (dto != null) {
					ckCtDrvs.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return ckCtDrvs;
	}

	protected String formatOrderBy(String attribute) throws Exception {
		attribute = Optional.ofNullable(attribute).orElse("");
		return attribute;
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected void initBusinessValidator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'initBusinessValidator'");
	}

	protected CkCtDrvAvail dtoFromEntity(TCkCtDrv tCkCtDrv, boolean withData) throws Exception {
		LOG.debug("dtoFromEntity");
		if (tCkCtDrv == null) {
			throw new ParameterException("param entity null");
		}
		CkCtDrvAvail ckCtDrvAvail = new CkCtDrvAvail(tCkCtDrv);
		if (tCkCtDrv.getTCoreAccn() != null) {
			ckCtDrvAvail.setTCoreAccn(new CoreAccn(tCkCtDrv.getTCoreAccn()));
		}

		ckCtDrvAvail.setDrvMobilePassword(null);

		if (withData) {
			if (tCkCtDrv.getDrvLicensePhotoLoc() != null) {
				try {
					String base64 = FileUtil.toBase64(tCkCtDrv.getDrvLicensePhotoLoc());
					ckCtDrvAvail.setBase64File(base64);
				} catch (IOException e) {
					LOG.error(e);
				}
			}
		}

		if (ckCtDrvAvail.getDrvState().charAt(0) != RecordStatus.INACTIVE.getCode()) {
			Map<String, Integer> data = countDataJob(tCkCtDrv.getDrvId());
			if (data != null && !data.isEmpty()) {
				ckCtDrvAvail.setJobsAllocated(data.get("jobsAllocated"));
				ckCtDrvAvail.setJobsRemaining(data.get("jobsRemaining"));
				ckCtDrvAvail.setJobsCompleted(data.get("jobsCompleted"));
			}
		}

		return ckCtDrvAvail;
	}

	@Override
	protected TCkCtDrv entityFromDTO(CkCtDrvAvail ckCtDrv) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (ckCtDrv == null) {
			throw new ParameterException("param entity null");
		}
		TCkCtDrv tCkCtDrv = new TCkCtDrv(ckCtDrv);
		if (ckCtDrv.getTCoreAccn() != null) {
			tCkCtDrv.setTCoreAccn(ckCtDrv.getTCoreAccn().toEntity(new TCoreAccn()));
		}
		return tCkCtDrv;
	}

	@Override
	protected String entityKeyFromDTO(CkCtDrvAvail ckCtDrv) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtDrv == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtDrv.getDrvId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtDrvAvail ckCtDrv)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		if (ckCtDrv == null) {
			throw new ParameterException("dto param null");
		}
		if (ckCtDrv.getCoreMstLocale() == null) {
			throw new ProcessingException("coreMstLocale null");
		}
		return ckCtDrv.getCoreMstLocale();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtDrvAvail ckCtDrv)
			throws ParameterException, ProcessingException {
		LOG.debug("getParameters");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		if (ckCtDrv == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();
		if (StringUtils.isNotBlank(ckCtDrv.getDrvId())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_ID, ckCtDrv.getDrvId());
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvName())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_NAME, "%" + ckCtDrv.getDrvName() + "%");
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvLicenseNo())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_LICENSE_NO, "%" + ckCtDrv.getDrvLicenseNo() + "%");
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvMobileId())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_MOBILE_ID, "%" + ckCtDrv.getDrvMobileId() + "%");
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvEmail())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_EMAIL, "%" + ckCtDrv.getDrvEmail() + "%");
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvPhone())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_PHONE, "%" + ckCtDrv.getDrvPhone() + "%");
		}
		if (ckCtDrv.getDrvDtCreate() != null) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_DT_CREATE, sdf.format(ckCtDrv.getDrvDtCreate()));
		}
		if (ckCtDrv.getDrvDtLupd() != null) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_DT_LUPD, sdf.format(ckCtDrv.getDrvDtLupd()));
		}

		if (ckCtDrv.getDrvState() != null) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_STATE, ckCtDrv.getDrvState());
		}

		if (ckCtDrv.getDrvStatus() != null) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_STATUS, ckCtDrv.getDrvStatus());
		} else {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_STATUS,
					Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.SUSPENDED.getCode()));
		}

		try {
			List<String> listTCoreAccn = accnCompany(principal);
			if (!listTCoreAccn.isEmpty() && listTCoreAccn.size() > 0) {
				parameters.put(CkCtDrvConstant.ColumnParam.DRV_COMPANY, listTCoreAccn);
			} else {
				// just fill in with the principal, so that it won't throw any exception, it is
				// expected co/ff don't have drivers
				parameters.put(CkCtDrvConstant.ColumnParam.DRV_COMPANY,
						Arrays.asList(principal.getCoreAccn().getAccnId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtDrvAvail ckCtDrv, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :", IN = " in :";
		if (ckCtDrv == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();

		if (StringUtils.isNotBlank(ckCtDrv.getDrvId())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_ID + EQUAL
					+ CkCtDrvConstant.ColumnParam.DRV_ID);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvName())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_NAME + CONTAIN
					+ CkCtDrvConstant.ColumnParam.DRV_NAME);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvLicenseNo())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_LICENSE_NO + CONTAIN
					+ CkCtDrvConstant.ColumnParam.DRV_LICENSE_NO);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvMobileId())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_MOBILE_ID + CONTAIN
					+ CkCtDrvConstant.ColumnParam.DRV_MOBILE_ID);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvEmail())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_EMAIL + CONTAIN
					+ CkCtDrvConstant.ColumnParam.DRV_EMAIL);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvPhone())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_PHONE + CONTAIN
					+ CkCtDrvConstant.ColumnParam.DRV_PHONE);
			wherePrinted = true;
		}
		if (ckCtDrv.getDrvDtCreate() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtDrvConstant.Column.DRV_DT_CREATE + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtDrvConstant.ColumnParam.DRV_DT_CREATE);
			wherePrinted = true;
		}
		if (ckCtDrv.getDrvDtLupd() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtDrvConstant.Column.DRV_DT_LUPD + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtDrvConstant.ColumnParam.DRV_DT_LUPD);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvState())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_STATE + EQUAL
					+ CkCtDrvConstant.ColumnParam.DRV_STATE);
			wherePrinted = true;
		}
		if (ckCtDrv.getDrvState() != null) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_STATE + EQUAL
					+ CkCtDrvConstant.ColumnParam.DRV_STATE);
			wherePrinted = true;
		}

		condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_STATUS + IN
				+ CkCtDrvConstant.ColumnParam.DRV_STATUS);
		wherePrinted = true;

		condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_COMPANY + IN
				+ CkCtDrvConstant.ColumnParam.DRV_COMPANY);
		wherePrinted = true;

		return condition.toString();
	}

	@Override
	protected TCkCtDrv initEnity(TCkCtDrv tCkCtDrv) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (tCkCtDrv != null) {
			Hibernate.initialize(tCkCtDrv.getTCoreAccn());
		}
		return tCkCtDrv;
	}

	@Override
	protected CkCtDrvAvail whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkCtDrvAvail ckCtDrv = new CkCtDrvAvail();
		CoreAccn coreAccn = new CoreAccn();
		ckCtDrv.setTCoreAccn(coreAccn);
		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere == null) {
				continue;
			}
			String attribute = "o." + entityWhere.getAttribute();
			if (CkCtDrvConstant.Column.DRV_COMPANY.equalsIgnoreCase(attribute)) {
				coreAccn.setAccnId(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_ID.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvId(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_STATUS.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvStatus((entityWhere.getValue() == null) ? null : entityWhere.getValue().charAt(0));
			} else if (CkCtDrvConstant.Column.DRV_NAME.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvName(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_LICENSE_NO.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvLicenseNo(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_MOBILE_ID.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvMobileId(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_EMAIL.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvEmail(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_PHONE.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvPhone(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_STATE.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvState(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_DT_CREATE.equalsIgnoreCase(attribute)) {
				try {
					ckCtDrv.setDrvDtCreate(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if (CkCtDrvConstant.Column.DRV_DT_LUPD.equalsIgnoreCase(attribute)) {
				try {
					ckCtDrv.setDrvDtLupd(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			}
		}
		return ckCtDrv;
	}

	/**
	 * Retrieves lists of TO(s) associated to the principal. If the principal is
	 * co/ff it will retrieve the trucking operator from the contract. Since drivers
	 * belongs to trucking operators.
	 */
	public List<String> accnCompany(Principal principal) throws Exception {
		List<String> listTCoreAccn = new ArrayList<>();

		if (principal.getCoreAccn().getTMstAccnType().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())
				|| principal.getCoreAccn().getTMstAccnType().getAtypId()
						.equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())) {

			List<TCkCtContract> listTCkCtContract = ckCtContractDao
					.findValidContractByCoFf(principal.getCoreAccn().getAccnId());

			if (listTCkCtContract != null && listTCkCtContract.size() > 0) {
				listTCoreAccn = listTCkCtContract.stream()
						.map(tCkCtContract -> tCkCtContract.getTCoreAccnByConTo().getAccnId())
						.collect(Collectors.toList());
			}

		} else if (principal.getCoreAccn().getTMstAccnType().getAtypId()
				.equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())) {
			listTCoreAccn = Arrays.asList(principal.getCoreAccn().getAccnId());
		}

		return listTCoreAccn;
	}

	public Map<String, Integer> countDataJob(String drvId) throws Exception {
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}

		Map<String, Integer> data = new HashMap<>();

		StringBuilder hqlStat = new StringBuilder();
		hqlStat.append("SELECT "
				+ "COUNT(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN ('ASG', 'PAUSED', 'ONGOING', 'DLV') THEN o.TCkJob.jobId END) AS JOB_ALLOCATED, "
				+ "COUNT(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN ('PAUSED', 'ONGOING') THEN o.TCkJob.jobId END) AS JOB_REMAINING, "
				+ "COUNT(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN ('DLV') THEN o.TCkJob.jobId END) AS JOB_COMPLETED "
				+ "FROM TCkJobTruck o " + "LEFT JOIN o.TCkCtDrv tckctdrv " + "WHERE o.TCkCtDrv.drvId IN :drvId "
				+ "AND o.TCkCtDrv.drvStatus IN :drvStatus " + "AND o.jobStatus = :jobStatus ");

		Map<String, Object> params = new HashMap<>();
		params.put("drvId", drvId);
		params.put("drvStatus", Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.SUSPENDED.getCode()));
		params.put("jobStatus", RecordStatus.ACTIVE.getCode());

		List<TCkJobTruck> list = ckJobTruckDao.getByQuery(hqlStat.toString(), params);
		if (list != null && list.size() > 0) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Object[] obj = (Object[]) it.next();
				data.put("jobsAllocated", NumberUtil.toInteger(obj[0]));
				data.put("jobsRemaining", NumberUtil.toInteger(obj[1]));
				data.put("jobsCompleted", NumberUtil.toInteger(obj[2]));
			}
		}
		return data;
	}

	@Override
	public CkCtDrvAvail newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CkCtDrvAvail deleteById(String arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CkCtDrvAvail findById(String arg0) throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtDrvAvail dtoFromEntity(TCkCtDrv arg0) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtDrvAvail preSaveUpdateDTO(TCkCtDrv arg0, CkCtDrvAvail arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void preSaveValidation(CkCtDrvAvail arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtDrvAvail arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtDrvAvail setCoreMstLocale(CoreMstLocale arg0, CkCtDrvAvail arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCkCtDrv updateEntity(ACTION arg0, TCkCtDrv arg1, Principal arg2, Date arg3)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCkCtDrv updateEntityStatus(TCkCtDrv arg0, char arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
}
