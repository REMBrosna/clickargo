package com.guudint.clickargo.clictruck.master.model;


import com.vcc.camelone.common.COAbstractEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "T_CK_CT_MST_UOM_SIZE")
public class TCkCtMstUomSize extends COAbstractEntity<TCkCtMstUomSize> {

    private static final long serialVersionUID = 1L;

    private String sizId;
    private String sizName;
    private String sizDesc;
    private Character sizStatus;
    private Date sizDtCreate;
    private String sizUidCreate;
    private Date sizDtLupd;
    private String sizUidLupd;


    public TCkCtMstUomSize() {
    }

    public TCkCtMstUomSize(String sizId, String sizDesc, String sizName, Character sizStatus, Date sizDtCreate,
                           String sizUidCreate, Date sizDtLupd, String sizUidLupd) {
        this.sizId = sizId;
        this.sizName = sizName;
        this.sizDesc = sizDesc;
        this.sizStatus = sizStatus;
        this.sizDtCreate = sizDtCreate;
        this.sizUidCreate = sizUidCreate;
        this.sizDtLupd = sizDtLupd;
        this.sizUidLupd = sizUidLupd;
    }
    @Id
    @Column(name = "SIZ_ID", nullable = false, length = 35)
    public String getSizId() {
        return sizId;
    }

    public void setSizId(String sizId) {
        this.sizId = sizId;
    }
    @Column(name = "SIZ_NAME", nullable = false, length = 35)
    public String getSizName() {
        return sizName;
    }

    public void setSizName(String sizName) {
        this.sizName = sizName;
    }

    @Column(name = "SIZ_DESC", nullable = false, length = 35)
    public String getSizDesc() {
        return sizDesc;
    }

    public void setSizDesc(String sizDesc) {
        this.sizDesc = sizDesc;
    }
    @Column(name = "SIZ_STATUS", columnDefinition = "CHAR(1) DEFAULT 'A'")
    public Character getSizStatus() {
        return sizStatus;
    }

    public void setSizStatus(Character sizStatus) {
        this.sizStatus = sizStatus;
    }

    @Column(name = "SIZ_DT_CREATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSizDtCreate() {
        return sizDtCreate;
    }

    public void setSizDtCreate(Date sizDtCreate) {
        this.sizDtCreate = sizDtCreate;
    }
    @Column(name = "SIZ_UID_CREATE", length = 35, updatable = false)
    public String getSizUidCreate() {
        return sizUidCreate;
    }

    public void setSizUidCreate(String sizUidCreate) {
        this.sizUidCreate = sizUidCreate;
    }
    @Column(name = "SIZ_DT_LUPD")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSizDtLupd() {
        return sizDtLupd;
    }

    public void setSizDtLupd(Date sizDtLupd) {
        this.sizDtLupd = sizDtLupd;
    }
    @Column(name = "SIZ_UID_LUPD", length = 35)
    public String getSizUidLupd() {
        return sizUidLupd;
    }

    public void setSizUidLupd(String sizUidLupd) {
        this.sizUidLupd = sizUidLupd;
    }

    @Override
    public void init() {

    }

    @Override
    public int compareTo(TCkCtMstUomSize o) {
        return 0;
    }


}
