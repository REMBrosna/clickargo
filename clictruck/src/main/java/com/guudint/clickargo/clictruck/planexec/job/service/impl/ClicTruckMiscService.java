package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.accnconfigex.service.ClictruckAccnConfigExService;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.guudint.clickargo.clictruck.common.dto.CkCtLocation;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote.DebitNoteStates;
import com.guudint.clickargo.clictruck.finacing.constant.FinancingConstants.FinancingTypes;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.planexec.job.dto.AttachJson;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkAttachJson;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.CkCtTripWorkflowServiceImpl.TripStatus;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripService;
import com.guudint.clickargo.common.CKCountryConfig;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dao.CkAccnDao;
import com.guudint.clickargo.common.dao.CkAccnOpmDao;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.model.TCkAccn;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.dto.CkJobAttach;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.job.model.TCkJobAttach;
import com.guudint.clickargo.job.service.impl.CkJobAttachService;
import com.guudint.clickargo.master.dao.CoreAccnConfigDao;
import com.guudint.clickargo.master.dao.CoreAccnDao;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.AttachmentTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccnConfig;
import com.vcc.camelone.ccm.model.TCoreAccnConfigId;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.master.dto.MstAccnType;
import com.vcc.camelone.util.PrincipalUtilService;

@Service
public class ClicTruckMiscService {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(ClicTruckMiscService.class);
	private final String acfgKey = "FINANCE_OPTIONS";
	private static final String FINANCE = "FINANCE";
	private static final String NON_FINANCE = "NON_FINANCE";
	private static final String FINANCE_EXT = "FINANCE_EXT";

	@Autowired
	private CkJobAttachService jobAttachService;

	@Autowired
	protected IEntityService<TCkJob, String, CkJob> ckJobService;

	@Autowired
	protected GenericDao<TCkCtRateTable, String> ckCtRateTable;

	@Autowired
	protected GenericDao<TCkCtLocation, String> ckCtLocation;

	@Autowired
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@Autowired
	private PrincipalUtilService principalUtilService;

	@Autowired
	private CkJobTruckService jobTruckService;

	@Autowired
	@Qualifier("ckCtDebitNoteDao")
	private GenericDao<TCkCtDebitNote, String> ckCtDebitNoteDao;

	@Autowired
	@Qualifier("ckCtTripDao")
	private GenericDao<TCkCtTrip, String> ckCtTripDao;

	@Autowired
	@Qualifier("ckCtTripAttachDao")
	private GenericDao<TCkCtTripAttach, String> ckCtTripAttachDao;

	@Autowired
	private CoreAccnConfigDao coreAccnConfigDao;

	@Autowired
	private CoreAccnDao coreAccnDao;

	@Autowired
	private CkCtTripService tripService;

	@Autowired
	private CkAccnOpmDao accnOpmDao;

	@Autowired
	private CkAccnDao ckAccnDao;

	@Autowired
	@Qualifier("clictruckAccnConfigExService")
	private ClictruckAccnConfigExService clictruckAccnConfigExtService;

	/**
	 * Load the truck operators associated with this principal cargo owner or
	 * freight forwarder.
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CoreAccn> getTruckOperatorsByCoFf(Principal principal) throws ParameterException, Exception {
		LOG.debug("getTruckOperatorsByCoFf");
		try {

			if (principal == null)
				throw new ParameterException("param principal empty or null");

			SimpleDateFormat tfDate = new SimpleDateFormat("yyyy-MM-dd");
			List<CoreAccn> truckOpsList = new ArrayList<>();
			String hql = "FROM TCkCtRateTable o WHERE o.TCoreAccnByRtCoFf.accnId = :accnId AND o.rtStatus = :rtStatus"
					+ " AND :now BETWEEN DATE_FORMAT(o.rtDtStart, '%Y-%m-%d') AND DATE_FORMAT(o.rtDtEnd, '%Y-%m-%d')";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("accnId", principal.getCoreAccn().getAccnId());
			params.put("rtStatus", RecordStatus.ACTIVE.getCode());
			params.put("now", tfDate.format(Calendar.getInstance().getTime()));

			List<TCkCtRateTable> listRateTable = ckCtRateTable.getByQuery(hql, params);
			if (listRateTable != null && listRateTable.size() > 0) {
				for (TCkCtRateTable c : listRateTable) {
					Hibernate.initialize(c.getTCoreAccnByRtCompany());
					TCoreAccn accn = c.getTCoreAccnByRtCompany();
					truckOpsList.add(new CoreAccn(accn));
				}
			}

			return truckOpsList.stream()
					.collect(Collectors.toConcurrentMap(CoreAccn::getAccnId, Function.identity(), (p, q) -> p)).values()
					.stream().sorted(Comparator.comparing(CoreAccn::getAccnName)).collect(Collectors.toList());

		} catch (Exception ex) {
			LOG.error("getTruckOperatorsByCoFf", ex);
			throw ex;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtLocation> getLocationsByTruckOperator(Principal principal, String accnId)
			throws ParameterException, Exception {
		LOG.debug("getLocationsByTruckOperator");
		try {

			if (principal == null)
				throw new ParameterException("param principal empty or null");

			SimpleDateFormat tfDate = new SimpleDateFormat("yyyy-MM-dd");
			List<CkCtLocation> locsList = new ArrayList<>();
			String hql = "FROM TCkCtLocation o WHERE o.TCoreAccn.accnId = :accnId AND o.locStatus = :locStatus"
					+ " AND :now BETWEEN DATE_FORMAT(o.locDtStart, '%Y-%m-%d') AND DATE_FORMAT(o.locDtEnd, '%Y-%m-%d')";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("accnId", accnId);
			params.put("locStatus", RecordStatus.ACTIVE.getCode());
			params.put("now", tfDate.format(Calendar.getInstance().getTime()));

			List<TCkCtLocation> listLocations = ckCtLocation.getByQuery(hql, params);
			if (listLocations != null && listLocations.size() > 0) {
				for (TCkCtLocation c : listLocations) {
					Hibernate.initialize(c.getTCoreAccn());
					Hibernate.initialize(c.getTCkCtMstLocationType());
					locsList.add(new CkCtLocation(c));
				}
			}
			return locsList.stream().sorted(Comparator.comparing(CkCtLocation::getLocName))
					.collect(Collectors.toList());

		} catch (Exception ex) {
			LOG.error("getLocationsByTruckOperator", ex);
			throw ex;
		}

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobAttach getAttachment(AttachmentTypes attachmentType, String ckJobId)
			throws ParameterException, EntityNotFoundException, Exception {
		LOG.debug("getAttachment");
		try {

			if (StringUtils.isBlank(ckJobId))
				throw new ParameterException("param ckJobId null or empty");

			if (attachmentType == null)
				throw new ParameterException("param attachmentType is null");

			// Retrieve the t_ck_job just to make sure that the job is really existing
			CkJob ckJob = ckJobService.findById(ckJobId);
			if (ckJob == null)
				throw new EntityNotFoundException("ckJob " + ckJobId + " not foundd");

			// Query from the jobAttach
			StringBuilder hql = new StringBuilder("FROM TCkJobAttach o WHERE o.attStatus=:attStatus");
			hql.append(" AND o.TMstAttType.mattId=:attType");
			hql.append(" AND o.TCkJob.jobId=:jobId");
			Map<String, Object> params = new HashMap<>();
			params.put("attStatus", RecordStatus.ACTIVE.getCode());
			params.put("attType", attachmentType.getId());
			params.put("jobId", ckJob.getJobId());

			// is it only expecting one record if we based on attachment type?
			List<TCkJobAttach> listJobAtt = jobAttachService.getDao().getByQuery(hql.toString(), params);
			if (listJobAtt != null && listJobAtt.size() > 0) {
				TCkJobAttach att = listJobAtt.get(0);
				return jobAttachService.dtoFromEntity(att, true);
			}
		} catch (Exception ex) {
			LOG.error("getAttachment", ex);
			throw ex;
		}

		return null;
	}

	/**
	 * Retrieve the trucking operators order by highest payable amount.
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CoreAccn> getTruckingOperatorsWithJob() throws Exception {

		List<CoreAccn> listAccn = new ArrayList<>();
		List<String> listAccnId = new ArrayList<>();
		String hql = "from TCkJobTruck o where o.TCkJob.TCkMstJobState.jbstId=:apvState and o.jobStatus=:status and o.jobOutPaymentState=:outPaymentState";
		Map<String, Object> params = new HashMap<>();
		params.put("apvState", JobStates.APP.name());
		params.put("status", RecordStatus.ACTIVE.getCode());
		params.put("outPaymentState", JobPaymentStates.NEW.name());
		List<TCkJobTruck> jobTruckList = ckJobTruckDao.getByQuery(hql, params);
		if (jobTruckList != null && jobTruckList.size() > 0) {
			for (TCkJobTruck jt : jobTruckList) {
				TCoreAccn toAccn = jt.getTCoreAccnByJobPartyTo();
				if (!listAccnId.contains(toAccn.getAccnId())) {
					listAccnId.add(toAccn.getAccnId());
					listAccn.add(new CoreAccn(toAccn));
				}
			}
		}

		return listAccn;
	}

	/**
	 * Retrieve the Job Truck, status is APP_BILL, JOB_STATUS is A,
	 * T_CK_RECORD_DATE.RCD_DT_APPROVED is yesterday
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<TCkJobTruck> getJobTruckList(Date beginApproveTime, Date endApproveTime) throws Exception {

		String hql = "from TCkJobTruck o where o.TCkJob.TCkMstJobState.jbstId=:apvState and o.jobStatus=:status "
				+ " and o.TCkJob.TCkRecordDate.rcdDtBillApproved >= :beginApproveTime and o.TCkJob.TCkRecordDate.rcdDtBillApproved < :endApproveTime "
				+ " order by o.TCkJob.TCkRecordDate.rcdDtBillApproved asc";

		Map<String, Object> params = new HashMap<>();
		params.put("apvState", JobStates.APP_BILL.name());
		params.put("status", RecordStatus.ACTIVE.getCode());
		params.put("beginApproveTime", beginApproveTime);
		params.put("endApproveTime", endApproveTime);

		List<TCkJobTruck> jobTruckList = ckJobTruckDao.getByQuery(hql, params);

		return jobTruckList;
	}

	/**
	 * Retrieves the lists of trucking operators that GLI has payables to.
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CoreAccn> getTruckingOperatorsWithPayables() throws Exception {
		Principal principal = principalUtilService.getPrincipal();
		if (principal == null)
			throw new ProcessingException("principal null");

		List<CoreAccn> listAccn = new ArrayList<>();
		// just place holder for uniqe accnId
		List<String> listAccnId = new ArrayList<>();
		String hql = "from TCkCtDebitNote o where o.TCoreAccnByDnTo.accnId=:gliAccn and o.dnStatus=:recStatus and o.TCkCtMstDebitNoteState.dnstId=:dnState order by o.dnTotal desc";
		Map<String, Object> params = new HashMap<>();
		params.put("gliAccn", principal.getCoreAccn().getAccnId());
		params.put("recStatus", RecordStatus.ACTIVE.getCode());
		params.put("dnState", DebitNoteStates.NEW.name());
		List<TCkCtDebitNote> dnList = ckCtDebitNoteDao.getByQuery(hql, params);
		if (dnList != null && dnList.size() > 0) {
			for (TCkCtDebitNote dn : dnList) {
				TCoreAccn dnAccnFrom = dn.getTCoreAccnByDnFrom();
				if (!listAccnId.contains(dnAccnFrom.getAccnId())) {
					listAccnId.add(dnAccnFrom.getAccnId());
					listAccn.add(new CoreAccn(dnAccnFrom));
				}
			}
		}

		return listAccn;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public ResponseEntity<Object> updateJobTruckByAction(JobActions jobAction, String jobId, String remarks) {

		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			if (StringUtils.isBlank(jobId))
				throw new ParameterException("param jobId null or empty");
			if (StringUtils.isBlank(remarks))
				throw new ParameterException("param remarks null or empty");
			if (jobAction == null)
				throw new ParameterException("param jobAction is null");

			CkJobTruck dto = jobTruckService.findById(jobId);
			if (dto == null)
				throw new ProcessingException("job truck id " + jobId + " not found");

			if (jobAction == JobActions.REJECT) {
				dto.setRejectRemarks(remarks);
				dto.setJobRemarks(remarks);
				dto.setAction(jobAction);
			}

			return ResponseEntity.ok(jobTruckService.update(dto, principalUtilService.getPrincipal()));
		} catch (ValidationException ex) {
			serviceStatus.setData(remarks);
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * This method set isJobFinanced flag in CkJobTruck based on TCoreAccnConfig
	 * that has been setup on account creation
	 * 
	 * @param dto
	 * @throws Exception
	 */
	@Transactional
	public void checkAndSetFinanceOptions(CkJobTruck dto) throws Exception {
		LOG.debug("checkAndSetFinanceOptions");

		if (null == dto)
			throw new ParameterException("param dto null");

		Optional<TCoreAccn> opAccnCoFf = Optional
				.ofNullable(coreAccnDao.find(dto.getTCoreAccnByJobPartyCoFf().getAccnId()));

		if (!opAccnCoFf.isPresent())
			throw new ParameterException("param account CO/FF null");

		Hibernate.initialize(opAccnCoFf.get().getTMstAccnType());

		CoreAccn coreAccn = new CoreAccn(opAccnCoFf.get());
		coreAccn.setTMstAccnType(new MstAccnType(opAccnCoFf.get().getTMstAccnType()));

		if (null != coreAccn && (coreAccn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())
				|| coreAccn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name()))) {
			TCoreAccnConfigId tCoreAccnConfigId = new TCoreAccnConfigId();
			tCoreAccnConfigId.setAcfgKey(acfgKey);
			tCoreAccnConfigId.setAcfgAccnid(opAccnCoFf.get().getAccnId());

			Optional<TCoreAccnConfig> opCoreAccnConfig = Optional.ofNullable(coreAccnConfigDao.find(tCoreAccnConfigId));

			if (opCoreAccnConfig.isPresent() && StringUtils.isNotBlank(opCoreAccnConfig.get().getAcfgVal())) {
				switch (opCoreAccnConfig.get().getAcfgVal()) {
				case FINANCE:
					dto.setJobIsFinanced('F');
					break;
				case NON_FINANCE:
					dto.setJobIsFinanced('N');
					break;
				case FINANCE_EXT:
					dto.setJobIsFinanced('E');
					break;
				default:
					dto.setJobIsFinanced('F');
					break;
				}
			} else {
				
				CKCountryConfig ctryCfg = clictruckAccnConfigExtService.getCtryEnv();
				if(ctryCfg != null && ctryCfg.getCountry().equalsIgnoreCase("SG")) {
					dto.setJobIsFinanced('N');
				}  else {
					// Set default to F if it's INDO
					dto.setJobIsFinanced('F');
				}
				
			}
		}
	}

	@Transactional
	public void checkAndSetFinanceOptions(CkJobTruck dto, boolean isOpmEnabled) throws Exception {
		LOG.debug("checkAndSetFinanceOptions");

		if (null == dto)
			throw new ParameterException("param dto null");

		if (isOpmEnabled) {

			// During job creation, only one should have OPM but we check below because we
			// don't have the other
			// way to check if it's CO/FF or TO that has OPM. There is no special reason why
			// we check for CO/FF first.
			boolean isCoOpm = false;
			if (dto.getTCoreAccnByJobPartyCoFf() != null) {
				isCoOpm = accnOpmDao.findByAccnId(dto.getTCoreAccnByJobPartyCoFf().getAccnId(),
						RecordStatus.ACTIVE.getCode()) == null ? false : true;
			}

			boolean isToOpm = false;
			if (dto.getTCoreAccnByJobPartyTo() != null) {
				isToOpm = accnOpmDao.findByAccnId(dto.getTCoreAccnByJobPartyTo().getAccnId(),
						RecordStatus.ACTIVE.getCode()) == null ? false : true;
			}

			TCkAccn ckAccn = null;
			// Check if CO/FF is OPM. If true, set the financeOption column
			if (isCoOpm) {
				// Retrieve details from T_CK_ACCN
				ckAccn = ckAccnDao.findByAccnId(dto.getTCoreAccnByJobPartyCoFf().getAccnId(),
						RecordStatus.ACTIVE.getCode());

			} else if (isToOpm) {
				// otherwise, check if it's the TO is OPM and retrieve from T_CK_ACCN
				ckAccn = ckAccnDao.findByAccnId(dto.getTCoreAccnByJobPartyTo().getAccnId(),
						RecordStatus.ACTIVE.getCode());
			}

			if (ckAccn == null) {
				// if both CO/TO not in OPM, get the details of the co/ff
				ckAccn = ckAccnDao.findByAccnId(dto.getTCoreAccnByJobPartyCoFf().getAccnId(),
						RecordStatus.ACTIVE.getCode());
				if (ckAccn != null) {
					dto.setJobFinanceOpt(ckAccn.getCaccnFinancingType());
					dto.setJobFinancer(null);
				} else {
					// Check the country clictruck is deployed if it's SG it's NF, otherwise it's BC
					// by default
					CKCountryConfig ctryCfg = clictruckAccnConfigExtService.getCtryEnv();
					if (ctryCfg != null && ctryCfg.getCountry().equalsIgnoreCase("SG")) {
						dto.setJobFinanceOpt(FinancingTypes.NF.name());
					} else {
						dto.setJobFinanceOpt(FinancingTypes.BC.name());
					}

					dto.setJobFinancer(null);
				}
			} else {
				dto.setJobFinanceOpt(ckAccn.getCaccnFinancingType());
				dto.setJobFinancer(ckAccn.getCaccnFinancer());
			}

			dto.setJobIsFinanced('F');

		} else {
			// reset two values below
			dto.setJobFinanceOpt(null);
			dto.setJobFinancer(null);
			checkAndSetFinanceOptions(dto);
		}

	}

	/**
	 * This methods retrieves the list of trip attachment associated with a truck
	 * job
	 * 
	 * @param truckJobId
	 * @return
	 * @throws ProcessingException
	 * @throws EntityNotFoundException
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkAttachJson> getTripListByJobId(String truckJobId)
			throws ProcessingException, EntityNotFoundException, Exception {
		LOG.info("getTripListByJobId");

		if (StringUtils.isEmpty(truckJobId))
			throw new ParameterException("param truckJobId null or empty");

		// Return empty list for new truckJob
		List<CkAttachJson> attachJsonList = new ArrayList<CkAttachJson>();
		if (StringUtils.equals(truckJobId, ICkConstant.DASH))
			return attachJsonList;

		DetachedCriteria crit = DetachedCriteria.forClass(TCkCtTrip.class);
		crit.add(Restrictions.in("trStatus",
				new Character[] { TripStatus.M_ACTIVE.getStatusCode(), TripStatus.M_DELIVERED.getStatusCode(),
						TripStatus.M_PICKED_UP.getStatusCode(), TripStatus.DLV.getStatusCode() }));
		crit.add(Restrictions.eq("TCkJobTruck.jobId", truckJobId));

		List<TCkCtTrip> trips = ckCtTripDao.getByCriteria(crit);
		List<String> tripIds = new ArrayList<String>();
		if (trips.size() > 0) {
			for (TCkCtTrip trip : trips) {
				tripIds.add(trip.getTrId());
			}
		}

		Set<String> tripSet = new HashSet<>();
		Set<CkAttachJson> attchSet = getInitAttachSet(truckJobId, tripSet);
		if (!tripIds.isEmpty()) {
			DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtTripAttach.class);
			criteria.add(Restrictions.eq("atStatus", RecordStatus.ACTIVE.getCode()));
			criteria.add(Restrictions.in("TCkCtTrip.trId", tripIds));
			criteria.addOrder(Order.asc("TCkCtMstTripAttachType.atypId"));


		try {
			List<TCkCtTripAttach> attachList = ckCtTripAttachDao.getByCriteria(criteria);
			if (attachList != null && attachList.size() > 0) {
				for (TCkCtTripAttach att : attachList) {
					String tripId = att.getTCkCtTrip().getTrId();
					CkCtTrip ckCtTrip = tripService.findById(tripId);
					AttachJson json = Dto2Json(att, false);

					if (tripSet.add(tripId)) {
						CkAttachJson pJson = new CkAttachJson();
						pJson.setRow(json);
						attchSet.add(pJson);

					} else {
						// get the object with the same tripId and add in to the subRow
						attchSet.forEach(e -> {
							if (att.getTCkCtTrip().getTrId().equalsIgnoreCase(e.getRow().getTripId())
									&& StringUtils.isEmpty(e.getRow().getId())) {
								LOG.info("Inserting to row...");
								try {
									e.getRow().setId(att.getAtId());
									e.getRow().setTripId(att.getTCkCtTrip().getTrId());
									e.getRow().setTCkCtTrip(ckCtTrip);
									e.getRow().setFileType(att.getTCkCtMstTripAttachType().getAtypDesc());
									e.getRow().setFileName(att.getAtName());
									e.getRow().setStatus(att.getAtStatus());
									e.getRow().setCreatedAt(att.getAtDtCreate());
								} catch (Exception ex) {
									LOG.error("findAttGroupByFileType", ex);
								}

							} else {
								LOG.info("Inserting to subRow");
								if (att.getTCkCtTrip().getTrId().equalsIgnoreCase(e.getRow().getTripId())) {

									if (e.getSubRow() == null) {
										e.setSubRow(new ArrayList<>());
									}
									e.getSubRow().add(json);
								}
							}
						});
					}
				}
			}

			attachJsonList = attchSet.stream().sorted((o1, o2) -> {
				if (o1.getRow().getSeq() > o2.getRow().getSeq())
					return 1;
				else
					return -1;

			}).collect(Collectors.toList());

			return attachJsonList;

		} catch (ProcessingException ex) {
			LOG.error("getTripListByJobId", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getTripListByJobId", ex);
			throw ex;
		}
	}else {
			LOG.info("No trips found for truckJobId: " + truckJobId);
			return attachJsonList;
		}
}
	// Helper Methods
	//////////////////
	/**
	 * 
	 * @param truckJobId
	 * @param tripSet
	 * @return
	 * @throws Exception
	 */
	private Set<CkAttachJson> getInitAttachSet(String truckJobId, Set<String> tripSet) throws Exception {

		Set<CkAttachJson> attchSet = new HashSet<>();
		Set<CkCtTrip> tripList = tripService.findTripsByTruckJobId(truckJobId,
				Arrays.asList(RecordStatus.ACTIVE.getCode()));
		if (tripList != null && tripList.size() > 0) {
			int index = 1;
			for (CkCtTrip d : tripList) {
				try {
					AttachJson json = new AttachJson();
					json.setTripId(d.getTrId());
					json.setLocFrom(d.getTCkCtTripLocationByTrFrom() != null
							? d.getTCkCtTripLocationByTrFrom().getTCkCtLocation().getLocName()
							: null);
					json.setLocTo(d.getTCkCtTripLocationByTrTo() != null
							? d.getTCkCtTripLocationByTrTo().getTCkCtLocation().getLocName()
							: null);
					json.setSeq(index++);

					String tripId = d.getTrId();
					if (tripSet.add(tripId)) {
						CkAttachJson pJson = new CkAttachJson();
						pJson.setRow(json);
						attchSet.add(pJson);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return attchSet;
	}

	/**
	 * 
	 * @param coreAttach
	 * @param isWithData
	 * @return
	 * @throws ParameterException
	 * @throws EntityNotFoundException
	 * @throws ProcessingException
	 * @throws Exception
	 */
	public AttachJson Dto2Json(TCkCtTripAttach coreAttach, boolean isWithData)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		LOG.debug("Dto2Json");

		if (null == coreAttach)
			throw new ParameterException("param coreAttach null");

		AttachJson attach = new AttachJson();

		try {

			attach.setId(coreAttach.getAtId());
			attach.setTripId(coreAttach.getTCkCtTrip().getTrId());
			attach.setFileType(coreAttach.getTCkCtMstTripAttachType().getAtypDesc());
			attach.setFileName(coreAttach.getAtName());
			attach.setCreatedAt(coreAttach.getAtDtCreate());
			attach.setStatus(coreAttach.getAtStatus());
			attach.setTCkCtTrip(null != coreAttach.getTCkCtTrip() ? new CkCtTrip(coreAttach.getTCkCtTrip()) : null);

			attach.setLocFrom(coreAttach.getTCkCtTrip().getTCkCtTripLocationByTrFrom().getTCkCtLocation().getLocName());
			attach.setLocTo(coreAttach.getTCkCtTrip().getTCkCtTripLocationByTrTo().getTCkCtLocation().getLocName());

			return attach;
//		} catch (EntityNotFoundException ex) {
//			LOG.error("Dto2Json", ex);
//			throw ex;
//
//		} catch (ProcessingException ex) {
//			LOG.error("Dto2Json", ex);
//			throw ex;

		} catch (Exception ex) {
			LOG.error("Dto2Json", ex);
			throw ex;
		}
	}

}
