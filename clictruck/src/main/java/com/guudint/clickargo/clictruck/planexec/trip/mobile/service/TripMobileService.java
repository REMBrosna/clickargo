package com.guudint.clickargo.clictruck.planexec.trip.mobile.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.guudint.clickargo.clictruck.common.dto.*;
import com.guudint.clickargo.clictruck.dsv.pod.PodServiceImpl;
import com.guudint.clickargo.clictruck.jobupload.service.SendEpodService;
import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.scheduler.TripDeliveryMonitoringScheduler;
import com.guudint.clickargo.clictruck.track.service.WhatsappYCloudService;
import com.guudint.clickargo.common.dao.CkAccnDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clicservice.service.ICkWorkflowService;
import com.guudint.clickargo.clictruck.common.dao.CkCtDrvDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.common.service.CkCtDrvService;
import com.guudint.clickargo.clictruck.common.service.CkCtVehService;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.TruckTripAttachmentEnum;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstTripAttachTypeDao;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstCargoType;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehState;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkCtJobTripDeliveryDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtJobTripDelivery;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruckAddAttr;
import com.guudint.clickargo.clictruck.planexec.job.event.TruckJobStateChangeEvent;
import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkJobMTripDto;
import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkJobMTruckAddAttrDto;
import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkJobTruckMobileDto;
import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkMTripCargoDetails;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTripDelivery;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.IJobDeliveryService;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkCtTripDoServiceImpl;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckAddtlAttrService;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripLocationDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripAttach;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoFm;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoMm;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripDo;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.dto.MobileTripCargo;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.dto.TripAttachment;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.CkCtTripWorkflowServiceImpl.TripStatus;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripCargoFmService;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripCargoMmService;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripDoService;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripService;
import com.guudint.clickargo.clictruck.track.dto.DistanceMatrixDto;
import com.guudint.clickargo.clictruck.track.service.TrackTraceEnterExitLocService;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dto.CkRecordDate;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.model.TCkRecordDate;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.common.util.NotificationsUtil;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.master.dto.CkMstCntType;
import com.guudint.clickargo.master.dto.CkMstJobState;
import com.guudint.clickargo.master.dto.CkMstJobType;
import com.guudint.clickargo.master.dto.CkMstShipmentType;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.master.model.TCkMstJobState;
import com.guudint.clickargo.master.model.TCkMstShipmentType;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.can.model.TCoreNotificationTemplate;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.config.model.TCoreSysparam;

@Service
public class TripMobileService {

	private static Logger LOG = Logger.getLogger(TripMobileService.class);
	private static String KEY_DESTINATION_TIME_BUFFER = "CLICTRUCK_DESTINATION_TIME_BUFFER";
	private static String CLICTRUCK_DELIVERY_API_CALL = "CLICTRUCK_DELIVERY_API_CALL";
	@Autowired
	private SendEpodService sendEpodService;
	public enum TripAttachTypeEnum {
		PHOTO("DOCUMENT"), PHOTO_PICKUP("PHOTO_PICKUP"), PHOTO_DROPOFF("PHOTO_DROPOFF"), DOCUMENT("DOCUMENT"), SIGNATURE("SIGNATURE");

		private String desc;

		private TripAttachTypeEnum(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}

	public enum DeliveryStatus {
		DELIVERING, NEW, DELIVERED;

	}

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	@Autowired
	@Qualifier("auditLogDao")
	protected GenericDao<TCoreAuditlog, String> auditLogDao;

	@Autowired
	private CkCtTripAttachDao ckCtTripAttachDao;

	@Autowired
	private CkCtMstTripAttachTypeDao ckCtMstTripAttachTypeDao;

	@Autowired
	private CkCtTripLocationDao ckCtTripLocationDao;

	@Autowired
	private CkCtTripDao ckCtTripDao;

	@Autowired
	private CkJobTruckDao ckJobTruckDao;

	@Autowired
	private CkCtDrvDao ckCtDrvDao;

	@Autowired
	private CkCtTripService ckCtTripService;

	@Autowired
	protected ICkSession ckSession;

	@Autowired
	private CkCtTripWorkflowServiceImpl ckCtTripWorkflowServiceImpl;

	@Autowired
	private IJobDeliveryService jobDeliveryService;

	@Autowired
	private CkCtJobTripDeliveryDao ckCtJobTripDeliveryDao;

	@Autowired
	@Qualifier("ckJobTruckMobileWorkflowService")
	private ICkWorkflowService<TCkJobTruck, CkJobTruck> ckJobTruckMobileWorkflowService;

	@Autowired
	private IEntityService<TCkRecordDate, String, CkRecordDate> ckRecordService;

	@Autowired
	private IEntityService<TCkJob, String, CkJob> ckJobService;

	@Autowired
	TrackTraceEnterExitLocService trackTraceEnterExitLocService;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	@Autowired
	@Qualifier("ckJobTruckService")
	private IEntityService<TCkJobTruck, String, CkJobTruck> ckJobTruckService;

	@Autowired
	protected NotificationsUtil notifUtil;

	@Autowired
	private CkJobTruckServiceUtil truckServiceUtil;

	@Autowired
	@Qualifier("ckCtDrvService")
	private IEntityService<TCkCtDrv, String, CkCtDrv> ckCtDrvService;

	@Autowired
	private CkCtDrvService driverService;

	@Autowired
	@Qualifier("ckCtVehService")
	private IEntityService<TCkCtVeh, String, CkCtVeh> ckCtVehService;

	@Autowired
	private CkCtVehService vehicleService;

	@Autowired
	private CkCtTripDoServiceImpl ckCtTripDoServiceImpl;

	@Autowired
	private CkCtTripDoService ckCtTripDoService;

	@Autowired
	private CkJobTruckServiceUtil ckJobTruckUtilService;

	@Autowired
	private CkJobTruckAddtlAttrService addAttrService;

	@Autowired
	private CkCtTripCargoMmService mmTripService;

	@Autowired
	private CkCtTripCargoFmService tripFmService;
	@Autowired
	private CkJobTruckServiceUtil ckJobTruckServiceUtil;
	@Autowired
	private TripDeliveryMonitoringScheduler tripDeliveryMonitoringScheduler;

	@Autowired
	private CkAccnDao ckAccnDao;

	@Autowired
	@Qualifier("ckCtAlertDao")
	private GenericDao<TCkCtAlert, String> ckCtAlertDao;
	protected static ObjectMapper mapper = new ObjectMapper();

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtTripAttach> downloadTripAttachment(String id, String atypid, boolean isTrip) throws Exception {
		LOG.debug("downloadTripAttachment");

		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");

		if (StringUtils.isBlank(id))
			throw new ParameterException("param id is null");

		if (StringUtils.isBlank(atypid)) {
			atypid = TripAttachTypeEnum.PHOTO_DROPOFF.name();
		}

		try {
			List<CkCtTripAttach> listCkCtTripAttach = new ArrayList<CkCtTripAttach>();

			List<TCkCtTripAttach> tripAttachList = null;
			if (isTrip) {
				tripAttachList = ckCtTripAttachDao.findByTrIdAndAtyId(id, atypid);
			} else {
				tripAttachList = ckCtTripAttachDao.findByTruckJobIdAndType(id, atypid);
			}

			tripAttachList.forEach((item) -> {

				CkCtTripAttach CkCtTripAttach = new CkCtTripAttach(item);

				// Getting attachment data from file using atLoc
				try {
					File filepath = new File(CkCtTripAttach.getAtLoc());
					CkCtTripAttach.setAtLocData(FileCopyUtils.copyToByteArray(filepath));
					CkCtTripAttach.setAtLoc(null);
				} catch (IOException e) {
					e.printStackTrace();
				}

				listCkCtTripAttach.add(CkCtTripAttach);

			});

			return listCkCtTripAttach;
		} catch (Exception e) {
			LOG.error(e);
			throw new ProcessingException(e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtTripAttach> uploadTripAttachment(TripAttachment dto) throws Exception {
		LOG.debug("uploadTripAttachment");

		if (null == dto)
			throw new ParameterException("dto is null");

		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");

		try {
			Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
			Date now = Calendar.getInstance().getTime();
			List<CkCtTripAttach> listCkCtTripAttach = new ArrayList<CkCtTripAttach>();

			String basePath;
			basePath = getSysParam(CtConstant.KEY_ATTCH_BASE_LOCATION);
			if (StringUtils.isBlank(basePath)) {
				throw new ProcessingException("basePath is not configured");
			}

			String jobId = dto.getTruckJobId();

			Path jobDir = Paths.get(basePath.concat(jobId));
			if (!Files.exists(jobDir)) {
				Files.createDirectories(jobDir);
			}

			CkCtTrip ckCtTrip = ckCtTripService.findById(dto.getTripId());

			if (ckCtTrip != null) {

				TCkCtTrip tripEntity = new TCkCtTrip();
				ckCtTrip.toEntity(tripEntity);

				String tripId = ckCtTrip.getTrId();

				Path tripDir = jobDir.resolve(tripId);
				if (!Files.exists(tripDir)) {
					Files.createDirectories(tripDir);
				}

				// Check first if imageData is not empty, then process this otherwise process
				// the list data
				if (dto.getImageData() != null && dto.getImageData().size() > 0) {
					for (String key : dto.getImageData().keySet()) {
						String type = key;
						for (Map<String, String> dataMap : dto.getImageData().get(key)) {
							byte[] data = Base64.getDecoder().decode(dataMap.get("data"));
							TCkCtTripAttach tCkCtTripAttach = new TCkCtTripAttach();
							tCkCtTripAttach.setAtId(CkUtil.generateId(MobileTripCargo.PREFIX_ID));
							tCkCtTripAttach.setTCkCtTrip(tripEntity);

							tCkCtTripAttach.setTCkCtMstTripAttachType(ckCtMstTripAttachTypeDao.find(type));

							String typeData = tCkCtTripAttach.getTCkCtMstTripAttachType().getAtypId();
							tCkCtTripAttach.setAtName(jobId.concat("_" + typeData).concat("_" + dataMap.get("name")));
							tCkCtTripAttach.setAtSource(TruckTripAttachmentEnum.MOBILE.name());

							String fileLoc = tripDir
									.resolve(jobId.concat("_" + typeData).concat("_" + dataMap.get("name"))).toString();

							String filePath = FileUtil.saveAttachment(fileLoc, data);
							tCkCtTripAttach.setAtLoc(filePath);
							tCkCtTripAttach.setAtStatus(RecordStatus.ACTIVE.getCode());

							tCkCtTripAttach
									.setAtUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
							tCkCtTripAttach.setAtDtCreate(now);
							tCkCtTripAttach.setAtDtLupd(now);
							tCkCtTripAttach.setAtUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);

							ckCtTripAttachDao.saveOrUpdate(tCkCtTripAttach);
							listCkCtTripAttach.add(new CkCtTripAttach(tCkCtTripAttach));
						}

					}
				} else {
					for (HashMap<String, String> dataMap : dto.getListData()) {
						byte[] data = Base64.getDecoder().decode(dataMap.get("data"));

						TCkCtTripAttach tCkCtTripAttach = new TCkCtTripAttach();
						tCkCtTripAttach.setAtId(CkUtil.generateId(MobileTripCargo.PREFIX_ID));
						tCkCtTripAttach.setTCkCtTrip(ckCtTripDao.find(ckCtTrip.getTrId()));

						tCkCtTripAttach.setTCkCtMstTripAttachType(ckCtMstTripAttachTypeDao.find(dto.getTypeData()));

						String typeData = tCkCtTripAttach.getTCkCtMstTripAttachType().getAtypId();
						tCkCtTripAttach.setAtName(jobId.concat("_" + typeData).concat("_" + dataMap.get("name")));
						tCkCtTripAttach.setAtSource(TruckTripAttachmentEnum.MOBILE.name());

						String fileLoc = tripDir.resolve(jobId.concat("_" + typeData).concat("_" + dataMap.get("name")))
								.toString();

						String filePath = FileUtil.saveAttachment(fileLoc, data);
						tCkCtTripAttach.setAtLoc(filePath);
						tCkCtTripAttach.setAtStatus(RecordStatus.ACTIVE.getCode());

						tCkCtTripAttach.setAtUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
						tCkCtTripAttach.setAtDtCreate(now);
						tCkCtTripAttach.setAtDtLupd(now);
						tCkCtTripAttach.setAtUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);

						ckCtTripAttachDao.saveOrUpdate(tCkCtTripAttach);
						listCkCtTripAttach.add(new CkCtTripAttach(tCkCtTripAttach));
					}
				}

			}
			return listCkCtTripAttach;
		} catch (Exception e) {
			LOG.error(e);
			throw new ProcessingException(e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void removeTripAttachment(TripAttachment dto) throws Exception {
		LOG.debug("removeTripAttachment");

		if (null == dto)
			throw new ParameterException("dto is null");

		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");

		try {
			List<TCkCtTripAttach> listTCkCtTripAttach = new ArrayList<>();
//			for (CkCtTrip ckCtTrip : dto.getCkJobTruck().getTckCtTripList()) {
			CkCtTrip ckCtTrip = ckCtTripService.findById(dto.getTripId());
			if (ckCtTrip != null) {
				switch (dto.getTypeData()) {
				case "PHOTO_PICKUP":
					listTCkCtTripAttach
							.addAll(ckCtTripAttachDao.findByTrIdAndAtyId(ckCtTrip.getTrId(), dto.getTypeData()));
					break;
				case "PHOTO_DROPOFF":
					listTCkCtTripAttach
							.addAll(ckCtTripAttachDao.findByTrIdAndAtyId(ckCtTrip.getTrId(), dto.getTypeData()));
					break;
				default:
					break;
				}
			}

			if (!listTCkCtTripAttach.isEmpty()) {
				for (TCkCtTripAttach tCkCtTripAttach : listTCkCtTripAttach) {

					String filePath = tCkCtTripAttach.getAtLoc();

					File file = new File(filePath);
					if (file.exists()) {
						file.delete();
					}
					ckCtTripAttachDao.remove(tCkCtTripAttach);
				}
			}
		} catch (Exception e) {
			LOG.error(e);
			throw new ProcessingException(e);
		}
	}
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruckMobileDto updateTrip(MobileTripCargo dto) throws Exception {
		LOG.debug("updateTrip");
		try {
			if (dto == null)
				throw new ParameterException("param dto null");

			if (dto.getAction() == null)
				throw new ParameterException("param action is null");

			Principal principal = ckSession.getPrincipal();
			if (null == principal)
				throw new ProcessingException("principal is null");

			CkCtTrip tripFromDb = ckCtTripService.findById(dto.getTripId());
			if (tripFromDb != null) {
				CkCtTripLocation ckCtTripLocation = new CkCtTripLocation();
				switch (dto.getAction()) {
				case MPICKUP:
					ckCtTripLocation = tripFromDb.getTCkCtTripLocationByTrFrom();
					tripFromDb = ckCtTripWorkflowServiceImpl.moveState(FormActions.MPICKUP, tripFromDb, principal, ServiceTypes.CLICTRUCK);
					// update the photo comment
					ckCtTripLocation.setTlocComment(dto.getPhotoComment());

					updateCkCtTripLocation(dto.getAction(), ckCtTripLocation, principal);
					// check if it's multidropoff and iterate through the dropofflists and update
					// the states from active to pickup
					// since there's only one pickup location
					if (dto.isMultiDropOff()) {
						if (dto.getDropOffTrips() != null && dto.getDropOffTrips().size() > 0) {
							for (String dTripId : dto.getDropOffTrips()) {

								CkCtTrip dropTripDb = ckCtTripService.findById(dTripId);
								// exclude the first seq as it is already updated above
								if (dropTripDb.getTrSeq() > 0) {
									ckCtTripWorkflowServiceImpl.moveState(FormActions.MPICKUP, dropTripDb, principal,
											ServiceTypes.CLICTRUCK);

								}

							}
						}
					}
					break;
				case MREDO:
					ckCtTripLocation = tripFromDb.getTCkCtTripLocationByTrFrom();
					tripFromDb = ckCtTripWorkflowServiceImpl.moveState(FormActions.MREDO, tripFromDb, principal,
							ServiceTypes.CLICTRUCK);

					updateCkCtTripLocation(dto.getAction(), ckCtTripLocation, principal);

					TripAttachment uploadTripAttachment = new TripAttachment();
					uploadTripAttachment.setTypeData(TripAttachTypeEnum.PHOTO_PICKUP.name());
					uploadTripAttachment.setTruckJobId(dto.getTruckJobId());
					uploadTripAttachment.setTripId(dto.getTripId());
					removeTripAttachment(uploadTripAttachment);

					// If you are to revert the pickup location state, the dropoff should also be
					if (dto.isMultiDropOff()) {
						if (dto.getDropOffTrips() != null && dto.getDropOffTrips().size() > 0) {
							for (String dTripId : dto.getDropOffTrips()) {
								CkCtTrip dropTripDb = ckCtTripService.findById(dTripId);
								// exclude the first seq as it is already updated above
								if (dropTripDb.getTrSeq() > 0) {
									ckCtTripWorkflowServiceImpl.moveState(FormActions.MREDO, dropTripDb, principal,
											ServiceTypes.CLICTRUCK);
								}

							}
						}
					}
					break;
				case MDELIVER:
					ckCtTripLocation = tripFromDb.getTCkCtTripLocationByTrTo();
					ckCtTripWorkflowServiceImpl.moveState(FormActions.MDELIVER, tripFromDb, principal,
							ServiceTypes.CLICTRUCK);
					updateCkCtTripLocation(dto.getAction(), ckCtTripLocation, principal);

					// If you are to deliver the pickup location state, the dropoff should also be
					// this means starting the job to delivering...
					if (dto.isMultiDropOff()) {
						if (dto.getDropOffTrips() != null && dto.getDropOffTrips().size() > 0) {
							for (String dTripId : dto.getDropOffTrips()) {
								CkCtTrip dropTripDb = ckCtTripService.findById(dTripId);
								// exclude the first seq as it is already updated above
								if (dropTripDb.getTrSeq() > 0) {
									ckCtTripWorkflowServiceImpl.moveState(FormActions.MDELIVER, dropTripDb, principal,
											ServiceTypes.CLICTRUCK);
								}

							}
						}
					}

					// auto create tripdo for mobile app
					createTripDoForMobile(tripFromDb, principal);
					// for create record
					TCkJobTruck tCkJobTruck = insertJobTripDelivery(dto, tripFromDb);
					TCkCtJobTripDelivery tCkCtJobTripDelivery = ckCtJobTripDeliveryDao.findByJobId(tCkJobTruck.getJobId());
					if (Objects.nonNull(tCkCtJobTripDelivery)){
						if (tCkCtJobTripDelivery.getJtdStatus().equalsIgnoreCase(DeliveryStatus.DELIVERING.name())){
							tCkCtJobTripDelivery.setJtdMsgState(NotificationTemplateName.CARGO_PICKED_UP.getDesc().toUpperCase());
							ckCtJobTripDeliveryDao.saveOrUpdate(tCkCtJobTripDelivery);
							this.sendWhatApp(tripFromDb, dto.getTruckJobId(), NotificationTemplateName.CARGO_PICKED_UP.getDesc());
						}
					}
					break;
				default:
					break;
				}
			}

			Optional<TCkJobTruck> opTCkJobTruckNew = Optional.ofNullable(ckJobTruckDao.find(dto.getTruckJobId()));
			CkJobTruckMobileDto ckJobTruck = dtoFromEntity(opTCkJobTruckNew.get());
			return ckJobTruck;
		} catch (Exception e) {
			LOG.error("", e);
			throw new ProcessingException(e);
		}
	}

	public String removeSpecialCharacters(String input) {
		String regex = "[^0-9]";
		return input.replaceAll(regex, "");
	}

	public TCkJobTruck insertJobTripDelivery(MobileTripCargo dto, CkCtTrip trip) throws Exception {
		LOG.info("insertJobTripDelivery"+ trip);

		TCkCtJobTripDelivery entity = new TCkCtJobTripDelivery();
		entity.setJtdId(CkUtil.generateId(CkCtJobTripDelivery.PREFIX_ID));

		CkJobTruck jobTruck = ckJobTruckService.findById(dto.getTruckJobId());
		entity.setJtdJobId(jobTruck.getJobId());
		if (jobTruck.getTCkCtVeh() != null && jobTruck.getTCkCtVeh().getVhGpsImei() != null) {
			entity.setJtdImei(jobTruck.getTCkCtVeh().getVhGpsImei());
		} else {
			entity.setJtdImei("");
		}
		entity.setJtdDtDeliver(new Date());
		entity.setJtdDtNotifiedDeliver(new Date());

		entity.setJtdJobTrip(trip.getTrId());

		String originLoc = null;
		CkCtTripLocation locFrom = trip.getTCkCtTripLocationByTrFrom();
		if (StringUtils.isNotEmpty(locFrom.getTlocLocGps())) {
			originLoc = locFrom.getTlocLocGps();
		} else if (StringUtils.isNotEmpty(locFrom.getTlocLocAddress())) {
			originLoc = locFrom.getTlocLocAddress();
		} else if (StringUtils.isNotEmpty(locFrom.getTlocLocName())) {
			originLoc = locFrom.getTlocLocName();
		}

		String destLoc = null;
		CkCtTripLocation locTo = trip.getTCkCtTripLocationByTrTo();
		if (StringUtils.isNotEmpty(locTo.getTlocLocGps())) {
			destLoc = locTo.getTlocLocGps();
		} else if (StringUtils.isNotEmpty(locTo.getTlocLocAddress())) {
			destLoc = locTo.getTlocLocAddress();
		} else if (StringUtils.isNotEmpty(locTo.getTlocLocName())) {
			destLoc = locTo.getTlocLocName();
		}

		if (originLoc == null)
			throw new ProcessingException("originLoc is null");

		if (destLoc == null)
			throw new ProcessingException("destLoc is null");

		DistanceMatrixDto distanceMatrixDto = new DistanceMatrixDto();
		Optional<TCoreSysparam> opTCoreSysparam = Optional
				.ofNullable(coreSysparamDao.find(CLICTRUCK_DELIVERY_API_CALL));
		// check for using Actual API or not
		if (opTCoreSysparam.isPresent() && opTCoreSysparam.get().getSysVal().equalsIgnoreCase("N")) {
			// for testing with dummy data on GoogleApiUtil class
			distanceMatrixDto = mapper.readValue("{" + GoogleApiUtil.firstCallOrigin + "}", DistanceMatrixDto.class);
		} else if (opTCoreSysparam.isPresent() && opTCoreSysparam.get().getSysVal().equalsIgnoreCase("Y")) {
			// for call Google API's
			System.out
					.println("getDistanceMatrix Param : " + originLoc + " , " + destLoc + " , " + jobTruck.getJobId());
			distanceMatrixDto = jobDeliveryService.getDistanceMatrix(originLoc, destLoc, jobTruck.getJobId());
		}

		entity.setJtdOriginLoc(originLoc);
		entity.setJtdDestLoc(destLoc);
		if (distanceMatrixDto != null && !distanceMatrixDto.getRows().isEmpty()) {
			DistanceMatrixDto.Row row = distanceMatrixDto.getRows().get(0);
			if (row != null && !row.getElements().isEmpty()) {
				DistanceMatrixDto.Element element = row.getElements().get(0);

				if (element.getDistance() != null) {
					entity.setJtdDistance(element.getDistance().getValue());
				} else {
					entity.setJtdDistance(0);
				}

				if (element.getDuration() != null) {
					entity.setJtdDuration((double) element.getDuration().getValue());
				} else {
					entity.setJtdDuration(0.0);
				}
			}
			entity.setJtdDtLastScan(new Date());
		}

		entity.setJtdDtPreNotify(null);
		entity.setJtdStatus(DeliveryStatus.DELIVERING.name());
		entity.setJtdDtCreate(new Date());
		ckCtJobTripDeliveryDao.saveOrUpdate(entity);

		TCkJobTruck tCkJobTruck = ckJobTruckDao.find(dto.getTruckJobId());
		jobTruck.setJobDtDelivery(new Date());
		ckJobTruckDao.saveOrUpdate(tCkJobTruck);
		return tCkJobTruck;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruckMobileDto confirmationTripDropOff(TripAttachment dto) throws Exception {
		LOG.debug("confirmationTripDropOff");
		try {
			if (dto == null)
				throw new ParameterException("param dto null");

			Principal principal = ckSession.getPrincipal();
			if (null == principal)
				throw new ProcessingException("principal is null");

			CkCtTrip ckCtTrip = ckCtTripService.findById(dto.getTripId());

			CkCtTripLocation ckCtTripLocation = ckCtTrip.getTCkCtTripLocationByTrTo();
			ckCtTrip = ckCtTripWorkflowServiceImpl.moveState(FormActions.MDROPOFF, ckCtTrip, principal,
					ServiceTypes.CLICTRUCK);
			ckCtTripLocation.setTlocComment(dto.getComment());
			ckCtTripLocation.setTlocLocName(principal.getUserId());
			ckCtTripLocation.setTlocCargoRec(dto.getTlocCargoRec());
			updateCkCtTripLocation(FormActions.MDROPOFF, ckCtTripLocation, principal);
			uploadTripAttachment(dto);

			TCkJobTruck jobTruck = ckJobTruckDao.find(dto.getTruckJobId());

			CkJobTruck ckJobTruck = dtoFromEntity(jobTruck);

			// update Job State to DELIVERED using the workflow, to update relevant fields
			// such as the recorddate,
			// jobtruck delivery date
			if (dto.isUpdateTruckJob() || dto.isMultiDrop()) {
				List<TCkCtTrip> multipleTrips = ckCtTripService.getTripsByTruckJobId(jobTruck.getJobId());
				for(TCkCtTrip trip : multipleTrips) {
					if('A' == trip.getTrStatus()) {
						trip.setTrId(trip.getTrId());
						trip.setTrStatus('R');
						trip.setTrDtLupd(new Date());
						trip.setTrUidLupd(principal.getUserId());
						ckCtTripDao.update(trip);
					}
				}
				boolean isNotMultiDropNotCompleted = multipleTrips != null && !multipleTrips.isEmpty() &&
						multipleTrips.stream().allMatch(trip -> trip.getTrStatus() == 'D');
				// if all of multi drop not completed the Job Truck won't be change status to DELIVERED unless all trip are done
				if(isNotMultiDropNotCompleted) {
					ckJobTruck = ckJobTruckMobileWorkflowService.moveState(FormActions.STOP, ckJobTruck, principal,
							ServiceTypes.CLICTRUCK);
					jobTruck.setJobDtDelivery(new Date());
					jobTruck.setJobProcessVia(CtConstant.JobProcessVia.MOBILE);
					ckJobTruck.getTCkJob().getTCkRecordDate().setRcdDtComplete(new Date());
					ckJobTruck.getTCkJob().getTCkRecordDate().setRcdUidComplete(principal.getUserId());

					ckJobTruckDao.saveOrUpdate(jobTruck);
					ckRecordService.update(ckJobTruck.getTCkJob().getTCkRecordDate(), principal);
					ckJobService.update(ckJobTruck.getTCkJob(), principal);
				}

				// for the track/trace report
				trackTraceEnterExitLocService.getEnterExitTimeOfLocation(ckJobTruck.getJobId());

				// to notify via email the intended recipients
				eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.STOP, ckJobTruck, principal));

				this.sendWhatApp(ckCtTrip, ckJobTruck.getJobId(), NotificationTemplateName.CARGO_DELIVERED.getDesc());
				// update driver/truck
				updateDriverTruckPairs(ckJobTruck, principal);

				// update job trip delivery
				updateJobTripDelivery(dto.getTruckJobId());
				//generate E-POD when Job is delivered
				if(ckJobTruck.getTCkJob().getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.DLV.name())) {
					LOG.error("Immediately Epo Executed : "+ ckJobTruck.getTCkJob().getJobId());
					sendEpodService.sendePodEmail();
				}
			}
			Optional<TCkJobTruck> opTCkJobTruckNew = Optional.ofNullable(ckJobTruckDao.find(dto.getTruckJobId()));
			return dtoFromEntity(opTCkJobTruckNew.get());
		} catch (Exception e) {
			LOG.error("confirmationTripDropOff: "+e);
			throw new ProcessingException(e);
		}
	}

	protected TCkCtTripLocation updateCkCtTripLocation(FormActions action, CkCtTripLocation dto, Principal principal)
			throws Exception {
		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		Date now = Calendar.getInstance().getTime();

		TCkCtTripLocation tCkCtTripLocation = ckCtTripLocationDao.find(dto.getTlocId());

		switch (action) {
		case MPICKUP:
			tCkCtTripLocation.setTlocComment(dto.getTlocComment() != null ? dto.getTlocComment() : null);
			tCkCtTripLocation.setTlocDtEnd(now);
			break;
		case MREDO:
			tCkCtTripLocation.setTlocComment(null);
			tCkCtTripLocation.setTlocDtEnd(null);
			break;
		case MDELIVER:
			tCkCtTripLocation.setTlocDtStart(now);
			break;
		case MDROPOFF:
			tCkCtTripLocation.setTlocComment(dto.getTlocComment());
			if (dto.getTlocCargoRec() != null){
				tCkCtTripLocation.setTlocCargoRec(dto.getTlocCargoRec());
			}
			tCkCtTripLocation.setTlocDtEnd(now);
			if (dto.getTlocIsDeviated() != null && dto.getTlocIsDeviated().equals('Y')) {
				tCkCtTripLocation.setTlocIsDeviated(dto.getTlocIsDeviated());
				tCkCtTripLocation.setTlocDeviationComment(dto.getTlocDeviationComment());
			}
			if (dto.getTlocName() != null) {
				tCkCtTripLocation.setTlocName(dto.getTlocName());
			}
			break;
		default:
			break;
		}
		tCkCtTripLocation.setTlocDtLupd(now);
		tCkCtTripLocation.setTlocUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
		ckCtTripLocationDao.saveOrUpdate(tCkCtTripLocation);
		return tCkCtTripLocation;
	}

	@Transactional
	public CkJobTruckMobileDto onGoingJob() throws Exception {
		LOG.debug("onGoingJob");

		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");

		Optional<TCkCtDrv> tCkCtDrv = Optional.ofNullable(ckCtDrvDao.findByMobileUserId(principal.getUserId()));
		CkJobTruckMobileDto mCkJobTruck = null;
		if (tCkCtDrv.isPresent()) {
			List<TCkJobTruck> listTCkJobTruck = ckJobTruckDao.findOngoingJobByDrvId(tCkCtDrv.get().getDrvId());

			if (!listTCkJobTruck.isEmpty()) {
				// should only have one mobile enabled onGoing job
				mCkJobTruck = dtoFromEntity(listTCkJobTruck.get(0));
			}
		}
		return mCkJobTruck;
	}

	protected String getSysParam(String key) throws Exception {
		LOG.debug("getSysParam");
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam == null)
			throw new EntityNotFoundException("sysParam " + key + " not configured");

		return sysParam.getSysVal();

	}

	public void tripLocationRemarks(CkCtTripLocation dto) throws Exception {

		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");

		if (dto == null)
			throw new ParameterException("param dto null");

		TCkCtTripLocation tCkCtTripLocation = ckCtTripLocationDao.find(dto.getTlocId());
		if (tCkCtTripLocation == null)
			throw new EntityNotFoundException("trip location does not exist. please try again");

		tCkCtTripLocation.setTlocRemarks(dto.getTlocRemarks());
		tCkCtTripLocation.setTlocSpecialInstn(dto.getTlocSpecialInstn());
		ckCtTripLocationDao.saveOrUpdate(tCkCtTripLocation);

	}

	public CkJobTruckMobileDto dtoFromEntity(TCkJobTruck entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkJobTruckMobileDto dto = new CkJobTruckMobileDto(entity);

			// no deep copy from BeanUtils
			TCkJob ckJobE = entity.getTCkJob();
			if (null != ckJobE) {
				Optional<TCkMstShipmentType> opShipmentType = Optional.ofNullable(ckJobE.getTCkMstShipmentType());
				if (opShipmentType.isPresent())
					dto.setShipmentType(ckJobE.getTCkMstShipmentType().getShtName());

				Optional<TCkMstJobState> opJobState = Optional.ofNullable(ckJobE.getTCkMstJobState());
				if (opJobState.isPresent())
					dto.setJobState(ckJobE.getTCkMstJobState().getJbstId());

				Optional<TCkCtVeh> opVeh = Optional.ofNullable(entity.getTCkCtVeh());
				if (opVeh.isPresent())
					dto.setTCkCtVeh(new CkCtVeh(opVeh.get()));

				CkJob job = new CkJob(ckJobE);
				job.setTCkMstJobState(new CkMstJobState(ckJobE.getTCkMstJobState()));
				job.setTCkRecordDate(new CkRecordDate(ckJobE.getTCkRecordDate()));
				job.setTCkMstShipmentType(new CkMstShipmentType(ckJobE.getTCkMstShipmentType()));
				job.setTCkMstJobType(new CkMstJobType(ckJobE.getTCkMstJobType()));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobCoAccn())
						.ifPresent(e -> job.setTCoreAccnByJobCoAccn(new CoreAccn(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobFfAccn())
						.ifPresent(e -> job.setTCoreAccnByJobFfAccn(new CoreAccn(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobOwnerAccn())
						.ifPresent(e -> job.setTCoreAccnByJobOwnerAccn(new CoreAccn(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobToAccn())
						.ifPresent(e -> job.setTCoreAccnByJobToAccn(new CoreAccn(e)));

				dto.setTCkJob(job);

			}

			List<CkCtTrip> tckCtTrips = ckCtTripService.findTripsByTruckJobAndStatus(dto.getJobId(),
					Arrays.asList(TripStatus.M_ACTIVE.getStatusCode(), TripStatus.M_PICKED_UP.getStatusCode(),
							TripStatus.M_DELIVERED.getStatusCode(), TripStatus.DLV.getStatusCode()));
			List<CkJobMTripDto> jobMtrips = new ArrayList<>();
			if (tckCtTrips != null) {
				for (CkCtTrip t : tckCtTrips) {
					CkJobMTripDto mTripDto = new CkJobMTripDto();
					mTripDto.setId(t.getTrId());
					mTripDto.setSeqNo(t.getTrSeq());
					mTripDto.setStatus(t.getTrStatus());

					Optional<CkCtTripLocation> opFromTripLoc = Optional.ofNullable(t.getTCkCtTripLocationByTrFrom());
					if (opFromTripLoc.isPresent()) {
						CkCtTripLocation from = opFromTripLoc.get();
						mTripDto.setFromLocName(from.getTlocLocName());
						mTripDto.setFromLocAddr(from.getTlocLocAddress());
						mTripDto.setEstPickupTime(from.getTlocDtLoc());
						mTripDto.setJobStartTime(from.getTlocDtStart());
						mTripDto.setPickedUpTime(from.getTlocDtEnd());
						mTripDto.setFromLocRemarks(from.getTlocRemarks());

					}

					Optional<CkCtTripLocation> opToTripLoc = Optional.ofNullable(t.getTCkCtTripLocationByTrTo());
					if (opToTripLoc.isPresent()) {
						CkCtTripLocation to = opToTripLoc.get();
						mTripDto.setToLocName(to.getTlocLocName());
						mTripDto.setToLocAddr(to.getTlocLocAddress());
						mTripDto.setEstDropOffTime(to.getTlocDtLoc());
						mTripDto.setDeliverStartTime(to.getTlocDtStart());
						mTripDto.setJobFinishTime(to.getTlocDtEnd());
						mTripDto.setCargoRecipient(to.getTlocCargoRec());
						mTripDto.setToLocRemarks(to.getTlocRemarks());

					}

					List<CkMTripCargoDetails> tripCargos = new ArrayList<>();
					// determine if the job is domestic
					if (ckJobTruckUtilService.isFirstMile(dto.getShipmentType())) {
						List<CkCtTripCargoFm> fmTripCargos = tripFmService.findTripCargoFmsByTripId(t.getTrId());

						fmTripCargos.forEach(el -> {
							CkMTripCargoDetails cargoDetails = new CkMTripCargoDetails();
							// populate relevant fields such as cargo details, special instructions
							Optional<CkMstCntType> opCntType = Optional.ofNullable(el.getTCkMstCntType());
							if (opCntType.isPresent())
								cargoDetails.setCntType(opCntType.get().getCnttName());

							cargoDetails.setCntNo(el.getCgCntNo());
							cargoDetails.setCntSealNo(el.getCgCntSealNo());
							cargoDetails.setCntLoad(el.getCgCntFullLoadStr());

							Optional<CkCtMstCargoType> opCargoType = Optional.ofNullable(el.getTCkCtMstCargoType());
							if (opCargoType.isPresent())
								cargoDetails.setGoodsType(opCargoType.get().getCrtypName());
							cargoDetails.setGoodsDesc(el.getCgCargoDesc());
							cargoDetails.setSpecialInstructions(el.getCgCargoSpecialInstn());
							tripCargos.add(cargoDetails);
						});
					} else {
						List<CkCtTripCargoMm> mmTripCargos = mmTripService.findTripCargoFmmsByTripId(t.getTrId());
						mmTripCargos.forEach(el -> {
							CkMTripCargoDetails cargoDetails = new CkMTripCargoDetails();
							// populate relevant fields such as cargo details, special instructions

							Optional<CkCtMstCargoType> opCargoType = Optional.ofNullable(el.getTCkCtMstCargoType());
							if (opCargoType.isPresent())
								cargoDetails.setGoodsType(opCargoType.get().getCrtypName());
							cargoDetails.setGoodsDesc(el.getCgCargoDesc());
							cargoDetails.setSpecialInstructions(el.getCgCargoSpecialInstn());
							cargoDetails.setCnCgId(el.getCgId());
							cargoDetails.setCgPickupStatus(el.getCgPickupStatus());
							cargoDetails.setCgDropOffStatus(el.getCgDropOffStatus());
							tripCargos.add(cargoDetails);
						});

					}

					mTripDto.setCargos(tripCargos);
					jobMtrips.add(mTripDto);
				}

				dto.setTrip(jobMtrips);
			}

			// TODO to do later part
//			dto = checkLabelIndicates(dto);

			// initialize driver ref no from job id
			dto.setDriverRefNo(entity.getJobId());

			// Set the driver ref no. from customer ref to be displayed in the mobile app
			// First element from comma-separated string will be used and the rest will be
			// displayed in the details
			// below the card header

			if (StringUtils.isNotBlank(entity.getJobCustomerRef()) && !StringUtils.equalsIgnoreCase(entity.getJobCustomerRef(), "-")) {
				// Split and get the first
				List<String> values = Arrays.asList(entity.getJobCustomerRef().split(","));
				if (values != null && values.size() > 0) {
					dto.setDriverRefNo(values.get(0));
				}
			} else if (StringUtils.isNotBlank(entity.getJobShipmentRef()) && !StringUtils.equalsIgnoreCase(entity.getJobShipmentRef(), "-")) {
				dto.setDriverRefNo(entity.getJobShipmentRef());
			}

			// SG Requirement for additional fields
			// SG Requirement for additional fields
			List<CkJobTruckAddAttr> addFieldsList = addAttrService.getAdditionalFields(dto);
			if (addFieldsList != null && addFieldsList.size() > 0) {
				List<CkJobMTruckAddAttrDto> list = new ArrayList<>();
				for (CkJobTruckAddAttr aF : addFieldsList) {
					String label = Optional.of(aF.getTCkCtConAddAttr().getCaaLabel()).orElse("");
					String value = Optional.of(aF.getJaaValue()).orElse("");
					if (StringUtils.isNotBlank(label) && StringUtils.isNotBlank(value)) {
						CkJobMTruckAddAttrDto d = new CkJobMTruckAddAttrDto(label, value);
						list.add(d);
					}
				}

				dto.setAddAttrDto(list);
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

	public String formatTemplateContents(String templateId, String jobId, String destAddr, String vehPlateNote,
			Long duration) throws Exception {
		TCoreNotificationTemplate waTemplate = notifUtil.getNotificationTemplate(templateId, ServiceTypes.CLICTRUCK);
		if (waTemplate != null) {
			String content = waTemplate.getNtplTempalte().replaceAll(":jobNumber", jobId)
					.replaceAll(":destAddr", destAddr).replaceAll(":vehPlateNo", vehPlateNote);
			if (templateId.equalsIgnoreCase(CkJobTruckMobileDto.NOTIF_TMPLT_30MINS_ARRIVAL) && duration != null) {
				content = content.replaceAll(":duration", (duration / 60) + " minutes");
			}

			return content;

		}

		return "";

	}

//////////////////////////
///// Helper Methods
//////////////////////////

	private void sendWhatApp(CkCtTrip ckCtTrip, String jobId, String templateName) throws Exception {
		LOG.debug("sendWhatApp");
		try {
			if (ckCtTrip == null)
				throw new ParameterException("param ckCtTrip null");

			if (StringUtils.isBlank(jobId))
				throw new ParameterException("param jobId null or empty");

			// Retrieve the jobTruck to get the vehicle details
			CkJobTruck dto = ckJobTruckService.findById(jobId);
			if (dto == null)
				throw new EntityNotFoundException("no job truck found: " + jobId);

			// TODO what if it's in others?
//			String vehPlateNo = "";
//			Optional<CkCtVeh> opJobTruckVeh = Optional.ofNullable(jobTruckDto.getTCkCtVeh());
//			if (opJobTruckVeh.isPresent())
//				vehPlateNo = opJobTruckVeh.get().getVhPlateNo();

			TCkCtTrip tCkCtTrip = ckCtTripDao.find(ckCtTrip.getTrId());
			//find clasquin Phone Number
			String clasQuinContactNo = ckJobTruckServiceUtil.getClasquinPhoneNumber(dto);
			//send whatApp via YCloud
			CkJobTruck jobTruck = ckJobTruckService.findById(dto.getJobId());
			String generateLink = tripDeliveryMonitoringScheduler.constructGenerateLink(jobTruck);
			this.formatContent(clasQuinContactNo, jobTruck, tCkCtTrip, templateName, generateLink);
		} catch (Exception e) {
			LOG.error(e);
			throw new ProcessingException(e);
		}
	}
	//send whatApp via YCloud
	public void formatContent(String clasQuinContactNo, CkJobTruck jobTruck, TCkCtTrip tCkCtTrip, String templateDesc, String generateLink) throws Exception {
		// Log the sendWA method call
		LOG.info("Sending WhatsApp notification for trip id: " + (tCkCtTrip != null ? tCkCtTrip.getTrId() : "null") + " with template: " + templateDesc);

		Boolean isSubscribed = ckAccnDao.findByAccnIdSubscribed(jobTruck.getTCoreAccnByJobPartyTo().getAccnId(), RecordStatus.ACTIVE.getCode(), null);
		if (!isSubscribed) {
			LOG.info("This account ID not subscribed to WhatsApp: " + jobTruck.getTCoreAccnByJobPartyTo().getAccnId());
		}else {
			ArrayList<String> texts = new ArrayList<>();
			String accnName = null;
			String accnId = null;
			if (Objects.nonNull(jobTruck.getTCoreAccnByJobPartyTo())) {
				accnName = jobTruck.getTCoreAccnByJobPartyTo().getAccnName();
				accnId = jobTruck.getTCoreAccnByJobPartyTo().getAccnId();
			}
			TCoreSysparam sysParam = coreSysparamDao.find("CLICTRUCK_CLASQUIN_MILLESIMA");
			if (StringUtils.isNotBlank(clasQuinContactNo) && sysParam != null) {
				accnName = sysParam.getSysVal();
			}
			if (Objects.nonNull(jobTruck.getTCkJob().getTCoreAccnByJobSlAccn()) && StringUtils.isNotBlank(clasQuinContactNo)) {
				accnName = jobTruck.getTCkJob().getTCoreAccnByJobSlAccn().getAccnName();
			}
			List<TCkCtAlert> listAlerts = findByAlert(accnId);
			for (TCkCtAlert entity : listAlerts) {
				String alertTemplateId = entity.gettCkCtMstAlert().getAltTemplateId();
				if (templateDesc.equalsIgnoreCase(NotificationTemplateName.CARGO_DELIVERED.getDesc()) &&
						alertTemplateId.equalsIgnoreCase(NotificationTemplateName.CARGO_DELIVERED.getDesc())) {
					texts.add((accnName != null ? accnName : ""));
					texts.add(jobTruck.getJobId());
				}else if (templateDesc.equalsIgnoreCase(NotificationTemplateName.CARGO_PICKED_UP.getDesc()) &&
						alertTemplateId.equalsIgnoreCase(NotificationTemplateName.CARGO_PICKED_UP.getDesc())) {
					texts.add(accnName != null ? accnName : "");
					texts.add(" " + (accnName != null ? accnName : ""));
					texts.add(jobTruck.getJobId());
					texts.add(generateLink != null ? generateLink : "");
					if (jobTruck.getTCoreAccnByJobPartyTo() != null && jobTruck.getTCoreAccnByJobPartyTo().getAccnContact() != null && jobTruck.getTCoreAccnByJobPartyTo().getAccnContact().getContactTel() != null) {
						texts.add(removeSpecialCharacters(jobTruck.getTCoreAccnByJobPartyTo().getAccnContact().getContactTel()));
					}
				}
			}
			// Send WhatsApp via YCloud
			if (tCkCtTrip != null && tCkCtTrip.getTCkCtTripLocationByTrTo() != null && Objects.nonNull(tCkCtTrip.getTCkCtTripLocationByTrTo().getTlocMobileNo())) {
				jobDeliveryService.sendYCloudWhatAppMsg(clasQuinContactNo,
						removeSpecialCharacters(tCkCtTrip.getTCkCtTripLocationByTrTo().getTlocMobileNo()),
						texts, jobTruck.getJobId(), templateDesc);
			}
			LOG.debug("Mobile Phone: " + (tCkCtTrip != null ? tCkCtTrip.getTCkCtTripLocationByTrTo().getTlocMobileNo() : "null"));
		}
	}

	/*
	 * Checks if the truck job pickup and drop off is less than or greater than the
	 * destination time buffer.
	 */
//	private CkJobTruckMobileDto checkLabelIndicates(CkJobTruckMobileDto jobTruckDto, CkJobMTripDto tripDto) throws Exception {
//		LOG.debug("checkLabelIndicates");
//		try {
//
//			if (dto != null) {
//
//				Optional<TCoreSysparam> opTCoreSysparam = Optional
//						.ofNullable(coreSysparamDao.find(KEY_DESTINATION_TIME_BUFFER));
//				if (opTCoreSysparam.isPresent()) {
//					Long bufferTimeInMins = Long.valueOf(opTCoreSysparam.get().getSysVal());
//
//					Calendar now = Calendar.getInstance();
//					if (dto.getTrip().getEstPickupTime() != null && dto.getTrip().getEstDropOffTime() != null) {
//
//						Instant pickUp = dto.getTrip().getEstPickupTime().toInstant();
//						Instant dropOff = dto.getTrip().getEstDropOffTime().toInstant();
//
//						Long durationPickUp = Duration.between(now.toInstant(), pickUp).toMinutes();
//						Long durationDropOff = Duration.between(now.toInstant(), dropOff).toMinutes();
//						if (durationPickUp > bufferTimeInMins && durationDropOff > bufferTimeInMins) {
//							dto.setGreen(true);
//						} else if (durationPickUp > bufferTimeInMins || durationDropOff > bufferTimeInMins) {
//							dto.setOrange(true);
//						} else if (durationPickUp < bufferTimeInMins && durationDropOff < bufferTimeInMins) {
//							dto.setRed(true);
//						}
//					}
//				}
//			}
//			return dto;
//		} catch (Exception e) {
//			LOG.error(e);
//			throw e;
//		}
//	}

	private void updateDriverTruckPairs(CkJobTruck jobTruck, Principal principal) throws Exception {
		try {

			if (jobTruck == null)
				throw new ParameterException("param jobtruck null");

			if (principal == null)
				throw new ParameterException("param principal null");

			// Retrieve the truck details from db
			CkJobTruck jobTruckDto = ckJobTruckService.findById(jobTruck.getJobId());
			if (jobTruckDto == null)
				throw new EntityNotFoundException("job truck not found: " + jobTruck.getJobId());

			CkCtDrv driver = jobTruckDto.getTCkCtDrv();
			if (jobTruckDto.getTCkCtVeh() != null) {
				CkCtVeh vehicle = jobTruckDto.getTCkCtVeh();

				// Job valid states that driver/vehicle maybe associated to the job
				List<String> jobMobileStates = Arrays.asList(JobStates.ASG.name(), JobStates.ONGOING.name(),
						JobStates.PAUSED.name());

				List<String> jobNonMobileStates = Arrays.asList(JobStates.ASG.name(), JobStates.ONGOING.name());

				// Get the lists of jobs associated to the driver/veh pair.
				List<CkJobTruck> jobsList = truckServiceUtil.getTrucksAssignedToDriverTruckPair(driver.getDrvId(),
						vehicle.getVhId(), true);
				// if the lists is empty that means, update the driver and vehicle
				if (jobsList != null && jobsList.size() <= 0) {
					// Check if the vehicle is free from any other association specially for
					// non-mobile jobs before updating it to unassigned first. Otherwise, in web,
					// user might be able to select
					// again this vehicle when it is already assigned to another.
					boolean isVehFree = vehicleService.isVehicleFree(vehicle.getVhId(), false, jobNonMobileStates);
					if (isVehFree) {
						// update the vehicle state first before the driver for it to be re-assigned to
						// other jobs/trip.
						CkCtVeh vehDto = ckCtVehService.findById(vehicle.getVhId());
						if (vehDto != null) {
							CkCtMstVehState vehState = new CkCtMstVehState();
							vehState.setVhstId(VehStates.UNASSIGNED.name());
							vehDto.setTCkCtMstVehState(vehState);
							ckCtVehService.update(vehDto, principal);
						}
					}

					// Check again if driver is assigned to other jobs before updating to unassigned
					boolean isDriverFreeFromMobile = driverService.isDriverFree(driver.getDrvId(), true,
							jobMobileStates);
					if (isDriverFreeFromMobile) {
						// if driver is free from mobile, check if it's free from non-mobile jobs
						boolean isDriverFree = driverService.isDriverFree(driver.getDrvId(), false, jobNonMobileStates);
						if (isDriverFree) {
							// update the driver then
							CkCtDrv drvDto = ckCtDrvService.findById(driver.getDrvId());
							if (drvDto != null) {
								drvDto.setDrvState(DriverStates.UNASSIGNED.name());
								ckCtDrvService.update(drvDto, principal);
							}
						}
					}
				}
			}

		} catch (Exception ex) {
			LOG.error("TruckDriverUpdateEventListener.onApplicationEvent: ", ex);
		}
	}

	private void updateJobTripDelivery(String jobId) throws Exception {

		Optional<TCkCtJobTripDelivery> tCkCtJobTripDelivery = Optional
				.ofNullable(ckCtJobTripDeliveryDao.findByJobId(jobId));

		if (tCkCtJobTripDelivery.isPresent()
				&& tCkCtJobTripDelivery.get().getJtdStatus().equalsIgnoreCase(DeliveryStatus.DELIVERING.name())) {
			tCkCtJobTripDelivery.get().setJtdStatus(DeliveryStatus.DELIVERED.name());
			tCkCtJobTripDelivery.get().setJtdDtLupd(new Date());
			ckCtJobTripDeliveryDao.saveOrUpdate(tCkCtJobTripDelivery.get());
		}
	}

	private void createTripDoForMobile(CkCtTrip trip, Principal principal) throws Exception {
		// find if there's already a tripDO already existing for this trip, because
		// for jobs loaded from xml, it is auto created
		CkCtTripDo tripDoDb = ckCtTripDoService.findByTripId(trip.getTrId());
		// because the method always instantiates to empty object
		if (tripDoDb != null && StringUtils.isBlank(tripDoDb.getDoNo())) {
			// auto-create tripDO
			CkCtTripDo tripDo = new CkCtTripDo();
			tripDo.setDoId(CkUtil.generateId(ICkConstant.PREFIX_JOB_ATT));
			tripDo.setTCkCtTrip(trip);

			tripDo.setDoStatus(RecordStatus.ACTIVE.getCode());
			tripDo.setDoDtCreate(Calendar.getInstance().getTime());
			tripDo.setDoUidCreate(principal.getUserId());
			tripDo.setDoDtLupd(Calendar.getInstance().getTime());
			tripDo.setDoUidLupd(principal.getUserId());
			if( trip.getTrSeq() == 0) {
				tripDo.setDoNo("M-" + trip.getTCkJobTruck().getJobId() + "-DO"); 
			} else {
				tripDo.setDoNo("M-" + trip.getTCkJobTruck().getJobId() + "-" + trip.getTrSeq() + "-DO"); 
			}
			ckCtTripDoServiceImpl.createDo(tripDo, principal);
		}
		// if there is then no need to create

	}

	private List<TCkCtAlert> findByAlert(String accnId) throws Exception {
		String hql = "FROM TCkCtAlert a WHERE a.altStatus = :status AND a.tCoreAccn.accnId = :accnId";
		Map<String, Object> params = new HashMap<>();
		params.put("accnId", accnId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		return ckCtAlertDao.getByQuery(hql, params);
	}


}
