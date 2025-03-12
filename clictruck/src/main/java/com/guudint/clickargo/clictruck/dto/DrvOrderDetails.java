package com.guudint.clickargo.clictruck.dto;

import java.util.ArrayList;
import java.util.List;

public class DrvOrderDetails {

	private GeneralFields generalFields;
	private LocationFields locationFields;
	private List<CargoFields> cargoFieldsList;
	
	public DrvOrderDetails() {
		
	}

	public DrvOrderDetails(GeneralFields generalFields, LocationFields locationFields,
			CargoFields cargoFields, List<CargoFields> cargoFieldsList) {
		super();
		this.generalFields = generalFields;
		this.locationFields = locationFields;
		this.cargoFieldsList = cargoFieldsList;
	}

	public GeneralFields getGeneralFields() {
		return generalFields;
	}
	
	public void setGeneralFields(GeneralFields generalFields) {
		this.generalFields = generalFields;
	}
	
	public LocationFields getLocationFields() {
		return locationFields;
	}
	
	public void setLocationFields(LocationFields locationFields) {
		this.locationFields = locationFields;
	}

	public List<CargoFields> getCargoFieldsList() {
		if (cargoFieldsList == null) {
			cargoFieldsList = new ArrayList<>();
		}
		return cargoFieldsList;
	}

	public void setCargoFieldsList(List<CargoFields> cargoFieldsList) {
		this.cargoFieldsList = cargoFieldsList;
	}

}
