package com.acleda.company.student.administrator.service;

import com.acleda.company.student.administrator.dto.AppUser;
import com.acleda.company.student.administrator.dto.ChangePassword;
import com.acleda.company.student.administrator.model.Role;
import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.administrator.repository.AppUserRepository;
import com.acleda.company.student.audit.dto.AuditLog;
import com.acleda.company.student.audit.model.TAuditLog;
import com.acleda.company.student.common.AbstractEntityService;
import com.acleda.company.student.common.IEntityService;
import com.acleda.company.student.common.dto.EntityWhere;
import com.acleda.company.student.common.payload.EntityFilterRequest;
import com.acleda.company.student.configuration.principal.AccountPrincipalService;
import com.acleda.company.student.websocket.service.ChatService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

@Service("userService")
public class UserService extends AbstractEntityService<TAppUser, String, AppUser> {
    private static final String auditTag = "TAppUser";
    private static final String tableName = "appuser";
    protected static Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService() {
        super("appUserRepository", "studentsService", "TAppUser", TAppUser.class);
    }
    @Autowired
    private AccountPrincipalService accountPrincipalService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    @Qualifier("auditLogService")
    private IEntityService<TAuditLog, String, AuditLog> auditLogService;


    @Override
    protected TAppUser initEntity(TAppUser entity) throws Exception {
        return entity;
    }

    @Override
    protected TAppUser entityFromDTO(AppUser dto) throws Exception {
        log.debug("userService.entityFromDTO");
        try {
            if (null == dto) {
                throw new Exception("dto is null");
            }
            //check if user is null get email instead
            TAppUser user = appUserRepository.findAppUserByUsernameOrEmail(
                    dto.getUsername() != null ? dto.getUsername() : dto.getEmail()
            );
            if (Objects.nonNull(user)) {
                // Copy basic fields
                BeanUtils.copyProperties(dto, user, getNullOrIgnoredProperties(dto, "password", "roles"));

                // Manually update roles
                if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
                    user.getRoles().clear();
                    user.getRoles().addAll(dto.getRoles());
                }
                return user;
            }
            else {
                TAppUser entity = new TAppUser();
                entity = dto.toEntity(entity);
                return entity;
            }
        } catch (Exception ex) {
            log.error("userService.entityFromDTO", ex);
            throw ex;
        }
    }

    public static String[] getNullOrIgnoredProperties(Object source, String... ignoredProperties) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> excluded = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object value = src.getPropertyValue(pd.getName());
            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                excluded.add(pd.getName());
            }
        }

        if (ignoredProperties != null) {
            excluded.addAll(Arrays.asList(ignoredProperties));
        }

        return excluded.toArray(new String[0]);
    }

    @Override
    protected AppUser dtoFromEntity(TAppUser entity) throws Exception {
        log.debug("userService.dtoFromEntity");
        try {
            if (null == entity) {
                throw new Exception("param entity null");
            }
            AppUser dto = new AppUser();
            dto.setId(String.valueOf(entity.getId()));
            dto.setUsername(entity.getUsername());
            dto.setEmail(entity.getEmail());
            dto.setFirstname(entity.getFirstname());
            dto.setLastname(entity.getLastname());
            dto.setFullName(entity.getFirstname().concat(" ").concat(entity.getLastname()));
            dto.setAddress(entity.getAddress());
            dto.setGender(entity.getGender());
            dto.setDtOfBirth(entity.getDtOfBirth());
            dto.setConNumber(entity.getConNumber());
            dto.setUsrDtCreate(entity.getUsrDtCreate());
            dto.setStatus(entity.getStatus());
            dto.setMessageCount(chatService.getMessagesFromSender(entity.getUsername()).size());
            return dto;
        } catch (Exception ex) {
            log.error("userService.dtoFromEntity", ex);
            throw ex;
        }

    }

    @Override
    protected String entityKeyFromDTO(AppUser dto) throws Exception {
        log.debug("userService.entityKeyFromDTO");

        try {
            if (null == dto) {
                throw new Exception("dto param null");
            }
            return dto.getId();
        } catch (Exception ex) {
            log.error("userService.entityKeyFromDTO", ex);
            throw ex;
        }
    }

    @Override
    protected TAppUser updateEntity(ACTION attribute, TAppUser entity, TAppUser principal, Date date) throws Exception {
        log.debug("userService.updateEntity");

        try {
            if (null == entity)
                throw new Exception("param entity is null");
            if (null == principal)
                throw new Exception("param principal is null");
            if (null == date)
                throw new Exception("param date is null");


            return entity;
        } catch (Exception ex) {
            log.error("userService.updateEntity", ex);
            throw ex;
        }
    }

    @Override
    protected TAppUser updateEntityStatus(TAppUser entity, char status) throws Exception {
        log.debug("userService.updateEntityStatus");

        try {
            if (null == entity) {
                throw new Exception("entity param is null");
            }
            entity.setStatus(status);
            return entity;
        } catch (Exception ex) {
            log.error("userService.updateEntityStatus", ex);
            throw ex;
        }

    }

    @Override
    protected AppUser preSaveUpdateDTO(TAppUser storedEntity, AppUser dto) throws Exception {
        log.debug("userService.preSaveUpdateDTO");

        try {
            if (null == storedEntity) {
                throw new Exception("param storedEntity is null");
            }
            if (null == dto) {
                throw new Exception("param dto is null");
            }


            return dto;
        } catch (Exception ex) {
            log.error("userService.preSaveUpdateDTO", ex);
            throw ex;
        }

    }

    @Override
    protected String getWhereClause(AppUser dto, boolean wherePrinted) throws Exception {
        log.debug("userService.getWhereClause");

        try {
            TAppUser principal = accountPrincipalService.getAccountPrincipal();
            if (null == dto) {
                throw new Exception("param dto null");
            }
            StringBuilder searchStatement = new StringBuilder();

            boolean isStudent = principal.getRoles().stream()
                    .anyMatch(t -> t.getName().equalsIgnoreCase("ROLE_STUDENT"));

            boolean isAdmin = principal.getRoles().stream()
                    .anyMatch(t -> t.getName().equalsIgnoreCase("ROLE_ADMIN"));

            if (isAdmin) {
                // Admin should see all student records except their own
                searchStatement.append(" JOIN o.roles r ");
                searchStatement.append(getOperator(wherePrinted) + "r.name = :role ");
                wherePrinted = true;

//                if (principal.getUsername() != null) {
//                    searchStatement.append(" AND o.username != :currentUsername ");
//                }
            } else if (isStudent) {
                // Student sees only their own record
                searchStatement.append(" JOIN o.roles r ");
                searchStatement.append(getOperator(wherePrinted) + "r.name = :role ");
                wherePrinted = true;

                if (!StringUtils.isEmpty(dto.getUsername())) {
                    searchStatement.append(" AND o.username = :username ");
                }
            }
            searchStatement.append(" AND o.status = :status ");
            return searchStatement.toString();
        } catch (Exception ex) {
            log.error("userService.getWhereClause", ex);
            throw ex;
        }
    }


    @Override
    protected HashMap<String, Object> getParameters(AppUser dto) throws Exception {
        log.debug("userService.getParameters");
        TAppUser principal = accountPrincipalService.getAccountPrincipal();
        try {
            if (null == dto) {
                throw new Exception("param dto null");
            }
            boolean isStudent = principal.getRoles().stream()
                    .anyMatch(t -> t.getName().equalsIgnoreCase("ROLE_STUDENT"));
            boolean isAdmin = principal.getRoles().stream()
                    .anyMatch(t -> t.getName().equalsIgnoreCase("ROLE_ADMIN"));
            HashMap<String, Object> parameters = new HashMap<>();
            if (!StringUtils.isEmpty(dto.getUsername()) && isStudent) {
                parameters.put("username", dto.getUsername());
            }
            if (!principal.getRoles().isEmpty() && isStudent) {
                parameters.put("role", principal.getRoles().stream()
                        .map(Role::getName)
                        .findFirst()
                        .orElse(null));
            }
            if (isAdmin) {
                parameters.put("role", "ROLE_STUDENT");
            }
            parameters.put("status", 'A');
            return parameters;
        } catch (Exception ex) {
            log.error("userService.getParameters", ex);
            throw ex;
        }
    }

    @Override
    protected AppUser whereDto(EntityFilterRequest filterRequest) throws Exception {
        try {
            if (null == filterRequest)
                throw new Exception("param filterRequest null");

            AppUser dto = new AppUser();
            for (EntityWhere entityWhere : filterRequest.getWhereList()) {
                Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
                if (!opValue.isPresent())
                    continue;

                if (entityWhere.getAttribute().equalsIgnoreCase("username")) {
                    dto.setUsername(opValue.get());
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
    public AppUser findById(String id) throws Exception {
        log.debug("StudentsService.findById");

        try {
            if (StringUtils.isEmpty(id)) {
                throw new Exception("param id is null or empty");
            }

            Optional<TAppUser> entity = repository.findById(id);
            if (!entity.isPresent()) {
                throw new Exception("AppUser not found with id: " + id);
            }

            TAppUser initializedEntity = this.initEntity(entity.get());

            return this.dtoFromEntity(initializedEntity);
        } catch (Exception ex) {
            log.error("StudentsService.findById", ex);
            throw ex;
        }
    }


    @Override
    public List<AppUser> findByAnd(AppUser var1, int var2, int var3, String var4, String var5) throws Exception {
        return List.of();
    }

    @Override
    public int countByAnd(AppUser var1) throws Exception {
        return 0;
    }

    @Override
    public Object addObj(Object object, TAppUser appUser) throws Exception {
        return super.addObj(object, appUser);
    }

    @Override
    public AppUser update(AppUser var1, TAppUser var2, boolean var3) throws Exception {
        return null;
    }

    @Override
    public Object updateObj(Object object, TAppUser appUser) throws Exception {
        return super.updateObj(object, appUser);
    }

    @Override
    public AppUser updateStatus(AppUser var1, TAppUser var2, char var3) throws Exception {
        return null;
    }

    @Override
    public Object updateObjStatus(Object var1, TAppUser var2, char var3) throws Exception {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public AppUser delete(AppUser dto, TAppUser appUser) throws Exception {
        Date now = Calendar.getInstance().getTime();
        try {
            if (StringUtils.isEmpty(dto.getId()))
                throw new Exception("param id null or empty");
            if (null == appUser)
                throw new Exception("param principal null");

            Optional<TAppUser> optionalEntity = repository.findById(dto.getId());
            if (!optionalEntity.isPresent())
                throw new Exception("Student not found with id: " + dto.getId());

            TAppUser entity = optionalEntity.get();
            entity = this.initEntity(entity);

            // Mark as inactive (soft delete)
            this.updateEntityStatus(entity, 'I');
            this.updateEntity(ACTION.MODIFY, entity, appUser, now);

            // Optionally convert updated entity back to DTO and return
            return dtoFromEntity(entity);
        } catch (Exception ex) {
            log.error("StudentsService.delete", ex);
            throw ex;
        }
    }

    @Override
    public AppUser deleteById(String id, TAppUser principal) throws Exception {
        log.debug("StudentsService.deleteById");

        Date now = Calendar.getInstance().getTime();
        try {
            if (StringUtils.isEmpty(id))
                throw new Exception("param id null or empty");
            if (null == principal)
                throw new Exception("param principal null");

            Optional<TAppUser> optionalEntity = repository.findById(id);
            if (!optionalEntity.isPresent())
                throw new Exception("AppUser not found with id: " + id);

            TAppUser entity = optionalEntity.get();
            entity = this.initEntity(entity);

            this.updateEntityStatus(entity, 'I');
            this.updateEntity(ACTION.MODIFY, entity, principal, now);

            AppUser dto = dtoFromEntity(entity);
            this.delete(dto, principal);
            return dto;
        } catch (Exception ex) {
            log.error("StudentsService.deleteById", ex);
            throw ex;
        }
    }


    @Override
    public List<AppUser> filterBy(EntityFilterRequest filterRequest) throws Exception {
        try {
            if (null == filterRequest)
                throw new Exception("param filterRequest null");

            AppUser dto = this.whereDto(filterRequest);
            if (null == dto)
                throw new Exception("whereDto null");

            filterRequest.setTotalRecords(super.countByAnd(dto));

            String selectClause = "FROM TAppUser o ";
            String orderByClause = filterRequest.getOrderBy().toString();
            List<TAppUser> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
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
    public boolean isRecordExists(AppUser var1) throws Exception {
        return false;
    }

    @Override
    public AppUser deleteObj(Object object, TAppUser appUser) throws Exception {
        return super.deleteObj(object, appUser);
    }



}