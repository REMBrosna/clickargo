package com.acleda.company.student.notification.logs.model;

import com.acleda.company.student.common.COAbstractEntity;
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
@Table(name = "T_NOTIFICATION_LOGS")
public class TNotificationLogs extends COAbstractEntity<TNotificationLogs> {

    @Serial
    private static final long serialVersionUID = -5436487979180618604L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOL_ID")
    private Long nolId;

    @Column(name = "NOL_BODY")
    private String nolBody;

    @Column(name = "NOL_RETRY")
    private int nolRetry;

    @Column(name = "NOL_TYPE")
    private String nolType;

    @Column(name = "NOL_STATUS", nullable = false, length = 1)
    private Character nolStatus;

    @Column(name = "NOL_REC_STATUS", nullable = false, length = 1)
    private Character nolRecStatus;

    @Override
    public void init() {
        // Custom initialization logic (if needed)
    }

    @Override
    public int compareTo(@NotNull TNotificationLogs o) {
        return 0;
    }
}
