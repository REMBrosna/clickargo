package com.acleda.company.student.notification.logs.service;

import com.acleda.company.student.notification.logs.model.TNotificationLogs;
import com.acleda.company.student.notification.logs.repository.NotificationLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationLogsService {

    @Autowired
    private NotificationLogsRepository notificationLogsRepository;

    public void create(TNotificationLogs notificationLogs) throws Exception {
        notificationLogsRepository.save(notificationLogs);
    }

    public TNotificationLogs newNotificationLogs(String channelType){
        TNotificationLogs tNotificationLogs = new TNotificationLogs();
        tNotificationLogs.setNolType(channelType);
        tNotificationLogs.setNolStatus('P');
        tNotificationLogs.setNolRecStatus('A');
        return tNotificationLogs;
    }
}
