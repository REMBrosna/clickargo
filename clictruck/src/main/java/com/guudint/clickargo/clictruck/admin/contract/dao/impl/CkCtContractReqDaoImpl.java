package com.guudint.clickargo.clictruck.admin.contract.dao.impl;

import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractReqDao;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractReq;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtContractReqDaoImpl extends GenericDaoImpl<TCkCtContractReq, String> implements CkCtContractReqDao {

	@Override
	public Optional<TCkCtContractReq> findByName(String name) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtContractReq.class);
		criteria.add(Restrictions.eq("crName", name));
		return Optional.ofNullable(getOne(criteria));
	}

}
