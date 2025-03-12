package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.master.model.*;
import com.vcc.camelone.common.exception.ValidationException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.guudint.clickargo.clictruck.accnconfigex.service.ClictruckAccnConfigExService;
import com.guudint.clickargo.clictruck.admin.ratetable.service.ICkCtTripRateService;
import com.guudint.clickargo.clictruck.common.constant.CkCtLocationConstant.LocationId;
import com.guudint.clickargo.clictruck.common.dao.CkCtDeptUsrDao;
import com.guudint.clickargo.clictruck.common.dto.CkCtDept;
import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtDeptUsr;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtDebitNoteDao;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote.DebitNoteStates;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.finacing.dto.ToInvoiceStates;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoFm;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoMm;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoFm;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoMm;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCharge;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripCargoFmService;
import com.guudint.clickargo.clictruck.track.service.TrackTraceCoordinateService;
import com.guudint.clickargo.common.CKCountryConfig;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dto.CkRecordDate;
import com.guudint.clickargo.job.dao.CkJobRejectDao;
import com.guudint.clickargo.job.dao.CkJobRemarksDao;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.master.dto.CkMstJobState;
import com.guudint.clickargo.master.dto.CkMstShipmentType;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.ShipmentTypes;
import com.guudint.clickargo.master.model.TCkMstCntType;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreContact;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.util.PrincipalUtilService;

/*
 * Utility service for {@code CkJobTruckService} so that the lines of codes won't be long. 
 */
@Service
public class CkJobTruckServiceUtil {

	private static Logger log = Logger.getLogger(CkJobTruckServiceUtil.class);

	public static final String TRIP_CRG_FM_PREFIX = "CTFM";
	public static final String TRIP_PREFIX = "CTTR";
	public static final String TRIPLOC_PREFIX = "CTTL";
	public static final String TRIP_CHARGE_PREFIX = "CTTC";
	public static final String CD_PREFIX = "CTCD";

	@Autowired
	CkCtTripDao ckCtTripDao;

	@Autowired
	@Qualifier("ckCtTripCargoFmDao")
	private GenericDao<TCkCtTripCargoFm, String> ckCtTripCargoFmDao;

	@Autowired
	private GenericDao<TCkCtTripCargoMm, String> ckCtTripCargoMmDao;

	@Autowired
	@Qualifier("ckCtTripChargeDao")
	private GenericDao<TCkCtTripCharge, String> ckCtTripChargeDao;

	@Autowired
	private GenericDao<TCkCtTripLocation, String> ckCtTripLocationDao;

	@Autowired
	private GenericDao<TCkCtLocation, String> ckCtLocationDao;

	@Autowired
	private ICkCtTripRateService tripRateService;

	@Autowired
	@Qualifier("coreUserDao")
	private GenericDao<TCoreUsr, String> coreUserDao;

	@Autowired
	@Qualifier("ckCtContactDetailDao")
	private GenericDao<TCkCtContactDetail, String> ckCtContactDetailDao;

	@Autowired
	private CkCtTripCargoFmService tripFmService;

	@Autowired
	private PrincipalUtilService principalUtilService;

	@Autowired
	private CkJobRejectDao ckJobRejectDao;

	@Autowired
	private CkJobRemarksDao ckJobRemarksDao;

	@Autowired
	@Qualifier("ckJobTruckDao")
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@Autowired
	private CkCtPlatformInvoiceDao platformInvoiceDao;

	@Autowired
	private CkCtDebitNoteDao dDebitNoteDao;
	@Autowired
	@Qualifier("coreSysparamDao")
	private GenericDao<TCoreSysparam, String> sysParamDao;
	@Autowired
	TrackTraceCoordinateService coordinateService;

	@Autowired
	@Qualifier("clictruckAccnConfigExService")
	private ClictruckAccnConfigExService cltAccnConfigExService;

	@Autowired
	private CkCtDeptUsrDao depUsrDao;

	@Autowired
	GenericDao<TCoreAuditlog, String> auditLogDao;

	/**
	 * @param dto
	 * @param principal
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void addOrUpdateTrips(CkJobTruck dto, Principal principal) throws Exception {

		// Trip list from frontend
		List<CkCtTrip> ckCtTrips = dto.getTckCtTripList();

		// Holder for how many trips are processed, so that we can determine if it's fm
		// or mm aside from the shipment type
		List<CkCtTrip> tripsProcessed = new ArrayList<>();

		if (null != ckCtTrips && ckCtTrips.size() > 0) {
			// DELETE CURRENT TRIP LIST
			removeExistingData(dto);

			double totalJobCharge = 0.0;

			for (int i = 0; i < ckCtTrips.size(); i++) {

				CkCtTrip trip = ckCtTrips.get(i);

				if (trip != null) {
					// CREATE NEW TRIP and TRIP LOCATION RECORDS
					TCkCtTrip tripEntity = new TCkCtTrip();
					trip.toEntity(tripEntity);
					tripEntity.setTrId(CkUtil.generateIdSynch(TRIP_PREFIX));
					// just set to pass to create fm
					trip.setTrId(tripEntity.getTrId());

					if (null == tripEntity.getTrSeq()) {
						tripEntity.setTrSeq(i);
					}
					tripEntity.setTrStatus(RecordStatus.ACTIVE.getCode());
					tripEntity.setTrUidCreate(principal.getUserId());
					tripEntity.setTrDtCreate(Calendar.getInstance().getTime());
					tripEntity.setTrUidLupd(principal.getUserId());
					tripEntity.setTrDtLupd(Calendar.getInstance().getTime());
					tripEntity.setTrChargeOpen(null != trip.getTrChargeOpen() ? trip.getTrChargeOpen() : 'N');

					CkCtTripLocation to = trip.getTCkCtTripLocationByTrTo();
					if (null != to && null != to.getTCkCtLocation())
						tripEntity.setTCkCtTripLocationByTrTo(createLocation(to, principal));

					CkCtTripLocation from = trip.getTCkCtTripLocationByTrFrom();
					if (null != from && null != from.getTCkCtLocation())
						tripEntity.setTCkCtTripLocationByTrFrom(createLocation(from, principal));

					CkCtTripLocation depot = trip.getTCkCtTripLocationByTrDepot();
					if (null != depot && null != depot.getTCkCtLocation())
						tripEntity.setTCkCtTripLocationByTrDepot(createLocation(depot, principal));

					tripEntity.setTCkJobTruck(dto.toEntity(new TCkJobTruck()));

					// Re-calculating trip charge in case the field from dto not calculating
					// properly
					// or the user has changed some of the critieria but frontend not updated
					// and submitted the request.
					if (trip.getTCkCtTripCharge() != null) {
						if (trip.getTrChargeOpen() != null && trip.getTrChargeOpen() == 'N') {
							// if the charge open is not checked and trip charge is captured from ui
							// Has bug for MultiDrops
							/*-
							if (to != null && from != null && dto.getTCoreAccnByJobPartyTo() != null
									&& dto.getTCoreAccnByJobPartyCoFf() != null && dto.getTCkCtMstVehType() != null) {
								// make sure all the fields are present
								TripChargeReq tcr = new TripChargeReq(dto.getTCoreAccnByJobPartyTo().getAccnId(),
										dto.getTCoreAccnByJobPartyCoFf().getAccnId(), Currencies.IDR.getCode(),
										from.getTCkCtLocation().getLocId(), to.getTCkCtLocation().getLocId(),
										dto.getTCkCtMstVehType().getVhtyId());
								Optional<CkCtTripRate> tripRate = tripRateService
										.getByTripRateTableAndFromToVehTypeCurr(tcr);
							
								// Only proceed if there is trip rate found
								if (tripRate.isPresent()) {
									if (trip.getTCkCtTripCharge().getTcPrice().doubleValue() != tripRate.get()
											.getTrCharge().doubleValue()) {
										// update dto trip charge with what is found in tcr object
										trip.getTCkCtTripCharge().setTcPrice(tripRate.get().getTrCharge());
									}
								}
							}
							*/
						} else {
							trip.getTCkCtTripCharge().setTcIsOpen('Y');
						}

					}

					// to avoid nullpointer exception
					if (trip.getTCkCtTripCharge() != null) {
						// One trip will only have one trip charge
						TCkCtTripCharge tripCharge = new TCkCtTripCharge();

						trip.getTCkCtTripCharge().copyBeanProperties(tripCharge);
						tripCharge.setTcId(CkUtil.generateIdSynch(TRIP_CHARGE_PREFIX));
						tripCharge.setTcStatus(RecordStatus.ACTIVE.getCode());
						tripCharge.setTcUidCreate(principal.getUserId());
						tripCharge.setTcDtCreate(Calendar.getInstance().getTime());
						tripCharge.setTcUidLupd(principal.getUserId());
						tripCharge.setTcDtLupd(Calendar.getInstance().getTime());
						// TODO: determine where to get
						tripCharge.setTcPlatformFee(new BigDecimal(0.0));
						tripCharge.setTcGovtTax(new BigDecimal(0.0));
						tripCharge.setTcWitholdTax(new BigDecimal(0.0));
						ckCtTripChargeDao.add(tripCharge);

						// Total Job Charge AA [START] - might need to add other fields such as tax
						totalJobCharge += totalJobCharge += tripCharge.getTcPrice() != null
								? tripCharge.getTcPrice().doubleValue()
								: 0.0;

						// Total Job Charge AA [END]

						tripEntity.setTCkCtTripCharge(tripCharge);
					}

					// Finally add trip here
					ckCtTripDao.add(tripEntity);
					tripsProcessed.add(trip);

					// if Domestic
					List<CkCtTripCargoMm> tripCargoMmList = trip.getTripCargoMmList();
					if (tripCargoMmList != null && tripCargoMmList.size() > 0) {
						for (CkCtTripCargoMm mm : tripCargoMmList) {
							this.createMidMileTrip(tripEntity.getTrId(), mm, principal);
						}
					}
				}

			}
			// If existing records are all removed from FE, remove all
		} else if (ObjectUtils.isEmpty(ckCtTrips)) {
			removeExistingData(dto);
		}

		// if itripsProcessed size is 1 then it's FM, otherwise it's MM
		if (tripsProcessed.size() > 1) {

		} else {
			// avoid indexarrayoutofbounds
			if (!tripsProcessed.isEmpty() && tripsProcessed.get(0).getTckCtTripCargoFmList() != null
					&& tripsProcessed.get(0).getTckCtTripCargoFmList().size() > 0) {
				// since only one trip cargo for first mile (fm)
				CkCtTripCargoFm tripFm = tripsProcessed.get(0).getTckCtTripCargoFmList().get(0);
				createFirstMileTrip(tripsProcessed.get(0), tripFm, principal);

			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateTripDateTimeRemark(CkJobTruck dto, Principal principal) throws Exception {

		for (CkCtTrip trip : dto.getTckCtTripList()) {
			updateTripLocation(trip.getTCkCtTripLocationByTrFrom(), principal);
			updateTripLocation(trip.getTCkCtTripLocationByTrTo(), principal);
		}
	}

	/**
	 * @param dto
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void removeExistingData(CkJobTruck dto) throws Exception {
		if (null != dto.getJobId()) {
			DetachedCriteria dc = DetachedCriteria.forClass(TCkCtTrip.class);
			dc.add(Restrictions.eq("TCkJobTruck.jobId", dto.getJobId()));
			List<TCkCtTrip> _tripList = ckCtTripDao.getByCriteria(dc);
			List<String> tripIds = new ArrayList<String>();
			List<String> tripLocs = new ArrayList<String>();
			List<String> tripChrgs = new ArrayList<String>();
			if (null != _tripList && _tripList.size() > 0) {
				for (TCkCtTrip _trip : _tripList) {
					tripIds.add(_trip.getTrId());
					if (null != _trip.getTCkCtTripLocationByTrFrom())
						tripLocs.add(_trip.getTCkCtTripLocationByTrFrom().getTlocId());
					if (null != _trip.getTCkCtTripLocationByTrTo())
						tripLocs.add(_trip.getTCkCtTripLocationByTrTo().getTlocId());
					if (null != _trip.getTCkCtTripLocationByTrDepot())
						tripLocs.add(_trip.getTCkCtTripLocationByTrDepot().getTlocId());
					if (null != _trip.getTCkCtTripCharge())
						tripChrgs.add(_trip.getTCkCtTripCharge().getTcId());
				}
			}

			if (!_tripList.isEmpty()) {
				Map<String, Object> parameters = new HashMap<>();
				parameters.put("tripIds", tripIds);
				if (null != tripIds && tripIds.size() > 0)
					ckCtTripCargoFmDao.executeUpdate(
							"DELETE FROM TCkCtTripCargoFm o WHERE o.TCkCtTrip.trId IN (:tripIds)", parameters);
				parameters.clear();
				parameters.put("tripIds", tripIds);
				if (null != tripIds && tripIds.size() > 0)
					ckCtTripCargoFmDao.executeUpdate(
							"DELETE FROM TCkCtTripCargoMm o WHERE o.TCkCtTrip.trId IN (:tripIds)", parameters);
				parameters.clear();

				// for DSV job, need to delete TCkCtTripDo
				parameters.put("tripIds", tripIds);
				if (null != tripIds && tripIds.size() > 0)
					ckCtTripCargoFmDao.executeUpdate("DELETE FROM TCkCtTripDo o WHERE o.TCkCtTrip.trId IN (:tripIds)",
							parameters);
				parameters.clear();
				//
				parameters.put("tripIds", tripIds);
				if (null != tripIds && tripIds.size() > 0)
					ckCtTripDao.executeUpdate("DELETE FROM TCkCtTrip o WHERE o.trId IN :tripIds", parameters);
				parameters.clear();
				parameters.put("tripChrgs", tripChrgs);
				if (null != tripChrgs && tripChrgs.size() > 0)
					ckCtTripChargeDao.executeUpdate("DELETE FROM TCkCtTripCharge o WHERE o.tcId IN (:tripChrgs)",
							parameters);

				if (tripLocs != null && tripLocs.size() > 0) {
					parameters.clear();
					parameters.put("tripLocs", tripLocs);
					ckCtTripLocationDao.executeUpdate("DELETE FROM TCkCtTripLocation o WHERE o.tlocId IN (:tripLocs)",
							parameters);
				}
				parameters.clear();
			}

		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public TCkCtTripCargoFm createFirstMileTrip(CkCtTrip trip, CkCtTripCargoFm fmDto, Principal principal)
			throws Exception {
		TCkCtTripCargoFm tripCargoFmE = new TCkCtTripCargoFm();
		fmDto.toEntity(tripCargoFmE);
		tripCargoFmE.setCgId(CkUtil.generateIdSynch(TRIP_CRG_FM_PREFIX));
		tripCargoFmE.setCgStatus(RecordStatus.ACTIVE.getCode());
		tripCargoFmE.setCgUidCreate(principal.getUserId());
		tripCargoFmE.setCgDtCreate(Calendar.getInstance().getTime());
		tripCargoFmE.setCgUidLupd(principal.getUserId());
		tripCargoFmE.setCgDtLupd(Calendar.getInstance().getTime());

		if (null != fmDto.getTCkCtMstCargoType() && !StringUtils.isEmpty(fmDto.getTCkCtMstCargoType().getCrtypId())) {
			TCkCtMstCargoType cargoTypeE = new TCkCtMstCargoType();
			fmDto.getTCkCtMstCargoType().toEntity(cargoTypeE);
			tripCargoFmE.setTCkCtMstCargoType(cargoTypeE);
		}
		if (null != fmDto.getTCkMstCntType() && !StringUtils.isEmpty(fmDto.getTCkMstCntType().getCnttId())) {
			TCkMstCntType contTypeE = new TCkMstCntType();
			fmDto.getTCkMstCntType().toEntity(contTypeE);
			tripCargoFmE.setTCkMstCntType(contTypeE);
		}

		TCkCtTrip tckCtTrip = ckCtTripDao.find(trip.getTrId());
		tripCargoFmE.setTCkCtTrip(tckCtTrip);
		ckCtTripCargoFmDao.add(tripCargoFmE);

		return tripCargoFmE;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void createMidMileTrip(String tripId, CkCtTripCargoMm mmDto, Principal principal) throws Exception {

		TCkCtTripCargoMm tripCargommE = new TCkCtTripCargoMm();
		mmDto.toEntity(tripCargommE);

		tripCargommE.setCgId(CkUtil.generateIdSynch(TRIP_CRG_FM_PREFIX));
		tripCargommE.setCgStatus(RecordStatus.ACTIVE.getCode());
		tripCargommE.setCgUidCreate(principal.getUserId());
		tripCargommE.setCgDtCreate(Calendar.getInstance().getTime());
		tripCargommE.setCgUidLupd(principal.getUserId());
		tripCargommE.setCgDtLupd(Calendar.getInstance().getTime());

		tripCargommE.setTCkCtMstCargoType(new TCkCtMstCargoType(mmDto.getTCkCtMstCargoType().getCrtypId(), ""));
		TCkCtMstUomWeight tCkCtMstUomWeight = new TCkCtMstUomWeight();
		if (mmDto.getTCkCtMstUomWeight() != null) {
			tCkCtMstUomWeight.setWeiId(mmDto.getTCkCtMstUomWeight().getWeiId());
			tripCargommE.setTCkCtMstUomWeight(tCkCtMstUomWeight);
		}

		TCkCtMstUomVolume tCkCtMstUomVolume = new TCkCtMstUomVolume();
		if (mmDto.getTCkCtMstUomVolume() != null) {
			tCkCtMstUomVolume.setVolId(mmDto.getTCkCtMstUomVolume().getVolId());
			tripCargommE.setTCkCtMstUomVolume(tCkCtMstUomVolume);
		}

		TCkCtMstUomSize tCkCtMstUomSize = new TCkCtMstUomSize();
		if (mmDto.getTCkCtMstUomSize() != null) {
			tCkCtMstUomSize.setSizId(mmDto.getTCkCtMstUomSize().getSizId());
			tripCargommE.setTCkCtMstUomSize(tCkCtMstUomSize);
		}

		// tripCargommE.setTCkCtTrip(new TCkCtTrip(tripId));
		TCkCtTrip tckCtTrip = ckCtTripDao.find(tripId);
		tripCargommE.setTCkCtTrip(tckCtTrip);

		ckCtTripCargoMmDao.add(tripCargommE);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtContactDetail createContactDetails(Principal principal) throws Exception {
		try {
			TCoreUsr coreUsr = coreUserDao.find(principal.getUserId());
			if (null != coreUsr) {
				TCkCtContactDetail tCkCtContactDetailCoFf = new TCkCtContactDetail();
				tCkCtContactDetailCoFf.setCdId(CkUtil.generateIdSynch(CD_PREFIX));
				tCkCtContactDetailCoFf.setCdStatus(RecordStatus.ACTIVE.getCode());
				tCkCtContactDetailCoFf.setCdName(coreUsr.getUsrName());
				tCkCtContactDetailCoFf.setCdEmail(coreUsr.getUsrContact().getContactEmail());
				tCkCtContactDetailCoFf.setCdPhone(coreUsr.getUsrContact().getContactTel());
				tCkCtContactDetailCoFf.setCdUidCreate(principal.getUserId());
				tCkCtContactDetailCoFf.setCdDtCreate(Calendar.getInstance().getTime());
				tCkCtContactDetailCoFf.setRrUidLupd(principal.getUserId());
				tCkCtContactDetailCoFf.setCdDtLupd(Calendar.getInstance().getTime());
				ckCtContactDetailDao.add(tCkCtContactDetailCoFf);

				return new CkCtContactDetail(tCkCtContactDetailCoFf);
			}
		} catch (Exception ex) {
			throw ex;
		}

		return null;

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTripCargoFm getTripCargoFirstMile(CkCtTrip trip) throws Exception {
		List<CkCtTripCargoFm> fmList = tripFmService.findTripCargoFmsByTripId(trip.getTrId());
		if (fmList != null && fmList.size() > 0)
			return fmList.get(0);

		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public boolean isFirstMile(String shipmentType) throws Exception {
		if (StringUtils.isBlank(shipmentType))
			throw new ParameterException("param shipmentType null");

		if (ShipmentTypes.getByName(shipmentType) == null)
			throw new ParameterException("param shipmentType " + shipmentType + " not valid");

		return shipmentType.equalsIgnoreCase(ShipmentTypes.IMPORT.getId())
				|| shipmentType.equalsIgnoreCase(ShipmentTypes.EXPORT.getId());

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public boolean isJobRejected(String jobId) throws Exception {
		return ckJobRejectDao.hasRejectRemarks(jobId);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public boolean isJobRemarked(String jobId) throws Exception {
		return ckJobRemarksDao.hasRemarks(jobId);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<String> concurrentCheckJobsPaying(List<String> jobIds) throws Exception {
		if (jobIds == null)
			return null;

		List<String> jobIdList = new ArrayList<>();
		String hql = "from TCkJobTruck o where o.jobId in (:jobIds) and o.jobStatus=:status and o.jobOutPaymentState=:payingState";
		Map<String, Object> params = new HashMap<>();
		params.put("jobIds", jobIds);
		params.put("status", RecordStatus.ACTIVE.getCode());
		params.put("payingState", JobPaymentStates.PAYING.name());
		List<TCkJobTruck> listJobs = ckJobTruckDao.getByQuery(hql, params);
		if (listJobs != null && listJobs.size() > 0) {
			for (TCkJobTruck job : listJobs) {
				jobIdList.add(job.getJobId());
			}

		}

		return jobIdList;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<String> concurrentCheckJobsForInPayment(List<String> jobIds) throws Exception {
		if (jobIds == null)
			return null;

		List<String> jobIdList = new ArrayList<>();
		String hql = "from TCkJobTruck o where o.jobId in (:jobIds) and o.jobStatus=:status and o.jobInPaymentState=:payingState";
		Map<String, Object> params = new HashMap<>();
		params.put("jobIds", jobIds);
		params.put("status", RecordStatus.ACTIVE.getCode());
		params.put("payingState", JobPaymentStates.PAYING.name());
		List<TCkJobTruck> listJobs = ckJobTruckDao.getByQuery(hql, params);
		if (listJobs != null && listJobs.size() > 0) {
			for (TCkJobTruck job : listJobs) {
				jobIdList.add(job.getJobId());
			}

		}

		return jobIdList;
	}

	/**
	 * Initializes
	 */
	public CkJobTruck initTruckJob(String shipmentType)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {

		Principal principal = principalUtilService.getPrincipal();
		if (principal == null)
			throw new ParameterException("param principal null");

		if (ShipmentTypes.getByName(shipmentType) == null)
			throw new ParameterException("param shipmentType not valid");

		CkJobTruck ckJobTruck = new CkJobTruck();
		CkJob ckJob = new CkJob();
		ckJob.setTCkMstJobState(new CkMstJobState());

		ShipmentTypes stEnum = ShipmentTypes.getByName(shipmentType);
		ckJob.setTCkMstShipmentType(new CkMstShipmentType(stEnum.getId(), stEnum.getDesc()));
		ckJob.setTCkRecordDate(new CkRecordDate());
		ckJobTruck.setTCkJob(ckJob);

		ckJobTruck.setDomestic(shipmentType.equalsIgnoreCase(ShipmentTypes.DOMESTIC.name()));

		// Initialize Job Party CoFf - Account
		CoreAccn tCoreAccnByJobPartyCoFf = principal.getCoreAccn();
		ckJobTruck.setTCoreAccnByJobPartyCoFf(tCoreAccnByJobPartyCoFf);

		try {
			TCoreUsr coreUsr = coreUserDao.find(principal.getUserId());
			if (null != coreUsr) {
				CkCtContactDetail contactCoFf = new CkCtContactDetail();
				contactCoFf.setCdName(coreUsr.getUsrName());
				contactCoFf.setCdEmail(coreUsr.getUsrContact().getContactEmail());
				contactCoFf.setCdPhone(coreUsr.getUsrContact().getContactTel());
				ckJobTruck.setTCkCtContactDetailByJobContactCoFf(contactCoFf);
			}
		} catch (Exception ex) {
			throw ex;
		}

		// SG REQUIREMENT: Set the hidden fields if there is
		try {
			ckJobTruck.setHiddenFields(cltAccnConfigExService.getFieldsToHide(principalUtilService.getPrincipal()));

			// Check if need to display additional fields
			CKCountryConfig ctryCfg = cltAccnConfigExService.getCtryEnv();
			ckJobTruck.setShowAdditionalFields(ctryCfg.getCountry().equalsIgnoreCase("SG"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ckJobTruck;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateInv2Status(String jobId, ToInvoiceStates invStatus, String userId, Date date) throws Exception {
		// update invoice
		List<TCkCtPlatformInvoice> invList = platformInvoiceDao.findByJobId(jobId);
		if (null != invList) {
			for (TCkCtPlatformInvoice inv : invList) {
				inv.setTCkCtMstToInvoiceState(new TCkCtMstToInvoiceState(invStatus.name(), ""));
				inv.setInvUidLupd(userId);
				inv.setInvDtLupd(date);
				platformInvoiceDao.update(inv);
			}
		}

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateDnInv2Status(String jobId, DebitNoteStates dnStatus, String userId, Date date) throws Exception {

		// update debit note
		List<TCkCtDebitNote> dnList = dDebitNoteDao.findByJobId(jobId);
		if (null != dnList) {
			for (TCkCtDebitNote dn : dnList) {
				dn.setTCkCtMstDebitNoteState(new TCkCtMstDebitNoteState(dnStatus.name(), ""));
				dn.setDnUidLupd(userId);
				dn.setDnDtLupd(new Date());
				dDebitNoteDao.update(dn);
			}
		}
	}

	/**
	 * Retrieves the lists of job truck that the driver/vehicle is associated to.
	 * Job states should be in ASG, PAUSED, ONGOING
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkJobTruck> getTrucksAssignedToDriverTruckPair(String drvrId, String vehId, boolean isMobileEnabled)
			throws Exception {

		List<CkJobTruck> dtoList = new ArrayList<>();
		if (StringUtils.isBlank(drvrId))
			throw new ParameterException("param drvrId null or empty");
		if (StringUtils.isBlank(vehId))
			throw new ParameterException("param vehId null or empty");

		String hql = "from TCkJobTruck o where o.TCkCtDrv.drvId=:drvrId and o.TCkCtVeh.vhId=:vehId"
				+ " and o.TCkJob.TCkMstJobState.jbstId in (:jobStates) " + " and o.jobMobileEnabled = :mobileEnabled";
		Map<String, Object> params = new HashMap<>();
		params.put("drvrId", drvrId);
		params.put("vehId", vehId);
		params.put("mobileEnabled", isMobileEnabled ? 'Y' : 'N');
		params.put("jobStates", Arrays.asList(JobStates.ASG.name(), JobStates.PAUSED.name(), JobStates.ONGOING.name()));

		List<TCkJobTruck> list = ckJobTruckDao.getByQuery(hql, params);
		if (list != null && list.size() > 0) {
			for (TCkJobTruck e : list) {
				Hibernate.initialize(e.getTCkJob());
				Hibernate.initialize(e.getTCkCtDrv());
				Hibernate.initialize(e.getTCkCtVeh());

				CkJobTruck dto = new CkJobTruck(e);
				dto.setTCkJob(new CkJob(e.getTCkJob()));
				dto.setTCkCtDrv(new CkCtDrv(e.getTCkCtDrv()));
				dto.setTCkCtVeh(new CkCtVeh(e.getTCkCtVeh()));
				dtoList.add(dto);
			}
		}

		return dtoList;

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateTripLocGPS(String jobId) throws Exception {

		if (StringUtils.isBlank(jobId)) {
			throw new ParameterException("Param jobId is empty or blank.");
		}

		List<TCkCtTrip> tripList = ckCtTripDao.findByJobId(jobId);

		for (TCkCtTrip trip : tripList) {

			TCkCtTripLocation loc = trip.getTCkCtTripLocationByTrDepot();
			this.updateLocGPS(loc);

			loc = trip.getTCkCtTripLocationByTrFrom();
			this.updateLocGPS(loc);

			loc = trip.getTCkCtTripLocationByTrTo();
			this.updateLocGPS(loc);
		}
	}

	private void updateLocGPS(TCkCtTripLocation loc) throws Exception {

		Optional<TCkCtTripLocation> locOpt = Optional.ofNullable(loc);

		if (locOpt.isPresent() && locOpt.get().getTCkCtLocation() != null) {
			// copy from TCkCtLocation
			if (StringUtils.isNoneBlank(loc.getTCkCtLocation().getLocGps())) {
				loc.setTlocLocGps(loc.getTCkCtLocation().getLocGps());
			}

			// if location is REGION
			if (loc.getTCkCtLocation().getTCkCtMstLocationType() != null && LocationId.REGION
					.equalsIgnoreCase(loc.getTCkCtLocation().getTCkCtMstLocationType().getLctyId())) {

				// and different location name or address,
				String addressOrName = null;
				if (loc.isDiferentAddressWithLocation() && StringUtils.isNoneBlank(loc.getTlocLocAddress())) {
					addressOrName = loc.getTlocLocAddress();
				} else if (loc.isDiferentNameWithLocation() && StringUtils.isNoneBlank(loc.getTlocLocName())) {
					addressOrName = loc.getTlocLocName();
				}
				// fetch location again.
				if (StringUtils.isNoneBlank(addressOrName)) {
					String gps = coordinateService.fetchCoordinate(addressOrName);
					log.info("address: " + addressOrName + " GPS: " + gps);
					loc.setTlocLocGps(gps);
					ckCtTripLocationDao.update(loc);
				}
			}
		}

	}

	private TCkCtTripLocation createLocation(CkCtTripLocation dtoLocation, Principal principal) throws Exception {
		TCkCtTripLocation newTo = new TCkCtTripLocation();
		dtoLocation.toEntity(newTo);

		TCkCtLocation newLocationTo = ckCtLocationDao.find(dtoLocation.getTCkCtLocation().getLocId());

		newTo.setTlocId(CkUtil.generateIdSynch(TRIPLOC_PREFIX));
		newTo.setTCkCtLocation(newLocationTo);
		newTo.setTlocLocName(newLocationTo.getLocName());
		if (newLocationTo.isRegion()) {
			// region
			newTo.setTlocLocAddress(dtoLocation.getTlocLocAddress());
		} else {
			// address
			newTo.setTlocLocAddress(newLocationTo.getLocAddress());
		}

		newTo.setTlocStatus(RecordStatus.ACTIVE.getCode());
		newTo.setTlocUidCreate(principal.getUserId());
		newTo.setTlocUidLupd(principal.getUserId());
		ckCtTripLocationDao.add(newTo);
		return newTo;
	}

	private TCkCtTripLocation updateTripLocation(CkCtTripLocation dtoLocation, Principal principal) throws Exception {

		TCkCtTripLocation loc = ckCtTripLocationDao.find(dtoLocation.getTlocId());

		loc.setTlocRemarks(dtoLocation.getTlocRemarks());
		loc.setTlocMobileNo(dtoLocation.getTlocMobileNo());
		loc.setTlocDtLoc(dtoLocation.getTlocDtLoc());
		// tlocDtLoc

		loc.setTlocUidCreate(principal.getUserId());
		loc.setTlocUidLupd(principal.getUserId());

		ckCtTripLocationDao.saveOrUpdate(loc);
		return loc;
	}

	/**
	 * SG2 REQUIREMENT: Department categorization, if user is in the department, set
	 * job co/to department.
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void setDepartment(CkJobTruck dto, AccountTypes accnType, Principal principal) throws Exception {

		if (dto == null)
			throw new ParameterException("param dto null");

		if (principal == null)
			throw new ParameterException("principal null");

		TCkCtDeptUsr deptUser = depUsrDao.getUser(principal.getUserId());
		if (deptUser != null) {
			Hibernate.initialize(deptUser.getTCkCtDept());
			if (accnType == AccountTypes.ACC_TYPE_TO)
				dto.setTCkCtDeptByJobToDepartment(new CkCtDept(deptUser.getTCkCtDept()));
			else
				dto.setTCkCtDeptByJobCoDepartment(new CkCtDept(deptUser.getTCkCtDept()));

		}

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtDept getPrincipalDepartment(Principal principal) throws Exception {
		TCkCtDeptUsr deptUser = depUsrDao.getUser(principal.getUserId());
		if (deptUser != null) {
			Hibernate.initialize(deptUser.getTCkCtDept());
			Hibernate.initialize(deptUser.getTCkCtDept().getTCoreAccn());
			CkCtDept dept = new CkCtDept(deptUser.getTCkCtDept());
			dept.setTCoreAccn(new CoreAccn(deptUser.getTCkCtDept().getTCoreAccn()));
			return dept;

		}
		return null;
	}

	public String getClasquinPhoneNumber(CkJobTruck ckJobTruck) throws Exception {
		TCoreSysparam sysParam = sysParamDao.find("CLICTRUCK_WHATSAPP_NOTIF_ACCNS");
		if (sysParam != null && StringUtils.isNotEmpty(sysParam.getSysVal())) {
			List<String> list = Arrays.asList(sysParam.getSysVal().split(","));
			if (list.contains(ckJobTruck.getTCoreAccnByJobPartyTo().getAccnId())) {
				return Optional.ofNullable(ckJobTruck.getTCoreAccnByJobPartyTo().getAccnContact())
						.map(CoreContact::getContactTel).orElse(null);
			}
		}
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck updateTruckDriver(CkJobTruck ckJobTruck) throws Exception {

		if (ckJobTruck == null) {
			throw new ParameterException(" Parameter is null");
		}

		if (StringUtils.isEmpty(ckJobTruck.getJobId())) {
			throw new ParameterException(" Parameter job truck id is null");
		}

		try {
			TCkJobTruck tJobTruck = ckJobTruckDao.find(ckJobTruck.getJobId());
			String remark = "";

			// Driver
			if (ckJobTruck != null && ckJobTruck.getTCkCtDrv() != null
					&& StringUtils.isNotEmpty(ckJobTruck.getTCkCtDrv().getDrvId())) {
				
				tJobTruck.setTCkCtDrv(new TCkCtDrv(ckJobTruck.getTCkCtDrv().getDrvId()));
				remark = "Driver id: " + ckJobTruck.getTCkCtDrv().getDrvId();
			} else {
				tJobTruck.setTCkCtDrv(null);
			}
			
			// Vehicle
			if (ckJobTruck != null && ckJobTruck.getTCkCtVeh() != null
					&& StringUtils.isNotEmpty(ckJobTruck.getTCkCtVeh().getVhId())) {
				
				tJobTruck.setTCkCtVeh(new TCkCtVeh(ckJobTruck.getTCkCtVeh().getVhId()));
				remark = "  Truck id: " + ckJobTruck.getTCkCtVeh().getVhId();
			} else {
				tJobTruck.setTCkCtVeh(null);
			}
			
			this.addAuditLog(ckJobTruck.getJobId(), "UPDATE DRIVER TRUCK", remark);

		} catch (Exception e) {
			log.error("", e);
			throw e;
		}
		
		return ckJobTruck;
	}
	
	public void addAuditLog(String recKey, String event, String remark) throws Exception {

		TCoreAuditlog auditLog = new TCoreAuditlog();
		auditLog.setAudtId(CkUtil.generateId("CON"));
		auditLog.setAudtReckey(recKey);
		auditLog.setAudtTimestamp(new Date(0));
		auditLog.setAudtEvent(event);
		auditLog.setAudtUid("SYS");
		auditLog.setAudtRemoteIp("");
		auditLog.setAudtUname("SYS");
		auditLog.setAudtRemarks(remark);
		
		auditLogDao.add(auditLog);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void addOrUpdateTripsWhenOnGoingAndAssigned(CkJobTruck dto, Principal principal) throws Exception {

		List<CkCtTrip> ckCtTrips = dto.getTckCtTripList();

		if (null != ckCtTrips && ckCtTrips.size() > 0) {
			double totalJobCharge = 0.0;
			for (int i = 0; i < ckCtTrips.size(); i++) {

				CkCtTrip trip = ckCtTrips.get(i);
				TCkCtTrip existingTrip = ckCtTripDao.find(trip.getTrId());
				if (Objects.nonNull(existingTrip) && (existingTrip.getTrStatus() == 'R' || existingTrip.getTrStatus() == 'A' || existingTrip.getTrStatus() == 'P')) {

					trip.toEntity(existingTrip);
					existingTrip.setTrId(existingTrip.getTrId());
					// just set to pass to create fm
					trip.setTrId(existingTrip.getTrId());

					if (null == existingTrip.getTrSeq()) {
						existingTrip.setTrSeq(i);
					}
					existingTrip.setTrStatus(existingTrip.getTrStatus());
					existingTrip.setTrUidCreate(principal.getUserId());
					existingTrip.setTrDtCreate(Calendar.getInstance().getTime());
					existingTrip.setTrUidLupd(principal.getUserId());
					existingTrip.setTrDtLupd(Calendar.getInstance().getTime());
					existingTrip.setTrChargeOpen(null != trip.getTrChargeOpen() ? trip.getTrChargeOpen() : 'N');

					CkCtTripLocation to = trip.getTCkCtTripLocationByTrTo();
					if (null != to && null != to.getTCkCtLocation())
						existingTrip.setTCkCtTripLocationByTrTo(createLocation(to, principal));

					CkCtTripLocation from = trip.getTCkCtTripLocationByTrFrom();
					if (null != from && null != from.getTCkCtLocation())
						existingTrip.setTCkCtTripLocationByTrFrom(createLocation(from, principal));

					CkCtTripLocation depot = trip.getTCkCtTripLocationByTrDepot();
					if (null != depot && null != depot.getTCkCtLocation())
						existingTrip.setTCkCtTripLocationByTrDepot(createLocation(depot, principal));

					existingTrip.setTCkJobTruck(dto.toEntity(new TCkJobTruck()));

					// Re-calculating trip charge in case the field from dto not calculating
					// properly
					// or the user has changed some of the critieria but frontend not updated
					// and submitted the request.
					if (trip.getTCkCtTripCharge() != null) {
						if (trip.getTrChargeOpen() != null && trip.getTrChargeOpen() == 'N') {
						} else {
							trip.getTCkCtTripCharge().setTcIsOpen('Y');
						}

					}

					// to avoid nullpointer exception
					if (trip.getTCkCtTripCharge() != null) {
						// Check if a charge with the same ID already exists
						TCkCtTripCharge existingCharge = ckCtTripChargeDao.find(trip.getTCkCtTripCharge().getTcId());

						if (existingCharge != null) {
							// Update the existing charge
							trip.getTCkCtTripCharge().copyBeanProperties(existingCharge);
							existingCharge.setTcStatus(trip.getTCkCtTripCharge().getTcStatus());
							existingCharge.setTcUidLupd(principal.getUserId());
							existingCharge.setTcDtLupd(Calendar.getInstance().getTime());
							existingCharge.setTcPlatformFee(new BigDecimal("0.0"));
							existingCharge.setTcGovtTax(new BigDecimal("0.0"));
							existingCharge.setTcWitholdTax(new BigDecimal("0.0"));
							ckCtTripChargeDao.saveOrUpdate(existingCharge);
						} else {
							// Create a new trip charge
							TCkCtTripCharge newCharge = new TCkCtTripCharge();
							trip.getTCkCtTripCharge().copyBeanProperties(newCharge);
							newCharge.setTcUidCreate(principal.getUserId());
							newCharge.setTcDtCreate(Calendar.getInstance().getTime());
							newCharge.setTcPlatformFee(new BigDecimal("0.0"));
							newCharge.setTcGovtTax(new BigDecimal("0.0"));
							newCharge.setTcWitholdTax(new BigDecimal("0.0"));
							ckCtTripChargeDao.saveOrUpdate(newCharge);
						}
					}
					ckCtTripDao.saveOrUpdate(existingTrip);
				} else {
					if(existingTrip.getTrStatus() == 'D' && !trip.getTCkCtTripLocationByTrTo().getTCkCtLocation().getLocId().equals(existingTrip.getTCkCtTripLocationByTrTo().getTCkCtLocation().getLocId())){
						ObjectMapper mapper = new ObjectMapper();
						Map<String, Object> validateErrParam = new HashMap<>();
						validateErrParam.put("alreadyDroppedOff", "You couldn’t modify the trip because it had already been dropped off.");
						throw new ValidationException(mapper.writeValueAsString(validateErrParam));
					}
				}
			}
		}
	}
}
