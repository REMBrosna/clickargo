package com.guudint.clickargo.clictruck.planexec.trip.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtTripAttachDaoImpl extends GenericDaoImpl<TCkCtTripAttach, String> implements CkCtTripAttachDao {

	@Override
    public List<TCkCtTripAttach> findByTrIdAndAtyId(String trId, String atypId) throws Exception {
		String hql = "from TCkCtTripAttach o WHERE TCkCtTrip.trId = :trId "
				+ "AND o.TCkCtMstTripAttachType.atypId = :atypId AND o.atStatus = :atStatus";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("trId", trId);
		params.put("atypId", atypId);
		params.put("atStatus", RecordStatus.ACTIVE.getCode());
		return getByQuery(hql, params);
    }

	@Override
	public List<TCkCtTripAttach> findByTrIdAndAtyIds(String trId, List<TripMobileService.TripAttachTypeEnum> atypIds) throws Exception {

		List<String> ids = atypIds.stream()
				.map(TripMobileService.TripAttachTypeEnum::getDesc)
				.collect(Collectors.toList());

		String hql = "from TCkCtTripAttach o WHERE o.TCkCtTrip.trId = :trId "
				+ "AND o.TCkCtMstTripAttachType.atypId IN (:atypIds) "
				+ "AND o.atStatus = :atStatus";

		Map<String, Object> params = new HashMap<>();
		params.put("trId", trId);
		params.put("atypIds", ids);
		params.put("atStatus", RecordStatus.ACTIVE.getCode());

		return getByQuery(hql, params);
	}

	@Override
	public List<TCkCtTripAttach> findByJobId(String jobId) throws Exception {
		String hql = "from TCkCtTripAttach o WHERE TCkCtTrip.TCkJobTruck.TCkJob.jobId = :jobId "
				+ " AND o.atStatus = :atStatus";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("jobId", jobId);
		params.put("atStatus", RecordStatus.ACTIVE.getCode());
		return getByQuery(hql, params);
	}

	@Override
	public List<TCkCtTripAttach> findByAtypIdAndTrIds(String atypId, List<String> trIds) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtTripAttach.class);
        criteria.add(Restrictions.eq("TCkCtMstTripAttachType.atypId", atypId));
        criteria.add(Restrictions.eq("atStatus", RecordStatus.ACTIVE.getCode()));
        criteria.add(Restrictions.in("TCkCtTrip.trId", trIds));
		return getByCriteria(criteria);
	}

	@Override
	public List<TCkCtTripAttach> findByTruckJobIdAndType(String jobId, String atypId) throws Exception {
		String hql = "from TCkCtTripAttach o WHERE TCkCtTrip.TCkJobTruck.jobId = :jobId "
				+ " AND o.TCkCtMstTripAttachType.atypId = :atypId  AND o.atStatus = :atStatus";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("jobId", jobId);
		params.put("atStatus", RecordStatus.ACTIVE.getCode());
		params.put("atypId", atypId);
		return getByQuery(hql, params);
	}
		
}
