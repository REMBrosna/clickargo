package com.guudint.clickargo.clictruck.common.dto;
import com.vcc.camelone.common.dto.AbstractDTO;

import java.math.BigDecimal;
import java.util.Date;

public class CkCtRentalApp extends AbstractDTO<CkCtRentalApp, CkCtRentalApp> {

	private String vrId;
	private String provider;
	private String truck;
	private Short lease;
	private BigDecimal price;
	private Short quantity;
	private String accn;
	private String name;
	private String contact;
	private String email;
	private String company;
	private Character vrStatus;
	private Date vrDtCreate;
	private String vrUidCreate;
	private Date vrDtLupd;
	private String vrUidLupd;
	public String history;

	public CkCtRentalApp() {
	}

	public CkCtRentalApp(CkCtRentalApp entity) {
		super(entity);
	}

	public CkCtRentalApp(String vrId, String provider, String truck, Short lease, BigDecimal price, Short quantity, String accn, String name, String contact, String email, String company, Character vrStatus, Date vrDtCreate, String vrUidCreate, Date vrDtLupd, String vrUidLupd, String history) {
		this.vrId = vrId;
		this.provider = provider;
		this.truck = truck;
		this.lease = lease;
		this.price = price;
		this.quantity = quantity;
		this.accn = accn;
		this.name = name;
		this.contact = contact;
		this.email = email;
		this.company = company;
		this.vrStatus = vrStatus;
		this.vrDtCreate = vrDtCreate;
		this.vrUidCreate = vrUidCreate;
		this.vrDtLupd = vrDtLupd;
		this.vrUidLupd = vrUidLupd;
		this.history = history;
	}

	public String getVrId() {
		return vrId;
	}

	public void setVrId(String vrId) {
		this.vrId = vrId;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getTruck() {
		return truck;
	}

	public void setTruck(String truck) {
		this.truck = truck;
	}

	public Short getLease() {
		return lease;
	}

	public void setLease(Short lease) {
		this.lease = lease;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Short getQuantity() {
		return quantity;
	}

	public void setQuantity(Short quantity) {
		this.quantity = quantity;
	}

	public String getAccn() {
		return accn;
	}

	public void setAccn(String accn) {
		this.accn = accn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Date getVrDtCreate() {
		return vrDtCreate;
	}

	public void setVrDtCreate(Date vrDtCreate) {
		this.vrDtCreate = vrDtCreate;
	}

	public String getVrUidCreate() {
		return vrUidCreate;
	}

	public void setVrUidCreate(String vrUidCreate) {
		this.vrUidCreate = vrUidCreate;
	}

	public Date getVrDtLupd() {
		return vrDtLupd;
	}

	public void setVrDtLupd(Date vrDtLupd) {
		this.vrDtLupd = vrDtLupd;
	}

	public String getVrUidLupd() {
		return vrUidLupd;
	}

	public void setVrUidLupd(String vrUidLupd) {
		this.vrUidLupd = vrUidLupd;
	}

	public Character getVrStatus() {
		return vrStatus;
	}

	public void setVrStatus(Character vrStatus) {
		this.vrStatus = vrStatus;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	@Override
	public void init() {

	}

	@Override
	public int compareTo(CkCtRentalApp o) {
		return 0;
	}
}
