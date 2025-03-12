package com.guudint.clickargo.clictruck.common.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.common.dao.CkCtDeptVehDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtDeptVeh;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class CkCtDeptVehDaoImpl extends GenericDaoImpl<TCkCtDeptVeh, String> implements CkCtDeptVehDao {

	@Override
	public List<TCkCtDeptVeh> getVehiclesByDept(String deptId) throws Exception {
		String hql = "FROM TCkCtDeptVeh o where o.TCkCtDept.deptId = :deptId and o.dvStatus = :status";
		Map<String, Object> params = new HashMap<>();
		params.put("deptId", deptId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		return this.getByQuery(hql, params);
	}

	@Override
	public List<TCkCtDeptVeh> getVehiclesByAccnDept(String accnId) throws Exception {
		String hql = "FROM TCkCtDeptVeh o where o.TCkCtDept.TCoreAccn.accnId = :accnId and o.dvStatus = :status";
		Map<String, Object> params = new HashMap<>();
		params.put("accnId", accnId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		return this.getByQuery(hql, params);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public TCkCtDeptVeh getVehicleDeptByVeh(String vehId) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtDeptVeh.class);
		criteria.add(Restrictions.eq("TCkCtVeh.vhId", vehId));
		criteria.add(Restrictions.eq("dvStatus", RecordStatus.ACTIVE.getCode()));
		return getOne(criteria);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public List<TCkCtDeptVeh> getAll() throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("status", RecordStatus.ACTIVE.getCode());
		String hql = "SELECT o FROM TCkCtDeptVeh o WHERE o.dvStatus = :status";
		return this.getByQuery(hql, params);
	}
}
