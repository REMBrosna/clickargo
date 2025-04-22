package com.acleda.company.student.notification.configure.model;

import com.acleda.company.student.common.COAbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_NOTIFICATION_APP")
public class TNotificationApplication extends COAbstractEntity<TNotificationApplication> {

    @Serial
    private static final long serialVersionUID = -4332267580257361093L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOT_ID")
    private Long notId;

    @Column(name = "NOT_ACTION")
    private String notAction;

    @Column(name = "NOT_EMAIL_TEMPLATE_ID")
    private Long notEmailTemplateId;

    @Column(name = "NOT_REQUIRED_EMAIL")
    private Character notRequiredEmail;

    @Column(name = "NOT_REC_STATUS")
    private Character notRecStatus;

    @Column(name = "NOT_DT_CREATE")
    private Date notDtCreate;

    @Column(name = "NOT_UID_CREATE")
    private String notUidCreate;

    @Column(name = "NOT_DT_LUPD")
    private Date notDtLupd;

    @Column(name = "NOT_UID_LUPD")
    private String notUidLupd;

    @Override
    public void init() {
        // Initialization logic if needed
    }

    @Override
    public int compareTo(TNotificationApplication o) {
        return 0;
    }
}

