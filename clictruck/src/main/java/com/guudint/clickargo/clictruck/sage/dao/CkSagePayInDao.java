package com.guudint.clickargo.clictruck.sage.dao;

import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.sage.model.VCkSagePayIn;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkSagePayInDao extends GenericDao<VCkSagePayIn, String> {
	
	public List<VCkSagePayIn> findByDate(Date beginDate, Date endDate) throws Exception;
	
}
