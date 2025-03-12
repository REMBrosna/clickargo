/**
 * 
 */
package com.guudint.clickargo.clictruck.planexec.job.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.sage.service.SageUtil;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

/**
 * @author adenny
 *
 */
public class CkJobTruckDaoImpl extends GenericDaoImpl<TCkJobTruck, String> implements CkJobTruckDao {

	private static Logger log = Logger.getLogger(CkJobTruckDaoImpl.class);

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TCkJobTruck> findByIds(List<String> jobIds) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkJobTruck.class);
		criteria.add(Restrictions.in("jobId", jobIds));
		return getByCriteria(criteria);
	}
	
	@Override
	public List<TCkJobTruck> findByIdPrefixs(String jobIdPrefix) throws Exception {
		String hql = "from TCkJobTruck o where o.jobId like :jobIdPrefix";
		Map<String, Object> params = new HashMap<>();
		params.put("jobIdPrefix", jobIdPrefix + "%");
		return getByQuery(hql, params);
	}

	@Override
	public List<TCkJobTruck> findByDrvMobileId(String drvMobileId, List<String> states) throws Exception {
		Map<String, Object> params = new HashMap<>();
		StringBuilder hql = new StringBuilder("from TCkJobTruck o " + "where o.TCkCtDrv.drvMobileId = :drvMobileId");
		params.put("drvMobileId", drvMobileId);
		if (states != null) {
			// find jobs assigned to this driver that is in valid state
			hql.append(" and o.TCkJob.TCkMstJobState.jbstId  in (:includeState)");
			params.put("includeState", states);
		}

		return getByQuery(hql.toString(), params);
	}

	@Override
	public List<TCkJobTruck> findByDrvMobileId(String drvMobileId) throws Exception {
		return findByDrvMobileId(drvMobileId, null);
	}
	
	@Override
	public List<TCkJobTruck> findOngoingJob() throws Exception {
		String hql = "from TCkJobTruck o " + "where o.TCkJob.TCkMstJobState.jbstId = :jbstId";
		Map<String, Object> params = new HashMap<>();
		params.put("jbstId", JobStates.ONGOING.name());
		return getByQuery(hql, params);
	}

	/**
	 * For retrieving onGoing mobile enabled job. 
	 */
	@Override
	public List<TCkJobTruck> findOngoingJobByDrvId(String drvId) throws Exception {
		String hql = "from TCkJobTruck o" + " where o.TCkJob.TCkMstJobState.jbstId = :jbstId"
				+ " and o.TCkCtDrv.drvId = :drvId"
				+ " and o.jobMobileEnabled = 'Y'";
		Map<String, Object> params = new HashMap<>();
		params.put("jbstId", JobStates.ONGOING.name());
		params.put("drvId", drvId);
		return getByQuery(hql, params);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TCkJobTruck> findByParentId(String parentId) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkJobTruck.class);
		criteria.add(Restrictions.eq("TCkJob.jobId", parentId));
		return getByCriteria(criteria);
	}
	
	
	@Override
	public List<TCkJobTruck> findByDrvIdAndJobStatus(String drvId, String ...status) throws Exception {
		String hql = "from TCkJobTruck o" + " where o.TCkJob.TCkMstJobState.jbstId IN :jobStates"
				+ " and o.TCkCtDrv.drvId = :drvId";
		Map<String, Object> params = new HashMap<>();
		params.put("jobStates", Arrays.asList(status));
		params.put("drvId", drvId);
		return getByQuery(hql, params);
	}

	/*
	 * docVerifyDate: yyyymmdd
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TCkJobTruck> findByFinancerAndDocVerifyDate(List<String> jobFinanceOptList, String docVerifyDate) throws Exception {
		
		Date date = null;
		
		try {
			date = new SimpleDateFormat("yyyyMMdd").parse(docVerifyDate);
		} catch(Exception e) {
			log.error("Fail to parse " + docVerifyDate + " to date", e);
			throw e;
		}
		
		//Date beginTime = sageUtil.getBeginDate(date);
		Date endTime = (new SageUtil()).getEndDate(date);

		String hql = "from TCkJobTruck o" 
				+ " where o.TCkJob.TCkMstJobState.jbstId IN :jobStates"
				+ " and o.jobFinanceOpt in :jobFinanceOpt "
				+ " and o.jobDtOpmUtilize is null "
				+ " and o.TCkJob.TCkRecordDate.rcdDtBillApproved < :rcdDtBillApproved " 
				+ " and o.TCoreAccnByJobPartyCoFf.accnStatus = 'A' "
				+ " and o.TCoreAccnByJobPartyTo.accnStatus = 'A' " 
				+ " and ( jobFinanceOpt = 'OC' and o.TCoreAccnByJobPartyCoFf.accnId in  "
				+ " 	(select caoId from TCkAccnOpm ao where ao.caoId = o.TCoreAccnByJobPartyCoFf.accnId"
				+ "			and ao.caoStatus = 'A' ) "
				+ "		or "
				+ "		(jobFinanceOpt = 'OT' and o.TCoreAccnByJobPartyTo.accnId in  "
				+ "				 	(select caoId from TCkAccnOpm ao where ao.caoId = o.TCoreAccnByJobPartyTo.accnId "
				+ "							and ao.caoStatus = 'A' )))" ;
		
		Map<String, Object> params = new HashMap<>();
		params.put("jobStates", Arrays.asList(JobStates.APP_BILL.name()));
		params.put("jobFinanceOpt", jobFinanceOptList);
		params.put("rcdDtBillApproved", endTime);
		
		return getByQuery(hql, params);

		/*
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkJobTruck.class);
		//criteria.add(Restrictions.eq("jobFinancer", jobFinancer));
		criteria.add(Restrictions.in("jobFinanceOpt", jobFinanceOptList));
		criteria.add(Restrictions.isNull("jobDtOpmUtilize"));
		//criteria.add(Restrictions.eq("TCoreAccnByJobPartyTo.", Constant.ACTIVE_STATUS));
		//criteria.add(Restrictions.eq("", Constant.ACTIVE_STATUS));
		
        criteria.createAlias("TCkJob", "ckjob");
        criteria.createAlias("ckjob.TCkRecordDate", "recordDate");
        
		//criteria.add(Restrictions.between("recordDate.rcdDtBillApproved", beginTime, endTime) );
		criteria.add(Restrictions.le("recordDate.rcdDtBillApproved", endTime) );
		
		return getByCriteria(criteria);
		*/
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void updateUtilizeDate(List<String> jobTruckIdList) throws Exception {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("jobTruckIdList", jobTruckIdList);

		this.executeUpdate(
				"update TCkJobTruck o set jobDtOpmUtilize = sysdate() WHERE o.jobId IN (:jobTruckIdList)", parameters);
    }

	@Override
    public List<TCkJobTruck> findByStatusAndEpodIsNull(String jobStatus, int limit) throws Exception {
    	
		String hql = "from TCkJobTruck o" + " where o.TCkJob.TCkMstJobState.jbstId = :jobStates"
				+ " and o.jobDtEpod is null"
				+ " order by jobId desc ";
		
		Map<String, Object> params = new HashMap<>();
		params.put("jobStates", jobStatus);
		return getByQuery(hql, params, limit, 0);
    }
    
    public List<TCkJobTruck> findByStatusAndEpodIsNull(String jobStatus, String dsvAS) throws Exception {
    	
		String hql = "from TCkJobTruck o" + " where o.TCkJob.TCkMstJobState.jbstId = :jobStates"
				+ " and o.TCoreAccnByJobPartyCoFf.accnId != :dsvAS"
				+ " and o.jobDtEpod is null"
				+ " order by jobId desc ";
		
		Map<String, Object> params = new HashMap<>();
		params.put("jobStates", jobStatus);
		params.put("dsvAS", dsvAS);
		return getByQuery(hql, params, 100, 0);
    }

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<TCkJobTruck> findByCoFf(String deptId, String accnId) throws Exception {
		Map<String, Object> params = new HashMap<>();
		String hql;
		params.put("jobStates", JobStates.ONGOING.name());
		params.put("accnId", accnId);
		if ("nonDep".equalsIgnoreCase(deptId)) {
			hql = "FROM TCkJobTruck o WHERE o.TCkJob.TCkMstJobState.jbstId = :jobStates " +
					"AND o.TCkCtDeptByJobCoDepartment.deptId IS NULL " +
					"AND o.TCkCtVeh.vhId IS NOT NULL AND (o.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR o.TCkJob.TCoreAccnByJobFfAccn.accnId = :accnId)";
		} else {
			params.put("deptId", deptId);
			hql = "FROM TCkJobTruck o WHERE o.TCkJob.TCkMstJobState.jbstId = :jobStates " +
					"AND (o.TCkCtDeptByJobCoDepartment.deptId = :deptId OR o.TCkCtDeptByJobCoDepartment.deptId IS NULL) " +
					"AND o.TCkCtVeh.vhId IS NOT NULL AND (o.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR o.TCkJob.TCoreAccnByJobFfAccn.accnId = :accnId)";
		}
		return getByQuery(hql, params);
	}
}
