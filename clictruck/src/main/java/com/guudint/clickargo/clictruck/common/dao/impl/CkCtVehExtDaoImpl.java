package com.guudint.clickargo.clictruck.common.dao.impl;

import java.util.*;

import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.vcc.camelone.common.dao.GenericDao;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.common.dao.CkCtVehExtDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtVehExt;
import com.guudint.clickargo.clictruck.common.model.TCkCtVehExtId;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class CkCtVehExtDaoImpl extends GenericDaoImpl<TCkCtVehExt, TCkCtVehExtId> implements CkCtVehExtDao {


	@Autowired
	@Qualifier("ckCtAlertDao")
	private GenericDao<TCkCtAlert, String> ckCtAlertDao;

	@Override
	public List<TCkCtVehExt> findByMonitoring(String param, Character monitor, String dueDate) throws Exception {
		String hql = "FROM TCkCtVehExt o WHERE " + "id.vextParam = :param " + "AND vextStatus = :status "
				+ "AND vextNotify = :notify " + "AND vextMonitorMthd = :monitor ";

		if (monitor.equals('D') && dueDate != null) {
			hql += "AND DATEDIFF(vextValue, :dueDate) = vextMonitorValue";
		}

		Map<String, Object> params = new HashMap<>();
		params.put("status", RecordStatus.ACTIVE.getCode());
		params.put("notify", 'Y');
		params.put("monitor", monitor);
		params.put("param", param);

		if (monitor.equals('D') && dueDate != null) {
			params.put("dueDate", dueDate);
		}

		return this.getByQuery(hql, params);
	}

	@Override
	public Optional<TCkCtVehExt> findByIdAndMonitoring(String vhId, String param, Character monitor) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtVehExt.class);
		criteria.add(Restrictions.eq("id.vextId", vhId));
		criteria.add(Restrictions.eq("id.vextParam", param));
		criteria.add(Restrictions.eq("vextMonitorMthd", monitor));
		criteria.add(Restrictions.eq("vextStatus", RecordStatus.ACTIVE.getCode()));
		return Optional.ofNullable(getOne(criteria));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public TCkCtVehExt findByVehIdAndKey(String vehId, String key) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtVehExt.class);
		criteria.add(Restrictions.eq("id.vextId", vehId));
		criteria.add(Restrictions.like("id.vextParam", key + "%"));
		criteria.add(Restrictions.eq("vextStatus", RecordStatus.ACTIVE.getCode()));
		return getOne(criteria);
	}

	public List<TCkCtAlert> findByAlertCondition(Date formattedDate, List<String> listState) throws Exception {
		String hql = "FROM TCkCtAlert a WHERE a.altStatus = :status AND a.tCkCtMstAlert.altTemplateId IN (:altTemplateId) ";
		if (formattedDate != null) {
			hql += "AND DATEDIFF(a.altConditionDt, :formattedDate) <= a.altConditionValue ";
		}

		Map<String, Object> params = new HashMap<>();
		params.put("status", 'A');
		params.put("altTemplateId", listState);

		if (formattedDate != null) {
			params.put("formattedDate", formattedDate);
		}

		return ckCtAlertDao.getByQuery(hql, params);
	}

}
