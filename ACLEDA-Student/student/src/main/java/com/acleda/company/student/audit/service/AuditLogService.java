package com.acleda.company.student.audit.service;

import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.audit.dto.AuditLog;
import com.acleda.company.student.audit.model.TAuditLog;
import com.acleda.company.student.common.AbstractEntityService;
import com.acleda.company.student.common.dto.EntityWhere;
import com.acleda.company.student.common.payload.EntityFilterRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuditLogService extends AbstractEntityService<TAuditLog, String, AuditLog> {
    private static final String auditTag = "T TAuditLog";
    private static final String tableName = "t_audit_Log";
    protected static Logger log = LoggerFactory.getLogger(AuditLogService.class);

    public AuditLogService() {
        super("auditLogRepository", "auditLogService", "TAuditLog", TAuditLog.class);
    }

    @Override
    protected TAuditLog initEntity(TAuditLog entity) throws Exception {
        return entity;
    }

    @Override
    protected TAuditLog entityFromDTO(AuditLog dto) throws Exception {
        log.debug("AuditLogService.entityFromDTO");
        try {
            if (null == dto) {
                throw new Exception("dto is null");
            }

            TAuditLog entity = new TAuditLog();
            entity = dto.toEntity(entity);
            if (StringUtils.isNotEmpty(dto.getAudtId())){
                entity.setAudtId(Long.parseLong(dto.getAudtId()));
            }
            return entity;
        } catch (Exception ex) {
            log.error("AuditLogService.entityFromDTO", ex);
            throw ex;
        }
    }

    @Override
    protected AuditLog dtoFromEntity(TAuditLog entity) throws Exception {
        log.debug("AuditLogService.dtoFromEntity");
        try {
            if (null == entity) {
                throw new Exception("param entity null");
            }
            AuditLog dto = new AuditLog(entity);
            dto.setAudtId(Long.toString(entity.getAudtId()));
            return dto;
        } catch (Exception ex) {
            log.error("AuditLogService.dtoFromEntity", ex);
            throw ex;
        }

    }
    @Override
    protected String entityKeyFromDTO(AuditLog dto) throws Exception {
        log.debug("AuditLogService.entityKeyFromDTO");

        try {
            if (null == dto) {
                throw new Exception("dto param null");
            }
            return dto.getAudtId();
        } catch (Exception ex) {
            log.error("AuditLogService.entityKeyFromDTO", ex);
            throw ex;
        }
    }

    @Override
    protected TAuditLog updateEntity(ACTION attribute, TAuditLog entity, TAppUser principal, Date date) throws Exception {
        log.debug("AuditLogService.updateEntity");

        try {
            if (null == entity)
                throw new Exception("param entity is null");
            if (null == principal)
                throw new Exception("param principal is null");
            if (null == date)
                throw new Exception("param date is null");

            Optional<String> opUserId = Optional.ofNullable(principal.getUsername());
            switch (attribute) {
                case CREATE:
                    entity.setAudtTimestamp(date);
                    break;

                case MODIFY:
                    entity.setAudtUid(opUserId.orElse("SYS"));
                    entity.setAudtTimestamp(date);
                    break;

                default:
                    break;
            }

            return entity;
        } catch (Exception ex) {
            log.error("AuditLogService.updateEntity", ex);
            throw ex;
        }
    }

    @Override
    protected TAuditLog updateEntityStatus(TAuditLog entity, char status) throws Exception {
        log.debug("AuditLogService.updateEntityStatus");

        try {
            if (null == entity) {
                throw new Exception("entity param is null");
            }
            return entity;
        } catch (Exception ex) {
            log.error("AuditLogService.updateEntityStatus", ex);
            throw ex;
        }

    }
    @Override
    protected AuditLog preSaveUpdateDTO(TAuditLog storedEntity, AuditLog dto) throws Exception {
        log.debug("AuditLogService.preSaveUpdateDTO");

        try {
            if (null == storedEntity) {
                throw new Exception("param storedEntity is null");
            }
            if (null == dto) {
                throw new Exception("param dto is null");
            }

            dto.setAudtUid(storedEntity.getAudtUid());
            dto.setAudtAccnid(storedEntity.getAudtAccnid());

            return dto;
        } catch (Exception ex) {
            log.error("AuditLogService.preSaveUpdateDTO", ex);
            throw ex;
        }

    }
    @Override
    protected String getWhereClause(AuditLog dto, boolean wherePrinted) throws Exception {
        log.debug("AuditLogService.getWhereClause");

        try {
            if (null == dto) {
                throw new Exception("param dto null");
            }

            StringBuilder searchStatement = new StringBuilder();
            if (!StringUtils.isEmpty(dto.getAudtId())) {
                searchStatement.append(getOperator(wherePrinted) + "o.audtId LIKE :audtId");
                wherePrinted = true;
            }
            if (!StringUtils.isEmpty(dto.getAudtUname())) {
                searchStatement.append(getOperator(wherePrinted) + "o.audtUname = :audtUname");
                wherePrinted = true;
            }
            return searchStatement.toString();
        } catch (Exception ex) {
            log.error("AuditLogService.getWhereClause", ex);
            throw ex;
        }
    }

    @Override
    protected HashMap<String, Object> getParameters(AuditLog dto) throws Exception {
        log.debug("AuditLogService.getParameters");

        try {
            if (null == dto) {
                throw new Exception("param dto null");
            }

            HashMap<String, Object> parameters = new HashMap<>();
            if (!StringUtils.isEmpty(dto.getAudtId())) {
                parameters.put("audtId", "%" + dto.getAudtId() + "%");
            }
            if (!StringUtils.isEmpty(dto.getAudtUname())) {
                parameters.put("audtUname", dto.getAudtUname());
            }
            return parameters;
        } catch (Exception ex) {
            log.error("AuditLogService.getParameters", ex);
            throw ex;
        }
    }

    @Override
    protected AuditLog whereDto(EntityFilterRequest filterRequest) throws Exception {
        try {
            if (null == filterRequest)
                throw new Exception("param filterRequest null");

            AuditLog dto = new AuditLog();
            for (EntityWhere entityWhere : filterRequest.getWhereList()) {
                Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
                if (opValue.isEmpty()) {
                    continue;
                }

                if (entityWhere.getAttribute().equalsIgnoreCase("audtId")) {
                    dto.setAudtUid(opValue.get());
                }
                if (entityWhere.getAttribute().equalsIgnoreCase("audtUname")) {
                    dto.setAudtUname(opValue.get());
                }
            }
            log.info("dto: " + dto.toString());
            return dto;
        } catch (Exception ex) {
            log.error("whereDto", ex);
            throw ex;
        }
    }

    @Override
    public AuditLog findById(String id) throws Exception {
        log.debug("StudentsService.findById");

        try {
            if (StringUtils.isEmpty(id)) {
                throw new Exception("param id is null or empty");
            }

            Optional<TAuditLog> entity = repository.findById(id);
            if (!entity.isPresent()) {
                throw new Exception("AuditLog not found with id: " + id);
            }

            TAuditLog initializedEntity = this.initEntity(entity.get());

            return this.dtoFromEntity(initializedEntity);
        } catch (Exception ex) {
            log.error("StudentsService.findById", ex);
            throw ex;
        }
    }


    @Override
    public List<AuditLog> findByAnd(AuditLog var1, int var2, int var3, String var4, String var5) throws Exception {
        return List.of();
    }

    @Override
    public int countByAnd(AuditLog var1) throws Exception {
        return 0;
    }

    @Override
    public Object addObj(Object object, TAppUser appUser) throws Exception {
        return super.addObj(object, appUser);
    }

    @Override
    public AuditLog update(AuditLog var1, TAppUser var2, boolean var3) throws Exception {
        return null;
    }

    @Override
    public Object updateObj(Object object, TAppUser appUser) throws Exception {
        return super.updateObj(object, appUser);
    }

    @Override
    public AuditLog updateStatus(AuditLog var1, TAppUser var2, char var3) throws Exception {
        return null;
    }

    @Override
    public Object updateObjStatus(Object var1, TAppUser var2, char var3) throws Exception {
        return null;
    }

    @Override
    public AuditLog delete(AuditLog var1, TAppUser var2) throws Exception {
        return null;
    }

    @Override
    public AuditLog deleteById(String id, TAppUser principal) throws Exception {
        log.debug("StudentsService.deleteById");

        Date now = Calendar.getInstance().getTime();
        try {
            if (StringUtils.isEmpty(id))
                throw new Exception("param id null or empty");
            if (null == principal)
                throw new Exception("param principal null");

            Optional<TAuditLog> optionalEntity = repository.findById(id);
            if (!optionalEntity.isPresent())
                throw new Exception("AuditLog not found with id: " + id);

            TAuditLog entity = optionalEntity.get();
            entity = this.initEntity(entity);

            this.updateEntityStatus(entity, 'I');
            this.updateEntity(ACTION.MODIFY, entity, principal, now);

            AuditLog dto = dtoFromEntity(entity);
            this.delete(dto, principal);
            return dto;
        } catch (Exception ex) {
            log.error("StudentsService.deleteById", ex);
            throw ex;
        }
    }


    @Override
    public List<AuditLog> filterBy(EntityFilterRequest filterRequest) throws Exception {
        try {
            if (null == filterRequest)
                throw new Exception("param filterRequest null");

            AuditLog dto = this.whereDto(filterRequest);
            if (null == dto)
                throw new Exception("whereDto null");

            filterRequest.setTotalRecords(super.countByAnd(dto));

            String selectClause = "FROM TAuditLog o ";
            String orderByClause = filterRequest.getOrderBy().toString();
            List<TAuditLog> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
                    filterRequest.getDisplayLength(), filterRequest.getDisplayStart());

            return entities.stream().map(x -> {
                try {
                    return dtoFromEntity(x);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("filterBy", ex);
            throw ex;
        }
    }

    @Override
    public boolean isRecordExists(AuditLog var1) throws Exception {
        return false;
    }
}
