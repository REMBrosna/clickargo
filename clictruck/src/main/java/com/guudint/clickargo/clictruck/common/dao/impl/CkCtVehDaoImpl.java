package com.guudint.clickargo.clictruck.common.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.common.dao.CkCtVehDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtVehDaoImpl extends GenericDaoImpl<TCkCtVeh, String> implements CkCtVehDao {

	@Override
	public List<TCkCtVeh> findVehTypeByCompany(String companyId) throws Exception {
		String hql = "FROM TCkCtVeh o where TCoreAccn.accnId = :companyId and vhStatus = :status " +
				"ORDER BY o.TCkCtMstVehType.vhtyName ASC";
		Map<String, Object> params = new HashMap<>();
		params.put("companyId", companyId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		return this.getByQuery(hql, params);
	}
	
	@Override
	public List<TCkCtVeh> findVehTypeByCompany(String companyId, List<String> states) throws Exception {
		StringBuilder hql = new StringBuilder("FROM TCkCtVeh o where TCoreAccn.accnId = :companyId and vhStatus = :status");
		Map<String, Object> params = new HashMap<>();
		params.put("companyId", companyId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		if(states!=null) {
			if (states != null) {
				// find jobs assigned to this driver that is in valid state
				hql.append("  and TCkCtMstVehState.vhstId in (:includeState)");
				params.put("includeState", states);
			}
		}
		
		return getByQuery(hql.toString(), params);
	}

	@Override
	public Optional<TCkCtVeh> findByImei(String imei) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtVeh.class);
        criteria.add(Restrictions.eq("vhGpsImei", imei));
        return Optional.ofNullable(getOne(criteria));
	}

	@Override
	public List<TCkCtVeh> findByCompanyPlateNo(String companyId, String vhPlateNo) throws Exception {
		String hql = "FROM TCkCtVeh o where TCoreAccn.accnId = :companyId "
				+ " and o.vhPlateNo = :vhPlateNo"
				+ " and vhStatus = :status";
		Map<String, Object> params = new HashMap<>();
		params.put("companyId", companyId);
		params.put("vhPlateNo", vhPlateNo);
		params.put("status", RecordStatus.ACTIVE.getCode());
		return this.getByQuery(hql, params);
	}

	@Override
	public List<TCkCtVeh> findVehNotInDepartment(String companyId) throws Exception {
		String hql = "FROM TCkCtVeh o where TCoreAccn.accnId = :companyId "
				+ " and o.vhId not in ("
				+ "		select dv.TCkCtVeh.vhId from TCkCtDeptVeh dv"
				+ "		)"
				+ " and vhStatus = :status";
		Map<String, Object> params = new HashMap<>();
		params.put("companyId", companyId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		return this.getByQuery(hql, params);
	}
}
