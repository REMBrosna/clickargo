package com.guudint.clickargo.clictruck.common.dao.impl;

import java.util.Optional;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.common.dao.CkCtVehMlogDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtVehMlog;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtVehMlogDaoImpl extends GenericDaoImpl<TCkCtVehMlog, String> implements CkCtVehMlogDao {

	@Override
	public Optional<TCkCtVehMlog> findByVehId(String vhId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtVehMlog.class);
		criteria.add(Restrictions.eq("TCkCtVeh.vhId", vhId));
		criteria.add(Restrictions.eq("vmlStatus", RecordStatus.ACTIVE.getCode()));
		try {
			return Optional.ofNullable(getOne(criteria));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
