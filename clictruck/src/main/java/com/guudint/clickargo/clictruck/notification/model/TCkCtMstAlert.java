package com.guudint.clickargo.clictruck.notification.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.guudint.clickargo.clictruck.notification.dto.CkCtMstAlert;
import com.vcc.camelone.common.dto.AbstractDTO;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "T_CK_CT_MST_ALERT")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class TCkCtMstAlert extends AbstractDTO<TCkCtMstAlert, CkCtMstAlert> {

    // Static Attributes
    private static final long serialVersionUID = 7596532948027037380L;

    // Attributes
    private String altId;
    private String altName;
    private String altModule;
    private String altNotificationType;
    private String altTemplateId; // Changed from Character to String
    private String altConditionType;
    private Character altStatus;
    private Date altDtCreate;
    private String altUidCreate;
    private Date altDtLupd;
    private String altUidLupd;

    // Constructors
    public TCkCtMstAlert() {
    }

    public TCkCtMstAlert(CkCtMstAlert entity, String altId, String altName, String altModule, String altNotificationType, String altTemplateId, String altConditionType, Character altStatus, Date altDtCreate, String altUidCreate, Date altDtLupd, String altUidLupd) {
        super(entity);
        this.altId = altId;
        this.altName = altName;
        this.altModule = altModule;
        this.altNotificationType = altNotificationType;
        this.altTemplateId = altTemplateId;
        this.altConditionType = altConditionType;
        this.altStatus = altStatus;
        this.altDtCreate = altDtCreate;
        this.altUidCreate = altUidCreate;
        this.altDtLupd = altDtLupd;
        this.altUidLupd = altUidLupd;
    }

    // Getters and Setters
    @Id
    @Column(name = "ALT_ID", unique = true, nullable = false, length = 35)
    public String getAltId() {
        return altId;
    }

    public void setAltId(String altId) {
        this.altId = altId;
    }

    @Column(name = "ALT_NAME", length = 100)
    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    @Column(name = "ALT_MODULE", length = 100)
    public String getAltModule() {
        return altModule;
    }

    public void setAltModule(String altModule) {
        this.altModule = altModule;
    }

    @Column(name = "ALT_NOTIFICATION_TYPE", length = 100)
    public String getAltNotificationType() {
        return altNotificationType;
    }

    public void setAltNotificationType(String altNotificationType) {
        this.altNotificationType = altNotificationType;
    }

    @Column(name = "ALT_TEMPLATE_ID", length = 100) // Updated to String with a specified length
    public String getAltTemplateId() {
        return altTemplateId;
    }

    public void setAltTemplateId(String altTemplateId) {
        this.altTemplateId = altTemplateId;
    }
    @Column(name = "ALT_CONDITION_TYPE")
    public String getAltConditionType() {
        return altConditionType;
    }

    public void setAltConditionType(String altConditionType) {
        this.altConditionType = altConditionType;
    }
    @Column(name = "ALT_STATUS", length = 1)
    public Character getAltStatus() {
        return altStatus;
    }

    public void setAltStatus(Character altStatus) {
        this.altStatus = altStatus;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ALT_DT_CREATE")
    public Date getAltDtCreate() {
        return altDtCreate;
    }

    public void setAltDtCreate(Date altDtCreate) {
        this.altDtCreate = altDtCreate;
    }

    @Column(name = "ALT_UID_CREATE", length = 35)
    public String getAltUidCreate() {
        return altUidCreate;
    }

    public void setAltUidCreate(String altUidCreate) {
        this.altUidCreate = altUidCreate;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ALT_DT_LUPD")
    public Date getAltDtLupd() {
        return altDtLupd;
    }

    public void setAltDtLupd(Date altDtLupd) {
        this.altDtLupd = altDtLupd;
    }

    @Column(name = "ALT_UID_LUPD", length = 35)
    public String getAltUidLupd() {
        return altUidLupd;
    }

    public void setAltUidLupd(String altUidLupd) {
        this.altUidLupd = altUidLupd;
    }

    // Override Methods
    @Override
    public int compareTo(TCkCtMstAlert o) {
        // Implement comparison logic if required
        return 0;
    }

    @Override
    public void init() {
        // Implement initialization logic if required
    }
}
