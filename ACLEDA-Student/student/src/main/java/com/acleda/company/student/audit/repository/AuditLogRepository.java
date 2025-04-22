package com.acleda.company.student.audit.repository;

import com.acleda.company.student.audit.model.TAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("auditLogRepository")
public interface AuditLogRepository extends JpaRepository<TAuditLog, String> {
}
