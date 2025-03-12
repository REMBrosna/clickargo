package com.guudint.clickargo.clictruck.notification.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.guudint.clickargo.clictruck.notification.dto.CkCtAlert;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "T_CK_CT_ALERT")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class TCkCtAlert extends AbstractDTO<TCkCtAlert, CkCtAlert> {

    // Static Attributes
    private static final long serialVersionUID = 7596532948027037380L;

    // Attributes
    private String altId;
    private TCoreAccn tCoreAccn;
    private TCkCtMstAlert tCkCtMstAlert;
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

    @Override
    public void init() {

    }

    // Default constructor
    public TCkCtAlert() {
        super();
    }

    // Constructor with entity
    public TCkCtAlert(CkCtAlert entity) {
        super(entity);
    }

    // Constructor with all fields

    public TCkCtAlert(CkCtAlert entity, String altId, TCoreAccn tCoreAccn, TCkCtMstAlert tCkCtMstAlert, String altConditionType, String altConditionValue, Date altConditionDt, String altReferId, String altRepCon, String altRemarks, Character altStatus, Date altDtCreate, String altUidCreate, Date altDtLupd, String altUidLupd) {
        super(entity);
        this.altId = altId;
        this.tCoreAccn = tCoreAccn;
        this.tCkCtMstAlert = tCkCtMstAlert;
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

    @Override
    public String toString() {
        return "TCkCtAlert{" +
                "altId='" + altId + '\'' +
                ", tCoreAccn=" + tCoreAccn +
                ", tCkCtMstAlert=" + tCkCtMstAlert +
                ", altConditionType='" + altConditionType + '\'' +
                ", altConditionValue='" + altConditionValue + '\'' +
                ", altConditionDt=" + altConditionDt +
                ", altReferId='" + altReferId + '\'' +
                ", altRepCon='" + altRepCon + '\'' +
                ", altRemarks='" + altRemarks + '\'' +
                ", altStatus=" + altStatus +
                ", altDtCreate=" + altDtCreate +
                ", altUidCreate='" + altUidCreate + '\'' +
                ", altDtLupd=" + altDtLupd +
                ", altUidLupd='" + altUidLupd + '\'' +
                '}';
    }

    @Id
    @Column(name = "ALT_ID", unique = true, nullable = false, length = 36)
    public String getAltId() {
        return altId;
    }

    public void setAltId(String altId) {
        this.altId = altId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ALT_ACCN_ID", nullable = false)
    public TCoreAccn gettCoreAccn() {
        return tCoreAccn;
    }

    public void settCoreAccn(TCoreAccn tCoreAccn) {
        this.tCoreAccn = tCoreAccn;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ALT_ALERT_MASTER_TABLE_REF_ID", nullable = false)
    public TCkCtMstAlert gettCkCtMstAlert() {
        return tCkCtMstAlert;
    }

    public void settCkCtMstAlert(TCkCtMstAlert tCkCtMstAlert) {
        this.tCkCtMstAlert = tCkCtMstAlert;
    }

    @Column(name = "ALT_CONDITION_TYPE", length = 20)
    public String getAltConditionType() {
        return altConditionType;
    }

    public void setAltConditionType(String altConditionType) {
        this.altConditionType = altConditionType;
    }

    @Column(name = "ALT_CONDITION_VALUE")
    public String getAltConditionValue() {
        return altConditionValue;
    }

    public void setAltConditionValue(String altConditionValue) {
        this.altConditionValue = altConditionValue;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ALT_CONDITION_DATE")
    public Date getAltConditionDt() {
        return altConditionDt;
    }

    public void setAltConditionDt(Date altConditionDt) {
        this.altConditionDt = altConditionDt;
    }

    @Column(name = "ALT_REFERENCE_ID", length = 35)
    public String getAltReferId() {
        return altReferId;
    }

    public void setAltReferId(String altReferId) {
        this.altReferId = altReferId;
    }

    @Column(name = "ALT_RECIPIENT_CONTACT", length = 100)
    public String getAltRepCon() {
        return altRepCon;
    }

    public void setAltRepCon(String altRepCon) {
        this.altRepCon = altRepCon;
    }

    @Column(name = "ALT_REMARKS")
    public String getAltRemarks() {
        return altRemarks;
    }

    public void setAltRemarks(String altRemarks) {
        this.altRemarks = altRemarks;
    }

    @Column(name = "ALT_STATUS", length = 1)
    public Character getAltStatus() {
        return altStatus;
    }

    public void setAltStatus(Character altStatus) {
        this.altStatus = altStatus;
    }

    @Column(name = "ALT_DT_CREATE")
    @Temporal(TemporalType.TIMESTAMP)
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

    @Column(name = "ALT_DT_LUPD")
    @Temporal(TemporalType.TIMESTAMP)
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

    @Override
    public int compareTo(TCkCtAlert o) {
        return 0;
    }
}
