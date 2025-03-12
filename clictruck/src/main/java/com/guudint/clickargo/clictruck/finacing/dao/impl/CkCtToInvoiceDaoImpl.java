package com.guudint.clickargo.clictruck.finacing.dao.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.finacing.dao.CkCtToInvoiceDao;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtToInvoiceDaoImpl extends GenericDaoImpl<TCkCtToInvoice, String> implements CkCtToInvoiceDao {

    @Override
    public List<TCkCtToInvoice> findByTripId(String tripId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtToInvoice.class);
        criteria.add(Restrictions.eq("TCkCtTrip", new TCkCtTrip(tripId)));
        return getByCriteria(criteria);
    }

    @Override
    public Optional<TCkCtToInvoice> findByTripIdAndInvNo(String tripId, String invNo) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtToInvoice.class);
        criteria.add(Restrictions.eq("TCkCtTrip", new TCkCtTrip(tripId)));
        criteria.add(Restrictions.eq("invNo", invNo));
        return Optional.ofNullable(getOne(criteria));
    }

    @Override
    public List<TCkCtToInvoice> findByJobId(String jobId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtToInvoice.class);
        criteria.add(Restrictions.eq("invJobId", jobId));
        criteria.add(Restrictions.eq("TCkCtMstToInvoiceState.instId", "NEW"));
        return getByCriteria(criteria);
    }

}
