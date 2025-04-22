package com.acleda.company.student.notification.configure.dto;


import com.acleda.company.student.common.AbstractDTO;
import com.acleda.company.student.notification.configure.model.TNotificationApplication;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationApplication extends AbstractDTO<NotificationApplication, TNotificationApplication> {

    private Long notId;
    private String notAction;
    private Long notEmailTemplateId;
    private Character notRequiredEmail;
    private Character notRecStatus;
    private Date notDtCreate;
    private String notUidCreate;
    private Date notDtLupd;
    private String notUidLupd;

    @Override
    public void init() {

    }

    @Override
    public int compareTo(NotificationApplication o) {
        return 0;
    }
}
