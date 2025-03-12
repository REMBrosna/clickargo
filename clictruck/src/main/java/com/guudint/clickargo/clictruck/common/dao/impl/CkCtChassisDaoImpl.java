package com.guudint.clickargo.clictruck.common.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dao.CkCtChassisDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtChassis;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtChassisDaoImpl extends GenericDaoImpl<TCkCtChassis, String> implements CkCtChassisDao {

	@Transactional
	@Override
	public List<TCkCtChassis> findExistingChassis(String chsNo, String chassisType, String accnId) throws Exception {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtChassis.class);
		criteria.add(Restrictions.eq("chsNo", chsNo));
		criteria.add(Restrictions.eq("TCkCtMstChassisType.chtyId", chassisType));
		criteria.add(Restrictions.eq("TCoreAccn.accnId", accnId));
		criteria.add(Restrictions.eq("chsStatus", RecordStatus.ACTIVE.getCode()));
		return getByCriteria(criteria);
	}
	
	@Transactional
	@Override
	public List<TCkCtChassis> findChassisByCompany(String accnId) throws Exception {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtChassis.class);
		criteria.add(Restrictions.eq("TCoreAccn.accnId", accnId));
		criteria.add(Restrictions.eq("chsStatus", RecordStatus.ACTIVE.getCode()));
		return getByCriteria(criteria);
	}

	@Transactional
	@Override
	public TCkCtChassis findChassisByChsNo(String chsNo, String accnId) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtChassis.class);
		criteria.add(Restrictions.eq("chsNo", chsNo));
		criteria.add(Restrictions.eq("TCoreAccn.accnId", accnId));
		criteria.add(Restrictions.eq("chsStatus", RecordStatus.ACTIVE.getCode()));
		return getOne(criteria);
	}

}
