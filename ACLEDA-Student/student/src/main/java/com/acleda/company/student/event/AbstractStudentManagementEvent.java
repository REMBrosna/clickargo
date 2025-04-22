package com.acleda.company.student.event;

import com.acleda.company.student.notification.logs.model.TNotificationLogs;
import com.acleda.company.student.notification.logs.service.NotificationLogsService;
import com.acleda.company.student.notification.param.EmailParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Log4j2
public abstract class AbstractStudentManagementEvent {
    @Autowired
    private NotificationLogsService notificationLogsService;
    protected abstract EmailParam emailParam(String obj);
    protected void saveNotificationLogs(String obj) throws Exception {
        notificationLogsService.create(covertParamToJson(Long.valueOf("1"), obj));
    }
    private TNotificationLogs covertParamToJson(Long templateId, String object) throws Exception {
        TNotificationLogs obj = notificationLogsService.newNotificationLogs("EMAIL");
        EmailParam emailParam = emailParam(object);
        emailParam.setTemplateId(templateId);
        obj.setNolBody(emailParam.toJson());
        notificationLogsService.create(obj);
        return obj;
    }

    protected String[] convertListToArrayString(List<String> list){
        String[] strArr = new String[list.size()];
        strArr = list.toArray(strArr);
        return strArr;
    }
}
