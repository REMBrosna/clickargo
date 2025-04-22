package com.acleda.company.student.notification.template.dto;


import com.acleda.company.student.common.AbstractDTO;
import com.acleda.company.student.notification.template.model.TNotificationTemplate;
import jakarta.validation.constraints.NotNull;

public class NotificationTemplate extends AbstractDTO<NotificationTemplate, TNotificationTemplate> {

    private static final long serialVersionUID = 271232349624670715L;
    private Long notId;
    private String notChannelType;
    private String notSubject;
    private String notContentType;
    private String notContent;
    private String notDesc;
    private Character notRecStatus;

    public NotificationTemplate() {
    }

    public NotificationTemplate(Long notId, String notChannelType, String notSubject, String notContentType, String notContent, String notDesc, Character notRecStatus) {
        this.notId = notId;
        this.notChannelType = notChannelType;
        this.notSubject = notSubject;
        this.notContentType = notContentType;
        this.notContent = notContent;
        this.notDesc = notDesc;
        this.notRecStatus = notRecStatus;
    }

    public Long getNotId() {
        return notId;
    }

    public void setNotId(Long notId) {
        this.notId = notId;
    }

    public String getNotChannelType() {
        return notChannelType;
    }

    public void setNotChannelType(String notChannelType) {
        this.notChannelType = notChannelType;
    }

    public String getNotSubject() {
        return notSubject;
    }

    public void setNotSubject(String notSubject) {
        this.notSubject = notSubject;
    }

    public String getNotContentType() {
        return notContentType;
    }

    public void setNotContentType(String notContentType) {
        this.notContentType = notContentType;
    }

    public String getNotContent() {
        return notContent;
    }

    public void setNotContent(String notContent) {
        this.notContent = notContent;
    }

    public String getNotDesc() {
        return notDesc;
    }

    public void setNotDesc(String notDesc) {
        this.notDesc = notDesc;
    }

    public Character getNotRecStatus() {
        return notRecStatus;
    }

    public void setNotRecStatus(Character notRecStatus) {
        this.notRecStatus = notRecStatus;
    }

    @Override
    public void init() {

    }

    @Override
    public int compareTo(@NotNull NotificationTemplate o) {
        return 0;
    }
}
