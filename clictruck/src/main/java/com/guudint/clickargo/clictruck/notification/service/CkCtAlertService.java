package com.guudint.clickargo.clictruck.notification.service;

import com.guudint.clickargo.clicservice.model.TCkSuspensionLog;
import com.guudint.clickargo.clictruck.admin.account.dto.AccountSuspend;
import com.guudint.clickargo.clictruck.common.dto.NotificationTypeName;
import com.guudint.clickargo.clictruck.notification.dto.CkCtAlert;
import com.guudint.clickargo.clictruck.notification.dto.CkCtMstAlert;
import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.guudint.clickargo.clictruck.notification.model.TCkCtMstAlert;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dao.CkAccnDao;
import com.guudint.clickargo.tax.model.TCkTaxSeq;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

public class CkCtAlertService extends AbstractClickCargoEntityService<TCkCtAlert, String, CkCtAlert> {

    // Static Attributes
    ////////////////////
    private static final Logger log = Logger.getLogger(CkCtAlertService.class);
    private static final String auditTag = "ENTITY ID";
    private static final String tableName = "T_CK_CT_ALERT";


    @Autowired
    private CkAccnDao ckAccnDao;



    /**
     * Constructor
     */
    public CkCtAlertService() {
        super("ckCtAlertDao", auditTag, TCkCtAlert.class.getName(), tableName);
        // TODO Auto-generated constructor stub
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public TCkCtAlert initEnity(TCkCtAlert entity) throws ParameterException, ProcessingException {
        if (null != entity) {
            Hibernate.initialize(entity.gettCoreAccn());
            Hibernate.initialize(entity.gettCkCtMstAlert());
        }
        return entity;
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public CkCtAlert add(CkCtAlert dto, Principal principal)
            throws ParameterException, ProcessingException, ValidationException, EntityNotFoundException {
        try {
            if (this.isRecordExists(dto, "ADD")) {
                throw new ParameterException("An alert with the following details already exists!");
            }
            Boolean isSubscribed = ckAccnDao.findByAccnIdSubscribed(dto.getCoreAccn().getAccnId(), RecordStatus.ACTIVE.getCode(), dto.getCkCtMstAlert().getAltNotificationType());
            if (!isSubscribed) {
                throw new ParameterException("Your company is not subscribed to the "+ type(dto.getCkCtMstAlert().getAltNotificationType())  +" service");
            }

            // Proceed to add the alert
            return super.add(dto, principal);

        } catch (ParameterException | EntityNotFoundException | ValidationException ex) {
            log.error("Validation error: " + ex.getMessage(), ex);
            throw ex;  // Rethrow specific exceptions to handle them appropriately
        } catch (Exception ex) {
            log.error("Error executing query: " + ex.getMessage(), ex);
            throw new ProcessingException(ex);  // Catch all other exceptions and wrap in ProcessingException
        }
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public CkCtAlert update(CkCtAlert dto, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        try {
            if (this.isRecordExists(dto, "UPDATE")) {
                throw new ParameterException("An alert with the following detail already exists!");
            }
            // Check if the company is subscribed to the WhatsApp service
            Boolean isSubscribed = ckAccnDao.findByAccnIdSubscribed(dto.getCoreAccn().getAccnId(), RecordStatus.ACTIVE.getCode(), dto.getCkCtMstAlert().getAltNotificationType());
            if (!isSubscribed) {
                throw new ParameterException("Your company is not subscribed to the " + type(dto.getCkCtMstAlert().getAltNotificationType()) + " service");
            }
            return super.update(dto, principal);
        }catch (ParameterException | EntityNotFoundException | ValidationException ex) {
            log.error("Validation error: " + ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Error executing query: " + ex.getMessage(), ex);
            throw new ProcessingException(ex);
        }
    }
    public Boolean isRecordExists(CkCtAlert dto, String action) throws ProcessingException {
        Date altConditionDt = dto.getAltConditionDt();

        // Reset time to 00:00:00 for date comparison
        if (altConditionDt != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(altConditionDt);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            altConditionDt = cal.getTime();  // Now time is reset to 00:00:00
        }

        String sql = "SELECT o FROM TCkCtAlert o WHERE o.tCkCtMstAlert.altName = :altName " +
                "AND o.altReferId = :altReferId " +
                "AND o.tCkCtMstAlert.altNotificationType = :altNotificationType " +
                "AND DATE(o.altConditionDt) = DATE(:altConditionDt) " +  // MySQL date function
                "AND o.altConditionValue = :altConditionValue";

        Map<String, Object> param = new HashMap<>();
        param.put("altName", dto.getCkCtMstAlert().getAltName());
        param.put("altNotificationType", dto.getCkCtMstAlert().getAltNotificationType());
        param.put("altReferId", dto.getAltReferId());
        param.put("altConditionDt", altConditionDt);
        param.put("altConditionValue", dto.getAltConditionValue());

        if (action.equalsIgnoreCase("UPDATE")) {
            sql += " AND o.altId != :altId";
            param.put("altId", dto.getAltId());
        }

        List<TCkCtAlert> list;
        try {
            list = this.dao.getByQuery(sql, param);
        } catch (Exception ex) {
            log.error("Error executing query: " + ex.getMessage(), ex);
            throw new ProcessingException(ex);
        }

        return !list.isEmpty();
    }


    @Override
    protected TCkCtAlert entityFromDTO(CkCtAlert dto) throws ParameterException, ProcessingException {
        log.debug("entityFromDTO");

        try {
            if (dto == null) {
                throw new ParameterException("dto param null");
            }

            TCkCtAlert entity = new TCkCtAlert();
            BeanUtils.copyProperties(dto, entity);
            entity.setAltStatus('A');
            // Set the core account
            TCoreAccn tCoreAccn = new TCoreAccn();
            tCoreAccn.setAccnId(dto.getCoreAccn().getAccnId());
            entity.settCoreAccn(tCoreAccn);

            // Set the master alert
            TCkCtMstAlert tCkCtMstAlert = new TCkCtMstAlert();
            tCkCtMstAlert.setAltId(dto.getCkCtMstAlert().getAltId());
            entity.settCkCtMstAlert(tCkCtMstAlert);

            // Parse the altConditionDt from ISO format to Date, then format it
            if (dto.getAltConditionDt() != null) {
                entity.setAltConditionDt(dto.getAltConditionDt());
            }
            return entity;
        } catch (ParameterException ex) {
            log.error("entityFromDTO", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("entityFromDTO", ex);
            throw new ProcessingException(ex);
        }
    }



    @Override
    public CkCtAlert dtoFromEntity(TCkCtAlert entity) throws ParameterException, ProcessingException {
        log.debug("dtoFromEntity");
        try {
            if (null == entity)
                throw new ParameterException("param entity null");
            CkCtAlert dto = new CkCtAlert();
            BeanUtils.copyProperties(entity, dto);

            CoreAccn coreAccn = new CoreAccn();
            coreAccn.setAccnId(entity.gettCoreAccn().getAccnId());
            coreAccn.setAccnName(entity.gettCoreAccn().getAccnName());
            coreAccn.setCityCode(entity.gettCoreAccn().getCityCode());
            dto.setCoreAccn(coreAccn);

            CkCtMstAlert ckCtMstAlert = new CkCtMstAlert();
            ckCtMstAlert.setAltId(entity.gettCkCtMstAlert().getAltId());
            ckCtMstAlert.setAltModule(entity.gettCkCtMstAlert().getAltModule());
            ckCtMstAlert.setAltName(entity.gettCkCtMstAlert().getAltName());
            ckCtMstAlert.setAltNotificationType(entity.gettCkCtMstAlert().getAltNotificationType());
            ckCtMstAlert.setAltConditionType(entity.gettCkCtMstAlert().getAltConditionType());
            ckCtMstAlert.setAltTemplateId(entity.gettCkCtMstAlert().getAltTemplateId());
            dto.setCkCtMstAlert(ckCtMstAlert);
            return dto;
        } catch (ParameterException ex) {
            log.error("dtoFromEntity", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("dtoFromEntity", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    protected String entityKeyFromDTO(CkCtAlert dto) throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        log.debug("entityKeyFromDTO");

        try {
            if (null == dto)
                throw new ParameterException("dto param null");

            return dto.getAltId();
        } catch (ParameterException ex) {
            log.error("entityKeyFromDTO", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("entityKeyFromDTO", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    protected TCkCtAlert updateEntity(ACTION attribute, TCkCtAlert entity, Principal principal, Date date) throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        log.debug("updateEntity");

        try {
            if (null == entity)
                throw new ParameterException("param entity null");
            if (null == principal)
                throw new ParameterException("param principal null");
            if (null == date)
                throw new ParameterException("param date null");

            Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
            switch (attribute) {
                case CREATE:
                    entity.setAltId(UUID.randomUUID().toString());
                    entity.setAltUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
                    entity.setAltDtCreate(date);
                    entity.setAltUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
                    entity.setAltDtLupd(date);

                    break;
                case MODIFY:
                    entity.setAltUidLupd(opUserId.orElse("SYS"));
                    entity.setAltDtLupd(date);
                    break;
                case DELETE:
                    entity.setAltUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
                    entity.setAltDtLupd(date);
                    entity.setAltStatus('I');
                    break;

                default:
                    break;
            }

            return entity;
        } catch (ParameterException ex) {
            log.error("updateEntity", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("updateEntity", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    public TCkCtAlert updateEntityStatus(TCkCtAlert entity, char status) throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        log.debug("updateEntityStatus");

        try {
            if (null == entity)
                throw new ParameterException("param entity null");

            entity.setAltStatus(status);
            return entity;
        } catch (ParameterException ex) {
            log.error("updateEntity", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("updateEntityStatus", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    protected CkCtAlert preSaveUpdateDTO(TCkCtAlert entity, CkCtAlert dto) throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        log.debug("preSaveUpdateDTO");

        try {
            if (null == entity)
                throw new ParameterException("param storedEntity null");
            if (null == dto)
                throw new ParameterException("param dto null");

            dto.setAltUidCreate(entity.getAltUidCreate());
            dto.setAltDtCreate(entity.getAltDtCreate());

            return dto;
        } catch (ParameterException ex) {
            log.error("updateEntity", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("preSaveUpdateEntity", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    protected void preSaveValidation(CkCtAlert CkCtAlert, Principal principal) throws ParameterException, ProcessingException {

    }

    @Override
    protected ServiceStatus preUpdateValidation(CkCtAlert CkCtAlert, Principal principal) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected String getWhereClause(CkCtAlert dto, boolean wherePrinted) throws ParameterException, ProcessingException {
        log.debug("getWhereClause");

        try {
            if (null == dto)
                throw new ParameterException("param dto null");

            StringBuilder searchStatement = new StringBuilder();
            if (dto.getAltReferId() != null) {
                searchStatement.append(getOperator(wherePrinted)).append("LOWER(o.altReferId) = LOWER(:altReferId)");
                wherePrinted = true;
            }
            if (!StringUtils.isEmpty(dto.getAltId())) {
                searchStatement.append(getOperator(wherePrinted)).append("LOWER(o.altId) = LOWER(:altId)");
                wherePrinted = true;
            }
            if (Objects.nonNull(dto.getCkCtMstAlert()) && dto.getCkCtMstAlert().getAltModule() != null) {
                searchStatement.append(getOperator(wherePrinted)).append("LOWER(o.tCkCtMstAlert.altModule) = LOWER(:altModule)");
                wherePrinted = true;
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


    @Override
    protected HashMap<String, Object> getParameters(CkCtAlert dto) throws ParameterException, ProcessingException {
        log.debug("getParameters");

        try {
            if (null == dto)
                throw new ParameterException("param dto null");

            HashMap<String, Object> parameters = new HashMap<>();
            if (!StringUtils.isEmpty(dto.getAltId())) {
                parameters.put("altId", dto.getAltId());
            }
            if (dto.getAltReferId() != null) {
                parameters.put("altReferId", dto.getAltReferId());
            }
            if (Objects.nonNull(dto.getCkCtMstAlert()) && dto.getCkCtMstAlert().getAltModule() != null) {
                parameters.put("altModule", dto.getCkCtMstAlert().getAltModule());
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



    @Override
    protected CkCtAlert whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
        log.debug("whereDto");

        try {
            if (null == filterRequest)
                throw new ParameterException("param filterRequest null");

            CkCtAlert dto = new CkCtAlert();
            for (EntityWhere entityWhere : filterRequest.getWhereList()) {
                Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
                if (!opValue.isPresent())
                    continue;

                if (entityWhere.getAttribute().equalsIgnoreCase("altId")) {
                    dto.setAltId(opValue.get());
                }
                if (entityWhere.getAttribute().equalsIgnoreCase("altReferId")) {
                    dto.setAltReferId(opValue.get());
                }
                if (entityWhere.getAttribute().equalsIgnoreCase("altModule")) {
                    CkCtMstAlert ckCtMstAlert = new CkCtMstAlert();
                    ckCtMstAlert.setAltModule(opValue.get());
                    dto.setCkCtMstAlert(ckCtMstAlert);
                }
            }

            log.debug("dto: " + dto);
            return dto;
        } catch (ParameterException ex) {
            log.error("whereDto", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("whereDto", ex);
            throw new ProcessingException(ex);
        }
    }



    @Override
    protected CoreMstLocale getCoreMstLocale(CkCtAlert CkCtAlert) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtAlert setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtAlert CkCtAlert) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CkCtAlert findById(String key) throws ParameterException, EntityNotFoundException, ProcessingException {
        log.debug("findById");

        try {
            if (StringUtils.isEmpty(key))
                throw new ParameterException("param id null or empty");

            TCkCtAlert entity = dao.find(key);
            if (null == entity)
                throw new EntityNotFoundException("id: " + key);
            this.initEnity(entity);
            return this.dtoFromEntity(entity);
        } catch (ParameterException | EntityNotFoundException ex) {
            log.error("entityFromDTO", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("entityFromDTO", ex);
            throw new ProcessingException(ex);
        }
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CkCtAlert deleteById(String key, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        log.debug("deleteById");

        try {
            if (StringUtils.isEmpty(key))
                throw new ParameterException("param id null or empty");
            if (null == principal)
                throw new ParameterException("param principal null");

            // Fetch the entity
            // TCkJobTruck entity = dao.find(uuid);
            //Fetch data from TCkJobTruck by using job id
            TCkCtAlert entity = this.dao.find(key);
            if (null == entity)
                throw new EntityNotFoundException("id: " + key);

            // Mark the entity as inactive and update it
            this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
            Date now = Calendar.getInstance().getTime();
            this.updateEntity(ACTION.DELETE, entity, principal, now);

            // Convert entity to DTO
            CkCtAlert dto = dtoFromEntity(entity);

            // Perform actual delete
            this.delete(dto, principal);

            return dto;
        } catch (ParameterException | EntityNotFoundException ex) {
            log.error("deleteById", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("deleteById", ex);
            throw new ProcessingException(ex);
        }
    }

    @Override
    public List<CkCtAlert> filterBy(EntityFilterRequest filterRequest) throws ParameterException, EntityNotFoundException, ProcessingException {
        log.debug("Entering filterBy");

        try {
            if (filterRequest == null)
                throw new ParameterException("Parameter filterRequest is null");

            CkCtAlert dto = this.whereDto(filterRequest);
            if (dto == null)
                throw new ProcessingException("DTO from whereDto is null");
            int totalRecords = super.countByAnd(dto);
            filterRequest.setTotalRecords(totalRecords);
            log.debug("Total records found: " + totalRecords);
            String selectClause = "FROM TCkCtAlert o ";
            String orderByClause = formatOrderBy(filterRequest.getOrderBy().toString());
            List<TCkCtAlert> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
                    filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
            return entities.stream().map(entity -> {
                try {
                    return dtoFromEntity(entity);
                } catch (Exception e) {
                    log.error("Error converting entity to DTO", e);
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (ParameterException | ProcessingException ex) {
            log.error("Parameter or processing exception in filterBy", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected exception in filterBy", ex);
            throw new ProcessingException("An error occurred while processing the request"+ ex);
        }
    }

    @Override
    protected void initBusinessValidator() {

    }

    @Override
    protected Logger getLogger() {
        return null;
    }

    @Override
    public CkCtAlert newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }
    protected String formatOrderBy(String orderByClause) throws Exception {
        String newAttr = orderByClause;

        if(StringUtils.contains(newAttr, "ckCtMstAlert"))
            newAttr = newAttr.replace("ckCtMstAlert","tCkCtMstAlert");

        return newAttr;
    }
    private String type(String notificationType){
        if (notificationType.equalsIgnoreCase(NotificationTypeName.WHATSAPP.getDesc())){
            return NotificationTypeName.WHATSAPP.getDesc();
        }else if (notificationType.equalsIgnoreCase(NotificationTypeName.EMAIL.getDesc())){
            return NotificationTypeName.EMAIL.getDesc();
        }else if (notificationType.equalsIgnoreCase(NotificationTypeName.SMS.getDesc())){
            return NotificationTypeName.SMS.getDesc();
        }
        return notificationType;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CkCtAlert updateStatus(CkCtAlert dto, char status) throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        log.debug("updateEntityStatus");

        try {
            if (null == dto)
                throw new ParameterException("param entity null");

            CkCtAlert tCkCtAlert = this.find(dto);
            tCkCtAlert.setAltStatus(status);
            return tCkCtAlert;
        } catch (ParameterException ex) {
            log.error("updateEntity", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("updateEntityStatus", ex);
            throw new ProcessingException(ex);
        }
    }
}
