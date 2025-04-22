package com.acleda.company.student.notification.template.service;

import com.acleda.company.student.notification.template.repository.NotificationTemplateRepository;
import com.acleda.company.student.notification.template.model.TNotificationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationTemplateService {

    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;

    public void create(TNotificationTemplate tNotificationTemplate) throws Exception {
        notificationTemplateRepository.save(tNotificationTemplate);
    }

    public Optional<TNotificationTemplate> getTNotificationTemplateByTemChannelType(Long id) throws Exception {
        return notificationTemplateRepository.findById(id);
    }
}
