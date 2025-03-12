package com.guudint.clickargo.clictruck.planexec.trip.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtTripDoDaoImpl extends GenericDaoImpl<TCkCtTripDo, String> implements CkCtTripDoDao {

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public List<TCkCtTripDo> findByTripId(String tripId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtTripDo.class);
        TCkCtTrip tCkCtTrip = new TCkCtTrip();
        tCkCtTrip.setTrId(tripId);
        criteria.add(Restrictions.eq("TCkCtTrip", tCkCtTrip));
        return getByCriteria(criteria);
    }
 
}
