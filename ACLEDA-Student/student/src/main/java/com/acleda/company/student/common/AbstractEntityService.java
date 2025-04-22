package com.acleda.company.student.common;

import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.audit.model.TAuditLog;
import com.acleda.company.student.audit.repository.AuditLogRepository;
import com.acleda.company.student.common.exceptions.ParameterException;
import com.acleda.company.student.common.payload.EntityFilterRequest;
import io.jsonwebtoken.lang.Collections;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Log4j2
public abstract class AbstractEntityService <E, K, D> implements IEntityService<E, K, D>, ApplicationContextAware {
    @Autowired
    protected ApplicationContext applicationContext;

    @PersistenceContext
    protected EntityManager entityManager;

    protected Class<E> entityClass;
    @Autowired(required = false)
    protected HttpServletRequest request;

    protected JpaRepository<E, K> repository;
    @Autowired
    protected AuditLogRepository auditLogRepository;
    protected String repositoryName;
    protected String moduleName;
    protected String entityName;
    protected String tableName;

    public AbstractEntityService(String repositoryName, String moduleName, String entityName, Class<E> entityClass) {
        log.debug("AbstractEntityService");
        this.repositoryName = repositoryName;
        this.moduleName = moduleName;
        this.entityName = entityName;
        this.entityClass = entityClass; // Initialize entityClass
    }

    // Optional constructor with tableName
    public AbstractEntityService(String repositoryName, String moduleName, String entityName, String tableName, Class<E> entityClass) {
        this(repositoryName, moduleName, entityName, entityClass); // Delegate to main constructor
        this.tableName = tableName;
    }

    @PostConstruct
    protected void init() {
        log.debug("Initializing AbstractEntityService");
        try {
            this.repository = applicationContext.getBean(this.repositoryName, JpaRepository.class);
        } catch (Exception e) {
            log.error("Failed to initialize repository for {}", repositoryName, e);
            throw new IllegalStateException("Could not initialize repository", e);
        }
    }
    public void setApplicationContext(ApplicationContext context) {
        log.debug("setApplicationContext");
        try {
            this.applicationContext = context;
        } catch (Exception e) {
            log.error("setApplicationContext", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    protected abstract E initEntity(E entity) throws Exception;
    protected abstract D dtoFromEntity(E entity) throws Exception;
    protected abstract E entityFromDTO(D dto) throws Exception;
    protected abstract K entityKeyFromDTO(D dto) throws Exception;

    protected abstract E updateEntity(ACTION action, E entity, TAppUser user, Date date) throws Exception;

    protected abstract E updateEntityStatus(E entity, char status) throws Exception;

    protected abstract D preSaveUpdateDTO(E entity, D dto) throws Exception;

    protected abstract String getWhereClause(D dto, boolean wherePrinted) throws Exception;

    protected abstract HashMap<String, Object> getParameters(D dto) throws Exception;

    protected abstract D whereDto(EntityFilterRequest request) throws Exception;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public D find(D dto) throws Exception {
        log.debug("find");
        try {
            if (dto == null) throw new Exception("dto param null");
            K key = this.entityKeyFromDTO(dto);
            if (key == null) throw new Exception("entityKeyFromDTO");
            Optional<E> entityOpt = this.repository.findById(key);
            if (!entityOpt.isPresent()) throw new Exception("key: " + key.toString());
            E entity = entityOpt.get();
            this.initEntity(entity);
            D result = this.dtoFromEntity(entity);
            if (result == null) throw new Exception("dtoFromEntity");
            return result;
        } catch (Exception e) {
            log.error("find", e);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<D> findAll() throws Exception {
        log.debug("findAll");
        try {
            List<E> entities = this.repository.findAll();
            List<D> dtos = new ArrayList<>();
            for (E entity : entities) {
                this.initEntity(entity);
                dtos.add(this.dtoFromEntity(entity));
            }
            return dtos;
        } catch (Exception e) {
            log.error("findAll", e);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<D> findByAnd(D dto, int iDisplayStart, int iDisplayLength, String selectClause, String orderByClause) throws Exception {
        log.debug("findByAnd");
        boolean throwExceptionOnEmpty = false; // Renamed for clarity

        try {
            if (dto == null) {
                throw new Exception("param dto null");
            } else if (StringUtils.isEmpty(selectClause)) {
                throw new Exception("param selectClause null or empty");
            } else {
                List<E> entities = this.findEntitiesByAnd(dto, selectClause, orderByClause, iDisplayLength, iDisplayStart);
                if (throwExceptionOnEmpty && Collections.isEmpty(entities)) {
                    throw new Exception("entities null or empty");
                } else {
                    List<D> dtos = new ArrayList<>();
                    for (E entity : entities) {
                        this.initEntity(entity); // Fixed typo: initEnity -> initEntity
                        dtos.add(this.dtoFromEntity(entity));
                    }
                    return dtos;
                }
            }
        } catch (Exception e) {
            log.error("findByAnd failed", e);
            throw e;
        }
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public int countByAnd(D dto) throws Exception {
        log.debug("countByAnd");

        try {
            if (dto == null) {
                throw new Exception("param dto null");
            } else {
                String whereClause = this.getWhereClause(dto, false);
                HashMap<String, Object> parameters = this.getParameters(dto);
                String jpqlQuery = buildCountQuery(whereClause, parameters);

                Query query = entityManager.createQuery(jpqlQuery);
                if (parameters != null && !parameters.isEmpty()) {
                    parameters.forEach(query::setParameter);
                }

                Long count = (Long) query.getSingleResult();
                return count.intValue();
            }
        } catch (Exception e) {
            log.error("countByAnd failed", e);
            throw e;
        }
    }

    private String buildCountQuery(String whereClause, HashMap<String, Object> parameters) {
        return StringUtils.isNotEmpty(whereClause) && parameters != null && !parameters.isEmpty()
                ? "SELECT COUNT(o) FROM " + this.entityName + " o" + whereClause
                : "SELECT COUNT(o) FROM " + this.entityName + " o";
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public D add(D dto, TAppUser principal) throws Exception {
        log.debug("add");
        Date now = Calendar.getInstance().getTime();
        try {
            if (dto == null) throw new Exception("param dto null");
            if (principal == null) throw new Exception("param principal null");
            E entity = this.entityFromDTO(dto);
            if (entity == null) throw new Exception("entityFromDTO");
            K key = this.entityKeyFromDTO(dto);
            if (key != null && this.repository.existsById(key)) {
                throw new Exception("entity exists: " + key.toString());
            }
            entity = this.updateEntity(ACTION.CREATE, entity, principal, now);
            this.repository.save(entity);
            audit(principal,key, "CREATE", tableName);
            D result = this.dtoFromEntity(entity);
            if (result == null) throw new Exception("dtoFromEntity");
            return result;
        } catch (Exception e) {
            log.error("add", e);
            throw e;
        }
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    public Object addObj(Object object, TAppUser principal) throws Exception {
        log.debug("addObj");
        return this.add((D) object, principal);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public D update(D dto, TAppUser principal) throws Exception {
        log.debug("update");
        Date now = Calendar.getInstance().getTime();
        try {
            if (dto == null) throw new Exception("param dto null");
            if (principal == null) throw new Exception("param principal null");
            K key = this.entityKeyFromDTO(dto);
            if (key == null) throw new Exception("entityKeyFromDTO");
            Optional<E> storedEntityOpt = this.repository.findById(key);
            if (!storedEntityOpt.isPresent()) throw new Exception("key: " + key.toString());
            E storedEntity = storedEntityOpt.get();
            this.initEntity(storedEntity);
            this.preSaveUpdateDTO(storedEntity, dto);
            E entity = this.entityFromDTO(dto);
            if (entity == null) throw new Exception("entityFromDTO");
            entity = this.updateEntity(ACTION.MODIFY, entity, principal, now);
            BeanUtils.copyProperties(entity, storedEntity);
            this.repository.save(storedEntity);
            audit(principal,key, "UPDATE", tableName);
            return dto;
        } catch (Exception e) {
            log.error("update", e);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Object updateObj(Object object, TAppUser principal) throws Exception {
        log.debug("updateObj");
        return this.update((D) object, principal);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public Object updateObjStatus(Object object, TAppUser principal, char status) throws Exception {
        log.debug("updateObjStatus");
        return this.updateStatus((D) object, principal, status);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public D updateStatus(D dto, TAppUser principal, char status) throws Exception {
        log.debug("updateStatus"); // Updated log message for clarity
        Date now = Calendar.getInstance().getTime();

        try {
            if (dto == null) {
                throw new Exception("param dto null");
            } else if (principal == null) {
                throw new Exception("param principal null");
            } else {
                K key = this.entityKeyFromDTO(dto);
                if (key == null) {
                    throw new Exception("entityKeyFromDTO returned null");
                } else {
                    Optional<E> storedEntityOpt = this.repository.findById(key);
                    if (!storedEntityOpt.isPresent()) {
                        throw new Exception("Entity not found for key: " + key.toString());
                    } else {
                        E storedEntity = storedEntityOpt.get();
                        this.initEntity(storedEntity); // Renamed from initEnity to initEntity (assuming typo fix)
                        if ("A".equalsIgnoreCase(String.valueOf(status)) || "I".equalsIgnoreCase(String.valueOf(status))) {
                            this.updateEntityStatus(storedEntity, status); // Update status (e.g., 'A' or 'I')
                            storedEntity = this.updateEntity(ACTION.MODIFY, storedEntity, principal, now); // Update audit fields
                            this.repository.save(storedEntity); // Persist changes with JpaRepository
                            dto = this.dtoFromEntity(storedEntity); // Convert back to DTO
                            String auditStatus = "A".equalsIgnoreCase(String.valueOf(status))
                                    ? ACTION.ACTIVATE.toString()
                                    : ACTION.DEACTIVATE.toString();
                            log.debug("Entity status updated to: " + auditStatus); // Optional: log the action
                        }
                        return dto;
                    }
                }
            }
        } catch (Exception e) {
            log.error("updateStatus failed", e);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public D delete(D dto, TAppUser principal) throws Exception {
        log.debug("delete");
        Date now = Calendar.getInstance().getTime();

        try {
            if (dto == null) {
                throw new Exception("param dto null");
            } else if (principal == null) {
                throw new Exception("param principal null");
            } else {
                K key = this.entityKeyFromDTO(dto);
                if (key == null) {
                    throw new Exception("entityKeyFromDTO returned null");
                } else {
                    Optional<E> storedEntityOpt = this.repository.findById(key); // Replaced getDao().find with JpaRepository
                    if (!storedEntityOpt.isPresent()) {
                        throw new Exception("Entity not found for key: " + key.toString());
                    } else {
                        E storedEntity = storedEntityOpt.get();
                        this.updateEntityStatus(storedEntity, 'I'); // Soft delete: set status to 'I'
                        this.updateEntity(ACTION.MODIFY, storedEntity, principal, now); // Update audit fields
                        this.repository.save(storedEntity); // Persist changes
                        return dto; // Return the original DTO (not updated)
                    }
                }
            }
        } catch (Exception e) {
            log.error("delete failed", e);
            throw e;
        }
    }
    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class}
    )
    public D deleteObj(Object obj, TAppUser principal) throws Exception {
        log.debug("deleteObj");
        return this.delete((D) obj, principal);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public boolean isRecordExists(D dto) throws Exception {
        log.debug("isRecordExists");

        try {
            if (dto == null) {
                throw new Exception("dto param null");
            } else {
                K key = this.entityKeyFromDTO(dto);
                if (key == null) {
                    return false; // No key means no record can exist
                } else {
                    return this.repository.existsById(key); // Use JpaRepository's existsById
                }
            }
        } catch (Exception e) {
            log.error("isRecordExists failed", e);
            throw e;
        }
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    protected List<E> findEntitiesByAnd(D dto, String selectClause, String orderByClause, int limit, int offset) throws Exception {
        log.debug("findEntitiesByAnd");

        try {
            if (dto == null) {
                throw new Exception("param dto null");
            } else if (StringUtils.isEmpty(selectClause)) {
                throw new Exception("param selectClause null or empty");
            } else if (StringUtils.isEmpty(orderByClause)) {
                throw new Exception("param orderByClause null or empty");
            } else {
                String whereClause = this.getWhereClause(dto, false);
                log.debug("whereClause: " + whereClause);
                HashMap<String, Object> parameters = this.getParameters(dto);
                String jpqlQuery = StringUtils.isEmpty(whereClause)
                        ? selectClause + orderByClause
                        : selectClause + whereClause + orderByClause;

                // Use EntityManager to execute the dynamic JPQL query
                TypedQuery<E> query = entityManager.createQuery(jpqlQuery, entityClass);
                if (parameters != null) {
                    parameters.forEach(query::setParameter); // Set query parameters
                }
                query.setFirstResult(offset); // Pagination: offset
                query.setMaxResults(limit);   // Pagination: limit

                List<E> entities = query.getResultList();
                for (E entity : entities) {
                    this.initEntity(entity); // Renamed from initEnity to initEntity (assuming typo fix)
                }

                return entities;
            }
        } catch (Exception e) {
            log.error("findEntitiesByAnd failed", e);
            throw e;
        }
    }
    protected void audit(TAppUser principal, K entityID, String actionType, String auditTag) {
        Date now = Calendar.getInstance().getTime();
        try {
            if (null == principal)
                throw new ParameterException("param principal null");
            if (null == entityID)
                throw new ParameterException("param entityID null");
            if (StringUtils.isEmpty(actionType))
                throw new ParameterException("param actionType null or empty");

            // Create TAuditLog instance
            TAuditLog auditLog = new TAuditLog();
            auditLog.setAudtRemoteIp(getLocalAddress());
            auditLog.setAudtTimestamp(now);
            auditLog.setAudtUname(principal.getUsername());
            auditLog.setAudtEvent(actionType);
            auditLog.setAudtRemarks(auditTag + " " + actionType);
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("recordAudit for {} on entity {}", actionType, entityID, e);
        }
    }
    public String getLocalAddress() {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (StringUtils.isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    protected String getOperator(boolean whereprinted) {
        return whereprinted ? " AND " : " WHERE ";
    }
    public JpaRepository<E, K> getRepository() {
        return this.repository;
    }

    public void setRepository(JpaRepository<E, K> repository) {
        this.repository = repository;
    }

    public static enum ACTION {
        CREATE, MODIFY, DELETE, ACTIVATE, DEACTIVATE;
    }
}
