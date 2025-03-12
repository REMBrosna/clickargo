package com.guudint.clickargo.clictruck.common.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.common.dao.CkCtDeptUsrDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtDeptUsr;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtDeptUsrDaoImpl extends GenericDaoImpl<TCkCtDeptUsr, String> implements CkCtDeptUsrDao {

	@Override
	public List<TCkCtDeptUsr> getUsersByDept(String deptId) throws Exception {
		String hql = "FROM TCkCtDeptUsr o where o.TCkCtDept.deptId = :deptId and o.duStatus = :status";
		Map<String, Object> params = new HashMap<>();
		params.put("deptId", deptId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		return this.getByQuery(hql, params);
	}

	@Override
	public List<TCkCtDeptUsr> getUsersByAccnDept(String accnId) throws Exception {
		String hql = "FROM TCkCtDeptUsr o where o.TCkCtDept.TCoreAccn.accnId = :accnId and o.duStatus = :status";
		Map<String, Object> params = new HashMap<>();
		params.put("accnId", accnId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		return this.getByQuery(hql, params);
	}

	@Override
	public TCkCtDeptUsr getUser(String usrUid) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtDeptUsr.class);
		criteria.add(Restrictions.eq("TCoreUsr.usrUid", usrUid));
		criteria.add(Restrictions.eq("duStatus", RecordStatus.ACTIVE.getCode()));
		return getOne(criteria);
	}

}
