package com.guudint.clickargo.clictruck.common.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.common.dao.CkCtAccnInqReqDocsDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtAccnInqReqDocs;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtAccnInqReqDocsDaoImpl extends GenericDaoImpl<TCkCtAccnInqReqDocs, String>
		implements CkCtAccnInqReqDocsDao {

	@Override
	public List<TCkCtAccnInqReqDocs> getDocsByAccnReq(String accnInqReqId) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtAccnInqReqDocs.class);
		criteria.add(Restrictions.eq("TCkCtAccnInqReq.airId", accnInqReqId));
		criteria.add(Restrictions.eq("airdStatus", RecordStatus.ACTIVE.getCode()));
		return getByCriteria(criteria);
	}

}
