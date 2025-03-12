package com.guudint.clickargo.clictruck.jobupload.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.jobupload.dao.CkJobUploadDao;
import com.guudint.clickargo.clictruck.jobupload.model.TCkJobUpload;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

@Service
@Transactional
public class CkJobUploadDaoImpl extends GenericDaoImpl<TCkJobUpload, String> implements CkJobUploadDao {

	public List<TCkJobUpload> findByAccn(String accnId) throws Exception {

		DetachedCriteria criteria = DetachedCriteria.forClass(TCkJobUpload.class);
		criteria.add(Restrictions.eq("upAccnId", accnId));
		
		return super.getByCriteria(criteria);
	}
}
