package com.guudint.clickargo.clictruck.master.dto;


import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomSize;
import com.vcc.camelone.common.dto.AbstractDTO;

import java.util.Date;

public class CkCtMstUomSize extends AbstractDTO<CkCtMstUomSize, TCkCtMstUomSize> {

    private static final long serialVersionUID = 1L;

    private String sizId;
    private String sizDesc;
    private Character sizStatus;
    private Date sizDtCreate;
    private String sizUidCreate;
    private Date sizDtLupd;
    private String sizUidLupd;

    public CkCtMstUomSize() {
    }

    public CkCtMstUomSize(TCkCtMstUomSize entity, String sizId, String sizDesc, Character sizStatus, Date sizDtCreate, String sizUidCreate, Date sizDtLupd, String sizUidLupd) {
        super(entity);
        this.sizId = sizId;
        this.sizDesc = sizDesc;
        this.sizStatus = sizStatus;
        this.sizDtCreate = sizDtCreate;
        this.sizUidCreate = sizUidCreate;
        this.sizDtLupd = sizDtLupd;
        this.sizUidLupd = sizUidLupd;
    }

    public CkCtMstUomSize(TCkCtMstUomSize entity) {
        super(entity);
    }

    public String getSizId() {
        return sizId;
    }

    public void setSizId(String sizId) {
        this.sizId = sizId;
    }

    public String getSizDesc() {
        return sizDesc;
    }

    public void setSizDesc(String sizDesc) {
        this.sizDesc = sizDesc;
    }

    public Character getSizStatus() {
        return sizStatus;
    }

    public void setSizStatus(Character sizStatus) {
        this.sizStatus = sizStatus;
    }

    public Date getSizDtCreate() {
        return sizDtCreate;
    }

    public void setSizDtCreate(Date sizDtCreate) {
        this.sizDtCreate = sizDtCreate;
    }

    public String getSizUidCreate() {
        return sizUidCreate;
    }

    public void setSizUidCreate(String sizUidCreate) {
        this.sizUidCreate = sizUidCreate;
    }

    public Date getSizDtLupd() {
        return sizDtLupd;
    }

    public void setSizDtLupd(Date sizDtLupd) {
        this.sizDtLupd = sizDtLupd;
    }

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
    public int compareTo(CkCtMstUomSize o) {
        return 0;
    }
}
