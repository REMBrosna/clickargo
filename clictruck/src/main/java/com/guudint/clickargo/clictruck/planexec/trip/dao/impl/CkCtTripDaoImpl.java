package com.guudint.clickargo.clictruck.planexec.trip.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtTripDaoImpl extends GenericDaoImpl<TCkCtTrip, String> implements CkCtTripDao {

    @Override
    public List<TCkCtTrip> findByJobId(String jobId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtTrip.class);
        TCkJobTruck tCkJobTruck = new TCkJobTruck();
        tCkJobTruck.setJobId(jobId);
        criteria.add(Restrictions.eq("TCkJobTruck", tCkJobTruck));
        return getByCriteria(criteria);
    }

}
