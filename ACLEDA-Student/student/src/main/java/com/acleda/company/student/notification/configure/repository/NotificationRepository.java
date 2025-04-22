package com.acleda.company.student.notification.configure.repository;


import com.acleda.company.student.notification.configure.model.TNotificationApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<TNotificationApplication, Long> {

}
