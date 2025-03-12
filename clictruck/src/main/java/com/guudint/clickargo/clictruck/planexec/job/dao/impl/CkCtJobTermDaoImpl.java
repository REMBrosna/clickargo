package com.guudint.clickargo.clictruck.planexec.job.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.job.dao.CkCtJobTermDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTerm;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtJobTermDaoImpl extends GenericDaoImpl<TCkCtJobTerm, String> implements CkCtJobTermDao {
	
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<TCkCtJobTerm> findByReqId(String jtrId) throws Exception {
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtJobTerm.class);
        criteria.add(Restrictions.eq("TCkCtJobTermReq.jtrId", jtrId));
        return getByCriteria(criteria);
    }

}
