package com.guudint.clickargo.clictruck.apigateway.model;

import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.COAbstractEntity;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "T_CK_CT_VENDOR_COMPANY")
public class TCkCtVendorCompany extends COAbstractEntity<TCkCtVendorCompany> {

    private String venId;
    private TCoreAccn venVendorAccn;
    private TCoreAccn venCompanyAccn;
    private Character venStatus;
    private Date venDtCreate;
    private String venUidCreate;
    private Date venDtLupd;
    private String venUidLupd;


    @Override
    public void init() {

    }

    public TCkCtVendorCompany() {}

    public TCkCtVendorCompany(String venId, TCoreAccn venVendorAccn, TCoreAccn venCompanyAccn, Character venStatus, Date venDtCreate, String venUidCreate, Date venDtLupd, String venUidLupd) {
        this.venId = venId;
        this.venVendorAccn = venVendorAccn;
        this.venCompanyAccn = venCompanyAccn;
        this.venStatus = venStatus;
        this.venDtCreate = venDtCreate;
        this.venUidCreate = venUidCreate;
        this.venDtLupd = venDtLupd;
        this.venUidLupd = venUidLupd;
    }

    @Id
    @Column(name = "VEN_ID", nullable = false, length = 35)
    public String getVenId() {
        return venId;
    }

    public void setVenId(String venId) {
        this.venId = venId;
    }
    @ManyToOne
    @JoinColumn(name = "VEN_VENDOR_ACCN", nullable = false, referencedColumnName = "ACCN_ID")
    public TCoreAccn getVenVendorAccn() {
        return venVendorAccn;
    }

    public void setVenVendorAccn(TCoreAccn venVendorAccn) {
        this.venVendorAccn = venVendorAccn;
    }
    @ManyToOne
    @JoinColumn(name = "VEN_COMPANY_ACCN", nullable = false, referencedColumnName = "ACCN_ID")
    public TCoreAccn getVenCompanyAccn() {
        return venCompanyAccn;
    }

    public void setVenCompanyAccn(TCoreAccn venCompanyAccn) {
        this.venCompanyAccn = venCompanyAccn;
    }
    @Column(name = "VEN_STATUS", columnDefinition = "CHAR(1) DEFAULT 'A'")
    public Character getVenStatus() {
        return venStatus;
    }

    public void setVenStatus(Character venStatus) {
        this.venStatus = venStatus;
    }
    @Column(name = "VEN_DT_CREATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getVenDtCreate() {
        return venDtCreate;
    }

    public void setVenDtCreate(Date venDtCreate) {
        this.venDtCreate = venDtCreate;
    }
    @Column(name = "VEN_UID_CREATE", length = 35, updatable = false)
    public String getVenUidCreate() {
        return venUidCreate;
    }

    public void setVenUidCreate(String venUidCreate) {
        this.venUidCreate = venUidCreate;
    }
    @Column(name = "VEN_DT_LUPD")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getVenDtLupd() {
        return venDtLupd;
    }

    public void setVenDtLupd(Date venDtLupd) {
        this.venDtLupd = venDtLupd;
    }
    @Column(name = "VEN_UID_LUPD", length = 35)
    public String getVenUidLupd() {
        return venUidLupd;
    }

    public void setVenUidLupd(String venUidLupd) {
        this.venUidLupd = venUidLupd;
    }

    @Override
    public int compareTo(TCkCtVendorCompany o) {
        return 0;
    }
}
