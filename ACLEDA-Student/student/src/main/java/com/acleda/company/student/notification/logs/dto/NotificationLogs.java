package com.acleda.company.student.notification.logs.dto;


import com.acleda.company.student.common.AbstractDTO;
import com.acleda.company.student.notification.logs.model.TNotificationLogs;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;

public class NotificationLogs extends AbstractDTO<NotificationLogs, TNotificationLogs> {

    @Serial
    private static final long serialVersionUID = -1038804419128970736L;
    private Long nolId;
    private String nolBody;
    private int nolRetry;
    private String nolType;
    private Character nolStatus;
    private Character nolRecStatus;

    public NotificationLogs() {
    }

    public NotificationLogs(TNotificationLogs entity) {
        super(entity);
    }

    public NotificationLogs(Long nolId, String nolBody, int nolRetry, String nolType, Character nolStatus, Character nolRecStatus) {
        this.nolId = nolId;
        this.nolBody = nolBody;
        this.nolRetry = nolRetry;
        this.nolType = nolType;
        this.nolStatus = nolStatus;
        this.nolRecStatus = nolRecStatus;
    }

    public Long getNolId() {
        return nolId;
    }

    public void setNolId(Long nolId) {
        this.nolId = nolId;
    }

    public String getNolBody() {
        return nolBody;
    }

    public void setNolBody(String nolBody) {
        this.nolBody = nolBody;
    }

    public int getNolRetry() {
        return nolRetry;
    }

    public void setNolRetry(int nolRetry) {
        this.nolRetry = nolRetry;
    }

    public String getNolType() {
        return nolType;
    }

    public void setNolType(String nolType) {
        this.nolType = nolType;
    }

    public Character getNolStatus() {
        return nolStatus;
    }

    public void setNolStatus(Character nolStatus) {
        this.nolStatus = nolStatus;
    }

    public Character getNolRecStatus() {
        return nolRecStatus;
    }

    public void setNolRecStatus(Character nolRecStatus) {
        this.nolRecStatus = nolRecStatus;
    }

    @Override
    public void init() {

    }

    @Override
    public int compareTo(@NotNull NotificationLogs o) {
        return 0;
    }
}
