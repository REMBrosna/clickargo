package com.guudint.clickargo.clictruck.master.model;


import com.vcc.camelone.common.COAbstractEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "T_CK_CT_MST_UOM_WEIGHT")
public class TCkCtMstUomWeight extends COAbstractEntity<TCkCtMstUomWeight> {

    private static final long serialVersionUID = 1L;

    private String weiId;
    private String weiName;
    private String weiDesc;
    private Character weiStatus;
    private Date weiDtCreate;
    private String weiUidCreate;
    private Date weiDtLupd;
    private String weiUidLupd;


    public TCkCtMstUomWeight() {
    }

    public TCkCtMstUomWeight(String weiId, String weiName, String weiDesc, Character weiStatus, Date weiDtCreate, String weiUidCreate, Date weiDtLupd, String weiUidLupd) {
        this.weiId = weiId;
        this.weiDesc = weiDesc;
        this.weiName = weiName;
        this.weiStatus = weiStatus;
        this.weiDtCreate = weiDtCreate;
        this.weiUidCreate = weiUidCreate;
        this.weiDtLupd = weiDtLupd;
        this.weiUidLupd = weiUidLupd;
    }
    @Id
    @Column(name = "WEI_ID", nullable = false, length = 35)
    public String getWeiId() {
        return weiId;
    }

    public void setWeiId(String weiId) {
        this.weiId = weiId;
    }

    @Column(name = "WEI_NAME", nullable = false, length = 255)
    public String getWeiName() {
        return weiName;
    }

    public void setWeiName(String weiName) {
        this.weiName = weiName;
    }
    @Column(name = "WEI_DESC", nullable = false, length = 35)
    public String getWeiDesc() {
        return weiDesc;
    }

    public void setWeiDesc(String weiDesc) {
        this.weiDesc = weiDesc;
    }
    @Column(name = "WEI_STATUS", columnDefinition = "CHAR(1) DEFAULT 'A'")
    public Character getWeiStatus() {
        return weiStatus;
    }

    public void setWeiStatus(Character weiStatus) {
        this.weiStatus = weiStatus;
    }

    @Column(name = "WEI_DT_CREATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getWeiDtCreate() {
        return weiDtCreate;
    }

    public void setWeiDtCreate(Date weiDtCreate) {
        this.weiDtCreate = weiDtCreate;
    }
    @Column(name = "WEI_UID_CREATE", length = 35, updatable = false)
    public String getWeiUidCreate() {
        return weiUidCreate;
    }

    public void setWeiUidCreate(String weiUidCreate) {
        this.weiUidCreate = weiUidCreate;
    }
    @Column(name = "WEI_DT_LUPD")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getWeiDtLupd() {
        return weiDtLupd;
    }

    public void setWeiDtLupd(Date weiDtLupd) {
        this.weiDtLupd = weiDtLupd;
    }
    @Column(name = "WEI_UID_LUPD", length = 35)
    public String getWeiUidLupd() {
        return weiUidLupd;
    }

    public void setWeiUidLupd(String weiUidLupd) {
        this.weiUidLupd = weiUidLupd;
    }

    @Override
    public void init() {

    }

    @Override
    public int compareTo(TCkCtMstUomWeight o) {
        return 0;
    }


}
