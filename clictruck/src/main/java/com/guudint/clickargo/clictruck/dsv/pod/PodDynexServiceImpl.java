package com.guudint.clickargo.clictruck.dsv.pod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.dsv.service.IPodService;
import com.guudint.clickargo.clictruck.dsv.service.impl.DsvShipmentService;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripService;

import net.sf.jasperreports.engine.JRException;

@Service("podDynexService")
public class PodDynexServiceImpl implements IPodService {
	
	@Autowired
	CkJobTruckExtDao ckJobTruckExtDao;
	@Autowired
	DsvShipmentService auxiliary;
	@Autowired
	CkCtTripService ckCtTripService;

	@Override
	public String generateShipmentReport(String jobId) throws JRException {
		return null;
	}

	@Override
	public String generatePodFileName(TCkJobTruck jobTruck, TCkCtTrip trip) throws Exception {

		return null;
	}

}
