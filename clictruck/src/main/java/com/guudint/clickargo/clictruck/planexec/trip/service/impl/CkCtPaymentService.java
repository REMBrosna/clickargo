package com.guudint.clickargo.clictruck.planexec.trip.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentDetails.InvoiceDetails;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService.CkPaymentTypes;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPayment;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPayment;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.impl.CkCoreAccnService;
import com.guudint.clickargo.external.services.IPaymentGateway;
import com.guudint.clickargo.master.dto.CkMstPaymentType;
import com.guudint.clickargo.master.dto.CkMstServiceType;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.payment.dto.CkPaymentTxn;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.entity.AbstractEntityService;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.master.dto.MstCurrency;
import com.vcc.camelone.master.model.TMstCurrency;

@Service
public class CkCtPaymentService extends AbstractEntityService<TCkCtPayment, String, CkCtPayment> {

	// Static Attributes
	////////////////////
	private static Logger LOG = LogManager.getLogger(CkCtPaymentService.class);

	private static String AUDIT_TAG = "CT PAYMENT";
	private static String TABLE_NAME = "T_CK_CT_PAYMENT";

	@Autowired
	private CkCoreAccnService ckCoreAccnService;

	@Autowired
	@Qualifier("ckPaymentTxnDao")
	private GenericDao<TCkPaymentTxn, String> ckPaymentTxnDao;

	@Autowired
	@Qualifier("paymentGatewayService")
	private IPaymentGateway paymentGateway;

	public CkCtPaymentService() {
		super("ckCtPaymentDao", AUDIT_TAG, TCkCtPayment.class.getName(), TABLE_NAME);
	}

	@Override
	public CkCtPayment findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {

			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtPayment entity = dao.find(id);
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
	public CkCtPayment deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CkCtPayment> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtPayment initEnity(TCkCtPayment entity) throws ParameterException, ProcessingException {
		if (entity != null) {
			Hibernate.initialize(entity.getTCkPaymentTxn());
			Hibernate.initialize(entity.getTMstCurrency());
		}

		return entity;
	}

	@Override
	protected TCkCtPayment entityFromDTO(CkCtPayment dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtPayment entity = new TCkCtPayment();
			entity = dto.toEntity(entity);
			entity.setTCkPaymentTxn(
					null == dto.getTCkPaymentTxn() ? null : dto.getTCkPaymentTxn().toEntity(new TCkPaymentTxn()));
			entity.setTMstCurrency(
					null == dto.getTMstCurrency() ? null : dto.getTMstCurrency().toEntity(new TMstCurrency()));

			return entity;
		} catch (ParameterException ex) {
			LOG.error("entityFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("entityFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtPayment dtoFromEntity(TCkCtPayment entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		try {
			if (entity == null)
				throw new ParameterException("param entity null");

			CkCtPayment dto = new CkCtPayment(entity);

			TCkPaymentTxn tCkPaymentTxn = entity.getTCkPaymentTxn();
			if (tCkPaymentTxn != null) {
				CkPaymentTxn ckPaymentTxn = new CkPaymentTxn(tCkPaymentTxn);
				Optional.ofNullable(tCkPaymentTxn.getTCkMstPaymentType())
						.ifPresent(e -> ckPaymentTxn.setTCkMstPaymentType(new CkMstPaymentType(e)));
				Optional.ofNullable(tCkPaymentTxn.getTCkMstServiceType())
						.ifPresent(e -> ckPaymentTxn.setTCkMstServiceType(new CkMstServiceType(e)));
				Optional.ofNullable(tCkPaymentTxn.getTCoreAccnByPtxPayee())
						.ifPresent(e -> ckPaymentTxn.setTCoreAccnByPtxPayee(new CoreAccn(e)));
				Optional.ofNullable(tCkPaymentTxn.getTCoreAccnByPtxPayer())
						.ifPresent(e -> ckPaymentTxn.setTCoreAccnByPtxPayer(new CoreAccn(e)));
				Optional.ofNullable(tCkPaymentTxn.getTMstCurrency())
						.ifPresent(e -> ckPaymentTxn.setTMstCurrency(new MstCurrency(e)));
				dto.setTCkPaymentTxn(ckPaymentTxn);
			}

			TMstCurrency mstCurrency = entity.getTMstCurrency();
			if (mstCurrency != null) {
				dto.setTMstCurrency(new MstCurrency(mstCurrency));
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
	protected String entityKeyFromDTO(CkCtPayment dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return null == dto.getCtpId() ? null : dto.getCtpId();
		} catch (ParameterException ex) {
			LOG.error("entityKeyFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("entityKeyFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkCtPayment updateEntity(ACTION attriubte, TCkCtPayment entity, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");
			if (null == principal)
				throw new ParameterException("param principal null");
			if (null == date)
				throw new ParameterException("param date null");

			Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
			switch (attriubte) {
			case CREATE:
				entity.setCtpUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setCtpDtCreate(date);
				entity.setCtpUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setCtpDtLupd(date);
				break;

			case MODIFY:
				entity.setCtpUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setCtpDtLupd(date);
				break;

			default:
				break;
			}

			return entity;
		} catch (ParameterException ex) {
			LOG.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("updateEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkCtPayment updateEntityStatus(TCkCtPayment entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setCtpStatus(status);
			return entity;
		} catch (ParameterException ex) {
			LOG.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("updateEntityStatus", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtPayment preSaveUpdateDTO(TCkCtPayment storedEntity, CkCtPayment dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setCtpUidCreate(storedEntity.getCtpUidCreate());
			dto.setCtpDtCreate(storedEntity.getCtpDtCreate());

			return dto;
		} catch (ParameterException ex) {
			LOG.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("preSaveUpdateEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected void preSaveValidation(CkCtPayment dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtPayment dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWhereClause(CkCtPayment dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtPayment dto) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtPayment whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtPayment dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtPayment setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtPayment dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public TCkCtPayment createCtPayment(TCkPaymentTxn txn, String doJobId, InvoiceDetails invDetails,
			Principal principal) throws Exception {
		Date now = new Date();
		TCkCtPayment ctPymnt = new TCkCtPayment();
		String accnType = ckCoreAccnService.getPrincipalAccountType(principal);
		if (accnType.equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())) {
			ctPymnt.setCtpId(CkUtil.generateIdSynch(CkCtPayment.PREFIX_FF_CT_PYMNT));
		} else if (accnType.equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {
			ctPymnt.setCtpId(CkUtil.generateIdSynch(CkCtPayment.PREFIX_CO_CT_PYMNT));
		} else if (accnType.equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
			ctPymnt.setCtpId(CkUtil.generateIdSynch(CkCtPayment.PREFIX_SP_CT_PYMNT));
		}

		ctPymnt.setTCkPaymentTxn(txn);
		TMstCurrency mstCurr = new TMstCurrency();
		mstCurr.setCcyCode(invDetails.getInvCurrency());
		ctPymnt.setTMstCurrency(mstCurr);
		ctPymnt.setCtpRef(invDetails.getId());
		ctPymnt.setCtpJob(doJobId);
		ctPymnt.setCtpItem(invDetails.getInvDesc());
		ctPymnt.setCtpAttach(invDetails.getFileLocation());
		ctPymnt.setCtpQty((short) 1);
		// just added tax in the calculation in case later it will be added, although
		// this one is always 0 for now as it is included in the inv amount already.
		ctPymnt.setCtpAmount(invDetails.getInvAmt());

		ctPymnt.setCtpStatus(RecordStatus.ACTIVE.getCode());
		ctPymnt.setCtpState(JobPaymentStates.NEW.name());
		ctPymnt.setCtpDtCreate(now);
		ctPymnt.setCtpUidCreate(principal.getUserId());
		ctPymnt.setCtpDtLupd(now);
		ctPymnt.setCtpUidLupd(principal.getUserId());
		dao.add(ctPymnt);

		// Retrieve from db to commit
		TCkCtPayment ctPaymentE = dao.find(ctPymnt.getCtpId());
		return this.initEnity(ctPaymentE);

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public TCkCtPayment createCtPayment(TCkPaymentTxn txn, String doJobId, InvoiceDetails invDetails,
			Principal principal, CkPaymentTypes ckPaymentTypes) throws Exception {
		Date now = new Date();
		TCkCtPayment ctPymnt = new TCkCtPayment();
		String accnType = ckCoreAccnService.getPrincipalAccountType(principal);
		if (accnType.equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())) {
			ctPymnt.setCtpId(CkUtil.generateIdSynch(CkCtPayment.PREFIX_FF_CT_PYMNT));
		} else if (accnType.equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {
			ctPymnt.setCtpId(CkUtil.generateIdSynch(CkCtPayment.PREFIX_CO_CT_PYMNT));
		} else if (accnType.equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
			ctPymnt.setCtpId(CkUtil.generateIdSynch(CkCtPayment.PREFIX_SP_CT_PYMNT));
		}

		ctPymnt.setTCkPaymentTxn(txn);
		TMstCurrency mstCurr = new TMstCurrency();
		mstCurr.setCcyCode(invDetails.getInvCurrency());
		ctPymnt.setTMstCurrency(mstCurr);
		if (ckPaymentTypes == CkPaymentTypes.OUTBOUND) {
			ctPymnt.setCtpRef(invDetails.getId());
		} else {
			//since invoice no. is already generated after payout
			ctPymnt.setCtpRef(invDetails.getInvNo());
		}
			
		ctPymnt.setCtpJob(doJobId);
		ctPymnt.setCtpItem(invDetails.getInvDesc());
		ctPymnt.setCtpAttach(invDetails.getFileLocation());
		ctPymnt.setCtpQty((short) 1);
		// just added tax in the calculation in case later it will be added, although
		// this one is always 0 for now as it is included in the inv amount already.
		ctPymnt.setCtpAmount(invDetails.getInvAmt());

		ctPymnt.setCtpStatus(RecordStatus.ACTIVE.getCode());
		ctPymnt.setCtpState(JobPaymentStates.NEW.name());
		ctPymnt.setCtpDtCreate(now);
		ctPymnt.setCtpUidCreate(principal.getUserId());
		ctPymnt.setCtpDtLupd(now);
		ctPymnt.setCtpUidLupd(principal.getUserId());
		dao.add(ctPymnt);

		// Retrieve from db to commit
		TCkCtPayment ctPaymentE = dao.find(ctPymnt.getCtpId());
		return this.initEnity(ctPaymentE);

	}

	/**
	 * Returns a list of job IDs that already have a payment record or already
	 * subject for payment. This is to prompt the user that the selected job ID
	 * might have already been selected for payment by other user.
	 */
	public List<String> concurrentCheckJobsForPayment(List<String> jobIds) throws Exception {
		if (jobIds == null)
			return null;

		String hql = "from TCkCtPayment o where o.ctpJob in (:jobIds) and o.ctpStatus=:status and o.ctpState in (:ctPaymentState)";
		Map<String, Object> params = new HashMap<>();
		params.put("jobIds", jobIds);
		params.put("ctPaymentState", Arrays.asList(JobPaymentStates.NEW.name(), JobPaymentStates.PAYING.name()));
		params.put("status", RecordStatus.ACTIVE.getCode());

		List<String> jobIdsList = new ArrayList<>();
		List<TCkCtPayment> ctPaymentList = dao.getByQuery(hql, params);
		if (ctPaymentList != null && ctPaymentList.size() > 0) {
			for (TCkCtPayment pm : ctPaymentList) {
				jobIdsList.add(pm.getCtpJob());
			}
		}

		return jobIdsList;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateCtPaymentByTxn(String txnId, JobPaymentStates state) throws Exception {
		if (StringUtils.isBlank(txnId))
			throw new ParameterException("param txnId null or empty");

		if (state == null)
			throw new ParameterException("param state null");

		String hql = "from TCkCtPayment o where o.TCkPaymentTxn.ptxId=:txnId and o.ctpStatus=:status and o.ctpState in (:states)";
		Map<String, Object> params = new HashMap<>();
		params.put("txnId", txnId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		params.put("states", Arrays.asList(JobPaymentStates.NEW.name(), JobPaymentStates.PENDING.getAltCode()));

		List<TCkCtPayment> list = dao.getByQuery(hql, params);
		if (list != null && list.size() > 0) {
			Date now = new Date();
			for (TCkCtPayment cp : list) {
				cp.setCtpState(state.getAltCode());
				cp.setCtpDtLupd(now);
				cp.setCtpUidLupd("SYS");
				dao.update(cp);
			}
		}

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<TCkCtPayment> getCtPaymentByTxn(String txnId) throws Exception {
		if (StringUtils.isBlank(txnId))
			throw new ParameterException("param txnId null or empty");

		String hql = "from TCkCtPayment o where o.TCkPaymentTxn.ptxId=:txnId and o.ctpStatus=:status and o.ctpState != :state";
		Map<String, Object> params = new HashMap<>();
		params.put("txnId", txnId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		params.put("state", JobPaymentStates.CANCELLED.getAltCode());

		List<TCkCtPayment> list = dao.getByQuery(hql, params);
		return list;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public TCkCtPayment getByRef(String ref) throws Exception {
		if (StringUtils.isBlank(ref))
			throw new ParameterException("param ref null or empty");

		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtPayment.class);
		criteria.add(Restrictions.eq("ctpRef", ref));
		criteria.add(Restrictions.eq("ctpStatus", RecordStatus.ACTIVE.getCode()));
		return dao.getOne(criteria);

	}

}
