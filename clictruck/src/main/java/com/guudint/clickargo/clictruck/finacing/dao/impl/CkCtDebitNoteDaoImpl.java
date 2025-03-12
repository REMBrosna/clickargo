package com.guudint.clickargo.clictruck.finacing.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.finacing.dao.CkCtDebitNoteDao;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtDebitNoteDaoImpl extends GenericDaoImpl<TCkCtDebitNote, String> implements CkCtDebitNoteDao {

	public List<TCkCtDebitNote> findDebitNotes(Date beginDate, Date endDate, String fromAccnId) throws Exception {

		String hql = "from TCkCtDebitNote pi "
				+ " where dnDtIssue >= :beginDate and dnDtIssue <= :endDate "
				+ " 	and TCoreAccnByDnFrom.accnId = :fromAccnId";

		Map<String, Object> params = new HashMap<>();
		params.put("beginDate", beginDate);
		params.put("endDate", endDate);
		params.put("fromAccnId", fromAccnId);

		return getByQuery(hql, params);
	}

	@Override
	public List<TCkCtDebitNote> findByJobId(String jobId) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtDebitNote.class);
        criteria.add(Restrictions.eq("dnJobId", jobId));
        return getByCriteria(criteria);
	}
}
