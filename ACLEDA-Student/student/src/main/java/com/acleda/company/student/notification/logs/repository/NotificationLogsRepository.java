package com.acleda.company.student.notification.logs.repository;


import com.acleda.company.student.notification.logs.model.TNotificationLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogsRepository extends JpaRepository<TNotificationLogs, Long> {
}
