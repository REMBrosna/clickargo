package com.guudint.clickargo.clictruck.master.dto;


import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomVolume;
import com.vcc.camelone.common.COAbstractEntity;
import com.vcc.camelone.common.dto.AbstractDTO;

import javax.persistence.*;
import java.util.Date;


public class CkCtMstUomVolume extends AbstractDTO<CkCtMstUomVolume, TCkCtMstUomVolume> {

    private static final long serialVersionUID = 1L;

    private String volId;
    private String volDesc;
    private Character volStatus;
    private Date volDtCreate;
    private String volUidCreate;
    private Date volDtLupd;
    private String volUidLupd;


    @Override
    public void init() {

    }

    public CkCtMstUomVolume() {
    }

    public CkCtMstUomVolume(String volId, String volDesc, Character volStatus, Date volDtCreate, String volUidCreate, Date volDtLupd, String volUidLupd) {
        this.volId = volId;
        this.volDesc = volDesc;
        this.volStatus = volStatus;
        this.volDtCreate = volDtCreate;
        this.volUidCreate = volUidCreate;
        this.volDtLupd = volDtLupd;
        this.volUidLupd = volUidLupd;
    }

    public CkCtMstUomVolume(TCkCtMstUomVolume entity) {
        super(entity);
    }

    public String getVolId() {
        return volId;
    }

    public void setVolId(String volId) {
        this.volId = volId;
    }

    public String getVolDesc() {
        return volDesc;
    }

    public void setVolDesc(String volDesc) {
        this.volDesc = volDesc;
    }

    public Character getVolStatus() {
        return volStatus;
    }

    public void setVolStatus(Character volStatus) {
        this.volStatus = volStatus;
    }

    public Date getVolDtCreate() {
        return volDtCreate;
    }

    public void setVolDtCreate(Date volDtCreate) {
        this.volDtCreate = volDtCreate;
    }

    public String getVolUidCreate() {
        return volUidCreate;
    }

    public void setVolUidCreate(String volUidCreate) {
        this.volUidCreate = volUidCreate;
    }

    public Date getVolDtLupd() {
        return volDtLupd;
    }

    public void setVolDtLupd(Date volDtLupd) {
        this.volDtLupd = volDtLupd;
    }

    public String getVolUidLupd() {
        return volUidLupd;
    }

    public void setVolUidLupd(String volUidLupd) {
        this.volUidLupd = volUidLupd;
    }

    @Override
    public int compareTo(CkCtMstUomVolume o) {
        return 0;
    }
}
