package com.guudint.clickargo.clictruck.notification.dto;

import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

import java.util.Date;

public class CkCtAlert extends AbstractDTO<CkCtAlert, TCkCtAlert> {
    private String altId;
    private CoreAccn coreAccn;
    private CkCtMstAlert ckCtMstAlert;
    private String altConditionType;
    private String altConditionValue;
    private Date altConditionDt;
    private String altReferId;
    private String altRepCon;
    private String altRemarks;
    private Character altStatus;
    private Date altDtCreate;
    private String altUidCreate;
    private Date altDtLupd;
    private String altUidLupd;
    public CkCtAlert() {

    }

    public CkCtAlert(TCkCtAlert entity, String altId, CoreAccn coreAccn, CkCtMstAlert ckCtMstAlert, String altConditionType, String altConditionValue, Date altConditionDt, String altReferId, String altRepCon, String altRemarks, Character altStatus, Date altDtCreate, String altUidCreate, Date altDtLupd, String altUidLupd) {
        super(entity);
        this.altId = altId;
        this.coreAccn = coreAccn;
        this.ckCtMstAlert = ckCtMstAlert;
        this.altConditionType = altConditionType;
        this.altConditionValue = altConditionValue;
        this.altConditionDt = altConditionDt;
        this.altReferId = altReferId;
        this.altRepCon = altRepCon;
        this.altRemarks = altRemarks;
        this.altStatus = altStatus;
        this.altDtCreate = altDtCreate;
        this.altUidCreate = altUidCreate;
        this.altDtLupd = altDtLupd;
        this.altUidLupd = altUidLupd;
    }

    public String getAltId() {
        return altId;
    }

    public void setAltId(String altId) {
        this.altId = altId;
    }

    public CoreAccn getCoreAccn() {
        return coreAccn;
    }

    public void setCoreAccn(CoreAccn coreAccn) {
        this.coreAccn = coreAccn;
    }

    public CkCtMstAlert getCkCtMstAlert() {
        return ckCtMstAlert;
    }

    public void setCkCtMstAlert(CkCtMstAlert ckCtMstAlert) {
        this.ckCtMstAlert = ckCtMstAlert;
    }

    public String getAltConditionType() {
        return altConditionType;
    }

    public void setAltConditionType(String altConditionType) {
        this.altConditionType = altConditionType;
    }

    public String getAltConditionValue() {
        return altConditionValue;
    }

    public void setAltConditionValue(String altConditionValue) {
        this.altConditionValue = altConditionValue;
    }

    public Date getAltConditionDt() {
        return altConditionDt;
    }

    public void setAltConditionDt(Date altConditionDt) {
        this.altConditionDt = altConditionDt;
    }
    public String getAltReferId() {
        return altReferId;
    }

    public void setAltReferId(String altReferId) {
        this.altReferId = altReferId;
    }

    public String getAltRepCon() {
        return altRepCon;
    }

    public void setAltRepCon(String altRepCon) {
        this.altRepCon = altRepCon;
    }

    public String getAltRemarks() {
        return altRemarks;
    }

    public void setAltRemarks(String altRemarks) {
        this.altRemarks = altRemarks;
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
    public int compareTo(CkCtAlert o) {
        return 0;
    }

}
