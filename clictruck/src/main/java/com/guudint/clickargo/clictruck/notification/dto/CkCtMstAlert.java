package com.guudint.clickargo.clictruck.notification.dto;

import com.guudint.clickargo.clictruck.notification.model.TCkCtMstAlert;
import com.vcc.camelone.common.dto.AbstractDTO;

import java.util.Date;

public class CkCtMstAlert extends AbstractDTO<CkCtMstAlert, TCkCtMstAlert> {
    private String altId;
    private String altName;
    private String altModule;
    private String altNotificationType;
    private String altTemplateId;
    private String altConditionType;
    private Character altStatus;
    private Date altDtCreate;
    private String altUidCreate;
    private Date altDtLupd;
    private String altUidLupd;
    public CkCtMstAlert() {
    }

    public CkCtMstAlert(TCkCtMstAlert entity, String altId, String altName, String altModule, String altNotificationType, String altTemplateId, String altConditionType, Character altStatus, Date altDtCreate, String altUidCreate, Date altDtLupd, String altUidLupd) {
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

    public CkCtMstAlert(TCkCtMstAlert entity) {
    }

    public String getAltId() {
        return altId;
    }

    public void setAltId(String altId) {
        this.altId = altId;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    public String getAltModule() {
        return altModule;
    }

    public void setAltModule(String altModule) {
        this.altModule = altModule;
    }

    public String getAltNotificationType() {
        return altNotificationType;
    }

    public void setAltNotificationType(String altNotificationType) {
        this.altNotificationType = altNotificationType;
    }

    public String getAltTemplateId() {
        return altTemplateId;
    }

    public void setAltTemplateId(String altTemplateId) {
        this.altTemplateId = altTemplateId;
    }

    public String getAltConditionType() {
        return altConditionType;
    }

    public void setAltConditionType(String altConditionType) {
        this.altConditionType = altConditionType;
    }

    public Character getAltStatus() {
        return altStatus;
    }

    public void setAltStatus(Character altStatus) {
        this.altStatus = altStatus;
    }

    public Date getAltDtCreate() {
        return altDtCreate;
    }

    public void setAltDtCreate(Date altDtCreate) {
        this.altDtCreate = altDtCreate;
    }

    public String getAltUidCreate() {
        return altUidCreate;
    }

    public void setAltUidCreate(String altUidCreate) {
        this.altUidCreate = altUidCreate;
    }

    public Date getAltDtLupd() {
        return altDtLupd;
    }

    public void setAltDtLupd(Date altDtLupd) {
        this.altDtLupd = altDtLupd;
    }

    public String getAltUidLupd() {
        return altUidLupd;
    }

    public void setAltUidLupd(String altUidLupd) {
        this.altUidLupd = altUidLupd;
    }

    @Override
    public void init() {

    }

    @Override
    public int compareTo(CkCtMstAlert o) {
        return 0;
    }
    
}
