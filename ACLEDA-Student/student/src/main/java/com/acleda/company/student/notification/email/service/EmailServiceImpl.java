package com.acleda.company.student.notification.email.service;

import com.acleda.company.student.administrator.enums.EnumContentType;
import com.acleda.company.student.notification.param.EmailParam;
import com.acleda.company.student.notification.template.model.TNotificationTemplate;
import com.acleda.company.student.notification.template.service.NotificationTemplateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Optional;

@Service
@Log4j2
public class EmailServiceImpl{

    private final NotificationTemplateService templateService;
    private final JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(NotificationTemplateService templateService, JavaMailSender emailSender) {
        this.templateService = templateService;
        this.emailSender = emailSender;
    }

    @Async
    public void notify(EmailParam emailParam) {
        try {
            if (emailParam == null || emailParam.getTo() == null || emailParam.getTemplateId() == null) {
                log.warn("EmailParam, recipient, or templateId is null. Skipping email send.");
                return;
            }

            Optional<TNotificationTemplate> optionalTemplate = templateService.getTNotificationTemplateByTemChannelType(emailParam.getTemplateId());
            TNotificationTemplate notificationTemplate = optionalTemplate.get();
            if (notificationTemplate.getNotContentType().equalsIgnoreCase(EnumContentType.HTML.name())){
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(emailParam.getTo());
                helper.setCc("monitor@gmail.com");
                helper.setSubject(notificationTemplate.getNotSubject());
                helper.setText(updateContent(emailParam, notificationTemplate.getNotContent()), true);
                emailSender.send(message);
            } else {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(emailParam.getTo());
                message.setCc("monitor@gmail.com");
                message.setSubject(notificationTemplate.getNotSubject());
                message.setText(updateContent(emailParam, notificationTemplate.getNotContent()));
                emailSender.send(message);
            }
            log.info("✅ Email sent successfully to {}", String.join(", ", emailParam.getTo()));
        } catch (MailException | MessagingException exception) {
            log.error("📧 Failed to send email due to mail/messaging error", exception);
        } catch (Exception e) {
            log.error("📧 Unexpected error occurred while sending email", e);
        }
    }
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        } catch (MailException exception) {
            log.error("sendSimpleMessage ", exception);
        }
    }

    private String updateContent(EmailParam emailParam, String template){
        HashMap<String, String> hmFields = emailParam.getContentFields();
        for (String key : hmFields.keySet()) {
            String value = hmFields.get(key);
            template = template.replaceAll(key, value);
        }
        return template;
    }
}