package com.guudint.clickargo.clictruck.master.model;


import com.vcc.camelone.common.COAbstractEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "T_CK_CT_MST_UOM_VOLUME")
public class TCkCtMstUomVolume extends COAbstractEntity<TCkCtMstUomVolume> {

    private static final long serialVersionUID = 1L;

    private String volId;
    private String volDesc;
    private String volName;
    private Character volStatus;
    private Date volDtCreate;
    private String volUidCreate;
    private Date volDtLupd;
    private String volUidLupd;


    public TCkCtMstUomVolume() {
    }

    public TCkCtMstUomVolume(String volId, String volName, String volDesc, Character volStatus, Date volDtCreate, String volUidCreate, Date volDtLupd, String volUidLupd) {
        this.volId = volId;
        this.volDesc = volDesc;
        this.volName = volName;
        this.volStatus = volStatus;
        this.volDtCreate = volDtCreate;
        this.volUidCreate = volUidCreate;
        this.volDtLupd = volDtLupd;
        this.volUidLupd = volUidLupd;
    }

    @Id
    @Column(name = "VOL_ID", nullable = false, length = 35)
    public String getVolId() {
        return volId;
    }

    public void setVolId(String volId) {
        this.volId = volId;
    }
    @Column(name = "VOL_NAME", nullable = false, length = 255)
    public String getVolName() {
        return volName;
    }

    public void setVolName(String volName) {
        this.volName = volName;
    }

    @Column(name = "VOL_DESC", nullable = false, length = 35)
    public String getVolDesc() {
        return volDesc;
    }

    public void setVolDesc(String volDesc) {
        this.volDesc = volDesc;
    }

    @Column(name = "VOL_STATUS", columnDefinition = "CHAR(1) DEFAULT 'A'")
    public Character getVolStatus() {
        return volStatus;
    }

    public void setVolStatus(Character volStatus) {
        this.volStatus = volStatus;
    }
    @Column(name = "VOL_DT_CREATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getVolDtCreate() {
        return volDtCreate;
    }

    public void setVolDtCreate(Date volDtCreate) {
        this.volDtCreate = volDtCreate;
    }
    @Column(name = "VOL_UID_CREATE", length = 35, updatable = false)
    public String getVolUidCreate() {
        return volUidCreate;
    }

    public void setVolUidCreate(String volUidCreate) {
        this.volUidCreate = volUidCreate;
    }
    @Column(name = "VOL_DT_LUPD")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getVolDtLupd() {
        return volDtLupd;
    }

    public void setVolDtLupd(Date volDtLupd) {
        this.volDtLupd = volDtLupd;
    }
    @Column(name = "VOL_UID_LUPD", length = 35)
    public String getVolUidLupd() {
        return volUidLupd;
    }

    public void setVolUidLupd(String volUidLupd) {
        this.volUidLupd = volUidLupd;
    }
    @Override
    public void init() {

    }

    @Override
    public int compareTo(TCkCtMstUomVolume o) {
        return 0;
    }


}
