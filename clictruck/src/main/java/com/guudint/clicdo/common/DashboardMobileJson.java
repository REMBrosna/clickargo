package com.guudint.clicdo.common;

import java.io.Serializable;

public class DashboardMobileJson implements Serializable {

	private static final long serialVersionUID = 5257932571494019609L;

	private int id;
	private String dbType;
	private String title;
	private Integer count;
	private String accnType;
	private String image;

	// Constructors
	///////////////
	public DashboardMobileJson() {
	}

	public DashboardMobileJson(String dbType, String title) {
		this.dbType = dbType;
		this.title = title;
	}

	// Override
	/////////////
	@Override
	public String toString() {
		return "DashboardMobileJson [id=" + id + ", dbType=" + dbType + ", title=" + title + ", count=" + count
				+ ", accnType=" + accnType + ", image=" + image + "]";
	}

	// Properties
	/////////////
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getAccnType() {
		return accnType;
	}

	public void setAccnType(String accnType) {
		this.accnType = accnType;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
