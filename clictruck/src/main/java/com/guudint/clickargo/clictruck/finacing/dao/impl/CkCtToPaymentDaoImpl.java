package com.guudint.clickargo.clictruck.finacing.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.finacing.dao.CkCtToPaymentDao;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToPayment;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtToPaymentDaoImpl extends GenericDaoImpl<TCkCtToPayment, String> implements CkCtToPaymentDao {

    @Override
    public List<TCkCtToPayment> findByTopReference(String topReference) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtToPayment.class);
        criteria.add(Restrictions.eq("topReference", topReference));
        return getByCriteria(criteria);
    }

    @Override
    public List<TCkCtToPayment> findByAccnTo(String accnId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtToPayment.class);
        criteria.add(Restrictions.eq("TCoreAccn.accnId", accnId));
        return getByCriteria(criteria);
    }

}
