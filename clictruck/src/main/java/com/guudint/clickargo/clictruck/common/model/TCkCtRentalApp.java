package com.guudint.clickargo.clictruck.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.COAbstractEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "T_CK_CT_RENTAL_APP")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler" })
public class TCkCtRentalApp extends COAbstractEntity<TCkCtRentalApp> {

    private static final long serialVersionUID = -3012135870942707850L;
	private String vrId;
    private TCoreAccn vrAccn;
    private String vrProvider;
    private String vrTruck;
    private Short vrLease;
    private BigDecimal vrPrice;
    private Short vrQty;
    private String vrCtName;
    private String vrCtMobile;
    private String vrCtEmail;
    private Character vrStatus;
    private Date vrDtCreate;
    private String vrUidCreate;
    private Date vrDtLupd;
    private String vrUidLupd;

    public TCkCtRentalApp() {
    }

    public TCkCtRentalApp(String vrId, TCoreAccn vrAccn, String vrProvider, String vrTruck, Short vrLease, BigDecimal vrPrice, Short vrQty, String vrCtName, String vrCtMobile, String vrCtEmail, Character vrStatus, Date vrDtCreate, String vrUidCreate, Date vrDtLupd, String vrUidLupd) {
        this.vrId = vrId;
        this.vrAccn = vrAccn;
        this.vrProvider = vrProvider;
        this.vrTruck = vrTruck;
        this.vrLease = vrLease;
        this.vrPrice = vrPrice;
        this.vrQty = vrQty;
        this.vrCtName = vrCtName;
        this.vrCtMobile = vrCtMobile;
        this.vrCtEmail = vrCtEmail;
        this.vrStatus = vrStatus;
        this.vrDtCreate = vrDtCreate;
        this.vrUidCreate = vrUidCreate;
        this.vrDtLupd = vrDtLupd;
        this.vrUidLupd = vrUidLupd;
    }

    @Id
    @Column(name = "VR_ID", unique = true, nullable = false, length = 35)
    public String getVrId() {
        return vrId;
    }

    public void setVrId(String vrId) {
        this.vrId = vrId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VR_ACCN")
    public TCoreAccn getVrAccn() {
        return vrAccn;
    }

    public void setVrAccn(TCoreAccn vrAccn) {
        this.vrAccn = vrAccn;
    }

    @Column(name = "VR_PROVIDER", length = 256)
    public String getVrProvider() {
        return vrProvider;
    }

    public void setVrProvider(String vrProvider) {
        this.vrProvider = vrProvider;
    }

    @Column(name = "VR_TRUCK", length = 256)
    public String getVrTruck() {
        return vrTruck;
    }

    public void setVrTruck(String vrTruck) {
        this.vrTruck = vrTruck;
    }

    @Column(name = "VR_LEASE")
    public Short getVrLease() {
        return vrLease;
    }

    public void setVrLease(Short vrLease) {
        this.vrLease = vrLease;
    }

    @Column(name = "VR_PRICE", precision = 15)
    public BigDecimal getVrPrice() {
        return vrPrice;
    }

    public void setVrPrice(BigDecimal vrPrice) {
        this.vrPrice = vrPrice;
    }

    @Column(name = "VR_QTY")
    public Short getVrQty() {
        return vrQty;
    }

    public void setVrQty(Short vrQty) {
        this.vrQty = vrQty;
    }

    @Column(name = "VR_CT_NAME", length = 256)
    public String getVrCtName() {
        return vrCtName;
    }

    public void setVrCtName(String vrCtName) {
        this.vrCtName = vrCtName;
    }

    @Column(name = "VR_CT_MOBILE", length = 256)
    public String getVrCtMobile() {
        return vrCtMobile;
    }

    public void setVrCtMobile(String vrCtMobile) {
        this.vrCtMobile = vrCtMobile;
    }

    @Column(name = "VR_CT_EMAIL", length = 256)
    public String getVrCtEmail() {
        return vrCtEmail;
    }

    public void setVrCtEmail(String vrCtEmail) {
        this.vrCtEmail = vrCtEmail;
    }

    @Column(name = "VR_STATUS", length = 1)
    public Character getVrStatus() {
        return vrStatus;
    }

    public void setVrStatus(Character vrStatus) {
        this.vrStatus = vrStatus;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "VR_DT_CREATE", length = 19)
    public Date getVrDtCreate() {
        return vrDtCreate;
    }

    public void setVrDtCreate(Date vrDtCreate) {
        this.vrDtCreate = vrDtCreate;
    }

    @Column(name = "VR_UID_CREATE", length = 35)
    public String getVrUidCreate() {
        return vrUidCreate;
    }

    public void setVrUidCreate(String vrUidCreate) {
        this.vrUidCreate = vrUidCreate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "VR_DT_LUPD", length = 19)
    public Date getVrDtLupd() {
        return vrDtLupd;
    }

    public void setVrDtLupd(Date vrDtLupd) {
        this.vrDtLupd = vrDtLupd;
    }

    @Column(name = "VR_UID_LUPD", length = 35)
    public String getVrUidLupd() {
        return vrUidLupd;
    }

    public void setVrUidLupd(String vrUidLupd) {
        this.vrUidLupd = vrUidLupd;
    }

    @Override
    public void init() {

    }

    @Override
    public int compareTo(TCkCtRentalApp o) {
        return 0;
    }
}
