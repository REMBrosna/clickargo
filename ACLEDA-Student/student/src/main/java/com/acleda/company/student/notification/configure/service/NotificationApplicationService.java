package com.acleda.company.student.notification.configure.service;

import com.acleda.company.student.notification.configure.model.TNotificationApplication;
import com.acleda.company.student.notification.configure.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationApplicationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void create(TNotificationApplication notificationApplication) throws Exception {
        notificationRepository.save(notificationApplication);
    }

}
