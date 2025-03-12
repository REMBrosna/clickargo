package com.guudint.clickargo.clictruck.track.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.track.dao.CkCtTrackLocDao;
import com.guudint.clickargo.clictruck.track.model.TCkCtTrackLoc;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

@Service
public class CkCtTrackLocDaoImpl extends GenericDaoImpl<TCkCtTrackLoc, String>
		implements CkCtTrackLocDao {

	@Override
	public List<TCkCtTrackLoc> findByJobId(String jobTruckId) throws Exception {

        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtTrackLoc.class);
        criteria.add(Restrictions.eq("TCkJobTruck.jobId", jobTruckId));
        return super.getByCriteria(criteria);
	}
}
