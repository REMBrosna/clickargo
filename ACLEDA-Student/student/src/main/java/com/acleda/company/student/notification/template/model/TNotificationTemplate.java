package com.acleda.company.student.notification.template.model;

import com.acleda.company.student.common.COAbstractEntity;
import com.acleda.company.student.notification.logs.model.TNotificationLogs;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "T_NOTIFICATION_TEMPLATE")
public class TNotificationTemplate extends COAbstractEntity<TNotificationLogs> {

    @Serial
    private static final long serialVersionUID = 6358402997937898615L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOT_ID")
    private Long notId;

    @Column(name = "NOT_CHANNEL_TYPE")
    private String notChannelType;

    @Column(name = "NOT_SUBJECT")
    private String notSubject;

    @Column(name = "NOT_CONTENT_TYPE")
    private String notContentType;

    @Column(name = "NOT_CONTENT")
    private String notContent;

    @Column(name = "NOT_DESC")
    private String notDesc;

    @Column(name = "NOT_REC_STATUS", nullable = false, length = 1)
    private Character notRecStatus;

    @Override
    public void init() {

    }

    @Override
    public int compareTo(TNotificationLogs o) {
        return 0;
    }
}
