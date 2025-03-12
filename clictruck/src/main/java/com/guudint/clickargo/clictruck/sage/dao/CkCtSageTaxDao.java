package com.guudint.clickargo.clictruck.sage.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.sage.model.TCkCtSageTax;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtSageTaxDao extends GenericDao<TCkCtSageTax, String> {
	
	public List<TCkCtSageTax> findActiveSageTax() throws Exception;
	
}
