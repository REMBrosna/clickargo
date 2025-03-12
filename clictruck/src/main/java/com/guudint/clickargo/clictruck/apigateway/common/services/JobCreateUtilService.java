package com.guudint.clickargo.clictruck.apigateway.common.services;

import com.guudint.clicdo.common.IClicTruckConstant;
import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractDao;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.apigateway.common.AbstractApiGatewayService;
import com.guudint.clickargo.clictruck.apigateway.dto.*;
import com.guudint.clickargo.clictruck.common.constant.CkCtLocationConstant;
import com.guudint.clickargo.clictruck.common.constant.CkCtLocationConstant.LocationId;
import com.guudint.clickargo.clictruck.common.dao.CkCtEpodTemplateDao;
import com.guudint.clickargo.clictruck.common.dao.CkCtLocationDao;
import com.guudint.clickargo.clictruck.common.dao.CkCtVehDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtEpodTemplate;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.constant.TruckJobSourceEnum;
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
import com.guudint.clickargo.common.dao.CkRecordDateDao;
import com.guudint.clickargo.common.model.TCkRecordDate;
import com.guudint.clickargo.common.service.ICkSession;
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
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.util.PrincipalUtilService;
import com.vcc.camelone.util.email.SysParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.guudint.clickargo.clictruck.dto.JobLoading.isValidLoading;
import static com.guudint.clickargo.clictruck.dto.JobSubType.isValidJobSubType;

/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
@Service
public class JobCreateUtilService extends AbstractApiGatewayService<Job> {
    // Static Attributes
    ////////////////////
    private static Logger log = Logger.getLogger(JobCreateUtilService.class);
    private static final String FULL_TRUCK_LOAD = "FTL";
    private static final String JOB_SUB_TYPE = "LOCAL";
    private static final SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
    private CkCtMstVehTypeDao ckCtMstVehTypeDao;
    @Autowired
    private CkCtVehDao ckCtVehDao;
    @Autowired
    private GenericDao<TCoreUsr, String> coreUserDao;
    @Autowired
    private PrincipalUtilService principalUtilService;
    @Autowired
    private CkCtEpodTemplateDao ckCtEpodTemplateDao;
    @Autowired
    private CkCtMstVehTypeDao CkCtMstVehTypeDao;
    @Autowired
    @Qualifier("coreSysparamDao")
    private GenericDao<TCoreSysparam, String> sysParamDao;
    @Autowired
    private CargoServiceImpl cargoService;
    @Autowired
    private EpodServiceImpl epodService;
    @Autowired
    private ICkSession ckSession;
    @Override
    @Transactional
    public Object create(String accnId, Object t) throws Exception {
        Principal principal = ckSession.getPrincipal();
        return this.createJob(principal.getUserId(), accnId, (Job)t);
    }
    @Transactional
    public TCkJobTruck createJob(String userId, String accountId, Job jobReq) throws Exception {

        // TCkJobTruck
        TCkJobTruck jobTruck = this.prepareJobTruck(userId, accountId, jobReq);

        ckRecordDateDao.add(jobTruck.getTCkJob().getTCkRecordDate());
        ckJobDao.add(jobTruck.getTCkJob());

        ckCtContactDetailDao.add(jobTruck.getTCkCtContactDetailByJobContactCoFf());
        ckCtContactDetailDao.add(jobTruck.getTCkCtContactDetailByJobContactTo());
        jobTruckDao.add(jobTruck);

        createTrip(userId, accountId, jobReq, jobTruck); // trip seq from 0;
        addExtendAttributes(jobTruck, jobReq, userId, accountId);
        audit(jobTruck.getJobId(), "CREATED", userId);
        return jobTruck;
    }

    @Transactional
    public void addExtendAttributes(TCkJobTruck jobTruck, Job job, String userId, String accnId) throws Exception {
        // Retrieve the value of the "epodId" field from the Job instance
        Field epodIdField = Job.class.getDeclaredField("epodId");
        epodIdField.setAccessible(true);
        Object epodIdValue = epodIdField.get(job);

        if (epodIdValue != null) {
            TCkJobTruckExt epodExt = new TCkJobTruckExt();
            epodExt.setJextId(CkUtil.generateId(TCkJobTruckExt.PREFIX_ID) + UUID.randomUUID().toString().substring(0, 5));
            epodExt.setJextKey(epodIdField.getName());
            epodExt.setJextVal(epodIdValue.toString());
            epodExt.setTCkJobTruck(jobTruck);
            epodExt.setTCoreAccn(new TCoreAccn(accnId, null, ' ', null));
            epodExt.setJextStatus(Constant.ACTIVE_STATUS);
            epodExt.setJextDtCreate(new Date());
            epodExt.setJextUidCreate(userId);
            ckJobTruckExtDao.add(epodExt);
        }
        JobDetails details = job.getDetails();
        if (details != null) {
            Field[] fields = JobDetails.class.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = field.get(details);

                if (fieldValue != null) {
                    String key = field.getName();

                    if ("containers".equals(key)) {
                        List<Container> containers = (List<Container>) fieldValue;
                        Field[] containerFields = Container.class.getDeclaredFields();

                        int seq = 1;

                        for (Container container : containers) {
                            for (Field conField : containerFields) {
                                conField.setAccessible(true);
                                Object conValue = conField.get(container);

                                if (conValue != null) {
                                    String conKey = key.concat("."+conField.getName());
                                    String conValueStr = conValue.toString();

                                    // Concatenate the sequence with the key (same seq for all fields in one container)
                                    String concatenatedKey = conKey + "_" + seq;

                                    TCkJobTruckExt ext = new TCkJobTruckExt();
                                    ext.setJextId(CkUtil.generateId(TCkJobTruckExt.PREFIX_ID) + UUID.randomUUID().toString().substring(0, 5));
                                    ext.setJextKey(concatenatedKey); // Use the concatenated key
                                    ext.setJextVal(conValueStr);
                                    ext.setTCkJobTruck(jobTruck);
                                    ext.setTCoreAccn(new TCoreAccn(accnId, null, ' ', null));
                                    ext.setJextStatus(Constant.ACTIVE_STATUS);
                                    ext.setJextDtCreate(new Date());
                                    ext.setJextUidCreate(userId);

                                    // Persist the TCkJobTruckExt object
                                    ckJobTruckExtDao.add(ext);
                                }
                            }
                            seq++;
                        }
                    } else {
                        TCkJobTruckExt ext = new TCkJobTruckExt(CkUtil.generateId(TCkJobTruckExt.PREFIX_ID)
                                + UUID.randomUUID().toString().substring(0, 5), key, fieldValue.toString());
                        ext.setTCkJobTruck(jobTruck);
                        ext.setTCoreAccn(new TCoreAccn(accnId, null, ' ', null));
                        ext.setJextStatus(Constant.ACTIVE_STATUS);
                        ext.setJextDtCreate(new Date());
                        ext.setJextUidCreate(userId);
                        ckJobTruckExtDao.add(ext);
                    }
                }
            }
        }
    }
    private TCkJobTruck prepareJobTruck(String userId, String accountId, Job jobReq) throws Exception {
        Principal principal = principalUtilService.getPrincipal();
        //Check validation before create job
        Map<String, Object> validationErrors = validateJobRequest(jobReq, accountId);
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        String jobId = CkUtil.generateId(ICkConstant.PREFIX_PARENT_JOB);
        String jobTruckId = CkUtil.generateId(IClicTruckConstant.PREFIX_CK_TRUCK_JOB);

        // login Account id
        TCoreAccn accnLogin = ckCoreAccnDao.find(accountId);
        TCoreAccn accnCo = null;
        TCoreAccn accnTo = null;

        // Validate and fetch contract
        TCkCtContract contract = ckCtContractDao.findByConId(jobReq.getContractId())
                .orElseThrow(() -> new ParameterException("Invalid Contract ID: " + jobReq.getContractId()));

        if (AccountTypes.ACC_TYPE_TO.name().equalsIgnoreCase(accnLogin.getTMstAccnType().getAtypId())) {
            if (accnLogin.getAccnId().equalsIgnoreCase(contract.getTCoreAccnByConTo().getAccnId())) {
                accnTo = accnLogin;
                accnCo = contract.getTCoreAccnByConCoFf();
            } else {
                throw new ParameterException(
                        String.format("Contract %s, TO is not %s", jobReq.getContractId(), accnLogin.getAccnId()));
            }
        } else {
            if (accnLogin.getAccnId().equalsIgnoreCase(contract.getTCoreAccnByConCoFf().getAccnId())) {
                accnCo = accnLogin;
                accnTo = contract.getTCoreAccnByConTo();
            } else {
                throw new ParameterException(
                        String.format("Contract %s, CO/FF is not %s", jobReq.getContractId(), accnLogin.getAccnId()));
            }
        }

        log.info("CO: " + accnCo.getAccnId() + " TO: " + accnTo.getAccnId());

        // TCkRecordDate
        TCkRecordDate jobRecordDate = new TCkRecordDate();
        jobRecordDate.setRcdId(CkUtil.generateId());
        jobRecordDate.setRcdDtSubmit(new Date());

        // TCkJob
        TCkJob job = new TCkJob();
        job.setTCkRecordDate(jobRecordDate);
        job.setJobId(jobId);
        if (accnLogin.getTMstAccnType().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())){
            job.setTCkMstJobState(new TCkMstJobState(JobStates.ACP.name(), JobStates.ACP.name()));
        }else {
            job.setTCkMstJobState(new TCkMstJobState(JobStates.SUB.name(), JobStates.SUB.name()));
        }
        if (StringUtils.isBlank(jobReq.getLoading())){
            job.setJobLoading(FULL_TRUCK_LOAD);
        }else {
            job.setJobLoading(jobReq.getLoading());
        }

        if (StringUtils.isBlank(jobReq.getJobSubType())){
            job.setJobSubType(JOB_SUB_TYPE);
        }else {
            job.setJobSubType(jobReq.getJobSubType());
        }

        ShipmentTypes shipType = ShipmentTypes.DOMESTIC;
        job.setTCkMstShipmentType(new TCkMstShipmentType(shipType.getId(), null));
        job.setTCkMstJobType(new TCkMstJobType(JobTypes.TRKO.name(), null));
        job.setTCoreAccnByJobToAccn(accnTo);
        job.setTCoreAccnByJobOwnerAccn(accnCo);

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
        TCkCtMstVehType mstVehType = null;
        // Truck
        TCkCtVeh veh = null;
        if (jobReq.getTruckType() != null) {
            mstVehType = this.getVehType(jobReq.getTruckType());
        }
        // Validate truck type consistency across company vehicles
        if (mstVehType != null) {
            ArrayList<String> vehTypeList = new ArrayList<>();
            List<TCkCtVeh> companyVehList = ckCtVehDao.findVehTypeByCompany(accnTo.getAccnId());
            companyVehList.stream()
                    .map(v -> v.getTCkCtMstVehType().getVhtyId())
                    .filter(vhtyId -> !vhtyId.isEmpty())
                    .forEach(vehTypeList::add);
        }
        // TCkJobTruck
        TCkJobTruck jobTruck = new TCkJobTruck();

        jobTruck.setJobId(jobTruckId);
        jobTruck.setTCkJob(job);
        jobTruck.setTCkCtContactDetailByJobContactTo(tContactTo);
        jobTruck.setTCkCtContactDetailByJobContactCoFf(tContactCo);

        jobTruck.setTCoreAccnByJobPartyCoFf(accnCo);
        jobTruck.setTCoreAccnByJobPartyTo(accnTo);

        jobTruck.setJobDtBooking(yyyy_MM_dd.parse(jobReq.getBookingDate()));
        jobTruck.setJobDtPlan(yyyy_MM_dd.parse(jobReq.getPlanDate()));

        jobTruck.setJobCustomerRef(jobReq.getCustomerRef());
        jobTruck.setJobShipmentRef(jobReq.getShipmentRef());

        jobTruck.setJobTotalCharge(BigDecimal.ONE);

        // new CkTruckMiscMobileService().checkAndSetFinanceOptions()
        jobTruck.setJobIsFinanced('N');
        // new CkTruckMiscMobileService().checkAndSetMobileEnable()
        jobTruck.setJobMobileEnabled('Y');
        jobTruck.setJobSource(TruckJobSourceEnum.API.name());
        jobTruck.setTCkCtMstVehType(mstVehType);
        jobTruck.setJobStatus(Constant.ACTIVE_STATUS);
        jobTruck.setJobUidCreate(userId);
        jobTruck.setJobDtCreate(new Date());
        jobTruck.setJobDtLupd(new Date()); // order by this field in frontEnd.

        return jobTruck;
    }

    @SuppressWarnings("unused")
    private void createTrip(String userId, String accountId, Job jobRecord,
                                 TCkJobTruck jobTruck) throws Exception {
        String accnToId = jobTruck.getTCoreAccnByJobPartyTo().getAccnId();

        LocationDetails pickUp = jobRecord.getPickUp();
        List<LocationDetails> destinations = jobRecord.getDestinations();

        String fromLocName = pickUp.getLocationName();
        String fromLocNameDetails = pickUp.getLocationDetails();

        // From location;
        TCkCtLocation fromLoc = null;
        fromLoc = ckCtLocationDao.findByLocationName(pickUp.getLocationName(), accnToId).orElse(null);
        if (Objects.isNull(fromLoc)){
            fromLoc = ckCtLocationDao.findByAddressAndCompany(pickUp.getLocationDetails(), accnToId).orElse(null);
        }
        if (Objects.isNull(fromLoc)){
            // create new location;
            fromLoc = createLocation(jobTruck.getTCoreAccnByJobPartyTo(), LocationId.ADDRESS, fromLocName, pickUp.getLocationName());
        }
        TCkCtTripLocation tripLocFrom = new TCkCtTripLocation();
        tripLocFrom.setTlocId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIPLOC_PREFIX));
        tripLocFrom.setTCkCtLocation(fromLoc);
        tripLocFrom.setTlocLocAddress(fromLoc.getLocAddress());
        tripLocFrom.setTlocLocName(fromLoc.getLocName());
        tripLocFrom.setTlocLocGps(fromLoc.getLocGps());
        tripLocFrom.setTlocStatus(Constant.ACTIVE_STATUS);
        tripLocFrom.setTlocDtCreate(new Date());
        tripLocFrom.setTlocRemarks(pickUp.getRemark());
        tripLocFrom.setTlocMobileNo(pickUp.getMobileNo());
        if (pickUp.getDatetime() != null){
            Date date = yyyyMMddHHmmss.parse(pickUp.getDatetime());
            tripLocFrom.setTlocDtLoc(date);
        }
        ckCtTripLocationDao.saveOrUpdate(tripLocFrom);

        List<LocationDetails> locationDetailsList = jobRecord.getDestinations();
        int tripSeq = 0;
        for (LocationDetails currentDestination : locationDetailsList){

            // To location;
            TCkCtLocation toLoc = null;
            toLoc = ckCtLocationDao.findByLocationName(currentDestination.getLocationName(), accnToId).orElse(null);
            if (toLoc == null) {
                toLoc = ckCtLocationDao.findByAddressAndCompany(currentDestination.getLocationDetails(),
                        accnToId).orElse(null);
            }
            if (toLoc == null) {
                toLoc = this.createLocation(jobTruck.getTCoreAccnByJobPartyTo(), LocationId.ADDRESS,
                        currentDestination.getLocationName(), currentDestination.getLocationDetails());
            }
            // TCkCtTripLocation
            TCkCtTripLocation tripLocTo = new TCkCtTripLocation();
            tripLocTo.setTlocId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIPLOC_PREFIX));
            tripLocTo.setTCkCtLocation(toLoc);
            if(currentDestination.getDatetime() != null){
                Date date = yyyyMMddHHmmss.parse(currentDestination.getDatetime());
                tripLocTo.setTlocDtLoc(date);
            }
            tripLocTo.setTlocMobileNo(currentDestination.getMobileNo());
            tripLocTo.setTlocRemarks(currentDestination.getRemark());
            tripLocTo.setTlocLocName(toLoc.getLocName());
            tripLocTo.setTlocLocAddress(toLoc.getLocAddress());
            tripLocTo.setTlocLocGps(toLoc.getLocGps());
            tripLocTo.setTlocStatus(Constant.ACTIVE_STATUS);
            tripLocTo.setTlocDtCreate(new Date());
            ckCtTripLocationDao.saveOrUpdate(tripLocTo);

            // TCkCtTripCharge
            TCkCtTripCharge tripCharge = new TCkCtTripCharge(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_CHARGE_PREFIX), 'Y');
            tripCharge.setTcStatus(Constant.ACTIVE_STATUS);
            tripCharge.setTcDtCreate(new Date());
            ckCtTripChargeDao.add(tripCharge);

            // TCkCtTrip
            TCkCtTrip trip = new TCkCtTrip(CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_PREFIX));
            trip.setTCkJobTruck(jobTruck);
            trip.setTCkCtTripLocationByTrFrom(tripLocFrom);
            trip.setTCkCtTripLocationByTrTo(tripLocTo);
            trip.setTCkCtTripCharge(tripCharge);
            trip.setTrSeq(tripSeq);
            trip.setTrChargeOpen('Y');
            trip.setTrStatus(Constant.ACTIVE_STATUS);
            trip.setTrDtCreate(new Date());
            ckCtTripDao.add(trip);

            // save cargo inside this method
            this.createCargoMM(userId, currentDestination, trip);

            // TCkCtTripDo
            TCkCtTripDo ckCtTripDo = new TCkCtTripDo();
            ckCtTripDo.setDoId(CkUtil.generateId(ICkConstant.PREFIX_JOB_ATT));
            ckCtTripDo.setTCkCtTrip(trip);
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
            ckCtTripDoDao.saveOrUpdate(ckCtTripDo);
            tripSeq++;
        }
    }
    private TCkCtLocation createLocation(TCoreAccn toAccn, String locType, String locName, String locAddress) throws Exception {
        TCkCtLocation loc = new TCkCtLocation();
        // Create a new location
        loc.setLocId(CkUtil.generateId(CkCtLocationConstant.Prefix.PREFIX_CK_CT_LOCATION));
        TCkCtMstLocationType tCkCtMstLocationType = new TCkCtMstLocationType();
        tCkCtMstLocationType.setLctyId(locType);
        loc.setTCkCtMstLocationType(tCkCtMstLocationType);
        loc.setTCoreAccn(toAccn);
        loc.setLocAddress(locAddress);
        loc.setLocName(locName);
        loc.setLocDtStart(new Date());
        loc.setLocDtEnd(new Date());
        loc.setLocStatus('A');
        loc.setLocDtCreate(new Date());
        loc.setLocUidCreate("SYS");
        // Save the new location to the database
        ckCtLocationDao.add(loc);
        return loc;
    }

    private void createCargoMM(String userId, LocationDetails currentDestination,
                                                 TCkCtTrip trip) throws Exception {
        List<Cargo> dropOffCargos = currentDestination.getCargos();

        for (Cargo cargo : dropOffCargos) {
            Date now = new Date();
            // Generate unique ID for the cargo entry
            String id = CkUtil.generateIdSynch(CkJobTruckServiceUtil.TRIP_CRG_FM_PREFIX) + UUID.randomUUID().toString().substring(0, 5);

            TCkCtTripCargoMm cargoObject = new TCkCtTripCargoMm(id, trip);
            TCkCtMstCargoType tCkCtMstCargoType = new TCkCtMstCargoType();
            tCkCtMstCargoType.setCrtypId(cargo.getType());
            cargoObject.setTCkCtMstCargoType(tCkCtMstCargoType);
            if (StringUtils.isNotBlank(cargo.getVolume())){
                cargoObject.setCgCargoVolume(Double.parseDouble(cargo.getVolume()));
            }
            if(StringUtils.isNotBlank(cargo.getVolumeUOM())){
                TCkCtMstUomVolume tCkCtMstUomVolume = new TCkCtMstUomVolume();
                tCkCtMstUomVolume.setVolId(cargo.getVolumeUOM());
                cargoObject.setTCkCtMstUomVolume(tCkCtMstUomVolume);
            }
            if (StringUtils.isNotBlank(cargo.getQuantity())){
                cargoObject.setCgCargoQty(Double.parseDouble(cargo.getQuantity()));
            }
            if (StringUtils.isNotBlank(cargo.getQuantityUOM())){
                cargoObject.setCgCargoQtyUom(cargo.getQuantityUOM());
            }
            if (StringUtils.isNotBlank(cargo.getWeight())){
                cargoObject.setCgCargoWeight(Double.parseDouble(cargo.getWeight()));
            }
            if (StringUtils.isNotBlank(cargo.getWeightUOM())){
                TCkCtMstUomWeight tCkCtMstUomWeight = new TCkCtMstUomWeight();
                tCkCtMstUomWeight.setWeiId(cargo.getWeightUOM());
                cargoObject.setTCkCtMstUomWeight(tCkCtMstUomWeight);
            }
            if (StringUtils.isNotBlank(cargo.getWidth())){
                cargoObject.setCgCargoWidth(Double.parseDouble(cargo.getWidth()));
            }
            if (StringUtils.isNotBlank(cargo.getHeight())){
                cargoObject.setCgCargoHeight(Double.parseDouble(cargo.getHeight()));
            }
            if (StringUtils.isNotBlank(cargo.getLength())){
                cargoObject.setCgCargoLength(Double.parseDouble(cargo.getLength()));
            }
            if (StringUtils.isNotBlank(cargo.getSizeUOM())){
                TCkCtMstUomSize tCkCtMstUomSize = new TCkCtMstUomSize();
                tCkCtMstUomSize.setSizId(cargo.getSizeUOM());
                cargoObject.setTCkCtMstUomSize(tCkCtMstUomSize);
            }
            cargoObject.setCgCargoDesc(cargo.getDescription());
            cargoObject.setCgCargoMarksNo(cargo.getMarksAndNo());
            cargoObject.setCgCargoSpecialInstn(cargo.getSpecialInstruction());
            cargoObject.setCgStatus(Constant.ACTIVE_STATUS);
            cargoObject.setCgDtCreate(now);
            cargoObject.setCgUidCreate(userId);

            ckCtTripCargoMmDao.add(cargoObject);
        }
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
    private Date validDateFormat(String dateStr, String fieldName) throws Exception {
        try {
            return yyyy_MM_dd.parse(dateStr);
        } catch (ParseException e) {
            throw new ParameterException(String.format("Invalid format for '%s'. Expected format is 'yyyy-MM-dd'. Provided value: '%s'.", fieldName, dateStr));
        }
    }
    private Date validDateFormatTwo(String dateStr, String fieldName) throws Exception {
        try {
            return yyyyMMddHHmmss.parse(dateStr);
        } catch (ParseException e) {
            throw new ParameterException(String.format("Invalid format for '%s'. Expected format is 'yyyy-MM-dd HH:mm:ss'. Provided value: '%s'.", fieldName, dateStr));
        }
    }
    private TCkCtMstVehType getVehType(String vehTypeId) throws Exception {

        return ckCtMstVehTypeDao.find(vehTypeId);
    }

    private Map<String, Object> validateJobRequest(Job jobReq, String accountId) throws Exception {
        Date now = Calendar.getInstance().getTime();
        String currentDay = yyyy_MM_dd.format(now);
        Date todayDate = yyyy_MM_dd.parse(currentDay);

        Map<String, Object> validationErrors = new HashMap<>();
        // Validate contractId
        if (StringUtils.isBlank(jobReq.getContractId())) {
            validationErrors.put("contractId", "Contract is required! Enter the Contract ID that the job is related to.");
        }
        // Validate shipmentRef
        if (StringUtils.isBlank(jobReq.getShipmentRef())) {
            validationErrors.put("shipmentRef", "Shipment Ref is required! Enter the Shipment Reference Number.");
        }
        // Validate loading type
        if (StringUtils.isBlank(jobReq.getLoading())) {
            validationErrors.put("loading", "Loading is required! Enter FTL or LTL.");
        }
        if (StringUtils.isNotBlank(jobReq.getLoading()) && !isValidLoading(jobReq.getLoading())) {
            validationErrors.put("loading", String.format("Invalid loading provided: %s. Enter FTL or LTL.", jobReq.getLoading()));
        }
        if (StringUtils.isBlank(jobReq.getJobSubType())) {
            validationErrors.put("jobSubType", "Job SubType is required!");
        }
        if (StringUtils.isNotBlank(jobReq.getJobSubType()) && !isValidJobSubType(jobReq.getJobSubType())){
            validationErrors.put("jobSubType", String.format("Invalid job subType provided: %s.", jobReq.getJobSubType()));
        }
        // Validate bookingDate
        if (StringUtils.isBlank(jobReq.getBookingDate())) {
            validationErrors.put("bookingDate", "Booking Date is required! Format is either YYYY-MM-DD.");
        }
        if (StringUtils.isNotBlank(jobReq.getBookingDate())){
            Date bookingDate = validDateFormat((jobReq.getBookingDate()), "bookingDate");
            if (bookingDate.before(todayDate)) {
                validationErrors.put("bookingDate", "Booking Date cannot be earlier than today.");
            }
        }
        // Validate planDate
        if (StringUtils.isBlank(jobReq.getPlanDate())) {
            validationErrors.put("planDate", "Plan Date is required! Format is either YYYY-MM-DD.");
        }
        if (StringUtils.isNotBlank(jobReq.getPlanDate())){
            Date planDate = validDateFormat((jobReq.getPlanDate()), "planDate");
            if (planDate.before(todayDate)) {
                validationErrors.put("planDate", "Plan Date cannot be earlier than today.");
            }
        }
        // Validate truckType
        if (StringUtils.isBlank(jobReq.getTruckType())) {
            validationErrors.put("truckType", "Truck Type is required!");
        }
        if (StringUtils.isNotBlank(jobReq.getTruckType()) && Objects.isNull(CkCtMstVehTypeDao.find(jobReq.getTruckType()))){
            validationErrors.put("truckType", "Invalid Truck Type provided : " + jobReq.getTruckType());
        }

        // Validate pickUp and its nested fields
        if (Objects.isNull(jobReq.getPickUp()) || isPickUpEmpty(jobReq.getPickUp())) {
            validationErrors.put("pickUp", "Pick up is required and cannot be empty!");
        }  else {
            Map<String, String> pickUpErrors = new HashMap<>();
            if (StringUtils.isBlank(jobReq.getPickUp().getDatetime())){
                pickUpErrors.put("datetime", "Pick up date time name is required!");
            }
            if (StringUtils.isBlank(jobReq.getPickUp().getLocationName())){
                pickUpErrors.put("locationName", "Pick up location name is required!");
            }
            if (StringUtils.isNotBlank(jobReq.getPickUp().getDatetime())){
                Date pickUpDate = validDateFormatTwo(jobReq.getPickUp().getDatetime(), "datetime");
                if (pickUpDate.before(todayDate)) {
                    pickUpErrors.put("datetime", "Pick Date cannot be earlier than today.");
                }
            }
            if (!pickUpErrors.isEmpty()) {
                validationErrors.put("pickUp", pickUpErrors);
            }
        }
        // Validate destinations
        if (jobReq.getDestinations() == null || jobReq.getDestinations().isEmpty()) {
            validationErrors.put("destinations", "At least one destination is required!");
        } else {
            Map<String, String> destinationErrors = new HashMap<>();
            List<TCkCtMstCargoType> cargoTypeList = cargoService.cargoTypeList();
            for (LocationDetails destination : jobReq.getDestinations()) {
                if (StringUtils.isNotBlank(destination.getDatetime())){
                    Date dropOff = validDateFormatTwo(destination.getDatetime(), "datetime");
                    if (dropOff.before(todayDate)) {
                        destinationErrors.put("datetime", "Destinations cannot be earlier than today.");
                    }
                }
                for (Cargo dto : destination.getCargos()){
                    boolean foundMatch = false;
                    for (TCkCtMstCargoType entity : cargoTypeList){
                        if (entity.getCrtypId().equals(dto.getType())){
                            foundMatch = true;
                            break;
                        }
                    }
                    if (!foundMatch){
                        destinationErrors.put("cargos", "Invalid cargo type : " + dto.getType());
                    }
                }
                if ( StringUtils.isBlank(destination.getLocationName())) {
                    destinationErrors.put("locationName", "Destination location name is required! Enter the destination name.");
                }
                if (StringUtils.isBlank(destination.getLocationDetails())) {
                    destinationErrors.put("locationDetails", "Destination location is required! Enter the destination address.");
                }
                if (StringUtils.isBlank(destination.getMobileNo())) {
                    destinationErrors.put("mobileNo", "Mobile Number is Required! Enter the cargo recipient's contact number.");
                }
            }
            if (!destinationErrors.isEmpty()) {
                validationErrors.put("destinations", destinationErrors);
            }
        }
        //EPOD ID
        if (StringUtils.isBlank(jobReq.getEpodId())) {
            validationErrors.put("epodId", "ePOD ID cannot be empty!");
        }
        if (StringUtils.isNotBlank(jobReq.getEpodId()) && !getSpecificTemplate(jobReq.getEpodId(), accountId)) {
            validationErrors.put("epodId", "Invalid ePOD ID provided: " + jobReq.getEpodId());
        }
        return validationErrors;
    }
    private boolean isPickUpEmpty(LocationDetails pickUp) {
        return (Objects.isNull(pickUp.getLocationName()) || StringUtils.isBlank(pickUp.getLocationName())) &&
                (Objects.isNull(pickUp.getDatetime()) || StringUtils.isBlank(pickUp.getDatetime())) &&
                (Objects.isNull(pickUp.getMobileNo()) || StringUtils.isBlank(pickUp.getMobileNo())) &&
                (Objects.isNull(pickUp.getRemark()) || StringUtils.isBlank(pickUp.getRemark())) &&
                (Objects.isNull(pickUp.getDriverComment()) || StringUtils.isBlank(pickUp.getDriverComment())) &&
                (Objects.isNull(pickUp.getCargos()) || pickUp.getCargos().isEmpty());
    }
    private boolean getSpecificTemplate(String epodId, String accountId) throws Exception {
        List<TCkCtEpodTemplate> epodTemplates = epodService.getListEpodTemplate(accountId);
        for (TCkCtEpodTemplate entity : epodTemplates) {
            if (epodId.equals(entity.getEpodId())) {
                return true; // Match found
            }
        }
        return false;
    }
}
