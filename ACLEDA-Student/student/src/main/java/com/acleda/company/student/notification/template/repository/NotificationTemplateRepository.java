package com.acleda.company.student.notification.template.repository;

import com.acleda.company.student.notification.template.model.TNotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTemplateRepository extends JpaRepository<TNotificationTemplate, Long> {
}
