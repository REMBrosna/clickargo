package com.guudint.clickargo.clictruck.sage.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.sage.dao.CkSagePayOutDao;
import com.guudint.clickargo.clictruck.sage.model.VCkSagePayOut;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

@Service
public class CkSagePayoutDaoImpl extends GenericDaoImpl<VCkSagePayOut, String>
		implements CkSagePayOutDao {
	
	public List<VCkSagePayOut> findByDate(Date beginDate, Date endDate) throws Exception {

        DetachedCriteria criteria = DetachedCriteria.forClass(VCkSagePayOut.class);
        
        criteria.add(Restrictions.ge("ptxDtPaid", beginDate));
        criteria.add(Restrictions.lt("ptxDtPaid", endDate));
        
        return super.getByCriteria(criteria);
	}
}