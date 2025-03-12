package com.guudint.clickargo.clictruck.sage.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.sage.dao.CkSageBookingDao;
import com.guudint.clickargo.clictruck.sage.model.VCkSageBooking;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

@Service
public class CkSageBookingDaoImpl extends GenericDaoImpl<VCkSageBooking, String>
		implements CkSageBookingDao {
	
	public List<VCkSageBooking> findByDate(Date beginDate, Date endDate) throws Exception {

        DetachedCriteria criteria = DetachedCriteria.forClass(VCkSageBooking.class);
        
        criteria.add(Restrictions.ge("rcdDtBillApproved", beginDate));
        criteria.add(Restrictions.lt("rcdDtBillApproved", endDate));
        
        return super.getByCriteria(criteria);
	}
	
}
