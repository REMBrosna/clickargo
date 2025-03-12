package com.guudint.clickargo.clictruck.planexec.job.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.planexec.job.dao.CkCtJobTripDeliveryDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTripDelivery;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtJobTripDeliveryDaoImpl extends GenericDaoImpl<TCkCtJobTripDelivery, String>
		implements CkCtJobTripDeliveryDao {

	@Override
	public TCkCtJobTripDelivery findByJobId(String jobId) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtJobTripDelivery.class);
		criteria.add(Restrictions.eq("jtdJobId", jobId));
		return getOne(criteria);
	}

}
