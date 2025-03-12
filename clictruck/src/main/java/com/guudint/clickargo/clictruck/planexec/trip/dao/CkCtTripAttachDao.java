package com.guudint.clickargo.clictruck.planexec.trip.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtTripAttachDao extends GenericDao<TCkCtTripAttach, String> {

	List<TCkCtTripAttach> findByTrIdAndAtyId(String trId, String atypId) throws Exception;

	List<TCkCtTripAttach> findByTrIdAndAtyIds(String trId, List<TripMobileService.TripAttachTypeEnum> atypIds) throws Exception;

	List<TCkCtTripAttach> findByAtypIdAndTrIds(String atypId, List<String> tripIds) throws Exception;

	List<TCkCtTripAttach> findByJobId(String jobId) throws Exception;

	List<TCkCtTripAttach> findByTruckJobIdAndType(String jobId, String atypId) throws Exception;
}
