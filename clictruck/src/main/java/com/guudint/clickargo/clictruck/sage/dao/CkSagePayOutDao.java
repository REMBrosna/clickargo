package com.guudint.clickargo.clictruck.sage.dao;

import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.sage.model.VCkSagePayOut;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkSagePayOutDao extends GenericDao<VCkSagePayOut, String> {
	
	public List<VCkSagePayOut> findByDate(Date beginDate, Date endDate) throws Exception;
	
}
