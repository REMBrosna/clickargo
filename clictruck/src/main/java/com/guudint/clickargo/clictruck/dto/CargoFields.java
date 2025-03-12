package com.guudint.clickargo.clictruck.dto;

public class CargoFields {

	private int seq;
	private String cargoType;
	private String size;
	private String weight;
	private String volWeight;
	private String truckType;
	private String status;
	
	public CargoFields() {

	}

	public CargoFields(int seq, String cargoType, String size, String weight, String volWeight, String truckType,
			String status) {
		super();
		this.seq = seq;
		this.cargoType = cargoType;
		this.size = size;
		this.weight = weight;
		this.volWeight = volWeight;
		this.truckType = truckType;
		this.status = status;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getCargoType() {
		return cargoType;
	}
	
	public void setCargoType(String cargoType) {
		this.cargoType = cargoType;
	}
	
	public String getSize() {
		return size;
	}
	
	public void setSize(String size) {
		this.size = size;
	}
	
	public String getWeight() {
		return weight;
	}
	
	public void setWeight(String weight) {
		this.weight = weight;
	}
	
	public String getVolWeight() {
		return volWeight;
	}
	
	public void setVolWeight(String volWeight) {
		this.volWeight = volWeight;
	}
	
	public String getTruckType() {
		return truckType;
	}
	
	public void setTruckType(String truckType) {
		this.truckType = truckType;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
}
