package com.guudint.clickargo.clictruck.common.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.common.model.TCkCtDeptVeh;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtDeptVehDao extends GenericDao<TCkCtDeptVeh, String> {

	public List<TCkCtDeptVeh> getVehiclesByDept(String deptId) throws Exception;

	public List<TCkCtDeptVeh> getVehiclesByAccnDept(String accnId) throws Exception;

	public TCkCtDeptVeh getVehicleDeptByVeh(String vehId) throws Exception;

	public List<TCkCtDeptVeh> getAll() throws Exception;
}
