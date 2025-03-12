package com.guudint.clicdo.common;

import java.io.Serializable;
import java.util.Map;

public class DashboardJson implements Serializable {

	private static final long serialVersionUID = 5257932571494019609L;

	private int id;
	private String dbType;
	private String title;
	private Map<String, Integer> transStatistic;
	private String accnType;
	private String image;

	public DashboardJson() {
	}

	public DashboardJson(String dbType, String title) {
		this.dbType = dbType;
		this.title = title;
	}

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

	public Map<String, Integer> getTransStatistic() {
		return transStatistic;
	}

	public void setTransStatistic(Map<String, Integer> transStatistic) {
		this.transStatistic = transStatistic;
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
