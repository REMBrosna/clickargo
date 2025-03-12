package com.guudint.clickargo.clictruck.planexec.trip.dao.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPaymentDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPayment;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtPaymentDaoImpl extends GenericDaoImpl<TCkCtPayment, String> implements CkCtPaymentDao {

    @Override
    public Optional<TCkCtPayment> findByJobId(String jobId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtPayment.class);
        criteria.add(Restrictions.eq("ctpJob", jobId));
        return Optional.ofNullable(getOne(criteria));
    }
 
    @Override
    public List<TCkCtPayment> findByPtxId(String ptxId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtPayment.class);
        criteria.add(Restrictions.eq("TCkPaymentTxn.ptxId", ptxId));
        return getByCriteria(criteria);
    }
}
