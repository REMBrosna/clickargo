package com.guudint.clickargo.clictruck.dsv.service;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;

public interface IPodService {
	
	public String generateShipmentReport(String jobId) throws Exception;
	
	public String generatePodFileName(TCkJobTruck jobTruck, TCkCtTrip trip) throws Exception;

}
