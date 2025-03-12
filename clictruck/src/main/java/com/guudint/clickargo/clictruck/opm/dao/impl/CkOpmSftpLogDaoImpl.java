package com.guudint.clickargo.clictruck.opm.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.opm.dao.CkOpmSftpLogDao;
import com.guudint.clickargo.clictruck.opm.model.TCkOpmSftpLog;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

@Service
public class CkOpmSftpLogDaoImpl extends GenericDaoImpl<TCkOpmSftpLog, String> implements CkOpmSftpLogDao {
	
	@Override
	@Transactional
	public TCkOpmSftpLog findByFileName(String fileName) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkOpmSftpLog.class);
		criteria.add(Restrictions.eq("opmslFileName", fileName));
		return getOne(criteria);
	}
}
