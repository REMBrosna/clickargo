package com.acleda.company.student.administrator.listener;

import com.acleda.company.student.administrator.model.TAppUser;
import com.acleda.company.student.administrator.repository.AppUserRepository;
import com.acleda.company.student.administrator.repository.impl.UserDetailsServiceImpl;
import com.acleda.company.student.event.AbstractStudentManagementEvent;
import com.acleda.company.student.event.AppUserEvent;
import com.acleda.company.student.notification.email.service.EmailServiceImpl;
import com.acleda.company.student.notification.param.EmailParam;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
@AllArgsConstructor
@Slf4j
public class AppUserEventListener extends AbstractStudentManagementEvent implements ApplicationListener<AppUserEvent> {
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private EmailServiceImpl emailService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(AppUserEvent event) {
        super.saveNotificationLogs(event.getTAppUser().getUsername());

        try {
            TAppUser user = appUserRepository.findAppUserByUsername(event.getTAppUser().getUsername());
            if (Objects.nonNull(user)) {
                HashMap<String, String> content = new HashMap<>();
                content.put(":username", user.getEmail());
                content.put(":password", event.getNewPassword());

                List<String> recipientList = new ArrayList<>();
                recipientList.add(user.getEmail());

                EmailParam emailParam = new EmailParam();
                emailParam.setTo(convertListToArrayString(recipientList));
                emailParam.setContentFields(content);

                String templateId = "1";
                emailParam.setTemplateId(Long.valueOf(templateId));
                // ✅ Static/fake CC
                List<String> ccList = new ArrayList<>();
                ccList.add("monitor@gmail.com"); // Change to any default CC email
                emailParam.setCc(convertListToArrayString(ccList));

                emailService.notify(emailParam);
                log.info("EmailParam ready and notification sent to {}", user.getEmail());
            }
        } catch (Exception e) {
            log.error("Error in onApplicationEvent", e);
        }
    }
    @Override
    protected EmailParam emailParam(String username) {
        // Optional override if your AbstractStudentManagementEvent uses this
        EmailParam emailParam = new EmailParam();
        try {
            TAppUser user = appUserRepository.findAppUserByUsername(username);
            if (Objects.nonNull(user)) {
                HashMap<String, String> content = new HashMap<>();
                content.put(":username", user.getEmail());
                content.put(":password", ""); // no password here unless it's available

                List<String> recipientList = new ArrayList<>();
                recipientList.add(user.getEmail());

                emailParam.setTo(convertListToArrayString(recipientList));
                emailParam.setContentFields(content);
            }
        } catch (Exception e) {
            log.error("Error building emailParam in emailParam()", e);
        }
        return emailParam;
    }
}


