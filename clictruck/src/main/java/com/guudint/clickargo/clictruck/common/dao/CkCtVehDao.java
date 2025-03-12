package com.guudint.clickargo.clictruck.common.dao;

import java.util.List;
import java.util.Optional;

import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtVehDao extends GenericDao<TCkCtVeh, String> {

	List<TCkCtVeh> findVehTypeByCompany(String companyId) throws Exception;
	
	List<TCkCtVeh> findVehTypeByCompany(String companyId, List<String>states) throws Exception;
	
	Optional<TCkCtVeh> findByImei(String imei) throws Exception;
	
	List<TCkCtVeh> findByCompanyPlateNo(String companyId, String vhPlateNo) throws Exception;
	
	List<TCkCtVeh> findVehNotInDepartment(String companyId) throws Exception;
}
