package com.acleda.company.student.master.utils;

import java.util.Arrays;
import java.util.Optional;

public enum ServiceEntity {
    USER("users", "com.acleda.company.student.administrator.dto.AppUser", "userService"),
    AUDIT_LOG("auditLog", "com.guud.company.library.audit.dto.AuditLog", "auditLogService");

    private final String entityName;
    private final String entityDTOs;
    private final String entityServices;

    ServiceEntity(String entityName, String entityDTOs, String entityServices) {
        this.entityName = entityName;
        this.entityServices = entityServices;
        this.entityDTOs = entityDTOs;
    }

    public static Optional<ServiceEntity> isExisting(String entityName) {
        return Arrays.stream(ServiceEntity.values())
                .filter(env -> env.entityName.equals(entityName))
                .findFirst();
    }

    public static ServiceEntity getMasterServiceEntityByEntityName(String entityName) {
        return Arrays.stream(ServiceEntity.values())
                .filter(env -> env.entityName.equals(entityName))
                .findFirst().orElse(null);
    }
    public String getEntityName() {
        return entityName;
    }

    public String getEntityServices() {
        return entityServices;
    }

    public String getEntityDTOs() {
        return entityDTOs;
    }
}
