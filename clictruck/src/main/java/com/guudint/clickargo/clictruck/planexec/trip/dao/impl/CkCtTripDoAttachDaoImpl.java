package com.guudint.clickargo.clictruck.planexec.trip.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtTripDoAttachDaoImpl extends GenericDaoImpl<TCkCtTripDoAttach, String> implements CkCtTripDoAttachDao {

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public List<TCkCtTripDoAttach> findByTripId(String tripId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtTripDoAttach.class);
        TCkCtTrip tCkCtTrip = new TCkCtTrip();
        tCkCtTrip.setTrId(tripId);
        criteria.add(Restrictions.eq("TCkCtTrip", tCkCtTrip));
        return getByCriteria(criteria);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public List<TCkCtTripDoAttach> findByJobId(String jobTruckId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtTripDoAttach.class);

        //criteria.add(Restrictions.eq("TCkCtTrip.TCkJobTruck.jobId", jobTruckId));
        
        criteria.createAlias("TCkCtTrip", "trip");
        criteria.createAlias("trip.TCkJobTruck", "jobTruck");
        criteria.add(Restrictions.eq("jobTruck.jobId", jobTruckId));

        return getByCriteria(criteria);
    }

}
