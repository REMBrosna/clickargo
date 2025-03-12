package com.guudint.clickargo.clictruck.notification.service;

import com.guudint.clickargo.clictruck.notification.dto.CkCtMstAlert;
import com.guudint.clickargo.clictruck.notification.model.TCkCtMstAlert;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.entity.AbstractEntityService;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

public class CkCtMstAlertService extends AbstractEntityService<TCkCtMstAlert, String, CkCtMstAlert> {

    // Static Attributes
    ////////////////////
    private static final Logger log = Logger.getLogger(CkCtMstAlertService.class);
    private static final String auditTag = "ENTITY ID";
    private static final String tableName = "T_CK_CT_MST_ALERT";

    /**
     * Constructor
     */
    public CkCtMstAlertService() {
        super("ckCtMstAlertDao", auditTag, TCkCtMstAlert.class.getName(), tableName);
        // TODO Auto-generated constructor stub
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public TCkCtMstAlert initEnity(TCkCtMstAlert entity) throws ParameterException, ProcessingException {
        return entity;
    }
    @Override
    protected TCkCtMstAlert entityFromDTO(CkCtMstAlert dto) throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        log.debug("entityFromDTO");
        try {
            if (null == dto)
                throw new ParameterException("dto param null");

            TCkCtMstAlert entity = new TCkCtMstAlert();
            entity.setAltStatus('A');
            entity = dto.toEntity(entity);

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
    protected CkCtMstAlert dtoFromEntity(TCkCtMstAlert entity) throws ParameterException, ProcessingException {
        log.debug("dtoFromEntity");
        try {
            if (null == entity)
                throw new ParameterException("param entity null");
            CkCtMstAlert dto = new CkCtMstAlert(entity);
            BeanUtils.copyProperties(entity, dto);
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
    protected String entityKeyFromDTO(CkCtMstAlert dto) throws ParameterException, ProcessingException {
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
    protected TCkCtMstAlert updateEntity(ACTION attribute, TCkCtMstAlert entity, Principal principal, Date date) throws ParameterException, ProcessingException {
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
    protected TCkCtMstAlert updateEntityStatus(TCkCtMstAlert entity, char status) throws ParameterException, ProcessingException {
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
    protected CkCtMstAlert preSaveUpdateDTO(TCkCtMstAlert tCkCtMstAlert, CkCtMstAlert ckCtMstAlert) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected void preSaveValidation(CkCtMstAlert ckCtMstAlert, Principal principal) throws ParameterException, ProcessingException {

    }

    @Override
    protected ServiceStatus preUpdateValidation(CkCtMstAlert ckCtMstAlert, Principal principal) throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    protected String getWhereClause(CkCtMstAlert dto, boolean wherePrinted) throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        log.debug("getWhereClause");

        try {
            if (null == dto)
                throw new ParameterException("param dto null");

            StringBuilder searchStatement = new StringBuilder();
            if (!StringUtils.isEmpty(dto.getAltId())) {
                searchStatement.append(getOperator(wherePrinted)).append("LOWER(o.altId) LIKE LOWER(:altId)");
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
    protected HashMap<String, Object> getParameters(CkCtMstAlert dto) throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        log.debug("getParameters");

        try {
            if (null == dto)
                throw new ParameterException("param dto null");

            HashMap<String, Object> parameters = new HashMap<>();
            if (!StringUtils.isEmpty(dto.getAltId())) {
                parameters.put("altId", "%" + dto.getAltId() + "%");
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
    protected CkCtMstAlert whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        log.debug("whereDto");

        try {
            if (null == filterRequest)
                throw new ParameterException("param filterRequest null");

            CkCtMstAlert dto = new CkCtMstAlert();
            for (EntityWhere entityWhere : filterRequest.getWhereList()) {
                Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
                if (!opValue.isPresent())
                    continue;

                if (entityWhere.getAttribute().equalsIgnoreCase("altId")) {
                    dto.setAltId(opValue.get());
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
    protected CoreMstLocale getCoreMstLocale(CkCtMstAlert ckCtMstAlert) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    protected CkCtMstAlert setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtMstAlert ckCtMstAlert) throws ParameterException, EntityNotFoundException, ProcessingException {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CkCtMstAlert findById(String key) throws ParameterException, EntityNotFoundException, ProcessingException {
        log.debug("findById");

        try {
            if (StringUtils.isEmpty(key))
                throw new ParameterException("param id null or empty");

            TCkCtMstAlert entity = dao.find(key);
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
    public CkCtMstAlert deleteById(String s, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
        return null;
    }

    @Override
    public List<CkCtMstAlert> filterBy(EntityFilterRequest filterRequest) throws ParameterException, EntityNotFoundException, ProcessingException {
        log.debug("Entering filterBy");

        try {
            if (filterRequest == null)
                throw new ParameterException("Parameter filterRequest is null");

            CkCtMstAlert dto = this.whereDto(filterRequest);
            if (dto == null)
                throw new ProcessingException("DTO from whereDto is null");
            int totalRecords = super.countByAnd(dto);
            filterRequest.setTotalRecords(totalRecords);
            log.debug("Total records found: " + totalRecords);
            String selectClause = "FROM TCkCtMstAlert o ";
            String orderByClause = filterRequest.getOrderBy().toString();
            List<TCkCtMstAlert> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
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

}
