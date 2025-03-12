package com.guudint.clickargo.clictruck.jobupload.service;

import com.beust.jcommander.ParameterException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractDao;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.common.constant.CkCtLocationConstant;
import com.guudint.clickargo.clictruck.common.constant.CkCtLocationConstant.LocationId;
import com.guudint.clickargo.clictruck.common.dao.CkCtDrvDao;
import com.guudint.clickargo.clictruck.common.dao.CkCtLocationDao;
import com.guudint.clickargo.clictruck.common.dao.CkCtVehDao;
import com.guudint.clickargo.clictruck.common.dto.VehStates;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.CtConstant.AccountTypeEnum;
import com.guudint.clickargo.clictruck.constant.TruckJobSourceEnum;
import com.guudint.clickargo.clictruck.dsv.service.impl.DsvShipmentService;
import com.guudint.clickargo.clictruck.jobupload.dto.JobRecord;
import com.guudint.clickargo.clictruck.jobupload.model.JobRecordTempate.JobRecordTempateItem;
import com.guudint.clickargo.clictruck.jobupload.validator.JobUploadServiceValidator;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstUomSizeDao;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstUomVolumeDao;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstUomWeightDao;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstVehTypeDao;
import com.guudint.clickargo.clictruck.master.model.*;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkCtContactDetailDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.planexec.trip.dao.*;
import com.guudint.clickargo.clictruck.planexec.trip.model.*;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dao.CkAccnDao;
import com.guudint.clickargo.common.dao.CkRecordDateDao;
import com.guudint.clickargo.common.enums.CargoTypes;
import com.guudint.clickargo.common.model.TCkAccn;
import com.guudint.clickargo.common.model.TCkRecordDate;
import com.guudint.clickargo.job.dao.CkJobDao;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.manageaccn.dao.CkCoreAccnDao;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.JobTypes;
import com.guudint.clickargo.master.enums.ShipmentTypes;
import com.guudint.clickargo.master.model.TCkMstJobState;
import com.guudint.clickargo.master.model.TCkMstJobType;
import com.guudint.clickargo.master.model.TCkMstShipmentType;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.config.dao.CoreSysparamDao;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.util.email.SysParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class JobUploadUtilService {

	private static Logger log = Logger.getLogger(JobUploadUtilService.class);

	private static final String FULL_TRUCK_LOAD = "FTL";
	private static final String JOB_SUB_TYPE = "LOCAL";

	@Autowired
	private CkRecordDateDao ckRecordDateDao;
	@Autowired
	private CkJobDao ckJobDao;
	@Autowired
	private CkCtContactDetailDao ckCtContactDetailDao;
	@Autowired
	private CkJobTruckDao jobTruckDao;
	@Autowired
	private CkCtTripLocationDao ckCtTripLocationDao;
	@Autowired
	private CkCtLocationDao ckCtLocationDao;
	@Autowired
	private CkCtTripChargeDao ckCtTripChargeDao;
	@Autowired
	private CkCtTripDao ckCtTripDao;
	@Autowired
	private CkCtTripCargoMmDao ckCtTripCargoMmDao;
	@Autowired
	protected SysParam sysParam;
	@Autowired
	private CkCoreAccnDao ckCoreAccnDao;
	@Autowired
	private CkCtTripDoDao ckCtTripDoDao;
	@Autowired
	private GenericDao<TCoreAuditlog, String> auditLogDao;
	@Autowired
	private CkCtContractDao ckCtContractDao;
	@Autowired
	private CkJobTruckExtDao ckJobTruckExtDao;
	@Autowired
	private CkCtDrvDao CkCtDrvDao;
	@Autowired
	private CkCtVehDao ckCtVehDao;
	@Autowired
	private CkCtMstVehTypeDao ckCtMstVehTypeDao;
	@Autowired
	private CkAccnDao ckAccnDao;
	@Autowired
	private DsvShipmentService dsvShipmentService;
	@Autowired
	@Qualifier("coreUserDao")
	private GenericDao<TCoreUsr, String> coreUserDao;

	@Autowired
	@Qualifier("coreAccDao")
	private GenericDao<TCoreAccn, String> coreAccnDao;
	@Autowired
	private JobUploadServiceValidator serviceValidator;
	@Autowired
	private CkCtMstUomWeightDao ckCtMstUomWeightDao;
	@Autowired
	private CkCtMstUomVolumeDao ckCtMstUomVolumeDao;
	@Autowired
	private CkCtMstUomSizeDao ckCtMstUomSizeDao;
	@Autowired
	private CoreSysparamDao coreSysparamDao;

	@Transactional
	public TCkJobTruck createJob(Principal principal, String accountId, JobRecord jobRecord) throws Exception {

		String userId = principal.getUserId();
		// TCkJobTruck
		TCkJobTruck jobTruck = this.prepareJobTruck(principal, accountId, jobRecord);

		ckRecordDateDao.add(jobTruck.getTCkJob().getTCkRecordDate());
		ckJobDao.add(jobTruck.getTCkJob());
		ckCtContactDetailDao.add(jobTruck.getTCkCtContactDetailByJobContactCoFf());
		ckCtContactDetailDao.add(jobTruck.getTCkCtContactDetailByJobContactTo());
		jobTruckDao.add(jobTruck);

		TCkCtTrip trip = this.createTrip(userId, accountId, jobRecord, jobTruck, 0, null); // trip seq from 0;
		if (jobRecord.getMultiDrops() != null && jobRecord.getMultiDrops().size() > 0) {
			for (int i = 0; i < jobRecord.getMultiDrops().size(); i++) {
				JobRecord subJobRecord = jobRecord.getMultiDrops().get(i);
				trip = this.createTrip(userId, accountId, subJobRecord, jobTruck, i + 1, trip); // trip seq from 1;
			}
		}
		this.addExtendAttributes(jobTruck, jobRecord.getExtendAttrs(), userId, accountId);
		this.audit(jobTruck.getJobId(), "CREATED", userId);
		return jobTruck;
	}

	@Transactional
	public TCkJobTruck createJobWithMultiDrop(Principal principal, String accountId, JobRecord jobRecord, List<TCkJobTruck> jobTrucksObjectList, int tripSeq,List<TCkCtTrip> existTrips) throws Exception {

		String userId = principal.getUserId();
		TCkJobTruck jobTruck = this.prepareJobTruck(principal, accountId, jobRecord);

		boolean existsRefNo = jobRecord.getShipmentRefNo() != null &&
				jobTrucksObjectList.stream()
						.anyMatch(job -> jobRecord.getShipmentRefNo().trim().equals(job.getJobShipmentRef().trim()));

		if (existsRefNo) {
			// If shipment reference exists, update jobTruck with existing job details to create trip
			jobTrucksObjectList.stream()
					.filter(job -> jobRecord.getShipmentRefNo().trim().equals(job.getJobShipmentRef().trim()))
					.findFirst()
					.ifPresent(existingJob -> {
						jobTruck.setJobId(existingJob.getJobId().trim());
						jobTruck.setJobShipmentRef(existingJob.getJobShipmentRef().trim());
					});
		} else {
			// Persist related entities only if the shipment reference doesn't exist then create new job
			this.saveJobEntities(jobTruck);
			jobTrucksObjectList.add(jobTruck);
		}

		this.createMultipleTrips(userId, accountId, jobRecord, jobTruck, tripSeq, existTrips);
		this.addExtendAttributes(jobTruck, jobRecord.getExtendAttrs(), userId, accountId);

		audit(jobTruck.getJobId(), "CREATED", userId);

		return jobTruck;
	}

	private void saveJobEntities(TCkJobTruck jobTruck) throws Exception {
		ckRecordDateDao.add(jobTruck.getTCkJob().getTCkRecordDate());
		ckJobDao.add(jobTruck.getTCkJob());
		ckCtContactDetailDao.add(jobTruck.getTCkCtContactDetailByJobContactCoFf());
		ckCtContactDetailDao.add(jobTruck.getTCkCtContactDetailByJobContactTo());
		jobTruckDao.add(jobTruck);
	}

	@Transactional
	public void addExtendAttributes(TCkJobTruck jobTruck, Map<String, Object> extendAttrs, String userId, String accnId)
			throws Exception {

		for (Map.Entry<String, Object> attr : extendAttrs.entrySet()) {

			String id = CkUtil.generateId(TCkJobTruckExt.PREFIX_ID) + UUID.randomUUID().toString().substring(0, 5);
			TCkJobTruckExt ext = new TCkJobTruckExt(id, attr.getKey(), conver2Str(attr.getValue()));
			ext.setTCkJobTruck(jobTruck);
			ext.setTCoreAccn(new TCoreAccn(accnId, null, ' ', null));
			
			if(attr.getValue() instanceof java.util.Date) {
				ext.setJextValType("Date");
			}
			
			ext.setJextStatus(Constant.ACTIVE_STATUS);
			ext.setJextDtCreate(new Date());
			ext.setJextUidCreate(userId);

			ckJobTruckExtDao.add(ext);
		}
	}
	
	private String conver2Str(Object obj) {
		if(obj == null) {
			return null;
		}
		if( obj instanceof java.util.Date) {
			return ((Date)obj).getTime() + "";
		}
		return obj.toString();
	}

	@Transactional
	public List<JobRecordTempateItem> getJobUploadTemplate(String accnId) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		String jsonTemplateBody = this.getJsonTemplate(accnId);

		List<JobRecordTempateItem> templateItemList = null;

		try {
			templateItemList = objectMapper.readValue(jsonTemplateBody, new TypeReference<List<JobRecordTempateItem>>() {});
			log.info("templateItemList: " + templateItemList);
			if (templateItemList == null || templateItemList.size() == 0) {
				throw new Exception();
			}
		} catch (Exception e) {
			log.error("", e);
			throw new Exception("Fail to parse excel template to JSON in T_CK_ACCN : " + accnId);
		}
		return templateItemList;
	}
	
	
	private String getJsonTemplate(String accnId) throws Exception{

		TCkAccn ckAccn = ckAccnDao.find(accnId);
		
		String filePath = null;
		
		if (null != ckAccn && !StringUtils.isBlank(ckAccn.getCaccnExcelTemplate())) {
			filePath = ckAccn.getCaccnExcelTemplate();
		} else {
			// default
			filePath = sysParam.getValString("CLICTRUCK_UPLOAD_JSON_TEMPATE", "/docs/UploadExcelTemplate/Default.json");
		}
		// root path
		String rootPath = sysParam.getValString(CtConstant.KEY_JRXML_BASE_PATH, "/home/vcc/jasper/");
		log.info("Json template " + accnId + "  " + rootPath);

		return new String(Files.readAllBytes(Paths.get(rootPath + filePath)));
	}

	private TCkJobTruck prepareJobTruck(Principal principal, String accountId, JobRecord jobRecord) throws Exception {

		String jobId = CkUtil.generateId(ICkConstant.PREFIX_PARENT_JOB);
		String jobTruckId = CkUtil.generateId("CKCTJ");

		// login Account id
		TCoreAccn accnLogin = ckCoreAccnDao.find(accountId);
		TCoreAccn accnCo = null;
		TCoreAccn accnTo = null;

		// Contract
		TCkCtContract contract = null;
		// Find Contract for Sagawa TO
		if (accnLogin.getAccnId().equalsIgnoreCase("SSA") && accnLogin.getTMstAccnType().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.getDesc())){
			contract = getContractByFF(AccountTypeEnum.valueOf(jobRecord.getContractId()).getValue(), "SSA");

		// Find Contract for Sagawa FF
		} else if (AccountTypeEnum.isValid(accnLogin.getAccnId())){
			contract = getContractByFF(accnLogin.getAccnId(), jobRecord.getContractId());
		}else {
			contract = ckCtContractDao.findByName(jobRecord.getContractId()).orElse(null);
		}

		if (null == contract) {
			throw new Exception("Fail to find contract: " + jobRecord.getContractId());
		}

		if (AccountTypes.ACC_TYPE_TO.name().equalsIgnoreCase(accnLogin.getTMstAccnType().getAtypId())) {
			if (accnLogin.getAccnId().equalsIgnoreCase(contract.getTCoreAccnByConTo().getAccnId())) {
				accnTo = accnLogin;
				accnCo = contract.getTCoreAccnByConCoFf();
			}
		} else {
			if (accnLogin.getAccnId().equalsIgnoreCase(contract.getTCoreAccnByConCoFf().getAccnId())) {
				accnCo = accnLogin;
				accnTo = contract.getTCoreAccnByConTo();
			}
		}

		if (accnCo != null) {
			log.info("CO: " + accnCo.getAccnId() + " TO: " + accnTo.getAccnId());
		}

		TCkCtMstVehType mstVehType = null;

		//

		// TCkRecordDate
		TCkRecordDate jobRecordDate = new TCkRecordDate();
		jobRecordDate.setRcdId(CkUtil.generateId());
		jobRecordDate.setRcdDtSubmit(new Date());

		// TCkJob
		TCkJob job = new TCkJob();
		job.setTCkRecordDate(jobRecordDate);
		job.setJobId(jobId);
		if (principal.getCoreAccn().getTMstAccnType().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())){
			job.setTCkMstJobState(new TCkMstJobState(JobStates.ACP.name(), JobStates.ACP.name()));
		}else {
			job.setTCkMstJobState(new TCkMstJobState(JobStates.SUB.name(), JobStates.SUB.name()));
		}
		if (StringUtils.isBlank(jobRecord.getLoading())){
			job.setJobLoading(FULL_TRUCK_LOAD);
		} else {
			serviceValidator.validationJobLoading(jobRecord.getLoading());
			job.setJobLoading(jobRecord.getLoading());
		}
		if (StringUtils.isBlank(jobRecord.getJobSubType())){
			job.setJobSubType(JOB_SUB_TYPE);
		} else {
			serviceValidator.validationJobSubType(jobRecord.getJobSubType());
			job.setJobSubType(jobRecord.getJobSubType());
		}
		ShipmentTypes shipType = ShipmentTypes.DOMESTIC;
		job.setTCkMstShipmentType(new TCkMstShipmentType(shipType.getId(), null));
		job.setTCkMstJobType(new TCkMstJobType(JobTypes.TRKO.name(), null));
		job.setTCoreAccnByJobToAccn(accnTo);

		if (jobRecord.getCargoOwner() != null) {
			TCoreAccn coreAccn = coreAccnDao.find(jobRecord.getCargoOwner());
//			job.setTCoreAccnByJobOwnerAccn(coreAccn);
			job.setTCoreAccnByJobSlAccn(coreAccn);
		}else {
			job.setTCoreAccnByJobOwnerAccn(accnCo);
		}

		if (AccountTypes.ACC_TYPE_FF.name().equalsIgnoreCase(accnCo.getTMstAccnType().getAtypId())) {
			// FF
			job.setTCoreAccnByJobFfAccn(accnCo);
			job.setTCoreAccnByJobCoAccn(null);
		} else {
			// CO
			job.setTCoreAccnByJobFfAccn(null);
			job.setTCoreAccnByJobCoAccn(accnCo);
		}

		job.setJobStatus(Constant.ACTIVE_STATUS);
		job.setJobDtCreate(new Date());

		// TCkCtContactDetail TO
		TCkCtContactDetail tContactTo = new TCkCtContactDetail();
		tContactTo.setCdId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.CD_PREFIX));
		tContactTo.setCdStatus(Constant.ACTIVE_STATUS);
		tContactTo.setCdDtCreate(new Date());

		// TCkCtContactDetail CO
		TCkCtContactDetail tContactCo = new TCkCtContactDetail();
		tContactCo.setCdId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.CD_PREFIX));
		tContactCo.setCdStatus(Constant.ACTIVE_STATUS);
		tContactCo.setCdDtCreate(new Date());

		// Set Contact Name, Phone, and Email to null for "TO"
		if (!AccountTypes.ACC_TYPE_TO.name().equalsIgnoreCase(accnLogin.getTMstAccnType().getAtypId())){
			tContactTo.setCdName("");
			tContactTo.setCdPhone("");
			tContactTo.setCdEmail("");

			TCoreUsr coreUsr = coreUserDao.find(principal.getUserId());
			// Set Contact Name, Phone, and Email to the user who uploaded the Excel for "CO"
			tContactCo.setCdName(coreUsr.getUsrName()); // CO/FF user's username
			tContactCo.setCdPhone(coreUsr.getUsrContact().getContactTel()); // CO/FF user's mobile number
			tContactCo.setCdEmail(coreUsr.getUsrContact().getContactEmail()); // CO/FF user's email

		}else {
			// Set Contact Name, Phone, and Email to the user who uploaded the Excel for "CO"
			tContactCo.setCdName(accnCo.getAccnName()); // CO/FF user's username
			tContactCo.setCdPhone(accnCo.getAccnContact().getContactTel()); // CO/FF user's mobile number
			tContactCo.setCdEmail(accnCo.getAccnContact().getContactEmail()); // CO/FF user's email

			TCoreUsr coreUsr = coreUserDao.find(principal.getUserId());
			tContactTo.setCdName(coreUsr.getUsrName());
			tContactTo.setCdPhone(coreUsr.getUsrContact().getContactTel());
			tContactTo.setCdEmail(coreUsr.getUsrContact().getContactEmail());
		}
		// Driver
		TCkCtDrv tCkCtDrv = null;
		if (StringUtils.isNotBlank(jobRecord.getDriverName())) {
			tCkCtDrv = CkCtDrvDao.findByDriverNameAccnId(accnTo.getAccnId(), jobRecord.getDriverName());
			if (tCkCtDrv == null) {
				throw new ParameterException("Fail to find driver by name: " + jobRecord.getDriverName());
			}
			tCkCtDrv.setDrvState(VehStates.ASSIGNED.name());
		}
		// Truck
		TCkCtVeh veh = null;
		if (StringUtils.isNotBlank(jobRecord.getTruckPlateNo())) {
			List<TCkCtVeh> vehList = ckCtVehDao.findByCompanyPlateNo(accnTo.getAccnId(), jobRecord.getTruckPlateNo());
			if (vehList.isEmpty()){
				throw new ParameterException("Fail to find vehicle by plate NO: " + jobRecord.getTruckPlateNo());
			}
			veh = vehList.get(0);
			mstVehType = veh.getTCkCtMstVehType(); // set truck type
		}
		if (null == mstVehType && jobRecord.getCargoTruckType() != null) {
			mstVehType = this.getVehType(jobRecord.getCargoTruckType());
		}
		// Validate truck type consistency across company vehicles
		if (mstVehType != null) {
			String vehType = mstVehType.getVhtyId();
			List<TCkCtVeh> companyVehList = ckCtVehDao.findVehTypeByCompany(accnTo.getAccnId());

			boolean exists = companyVehList.stream()
					.anyMatch(v -> v.getTCkCtMstVehType().getVhtyId().equalsIgnoreCase(vehType));
			if (!exists) {
				throw new ParameterException("Invalid Truck Type provided : " + vehType);
			}
		}
		if (tCkCtDrv != null && veh != null) {
			// Job is Assign status
			job.setTCkMstJobState(new TCkMstJobState(JobStates.ASG.name(), JobStates.ASG.name()));
		}

		// TCkJobTruck
		TCkJobTruck jobTruck = new TCkJobTruck();

		jobTruck.setJobId(jobTruckId);
		jobTruck.setTCkJob(job);
		jobTruck.setTCkCtContactDetailByJobContactTo(tContactTo);
		jobTruck.setTCkCtContactDetailByJobContactCoFf(tContactCo);

		jobTruck.setTCoreAccnByJobPartyCoFf(accnCo);
		jobTruck.setTCoreAccnByJobPartyTo(accnTo);

		jobTruck.setJobDtBooking(jobRecord.getBookingDate());
		jobTruck.setJobDtPlan(jobRecord.getPlanDate());

		jobTruck.setTCkCtMstVehType(mstVehType);
		jobTruck.setJobVehOth(null);
		jobTruck.setJobCustomerRef(jobRecord.getJobCustomerRef());
		jobTruck.setJobShipmentRef(jobRecord.getShipmentRefNo());

		jobTruck.setTCkCtDrv(tCkCtDrv);
		jobTruck.setTCkCtVeh(veh);

		jobTruck.setJobTotalCharge(BigDecimal.ONE);

		// new CkTruckMiscMobileService().checkAndSetFinanceOptions()
		jobTruck.setJobIsFinanced('N');
		// new CkTruckMiscMobileService().checkAndSetMobileEnable()
		jobTruck.setJobMobileEnabled('Y');
		jobTruck.setJobSource(TruckJobSourceEnum.EXCEL.name());

		jobTruck.setJobStatus(Constant.ACTIVE_STATUS);
		jobTruck.setJobUidCreate(principal.getUserId());
		jobTruck.setJobDtCreate(new Date());
		jobTruck.setJobDtLupd(new Date()); // order by this field in frontEnd.

		// FF-CO
		this.setFFCO(jobRecord, accnLogin, jobTruck, job);

		return jobTruck;
	}

	@SuppressWarnings("unused")
	private TCkCtTrip createTrip(String userId, String accountId, JobRecord jobRecord, TCkJobTruck jobTruck, int tripSeq, TCkCtTrip previousTrip) throws Exception {
		SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String accnToId = jobTruck.getTCoreAccnByJobPartyTo().getAccnId();

		String fromLocNameDetails = dsvShipmentService.getLocNameFromAddress(jobRecord.getStartLocAddress());
		String endLocNameDetails = dsvShipmentService.getLocNameFromAddress(jobRecord.getEndLocAddress());
		String fromLocName = dsvShipmentService.getLocNameFromAddress(jobRecord.getEndLoc());
		String endLocName = dsvShipmentService.getLocNameFromAddress(jobRecord.getStartLoc());

//		if(fromLocName.equalsIgnoreCase(endLocName) && fromLocNameDetails.equalsIgnoreCase(endLocNameDetails)){
//			throw new Exception("From and To location details must not be the same address.");
//		}

		// From location;
		TCkCtLocation fromLoc = null;
		TCkCtTripLocation tripLocFrom = null;
		if (previousTrip == null) {
			if (StringUtils.isNotBlank(jobRecord.getStartLoc())) {
				fromLoc = ckCtLocationDao.findByLocationName(jobRecord.getStartLoc(), accnToId).orElse(null);
			}else {
				fromLoc = ckCtLocationDao.findByAddressAndCompany(jobRecord.getStartLocAddress(), accnToId).orElse(null);
			}
			if (null == fromLoc) {
				// create new location;
				fromLoc = this.createLocation(accnToId, LocationId.REGION, fromLocName, jobRecord.getStartLoc());
			}
			tripLocFrom = new TCkCtTripLocation();
			tripLocFrom.setTlocId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIPLOC_PREFIX));
			tripLocFrom.setTCkCtLocation(fromLoc);
			tripLocFrom.setTlocLocAddress(fromLoc.getTCkCtMstLocationType().getLctyId().equalsIgnoreCase(LocationId.REGION)?
					jobRecord.getStartLocAddress() : fromLoc.getLocAddress()); // departure
			tripLocFrom.setTlocLocName(fromLoc.getLocName());
			tripLocFrom.setTlocLocGps(fromLoc.getLocGps());
			tripLocFrom.setTlocStatus(Constant.ACTIVE_STATUS);
			tripLocFrom.setTlocDtCreate(new Date());
			tripLocFrom.setTlocRemarks(jobRecord.getFromLocRemarks());
			tripLocFrom.setTlocMobileNo(jobRecord.getFromLocMobileNumber());
			if (jobRecord.getFromLocDateTime() != null){
				String dateString = normalizeDate(jobRecord.getFromLocDateTime(), true);
				Date date = inputFormat.parse(dateString);
				log.info("=>> getFromLocDateTime DATE TIME CONVERSION: "+inputFormat.parse(dateString) + "DATE: "+dateString);
				tripLocFrom.setTlocDtLoc(date);
			}
		} else {
			tripLocFrom = previousTrip.getTCkCtTripLocationByTrFrom();
		}
		// To location;
		TCkCtLocation toLoc = null;
		if (jobRecord.getStartLoc() != null) {
			toLoc = ckCtLocationDao.findByLocationName(jobRecord.getEndLoc(), accnToId).orElse(null);
		}else {
			toLoc = ckCtLocationDao.findByAddressAndCompany(jobRecord.getEndLocAddress(), accnToId).orElse(null);
		}
		if (null == toLoc) {
			// create new location;
			toLoc = this.createLocation(accnToId, LocationId.REGION, endLocName, jobRecord.getEndLoc());
		}

		// TCkCtTripLocation
		TCkCtTripLocation tripLocTo = new TCkCtTripLocation();
		tripLocTo.setTlocId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIPLOC_PREFIX));
		tripLocTo.setTCkCtLocation(toLoc);
		if(jobRecord.getToLocDateTime() != null){
			String dateString = normalizeDate(jobRecord.getToLocDateTime(), true);
			Date date = inputFormat.parse(dateString);
			log.info("=>> setTlocDtLoc DATE TIME CONVERSION: "+inputFormat.parse(dateString) + "DATE: "+dateString);
			tripLocTo.setTlocDtLoc(date);
		}
		tripLocTo.setTlocCargoRec(jobRecord.getToLocCargoRec());
		tripLocTo.setTlocMobileNo(jobRecord.getToLocMobileNumber());
		tripLocTo.setTlocRemarks(jobRecord.getToLocRemark());
		tripLocTo.setTlocLocAddress(toLoc.getTCkCtMstLocationType().getLctyId().equalsIgnoreCase(LocationId.REGION)?
				jobRecord.getEndLocAddress() : toLoc.getLocAddress()); // destination
		tripLocTo.setTlocLocName(toLoc.getLocName());
		tripLocTo.setTlocLocGps(toLoc.getLocGps());
		tripLocTo.setTlocStatus(Constant.ACTIVE_STATUS);
		tripLocTo.setTlocDtCreate(new Date());

		// TCkCtTripCharge
		TCkCtTripCharge tripCharge = new TCkCtTripCharge(
				CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_CHARGE_PREFIX), 'Y');
		tripCharge.setTcStatus(Constant.ACTIVE_STATUS);
		tripCharge.setTcDtCreate(new Date());

		// TCkCtTrip
		TCkCtTrip trip = new TCkCtTrip(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_PREFIX));
		trip.setTCkJobTruck(jobTruck);
		trip.setTCkCtTripLocationByTrFrom(tripLocFrom);
		trip.setTCkCtTripLocationByTrTo(tripLocTo);
		// trip.setTCkCtTripLocationByTrDepot(locDepot);
		trip.setTCkCtTripCharge(tripCharge);
		trip.setTrSeq(tripSeq);

		trip.setTrChargeOpen('Y');
		trip.setTrStatus(Constant.ACTIVE_STATUS);
		trip.setTrDtCreate(new Date());

		// TCkCtTripCargoMm
		List<TCkCtTripCargoMm> cargoMmList = this.createCargoMM(userId, jobRecord, trip);

		// TCkCtTripDo
		TCkCtTripDo ckCtTripDo = new TCkCtTripDo();
		ckCtTripDo.setDoId(CkUtil.generateId(ICkConstant.PREFIX_JOB_ATT));
		ckCtTripDo.setTCkCtTrip(trip);
		// ckCtTripDo.setDoNo(msg.getHeader().getMessageId()); // shipment Message Id
		String doNo;
		if( tripSeq == 0) {
			doNo = "M-" + jobTruck.getJobId() + "-DO";
		} else {
			doNo = "M-" + jobTruck.getJobId() + "-" + tripSeq + "-DO";
		}
		ckCtTripDo.setDoNo(doNo);

		ckCtTripDo.setDoStatus(RecordStatus.ACTIVE.getCode());
		ckCtTripDo.setDoDtCreate(Calendar.getInstance().getTime());
		ckCtTripDo.setDoUidCreate("SYS");
		ckCtTripDo.setDoDtLupd(Calendar.getInstance().getTime());
		ckCtTripDo.setDoUidLupd("SYS");

		// ckCtTripLocationDao.add(locDepot);
		ckCtTripLocationDao.saveOrUpdate(tripLocFrom);
		ckCtTripLocationDao.saveOrUpdate(tripLocTo);

		ckCtTripChargeDao.add(tripCharge);
		ckCtTripDao.add(trip);

		for (TCkCtTripCargoMm cargoMm : cargoMmList) {
			ckCtTripCargoMmDao.add(cargoMm);
		}

		ckCtTripDoDao.saveOrUpdate(ckCtTripDo);

		return trip;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public void createMultipleTrips(String userId, String accountId, JobRecord jobRecord, TCkJobTruck jobTruck, int tripSeq, List<TCkCtTrip> existTrips) throws Exception {
		SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String accnToId = jobTruck.getTCoreAccnByJobPartyTo().getAccnId();

		// Validate that From and To locations are not the same
		String fromLocDetails = dsvShipmentService.getLocNameFromAddress(jobRecord.getStartLocAddress());
		String toLocDetails = dsvShipmentService.getLocNameFromAddress(jobRecord.getEndLocAddress());
		if (fromLocDetails.equalsIgnoreCase(toLocDetails)) {
			throw new IllegalArgumentException("From and To location details must not be the same address.");
		}

		// Create FROM Location
		TCkCtLocation fromLoc = StringUtils.isNotBlank(jobRecord.getStartLoc()) ?
				ckCtLocationDao.findByLocationName(jobRecord.getStartLoc(), accnToId).orElse(null) :
				ckCtLocationDao.findByAddressAndCompany(jobRecord.getStartLocAddress(), accnToId).orElse(null);
		if (fromLoc == null) {
			fromLoc = createLocation(accnToId, LocationId.REGION, fromLocDetails, jobRecord.getStartLoc());
		}

		TCkCtTripLocation tripLocFrom = new TCkCtTripLocation();
		tripLocFrom.setTlocId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIPLOC_PREFIX));
		tripLocFrom.setTCkCtLocation(fromLoc);
		tripLocFrom.setTlocLocAddress(fromLoc.getTCkCtMstLocationType().getLctyId().equalsIgnoreCase(LocationId.REGION) ?
				jobRecord.getStartLocAddress() : fromLoc.getLocAddress());
		tripLocFrom.setTlocLocName(fromLoc.getLocName());
		tripLocFrom.setTlocLocGps(fromLoc.getLocGps());
		tripLocFrom.setTlocStatus(Constant.ACTIVE_STATUS);
		tripLocFrom.setTlocDtCreate(new Date());
		tripLocFrom.setTlocRemarks(jobRecord.getFromLocRemarks());
		tripLocFrom.setTlocMobileNo(jobRecord.getFromLocMobileNumber());
		if (jobRecord.getFromLocDateTime() != null) {
			tripLocFrom.setTlocDtLoc(inputFormat.parse(normalizeDate(jobRecord.getFromLocDateTime(), true)));
		}

		// Create TO Location
		TCkCtLocation toLoc = StringUtils.isNotBlank(jobRecord.getEndLoc()) ?
				ckCtLocationDao.findByLocationName(jobRecord.getEndLoc(), accnToId).orElse(null) :
				ckCtLocationDao.findByAddressAndCompany(jobRecord.getEndLocAddress(), accnToId).orElse(null);
		if (toLoc == null) {
			toLoc = createLocation(accnToId, LocationId.REGION, toLocDetails, jobRecord.getEndLoc());
		}

		TCkCtTripLocation tripLocTo = new TCkCtTripLocation();
		tripLocTo.setTlocId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIPLOC_PREFIX));
		tripLocTo.setTCkCtLocation(toLoc);
		tripLocTo.setTlocLocAddress(toLoc.getTCkCtMstLocationType().getLctyId().equalsIgnoreCase(LocationId.REGION) ?
				jobRecord.getEndLocAddress() : toLoc.getLocAddress());
		tripLocTo.setTlocLocName(toLoc.getLocName());
		tripLocTo.setTlocLocGps(toLoc.getLocGps());
		tripLocTo.setTlocStatus(Constant.ACTIVE_STATUS);
		tripLocTo.setTlocDtCreate(new Date());
		tripLocTo.setTlocRemarks(jobRecord.getToLocRemark());
		tripLocTo.setTlocMobileNo(jobRecord.getToLocMobileNumber());
		if (jobRecord.getToLocDateTime() != null) {
			tripLocTo.setTlocDtLoc(inputFormat.parse(normalizeDate(jobRecord.getToLocDateTime(), true)));
		}

		// Create Trip Charge
		TCkCtTripCharge tripCharge = new TCkCtTripCharge(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_CHARGE_PREFIX), 'Y');
		tripCharge.setTcStatus(Constant.ACTIVE_STATUS);
		tripCharge.setTcDtCreate(new Date());

		// Create Trip
		TCkCtTrip trip = new TCkCtTrip(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_PREFIX));
		trip.setTCkJobTruck(jobTruck);
		trip.setTCkCtTripLocationByTrFrom(tripLocFrom);
		trip.setTCkCtTripLocationByTrTo(tripLocTo);
		trip.setTCkCtTripCharge(tripCharge);
		trip.setTrSeq(tripSeq);
		trip.setTrChargeOpen('Y');
		trip.setTrStatus(Constant.ACTIVE_STATUS);
		trip.setTrDtCreate(new Date());

		// Create Delivery Order
		TCkCtTripDo tripDo = new TCkCtTripDo();
		tripDo.setDoId(CkUtil.generateId(ICkConstant.PREFIX_JOB_ATT));
		tripDo.setTCkCtTrip(trip);
		String doNo = (tripSeq == 0) ? "M-" + jobTruck.getJobId() + "-DO" : "M-" + jobTruck.getJobId() + "-" + tripSeq + "-DO";
		tripDo.setDoNo(doNo);
		tripDo.setDoStatus(RecordStatus.ACTIVE.getCode());
		tripDo.setDoDtCreate(new Date());
		tripDo.setDoUidCreate("SYS");
		tripDo.setDoDtLupd(new Date());
		tripDo.setDoUidLupd("SYS");

		boolean existTrip = existTrips.stream().anyMatch(tr ->
				isSameJobTruck(tr, jobTruck)
						&& isMatchingStartLocation(tr, jobRecord)
						&& isMatchingEndLocation(tr, jobRecord)
//						isMatchingToLocationDateTime(tr, jobRecord)
		);
		// will add cargo to existing trip not new trip
		if (existTrip){
			existTrips.stream()
					.filter(tr -> tr.getTCkJobTruck().getJobId().trim().equals(jobTruck.getJobId().trim()))
					.findFirst()
					.ifPresent(existTr -> {
						trip.setTrId(existTr.getTrId().trim());
					});
		}else {
			// Save all entities
			ckCtTripLocationDao.saveOrUpdate(tripLocFrom);
			ckCtTripLocationDao.saveOrUpdate(tripLocTo);
			ckCtTripChargeDao.add(tripCharge);
			ckCtTripDao.add(trip);
			ckCtTripDoDao.saveOrUpdate(tripDo);
			existTrips.add(trip);
		}

		// Create Cargo Mid Mile
		TCkCtTripCargoMm cargoMm = this.prepareCargoMM(userId, jobRecord, trip);
		ckCtTripCargoMmDao.add(cargoMm);
	}

	private boolean isSameJobTruck(TCkCtTrip trip, TCkJobTruck jobTruck) {
		return trip.getTCkJobTruck().getJobId().trim().equals(jobTruck.getJobId().trim());
	}

	private boolean isMatchingStartLocation(TCkCtTrip trip, JobRecord jobRecord) {
		String tripLocName = trip.getTCkCtTripLocationByTrFrom().getTlocLocName().trim();
		String tripLocAddress = trip.getTCkCtTripLocationByTrFrom().getTlocLocAddress().trim();
		String startLoc = jobRecord.getStartLoc().trim();
		String startLocAddress = jobRecord.getStartLocAddress().trim();

		return tripLocName.equals(startLoc) ||
				tripLocAddress.equals(startLoc) ||
				tripLocName.equals(startLocAddress) ||
				tripLocAddress.equals(startLocAddress);
	}

	private boolean isMatchingEndLocation(TCkCtTrip trip, JobRecord jobRecord) {
		String tripLocName = trip.getTCkCtTripLocationByTrTo().getTlocLocName().trim();
		String tripLocAddress = trip.getTCkCtTripLocationByTrTo().getTlocLocAddress().trim();
		String endLoc = jobRecord.getEndLoc().trim();
		String endLocAddress = jobRecord.getEndLocAddress().trim();

		return tripLocName.equals(endLoc) ||
				tripLocAddress.equals(endLoc) ||
				tripLocName.equals(endLocAddress) ||
				tripLocAddress.equals(endLocAddress);
	}

	public static String normalizeDate(String input, boolean isUtc) throws Exception {
		TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

		SimpleDateFormat inputFormatWithAMPM = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		inputFormatWithAMPM.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

		SimpleDateFormat inputFormat24Hour = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		inputFormat24Hour.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

		SimpleDateFormat inputFormatFull = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		inputFormatFull.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

		SimpleDateFormat inputFormatDateOnly = new SimpleDateFormat("dd/MM/yyyy");
		inputFormatDateOnly.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

		SimpleDateFormat inputFormatDateOnly2 = new SimpleDateFormat("dd-MMM-yyyy");
		inputFormatDateOnly2.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

		SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (isUtc){
			outputFormat.setTimeZone(utcTimeZone);
		}else {
			outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
		}

		Date date;
		try {
			// Try parsing as AM/PM format
			date = inputFormatWithAMPM.parse(input);
		} catch (Exception e1) {
			try {
				// Try parsing as 24-hour format
				date = inputFormat24Hour.parse(input);
			} catch (Exception e2) {
				try {
					// Try parsing as the full format (which may include a zone)
					date = inputFormatFull.parse(input);
				} catch (Exception e3) {
					// Lastly, try parsing as date-only (defaults to 00:00:00)
					try {
						// Try parsing as the full format (which may include a zone)
						date = inputFormatDateOnly.parse(input);
					} catch (Exception e4) {
						// Lastly, try parsing as date-only (defaults to 00:00:00)
						date = inputFormatDateOnly2.parse(input);
					}
				}
			}
		}
		return outputFormat.format(date);
	}

	private TCkCtLocation createLocation(String accnId, String locType, String locName, String locAddress) throws Exception {
		// Attempt to find a default location by locName
		TCkCtLocation defaultLoc = ckCtLocationDao
				.findByDefaultRegion(locType, locName, accnId)
				.orElse(null);

		// Check if a new location is needed
		if (defaultLoc == null ||
				(!defaultLoc.getLocName().equalsIgnoreCase(locType) && !defaultLoc.getLocName().equalsIgnoreCase(locName))) {

			// Try finding a more generic default location
			defaultLoc = ckCtLocationDao
					.findByDefaultRegion(locType, CkCtLocationConstant.ColumnParam.LOC_DEFAULT, accnId)
					.orElse(null);

			TCkCtLocation loc = new TCkCtLocation();

			if (defaultLoc == null) {
				// Create a new default location if none exists
				loc.setLocId(CkUtil.generateId(CkCtLocationConstant.Prefix.PREFIX_CK_CT_LOCATION));
				loc.setTCkCtMstLocationType(new TCkCtMstLocationType(LocationId.REGION, ""));
				loc.setTCoreAccn(new TCoreAccn(accnId, null, ' ', null));
				loc.setLocAddress(""); // default to empty
				loc.setLocName(CkCtLocationConstant.ColumnParam.LOC_DEFAULT);
				loc.setLocStatus(Constant.ACTIVE_STATUS);
				loc.setLocDtStart(new Date());
				loc.setLocDtEnd(new Date());
				loc.setLocDtCreate(new Date());
				loc.setLocUidCreate("sys");

				// Save the new location to the database
				ckCtLocationDao.add(loc);
			} else {
				// Reuse the existing default location details
				loc.setLocId(defaultLoc.getLocId());
				loc.setTCkCtMstLocationType(defaultLoc.getTCkCtMstLocationType());
				loc.setTCoreAccn(defaultLoc.getTCoreAccn());
				loc.setLocAddress(defaultLoc.getLocAddress());
				loc.setLocName(defaultLoc.getLocName());
				loc.setLocDtStart(defaultLoc.getLocDtStart());
				loc.setLocDtEnd(defaultLoc.getLocDtEnd());
				loc.setLocStatus(defaultLoc.getLocStatus());
				loc.setLocDtCreate(defaultLoc.getLocDtCreate());
				loc.setLocUidCreate(defaultLoc.getLocUidCreate());
			}
			return loc;
		}

		// Return the existing default location if no new location is created
		return defaultLoc;
	}


	private List<TCkCtTripCargoMm> createCargoMM(String userId, JobRecord jobRecord, TCkCtTrip trip) throws Exception {

		List<TCkCtTripCargoMm> cargoMmList = new ArrayList<>();

		Date now = new Date();

		TCkCtTripCargoMm cargoMm = new TCkCtTripCargoMm(
				CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_CRG_FM_PREFIX), trip);

		cargoMm.setTCkCtMstCargoType(this.getCargoType(jobRecord));

		cargoMm.setCgStatus(Constant.ACTIVE_STATUS);
		cargoMm.setCgDtCreate(now);
		cargoMm.setCgUidCreate(userId);
		cargoMm.setCgCargoVolume(jobRecord.getCgCargoVolume());

		if (StringUtils.isNotBlank(jobRecord.getCgCargoWeightUom())) {
			TCkCtMstUomWeight tCkCtMstUomWeight = ckCtMstUomWeightDao.getWeightUomByDesc(jobRecord.getCgCargoWeightUom());
			if (Objects.nonNull(tCkCtMstUomWeight)) {
				if (cargoMm.getTCkCtMstUomWeight() == null) {
					cargoMm.setTCkCtMstUomWeight(new TCkCtMstUomWeight()); // Ensure initialization
				}
				cargoMm.getTCkCtMstUomWeight().setWeiId(tCkCtMstUomWeight.getWeiId());
			}
		}

		cargoMm.setCgCargoLength(jobRecord.getCgCargoLength());
		cargoMm.setCgCargoWidth(jobRecord.getCgCargoWidth());

		if (StringUtils.isNotBlank(jobRecord.getCgCargoSizeUom())) {
			TCkCtMstUomSize tCkCtMstUomSize = ckCtMstUomSizeDao.getSizeUomByDesc(jobRecord.getCgCargoSizeUom());
			if (Objects.nonNull(tCkCtMstUomSize)) {
				if (cargoMm.getTCkCtMstUomSize() == null) {
					cargoMm.setTCkCtMstUomSize(new TCkCtMstUomSize()); // Ensure initialization
				}
				cargoMm.getTCkCtMstUomSize().setSizId(tCkCtMstUomSize.getSizId());
			}
		}

		cargoMm.setCgCargoHeight(jobRecord.getCgCargoHeight());
		cargoMm.setCgCargoQty(jobRecord.getCgCargoQty());
		cargoMm.setCgCargoQtyUom(jobRecord.getCargoQtyUom());
		cargoMm.setCgCargoWeight(jobRecord.getCgCargoWeight());

		if (StringUtils.isNotBlank(jobRecord.getCgCargoVolumeUom())) {
			TCkCtMstUomVolume tCkCtMstUomVolume = ckCtMstUomVolumeDao.getVolumeUomByDesc(jobRecord.getCgCargoVolumeUom());
			if (Objects.nonNull(tCkCtMstUomVolume)) {
				if (cargoMm.getTCkCtMstUomVolume() == null) {
					cargoMm.setTCkCtMstUomVolume(new TCkCtMstUomVolume()); // Ensure initialization
				}
				cargoMm.getTCkCtMstUomVolume().setVolId(tCkCtMstUomVolume.getVolId());
			}
		}
		cargoMm.setCgCargoMarksNo(jobRecord.getCgCargoMarksNo());
		cargoMm.setCgCargoDesc(jobRecord.getDescription());
		cargoMm.setCgCargoSpecialInstn(jobRecord.getCgCargoSpecialInstn());
		cargoMmList.add(cargoMm);

		return cargoMmList;
	}

	private TCkCtTripCargoMm prepareCargoMM(String userId, JobRecord jobRecord, TCkCtTrip trip) throws Exception {

		Date now = new Date();

		TCkCtTripCargoMm cargoMm = new TCkCtTripCargoMm(
				CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_CRG_FM_PREFIX), trip);

		cargoMm.setTCkCtMstCargoType(this.getCargoType(jobRecord));

		cargoMm.setCgStatus(Constant.ACTIVE_STATUS);
		cargoMm.setCgDtCreate(now);
		cargoMm.setCgUidCreate(userId);
		cargoMm.setCgCargoVolume(jobRecord.getCgCargoVolume());
		cargoMm.setCgCargoLength(jobRecord.getCgCargoLength());
		cargoMm.setCgCargoWidth(jobRecord.getCgCargoWidth());
		if (StringUtils.isNotBlank(jobRecord.getCgCargoWeightUom())) {
			TCkCtMstUomWeight tCkCtMstUomWeight = ckCtMstUomWeightDao.getWeightUomByDesc(jobRecord.getCgCargoWeightUom());
			if (Objects.nonNull(tCkCtMstUomWeight)) {
				if (cargoMm.getTCkCtMstUomWeight() == null) {
					cargoMm.setTCkCtMstUomWeight(new TCkCtMstUomWeight()); // Ensure initialization
				}
				cargoMm.getTCkCtMstUomWeight().setWeiId(tCkCtMstUomWeight.getWeiId());
			}
		}
		cargoMm.setCgCargoHeight(jobRecord.getCgCargoHeight());
		cargoMm.setCgCargoQty(jobRecord.getCgCargoQty());
		cargoMm.setCgCargoWeight(jobRecord.getCgCargoWeight());
		if (StringUtils.isNotBlank(jobRecord.getCgCargoSizeUom())) {
			TCkCtMstUomSize tCkCtMstUomSize = ckCtMstUomSizeDao.getSizeUomByDesc(jobRecord.getCgCargoSizeUom());
			if (Objects.nonNull(tCkCtMstUomSize)) {
				if (cargoMm.getTCkCtMstUomSize() == null) {
					cargoMm.setTCkCtMstUomSize(new TCkCtMstUomSize()); // Ensure initialization
				}
				cargoMm.getTCkCtMstUomSize().setSizId(tCkCtMstUomSize.getSizId());
			}
		}
		if (StringUtils.isNotBlank(jobRecord.getCgCargoVolumeUom())) {
			TCkCtMstUomVolume tCkCtMstUomVolume = ckCtMstUomVolumeDao.getVolumeUomByDesc(jobRecord.getCgCargoVolumeUom());
			if (Objects.nonNull(tCkCtMstUomVolume)) {
				if (cargoMm.getTCkCtMstUomVolume() == null) {
					cargoMm.setTCkCtMstUomVolume(new TCkCtMstUomVolume()); // Ensure initialization
				}
				cargoMm.getTCkCtMstUomVolume().setVolId(tCkCtMstUomVolume.getVolId());
			}
		}
		cargoMm.setCgCargoMarksNo(jobRecord.getCgCargoMarksNo());
		cargoMm.setCgCargoDesc(jobRecord.getDescription());
		cargoMm.setCgCargoSpecialInstn(jobRecord.getCgCargoSpecialInstn());

		return cargoMm;
	}

	private TCkCtMstCargoType getCargoType(JobRecord jobRecord) {
		if (jobRecord == null || StringUtils.isBlank(jobRecord.getCargoType())) {
			return null;
		}

		try {
			String typeValue = CargoTypes.valueOf(jobRecord.getCargoType().toUpperCase()).getDesc();
			return new TCkCtMstCargoType(typeValue);
		} catch (IllegalArgumentException e) {
			log.error("Invalid cargo type: {}", e);
		} catch (Exception e) {
			log.error("Unexpected error in getCargoType(): {}", e);
		}

		return null;
	}
	private void audit(String jobTruckId, String action, String userId) {
		Date now = Calendar.getInstance().getTime();
		try {

			TCoreAuditlog tCoreAuditlog = new TCoreAuditlog(null, "JOB " + action + " FROM USER UPLOAD", now, userId,
					jobTruckId);

			tCoreAuditlog.setAudtReckey(jobTruckId);
			tCoreAuditlog.setAudtRemoteIp("-");
			tCoreAuditlog.setAudtUname(userId);
			tCoreAuditlog.setAudtRemarks("JOB " + action + " FROM USER UPLOAD");
			tCoreAuditlog.setAudtParam1("TCKJOBTRUCK" + jobTruckId);
			auditLogDao.add(tCoreAuditlog);
		} catch (Exception e) {
			log.error("recordAudit", e);
		}
	}
	private TCkCtMstVehType getVehType(String vhtyIdOrName) throws Exception {
		
		// find by Id
		TCkCtMstVehType vehType = ckCtMstVehTypeDao.find(vhtyIdOrName);
		if( vehType != null) {
			return vehType;
		}
		
		// find by Name
		List<TCkCtMstVehType> vehTypeList = ckCtMstVehTypeDao.findByVhtyName(vhtyIdOrName);

		if (vehTypeList != null && vehTypeList.size() > 0) {
			return vehTypeList.get(0);
		}
		return null;
	}
	private void setFFCO(JobRecord jobRecord, TCoreAccn accnLogin, TCkJobTruck jobTruck, TCkJob job) {
		if ("DSVS".equalsIgnoreCase(accnLogin.getAccnId())
				&& "DSVS-WEITAO-BSH".equalsIgnoreCase(jobRecord.getContractId())) {
			// upload by DSV solution
			/*-
			Boolean isBSHco = jobRecord.getExtendAttrs().values().stream()
					.filter(v -> v.toUpperCase().indexOf("BSH") != -1).count() > 0;
			if (isBSHco) {
				job.setTCoreAccnByJobSlAccn(new TCoreAccn("BSH", null, ' ', null));
			}
			*/
			job.setTCoreAccnByJobSlAccn(new TCoreAccn("BSH", null, ' ', null));
		}
	}
	private TCkCtContract getContractByFF(String FFAccnId, String TOAccnId) throws Exception {
		String hql = "FROM TCkCtContract o where o.TCoreAccnByConCoFf.accnId=:ffAccnId AND o.TCoreAccnByConTo.accnId=:toAccnId AND conStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("ffAccnId", FFAccnId);
		params.put("toAccnId", TOAccnId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCkCtContract> ckCtContracts = ckCtContractDao.getByQuery(hql,params);
		if (ckCtContracts != null && !ckCtContracts.isEmpty()){
			return ckCtContracts.get(0);
		}
		return null;
	}
	public String getSysParam(String key, String defaultVal) throws Exception {
		if (StringUtils.isBlank(key)) {
			throw new com.vcc.camelone.common.exception.ParameterException("param key null or empty");
		}
		TCoreSysparam sysParam = coreSysparamDao.find(key);
		return (sysParam != null && StringUtils.isNotBlank(sysParam.getSysVal()))
				? sysParam.getSysVal()
				: defaultVal;
	}
}
