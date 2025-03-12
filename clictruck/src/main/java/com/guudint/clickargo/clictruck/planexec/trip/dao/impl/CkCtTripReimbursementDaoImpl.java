package com.guudint.clickargo.clictruck.planexec.trip.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripReimbursementDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripReimbursement;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class CkCtTripReimbursementDaoImpl extends GenericDaoImpl<TCkCtTripReimbursement, String>
		implements CkCtTripReimbursementDao {

	@Override
	public List<TCkCtTripReimbursement> findByTripIdAndStatus(String tripId, Character status) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtTripReimbursement.class);
		criteria.add(Restrictions.eq("TCkCtTrip", new TCkCtTrip(tripId)));
		criteria.add(Restrictions.eq("trStatus", status));
		return getByCriteria(criteria);
	}

	@Override
	public BigDecimal sumTotalByTripIdAndStatus(String tripId, Character status) throws Exception {
		String hql = "select sum(o.trTotal) from TCkCtTripReimbursement o "
				+ "where o.TCkCtTrip.trId = :tripId and o.trStatus = :trStatus";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("tripId", tripId);
		query.setParameter("trStatus", status);
		return NumberUtil.toBigDecimal(query.uniqueResult());
	}

}
