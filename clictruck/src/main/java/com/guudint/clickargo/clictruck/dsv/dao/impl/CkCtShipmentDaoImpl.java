package com.guudint.clickargo.clictruck.dsv.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.dsv.dao.CkCtShipmentDao;
import com.guudint.clickargo.clictruck.dsv.model.TCkCtShipment;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

@Service
@Transactional
public class CkCtShipmentDaoImpl extends GenericDaoImpl<TCkCtShipment, String> implements CkCtShipmentDao {

	@Override
	public List<TCkCtShipment> fetchByShipmentId(String shipmentId, String... shipmentStatus) throws Exception {

		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtShipment.class);

		criteria.add(Restrictions.eq("shMsgId", shipmentId));

		if (null != shipmentStatus && shipmentStatus.length > 0) {
			criteria.add(Restrictions.in("TCkCtMstShipmentState.stId", shipmentStatus));
		}

		criteria.addOrder(Order.desc("shDtCreate"));

		return super.getByCriteria(criteria);
	}

	@Override
	public List<TCkCtShipment> fetchByJobId(String jobParentId) throws Exception {

		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtShipment.class);

		criteria.add(Restrictions.eq("TCkJob.jobId", jobParentId));

		criteria.addOrder(Order.desc("shDtCreate"));

		return super.getByCriteria(criteria);
	}

	@Override
	public List<TCkCtShipment> fetchUnprocessedJob(JobStates jobStates) throws Exception {
/*-
 SELECT * 
FROM clickargo2.T_CK_CT_SHIPMENT ct, clickargo2.T_CK_JOB job
where ct.SH_JOB = job.JOB_ID
	and job.JOB_STATE = 'DLV'
    and ( ct.SH_JOB, ct.SH_DT_CREATE) in 
    ( select SH_JOB, max(SH_DT_CREATE)
       from clickargo2.T_CK_CT_SHIPMENT ship
       group by SH_JOB)
order by SH_DT_CREATE desc;
 */
		String hql = "FROM TCkCtShipment o "
				+ "	where o.TCkJob.TCkMstJobState.jbstId = :jbstId "
				+ " and shStatusmessagePath is null "
				+ " and (o.TCkJob.jobId, o.shDtCreate) "
				+ " in ("
				+ "		select sp.TCkJob.jobId, max(sp.shDtCreate)"
				+ "		from TCkCtShipment sp"
				+ "		group by sp.TCkJob.jobId"
				+ "	)"
				+ "	order by shDtCreate asc";
		
		Map<String, Object> params = new HashMap<>();
		params.put("jbstId", jobStates.name());
		return this.getByQuery(hql, params, 20, 0);
	}

	/*-
		@Override
		public List<TCkCtShipment> fetchUnprocessedJob(JobStates jobStates) throws Exception {
	
			DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtShipment.class);
	
			criteria.add(Restrictions.eq("TCkJob.TCkMstJobState.jbstId", jobStates.name()));
			criteria.add(Restrictions.isNull("shStatusmessagePath"));
	
			criteria.addOrder(Order.asc("shDtCreate"));
	
			return super.getByCriteria(criteria);
		}
		*/
	
	public List<TCkCtShipment> fetchDtStatusMessagePush2SftpIsNullAndStatusMessageIsNotNull() throws Exception {

		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtShipment.class);

		criteria.add(Restrictions.isNull("shDtStatusmessagePush2sftp"));
		criteria.add(Restrictions.isNotNull("shStatusmessagePath"));

		criteria.addOrder(Order.desc("shDtCreate"));

		return super.getByCriteria(criteria);
	}
}
