package com.guudint.clickargo.clictruck.planexec.trip.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtPlatformInvoiceDaoImpl extends GenericDaoImpl<TCkCtPlatformInvoice, String>
		implements CkCtPlatformInvoiceDao {

	@Override
	public List<TCkCtPlatformInvoice> findPlatformInvoices(Date beginDate, Date endDate) throws Exception {

		String hql = "from TCkCtPlatformInvoice pi where invDtIssue >= :beginDate and invDtIssue <= :endDate";

		Map<String, Object> params = new HashMap<>();
		params.put("beginDate", beginDate);
		params.put("endDate", endDate);
		
		return getByQuery(hql, params);

	}
/*-
	@Override
	public List<TCkCtPlatformInvoice> findByJobId(String jobId, String accnType) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtPlatformInvoice.class);

        criteria.createAlias("TCoreAccnByInvFrom", "invFrom");
        criteria.createAlias("invFrom.TMstAccnType", "accnType");
        
        criteria.add(Restrictions.eq("invJobId", jobId));
        criteria.add(Restrictions.eq("accnType.atypId", accnType));
        return getByCriteria(criteria);
	}
*/	
	@Override
	public List<TCkCtPlatformInvoice> findByJobIdAndInvTo(String jobId, String accnType) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtPlatformInvoice.class);

        criteria.createAlias("TCoreAccnByInvTo", "invTo");
        criteria.createAlias("invTo.TMstAccnType", "accnType");
        
        criteria.add(Restrictions.eq("invJobId", jobId));
        criteria.add(Restrictions.eq("accnType.atypId", accnType));
        return getByCriteria(criteria);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<TCkCtPlatformInvoice> findByJobId(String jobId) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtPlatformInvoice.class);
        criteria.add(Restrictions.eq("invJobId", jobId));
        return getByCriteria(criteria);
	}

	@Override
    public List<TCkCtPlatformInvoice> findByInvDtIssue(String start) throws Exception {
        String hql = "from TCkCtPlatformInvoice ti "
                + "where DATE_FORMAT(ti.invDtIssue, '%d/%m/%Y') = :date";
        Map<String, Object> params = new HashMap<>();
        params.put("date", start);
        return getByQuery(hql, params);
    }
	
	@Override
	public List<TCkCtPlatformInvoice> findByInvoiceNumber(String invNo) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtPlatformInvoice.class);
        criteria.add(Restrictions.eq("invNo", invNo));
        return getByCriteria(criteria);
	}
	
	@Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<TCkCtPlatformInvoice> findByAccnIdAndStatus(String accnId, List<String> invStatus) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtPlatformInvoice.class);
        criteria.add(Restrictions.eq("TCoreAccnByInvTo.accnId", accnId));
        criteria.add(Restrictions.in("TCkCtMstToInvoiceState.instId", invStatus));
        
        return getByCriteria(criteria);
	}

	@Override
	public List<TCkCtPlatformInvoice> findByPaidDateAndAccnType( Date beginDate, Date endDate, String accnType) throws Exception {
		String hql = "from TCkCtPlatformInvoice pi "
				+ " where TCkCtMstToInvoiceState.instId = :invStatus "
				+ "		and TCoreAccnByInvTo.TMstAccnType.atypId <= :accnType"
				+ "		and invDtPaid >= :beginDate"
				+ "		and invDtPaid <= :endDate"
				+ " order by invDtPaid asc";

		Map<String, Object> params = new HashMap<>();
		params.put("invStatus", JobPaymentStates.PAID.name());
		params.put("accnType", AccountTypes.ACC_TYPE_TO.name());
		params.put("beginDate", beginDate);
		params.put("endDate", endDate);
		
		return getByQuery(hql, params);
	}
}
