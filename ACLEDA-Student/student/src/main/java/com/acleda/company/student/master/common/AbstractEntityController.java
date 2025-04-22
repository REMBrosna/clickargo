package com.acleda.company.student.master.common;

import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.configuration.principal.AccountPrincipalService;
import com.acleda.company.student.common.AbstractDTO;
import com.acleda.company.student.common.IEntityService;
import com.acleda.company.student.common.dto.EntityOrderBy;
import com.acleda.company.student.common.dto.EntityWhere;
import com.acleda.company.student.common.payload.EntityFilterRequest;
import com.acleda.company.student.common.payload.EntityFilterResponse;
import com.acleda.company.student.infrastructure.api.ApiResponse;
import com.acleda.company.student.master.utils.ServiceEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractEntityController {
    private static final Logger log = LoggerFactory.getLogger(AbstractEntityController.class);

    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    protected HttpServletRequest httpRequest;
    @Autowired
    protected HttpServletResponse httpResponse;
    @Autowired
    private AccountPrincipalService principalService;
    protected ObjectMapper objectMapper = new ObjectMapper();

    public AbstractEntityController() {
    }

    protected boolean isActionValid(String action) {
        EntityStatusAction[] actions = EntityStatusAction.values();
        EntityStatusAction[] var6 = actions;
        int var5 = actions.length;

        for (int var4 = 0; var4 < var5; ++var4) {
            EntityStatusAction a = var6[var4];
            if (a.getAction().equalsIgnoreCase(action)) {
                return true;
            }
        }

        return false;
    }

    public ResponseEntity<Object> createEntity(@PathVariable String entity, @RequestBody String object) {
        ApiResponse<Object> serviceStatus = new ApiResponse<>();
        try {
            Optional<Object> opEntity = this.createEntityProxy(entity, object);
            if (!opEntity.isPresent()) {
                serviceStatus.setMessage("Entity is null");
                serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                serviceStatus.setCode(ApiResponse.OK);
                serviceStatus.setData(opEntity.get());
                return ResponseEntity.ok(serviceStatus);
            }
        } catch (Exception var5) {
            serviceStatus.setMessage("Create entity error");
            serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getEntities(@PathVariable String entity) {
        ApiResponse<Object> serviceStatus = new ApiResponse<>();
        try {
            Optional<List> opEntities = this.getEntitiesProxy(entity);
            if (!opEntities.isPresent()) {
                serviceStatus.setMessage("entity list null or empty");
                serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                serviceStatus.setCode(ApiResponse.OK);
                serviceStatus.setData(opEntities.get());
                return ResponseEntity.ok(serviceStatus);
            }
        } catch (Exception var5) {
            serviceStatus.setMessage("Get entity error");
            serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getEntityById(@PathVariable String entity, @PathVariable String id) {
        ApiResponse<Object> serviceStatus = new ApiResponse<>();

        try {
            Optional<Object> opEntity = this.getEntityByIdProxy(entity, id);
            if (!opEntity.isPresent()) {
                serviceStatus.setMessage("entity list null or empty");
                serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                serviceStatus.setCode(ApiResponse.OK);
                serviceStatus.setData(opEntity.get());
                return ResponseEntity.ok(serviceStatus);
            }
        } catch (Exception var5) {
            serviceStatus.setMessage("Get entityById error");
            serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateEntity(@RequestBody String object, @PathVariable String entity, @PathVariable String id) {
        ApiResponse<Object> serviceStatus = new ApiResponse<>();
        try {
            Optional<Object> opEntity = this.updateEntityProxy(entity, id, object);
            if (!opEntity.isPresent()) {
                serviceStatus.setMessage("entity list null or empty");
                serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                serviceStatus.setCode(ApiResponse.OK);
                serviceStatus.setData(opEntity.get());
                return ResponseEntity.ok(serviceStatus);
            }
        } catch (Exception var5) {
            serviceStatus.setMessage("updateEntity error");
            serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateEntityStatus(@RequestBody String object, @PathVariable String entity, @PathVariable String id, String action) {
        ApiResponse<Object> serviceStatus = new ApiResponse<>();
        try {
            Optional<Object> opEntity = this.updateEntityStatusProxy(object, entity, id, action);
            if (!opEntity.isPresent()) {
                serviceStatus.setMessage("entity list null or empty");
                serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                serviceStatus.setCode(ApiResponse.OK);
                serviceStatus.setData(opEntity.get());
                return ResponseEntity.ok(serviceStatus);
            }
        } catch (Exception var5) {
            serviceStatus.setMessage("updateEntityStatus error");
            serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
        ApiResponse<Object> serviceStatus = new ApiResponse<>();

        try {
            Optional<Object> opEntity = this.getEntitiesByProxy(entity, params);
            return ResponseEntity.ok(opEntity);
        } catch (Exception var5) {
            serviceStatus.setMessage("getEntitiesBy error");
            serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected Optional<Object> createEntityProxy(String entity, String object) throws Exception {
        log.debug("createEntityProxy");

        try {
            if (StringUtils.isEmpty(entity)) {
                throw new Exception("param entity null or empty");
            } else if (StringUtils.isEmpty(object)) {
                throw new Exception("param object null or empty");
            } else if (!ServiceEntity.isExisting(entity).isPresent()) {
                throw new Exception("entity not mapped: " + entity);
            } else {
                TAppUser principal = principalService.getAccountPrincipal();
                if (principal == null) {
                    throw new Exception("principal null");
                } else {
                    Class<?> entityClass = Class.forName(ServiceEntity.getMasterServiceEntityByEntityName(entity).getEntityDTOs());
                    Object dto = this.objectMapper.readValue(object, entityClass);
                    AbstractDTO<?, ?> entityDto = (AbstractDTO) dto;
                    Object bean = this.applicationContext.getBean(ServiceEntity.getMasterServiceEntityByEntityName(entity).getEntityServices());
                    IEntityService<?, ?, ?> service = (IEntityService) bean;
                    dto = service.addObj(entityDto, principal);
                    return Optional.of(dto);
                }
            }
        } catch (Exception var9) {
            log.error("createEntityProxy", var9);
            throw var9;
        }
    }

    protected Optional<List> getEntitiesProxy(String entity) throws Exception {
        log.debug("getEntitiesProxy");

        try {
            if (StringUtils.isEmpty(entity)) {
                throw new Exception("param entity null or empty");
            } else if (!ServiceEntity.isExisting(entity).isPresent()) {
                throw new Exception("entity not mapped: " + entity);
            } else {
                Object bean = this.applicationContext.getBean(ServiceEntity.getMasterServiceEntityByEntityName(entity).getEntityServices());
                IEntityService<?, ?, ?> service = (IEntityService) bean;
                return Optional.of(service.findAll());
            }
        } catch (Exception var4) {
            log.error("getEntitiesProxy", var4);
            throw var4;
        }
    }

    protected Optional<Object> getEntityByIdProxy(String entity, String id) throws Exception {
        log.debug("getEntityByIdProxy");

        try {
            if (StringUtils.isEmpty(entity)) {
                throw new Exception("param entity null or empty");
            } else if (StringUtils.isEmpty(id)) {
                throw new Exception("param id null or empty");
            } else if (!ServiceEntity.isExisting(entity).isPresent()) {
                throw new Exception("entity not mapped: " + entity);
            } else {
                Object bean = this.applicationContext.getBean(ServiceEntity.getMasterServiceEntityByEntityName(entity).getEntityServices());
                IEntityService<?, ?, ?> service = (IEntityService) bean;
                return Optional.of(service.findById(id));
            }
        } catch (Exception var5) {
            log.error("getEntityByIdProxy", var5);
            throw var5;
        }
    }

    protected Optional<Object> updateEntityProxy(String entity, String id, String object) throws Exception {
        log.debug("updateEntityProxy");

        try {
            if (StringUtils.isEmpty(entity)) {
                throw new Exception("param entity null or empty");
            } else if (StringUtils.isEmpty(id)) {
                throw new Exception("param entity null or empty");
            } else if (StringUtils.isEmpty(object)) {
                throw new Exception("param object null or empty");
            } else if (!ServiceEntity.isExisting(entity).isPresent()) {
                throw new Exception("entity not mapped: " + entity);
            } else {
                TAppUser principal = principalService.getAccountPrincipal();
                if (principal == null) {
                    throw new Exception("principal null");
                } else {
                    Class<?> entityClass = Class.forName(ServiceEntity.getMasterServiceEntityByEntityName(entity).getEntityDTOs());
                    Object dto = this.objectMapper.readValue(object, entityClass);
                    AbstractDTO<?, ?> entityDto = (AbstractDTO) dto;
                    log.debug(entityDto.toJson());
                    Object bean = this.applicationContext.getBean((ServiceEntity.getMasterServiceEntityByEntityName(entity).getEntityServices()));
                    IEntityService<?, ?, ?> service = (IEntityService) bean;
                    dto = service.updateObj(entityDto, principal);
                    return Optional.of(dto);
                }
            }
        } catch (Exception var10) {
            log.error("updateEntityProxy", var10);
            throw var10;
        }
    }

    protected Optional<Object> updateEntityStatusProxy(String object, String entity, String id, String action) throws Exception {
        log.debug("updateEntityProxy");

        try {
            if (StringUtils.isEmpty(entity)) {
                throw new Exception("param entity null or empty");
            } else if (StringUtils.isEmpty(id)) {
                throw new Exception("param entity null or empty");
            } else if (StringUtils.isEmpty(object)) {
                throw new Exception("param object null or empty");
            } else if (StringUtils.isEmpty(action)) {
                throw new Exception("param action null or empty");
            } else if (!ServiceEntity.isExisting(entity).isPresent()) {
                throw new Exception("entity not mapped: " + entity);
            } else if (!this.isActionValid(action)) {
                throw new Exception("action is not valid");
            } else {
                char status = 'A';
                if (action.equals(EntityStatusAction.INACTIVATE.getAction())) {
                    status = 'I';
                }

                TAppUser principal = principalService.getAccountPrincipal();
                if (principal == null) {
                    throw new Exception("principal null");
                } else {
                    Class<?> entityClass = Class.forName(ServiceEntity.getMasterServiceEntityByEntityName(entity).getEntityDTOs());
                    Object dto = this.objectMapper.readValue(object, entityClass);
                    AbstractDTO<?, ?> entityDto = (AbstractDTO) dto;
                    log.debug(entityDto.toJson());
                    Object bean = this.applicationContext.getBean((String) ServiceEntity.getMasterServiceEntityByEntityName(entity).getEntityServices());
                    IEntityService<?, ?, ?> service = (IEntityService) bean;
                    dto = service.updateObjStatus(entityDto, principal, status);
                    return Optional.of(dto);
                }
            }
        } catch (Exception var12) {
            log.error("updateEntityProxy", var12);
            throw var12;
        }
    }

    protected Optional<Object> getEntitiesByProxy(String entity, Map<String, String> params) throws Exception {
        log.debug("getEntitiesProxy");

        try {
            if (StringUtils.isEmpty(entity)) {
                throw new Exception("param entity null or empty");
            } else if (Collections.isEmpty(params)) {
                throw new Exception("param params null or empty");
            } else if (!ServiceEntity.isExisting(entity).isPresent()) {
                throw new Exception("entity not mapped: " + entity);
            } else {
                EntityFilterRequest filterRequest = new EntityFilterRequest();
                filterRequest.setDisplayStart(params.containsKey("iDisplayStart") ? Integer.valueOf(params.get("iDisplayStart")) : -1);
                filterRequest.setDisplayLength(params.containsKey("iDisplayLength") ? Integer.valueOf(params.get("iDisplayLength")) : -1);
                ArrayList<EntityWhere> whereList = new ArrayList();
                List<String> searches = (List) params.keySet().stream().filter((x) -> {
                    return x.contains("sSearch_");
                }).collect(Collectors.toList());

                for (int nIndex = 1; nIndex <= searches.size(); ++nIndex) {
                    String searchParam = params.get("sSearch_" + nIndex);
                    String valueParam = params.get("mDataProp_" + nIndex);
                    log.debug("searchParam: " + searchParam + " valueParam: " + valueParam);
                    whereList.add(new EntityWhere(valueParam, searchParam));
                }

                filterRequest.setWhereList(whereList);
                Optional<String> opSortAttribute = Optional.ofNullable(params.get("mDataProp_0"));
                Optional<String> opSortOrder = Optional.ofNullable(params.get("sSortDir_0"));
                if (opSortAttribute.isPresent() && opSortOrder.isPresent()) {
                    EntityOrderBy orderBy = new EntityOrderBy();
                    orderBy.setAttribute(opSortAttribute.get());
                    orderBy.setOrdered(opSortOrder.get().equalsIgnoreCase("desc") ? EntityOrderBy.ORDERED.DESC : EntityOrderBy.ORDERED.ASC);
                    filterRequest.setOrderBy(orderBy);
                }

                if (!filterRequest.isValid()) {
                    throw new Exception("Invalid request: " + filterRequest.toString());
                } else {
                    Object bean = this.applicationContext.getBean(ServiceEntity.getMasterServiceEntityByEntityName(entity).getEntityServices());
                    IEntityService<?, ?, ?> service = (IEntityService) bean;
                    List<Object> entities = (List<Object>) service.filterBy(filterRequest);
                    EntityFilterResponse filterResponse = new EntityFilterResponse();
                    filterResponse.setData((ArrayList) entities);
                    filterResponse.setTotalRecords(entities.size());
                    filterResponse.setTotalDisplayRecords(filterRequest.getTotalRecords());
                    return Optional.of(filterResponse);
                }
            }
        } catch (Exception var12) {
            log.error("getEntitiesProxy", var12);
            throw var12;
        }
    }

    public ResponseEntity<Object> deleteEntityById(@PathVariable String entity, @PathVariable String id) {
        ApiResponse<Object> serviceStatus = new ApiResponse<>();

        try {
            this.deleteEntityByIdProxy(entity, id);
            serviceStatus.setCode(ApiResponse.OK);
            serviceStatus.setMessage("Success");
            serviceStatus.setData(true);
            return ResponseEntity.ok(serviceStatus);
        } catch (Exception var5) {
            serviceStatus.setData(false);
            serviceStatus.setMessage("deleteEntityById error");
            serviceStatus.setCode(ApiResponse.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    protected void deleteEntityByIdProxy(String entity, String id) throws Exception {
        log.debug("getEntityByIdProxy");

        try {
            if (StringUtils.isEmpty(entity)) {
                throw new Exception("param entity null or empty");
            } else if (StringUtils.isEmpty(id)) {
                throw new Exception("param id null or empty");
            } else if (!ServiceEntity.isExisting(entity).isPresent()) {
                throw new Exception("entity not mapped: " + entity);
            } else {
                TAppUser principal = principalService.getAccountPrincipal();
                if (principal == null) {
                    throw new Exception("principal null");
                } else {
                    Object bean = this.applicationContext.getBean(ServiceEntity.getMasterServiceEntityByEntityName(entity).getEntityServices());
                    IEntityService<?, ?, ?> service = (IEntityService) bean;
                    AbstractDTO<?, ?> entityDto = (AbstractDTO) service.findById(id);
                    if (entityDto == null) {
                        throw new Exception("entity not found: " + id);
                    } else {
                        log.debug(entityDto.toJson());
                        service.deleteObj(entityDto, principal);
                    }
                }
            }
        } catch (Exception var7) {
            log.error("getEntityByIdProxy", var7);
            throw var7;
        }
    }
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static enum EntityStatusAction {
        ACTIVATE("activate"),
        INACTIVATE("inactivate");

        String action;

        private EntityStatusAction(String action) {
            this.action = action;
        }

        public String getAction() {
            return this.action;
        }
    }
}
