package com.guudint.clickargo.clictruck.master.dto;


import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomWeight;
import com.vcc.camelone.common.dto.AbstractDTO;

import javax.persistence.*;
import java.util.Date;


public class CkCtMstUomWeight extends AbstractDTO<CkCtMstUomWeight, TCkCtMstUomWeight> {

    private static final long serialVersionUID = 1L;

    private String weiId;
    private String weiDesc;
    private Character weiStatus;
    private Date weiDtCreate;
    private String weiUidCreate;
    private Date weiDtLupd;
    private String weiUidLupd;


    @Override
    public void init() {

    }

    public CkCtMstUomWeight() {
    }

    public CkCtMstUomWeight(String weiId, String weiDesc, Character weiStatus, Date weiDtCreate, String weiUidCreate, Date weiDtLupd, String weiUidLupd) {
        this.weiId = weiId;
        this.weiDesc = weiDesc;
        this.weiStatus = weiStatus;
        this.weiDtCreate = weiDtCreate;
        this.weiUidCreate = weiUidCreate;
        this.weiDtLupd = weiDtLupd;
        this.weiUidLupd = weiUidLupd;
    }

    public CkCtMstUomWeight(TCkCtMstUomWeight entity) {
        super(entity);
    }

    public String getWeiId() {
        return weiId;
    }

    public void setWeiId(String weiId) {
        this.weiId = weiId;
    }

    public String getWeiDesc() {
        return weiDesc;
    }

    public void setWeiDesc(String weiDesc) {
        this.weiDesc = weiDesc;
    }

    public Character getWeiStatus() {
        return weiStatus;
    }

    public void setWeiStatus(Character weiStatus) {
        this.weiStatus = weiStatus;
    }

    public Date getWeiDtCreate() {
        return weiDtCreate;
    }

    public void setWeiDtCreate(Date weiDtCreate) {
        this.weiDtCreate = weiDtCreate;
    }

    public String getWeiUidCreate() {
        return weiUidCreate;
    }

    public void setWeiUidCreate(String weiUidCreate) {
        this.weiUidCreate = weiUidCreate;
    }

    public Date getWeiDtLupd() {
        return weiDtLupd;
    }

    public void setWeiDtLupd(Date weiDtLupd) {
        this.weiDtLupd = weiDtLupd;
    }

    public String getWeiUidLupd() {
        return weiUidLupd;
    }

    public void setWeiUidLupd(String weiUidLupd) {
        this.weiUidLupd = weiUidLupd;
    }

    @Override
    public int compareTo(CkCtMstUomWeight o) {
        return 0;
    }
}
