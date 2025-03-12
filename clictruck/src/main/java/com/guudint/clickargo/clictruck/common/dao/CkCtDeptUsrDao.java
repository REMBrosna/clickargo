package com.guudint.clickargo.clictruck.common.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.common.model.TCkCtDeptUsr;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtDeptUsrDao extends GenericDao<TCkCtDeptUsr, String> {

	public List<TCkCtDeptUsr> getUsersByDept(String deptId) throws Exception;

	public List<TCkCtDeptUsr> getUsersByAccnDept(String accnId) throws Exception;
	
	public TCkCtDeptUsr getUser(String usrUid) throws Exception;
}
