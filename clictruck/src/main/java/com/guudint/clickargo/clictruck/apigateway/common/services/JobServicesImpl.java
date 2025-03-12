package com.guudint.clickargo.clictruck.apigateway.common.services;

import com.google.gson.JsonObject;
import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractDao;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.apigateway.common.ApiGatewayService;
import com.guudint.clickargo.clictruck.apigateway.dto.*;
import com.guudint.clickargo.clictruck.common.dto.CkCtDept;
import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.service.impl.CkCtVehServiceImpl;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.guudint.clickargo.clictruck.planexec.job.service.IJobTruckStateService;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoMm;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripCargoMmService;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dto.CkRecordDate;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.model.TCkRecordDate;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.job.dao.CkJobRemarksDao;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.*;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.entity.AbstractEntityService;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.master.controller.PathNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class JobServicesImpl extends AbstractEntityService<TCkJobTruck, String, Job> implements ApiGatewayService<Job> {
    // Static Attributes
    ////////////////////
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(JobServicesImpl.class);
    private static final String AUDIT_TAG = "CK JOB TRUCK";
    private static final String TABLE_NAME = "T_CK_JOB_TRUCK";
    @Autowired
    private GenericDao<TCkJob, String> ckJobDao;
    @Autowired
    private GenericDao<TCkJobTruck, String> ckJobTruckDao;
    @Autowired
    private GenericDao<TCkCtTrip, String> ckCtTripDao;
    @Autowired
    private CkCtContractDao ckCtContractDao;
    @Autowired
    protected ICkSession ckSession;
    @Autowired
    private CkJobTruckServiceUtil ckJobTruckUtilService;
    @Autowired
    private JobCreateUtilService jobCreateUtilService;
    @Autowired
    private IJobTruckStateService<CkJobTruck> jobTruckStateService;
    @Autowired
    protected IEntityService<TCkJob, String, CkJob> ckJobService;
    @Autowired
    protected IEntityService<TCkRecordDate, String, CkRecordDate> ckRecordService;
    @Autowired
    private CkCtTripCargoMmService cargoMmService;
    @Autowired
    private IEntityService<TCkCtDrv, String, CkCtDrv> ckCtDrvService;
    @Autowired
    private CkCtVehServiceImpl ckCtVehService;
    @Autowired
    private IEntityService<TCkJobTruck, String, CkJobTruck> ckJobTruckService;
    @Autowired
    private CkJobRemarksDao ckJobRemarksDao;
    @Autowired
    private CkJobTruckExtDao ckJobTruckExtDao;

    @Autowired
    @Qualifier("coreAccDao")
    private GenericDao<TCoreAccn, String> coreAccDao;
    public JobServicesImpl() {
        super("ckJobTruckDao", AUDIT_TAG, JobServicesImpl.class.getName(), TABLE_NAME);
    }
    @Override
    @Transactional
    public Object create(String accnId, Object t) throws Exception {
        // Delegate creation logic
        Object job = jobCreateUtilService.create(accnId, t);
        // Convert entity to DTO
        return dtoFromEntity((TCkJobTruck) job);
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public TCkJobTruck initEnity(TCkJobTruck entity) throws ParameterException, ProcessingException {
        if (null != entity) {
            Hibernate.initialize(entity.getTCkJob());
            Optional.ofNullable(entity.getTCkJob().getTCkMstJobState()).ifPresent(Hibernate::initialize);
            Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobCoAccn()).ifPresent(Hibernate::initialize);
            Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobFfAccn()).ifPresent(Hibernate::initialize);
            Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobOwnerAccn()).ifPresent(Hibernate::initialize);
            Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobToAccn()).ifPresent(Hibernate::initialize);
            Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobSlAccn()).ifPresent(Hibernate::initialize);
            Hibernate.initialize(entity.getTCkCtContactDetailByJobContactTo());
            Hibernate.initialize(entity.getTCkCtContactDetailByJobContactCoFf());
            Hibernate.initialize(entity.getTCkCtDebitNoteByJobInvoiceeDebitNote());
            Hibernate.initialize(entity.getTCkCtDebitNoteByJobInvoicerDebitNote());
            Hibernate.initialize(entity.getTCkCtDrv());
            Hibernate.initialize(entity.getTCkCtMstVehType());
            Hibernate.initialize(entity.getTCkCtToInvoice());
            Hibernate.initialize(entity.getTCkCtVeh());
            Hibernate.initialize(entity.getTCoreAccnByJobPartyTo());
            Hibernate.initialize(entity.getTCoreAccnByJobPartyCoFf());
            Optional.ofNullable(entity.getTCkCtDeptByJobCoDepartment()).ifPresent(Hibernate::initialize);
            Optional.ofNullable(entity.getTCkCtDeptByJobToDepartment()).ifPresent(Hibernate::initialize);

        }
        return entity;
    }

    @Override
    protected TCkJobTruck entityFromDTO(Job job) throws ParameterException, ProcessingException {
        return null;
    }
    @Override
    protected String entityKeyFromDTO(Job job) throws ParameterException, ProcessingException {
        if (job == null) {
            throw new ParameterException("param dto null");
        }
        return job.getJobId();
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public TCkJobTruck updateEntity(ACTION attriubte, TCkJobTruck entity, Principal principal, Date date) throws ParameterException, ProcessingException {
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
                    entity.setJobUidCreate(opUserId.orElse("SYS"));
                    entity.setJobDtCreate(date);
                    entity.setJobUidCreate(opUserId.orElse("SYS"));
                    entity.setJobDtLupd(date);
                    break;

                case MODIFY:
                    entity.setJobUidLupd(opUserId.orElse("SYS"));
                    entity.setJobDtLupd(date);
                    break;
                case DELETE:
                    entity.setJobUidLupd(opUserId.orElse("SYS"));
                    entity.setJobDtLupd(date);
                    entity.setJobStatus('I');
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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public TCkJobTruck updateEntityStatus(TCkJobTruck entity, char status) throws ParameterException, ProcessingException {
        LOG.debug("updateEntityStatus");

        try {
            if (null == entity)
                throw new ParameterException("entity param null");

            entity.setJobStatus(status);
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
    @Transactional
    public Job dtoFromEntity(TCkJobTruck entity) throws ParameterException, ProcessingException {
        LOG.debug("dtoFromEntity");
        try {
            SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (entity == null) {
                throw new ParameterException("param entity null");
            }
            Job dto = new Job();
            dto.setJobId(entity.getJobId());
            String coFf = entity.getTCoreAccnByJobPartyCoFf().getAccnId();
            String to = entity.getTCoreAccnByJobPartyTo().getAccnId();
            List<TCkCtContract> findValidContract = ckCtContractDao.findValidContract(coFf, to);
            this.processContracts(findValidContract, dto);
            dto.setPlanDate(entity.getJobDtPlan() != null ? sdfShort.format(entity.getJobDtPlan()) : null);
            dto.setBookingDate(entity.getJobDtBooking() != null ? sdfShort.format(entity.getJobDtBooking()) : null);
            dto.setDeliveredDate(entity.getJobDtDelivery() != null ? sdfShort.format(entity.getJobDtDelivery()) : null);
            dto.setShipmentRef(entity.getJobShipmentRef());
            dto.setCustomerRef(entity.getJobCustomerRef());
            if (Objects.nonNull(entity.getTCkJob())){
                dto.setLoading(entity.getTCkJob().getJobLoading());
                dto.setJobSubType(entity.getTCkJob().getJobSubType());
                if (Objects.nonNull(entity.getTCkJob().getTCkMstJobState())){
                    String jobState = JobStatesEnum.fromStateName(entity.getTCkJob().getTCkMstJobState().getJbstId());
                    boolean status = ckJobRemarksDao.hasRemarks(entity.getTCkJob().getJobId());
                    if (status){
                        dto.setStatus(JobStatesEnum.REJ.getDesc());
                    }else {
                        dto.setStatus(jobState);
                    }
                }
            }
            if (Objects.nonNull(entity.getTCkCtMstVehType())){
                dto.setTruckType(entity.getTCkCtMstVehType().getVhtyId());
            }

            LocationDetails pickUp = new LocationDetails();
            ArrayList<LocationDetails> destinations = new ArrayList<>();
            getExtendAttributes(entity.getJobId(), dto);
            Driver driver = new Driver();
            Truck veh = new Truck();
            List<TCkCtTrip> jobTrip = getJobTrip(entity.getJobId());
            if (jobTrip != null) {
                // Consolidated cargo list for the pickup location
                ArrayList<Cargo> pickUpCargoList = new ArrayList<>();

                for (TCkCtTrip trip : jobTrip) {
                    if (trip != null) {

                        // Fetching cargo data for the trip
                        List<CkCtTripCargoMm> mmTripCargos = cargoMmService.findTripCargoFmmsByTripId(trip.getTrId());
                        ArrayList<Cargo> dropOffCargoList = new ArrayList<>();

                        if (mmTripCargos != null) {
                            mmTripCargos.forEach(cargo -> {
                                if (cargo != null) {
                                    Cargo object = new Cargo();
                                    object.setId(cargo.getCgId());
                                    object.setType(cargo.getTCkCtMstCargoType() != null ? cargo.getTCkCtMstCargoType().getCrtypId() : null);
                                    object.setVolume(String.valueOf(cargo.getCgCargoVolume()));
                                    object.setVolumeUOM(String.valueOf(cargo.getTCkCtMstUomVolume() != null ? cargo.getTCkCtMstUomVolume().getVolId() : ""));
                                    object.setWeight(String.valueOf(cargo.getCgCargoWeight()));
                                    object.setWeightUOM(String.valueOf(cargo.getTCkCtMstUomWeight() != null ? cargo.getTCkCtMstUomWeight().getWeiId() : ""));
                                    object.setLength(String.valueOf(cargo.getCgCargoLength()));
                                    object.setWidth(String.valueOf(cargo.getCgCargoWidth()));
                                    object.setHeight(String.valueOf(cargo.getCgCargoHeight()));
                                    object.setSizeUOM(cargo.getTCkCtMstUomSize() != null ? cargo.getTCkCtMstUomSize().getSizId() : "");
                                    object.setQuantity(String.valueOf(cargo.getCgCargoQty()));
                                    object.setQuantityUOM(String.valueOf(cargo.getCgCargoQtyUom()));
                                    object.setMarksAndNo(cargo.getCgCargoMarksNo());
                                    object.setDescription(cargo.getCgCargoDesc());
                                    object.setSpecialInstruction(cargo.getCgCargoSpecialInstn());

                                    // Add cargo to the drop-off specific list
                                    dropOffCargoList.add(object);

                                    // Add cargo to the consolidated pickup list
                                    pickUpCargoList.add(object);
                                }
                            });
                        }

                        // Processing the pickup location
                        TCkCtTripLocation fromLoc = trip.getTCkCtTripLocationByTrFrom();
                        if (fromLoc != null) {
                            pickUp.setLocationName(fromLoc.getTlocLocName());
                            pickUp.setLocationDetails(fromLoc.getTlocLocAddress());
                            pickUp.setDatetime(fromLoc.getTlocDtLoc() != null ? sdfLong.format(fromLoc.getTlocDtLoc()) : null);
                            pickUp.setMobileNo(fromLoc.getTlocMobileNo());
                            pickUp.setRemark(fromLoc.getTlocRemarks());
                            pickUp.setDriverComment(fromLoc.getTlocComment());
                            pickUp.setCargos(pickUpCargoList); // Assigning consolidated cargo list to pickup
                        }

                        // Processing the destination location
                        TCkCtTripLocation toLoc = trip.getTCkCtTripLocationByTrTo();
                        if (toLoc != null) {
                            LocationDetails destination = new LocationDetails();
                            destination.setLocationName(toLoc.getTlocLocName());
                            destination.setLocationDetails(toLoc.getTlocLocAddress());
                            destination.setDatetime(toLoc.getTlocDtLoc() != null ? sdfLong.format(toLoc.getTlocDtLoc()) : null);
                            destination.setMobileNo(toLoc.getTlocMobileNo());
                            destination.setRemark(toLoc.getTlocRemarks());
                            destination.setDriverComment(toLoc.getTlocComment());
                            destination.setCargos(dropOffCargoList); // Assigning drop-off specific cargo list
                            destinations.add(destination);
                        }
                    }
                }
            }

            if (Objects.nonNull(entity.getTCkCtDrv())){
                driver.setId(entity.getTCkCtDrv().getDrvId());
                driver.setCompanyId(entity.getTCkCtDrv().getTCoreAccn() != null ? entity.getTCkCtDrv().getTCoreAccn().getAccnId() : null);
                driver.setName(entity.getTCkCtDrv().getDrvName());
                driver.setLicenseNo(entity.getTCkCtDrv().getDrvLicenseNo());
                driver.setEmail(entity.getTCkCtDrv().getDrvEmail());
                driver.setMobileNo(entity.getTCkCtDrv().getDrvPhone());
            }

            if (entity.getTCkCtVeh() != null) {
                veh.setId(entity.getTCkCtVeh().getVhId());
                veh.setCompanyId(entity.getTCkCtVeh().getTCoreAccn() != null ? entity.getTCkCtVeh().getTCoreAccn().getAccnId() : null);
                veh.setType(entity.getTCkCtMstVehType() != null ? entity.getTCkCtMstVehType().getVhtyId() : null);
                veh.setLicenseNo(entity.getTCkCtVeh().getVhPlateNo());
                veh.setClazz(entity.getTCkCtVeh().getVhClass() != null ? entity.getTCkCtVeh().getVhClass() : 0);
                veh.setLength(entity.getTCkCtVeh().getVhLength() != null ? String.valueOf(entity.getTCkCtVeh().getVhLength()) : null);
                veh.setWidth(entity.getTCkCtVeh().getVhWidth() != null ? String.valueOf(entity.getTCkCtVeh().getVhWidth()) : null);
                veh.setHeight(entity.getTCkCtVeh().getVhHeight() != null ? String.valueOf(entity.getTCkCtVeh().getVhHeight()) : null);
                veh.setMaxWeight(entity.getTCkCtVeh().getVhWeight() != null ? String.valueOf(entity.getTCkCtVeh().getVhWeight()) : null);
                veh.setVolume(entity.getTCkCtVeh().getVhVolume() != null ? String.valueOf(entity.getTCkCtVeh().getVhVolume()) : null);
                veh.setRemark(entity.getTCkCtVeh().getVhRemarks());
                if (entity.getTCkCtVeh().getTCkCtMstChassisType() != null){
                    Chasis chasis = new Chasis();
                    chasis.setSize(entity.getTCkCtVeh().getTCkCtMstChassisType().getChtyId());
                    if (entity.getTCkCtVeh().getVhChassisNo() != null){
                        JsonObject json = ckCtVehService.getAsJson(entity.getTCkCtVeh().getVhChassisNo());
                        if (json == null){
                            chasis.setNumber(entity.getTCkCtVeh().getVhChassisNo());
                        } else {
                            String vhChassisNoOth = String.valueOf(json.get("vhChassisNoOth"));
                            chasis.setNumber(vhChassisNoOth);
                        }
                    }
                    veh.setChasis(chasis);
                }
            }
            dto.setPickUp(pickUp);
            dto.setDestinations(destinations);
            dto.setDriver(driver);
            dto.setTruck(veh);
            //load EPOD from job extension
            TCkJobTruckExt ckJobTruckExts = ckJobTruckExtDao.findByJobIdContainEpodId(entity.getJobId());
            dto.setEpodId(ckJobTruckExts != null ? ckJobTruckExts.getJextVal() : "");
            String rejectedRemark = null;
            if (Objects.nonNull(entity.getTCkJob())){
                rejectedRemark = ckJobRemarksDao.rejectedRemarks(entity.getTCkJob().getJobId());
            }
            dto.setRejReason(rejectedRemark);
            return dto;
        } catch (ParameterException ex) {
            LOG.error("dtoFromEntity", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("dtoFromEntity", ex);
            throw new ProcessingException(ex);
        }
    }
    private void processContracts(List<TCkCtContract> findValidContract, Job dto) {
        // Initialize the Contract object
        Contract object = new Contract();
        // Process contracts in parallel
        findValidContract.parallelStream().forEach(contract -> {
            if (contract != null) {
                object.setContractId(contract.getConId());
                object.setContractName(contract.getConName());
                // Process "To" company
                TCoreAccn conToAccn = contract.getTCoreAccnByConTo();
                if (conToAccn != null && conToAccn.getTMstAccnType() != null &&
                        AccountTypes.ACC_TYPE_TO.name().equalsIgnoreCase(conToAccn.getTMstAccnType().getAtypId())) {
                    Company conTo = new Company();
                    conTo.setId(conToAccn.getAccnId());
                    conTo.setName(conToAccn.getAccnName());
                    conTo.setType(AccountTypeEnum.ACC_TYPE_TO.getDesc());
                    object.setTo(conTo);
                }

                // Process "Co" company
                TCoreAccn conCoAccn = contract.getTCoreAccnByConCoFf();
                if (conCoAccn != null && conCoAccn.getTMstAccnType() != null &&
                        AccountTypes.ACC_TYPE_CO.name().equalsIgnoreCase(conCoAccn.getTMstAccnType().getAtypId())) {
                    Company conCo = new Company();
                    conCo.setId(conCoAccn.getAccnId());
                    conCo.setName(conCoAccn.getAccnName());
                    conCo.setType(AccountTypeEnum.ACC_TYPE_CO.getDesc());
                    object.setCo(conCo);
                }

                // Process "Ff" company
                if (conCoAccn != null && conCoAccn.getTMstAccnType() != null &&
                        AccountTypes.ACC_TYPE_FF.name().equalsIgnoreCase(conCoAccn.getTMstAccnType().getAtypId())) {
                    Company conFf = new Company();
                    conFf.setId(conCoAccn.getAccnId());
                    conFf.setName(conCoAccn.getAccnName());
                    conFf.setType(AccountTypeEnum.ACC_TYPE_CO.getDesc());
                    object.setFf(conFf);
                }
            }
        });

        // Set the Contract object in the Job DTO
        dto.setContract(object);
    }
    @Override
    protected Job preSaveUpdateDTO(TCkJobTruck tCkJobTruck, Job job) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected void preSaveValidation(Job job, Principal principal) throws ParameterException, ProcessingException {

    }

    @Override
    protected ServiceStatus preUpdateValidation(Job job, Principal principal) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected String getWhereClause(Job dto, boolean wherePrinted) throws ParameterException, ProcessingException {
        LOG.debug("getWhereClause");

        try {
            Principal principal = ckSession.getPrincipal();
            if (dto == null) {
                throw new ParameterException("param dto null");
            }
            if (principal == null) {
                throw new ProcessingException("Invalid token");
            }
            TCoreAccn accn = coreAccDao.find(dto.getAccnId());

//            CoreAccn accn = principal.getCoreAccn();
            StringBuilder searchStatement = new StringBuilder();

            wherePrinted = appendCondition(searchStatement, wherePrinted, "LOWER(o.jobId) LIKE LOWER(:jobId)", dto.getJobId());
            wherePrinted = appendCondition(searchStatement, wherePrinted, "LOWER(o.jobShipmentRef) LIKE LOWER(:shipmentRef)", dto.getShipmentRef());
            wherePrinted = appendCondition(searchStatement, wherePrinted, "LOWER(o.jobCustomerRef) LIKE LOWER(:customerRef)", dto.getCustomerRef());
            wherePrinted = appendCondition(searchStatement, wherePrinted, "o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates", "jobTruckStates");
            // Handle Contract filters
            if (Objects.nonNull(dto.getContract())) {
                Contract contract = dto.getContract();

                if (Objects.nonNull(contract.getTo())) {
                    wherePrinted = appendCondition(searchStatement, wherePrinted, "LOWER(o.TCoreAccnByJobPartyTo.accnName) LIKE (:to)", contract.getTo().getName());
                }

                if (Objects.nonNull(contract.getCo()) || Objects.nonNull(contract.getFf())) {
                    searchStatement.append(getOperator(wherePrinted))
                            .append("(LOWER(o.TCoreAccnByJobPartyCoFf.accnId) LIKE (:co) OR LOWER(o.TCoreAccnByJobPartyCoFf.accnName) LIKE (:ff))");
                    wherePrinted = true;
                }
            }
            if (Objects.nonNull(dto.getPickUp())){
                wherePrinted = appendCondition(searchStatement, wherePrinted,
                        " o.jobId IN ( " +
                                "SELECT trip.TCkJobTruck.jobId FROM TCkCtTrip trip " +
                                "JOIN trip.TCkCtTripLocationByTrFrom loc " +
                                "WHERE LOWER(TRIM(loc.tlocLocAddress)) LIKE LOWER(TRIM(:pickUp))) ", dto.getPickUp().getLocationDetails());
            }
            if (Objects.nonNull(dto.getDestinations())){
                wherePrinted = appendCondition(searchStatement, wherePrinted,
                        " o.jobId IN ( " +
                                "SELECT trip.TCkJobTruck.jobId FROM TCkCtTrip trip " +
                                "JOIN trip.TCkCtTripLocationByTrTo loc " +
                                "WHERE LOWER(TRIM(loc.tlocLocAddress)) LIKE LOWER(TRIM(:dropOff))) ", dto.getDestinations().get(0).getLocationDetails());
            }

            // Handle account-specific filters
            String accTypeId = accn.getTMstAccnType().getAtypId();
            if (AccountTypes.ACC_TYPE_FF.name().equals(accTypeId) || AccountTypes.ACC_TYPE_CO.name().equals(accTypeId)) {
                wherePrinted = appendCondition(searchStatement, wherePrinted, "o.TCoreAccnByJobPartyCoFf.accnId = :accnId", accn.getAccnId());
            } else if (AccountTypes.ACC_TYPE_TO.name().equals(accTypeId)) {
                wherePrinted = appendCondition(searchStatement, wherePrinted, "o.TCoreAccnByJobPartyTo.accnId = :accnId", accn.getAccnId());
            } else if (AccountTypes.ACC_TYPE_FF_CO.name().equals(accTypeId)) {
                wherePrinted = appendCondition(searchStatement, wherePrinted, "o.TCkJob.TCoreAccnByJobSlAccn.accnId = :accnId", accn.getAccnId());
            }
            // Check department-specific filters
            CkCtDept principalDept = ckJobTruckUtilService.getPrincipalDepartment(principal);
            if (principalDept != null) {
                String deptCondition = accTypeId.equals(AccountTypes.ACC_TYPE_FF.name()) || accTypeId.equals(AccountTypes.ACC_TYPE_CO.name())
                        ? "(o.TCkCtDeptByJobCoDepartment.deptId = :deptId OR o.TCkCtDeptByJobCoDepartment.deptId IS NULL)"
                        : "(o.TCkCtDeptByJobToDepartment.deptId = :deptId OR o.TCkCtDeptByJobToDepartment.deptId IS NULL)";
                wherePrinted = appendCondition(searchStatement, wherePrinted, deptCondition, principalDept.getDeptId());
            }

            return searchStatement.toString();
        } catch (ParameterException | ProcessingException ex) {
            LOG.error("getWhereClause", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("getWhereClause", ex);
            throw new ProcessingException(ex);
        }
    }
    private boolean appendCondition(StringBuilder builder, boolean wherePrinted, String condition, Object value) {
        if (value != null && !StringUtils.isEmpty(value.toString())) {
            builder.append(getOperator(wherePrinted)).append(condition);
            return true;
        }
        return wherePrinted;
    }
    @Override
    protected HashMap<String, Object> getParameters(Job dto) throws ParameterException, ProcessingException {
        LOG.debug("getParameters");
        Principal principal = ckSession.getPrincipal();

        try {
            if (dto == null) {
                throw new ParameterException("param dto null");
            }
            if (principal == null) {
                throw new ProcessingException("Invalid token");
            }

            HashMap<String, Object> parameters = new HashMap<>();
            CoreAccn accn = principal.getCoreAccn();
            parameters.put("accnId", dto.getAccnId());

            if (!StringUtils.isEmpty(dto.getJobId())) {
                parameters.put("jobId", "%" + dto.getJobId() + "%");
            }
            if (!StringUtils.isEmpty(dto.getShipmentRef())) {
                parameters.put("shipmentRef", "%" + dto.getShipmentRef() + "%");
            }
            if (!StringUtils.isEmpty(dto.getCustomerRef())) {
                parameters.put("customerRef", "%" + dto.getCustomerRef() + "%");
            }
            if (Objects.nonNull(dto.getContract())) {
                if (Objects.nonNull(dto.getContract().getTo())) {
                    parameters.put("to", "%" + dto.getContract().getTo().getName() + "%");
                }
                if (Objects.nonNull(dto.getContract().getCo())) {
                    parameters.put("co", "%" + dto.getContract().getCo().getName() + "%");
                }
                if (Objects.nonNull(dto.getContract().getFf())) {
                    parameters.put("ff", "%" + dto.getContract().getFf().getName() + "%");
                }
            }
            if (Objects.nonNull(dto.getPickUp())){
                parameters.put("pickUp", "%" + dto.getPickUp().getLocationDetails().trim() + "%");
            }
            if (Objects.nonNull(dto.getDestinations())){
                parameters.put("dropOff", "%" + dto.getDestinations().get(0).getLocationDetails() + "%");
            }
            // check if the user is in a department
            CkCtDept principalDept = ckJobTruckUtilService.getPrincipalDepartment(principal);
            if (Objects.nonNull(principalDept)) {
                parameters.put("deptId", principalDept.getDeptId());
            }
            if (dto.getStatus() != null
                    && !dto.getStatus().equalsIgnoreCase("ALL")) {
                String status = JobStatesEnum.fromStateDesc(dto.getStatus());
                parameters.put("jobTruckStates", status);
            }else {
                parameters.put("jobTruckStates", Arrays.asList(JobStates.ACP.name(), JobStates.ASG.name(),
                        JobStates.CAN.name(), JobStates.DEL.name(), JobStates.NEW.name(),
                        JobStates.ONGOING.name(), JobStates.PAUSED.name(),  JobStates.REJ.name(),
                        JobStates.SUB.name(), JobStates.DLV.name()));
            }
            return parameters;
        } catch (ParameterException | ProcessingException ex) {
            LOG.error("getParameters", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("getParameters", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    protected Job whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
        LOG.debug("whereDto");
        try {
            if (null == filterRequest)
                throw new ParameterException("param filterRequest null");

            Job dto = new Job();
            Contract contract = new Contract();
            LocationDetails pickUp = new LocationDetails();
            ArrayList<LocationDetails> dropOff = new ArrayList<>();

            // Create separate Company instances for each field
            Company to = new Company();
            Company co = new Company();
            Company ff = new Company();

            for (EntityWhere entityWhere : filterRequest.getWhereList()) {
                Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
                if (!opValue.isPresent()) {
                    continue;
                }
                // Search for jobNo and set the jobId
                if (entityWhere.getAttribute().equalsIgnoreCase("jobNo")) {
                    dto.setJobId(opValue.get());
                }

                // Search for "to" and set the company name in the contract
                if (entityWhere.getAttribute().equalsIgnoreCase("to")) {
                    to.setName(opValue.get());
                    contract.setTo(to);
                    dto.setContract(contract);
                }

                // Search for "co" and set the company name in the contract
                if (entityWhere.getAttribute().equalsIgnoreCase("co")) {
                    co.setName(opValue.get());
                    contract.setCo(co);
                    dto.setContract(contract);
                }

                // Search for "ff" and set the company name in the contract
                if (entityWhere.getAttribute().equalsIgnoreCase("ff")) {
                    ff.setName(opValue.get());
                    contract.setFf(ff);
                    dto.setContract(contract);
                }

                if (entityWhere.getAttribute().equalsIgnoreCase("pickUp")) {
                    pickUp.setLocationDetails(opValue.get());
                    dto.setPickUp(pickUp);
                }
                if (entityWhere.getAttribute().equalsIgnoreCase("dropOff")) {
                    LocationDetails destination = new LocationDetails();
                    destination.setLocationDetails(opValue.get());
                    dropOff.add(destination);
                    dto.setDestinations(dropOff);
                }
                if (entityWhere.getAttribute().equalsIgnoreCase("status")) {
                    dto.setStatus(opValue.get());
                }
                if (entityWhere.getAttribute().equalsIgnoreCase("accnId")) {
                    dto.setAccnId(opValue.get());
                }
            }

            return dto;
        } catch (ParameterException ex) {
            LOG.error("whereDto: ParameterException occurred", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("whereDto: Unexpected exception occurred", ex);
            throw new ProcessingException(ex);
        }
    }
    @Override
    protected CoreMstLocale getCoreMstLocale(Job job) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected Job setCoreMstLocale(CoreMstLocale coreMstLocale, Job job) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public Job findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.debug("findById");

        if (StringUtils.isBlank(id)) {
            throw new ParameterException("param id null or empty");
        }
        try {
            TCkJobTruck job = dao.find(id);
            if (job == null) {
                throw new EntityNotFoundException("id::" + id);
            }
            initEnity(job);
            return dtoFromEntity(job);
        } catch (Exception e) {
            LOG.error(e);
            throw new ProcessingException(e);
        }
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public Job deleteById(String uuid, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        LOG.debug("deleteById");

        try {
            if (StringUtils.isEmpty(uuid))
                throw new ParameterException("param id null or empty");
            if (null == principal)
                throw new ParameterException("param principal null");

            // Fetch the entity
            // TCkJobTruck entity = dao.find(uuid);
            //Fetch data from TCkJobTruck by using job id
            TCkJobTruck entity = this.getJobTrucks(uuid);
            if (null == entity)
                throw new EntityNotFoundException("id: " + uuid);

            // Mark the entity as inactive and update it
            this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
            Date now = Calendar.getInstance().getTime();
            this.updateEntity(ACTION.DELETE, entity, principal, now);

            // Convert entity to DTO
            Job dto = dtoFromEntity(entity);

            // Perform actual delete
            delete(dto, principal);

            return dto;
        } catch (ParameterException | EntityNotFoundException ex) {
            LOG.error("deleteById", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("deleteById", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public List<Job> filterBy(EntityFilterRequest filterRequest)
            throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.debug("filterBy");

        try {
            if (null == filterRequest)
                throw new ParameterException("param filterRequest null");

            Job dto = this.whereDto(filterRequest);
            if (null == dto)
                throw new ProcessingException("whereDto null");

            filterRequest.setTotalRecords(this.countByAnd(dto));
            String selectClause = "FROM TCkJobTruck o ";
            String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
            List<TCkJobTruck> entities = super.findEntitiesByAnd(dto, selectClause, orderByClauses,
                    filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
            List<Job> dtos = entities.stream().map(x -> {
                try {
                    initEnity(x);
                    return dtoFromEntity(x);
                } catch (ParameterException | ProcessingException e) {
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public int countByAnd(Job dto) throws ParameterException, EntityNotFoundException, ProcessingException {
        LOG.debug("countByAnd");

        try {
            if (dto == null)
                throw new ParameterException("param dto null");

            String whereClause = this.getWhereClause(dto, false); // abstract callback
            HashMap<String, Object> parameters = this.getParameters(dto); // abstract callback

            int count = 0;
            if (StringUtils.isNotEmpty(whereClause) && null != parameters && parameters.size() > 0) {
                count = this.ckJobDao.count("SELECT COUNT(o) FROM TCkJobTruck o" + whereClause, parameters);

            } else {
                count = this.ckJobDao.count("SELECT COUNT(o) FROM TCkJobTruck o");
            }
            return count;
        } catch (ParameterException | EntityNotFoundException ex) {
            LOG.error("countByAnd", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("countByAnd", ex);
            throw new ProcessingException(ex);
        }
    }

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
    protected String formatOrderBy(String attribute) throws Exception {

        String newAttr = attribute;
        if (StringUtils.contains(newAttr, "tcoreAccn"))
            newAttr = newAttr.replace("tcoreAccn", "TCoreAccn");

        if (StringUtils.contains(newAttr, "tcoreAccnByJobPartyTo"))
            newAttr = newAttr.replace("tcoreAccnByJobPartyTo", "TCoreAccnByJobPartyTo");

        if (StringUtils.contains(newAttr, "tcoreAccnByJobPartyCoFf"))
            newAttr = newAttr.replace("tcoreAccnByJobPartyCoFf", "TCoreAccnByJobPartyCoFf");

        if (StringUtils.contains(newAttr, "tckJob"))
            newAttr = newAttr.replace("tckJob", "TCkJob");

        if (StringUtils.contains(newAttr, "tckMstShipmentType"))
            newAttr = newAttr.replace("tckMstShipmentType", "TCkMstShipmentType");

        if (StringUtils.contains(newAttr, "tckRecordDate"))
            newAttr = newAttr.replace("tckRecordDate", "TCkRecordDate");

        if (StringUtils.contains(newAttr, "tckMstJobState"))
            newAttr = newAttr.replace("tckMstJobState", "TCkMstJobState");

        if (StringUtils.contains(newAttr, "tckMstJobType"))
            newAttr = newAttr.replace("tckMstJobType", "TCkMstJobType");
        if (StringUtils.contains(newAttr, "jobNo")){
            newAttr = newAttr.replace("jobNo", "jobId");
        }
        if (StringUtils.contains(newAttr, "createdDate")){
            newAttr = newAttr.replace("createdDate", "jobDtCreate");
        }
        if (StringUtils.contains(newAttr, "lastUpdatedDate")){
            newAttr = newAttr.replace("lastUpdatedDate", "jobDtLupd");
        }
        return newAttr;
    }

    @Override
    public Optional<Object> getList(Map<String, String> params) throws Exception {
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Object> getListByAccnId(String accnId, Map<String, String> params) throws Exception {
        LOG.debug("getJobList");

        try {
            if (params == null) {
                throw new ParameterException("Parameter map is null");
            }
            EntityFilterRequest filterRequest = new EntityFilterRequest();
            // Extract pagination parameters
            int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) : 1;
            int perPage = params.containsKey("perPage") ? Integer.parseInt(params.get("perPage")) : 10;
            perPage = Math.min(perPage, 100);
            filterRequest.setDisplayStart((page - 1) * perPage);
            filterRequest.setDisplayLength(perPage);

            // Extract sorting parameters
            String sortBy = params.getOrDefault("sortBy", "createdDate");
            String sortType = params.getOrDefault("sortType", "DESC");
            EntityOrderBy orderBy = new EntityOrderBy();
            orderBy.setAttribute(sortBy);
            orderBy.setOrdered("DESC".equalsIgnoreCase(sortType) ? EntityOrderBy.ORDERED.DESC : EntityOrderBy.ORDERED.ASC);
            filterRequest.setOrderBy(orderBy);
            if (!SortByEnum.isValid(sortBy)){
                throw new ParameterException("Invalid sort by value: " + sortBy);
            }
            // Extract filtering parameters
            ArrayList<EntityWhere> whereList = new ArrayList<>();
            Map<String, String> filters = new HashMap<>();
            filters.put("status", params.get("status"));
            filters.put("jobNo", params.get("jobNo"));
            filters.put("co", params.get("co"));
            filters.put("ff", params.get("ff"));
            filters.put("to", params.get("to"));
            filters.put("shipmentRef", params.get("shipmentRef"));
            filters.put("customerRef", params.get("customerRef"));
            filters.put("pickUp", params.get("pickUp"));
            filters.put("dropOff", params.get("dropOff"));
            filters.put("accnId", accnId);

            // Validate the "status" filter
            String status = filters.get("status");
            if (status != null && !JobStatesEnum.isValid(status)) {
                throw new ParameterException("Invalid status value: " + status);
            }
            filters.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    whereList.add(new EntityWhere(key, value));
                }
            });
            filterRequest.setWhereList(whereList);

            // Validate the filterRequest
            if (!filterRequest.isValid()) {
                throw new ParameterException("Invalid filter request: " + filterRequest.toJson());
            }

            // Fetch data
            Job dto = this.whereDto(filterRequest);
            int totalRecords = this.countByAnd(dto);
            List<Job> entities = this.filterBy(filterRequest);
            // Construct the response
            EntityFilterResponse filterResponse = new EntityFilterResponse();
            if (entities == null || entities.isEmpty()) {
                filterResponse.setTotal(0);
                filterResponse.setPerPage(perPage);
                filterResponse.setCurrentPage(page);
                filterResponse.setTotalPages(1);
                filterResponse.setData(new ArrayList<>());
            } else {
                filterResponse.setTotal(totalRecords);
                filterResponse.setPerPage(perPage);
                filterResponse.setCurrentPage(page);
                filterResponse.setTotalPages((int) Math.ceil((double) totalRecords / perPage));
                filterResponse.setData(new ArrayList<>(entities));
            }
            return Optional.of(filterResponse);
        } catch (ParameterException | PathNotFoundException | ProcessingException ex) {
            LOG.error("getJobList", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("getJobList", ex);
            throw new ProcessingException(ex);
        }
    }

    private List<TCkCtTrip> getJobTrip(String jobId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("jobId", jobId);
        String hql = "SELECT t FROM TCkCtTrip t WHERE t.TCkJobTruck.jobId = :jobId AND t.trStatus = 'A'";
        return ckCtTripDao.getByQuery(hql, params);
    }
    private TCkJobTruck getJobTrucks(String jobId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("jobId", jobId);
        String sql = "SELECT o FROM TCkJobTruck o WHERE o.jobId = :jobId AND o.jobStatus = 'A'";
        List<TCkJobTruck> jobTruckList = ckJobTruckDao.getByQuery(sql, params);
        return jobTruckList.isEmpty() ? null : jobTruckList.get(0);
    }
    private List<TCkJobTruckExt> loadJobTruckExt(String jobId) throws Exception {
        return ckJobTruckExtDao.findAllByJobId(jobId);
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Job assignJobToDriver(AssignToDriver dto, String jobId)
            throws Exception {
        LOG.debug("Assigning job to driver with Job ID: {} and Driver ID: {}", jobId, dto.getDriverId());
        Principal principal = ckSession.getPrincipal();
        // Validate input
        if (jobId == null || dto.getDriverId() == null || dto.getTruckId() == null) {
            throw new ParameterException("Job ID, Driver ID, or Truck ID cannot be null");
        }
        // Find and assign driver
        CkCtDrv driver = ckCtDrvService.findById(dto.getDriverId());
        if (driver == null) {
            throw new ParameterException("Driver not found for ID: " + dto.getDriverId());
        }

        // Find and assign truck
        CkCtVeh truck = ckCtVehService.findById(dto.getTruckId());
        if (truck == null) {
            throw new ParameterException("Truck not found for ID: " + dto.getTruckId());
        }

        // Update job entity
        CkJobTruck jobTruck = ckJobTruckService.findById(jobId);
        if (jobTruck == null) {
            throw new ParameterException("JobTruck not found for Job ID: " +jobId);
        }
        jobTruck.setTCkCtDrv(driver);
        jobTruck.setTCkCtVeh(truck);

        // Assign job and update state
        CkJobTruck updatedJobTruck = jobTruckStateService.assignJob(jobTruck, principal);
        ckJobTruckService.update(updatedJobTruck, principal);

        // Retrieve job entity
        TCkJobTruck entity = this.getJobTrucks(jobId);
        if (entity == null) {
            throw new ParameterException("Job not found for ID: " + jobId);
        }
        // Initialize and convert to DTO
        initEnity(entity);
        return dtoFromEntity(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Job rejectedOrCancel(String jobId, JobActions jobAction, String remarks, Principal principal)
            throws Exception {
        LOG.debug("Canceling job with ID: {} with action: {}", jobId, jobAction);
        // Retrieve job
        CkJobTruck dto = ckJobTruckService.findById(jobId);
        if (dto == null) {
            throw new ParameterException("Job not found for ID: " + jobId);
        }
        if (JobActions.REJECT.equals(jobAction) && StringUtils.isBlank(remarks)){
            throw new ParameterException("Remark is required for Reject action!");
        }
        switch (jobAction){
            case REJECT:
                //Not using rejection remarks
                dto.setJobRemarks(remarks);
            case DELETE:
                //This action using for CANCEL
                dto.setAction(jobAction);
                break;
            default:
                LOG.debug("Invalid job action {} :" , jobAction);
        }
        // Persist the changes to the database (this is where the update happens)
        ckJobTruckService.update(dto, principal);
        TCkJobTruck entity = dao.find(jobId);
        // Initialize and convert entity to DTO
        initEnity(entity);
        return dtoFromEntity(entity);
    }
    @Transactional
    public void getExtendAttributes(String jobId, Job job) throws Exception {
        JobDetails jobDetails = new JobDetails();
        Map<Integer, Container> containerMap = new TreeMap<>(); // TreeMap for auto-sorted order
        List<TCkJobTruckExt> truckExtList = ckJobTruckExtDao.findAllByJobTruckId(jobId);

        for (TCkJobTruckExt ext : truckExtList) {
            String key = ext.getJextKey();
            String value = ext.getJextVal();
            LOG.debug("Processing key: {}, value: {}", key, value);

            try {
                if (key.startsWith("containers.")) {
                    // Extract base key and sequence number
                    String[] parts = key.split("\\.");
                    String containerKey = parts[1]; // e.g., "weight_2"
                    String[] containerParts = containerKey.split("_");

                    String baseKey = containerParts[0]; // e.g., "weight"
                    int seq = containerParts.length > 1 ? Integer.parseInt(containerParts[1]) : 0;

                    // Retrieve or create the container for this sequence
                    Container currentContainer = containerMap.computeIfAbsent(seq, k -> new Container());

                    // Set the container field value
                    Field containerField = Container.class.getDeclaredField(baseKey);
                    containerField.setAccessible(true);
                    containerField.set(currentContainer, convertValue(containerField.getType(), value));
                } else {
                    // Handle job details fields
                    Field jobDetailsField = JobDetails.class.getDeclaredField(key);
                    jobDetailsField.setAccessible(true);
                    jobDetailsField.set(jobDetails, convertValue(jobDetailsField.getType(), value));
                }
            } catch (NoSuchFieldException e) {
                LOG.warn("Key '{}' does not match any field in the Container or JobDetails class", key);
            } catch (IllegalAccessException e) {
                LOG.error("Error setting field '{}' in the Container or JobDetails class", key, e);
            } catch (NumberFormatException e) {
                LOG.warn("Invalid sequence format in key '{}'", key);
            }
        }

        // Convert sorted container map to a list
        List<Container> containerList = new ArrayList<>(containerMap.values());
        jobDetails.setContainers(containerList);

        // Assign JobDetails to Job
        job.setDetails(jobDetails);
    }

    // Convert string value to the appropriate field type
    private Object convertValue(Class<?> fieldType, String value) {
        if (value == null) {
            return null;
        }
        if (fieldType.equals(String.class)) {
            return value;
        }
        if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            return Integer.parseInt(value);
        }
        if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
            return Double.parseDouble(value);
        }
        if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
            return Float.parseFloat(value);
        }
        if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            return Long.parseLong(value);
        }
        if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }

}
