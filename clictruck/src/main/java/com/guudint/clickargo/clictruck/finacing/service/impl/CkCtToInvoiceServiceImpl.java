package com.guudint.clickargo.clictruck.finacing.service.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.finacing.constant.CkCtToInvoiceConstant;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtToInvoiceDao;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.dto.TripCharges;
import com.guudint.clickargo.clictruck.finacing.dto.TripDoDetail;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.service.CkCtToInvoiceService;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstToInvoiceState;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstToInvoiceState;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.locale.dto.CoreMstLocale;

public class CkCtToInvoiceServiceImpl extends AbstractClickCargoEntityService<TCkCtToInvoice, String, CkCtToInvoice>
		implements CkCtToInvoiceService {

	@Autowired
	private CkCtToInvoiceDao toInvoiceDao;

	@Autowired
	private CkCtTripDoDao ckCtTripDoDao;
	@Autowired
	private CkCtToInvoiceDao ckCtToInvoiceDao;

	@Autowired
	@Qualifier("ckCtTripService")
	private IEntityService<TCkCtTrip, String, CkCtTrip> ckCtTripService;

	private static Logger LOG = Logger.getLogger(CkCtToInvoiceServiceImpl.class);

	public CkCtToInvoiceServiceImpl() {
		super(CkCtToInvoiceConstant.Table.NAME_DAO, CkCtToInvoiceConstant.Prefix.AUDIT_TAG,
				CkCtToInvoiceConstant.Table.NAME_ENTITY, CkCtToInvoiceConstant.Table.NAME);
	}

	@Override
	public CkCtToInvoice newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.info("newObj");
		CkCtToInvoice ckCtToInvoice = new CkCtToInvoice();
		ckCtToInvoice.setTCkCtMstToInvoiceState(new CkCtMstToInvoiceState());
		ckCtToInvoice.setTCoreAccnByInvTo(new CoreAccn());
		ckCtToInvoice.setTCoreAccnByInvFrom(new CoreAccn());
		// Fixed NPE when creating invoice
		ckCtToInvoice.setInvNo(StringUtils.EMPTY);
		return ckCtToInvoice;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtToInvoice deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("deleteById");
		if (StringUtils.isBlank(id)) {
			throw new ParameterException("param id null or empty");
		}
		if (principal == null) {
			throw new ParameterException("param principal null or empty");
		}
		try {
			TCkCtToInvoice tCkCtToInvoice = dao.find(id);
			if (tCkCtToInvoice == null) {
				throw new EntityNotFoundException("id::" + id);
			}
			updateEntityStatus(tCkCtToInvoice, RecordStatus.INACTIVE.getCode());
			updateEntity(ACTION.MODIFY, tCkCtToInvoice, principal, new Date());
			CkCtToInvoice ckCtToInvoice = dtoFromEntity(tCkCtToInvoice);
			this.delete(ckCtToInvoice, principal);
			return ckCtToInvoice;
		} catch (Exception e) {
			LOG.error(e);
			throw new ProcessingException(e);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtToInvoice> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.info("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtToInvoice ckCtToInvoice = whereDto(filterRequest);
		filterRequest.setTotalRecords(countByAnd(ckCtToInvoice));
		String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
		List<TCkCtToInvoice> tCkCtToInvoices = findEntitiesByAnd(ckCtToInvoice, "from TCkCtToInvoice o", orderByClause,
				filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
		List<CkCtToInvoice> ckCtToInvoices = new ArrayList<>();
		for (TCkCtToInvoice tCkCtToInvoice : tCkCtToInvoices) {
			CkCtToInvoice dto = dtoFromEntity(tCkCtToInvoice);
			ckCtToInvoices.add(dto);
		}
		return ckCtToInvoices;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtToInvoice findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.info("findById");
		if (StringUtils.isBlank(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtToInvoice tCkCtToInvoice = dao.find(id);
			if (tCkCtToInvoice == null) {
				throw new EntityNotFoundException("id::" + id);
			}
			initEnity(tCkCtToInvoice);
			return dtoFromEntity(tCkCtToInvoice);
		} catch (Exception e) {
			LOG.error(e);
			throw new ProcessingException(e);
		}
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected void initBusinessValidator() {

	}

	@Override
	public CkCtToInvoice dtoFromEntity(TCkCtToInvoice tCkCtToInvoice)
			throws ParameterException, ProcessingException {
		LOG.info("dtoFromEntity");
		if (tCkCtToInvoice == null) {
			throw new ParameterException("param entity null");
		}
		CkCtToInvoice ckCtToInvoice = new CkCtToInvoice(tCkCtToInvoice);

		Optional<TCkCtMstToInvoiceState> opInvState = Optional.ofNullable(tCkCtToInvoice.getTCkCtMstToInvoiceState());
		opInvState.ifPresent(e -> ckCtToInvoice.setTCkCtMstToInvoiceState(new CkCtMstToInvoiceState(e)));

		Optional<TCoreAccn> opAccnByInvFrom = Optional.ofNullable(tCkCtToInvoice.getTCoreAccnByInvFrom());
		opAccnByInvFrom.ifPresent(e -> ckCtToInvoice.setTCoreAccnByInvFrom(new CoreAccn(e)));

		Optional<TCoreAccn> opAccnByInvTo = Optional.ofNullable(tCkCtToInvoice.getTCoreAccnByInvTo());
		opAccnByInvTo.ifPresent(e -> ckCtToInvoice.setTCoreAccnByInvTo(new CoreAccn(e)));

		Optional<TCkCtTrip> opTckTrip = Optional.ofNullable(tCkCtToInvoice.getTCkCtTrip());
		opTckTrip.ifPresent(e -> ckCtToInvoice.setTCkCtTrip(new CkCtTrip(e)));


		return ckCtToInvoice;
	}

	@Override
	public TCkCtToInvoice entityFromDTO(CkCtToInvoice ckCtToInvoice) throws ParameterException, ProcessingException {
		LOG.info("entityFromDTO");
		if (ckCtToInvoice == null) {
			throw new ParameterException("param entity null");
		}
		TCkCtToInvoice tCkCtToInvoice = new TCkCtToInvoice(ckCtToInvoice);
		if (ckCtToInvoice.getTCkCtMstToInvoiceState() != null) {
			tCkCtToInvoice.setTCkCtMstToInvoiceState(
					ckCtToInvoice.getTCkCtMstToInvoiceState().toEntity(new TCkCtMstToInvoiceState()));
		}
		if (ckCtToInvoice.getTCoreAccnByInvFrom() != null) {
			tCkCtToInvoice.setTCoreAccnByInvFrom(ckCtToInvoice.getTCoreAccnByInvFrom().toEntity(new TCoreAccn()));
		}
		if (ckCtToInvoice.getTCoreAccnByInvTo() != null) {
			tCkCtToInvoice.setTCoreAccnByInvTo(ckCtToInvoice.getTCoreAccnByInvTo().toEntity(new TCoreAccn()));
		}
		if (ckCtToInvoice.getTCkCtTrip() != null) {
			tCkCtToInvoice.setTCkCtTrip(ckCtToInvoice.getTCkCtTrip().toEntity(new TCkCtTrip()));
		}
		saveOrUpdateReceipt(ckCtToInvoice);
		tCkCtToInvoice.setInvName(ckCtToInvoice.getInvName());
		tCkCtToInvoice.setInvLoc(ckCtToInvoice.getInvLoc());
		return tCkCtToInvoice;
	}

	@Override
	protected String entityKeyFromDTO(CkCtToInvoice ckCtToInvoice) throws ParameterException, ProcessingException {
		if (ckCtToInvoice == null) {
			throw new ParameterException("param dto null");
		}
		return ckCtToInvoice.getInvId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtToInvoice ckCtToInvoice)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		if (ckCtToInvoice == null) {
			throw new ParameterException("dto param null");
		}
		if (ckCtToInvoice.getCoreMstLocale() == null) {
			throw new ProcessingException("coreMstLocale null");
		}
		return ckCtToInvoice.getCoreMstLocale();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtToInvoice arg0) throws ParameterException, ProcessingException {
		return new HashMap<>();
	}

	@Override
	protected String getWhereClause(CkCtToInvoice arg0, boolean arg1) throws ParameterException, ProcessingException {
		return "";
	}

	@Override
	protected TCkCtToInvoice initEnity(TCkCtToInvoice tCkCtToInvoice) throws ParameterException, ProcessingException {
		LOG.info("initEntity");
		if (tCkCtToInvoice != null) {
			Hibernate.initialize(tCkCtToInvoice.getTCkCtMstToInvoiceState());
			Hibernate.initialize(tCkCtToInvoice.getTCoreAccnByInvFrom());
			Hibernate.initialize(tCkCtToInvoice.getTCoreAccnByInvTo());
			Hibernate.initialize(tCkCtToInvoice.getTCkCtTrip());
		}
		return tCkCtToInvoice;
	}

	@Override
	protected CkCtToInvoice preSaveUpdateDTO(TCkCtToInvoice tCkCtToInvoice, CkCtToInvoice ckCtToInvoice)
			throws ParameterException, ProcessingException {
		LOG.info("preSaveUpdateDTO");
		if (tCkCtToInvoice == null) {
			throw new ParameterException("param entity null");
		}
		if (ckCtToInvoice == null) {
			throw new ParameterException("param dto null");
		}
		saveOrUpdateReceipt(ckCtToInvoice);
		ckCtToInvoice.setInvDtCreate(tCkCtToInvoice.getInvDtCreate());
		ckCtToInvoice.setInvUidCreate(tCkCtToInvoice.getInvUidCreate());
		return ckCtToInvoice;
	}

	@Override
	protected void preSaveValidation(CkCtToInvoice arg0, Principal arg1)
			throws ParameterException, ProcessingException {

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtToInvoice arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected CkCtToInvoice setCoreMstLocale(CoreMstLocale arg0, CkCtToInvoice arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtToInvoice updateEntity(ACTION action, TCkCtToInvoice entity, Principal principal, Date date)
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
			switch (action) {
			case CREATE:
				entity.setInvUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setInvDtCreate(date);
				entity.setInvUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setInvDtLupd(date);
				break;

			case MODIFY:
				entity.setInvUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setInvDtLupd(date);
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
	protected TCkCtToInvoice updateEntityStatus(TCkCtToInvoice tCkCtToInvoice, char status)
			throws ParameterException, ProcessingException {
		LOG.info("updateEntityStatus");
		if (tCkCtToInvoice == null) {
			throw new ParameterException("entity param null");
		}
		tCkCtToInvoice.setInvStatus(status);
		return tCkCtToInvoice;
	}

	@Override
	protected CkCtToInvoice whereDto(EntityFilterRequest arg0) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'whereDto'");
	}

	private EntityOrderBy formatOrderByObj(EntityOrderBy orderBy) {
		if (orderBy == null) {
			return null;
		}
		if (StringUtils.isEmpty(orderBy.getAttribute())) {
			return null;
		}
		String newAttr = orderBy.getAttribute();
		newAttr = newAttr.replaceAll("tckCtMstToInvoiceState", "TCkCtMstToInvoiceState")
				.replaceAll("tcoreAccnByInvTo", "TCoreAccnByInvTo")
				.replaceAll("tcoreAccnByInvFrom", "TCoreAccnByInvFrom");
		orderBy.setAttribute(newAttr);
		return orderBy;
	}

	private void saveOrUpdateReceipt(CkCtToInvoice ckCtToInvoice) {
		if (StringUtils.isNotBlank(ckCtToInvoice.getBase64File())
				&& StringUtils.isNotBlank(ckCtToInvoice.getInvName())) {
			try {
				byte[] data = Base64.getDecoder().decode(ckCtToInvoice.getBase64File());
			
				String filePath = FileUtil.saveAttachment(ckCtToInvoice.getInvJobId() ,ckCtToInvoice.getInvName(), data);
				ckCtToInvoice.setInvLoc(filePath);
			} catch (Exception e) {
				LOG.error(e);
			}
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Optional<CkCtToInvoice> getByTripId(String tripId)
			throws ParameterException, EntityNotFoundException, Exception {
		try {
			if (StringUtils.isBlank(tripId))
				throw new ParameterException("param tripId null or empty");

			// In this state, adding invoice, the job should already have a trip.
			CkCtTrip ckCtTrip = ckCtTripService.findById(tripId);
			if (ckCtTrip == null)
				throw new EntityNotFoundException("trip entity not found: " + tripId);

			// Get the DO details per trip. At this state, the DO should already been added
			// during start/stop of job.
			List<TCkCtTripDo> tCkCtTripDos = ckCtTripDoDao.findByTripId(ckCtTrip.getTrId());
			// this should only expecting one since one tripDo is per trip
			if (tCkCtTripDos == null || tCkCtTripDos.isEmpty())
				throw new EntityNotFoundException("tripDo not found for trip: " + ckCtTrip.getTrId());

			// Store the trip charges
			TripCharges tripCharges = new TripCharges();

			TCkCtTripDo tripDo = tCkCtTripDos.get(0);
			TripDoDetail tripDoDetail = new TripDoDetail();
			tripDoDetail.setDoNumber(tripDo.getDoNo());
			tripDoDetail.setDoDocument(tripDo.getDoUnsigned());
			tripDoDetail.setPod(tripDo.getDoSigned());

			CkCtToInvoice ckCtToInvoice = new CkCtToInvoice();

			// check if there is an existing invoice for this trip
			List<TCkCtToInvoice> toInvoices = toInvoiceDao.findByTripId(tripId);
			if (toInvoices == null || toInvoices.isEmpty()) {
				ckCtToInvoice.setTCkCtTrip(ckCtTrip);

				CkJobTruck jobTruck = ckCtTrip.getTCkJobTruck();
				ckCtToInvoice.setTCoreAccnByInvFrom(jobTruck.getTCoreAccnByJobPartyTo());
				ckCtToInvoice.setTCoreAccnByInvTo(jobTruck.getTCoreAccnByJobPartyCoFf());

				tripCharges.setAmount(ckCtTrip.getTCkCtTripCharge().getTcPrice());
				tripCharges.setOpenPrice(ckCtTrip.getTrChargeOpen());

			} else {
				// 1 trip = 1 invoice
				TCkCtToInvoice inv = toInvoices.get(0);
				Hibernate.initialize(inv.getTCkCtMstToInvoiceState());
				Hibernate.initialize(inv.getTCkCtTrip());
				Hibernate.initialize(inv.getTCoreAccnByInvFrom());
				Hibernate.initialize(inv.getTCoreAccnByInvTo());

				inv.copyBeanProperties(ckCtToInvoice);
				ckCtToInvoice.setTCkCtMstToInvoiceState(new CkCtMstToInvoiceState(inv.getTCkCtMstToInvoiceState()));
				ckCtToInvoice.setTCkCtTrip(new CkCtTrip(inv.getTCkCtTrip()));
				ckCtToInvoice.setTCoreAccnByInvFrom(new CoreAccn(inv.getTCoreAccnByInvFrom()));
				ckCtToInvoice.setTCoreAccnByInvTo(new CoreAccn(inv.getTCoreAccnByInvTo()));

				tripCharges.setAmount(inv.getTCkCtTrip().getTCkCtTripCharge().getTcPrice());
				tripCharges.setOpenPrice(inv.getTCkCtTrip().getTrChargeOpen());

			}

			ckCtToInvoice.setTripDoDetail(tripDoDetail);
			ckCtToInvoice.setTripCharges(tripCharges);

			return Optional.ofNullable(ckCtToInvoice);
		} catch (Exception e) {
			LOG.error(e);
			throw e;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Object addObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		CkCtToInvoice ckCtToInvoice = (CkCtToInvoice) object;
		try {
			// insert invoice to all trips in job
			Date now = Calendar.getInstance().getTime();
			Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
			// List<TCkCtTrip> tCkCtTrips = ckCtTripDao.findByJobId(dto.getInvJobId());
			// for (TCkCtTrip tCkCtTrip : tCkCtTrips) {
			CkCtTrip tCkCtTrip = ckCtToInvoice.getTCkCtTrip();
			List<TCkCtToInvoice> optCkCtToInvoice = ckCtToInvoiceDao.findByTripId(tCkCtTrip.getTrId());
			if (optCkCtToInvoice.isEmpty()) {
				ckCtToInvoice.setInvId(CkUtil.generateId(CkCtToInvoiceConstant.Prefix.PREFIX_CK_CT_TO_INVOICE));
				// ckCtToInvoice.setTCkCtTrip(tCkCtTrip);
				CkCtMstToInvoiceState invoiceState = new CkCtMstToInvoiceState();
				invoiceState.setInstId("NEW");
				ckCtToInvoice.setTCkCtMstToInvoiceState(invoiceState);
				ckCtToInvoice.setInvUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
				ckCtToInvoice.setInvDtCreate(now);
				ckCtToInvoice.setInvDtLupd(now);
				ckCtToInvoice.setInvUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
				// Fixed NPE when creating invoice when InvNo is not present. Validation is done during Submit only.
				ckCtToInvoice.setInvNo(null != ckCtToInvoice.getInvNo() ? ckCtToInvoice.getInvNo(): StringUtils.EMPTY);
				// ckCtToInvoice.setTCoreAccnByInvFrom(dto.getTCoreAccnByInvFrom());
				// ckCtToInvoice.setTCoreAccnByInvTo(dto.getTCoreAccnByInvTo());
				ckCtToInvoiceDao.add(entityFromDTO(ckCtToInvoice));
				audit(principal, ckCtToInvoice.getInvId(), ACTION.CREATE.toString());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ProcessingException(e);
		}
		return ckCtToInvoice;
	}
	
	@Transactional
	public List<CkCtToInvoice> getListInvoiceByJobId(String jobId) throws Exception {
		LOG.info("getListInvoiceByJobId");
		List<CkCtToInvoice> ckCtToInvoices = new ArrayList<>();
		List<TCkCtToInvoice> tCkCtToInvoices = ckCtToInvoiceDao.findByJobId(jobId);
		if(tCkCtToInvoices!=null && tCkCtToInvoices.size()>0) {
			for(TCkCtToInvoice tCkCtToInvoice : tCkCtToInvoices) {
				ckCtToInvoices.add(dtoFromEntity(tCkCtToInvoice));
			}
		}
		return ckCtToInvoices;
	}
}
