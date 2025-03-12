package com.guudint.clickargo.clictruck.sage.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.sage.dao.CkSagePayInDao;
import com.guudint.clickargo.clictruck.sage.model.VCkSagePayIn;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

@Service
public class CkSagePayInDaoImpl extends GenericDaoImpl<VCkSagePayIn, String>
		implements CkSagePayInDao {
	
	public List<VCkSagePayIn> findByDate(Date beginDate, Date endDate) throws Exception {

        DetachedCriteria criteria = DetachedCriteria.forClass(VCkSagePayIn.class);
        
        criteria.add(Restrictions.ge("ptxDtPaid", beginDate));
        criteria.add(Restrictions.lt("ptxDtPaid", endDate));
        
        return super.getByCriteria(criteria);
	}
}